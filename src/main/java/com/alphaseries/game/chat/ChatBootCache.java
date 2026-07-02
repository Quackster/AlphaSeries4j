package com.alphaseries.game.chat;

import com.alphaseries.dao.mysql.ChatDao;
import com.alphaseries.db.Database;
import com.alphaseries.db.MySQL;

import java.util.List;

public final class ChatBootCache {
    private ChatBootCache() {
    }

    public static void buildChatSettingsCache() {
        ChatDao chat = chatDao();
        List<ChatSettings.FilterWord> filterRows = List.of();
        List<ChatSettings.Gesture> gestureRows = List.of();
        if (chat != null) {
            try {
                filterRows = chat.filterWords();
                gestureRows = chat.gestureRows();
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        ChatState.instance().setSettings(ChatSettings.fromRows(filterRows, gestureRows));
    }

    private static ChatDao chatDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new ChatDao(database);
    }
}
