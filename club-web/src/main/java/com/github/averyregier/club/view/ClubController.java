package com.github.averyregier.club.view;

import com.github.averyregier.club.application.ClubApplication;
import com.github.averyregier.club.application.ClubFactory;
import com.github.averyregier.club.broker.CeremonyBroker;
import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.club.adapter.CeremonyAdapter;
import com.github.averyregier.club.domain.program.AccomplishmentLevel;
import com.github.averyregier.club.domain.program.Award;
import com.github.averyregier.club.domain.program.Book;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.utility.MapBuilder;
import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import static com.github.averyregier.club.domain.utility.UtilityMethods.*;
import static spark.Spark.*;

/**
 * Created by avery on 12/26/14.
 */
public class ClubController extends BaseController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public void init(ClubApplication app) {
        get("/:id/viewProgram", (request, response) -> {
            Program program = app.getProgram(request.params(":id"));
            return render(
                    newModel(request, program.getShortCode())
                            .put("program", program)
                            .build(),
                    "viewProgram.ftl");
        });

        get("/club/:club", (request, response) -> {
            Optional<Club> club = lookupClub(app, request);
            if (club.isPresent()) {
                return render(
                        newModel(request, club.get().getShortCode())
                                .put("club", club.get())
                                .put("clubbers", club.get().getClubNightReport().entrySet())
                                .build(),
                        "viewClub.ftl");
            } else {
                return gotoMy(response);
            }
        });

        get("/club/:club/clubbers", (request, response) -> {
            Optional<Club> club = lookupClub(app, request);
            if (club.isPresent()) {
                return render(
                        newModel(request, "All " + club.get().getShortCode() + " Clubber's Upcoming Sections")
                                .put("club", club.get())
                                .build(),
                        "allClubbersQuick.ftl");
            } else {
                return gotoMy(response);
            }
        });

        post("/club/:club/workers/:personId", (request, response) -> {
            Optional<Club> club = lookupClub(app, request);
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

        before("/club/:club/awards", (request, response) -> {
            if (request.requestMethod().equalsIgnoreCase("POST")) {
                Optional<Club> club = lookupClub(app, request);
                if (club.isPresent()) {
                    HashSet<String> awards = asLinkedSet(request.queryMap("award").values());
                    Ceremony ceremony = persistCeremony(app, new CeremonyAdapter(findToday(club)));
                    club.get().getAwardsNotYetPresented(AccomplishmentLevel.group).stream()
                            .filter(a -> awards.contains(a.getId()))
                            .forEach(a -> a.presentAt(ceremony));
                }
                response.redirect(request.url());
                halt();
            }
        });

        get("/club/:club/awards", (request, response) -> {
            Optional<Club> club = lookupClub(app, request);
            if (club.isPresent()) {
                String accomplishmentLevel = request.queryParams("accomplishmentLevel");
                AccomplishmentLevel type = AccomplishmentLevel.valueOf(
                        accomplishmentLevel != null ? accomplishmentLevel : AccomplishmentLevel.group.name());
                return render(
                        newModel(request, club.get().getShortCode() + " Awards")
                                .put("club", club.get())
                                .put("awards", club.get().getAwardsNotYetPresented(type))
                                .build(),
                        "awards.ftl");
            } else {
                return gotoMy(response);
            }
        });

        get("/my", (request, response) -> {
            User user = getUser(request);
            Collection<Program> programs = app.getPrograms(user);
            if (programs.isEmpty()) {
                response.redirect("/protected/hello");
                return null;
            } else {
                MapBuilder<String, Object> model = newModel(request, "Home")
                        .put("me", user)
                        .put("programs", programs);

                user.asClubLeader().ifPresent(l -> l.getClub().ifPresent(c -> model.put("mygroup", c)));
                return render(model.build(), "my.ftl");
            }
        });

        before("/clubbers/:personId/sections/:sectionId", (request, response) -> {
            if (request.requestMethod().equalsIgnoreCase("POST")) {
                User user = getUser(request);
                String id = request.params(":personId");
                Clubber clubber = app.findClubber(id);
                ClubberRecord record = getClubberRecord(request, clubber);
                if ("true".equalsIgnoreCase(request.queryParams("sign"))) {
                    if (clubber.maySignRecords(user)) {
                        Listener listener = app.findListener(request.queryParams("listener"), clubber.getClub().get());
                        LocalDate date = parseDate(request.queryParams("date")).orElseGet(() -> findToday(clubber));
                        record.sign(listener, request.queryParams("note"), date);
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

        get("/clubbers/:personId/sections/:sectionId", (request, response) -> {
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
                    .put("mayUnSign", maySign && record.mayBeUnsigned())
                    .put("suggestedDate", findToday(clubber));
            if (clubber.isLeaderInSameClub(user)) {
                builder = builder
                        .put("defaultListener", getDefaultListener(user, clubber))
                        .put("listeners", clubber.getClub().get().getListeners());
            }
            return render(builder.build(), "clubberSection.ftl");
        });

        before("/clubbers/:personId/sections", ((request, response) -> {
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

        get("/clubbers/:personId/books/:bookId", (request, response) -> {
            User user = getUser(request);
            String id = request.params(":personId");
            Clubber clubber = app.findClubber(id);
            if (clubber.maySeeRecords(user)) {
                String bookId = request.params(":bookId");
                Optional<String> modelAndView = clubber.getBook(bookId)
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
        });

        get("/clubbers/:personId/sections/:sectionId/awards/:awardName/catchup", (request, response) -> {
            User user = getUser(request);
            String id = request.params(":personId");
            Clubber clubber = app.findClubber(id);
            if (clubber.mayRecordSigning(user)) {
                Optional<Section> section = clubber.lookupSection(request.params(":sectionId"));
                Optional<Award> award = optMap(section, s -> s.findAward(request.params(":awardName")));
                if (award.isPresent()) {
                    if (!clubber.hasAward(award.get())) {
                        return render(
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
        });

        post("/clubbers/:personId/sections/:sectionId/awards/:awardName/catchup", (request, response) -> {
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

        post("/clubbers/:personId/books/:bookId/awards/:awardName/presentation", (request, response) -> {
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

    private String mapClubberBookRecords(Request request, User user, Clubber clubber, Book book) {
        Map<String, Object> model = newModel(request, clubber.getName().getFullName() + " - " + book.getName())
                .put("me", user)
                .put("clubber", clubber)
                .put("previous", clubber.findPreviousBook(book, clubber.getClub().get().getProgram()))
                .put("next", clubber.findNextBook(book))
                .put("book", book)
                .put("sectionGroups", clubber.getBookRecordsByGroup(book).entrySet())
                .put("awards", clubber.getBookAwards(book).entrySet())
                .put("catchup", clubber.mayRecordSigning(user))
                .build();
        return render(model, "clubberBook.ftl");
    }

    private ClubberRecord getClubberRecord(Request request, Clubber clubber) {
        String sectionId = request.params(":sectionId");
        Optional<Section> section = clubber.lookupSection(decode(sectionId));
        return clubber.getRecord(section).orElseThrow(IllegalArgumentException::new);
    }
}
