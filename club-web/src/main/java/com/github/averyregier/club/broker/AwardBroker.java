package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.AwardRecord;
import com.github.averyregier.club.domain.club.AwardPresentation;
import com.github.averyregier.club.domain.utility.Named;
import org.jooq.DSLContext;
import org.jooq.TableField;

import java.util.Map;

import static com.github.averyregier.club.db.tables.Award.AWARD;

/**
 * Created by avery on 3/2/15.
 */
public class AwardBroker extends Broker<AwardPresentation> {
    public AwardBroker(Connector connector) {
        super(connector);
    }

    @Override
    protected void persist(AwardPresentation award, DSLContext create) {
        if(create.insertInto(AWARD)
                .set(AWARD.ID, award.getId().getBytes())
                .set(mapFields(award))
                .onDuplicateKeyUpdate()
                .set(mapFields(award))
                .execute() != 1) {
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
}
