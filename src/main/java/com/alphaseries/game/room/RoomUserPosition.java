package com.alphaseries.game.room;

public record RoomUserPosition(long positionX, long positionY, boolean found) {
    public static RoomUserPosition absent() {
        return new RoomUserPosition(0L, 0L, false);
    }

    public static RoomUserPosition from(RepresentedRoomCache.Position position) {
        if (position == null || !position.found()) {
            return absent();
        }
        return new RoomUserPosition(position.positionX(), position.positionY(), true);
    }

    public static RoomUserPosition fromCoordinates(long positionX, long positionY) {
        return new RoomUserPosition(positionX, positionY, true);
    }
}
