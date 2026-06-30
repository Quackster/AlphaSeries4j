package com.alphaseries.game.user;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public record UserGroupRow(
    String name,
    String description,
    String badgeId,
    long roomId
) {
    public static UserGroupRow fromLegacy(String groupRow) {
        String[] fields = StringUtils.text(groupRow).split("\t", -1);
        return new UserGroupRow(
            StringUtils.field(fields, 0),
            StringUtils.field(fields, 1),
            StringUtils.field(fields, 2),
            NumberUtils.parseLong(StringUtils.field(fields, 3)));
    }
}
