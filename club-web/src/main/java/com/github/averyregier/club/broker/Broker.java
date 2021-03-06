package com.github.averyregier.club.broker;

import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by avery on 8/23/15.
 */
public class Broker {
    protected final Connector connector;

    public Broker(Connector connector) {
        this.connector = connector;
    }

    private Connection connect() throws SQLException {
        return connector.connect();
    }

    protected void execute(Consumer<DSLContext> c) {
        try (Connection connection = connect()) {
            DSLContext create = create(connection);

            c.accept(create);
        } catch (SQLException e) {
            throw new DataAccessException("Connection failure", e);
        }
    }

    private DSLContext create(Connection connection) {
        return DSL.using(connection, connector.getDialect(), connector.getSettings());
    }

    protected <T> T query(Function<DSLContext, T> c) {
        try (Connection connection = connect()) {
            DSLContext create = create(connection);
            return c.apply(create);
        } catch (SQLException e) {
            throw new DataAccessException("Connection failure", e);
        }
    }

    protected void fail(String reason) {
        throw new DataAccessException(reason);
    }
}
