package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;

import java.sql.SQLException;
import java.util.List;

public final class ChatDao {
    private final Database database;

    public ChatDao(Database database) {
        this.database = database;
    }

    public List<FilterWord> filterWords() throws SQLException {
        return database.query(
            "SELECT word FROM settings_filter LIMIT 100",
            resultSet -> new FilterWord(resultSet.getString(1)));
    }

    public List<GestureRow> gestureRows() throws SQLException {
        return database.query(
            "SELECT smiley,gesture FROM settings_gesture LIMIT 100",
            resultSet -> new GestureRow(resultSet.getString(1), resultSet.getLong(2)));
    }

    public record FilterWord(String word) {
        public String legacyRow() {
            return word == null ? "" : word;
        }
    }

    public record GestureRow(String smiley, long gestureId) {
        public String legacyRow() {
            return (smiley == null ? "" : smiley) + "\t" + gestureId;
        }
    }
}
