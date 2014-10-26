package com.github.averyregier.club.view;

import com.github.averyregier.club.application.ClubApplication;
import com.github.averyregier.club.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.Optional;

import static spark.Spark.*;

public class RegistrationController extends ModelMaker {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public void init(ClubApplication app) {
        post("/submitRegistration", (request, response) -> {
            logger.info("Submitting registration");

            UserBean bean = mapUser(request);

            User user = app.getUserManager().createUser(bean.getUniqueId());
            user.update(bean);
            Login.resetCookies(request, response, user.getId(), user);
            return null;
//            request.attribute("user", bean);
//            return new spark.ModelAndView(new HashMap<>(), "registrationSuccess.ftl");
        });

        before("/protected/*/*", (request, response) -> {
            if(app.getProgram() == null) {
                response.redirect("/protected/setup");
                halt();
            }
        });

        get("/protected/*/family", (request, response) ->{
            User user = ((Optional<User>) request.attribute("user")).get();
            return new spark.ModelAndView(
                    toMap("regInfo", app.getProgram().createRegistrationForm(user)),
                    "family.ftl");
        }, new FreeMarkerEngine());
    }

    private UserBean mapUser(Request request) {
        UserBean user = new UserBean();
        user.setEmail(request.queryParams("email"));
        user.setName(request.queryParams("name"));
        user.setDob(request.queryParams("dob"));
        user.setCountry(request.queryParams("country"));
        user.setLanguage(request.queryParams("language"));
        user.setGender(request.queryParams("gender"));
        user.setLanguage(request.queryParams("location"));
        user.setProfileImageURL(request.queryParams("profileImageURL"));
        user.setUniqueId(request.queryParams("uniqueId"));
        return user;
    }

}
