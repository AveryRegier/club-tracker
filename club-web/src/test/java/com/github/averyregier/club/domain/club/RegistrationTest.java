package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.PersonManager;
import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.domain.club.adapter.ProgramAdapter;
import com.github.averyregier.club.domain.program.AgeGroup;
import com.github.averyregier.club.domain.program.awana.TnTCurriculum;
import com.github.averyregier.club.domain.utility.UtilityMethods;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static com.github.averyregier.club.domain.utility.UtilityMethods.asLinkedSet;
import static com.github.averyregier.club.domain.utility.UtilityMethods.map;
import static org.junit.Assert.*;

/**
 * Created by avery on 11/21/14.
 */
public class RegistrationTest {
    private Program program;

    @Before
    public void setup() {
        program = new ProgramAdapter("en_US", "Mock Org", "AWANA");
        program.addClub(TnTCurriculum.get());
        program.setPersonManager(new PersonManager());
    }

    @Test
    public void selfRegistration() {
        User me = new User();
        Map<String, String> formValues =
                 map("me.name.given", "Green")
                .put("me.name.surname", "Flubber")
                .build();
        RegistrationInformation form = program.updateRegistrationForm(formValues);
        Family family = form.register(me);
        assertNotNull(family);
        assertEquals(asLinkedSet(me.asParent().get()), family.getParents());
        assertEquals("Green", me.getName().getGivenName());
        assertEquals("Flubber", me.getName().getSurname());
        assertSame(family, me.getFamily().get());
    }

    @Test
    public void spouseRegistration() {
        User me = new User();
        Map<String, String> formValues =
                map("me.name.given", "Green")
               .put("me.name.surname", "Flubber")
               .put("spouse.name.given", "Gooey")
               .put("spouse.name.surname", "Flubber-Goo")
               .build();
        RegistrationInformation form = program.updateRegistrationForm(formValues);
        Family family = form.register(me);
        assertNotNull(family);
        Set<Parent> parents = family.getParents();
        assertContains(me.asParent().get(), parents);
        assertEquals("Green", me.getName().getGivenName());
        assertEquals("Flubber", me.getName().getSurname());
        assertSame(family, me.getFamily().get());

        assertEquals(2, parents.size());
        Parent spouse = UtilityMethods.getOther(parents, me.asParent().get()).orElse(null);

        assertEquals("Gooey", spouse.getName().getGivenName());
        assertEquals("Flubber-Goo", spouse.getName().getSurname());
        assertSame(family, spouse.getFamily().get());
    }

    @Test
    public void childRegistration() {
        User me = new User();
        Map<String, String> formValues =
                 map("me.name.given", "Green")
                .put("me.name.surname", "Flubber")
                .put("child1.childName.given", "Johny")
                .put("child1.childName.surname", "Flubber")
                .put("child1.ageGroup","THIRD_GRADE")
                .build();
        RegistrationInformation form = program.updateRegistrationForm(formValues);
        Family family = form.register(me);
        assertNotNull(family);
        assertEquals(asLinkedSet(me.asParent().get()), family.getParents());
        assertEquals("Green", me.getName().getGivenName());
        assertEquals("Flubber", me.getName().getSurname());

        Set<Clubber> clubbers = family.getClubbers();
        assertEquals(1, clubbers.size());
        Clubber clubber = clubbers.stream().findAny().get();

        assertEquals("Johny", clubber.getName().getGivenName());
        assertEquals("Flubber", clubber.getName().getSurname());
        assertEquals(AgeGroup.DefaultAgeGroup.THIRD_GRADE, clubber.getCurrentAgeGroup());

        assertSame(family, clubber.getFamily().get());

        assertTrue(program.getClubbers().contains(clubber));
        assertTrue(clubber.getClub().isPresent());
        Club clubbersClub = clubber.getClub().get();
        assertTrue(program.getClubs().contains(clubbersClub));
        assertEquals("TnT", clubbersClub.getShortName());
//        assertTrue(clubber.getNextSection().isPresent());
//        Section clubberSection = clubber.getNextSection().get();
//        assertEquals(1, clubberSection.sequence());
    }

    @Test
    public void wholeFamilyRegistrationTest() {
        wholeFamilyRegistration();
    }

    public User wholeFamilyRegistration() {
        User me = new User(program.getPersonManager().createPerson());
        Map<String, String> formValues =
                 map("me.name.given", "Green")
                .put("me.name.surname", "Flubber")
                .put("spouse.name.given", "Gooey")
                .put("spouse.name.surname", "Flubber-Goo")
                .put("child1.childName.given", "Johny")
                .put("child1.childName.surname", "Flubber")
                .put("child2.childName.given", "Betsy")
                .put("child2.childName.surname", "Flubber")
                .put("child3.childName.given", "Edna")
                .put("child3.childName.surname", "Flubber")
                .build();
        RegistrationInformation form = program.updateRegistrationForm(formValues);
        Family family = form.register(me);
        assertNotNull(family);
        Set<Parent> parents = family.getParents();
        assertContains(me.asParent().get(), parents);
        assertEquals("Green", me.getName().getGivenName());
        assertEquals("Flubber", me.getName().getSurname());

        assertEquals(2, parents.size());
        Parent spouse = UtilityMethods.getOther(parents, me.asParent().get()).orElse(null);

        assertEquals("Gooey", spouse.getName().getGivenName());
        assertEquals("Flubber-Goo", spouse.getName().getSurname());

        Set<Clubber> clubbers = family.getClubbers();
        assertEquals(3, clubbers.size());
        Iterator<Clubber> iterator = clubbers.iterator();
        Clubber clubber = iterator.next();
        assertEquals("Johny", clubber.getName().getGivenName());
        assertEquals("Flubber", clubber.getName().getSurname());
        clubber = iterator.next();
        assertEquals("Betsy", clubber.getName().getGivenName());
        assertEquals("Flubber", clubber.getName().getSurname());
        clubber = iterator.next();
        assertEquals("Edna", clubber.getName().getGivenName());
        assertEquals("Flubber", clubber.getName().getSurname());

        Collection<Person> people = program.getPersonManager().getPeople();
        assertTrue(allPeoplePresent(people, family.getClubbers()));
        assertTrue(allPeoplePresent(people, family.getParents()));

        return me;
    }

    private boolean allPeoplePresent(Collection<Person> people, Set<? extends Person> persons) {
        return persons.stream().map(p -> p.getUpdater()).allMatch(p -> people.contains(p));
    }

    public Program getProgram() {
        return program;
    }

    private <T> void assertContains(T item, Set<T> set) {
        assertTrue(set.contains(item));
    }
}
