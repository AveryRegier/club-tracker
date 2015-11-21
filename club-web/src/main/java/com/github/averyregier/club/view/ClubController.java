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
import com.github.averyregier.club.domain.utility.InputField;
import com.github.averyregier.club.domain.utility.MapBuilder;
import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Request;
import spark.template.freemarker.FreeMarkerEngine;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
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
                        newModel(request, "All "+club.get().getShortCode()+" Clubber's Upcoming Sections")
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
                    if(family.map(Family::shouldInvite).orElse(false)) {
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
                        newModel(request, club.get().getShortCode()+" Awards")
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
                Clubber clubber = findClubber(app, id);
                ClubberRecord record = getClubberRecord(request, clubber);
                if ("true".equalsIgnoreCase(request.queryParams("sign"))) {
                    if (maySignRecords(user, clubber)) {
                        record.sign(user.asListener().orElseThrow(IllegalStateException::new), request.queryParams("note"));
                    } else {
                        throw new IllegalAccessException("You are not authorized to sign this section");
                    }
                } else if("true".equalsIgnoreCase(request.queryParams("unsign"))) {
                    if (maySignRecords(user, clubber)) {
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
            Clubber clubber = findClubber(app, id);
            ClubberRecord record = getClubberRecord(request, clubber);
            boolean maySign = maySignRecords(user, clubber);
            Section section = record.getSection();
            Map<String, Object> model = newModel(request, getSectionTitle(section))
                    .put("me", user)
                    .put("clubber", clubber)
                    .put("section", section)
                    .put("record", record)
                    .put("previousSection", clubber.getSectionBefore(section))
                    .put("nextSection", clubber.getSectionAfter(section))
                    .put("maySign", maySign && !record.getSigning().isPresent())
                    .put("mayUnSign", maySign && record.mayBeUnsigned())
                    .build();
            return new ModelAndView(model, "clubberSection.ftl");
        }, new FreeMarkerEngine());

        before("/protected/clubbers/:personId/sections", ((request, response) -> {
            User user = getUser(request);
            String id = request.params(":personId");
            Clubber clubber = findClubber(app, id);
            if (maySeeRecords(user, clubber)) {
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
            Clubber clubber = findClubber(app, id);
            if (maySeeRecords(user, clubber)) {
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
            Clubber clubber = findClubber(app, id);
            if(mayRecordSigning(user, clubber)) {
                Optional<Section> section = lookupSection(clubber, request.params(":sectionId"));
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
            Clubber clubber = findClubber(app, id);
            if (mayRecordSigning(user, clubber)) {
                Optional<Section> section = lookupSection(clubber, request.params(":sectionId"));
                Optional<Award> award = optMap(section, s -> s.findAward(request.params(":awardName")));
                if (award.isPresent()) {
                    Listener listener = findListener(app, request.queryParams("listener"), clubber.getClub().get());
                    if (!clubber.hasAward(award.get())) {
                        LocalDate date = parseDate(request).orElseGet(() -> findToday(clubber).minusDays(1));
                        catchUp(clubber, listener, award.get(), date,  app);
                        response.redirect("/protected/clubbers/"+clubber.getId()+"/sections");
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
    }

    private String getSectionTitle(Section section) {
        return section.getGroup().getContainer().getShortCode()+"-"+section.getGroup().getShortCode()+"."+section.getShortCode();
    }

    private List<Object> getClubList(Program p) {
        return Stream.concat(Stream.of(p), p.getClubs().stream()).collect(Collectors.toList());
    }

    private String getDefaultListener(User user, Clubber clubber) {
        return isInSameClub(clubber, user.asListener()) ? user.getId() : getLastListener(clubber).map(Person::getId).orElse("");
    }

    private Optional<Listener> getLastListener(Clubber clubber) {
        return chain(clubber.getLastRecord(), ClubberRecord::getSigning).map(Signing::by);
    }

    private Optional<LocalDate> parseDate(Request request) {
        String date = request.queryParams("date");
        if(killWhitespace(date) == null) return Optional.empty();
        try {
            Date input = InputField.Type.dateFormat.get()
                    .parse(date);
            return Optional.of(input.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        } catch (ParseException e) {
            return Optional.empty();
        }
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
        award.getSections().stream()
                .flatMap(s -> stream(clubber.getRecord(Optional.of(s))))
                .map(r -> r.catchup(listener, "Catchup records to current status", date))
                .flatMap(s->s.getCompletionAwards().stream())
                .forEach(awardPresentation -> awardPresentation.presentAt(ceremony));
    }

    private Optional<Section> lookupSection(Clubber clubber, String sectionId) {
        return optMap(clubber.getClub(), c -> c.getCurriculum().lookup(sectionId));
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
                .put("catchup", mayRecordSigning(user, clubber))
                .build();
        return new ModelAndView(model, "clubberBook.ftl");
    }


    private boolean maySeeRecords(Person person, Clubber clubber) {
        return  isLeaderInSameClub(person, clubber) ||
                isListenerInSameClub(person, clubber) ||
                isParentOf(person, clubber) ||
                isSamePerson(person, clubber);
    }

    private boolean maySignRecords(Person user, Clubber clubber) {
        return  isListenerInSameClub(user, clubber) &&
                !(
                    isParentOf(user, clubber) ||
                    isSamePerson(user, clubber)
                );
    }

    private boolean mayRecordSigning(Person user, Clubber clubber) {
        return  (isListenerInSameClub(user, clubber) ||
                 isLeaderInSameClub(user, clubber)) &&
                !isSamePerson(user, clubber);
        // if a leader is also a parent, we'll still allow them to maintain section history
    }

    private boolean isSamePerson(Person a, Person b) {
        return a.getUpdater() == b.getUpdater();
    }

    private boolean isLeaderInSameClub(Person person, Clubber clubber) {
        return isInAncestorClub(clubber, person.asClubLeader());
    }

    private boolean isInAncestorClub(Clubber clubber, Optional<? extends ClubMember> member) {
        return getParentClubId(clubber)
                .map(parentClubId ->
                        streamAncestry(member)
                                .filter(g -> parentClubId.equals(g.getId()))
                                .findFirst()
                                .map(x -> true)
                                .orElse(false))
                .orElse(false);
    }

    private Stream<ClubGroup> streamAncestry(Optional<? extends ClubMember> member) {
        return stream(optMap(member, m -> m.getClub().map(c -> (ClubGroup) c)), ClubGroup::getParentGroup);
    }

    private Optional<String> getParentClubId(ClubMember member) {
        return chain(member.getClub(), Club::getParentGroup).map(ClubGroup::getId);
    }

    private boolean isListenerInSameClub(Person person, Clubber clubber) {
        return isInSameClub(clubber, person.asListener());
    }

    private boolean isInSameClub(Clubber clubber, Optional<? extends ClubMember> member) {
        return member
                .map(l -> getClubId(clubber).map(id -> id.equals(getClubId(l).orElse(null))).orElse(false))
                .orElse(false);
    }

    private Optional<String> getClubId(ClubMember member) {
        return member.getClub().map(Club::getId);
    }

    private boolean isParentOf(Person person, Clubber clubber) {
        return person.asParent()
                .map(Person::getFamily)
                .map(of -> of.map(f -> f.getId().equals(clubber.getFamily().map(Family::getId).orElse(null))).orElse(false))
                .orElse(false);
    }

    private ClubberRecord getClubberRecord(Request request, Clubber clubber) {
        String sectionId = request.params(":sectionId");
        Optional<Section> section = lookupSection(clubber, decode(sectionId));
        return clubber.getRecord(section).orElseThrow(IllegalArgumentException::new);
    }

    private Clubber findClubber(ClubApplication app, String id) {
        return findPerson(app, id)
                        .asClubber()
                        .orElseThrow(IllegalArgumentException::new);
    }

    private Listener findListener(ClubApplication app, String id, Club club) {
        Listener listener = findPerson(app, id)
                .asListener()
                .orElseThrow(IllegalArgumentException::new);
        if(listener.getClub().orElseThrow(IllegalArgumentException::new).getId().equals(club.getId())) {
            return listener;
        } else throw new IllegalArgumentException();
    }

    private Person findPerson(ClubApplication app, String id) {
        return app.getPersonManager()
                        .lookup(id)
                        .orElseThrow(IllegalArgumentException::new);
    }
}
