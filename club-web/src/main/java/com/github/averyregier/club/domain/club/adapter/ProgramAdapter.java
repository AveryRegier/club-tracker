package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.PersonManager;
import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.policy.Policy;
import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.program.Programs;
import com.github.averyregier.club.domain.utility.*;
import com.github.averyregier.club.domain.utility.adapter.InputFieldBuilder;
import com.github.averyregier.club.domain.utility.adapter.InputFieldGroupBuilder;
import com.github.averyregier.club.domain.utility.adapter.StandardInputFields;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.averyregier.club.domain.utility.UtilityMethods.*;

/**
* Created by avery on 9/23/14.
*/
public class ProgramAdapter extends ClubAdapter implements Program {
    private final String acceptLanguage;
    private String organizationName;
    private SortedSet<ClubAdapter> clubs;
    private PersonManager personManager;
    private Map<RegistrationSection, List<InputFieldDesignator>> extraFields = new HashMap<>();

    public ProgramAdapter() {
        this(null,null,(String)null);
    }
    public ProgramAdapter(String acceptLanguage, String organizationName, String curriculum) {
        super(curriculum != null ? Programs.valueOf(curriculum).get() : null);
        this.acceptLanguage = acceptLanguage;
        this.organizationName = organizationName;
    }

    public ProgramAdapter(String acceptLanguage, String organizationname, Optional<Club> club) {
        super(club.map(Club::getCurriculum).orElse(null));
        this.acceptLanguage = acceptLanguage;
        this.organizationName = organizationname;

    }

    public ProgramAdapter(String acceptLanguage, String organizationname, Curriculum curriculum) {
        super(curriculum);
        this.acceptLanguage = acceptLanguage;
        this.organizationName = organizationname;

    }

    @Override
    public Set<Club> getClubs() {
        return Collections.unmodifiableSet(downcast(clubs()));
    }

    private synchronized SortedSet<ClubAdapter> clubs() {
        if(clubs == null) {
            clubs = loadClubs();
        }
        return clubs;
    }

    protected TreeSet<ClubAdapter> loadClubs() {
        return new TreeSet<>();
    }

    @SuppressWarnings("unchecked")
    private <T> SortedSet<T> downcast(SortedSet<? extends T> set) {
        return (SortedSet<T>) set;
    }

    @Override
    public RegistrationInformation createRegistrationForm(Person user) {
        if(user.getFamily().isPresent()) {
            return createRegistrationForm(user.getFamily().get(), user);
        }
        InputFieldGroup me = buildMeFields();
        InputFieldGroup household = buildHouseholdFields();
        InputField action = StandardInputFields.action.createField(getLocale()).build();
        List<InputFieldDesignator> list = Arrays.asList(me, household, action);

        Map<String, String> map = prefix(me.getShortCode(), me.map(user));

        return new ProgramRegistrationInformation(list, map);
    }

    @Override
    public RegistrationInformation createRegistrationForm() {
        InputFieldGroup parent = buildParentFields();
        InputFieldGroup household = buildHouseholdFields();
        InputFieldGroup child1 = buildChildFields(new InputFieldGroupBuilder().id("child1").name("About Child"));

        InputField action = StandardInputFields.action.createField(getLocale()).build();
        List<InputFieldDesignator> list = Arrays.asList(parent, household, child1, action);

        return new ProgramRegistrationInformation(list, Collections.emptyMap());
    }

    private RegistrationInformation createRegistrationForm(Family family, Person user) {
        List<InputFieldDesignator> list = new ArrayList<>();
        InputFieldGroup me = buildMeFields();
        list.add(me);
        Map<String, String> map = prefix(me.getShortCode(), me.map(user));
        InputFieldGroup household = buildHouseholdFields();
        putAll(map, household.getShortCode(), household.map(user));
        list.add(household);

        Parent spouse = getOther(family.getParents(), user.asParent().get()).orElse(null);
        boolean hasSpouse = false;
        if(spouse != null) {
            InputFieldGroup spouseFields = addSpouse(list);
            putAll(map, spouseFields.getShortCode(), spouseFields.map(spouse));
            hasSpouse = true;
        }

        int i=0;
        for(Clubber clubber: family.getClubbers()) {
            InputFieldGroup childFields = addChild(list, ++i);
            putAll(map, childFields.getShortCode(), childFields.map(clubber));
        }

        addActionFields(list, hasSpouse);

        return new ProgramRegistrationInformation(list, map);
    }

