package com.alphaseries.game.messenger;

import java.util.List;

public record AcceptedFriendRequests(String callerPayload, long acceptedCount, List<MessengerNotification> notifications) {
    public AcceptedFriendRequests {
        callerPayload = callerPayload == null ? "" : callerPayload;
        notifications = notifications == null ? List.of() : List.copyOf(notifications);
    }

    public boolean valid() {
        return acceptedCount > 0L && !callerPayload.isEmpty();
    }

    public List<MessengerNotification> deliveryPayloads() {
        return notifications.stream()
            .filter(MessengerNotification::valid)
            .toList();
    }
}
