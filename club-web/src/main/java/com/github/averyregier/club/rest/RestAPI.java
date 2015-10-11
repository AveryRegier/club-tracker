package com.github.averyregier.club.rest;

import com.github.averyregier.club.application.ClubApplication;
import com.github.averyregier.club.view.ModelMaker;

import static spark.Spark.get;

/**
 * Created by avery on 8/30/14.
 */
public class RestAPI extends ModelMaker {
    public void init(ClubApplication app) {
        get("/protected/hello", (request, response) -> {
            String name = getUser(request).getName().getFullName();
            return "Hello "+ name +"!  You need to be invited to use this application.";
        });
    }
}
