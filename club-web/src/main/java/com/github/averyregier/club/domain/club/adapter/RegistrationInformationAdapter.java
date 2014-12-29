package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.program.AgeGroup;
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

        FamilyAdapter family = (FamilyAdapter) user.getFamily().orElse(new FamilyAdapter(thisParent));

        for(InputFieldDesignator section: getForm()) {
            Map<String, Object> theResults = (Map<String, Object>) results.get(section);
            if(theResults != null) {
                Matcher matcher = pattern.matcher(section.getShortCode());
                if(matcher.find()) {
                    switch (matcher.group(1)) {
                        case "me":
                            user.getUpdater().setName((Name) theResults.get("name"));
                            thisParent.getUpdater().setFamily(family);
                            break;
                        case "spouse":
                            Name name = (Name) theResults.get("name");
                            ParentAdapter spouse = (ParentAdapter)getOther(family.getParents(), thisParent)
                                    .orElse(new ParentAdapter(new PersonAdapter()));
                            family.addParent(spouse);
                            spouse.getUpdater().setName(name);
                            spouse.getUpdater().setFamily(family);
                            break;
                        case "child":
                            int childNumber = Integer.parseInt(matcher.group(2));
                            Name childName = (Name) theResults.get("childName");
                            Optional<Clubber> clubber = family.getClubbers().stream().skip(childNumber - 1).findFirst();
                            if(clubber.isPresent()) {
                                clubber.get().getUpdater().setName(childName);
                            } else {
                                ClubberAdapter child = newClubber(family, theResults);
                            }
                            break;
                    }
                }
            }
        }

        return family;
    }

    private ClubberAdapter newClubber(final FamilyAdapter family, final Map<String, Object> theResults) {
        Name childName = (Name) theResults.get("childName");
        AgeGroup ageGroup = (AgeGroup) theResults.get("ageGroup");

        ClubberAdapter child = new ClubberAdapter();

        child.getUpdater().setName(childName);
        child.getUpdater().setFamily(family);
        child.getUpdater().setAgeGroup(ageGroup);
        family.addClubber(child);
        getProgram().register(child);
        return child;
    }

    abstract ProgramAdapter getProgram();

}
