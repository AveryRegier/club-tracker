package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.LoginRecord;
import com.github.averyregier.club.domain.PersonManager;
import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.view.UserBean;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.TableField;

import java.util.Map;
import java.util.Optional;

import static com.github.averyregier.club.db.tables.Login.LOGIN;
import static com.github.averyregier.club.domain.utility.UtilityMethods.convert;

/**
 * Created by avery on 4/23/15.
 */
public class LoginBroker extends Broker<User.Login> {
    public LoginBroker(Connector connector) {
        super(connector);
    }

    @Override
    protected void persist(User.Login login, DSLContext create) {
        if(create.insertInto(LOGIN)
                .set(LOGIN.ID, login.getID().getBytes())
                .set(LOGIN.PROVIDER_ID, login.getProviderID())
                .set(LOGIN.UNIQUE_ID, login.getUniqueID())
                .set(mapFields(login))
                .onDuplicateKeyUpdate()
                .set(mapFields(login))
                .execute() != 1) {
            fail("Login persistence failed: " + login.getID() + ", "+login.getProviderID() + ", "+login.getUniqueID());
        }
    }

    private Map<TableField<LoginRecord, ?>, Object> mapFields(User.Login login) {
        return JooqUtil.<LoginRecord>map()
                .set(LOGIN.AUTH, login.getAuth())
                .build();
    }

    public Optional<User> find(String providerId, String uniqueID, final PersonManager manager) {
        return query((create)->{
            Result<LoginRecord> result = create.selectFrom(LOGIN)
                    .where(LOGIN.PROVIDER_ID.eq(providerId))
                    .and(LOGIN.UNIQUE_ID.eq(uniqueID))
                    .fetch();
            return result.stream().findFirst().map(r->{
                User user = new User(
                        manager.lookup(convert(r.getId())).get(), r.getAuth());
                UserBean bean = new UserBean();
                bean.setUniqueId(uniqueID);
                bean.setProviderId(providerId);
                user.update(bean);
                return user;
            });
        });
    }

}
