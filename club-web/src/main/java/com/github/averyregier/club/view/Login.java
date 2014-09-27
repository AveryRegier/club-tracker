package com.github.averyregier.club.view;

import com.github.averyregier.club.application.ClubApplication;
import com.github.averyregier.club.domain.User;
import org.brickred.socialauth.AuthProvider;
import org.brickred.socialauth.Profile;
import org.brickred.socialauth.SocialAuthConfig;
import org.brickred.socialauth.SocialAuthManager;
import spark.Request;
import spark.Response;
import spark.Session;
import spark.template.freemarker.FreeMarkerEngine;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

import static spark.Spark.*;

/**
 * Created by avery on 8/30/14.
 */
public class Login extends ModelMaker {
    public static void resetCookies(Request req, Response httpResponse, String identifier, User user) {
        httpResponse.cookie("auth", user.resetAuth(), 60 * 60 * 3, false);
        httpResponse.cookie("userID", identifier, 60*60*3, false);
        httpResponse.redirect(req.session().attribute("location"));
        req.session().removeAttribute("location");
    }

    public void init(final ClubApplication app) {
        before("/protected/*", (request, response) -> {
            // ... check if authenticated
            String auth = request.cookie("auth");
            if(auth != null) {
                Optional<User> user = app.getUserManager().getUser(request.cookie("userID"));
                if(user.isPresent() && user.get().authenticate(auth)) {
                    request.attribute("user", user);
                    return;
                }
            }
            request.session().attribute("location", request.url());
            String context = request.contextPath();
            context = context == null ? "" : context;
            response.redirect(context +"/login");
            halt();
        });

        new ConsumerService().init(app);

        get("/login", (request, response) ->
                new spark.ModelAndView(new HashMap<>(), "index.ftl"), new FreeMarkerEngine());

        get("/socialauth", (request, response) -> {
            SocialAuthManager manager = getSocialAuthManager(request);

            String returnToUrl = request.url().replace("socialauth", "authSuccess");
            System.out.println(returnToUrl);
            // returnToUrl =
            // "http://opensource.brickred.com/socialauth-struts-demo/socialAuthSuccessAction.do";
            try {
                String url = manager.getAuthenticationUrl(request.queryParams("id"), returnToUrl);
                response.redirect(url);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return null;
        });

        get("/authSuccess", (request, response) -> {
            SocialAuthManager manager = getSocialAuthManager(request);
            try {
                manager.connect(request.queryMap().toMap().entrySet().stream()
                        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()[0])));
                AuthProvider provider = manager.getCurrentAuthProvider();
                if(manager == null || provider == null) {
                    response.redirect("/login");
                } else {

                    try {
                        String validatedId = provider.getUserProfile().getValidatedId();
                        Optional<User> user = app.getUserManager().getUser(validatedId);
                        if (!user.isPresent()) {
                            return Login.this.registration(provider);
                        } else {
                            Login.resetCookies(request, response, validatedId, user.get());
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.redirect("/login");
            }
            return null;
        }, new FreeMarkerEngine());
    }

    private spark.ModelAndView registration(final AuthProvider provider)
            throws Exception {
        Profile profile = provider.getUserProfile();
        if (profile.getFullName() == null) {
            String name = null;
            if (profile.getFirstName() != null) {
                name = profile.getFirstName();
            }
            if (profile.getLastName() != null) {
                if (profile.getFirstName() != null) {
                    name += " " + profile.getLastName();
                } else {
                    name = profile.getLastName();
                }
            }
            if (name == null && profile.getDisplayName() != null) {
                name = profile.getDisplayName();
            }
            if (name != null) {
                profile.setFullName(name);
            }
        }
        return new spark.ModelAndView(
                toMap("profile", profile),
                "registrationForm.ftl");
    }

    private SocialAuthManager getSocialAuthManager(Request request) {
        SocialAuthManager manager;
        Session session = request.session(true);
        if (session.attribute("socialAuthManager") != null) {
            manager = session.attribute("socialAuthManager");
//                if ("signout".equals(mode)) {
//                    manager.disconnectProvider(id);
//                    return "home";
//                }
        } else {
            try {
                InputStream in = Login.class.getClassLoader()
                        .getResourceAsStream("oauth_consumer.properties");
                SocialAuthConfig conf = SocialAuthConfig.getDefault();
                conf.load(in);
                manager = new SocialAuthManager();
                manager.setSocialAuthConfig(conf);
                session.attribute("socialAuthManager", manager);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return manager;
    }
}
