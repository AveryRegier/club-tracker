package com.github.averyregier.club.broker;

import org.jooq.*;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockDataProvider;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by avery on 2/21/15.
 */
public class BrokerTestUtil {
    static Connector mockConnector(MockDataProvider provider) {
        // Put your provider into a MockConnection and use that connection
        // in your application. In this case, with a jOOQ DSLContext:
        Connection connection = new MockConnection(provider);

        return new Connector() {

            @Override
            public Connection connect() throws SQLException {
                return connection;
            }

            @Override
            public SQLDialect getDialect() {
                return SQLDialect.HSQLDB;
            }
        };
    }

    public static List<String> parseColumnsFrom(String sql) {
        List<String> columns;
        columns = new ArrayList<>();
        Pattern pattern = Pattern.compile(".*?(\\S+)\\s*=\\s*[.&&^[=\\?]]*?");
        Matcher matcher = pattern.matcher(sql);
        while (matcher.find()) {
            String column = matcher.group(1);
            List<String> split = Arrays.asList(column.split("\""));
            column = split.get(split.size() - 1);
            columns.add(column);
        }
        return columns;
    }

    static MockDataProvider mergeProvider(Consumer<StatementVerifier> idFn, Consumer<StatementVerifier> fieldsFn) {
        return new MockDataProviderBuilder()
                    .updateCount(1)
                    .statement(StatementType.MERGE, idFn)
                    .statement(StatementType.UPDATE, fieldsFn)
                    .statement(StatementType.INSERT, (s) -> {
                        idFn.accept(s);
                        fieldsFn.accept(s);
                    })
                    .build();
    }
    static MockDataProvider mergeProvider(Consumer<StatementVerifier> idFn) {
        return new MockDataProviderBuilder()
                .updateCount(1)
                .statement(StatementType.MERGE, idFn)
                .statement(StatementType.INSERT, idFn)
                .build();
    }

    static <R extends UpdatableRecord<R>> MockDataProvider selectOne(
            Consumer<StatementVerifier> assertWhereFn,
            Table<R> table,
            Consumer<R> fn)
    {
        return select(assertWhereFn, (create) -> {
            Result<R> result = create.newResult(table);
            R record = create.newRecord(table);
            result.add(record);
            fn.accept(record);
            return result;
        });
    }

    static MockDataProvider select(Consumer<StatementVerifier> assertWhereFn, Function<DSLContext, Result<?>> addResultFn) {
        return new MockDataProviderBuilder().statement(StatementType.SELECT,
                assertWhereFn).reply(addResultFn).build();
    }
}
