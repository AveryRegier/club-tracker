package com.github.averyregier.club.rest;

import com.github.averyregier.club.application.ClubApplication;

import static spark.Spark.get;

/**
 * Created by avery on 8/30/14.
 */
public class RestAPI {
    public void init(ClubApplication app) {
        get("/protected/hello", (request, response) -> {
            String name = app.getUserManager().getUser(request.cookie("userID")).get().getName().getFullName();
            return "Hello "+ name +"!";
        });
    }
}
