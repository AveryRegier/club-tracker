package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.policy.Policy;
import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.program.Programs;
import com.github.averyregier.club.domain.utility.*;
import com.github.averyregier.club.domain.utility.adapter.InputFieldGroupBuilder;
import com.github.averyregier.club.domain.utility.adapter.StandardInputFields;

import java.util.*;
import java.util.stream.Collectors;

/**
* Created by avery on 9/23/14.
*/
public class ProgramAdapter implements Program {
    private final String acceptLanguage;
    private String organizationName;
    private final String curriculum;
    private SortedSet<ClubAdapter> clubs = new TreeSet<>();

    public ProgramAdapter(String acceptLanguage, String organizationName, String curriculum) {
        this.acceptLanguage = acceptLanguage;
        this.organizationName = organizationName;
        this.curriculum = curriculum;
    }

    @Override
    public Set<Club> getClubs() {
        return Collections.unmodifiableSet(downcast(clubs));
    }

    @SuppressWarnings("unchecked")
    private <T> SortedSet<T> downcast(SortedSet<? extends T> set) {
        return (SortedSet<T>) set;
    }

    @Override
    public RegistrationInformation createRegistrationForm(User user) {
        InputFieldGroup me = buildMeFields();
        InputField action = StandardInputFields.action.createField(getLocale()).build();
        List<InputFieldDesignator> list = Arrays.asList(me, action);

        Map<String, String> map = UtilityMethods.prefix(me.getShortCode(), me.map(user));

        return new RegistrationInformationAdapter() {
            @Override
            public List<InputFieldDesignator> getForm() {
                return list;
            }

            @Override
            public Map<String, String> getFields() {
                return map;
            }

            @Override
            ProgramAdapter getProgram() {
                return ProgramAdapter.this;
            }
        };
    }

    private InputFieldGroup buildMeFields() {
        return buildPersonFields(new InputFieldGroupBuilder().id("me").name("About Myself"));
    }

    private InputFieldGroup buildPersonFields(InputFieldGroupBuilder builder) {
        return builder
                .group(StandardInputFields.name.createGroup(getLocale()))
                .field(StandardInputFields.gender.createField(getLocale()))
                .field(StandardInputFields.email.createField(getLocale()))
                .build();
    }
    private InputFieldGroup buildChildFields(InputFieldGroupBuilder builder) {
        return builder
                .group(StandardInputFields.childName.createGroup(getLocale()))
                .field(StandardInputFields.gender.createField(getLocale()))
                .field(StandardInputFields.email.createField(getLocale()))
                .field(StandardInputFields.ageGroup.createField(getLocale()))
                .build();
    }

    @Override
    public RegistrationInformation updateRegistrationForm(Map<String, String> values) {
        InputFieldGroup me = buildMeFields();
        Map<String, String> fields = new HashMap<>(values);
        String actionName = fields.remove("action");
        Action action = actionName != null ? Action.valueOf(actionName) : null;
        List<InputFieldDesignator> list = new ArrayList<>();
        list.add(me);

        boolean hasSpouse = hasSpouse(values);
        if(hasSpouse) {
            addSpouse(action, list);
        } else if(action == Action.spouse) {
            addSpouse(action, list);
            hasSpouse = true;
            fields.put("spouse.gender", Person.Gender.lookup(values.get("me.gender")).map(g -> g.opposite().name()).orElse(null));
            fields.put("spouse.name.surname", values.get("me.name.surname"));
        }

        int num = getNextChildNumber(values);
        for(int i=1; i<num; i++) {
            addChild(list, i);
        }
        if(action == Action.child) {
            addChild(list, num);
            fields.put("child" + num + ".childName.surname", values.get("me.name.surname"));
        }

        if(hasSpouse) {
            list.add(StandardInputFields.action.createField(getLocale()).exclude(Action.spouse.name()).build());
        } else {
            list.add(StandardInputFields.action.createField(getLocale()).build());
        }

        return new RegistrationInformationAdapter() {
            @Override
            public List<InputFieldDesignator> getForm() {
                return list;
            }

            @Override
            public Map<String, String> getFields() {
                return fields;
            }

            @Override
            ProgramAdapter getProgram() {
                return ProgramAdapter.this;
            }
        };
    }

    private void addChild(List<InputFieldDesignator> list, int i) {
        list.add(buildChildFields(new InputFieldGroupBuilder().id("child" + i).name("About My Child")));
    }

    private boolean hasSpouse(Map<String, String> values) {
        return values.keySet().stream()
                .anyMatch(k -> k.startsWith("spouse"));

    }

    private void addSpouse(Action action, List<InputFieldDesignator> list) {
        list.add(buildPersonFields(new InputFieldGroupBuilder().id("spouse").name("About My Spouse")));
    }

    private int getNextChildNumber(Map<String, String> values) {
        return values.keySet().stream()
                .filter(k -> k.startsWith("child"))
                .map(k->Integer.parseInt(k.substring(5,k.indexOf('.'))))
                .max(Comparator.comparingInt(num->num)).orElse(0)+1;
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
        return clubs.stream().flatMap(c->c.getClubbers().stream()).collect(Collectors.toSet());
    }

    @Override
    public int compareTo(Club o) {
        return 0;
    }

    void register(ClubberAdapter clubber) {
        clubs.forEach(c -> c.addClubber(clubber));
    }
}
