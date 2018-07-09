package com.github.averyregier.club.view;

import com.github.averyregier.club.application.ClubApplication;
import com.github.averyregier.club.broker.ProviderBroker;
import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.club.adapter.SettingsAdapter;
import com.github.averyregier.club.domain.login.Provider;
import org.brickred.socialauth.Profile;
import spark.Request;
import spark.Response;

import java.util.EnumSet;
import java.util.Map;
import java.util.UUID;

import static spark.Spark.before;
import static spark.Spark.halt;

/**
 * Created by avery on 1/10/15.
 */
public class FastSetup {
    public void init(ClubApplication app) {
        before("/test-setup", (request, response) -> {

            User user;
            if (new ProviderBroker(app.getConnector()).find().isEmpty()) {
                new ProviderBroker(app.getConnector()).persist(new Provider("example", "Example", "", "", "", ""));
                user = setupJohnDoe(app);
                synchronized (this) {
                    String demoId = new UUID(1, 1).toString();
                    if (app.getProgram(demoId) == null) {

                        Program program = app.setupProgramWithId("ABC", "AWANA", "en_US", demoId);
                        program.getCurriculum().getSeries().stream()
                                .map(program::addClub)
                                .filter(c -> c.getShortCode().equals("TnT"))
                                .forEach(c -> {
                                    c.replacePolicies(EnumSet.of(Policy.noSectionAwards), new SettingsAdapter(c));
                                });

                        program.assign(user, ClubLeader.LeadershipRole.COMMANDER);
                        program.getClubs().stream().skip(3).findFirst().ifPresent(c -> c.recruit(user));

                        registerDoeFamily(user, program);

                        Family family = registerSmithFamily(app, program);

                        signSomeSections(user, family);
                    }
                }
            } else {
                user = setupJohnDoe(app);
            }
            goToMy(request, response, user);
            halt();
        });

    }

    private void signSomeSections(User user, Family family) {
        family.getClubbers().stream().flatMap(c -> c
                .getNextSections(10).stream())
                .forEach(r -> r.sign(user.asListener().get(), ""));
    }

    private Family registerSmithFamily(ClubApplication app, Program program) {
        Profile profile = new Profile();
        profile.setFirstName("Mary");
        profile.setLastName("Smith");
        profile.setGender("female");
        profile.setEmail("mary.smith@example.com");
        profile.setValidatedId("Example-ID-For-Mary-Smith");
        profile.setProviderId("example");
        User user = Login.setupUser(app, profile, null);

        RegistrationInformation form = program.createRegistrationForm(user);
        Map<String, String> fields = form.getFields();
        fields.put("child1.childName.given", "Joe");
        fields.put("child1.childName.surname", "Smith");
        fields.put("child1.gender", "MALE");
        fields.put("child1.ageGroup", "THIRD_GRADE");
        fields.put("child2.childName.given", "Jimmy");
        fields.put("child2.childName.surname", "Smith");
        fields.put("child2.gender", "MALE");
        fields.put("child2.ageGroup", "FIRST_GRADE");
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
        Login.resetCookies(request, response, user);
    }

    private User setupJohnDoe(ClubApplication app) {
        Profile profile = new Profile();
        profile.setFirstName("John");
        profile.setLastName("Doe");
        profile.setGender("male");
        profile.setEmail("john.doe@example.com");
        profile.setValidatedId("Example-ID-For-John-Doe");
        profile.setProviderId("example");
        return Login.setupUser(app, profile, null);
    }
}
