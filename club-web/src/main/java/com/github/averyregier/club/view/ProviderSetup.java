package com.github.averyregier.club.view;

import com.github.averyregier.club.application.ClubApplication;
import com.github.averyregier.club.broker.ProviderBroker;
import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.login.Provider;
import spark.template.freemarker.FreeMarkerEngine;

import static spark.Spark.*;

/**
 * Created by avery on 6/20/15.
 */
public class ProviderSetup extends BaseController {
    public void init(ClubApplication app) {
        String path = "/protected/provider";

        before(path, (request, response) -> {
            User user = getUser(request);
            if(user.asClubLeader().isPresent()) {

            } else {
                halt();
            }
        });

        get(path, (request, response)->{
            return new spark.ModelAndView(
                    new Object(),
                    "provider.ftl");
        }, new FreeMarkerEngine());

        post(path, (request, response) -> {

            String id = request.queryParams("providerId");
            String providerName = request.queryParams("providerName");
            String site = request.queryParams("site");
            String image = request.queryParams("image");
            String clientKey = request.queryParams("clientKey");
            String clientSecret = request.queryParams("clientSecret");

            new ProviderBroker(app.getConnector()).persist(new Provider(
                    id, providerName, image, site, clientKey, clientSecret));

            response.redirect("/protected/my");
            return null;
        });
    }
}
