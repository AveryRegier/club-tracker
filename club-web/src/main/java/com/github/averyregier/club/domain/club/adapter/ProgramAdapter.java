package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.policy.Policy;
import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.program.Programs;
import com.github.averyregier.club.domain.utility.InputField;
import com.github.averyregier.club.domain.utility.InputFieldDesignator;
import com.github.averyregier.club.domain.utility.InputFieldGroup;
import com.github.averyregier.club.domain.utility.UtilityMethods;
import com.github.averyregier.club.domain.utility.adapter.InputFieldGroupBuilder;
import com.github.averyregier.club.domain.utility.adapter.StandardInputFields;

import java.util.*;

/**
* Created by avery on 9/23/14.
*/
public class ProgramAdapter implements Program {
    private final String acceptLanguage;
    private String organizationName;
    private final String curriculum;
    private SortedSet<Club> clubs = new TreeSet<>();

    public ProgramAdapter(String acceptLanguage, String organizationName, String curriculum) {
        this.acceptLanguage = acceptLanguage;
        this.organizationName = organizationName;
        this.curriculum = curriculum;
    }

    @Override
    public Set<Club> getClubs() {
        return Collections.unmodifiableSortedSet(clubs);
    }

    @Override
    public RegistrationInformation createRegistrationForm(User user) {
        InputFieldGroup me = new InputFieldGroupBuilder().id("me").name("About Myself")
                .group(StandardInputFields.name.createGroup(getLocale()))
                .field(StandardInputFields.gender.createField(getLocale()))
                .field(StandardInputFields.email.createField(getLocale()))
                .build();
        InputField action = StandardInputFields.action.createField(getLocale()).build();
        List<InputFieldDesignator> list = Arrays.asList(me, action);

        Map<String, String> map = UtilityMethods.prefix(me.getShortCode(), me.map(user));

//        Map<InputField, ? extends Object> map = me.getFields().stream().collect(Collectors.toMap(f -> f, f -> f.map(user)));

        return new RegistrationInformation() {
            @Override
            public List<InputFieldDesignator> getForm() {
                return list;
            }

            @Override
            public Map<String, String> getFields() {
                return map;
            }
        };
    }

    @Override
    public Locale getLocale() {
        return Locale.forLanguageTag(acceptLanguage);
    }

    @Override
    public void addClub(final Curriculum series) {
        clubs.add(new ClubAdapter(this, series));
    }

    @Override
    public void setName(String organizationName) {
        this.organizationName = organizationName;
    }

    @Override
    public Set<Policy> getPolicies() {
        return null;
    }

    @Override
    public Optional<Program> asProgram() {
        return null;
    }

    @Override
    public String getShortName() {
        return organizationName;
    }

    @Override
    public ClubLeader assign(Person person, ClubLeader.LeadershipRole role) {
        return null;
    }

    @Override
    public Curriculum getCurriculum() {
        return Programs.valueOf(curriculum).get();
    }

    @Override
    public Set<Listener> getListeners() {
        return null;
    }

    @Override
    public Listener recruit(Person person) {
        return null;
    }

    @Override
    public Optional<ClubGroup> getParentGroup() {
        return null;
    }

    @Override
    public Program getProgram() {
        return null;
    }

    @Override
    public Optional<Club> asClub() {
        return null;
    }

    @Override
    public Set<Clubber> getClubbers() {
        return null;
    }

    @Override
    public int compareTo(Club o) {
        return 0;
    }
}
