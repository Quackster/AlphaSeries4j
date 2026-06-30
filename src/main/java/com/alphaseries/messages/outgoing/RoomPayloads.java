package com.alphaseries.messages.outgoing;

import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.protocol.PacketBuilder;

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
}
