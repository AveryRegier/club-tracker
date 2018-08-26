package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.PersonManager;
import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.program.Programs;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.utility.*;
import com.github.averyregier.club.domain.utility.adapter.InputFieldGroupBuilder;
import com.github.averyregier.club.domain.utility.adapter.StandardInputFields;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
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
    private Supplier<Map<RegistrationSection, InputFieldGroup>> extraFields;
    private Map<String, ClubYear> clubYears;

    public ProgramAdapter() {
        this(null,null,(String)null);
    }
    public ProgramAdapter(String acceptLanguage, String organizationName, String curriculum) {
        super(curriculum != null ? Programs.valueOf(curriculum).get() : null);
        this.acceptLanguage = acceptLanguage;
        this.organizationName = organizationName;
        this.extraFields = once(HashMap::new);
    }

    public ProgramAdapter(String acceptLanguage, String organizationname, Optional<Club> club) {
        super(club.map(Club::getCurriculum).orElse(null));
        this.acceptLanguage = acceptLanguage;
        this.organizationName = organizationname;
        this.extraFields = once(HashMap::new);
    }

    public ProgramAdapter(String acceptLanguage, String organizationname, Curriculum curriculum,
                          Supplier<Map<RegistrationSection, InputFieldGroup>> registrationForm)
    {
        super(curriculum);
        this.acceptLanguage = acceptLanguage;
        this.organizationName = organizationname;
        this.extraFields = once(registrationForm);
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
        InputField spouseAction = buildSpouseAction();
        InputFieldGroup household = buildHouseholdFields();
        InputField action = buildChildAction();
        List<InputFieldDesignator> list = Arrays.asList(me, spouseAction, household, action);

        Map<String, String> map = prefix(me.getShortCode(), me.map(user));

        return new ProgramRegistrationInformation(list, map);
    }

    @Override
    public RegistrationInformation createRegistrationForm() {
        InputFieldGroup parent = buildParentFields();
        InputField spouseAction = buildSpouseAction();
        InputFieldGroup household = buildHouseholdFields();

        InputField childAction = buildChildAction();
        List<InputFieldDesignator> list = Arrays.asList(parent, spouseAction, household, childAction);

        return new ProgramRegistrationInformation(list, Collections.emptyMap());
    }

    private RegistrationInformation createRegistrationForm(Family family, Person user) {
        List<InputFieldDesignator> list = new ArrayList<>();
        InputFieldGroup me = buildMeFields();
        list.add(me);
        Map<String, String> map = prefix(me.getShortCode(), me.map(user));

        Parent spouse = getOther(family.getParents(), user.asParent().get()).orElse(null);
        if(spouse != null) {
            InputFieldGroup spouseFields = addSpouse(list);
            putAll(map, spouseFields.getShortCode(), spouseFields.map(spouse));
        } else {
            addSpouseAction(list);
        }

        InputFieldGroup household = buildHouseholdFields();
        putAll(map, household.getShortCode(), household.map(user));
        list.add(household);

        int i=0;
        for(Clubber clubber: family.getClubbers()) {
            InputFieldGroup childFields = addChild(list, ++i);
            putAll(map, childFields.getShortCode(), childFields.map(clubber));
        }

        addActionFields(list, true);

        return new ProgramRegistrationInformation(list, map);
    }

    private void addActionFields(List<InputFieldDesignator> list, boolean hasSpouse) {
        if(hasSpouse) {
            list.add(buildChildAction());
        } else {
            list.add(StandardInputFields.action.createField(getLocale()).build());
        }
    }

    private InputField buildChildAction() {
        return StandardInputFields.action.createField(getLocale()).exclude(Action.spouse.name()).build();
    }

    public void addSpouseAction(List<InputFieldDesignator> list) {
        list.add(buildSpouseAction());
    }

    public InputField buildSpouseAction() {
        return StandardInputFields.action.createField(getLocale()).exclude(Action.child.name()).build();
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
        Map<RegistrationSection, InputFieldGroup> fields = extraFields.get();
        if(fields.containsKey(section)) {
            fields.get(section).getFieldDesignations().forEach(designator -> {
                builder.add(designator.copy());
            });
        }
    }

    @Override
    public RegistrationInformation updateRegistrationForm(Map<String, String> values) {
        boolean hasSpouse = hasSpouse(values);
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
        if(hasSpouse) {
            addSpouse(list);
        } else if(action == Action.spouse) {
            addSpouse(list);
            fields.put("spouse.gender", Person.Gender.lookup(values.get(parentGroupKey+".gender")).map(g -> g.opposite().name()).orElse(null));
            fields.put("spouse.name.surname", values.get(parentGroupKey+".name.surname"));
        } else {
            addSpouseAction(list);
        }

        list.add(buildHouseholdFields());


        int num = getNextChildNumber(values);
        for(int i=1; i<num; i++) {
            addChild(list, i);
        }
        if(action == Action.child) {
            addChild(list, num);
            fields.put("child" + num + ".childName.surname", values.get(parentGroupKey+".name.surname"));
        }

        addActionFields(list, true);

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
    public Program addField(RegistrationSection section, InputFieldDesignator designator) {
        extraFields.get().compute(section, (k, old) -> {
            if(!alreadyPresent(designator, old)) {
                InputFieldGroupBuilder builder = new InputFieldGroupBuilder();
                if (old != null) {
                    builder.copy(old);
                } else {
                    builder
                            .name(section.name())
                            .id(UUID.randomUUID().toString());
                }
                return persist(builder.add(designator.copy()).build());
            } else {
                return old;
            }
        });
        return this;
    }

    private boolean alreadyPresent(InputFieldDesignator designator, InputFieldGroup old) {
        return old != null &&
                old.getFieldDesignations().stream()
                 .anyMatch(d->d.getName().equals(designator.getName()));
    }

    public Optional<InputField> findField(String fieldId) {
        return extraFields.get().values().stream()
                .map(v->v.findField(fieldId))
                .filter(Optional::isPresent)
                .findFirst()
                .orElse(Optional.empty());
    }

    @Override
    public ZoneId getTimeZone() {
        return UtilityMethods.getDefaultZone();
    }

    protected InputFieldGroup persist(InputFieldGroup group) {
        return group;
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
        return clubs().stream()
                .flatMap(c->c.getClubbers().stream())
                .collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public void setMeetings(String clubYear, List<LocalDate> dates) {
        ensureMeetingsLoaded();
        ClubYear result = this.clubYears.compute(clubYear, (cy, old) -> new ClubYearAdapter(this, clubYear, dates));
        persist(result);
        for (Club club: getClubs()) {
            for(Curriculum curriculum: club.getScheduledCurriculum()) {
                List<Section> scheduledSections = curriculum.getScheduledSections();
                Schedule<Club, Section> schedule = Schedule.generate(club, dates, scheduledSections);
                TeachingPlan teachingPlan = new TeachingPlanAdapter(curriculum, schedule, result);
                club.setSchedule(teachingPlan);
            }
        }
    }

    protected void persist(ClubYear clubYear) {
    }

    @Override
    public List<ClubMeeting> getMeetings() {
        ensureMeetingsLoaded();
        return this.clubYears.values().stream()
                .flatMap(v->v.getSchedule().getList().stream().map(Scheduled::getEvent))
                .collect(Collectors.toList());
    }

    private void ensureMeetingsLoaded() {
        if(clubYears == null) {
            clubYears = loadClubYears();
        }
    }

    protected ConcurrentHashMap<String, ClubYear> loadClubYears() {
        return new ConcurrentHashMap<>();
    }

    @Override
    public int compareTo(Club o) {
        return 0;
    }

    void register(ClubberAdapter clubber) {
        clubs().stream()
                .filter(c -> c.accepts(clubber))
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
