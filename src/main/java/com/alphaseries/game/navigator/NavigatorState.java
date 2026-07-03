package com.alphaseries.game.navigator;

import com.alphaseries.dao.mysql.RoomDao;

import java.time.LocalDateTime;
import java.util.List;

public final class NavigatorState {
    private static final NavigatorState INSTANCE = new NavigatorState();

    private NewFriendRooms newFriendRooms = NewFriendRooms.empty(null);
    private RecommendedRooms recommendedRooms = RecommendedRooms.empty();
    private RoomCategoryCache roomCategoryCache = RoomCategoryCache.empty();

    private NavigatorState() {
    }

    public static NavigatorState instance() {
        return INSTANCE;
    }

    public synchronized NewFriendRooms newFriendRooms() {
        return newFriendRooms;
    }

    public synchronized void setNewFriendRooms(NewFriendRooms rooms) {
        newFriendRooms = rooms == null ? NewFriendRooms.empty(null) : rooms;
    }

    public synchronized void setNewFriendRooms(List<NewFriendRooms.RoomPick> rooms, LocalDateTime expiresAt) {
        newFriendRooms = NewFriendRooms.fromRoomPicks(rooms, expiresAt);
    }

    public synchronized RecommendedRooms recommendedRooms() {
        return recommendedRooms;
    }

    public synchronized void setRecommendedRooms(RecommendedRooms rooms) {
        recommendedRooms = rooms == null ? RecommendedRooms.empty() : rooms;
    }

    public synchronized void setRecommendedRooms(Iterable<RecommendedRooms.Payload> payloads, long count) {
        recommendedRooms = RecommendedRooms.fromPayloads(payloads, count);
    }

    public synchronized RoomCategoryCache roomCategoryCache() {
        return roomCategoryCache;
    }

    public synchronized void setRoomCategoryCache(RoomCategoryCache cache) {
        roomCategoryCache = cache == null ? RoomCategoryCache.empty() : cache;
    }

    public synchronized void setRoomCategoryDefaults(List<RoomCategoryCache.DefaultCategoryId> defaultCategoryIds) {
        roomCategoryCache = RoomCategoryCache.fromPayloadRows(
            defaultCategoryIds == null ? List.of() : List.copyOf(defaultCategoryIds),
            roomCategoryCache.categoryRowList(),
            roomCategoryCache.payloadRows());
    }

    public synchronized void setRoomCategoryRows(List<RoomDao.RoomCategoryRow> categoryRows) {
        roomCategoryCache = RoomCategoryCache.fromPayloadRows(
            roomCategoryCache.defaultCategoryIdRows(),
            categoryRows == null ? List.of() : List.copyOf(categoryRows),
            roomCategoryCache.payloadRows());
    }

    public synchronized void setRoomCategoryPayloads(List<RoomCategoryCache.CategoryPayload> payloads) {
        roomCategoryCache = RoomCategoryCache.fromPayloadRows(
            roomCategoryCache.defaultCategoryIdRows(),
            roomCategoryCache.categoryRowList(),
            payloads == null ? List.of() : List.copyOf(payloads));
    }

}
