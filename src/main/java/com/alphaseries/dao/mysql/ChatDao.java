package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;
import com.alphaseries.game.chat.ChatSettings;

import java.sql.SQLException;
import java.util.List;

public final class ChatDao {
    private final Database database;

    public ChatDao(Database database) {
        this.database = database;
    }

    public List<ChatSettings.FilterWord> filterWords() throws SQLException {
        return database.query(
            "SELECT word FROM settings_filter LIMIT 100",
            resultSet -> new ChatSettings.FilterWord(resultSet.getString(1)));
    }

    public List<ChatSettings.Gesture> gestureRows() throws SQLException {
        return database.query(
            "SELECT smiley,gesture FROM settings_gesture LIMIT 100",
            resultSet -> new ChatSettings.Gesture(resultSet.getString(1), resultSet.getLong(2)));
    }
}
