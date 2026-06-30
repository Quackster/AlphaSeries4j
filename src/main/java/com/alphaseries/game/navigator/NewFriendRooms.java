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
        List<RoomPick> rooms = new ArrayList<>();
        String rowText = StringUtils.text(rows);
        if (rowText.isEmpty()) {
            return new NewFriendRooms(rooms, expiresAt);
        }
        for (String row : rowText.split("\r", -1)) {
            if (row.isEmpty()) {
                rooms.add(RoomPick.empty());
                continue;
            }
            String[] fields = row.split("\t", -1);
            if (fields.length < 2) {
                rooms.add(RoomPick.empty());
                continue;
            }
            rooms.add(new RoomPick(NumberUtils.parseLong(fields[0]), NumberUtils.parseLong(fields[1])));
        }
        return new NewFriendRooms(rooms, expiresAt);
    }

    public static NewFriendRooms fromRoomPicks(List<RoomPick> rooms, LocalDateTime expiresAt) {
        return new NewFriendRooms(rooms, expiresAt);
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
        private static RoomPick empty() {
            return new RoomPick(0L, 0L);
        }
    }
}
