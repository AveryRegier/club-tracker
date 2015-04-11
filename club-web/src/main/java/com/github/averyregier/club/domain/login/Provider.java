package com.github.averyregier.club.domain.login;

/**
 * Created by avery on 4/11/15.
 */
public class Provider {
    private String id, name, image, site, clientKey, secret;

    public Provider(String id, String name, String image, String site, String clientKey, String secret) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.site = site;
        this.clientKey = clientKey;
        this.secret = secret;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getSite() {
        return site;
    }

    public String getClientKey() {
        return clientKey;
    }

    public String getSecret() {
        return secret;
    }
}
