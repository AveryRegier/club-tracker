package com.github.averyregier.club.application;

import com.github.averyregier.club.broker.ConfiguredConnector;
import com.github.averyregier.club.broker.Connector;
import com.github.averyregier.club.broker.LoginBroker;
import com.github.averyregier.club.broker.PersonBroker;
import com.github.averyregier.club.domain.PersonManager;
import com.github.averyregier.club.domain.UserManager;
import com.github.averyregier.club.domain.club.Program;
import com.github.averyregier.club.domain.club.adapter.ProgramAdapter;
import com.github.averyregier.club.repository.PersistedPersonManager;
import com.github.averyregier.club.repository.PersistedUserManager;
import com.github.averyregier.club.rest.RestAPI;
import com.github.averyregier.club.view.*;
import spark.servlet.SparkApplication;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import static spark.Spark.exception;
import static spark.SparkBase.setPort;

/**
 * Created by avery on 8/30/14.
 */
public class ClubApplication implements SparkApplication, ServletContextListener {
    public static void main(String... args) throws SQLException, ClassNotFoundException {
        if(args.length > 0) {
            setPort(Integer.parseInt(args[0]));
        }
        spark.Spark.staticFileLocation("/public");

        new ClubApplication().init();
    //    connect();

    }

    private final UserManager userManager = createUserManager();

    private PersistedUserManager createUserManager() {
        PersonManager personManager = new PersistedPersonManager(() -> new PersonBroker(connector));
        return new PersistedUserManager(personManager, ()->new LoginBroker(connector));
    }

    private Program program;
    private ConfiguredConnector connector;

    @Override
    public void init() {

        loadConfig();

        exception(Exception.class, (e, request, response) -> {
            response.status(404);
            response.body(e.getLocalizedMessage());
            e.printStackTrace();
        });

        if(connector != null) connector.migrate();

        new InitialSetup().init(this);
        new FastSetup().init(this);
        new Login().init(this);
        new SetupController().init(this);
        new RegistrationController().init(this);
        new RestAPI().init(this);
        new ClubController().init(this);
    }

    private void loadConfig() {
        try {
            Properties config = new Properties();
            InputStream stream = getClass().getResourceAsStream("/config.properties");
            if(stream != null) {
                config.load(stream);
                connector = new ConfiguredConnector(config);
            }
        } catch (IOException|ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public Program setupProgram(String organizationName, String curriculum, String acceptLanguage) {
        program = new ProgramAdapter(acceptLanguage, organizationName, curriculum);
        program.setPersonManager(userManager.getPersonManager());
        return program;
    }

    public Program getProgram() {
        return program;
    }

    public Connector getConnector() {
        return connector;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        spark.Spark.staticFileLocation("/public");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
