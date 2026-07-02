package com.alphaseries.game.messenger;

public record MessengerNotification(long socketIndex, String payload) {
    public MessengerNotification {
        payload = payload == null ? "" : payload;
    }

    public boolean valid() {
        return socketIndex > 0L && !payload.isEmpty();
    }
}
