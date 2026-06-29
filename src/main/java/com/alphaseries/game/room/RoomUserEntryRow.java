package com.alphaseries.game.room;

public record RoomUserEntryRow(
    long userId,
    String name,
    String figure,
    String motto,
    String gender,
    long positionX,
    long positionY,
    long roomSlot
) {
}
