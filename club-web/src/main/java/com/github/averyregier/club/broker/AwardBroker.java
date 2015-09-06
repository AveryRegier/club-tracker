package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.AwardRecord;
import com.github.averyregier.club.domain.club.AwardPresentation;
import com.github.averyregier.club.domain.club.Clubber;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.utility.Named;
import com.github.averyregier.club.repository.PersistedAwardPresentation;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.TableField;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.averyregier.club.db.tables.Award.AWARD;
import static com.github.averyregier.club.domain.utility.UtilityMethods.convert;
import static com.github.averyregier.club.domain.utility.UtilityMethods.equalsAny;

/**
 * Created by avery on 3/2/15.
 */
public class AwardBroker extends PersistenceBroker<AwardPresentation> {
    public AwardBroker(Connector connector) {
        super(connector);
    }

    @Override
    protected void persist(AwardPresentation award, DSLContext create) {
        if(!equalsAny(create.insertInto(AWARD)
                .set(AWARD.ID, award.getId().getBytes())
                .set(mapFields(award))
                .onDuplicateKeyUpdate()
                .set(mapFields(award))
                .execute(), 1, 2)) {
            fail("Award persistence failed: " + award.to().getId() + " for " + award.forAccomplishment().getName());
        }
    }

    private Map<TableField<AwardRecord, ?>, Object> mapFields(AwardPresentation award) {
        return JooqUtil.<AwardRecord>map()
                .set(AWARD.CLUBBER_ID, award.to().getId())
                .set(AWARD.SECTION_ID, award.record().getSection().getId())
                .set(AWARD.TOKEN, award.token().map(Named::getName))
                .set(AWARD.ACCOMPLISHMENT, award.forAccomplishment().getName())
                .set(AWARD.PRESENTED_AT, award.presentedAt())
                .build();
    }

    public List<AwardPresentation> find(Clubber clubber, Section section) {
        return query(create->{
            Result<AwardRecord> result = create.selectFrom(AWARD)
                    .where(AWARD.CLUBBER_ID.eq(clubber.getId().getBytes()))
                    .and(AWARD.SECTION_ID.eq(section.getId())).fetch();
            return result.stream().map(record -> {
                String accomplishment = record.getAccomplishment();
                String id = convert(record.getId());
                String token = record.getToken();
                String presentationId = convert(record.getPresentedAt());
                return new PersistedAwardPresentation(connector, presentationId, id, accomplishment, clubber, section, token);
            }).collect(Collectors.toList());
        });
    }

}
