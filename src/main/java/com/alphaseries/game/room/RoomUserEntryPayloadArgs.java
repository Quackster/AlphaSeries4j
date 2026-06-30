package com.alphaseries.game.room;

public record RoomUserEntryPayloadArgs(
    String userId,
    String userName,
    String figure,
    String motto,
    String gender,
    String roomUserIndex,
    String positionX,
    String positionY,
    String positionZ,
    String firstState,
    String secondState
) {
}
