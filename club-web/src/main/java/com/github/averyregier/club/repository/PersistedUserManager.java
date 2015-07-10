package com.github.averyregier.club.repository;

import com.github.averyregier.club.broker.LoginBroker;
import com.github.averyregier.club.domain.PersonManager;
import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.UserManager;
import com.github.averyregier.club.view.UserBean;
import org.jooq.exception.DataAccessException;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Created by avery on 7/3/15.
 */
public class PersistedUserManager extends UserManager {
    private Supplier<LoginBroker> brokerSupplier;

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
    public User createUser(UserBean bean) {
        return super.createUser(bean);
    }

    @Override
    protected User putUser(String userID, User user) {
        try {
            updateLogin(user);
            return super.putUser(userID, user);
        } catch(DataAccessException e) {
            return getUser(user.getLoginInformation().getProviderID(), userID)
                    .orElseThrow(()->e);
        }
    }

    @Override
    protected void updateLogin(User user) {
        getLoginBroker().persist(user.getLoginInformation());
    }

    @Override
    protected void updateIdentity(User user) {
        personManager.sync(user);
    }
}
