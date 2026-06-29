package com.alphaseries.game.room;

public record RoomOccupantRow(
    long roomUserIndex,
    long userId,
    String name,
    String figure,
    String motto,
    String gender,
    long positionX,
    long positionY,
    long socketIndex
) {
}
