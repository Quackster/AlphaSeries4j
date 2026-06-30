package com.alphaseries.game.room;

import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.util.StringUtils;

import java.util.List;

public final class RoomPortalSettings {
    private final String warpSpaceRows;
    private final String specialGateRows;
    private final List<RoomDao.WarpSpaceRow> warpSpaces;
    private final List<RoomDao.SpecialGateRow> specialGates;

    private RoomPortalSettings(String warpSpaceRows, String specialGateRows,
            List<RoomDao.WarpSpaceRow> warpSpaces, List<RoomDao.SpecialGateRow> specialGates) {
        this.warpSpaceRows = StringUtils.text(warpSpaceRows);
        this.specialGateRows = StringUtils.text(specialGateRows);
        this.warpSpaces = warpSpaces == null ? List.of() : List.copyOf(warpSpaces);
        this.specialGates = specialGates == null ? List.of() : List.copyOf(specialGates);
    }

    public static RoomPortalSettings fromLegacy(Object warpSpaceRows, Object specialGateRows) {
        if (warpSpaceRows instanceof RoomPortalSettings settings) {
            return settings;
        }
        if (specialGateRows instanceof RoomPortalSettings settings) {
            return settings;
        }
        return new RoomPortalSettings(StringUtils.text(warpSpaceRows), StringUtils.text(specialGateRows), List.of(), List.of());
    }

    public static RoomPortalSettings fromRows(List<RoomDao.WarpSpaceRow> warpSpaces,
            List<RoomDao.SpecialGateRow> specialGates) {
        return new RoomPortalSettings("0\r", "\r", warpSpaces, specialGates);
    }

    public static RoomPortalSettings empty() {
        return new RoomPortalSettings("", "", List.of(), List.of());
    }

    public String warpSpaceRows() {
        if (!warpSpaces.isEmpty()) {
            StringBuilder rows = new StringBuilder("0");
            for (RoomDao.WarpSpaceRow row : warpSpaces) {
                if (row != null) {
                    rows.append(row.roomId()).append('\t')
                        .append(row.positionX()).append('\t')
                        .append(row.positionY()).append('\t')
                        .append(row.warpRoomId()).append('\t')
                        .append(row.warpX()).append('\t')
                        .append(row.warpY()).append('\t')
                        .append(row.special()).append('\r');
                }
            }
            if (rows.length() == 1) {
                rows.append('\r');
            }
            return rows.toString();
        }
        return warpSpaceRows;
    }

    public String specialGateRows() {
        if (!specialGates.isEmpty()) {
            StringBuilder rows = new StringBuilder();
            for (RoomDao.SpecialGateRow row : specialGates) {
                if (row != null) {
                    rows.append(row.roomId()).append('\t').append(row.open()).append('\r');
                }
            }
            if (rows.length() == 0) {
                rows.append('\r');
            }
            return rows.toString();
        }
        return specialGateRows;
    }
}
