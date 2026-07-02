package com.alphaseries.game.trade;

public record TradeOfferAction(
    String sourcePayload,
    String targetPayload
) {
    public TradeOfferAction {
        sourcePayload = sourcePayload == null ? "" : sourcePayload;
        targetPayload = targetPayload == null ? "" : targetPayload;
    }

    public boolean valid() {
        return !sourcePayload.isEmpty() || !targetPayload.isEmpty();
    }
}
