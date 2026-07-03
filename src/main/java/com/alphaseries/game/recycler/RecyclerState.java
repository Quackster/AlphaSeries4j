package com.alphaseries.game.recycler;

import java.util.List;

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

    public synchronized void setRewards(List<RecyclerSettings.RewardGroup> rewardGroups) {
        settings = RecyclerSettings.fromRewardGroups(
            settings.status(),
            rewardGroups == null ? List.of() : List.copyOf(rewardGroups),
            settings.boxProductId());
    }

    public synchronized void setStatusPayload(RecyclerSettings.StatusPayload statusPayload) {
        settings = RecyclerSettings.fromRewardGroups(
            statusPayload,
            settings.rewardGroups(),
            settings.boxProductId());
    }

    synchronized void setStatusPayload(String statusPayload) {
        settings = RecyclerSettings.fromStatusPayload(
            statusPayload,
            settings.rewardGroups(),
            settings.boxProductId());
    }

    public synchronized void setBoxProductId(long boxProductId) {
        settings = RecyclerSettings.fromRewardGroups(
            settings.status(),
            settings.rewardGroups(),
            Math.max(0L, boxProductId));
    }

}
