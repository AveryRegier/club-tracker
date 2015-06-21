package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.ProviderRecord;
import com.github.averyregier.club.domain.login.Provider;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Result;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.averyregier.club.db.tables.Provider.PROVIDER;

/**
 * Created by avery on 4/11/15.
 */
public class ProviderBroker extends Broker<Provider> {
    protected ProviderBroker(Connector connector) {
        super(connector);
    }

    @Override
    protected void persist(Provider thing, DSLContext create) {
        if(create.insertInto(PROVIDER)
                .set(PROVIDER.PROVIDER_ID, thing.getId())
                .set(mapFields(thing))
                .onDuplicateKeyUpdate()
                .set(mapFields(thing))
                .execute() != 1)
        {
            fail("Provider persistence failed: "+thing);
        }
    }

    private Map<? extends Field<?>, ?> mapFields(Provider thing) {
        return JooqUtil.<ProviderRecord>map()
                .set(PROVIDER.NAME, thing.getName())
                .set(PROVIDER.IMAGE, thing.getImage())
                .set(PROVIDER.SITE, thing.getSite())
                .set(PROVIDER.CLIENT_KEY, thing.getClientKey())
                .set(PROVIDER.SECRET, thing.getSecret())
                .build();
    }

    public List<Provider> find() {
        return query(create->{
            Result<ProviderRecord> records = create.selectFrom(PROVIDER).fetch();
            return records.stream().map(r-> new Provider(
                    r.getProviderId(), r.getName(), r.getImage(), r.getSite(), r.getClientKey(), r.getSecret()
            )).collect(Collectors.toList());
        });
    }
}
