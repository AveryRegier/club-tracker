package com.github.averyregier.club.view;

import com.github.averyregier.club.application.ClubApplication;
import com.github.averyregier.club.domain.User;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.HashMap;
import java.util.Optional;

import static spark.Spark.before;
import static spark.Spark.get;

/**
 * Created by avery on 8/30/14.
 */
public class Login {
    public void init(final ClubApplication app) {
        before("/protected/*", (request, response) -> {
            // ... check if authenticated
            String auth = request.cookie("auth");
            if(auth != null) {
                Optional<User> user = app.getUserManager().getUser(request.cookie("userID"));
                if(user.isPresent() && user.get().authenticate(auth)) {
                    return;
                }
            }
            response.cookie("location", request.raw().getRequestURI(), 60*50*5);
            String context = request.contextPath();
            context = context == null ? "" : context;
            response.redirect(context +"/login");
        //    halt(401, "Go Away!");
        });

        new ConsumerService().init(app);

        get("/login", (request, response) ->
                new ModelAndView(new HashMap<>(), "index.ftl"), new FreeMarkerEngine());


    }
}
