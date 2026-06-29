package com.alphaseries.game.room;

public record RoomUserProfileRow(
    long roomUserIndex,
    String userName,
    String motto,
    long achievementScore,
    String figure
) {
}
