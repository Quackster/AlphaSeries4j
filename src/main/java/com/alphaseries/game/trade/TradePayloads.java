package com.alphaseries.game.trade;

import com.alphaseries.game.inventory.InventoryMessagePayloads;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.NumberUtils;

import java.util.ArrayList;
import java.util.List;

public final class TradePayloads {
    private TradePayloads() {
    }

    public record ItemPayload(long itemCount, String payload) {
        public ItemPayload {
            payload = payload == null ? "" : payload;
        }
    }

    public static List<RepresentedTradeOffer> storeOffer(
        List<RepresentedTradeOffer> tradeOffers,
        long socketIndex,
        long furnitureId,
        long productId,
        String signText,
        long secondaryValue
    ) {
        if (socketIndex <= 0L || furnitureId <= 0L || productId <= 0L) {
            return tradeOffers == null ? List.of() : new ArrayList<>(tradeOffers);
        }
        RepresentedTradeOffer newOffer = RepresentedTradeOffer.stored(
            socketIndex,
            furnitureId,
            productId,
            signText,
            secondaryValue);
        List<RepresentedTradeOffer> rebuilt = new ArrayList<>();
        boolean replacedExisting = false;
        for (RepresentedTradeOffer offer : tradeOffers == null ? List.<RepresentedTradeOffer>of() : tradeOffers) {
            if (offer.socketIndex() == socketIndex && offer.furnitureId() == furnitureId) {
                rebuilt.add(newOffer);
                replacedExisting = true;
            } else {
                rebuilt.add(offer);
            }
        }
        if (!replacedExisting) {
            rebuilt.add(newOffer);
        }
        return rebuilt;
    }

    public static List<RepresentedTradeOffer> removeOffer(
        List<RepresentedTradeOffer> tradeOffers,
        long socketIndex,
        long furnitureId
    ) {
        if (socketIndex <= 0L || tradeOffers == null || tradeOffers.isEmpty()) {
            return tradeOffers == null ? List.of() : new ArrayList<>(tradeOffers);
        }
        List<RepresentedTradeOffer> rebuilt = new ArrayList<>();
        for (RepresentedTradeOffer offer : tradeOffers) {
            if (offer.socketIndex() != socketIndex || (furnitureId > 0L && offer.furnitureId() != furnitureId)) {
                rebuilt.add(offer);
            }
        }
        return rebuilt;
    }

    public static List<Long> furnitureIds(List<RepresentedTradeOffer> tradeOffers, long socketIndex) {
        if (socketIndex <= 0L || tradeOffers == null || tradeOffers.isEmpty()) {
            return List.of();
        }
        List<Long> furnitureIds = new ArrayList<>();
        for (RepresentedTradeOffer offer : tradeOffers) {
            if (offer.socketIndex() == socketIndex && offer.furnitureId() > 0L) {
                furnitureIds.add(offer.furnitureId());
            }
        }
        return List.copyOf(furnitureIds);
    }

    public static String logItems(List<RepresentedTradeOffer> tradeOffers, long socketIndex) {
        if (socketIndex <= 0L || tradeOffers == null || tradeOffers.isEmpty()) {
            return "";
        }
        List<String> logItems = new ArrayList<>();
        for (RepresentedTradeOffer offer : tradeOffers) {
            if (offer.socketIndex() == socketIndex && offer.furnitureId() > 0L) {
                logItems.add(offer.furnitureId() + ":" + offer.productId());
            }
        }
        return String.join("\1", logItems);
    }

    public static ItemPayload itemPayload(List<RepresentedTradeOffer> tradeOffers, long socketIndex) {
        if (socketIndex <= 0L || tradeOffers == null || tradeOffers.isEmpty()) {
            return new ItemPayload(0L, "");
        }
        long itemCount = 0L;
        PacketBuilder payload = PacketBuilder.create();
        for (RepresentedTradeOffer offer : tradeOffers) {
            if (offer.socketIndex() == socketIndex) {
                payload.appendRaw(InventoryMessagePayloads.item(
                    offer.furnitureId(),
                    offer.productId(),
                    offer.signText(),
                    offer.secondaryValue()));
                itemCount++;
            }
        }
        return new ItemPayload(itemCount, payload.build());
    }

    public static String offerPayload(
        List<RepresentedTradeOffer> tradeOffers,
        long sourceSocketIndex,
        long targetSocketIndex,
        String sourceUserId,
        String targetUserId
    ) {
        if (sourceSocketIndex <= 0L || targetSocketIndex <= 0L) {
            return "";
        }
        ItemPayload sourceItems = itemPayload(tradeOffers, sourceSocketIndex);
        ItemPayload targetItems = itemPayload(tradeOffers, targetSocketIndex);
        return confirmation(
            NumberUtils.parseLong(sourceUserId),
            NumberUtils.parseLong(targetUserId),
            sourceItems.itemCount(),
            sourceItems.payload(),
            targetItems.itemCount(),
            targetItems.payload());
    }

    public static String confirmation(
        long sourceUserId,
        long targetUserId,
        long sourceItemCount,
        String sourceItemPayload,
        long targetItemCount,
        String targetItemPayload
    ) {
        return PacketBuilder.message("Al")
            .appendInt(sourceUserId)
            .appendInt(targetUserId)
            .appendInt(sourceItemCount)
            .appendRaw(sourceItemPayload)
            .appendInt(targetItemCount)
            .appendRaw(targetItemPayload)
            .build();
    }
}
