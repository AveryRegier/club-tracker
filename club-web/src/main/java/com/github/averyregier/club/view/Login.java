package com.github.averyregier.club.view;

import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.HashMap;

import static spark.Spark.before;
import static spark.Spark.get;

/**
 * Created by avery on 8/30/14.
 */
public class Login {
    public void init() {
        before("/protected/*", (request, response) -> {
            // ... check if authenticated
            if(request.cookie("auth") != null) {

            } else {
                response.redirect(request.contextPath()+"/login");
            //    halt(401, "Go Away!");
            }
        });

        new ConsumerService().init();

        get("/login", (request, response) ->
                new ModelAndView(new HashMap<>(), "index.ftl"), new FreeMarkerEngine());


    }
}