    private void addActionFields(List<InputFieldDesignator> list, boolean hasSpouse) {
        if(hasSpouse) {
            list.add(StandardInputFields.action.createField(getLocale()).exclude(Action.spouse.name()).build());
        } else {
            list.add(StandardInputFields.action.createField(getLocale()).build());
        }
    }

    private InputFieldGroup buildMeFields() {
        return buildPersonFields(new InputFieldGroupBuilder().id("me").name("About Myself"));
    }

    private InputFieldGroup buildParentFields() {
        return buildPersonFields(new InputFieldGroupBuilder().id("parent").name("About Parent"));
    }

    private InputFieldGroup buildPersonFields(InputFieldGroupBuilder builder) {
        return completeBuild(builder
                .group(StandardInputFields.name.createGroup(getLocale()))
                .field(StandardInputFields.gender.createField(getLocale()))
                .field(StandardInputFields.email.createField(getLocale())), RegistrationSection.parent);
    }

    private InputFieldGroup buildChildFields(InputFieldGroupBuilder builder) {
        return completeBuild(builder
                .group(StandardInputFields.childName.createGroup(getLocale()))
                .field(StandardInputFields.gender.createField(getLocale()))
                .field(StandardInputFields.email.createField(getLocale()))
                .field(StandardInputFields.ageGroup.createField(getLocale())), RegistrationSection.child);
    }

    private InputFieldGroup buildHouseholdFields() {
        return completeBuild(new InputFieldGroupBuilder()
                .id("household")
                .name("About Your Household")
                .group(StandardInputFields.address.createGroup(getLocale())), RegistrationSection.household);
    }

    private InputFieldGroup completeBuild(InputFieldGroupBuilder builder, RegistrationSection section) {
        addExtraFields(builder, section);
        return builder.build();
    }

    private void addExtraFields(InputFieldGroupBuilder builder, RegistrationSection section) {
        extraFields.getOrDefault(section, Collections.emptyList()).forEach(designator->{
            designator.asField().ifPresent(field-> builder.field(new InputFieldBuilder().copy(field)));
            designator.asGroup().ifPresent(group -> builder.group(new InputFieldGroupBuilder().copy(group)));
        });
    }

    @Override
    public RegistrationInformation updateRegistrationForm(Map<String, String> values) {
        String parentGroupKey;
        InputFieldGroup me;
        if(values.keySet().stream().anyMatch(k->k.startsWith("me."))) {
            me = buildMeFields();
            parentGroupKey = "me";
        } else {
            me = buildParentFields();
            parentGroupKey = "parent";
        }
        Map<String, String> fields = new HashMap<>(values);
        String actionName = fields.remove("action");
        Action action = actionName != null ? Action.valueOf(actionName) : null;
        List<InputFieldDesignator> list = new ArrayList<>();
        list.add(me);
        list.add(buildHouseholdFields());

        boolean hasSpouse = hasSpouse(values);
        if(hasSpouse) {
            addSpouse(list);
        } else if(action == Action.spouse) {
            addSpouse(list);
            hasSpouse = true;
            fields.put("spouse.gender", Person.Gender.lookup(values.get(parentGroupKey+".gender")).map(g -> g.opposite().name()).orElse(null));
            fields.put("spouse.name.surname", values.get(parentGroupKey+".name.surname"));
        }

        int num = getNextChildNumber(values);
        for(int i=1; i<num; i++) {
            addChild(list, i);
        }
        if(action == Action.child) {
            addChild(list, num);
            fields.put("child" + num + ".childName.surname", values.get(parentGroupKey+".name.surname"));
        }

        addActionFields(list, hasSpouse);

        return new ProgramRegistrationInformation(list, fields);
    }

