package com.alphaseries.game.trade;

public record TradeInteractionStateAction(
    long targetSocketIndex,
    long interactionState,
    String sourcePayload,
    String targetPayload,
    String completionPayload
) {
    public TradeInteractionStateAction {
        sourcePayload = sourcePayload == null ? "" : sourcePayload;
        targetPayload = targetPayload == null ? "" : targetPayload;
        completionPayload = completionPayload == null ? "" : completionPayload;
    }

    public boolean valid() {
        return targetSocketIndex > 0L && !sourcePayload.isEmpty() && !targetPayload.isEmpty();
    }
}
