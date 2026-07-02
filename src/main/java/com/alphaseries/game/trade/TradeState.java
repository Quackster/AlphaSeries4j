package com.alphaseries.game.trade;

import java.util.ArrayList;
import java.util.List;

public final class TradeState {
    private static final TradeState INSTANCE = new TradeState();

    private List<RepresentedInteractionPair> interactionPairs = new ArrayList<>();
    private List<RepresentedTradeOffer> tradeOffers = new ArrayList<>();

    private TradeState() {
    }

    public static TradeState instance() {
        return INSTANCE;
    }

    public synchronized void storeInteractionPair(long sourceSocketIndex, long targetSocketIndex, long interactionState) {
        if (sourceSocketIndex <= 0L || targetSocketIndex <= 0L) {
            return;
        }
        removeInteractionPair(sourceSocketIndex);
        removeInteractionPair(targetSocketIndex);
        interactionPairs.add(RepresentedInteractionPair.stored(sourceSocketIndex, targetSocketIndex, interactionState));
        interactionPairs.add(RepresentedInteractionPair.stored(targetSocketIndex, sourceSocketIndex, interactionState));
    }

    public synchronized void removeInteractionPair(long socketIndex) {
        if (socketIndex <= 0L || interactionPairs.isEmpty()) {
            return;
        }
        List<RepresentedInteractionPair> rebuilt = new ArrayList<>();
        for (RepresentedInteractionPair pair : interactionPairs) {
            if (pair.socketIndex() != socketIndex) {
                rebuilt.add(pair);
            }
        }
        interactionPairs = rebuilt;
        removeTradeOffer(socketIndex, 0L);
    }

    public synchronized int interactionPartner(long socketIndex) {
        if (socketIndex <= 0L || interactionPairs.isEmpty()) {
            return 0;
        }
        for (RepresentedInteractionPair pair : interactionPairs) {
            if (pair.socketIndex() == socketIndex) {
                return (int) pair.partnerSocketIndex();
            }
        }
        return 0;
    }

    public synchronized long interactionState(long socketIndex) {
        if (socketIndex <= 0L || interactionPairs.isEmpty()) {
            return 0L;
        }
        for (RepresentedInteractionPair pair : interactionPairs) {
            if (pair.socketIndex() == socketIndex) {
                return pair.interactionState();
            }
        }
        return 0L;
    }

    public synchronized void storeTradeOffer(
        long socketIndex,
        long furnitureId,
        long productId,
        String signText,
        long secondaryValue
    ) {
        tradeOffers = TradePayloads.storeOffer(tradeOffers, socketIndex, furnitureId, productId, signText, secondaryValue);
    }

    public synchronized void removeTradeOffer(long socketIndex, long furnitureId) {
        tradeOffers = TradePayloads.removeOffer(tradeOffers, socketIndex, furnitureId);
    }

    public synchronized List<Long> tradeOfferFurnitureIds(long socketIndex) {
        return TradePayloads.furnitureIds(tradeOffers, socketIndex);
    }

    public synchronized String tradeOfferLogItems(long socketIndex) {
        return TradePayloads.logItems(tradeOffers, socketIndex);
    }

    public synchronized String tradeOfferPayload(
        long sourceSocketIndex,
        long targetSocketIndex,
        String sourceUserId,
        String targetUserId
    ) {
        return TradePayloads.offerPayload(tradeOffers, sourceSocketIndex, targetSocketIndex, sourceUserId, targetUserId);
    }
}
