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
import com.github.averyregier.club.domain.program.SectionGroup;
import com.github.averyregier.club.domain.utility.InputField;
import com.github.averyregier.club.domain.utility.Named;
import com.github.averyregier.club.domain.utility.UtilityMethods;
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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.averyregier.club.domain.utility.UtilityMethods.*;
import static java.util.stream.Collectors.toList;
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
            HashMap<Object, Object> model = new HashMap<>();
            model.put("program", app.getProgram(request.params(":id")));
            return new ModelAndView(model, "viewProgram.ftl");
        }, new FreeMarkerEngine());

        get("/protected/club/:club", (request, response) -> {
            Optional<Club> club = app.getClubManager().lookup(request.params(":club"));
            if(club.isPresent()) {
                HashMap<Object, Object> model = new HashMap<>();
                model.put("club", club.get());
                model.put("clubbers", club.get().getClubNightReport().entrySet());
                return new ModelAndView(model, "viewClub.ftl");
            } else {
                response.redirect("/protected/my");
                return null;
            }
        }, new FreeMarkerEngine());

        get("/protected/club/:club/clubbers", (request, response) -> {
            Optional<Club> club = app.getClubManager().lookup(request.params(":club"));
            if(club.isPresent()) {
                HashMap<Object, Object> model = new HashMap<>();
                model.put("club", club.get());
                return new ModelAndView(model, "allClubbersQuick.ftl");
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
                    Ceremony ceremony = persistCeremony(app, new CeremonyAdapter());
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
                    if (maySignRecords(user, clubber)) {
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
                    .put("previousSection", clubber.getSectionBefore(record.getSection()))
                    .put("nextSection", clubber.getSectionAfter(record.getSection()))
                    .put("maySign", maySignRecords(user, clubber) && !record.getSigning().isPresent())
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
                    book = getLastBook(clubber);
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
                Optional<ModelAndView> modelAndView =
                        optMap(clubber.getClub().map(Club::getCurriculum), c -> c.lookupBook(bookId))
                                .map(book -> mapClubberBookRecords(user, clubber, book));
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
                                map("clubber", (Object) clubber)
                                        .put("defaultListener", getDefaultListener(user, clubber))
                                        .put("listeners", clubber.getClub().get().getListeners())
                                        .put("suggestedDate", getSuggestedDate(clubber))
                                        .build(), "catchup.ftl");
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
                        LocalDate date = parseDate(request).orElse(LocalDate.now());
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
        LocalDate defaultDate = LocalDate.now();
        return clubber.getLastRecord()
                .map(r -> r.getSigning().map(Signing::getDate)
                        .orElse(defaultDate))
                .orElse(defaultDate);
    }

    private ModelAndView mapClubberBookRecords(User user, Clubber clubber, Book book) {
        Map<String, Object> model = map("me", (Object)user)
                .put("clubber", clubber)
                .put("previous", findPreviousBook(clubber, book))
                .put("next", findNextBook(clubber, book))
                .put("book", book)
                .put("sectionGroups", getBookRecordsByGroup(clubber, book).entrySet())
                .put("awards", getBookAwards(clubber, book).entrySet())
                .put("catchup", mayRecordSigning(user, clubber))
                .build();
        return new ModelAndView(model, "clubberBook.ftl");
    }

    private Map<Award, Optional<AwardPresentation>> getBookAwards(Clubber clubber, Book book) {
        Map<Named, AwardPresentation> awarded = clubber.getAwards().stream()
                .collect(Collectors.toMap(AwardPresentation::forAccomplishment, Function.identity(), (u,v)->u));
        Map<Award, Optional<AwardPresentation>> map = new LinkedHashMap<>();
        book.getSections().stream()
                .flatMap(s -> s.getAwards().stream())
                .filter(UtilityMethods::notNull)
                .distinct()
                .forEach(a-> map.put(a, Optional.ofNullable(awarded.get(a))));
        return map;
    }

    private Optional<Book> findPreviousBook(Clubber clubber, Book book) {
        Optional<Book> previous = findPrevious(book, book.getContainer().getBooks());
        if(previous.isPresent()) {
            Optional<Book> after = previous;
            do {
                previous = after;
                after = findNextBook(clubber, after.get());
            } while(after.isPresent() && after.get() != book);
        }
        return previous;
    }

    private Optional<Book> findNextBook(Clubber clubber, Book book) {
        // get any optional extra books that may have been done in a previous year
        LinkedHashSet<Book> bookList = new LinkedHashSet<>(book.getContainer().recommendedBookList(book.getAgeGroups().get(0)));
        bookList.addAll(book.getContainer().recommendedBookList(clubber.getCurrentAgeGroup()));

        return findNext(book, bookList);
    }

    private Map<SectionGroup, List<ClubberRecord>> getBookRecordsByGroup(Clubber clubber, Book book) {
        return book.getSectionGroups().stream()
                .flatMap(g -> g.getSections().stream())
                .map(s -> clubber.getRecord(Optional.of(s)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.groupingBy(r -> r.getSection().getContainer(), LinkedHashMap::new, toList()));
    }

    private Optional<Book> getLastBook(Clubber clubber) {
        return optMap(clubber.getClub(), c -> findLast(c.getCurriculum().getBooks()));
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
