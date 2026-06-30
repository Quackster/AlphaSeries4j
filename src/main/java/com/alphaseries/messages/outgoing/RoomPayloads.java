package com.alphaseries.messages.outgoing;

import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.List;

public final class RoomPayloads {
    private RoomPayloads() {
    }

    public static String settingsRead(RoomDao.RoomSettingsRead room, List<RoomDao.RoomRight> rightsRows) {
        if (room == null) {
            return "";
        }
        PacketBuilder tagPayload = PacketBuilder.create();
        long tagCount = 0L;
        if (!room.tagOne().isEmpty()) {
            tagPayload.appendString(room.tagOne());
            tagCount++;
        }
        if (!room.tagTwo().isEmpty()) {
            tagPayload.appendString(room.tagTwo());
            tagCount++;
        }

        PacketBuilder rightsPayload = PacketBuilder.create();
        long rightsCount = 0L;
        for (RoomDao.RoomRight right : rightsRows == null ? List.<RoomDao.RoomRight>of() : rightsRows) {
            if (right != null) {
                rightsPayload.appendInt(right.userId()).appendString(right.userName());
                rightsCount++;
            }
        }

        return PacketBuilder.message("GQ")
            .appendInt(room.roomId())
            .appendString(room.roomName())
            .appendString(room.description())
            .appendInt(room.doorStatus())
            .appendInt(room.categoryId())
            .appendInt(room.visitorsMax())
            .appendInt(room.modelVisitorsMax())
            .appendInt(tagCount)
            .appendRaw(tagPayload)
            .appendInt(rightsCount)
            .appendRaw(rightsPayload)
            .appendRaw('H')
            .appendInt(room.allowOthersPets())
            .appendInt(room.allowFeedPets())
            .appendInt(room.allowWalkthrough())
            .appendInt(room.disableWalls())
            .build();
    }

    public static String rollerMove(long furnitureId, long positionX, long positionY, String positionZ) {
        return PacketBuilder.message("AZ")
            .appendInt(furnitureId)
            .appendInt(positionX)
            .appendInt(positionY)
            .appendInt(NumberUtils.parseLong(positionZ))
            .build();
    }

    public static String iconUpdated(long roomId) {
        return PacketBuilder.message("GI")
            .appendInt(roomId)
            .appendString("")
            .build();
    }

    public static String icon(long backgroundId, long foregroundId, List<RoomIconItem> items) {
        PacketBuilder payload = PacketBuilder.create()
            .appendInt(backgroundId)
            .appendInt(foregroundId);
        List<RoomIconItem> iconItems = items == null ? List.of() : items;
        payload.appendInt(iconItems.size());
        for (RoomIconItem item : iconItems) {
            if (item != null) {
                payload.appendInt(item.type())
                    .appendInt(item.position());
            }
        }
        return payload.build();
    }

    public static String entryUpdated(long roomId) {
        return PacketBuilder.message("GH")
            .appendInt(roomId)
            .build();
    }

    public static String homeRoom(long roomId) {
        return PacketBuilder.message("GG")
            .appendInt(roomId)
            .build();
    }

    public static String currentRoom(long roomId) {
        return PacketBuilder.message("AE")
            .appendInt(roomId)
            .appendString("")
            .build();
    }

    public static String occupantEntries(long occupantCount, String occupantPayload) {
        return PacketBuilder.message("@\\")
            .appendInt(occupantCount)
            .appendRaw(occupantPayload)
            .build();
    }

    public static String occupantStatuses(long statusCount, String statusPayload) {
        return PacketBuilder.message("Du")
            .appendInt(statusCount)
            .appendRaw(statusPayload)
            .build();
    }

    public static String createdRoom(long roomId, String roomName) {
        return PacketBuilder.message("@{")
            .appendInt(roomId)
            .appendString(roomName)
            .build();
    }

    public static String officialRoomModel(long roomId, RoomDao.OfficialRoomModel officialRoom) {
        if (officialRoom == null) {
            return "";
        }
        return PacketBuilder.message("GE")
            .appendInt(roomId)
            .appendString(StringUtils.text(officialRoom.requiredFiles()))
            .appendInt(roomId)
            .appendString(StringUtils.text(officialRoom.caption()))
            .build();
    }

    public static String creatableRoomCount(long maxOwnedRooms, long ownedRoomCount) {
        return PacketBuilder.message("H@")
            .appendInt(maxOwnedRooms)
            .appendInt(ownedRoomCount)
            .build();
    }

    public static String roomRightRemoved() {
        return PacketBuilder.message("Fc")
            .appendInt(0L)
            .build();
    }

    public static String settingsUpdated(long roomId) {
        return PacketBuilder.message("GS")
            .appendInt(roomId)
            .build();
    }

    public static String rating(long ratingValue) {
        return PacketBuilder.message("EY")
            .appendInt(ratingValue)
            .build();
    }

    public static String wallOptions(long disableWalls, long thicknessFloor, long thicknessWallpaper) {
        return PacketBuilder.message("GX")
            .appendInt(disableWalls)
            .appendInt(thicknessFloor)
            .appendInt(thicknessWallpaper)
            .build();
    }

    public static String favouriteRemoved(long roomId) {
        return PacketBuilder.message("GK")
            .appendInt(roomId)
            .appendRaw('H')
            .build();
    }

    public static String favouriteAdded(long roomId) {
        return PacketBuilder.message("GK")
            .appendInt(roomId)
            .appendRaw(' ')
            .build();
    }

    public record RoomIconItem(long type, long position) {
    }
}
