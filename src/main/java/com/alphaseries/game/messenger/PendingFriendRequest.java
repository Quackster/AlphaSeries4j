package com.alphaseries.game.messenger;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public record PendingFriendRequest(long userId, String userName) {
    public static PendingFriendRequest fromLegacy(String rowText) {
        String[] fields = StringUtils.text(rowText).split("\t", -1);
        return new PendingFriendRequest(
            NumberUtils.parseLong(StringUtils.field(fields, 0)),
            StringUtils.field(fields, 1));
    }

    public static List<PendingFriendRequest> listFromLegacy(String rowText) {
        List<PendingFriendRequest> requests = new ArrayList<>();
        for (String row : StringUtils.text(rowText).split("\r", -1)) {
            if (!row.isEmpty()) {
                requests.add(fromLegacy(row));
            }
        }
        return requests;
    }
}
