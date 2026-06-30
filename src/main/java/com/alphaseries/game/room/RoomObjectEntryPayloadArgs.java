package com.alphaseries.game.room;

public record RoomObjectEntryPayloadArgs(
    String entityId,
    String displayName,
    String figure,
    String gender,
    String roomUserIndex,
    String positionX,
    String positionY,
    String positionZ,
    String objectType
) {
}
