package com.alphaseries.db;

import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface Database {
    void execute(String sqlText) throws SQLException;

    List<List<Object>> query(String sqlText) throws SQLException;

    default int execute(String sqlText, Object... parameters) throws SQLException {
        execute(renderPreparedSqlForFallback(sqlText, parameters));
        return 0;
    }

    default <T> List<T> query(String sqlText, RowMapper<T> mapper, Object... parameters) throws SQLException {
        List<T> mappedRows = new ArrayList<>();
        for (List<Object> row : query(renderPreparedSqlForFallback(sqlText, parameters))) {
            mappedRows.add(mapper.map(resultSetFor(row)));
        }
        return mappedRows;
    }

    default <T> Optional<T> queryOne(String sqlText, RowMapper<T> mapper, Object... parameters) throws SQLException {
        List<T> rows = query(sqlText, mapper, parameters);
        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }

    private static ResultSet resultSetFor(List<Object> row) {
        return (ResultSet) Proxy.newProxyInstance(
            ResultSet.class.getClassLoader(),
            new Class<?>[]{ResultSet.class},
            (proxy, method, args) -> {
                String methodName = method.getName();
                if ("getObject".equals(methodName) && args != null && args.length == 1 && args[0] instanceof Integer) {
                    return valueAt(row, (Integer) args[0]);
                }
                if ("getLong".equals(methodName) && args != null && args.length == 1 && args[0] instanceof Integer) {
                    Object value = valueAt(row, (Integer) args[0]);
                    if (value instanceof Number number) {
                        return number.longValue();
                    }
                    if (value == null || String.valueOf(value).isEmpty()) {
                        return 0L;
                    }
                    return Long.parseLong(String.valueOf(value));
                }
                if ("getString".equals(methodName) && args != null && args.length == 1 && args[0] instanceof Integer) {
                    Object value = valueAt(row, (Integer) args[0]);
                    return value == null ? null : String.valueOf(value);
                }
                if ("wasNull".equals(methodName)) {
                    return false;
                }
                if ("toString".equals(methodName)) {
                    return "MappedRowResultSet" + row;
                }
                throw new UnsupportedOperationException("ResultSet method not supported by mapped row adapter: " + methodName);
            });
    }

    private static Object valueAt(List<Object> row, int oneBasedIndex) {
        int index = oneBasedIndex - 1;
        return row != null && index >= 0 && index < row.size() ? row.get(index) : null;
    }

    private static String renderPreparedSqlForFallback(String sqlText, Object... parameters) {
        if (parameters == null || parameters.length == 0 || sqlText == null || sqlText.indexOf('?') < 0) {
            return sqlText;
        }
        StringBuilder rendered = new StringBuilder();
        int parameterIndex = 0;
        for (int index = 0; index < sqlText.length(); index++) {
            char ch = sqlText.charAt(index);
            if (ch == '?' && parameterIndex < parameters.length) {
                rendered.append(sqlLiteral(parameters[parameterIndex++]));
            } else {
                rendered.append(ch);
            }
        }
        return rendered.toString();
    }

    private static String sqlLiteral(Object value) {
        if (value == null) {
            return "NULL";
        }
        return "'" + String.valueOf(value).replace("'", "''") + "'";
    }
}
