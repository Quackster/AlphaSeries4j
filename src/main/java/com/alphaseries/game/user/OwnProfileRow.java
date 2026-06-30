package com.alphaseries.game.user;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public record OwnProfileRow(
    long userId,
    String name,
    String motto,
    String gender,
    long respectAmount,
    long scratchAmount
) {
    public static OwnProfileRow fromLegacy(String userRow) {
        String[] fields = StringUtils.text(userRow).split("\t", -1);
        if (fields.length < 6) {
            return null;
        }
        return new OwnProfileRow(
            NumberUtils.parseLong(StringUtils.field(fields, 0)),
            StringUtils.field(fields, 1),
            StringUtils.field(fields, 2),
            StringUtils.field(fields, 3),
            NumberUtils.parseLong(StringUtils.field(fields, 4)),
            NumberUtils.parseLong(StringUtils.field(fields, 5)));
    }
}
