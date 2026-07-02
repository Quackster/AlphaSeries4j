package com.alphaseries.game.room;

public record RoomUserEntryPayloadArgs(
    long userId,
    String userName,
    String figure,
    String motto,
    String gender,
    long roomUserIndex,
    long positionX,
    long positionY,
    String positionZ,
    long firstState,
    long secondState
) {
}
