package com.github.averyregier.club.domain;

import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.view.UserBean;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

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

    private User getUserObject(UserBean bean, Consumer<User> setupFn, Supplier<Person> personSupplier) {
        return getUser(bean.getProviderId(), bean.getUniqueId()).orElseGet(() -> {
            User u = new User(personSupplier.get());
            setupFn.accept(u);
            u.updateLogin(bean);
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
        return getUserObject(bean, (u) -> {
        }, personManager::createPerson);
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
        return syncUser(bean, personManager::createPerson);
    }

    public User acceptInvite(UserBean bean, Person person) {
        return syncUser(bean, ()->person);
    }

    private User syncUser(UserBean bean, Supplier<Person> createPerson) {
        User user = getUserObject(bean, User::resetAuth, createPerson);
        if(user.updateLogin(bean) | user.resetAuthIfNeeded()) {
            updateLogin(user);
        }
        if(user.updateIdentity(bean)) {
            updateIdentity(user);
        }
        return user;
    }

    protected void updateLogin(User user) {}
    protected void updateIdentity(User user) {}
}
