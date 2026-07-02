package com.alphaseries.game.room;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class FurnitureRoomCache {
    private FurnitureRoomCache() {
    }

    public static State trackMarker(
        String pendingRoomCache,
        String pendingFurnitureCache,
        String representedRoomCache,
        long roomId,
        long furnitureId
    ) {
        State state = State.from(pendingRoomCache, pendingFurnitureCache, representedRoomCache);
        if (furnitureId <= 0L) {
            return state;
        }

        String exactMarker = marker(furnitureId);
        state.pendingFurnitureCache = state.pendingFurnitureCache.replace(exactMarker, "") + exactMarker;
        state.pendingFurnitureCache = RepresentedRoomCache.removeRecord(state.pendingFurnitureCache, recordMarker(furnitureId));

        if (!state.representedRoomCache.isEmpty()) {
            state.representedRoomCache = RepresentedRoomCache.removeRecord(state.representedRoomCache, exactMarker);
            state.representedRoomCache = RepresentedRoomCache.removeRecord(state.representedRoomCache, recordMarker(furnitureId));
        }

        if (roomId > 0L) {
            String roomMarker = marker(roomId);
            state.pendingRoomCache = state.pendingRoomCache.replace(roomMarker, "");
            state.pendingRoomCache = RepresentedRoomCache.removeRecord(state.pendingRoomCache, recordMarker(roomId));
            state.pendingRoomCache += roomMarker;
        }
        return state;
    }

    public static State removeMarker(
        String pendingRoomCache,
        String pendingFurnitureCache,
        String representedRoomCache,
        long furnitureId
    ) {
        State state = State.from(pendingRoomCache, pendingFurnitureCache, representedRoomCache);
        if (furnitureId <= 0L) {
            return state;
        }

        String exactMarker = marker(furnitureId);
        String recordMarker = recordMarker(furnitureId);
        state.pendingRoomCache = state.pendingRoomCache.replace(exactMarker, "");
        state.pendingFurnitureCache = state.pendingFurnitureCache.replace(exactMarker, "");
        state.pendingRoomCache = RepresentedRoomCache.removeRecord(state.pendingRoomCache, recordMarker);
        state.pendingFurnitureCache = RepresentedRoomCache.removeRecord(state.pendingFurnitureCache, recordMarker);
        if (!state.representedRoomCache.isEmpty()) {
            state.representedRoomCache = RepresentedRoomCache.removeRecord(state.representedRoomCache, exactMarker);
            state.representedRoomCache = RepresentedRoomCache.removeRecord(state.representedRoomCache, recordMarker);
        }
        return state;
    }

    public static State stateCache(
        String pendingRoomCache,
        String pendingFurnitureCache,
        String representedRoomCache,
        long roomId,
        long furnitureId,
        long stateValue
    ) {
        State state = State.from(pendingRoomCache, pendingFurnitureCache, representedRoomCache);
        if (roomId <= 0L || furnitureId <= 0L) {
            return state;
        }

        String roomMarker = marker(roomId);
        state.pendingRoomCache = state.pendingRoomCache.replace(roomMarker, "");
        state.pendingRoomCache = RepresentedRoomCache.removeRecord(state.pendingRoomCache, recordMarker(roomId));
        state.pendingRoomCache += roomMarker;

        String furnitureMarker = marker(furnitureId);
        state.pendingFurnitureCache = state.pendingFurnitureCache.replace(furnitureMarker, "");
        state.pendingFurnitureCache += furnitureMarker;

        state.representedRoomCache = RepresentedRoomCache.removeRecord(state.representedRoomCache, recordMarker(roomId));
        state.representedRoomCache = RepresentedRoomCache.removeRecord(state.representedRoomCache, furnitureMarker);
        state.representedRoomCache = RepresentedRoomCache.removeRecord(state.representedRoomCache, recordMarker(furnitureId));
        state.representedRoomCache += "\1" + roomId + '\t' + furnitureId + '\t' + stateValue + '\2';
        return state;
    }

    public static State stateWrite(
        String pendingRoomCache,
        String pendingFurnitureCache,
        String representedRoomCache,
        long roomId,
        long furnitureId,
        String stateText
    ) {
        State state = State.from(pendingRoomCache, pendingFurnitureCache, representedRoomCache);
        if (furnitureId <= 0L) {
            return state;
        }

        String furnitureMarker = marker(furnitureId);
        state.pendingFurnitureCache = state.pendingFurnitureCache.replace(furnitureMarker, "");
        state.pendingFurnitureCache += furnitureMarker;

        if (roomId > 0L) {
            String roomMarker = marker(roomId);
            state.pendingRoomCache = state.pendingRoomCache.replace(roomMarker, "");
            state.pendingRoomCache += roomMarker;
        }

        state.representedRoomCache = RepresentedRoomCache.removeRecord(state.representedRoomCache, furnitureMarker);
        state.representedRoomCache = RepresentedRoomCache.removeRecord(state.representedRoomCache, recordMarker(furnitureId));
        state.representedRoomCache += "\1" + furnitureId + '\t' + roomId + '\t' + StringUtils.text(stateText) + '\2';
        return state;
    }

    private static String removePendingFurniture(String pendingFurnitureCache, long furnitureId) {
        String furnitureMarker = marker(furnitureId);
        return RepresentedRoomCache.removeRecord(StringUtils.text(pendingFurnitureCache).replace(furnitureMarker, ""), recordMarker(furnitureId));
    }

    private static String removePendingRoom(String pendingRoomCache, long roomId) {
        return StringUtils.text(pendingRoomCache).replace(marker(roomId), "");
    }

    private static List<Long> pendingFurnitureIds(String pendingFurnitureCache) {
        Set<Long> furnitureIds = new LinkedHashSet<>();
        for (String part : StringUtils.text(pendingFurnitureCache).split("\1", -1)) {
            long furnitureId = NumberUtils.parseLong(part);
            if (furnitureId > 0L) {
                furnitureIds.add(furnitureId);
            }
        }
        return new ArrayList<>(furnitureIds);
    }

    private static String marker(long id) {
        return "\1" + id + '\2';
    }

    private static String recordMarker(long id) {
        return "\1" + id + '\t';
    }

    public static final class State {
        public String pendingRoomCache = "";
        public String pendingFurnitureCache = "";
        public String representedRoomCache = "";

        public static State empty() {
            return new State();
        }

        private static State from(String pendingRoomCache, String pendingFurnitureCache, String representedRoomCache) {
            State state = new State();
            state.pendingRoomCache = StringUtils.text(pendingRoomCache);
            state.pendingFurnitureCache = StringUtils.text(pendingFurnitureCache);
            state.representedRoomCache = StringUtils.text(representedRoomCache);
            return state;
        }

        public static State from(
            String pendingRoomCache,
            String pendingFurnitureCache,
            RepresentedRoomCache representedRoomCache
        ) {
            State state = new State();
            state.pendingRoomCache = StringUtils.text(pendingRoomCache);
            state.pendingFurnitureCache = StringUtils.text(pendingFurnitureCache);
            state.representedRoomCache = representedRoomCache == null ? "" : representedRoomCache.cacheText();
            return state;
        }

        public List<Long> pendingFurnitureIds() {
            return FurnitureRoomCache.pendingFurnitureIds(pendingFurnitureCache);
        }

        public State removePendingFurniture(long furnitureId) {
            pendingFurnitureCache = FurnitureRoomCache.removePendingFurniture(pendingFurnitureCache, furnitureId);
            return this;
        }

        public State removePendingRoom(long roomId) {
            pendingRoomCache = FurnitureRoomCache.removePendingRoom(pendingRoomCache, roomId);
            return this;
        }
    }
}
