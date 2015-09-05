package com.github.averyregier.club.broker;

import org.jooq.DSLContext;

/**
 * Created by avery on 2/20/15.
 */
public abstract class PersistenceBroker<T> extends Broker {

    protected PersistenceBroker(Connector connector) {
        super(connector);
    }

    public void persist(T thing) {
        execute((create)->persist(thing, create));
    }

    protected abstract void persist(T thing, DSLContext create);

}
