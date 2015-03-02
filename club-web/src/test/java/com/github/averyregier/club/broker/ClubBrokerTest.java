package com.github.averyregier.club.broker;

import com.github.averyregier.club.domain.club.Club;
import com.github.averyregier.club.domain.club.Program;
import com.github.averyregier.club.domain.club.adapter.MockClub;
import com.github.averyregier.club.domain.club.adapter.ProgramAdapter;
import com.github.averyregier.club.domain.program.awana.TnTCurriculum;
import org.jooq.exception.DataAccessException;
import org.jooq.tools.jdbc.MockDataProvider;
import org.junit.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static com.github.averyregier.club.broker.BrokerTestUtil.mergeProvider;
import static com.github.averyregier.club.broker.BrokerTestUtil.mockConnector;
import static com.github.averyregier.club.db.tables.Club.CLUB;

/**
 * Created by avery on 2/25/15.
 */
public class ClubBrokerTest {

    @Test
    public void testPersist() {
        final MockClub club = newClub();

        MockDataProvider provider = mergeProvider(assertUUID(club), assertFields(club));

        setup(provider).persist(club);
    }

    @Test
    public void testPersistProgramAsClub() {
        final Program program = newClub().getProgram();

        MockDataProvider provider = mergeProvider(assertUUID(program), assertFields(program));

        setup(provider).persist(program);
    }

    @Test(expected = DataAccessException.class)
    public void testUpdatesNothing() throws Exception {
        final MockClub club = newClub();

        MockDataProvider provider = new MockDataProviderBuilder()
                .updateCount(0)
                .build();

        setup(provider).persist(club);
    }

    private MockClub newClub() {
        return new MockClub(TnTCurriculum.get(), new ProgramAdapter("en_US", "Any Org Nmae", "AWANA") {
            String id = UUID.randomUUID().toString();

            @Override
            public String getId() {
                return id;
            }
        }) {
            String id = UUID.randomUUID().toString();
            @Override
            public String getId() {
                return id;
            }
        };
    }

    private Broker<Club> setup(MockDataProvider provider) {
        return new ClubBroker(mockConnector(provider));
    }

    private Consumer<StatementVerifier> assertFields(Club club) {
        return (s) -> assertFields(club, s);
    }

    private Consumer<StatementVerifier> assertUUID(Club club) {
        return (s) -> s.assertUUID(club, CLUB.ID);
    }

    private void assertFields(Club club, StatementVerifier s) {
        s.assertUUID(club.getParentGroup(), CLUB.PARENT_CLUB_ID);
        s.assertFieldEquals(club.getCurriculum().getId(), CLUB.CURRICULUM);
    }
}
