package com.github.averyregier.club.view;

import com.github.averyregier.club.application.ClubApplication;
import com.github.averyregier.club.domain.club.ClubLeader;
import com.github.averyregier.club.domain.club.Program;
import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.program.Programs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.HashMap;
import java.util.Optional;

import static spark.Spark.*;

public class SetupController extends ModelMaker {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public void init(ClubApplication app) {
        get("/protected/setup", (request, response) -> {
            HashMap<Object, Object> model = new HashMap<>();
            model.put("roles", ClubLeader.LeadershipRole.values());
            model.put("programs", Programs.values());
            return new ModelAndView(model, "setup.ftl");
        }, new FreeMarkerEngine());

        post("/protected/setup", (request, response) -> {

            String organizationName = request.queryParams("organizationName");
            String myRole = request.queryParams("role");
            String curriculum = request.queryParams("program");
            String acceptLanguage = request.headers("Accept-Language").split(",")[0];

            Program program = app.setupProgram(organizationName, curriculum, acceptLanguage);
            program.assign(getUser(request), ClubLeader.LeadershipRole.valueOf(myRole));

            response.redirect("/protected/program/"+program.getId());
            return null;
        });

        before("/protected/program/:id", (request, response)->{
            if(app.getProgram(request.params(":id")) == null) {
                response.redirect("/protected/setup");
                halt();
            }
        });

        get("/protected/program/:id", (request, response) ->
                new ModelAndView(toMap("program", app.getProgram(request.params(":id"))), "program.ftl"),
                new FreeMarkerEngine());

        post("/protected/program/:id", (request, response) -> {
            Program program = app.getProgram(request.params(":id"));

            String clubId = request.queryParams("addClub");
            if(clubId != null && clubId.trim().length() != 0) {
                // add the club
                Optional<Curriculum> series = program.getCurriculum().getSeries(clubId);
                if(series.isPresent()) {
                    program.addClub(series.get());
                }
            }

            String organizationName = request.queryParams("organizationName");
            program.setName(organizationName);

            response.redirect("/protected/program/"+program.getId());
            return null;
        });
    }
}
