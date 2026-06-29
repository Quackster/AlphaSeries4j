package com.alphaseries.game.user;

public record UserGroupRow(
    String name,
    String description,
    String badgeId,
    long roomId
) {
}
