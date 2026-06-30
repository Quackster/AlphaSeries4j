package com.alphaseries.game.room;

public record RoomObjectEntryPayloadArgs(
    String entityId,
    String displayName,
    String figure,
    String gender,
    String roomUserIndex,
    String positionX,
    String positionY,
    String positionZ,
    String objectType
) {
    public static RoomObjectEntryPayloadArgs fromLegacyArgs(Object... args) {
        String[] values = RoomUserEntryPayloadArgs.values(args, 9);
        if (args != null && args.length == 1 && values[1].isEmpty()) {
            values[4] = values[0];
        }
        return new RoomObjectEntryPayloadArgs(
            values[0],
            values[1],
            values[2],
            values[3],
            values[4],
            values[5],
            values[6],
            values[7],
            values[8]);
    }

}
