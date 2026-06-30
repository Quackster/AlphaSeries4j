package com.alphaseries.game.recycler;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class RecyclerSettings {
    private final String statusPayload;
    private final List<RewardGroup> rewardGroups;
    private final long boxProductId;

    private RecyclerSettings(String statusPayload, List<RewardGroup> rewardGroups, long boxProductId) {
        this.statusPayload = StringUtils.text(statusPayload);
        this.rewardGroups = rewardGroups;
        this.boxProductId = boxProductId;
    }

    public static RecyclerSettings fromLegacy(String statusPayload, Object productLists, Object chances, long groupCount, long boxProductId) {
        if (productLists instanceof RecyclerSettings recyclerSettings) {
            return recyclerSettings;
        }
        List<RewardGroup> groups = new ArrayList<>();
        if (productLists instanceof Object[] productArray && chances instanceof Object[] chanceArray) {
            long maxGroups = Math.min(groupCount, Math.min(productArray.length, chanceArray.length));
            for (int index = 0; index < maxGroups; index++) {
                groups.add(new RewardGroup(NumberUtils.parseLong(chanceArray[index]), productIds(productArray[index])));
            }
        }
        return new RecyclerSettings(statusPayload, groups, boxProductId);
    }

    public static RecyclerSettings empty() {
        return new RecyclerSettings("", List.of(), 0L);
    }

    public String statusPayload() {
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

    public static List<Long> productIds(Object productList) {
        List<Long> ids = new ArrayList<>();
        for (String productRow : StringUtils.text(productList).split("\2", -1)) {
            long productId = NumberUtils.parseLong(productRow);
            if (productId > 0L) {
                ids.add(productId);
            }
        }
        return ids;
    }

    public static final class RewardGroup {
        private final long chance;
        private final List<Long> productIds;

        private RewardGroup(long chance, List<Long> productIds) {
            this.chance = chance;
            this.productIds = productIds;
        }

        public long chance() {
            return chance;
        }

        public List<Long> productIds() {
            return productIds;
        }
    }
}
