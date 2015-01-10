package com.github.averyregier.club.view;

import com.github.averyregier.club.application.ClubApplication;
import com.github.averyregier.club.domain.User;
import org.brickred.socialauth.Profile;

import static spark.Spark.before;
import static spark.Spark.halt;

/**
 * Created by avery on 1/10/15.
 */
public class FastSetup {
    public void init(ClubApplication app) {
        before("/test-setup", (request, response) -> {
            String context = request.contextPath();
            context = context == null ? "" : context;
            String location = context + "/protected/my";
            Profile profile = new Profile();
            profile.setFirstName("John");
            profile.setLastName("Doe");
            profile.setGender("male");
            profile.setEmail("john.doe@example.com");
            User user = Login.setupUser(app, profile);
            request.session().attribute("location", location);
            Login.resetCookies(request, response, user.getId(), user);
            halt();
        });

    }
}
