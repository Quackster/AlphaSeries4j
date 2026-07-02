package com.alphaseries.game.pet;

public record RepresentedBotEntry(
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
    public static RepresentedBotEntry from(BotRoomEntryRow row) {
        if (row == null) {
            return null;
        }
        return new RepresentedBotEntry(
            row.botId(),
            row.name(),
            row.motto(),
            row.speech(),
            row.responses(),
            row.positionX(),
            row.positionY(),
            row.positionZ(),
            row.positionR(),
            row.figure(),
            row.handleId(),
            row.handleActionId(),
            row.cacheAction(),
            row.speechSubmit(),
            row.allowWalk(),
            row.maxFieldsAway());
    }

    public static RepresentedBotEntry from(PetPlacementRow row, long positionX, long positionY, String positionZ, long positionR) {
        if (row == null) {
            return null;
        }
        return new RepresentedBotEntry(
            row.petId(),
            row.name(),
            row.motto(),
            row.speech(),
            row.responses(),
            positionX,
            positionY,
            positionZ,
            positionR,
            row.figure(),
            row.handleId(),
            row.handleActionId(),
            row.cacheAction(),
            row.speechSubmit(),
            row.allowWalk(),
            row.maxFieldsAway());
    }

}
