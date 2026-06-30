package com.alphaseries.game.messenger;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public record MessengerFriend(
    long userId,
    String userName,
    String motto,
    String figure,
    long level,
    long socketIndex,
    String lastOnline
) {
    public static MessengerFriend fromLegacySummaryRow(String rowText) {
        String[] fields = StringUtils.text(rowText).split("\t", -1);
        if (fields.length < 7) {
            return null;
        }
        return new MessengerFriend(
            NumberUtils.parseLong(fields[0]),
            fields[1],
            fields[2],
            fields[3],
            NumberUtils.parseLong(fields[4]),
            NumberUtils.parseLong(fields[5]),
            fields[6]);
    }

    public static MessengerFriend fromLegacyListRow(String rowText) {
        String[] fields = StringUtils.text(rowText).split("\t", -1);
        if (fields.length < 7) {
            return null;
        }
        return new MessengerFriend(
            NumberUtils.parseLong(fields[0]),
            fields[1],
            fields[4],
            fields[3],
            NumberUtils.parseLong(fields[5]),
            NumberUtils.parseLong(fields[2]),
            fields[6]);
    }

    public static List<MessengerFriend> listFromLegacyListRows(String rowText) {
        List<MessengerFriend> friends = new ArrayList<>();
        for (String row : StringUtils.text(rowText).split("\r", -1)) {
            if (!row.isEmpty()) {
                MessengerFriend friend = fromLegacyListRow(row);
                if (friend != null) {
                    friends.add(friend);
                }
            }
        }
        return friends;
    }
}
