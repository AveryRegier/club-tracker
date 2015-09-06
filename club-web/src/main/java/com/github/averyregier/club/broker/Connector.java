package com.github.averyregier.club.broker;

import org.jooq.SQLDialect;
import org.jooq.conf.Settings;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by avery on 2/21/15.
 */
public interface Connector {
    Connection connect() throws SQLException;

    SQLDialect getDialect();

    Settings getSettings();
}
