package com.github.averyregier.club.view;

import com.github.averyregier.club.application.ClubApplication;
import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.Family;
import com.github.averyregier.club.domain.club.Parent;
import com.github.averyregier.club.domain.club.RegistrationInformation;
import com.github.averyregier.club.domain.utility.UtilityMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.Map;
import java.util.Optional;

import static spark.Spark.*;

public class RegistrationController extends ModelMaker {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public void init(ClubApplication app) {
        post("/submitRegistration", (request, response) -> {
            logger.info("Submitting registration");

            UserBean bean = mapUser(request.cookie("provider"), request);

            User user = app.getUserManager().syncUser(bean);
            Login.resetCookies(request, response, user);
            return null;
        });

        before("/protected/*/*", (request, response) -> {
            if (!app.hasPrograms()) {
                response.redirect("/protected/setup");
                halt();
            }
        });

        get("/protected/:id/family", (request, response) -> {
            User user = getUser(request);
            return new spark.ModelAndView(
                    toMap("regInfo", app.getProgram(request.params(":id")).createRegistrationForm(user)),
                    "family.ftl");
        }, new FreeMarkerEngine());

        before("/protected/:id/family", (request, response) -> {
            if ("submit".equals(request.queryParams("submit"))) {
                logger.info("Submitting family registration");
                RegistrationInformation form = updateForm(app, request);
                Family family = form.register(getUser(request));
                postRegistrationRedirect(response, family);
                halt();
            }
        });

        post("/protected/:id/family", (request, response) -> {
            return updateRegistrationForm(app, request);
        }, new FreeMarkerEngine());

        get("/protected/:id/family/:familyId", (request, response) -> {
            User user = getUser(request);
            String programId = request.params(":id");
            String familyId = request.params(":familyId");
            if (leadsProgram(user, programId)) {
                return new spark.ModelAndView(
                        toMap("regInfo", app.getProgram(programId).createRegistrationForm(familyParent(app, familyId))),
                        "family.ftl");
            }
            response.redirect("/protected/my");
            halt();
            return null;
        }, new FreeMarkerEngine());

        before("/protected/:id/family/:familyId", (request, response) -> {
            User user = getUser(request);
            String programId = request.params(":id");
            String familyId = request.params(":familyId");
            if (!leadsProgram(user, programId)) {
                response.redirect("/protected/my");
                halt();
            } else if ("submit".equals(request.queryParams("submit"))) {
                logger.info("Submitting family registration");
                Family family = updateForm(app, request).register(familyParent(app, familyId));
                response.redirect("/protected/my");
                halt();
            }
        });

        post("/protected/:id/family/:familyId", (request, response) -> {
            return updateRegistrationForm(app, request);
        }, new FreeMarkerEngine());

        get("/protected/:id/newClubber", (request, response) -> {
            User user = getUser(request);
            String programId = request.params(":id");
            if (leadsProgram(user, programId)) {
                return new spark.ModelAndView(
                        toMap("regInfo", app.getProgram(programId).createRegistrationForm()),
                        "family.ftl");
            }
            response.redirect("/protected/my");
            halt();
            return null;
        }, new FreeMarkerEngine());

        before("/protected/:id/newClubber", (request, response) -> {
            User user = getUser(request);
            String programId = request.params(":id");
            if (!leadsProgram(user, programId)) {
                response.redirect("/protected/my");
                halt();
            } else if ("submit".equals(request.queryParams("submit"))) {
                logger.info("Submitting family registration");
                Family family = updateForm(app, request).register();
                postRegistrationRedirect(response, family);
                halt();
            }
        });

        post("/protected/:id/newClubber", (request, response) -> {
            return updateRegistrationForm(app, request);
        }, new FreeMarkerEngine());
    }

    private void postRegistrationRedirect(Response response, Family family) {
        if (shouldInvite(family)) {
            response.redirect("/protected/family/" + family.getId() + "/invite");
        } else {
            response.redirect("/protected/my");
        }
    }

    private boolean shouldInvite(Family family) {
        return family.getParents().stream() // for now, parents only, until we get clubber features
                .filter(p -> p.getEmail().isPresent())
                .filter(p -> !p.getLogin().isPresent())
                .findAny()
                .isPresent();
    }

    private RegistrationInformation updateForm(ClubApplication app, Request request) {
        Map<String, String> collect = UtilityMethods.transformToSingleValueMap(request.queryMap().toMap());
        return app.getProgram(request.params(":id")).updateRegistrationForm(collect);
    }

    private ModelAndView updateRegistrationForm(ClubApplication app, Request request) {
        logger.info("Adding additional family members");
        RegistrationInformation form = updateForm(app, request);
        return new ModelAndView(
                toMap("regInfo", form),
                "family.ftl");
    }

    private Parent familyParent(ClubApplication app, String familyId) {
        Optional<Family> family = app.getPersonManager().lookupFamily(familyId);
        Optional<Optional<Parent>> parent = family
                .map(f -> f.getParents().stream().findFirst());
        return parent
                .orElseThrow(IllegalArgumentException::new)
                .orElseThrow(IllegalArgumentException::new);
    }

    private boolean leadsProgram(User user, String programId) {
        return user.asClubLeader().map(l -> l.getProgram().getId().equals(programId)).filter(x->x).isPresent();
    }

    private UserBean mapUser(String provider, Request request) {
        UserBean user = new UserBean();
        user.setProviderId(provider);
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
