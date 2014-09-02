package com.github.averyregier.club.application;

import com.github.averyregier.club.view.Login;
import com.github.averyregier.club.rest.RestAPI;
import spark.servlet.SparkApplication;

import static spark.Spark.exception;
import static spark.SparkBase.*;

/**
 * Created by avery on 8/30/14.
 */
public class ClubApplication implements SparkApplication {
    public static void main(String... args) {
        if(args.length > 0) {
            setPort(Integer.parseInt(args[0]));
        }
        new ClubApplication().init();
    }

    @Override
    public void init() {
        exception(Exception.class, (e, request, response) -> {
            response.status(404);
            response.body(e.getLocalizedMessage());
        });

        new Login().init();
        new RestAPI().init();
    }
}
