package com.github.averyregier.club.view;

import com.github.averyregier.club.application.ClubApplication;
import com.github.averyregier.club.application.ClubFactory;
import com.github.averyregier.club.broker.CeremonyBroker;
import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.club.adapter.CeremonyAdapter;
import com.github.averyregier.club.domain.program.Award;
import com.github.averyregier.club.domain.program.Book;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.utility.MapBuilder;
import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Request;
import spark.template.freemarker.FreeMarkerEngine;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.averyregier.club.domain.utility.UtilityMethods.*;
import static spark.Spark.*;

/**
 * Created by avery on 12/26/14.
 */
public class ClubController extends ModelMaker {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public void init(ClubApplication app) {
        before("/", (request, response) -> {
            response.redirect("/protected/my");
        });

        get("/protected/:id/viewProgram", (request, response) -> {
            Program program = app.getProgram(request.params(":id"));
            return new ModelAndView(
                    newModel(request, program.getShortCode())
                            .put("program", program)
                            .build(),
                    "viewProgram.ftl");
        }, new FreeMarkerEngine());

        get("/protected/club/:club", (request, response) -> {
            Optional<Club> club = app.getClubManager().lookup(request.params(":club"));
            if(club.isPresent()) {
                return new ModelAndView(
                        newModel(request, club.get().getShortCode())
                            .put("club", club.get())
                            .put("clubbers", club.get().getClubNightReport().entrySet())
                            .build(),
                        "viewClub.ftl");
            } else {
                response.redirect("/protected/my");
                return null;
            }
        }, new FreeMarkerEngine());

        get("/protected/club/:club/clubbers", (request, response) -> {
            Optional<Club> club = app.getClubManager().lookup(request.params(":club"));
            if(club.isPresent()) {
                return new ModelAndView(
                        newModel(request, "All " + club.get().getShortCode() + " Clubber's Upcoming Sections")
                            .put("club", club.get())
                            .build(),
                        "allClubbersQuick.ftl");
            } else {
                response.redirect("/protected/my");
                return null;
            }
        }, new FreeMarkerEngine());

