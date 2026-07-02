package com.alphaseries.game.room;

import com.alphaseries.config.AppConfigState;
import com.alphaseries.config.AppPaths;
import com.alphaseries.dao.mysql.DaoProvider;
import com.alphaseries.dao.mysql.FurnitureDao;
import com.alphaseries.dao.mysql.PackageDao;
import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.dao.mysql.StaffModerationDao;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.game.GameDataCaches;
import com.alphaseries.game.catalog.CatalogState;
import com.alphaseries.game.inventory.InventoryPacketHandlers;
import com.alphaseries.game.pet.PetPacketHandlers;
import com.alphaseries.game.pet.PetWire;
import com.alphaseries.game.user.UserLookups;
import com.alphaseries.server.runtime.SessionLookups;
import com.alphaseries.server.runtime.SocketDelivery;
import com.alphaseries.util.NumberUtils;

public final class FurniturePacketHandlers {
    private FurniturePacketHandlers() {
    }

    /**
     * Original function: Proc_6_98_747D80.
     */
    public static long sendDimmerPresets(int socketIndex) {
        long currentPresetId = 0L;
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            FurnitureDimmers.PresetPayload dimmerPayload = FurnitureDimmers.presetsForUser(
                NumberUtils.parseLong(userId), roomId, roomDao(), furnitureDao());
            currentPresetId = dimmerPayload.currentPresetId();
            if (!dimmerPayload.payload().isEmpty()) {
                SocketDelivery.sendToSocket(socketIndex, dimmerPayload.payload());
            }
            return currentPresetId;
        } catch (Exception ignored) {
            return currentPresetId;
        }
    }

    /**
     * Original function: Proc_6_99_748460.
     */
    public static long toggleDimmerState(int socketIndex) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            FurnitureDimmers.StatePayload dimmerPayload = FurnitureDimmers.toggleStateForUser(
                NumberUtils.parseLong(userId), roomId, roomDao(), furnitureDao());
            if (!dimmerPayload.payload().isEmpty()) {
                SocketDelivery.broadcastToCurrentRoom(socketIndex, dimmerPayload.payload());
            }
            return dimmerPayload.state();
        } catch (Exception ignored) {
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_100_748C80.
     */
    public static long updateDimmerPreset(int socketIndex, FurnitureWire.DimmerPresetRequest request) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            FurnitureDimmers.UpdatePayload dimmerPayload = FurnitureDimmers.updatePresetForUser(
                NumberUtils.parseLong(userId),
                roomId,
                request.presetId(),
                request.backgroundId(),
                request.colourText(),
                request.lightLevel(),
                roomDao(),
                furnitureDao());
            if (!dimmerPayload.payload().isEmpty()) {
                SocketDelivery.broadcastToCurrentRoom(socketIndex, dimmerPayload.payload());
            }
            return dimmerPayload.furnitureId();
        } catch (Exception ignored) {
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_66_721D60.
     */
    public static void updateStickyNote(int socketIndex, FurnitureWire.StickyNoteUpdate note) {
        try {
            if (note.furnitureId() <= 0L) {
                return;
            }
            String callerUserId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !RoomLookups.userHasRoomRight(NumberUtils.parseLong(callerUserId), roomId, roomDao())) {
                return;
            }
            String payload = FurnitureLookups.updateStickyNotePayload(
                note, roomId, furnitureDao(), GameDataCaches.productCache());
            if (!payload.isEmpty()) {
                SocketDelivery.broadcastToCurrentRoom(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_67_722940.
     */
    public static void sendStickyNote(int socketIndex, FurnitureWire.FurnitureIdRequest request) {
        try {
            long furnitureId = request.furnitureId();
            if (furnitureId <= 0L) {
                return;
            }
            String callerUserId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L) {
                return;
            }
            String payload = FurnitureLookups.stickyNotePayload(
                furnitureId, roomId, furnitureDao(), GameDataCaches.productCache());
            if (!payload.isEmpty()) {
                SocketDelivery.sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_68_723170.
     */
    public static void deleteStickyNote(int socketIndex, FurnitureWire.FurnitureIdRequest request) {
        try {
            long furnitureId = request.furnitureId();
            if (furnitureId <= 0L) {
                return;
            }
            String callerUserId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !RoomLookups.userOwnsRoom(NumberUtils.parseLong(callerUserId), roomId, roomDao())) {
                return;
            }
            String payload = FurnitureLookups.deleteStickyNotePayload(
                furnitureId, roomId, furnitureDao(), GameDataCaches.productCache());
            if (!payload.isEmpty()) {
                SocketDelivery.broadcastToCurrentRoom(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_69_723630.
     */
    public static void openPresent(int socketIndex, FurnitureWire.FurnitureIdRequest request) {
        try {
            long furnitureId = request.furnitureId();
            if (furnitureId <= 0L) {
                return;
            }
            String callerUserId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !RoomLookups.userHasRoomRight(NumberUtils.parseLong(callerUserId), roomId, roomDao())) {
                return;
            }
            FurnitureLookups.PresentOpenResult result = FurnitureLookups.openPresent(
                furnitureId, roomId, NumberUtils.parseLong(callerUserId), furnitureDao(), GameDataCaches.productCache());
            if (result.valid()) {
                SocketDelivery.broadcastToCurrentRoom(socketIndex, result.removedPayload());
                SocketDelivery.sendToSocket(socketIndex, result.responsePayload());
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_70_724190.
     */
    public static void toggleWallFurnitureState(int socketIndex, FurnitureWire.FurnitureIdRequest request) {
        try {
            long furnitureId = request.furnitureId();
            if (furnitureId <= 0L) {
                return;
            }
            String callerUserId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !RoomLookups.userHasRoomRight(NumberUtils.parseLong(callerUserId), roomId, roomDao())) {
                return;
            }
            String payload = FurnitureLookups.toggleWallFurnitureStatePayload(
                furnitureId, roomId, furnitureDao(), CatalogState.instance().registry(), GameDataCaches.productCache());
            if (!payload.isEmpty()) {
                SocketDelivery.broadcastToCurrentRoom(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_73_725540.
     */
    public static void redeemCreditFurniture(int socketIndex, FurnitureWire.CreditFurnitureRequest request) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            if (roomId <= 0L || (!RoomLookups.userHasRoomRight(NumberUtils.parseLong(userId), roomId, roomDao())
                && !UserLookups.hasPermission(NumberUtils.parseLong(userId), "fuse_pick_up_any_furni", userDao(), AppConfigState.instance().permissionMatrix()))) {
                return;
            }
            long furnitureId = request.furnitureId();
            if (furnitureId <= 0L) {
                return;
            }
            long numericUserId = NumberUtils.parseLong(userId);
            FurnitureLookups.CreditFurnitureRedemption redemption = FurnitureLookups.redeemCreditFurniture(
                furnitureId, roomId, numericUserId, furnitureDao(), userDao(), GameDataCaches.productCache());
            if (!redemption.valid()) {
                return;
            }
            SocketDelivery.sendToSocket(socketIndex, redemption.creditsPayload());
            SocketDelivery.broadcastToCurrentRoom(socketIndex, redemption.removedPayload());
            RoomCacheFiles.invalidateRoom(roomId);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_139_768100.
     */
    public static void applyRoomDecorationFurniture(int socketIndex, FurnitureWire.FurnitureIdRequest request) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            if (userId.isEmpty() || roomId <= 0L) {
                return;
            }
            long furnitureId = request.furnitureId();
            if (furnitureId <= 0L) {
                return;
            }
            FurnitureLookups.RoomDecorationApplication application = FurnitureLookups.applyRoomDecorationFurniture(
                furnitureId,
                roomId,
                NumberUtils.parseLong(userId),
                furnitureDao(),
                roomDao(),
                CatalogState.instance().registry());
            if (!application.valid()) {
                return;
            }
            SocketDelivery.broadcastToCurrentRoom(socketIndex, application.roomPayload());
            SocketDelivery.sendToSocket(socketIndex, application.inventoryRemovePayload());
            InventoryPacketHandlers.sendInventoryToSocket(socketIndex);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_144_76BE70.
     */
    public static void returnRoomFurnitureToInventory(int socketIndex, FurnitureWire.FurnitureIdRequest request) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            if (userId.isEmpty() || "0".equals(userId) || roomId <= 0L) {
                return;
            }
            long furnitureId = request.furnitureId();
            if (furnitureId <= 0L) {
                return;
            }
            long userIdValue = NumberUtils.parseLong(userId);
            boolean canPickUpAny = UserLookups.hasPermission(userIdValue, "fuse_pick_up_any_furni", userDao(), AppConfigState.instance().permissionMatrix());
            FurnitureLookups.FurnitureInventoryReturn inventoryReturn =
                FurnitureLookups.returnRoomFurnitureToInventory(
                    furnitureId,
                    roomId,
                    userIdValue,
                    RoomLookups.userOwnsRoom(userIdValue, roomId, roomDao()),
                    RoomLookups.userHasRoomRight(userIdValue, roomId, roomDao()),
                    canPickUpAny,
                    furnitureDao());
            if (!inventoryReturn.valid()) {
                return;
            }
            SocketDelivery.broadcastToCurrentRoom(socketIndex, inventoryReturn.removedPayload());
            RoomCacheFiles.invalidateRoom(roomId);
            InventoryPacketHandlers.sendInventoryToSocket(socketIndex);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_149_775C10.
     */
    public static void toggleFloorFurnitureState(int socketIndex, FurnitureWire.FurnitureIdRequest request) {
        try {
            if (socketIndex <= 0) {
                return;
            }
            long furnitureId = request.furnitureId();
            if (furnitureId <= 0L) {
                return;
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            FurnitureLookups.FloorFurnitureStateToggle toggle = FurnitureLookups.toggleFloorFurnitureState(
                furnitureId,
                roomId,
                NumberUtils.parseLong(userId),
                furnitureDao(),
                GameDataCaches.productCache(),
                CatalogState.instance().registry(),
                AppPaths.applicationPath());
            if (!toggle.valid()) {
                return;
            }
            RoomState.instance().setFurnitureRoomCache(FurnitureStateWrites.refreshState(
                RoomState.instance().furnitureRoomCache(), roomId, toggle.furnitureId(), toggle.stateValue()));
            SocketDelivery.broadcastToCurrentRoom(socketIndex, toggle.payload());
            if (toggle.hasChargePayload()) {
                SocketDelivery.sendToSocket(socketIndex, toggle.chargePayload());
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_155_795C90.
     */
    public static void pickUpRoomFurniture(int socketIndex, FurnitureWire.FurnitureIdRequest request) {
        try {
            long furnitureId = request.furnitureId();
            if (socketIndex <= 0 || furnitureId <= 0L) {
                return;
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            if (userId.isEmpty() || "0".equals(userId) || roomId <= 0L) {
                return;
            }
            long userIdValue = NumberUtils.parseLong(userId);
            boolean canPickUpAny = UserLookups.hasPermission(userIdValue, "fuse_pick_up_any_furni", userDao(), AppConfigState.instance().permissionMatrix());
            FurnitureLookups.RoomFurniturePickup pickup = FurnitureLookups.pickUpRoomFurniture(
                furnitureId,
                roomId,
                userIdValue,
                RoomLookups.userOwnsRoom(userIdValue, roomId, roomDao()),
                RoomLookups.userHasRoomRight(userIdValue, roomId, roomDao()),
                canPickUpAny,
                furnitureDao());
            if (!pickup.valid()) {
                return;
            }
            if (pickup.moderationLogRequired()) {
                String sessionId = UserLookups.sessionId(userIdValue, userDao());
                StaffModerationDao moderationDao = staffModerationDao();
                if (moderationDao == null) {
                    return;
                }
                moderationDao.insertFurniturePickupLog(userIdValue, roomId, furnitureId, sessionId);
            }
            RoomState.instance().setFurnitureRoomCache(FurnitureStateWrites.removeMarker(
                RoomState.instance().furnitureRoomCache(), roomId, furnitureId));
            SocketDelivery.sendToSocket(socketIndex, pickup.inventoryRemovePayload());
            SocketDelivery.broadcastToCurrentRoom(socketIndex, pickup.removedPayload());
            RoomCacheFiles.invalidateRoom(roomId);
            InventoryPacketHandlers.sendInventoryToSocket(socketIndex);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_141_76A670.
     * Original function: Proc_6_159_79FCD0.
     */
    public static String moveFloorFurnitureInRoom(
        int socketIndex,
        FurnitureWire.FloorPlacementRequest request
    ) {
        return placeOrMoveFloorFurniture(socketIndex, request, false);
    }

    /**
     * Original function: Proc_6_142_76B310.
     */
    public static String placeFloorFurnitureFromInventory(
        int socketIndex,
        FurnitureWire.FloorPlacementRequest request
    ) {
        return placeOrMoveFloorFurniture(socketIndex, request, true);
    }

    /**
     * Original function: Proc_6_96_747000.
     * Original function: Proc_6_97_747640.
     */
    public static String useSimpleFloorItem(
        int socketIndex,
        FurnitureWire.SimpleFloorItemUseRequest request,
        long stateValue,
        boolean storeState,
        RoomUserPosition suppliedPosition
    ) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            if (userId.isEmpty() || "0".equals(userId) || roomId <= 0L) {
                return "";
            }
            long furnitureId = request.furnitureId();
            if (furnitureId <= 0L) {
                return "";
            }
            RoomDao rooms = roomDao();
            if (rooms == null) {
                return "";
            }
            long roomSlot = rooms.roomSlot(roomId);
            RoomUserPosition userPosition = suppliedPosition == null ? RoomUserPosition.absent() : suppliedPosition;
            if (!userPosition.found()) {
                userPosition = RoomUserPosition.from(
                    RoomState.instance().representedRooms().movementPosition(roomSlot, SessionLookups.representedRoomUserIndex(socketIndex, userId)));
            }
            FurnitureLookups.SimpleFloorUse use = FurnitureLookups.simpleFloorUse(
                furnitureId,
                roomId,
                userPosition,
                stateValue,
                storeState,
                furnitureDao(),
                GameDataCaches.productCache());
            if (!use.valid()) {
                return "";
            }
            SocketDelivery.broadcastToCurrentRoom(socketIndex, use.payload());
            if (use.storeState()) {
                RoomState.instance().setFurnitureRoomCache(FurnitureStateWrites.refreshState(
                    RoomState.instance().furnitureRoomCache(), roomId, use.furnitureId(), use.stateValue()));
            } else {
                RoomState.instance().setFurnitureRoomCache(FurnitureStateWrites.trackMarker(
                    RoomState.instance().furnitureRoomCache(), roomId, use.furnitureId()));
            }
            return use.payload();
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_150_777FA0.
     */
    public static Object openFloorFurniturePackageOrToggleState(
        int socketIndex,
        FurnitureWire.FloorFurniturePackageRequest request
    ) {
        try {
            if (socketIndex <= 0) {
                return "";
            }
            String requestPayload = request.requestPayload();
            long furnitureId = request.furnitureId();
            if (furnitureId <= 0L) {
                return "";
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return "";
            }
            FurnitureLookups.FloorFurniturePackageOpen packageOpen =
                FurnitureLookups.openFloorFurniturePackage(furnitureId, roomId, furnitureDao(), packageDao());
            if (packageOpen.petPreviewRequired()) {
                return PetPacketHandlers.sendPackagePreview(socketIndex, PetWire.packagePreviewRequest(requestPayload));
            }
            if (packageOpen.hasPayload()) {
                SocketDelivery.sendToSocket(socketIndex, packageOpen.payload());
                return packageOpen.furnitureId();
            }
            toggleFloorFurnitureState(socketIndex, new FurnitureWire.FurnitureIdRequest(furnitureId));
            return furnitureId;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_157_7974B0.
     */
    private static void placeWallFurnitureFromInventory(
        int socketIndex,
        FurnitureWire.WallFurniturePlacementRequest request,
        FurnitureDao.InventoryPlacementFurniture placementFurniture
    ) {
        try {
            if (socketIndex <= 0) {
                return;
            }
            String wallPayload = request.wallPayload();
            long furnitureId = placementFurniture == null ? 0L : placementFurniture.furnitureId();
            if (furnitureId <= 0L) {
                furnitureId = request.furnitureId();
            }
            if (furnitureId <= 0L) {
                return;
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long userIdValue = NumberUtils.parseLong(userId);
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            if (roomId <= 0L || (!RoomLookups.userHasRoomRight(userIdValue, roomId, roomDao())
                && !UserLookups.hasPermission(userIdValue, "fuse_pick_up_any_furni", userDao(), AppConfigState.instance().permissionMatrix()))) {
                return;
            }
            FurnitureLookups.WallFurniturePlacement placement = FurnitureLookups.placeWallFurnitureFromInventory(
                wallPayload,
                furnitureId,
                roomId,
                userIdValue,
                placementFurniture,
                furnitureDao(),
                GameDataCaches.productCache());
            if (!placement.valid()) {
                return;
            }
            SocketDelivery.sendToSocket(socketIndex, placement.inventoryRemovePayload());
            SocketDelivery.broadcastToCurrentRoom(socketIndex, placement.roomPayload());
            RoomCacheFiles.invalidateRoom(roomId);
            InventoryPacketHandlers.sendInventoryToSocket(socketIndex);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    private static String placeOrMoveFloorFurniture(
        int socketIndex,
        FurnitureWire.FloorPlacementRequest request,
        boolean fromInventory
    ) {
        try {
            FurnitureWire.FloorFurniturePlacement placement = request.placement();
            if (placement.furnitureId() <= 0L) {
                return "";
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long userIdValue = NumberUtils.parseLong(userId);
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            if (roomId <= 0L || (!RoomLookups.userHasRoomRight(userIdValue, roomId, roomDao())
                && !UserLookups.hasPermission(userIdValue, "fuse_pick_up_any_furni", userDao(), AppConfigState.instance().permissionMatrix()))) {
                return "";
            }
            FurnitureLookups.FloorFurniturePlacement placementResult = FurnitureLookups.placeOrMoveFloorFurniture(
                placement, roomId, userIdValue, fromInventory, furnitureDao(), CatalogState.instance().registry());
            if (!placementResult.valid()) {
                return "";
            }
            if (placementResult.wallFurniture()) {
                if (fromInventory) {
                    FurnitureDao furniture = furnitureDao();
                    if (furniture == null) {
                        return "";
                    }
                    FurnitureDao.InventoryPlacementFurniture item = furniture
                        .inventoryPlacementFurniture(placement.furnitureId(), userIdValue)
                        .orElse(null);
                    placeWallFurnitureFromInventory(
                        socketIndex,
                        FurnitureWire.wallFurniturePlacementRequest(request.placementPayload()),
                        item);
                }
                return "";
            }
            if (placementResult.hasInventoryRemovePayload()) {
                SocketDelivery.sendToSocket(socketIndex, placementResult.inventoryRemovePayload());
            }
            SocketDelivery.broadcastToCurrentRoom(socketIndex, placementResult.roomPayload());
            RoomCacheFiles.invalidateRoom(roomId);
            if (fromInventory) {
                InventoryPacketHandlers.sendInventoryToSocket(socketIndex);
            }
            return placementResult.roomPayload();
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    private static FurnitureDao furnitureDao() {
        return DaoProvider.furnitureDao();
    }

    private static PackageDao packageDao() {
        return DaoProvider.packageDao();
    }

    private static RoomDao roomDao() {
        return DaoProvider.roomDao();
    }

    private static StaffModerationDao staffModerationDao() {
        return DaoProvider.staffModerationDao();
    }

    private static UserDao userDao() {
        return DaoProvider.userDao();
    }
}
