package com.github.averyregier.club.application;

import com.github.averyregier.club.domain.UserManager;
import com.github.averyregier.club.view.Login;
import com.github.averyregier.club.rest.RestAPI;
import spark.servlet.SparkApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static spark.Spark.exception;
import static spark.SparkBase.*;

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

    @Override
    public void init() {
        exception(Exception.class, (e, request, response) -> {
            response.status(404);
            response.body(e.getLocalizedMessage());
            e.printStackTrace();
        });

        new Login().init(this);
        new RestAPI().init(this);
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
}
