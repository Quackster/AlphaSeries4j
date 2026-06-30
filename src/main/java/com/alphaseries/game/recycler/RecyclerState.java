package com.alphaseries.game.recycler;

public final class RecyclerState {
    private static final RecyclerState INSTANCE = new RecyclerState();

    private RecyclerSettings settings = RecyclerSettings.empty();

    private RecyclerState() {
    }

    public static RecyclerState instance() {
        return INSTANCE;
    }

    public synchronized RecyclerSettings settings() {
        return settings;
    }

    public synchronized void setSettings(RecyclerSettings settings) {
        this.settings = settings == null ? RecyclerSettings.empty() : settings;
    }

    public synchronized void setSettingsFromLegacy(
        String statusPayload,
        Object productLists,
        Object chances,
        long groupCount,
        long boxProductId
    ) {
        settings = RecyclerSettings.fromLegacy(statusPayload, productLists, chances, groupCount, boxProductId);
    }
}
