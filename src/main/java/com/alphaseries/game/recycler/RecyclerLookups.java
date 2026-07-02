package com.alphaseries.game.recycler;

import com.alphaseries.config.AppConfigState;
import com.alphaseries.dao.mysql.CatalogDao;
import com.alphaseries.dao.mysql.FurnitureDao;
import com.alphaseries.dao.mysql.RecyclerDao;
import com.alphaseries.game.inventory.InventoryMessagePayloads;
import com.alphaseries.messages.outgoing.RecyclerPayloads;
import com.alphaseries.util.NumberUtils;

import java.util.ArrayList;
import java.util.List;

public final class RecyclerLookups {
    private RecyclerLookups() {
    }

    public record SubmitResult(String rewardPayload, List<Long> removedFurnitureIds, List<String> deliveryPayloads) {
        public SubmitResult {
            rewardPayload = rewardPayload == null ? "" : rewardPayload;
            removedFurnitureIds = removedFurnitureIds == null ? List.of() : List.copyOf(removedFurnitureIds);
            deliveryPayloads = deliveryPayloads == null ? List.of() : List.copyOf(deliveryPayloads);
        }

        public SubmitResult(String rewardPayload, List<Long> removedFurnitureIds) {
            this(rewardPayload, removedFurnitureIds, RecyclerLookups.deliveryPayloads(rewardPayload, removedFurnitureIds));
        }

        public static SubmitResult empty() {
            return new SubmitResult("", List.of(), List.of());
        }

        public boolean valid() {
            return !rewardPayload.isEmpty();
        }
    }

    public static List<String> deliveryPayloads(String rewardPayload, List<Long> removedFurnitureIds) {
        List<String> payloads = new ArrayList<>();
        if (removedFurnitureIds != null) {
            for (long selectedFurnitureId : removedFurnitureIds) {
                if (selectedFurnitureId > 0L) {
                    payloads.add(InventoryMessagePayloads.remove(selectedFurnitureId));
                }
            }
        }
        if (rewardPayload != null && !rewardPayload.isEmpty()) {
            payloads.add(rewardPayload);
        }
        return List.copyOf(payloads);
    }

    /**
     * Original function: Proc_6_202_7D6760.
     */
    public static SubmitResult submitItems(
        String userId,
        RecyclerSelection selection,
        RecyclerSettings settings,
        FurnitureDao furniture,
        CatalogDao catalog,
        RecyclerDao recycler
    ) {
        try {
            if (statusEnabledValue() == 0L || furniture == null) {
                return SubmitResult.empty();
            }
            long userIdValue = NumberUtils.parseLong(userId);
            if (userIdValue <= 0L) {
                return SubmitResult.empty();
            }
            if (selection == null || !selection.valid()) {
                return SubmitResult.empty();
            }
            long validCount = furniture.recyclableInventoryCount(userIdValue, selection.selectedItemIds());
            if (validCount != selection.requestedCount()) {
                return SubmitResult.empty();
            }
            RecyclerSettings effectiveSettings = settings == null ? RecyclerSettings.empty() : settings;
            long rewardProductId = RecyclerRewards.representedRewardProduct(effectiveSettings, recycler);
            if (rewardProductId <= 0L) {
                return SubmitResult.empty();
            }
            long rewardDestinationId = catalog == null ? 0L : catalog.destinationIdByProduct(rewardProductId);
            if (rewardDestinationId <= 0L) {
                rewardDestinationId = rewardProductId;
            }
            furniture.updateRecyclerRewardBox(
                userIdValue,
                effectiveSettings.boxProductId(),
                RecyclerRewards.rewardSign(),
                rewardDestinationId);
            furniture.clearRecyclerItems(userIdValue, selection.selectedItemIds());
            furniture.insertRecyclerLog(userIdValue, selection.selectedItemIds(), rewardProductId);
            return new SubmitResult(RecyclerPayloads.reward(rewardProductId), selection.selectedItemIds());
        } catch (Exception ignored) {
            return SubmitResult.empty();
        }
    }

    /**
     * Original function: Proc_6_203_7D7F80.
     */
    public static String statusPayload() {
        return RecyclerPayloads.status(statusEnabledValue(), 0L);
    }

    private static long statusEnabledValue() {
        return AppConfigState.instance().settingsCache().longValueOrDefault("com.client.catalog.recycler.enabled", 0);
    }
}
