package com.alphaseries.game.room;

import com.alphaseries.util.StringUtils;

public record RoomUserEntryPayloadArgs(
    String userId,
    String userName,
    String figure,
    String motto,
    String gender,
    String roomUserIndex,
    String positionX,
    String positionY,
    String positionZ,
    String firstState,
    String secondState
) {
    public static RoomUserEntryPayloadArgs fromLegacyArgs(Object... args) {
        String[] values = values(args, 11);
        if (args != null && args.length == 1 && values[1].isEmpty()) {
            values[5] = values[0];
        }
        return new RoomUserEntryPayloadArgs(
            values[0],
            values[1],
            values[2],
            values[3],
            values[4],
            values[5],
            values[6],
            values[7],
            values[8],
            values[9],
            values[10]);
    }

    static String[] values(Object[] args, int fieldCount) {
        String[] values = new String[fieldCount];
        for (int i = 0; i < values.length; i++) {
            values[i] = "";
        }
        if (args == null || args.length == 0) {
            return values;
        }
        if (args.length == 1) {
            String recordText = StringUtils.text(args[0]);
            if (recordText.indexOf('\t') >= 0) {
                String[] fields = recordText.split("\t", -1);
                for (int i = 0; i < values.length; i++) {
                    values[i] = StringUtils.field(fields, i);
                }
            } else {
                values[0] = recordText;
            }
            return values;
        }
        for (int i = 0; i < values.length && i < args.length; i++) {
            values[i] = StringUtils.text(args[i]);
        }
        return values;
    }
}
