package com.alphaseries.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class JdbcDatabase implements Database {
    private final Connection connection;

    public JdbcDatabase(Connection connection) {
        this.connection = Objects.requireNonNull(connection, "connection");
    }

    @Override
    public void execute(String sqlText) throws SQLException {
        execute(sqlText, new Object[0]);
    }

    @Override
    public int execute(String sqlText, Object... parameters) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sqlText)) {
            bind(statement, parameters);
            return statement.executeUpdate();
        }
    }

    @Override
    public List<List<Object>> query(String sqlText) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sqlText);
             ResultSet resultSet = statement.executeQuery()) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            List<List<Object>> rows = new ArrayList<>();
            while (resultSet.next()) {
                List<Object> row = new ArrayList<>(columnCount);
                for (int column = 1; column <= columnCount; column++) {
                    row.add(resultSet.getObject(column));
                }
                rows.add(row);
            }
            return rows;
        }
    }

    @Override
    public <T> List<T> query(String sqlText, RowMapper<T> mapper, Object... parameters) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sqlText)) {
            bind(statement, parameters);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<T> rows = new ArrayList<>();
                while (resultSet.next()) {
                    rows.add(mapper.map(resultSet));
                }
                return rows;
            }
        }
    }

    private static void bind(PreparedStatement statement, Object... parameters) throws SQLException {
        if (parameters == null) {
            return;
        }
        for (int index = 0; index < parameters.length; index++) {
            statement.setObject(index + 1, parameters[index]);
        }
    }
}
