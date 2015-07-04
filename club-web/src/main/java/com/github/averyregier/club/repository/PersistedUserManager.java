package com.github.averyregier.club.repository;

import com.github.averyregier.club.broker.LoginBroker;
import com.github.averyregier.club.domain.PersonManager;
import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.UserManager;
import org.jooq.exception.DataAccessException;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Created by avery on 7/3/15.
 */
public class PersistedUserManager extends UserManager {
    private Supplier<LoginBroker> brokerSupplier;

    public PersistedUserManager(Supplier<LoginBroker> brokerSupplier) {
        this.brokerSupplier = brokerSupplier;
    }

    public PersistedUserManager(PersonManager personManager, Supplier<LoginBroker> brokerSupplier) {
        super(personManager);
        this.brokerSupplier = brokerSupplier;
    }

    @Override
    public Optional<User> getUser(String providerId, String userID) {
        Optional<User> user = super.getUser(providerId, userID);
        if(!user.isPresent()) {
            user = getLoginBroker().find(providerId, userID, personManager);
            if(user.isPresent()) {
                super.putUser(userID, user.get());
            }
        }
        return user;
    }

    private LoginBroker getLoginBroker() {
        return brokerSupplier.get();
    }

    @Override
    public User createUser(String providerId, String userID) {
        return super.createUser(providerId, userID);
    }

    @Override
    protected User putUser(String userID, User user) {
        try {
            getLoginBroker().persist(user.getLoginInformation());
            return super.putUser(userID, user);
        } catch(DataAccessException e) {
            return getUser(user.getLoginInformation().getProviderID(), userID).orElseThrow(()->e);
        }
    }
}
