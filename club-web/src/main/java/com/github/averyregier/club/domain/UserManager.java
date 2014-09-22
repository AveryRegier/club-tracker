package com.github.averyregier.club.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by avery on 9/2/14.
 */
public class UserManager {
    final Map<String, User> users = new HashMap<>();

//    public String authenticate(String userID, String password) {
//        try {
//            return getUserObject(userID).checkPassword(password);
//        } catch (IllegalArgumentException e) {
//            System.err.println("Login with unknown user "+userID+" attempted.");
//            return null;
//        }
//    }

    private User getUserObject(String userID) {
        User user = users.get(userID);
        if(user == null)  {
            user = new User();
            User old = users.putIfAbsent(userID, user);
            if(old != null) {
                user = old;
            }
//            new PlayerGetByLoginBroker(sp, userID).execute();
//            user = users.get(userID);
//            if(user == null) throw new IllegalArgumentException("User "+userID+" does not appear to exist.");
//            fillUser(user);
        }
        return user;
    }

    public Optional<User> getUser(String userID) {
        User user = users.get(userID);
        return Optional.ofNullable(user);
    }

    public User createUser(String userID) {
        return getUserObject(userID);
    }
}
