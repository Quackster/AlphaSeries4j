package com.alphaseries.dao.mysql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class SqlFragments {
    private SqlFragments() {
    }

    static String placeholders(int count) {
        return String.join(",", Collections.nCopies(Math.max(0, count), "?"));
    }

    static String unionAll(String... queries) {
        return String.join(" UNION ALL ", queries);
    }

    static Object[] parametersWithIds(List<?> leadingParameters, List<Long> ids) {
        return parametersWithIds(leadingParameters, ids, List.of());
    }

    static Object[] idParameters(List<Long> ids) {
        return parametersWithIds(List.of(), ids);
    }

    static Object[] parametersWithIds(List<?> leadingParameters, List<Long> ids, List<?> trailingParameters) {
        List<Object> parameters = new ArrayList<>();
        if (leadingParameters != null) {
            parameters.addAll(leadingParameters);
        }
        if (ids != null) {
            parameters.addAll(ids);
        }
        if (trailingParameters != null) {
            parameters.addAll(trailingParameters);
        }
        return parameters.toArray();
    }

    static Object[] parametersWithRepeatedIds(List<?> firstLeadingParameters, List<Long> ids, List<?> secondLeadingParameters) {
        List<Object> parameters = new ArrayList<>();
        if (firstLeadingParameters != null) {
            parameters.addAll(firstLeadingParameters);
        }
        if (ids != null) {
            parameters.addAll(ids);
        }
        if (secondLeadingParameters != null) {
            parameters.addAll(secondLeadingParameters);
        }
        if (ids != null) {
            parameters.addAll(ids);
        }
        return parameters.toArray();
    }
}
