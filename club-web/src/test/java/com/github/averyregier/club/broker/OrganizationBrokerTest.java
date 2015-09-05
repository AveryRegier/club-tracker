package com.github.averyregier.club.broker;

import com.github.averyregier.club.domain.ClubManager;
import com.github.averyregier.club.domain.club.Program;
import com.github.averyregier.club.domain.club.adapter.ProgramAdapter;
import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.program.Programs;
import org.jooq.DSLContext;
import org.jooq.Record7;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockExecuteContext;
import org.jooq.tools.jdbc.MockResult;
import org.junit.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static com.github.averyregier.club.broker.BrokerTestUtil.mergeProvider;
import static com.github.averyregier.club.broker.BrokerTestUtil.mockConnector;
import static com.github.averyregier.club.db.tables.Club.CLUB;
import static com.github.averyregier.club.db.tables.Organization.ORGANIZATION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
        final Program program = new ProgramAdapter(null, null, (String)null) {
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
            s.assertFieldEquals(program.getShortCode(), ORGANIZATION.ORGANIZATION_NAME);
            s.assertUUID(program, ORGANIZATION.CLUB_ID);
        };
    }

    private Consumer<StatementVerifier> assertNullFields(Program program) {
        return (s) -> {
            s.assertNullFields(ORGANIZATION.LOCALE);
            s.assertNullFields(ORGANIZATION.ORGANIZATION_NAME);
            s.assertUUID(program, ORGANIZATION.CLUB_ID);  // this one shouldn't be null ever
        };
    }

    private Consumer<StatementVerifier> assertUUID(Program program) {
        return (s) -> s.assertUUID(program, ORGANIZATION.ID);
    }

    private OrganizationBroker setup(MockDataProvider provider) {
        return new OrganizationBroker(mockConnector(provider));
    }

    @Test
    public void findProgram() {
        ClubManager clubManager = new ClubManager();
        String id = UUID.randomUUID().toString();
        Curriculum curriculum = Programs.AWANA.get();
        String orgName = "An Org";
        String locale = "en_US";
        DSLContext create = DSL.using(SQLDialect.HSQLDB);
        MockDataProvider provider = ctx -> {
            assertWhere(id, ctx);
            // TODO: assert the join
            // TODO: refactor this mess
            Result<Record7<byte[], String, String, byte[], byte[], byte[], String>> result =
                    create.newResult(
                        ORGANIZATION.ID,
                        ORGANIZATION.ORGANIZATION_NAME,
                        ORGANIZATION.LOCALE,
                        ORGANIZATION.CLUB_ID,
                        CLUB.ID,
                        CLUB.PARENT_CLUB_ID,
                        CLUB.CURRICULUM);

            Record7<byte[], String, String, byte[], byte[], byte[], String> record =
                    create.newRecord(
                        ORGANIZATION.ID,
                        ORGANIZATION.ORGANIZATION_NAME,
                        ORGANIZATION.LOCALE,
                        ORGANIZATION.CLUB_ID,
                        CLUB.ID,
                        CLUB.PARENT_CLUB_ID,
                        CLUB.CURRICULUM);
            result.add(record);

            record.setValue(ORGANIZATION.ID, id.getBytes());
            record.setValue(ORGANIZATION.CLUB_ID, id.getBytes());
            record.setValue(ORGANIZATION.ORGANIZATION_NAME, orgName);
            record.setValue(ORGANIZATION.LOCALE, locale);
            record.setValue(CLUB.ID, id.getBytes());
            record.setValue(CLUB.PARENT_CLUB_ID, null);
            record.setValue(CLUB.CURRICULUM, curriculum.getId());
            return new MockResult[] {new MockResult(1, result)};
        };

        Program program = setup(provider).find(id, clubManager).get();
        assertEquals(id, program.getId());
        assertFalse(program.getParentGroup().isPresent());
        assertEquals(curriculum, program.getCurriculum());
        assertEquals(locale, program.getLocale().toString());
        assertEquals(orgName, program.getShortCode());
    }

    @Test
    public void findNoProgram() {
        String id = UUID.randomUUID().toString();

        MockDataProvider provider = ctx -> {
            assertWhere(id, ctx);
            return emptyResults();
        };

        assertFalse(setup(provider).find(id, null).isPresent());
    }

    private MockResult[] emptyResults() {
        MockResult[] results = new MockResult[1];
        results[0] = new MockResult(0, null);
        return results;
    }

    private void assertWhere(String id, MockExecuteContext ctx) {
        Object[] bindings = ctx.bindings();
        assertEquals(id, new String((byte[]) bindings[bindings.length-1]));
    }
}
