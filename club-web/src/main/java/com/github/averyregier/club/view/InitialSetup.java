package com.github.averyregier.club.view;

import com.github.averyregier.club.application.ClubApplication;
import com.github.averyregier.club.broker.ProviderBroker;
import com.github.averyregier.club.domain.login.Provider;

import java.util.List;

import static spark.Spark.*;

/**
 * Created by avery on 6/20/15.
 */
public class InitialSetup extends BaseController {
    public void init(ClubApplication app) {
        path("/initial-setup", ()->{
            before("", (request, response) -> {
                List<Provider> providers = new ProviderBroker(app.getConnector()).find();
                if (!providers.isEmpty()) {
                    halt();
                }
            });

            get("", (request, response)-> render(
                    newModel(request, "Add Login Provider").build(),
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

                response.redirect("/protected/setup");
                return null;
            });
        });
    }
}
