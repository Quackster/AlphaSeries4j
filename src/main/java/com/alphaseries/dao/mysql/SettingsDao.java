package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;

import java.sql.SQLException;
import java.util.List;

public final class SettingsDao {
    private final Database database;

    public SettingsDao(Database database) {
        this.database = database;
    }

    public List<SettingRow> allSettings() throws SQLException {
        return database.query(
            "SELECT variable,value FROM settings",
            resultSet -> new SettingRow(resultSet.getString(1), resultSet.getString(2)));
    }

    public List<LocaleRow> roomEventLocaleRows() throws SQLException {
        return database.query(
            "SELECT variable,value FROM locales WHERE category=? AND variable LIKE ?",
            resultSet -> new LocaleRow(resultSet.getString(1), resultSet.getString(2)),
            2L,
            "roomevent_type_%");
    }

    public List<String> levelPrivileges(long rank, long hcLevel) throws SQLException {
        return database.query(
            "SELECT privilege FROM level_privileges WHERE min_level <= ? AND min_level_hc <= ?",
            resultSet -> resultSet.getString(1),
            rank,
            hcLevel);
    }

    public String value(String variableName) throws SQLException {
        return database.queryOne(
            "SELECT value FROM settings WHERE variable=?",
            resultSet -> resultSet.getString(1),
            variableName)
            .orElse("");
    }

    public record SettingRow(String variableName, String value) {
    }

    public record LocaleRow(String variableName, String value) {
    }
}
