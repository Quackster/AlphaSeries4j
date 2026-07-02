package com.alphaseries.game.chat;

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

}
