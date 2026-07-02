package com.alphaseries.game.messenger;

import java.util.List;

public record RemovedFriendships(String callerPayload, List<Long> targetUserIds, String notificationPayload) {
    public RemovedFriendships {
        callerPayload = callerPayload == null ? "" : callerPayload;
        targetUserIds = targetUserIds == null ? List.of() : List.copyOf(targetUserIds);
        notificationPayload = notificationPayload == null ? "" : notificationPayload;
    }

    public boolean valid() {
        return !callerPayload.isEmpty();
    }
}
