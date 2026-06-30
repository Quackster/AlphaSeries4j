package com.alphaseries.game.navigator;

import java.time.LocalDateTime;
import java.util.List;

public final class NavigatorState {
    private static final NavigatorState INSTANCE = new NavigatorState();

    private NewFriendRooms newFriendRooms = NewFriendRooms.empty(null);
    private RecommendedRooms recommendedRooms = RecommendedRooms.empty();

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

    public synchronized void setNewFriendRoomsFromLegacy(Object rows, LocalDateTime expiresAt) {
        newFriendRooms = NewFriendRooms.fromLegacy(rows, expiresAt);
    }

    public synchronized RecommendedRooms recommendedRooms() {
        return recommendedRooms;
    }

    public synchronized void setRecommendedRooms(RecommendedRooms rooms) {
        recommendedRooms = rooms == null ? RecommendedRooms.empty() : rooms;
    }

    public synchronized void setRecommendedRoomsFromLegacy(Object payloads, long count) {
        recommendedRooms = RecommendedRooms.fromLegacy(payloads, count);
    }
}
