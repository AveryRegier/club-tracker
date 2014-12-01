package com.github.averyregier.club.domain;

import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.club.adapter.ClubberAdapter;
import com.github.averyregier.club.domain.club.adapter.FamilyAdapter;
import com.github.averyregier.club.domain.utility.InputFieldDesignator;
import com.github.averyregier.club.view.UserBean;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by avery on 9/2/14.
 */
public class User implements Person, Parent {
    private String auth;
    private String id;
    private Name name;
    private Gender gender;
    private String email;

    public String resetAuth() {
        byte[] bytes = new byte[10];
        new Random(System.currentTimeMillis()).nextBytes(bytes);
        auth = new String(bytes);
        try {
            auth = URLEncoder.encode(auth, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return auth;
    }

    public boolean authenticate(String auth) {
        return auth != null && auth.equals(this.auth);
    }

    @Override
    public String getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    @Override
    public Optional<Gender> getGender() {
        return Optional.ofNullable(gender);
    }

    @Override
    public Optional<User> getLogin() {
        return Optional.of(this);
    }

    @Override
    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }

    @Override
    public Optional<Parent> asParent() {
        return Optional.of(this);
    }

    @Override
    public Optional<Listener> asListener() {
        return Optional.empty();
    }

    @Override
    public Optional<Clubber> asClubber() {
        return Optional.empty();
    }

    @Override
    public Optional<ClubLeader> asClubLeader() {
        return Optional.empty();
    }

    @Override
    public Family register(RegistrationInformation information) {
        if(getFamily().isPresent()) {
            return getFamily().get().update(information);
        } else {
            Pattern pattern = Pattern.compile("([a-z]*)(\\d?)");

            Map<InputFieldDesignator, Object> results = information.validate();

            LinkedHashSet<Parent> parents = new LinkedHashSet<>();
            LinkedHashSet<Clubber> clubbers = new LinkedHashSet<>();
            for(InputFieldDesignator section: information.getForm()) {
                Map<String, Object> theResults = (Map<String, Object>) results.get(section);
                if(theResults != null) {
                    Matcher matcher = pattern.matcher(section.getShortCode());
                    if(matcher.find()) {
                        switch (matcher.group(1)) {
                            case "me":
                                this.name = (Name) theResults.get("name");
                                parents.add(this);
                                break;
                            case "spouse":
                                User spouse = new User();
                                spouse.name = (Name) theResults.get("name");
                                parents.add(spouse);
                                break;
                            case "child":
                                Clubber child = new ClubberAdapter() {
                                    @Override
                                    public Name getName() {
                                        return (Name) theResults.get("childName");
                                    }
                                };
                                clubbers.add(child);
                                break;
                        }
                    }
                }
            }

            return new FamilyAdapter(parents, clubbers);
        }
    }

    @Override
    public Optional<Family> getFamily() {
        return Optional.empty();
    }

    public void setName(Object first, Object last) {
        this.name = new Name() {
            @Override
            public String getGivenName() {
                return first != null ? first.toString() : null;
            }

            @Override
            public String getSurname() {
                return last != null ? last.toString() : null;
            }

            @Override
            public List<String> getMiddleNames() {
                return null;
            }

            @Override
            public Optional<String> getTitle() {
                return null;
            }

            @Override
            public String getFriendlyName() {
                return null;
            }

            @Override
            public String getHonorificName() {
                return null;
            }

            @Override
            public String getFullName() {
                return combineName(first, last);
            }
        };
    }

    public void update(final UserBean user) {
        this.id = user.getUniqueId();

        setName(user.getFirstName(), user.getLastName());

        String gender1 = user.getGender();
        if(gender1 != null) {
            this.gender = Gender.valueOf(gender1.toUpperCase());
        }
        this.email = user.getEmail();
    }

    private String combineName(Object first, Object last) {
        return ((first != null ? first : "") +" "+ (last != null ? last : "")).trim();
    }

}
