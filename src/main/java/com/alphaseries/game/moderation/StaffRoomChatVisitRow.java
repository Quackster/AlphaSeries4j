package com.alphaseries.game.moderation;

public record StaffRoomChatVisitRow(
    long modelType,
    long roomId,
    String roomName,
    long timestampEnter,
    long timestampLeft
) {
}
