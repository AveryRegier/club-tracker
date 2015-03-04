package com.github.averyregier.club.broker;

import com.github.averyregier.club.domain.club.AwardPresentation;
import com.github.averyregier.club.domain.club.Club;
import com.github.averyregier.club.domain.club.Listener;
import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.club.adapter.CeremonyAdapter;
import com.github.averyregier.club.domain.club.adapter.ClubberAdapter;
import com.github.averyregier.club.domain.club.adapter.PersonAdapter;
import com.github.averyregier.club.domain.club.adapter.ProgramAdapter;
import com.github.averyregier.club.domain.program.adapter.CurriculumBuilder;
import com.github.averyregier.club.domain.program.awana.TnTSectionTypes;
import com.github.averyregier.club.domain.utility.Named;
import org.jooq.exception.DataAccessException;
import org.jooq.tools.jdbc.MockDataProvider;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static com.github.averyregier.club.broker.BrokerTestUtil.mergeProvider;
import static com.github.averyregier.club.broker.BrokerTestUtil.mockConnector;
import static com.github.averyregier.club.db.tables.Award.AWARD;

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
        return new ClubberAdapter(new PersonAdapter() {
            @Override
            public String getId() {
                return uuid;
            }
        }).getRecord(Optional.of(new CurriculumBuilder().book(0, b -> b
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
}