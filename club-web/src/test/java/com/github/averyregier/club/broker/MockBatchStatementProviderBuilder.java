package com.github.averyregier.club.broker;

import org.jooq.tools.jdbc.MockDataProvider;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by avery on 2/22/15.
 */
public class MockBatchStatementProviderBuilder {

    private Queue<MockDataProvider> statements = new ArrayDeque<>();

    public MockBatchStatementProviderBuilder statement(MockDataProvider provider) {
        statements.add(provider);
        return this;
    }


    public MockDataProvider build() {
        return ctx -> statements.remove().execute(ctx);
    }
}