        get("/protected/club/:club/workers", (request, response) -> {
            Optional<Club> club = app.getClubManager().lookup(request.params(":club"));
            if(club.isPresent()) {
                return new ModelAndView(
                        newModel(request, "Add "+club.get().getShortCode()+" Workers")
                            .put("club", club.get())
                            .build(),
                        "addWorker.ftl");
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
                    MapBuilder<String, Object> model = newModel(request,
                            "Assign "+club.get().getShortCode()+" role to "+person.get().getName().getFullName())
                        .put("club", club.get())
                        .put("person", person.get())
                        .put("roles", ClubLeader.LeadershipRole.values());
                    optMap(club, Club::asProgram).ifPresent(p -> model.put("clubs", getClubList(p)));
                    return new ModelAndView(model.build(), "workerRole.ftl");
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
                    Optional<Family> family = person.get().getFamily();
                    if (family.map(Family::shouldInvite).orElse(false)) {
                        response.redirect("/protected/family/" + family.get().getId() + "/invite");
                        return null;
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
                    Ceremony ceremony = persistCeremony(app, new CeremonyAdapter(findToday(club)));
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
                return new ModelAndView(
                        newModel(request, club.get().getShortCode() + " Awards")
                                .put("club", club.get())
                                .build(),
                        "awards.ftl");
            } else {
                response.redirect("/protected/my");
                return null;
            }
        }, new FreeMarkerEngine());

        get("/protected/my", (request, response) -> {
            User user = getUser(request);
            Collection<Program> programs = app.getPrograms(user);
            if(programs.isEmpty()) {
                response.redirect("/protected/hello");
                return null;
            } else {
                MapBuilder<String, Object> model = newModel(request, "Home")
                        .put("me", user)
                        .put("programs", programs);

                user.asClubLeader().ifPresent(l -> l.getClub().ifPresent(c -> model.put("mygroup", c)));
                return new ModelAndView(model.build(), "my.ftl");
            }
        }, new FreeMarkerEngine());

        before("/protected/clubbers/:personId/sections/:sectionId", (request, response) -> {
            if (request.requestMethod().equalsIgnoreCase("POST")) {
                User user = getUser(request);
                String id = request.params(":personId");
                Clubber clubber = app.findClubber(id);
                ClubberRecord record = getClubberRecord(request, clubber);
                if ("true".equalsIgnoreCase(request.queryParams("sign"))) {
                    if (clubber.maySignRecords(user)) {
                        Listener listener = app.findListener(request.queryParams("listener"), clubber.getClub().get());
                        record.sign(listener, request.queryParams("note"));
                    } else {
                        throw new IllegalAccessException("You are not authorized to sign this section");
                    }
                } else if ("true".equalsIgnoreCase(request.queryParams("unsign"))) {
                    if (clubber.maySignRecords(user)) {
                        record.unSign();
                    } else {
                        throw new IllegalAccessException("You are not authorized to un-sign this section");
                    }
                }
                response.redirect(request.url());
                halt();
            }
        });

        get("/protected/clubbers/:personId/sections/:sectionId", (request, response) -> {
            User user = getUser(request);
            String id = request.params(":personId");
            Clubber clubber = app.findClubber(id);
            ClubberRecord record = getClubberRecord(request, clubber);
            boolean maySign = clubber.maySignRecords(user);
            Section section = record.getSection();
            MapBuilder<String, Object> builder = newModel(request, section.getSectionTitle())
                    .put("me", user)
                    .put("clubber", clubber)
                    .put("section", section)
                    .put("record", record)
                    .put("previousSection", clubber.getSectionBefore(section))
                    .put("nextSection", clubber.getSectionAfter(section))
                    .put("maySign", maySign && !record.getSigning().isPresent())
                    .put("mayUnSign", maySign && record.mayBeUnsigned());
            if(clubber.isLeaderInSameClub(user)) {
                builder = builder
                        .put("defaultListener", getDefaultListener(user, clubber))
                        .put("listeners", clubber.getClub().get().getListeners());
            }
            return new ModelAndView(builder.build(), "clubberSection.ftl");
        }, new FreeMarkerEngine());

        before("/protected/clubbers/:personId/sections", ((request, response) -> {
            User user = getUser(request);
            String id = request.params(":personId");
            Clubber clubber = app.findClubber(id);
            if (clubber.maySeeRecords(user)) {
                Optional<Section> nextSection = clubber.getNextSection();
                Optional<Book> book = nextSection.map(s -> s.getContainer().getBook());
                if (!nextSection.isPresent()) {
                    book = clubber.getLastBook();
                }
                if (book.isPresent()) {
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
            Clubber clubber = app.findClubber(id);
            if (clubber.maySeeRecords(user)) {
                String bookId = request.params(":bookId");
                Optional<ModelAndView> modelAndView = clubber.getBook(bookId)
                        .map(book -> mapClubberBookRecords(request, user, clubber, book));
                if (modelAndView.isPresent()) {
                    return modelAndView.get();
                } else {
                    response.status(HttpStatus.SC_NOT_FOUND);
                }
            } else {
                response.status(HttpStatus.SC_FORBIDDEN);
            }
            return null;
        }, new FreeMarkerEngine());

        get("/protected/clubbers/:personId/sections/:sectionId/awards/:awardName/catchup", (request, response) -> {
            User user = getUser(request);
            String id = request.params(":personId");
            Clubber clubber = app.findClubber(id);
            if(clubber.mayRecordSigning(user)) {
                Optional<Section> section = clubber.lookupSection(request.params(":sectionId"));
                Optional<Award> award = optMap(section, s -> s.findAward(request.params(":awardName")));
                if (award.isPresent()) {
                    if(!clubber.hasAward(award.get())) {
                        return new ModelAndView(
                                newModel(request, "Catchup")
                                    .put("clubber", clubber)
                                    .put("defaultListener", getDefaultListener(user, clubber))
                                    .put("listeners", clubber.getClub().get().getListeners())
                                    .put("suggestedDate", getSuggestedDate(clubber))
                                    .build(),
                                "catchup.ftl");
                    } else {
                        response.status(HttpStatus.SC_PRECONDITION_FAILED);
                    }
                } else {
                    response.status(HttpStatus.SC_NOT_FOUND);
                }
            } else {
                response.status(HttpStatus.SC_FORBIDDEN);
            }
            return null;
        }, new FreeMarkerEngine());

        post("/protected/clubbers/:personId/sections/:sectionId/awards/:awardName/catchup", (request, response) -> {
            User user = getUser(request);
            String id = request.params(":personId");
            Clubber clubber = app.findClubber(id);
            if (clubber.mayRecordSigning(user)) {
                Optional<Section> section = clubber.lookupSection(request.params(":sectionId"));
                Optional<Award> award = optMap(section, s -> s.findAward(request.params(":awardName")));
                if (award.isPresent()) {
                    if (!clubber.hasAward(award.get())) {
                        Listener listener = app.findListener(request.queryParams("listener"), clubber.getClub().get());
                        LocalDate date = parseDate(request.queryParams("date")).orElseGet(() -> findToday(clubber).minusDays(1));
                        catchUp(clubber, listener, award.get(), date, app);
                        response.redirect("/protected/clubbers/" + clubber.getId() + "/sections");
                    } else {
                        response.status(HttpStatus.SC_NOT_MODIFIED);
                    }
                } else {
                    response.status(HttpStatus.SC_NOT_FOUND);
                }
            } else {
                response.status(HttpStatus.SC_FORBIDDEN);
            }
            return null;
        });

        post("/protected/clubbers/:personId/books/:bookId/awards/:awardName/presentation", (request, response) -> {
            User user = getUser(request);
            String id = request.params(":personId");
            Clubber clubber = app.findClubber(id);
            if (clubber.mayRecordSigning(user)) {
                Optional<Book> book = clubber.getBook(request.params(":bookId"));
                if (book.isPresent()) {
                    Optional<AwardPresentation> award = clubber.findPresentation(book.get(), request.params(":awardName"));
                    if (award.isPresent()) {
                        if (!award.get().notPresented()) {
                            award.get().undoPresentation();
                            response.redirect("/protected/clubbers/" + clubber.getId() + "/sections");
                        } else {
                            response.status(HttpStatus.SC_NOT_MODIFIED);
                        }
                    } else {
                        response.status(HttpStatus.SC_NOT_FOUND);
                    }
                } else {
                    response.status(HttpStatus.SC_NOT_FOUND);
                }
            } else {
                response.status(HttpStatus.SC_FORBIDDEN);
            }
            return null;
        });
    }

    private List<Object> getClubList(Program p) {
        return Stream.concat(Stream.of(p), p.getClubs().stream()).collect(Collectors.toList());
    }

    private String getDefaultListener(User user, Clubber clubber) {
        return clubber.isInSameClub(user.asListener()) ? user.getId() : clubber.getLastListener().map(Person::getId).orElse("");
    }

    private Ceremony persistCeremony(ClubFactory factory, Ceremony ceremony) {
        new CeremonyBroker(factory.getConnector()).persist(ceremony);
        return ceremony;
    }

    private void catchUp(Clubber clubber, Listener listener, Award award, LocalDate date, ClubFactory factory) {
        Ceremony ceremony = persistCeremony(factory, new CeremonyAdapter(date) {
            @Override
            public String getName() {
                return "Catch-up";
            }
        });
        clubber.catchup(listener, award, date, ceremony, "Catchup records to current status");
    }

    private LocalDate getSuggestedDate(Clubber clubber) {
        LocalDate defaultDate = findToday(clubber).minusDays(1);
        return clubber.getLastRecord()
                .map(r -> r.getSigning().map(Signing::getDate)
                        .orElse(defaultDate))
                .orElse(defaultDate);
    }

    private ModelAndView mapClubberBookRecords(Request request, User user, Clubber clubber, Book book) {
        Map<String, Object> model = newModel(request, clubber.getName().getFullName()+" - "+book.getName())
                .put("me", user)
                .put("clubber", clubber)
                .put("previous", clubber.findPreviousBook(book))
                .put("next", clubber.findNextBook(book))
                .put("book", book)
                .put("sectionGroups", clubber.getBookRecordsByGroup(book).entrySet())
                .put("awards", clubber.getBookAwards(book).entrySet())
                .put("catchup", clubber.mayRecordSigning(user))
                .build();
        return new ModelAndView(model, "clubberBook.ftl");
    }

    private ClubberRecord getClubberRecord(Request request, Clubber clubber) {
        String sectionId = request.params(":sectionId");
        Optional<Section> section = clubber.lookupSection(decode(sectionId));
        return clubber.getRecord(section).orElseThrow(IllegalArgumentException::new);
    }
}
