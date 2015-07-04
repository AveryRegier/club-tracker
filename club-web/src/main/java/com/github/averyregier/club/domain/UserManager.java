package com.github.averyregier.club.domain;

import com.github.averyregier.club.view.UserBean;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by avery on 9/2/14.
 */
public class UserManager {
    private final Map<String, User> users = new HashMap<>();
    protected final PersonManager personManager;

    public UserManager() {
        this(new PersonManager());
    }

    public UserManager(PersonManager personManager) {
        this.personManager = personManager;
    }

    private User getUserObject(String providerId, String userID) {
        return getUser(providerId, userID).orElseGet(() -> {
            User u = new User(personManager.createPerson());
            UserBean bean = new UserBean();
            bean.setProviderId(providerId);
            bean.setUniqueId(userID);
            u.update(bean);
            u = putUser(userID, u);
            return u;
        });
    }

    protected User putUser(String userID, User user) {
        User old = users.putIfAbsent(userID, user);
        if(old != null) {
            user = old;
        }
        return user;
    }

    public Optional<User> getUser(String providerId, String userID) {
        User user = users.get(userID);
        return Optional.ofNullable(user);
    }

    public User createUser(String providerId, String userID) {
        return getUserObject(providerId, userID);
    }

    public PersonManager getPersonManager() {
        return personManager;
    }
}
