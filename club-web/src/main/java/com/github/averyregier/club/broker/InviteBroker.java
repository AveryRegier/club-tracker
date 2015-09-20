package com.github.averyregier.club.broker;

import com.github.averyregier.club.application.ClubFactory;
import com.github.averyregier.club.db.tables.records.InviteRecord;
import com.github.averyregier.club.domain.club.Invitation;
import com.github.averyregier.club.domain.club.adapter.InvitationAdapter;
import org.jooq.DSLContext;
import org.jooq.TableField;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.averyregier.club.db.tables.Invite.INVITE;
import static com.github.averyregier.club.domain.utility.UtilityMethods.convert;
import static com.github.averyregier.club.domain.utility.UtilityMethods.equalsAny;

/**
 * Created by avery on 8/17/15.
 */
public class InviteBroker extends PersistenceBroker<Invitation> {
    private ClubFactory factory;

    public InviteBroker(ClubFactory factory) {
        super(factory.getConnector());
        this.factory = factory;
    }

    @Override
    protected void persist(Invitation invite, DSLContext create) {
        if(!equalsAny(create.insertInto(INVITE)
                .set(INVITE.ID, invite.getPerson().getId().getBytes())
                .set(INVITE.AUTH, invite.getAuth())
                .set(mapFields(invite))
                .onDuplicateKeyUpdate()
                .set(mapFields(invite))
                .execute(), 1, 2)) {
            fail("Invitation persistence failed: " + invite.getPerson().getId());
        }
    }

    private Map<TableField<InviteRecord, ?>, Object> mapFields(Invitation invite) {
        return JooqUtil.<InviteRecord>map()
                .set(INVITE.INVITED_BY, invite.by().getId().getBytes())
                .set(INVITE.SENT, Timestamp.from(invite.getSent()))
                .set(INVITE.COMPLETED, invite.getCompleted().map(Timestamp::from))
                .build();
    }

    public List<Invitation> find(String code) {
        return query(findInvitationFn(code));
    }

    private Function<DSLContext, List<Invitation>> findInvitationFn(String code) {
        return create -> create
                .selectFrom(INVITE)
                .where(INVITE.AUTH.eq(Integer.parseInt(code)))
                .fetch().stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    private Invitation map(InviteRecord record) {
        return new InvitationAdapter(
                factory.getPersonManager().lookup(convert(record.getId())),
                record.getAuth(),
                factory.getPersonManager().lookup(convert(record.getInvitedBy())),
                record.getSent().toInstant(),
                Optional.ofNullable(record.getCompleted()).map(Timestamp::toInstant));
    }
}
