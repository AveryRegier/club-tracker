package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.program.AgeGroup;
import com.github.averyregier.club.domain.utility.InputFieldDesignator;
import com.github.averyregier.club.domain.utility.builder.Later;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by avery on 12/2/14.
 */
public abstract class RegistrationInformationAdapter implements RegistrationInformation {
    @Override
    public Family register(User user) {
        if(user.getFamily().isPresent()) {
            return user.getFamily().get().update(this);
        } else {
            return newFamily(user);
        }
    }

    private Family newFamily(User user) {
        Pattern pattern = Pattern.compile("([a-z]*)(\\d?)");

        Map<InputFieldDesignator, Object> results = validate();

        Later<Family> familyLater = new Later<>();
        LinkedHashSet<Parent> parents = new LinkedHashSet<>();
        LinkedHashSet<Clubber> clubbers = new LinkedHashSet<>();
        for(InputFieldDesignator section: getForm()) {
            Map<String, Object> theResults = (Map<String, Object>) results.get(section);
            if(theResults != null) {
                Matcher matcher = pattern.matcher(section.getShortCode());
                if(matcher.find()) {
                    switch (matcher.group(1)) {
                        case "me":
                            user.setName((Name) theResults.get("name"));
                            user.setFamily(familyLater);
                            parents.add(user);
                            break;
                        case "spouse":
                            User spouse = new User();
                            spouse.setName((Name) theResults.get("name"));
                            spouse.setFamily(familyLater);
                            parents.add(spouse);
                            break;
                        case "child":
                            ClubberAdapter child = new ClubberAdapter() {
                                @Override
                                public Name getName() {
                                    return (Name) theResults.get("childName");
                                }

                                @Override
                                public AgeGroup getCurrentAgeGroup() {
                                    return (AgeGroup) theResults.get("ageGroup");
                                }

                                @Override
                                public Optional<Family> getFamily() {
                                    return Optional.of(familyLater.get());
                                }
                            };
                            getProgram().register(child);
                            clubbers.add(child);
                            break;
                    }
                }
            }
        }

        return familyLater.set(new FamilyAdapter(parents, clubbers));
    }

    abstract ProgramAdapter getProgram();
}
