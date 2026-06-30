package com.alphaseries.game.social;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public record BadgeRow(
    String badgeId,
    long slot,
    long rowId
) {
    public static BadgeRow fromLegacy(String rowText) {
        String[] fields = StringUtils.text(rowText).split("\t", -1);
        if (fields.length < 1) {
            return null;
        }
        return new BadgeRow(
            StringUtils.field(fields, 0),
            NumberUtils.parseLong(StringUtils.field(fields, 1)),
            NumberUtils.parseLong(StringUtils.field(fields, 2)));
    }

    public static List<BadgeRow> listFromLegacy(String rowsText) {
        List<BadgeRow> rows = new ArrayList<>();
        for (String row : StringUtils.text(rowsText).split("\r", -1)) {
            if (!row.isEmpty()) {
                BadgeRow badge = fromLegacy(row);
                if (badge != null) {
                    rows.add(badge);
                }
            }
        }
        return rows;
    }
}
