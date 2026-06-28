package com.alphaseries;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
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
        try (Statement statement = connection.createStatement()) {
            statement.execute(sqlText);
        }
    }

    @Override
    public List<List<Object>> query(String sqlText) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sqlText)) {
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
}
