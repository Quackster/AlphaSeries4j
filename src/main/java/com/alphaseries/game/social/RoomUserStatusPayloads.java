package com.alphaseries.game.social;

public record RoomUserStatusPayloads(
    String callerPayload,
    String targetPayload
) {
    public RoomUserStatusPayloads {
        callerPayload = callerPayload == null ? "" : callerPayload;
        targetPayload = targetPayload == null ? "" : targetPayload;
    }

    public boolean hasCallerPayload() {
        return !callerPayload.isEmpty();
    }

    public boolean hasTargetPayload() {
        return !targetPayload.isEmpty();
    }
}
