package com.github.averyregier.club.view;

import com.github.averyregier.club.application.ClubApplication;
import com.github.averyregier.club.broker.InviteBroker;
import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.club.adapter.InvitationAdapter;
import com.github.averyregier.club.domain.utility.HasId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Request;
import spark.template.freemarker.FreeMarkerEngine;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.averyregier.club.domain.utility.UtilityMethods.chain;
import static spark.Spark.*;

public class InviteController extends ModelMaker {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void init(ClubApplication app) {
        String invitationPath = "/protected/person/:id/invite";
        String familyInvitePath = "/protected/family/:familyId/invite";

        before(familyInvitePath, (request, response) -> {
            Optional<Family> family = app.getPersonManager().lookupFamily(request.params(":familyId"));
            if (family.isPresent()) {
                Set<Parent> toInvite = family.map(Family::getParents).orElse(Collections.emptySet())
                        .stream()
                        .filter(p -> p.getEmail().isPresent())
                        .filter(p -> !p.getLogin().isPresent())
                        .collect(Collectors.toSet());
                if (!toInvite.isEmpty()) {
                    request.attribute("people", toInvite);
                    return;
                }
            }
            response.redirect("/protected/my");
            halt();
        });

        get(familyInvitePath, (request, response) -> {
            return new ModelAndView(toMap("people", request.attribute("people")),
                    "inviteFamily.ftl");
        }, new FreeMarkerEngine());

        before(invitationPath, (request, response) -> {
            String id = request.params(":id");
            Optional<Person> person = app.getPersonManager().lookup(id);
            if (!person.isPresent()) {
                logger.debug("person " + id + " doesn't exist");
            }

            User inviter = getUser(request);
            if (inviter.asClubLeader().isPresent() || areFamilyMembers(person, inviter)) {
                if (person.map(Person::getEmail).isPresent()) {
                    logger.info("Inviting " + request.params(":id"));
                    Invitation invite = invite(app, inviter, person.get());
                    Program toInviteTo = findInvitedProgram(invite);

                    String mailToLink = "mailto:" + invite.getPerson().getEmail().get()
                            + "?Subject=" +
                            encode(toInviteTo.getCurriculum().getShortCode() + " at " + toInviteTo.getShortCode())
                            + "&Body=" +
                            encode(makeInvitationText(request, invite, toInviteTo));
                    logger.info(mailToLink);
                    response.redirect(mailToLink);
                    halt();
                    return;
                } else {
                    logger.debug("person " + id + " does not have an email address");
                }
            } else {
                logger.debug(inviter.getName().getFullName() +
                        " (" + inviter.getId() +
                        ") does not have the privilege to send invitations");
            }
            response.redirect("/protected/my");
            halt();
        });
    }

    private String encode(String s) {
        return s.replace(" ", "%20").replace("\n", "%0D%0A");
    }

    private boolean areFamilyMembers(Optional<Person> person, User inviter) {
        return inviter.getFamily()
                .map(f -> f.getId().equals(
                        person.get()
                                .getFamily()
                                .map(HasId::getId)
                                .orElse(null)))
                .orElse(false);
    }

    private String makeInvitationText(Request request, Invitation invite, Program toInviteTo) {
        String invitationURL = createInvitationURL(request, invite.getPerson(), invite);
        String innerTemplate = getInnerTemplate(invite);

        String template = lines(
                "Hello {0},",
                "",
                innerTemplate,
                "",
                "To get started please click this invitation link: {4} ",
                "",
                "Thanks,",
                "{3}");

        String fullName = invite.getPerson().getName().getFullName();
        String orgName = toInviteTo.getShortCode();
        String programName = toInviteTo.getCurriculum().getShortCode();
        String senderName = (invite.by().asClubLeader()
                .map(l -> l.getLeadershipRole().name()).orElse("") + " " +
                invite.by().getName().getFullName()).trim();
        String result = MessageFormat.format(template,
                fullName,
                orgName,
                programName,
                senderName,
                invitationURL);
        logger.info(result);
        return result;
    }

    private Program findInvitedProgram(Invitation invite) {
        LinkedHashSet<Club> clubs = new LinkedHashSet<>(invite.by().getClubs());
        clubs.retainAll(invite.getPerson().getClubs());
        return clubs.stream().map(Club::getProgram).distinct().findFirst().orElseThrow(IllegalStateException::new);
    }

    private String lines(String... lines) {
        StringBuilder builder = new StringBuilder();

        for (String line : lines) {
            builder.append(line);
            builder.append('\n');
        }

        return builder.toString();
    }

    private String getInnerTemplate(Invitation invite) {
        boolean isListener = invite.getPerson().asListener().isPresent();
        boolean isLeader   = invite.getPerson().asClubLeader().isPresent();
        boolean isParent   = hasChildren(invite);
        boolean isClubber  = invite.getPerson().asClubber().isPresent();

        return isLeader   ? getLeaderTemplate() :
               isParent   ? getParentTemplate() :
               isListener ? getListenerTemplate() :
               isClubber  ? getClubberTemplate() :
                            getUnknownUserTypeTemplate();
    }

    private Boolean hasChildren(Invitation invite) {
        return chain(invite.getPerson().asParent(), Person::getFamily)
                        .map(Family::getClubbers)
                        .map(c->!c.isEmpty())
                        .orElse(false);
    }

    private String getUnknownUserTypeTemplate() {
        return "I would like to invite you to join {2} at {1}.";
    }

    private String getParentTemplate() {
        return "I would like to invite you to use a new website that will help us communicate " +
                "about the {1} {2} program and the involvement of your family in it.";
    }
    private String getLeaderTemplate() {
        return "I would like to invite you to use a new website that will help us keep track of " +
                "the {1} {2} program and communicate with leaders and parents.";
    }
    private String getListenerTemplate() {
        return "I would like to invite you to use a new website that will help us keep track of " +
                "the {1} {2} program and communicate parents about the progress of their children.";
    }
    private String getClubberTemplate() {
        return "I would like to invite you to use a new website that will help us keep track of " +
                "your progress in {2} at {1}.";
    }

    private String createInvitationURL(Request request, Person person, Invitation invite) {
        String baseURI = request.url();
        try {
            return new URI(baseURI).resolve(
                            "/invite/" +
                            invite.getAuth()).toASCIIString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private Invitation invite(ClubApplication app, User inviter, Person person) {
        InviteBroker broker = new InviteBroker(app);
        int auth = getUniqueAuth(broker);
        InvitationAdapter invite = new InvitationAdapter(
                Optional.of(person),
                auth,
                Optional.of(inviter),
                Instant.now(),
                Optional.empty()
        );
        broker.persist(invite);
        return invite;
    }

    private int getUniqueAuth(InviteBroker broker) {
        SecureRandom random = new SecureRandom();
        int auth;
        do {
            auth = random.nextInt();
        } while(!broker.find(Integer.toString(auth)).isEmpty());
        return auth;
    }
}
