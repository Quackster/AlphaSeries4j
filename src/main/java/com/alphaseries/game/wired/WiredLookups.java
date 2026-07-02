package com.alphaseries.game.wired;

import com.alphaseries.config.AppPaths;
import com.alphaseries.dao.mysql.FurnitureDao;
import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.game.GameDataCaches;
import com.alphaseries.game.room.FurnitureLookups;
import com.alphaseries.game.room.FurnitureStateWrites;
import com.alphaseries.game.room.RoomLookups;
import com.alphaseries.game.room.RoomState;
import com.alphaseries.game.session.SessionState;
import com.alphaseries.util.FileUtils;
import com.alphaseries.util.NumberUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiConsumer;

public final class WiredLookups {
    private WiredLookups() {
    }

    public record RoomRequest(String userId, long roomId) {
        public boolean valid() {
            return NumberUtils.parseLong(userId) > 0L && roomId > 0L;
        }
    }

    public static RoomRequest roomRequest(int socketIndex, UserDao users, RoomDao rooms) {
        if (socketIndex <= 0) {
            return new RoomRequest("", 0L);
        }
        long userId = SessionState.instance().sessionUserIdBySocket(socketIndex);
        if (userId <= 0L && users != null) {
            try {
                userId = users.userIdBySocket(socketIndex);
            } catch (Exception ignored) {
                userId = 0L;
            }
        }
        long roomId = SessionState.instance().sessionCacheLong(String.valueOf(socketIndex), 1);
        if (roomId <= 0L && userId > 0L && rooms != null) {
            try {
                roomId = rooms.currentRoomIdByUser(userId);
            } catch (Exception ignored) {
                roomId = 0L;
            }
        }
        if (roomId <= 0L && rooms != null) {
            try {
                roomId = rooms.roomIdBySlot(socketIndex);
            } catch (Exception ignored) {
                roomId = 0L;
            }
        }
        return new RoomRequest(userId > 0L ? String.valueOf(userId) : "", roomId);
    }

    /**
     * Original function: Proc_6_84_733600.
     */
    public static String roomStartupCachePayload(long roomId, WiredSettings settings) {
        String payload = "Di" + (settings == null ? "" : settings.statePayload());
        try {
            if (roomId <= 0L) {
                return payload;
            }
            Path cacheRoot = Path.of(AppPaths.applicationPath(), "cache");
            String triggerCache = FileUtils.ensureTextFile(cacheRoot.resolve("wired_trigger").resolve(roomId + ".cache").toString());
            String actionCache = FileUtils.ensureTextFile(cacheRoot.resolve("wired_action").resolve(roomId + ".cache").toString());
            String conditionCache = FileUtils.ensureTextFile(cacheRoot.resolve("wired_condition").resolve(roomId + ".cache").toString());
            String pathfinderCache = FileUtils.ensureTextFile(cacheRoot.resolve("pathfinder").resolve(roomId + ".cache").toString());
            String destinationCache = FileUtils.ensureTextFile(cacheRoot.resolve("rooms").resolve("destination_" + roomId + ".cache").toString());
            String roomCache = FileUtils.ensureTextFile(cacheRoot.resolve("rooms").resolve(roomId + ".cache").toString());
            return payload + '\t' + triggerCache + '\t' + actionCache + '\t' + conditionCache
                + '\t' + pathfinderCache + '\t' + destinationCache + '\t' + roomCache;
        } catch (Exception ignored) {
            return payload;
        }
    }

    public static String editRecord(
        int socketIndex,
        String userId,
        long roomId,
        String packetPayload,
        String packetCode,
        long minimumCode,
        long maximumCode,
        String cacheFolder,
        boolean includeExtraValue,
        FurnitureDao furniture,
        RoomDao rooms
    ) {
        try {
            WiredWire.EditFurnitureRequest request = WiredWire.editFurnitureRequest(packetPayload, packetCode);
            long furnitureId = request.furnitureId();
            if (socketIndex <= 0 || furnitureId <= 0L || roomId <= 0L || userId == null || userId.isEmpty()
                || "0".equals(userId) || furniture == null || rooms == null) {
                return "";
            }
            if (!RoomLookups.userHasRoomRight(userId, roomId, rooms) && !RoomLookups.userOwnsRoom(userId, roomId, rooms)) {
                return "";
            }
            long productId = furniture.roomFurnitureProductById(furnitureId, roomId)
                .map(FurnitureDao.RoomFurnitureProduct::productId)
                .orElse(0L);
            long wiredCode = GameDataCaches.productCache().wiredCode(productId);
            if (wiredCode < minimumCode || wiredCode > maximumCode) {
                return "";
            }
            String recordText = WiredWire.editRecord(packetPayload, packetCode, wiredCode, includeExtraValue);
            if (recordText.isEmpty()) {
                return "";
            }
            List<Long> selectedIds = WiredPayloads.record(recordText).selectedFurnitureIds();
            if (!selectedIds.isEmpty() && !selectedItemsExist(roomId, selectedIds, furniture)) {
                return "";
            }
            WiredCache.appendRecord(cacheFolder, roomId, recordText);
            return recordText;
        } catch (Exception ignored) {
            return "";
        }
    }

