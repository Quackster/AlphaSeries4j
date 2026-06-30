package com.alphaseries.messages.outgoing;

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
            .appendRaw(rooms)
            .build());
    }

    public record FavouriteRoomsPayload(long roomCount, String payload) {
    }
}
