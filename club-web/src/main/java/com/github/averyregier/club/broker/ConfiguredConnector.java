package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.Club;
import com.github.averyregier.club.domain.utility.UtilityMethods;
import org.flywaydb.core.Flyway;
import org.jooq.SQLDialect;
import org.jooq.conf.MappedSchema;
import org.jooq.conf.RenderMapping;
import org.jooq.conf.RenderNameStyle;
import org.jooq.conf.Settings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import static com.github.averyregier.club.domain.utility.UtilityMethods.subMap;
import static com.github.averyregier.club.domain.utility.UtilityMethods.toStrings;

public class ConfiguredConnector implements Connector {
    protected final SQLDialect dialect;
    protected final String url;
    protected final String user;
    protected final String password;
    private final Map<String, String> placeholders;

    public ConfiguredConnector(Properties config) throws ClassNotFoundException {
        password = UtilityMethods.killWhitespace(config.getProperty("jdbc.password"));
        url = config.getProperty("jdbc.url");
        System.out.println("jdbc.url="+url);
        dialect = findSQLDialect(config);
        user = UtilityMethods.killWhitespace(config.getProperty("jdbc.user"));
        String driver = config.getProperty("jdbc.driver");
        placeholders = toStrings(subMap("placeholder", config));
        updateSchema(dialect).ifPresent(s->placeholders.put("schema", s));
        Class.forName(driver);
    }

    private static SQLDialect findSQLDialect(Properties config) {
        return SQLDialect.valueOf(config.getProperty("jooq.dialect"));
    }

    @Override
    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public SQLDialect getDialect() {
        return dialect;
    }

    @Override
    public Settings getSettings() {
        if(dialect == SQLDialect.POSTGRES) {
            return new Settings().withRenderNameStyle(RenderNameStyle.LOWER);
        }
        return updateSchema(dialect).map(s ->
                new Settings()
                        .withRenderNameStyle(RenderNameStyle.LOWER)
                        .withRenderMapping(new RenderMapping()
                        .withSchemata(
                            new MappedSchema().withInput(Club.CLUB.getName())
                                    .withOutput(s))))
                    .orElseGet(Settings::new);
    }

    static Optional<String> updateSchema(SQLDialect dialect) {
        if(dialect == SQLDialect.MYSQL) {
            return Optional.ofNullable(System.getProperty("RDS_DB_NAME"));
        }
        return Optional.empty();
    }

    public void migrate() {
        Flyway flyway = new Flyway();
        flyway.setDataSource(url, user, password);
        flyway.setPlaceholders(placeholders);
        String[] locations = flyway.getLocations();
        for (int i = 0; i < locations.length; i++) {
            locations[i] = locations[i]+"/"+getFolder(dialect);
        }
        flyway.setLocations(locations);
        flyway.migrate();
    }

    private String getFolder(SQLDialect dialect) {
        if(dialect == SQLDialect.MYSQL || dialect == SQLDialect.MARIADB) {
            return "mysql";
        }
        return "common";
    }
}