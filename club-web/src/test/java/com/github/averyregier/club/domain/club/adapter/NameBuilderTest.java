package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.Name;
import org.junit.Test;

import static com.github.averyregier.club.TestUtility.assertEmpty;
import static org.junit.Assert.*;

/**
 * Created by avery on 7/5/15.
 */
public class NameBuilderTest {

    @Test
    public void given() {
        NameBuilder builder = new NameBuilder();
        Name name = builder.given("given").build();
        assertNotNull(name);
        assertEquals("given", name.getGivenName());
        assertEmpty(name.getSurname());
        assertEmpty(name.getFriendlyName());
        assertEmpty(name.getHonorificName());
        assertEmpty(name.getMiddleNames());
        assertEmpty(name.getTitle());

        assertEquals("given", name.getFullName());
        assertChanged(builder);
    }

    @Test
    public void surname() {
        NameBuilder builder = new NameBuilder();
        Name name = builder.surname("surname").build();
        assertNotNull(name);
        assertEquals("surname", name.getSurname());
        assertEmpty(name.getGivenName());
        assertEmpty(name.getFriendlyName());
        assertEmpty(name.getHonorificName());
        assertEmpty(name.getMiddleNames());
        assertEmpty(name.getTitle());

        assertEquals("surname", name.getFullName());
        assertChanged(builder);
    }

    @Test
    public void friendly() {
        NameBuilder builder = new NameBuilder();
        Name name = builder.friendly("friendly").build();
        assertNotNull(name);
        assertEquals("friendly", name.getFriendlyName());
        assertEmpty(name.getGivenName());
        assertEmpty(name.getSurname());
        assertEmpty(name.getHonorificName());
        assertEmpty(name.getMiddleNames());
        assertEmpty(name.getTitle());

        assertEmpty(name.getFullName());
        assertChanged(builder);
    }

    @Test
    public void title() {
        NameBuilder builder = new NameBuilder();
        Name name = builder.title("title").build();
        assertNotNull(name);
        assertEquals("title", name.getTitle().get());
        assertEmpty(name.getGivenName());
        assertEmpty(name.getSurname());
        assertEmpty(name.getHonorificName());
        assertEmpty(name.getMiddleNames());
        assertEmpty(name.getFriendlyName());

        assertEmpty(name.getFullName());
        assertChanged(builder);
    }

    @Test
    public void honorific() {
        NameBuilder builder = new NameBuilder();
        Name name = builder.honorific("honorific").build();
        assertNotNull(name);
        assertEquals("honorific", name.getHonorificName());
        assertEmpty(name.getGivenName());
        assertEmpty(name.getSurname());
        assertEmpty(name.getTitle());
        assertEmpty(name.getMiddleNames());
        assertEmpty(name.getFriendlyName());

        assertEmpty(name.getFullName());
        assertChanged(builder);
    }

    @Test
    public void middle() {
        NameBuilder builder = new NameBuilder();
        Name name = builder.middle("middle").build();
        assertNotNull(name);
        assertEquals("middle", name.getMiddleNames().stream().findFirst().get());
        assertEmpty(name.getGivenName());
        assertEmpty(name.getSurname());
        assertEmpty(name.getTitle());
        assertEmpty(name.getHonorificName());
        assertEmpty(name.getFriendlyName());

        assertEmpty(name.getFullName());
        assertChanged(builder);
    }

    @Test
    public void fullNameIsFirstAndLast() {
        NameBuilder builder = new NameBuilder();
        Name name = builder.given("first").surname("last").build();
        assertEquals("first last", name.getFullName());
        assertChanged(builder);
    }

    @Test
    public void updateName() {
        Name initialName = new NameBuilder()
                .title("Mr")
                .given("first")
                .middle("middle")
                .surname("last")
                .honorific("Jr")
                .friendly("friendly")
                .build();

        Name updated = assertChanged(new NameBuilder(initialName, true)
                .title("Dr")
                .given("first"))
                .build();

        assertEquals("first last", updated.getFullName());
        assertEquals("Dr", updated.getTitle().get());
    }

    @Test(expected = IllegalStateException.class)
    public void wontBuildTwice() {
        NameBuilder nameBuilder = new NameBuilder();
        nameBuilder.build();
        nameBuilder.build();
    }

    @Test
    public void canBeCleared() {
        Name initialName = new NameBuilder()
                .title("Mr")
                .given("first")
                .middle("middle")
                .surname("last")
                .honorific("Jr")
                .friendly("friendly")
                .build();

        Name updated = assertChanged(new NameBuilder(initialName, true)
                .title(null)
                .surname(null)
                .middle(null))
                .build();

        assertEquals("first", updated.getFullName());
        assertFalse(updated.getTitle().isPresent());
        assertEmpty(updated.getMiddleNames());
    }

    @Test
    public void canBeClearedWithEmpty() {
        Name initialName = new NameBuilder()
                .title("Mr")
                .given("first")
                .middle("middle")
                .surname("last")
                .honorific("Jr")
                .friendly("friendly")
                .build();

        Name updated = assertChanged(new NameBuilder(initialName, true)
                .title("")
                .surname("")
                .middle(""))
                .build();

        assertEquals("first", updated.getFullName());
        assertFalse(updated.getTitle().isPresent());
        assertEmpty(updated.getMiddleNames());
    }

    @Test
    public void adoptIfEmpty() {
        Name initialName = new NameBuilder()
                .title("")
                .given("first")
                .middle("")
                .surname("last")
                .honorific("Jr")
                .friendly("")
                .build();

        Name updated = assertChanged(new NameBuilder(initialName, false)
                .title("Mr")
                .given("another")
                .middle("middle")
                .surname("another")
                .honorific("another")
                .friendly("friendly"))
                .build();

        assertWholeName(updated);
    }

    private void assertWholeName(Name updated) {
        assertEquals("first last", updated.getFullName());
        assertEquals("Mr", updated.getTitle().get());
        assertEquals("first", updated.getGivenName());
        assertEquals("middle", updated.getMiddleNames().stream().findFirst().get());
        assertEquals("last", updated.getSurname());
        assertEquals("Jr", updated.getHonorificName());
        assertEquals("friendly", updated.getFriendlyName());
    }

    @Test
    public void adoptIfEmpty2() {
        Name initialName = new NameBuilder()
                .title("Mr")
                .given("")
                .middle("middle")
                .surname("")
                .honorific("")
                .friendly("friendly")
                .build();

        Name updated = assertChanged(new NameBuilder(initialName, false)
                .title("another")
                .given("first")
                .middle("another")
                .surname("last")
                .honorific("Jr")
                .friendly("another"))
                .build();

        assertWholeName(updated);
    }

    private NameBuilder assertChanged(NameBuilder builder) {
        assertTrue(builder.isChanged());
        return builder;
    }

    @Test
    public void noChange() {
        NameBuilder builder = new NameBuilder();
        assertFalse(builder.isChanged());
    }
}