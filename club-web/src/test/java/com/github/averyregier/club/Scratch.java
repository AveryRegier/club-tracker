package com.github.averyregier.club;

import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.UserManager;
import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.club.adapter.ProgramAdapter;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.utility.UtilityMethods;
import org.junit.Before;
import org.junit.Test;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.averyregier.club.TestUtility.anyEqual;

/**
 * Created by avery on 9/6/2014.
 */
public class Scratch {
    private UserManager userManager;
    private String personId;

    @Before
    public void setup() {
        userManager = new UserManager();
        User diane = userManager.createUser("provider", "Diane");
        diane.getUpdater().setGender(Person.Gender.FEMALE);

        ProgramAdapter program = new ProgramAdapter("en_US", null, "AWANA");
        program.setPersonManager(userManager.getPersonManager());
        Club club = program.addClub(program.getCurriculum().getSeries("TnT").get());

        club.recruit(diane);

        User parent = userManager.createUser("provider", "Parent");
        Map<String, String> values = UtilityMethods.map("child1.childName.given", "Betty")
                .put("child1.ageGroup", "THIRD_GRADE")
                .put("child1.gender", "FEMALE")
                .build();
        Family family = program.updateRegistrationForm(values).register(parent);
        Clubber clubber = family.getClubbers().stream().findFirst().get();

        club.getCurriculum()
                .recommendedBookList(clubber.getCurrentAgeGroup()).stream()
                .flatMap(b -> b.getSections().stream())
                .filter(s -> anyEqual(s.getGroup().sequence(), 0, 1, 2))
                .forEach(s -> clubber.getRecord(Optional.of(s)).ifPresent(r -> r.sign(diane.asListener().get(), "")));

        personId = clubber.getId();
    }

    @Test
    public void checkoffList() {
        Optional<User> user = userManager.getUser("provider", "Diane");
        if(user.isPresent() && user.get().asListener().isPresent()) {
            Listener me = user.get().asListener().get();
            {
                // Screen: What are the next sections my clubbers are working on?
                Set<Clubber> quickList = me.getQuickList();
                Map<Clubber, List<ClubberRecord>> clubberNextSectionsMap = quickList.stream()
                        .filter(c -> c.getNextSection().isPresent())
                        .collect(Collectors.toMap(Function.identity(), c -> c.getNextSections(3)));

                PrintStream screen = System.out;
                clubberNextSectionsMap.entrySet().stream().forEach(e -> {
                    screen.print(e.getKey().getName().getFriendlyName());
                    screen.print('\t');
                    screen.print(e.getKey().getId());
                    screen.print('\t');
                    screen.println(e.getValue().stream()
                            .map(ClubberRecord::getSection)
                            .map(Section::getId).collect(Collectors.joining(", ")));
                });

                // Listener scans for 'Betty', section 3.1 (not in code)
                Optional<ClubberRecord> section = clubberNextSectionsMap.entrySet().stream()
                        .filter(e -> e.getKey().getName().getFriendlyName().equalsIgnoreCase("Betty"))
                        .flatMap(e -> e.getValue().stream())
                        .filter(s -> s.getSection().getGroup().sequence() == 3 && s.getSection().sequence() == 1)
                        .findFirst();

                if (section.isPresent()) {
                    section.get().sign(me, "Word Perfect!");
                }
            }
            {
                // in code, we'd be in a new transaction, trying to put together the action from the identities
                // POST /clubbers/{personID}/sections/{sectionID}/sign
                String sectionId = "TnT:UA:1©2010:3:2";
                String note = "Word Perfect!";
                userManager.getPersonManager().lookup(personId)
                        .ifPresent(p -> p.asClubber()
                                .ifPresent(c -> c.getRecord(c.getClub().get().getCurriculum().lookup(sectionId))
                                        .ifPresent(r -> {
                                            Signing signing = r.sign(me, note);
                                            System.out.println(signing.getDate());
                                            signing.getCompletionAwards().forEach(reward->
                                                System.out.println(c.getName().getFriendlyName()+" has earned "+reward));
                                        })));

            }
        }

    }
}
