package com.github.averyregier.club.view;

import com.github.averyregier.club.application.ClubApplication;
import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.club.adapter.SettingsAdapter;
import com.github.averyregier.club.domain.program.AgeGroup;
import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.utility.MapBuilder;
import com.github.averyregier.club.domain.utility.Setting;
import com.github.averyregier.club.domain.utility.Settings;
import com.github.averyregier.club.domain.utility.adapter.SettingAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Request;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.averyregier.club.domain.utility.UtilityMethods.killWhitespace;
import static com.github.averyregier.club.domain.utility.UtilityMethods.optMap;
import static spark.Spark.*;

/**
 * Created by avery on 12/26/14.
 */
public class ClubSetupController extends BaseController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public void init(ClubApplication app) {

        before("/protected/club/:club/policies", (request, response) -> {
            User user = getUser(request);
            Optional<Club> club = lookupClub(app, request);
            if (!club.map(c -> c.isLeader(user)).orElse(false)) {
                response.redirect("/protected/my");
                halt();
            }
        });

        get("/protected/club/:club/policies", (request, response) -> {
            Optional<Club> club = lookupClub(app, request);
            if (club.isPresent()) {
                MapBuilder<String, Object> builder = newModel(request, "Club " + club.get().getShortCode() + " Policies")
                        .put("club", club.get());
                Stream.of(Policy.values()).forEach(policy -> builder.put(policy.name(), ""));
                club.get().getPolicies().forEach(policy -> builder.put(policy.name(), "checked"));

                Map<String, String> defaults = club.get().getCurriculum().getAgeGroups().stream()
                        .collect(Collectors.toMap(AgeGroup::name,
                                ageGroup -> getCurriculum(club, ageGroup).getId()));

                club.get().getSettings().getSettings()
                        .forEach(setting -> defaults.put(
                                setting.getKey().replace("-book", ""), setting.marshall()));

                builder.put("defaultCurriculum", defaults);

                return new ModelAndView(
                        builder.build(),
                        "policies.ftl");
            } else return gotoMy(response);
        }, new FreeMarkerEngine());

        post("/protected/club/:club/policies", (request, response) -> {
            Optional<Club> club = lookupClub(app, request);
            if (club.isPresent()) {
                EnumSet<Policy> policies = EnumSet.noneOf(Policy.class);
                String[] temp = request.queryMap("policy").values();
                if (temp != null) {
                    for (String policy : temp) {
                        policies.add(Policy.valueOf(policy));
                    }
                }
                Club theClub = club.get();

                Settings settings = new SettingsAdapter(theClub);
                if (policies.contains(Policy.customizedBookSelections)) {
                    settings = buildCustomizedBookSettings(request, theClub);
                }
                theClub.replacePolicies(policies, settings);
                response.redirect("/protected/club/" + theClub.getId());
            } else {
                response.redirect("/protected/my");
            }
            return null;
        });

        get("/protected/club/:club/workers", (request, response) -> {
            Optional<Club> club = lookupClub(app, request);
            if (club.isPresent()) {
                return new ModelAndView(
                        newModel(request, "Add " + club.get().getShortCode() + " Workers")
                                .put("club", club.get())
                                .build(),
                        "addWorker.ftl");
            } else {
                return gotoMy(response);
            }
        }, new FreeMarkerEngine());

        get("/protected/club/:club/workers/:personId", (request, response) -> {
            Optional<Club> club = lookupClub(app, request);
            if (club.isPresent()) {
                Optional<Person> person = app.getPersonManager().lookup(request.params(":personId"));
                if (person.isPresent()) {
                    MapBuilder<String, Object> model = newModel(request,
                            "Assign " + club.get().getShortCode() + " role to " + person.get().getName().getFullName())
                            .put("club", club.get())
                            .put("person", person.get())
                            .put("roles", ClubLeader.LeadershipRole.values());
                    optMap(club, Club::asProgram).ifPresent(p -> model.put("clubs", getClubList(p)));
                    return new ModelAndView(model.build(), "workerRole.ftl");
                } else {
                    response.redirect("/protected/club/" + club.get().getId() + "/workers");
                    return null;
                }
            } else {
                return gotoMy(response);
            }
        }, new FreeMarkerEngine());
    }

    public Settings buildCustomizedBookSettings(Request request, Club theClub) {
        List<Setting<?>> collect = theClub.getCurriculum().getAgeGroups().stream().map(ageGroup -> {
            String key = ageGroup.name() + "-book";
            return Optional.ofNullable(killWhitespace(request.queryParams(key)))
                    .flatMap(value -> theClub
                            .getProgram()
                            .getCurriculum()
                            .getSeries(value))
                    .map(series -> new SettingAdapter<>(Curriculum.Type.get(), key, series));
        }).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());

        return new SettingsAdapter(theClub, theClub.createSettingDefinitions(), collect);
    }

    private List<Object> getClubList(Program p) {
        return Stream.concat(Stream.of(p), p.getClubs().stream()).collect(Collectors.toList());
    }
}
