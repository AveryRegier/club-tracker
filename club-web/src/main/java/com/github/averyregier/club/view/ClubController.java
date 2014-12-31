package com.github.averyregier.club.view;

import com.github.averyregier.club.application.ClubApplication;
import com.github.averyregier.club.domain.club.Club;
import com.github.averyregier.club.domain.club.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * Created by avery on 12/26/14.
 */
public class ClubController extends ModelMaker {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public void init(ClubApplication app) {
        get("/protected/viewProgram", (request, response) -> {
            HashMap<Object, Object> model = new HashMap<>();
            model.put("program", app.getProgram());
            return new ModelAndView(model, "viewProgram.ftl");
        }, new FreeMarkerEngine());

        get("/protected/club/:club", (request, response) -> {
            Optional<Club> club = app.getProgram().lookupClub(request.params(":club"));
            if(club.isPresent()) {
                HashMap<Object, Object> model = new HashMap<>();
                model.put("club", club.get());
                return new ModelAndView(model, "viewClub.ftl");
            } else {
                response.redirect("/protected/program");
                return null;
            }
        }, new FreeMarkerEngine());

        post("/protected/club/:club/listeners", (request, response) -> {
            Optional<Club> club = app.getProgram().lookupClub(request.params(":club"));
            if(club.isPresent()) {
                String[] ids = request.queryParams("id").split(",");
                for(String id: ids) {
                    Optional<Person> person = club.get().getProgram().getPersonManager().lookup(id);
                    if (person.isPresent()) {
                        club.get().recruit(person.get());
                    }
                }
                response.redirect("/protected/club/"+club.get().getShortName());
            } else {
                response.redirect("/protected/program");
                return null;
            }
            return null;
        });

        get("/protected/my", (request, response) -> {
            Map<String, Object> model = toMap("me", getUser(request));
            model.put("program", app.getProgram());
            return new ModelAndView(model, "my.ftl");
        }, new FreeMarkerEngine());
    }

}
