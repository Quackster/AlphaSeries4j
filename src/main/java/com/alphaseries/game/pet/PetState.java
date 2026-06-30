package com.alphaseries.game.pet;

public final class PetState {
    private static final PetState INSTANCE = new PetState();

    private PetSettings settings = PetSettings.empty();

    private PetState() {
    }

    public static PetState instance() {
        return INSTANCE;
    }

    public synchronized PetSettings settings() {
        return settings;
    }

    public synchronized void setSettings(PetSettings settings) {
        this.settings = settings == null ? PetSettings.empty() : settings;
    }

    public synchronized void setSettingsFromLegacy(String raceRows, Object levelRows, Object commandRows, long commandCount) {
        settings = PetSettings.fromLegacy(raceRows, levelRows, commandRows, commandCount);
    }
}
