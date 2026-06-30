package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;

import java.sql.SQLException;

public final class UpdaterDao {
    private final Database database;

    public UpdaterDao(Database database) {
        this.database = database;
    }

    public void executeUpdateSql(String sqlText) throws SQLException {
        database.execute(sqlText);
    }
}
