package com.alphaseries.messages.outgoing;

import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.game.navigator.LegacyNavigatorRoomRow;
import com.alphaseries.game.navigator.NavigatorRoom;
import com.alphaseries.game.navigator.NavigatorTagPopularity;
import com.alphaseries.game.navigator.NewFriendRooms;
import com.alphaseries.game.navigator.OfficialNavigatorItem;
import com.alphaseries.protocol.PacketBuilder;

import java.util.List;

public final class NavigatorPayloads {
    private NavigatorPayloads() {
    }

    public static FavouriteRoomsPayload favouriteRoomIds(List<Long> roomIds, long maxFavorites) {
        long roomCount = 0L;
        PacketBuilder rooms = PacketBuilder.create();
        for (Long roomIdValue : roomIds == null ? List.<Long>of() : roomIds) {
            long roomId = roomIdValue == null ? 0L : roomIdValue.longValue();
            if (roomId > 0L) {
                rooms.appendInt(roomId);
                roomCount++;
            }
        }
        return new FavouriteRoomsPayload(roomCount, PacketBuilder.message("GJ")
            .appendInt(maxFavorites)
            .appendInt(roomCount)
            .appendRaw(rooms.build())
            .build());
    }

    public record FavouriteRoomsPayload(long roomCount, String payload) {
    }

    public static String newFriendRoom(NewFriendRooms.RoomPick roomPick) {
        if (roomPick == null) {
            return "";
        }
        return PacketBuilder.message("L\u007f")
            .appendInt(roomPick.roomId())
            .appendInt(roomPick.modelType())
            .build();
    }

    public static String queryResult(String header, Object selector, long limitValue, String resultPayload) {
        return PacketBuilder.message(header)
            .appendString(selector)
            .appendInt(limitValue)
            .appendRaw(resultPayload)
            .build();
    }

    public static String roomFragment(LegacyNavigatorRoomRow room) {
        if (room == null) {
            return "";
        }
        return PacketBuilder.create()
            .appendInt(room.roomId())
            .appendInt(room.visitorsNow())
            .appendInt(room.visitorsMax())
            .appendInt(room.roomRate())
            .appendInt(room.categoryId())
            .appendInt(room.hasTrading())
            .appendInt(room.allowOtherPets())
            .appendInt(room.staffPicked())
            .appendString(room.roomName())
            .appendString(room.ownerName())
            .appendString(room.doorStatus())
            .appendString(room.description())
            .appendString(room.icon())
            .appendString(room.tagOne())
            .appendString(room.tagTwo())
            .appendRaw("H")
            .build();
    }

    public static String roomFragment(NavigatorRoom room) {
        if (room == null) {
            return "";
        }
        return PacketBuilder.create()
            .appendInt(room.roomId())
            .appendInt(room.visitorsNow())
            .appendInt(room.visitorsMax())
            .appendInt(room.roomRate())
            .appendInt(room.categoryId())
            .appendInt(room.hasTrading())
            .appendInt(room.allowOtherPets())
            .appendInt(room.staffPicked())
            .appendString(room.roomName())
            .appendString(room.ownerName())
            .appendString(room.doorStatus())
            .appendString(room.description())
            .appendString(room.icon())
            .appendString(room.tagOne())
            .appendString(room.tagTwo())
            .appendRaw("H")
            .build();
    }

    public static String legacyRoomList(List<LegacyNavigatorRoomRow> rows) {
        long roomCount = 0L;
        PacketBuilder payload = PacketBuilder.create();
        for (LegacyNavigatorRoomRow row : rows == null ? List.<LegacyNavigatorRoomRow>of() : rows) {
            if (row != null) {
                payload.appendRaw(roomFragment(row));
                roomCount++;
            }
        }
        return PacketBuilder.create().appendRaw(payload.build()).appendInt(roomCount).build();
    }

    public static String roomList(List<NavigatorRoom> rows) {
        long roomCount = 0L;
        PacketBuilder payload = PacketBuilder.create();
        for (NavigatorRoom row : rows == null ? List.<NavigatorRoom>of() : rows) {
            if (row != null) {
                payload.appendRaw(roomFragment(row));
                roomCount++;
            }
        }
        return PacketBuilder.create().appendRaw(payload.build()).appendInt(roomCount).build();
    }

    public static String singleRoom(NavigatorRoom room) {
        return room == null ? "" : roomFragment(room);
    }

