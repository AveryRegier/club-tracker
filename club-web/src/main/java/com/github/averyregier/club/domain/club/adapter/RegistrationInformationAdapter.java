package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.Clubber;
import com.github.averyregier.club.domain.club.Family;
import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.club.RegistrationInformation;
import com.github.averyregier.club.domain.utility.InputFieldDesignator;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.averyregier.club.domain.utility.UtilityMethods.getOther;

/**
 * Created by avery on 12/2/14.
 */
public abstract class RegistrationInformationAdapter implements RegistrationInformation {
    @Override
    public Family register(User user) {
        ParentAdapter thisParent = (ParentAdapter)user.asParent().orElse(new ParentAdapter(user));

        Pattern pattern = Pattern.compile("([a-z]*)(\\d?)");

        Map<InputFieldDesignator, Object> results = validate();

        FamilyAdapter family = (FamilyAdapter) user.getFamily().orElseGet(() -> new FamilyAdapter(thisParent));

        for(InputFieldDesignator section: getForm()) {
            Map<String, Object> theResults = (Map<String, Object>) results.get(section);
            if(theResults != null) {
                Matcher matcher = pattern.matcher(section.getShortCode());
                if(matcher.find()) {
                    switch (matcher.group(1)) {
                        case "me":
                            section.update(thisParent, theResults);
                            thisParent.getUpdater().setFamily(family);
                            break;
                        case "spouse":
                            ParentAdapter spouse = (ParentAdapter)getOther(family.getParents(), thisParent)
                                    .orElseGet(() -> new ParentAdapter(createPerson()));
                            family.addParent(spouse);
                            section.update(spouse, theResults);
                            spouse.getUpdater().setFamily(family);
                            break;
                        case "child":
                            int childNumber = Integer.parseInt(matcher.group(2));
                            Clubber child = findClubber(family, childNumber);
                            section.update(child, theResults);
                            getProgram().register((ClubberAdapter)child);
                            break;
                    }
                }
            }
        }

        return family;
    }

    private Person createPerson() {
        return getProgram().getPersonManager().createPerson();
    }

    private Clubber findClubber(FamilyAdapter family, int childNumber) {
        Optional<Clubber> clubber = family.getClubbers().stream().skip(childNumber - 1).findFirst();
        Clubber child;
        if(clubber.isPresent()) {
            child = clubber.get();
        } else {
            child = new ClubberAdapter(createPerson());
            family.addClubber(child);
            child.getUpdater().setFamily(family);
        }
        return child;
    }

    abstract ProgramAdapter getProgram();

}
