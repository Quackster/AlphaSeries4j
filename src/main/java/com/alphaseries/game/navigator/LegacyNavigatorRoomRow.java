package com.alphaseries.game.navigator;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public record LegacyNavigatorRoomRow(
    long roomId,
    String roomName,
    String ownerName,
    String doorStatus,
    long visitorsNow,
    long visitorsMax,
    String description,
    long hasTrading,
    long roomRate,
    long categoryId,
    String icon,
    String tagOne,
    String tagTwo,
    long allowOtherPets,
    long staffPicked
) {
    public static LegacyNavigatorRoomRow fromLegacyFields(String[] fields) {
        return new LegacyNavigatorRoomRow(
            number(fields, 0),
            field(fields, 1),
            field(fields, 2),
            field(fields, 3),
            number(fields, 4),
            number(fields, 5),
            field(fields, 6),
            number(fields, 7),
            number(fields, 9),
            number(fields, 10),
            field(fields, 11),
            field(fields, 12),
            field(fields, 13),
            number(fields, 14),
            number(fields, 15));
    }

    public static LegacyNavigatorRoomRow fromLegacy(String rowText) {
        return fromLegacyFields(StringUtils.text(rowText).split("\t", -1));
    }

    public static List<LegacyNavigatorRoomRow> listFromLegacy(String rowText) {
        List<LegacyNavigatorRoomRow> rows = new ArrayList<>();
        for (String row : StringUtils.text(rowText).split("\r", -1)) {
            if (!row.isEmpty()) {
                rows.add(fromLegacy(row));
            }
        }
        return rows;
    }

    private static String field(String[] fields, int index) {
        return StringUtils.field(fields, index);
    }

    private static long number(String[] fields, int index) {
        return NumberUtils.parseLong(field(fields, index));
    }
}
