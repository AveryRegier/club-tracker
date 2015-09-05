package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.CeremonyRecord;
import com.github.averyregier.club.domain.club.Ceremony;
import org.jooq.DSLContext;
import org.jooq.TableField;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.github.averyregier.club.db.tables.Ceremony.CEREMONY;

/**
 * Created by avery on 3/2/15.
 */
public class CeremonyBroker extends PersistenceBroker<Ceremony> {
    public CeremonyBroker(Connector connector) {
        super(connector);
    }

    @Override
    protected void persist(Ceremony ceremony, DSLContext create) {
        if(create.insertInto(CEREMONY)
                .set(CEREMONY.ID, ceremony.getId().getBytes())
                .set(mapFields(ceremony))
                .onDuplicateKeyUpdate()
                .set(mapFields(ceremony))
                .execute() != 1) {
            fail("Ceremony persistence failed: " + ceremony.getId());
        }

    }

    private Map<TableField<CeremonyRecord, ?>, Object> mapFields(Ceremony ceremony) {
        return JooqUtil.<CeremonyRecord>map()
                .set(CEREMONY.PRESENTATION_DATE, ceremony.presentationDate())
                .set(CEREMONY.NAME, ceremony.getName())
                .build();
    }

    public Optional<Ceremony> find(String ceremonyId) {
        Function<DSLContext, Optional<Ceremony>> fn = create -> {
            CeremonyRecord record = create.selectFrom(CEREMONY).where(CEREMONY.ID.eq(ceremonyId.getBytes())).fetchOne();
            if (record == null) return Optional.empty();
            LocalDate date = LocalDate.ofEpochDay(record.getPresentationDate().getTime());
            String name = record.getName();
            return Optional.of(new Ceremony() {

                @Override
                public String getName() {
                    return name;
                }

                @Override
                public String getId() {
                    return ceremonyId;
                }

                @Override
                public String getShortCode() {
                    return getName();
                }

                @Override
                public LocalDate presentationDate() {
                    return date;
                }
            });
        };
        Optional<Ceremony> query = query(fn);
        return query;
    }
}
