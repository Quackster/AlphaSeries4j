package com.alphaseries.game.wired;

public final class WiredState {
    private static final WiredState INSTANCE = new WiredState();

    private WiredSettings settings = WiredSettings.empty();

    private WiredState() {
    }

    public static WiredState instance() {
        return INSTANCE;
    }

    public synchronized WiredSettings settings() {
        return settings;
    }

    public synchronized void setSettings(WiredSettings settings) {
        this.settings = settings == null ? WiredSettings.empty() : settings;
    }

    synchronized void setStatePayload(String statePayload) {
        settings = WiredSettings.fromStatePayload(statePayload);
    }

}
