package com.github.averyregier.club.broker;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockExecuteContext;
import org.jooq.tools.jdbc.MockResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.github.averyregier.club.broker.BrokerTestUtil.mockConnector;
import static org.junit.Assert.fail;

/**
 * Created by avery on 2/22/15.
 */
public class MockDataProviderBuilder {

    private List<MockResult> results = new ArrayList<>();
    private List<Consumer<MockExecuteContext>> verifications = new ArrayList<>();
    private List<StatementVerifier> statements = new ArrayList<>();

    public MockDataProviderBuilder updateCount(int rows) {
        results.add(new MockResult(rows, null));
        return this;
    }

    public MockDataProviderBuilder reply(Function<DSLContext, Result<?>> fn) {

        new PersistenceBroker<Void>(mockConnector(ctx->null)){
            @Override
            protected void persist(Void thing, DSLContext create) {

            }
        }.execute((create)->
                results.add(new MockResult(-1, fn.apply(create))));

        return this;
    }

    public MockDataProviderBuilder verify(Consumer<MockExecuteContext> r) {
        verifications.add(r);
        return this;
    }

    public MockDataProviderBuilder statement(StatementType type, Consumer<StatementVerifier> fn) {
        if (statements.isEmpty()) {
            verifications.add((ctx) -> {
                loadStatements(ctx);
                statements.stream().forEach(StatementVerifier::verify);
            });
        }
        StatementVerifier svb = new StatementVerifier(type, fn);
        statements.add(svb);
        return this;
    }

    public MockDataProviderBuilder statement(StatementType type) {
        return statement(type, null);
    }

    private void loadStatements(MockExecuteContext ctx) {
        Collection<Object> bindings = new ArrayList<>(Arrays.asList(ctx.bindings()));
        int start, next = 0;
        StatementVerifier previous = null;
        for (StatementVerifier statement : statements) {
            StatementType type1 = statement.getType();
            String name = type1.name();
            start = next;
            String sql = ctx.sql();
            next = sql.toUpperCase().indexOf(name, start);
            if (next > start && previous != null) {
                previous.bind(sql.substring(start, next), bindings);
            } else if (next == -1) {
                fail(name + " not found");
            }
            previous = statement;
        }
        if (previous != null) {
            previous.bind(ctx.sql().substring(next), bindings);
        }
    }

    public MockDataProvider build() {
        return ctx -> {
            verifications.forEach(c -> c.accept(ctx));
            return results.toArray(new MockResult[results.size()]);
        };
    }
}
