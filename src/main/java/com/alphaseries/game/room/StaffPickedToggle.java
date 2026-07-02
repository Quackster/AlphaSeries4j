package com.alphaseries.game.room;

public record StaffPickedToggle(long roomId, long state) {
    public static StaffPickedToggle empty() {
        return new StaffPickedToggle(0L, 0L);
    }

    public boolean changed() {
        return roomId > 0L;
    }
}
