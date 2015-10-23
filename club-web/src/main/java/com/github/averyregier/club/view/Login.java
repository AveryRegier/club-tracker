package com.github.averyregier.club.view;

import com.github.averyregier.club.application.ClubApplication;
import com.github.averyregier.club.broker.InviteBroker;
import com.github.averyregier.club.broker.ProviderBroker;
import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.Invitation;
import com.github.averyregier.club.domain.club.Program;
import com.github.averyregier.club.domain.login.Provider;
import org.brickred.socialauth.*;
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

import static java.util.stream.Collectors.joining;
import static spark.Spark.*;

/**
 * Created by avery on 8/30/14.
 */
public class Login extends ModelMaker {

    public static void resetCookies(Request req, Response res, User user) {
        res.cookie("auth", user.getCurrentAuth(), 60 * 60 * 3, false);
        res.cookie("userID", user.getLoginInformation().getUniqueID(), 60 * 60 * 3, false);
        res.cookie("provider", user.getLoginInformation().getProviderID(), 60 * 60 * 3, false);
        String location = req.session().attribute("location");
        if(location == null || location.startsWith("/login")) {
            location = "/protected/my";
        }
        res.redirect(location);
        req.session().removeAttribute("location");
    }

    public void init(final ClubApplication app) {
        before("/invite/:code", (request, response) -> {
            String code = request.params(":code");
            Optional<Invitation> invitation = findOpenInvite(app, code);
            if(invitation.isPresent()) {
                request.session().attribute("invite", invitation.get());
                Optional<Program> program = app.getPrograms(invitation.get().getPerson()).stream().findFirst();
                if (program.isPresent()) {
                    String programId = program.get().getId();
                    response.redirect("/protected/" + programId + "/family");
                } else {
                    response.redirect("/protected/my");
                }
            } else {
                response.redirect("/protected/my");
            }
            halt();
        });

        before("/protected/*", (request, response) -> {
            // ... check if authenticated
            String auth = request.cookie("auth");
            if (auth != null) {
                Optional<User> user = app.getUserManager().getUser(
                        request.cookie("provider"), request.cookie("userID"));
                if (user.isPresent() && user.get().authenticate(auth)) {
                    request.attribute("user", user);
                    return;
                } else {
                    System.out.println("authentication failure: "+
                            user.map(User::getId).orElse("")+"="+auth);
                }
            } else {
                System.out.println("auth cookie not found");
            }
            request.cookies().entrySet().forEach(e->{
                System.out.println(e.getKey()+"="+e.getValue());
            });
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
            Map<String, String> requestParams = getRequestParams(request);
            try {
                manager.connect(requestParams);
                AuthProvider provider = manager.getCurrentAuthProvider();
                if (provider == null) {
                    printParams(requestParams);
                    response.redirect("/login");
                } else {
                    User auser = setupUser(app, provider.getUserProfile(), request.session().attribute("invite"));
                    System.out.println("login success: "+auser.getId());
                    resetCookies(request, response, auser);
                }
            } catch (Exception e) {
                e.printStackTrace();
                printParams(requestParams);
                response.redirect("/login");
            }
            return null;
        });
    }

    private void printParams(Map<String, String> requestParams) {
        System.out.println("Login failure, params follow");
        requestParams.entrySet().stream()
                .forEach(e -> System.out.println(e.getKey() + "=" + e.getValue()));
    }

    private Optional<Invitation> findOpenInvite(ClubApplication app, String code) {
        return new InviteBroker(app).find(code)
                .stream()
                .filter(i -> !i.getCompleted().isPresent())
                .findFirst();
    }

    private List<Provider> getProviders(ClubApplication app) {
        return new ProviderBroker(app.getConnector()).find();
    }

    public static User setupUser(ClubApplication app, Profile userProfile, Invitation invite) {
        UserBean bean = mapUser(userProfile);
        if(invite != null) {
            User user = app.getUserManager().acceptInvite(bean, invite.getPerson());
            new InviteBroker(app).persist(invite.complete());
            return user;
        } else {
            return app.getUserManager().syncUser(bean);
        }
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
        user.setProviderId(profile.getProviderId());
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
                manager.setPermission("facebook", Permission.AUTHENTICATE_ONLY);
                session.attribute("socialAuthManager", manager);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return manager;
    }
}
