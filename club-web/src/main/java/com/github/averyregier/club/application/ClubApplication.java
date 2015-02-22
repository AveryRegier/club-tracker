package com.github.averyregier.club.application;

import com.github.averyregier.club.broker.ConfiguredConnector;
import com.github.averyregier.club.broker.Connector;
import com.github.averyregier.club.domain.UserManager;
import com.github.averyregier.club.domain.club.Program;
import com.github.averyregier.club.domain.club.adapter.ProgramAdapter;
import com.github.averyregier.club.rest.RestAPI;
import com.github.averyregier.club.view.*;
import spark.servlet.SparkApplication;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import static spark.Spark.exception;
import static spark.SparkBase.setPort;

/**
 * Created by avery on 8/30/14.
 */
public class ClubApplication implements SparkApplication {
    public static void main(String... args) throws SQLException, ClassNotFoundException {
        if(args.length > 0) {
            setPort(Integer.parseInt(args[0]));
        }
        new ClubApplication().init();
    //    connect();

    }

    private final UserManager userManager = new UserManager();
    private Program program;
    private Connector connector;

    @Override
    public void init() {
        loadConfig();

        exception(Exception.class, (e, request, response) -> {
            response.status(404);
            response.body(e.getLocalizedMessage());
            e.printStackTrace();
        });

        spark.Spark.staticFileLocation("/public");

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
            InputStream stream = getClass().getResourceAsStream("config.properties");
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
}
