package com.github.averyregier.club.broker;

import com.github.averyregier.club.application.ClubFactory;
import com.github.averyregier.club.db.tables.records.ClubberRecord;
import com.github.averyregier.club.domain.club.Club;
import com.github.averyregier.club.domain.club.Clubber;
import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.club.adapter.ClubberAdapter;
import com.github.averyregier.club.domain.program.AgeGroup;
import com.github.averyregier.club.repository.FamilyLater;
import com.github.averyregier.club.repository.PersistedClubber;
import org.jooq.DSLContext;
import org.jooq.TableField;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.github.averyregier.club.db.tables.Clubber.CLUBBER;
import static com.github.averyregier.club.domain.utility.UtilityMethods.convert;
import static com.github.averyregier.club.domain.utility.UtilityMethods.equalsAny;

/**
 * Created by avery on 2/28/15.
 */
public class ClubberBroker extends PersistenceBroker<Clubber> {
    private ClubFactory factory;

    public ClubberBroker(ClubFactory factory) {
        super(factory.getConnector());
        this.factory = factory;
    }

    @Override
    protected void persist(Clubber clubber, DSLContext create) {
        if(!equalsAny(create.insertInto(CLUBBER)
                .set(CLUBBER.ID, clubber.getId().getBytes())
                .set(mapFields(clubber))
                .onDuplicateKeyUpdate()
                .set(mapFields(clubber))
                .execute(), 1, 2)) {
            fail("Clubber persistence failed: " + clubber.getId());
        }
    }

    private Map<TableField<ClubberRecord, ?>, Object> mapFields(Clubber clubber) {
        return JooqUtil.<ClubberRecord>map()
                .setHasId(CLUBBER.CLUB_ID, clubber.getClub())
                .setHasId(CLUBBER.FAMILY_ID, clubber.getFamily())
                .set(CLUBBER.AGE_GROUP, Optional.ofNullable(clubber.getCurrentAgeGroup()).map(AgeGroup::name))
                .build();
    }

    public Optional<Clubber> find(String clubberId) {
        Optional<Clubber> result = query(queryClubberMethod(clubberId));
        return result;
    }

    public Collection<Clubber> find(Club club) {
        return query(create->create
                .selectFrom(CLUBBER)
                .where(CLUBBER.CLUB_ID.eq(club.getId().getBytes()))
                .fetch().stream()
                .map(r-> mapClubber(convert(r.getId()), r, ()->club))
                .collect(Collectors.toList()));
    }


    private Function<DSLContext, Optional<Clubber>> queryClubberMethod(String clubberId) {
        return create -> {
            ClubberRecord record = create.selectFrom(CLUBBER)
                    .where(CLUBBER.ID.eq(clubberId.getBytes()))
                    .fetchOne();
            if (record == null) return Optional.empty();

            return Optional.of(mapClubber(clubberId, record, ()->{
                byte[] clubId = record.getClubId();
                if(clubId == null) return null;
                return factory.getClubManager().lookup(convert(clubId)).orElse(null);
            }));
        };
    }

    private Clubber mapClubber(String clubberId, ClubberRecord record, Supplier<Club> clubFinder) {
        Person person = factory.getPersonManager().lookup(clubberId).get();

        return person.getUpdater().asClubberNow().orElseGet(() -> {
            ClubberAdapter clubber = new PersistedClubber(factory, person);

            Club club = clubFinder.get();
            if (club != null) {
                clubber.setClub(club);
            }

            clubber.getUpdater().setAgeGroup(AgeGroup.DefaultAgeGroup.valueOf(record.getAgeGroup()));

            byte[] familyId = record.getFamilyId();
            if (familyId != null) {
                clubber.getUpdater().setFamily(new FamilyLater(factory, convert(familyId)));
            }
            return clubber;
        });
    }
}
