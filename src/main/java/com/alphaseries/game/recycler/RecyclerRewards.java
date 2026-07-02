package com.alphaseries.game.recycler;

import com.alphaseries.dao.mysql.RecyclerDao;
import com.alphaseries.util.RandomUtils;

import java.time.LocalDateTime;
import java.util.List;

public final class RecyclerRewards {
    private RecyclerRewards() {
    }

    public static long rewardProductFromGroups(RecyclerSettings recyclerSettings) {
        RecyclerSettings settings = recyclerSettings == null ? RecyclerSettings.empty() : recyclerSettings;
        if (!settings.hasRewardGroups()) {
            return 0L;
        }
        for (RecyclerSettings.RewardGroup rewardGroup : settings.rewardGroups()) {
            long chance = rewardGroup.chance();
            if (chance > 0L && RandomUtils.longInclusive(1, chance) == 1L) {
                long productId = randomProductFromList(rewardGroup.productIds());
                if (productId > 0L) {
                    return productId;
                }
            }
        }
        for (RecyclerSettings.RewardGroup rewardGroup : settings.rewardGroups()) {
            long productId = randomProductFromList(rewardGroup.productIds());
            if (productId > 0L) {
                return productId;
            }
        }
        return 0L;
    }

    public static long representedRewardProduct(RecyclerSettings recyclerSettings, RecyclerDao recycler) {
        try {
            long configuredRewardProduct = rewardProductFromGroups(recyclerSettings);
            if (configuredRewardProduct > 0L) {
                return configuredRewardProduct;
            }
            List<Long> rewardProductIds = recycler == null ? List.of() : recycler.fallbackRewardProductIds();
            return randomProductFromList(rewardProductIds);
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static long randomProductFromList(List<Long> productIds) {
        try {
            if (productIds == null || productIds.isEmpty()) {
                return 0L;
            }
            int selectedIndex = (int) RandomUtils.longInclusive(0, productIds.size() - 1L);
            selectedIndex = Math.max(0, Math.min(selectedIndex, productIds.size() - 1));
            return productIds.get(selectedIndex);
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static String rewardSign() {
        return LocalDateTime.now().toString().replace('T', ' ');
    }
}
