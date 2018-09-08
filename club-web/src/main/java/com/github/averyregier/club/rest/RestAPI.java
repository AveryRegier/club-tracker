package com.github.averyregier.club.rest;

import com.github.averyregier.club.application.ClubApplication;
import com.github.averyregier.club.domain.PersonManager;
import com.github.averyregier.club.domain.club.Family;
import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.club.Program;
import com.github.averyregier.club.domain.club.RegistrationInformation;
import com.github.averyregier.club.domain.utility.HasId;
import com.github.averyregier.club.domain.utility.InputField;
import com.github.averyregier.club.domain.utility.InputFieldDesignator;
import com.github.averyregier.club.view.BaseController;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static spark.Spark.*;

/**
 * Created by avery on 8/30/14.
 */
public class RestAPI extends BaseController {
    public void init(ClubApplication app) {
        get("/protected/hello", (request, response) -> {
            String name = getUser(request).getName().getFullName();
            return "Hello " + name + "!  You need to be invited to use this application.";
        });


        before("/protected/program/:id/export", ((request, response) -> {
            if(!getUser(request).asClubLeader()
                    .filter(l->l.getProgram().getId().equals(request.params(":id")))
                    .isPresent()) {
                halt();
            }
        }));

        get("/protected/program/:id/export", ((request, response) -> {
            response.type("text/csv");
            String programId = request.params(":id");
            Program program = app.getProgram(programId);

            String header = createHeader(program);

            PersonManager personManager = app.getPersonManager();

            return header + "\n" +
                    getAllFamilies(program, personManager)
                            .map(this::toCSV)
                            .collect(joining("\n"));
        }));
    }

    public Stream<RegistrationInformation> getAllFamilies(Program program, PersonManager personManager) {
        return personManager.getPeople().stream()
                .map(Person::getFamily)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Family::getId)
                .map(personManager::getParent)
                .distinct()
                .map(program::createRegistrationForm);
    }

    public String createHeader(Program program) {
        // TODO: this form needs a spouse and a bunch of kids
//        program.updateRegistrationForm()
        return program.createRegistrationForm()
                .getLeaves().stream()
                .filter(notAction())
                .map(InputFieldDesignator::getName)
                .collect(joining(","));
    }

    private String toCSV(RegistrationInformation form) {

        // TODO: ensure a spouse is added
        return form.getLeaves().stream()
                .filter(notAction())
                .map(HasId::getId)
                .map(id->form.getFields().get(id))
                .map(this::nullToEmpty)
                .collect(joining(","));
    }

    private Predicate<InputField> notAction() {
        return l->l.getType() != InputField.Type.action;
    }

    private String nullToEmpty(String s) {
        if(s == null) return "";
        if(s.contains(" ")) {
            return '"'+s+'"';
        }
        return s;
    }
}
