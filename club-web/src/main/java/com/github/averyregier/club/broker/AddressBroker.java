package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.AddressRecord;
import com.github.averyregier.club.domain.club.Address;
import com.github.averyregier.club.domain.club.adapter.AddressAdapter;
import com.github.averyregier.club.domain.utility.adapter.CountryValue;
import org.jooq.DSLContext;
import org.jooq.TableField;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.github.averyregier.club.db.tables.Address.ADDRESS;
import static com.github.averyregier.club.domain.utility.UtilityMethods.convert;

/**
 * Created by avery on 8/17/15.
 */
public class AddressBroker extends Broker<Address> {
    protected AddressBroker(Connector connector) {
        super(connector);
    }

    @Override
    protected void persist(Address address, DSLContext create) {
        if(create.insertInto(ADDRESS)
                .set(ADDRESS.ID, address.getId().getBytes())
                .set(mapFields(address))
                .onDuplicateKeyUpdate()
                .set(mapFields(address))
                .execute() != 1) {
            fail("Award persistence failed: " + address.getId());
        }
    }

    private Map<TableField<AddressRecord, ?>, Object> mapFields(Address address) {
        return JooqUtil.<AddressRecord>map()
                .set(ADDRESS.LINE1, address.getLine1())
                .set(ADDRESS.LINE2, address.getLine2())
                .set(ADDRESS.CITY, address.getCity())
                .set(ADDRESS.POSTAL_CODE, address.getPostalCode())
                .set(ADDRESS.TERRITORY, address.getTerritory())
                .set(ADDRESS.COUNTRY, address.getCountry().getValue())
                .build();
    }

    public Optional<Address> find(String id) {
        return query(findAddressFn(id));
    }

    private Function<DSLContext, Optional<Address>> findAddressFn(String id) {
        return create -> {
            AddressRecord record = create.selectFrom(ADDRESS).where(ADDRESS.ID.eq(id.getBytes())).fetchOne();
            if (record == null) return Optional.empty();
            return Optional.of(map(record));
        };
    }

    private Address map(AddressRecord record) {
        return new AddressAdapter(
                convert(record.getId()),
                record.getLine1(),
                record.getLine2(),
                record.getCity(),
                record.getPostalCode(),
                record.getTerritory(),
                CountryValue.findCountry(record.getCountry()));
    }
}
