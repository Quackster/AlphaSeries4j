package com.alphaseries.game.messenger;

public final class MessengerState {
    private static final MessengerState INSTANCE = new MessengerState();

    private MessengerSettings settings = MessengerSettings.empty();

    private MessengerState() {
    }

    public static MessengerState instance() {
        return INSTANCE;
    }

    public synchronized MessengerSettings settings() {
        return settings;
    }

    public synchronized void setSettings(MessengerSettings settings) {
        this.settings = settings == null ? MessengerSettings.empty() : settings;
    }

}
