package com.alphaseries.game.trade;

public record TradeInteractionRequestAction(
    long targetSocketIndex,
    String sourcePayload,
    String targetPayload
) {
    public TradeInteractionRequestAction {
        sourcePayload = sourcePayload == null ? "" : sourcePayload;
        targetPayload = targetPayload == null ? "" : targetPayload;
    }

    public boolean valid() {
        return targetSocketIndex > 0L && (!sourcePayload.isEmpty() || !targetPayload.isEmpty());
    }
}
