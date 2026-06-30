package com.alphaseries.game.navigator;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class NewFriendRooms {
    private final List<RoomPick> rooms;
    private final LocalDateTime expiresAt;

    private NewFriendRooms(List<RoomPick> rooms, LocalDateTime expiresAt) {
        this.rooms = List.copyOf(rooms);
        this.expiresAt = expiresAt;
    }

    public static NewFriendRooms fromLegacy(Object rows, LocalDateTime expiresAt) {
        if (rows instanceof NewFriendRooms newFriendRooms) {
            return newFriendRooms;
        }
        return new NewFriendRooms(roomPicks(rows), expiresAt);
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

    private static List<RoomPick> roomPicks(Object rows) {
        if (rows instanceof Iterable<?> values) {
            return roomPicks(values);
        }
        List<RoomPick> rooms = new ArrayList<>();
        String rowText = StringUtils.text(rows);
        if (rowText.isEmpty()) {
            return rooms;
        }
        for (String row : rowText.split("\r", -1)) {
            rooms.add(roomPick(row));
        }
        return rooms;
    }

    private static List<RoomPick> roomPicks(Iterable<?> values) {
        List<RoomPick> rooms = new ArrayList<>();
        for (Object value : values) {
            if (value instanceof RoomPick roomPick) {
                rooms.add(roomPick);
            }
        }
        return rooms;
    }

    private static RoomPick roomPick(String row) {
        String rowText = StringUtils.text(row);
        if (rowText.isEmpty()) {
            return RoomPick.empty();
        }
        String[] fields = rowText.split("\t", -1);
        if (fields.length < 2) {
            return RoomPick.empty();
        }
        return new RoomPick(NumberUtils.parseLong(fields[0]), NumberUtils.parseLong(fields[1]));
    }

    public record RoomPick(long roomId, long modelType) {
        private static RoomPick empty() {
            return new RoomPick(0L, 0L);
        }
    }
}
