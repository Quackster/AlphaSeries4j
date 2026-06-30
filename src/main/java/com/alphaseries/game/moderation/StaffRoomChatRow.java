package com.alphaseries.game.moderation;

public record StaffRoomChatRow(
    long hour,
    long minute,
    long userId,
    String userName,
    String description
) {
}
