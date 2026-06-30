package com.alphaseries.game.pet;

public final class PetState {
    private static final PetState INSTANCE = new PetState();

    private PetSettings settings = PetSettings.empty();
    private RepresentedBotRegistry representedBots = RepresentedBotRegistry.empty();

    private PetState() {
    }

    public static PetState instance() {
        return INSTANCE;
    }

    public synchronized PetSettings settings() {
        return settings;
    }

    public synchronized RepresentedBotRegistry representedBots() {
        return representedBots;
    }

    public synchronized void setRepresentedBots(RepresentedBotRegistry representedBots) {
        this.representedBots = representedBots == null ? RepresentedBotRegistry.empty() : representedBots;
    }

    public synchronized void setRepresentedBotsFromLegacy(Object allocatedEntityMarkers, Object recordCache) {
        representedBots = RepresentedBotRegistry.fromLegacy(allocatedEntityMarkers, recordCache);
    }

    public synchronized void setSettings(PetSettings settings) {
        this.settings = settings == null ? PetSettings.empty() : settings;
    }

    public synchronized void setSettingsFromLegacy(Object raceRows, Object levelRows, Object commandRows, long commandCount) {
        settings = PetSettings.fromLegacy(raceRows, levelRows, commandRows, commandCount);
    }
}
