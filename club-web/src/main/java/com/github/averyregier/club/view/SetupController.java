package com.github.averyregier.club.view;

import com.github.averyregier.club.application.ClubApplication;
import com.github.averyregier.club.domain.club.ClubLeader;
import com.github.averyregier.club.domain.club.Program;
import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.program.Programs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Filter;
import spark.Request;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static spark.Spark.*;

public class SetupController extends BaseController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public void init(ClubApplication app) {
        get("/setup", (request, response) ->
                render(newModel(request, "Club Setup")
                .put("roles", ClubLeader.LeadershipRole.values())
                .put("programs", Programs.values())
                .build(), "setup.ftl"));

        post("/setup", (request, response) -> {

            String organizationName = request.queryParams("organizationName");
            String myRole = request.queryParams("role");
            String curriculum = request.queryParams("program");
            String acceptLanguage = request.headers("Accept-Language").split(",")[0];

            Program program = app.setupProgram(organizationName, curriculum, acceptLanguage);
            program.assign(getUser(request), ClubLeader.LeadershipRole.valueOf(myRole));

            response.redirect("/protected/program/" + program.getId());
            return null;
        });

        before("/program/:id", gotoSetupScreenIfNecessary(app));

        get("/program/:id", (request, response) ->
                        createProgramView(request, app.getProgram(request.params(":id")), "program.ftl"));

        post("/program/:id", (request, response) -> {
            Program program = app.getProgram(request.params(":id"));

            String clubId = request.queryParams("addClub");
            if (clubId != null && clubId.trim().length() != 0) {
                // add the club
                Optional<Curriculum> series = program.getCurriculum().getSeries(clubId);
                if (series.isPresent()) {
                    program.addClub(series.get());
                }
            }

            String organizationName = request.queryParams("organizationName");
            program.setName(organizationName);

            response.redirect("/protected/program/" + program.getId());
            return null;
        });

        get("/program/:id/update", (request, response) -> {
            Program program = app.getProgram(request.params(":id"));
            app.addExtraFields(program);
            return createProgramView(request, program, "program.ftl");
        });

        before("/program/:id/schedule", gotoSetupScreenIfNecessary(app));

        get("/program/:id/schedule", (request, response) ->
                        createProgramView(request, app.getProgram(request.params(":id")), "schedule.ftl"));

        post("/program/:id/schedule", (request, response) -> {
            Program program = app.getProgram(request.params(":id"));
            List<LocalDate> dates = parseMeetingDates(request);

            String clubYear = determineClubYear(dates);
            program.setMeetings(clubYear, dates);

            response.redirect("/protected/program/" + program.getId());
            return null;
        });
    }

    public String determineClubYear(List<LocalDate> dates) {
        return dates.stream()
                .map(LocalDate::getYear).map(Object::toString)
                .filter(s->s.length() >= 2)
                .map(s->s.substring(s.length()-2))
                .distinct().collect(Collectors.joining("-"));
    }

    public List<LocalDate> parseMeetingDates(Request request) {
        return request.queryMap().toMap().entrySet().stream()
                .filter(e -> e.getKey().startsWith("week"))
                .map(Map.Entry::getValue)
                .flatMap(Arrays::stream)
                .map(this::parseDate)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted()
                .collect(Collectors.toList());
    }

    public Optional<LocalDate> parseDate(String value) {
        try {
            return Optional.ofNullable(LocalDate.parse(value));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    public Filter gotoSetupScreenIfNecessary(ClubApplication app) {
        return (request, response) -> {
            if (app.getProgram(request.params(":id")) == null) {
                response.redirect("/protected/setup");
                halt();
            }
        };
    }

    private String createProgramView(Request request, Program program, String viewName) {
        return render(
                newModel(request, "Program").put("program", program).build(),
                viewName);
    }
}
