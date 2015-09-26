package com.github.averyregier.club.view;

import com.github.averyregier.club.application.ClubApplication;
import com.github.averyregier.club.broker.CeremonyBroker;
import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.club.adapter.CeremonyAdapter;
import com.github.averyregier.club.domain.program.Book;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.program.SectionGroup;
import com.github.averyregier.club.domain.utility.UtilityMethods;
import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Request;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.averyregier.club.domain.utility.UtilityMethods.*;
import static java.util.stream.Collectors.toList;
import static spark.Spark.*;

/**
 * Created by avery on 12/26/14.
 */
public class ClubController extends ModelMaker {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public void init(ClubApplication app) {
        get("/protected/:id/viewProgram", (request, response) -> {
            HashMap<Object, Object> model = new HashMap<>();
            model.put("program", app.getProgram(request.params(":id")));
            return new ModelAndView(model, "viewProgram.ftl");
        }, new FreeMarkerEngine());

        get("/protected/club/:club", (request, response) -> {
            Optional<Club> club = app.getClubManager().lookup(request.params(":club"));
            if(club.isPresent()) {
                HashMap<Object, Object> model = new HashMap<>();
                model.put("club", club.get());
                return new ModelAndView(model, "viewClub.ftl");
            } else {
                response.redirect("/protected/my");
                return null;
            }
        }, new FreeMarkerEngine());

        get("/protected/club/:club/workers", (request, response) -> {
            Optional<Club> club = app.getClubManager().lookup(request.params(":club"));
            if(club.isPresent()) {
                HashMap<Object, Object> model = new HashMap<>();
                model.put("club", club.get());
                return new ModelAndView(model, "addWorker.ftl");
            } else {
                response.redirect("/protected/my");
                return null;
            }
        }, new FreeMarkerEngine());

        get("/protected/club/:club/workers/:personId", (request, response) -> {
            Optional<Club> club = app.getClubManager().lookup(request.params(":club"));
            if(club.isPresent()) {
                Optional<Person> person = app.getPersonManager().lookup(request.params(":personId"));
                if(person.isPresent()) {
                    HashMap<Object, Object> model = new HashMap<>();
                    model.put("club", club.get());
                    model.put("person", person.get());
                    model.put("roles", ClubLeader.LeadershipRole.values());
                    return new ModelAndView(model, "workerRole.ftl");
                } else {
                    response.redirect("/protected/club/"+club.get().getId()+"/workers");
                    return null;
                }
            } else {
                response.redirect("/protected/my");
                return null;
            }
        }, new FreeMarkerEngine());

        post("/protected/club/:club/workers/:personId", (request, response) -> {
            Optional<Club> club = app.getClubManager().lookup(request.params(":club"));
            if (club.isPresent()) {
                Optional<Person> person = app.getPersonManager().lookup(request.params(":personId"));
                if (person.isPresent()) {
                    String roleName = request.queryParams("role");
                    if ("Listener".equalsIgnoreCase(roleName)) {
                        club.get().recruit(person.get());
                    } else {
                        ClubLeader.LeadershipRole leadershipRole = ClubLeader.LeadershipRole.valueOf(roleName);
                        club.get().assign(person.get(), leadershipRole);
                    }
                }
                response.redirect("/protected/club/" + club.get().getId());
            } else {
                response.redirect("/protected/my");
            }
            return null;
        });

        before("/protected/club/:club/awards", (request, response) -> {
            if (request.requestMethod().equalsIgnoreCase("POST")) {
                Optional<Club> club = app.getClubManager().lookup(request.params(":club"));
                if (club.isPresent()) {
                    HashSet<String> awards = asLinkedSet(request.queryMap("award").values());
                    Ceremony ceremony = new CeremonyAdapter();
                    new CeremonyBroker(app.getConnector()).persist(ceremony);
                    club.get().getAwardsNotYetPresented().stream()
                            .filter(a -> awards.contains(a.getId()))
                            .forEach(a -> a.presentAt(ceremony));
                }
                response.redirect(request.url());
                halt();
            }
        });

        get("/protected/club/:club/awards", (request, response) -> {
            Optional<Club> club = app.getClubManager().lookup(request.params(":club"));
            if (club.isPresent()) {
                HashMap<Object, Object> model = new HashMap<>();
                model.put("club", club.get());
                return new ModelAndView(model, "awards.ftl");
            } else {
                response.redirect("/protected/my");
                return null;
            }
        }, new FreeMarkerEngine());

        get("/protected/my", (request, response) -> {
            User user = getUser(request);
            Map<String, Object> model = toMap("me", user);
            model.put("programs", app.getPrograms(user));
            user.asClubLeader().ifPresent(l -> l.getClub().ifPresent(c -> model.put("mygroup", c)));
            return new ModelAndView(model, "my.ftl");
        }, new FreeMarkerEngine());

        before("/protected/clubbers/:personId/sections/:sectionId", (request, response) -> {
            if (request.requestMethod().equalsIgnoreCase("POST")) {
                User user = getUser(request);
                String id = request.params(":personId");
                Clubber clubber = findClubber(app, id);
                ClubberRecord record = getClubberRecord(request, clubber);
                if ("true".equalsIgnoreCase(request.queryParams("sign"))) {
                    if(maySignRecords(user, clubber))  {
                        record.sign(user.asListener().orElseThrow(IllegalStateException::new), request.queryParams("note"));
                    } else {
                        throw new IllegalAccessException("You are not authorized to sign this section");
                    }
                }
                response.redirect(request.url());
                halt();
            }
        });

