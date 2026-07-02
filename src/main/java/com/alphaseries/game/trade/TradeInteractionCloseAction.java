package com.alphaseries.game.trade;

public record TradeInteractionCloseAction(
    long targetSocketIndex,
    String payload
) {
    public TradeInteractionCloseAction {
        payload = payload == null ? "" : payload;
    }

    public boolean valid() {
        return targetSocketIndex > 0L && !payload.isEmpty();
    }
}
