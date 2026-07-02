package com.alphaseries.game.recycler;

import com.alphaseries.config.AppConfigState;
import com.alphaseries.dao.mysql.CatalogDao;
import com.alphaseries.dao.mysql.FurnitureDao;
import com.alphaseries.dao.mysql.RecyclerDao;
import com.alphaseries.game.inventory.InventoryMessagePayloads;
import com.alphaseries.messages.outgoing.RecyclerPayloads;

import java.util.ArrayList;
import java.util.List;

public final class RecyclerLookups {
    private RecyclerLookups() {
    }

    public record SubmitResult(String rewardPayload, List<Long> removedFurnitureIds, DeliveryPayloads deliveryPayloads) {
        public SubmitResult {
            rewardPayload = rewardPayload == null ? "" : rewardPayload;
            removedFurnitureIds = removedFurnitureIds == null ? List.of() : List.copyOf(removedFurnitureIds);
            deliveryPayloads = deliveryPayloads == null
                ? new DeliveryPayloads(removedFurnitureIds, rewardPayload)
                : deliveryPayloads;
        }

        public SubmitResult(String rewardPayload, List<Long> removedFurnitureIds) {
            this(rewardPayload, removedFurnitureIds, new DeliveryPayloads(removedFurnitureIds, rewardPayload));
        }

        public static SubmitResult empty() {
            return new SubmitResult("", List.of(), DeliveryPayloads.empty());
        }

        public boolean valid() {
            return !rewardPayload.isEmpty();
        }
    }

    public record DeliveryPayloads(List<Long> removedFurnitureIds, String rewardPayload) {
        public DeliveryPayloads {
            removedFurnitureIds = removedFurnitureIds == null ? List.of() : List.copyOf(removedFurnitureIds);
            rewardPayload = rewardPayload == null ? "" : rewardPayload;
        }

        public static DeliveryPayloads empty() {
            return new DeliveryPayloads(List.of(), "");
        }

        public List<String> payloads() {
            List<String> payloads = new ArrayList<>();
            for (long selectedFurnitureId : removedFurnitureIds) {
                if (selectedFurnitureId > 0L) {
                    payloads.add(InventoryMessagePayloads.remove(selectedFurnitureId));
                }
            }
            if (!rewardPayload.isEmpty()) {
                payloads.add(rewardPayload);
            }
            return List.copyOf(payloads);
        }
    }

    /**
     * Original function: Proc_6_202_7D6760.
     */
    public static SubmitResult submitItems(
        long userId,
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
            if (userId <= 0L) {
                return SubmitResult.empty();
            }
            if (selection == null || !selection.valid()) {
                return SubmitResult.empty();
            }
            long validCount = furniture.recyclableInventoryCount(userId, selection.selectedItemIds());
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
                userId,
                effectiveSettings.boxProductId(),
                RecyclerRewards.rewardSign(),
                rewardDestinationId);
            furniture.clearRecyclerItems(userId, selection.selectedItemIds());
            furniture.insertRecyclerLog(userId, selection.selectedItemIds(), rewardProductId);
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
