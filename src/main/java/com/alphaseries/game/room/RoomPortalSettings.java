package com.alphaseries.game.room;

import com.alphaseries.dao.mysql.RoomDao;

import java.util.List;

public final class RoomPortalSettings {
    private final List<RoomDao.WarpSpaceRow> warpSpaces;
    private final List<RoomDao.SpecialGateRow> specialGates;

    private RoomPortalSettings(List<RoomDao.WarpSpaceRow> warpSpaces, List<RoomDao.SpecialGateRow> specialGates) {
        this.warpSpaces = warpSpaces == null ? List.of() : List.copyOf(warpSpaces);
        this.specialGates = specialGates == null ? List.of() : List.copyOf(specialGates);
    }

    public static RoomPortalSettings fromRows(List<RoomDao.WarpSpaceRow> warpSpaces,
            List<RoomDao.SpecialGateRow> specialGates) {
        return new RoomPortalSettings(warpSpaces, specialGates);
    }

    public static RoomPortalSettings empty() {
        return new RoomPortalSettings(List.of(), List.of());
    }

    public List<RoomDao.WarpSpaceRow> warpSpaces() {
        return List.copyOf(warpSpaces);
    }

    public List<RoomDao.SpecialGateRow> specialGates() {
        return List.copyOf(specialGates);
    }

}
