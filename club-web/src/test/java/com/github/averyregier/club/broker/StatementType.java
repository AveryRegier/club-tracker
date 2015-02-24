package com.github.averyregier.club.broker;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by avery on 2/21/15.
 */
public enum StatementType {
    SELECT,
    INSERT {
        @Override
        public List<String> parseColumnsFrom(String sql) {
            int start = sql.indexOf("(");
            int end = sql.indexOf(")", start);
            return Arrays.stream(sql.substring(start + 1, end).split(","))
                    .map(s -> s.trim().replaceAll("\"", ""))
                    .collect(Collectors.toList());
        }
    },
    UPDATE,
    DELETE,
    MERGE,
    UNKNOWN;

    public static StatementType find(String sql) {
        for (StatementType type : StatementType.values()) {
            if (type.name().equalsIgnoreCase(sql.substring(0, type.name().length()))) {
                return type;
            }
        }
        return UNKNOWN;
    }

    public List<String> parseColumnsFrom(String sql) {
        return BrokerTestUtil.parseColumnsFrom(sql);
    }
}
