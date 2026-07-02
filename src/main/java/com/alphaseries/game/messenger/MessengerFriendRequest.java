package com.alphaseries.game.messenger;

public record MessengerFriendRequest(String callerPayload, long targetUserId, String targetNotificationPayload) {
    public MessengerFriendRequest {
        callerPayload = callerPayload == null ? "" : callerPayload;
        targetNotificationPayload = targetNotificationPayload == null ? "" : targetNotificationPayload;
    }

    public boolean valid() {
        return !callerPayload.isEmpty();
    }

    public boolean hasTargetNotification() {
        return targetUserId > 0L && !targetNotificationPayload.isEmpty();
    }
}
