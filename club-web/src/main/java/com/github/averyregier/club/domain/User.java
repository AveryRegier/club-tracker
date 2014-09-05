package com.github.averyregier.club.domain;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;

/**
 * Created by avery on 9/2/14.
 */
public class User {
    private String auth;
    private String name;

    public String resetAuth() {
        byte[] bytes = new byte[10];
        new Random(System.currentTimeMillis()).nextBytes(bytes);
        auth = new String(bytes);
        try {
            auth = URLEncoder.encode(auth, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return auth;
    }

    public boolean authenticate(String auth) {
        return auth != null && auth.equals(this.auth);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
