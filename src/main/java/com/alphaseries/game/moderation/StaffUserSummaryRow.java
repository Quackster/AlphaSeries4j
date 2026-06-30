package com.alphaseries.game.moderation;

public record StaffUserSummaryRow(
    long userId,
    String userName,
    long createdMinutes,
    long lastOnlineMinutes,
    long socketIndex
) {
}
