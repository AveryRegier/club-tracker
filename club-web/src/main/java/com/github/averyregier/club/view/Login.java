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

import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import static com.github.averyregier.club.domain.utility.UtilityMethods.map;
import static java.util.stream.Collectors.joining;
import static spark.Spark.*;

/**
 * Created by avery on 8/30/14.
 */
public class Login extends BaseController {

    public static void resetCookies(Request req, Response res, User user) {
        if (!req.raw().isSecure()) {
            Session session = req.session(true);
            session.attribute("userID", user.getLoginInformation().getUniqueID());
            session.attribute("provider", user.getLoginInformation().getProviderID());
        }
        res.cookie("auth", user.getCurrentAuth(), 60 * 60 * 3, req.raw().isSecure());
        res.cookie("userID", user.getLoginInformation().getUniqueID(), 60 * 60 * 3, req.raw().isSecure());
        res.cookie("provider", user.getLoginInformation().getProviderID(), 60 * 60 * 3, req.raw().isSecure());
        String location = req.session().attribute("location");
        if (location == null || location.startsWith("/login")) {
            location = "/protected/my";
        }
        res.redirect(location);
        req.session().removeAttribute("location");
    }

    public void init(final ClubApplication app) {

        before("/", (request, response) -> {
            response.redirect("/protected/my");
        });

        before("/invite/:code", (request, response) -> {
            String code = request.params(":code");
            Optional<Invitation> invitation = findOpenInvite(app, code);
            if (invitation.isPresent()) {
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
            if (loggedIn(app, request)) return;
            redirectToLogin(request, response);
            halt();
        });

        get("/login", (request, response) ->
                render(
                        map("providers", getProviders(app))
                                .put("program", request.session().attribute("program"))
                                .build(),
                        "index.ftl"));

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
                    System.out.println("login success: " + auser.getId());
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

    private boolean loggedIn(ClubApplication app, Request request) {
        if (request.raw().isSecure()) {
            if (isLoggedInSecure(app, request)) return true;
        } else {
            if (isLoggedInInsecure(app, request)) return true;
        }
        printCookies(request);
        return false;
    }

    private void redirectToLogin(Request request, Response response) {
        request.session().attribute("location", request.url());
        String context = request.contextPath();
        context = context == null ? "" : context;
        response.redirect(context + "/login");
    }

    private void printCookies(Request request) {
        request.cookies().entrySet().forEach(e -> {
            System.out.println(e.getKey() + "=" + e.getValue());
        });
    }

    private boolean isLoggedInInsecure(ClubApplication app, Request request) {
        String provider = request.session().attribute("provider");
        String userID = request.session().attribute("userID");
        Optional<User> user = app.getUserManager().getUser(provider, userID);
        if (user.isPresent()) {
            request.attribute("user", user);
            return true;
        } else {
            System.out.println("authentication failure: " +
                    user.map(User::getId).orElse(""));
        }
        return false;
    }

    private boolean isLoggedInSecure(ClubApplication app, Request request) {
        String auth = request.cookie("auth");
        if (auth != null) {
            String provider = request.cookie("provider");
            String userID = request.cookie("userID");
            Optional<User> user = app.getUserManager().getUser(
                    provider, userID);
            if (user.isPresent() && user.get().authenticate(auth)) {
                request.attribute("user", user);
                return true;
            } else {
                System.out.println("authentication failure: " +
                        user.map(User::getId).orElse("") + "=" + auth);
            }
        } else {
            System.out.println("auth cookie not found");
        }
        return false;
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
        if (invite != null) {
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
        if (!empty(firstName))
            user.setFirstName(firstName);
        String lastName = profile.getLastName();
        if (!empty(lastName)) {
            user.setLastName(lastName);
        } else if (!empty(profile.getFullName())) {
            mapSplitName(profile.getFullName(), user);
        } else if (!empty(profile.getDisplayName())) {
            mapSplitName(profile.getDisplayName(), user);
        }
        user.setDisplayName(profile.getDisplayName());
        BirthDate dob = profile.getDob();
        if (dob != null) {
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
        if (parts.length > 0) {
            user.setFirstName(parts[0]);
        }
        if (parts.length > 1) {
            int lastIndex = parts.length - 1;
            user.setLastName(parts[lastIndex]);
            // if middle names were on user bean set them here
        }
    }

    private static boolean empty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private SocialAuthManager getSocialAuthManager(Request request, ClubApplication app) {
        Session session = request.session(true);
        SocialAuthManager manager = session.attribute("socialAuthManager");
        if (manager == null) {
            try {
                List<Provider> providers = getProviders(app);
                String propString = providers.stream()
                        .map(this::buildProviderComment)
                        .collect(joining("\n"));

//                InputStream in = Login.class.getClassLoader()
//                        .getResourceAsStream("oauth_consumer.properties");

                SocialAuthConfig conf = SocialAuthConfig.getDefault();
                Properties properties = new Properties();
                properties.load(new StringReader(propString));
                conf.setApplicationProperties(properties);
                manager = new SocialAuthManager();
                manager.setSocialAuthConfig(conf);
                manager.setPermission("facebook", Permission.AUTHENTICATE_ONLY);
                manager.setPermission("googleplus", Permission.AUTHENTICATE_ONLY);
                session.attribute("socialAuthManager", manager);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return manager;
    }

    private String buildProviderComment(Provider p) {
        return "#" + p.getName() + "\n" +
                p.getSite() + ".consumer_key = " + p.getClientKey() + "\n" +
                p.getSite() + ".consumer_secret = " + p.getSecret() + "\n";
    }
}
