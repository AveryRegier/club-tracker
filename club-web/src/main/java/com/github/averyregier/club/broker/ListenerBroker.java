package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.ListenerRecord;
import com.github.averyregier.club.domain.ClubManager;
import com.github.averyregier.club.domain.PersonManager;
import com.github.averyregier.club.domain.club.Club;
import com.github.averyregier.club.domain.club.Listener;
import com.github.averyregier.club.domain.club.adapter.ClubAdapter;
import com.github.averyregier.club.domain.club.adapter.ListenerAdapter;
import org.jooq.DSLContext;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.averyregier.club.db.tables.Listener.LISTENER;
import static com.github.averyregier.club.domain.utility.UtilityMethods.convert;

/**
 * Created by avery on 2/28/15.
 */
public class ListenerBroker extends PersistenceBroker<Listener> {
    public ListenerBroker(Connector connector) {
        super(connector);
    }

    @Override
    protected void persist(Listener listener, DSLContext create) {
        create.insertInto(LISTENER)
                .set(LISTENER.ID, listener.getId().getBytes())
                .set(LISTENER.CLUB_ID, listener.getClub().get().getId().getBytes())
                .onDuplicateKeyIgnore()
                .execute();
    }

    public Optional<Listener> find(String id, PersonManager personManager, ClubManager clubManager) {
        Function<DSLContext, Optional<Listener>> fn = create -> {
            List<ListenerRecord> collect = create.selectFrom(LISTENER)
                    .where(LISTENER.ID.eq(id.getBytes()))
                    .fetch().stream()
                    .collect(Collectors.toList());
            if (collect.isEmpty()) return Optional.empty();
            ListenerRecord record = collect.get(collect.size()-1);

            ClubAdapter clubAdapter = (ClubAdapter) clubManager.lookup(convert(record.getClubId())).get();
            Listener listener = map(id, personManager, clubAdapter);
            return Optional.of(listener);
        };
        Optional<Listener> result = query(fn);
        return result;
    }

    private Listener map(String id, PersonManager personManager, Club club) {
        ListenerAdapter listener = new ListenerAdapter(personManager.lookup(id).get());
        listener.setClubGroup(club);
        return listener;
    }

    public Set<Listener> find(Club club, PersonManager personManager) {
        return query(create-> create
                .selectFrom(LISTENER)
                .where(LISTENER.CLUB_ID.eq(club.getId().getBytes()))
                .fetch().stream()
                .map(record -> map(convert(record.getId()), personManager, club))
                .collect(Collectors.toCollection(LinkedHashSet::new)));
    }
}