    public static String tagPopularity(List<NavigatorTagPopularity> rows) {
        PacketBuilder payload = PacketBuilder.create();
        for (NavigatorTagPopularity row : rows == null ? List.<NavigatorTagPopularity>of() : rows) {
            payload.appendInt(row.visitorCount()).appendString(row.tag());
        }
        return payload.build();
    }

    public static String eventFragment(RoomDao.NavigatorEventRow event) {
        if (event == null) {
            return "";
        }
        return PacketBuilder.create()
            .appendInt(event.roomId())
            .appendInt(event.visitorsNow())
            .appendInt(event.visitorsMax())
            .appendInt(event.roomRate())
            .appendInt(event.categoryId())
            .appendInt(event.hasTrading())
            .appendRaw(" ")
            .appendString(event.eventName())
            .appendString(event.ownerName())
            .appendString(event.doorStatus())
            .appendString(event.description())
            .appendString(event.icon())
            .appendString(event.tagOne())
            .appendString(event.tagTwo())
            .appendString(event.formattedTime())
            .appendRaw("H")
            .build();
    }

    public static String eventList(List<RoomDao.NavigatorEventRow> rows) {
        long eventCount = 0L;
        PacketBuilder payload = PacketBuilder.create();
        for (RoomDao.NavigatorEventRow row : rows == null ? List.<RoomDao.NavigatorEventRow>of() : rows) {
            if (row != null) {
                payload.appendRaw(eventFragment(row));
                eventCount++;
            }
        }
        return PacketBuilder.create().appendRaw(payload.build()).appendInt(eventCount).build();
    }

    public static String combinedLegacyRoomList(
        List<RoomDao.NavigatorEventRow> eventRows,
        List<LegacyNavigatorRoomRow> roomRows
    ) {
        long itemCount = 0L;
        PacketBuilder payload = PacketBuilder.create();
        for (RoomDao.NavigatorEventRow row : eventRows == null ? List.<RoomDao.NavigatorEventRow>of() : eventRows) {
            if (row != null) {
                payload.appendRaw(eventFragment(row));
                itemCount++;
            }
        }
        for (LegacyNavigatorRoomRow row : roomRows == null ? List.<LegacyNavigatorRoomRow>of() : roomRows) {
            if (row != null) {
                payload.appendRaw(roomFragment(row));
                itemCount++;
            }
        }
        return PacketBuilder.create().appendRaw(payload.build()).appendInt(itemCount).build();
    }

    public static String combinedRoomList(List<RoomDao.NavigatorEventRow> eventRows, List<NavigatorRoom> roomRows) {
        long itemCount = 0L;
        PacketBuilder payload = PacketBuilder.create();
        for (RoomDao.NavigatorEventRow row : eventRows == null ? List.<RoomDao.NavigatorEventRow>of() : eventRows) {
            if (row != null) {
                payload.appendRaw(eventFragment(row));
                itemCount++;
            }
        }
        for (NavigatorRoom row : roomRows == null ? List.<NavigatorRoom>of() : roomRows) {
            if (row != null) {
                payload.appendRaw(roomFragment(row));
                itemCount++;
            }
        }
        return PacketBuilder.create().appendRaw(payload.build()).appendInt(itemCount).build();
    }

    public static String official(List<OfficialNavigatorItem> items, boolean includeCountPrefix) {
        PacketBuilder payload = PacketBuilder.create();
        long itemCount = 0L;
        for (OfficialNavigatorItem item : items == null ? List.<OfficialNavigatorItem>of() : items) {
            payload.appendRaw(officialItem(item));
            itemCount++;
        }
        if (includeCountPrefix) {
            return PacketBuilder.create().appendInt(itemCount).appendRaw(payload.build()).build();
        }
        return payload.build();
    }

    public static String officialItem(OfficialNavigatorItem item) {
        if (item == null) {
            return "";
        }
        PacketBuilder payload = PacketBuilder.create()
            .appendInt(item.typeId())
            .appendInt(item.styleId())
            .appendInt(item.iconId());
        for (String textField : item.textFields()) {
            payload.appendString(textField);
        }
        payload.appendInt(item.parentId())
            .appendInt(item.officialId());
        if (item.requiredLevelPresent()) {
            payload.appendInt(item.requiredLevel());
        }
        return payload.build();
    }
}
