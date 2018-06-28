package com.github.averyregier.club.broker;

import com.github.averyregier.club.domain.club.Schedule;
import org.jooq.DSLContext;

public class ScheduleBroker extends PersistenceBroker<Schedule> {
    public ScheduleBroker(Connector connector) {
        super(connector);
    }

    @Override
    protected void persist(Schedule thing, DSLContext create) {

    }
}
