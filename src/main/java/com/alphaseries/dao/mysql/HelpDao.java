package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;

import java.sql.SQLException;
import java.util.List;

public final class HelpDao {
    private final Database database;

    public HelpDao(Database database) {
        this.database = database;
    }

    public String faqSearchRows(String searchText) throws SQLException {
        List<String> rows = database.query(
            "SELECT id,name FROM faq WHERE name LIKE ? LIMIT 25",
            resultSet -> resultSet.getString(1) + "\t" + resultSet.getString(2),
            "%" + searchText + "%");
        return String.join("\r", rows);
    }
}
