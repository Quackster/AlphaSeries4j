package com.alphaseries.game.recycler;

import com.alphaseries.util.StringUtils;

import java.util.List;

public final class RecyclerSettings {
    private final String statusPayload;
    private final List<RewardGroup> rewardGroups;
    private final long boxProductId;

    private RecyclerSettings(String statusPayload, List<RewardGroup> rewardGroups, long boxProductId) {
        this.statusPayload = StringUtils.text(statusPayload);
        this.rewardGroups = rewardGroups == null ? List.of() : List.copyOf(rewardGroups);
        this.boxProductId = boxProductId;
    }

    public static RecyclerSettings empty() {
        return new RecyclerSettings("", List.of(), 0L);
    }

    public static RecyclerSettings fromRewardGroups(List<RewardGroup> rewardGroups, long boxProductId) {
        return new RecyclerSettings("", rewardGroups == null ? List.of() : rewardGroups, boxProductId);
    }

    public static RecyclerSettings fromRewardGroups(StatusPayload statusPayload, List<RewardGroup> rewardGroups,
                                                    long boxProductId) {
        return new RecyclerSettings(
            statusPayload == null ? "" : statusPayload.payload(),
            rewardGroups == null ? List.of() : rewardGroups,
            boxProductId);
    }

    static RecyclerSettings fromStatusPayload(String statusPayload, List<RewardGroup> rewardGroups,
                                              long boxProductId) {
        return new RecyclerSettings(statusPayload, rewardGroups == null ? List.of() : rewardGroups, boxProductId);
    }

    StatusPayload status() {
        return new StatusPayload(statusPayload);
    }

    String statusPayload() {
        return statusPayload;
    }

    public List<RewardGroup> rewardGroups() {
        return rewardGroups;
    }

    public long boxProductId() {
        return boxProductId;
    }

    public boolean hasRewardGroups() {
        return !rewardGroups.isEmpty();
    }

    public record RewardGroup(long chance, List<Long> productIds) {
        public RewardGroup {
            productIds = productIds == null ? List.of() : List.copyOf(productIds);
        }
    }

    public static final class StatusPayload {
        private final String payload;

        private StatusPayload(String payload) {
            this.payload = StringUtils.text(payload);
        }

        static StatusPayload fromPayload(String payload) {
            return new StatusPayload(payload);
        }

        String payload() {
            return payload;
        }
    }
}
