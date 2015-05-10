package com.github.averyregier.club.view;

import com.github.averyregier.club.application.ClubApplication;
import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.ClubLeader;
import com.github.averyregier.club.domain.club.Family;
import com.github.averyregier.club.domain.club.Program;
import com.github.averyregier.club.domain.club.RegistrationInformation;
import org.brickred.socialauth.Profile;
import spark.Request;
import spark.Response;

import java.util.Map;

import static spark.Spark.before;
import static spark.Spark.halt;

/**
 * Created by avery on 1/10/15.
 */
public class FastSetup {
    public void init(ClubApplication app) {
        before("/test-setup", (request, response) -> {
            User user = setupJohnDoe(app);
            synchronized (this) {
                if (app.getProgram() == null) {

                    Program program = app.setupProgram("ABC", "AWANA", "en_US");
                    program.getCurriculum().getSeries().stream().forEach(program::addClub);

                    program.assign(user, ClubLeader.LeadershipRole.COMMANDER);
                    program.getClubs().stream().findFirst().ifPresent(c -> c.recruit(user));

                    registerDoeFamily(user, program);

                    Family family = registerSmithFamily(app);

                    signSomeSections(user, family);
                }
            }
            goToMy(request, response, user);
            halt();
        });

    }

    private void signSomeSections(User user, Family family) {
        family.getClubbers().stream().flatMap(c->c
                .getNextSections(10).stream())
                .forEach(r->r.sign(user.asListener().get(), ""));
    }

    private Family registerSmithFamily(ClubApplication app) {
        Profile profile = new Profile();
        profile.setFirstName("Mary");
        profile.setLastName("Smith");
        profile.setGender("female");
        profile.setEmail("mary.smith@example.com");
        profile.setValidatedId("Example-ID-For-Mary-Smith");
        User user = Login.setupUser(app, profile);

        Program program = app.getProgram();

        RegistrationInformation form = program.createRegistrationForm(user);
        Map<String, String> fields = form.getFields();
        fields.put("child1.childName.given", "Joe");
        fields.put("child1.childName.surname", "Smith");
        fields.put("child1.gender", "MALE");
        fields.put("child1.ageGroup", "THIRD_GRADE");
        return program.updateRegistrationForm(fields).register(user);
    }

    private void registerDoeFamily(User user, Program program) {
        RegistrationInformation form = program.createRegistrationForm(user);
        Map<String, String> fields = form.getFields();
        fields.put("child1.childName.given", "Betty");
        fields.put("child1.childName.surname", "Doe");
        fields.put("child1.gender", "FEMALE");
        fields.put("child1.ageGroup", "FOURTH_GRADE");
        Family family = program.updateRegistrationForm(fields).register(user);
    }

    private void goToMy(Request request, Response response, User user) {
        String context = request.contextPath();
        context = context == null ? "" : context;
        String location = context + "/protected/my";
        request.session().attribute("location", location);
        Login.resetCookies(request, response, user.getLoginInformation().getUniqueID(), user);
    }

    private User setupJohnDoe(ClubApplication app) {
        Profile profile = new Profile();
        profile.setFirstName("John");
        profile.setLastName("Doe");
        profile.setGender("male");
        profile.setEmail("john.doe@example.com");
        profile.setValidatedId("Example-ID-For-John-Doe");
        return Login.setupUser(app, profile);
    }
}
