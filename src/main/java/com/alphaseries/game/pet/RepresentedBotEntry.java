package com.alphaseries.game.pet;

import com.alphaseries.util.NumberUtils;
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

    public static RepresentedBotEntry fromLegacy(Object fieldSource) {
        String[] fields = fieldSource instanceof String[] values
            ? values
            : StringUtils.text(fieldSource).split("\t", -1);
        return new RepresentedBotEntry(
            number(fields, 0),
            field(fields, 1),
            field(fields, 2),
            field(fields, 3),
            field(fields, 4),
            number(fields, 5),
            number(fields, 6),
            field(fields, 7),
            number(fields, 8),
            field(fields, 9),
            number(fields, 11),
            number(fields, 12),
            field(fields, 13),
            field(fields, 14),
            number(fields, 15),
            number(fields, 16));
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

    private static String field(String[] fields, int index) {
        return StringUtils.field(fields, index);
    }

    private static long number(String[] fields, int index) {
        return NumberUtils.parseLong(field(fields, index));
    }
}
