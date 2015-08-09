package com.github.averyregier.club.broker;

import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by avery on 2/20/15.
 */
public abstract class Broker<T> {
    protected final Connector connector;

    protected Broker(Connector connector) {
        this.connector = connector;
    }

    private Connection connect() throws SQLException {
        return connector.connect();
    }

    protected void execute(Consumer<DSLContext> c) {
        try (Connection connection = connect()) {
            DSLContext create = DSL.using(connection, connector.getDialect());

            c.accept(create);
        } catch (SQLException e) {
            throw new DataAccessException("Connection failure", e);
        }
    }

    protected <T> T query(Function<DSLContext, T> c) {
        try (Connection connection = connect()) {
            DSLContext create = DSL.using(connection, connector.getDialect());

            return c.apply(create);
        } catch (SQLException e) {
            throw new DataAccessException("Connection failure", e);
        }
    }

    public void persist(T thing) {
        execute((create)->persist(thing, create));
    }

    protected abstract void persist(T thing, DSLContext create);

    protected void fail(String reason) {
        throw new DataAccessException(reason);
    }

}
