package com.github.averyregier.club.view;

import com.github.averyregier.club.application.ClubApplication;
import com.github.averyregier.club.broker.ProviderBroker;
import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.login.Provider;

import java.util.Collections;

import static spark.Spark.*;

/**
 * Created by avery on 6/20/15.
 */
public class ProviderSetup extends BaseController {
    public void init(ClubApplication app) {
        path("/provider", ()->{
            before("", (request, response) -> {
                User user = getUser(request);
                if(user.asClubLeader().isPresent()) {

                } else {
                    halt();
                }
            });

            get("", (request, response)-> render(
                    Collections.emptyMap(),
                    "provider.ftl"));

            post("", (request, response) -> {

                String id = request.queryParams("providerId");
                String providerName = request.queryParams("providerName");
                String site = request.queryParams("site");
                String image = request.queryParams("image");
                String clientKey = request.queryParams("clientKey");
                String clientSecret = request.queryParams("clientSecret");

                new ProviderBroker(app.getConnector()).persist(new Provider(
                        id, providerName, image, site, clientKey, clientSecret));

                return gotoMy(response);
            });
        });
    }
}
