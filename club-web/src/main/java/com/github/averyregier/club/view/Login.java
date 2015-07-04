package com.github.averyregier.club.view;

import com.github.averyregier.club.application.ClubApplication;
import com.github.averyregier.club.broker.ProviderBroker;
import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.login.Provider;
import org.brickred.socialauth.AuthProvider;
import org.brickred.socialauth.Profile;
import org.brickred.socialauth.SocialAuthConfig;
import org.brickred.socialauth.SocialAuthManager;
import org.brickred.socialauth.util.BirthDate;
import spark.Request;
import spark.Response;
import spark.Session;
import spark.template.freemarker.FreeMarkerEngine;

import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static spark.Spark.*;

/**
 * Created by avery on 8/30/14.
 */
public class Login extends ModelMaker {
    public static void resetCookies(Request req, Response httpResponse, String providerId, String identifier, User user) {
        httpResponse.cookie("auth", user.resetAuth(), 60 * 60 * 3, false);
        httpResponse.cookie("userID", identifier, 60*60*3, false);
        httpResponse.cookie("provider", providerId, 60*60*3, false);
        String location = req.session().attribute("location");
        if(location == null) {
            location = "/protected/hello";
        }
        httpResponse.redirect(location);
        req.session().removeAttribute("location");
    }

    public static void resetCookies(Request req, Response res, User user) {
        resetCookies(req, res,
                user.getLoginInformation().getProviderID(),
                user.getLoginInformation().getUniqueID(),
                user);
    }

        public void init(final ClubApplication app) {
        before("/protected/*", (request, response) -> {
            // ... check if authenticated
            String auth = request.cookie("auth");
            if(auth != null) {
                Optional<User> user = app.getUserManager().getUser(
                        request.cookie("provider"), request.cookie("userID"));
                if(user.isPresent() && user.get().authenticate(auth)) {
                    request.attribute("user", user);
                    return;
                }
            }
            request.session().attribute("location", request.url());
            String context = request.contextPath();
            context = context == null ? "" : context;
            response.redirect(context + "/login");
            halt();
        });

        new ConsumerService().init(app);

        get("/login", (request, response) ->
                new spark.ModelAndView(toMap("providers",
                        getProviders(app)
                        ), "index.ftl"), new FreeMarkerEngine());

        get("/socialauth", (request, response) -> {
            SocialAuthManager manager = getSocialAuthManager(request, app);

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
            SocialAuthManager manager = getSocialAuthManager(request, app);
            try {
                manager.connect(getRequestParams(request));
                AuthProvider provider = manager.getCurrentAuthProvider();
                if(provider == null) {
                    response.redirect("/login");
                } else {
                    try {
                        User auser = setupUser(app, provider.getUserProfile());
                        resetCookies(request, response, auser);
//                            return Login.this.registration(provider);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.redirect("/login");
            }
            return null;
        });
    }

    private Map<String, String> getRequestParams(Request request) {
        return request.queryMap().toMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()[0]));
    }

    private List<Provider> getProviders(ClubApplication app) {
        return new ProviderBroker(app.getConnector()).find();
    }

    public static User setupUser(ClubApplication app, Profile userProfile) {
        UserBean userBean = mapUser(userProfile);
        User user = app.getUserManager()
                .getUser(userProfile.getProviderId(), userBean.getUniqueId())
                .orElseGet(() -> app.getUserManager().createUser(
                        userProfile.getProviderId(), userBean.getUniqueId()));
        user.update(userBean);
        return user;
    }

    private static UserBean mapUser(Profile profile) {
        UserBean user = new UserBean();
        user.setEmail(profile.getEmail());
        String firstName = profile.getFirstName();
        if(!empty(firstName))
            user.setFirstName(firstName);
        String lastName = profile.getLastName();
        if(!empty(lastName)) {
            user.setLastName(lastName);
        } else if(!empty(profile.getFullName())) {
            mapSplitName(profile.getFullName(), user);
        } else if(!empty(profile.getDisplayName())) {
            mapSplitName(profile.getDisplayName(), user);
        }
        user.setDisplayName(profile.getDisplayName());
        BirthDate dob = profile.getDob();
        if(dob != null) {
            user.setDob(dob.getDay() + "/" + dob.getMonth() + "/" + dob.getYear());
        }
        user.setCountry(profile.getCountry());
        user.setLanguage(profile.getLanguage());
        user.setGender(profile.getGender());
        user.setLocation(profile.getLocation());
        user.setProfileImageURL(profile.getProfileImageURL());
        user.setUniqueId(profile.getValidatedId());
        return user;
    }

    private static void mapSplitName(String fullName, UserBean user) {
        String[] parts = fullName.trim().split("\\s");
        if(parts.length > 0) {
            user.setFirstName(parts[0]);
        }
        if(parts.length > 1) {
            int lastIndex = parts.length - 1;
            user.setLastName(parts[lastIndex]);
            // if middle names were on user bean set them here
        }
    }

    private static boolean empty(String s) {
        return s == null || s.trim().isEmpty();
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

    private SocialAuthManager getSocialAuthManager(Request request, ClubApplication app) {
        Session session = request.session(true);
        SocialAuthManager manager = session.attribute("socialAuthManager");
        if (manager == null) {
            try {
                List<Provider> providers = getProviders(app);
                String propString = providers.stream().map(p -> {
                    return "#" + p.getName() + "\n" +
                            p.getSite() + ".consumer_key = " + p.getClientKey() + "\n" +
                            p.getSite() + ".consumer_secret = " + p.getSecret() + "\n";
                }).collect(joining("\n"));

//                InputStream in = Login.class.getClassLoader()
//                        .getResourceAsStream("oauth_consumer.properties");

                SocialAuthConfig conf = SocialAuthConfig.getDefault();
                Properties properties = new Properties();
                properties.load(new StringReader(propString));
                conf.setApplicationProperties(properties);
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
