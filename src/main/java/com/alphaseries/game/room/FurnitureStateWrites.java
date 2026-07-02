package com.alphaseries.game.room;

import com.alphaseries.dao.mysql.FurnitureDao;
import com.alphaseries.game.wired.WiredPayloads;
import com.alphaseries.messages.outgoing.FurniturePayloads;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class FurnitureStateWrites {
    private FurnitureStateWrites() {
    }

    public static Result write(
        FurnitureRoomCache.State cacheState,
        long roomId,
        long furnitureId,
        String stateText,
        FurnitureDao furniture
    ) {
        FurnitureRoomCache.State sourceState = cacheState == null
            ? FurnitureRoomCache.State.empty()
            : cacheState;
        if (furnitureId <= 0L) {
            return new Result(sourceState, roomId);
        }
        long resolvedRoomId = roomId;
        if (resolvedRoomId <= 0L) {
            if (furniture == null) {
                return new Result(sourceState, resolvedRoomId);
            }
            try {
                resolvedRoomId = furniture.roomIdByFurniture(furnitureId);
            } catch (Exception ignored) {
                return new Result(sourceState, resolvedRoomId);
            }
        }
        FurnitureRoomCache.State state = FurnitureRoomCache.stateWrite(
            sourceState,
            resolvedRoomId,
            furnitureId,
            StringUtils.text(stateText));
        return new Result(state, resolvedRoomId);
    }

    public static FurnitureRoomCache.State refreshState(
        FurnitureRoomCache.State cacheState,
        long roomId,
        long furnitureId,
        long stateValue
    ) {
        FurnitureRoomCache.State sourceState = cacheState == null
            ? FurnitureRoomCache.State.empty()
            : cacheState;
        if (roomId <= 0L || furnitureId <= 0L) {
            return sourceState;
        }
        try {
            FurnitureRoomCache.State state = FurnitureRoomCache.stateCache(sourceState, roomId, furnitureId, stateValue);
            deleteRoomCaches(roomId);
            return state;
        } catch (Exception ignored) {
            return sourceState;
        }
    }

    public static WiredStateApplyResult applyWiredSelectedStates(
        FurnitureRoomCache.State cacheState,
        long roomId,
        List<Long> selectedIds,
        String parameterText,
        long selectedFurnitureId,
        FurnitureDao furniture
    ) {
        FurnitureRoomCache.State state = cacheState == null
            ? FurnitureRoomCache.State.empty()
            : cacheState;
        if (roomId <= 0L || furniture == null) {
            return new WiredStateApplyResult(0L, state, List.of());
        }
        List<Long> effectiveSelectedIds = selectedFurnitureId > 0L
            ? List.of(selectedFurnitureId)
            : selectedIds == null ? List.of() : selectedIds;
        if (effectiveSelectedIds.isEmpty()) {
            return new WiredStateApplyResult(0L, state, List.of());
        }
        long stateValue = WiredPayloads.stateParameterValue(parameterText);
        long appliedCount = 0L;
        List<String> broadcastPayloads = new ArrayList<>();
        for (long furnitureId : effectiveSelectedIds) {
            if (furnitureId > 0L && FurnitureLookups.existsInRoom(roomId, furnitureId, furniture)) {
                try {
                    furniture.updateSignLimited(furnitureId, stateValue);
                    state = refreshState(state, roomId, furnitureId, stateValue);
                    broadcastPayloads.add(FurniturePayloads.stateChanged(furnitureId, stateValue));
                    appliedCount++;
                } catch (Exception ignored) {
                    // VB6 source suppresses helper failures.
                }
            }
        }
        return new WiredStateApplyResult(appliedCount, state, broadcastPayloads);
    }

    public static FurnitureRoomCache.State trackMarker(FurnitureRoomCache.State cacheState, long roomId, long furnitureId) {
        FurnitureRoomCache.State sourceState = cacheState == null
            ? FurnitureRoomCache.State.empty()
            : cacheState;
        if (furnitureId <= 0L) {
            return sourceState;
        }
        try {
            FurnitureRoomCache.State state = FurnitureRoomCache.trackMarker(sourceState, roomId, furnitureId);
            deleteRoomCaches(roomId);
            return state;
        } catch (Exception ignored) {
            return sourceState;
        }
    }

    public static FurnitureRoomCache.State removeMarker(FurnitureRoomCache.State cacheState, long roomId, long furnitureId) {
        FurnitureRoomCache.State sourceState = cacheState == null
            ? FurnitureRoomCache.State.empty()
            : cacheState;
        if (furnitureId <= 0L) {
            return sourceState;
        }
        try {
            FurnitureRoomCache.State state = FurnitureRoomCache.removeMarker(sourceState, furnitureId);
            deleteRoomCaches(roomId);
            return state;
        } catch (Exception ignored) {
            return sourceState;
        }
    }

    private static void deleteRoomCaches(long roomId) {
        RoomCacheFiles.invalidateRoom(roomId);
    }

    public record Result(FurnitureRoomCache.State state, long roomId) {
    }

    public record WiredStateApplyResult(
        long appliedCount,
        FurnitureRoomCache.State state,
        List<String> broadcastPayloads
    ) {
        public WiredStateApplyResult {
            state = state == null ? FurnitureRoomCache.State.empty() : state;
            broadcastPayloads = broadcastPayloads == null ? List.of() : List.copyOf(broadcastPayloads);
        }
    }
}