    private InputFieldGroup addChild(List<InputFieldDesignator> list, int i) {
        InputFieldGroup childFields = buildChildFields(new InputFieldGroupBuilder().id("child" + i).name("About My Child"));
        list.add(childFields);
        return childFields;
    }

    private boolean hasSpouse(Map<String, String> values) {
        return values.keySet().stream()
                .anyMatch(k -> k.startsWith("spouse"));

    }

    private InputFieldGroup addSpouse(List<InputFieldDesignator> list) {
        InputFieldGroup spouse = buildPersonFields(new InputFieldGroupBuilder().id("spouse").name("About My Spouse"));
        list.add(spouse);
        return spouse;
    }

    private int getNextChildNumber(Map<String, String> values) {
        return values.keySet().stream()
                .filter(k -> k.startsWith("child"))
                .map(k->Integer.parseInt(k.substring(5,k.indexOf('.'))))
                .max(Comparator.comparingInt(num->num)).orElse(0)+1;
    }

    @Override
    public Locale getLocale() {
        return UtilityMethods.parseLocale(acceptLanguage);
    }

    @Override
    public Club addClub(final Curriculum series) {
        ClubAdapter club = createClub(series);
        clubs().add(club);
        return club;
    }

    protected ClubAdapter createClub(final Curriculum series) {
        return new ClubAdapter(series) {
                @Override
                public Program getProgram() {
                    return ProgramAdapter.this;
                }
            };
    }

    @Override
    public void setName(String organizationName) {
        this.organizationName = organizationName;
    }

    @Override
    public Optional<Club> lookupClub(String shortCode) {
        if(shortCode.equals(this.organizationName)) return Optional.of(this);
        return clubs().stream()
                .filter(c->shortCode.equals(c.getShortCode()))
                .findFirst()
                .map(c->(Club)c);
    }

    @Override
    public void setPersonManager(PersonManager personManager) {
        this.personManager = personManager;
    }

    @Override
    public PersonManager getPersonManager() {
        return personManager;
    }

    @Override
    public Program addField(RegistrationSection section, InputFieldDesignator field) {
        extraFields.computeIfAbsent(section, (s)->new ArrayList<>()).add(field);
        return this;
    }

    @Override
    public Set<Policy> getPolicies() {
        return null;
    }

    @Override
    public Optional<Program> asProgram() {
        return Optional.of(this);
    }

    @Override
    public String getShortCode() {
        return organizationName;
    }

    @Override
    public Optional<ClubGroup> getParentGroup() {
        return Optional.empty();
    }

    @Override
    public Program getProgram() {
        return this;
    }

    @Override
    public Optional<Club> asClub() {
        return Optional.of(this);
    }

    @Override
    public Set<Clubber> getClubbers() {
        return clubs().stream().flatMap(c->c.getClubbers().stream()).collect(Collectors.toSet());
    }

    @Override
    public int compareTo(Club o) {
        return 0;
    }

    void register(ClubberAdapter clubber) {
        clubs().stream()
                .filter(c -> !c.getCurriculum().recommendedBookList(clubber.getCurrentAgeGroup()).isEmpty())
                .forEach(c -> c.addClubber(clubber));
    }

    private class ProgramRegistrationInformation extends RegistrationInformationAdapter {
        private final List<InputFieldDesignator> list;
        private final Map<String, String> map;

        public ProgramRegistrationInformation(List<InputFieldDesignator> list, Map<String, String> map) {
            this.list = list;
            this.map = map;
        }

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

        @Override
        protected ClubberAdapter createClubber() {
            return ProgramAdapter.this.createClubber();
        }

        @Override
        protected void syncFamily(Family family) {
            ProgramAdapter.this.syncFamily(family);
        }
    }

    protected ClubberAdapter createClubber() {
        return new ClubberAdapter(personManager.createPerson());
    }

    protected void syncFamily(Family family) {}
}
