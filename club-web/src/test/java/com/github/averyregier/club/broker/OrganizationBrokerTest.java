package com.github.averyregier.club.broker;

import com.github.averyregier.club.domain.club.Program;
import com.github.averyregier.club.domain.club.adapter.ProgramAdapter;
import org.jooq.exception.DataAccessException;
import org.jooq.tools.jdbc.MockDataProvider;
import org.junit.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static com.github.averyregier.club.broker.BrokerTestUtil.mergeProvider;
import static com.github.averyregier.club.broker.BrokerTestUtil.mockConnector;
import static com.github.averyregier.club.db.tables.Organization.ORGANIZATION;

/**
 * Created by avery on 2/25/15.
 */
public class OrganizationBrokerTest {
    @Test
    public void testPersistProgramAsOrganization() {
        final Program program = newProgram();

        MockDataProvider provider = mergeProvider(assertUUID(program), assertFields(program));

        setup(provider).persist(program);
    }

    @Test
    public void testPersistEmptyProgram() {
        final Program program = new ProgramAdapter(null, null, null) {
            String id = UUID.randomUUID().toString();

            @Override
            public String getId() {
                return id;
            }
        };

        MockDataProvider provider = mergeProvider(assertUUID(program), assertNullFields(program));

        setup(provider).persist(program);
    }

    @Test(expected = DataAccessException.class)
    public void testUpdatesNothing() throws Exception {
        final Program club = newProgram();

        MockDataProvider provider = new MockDataProviderBuilder()
                .updateCount(0)
                .build();

        setup(provider).persist(club);
    }

    private Program newProgram() {
        return new ProgramAdapter("en", "Any Org Nmae", "AWANA") {
            String id = UUID.randomUUID().toString();

            @Override
            public String getId() {
                return id;
            }
        };
    }

    private Consumer<StatementVerifier> assertFields(Program program) {
        return (s) -> {
            s.assertFieldEquals(program.getLocale().toString(), ORGANIZATION.LOCALE);
            s.assertFieldEquals(program.getShortCode(), ORGANIZATION.ORGANIZATIONNAME);
            s.assertUUID(program, ORGANIZATION.CLUB_ID);
        };
    }

    private Consumer<StatementVerifier> assertNullFields(Program program) {
        return (s) -> {
            s.assertNullFields(ORGANIZATION.LOCALE);
            s.assertNullFields(ORGANIZATION.ORGANIZATIONNAME);
            s.assertUUID(program, ORGANIZATION.CLUB_ID);  // this one shouldn't be null ever
        };
    }

    private Consumer<StatementVerifier> assertUUID(Program program) {
        return (s) -> s.assertUUID(program, ORGANIZATION.ID);
    }

    private Broker<Program> setup(MockDataProvider provider) {
        return new OrganizationBroker(mockConnector(provider));
    }
}