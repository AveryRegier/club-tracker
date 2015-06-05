package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.ClubberRecord;
import com.github.averyregier.club.domain.ClubManager;
import com.github.averyregier.club.domain.PersonManager;
import com.github.averyregier.club.domain.club.Clubber;
import com.github.averyregier.club.domain.club.Family;
import com.github.averyregier.club.domain.club.adapter.ClubAdapter;
import com.github.averyregier.club.domain.club.adapter.ClubberAdapter;
import com.github.averyregier.club.domain.club.adapter.FamilyAdapter;
import com.github.averyregier.club.domain.program.AgeGroup;
import org.jooq.DSLContext;
import org.jooq.TableField;

import java.util.Map;
import java.util.Optional;

import static com.github.averyregier.club.db.tables.Clubber.CLUBBER;
import static com.github.averyregier.club.domain.utility.UtilityMethods.convert;

/**
 * Created by avery on 2/28/15.
 */
public class ClubberBroker extends Broker<Clubber> {
    public ClubberBroker(Connector connector) {
        super(connector);
    }

    @Override
    protected void persist(Clubber clubber, DSLContext create) {
        if(create.insertInto(CLUBBER)
                .set(CLUBBER.ID, clubber.getId().getBytes())
                .set(mapFields(clubber))
                .onDuplicateKeyUpdate()
                .set(mapFields(clubber))
                .execute() != 1) {
            fail("Clubber persistence failed: " + clubber.getId());
        }
    }

    private Map<TableField<ClubberRecord, ?>, Object> mapFields(Clubber clubber) {
        return JooqUtil.<ClubberRecord>map()
                .set(CLUBBER.CLUB_ID, clubber.getClub().map(club -> club.getId().getBytes()))
                .set(CLUBBER.FAMILY_ID, clubber.getFamily().map(family -> family.getId().getBytes()))
                .set(CLUBBER.AGE_GROUP, Optional.ofNullable(clubber.getCurrentAgeGroup()).map(AgeGroup::name))
                .build();
    }

    public Optional<Clubber> find(String clubberId, PersonManager personManager, ClubManager clubManager) {
        return query(create-> {
            ClubberRecord record = create.selectFrom(CLUBBER)
                    .where(CLUBBER.ID.eq(clubberId.getBytes()))
                    .fetchOne();
            if(record == null) return Optional.empty();

            ClubberAdapter clubber = new ClubberAdapter(personManager.lookup(clubberId).get());

            byte[] clubId = record.getClubId();
            if(clubId != null) {
                clubber.setClub((ClubAdapter) clubManager.lookup(convert(clubId)).get());
            }

            clubber.getUpdater().setAgeGroup(AgeGroup.DefaultAgeGroup.valueOf(record.getAgeGroup()));

            byte[] familyId = record.getFamilyId();
            if(familyId != null) {
                Family family = new FamilyAdapter(convert(familyId), clubber);
                clubber.getUpdater().setFamily(family);
            }
            return Optional.of((Clubber)clubber);
        });
    }
}
