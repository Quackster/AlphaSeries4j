package com.alphaseries.game.room;

public record RoomUserTargetRow(
    long roomUserIndex,
    long userId,
    long socketIndex
) {
}
