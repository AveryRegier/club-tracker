package com.github.averyregier.club.view;

import com.github.averyregier.club.application.ClubApplication;
import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.Club;
import com.github.averyregier.club.domain.navigation.Breadcrumb;
import com.github.averyregier.club.domain.navigation.Breadcrumbs;
import com.github.averyregier.club.domain.program.AgeGroup;
import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.utility.Contained;
import com.github.averyregier.club.domain.utility.MapBuilder;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Session;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.github.averyregier.club.domain.utility.UtilityMethods.map;

/**
 * Created by avery on 9/27/14.
 */
public class BaseController {
    public static String render(Map<String, ?> model, String templatePath) {
        return new FreeMarkerEngine().render(new ModelAndView(model, templatePath));
    }

    String gotoMy(Response response) {
        response.redirect("/protected/my");
        return null;
    }

    public <K,V> Map<K,V> toMap(K key, V value) {
        HashMap<K, V> kvHashMap = new HashMap<>();
        kvHashMap.put(key, value);
        return kvHashMap;
    }

    @SuppressWarnings("unchecked")
    protected User getUser(Request request) {
        return ((Optional<User>) request.attribute("user")).get();
    }


    Map<String, String> getRequestParams(Request request) {
        return request.queryMap().toMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()[0]));
    }

    MapBuilder<String, Object> newModel(Request request, String title) {
        return map("title", (Object) title)
                .put("breadcrumbs", getBreadcrumbs(request, title));

    }

    private Breadcrumbs getBreadcrumbs(Request request, String title) {
        Session session = request.session(true);
        Breadcrumbs breadcrumbs = session.attribute("breadcrumbs");
        if(breadcrumbs == null) {
            breadcrumbs = new Breadcrumbs(new Breadcrumb("Home", "/protected/my"));
            session.attribute("breadcrumbs", breadcrumbs);
        }
        breadcrumbs.mark(new Breadcrumb(title, request.uri()));
        return breadcrumbs;
    }

    Optional<Club> lookupClub(ClubApplication app, Request request) {
        return app.getClubManager().lookup(request.params(":club"));
    }

    Curriculum getCurriculum(Optional<Club> club, AgeGroup ageGroup) {
        return club.map(Club::getCurriculum)
                .flatMap(curriculum->curriculum.recommendedBookList(ageGroup).stream().findFirst())
                .map(Contained::getContainer)
                .orElseGet(() -> club.map(Club::getCurriculum).orElseThrow(IllegalArgumentException::new));
    }
}
