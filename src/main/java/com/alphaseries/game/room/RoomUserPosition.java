package com.alphaseries.game.room;

import com.alphaseries.util.NumberUtils;

public record RoomUserPosition(long positionX, long positionY, boolean found) {
    public static RoomUserPosition absent() {
        return new RoomUserPosition(0L, 0L, false);
    }

    public static RoomUserPosition from(RepresentedRoomCache.Position position) {
        if (position == null || !position.found) {
            return absent();
        }
        return new RoomUserPosition(position.positionX, position.positionY, true);
    }

    public static RoomUserPosition fromHandlerArgs(Object[] args) {
        if (args == null || args.length < 5) {
            return absent();
        }
        return new RoomUserPosition(NumberUtils.parseLong(args[3]), NumberUtils.parseLong(args[4]), true);
    }
}
