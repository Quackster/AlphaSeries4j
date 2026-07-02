package com.alphaseries.game.moderation;

public final class ModerationState {
    private static final ModerationState INSTANCE = new ModerationState();

    private StaffSettings staffSettings = StaffSettings.empty();

    private ModerationState() {
    }

    public static ModerationState instance() {
        return INSTANCE;
    }

    public synchronized StaffSettings staffSettings() {
        return staffSettings;
    }

    public synchronized void setStaffSettings(StaffSettings staffSettings) {
        this.staffSettings = staffSettings == null ? StaffSettings.empty() : staffSettings;
    }
}
