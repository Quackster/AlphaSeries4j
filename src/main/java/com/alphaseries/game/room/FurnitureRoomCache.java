package com.alphaseries.game.room;

import com.alphaseries.util.StringUtils;

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

        public static State from(String pendingRoomCache, String pendingFurnitureCache, String representedRoomCache) {
            State state = new State();
            state.pendingRoomCache = StringUtils.text(pendingRoomCache);
            state.pendingFurnitureCache = StringUtils.text(pendingFurnitureCache);
            state.representedRoomCache = StringUtils.text(representedRoomCache);
            return state;
        }
    }
}
