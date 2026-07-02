package com.alphaseries.game.room;

public record RoomEventPayload(
    long categoryId,
    String categoryName,
    String eventName,
    String eventDescription,
    String tagOne,
    String tagTwo
) {
}
