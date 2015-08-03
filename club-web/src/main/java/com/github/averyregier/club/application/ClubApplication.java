package com.github.averyregier.club.application;

import com.github.averyregier.club.broker.*;
import com.github.averyregier.club.domain.ClubManager;
import com.github.averyregier.club.domain.PersonManager;
import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.UserManager;
import com.github.averyregier.club.domain.club.Program;
import com.github.averyregier.club.domain.program.Programs;
import com.github.averyregier.club.repository.PersistedClubManager;
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
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static spark.Spark.exception;
import static spark.SparkBase.setPort;

/**
 * Created by avery on 8/30/14.
 */
public class ClubApplication implements SparkApplication, ServletContextListener, ClubFactory {
    public static void main(String... args) throws SQLException, ClassNotFoundException {
        if(args.length > 0) {
            setPort(Integer.parseInt(args[0]));
        }
        spark.Spark.staticFileLocation("/public");

        new ClubApplication().init();
    //    connect();

    }

    private final UserManager userManager = createUserManager();
    private final ClubManager clubManager = new PersistedClubManager(this);

    private PersistedUserManager createUserManager() {
        PersonManager personManager = new PersistedPersonManager(() -> new PersonBroker(this));
        return new PersistedUserManager(personManager, ()->new LoginBroker(connector));
    }

    private Map<String, Program> programs = new ConcurrentHashMap<>();
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

    @Override
    public UserManager getUserManager() {
        return userManager;
    }

    @Override
    public Program setupProgram(String organizationName, String curriculum, String acceptLanguage) {
        String id = UUID.randomUUID().toString();
        return setupProgramWithId(organizationName, curriculum, acceptLanguage, id);
    }

    public Program setupProgramWithId(String organizationName, String curriculum, String acceptLanguage, String id) {
        Program program = clubManager.createProgram(acceptLanguage, organizationName,
                Programs.find(curriculum).orElseThrow(IllegalArgumentException::new), id);
        program.setPersonManager(userManager.getPersonManager());
        new OrganizationBroker(getConnector()).persist(program);
        programs.put(program.getId(), program);
        return program;
    }

    @Override
    public Program getProgram(String id) {
        return programs.computeIfAbsent(id,
                (key)->new OrganizationBroker(getConnector()).find(key, getClubManager()).orElse(null));
    }

    @Override
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

    @Override
    public PersonManager getPersonManager() {
        return userManager.getPersonManager();
    }

    @Override
    public boolean hasPrograms() {
        return !programs.isEmpty();
    }

    @Override
    public ClubManager getClubManager() {
        return clubManager;
    }

    @Override
    public Collection<Program> getPrograms(User user) {
        return programs.values(); // for now, but need a way to find only the relevant ones
    }
}
