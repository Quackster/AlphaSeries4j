package com.alphaseries.game.pet;

import com.alphaseries.util.StringUtils;

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

    public String recordText(long roomSlot) {
        return roomSlot + "\2" + botId + "\2"
            + StringUtils.text(name) + '\2' + StringUtils.text(motto) + '\2'
            + StringUtils.text(speech) + '\2' + StringUtils.text(responses) + '\2'
            + positionX + "\2" + positionY + "\2"
            + StringUtils.text(positionZ) + '\2' + positionR + '\2'
            + StringUtils.text(figure) + '\2' + handleId + '\2'
            + handleActionId + '\2' + StringUtils.text(cacheAction) + '\2'
            + StringUtils.text(speechSubmit) + '\2' + allowWalk + '\2'
            + maxFieldsAway;
    }
}
