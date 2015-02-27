package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.records.ClubRecord;
import com.github.averyregier.club.domain.club.Club;
import com.github.averyregier.club.domain.club.ClubGroup;
import com.github.averyregier.club.domain.club.Program;
import com.github.averyregier.club.domain.club.adapter.MockClub;
import com.github.averyregier.club.domain.club.adapter.ProgramAdapter;
import com.github.averyregier.club.domain.program.awana.TnTCurriculum;
import org.jooq.TableField;
import org.jooq.exception.DataAccessException;
import org.jooq.tools.jdbc.MockDataProvider;
import org.junit.Test;

import java.util.UUID;

import static com.github.averyregier.club.broker.BrokerTestUtil.mockConnector;
import static com.github.averyregier.club.db.tables.Club.CLUB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by avery on 2/25/15.
 */
public class ClubBrokerTest {

    @Test
    public void testPersist() {
        final MockClub club = newClub();

        MockDataProvider provider = new MockDataProviderBuilder()
                .updateCount(1)
                .statement(StatementType.MERGE, (s) -> assertUUID(club, s, CLUB.ID))
                .statement(StatementType.UPDATE, (s) -> assertFields(club, s))
                .statement(StatementType.INSERT, (s) -> {
                    assertUUID(club, s, CLUB.ID);
                    assertFields(club, s);
                })
                .build();

        setup(provider).persist(club);
    }

    @Test
    public void testPersistProgramAsClub() {
        final Program program = newClub().getProgram();

        MockDataProvider provider = new MockDataProviderBuilder()
                .updateCount(1)
                .statement(StatementType.MERGE, (s) -> assertUUID(program, s, CLUB.ID))
                .statement(StatementType.UPDATE, (s) -> assertFields(program, s))
                .statement(StatementType.INSERT, (s) -> {
                    assertUUID(program, s, CLUB.ID);
                    assertFields(program, s);
                })
                .build();

        setup(provider).persist(program);
    }

    private void assertFields(Club club, StatementVerifier s) {
        assertUUID(club.getParentGroup().orElse(null), s, CLUB.PARENT_CLUB_ID);
        assertEquals(club.getCurriculum().getId(), s.get(CLUB.CURRICULUM));
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

    private void assertUUID(ClubGroup club, StatementVerifier s, TableField<ClubRecord, byte[]> field) {
        if(club != null) {
            assertEquals(club.getId(), new String(s.get(field)));
        } else {
            assertNull(s.get(field));
        }
    }
}
