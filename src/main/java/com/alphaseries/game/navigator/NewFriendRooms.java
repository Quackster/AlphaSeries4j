package com.alphaseries.game.navigator;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

public final class NewFriendRooms {
    private final String rows;
    private final LocalDateTime expiresAt;

    private NewFriendRooms(String rows, LocalDateTime expiresAt) {
        this.rows = StringUtils.text(rows);
        this.expiresAt = expiresAt;
    }

    public static NewFriendRooms fromLegacy(String rows, LocalDateTime expiresAt) {
        return new NewFriendRooms(rows, expiresAt);
    }

    public boolean shouldRefresh(LocalDateTime now) {
        return rows.isEmpty() || expiresAt == null || !expiresAt.isAfter(now);
    }

    public RoomPick randomRoom() {
        if (rows.isEmpty()) {
            return RoomPick.empty();
        }
        String[] rowArray = rows.split("\r", -1);
        int rowIndex = ThreadLocalRandom.current().nextInt(0, rowArray.length);
        if (rowIndex < 0 || rowIndex >= rowArray.length || rowArray[rowIndex].isEmpty()) {
            return RoomPick.empty();
        }
        String[] fields = rowArray[rowIndex].split("\t", -1);
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
