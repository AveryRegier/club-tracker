package com.github.averyregier.club.rest;

import static spark.Spark.*;

/**
 * Created by avery on 8/30/14.
 */
public class RestAPI {
    public void init() {
        get("/hello", (request, response) -> {
            return "Hello World!";
        });
    }
}
