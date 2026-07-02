package com.alphaseries.game.achievement;

public final class AchievementState {
    private static final AchievementState INSTANCE = new AchievementState();

    private AchievementSettings settings = AchievementSettings.empty();

    private AchievementState() {
    }

    public static AchievementState instance() {
        return INSTANCE;
    }

    public synchronized AchievementSettings settings() {
        return settings;
    }

    public synchronized void setSettings(AchievementSettings settings) {
        this.settings = settings == null ? AchievementSettings.empty() : settings;
    }

}
