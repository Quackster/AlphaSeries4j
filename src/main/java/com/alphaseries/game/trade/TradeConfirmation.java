package com.alphaseries.game.trade;

public record TradeConfirmation(
    long targetSocketIndex,
    String payload
) {
    public TradeConfirmation {
        payload = payload == null ? "" : payload;
    }

    public boolean valid() {
        return targetSocketIndex > 0L && !payload.isEmpty();
    }
}
