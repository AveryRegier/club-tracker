package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.LoginRecord;
import com.github.averyregier.club.domain.User;
import org.jooq.DSLContext;
import org.jooq.TableField;

import java.util.Map;

import static com.github.averyregier.club.db.tables.Login.LOGIN;

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
}
