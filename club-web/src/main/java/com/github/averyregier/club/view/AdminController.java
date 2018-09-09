package com.github.averyregier.club.view;

import com.github.averyregier.club.application.ClubApplication;
import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.Club;
import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.utility.MapBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.averyregier.club.domain.utility.UtilityMethods.optMap;
import static spark.Spark.*;

/**
 * Created by avery on 12/26/14.
 */
public class AdminController extends BaseController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public void init(ClubApplication app) {
        path("/admin", ()->{
            before("/*", (request, response) -> {
                if(!leadsProgram(getUser(request))) {
                    response.redirect("/protected/my");
                    halt();
                }
            });

            before("", (request, response) -> {
                response.redirect("/protected/admin/");
                halt();
            });

            get("/", (request, response) -> {
                MapBuilder<String, Object> model = newModel(request, "Admin");
                User user = getUser(request);
                user.asClubLeader().ifPresent(l -> l.getClub().ifPresent(c -> model.put("mygroup", c)));
                return render(model.build(), "admin.ftl");
            });

            post("/reset", ((request, response) -> {
                app.reset();
                response.redirect("/protected/admin");
                return null;
            }));

            before("/reset", (request, response) -> {
                app.getProgramByName(request.params("name")).ifPresent(app::addExtraFields);

                response.redirect("/protected/admin");
                halt();
            });
        });
    }

    private boolean leadsProgram(Person person) {
        return optMap(person.asClubLeader().filter(l->!l.getLeadershipRole().isKiosk()),
                l -> optMap(l.getClub(), Club::asProgram)).isPresent();
    }

}
