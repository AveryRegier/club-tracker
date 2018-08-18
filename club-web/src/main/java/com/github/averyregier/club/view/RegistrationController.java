package com.github.averyregier.club.view;

import com.github.averyregier.club.application.ClubApplication;
import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.utility.UtilityMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static com.github.averyregier.club.domain.utility.UtilityMethods.optMap;
import static spark.Spark.*;

public class RegistrationController extends BaseController {

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
            redirectKiosk(request, response);
        });
        
        before("/protected/my", this::redirectKiosk);

        before("/register/:name", (request, response) -> {
            app.getProgramByName(request.params(":name")).ifPresent(program -> {
                request.session(true).attribute("program", program);
                response.redirect("/protected/" + program.getId() + "/family");
                halt();
            });
        });

        get("/protected/:id/family", (request, response) -> {
            User user = getUser(request);
            return createRegistrationView(request, app.getProgram(request.params(":id")).createRegistrationForm(user));
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
                return createRegistrationView(request, app.getProgram(programId).createRegistrationForm(familyParent(app, familyId)));
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
                postRegistrationRedirect(response, family);
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
                Program program = app.getProgram(programId);
                RegistrationInformation form = program.updateRegistrationForm(UtilityMethods.map("action", "child").build());
                return createRegistrationView(request, form);
            }
            response.redirect("/protected/my");
            halt();
            return null;
        }, new FreeMarkerEngine());

        get("/protected/:id/newWorker", (request, response) -> {
            User user = getUser(request);
            String programId = request.params(":id");
            if (leadsProgram(user, programId)) {
                return createRegistrationView(request, app.getProgram(programId).createRegistrationForm());
            }
            response.redirect("/protected/my");
            halt();
            return null;
        }, new FreeMarkerEngine());

        before("/protected/:id/newClubber", (request, response) -> {
            validateMayCreateNewPeople(app, request, response,
                    f -> postRegistrationRedirect(response, f));
        });

        post("/protected/:id/newClubber", (request, response) -> {
            return updateRegistrationForm(app, request);
        }, new FreeMarkerEngine());

        before("/protected/:id/newWorker", (request, response) -> {
            validateMayCreateNewPeople(app, request, response,
                    f -> postWorkerRegistrationRedirect(getUser(request), response, f));
        });

        post("/protected/:id/newWorker", (request, response) -> {
            return updateRegistrationForm(app, request);
        }, new FreeMarkerEngine());
    }

    public void redirectKiosk(Request request, Response response) {
        getUser(request).asClubLeader().ifPresent(l->{
            if(l.getLeadershipRole().isKiosk()) {
                if(!request.pathInfo().endsWith("/newClubber")) {
                    response.redirect("/protected/"+ l.getProgram().getId()+"/newClubber");
                    halt();
                }
            }
        });
    }

    private void validateMayCreateNewPeople(
            ClubApplication app, Request request, Response response, Consumer<Family> fn) {
        User user = getUser(request);
        String programId = request.params(":id");
        if (!leadsProgram(user, programId)) {
            response.redirect("/protected/my");
            halt();
        } else if ("submit".equals(request.queryParams("submit"))) {
            logger.info("Submitting family registration");
            Family family = updateForm(app, request).register();
            fn.accept(family);
            halt();
        }
    }

    private void postRegistrationRedirect(Response response, Family family) {
        if (family.shouldInvite()) {
            response.redirect("/protected/family/" + family.getId() + "/invite");
        } else {
            response.redirect("/protected/my");
        }
    }

    private void postWorkerRegistrationRedirect(User user, Response response, Family family) {
        Optional<Parent> aParent = family.getParents().stream()
                .filter(p -> !p.asListener().isPresent())
                .filter(p -> !p.asClubLeader().isPresent())
                .findFirst();
        if (aParent.isPresent()) {
            Optional<Club> leadingClub = optMap(user.asClubLeader(), ClubMember::getClub);
            if (leadingClub.isPresent()) {
                response.redirect("/protected/club/" + leadingClub.get().getId() + "/workers/" + aParent.get().getId());
                return;
            }
        }
        postRegistrationRedirect(response, family);
    }

    private RegistrationInformation updateForm(ClubApplication app, Request request) {
        Map<String, String> collect = UtilityMethods.transformToSingleValueMap(request.queryMap().toMap());
        return app.getProgram(request.params(":id")).updateRegistrationForm(collect);
    }

    private ModelAndView updateRegistrationForm(ClubApplication app, Request request) {
        logger.info("Adding additional family members");
        RegistrationInformation form = updateForm(app, request);
        return createRegistrationView(request, form);
    }

    private ModelAndView createRegistrationView(Request request, RegistrationInformation form) {
        return new ModelAndView(
                newModel(request, "Registration").put("regInfo", form).build(),
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
        return user.asClubLeader().map(l -> l.getProgram().getId().equals(programId)).filter(x -> x).isPresent();
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
