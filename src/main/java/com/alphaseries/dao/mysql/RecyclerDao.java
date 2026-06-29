package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;

import java.sql.SQLException;
import java.util.List;

public final class RecyclerDao {
    private final Database database;

    public RecyclerDao(Database database) {
        this.database = database;
    }

    public List<Long> fallbackRewardProductIds() throws SQLException {
        return database.query(
            "SELECT id_product FROM settings_recycler ORDER BY chance DESC LIMIT 100",
            resultSet -> resultSet.getLong(1));
    }
}
