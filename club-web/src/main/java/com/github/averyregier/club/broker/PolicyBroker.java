package com.github.averyregier.club.broker;

import com.github.averyregier.club.domain.club.Policy;
import com.github.averyregier.club.domain.club.PolicyHolder;
import org.jooq.DSLContext;

import java.util.EnumSet;
import java.util.stream.Collectors;

import static com.github.averyregier.club.db.tables.Policy.POLICY;

public class PolicyBroker extends PersistenceBroker<PolicyHolder> {
    public PolicyBroker(Connector connector) {
        super(connector);
    }

    @Override
    protected void persist(PolicyHolder thing, DSLContext create) {
        create.delete(POLICY)
                .where(POLICY.CLUB_ID.eq(thing.getId().getBytes()))
                .execute();
        for (Policy policy : thing.getPolicies()) {
            create.insertInto(POLICY)
                    .set(POLICY.CLUB_ID, thing.getId().getBytes())
                    .set(POLICY.POLICY_NAME, policy.name())
                    .execute();
        }
    }

    public EnumSet<Policy> loadPolicies(PolicyHolder policyHolder) {
        return query(create-> create
                .select(POLICY.POLICY_NAME)
                .from(POLICY)
                .where(POLICY.CLUB_ID.eq(policyHolder.getId().getBytes()))
                .fetch().stream()
                .map(record->Policy.valueOf(record.value1()))
                .collect(Collectors.toCollection(()->EnumSet.noneOf(Policy.class))));
    }
}
