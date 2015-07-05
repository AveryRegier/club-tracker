package com.github.averyregier.club.domain;

import com.github.averyregier.club.view.UserBean;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

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

    private User getUserObject(UserBean bean, Consumer<User> setupFn) {
        return getUser(bean.getProviderId(), bean.getUniqueId()).orElseGet(() -> {
            User u = new User(personManager.createPerson());
            setupFn.accept(u);
            u.update(bean);
            u = putUser(bean.getUniqueId(), u);
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

    public User createUser(UserBean bean) {
        return getUserObject(bean, (u)->{});
    }

    public PersonManager getPersonManager() {
        return personManager;
    }

    public User createUser(String provider, String userID) {
        UserBean bean = new UserBean();
        bean.setProviderId(provider);
        bean.setUniqueId(userID);
        return createUser(bean);
    }

    public User syncUser(UserBean bean) {
        User user = getUserObject(bean, (u) -> u.resetAuth());
        boolean changed = false;
        if(!user.getLoginInformation().getAuth().isPresent()) {
            user.resetAuth();
            changed = true;
        }
        if(user.update(bean) | changed) {
            update(user);
        }
        return user;
    }

    protected void update(User user) {}
}