        get("/protected/clubbers/:personId/sections/:sectionId", (request, response) -> {
            User user = getUser(request);
            String id = request.params(":personId");
            Clubber clubber = findClubber(app, id);
            ClubberRecord record = getClubberRecord(request, clubber);
            Map<String, Object> model = map("me", (Object) user)
                    .put("clubber", clubber)
                    .put("section", record.getSection())
                    .put("record", record)
                    .put("nextSection", clubber.getSectionAfter(record.getSection()))
                    .build();
            return new ModelAndView(model, "clubberSection.ftl");
        }, new FreeMarkerEngine());

        before("/protected/clubbers/:personId/sections", ((request, response) -> {
            User user = getUser(request);
            String id = request.params(":personId");
            Clubber clubber = findClubber(app, id);
            if(maySeeRecords(user, clubber)) {
                Optional<Section> nextSection = clubber.getNextSection();
                Optional<Book> book = nextSection.map(s->s.getContainer().getBook());
                if(!nextSection.isPresent()) {
                    book = getLastBook(clubber);
                }
                if(book.isPresent()) {
                    response.redirect("/protected/clubbers/" + clubber.getId() + "/books/" + book.get().getId());
                } else {
                    response.status(HttpStatus.SC_NOT_FOUND);
                }
            } else {
                response.status(HttpStatus.SC_FORBIDDEN);
            }
            halt();
        }));

        get("/protected/clubbers/:personId/books/:bookId", (request, response) -> {
            User user = getUser(request);
            String id = request.params(":personId");
            Clubber clubber = findClubber(app, id);
            if(maySeeRecords(user, clubber)) {
                String bookId = request.params(":bookId");
                Optional<Book> book = clubber.getClub()
                        .map(c -> c.getCurriculum()
                                .getBooks().stream()
                                .filter(b -> b.getId().equals(bookId))
                                .findFirst())
                        .orElse(Optional.empty());
                if (book.isPresent()) {
                    Map<String, Object> model = map("me", (Object) user)
                            .put("clubber", clubber)
                            .put("book", book.get())
                            .put("sectionGroups", getBookRecordsByGroup(clubber, book).entrySet())
                            .build();
                    return new ModelAndView(model, "clubberBook.ftl");
                } else {
                    response.status(HttpStatus.SC_NOT_FOUND);
                }
            } else {
                response.status(HttpStatus.SC_FORBIDDEN);
            }
            return null;
        }, new FreeMarkerEngine());
    }

    private Map<SectionGroup, List<ClubberRecord>> getBookRecordsByGroup(Clubber clubber, Optional<Book> book) {
        return book.get().getSectionGroups().stream()
                .flatMap(g -> g.getSections().stream())
                .map(s -> clubber.getRecord(Optional.of(s)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.groupingBy(r -> r.getSection().getContainer(), LinkedHashMap::new, toList()));
    }

    private Optional<Book> getLastBook(Clubber clubber) {
        return UtilityMethods.optMap(clubber.getClub(), c -> c.getCurriculum().getBooks().stream().sorted(Collections.reverseOrder()).findFirst());
    }

    private Boolean maySeeRecords(User user, Clubber clubber) {
        return  isLeaderInSameClub(user, clubber) ||
                isListenerInSameClub(user, clubber) ||
                isParentOf(user, clubber) ||
                isSamePerson(user, clubber);
    }

    private Boolean maySignRecords(User user, Clubber clubber) {
        return  isListenerInSameClub(user, clubber) &&
                !(
                    isParentOf(user, clubber) ||
                    isSamePerson(user, clubber)
                );
    }

    private boolean isSamePerson(User user, Clubber clubber) {
        return user.getUpdater() == clubber.getUpdater();
    }

    private boolean isLeaderInSameClub(User user, Clubber clubber) {
        return user.asClubLeader()
                .map(l -> clubber.getClub().orElse(null) == l.getClub().orElse(null))
                .orElse(false);
    }

    private boolean isListenerInSameClub(User user, Clubber clubber) {
        return user.asListener()
                .map(l -> clubber.getClub().orElse(null) == l.getClub().orElse(null))
                .orElse(false);
    }

    private boolean isParentOf(User user, Clubber clubber) {
        return user.asParent()
                .map(Person::getFamily)
                .map(of -> of.map(f ->f.getId().equals(clubber.getFamily().map(Family::getId).orElse(null))).orElse(false))
                .orElse(false);
    }

    private ClubberRecord getClubberRecord(Request request, Clubber clubber) {
        String sectionId = request.params(":sectionId");
        Optional<Section> section = UtilityMethods.optMap(clubber.getClub(),
                c -> c.getCurriculum().lookup(decode(sectionId)));
        return clubber.getRecord(section).orElseThrow(IllegalArgumentException::new);
    }

    private Clubber findClubber(ClubApplication app, String id) {
        return app.getPersonManager()
                        .lookup(id)
                        .orElseThrow(IllegalArgumentException::new)
                        .asClubber()
                        .orElseThrow(IllegalArgumentException::new);
    }
}
