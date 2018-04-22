package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.AwardRecord;
import com.github.averyregier.club.domain.PersonManager;
import com.github.averyregier.club.domain.club.*;
import com.github.averyregier.club.domain.club.adapter.CeremonyAdapter;
import com.github.averyregier.club.domain.club.adapter.ClubberAdapter;
import com.github.averyregier.club.domain.club.adapter.PersonAdapter;
import com.github.averyregier.club.domain.club.adapter.ProgramAdapter;
import com.github.averyregier.club.domain.program.Award;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.program.adapter.CurriculumBuilder;
import com.github.averyregier.club.domain.program.awana.TnTCurriculum;
import com.github.averyregier.club.domain.program.awana.TnTSectionTypes;
import com.github.averyregier.club.domain.utility.Named;
import org.jooq.Result;
import org.jooq.exception.DataAccessException;
import org.jooq.tools.jdbc.MockDataProvider;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static com.github.averyregier.club.broker.BrokerTestUtil.*;
import static com.github.averyregier.club.db.tables.Award.AWARD;
import static com.github.averyregier.club.domain.utility.UtilityMethods.convert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AwardBrokerTest {
    private Person person;
    private ProgramAdapter program;
    private Club club;
    private Listener listener;

    @Before
    public void setup() {
        person = new PersonAdapter();
        program = new ProgramAdapter("en_US", null, "AWANA");
        club = program.addClub(program.getCurriculum().getSeries("TnT").get());
        listener = club.recruit(person);
    }


    @Test
    public void testPersistMerges() throws Exception {
        final AwardPresentation award = newAward();

        MockDataProvider provider = mergeProvider(assertPrimaryKey(award), assertNotYetPresented(award));

        setup(provider).persist(award);
    }

    @Test(expected = DataAccessException.class)
    public void testUpdatesNothing() throws Exception {
        final AwardPresentation award = newAward();

        MockDataProvider provider = new MockDataProviderBuilder()
                .updateCount(0)
                .build();

        setup(provider).persist(award);
    }

    @Test
    public void testPersistsCorrectValues() throws Exception {
        final AwardPresentation award = newAward();

        award.presentAt(new CeremonyAdapter());

        MockDataProvider provider = mergeProvider(assertPrimaryKey(award), assertFields(award));

        setup(provider).persist(award);
    }


    private Consumer<StatementVerifier> assertNotYetPresented(AwardPresentation award) {
        return (s) -> {
            assertNeverNullFields(award, s);
            s.assertNullFields(AWARD.PRESENTED_AT);
        };
    }

    private Consumer<StatementVerifier> assertPrimaryKey(AwardPresentation award) {
        return (s) -> assertPrimaryKey(award, s);
    }

    private Consumer<StatementVerifier> assertFields(AwardPresentation award) {
        return (s) -> assertClubberFields(award, s);
    }

    private void assertPrimaryKey(AwardPresentation award, StatementVerifier s) {
        s.assertUUID(award.getId(), AWARD.ID);
    }

    private void assertClubberFields(AwardPresentation award, StatementVerifier s) {
        assertNeverNullFields(award, s);
        s.assertUUID(award.presentedAt(), AWARD.PRESENTED_AT);
        s.assertFieldEquals(award.token().map(Named::getName).orElse(null), AWARD.TOKEN);
    }

    private void assertNeverNullFields(AwardPresentation award, StatementVerifier s) {
        s.assertUUID(award.to().getId(), AWARD.CLUBBER_ID);
        s.assertFieldEquals(award.record().getSection().getId(), AWARD.SECTION_ID);
        s.assertFieldEquals(award.forAccomplishment().getName(), AWARD.ACCOMPLISHMENT);
    }

    private AwardPresentation newAward() {
        String uuid = UUID.randomUUID().toString();
        return new ClubberAdapter(new PersonAdapter(uuid))
            .getRecord(Optional.of(new CurriculumBuilder().book(0, b -> b
                .group(0, g -> g
                        .award(a -> a
                                .name("An Award")
                                .section(0, TnTSectionTypes.regular)))).build()
                .getBooks().get(0)
                .getSectionGroups().get(0)
                .getSections().get(0)))
                .get()
                .sign(listener, "")
                .getCompletionAwards().stream().findFirst().get();
    }

    private AwardBroker setup(MockDataProvider provider) {
        return new AwardBroker(mockConnector(provider));
    }

    @Test
    public void findNoAwards() {
        Clubber mockClubber = new ClubberAdapter(new PersonManager().createPerson());
        Section section = TnTCurriculum.get().getBooks().get(0).getSections().get(0);
        String sectionId = section.getId();

        MockDataProvider provider = select((s) -> {
            s.assertUUID(mockClubber.getId(), AWARD.CLUBBER_ID);
            s.assertFieldEquals(sectionId, AWARD.SECTION_ID);
        }, (r) -> r.newResult(AWARD));

        assertTrue(setup(provider).find(mockClubber, section).isEmpty());
    }

    @Test
    public void findAwards() {
        Clubber mockClubber = new ClubberAdapter(new PersonManager().createPerson());
        Section section = TnTCurriculum.get().getBooks().get(0).getSections().get(0);
        String sectionId = section.getId();
        ArrayList<AwardRecord> awardRecords = new ArrayList<>();
        awardRecords.add(new AwardRecord(
                UUID.randomUUID().toString().getBytes(),
                mockClubber.getId().getBytes(),
                sectionId,
                "Did Something",
                "Got Something",
                UUID.randomUUID().toString().getBytes()
        ));
        awardRecords.add(new AwardRecord(
                UUID.randomUUID().toString().getBytes(),
                mockClubber.getId().getBytes(),
                sectionId,
                "Did Something Else",
                null,
                null
        ));
        Award anAward = section.getAwards().stream().findAny().get();
        awardRecords.add(new AwardRecord(
                UUID.randomUUID().toString().getBytes(),
                mockClubber.getId().getBytes(),
                sectionId,
                anAward.getName(),
                anAward.selectAwarded().getName(),
                UUID.randomUUID().toString().getBytes()
        ));

        MockDataProvider provider = select((s) -> {
            s.assertUUID(mockClubber.getId(), AWARD.CLUBBER_ID);
            s.assertFieldEquals(sectionId, AWARD.SECTION_ID);
        }, (r) -> {
            Result<AwardRecord> result = r.newResult(AWARD);
            result.addAll(awardRecords);
            return result;
        });

        List<AwardPresentation> results = setup(provider).find(mockClubber, section);
        assertEquals(awardRecords.size(), results.size());
        int i=0;
        for(AwardRecord record: awardRecords) {
            AwardPresentation award = results.get(i++);
            assertEquals(record.getSectionId(), award.record().getSection().getId());
            assertEquals(record.getAccomplishment(), award.forAccomplishment().getName());
            assertEquals(record.getToken(), award.token().map(Named::getName).orElse(null));
            assertEquals(convert(record.getClubberId()), award.to().getId());
            assertEquals(convert(record.getId()), award.getId());
            assertEquals(mockClubber, award.to());
            assertEquals(record.getAccomplishment(), award.getShortCode());
        }
    }
}