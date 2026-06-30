package com.alphaseries.game.navigator;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.Arrays;
import java.util.List;

public record OfficialNavigatorItem(
    long typeId,
    long styleId,
    long iconId,
    String caption,
    String captionTwo,
    String captionThree,
    String unusedSlot,
    String roomId,
    String roomName,
    String ownerName,
    String doorStatus,
    String visitorsNow,
    String visitorsMax,
    String description,
    String hasTrading,
    String unusedTradingSlot,
    String roomRate,
    String categoryId,
    String roomIcon,
    String tagOne,
    String tagTwo,
    String allowOtherPets,
    String modelName,
    String requiredFiles,
    String modelVisitorsMax,
    long parentId,
    long officialId,
    long requiredLevel,
    boolean requiredLevelPresent
) {
    public static OfficialNavigatorItem fromLegacyFields(String[] fields) {
        if (fields == null || fields.length < 27) {
            return null;
        }
        return new OfficialNavigatorItem(
            number(fields, 0),
            number(fields, 1),
            number(fields, 2),
            field(fields, 3),
            field(fields, 4),
            field(fields, 5),
            field(fields, 6),
            field(fields, 7),
            field(fields, 8),
            field(fields, 9),
            field(fields, 10),
            field(fields, 11),
            field(fields, 12),
            field(fields, 13),
            field(fields, 14),
            field(fields, 15),
            field(fields, 16),
            field(fields, 17),
            field(fields, 18),
            field(fields, 19),
            field(fields, 20),
            field(fields, 21),
            field(fields, 22),
            field(fields, 23),
            field(fields, 24),
            number(fields, 25),
            number(fields, 26),
            number(fields, 27),
            fields.length >= 28);
    }

    public List<String> textFields() {
        return Arrays.asList(
            caption,
            captionTwo,
            captionThree,
            unusedSlot,
            roomId,
            roomName,
            ownerName,
            doorStatus,
            visitorsNow,
            visitorsMax,
            description,
            hasTrading,
            unusedTradingSlot,
            roomRate,
            categoryId,
            roomIcon,
            tagOne,
            tagTwo,
            allowOtherPets,
            modelName,
            requiredFiles,
            modelVisitorsMax);
    }

    private static String field(String[] fields, int index) {
        return StringUtils.field(fields, index);
    }

    private static long number(String[] fields, int index) {
        return NumberUtils.parseLong(field(fields, index));
    }
}
