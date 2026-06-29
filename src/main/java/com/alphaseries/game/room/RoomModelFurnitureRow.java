package com.alphaseries.game.room;

public record RoomModelFurnitureRow(
    long productId,
    long sourceId,
    String spriteId,
    long positionX,
    long positionY,
    long positionZ,
    String action,
    long rotation,
    long actionHeight
) {
}
