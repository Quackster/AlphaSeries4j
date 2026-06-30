package com.alphaseries.game.pet;

public record BotRoomEntryRow(
    long botId,
    String name,
    String motto,
    String speech,
    String responses,
    long positionX,
    long positionY,
    String positionZ,
    long positionR,
    String figure,
    long handleId,
    long handleActionId,
    String cacheAction,
    String speechSubmit,
    long allowWalk,
    long maxFieldsAway
) {
}
