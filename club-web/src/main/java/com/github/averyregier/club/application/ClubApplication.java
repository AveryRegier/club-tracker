package com.github.averyregier.club.application;

import com.github.averyregier.club.domain.UserManager;
import com.github.averyregier.club.domain.club.Program;
import com.github.averyregier.club.domain.club.adapter.ProgramAdapter;
import com.github.averyregier.club.rest.RestAPI;
import com.github.averyregier.club.view.ClubController;
import com.github.averyregier.club.view.Login;
import com.github.averyregier.club.view.RegistrationController;
import com.github.averyregier.club.view.SetupController;
import spark.servlet.SparkApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
        createDatabaseConnection();
    }

    private final UserManager userManager = new UserManager();
    private Program program;

    @Override
    public void init() {
        exception(Exception.class, (e, request, response) -> {
            response.status(404);
            response.body(e.getLocalizedMessage());
            e.printStackTrace();
        });

        spark.Spark.staticFileLocation("/public");

        new Login().init(this);
        new SetupController().init(this);
        new RegistrationController().init(this);
        new RestAPI().init(this);
        new ClubController().init(this);
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public static Connection createDatabaseConnection()
            throws SQLException, ClassNotFoundException {
        String driver = "org.apache.derby.jdbc.EmbeddedDriver";
        Class.forName(driver);
        String url = "jdbc:derby:clubDB;create=true";
        Connection c = DriverManager.getConnection(url);
        return c;
    }

    public Program setupProgram(String organizationName, String curriculum, String acceptLanguage) {
        program = new ProgramAdapter(acceptLanguage, organizationName, curriculum);
        return program;
    }

    public Program getProgram() {
        return program;
    }
}
