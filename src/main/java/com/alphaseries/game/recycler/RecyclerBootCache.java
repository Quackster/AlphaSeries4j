package com.alphaseries.game.recycler;

import com.alphaseries.dao.mysql.RecyclerDao;
import com.alphaseries.db.Database;
import com.alphaseries.db.MySQL;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.NumberUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class RecyclerBootCache {
    private RecyclerBootCache() {
    }

    public record RecyclerCache(
        RecyclerSettings.StatusPayload statusPayload,
        long groupCount,
        List<RecyclerSettings.RewardGroup> rewardGroups,
        Map<Long, String> productListByGroupIndex,
        Map<Long, Long> chanceByGroupIndex
    ) {
        public RecyclerCache {
            statusPayload = statusPayload == null
                ? RecyclerSettings.StatusPayload.fromPayload("")
                : statusPayload;
            rewardGroups = rewardGroups == null ? List.of() : List.copyOf(rewardGroups);
            productListByGroupIndex = productListByGroupIndex == null
                ? Map.of() : Map.copyOf(productListByGroupIndex);
            chanceByGroupIndex = chanceByGroupIndex == null ? Map.of() : Map.copyOf(chanceByGroupIndex);
        }
    }

    /**
     * Original function: Proc_1_0_6BA9D0.
     */
    public static void loadRecyclerRewardsCache() {
        List<RecyclerSettings.RewardGroup> rewardGroups = List.of();
        RecyclerDao recycler = recyclerDao();
        if (recycler != null) {
            try {
                List<RecyclerSettings.RewardGroup> loadedGroups = new ArrayList<RecyclerSettings.RewardGroup>();
                List<Long> chances = recycler.recyclerChances();
                for (Long chanceValue : chances) {
                    long chance = chanceValue == null ? 0L : chanceValue.longValue();
                    if (chance != 0L) {
                        loadedGroups.add(new RecyclerSettings.RewardGroup(chance,
                            recycler.rewardProductIdsByChance(chance)));
                    }
                }
                rewardGroups = List.copyOf(loadedGroups);
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        RecyclerCache cache = buildRecyclerCache(rewardGroups);
        RecyclerState.instance().setStatusPayload(cache.statusPayload());
        RecyclerState.instance().setRewards(cache.rewardGroups());
    }

    /**
     * Original function: Proc_1_0_6BA9D0.
     */
    public static RecyclerCache buildRecyclerCache(List<RecyclerSettings.RewardGroup> rewardGroups) {
        PacketBuilder payload = PacketBuilder.create();
        List<RecyclerSettings.RewardGroup> groups = new ArrayList<RecyclerSettings.RewardGroup>();
        Map<Long, String> productListByGroupIndex = new LinkedHashMap<Long, String>();
        Map<Long, Long> chanceByGroupIndex = new LinkedHashMap<Long, Long>();
        long groupCount = 0L;
        for (RecyclerSettings.RewardGroup rewardGroup : rewardGroups == null
            ? List.<RecyclerSettings.RewardGroup>of()
            : rewardGroups) {
            if (groupCount > 49L) {
                break;
            }
            if (rewardGroup != null) {
                long chanceValue = NumberUtils.parseLong(rewardGroup.chance());
                long groupIndex = groupCount;
                chanceByGroupIndex.put(groupIndex, chanceValue);

                long productCount = 0L;
                PacketBuilder productList = PacketBuilder.create();
                PacketBuilder groupPayload = PacketBuilder.create();
                List<Long> productIds = new ArrayList<Long>();
                for (Long productIdValue : rewardGroup.productIds()) {
                    long productId = NumberUtils.parseLong(productIdValue);
                    if (productId > 0L) {
                        productIds.add(productId);
                        productList.appendString(productId);
                        groupPayload.appendInt(productId);
                        productCount++;
                    }
                }

                productListByGroupIndex.put(groupIndex, productList.build());
                groups.add(new RecyclerSettings.RewardGroup(chanceValue, productIds));
                payload
                    .appendInt(chanceValue)
                    .appendInt(productCount)
                    .appendRaw(groupPayload.build());
                groupCount++;
            }
        }
        return new RecyclerCache(
            RecyclerSettings.StatusPayload.fromPayload(
                PacketBuilder.create().appendInt(groupCount).appendRaw(payload.build()).build()),
            groupCount,
            groups,
            productListByGroupIndex,
            chanceByGroupIndex);
    }

    private static RecyclerDao recyclerDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new RecyclerDao(database);
    }
}
