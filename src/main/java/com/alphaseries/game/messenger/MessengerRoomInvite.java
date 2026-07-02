package com.alphaseries.game.messenger;

import com.alphaseries.util.StringUtils;

import java.util.List;

public record MessengerRoomInvite(String payload, List<MessengerNotification> notifications) {
    public MessengerRoomInvite {
        payload = StringUtils.text(payload);
        notifications = notifications == null ? List.of() : List.copyOf(notifications);
    }

    public List<MessengerNotification> deliveryPayloads() {
        return notifications.stream()
            .filter(MessengerNotification::valid)
            .toList();
    }
}
