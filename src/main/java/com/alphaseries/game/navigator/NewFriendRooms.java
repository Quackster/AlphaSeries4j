package com.alphaseries.game.navigator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class NewFriendRooms {
    private final List<RoomPick> rooms;
    private final LocalDateTime expiresAt;

    private NewFriendRooms(List<RoomPick> rooms, LocalDateTime expiresAt) {
        this.rooms = List.copyOf(rooms);
        this.expiresAt = expiresAt;
    }

    public static NewFriendRooms fromRoomPicks(List<RoomPick> rooms, LocalDateTime expiresAt) {
        return new NewFriendRooms(rooms, expiresAt);
    }

    public static NewFriendRooms empty(LocalDateTime expiresAt) {
        return new NewFriendRooms(List.of(), expiresAt);
    }

    public List<RoomPick> roomPicks() {
        return List.copyOf(rooms);
    }

    public LocalDateTime expiresAt() {
        return expiresAt;
    }

    public boolean shouldRefresh(LocalDateTime now) {
        return rooms.isEmpty() || expiresAt == null || !expiresAt.isAfter(now);
    }

    public RoomPick randomRoom() {
        if (rooms.isEmpty()) {
            return RoomPick.empty();
        }
        int rowIndex = ThreadLocalRandom.current().nextInt(0, rooms.size());
        if (rowIndex < 0 || rowIndex >= rooms.size()) {
            return RoomPick.empty();
        }
        return rooms.get(rowIndex);
    }

    public record RoomPick(long roomId, long modelType) {
        public static RoomPick empty() {
            return new RoomPick(0L, 0L);
        }
    }
}
