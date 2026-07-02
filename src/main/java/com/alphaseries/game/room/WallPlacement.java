package com.alphaseries.game.room;

public record WallPlacement(long wallX, long wallY, long localX, long localY, boolean valid) {
    public static WallPlacement empty() {
        return new WallPlacement(0L, 0L, 0L, 0L, false);
    }

    public static WallPlacement valid(long wallX, long wallY, long localX, long localY) {
        return new WallPlacement(wallX, wallY, localX, localY, true);
    }
}
