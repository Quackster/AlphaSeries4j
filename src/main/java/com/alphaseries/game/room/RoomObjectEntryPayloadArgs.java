package com.alphaseries.game.room;

public record RoomObjectEntryPayloadArgs(
    long entityId,
    String displayName,
    String figure,
    String gender,
    long roomUserIndex,
    long positionX,
    long positionY,
    String positionZ,
    long objectType
) {
}
