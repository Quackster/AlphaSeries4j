package com.alphaseries.game.room;

import com.alphaseries.util.StringUtils;

public final class RoomPortalSettings {
    private final String warpSpaceRows;
    private final String specialGateRows;

    private RoomPortalSettings(String warpSpaceRows, String specialGateRows) {
        this.warpSpaceRows = StringUtils.text(warpSpaceRows);
        this.specialGateRows = StringUtils.text(specialGateRows);
    }

    public static RoomPortalSettings fromLegacy(String warpSpaceRows, String specialGateRows) {
        return new RoomPortalSettings(warpSpaceRows, specialGateRows);
    }

    public String warpSpaceRows() {
        return warpSpaceRows;
    }

    public String specialGateRows() {
        return specialGateRows;
    }
}
