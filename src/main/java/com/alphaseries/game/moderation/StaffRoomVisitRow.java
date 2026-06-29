package com.alphaseries.game.moderation;

public record StaffRoomVisitRow(
    long modelType,
    long roomId,
    String roomName,
    long hour,
    long minute
) {
}