    public static String editRecord(
        int socketIndex,
        RoomRequest request,
        String packetPayload,
        String packetCode,
        long minimumCode,
        long maximumCode,
        String cacheFolder,
        boolean includeExtraValue,
        FurnitureDao furniture,
        RoomDao rooms
    ) {
        if (request == null || !request.valid()) {
            return "";
        }
        return editRecord(
            socketIndex,
            request.userId(),
            request.roomId(),
            packetPayload,
            packetCode,
            minimumCode,
            maximumCode,
            cacheFolder,
            includeExtraValue,
            furniture,
            rooms);
    }

    /**
     * Original function: Proc_6_221_7ED1E0.
     */
    public static String createSnapshot(
        int socketIndex,
        String userId,
        long roomId,
        String packetPayload,
        FurnitureDao furniture,
        RoomDao rooms
    ) {
        try {
            WiredWire.SnapshotRequest request = WiredWire.snapshotRequest(packetPayload);
            long furnitureId = request.furnitureId();
            if (socketIndex <= 0 || furnitureId <= 0L || roomId <= 0L || userId == null || userId.isEmpty()
                || "0".equals(userId) || furniture == null || rooms == null) {
                return "";
            }
            if (!RoomLookups.userHasRoomRight(userId, roomId, rooms) && !RoomLookups.userOwnsRoom(userId, roomId, rooms)) {
                return "";
            }
            long productId = furniture.roomFurnitureProductById(furnitureId, roomId)
                .map(FurnitureDao.RoomFurnitureProduct::productId)
                .orElse(0L);
            if (productId <= 0L || GameDataCaches.productCache().wiredCode(productId) <= 0L) {
                return "";
            }
            Path snapshotPath = Path.of(AppPaths.applicationPath(), "cache", "wired_snapshots", furnitureId + ".cache");
            Files.createDirectories(snapshotPath.getParent());
            FileUtils.writeTextFile(snapshotPath.toString(), RoomState.instance().representedRooms().cacheText());
            return snapshotPath.toString();
        } catch (Exception ignored) {
            return "";
        }
    }

    public static String createSnapshot(
        int socketIndex,
        RoomRequest request,
        String packetPayload,
        FurnitureDao furniture,
        RoomDao rooms
    ) {
        if (request == null || !request.valid()) {
            return "";
        }
        return createSnapshot(socketIndex, request.userId(), request.roomId(), packetPayload, furniture, rooms);
    }

    public static long trigger(
        long roomId,
        long triggerCode,
        long selectedFurnitureId,
        FurnitureDao furniture,
        BiConsumer<Long, String> roomBroadcaster
    ) {
        if (roomId <= 0L) {
            return 0L;
        }
        long executedCount = 0L;
        for (WiredPayloads.WiredRecord record : WiredCache.records("wired_trigger", roomId)) {
            long recordCode = NumberUtils.parseLong(record.code());
            if ((triggerCode <= 0L || recordCode == triggerCode) && conditionsPass(roomId, furniture)) {
                executedCount += action(roomId, 0L, selectedFurnitureId, furniture, roomBroadcaster);
            }
        }
        return executedCount;
    }

    public static boolean conditionsPass(long roomId, FurnitureDao furniture) {
        if (roomId <= 0L) {
            return false;
        }
        for (WiredPayloads.WiredRecord record : WiredCache.records("wired_condition", roomId)) {
            List<Long> selectedIds = record.selectedFurnitureIds();
            if (!selectedIds.isEmpty() && !selectedItemsExist(roomId, selectedIds, furniture)) {
                return false;
            }
        }
        return true;
    }

    public static long action(
        long roomId,
        long actionCode,
        long selectedFurnitureId,
        FurnitureDao furniture,
        BiConsumer<Long, String> roomBroadcaster
    ) {
        if (roomId <= 0L) {
            return 0L;
        }
        long actionCount = 0L;
        for (WiredPayloads.WiredRecord record : WiredCache.records("wired_action", roomId)) {
            long recordCode = NumberUtils.parseLong(record.code());
            if (actionCode <= 0L || recordCode == actionCode) {
                actionCount += applySelected(
                    roomId, record.selectedFurnitureIds(), record.parameterText(), selectedFurnitureId, furniture, roomBroadcaster);
            }
        }
        return actionCount;
    }

    public static long applySelected(
        long roomId,
        List<Long> selectedIds,
        String parameterText,
        long selectedFurnitureId,
        FurnitureDao furniture,
        BiConsumer<Long, String> roomBroadcaster
    ) {
        if (roomId <= 0L || furniture == null) {
            return 0L;
        }
        FurnitureStateWrites.WiredStateApplyResult result = FurnitureStateWrites.applyWiredSelectedStates(
            RoomState.instance().furnitureRoomCache(), roomId, selectedIds, parameterText, selectedFurnitureId, furniture);
        RoomState.instance().setFurnitureRoomCache(result.state());
        if (roomBroadcaster != null) {
            for (String payload : result.broadcastPayloads()) {
                roomBroadcaster.accept(roomId, payload);
            }
        }
        return result.appliedCount();
    }

    public static boolean selectedItemsExist(long roomId, List<Long> selectedIds, FurnitureDao furniture) {
        return FurnitureLookups.selectedItemsExistInRoom(roomId, selectedIds, furniture);
    }
}
