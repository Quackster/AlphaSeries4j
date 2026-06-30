package com.alphaseries.game.chat;

import java.util.List;

public final class ChatState {
    private static final ChatState INSTANCE = new ChatState();

    private ChatSettings settings = ChatSettings.empty();

    private ChatState() {
    }

    public static ChatState instance() {
        return INSTANCE;
    }

    public synchronized ChatSettings settings() {
        return settings;
    }

    public synchronized void setSettings(ChatSettings settings) {
        this.settings = settings == null ? ChatSettings.empty() : settings;
    }

    public synchronized void setRows(List<ChatSettings.FilterWord> filterRows, List<ChatSettings.Gesture> gestureRows) {
        settings = ChatSettings.fromRows(filterRows, gestureRows);
    }

    public synchronized void setSettingsFromLegacy(Object filterRows, Object gestureRows) {
        settings = ChatSettings.fromLegacy(filterRows, gestureRows);
    }
}
