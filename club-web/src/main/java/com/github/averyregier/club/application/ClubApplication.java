package com.github.averyregier.club.application;

import com.github.averyregier.club.broker.*;
import com.github.averyregier.club.domain.ClubManager;
import com.github.averyregier.club.domain.PersonManager;
import com.github.averyregier.club.domain.UserManager;
import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.club.Program;
import com.github.averyregier.club.domain.club.RegistrationSection;
import com.github.averyregier.club.domain.program.Programs;
import com.github.averyregier.club.domain.utility.adapter.InputFieldBuilder;
import com.github.averyregier.club.domain.utility.adapter.InputFieldGroupBuilder;
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
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.averyregier.club.domain.utility.InputField.Type.date;
import static com.github.averyregier.club.domain.utility.InputField.Type.text;
import static spark.Spark.exception;
import static spark.SparkBase.port;

/**
 * Created by avery on 8/30/14.
 */
public class ClubApplication implements SparkApplication, ServletContextListener, ClubFactory {
    public static void main(String... args) throws SQLException, ClassNotFoundException {
        if(args.length > 0) {
            port(Integer.parseInt(args[0]));
        }

        new ClubApplication().init();
    //    connect();

    }

    private ConfiguredConnector connector;
    private UserManager userManager;
    private ClubManager clubManager;

    {
        reset();
    }

    private PersistedUserManager createUserManager() {
        PersonManager personManager = new PersistedPersonManager(
                () -> new PersonBroker(this),
                () -> new FamilyBroker(this),
                () -> new PersonRegistrationBroker(this));
        return new PersistedUserManager(personManager, ()->new LoginBroker(connector));
    }

    @Override
    public void init() {
        spark.Spark.staticFileLocation("/public");

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
        new ProviderSetup().init(this);
        new SetupController().init(this);
        new RegistrationController().init(this);
        new InviteController().init(this);
        new RestAPI().init(this);
        new ClubController().init(this);
        new AdminController().init(this);
    }

    private void loadConfig() {
        try {
            Properties config = new Properties();
            InputStream stream = getClass().getResourceAsStream("/config.properties");
            if(stream != null) {
                config.load(stream);

                System.getProperties().list(System.out);

                if(System.getProperties().containsKey("RDS_HOSTNAME")) {
                    config.setProperty("jdbc.user", System.getProperty("RDS_USERNAME"));
                    config.setProperty("jdbc.password", System.getProperty("RDS_PASSWORD"));
                    config.setProperty("jdbc.url", "jdbc:"
                            +config.getProperty("jdbc.url").split(":")[1]+
                            "://" +
                            System.getProperty("RDS_HOSTNAME")+":" +
                            System.getProperty("RDS_PORT")+"/" +
                            System.getProperty("RDS_DB_NAME"));
                }

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
        addExtraFields(program);

        return program;
    }

    public void addExtraFields(Program program) {
        program.addField(RegistrationSection.parent, new InputFieldBuilder()
                .name("Phone")
//                .id("phone")
                .type(text)
                .build());
        program.addField(RegistrationSection.child, new InputFieldBuilder()
                .name("Known Allergies/Medical Conditions")
//                .id("medical")
                .type(text)
                .build());
        program.addField(RegistrationSection.child, new InputFieldBuilder()
                .name("Birth Date")
//                .id("birthdate")
                .type(date)
                .build());
        program.addField(RegistrationSection.household, new InputFieldGroupBuilder()
                .name("Emergency Contact")
                .group(this::extraNameFields)
                .field((f) -> f.name("Phone Number")
//                      .id("phone")
                        .required().type(text))
                .field((f) -> f.name("Relationship to Children")
//                      .id("relationship")
                        .required().type(text))
                .build());
//        program.addField(RegistrationSection.household, new InputFieldGroupBuilder()
//                .name("Preferred Doctor")
//                .group(n -> extraNameFields(n))
//                .field(f -> f.name("Phone Number")
////                      .id("phone")
//                        .required().type(text))
//                .build());
        program.addField(RegistrationSection.household, new InputFieldBuilder()
//                .id("media")
                .name("Media Disclosure").type(text).required()
                .value("Granted", "Permission Granted", false)
                .value("Denied", "Permission Denied", false)
                .build());
//        program.addField(RegistrationSection.household, new InputFieldBuilder()
//                .name("Phone Number for Cancellations & Other " + program.getCurriculum().getShortCode() + " Related News")
////                .id("phone")
//                .type(text)
//                .build());
        program.addField(RegistrationSection.household, new InputFieldBuilder()
            .name("Additional Information About Your Family")
            .type(text)
            .build());
    }

    private InputFieldGroupBuilder extraNameFields(InputFieldGroupBuilder n) {
        return n.name("Name")
//                                .id("name")
                .field(f -> f
//                                        .id("given")
                        .name("Given").type(text))
                .field(f -> f
//                                        .id("surname")
                        .name("Surname").type(text));
    }

    @Override
    public Program getProgram(String id) {
        return getClubManager()
                .lookup(id)
                .filter(c -> c instanceof Program)
                .map(c -> (Program) c)
                .orElse(null);
    }

    public Optional<Program> getProgramByName(String name) {
        return getClubManager()
                .getPrograms()
                .stream()
                .filter(p->matches(name, p.getShortCode()))
                .findFirst();
    }

    private boolean matches(String match, String v) {
        String comparing = Stream.of(v.split("\\W+"))
                .collect(Collectors.joining())
                .toLowerCase();
        return comparing.equalsIgnoreCase(match);
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
        return getClubManager().hasPrograms();
    }

    @Override
    public ClubManager getClubManager() {
        return clubManager;
    }

    @Override
    public Collection<Program> getPrograms(Person person) {
        return person.getClubs().stream()
                .flatMap(c -> Optional.ofNullable(c.getProgram())
                    .map(Stream::of).orElse(Stream.empty()))
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public synchronized void reset() {
        userManager = createUserManager();
        clubManager = new PersistedClubManager(this);
    }
}
