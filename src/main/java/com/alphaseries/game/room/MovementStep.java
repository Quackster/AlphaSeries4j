package com.alphaseries.game.room;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public record MovementStep(long positionX, long positionY, long directionValue, long movingValue) {
    public static MovementStep fromLegacy(String movementText) {
        String[] fields = StringUtils.text(movementText).split("\0", -1);
        return new MovementStep(number(fields, 0), number(fields, 1), number(fields, 2), number(fields, 3));
    }

    private static long number(String[] fields, int index) {
        return index >= 0 && index < fields.length ? NumberUtils.parseLong(fields[index]) : 0L;
    }
}
