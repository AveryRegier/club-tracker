package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.AwardRecord;
import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.program.Award;
import com.github.averyregier.club.domain.program.Catalogued;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.utility.Named;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.TableField;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.github.averyregier.club.db.tables.Award.AWARD;
import static com.github.averyregier.club.domain.utility.UtilityMethods.convert;

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
                return new AwardPresentation() {

                    @Override
                    public String getId() {
                        return id;
                    }

                    @Override
                    public String getShortCode() {
                        return findAward().map(Named::getName).orElse(accomplishment);
                    }

                    @Override
                    public Person to() {
                        return clubber;
                    }

                    @Override
                    public Named forAccomplishment() {
                        return findAward()
                                .map(a -> (Named) a)
                                .orElse(() -> accomplishment);
                    }

                    private Optional<Award> findAward() {
                        return section.getAwards().stream()
                                .filter(a -> a.getName().equals(accomplishment))
                                .findFirst();
                    }

                    @Override
                    public LocalDate earnedOn() {
                        return Optional.ofNullable(record())
                                .map(r -> r.getSigning()
                                        .map(Signing::getDate))
                                .orElse(Optional.empty())
                                .orElseThrow(IllegalStateException::new);
                    }

                    @Override
                    public Ceremony presentedAt() {
                        if (presentationId == null) {
                            return null;
                        }
                        throw new UnsupportedOperationException("not yet implemented");
                    }

                    @Override
                    public Optional<Catalogued> token() {
                        if (token == null) return Optional.empty();
                        return Optional.of(findAward()
                                .map(a -> a.select(t -> t.getName().equalsIgnoreCase(token)))
                                .orElse(() -> token));
                    }

                    @Override
                    public ClubberRecord record() {
                        return clubber.getRecord(Optional.ofNullable(section)).orElse(null);
                    }

                    @Override
                    public void presentAt(Ceremony ceremony) {
                        throw new UnsupportedOperationException("not yet implemented");
                    }
                };
            }).collect(Collectors.toList());
        });
    }
}
