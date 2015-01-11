package com.github.averyregier.club.view;

import com.github.averyregier.club.application.ClubApplication;
import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.Club;
import com.github.averyregier.club.domain.club.Clubber;
import com.github.averyregier.club.domain.club.ClubberRecord;
import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.utility.UtilityMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Request;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.github.averyregier.club.domain.utility.UtilityMethods.decode;
import static com.github.averyregier.club.domain.utility.UtilityMethods.map;
import static spark.Spark.*;

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

        before("/protected/clubbers/:personId/sections/:sectionId", (request, response)-> {
            if(request.requestMethod().equalsIgnoreCase("POST")){
                User user = getUser(request);
                String id = request.params(":personId");
                Clubber clubber = findClubber(app, id);
                ClubberRecord record = getClubberRecord(request, clubber);
                if("true".equalsIgnoreCase(request.queryParams("sign"))) {
                    record.sign(user.asListener().orElseThrow(IllegalStateException::new), request.queryParams("note"));
                }
                response.redirect(request.url());
                halt();
            }
        });

        get("/protected/clubbers/:personId/sections/:sectionId", (request, response)->{
            User user = getUser(request);
            String id = request.params(":personId");
            Clubber clubber = findClubber(app, id);
            ClubberRecord record = getClubberRecord(request, clubber);
            Map<String, Object> model = map("me", (Object)user)
                    .put("clubber", clubber)
                    .put("section", record.getSection())
                    .put("record", record)
                    .build();
            model.put("program", app.getProgram());
            return new ModelAndView(model, "clubberSection.ftl");
        }, new FreeMarkerEngine());
    }

    private ClubberRecord getClubberRecord(Request request, Clubber clubber) {
        String sectionId = request.params(":sectionId");
        Optional<Section> section = UtilityMethods.optMap(clubber.getClub(),
                c -> c.getCurriculum().lookup(decode(sectionId)));
        return clubber.getRecord(section).orElseThrow(IllegalArgumentException::new);
    }

    private Clubber findClubber(ClubApplication app, String id) {
        return app.getProgram().getPersonManager()
                        .lookup(id)
                        .orElseThrow(IllegalArgumentException::new)
                        .asClubber()
                        .orElseThrow(IllegalArgumentException::new);
    }

}
