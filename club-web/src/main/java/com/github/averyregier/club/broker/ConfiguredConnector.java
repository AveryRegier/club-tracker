package com.github.averyregier.club.broker;

import com.github.averyregier.club.domain.utility.UtilityMethods;
import org.flywaydb.core.Flyway;
import org.jooq.SQLDialect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static com.github.averyregier.club.domain.utility.UtilityMethods.map;

public class ConfiguredConnector implements Connector {
    protected final SQLDialect dialect;
    protected final String url;
    protected final String user;
    protected final String password;

    public ConfiguredConnector(Properties config) throws ClassNotFoundException {
        password = UtilityMethods.killWhitespace(config.getProperty("jdbc.password"));
        url = config.getProperty("jdbc.url");
        dialect = SQLDialect.valueOf(config.getProperty("jooq.dialect"));
        user = UtilityMethods.killWhitespace(config.getProperty("jdbc.user"));
        String driver = config.getProperty("jdbc.driver");
        Class.forName(driver);
    }

    @Override
    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public SQLDialect getDialect() {
        return dialect;
    }

    public void migrate() {
        Flyway flyway = new Flyway();
        flyway.setDataSource(url, user, password);

        flyway.setPlaceholders(
                map("setup", "CREATE TYPE UUID AS BINARY(16);")
                        .put("generate_uuid", "")
                        .put("schema", "club")
                        .put("create_schema", "CREATE SCHEMA club AUTHORIZATION DBA;")
                        .build());
        flyway.migrate();
    }
}