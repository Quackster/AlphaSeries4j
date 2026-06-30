package com.alphaseries.game.moderation;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public record StaffRoomChatRow(
    long hour,
    long minute,
    long userId,
    String userName,
    String description
) {
    public static StaffRoomChatRow fromLegacy(String rowText) {
        String[] fields = StringUtils.text(rowText).split("\t", -1);
        if (fields.length < 5) {
            return null;
        }
        return new StaffRoomChatRow(
            NumberUtils.parseLong(StringUtils.field(fields, 0)),
            NumberUtils.parseLong(StringUtils.field(fields, 1)),
            NumberUtils.parseLong(StringUtils.field(fields, 2)),
            StringUtils.field(fields, 3),
            StringUtils.field(fields, 4));
    }
}
