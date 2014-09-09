package com.github.averyregier.club;

import com.github.averyregier.club.domain.PersonManager;
import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.Clubber;
import com.github.averyregier.club.domain.club.ClubberRecord;
import com.github.averyregier.club.domain.club.Listener;
import com.github.averyregier.club.domain.club.Signing;
import com.github.averyregier.club.domain.program.Section;
import org.junit.Test;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by rx39789 on 9/6/2014.
 */
public class Scratch {
    @Test
    public void checkoffList() {
        User user = new User();
        if(user.asListener().isPresent()) {
            Listener me = user.asListener().get();
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
                String personId = "1234-5678";
                String sectionId = "A-TnT-3b-3-1";
                String note = "Word Perfect!";
                new PersonManager().lookup(personId)
                        .ifPresent(p -> p.asClubber()
                                .ifPresent(c -> c.getRecord(c.getClub().get().getCurriculum().lookup(sectionId))
                                        .ifPresent(r -> {
                                            Signing signing = r.sign(me, note);
                                            System.out.println(signing.getDate());
                                            signing.getCompletionRewards().forEach(reward->
                                                System.out.println(c.getName().getFriendlyName()+" has earned "+reward));
                                        })));

            }
        }

    }
}
