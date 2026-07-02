package com.alphaseries.game.room;

import com.alphaseries.dao.mysql.FurnitureDao;
import com.alphaseries.dao.mysql.PackageDao;
import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.game.catalog.CatalogRegistry;
import com.alphaseries.game.catalog.ProductCache;
import com.alphaseries.game.inventory.CreditFurniture;
import com.alphaseries.game.inventory.InventoryMessagePayloads;
import com.alphaseries.messages.outgoing.FurniturePayloads;
import com.alphaseries.messages.outgoing.UserPayloads;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class FurnitureLookups {
    private FurnitureLookups() {
    }

    public record PresentOpenResult(String removedPayload, String responsePayload) {
        public PresentOpenResult {
            removedPayload = StringUtils.text(removedPayload);
            responsePayload = StringUtils.text(responsePayload);
        }

        public static PresentOpenResult empty() {
            return new PresentOpenResult("", "");
        }

        public boolean valid() {
            return !removedPayload.isEmpty() && !responsePayload.isEmpty();
        }
    }

    public record CreditFurnitureRedemption(String creditsPayload, String removedPayload) {
        public CreditFurnitureRedemption {
            creditsPayload = StringUtils.text(creditsPayload);
            removedPayload = StringUtils.text(removedPayload);
        }

        public static CreditFurnitureRedemption empty() {
            return new CreditFurnitureRedemption("", "");
        }

        public boolean valid() {
            return !creditsPayload.isEmpty() && !removedPayload.isEmpty();
        }
    }

    public record RoomDecorationApplication(String roomPayload, String inventoryRemovePayload) {
        public RoomDecorationApplication {
            roomPayload = StringUtils.text(roomPayload);
            inventoryRemovePayload = StringUtils.text(inventoryRemovePayload);
        }

        public static RoomDecorationApplication empty() {
            return new RoomDecorationApplication("", "");
        }

        public boolean valid() {
            return !roomPayload.isEmpty() && !inventoryRemovePayload.isEmpty();
        }
    }

    public record FurnitureInventoryReturn(String removedPayload) {
        public FurnitureInventoryReturn {
            removedPayload = StringUtils.text(removedPayload);
        }

        public static FurnitureInventoryReturn empty() {
            return new FurnitureInventoryReturn("");
        }

        public boolean valid() {
            return !removedPayload.isEmpty();
        }
    }

    public record RoomFurniturePickup(
        String inventoryRemovePayload,
        String removedPayload,
        boolean moderationLogRequired
    ) {
        public RoomFurniturePickup {
            inventoryRemovePayload = StringUtils.text(inventoryRemovePayload);
            removedPayload = StringUtils.text(removedPayload);
        }

        public static RoomFurniturePickup empty() {
            return new RoomFurniturePickup("", "", false);
        }

        public boolean valid() {
            return !inventoryRemovePayload.isEmpty() && !removedPayload.isEmpty();
        }
    }

    public record WallFurniturePlacement(String inventoryRemovePayload, String roomPayload) {
        public WallFurniturePlacement {
            inventoryRemovePayload = StringUtils.text(inventoryRemovePayload);
            roomPayload = StringUtils.text(roomPayload);
        }

        public static WallFurniturePlacement empty() {
            return new WallFurniturePlacement("", "");
        }

        public boolean valid() {
            return !inventoryRemovePayload.isEmpty() && !roomPayload.isEmpty();
        }
    }

    public record FloorFurniturePlacement(String inventoryRemovePayload, String roomPayload, boolean wallFurniture) {
        public FloorFurniturePlacement {
            inventoryRemovePayload = StringUtils.text(inventoryRemovePayload);
            roomPayload = StringUtils.text(roomPayload);
        }

        public static FloorFurniturePlacement empty() {
            return new FloorFurniturePlacement("", "", false);
        }

        public static FloorFurniturePlacement wallFurnitureResult() {
            return new FloorFurniturePlacement("", "", true);
        }

        public boolean valid() {
            return wallFurniture || !roomPayload.isEmpty();
        }

        public boolean hasInventoryRemovePayload() {
            return !inventoryRemovePayload.isEmpty();
        }
    }

    public record LocatedFurnitureStateRefresh(
        long roomId,
        long furnitureId,
        long stateValue,
        String payload,
        boolean clearSoundMarkers
    ) {
        public LocatedFurnitureStateRefresh {
            payload = StringUtils.text(payload);
        }

        public static LocatedFurnitureStateRefresh empty() {
            return new LocatedFurnitureStateRefresh(0L, 0L, 0L, "", false);
        }

        public boolean valid() {
            return roomId > 0L && furnitureId > 0L && !payload.isEmpty();
        }
    }

    public record FloorFurnitureStateToggle(
        long furnitureId,
        long stateValue,
        String payload,
        String chargePayload
    ) {
        public FloorFurnitureStateToggle {
            payload = StringUtils.text(payload);
            chargePayload = StringUtils.text(chargePayload);
        }

        public static FloorFurnitureStateToggle empty() {
            return new FloorFurnitureStateToggle(0L, 0L, "", "");
        }

        public boolean valid() {
            return furnitureId > 0L && !payload.isEmpty();
        }

        public boolean hasChargePayload() {
            return !chargePayload.isEmpty();
        }
    }

    public record FloorPositionStateRefresh(long roomId, long furnitureId, long stateValue, String payload) {
        public FloorPositionStateRefresh {
            payload = StringUtils.text(payload);
        }

        public boolean valid() {
            return roomId > 0L && furnitureId > 0L && !payload.isEmpty();
        }
    }

    public record FloorFurniturePackageOpen(long furnitureId, String payload, boolean petPreviewRequired) {
        public FloorFurniturePackageOpen {
            payload = StringUtils.text(payload);
        }

        public static FloorFurniturePackageOpen empty() {
            return new FloorFurniturePackageOpen(0L, "", false);
        }

        public boolean hasPayload() {
            return furnitureId > 0L && !payload.isEmpty();
        }
    }

    public record SimpleFloorUse(long furnitureId, long stateValue, boolean storeState, String payload) {
        public SimpleFloorUse {
            payload = StringUtils.text(payload);
        }

        public static SimpleFloorUse empty() {
            return new SimpleFloorUse(0L, 0L, false, "");
        }

        public boolean valid() {
            return furnitureId > 0L && !payload.isEmpty();
        }
    }

    public static long dimmerFurnitureId(long roomId, FurnitureDao furniture) {
        if (roomId <= 0L || furniture == null) {
            return 0L;
        }
        try {
            return furniture.dimmerFurnitureId(roomId);
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static boolean existsInRoom(long roomId, long furnitureId, FurnitureDao furniture) {
        if (roomId <= 0L || furnitureId <= 0L || furniture == null) {
            return false;
        }
        try {
            return furniture.existsInRoom(furnitureId, roomId);
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean selectedItemsExistInRoom(long roomId, List<Long> selectedFurnitureIds, FurnitureDao furniture) {
        if (roomId <= 0L) {
            return false;
        }
        List<Long> selectedIds = selectedFurnitureIds == null ? List.of() : selectedFurnitureIds;
        for (long furnitureId : selectedIds) {
            if (furnitureId > 0L && !existsInRoom(roomId, furnitureId, furniture)) {
                return false;
            }
        }
        return true;
    }

    public static long habbowheelFurnitureId(
        String requestPayload,
        long roomId,
        FurnitureDao furniture,
        ProductCache productCache
    ) {
        if (roomId <= 0L || furniture == null || productCache == null) {
            return 0L;
        }
        long furnitureId = FurnitureWire.habbowheelFurnitureId(requestPayload);
        if (furnitureId <= 0L) {
            return 0L;
        }
        try {
            FurnitureDao.SimpleFloorFurniture item = furniture.simpleFloorFurniture(furnitureId, roomId).orElse(null);
            if (item == null) {
                return 0L;
            }
            String productAction = productCache.primarySprite(item.productId());
            return "habbowheel".equals(productAction) ? furnitureId : 0L;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    /**
     * Original functions: Proc_6_96_747000 and Proc_6_97_747640.
     */
    public static SimpleFloorUse simpleFloorUse(
        long furnitureId,
        long roomId,
        RoomUserPosition userPosition,
        long stateValue,
        boolean storeState,
        FurnitureDao furniture,
        ProductCache productCache
    ) {
        try {
            if (furnitureId <= 0L || roomId <= 0L || furniture == null || productCache == null) {
                return SimpleFloorUse.empty();
            }
            FurnitureDao.SimpleFloorFurniture item = furniture.simpleFloorFurniture(furnitureId, roomId).orElse(null);
            if (item == null || item.productId() <= 0L || productCache.type(item.productId()) != 0L) {
                return SimpleFloorUse.empty();
            }
            if (userPosition != null && userPosition.found()
                && (Math.abs(userPosition.positionX() - item.positionX()) > 2L
                    || Math.abs(userPosition.positionY() - item.positionY()) > 2L)) {
                return SimpleFloorUse.empty();
            }
            return new SimpleFloorUse(
                furnitureId,
                stateValue,
                storeState,
                FurniturePayloads.simpleFloorUse(furnitureId, stateValue));
        } catch (Exception ignored) {
            return SimpleFloorUse.empty();
        }
    }

    public static String modelFurniturePayload(long modelId, RoomDao rooms) {
        if (modelId <= 0L || rooms == null) {
            return "";
        }
        try {
            return FurniturePayloads.floorList(rooms.modelFurnitureRows(modelId));
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_83_732640.
     */
    public static String modelFurniturePayloadForRoom(long modelId, long roomId, RoomDao rooms) {
        try {
            if (rooms == null) {
                return "";
            }
            long resolvedModelId = modelId;
            if (resolvedModelId <= 0L && roomId > 0L) {
                resolvedModelId = rooms.modelIdByRoom(roomId);
            }
            return modelFurniturePayload(resolvedModelId, rooms);
        } catch (Exception ignored) {
            return "";
        }
    }

    public static String wallFurniturePayload(long roomId, FurnitureDao furniture) {
        if (roomId <= 0L || furniture == null) {
            return "";
        }
        try {
            return FurniturePayloads.wallList(furniture.wallFurnitureInRoom(roomId));
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_66_721D60.
     */
    public static String updateStickyNotePayload(
        FurnitureWire.StickyNoteUpdate note,
        long roomId,
        FurnitureDao furniture,
        ProductCache productCache
    ) {
        try {
            if (note == null || note.furnitureId() <= 0L || roomId <= 0L || furniture == null || productCache == null) {
                return "";
            }
            FurnitureDao.RoomFurnitureWithWall sticky = furniture.roomFurnitureWithWall(note.furnitureId(), roomId).orElse(null);
            if (sticky == null || !productCache.isPostItProduct(sticky.productId())) {
                return "";
            }
            furniture.updatePostIt(note.furnitureId(), note.noteColor(), note.noteCaption());
            return FurniturePayloads.stickyNoteUpdated(note.furnitureId(), sticky.productId(), note.noteColor());
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_67_722940.
     */
    public static String stickyNotePayload(
        long furnitureId,
        long roomId,
        FurnitureDao furniture,
        ProductCache productCache
    ) {
        try {
            if (furnitureId <= 0L || roomId <= 0L || furniture == null || productCache == null) {
                return "";
            }
            FurnitureDao.RoomFurniture sticky = furniture.roomFurniture(furnitureId, roomId).orElse(null);
            if (sticky == null || !productCache.isPostItProduct(sticky.productId())) {
                return "";
            }
            String noteColor = StringUtils.left(sticky.sign(), 6);
            if (noteColor.isEmpty()) {
                noteColor = "FFFF33";
            }
            String noteCaption = StringUtils.text(sticky.caption()).replace('\u001f', '\r');
            return "@p" + furnitureId + '\2' + noteColor + '\r' + noteCaption + '\2';
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_68_723170.
     */
    public static String deleteStickyNotePayload(
        long furnitureId,
        long roomId,
        FurnitureDao furniture,
        ProductCache productCache
    ) {
        try {
            if (furnitureId <= 0L || roomId <= 0L || furniture == null || productCache == null) {
                return "";
            }
            FurnitureDao.RoomFurniture sticky = furniture.roomFurniture(furnitureId, roomId).orElse(null);
            if (sticky == null || !productCache.isPostItProduct(sticky.productId())) {
                return "";
            }
            furniture.deleteFurniture(furnitureId);
            return "AT" + furnitureId;
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_69_723630.
     */
    public static PresentOpenResult openPresent(
        long furnitureId,
        long roomId,
        long ownerId,
        FurnitureDao furniture,
        ProductCache productCache
    ) {
        try {
            if (furnitureId <= 0L || roomId <= 0L || ownerId <= 0L || furniture == null || productCache == null) {
                return PresentOpenResult.empty();
            }
            FurnitureDao.GiftBoxFurniture giftBox = furniture.giftBox(furnitureId, roomId).orElse(null);
            if (giftBox == null || giftBox.boxProductId() <= 0L || giftBox.openedProductId() <= 0L) {
                return PresentOpenResult.empty();
            }
            String boxAction = productCache.primarySprite(giftBox.boxProductId()).toLowerCase();
            if (!boxAction.contains("present_") || "ecotron_box".equals(boxAction)) {
                return PresentOpenResult.empty();
            }
            furniture.deleteFurniture(furnitureId);
            furniture.insertInventoryFurniture(giftBox.openedProductId(), ownerId, giftBox.openedSign());
            String responseClass = presentResponseClass(productCache.type(giftBox.openedProductId()));
            return new PresentOpenResult(
                FurniturePayloads.floorItemRemovedWithState(furnitureId, "H"),
                FurniturePayloads.presentOpened(
                    giftBox.openedProductId(),
                    responseClass,
                    productCache.itemData(giftBox.openedProductId())));
        } catch (Exception ignored) {
            return PresentOpenResult.empty();
        }
    }

    /**
     * Original function: Proc_6_73_725540.
     */
    public static CreditFurnitureRedemption redeemCreditFurniture(
        long furnitureId,
        long roomId,
        long userId,
        FurnitureDao furniture,
        UserDao users,
        ProductCache productCache
    ) {
        try {
            if (furnitureId <= 0L || roomId <= 0L || userId <= 0L
                || furniture == null || users == null || productCache == null) {
                return CreditFurnitureRedemption.empty();
            }
            FurnitureDao.RoomFurnitureProduct furnitureProduct =
                furniture.roomFurnitureProduct(furnitureId, roomId).orElse(null);
            if (furnitureProduct == null || furnitureProduct.productId() <= 0L) {
                return CreditFurnitureRedemption.empty();
            }
            long productId = furnitureProduct.productId();
            String productSprite = productCache.primarySprite(productId);
            if (productSprite.isEmpty()) {
                productSprite = productCache.alternateSprite(productId);
            }
            CreditFurniture creditFurniture = CreditFurniture.fromSprite(productSprite);
            if (!creditFurniture.redeemable()) {
                return CreditFurnitureRedemption.empty();
            }
            users.addCredits(userId, creditFurniture.value());
            long updatedCredits = users.credits(userId);
            furniture.deleteFurniture(furnitureId);
            return new CreditFurnitureRedemption(
                UserPayloads.creditsRefresh(updatedCredits),
                FurniturePayloads.floorItemRemovedWithState(furnitureId, "H"));
        } catch (Exception ignored) {
            return CreditFurnitureRedemption.empty();
        }
    }

    /**
     * Original function: Proc_6_139_768100.
     */
    public static RoomDecorationApplication applyRoomDecorationFurniture(
        long furnitureId,
        long roomId,
        long userId,
        FurnitureDao furniture,
        RoomDao rooms,
        CatalogRegistry catalogRegistry
    ) {
        try {
            if (furnitureId <= 0L || roomId <= 0L || userId <= 0L
                || furniture == null || rooms == null || catalogRegistry == null) {
                return RoomDecorationApplication.empty();
            }
            FurnitureDao.DecorationFurniture decorationFurniture = furniture
                .decorationFurniture(furnitureId, userId)
                .orElse(null);
            if (decorationFurniture == null) {
                return RoomDecorationApplication.empty();
            }
            CatalogRegistry.Product product = catalogRegistry.product(decorationFurniture.productId()).orElse(null);
            if (product == null) {
                return RoomDecorationApplication.empty();
            }
            RoomDao.RoomDecoration decoration = RoomDao.RoomDecoration.fromProductType(product.type());
            if (decoration == null) {
                return RoomDecorationApplication.empty();
            }
            String decorationValue = StringUtils.text(decorationFurniture.sign());
            if (decorationValue.isEmpty() || "0".equals(decorationValue)) {
                decorationValue = product.defaultDecoration();
            }
            if (decorationValue.isEmpty()) {
                decorationValue = product.sprite();
            }
            if (decorationValue.isEmpty()) {
                return RoomDecorationApplication.empty();
            }
            rooms.updateDecoration(roomId, decoration, decorationValue);
            furniture.deleteFurniture(furnitureId);
            return new RoomDecorationApplication(
                "@n" + decoration.wireName() + '\2' + decorationValue + '\2',
                InventoryMessagePayloads.remove(furnitureId));
        } catch (Exception ignored) {
            return RoomDecorationApplication.empty();
        }
    }

    /**
     * Original function: Proc_6_144_76BE70.
     */
    public static FurnitureInventoryReturn returnRoomFurnitureToInventory(
        long furnitureId,
        long roomId,
        long userId,
        boolean userOwnsRoom,
        boolean userHasRoomRight,
        boolean canPickUpAnyFurniture,
        FurnitureDao furniture
    ) {
        try {
            if (furnitureId <= 0L || roomId <= 0L || userId <= 0L || furniture == null) {
                return FurnitureInventoryReturn.empty();
            }
            FurnitureDao.RoomFurnitureOwnerProduct furnitureProduct = furniture
                .roomFurnitureOwnerProduct(furnitureId, roomId)
                .orElse(null);
            if (furnitureProduct == null || furnitureProduct.productId() <= 0L || furnitureProduct.ownerId() <= 0L) {
                return FurnitureInventoryReturn.empty();
            }
            boolean isOwner = furnitureProduct.ownerId() == userId;
            if (!isOwner && !userOwnsRoom && !canPickUpAnyFurniture) {
                return FurnitureInventoryReturn.empty();
            }
            if (!userHasRoomRight && !isOwner && !canPickUpAnyFurniture) {
                return FurnitureInventoryReturn.empty();
            }
            furniture.moveRoomFurnitureToInventory(furnitureId, userId);
            return new FurnitureInventoryReturn(FurniturePayloads.floorItemRemoved(furnitureId));
        } catch (Exception ignored) {
            return FurnitureInventoryReturn.empty();
        }
    }

    /**
     * Original function: Proc_6_155_795C90.
     */
    public static RoomFurniturePickup pickUpRoomFurniture(
        long furnitureId,
        long roomId,
        long userId,
        boolean userOwnsRoom,
        boolean userHasRoomRight,
        boolean canPickUpAnyFurniture,
        FurnitureDao furniture
    ) {
        try {
            if (furnitureId <= 0L || roomId <= 0L || userId <= 0L || furniture == null) {
                return RoomFurniturePickup.empty();
            }
            FurnitureDao.RoomFurnitureOwnerProduct furnitureProduct = furniture
                .roomFurnitureOwnerProduct(furnitureId, roomId)
                .orElse(null);
            if (furnitureProduct == null || furnitureProduct.productId() <= 0L || furnitureProduct.ownerId() <= 0L) {
                return RoomFurniturePickup.empty();
            }
            boolean isOwner = furnitureProduct.ownerId() == userId;
            if (!isOwner && !userOwnsRoom && !canPickUpAnyFurniture) {
                return RoomFurniturePickup.empty();
            }
            if (!userHasRoomRight && !isOwner && !canPickUpAnyFurniture) {
                return RoomFurniturePickup.empty();
            }
            furniture.moveRoomFurnitureToInventory(furnitureId, roomId, userId);
            return new RoomFurniturePickup(
                InventoryMessagePayloads.remove(furnitureId),
                FurniturePayloads.floorItemRemoved(furnitureId),
                !isOwner);
        } catch (Exception ignored) {
            return RoomFurniturePickup.empty();
        }
    }

    /**
     * Original function: Proc_6_157_7974B0.
     */
    public static WallFurniturePlacement placeWallFurnitureFromInventory(
        String wallPayload,
        long furnitureId,
        long roomId,
        long userId,
        FurnitureDao.InventoryPlacementFurniture placementFurniture,
        FurnitureDao furniture,
        ProductCache productCache
    ) {
        try {
            if (furnitureId <= 0L || roomId <= 0L || userId <= 0L || furniture == null || productCache == null) {
                return WallFurniturePlacement.empty();
            }
            if (placementFurniture == null || placementFurniture.productId() <= 0L) {
                placementFurniture = furniture.inventoryPlacementFurniture(furnitureId, userId).orElse(null);
            }
            long productId = placementFurniture == null ? 0L : placementFurniture.productId();
            if (productId <= 0L || productCache.type(productId) != 9L) {
                return WallFurniturePlacement.empty();
            }
            WallPlacement placement = RoomWire.wallPlacementFromPayload(wallPayload);
            if (!placement.valid()) {
                return WallFurniturePlacement.empty();
            }
            String wallPosition = StringUtils.sqlEscapedText((":w=" + placement.wallX() + "," + placement.wallY()
                + " l=" + placement.localX() + "," + placement.localY()).toLowerCase());
            furniture.placeWallFurniture(furnitureId, userId, roomId, wallPosition);
            String payload = FurniturePayloads.wallInventoryPlacement(
                furnitureId,
                productId,
                wallPosition,
                placementFurniture.sign(),
                placementFurniture.secondaryValue());
            if (payload.isEmpty()) {
                return WallFurniturePlacement.empty();
            }
            return new WallFurniturePlacement(InventoryMessagePayloads.remove(furnitureId), "AS" + payload);
        } catch (Exception ignored) {
            return WallFurniturePlacement.empty();
        }
    }

    /**
     * Original functions: Proc_6_141_76A670 and Proc_6_142_76B310.
     */
    public static FloorFurniturePlacement placeOrMoveFloorFurniture(
        FurnitureWire.FloorFurniturePlacement placement,
        long roomId,
        long userId,
        boolean fromInventory,
        FurnitureDao furniture,
        CatalogRegistry catalogRegistry
    ) {
        try {
            if (placement == null || placement.furnitureId() <= 0L || roomId <= 0L || userId <= 0L
                || furniture == null || catalogRegistry == null) {
                return FloorFurniturePlacement.empty();
            }
            FurnitureDao.InventoryPlacementFurniture item = fromInventory
                ? furniture.inventoryPlacementFurniture(placement.furnitureId(), userId).orElse(null)
                : furniture.roomPlacementFurniture(placement.furnitureId(), roomId).orElse(null);
            if (item == null || item.productId() <= 0L) {
                return FloorFurniturePlacement.empty();
            }
            CatalogRegistry.Product product = catalogRegistry.product(item.productId()).orElse(null);
            if (product == null) {
                return FloorFurniturePlacement.empty();
            }
            if (product.type() == 9L) {
                return FloorFurniturePlacement.wallFurnitureResult();
            }
            String positionZ = String.valueOf(product.squareZ());
            if (fromInventory) {
                furniture.placeFloorFurniture(
                    placement.furnitureId(),
                    userId,
                    roomId,
                    placement.positionX(),
                    placement.positionY(),
                    positionZ,
                    placement.rotation());
            } else {
                furniture.moveFloorFurniture(
                    placement.furnitureId(),
                    roomId,
                    userId,
                    placement.positionX(),
                    placement.positionY(),
                    positionZ,
                    placement.rotation());
            }
            String placementPayload = FurniturePayloads.floorPlacement(
                placement.furnitureId(),
                placement.positionX(),
                placement.positionY(),
                placement.rotation(),
                NumberUtils.parseLong(positionZ),
                "",
                item.sign(),
                item.secondaryValue(),
                item.productId());
            if (placementPayload.isEmpty()) {
                return FloorFurniturePlacement.empty();
            }
            return new FloorFurniturePlacement(
                fromInventory ? InventoryMessagePayloads.remove(placement.furnitureId()) : "",
                (fromInventory ? "A]" : "A_") + placementPayload,
                false);
        } catch (Exception ignored) {
            return FloorFurniturePlacement.empty();
        }
    }

    /**
     * Original function: Proc_6_154_78F040.
     */
    public static LocatedFurnitureStateRefresh refreshLocatedFurnitureState(
        long furnitureId,
        long productId,
        FurnitureDao furniture,
        ProductCache productCache
    ) {
        try {
            if (furnitureId <= 0L || furniture == null || productCache == null) {
                return LocatedFurnitureStateRefresh.empty();
            }
            FurnitureDao.RoomFurnitureState furnitureState = furniture.roomFurnitureState(furnitureId).orElse(null);
            if (furnitureState == null) {
                return LocatedFurnitureStateRefresh.empty();
            }
            long roomId = furnitureState.roomId();
            long resolvedProductId = productId <= 0L ? furnitureState.productId() : productId;
            if (roomId <= 0L || resolvedProductId <= 0L) {
                return LocatedFurnitureStateRefresh.empty();
            }
            long productType = productCache.type(resolvedProductId);
            String productSprite = productCache.primarySprite(resolvedProductId);
            if (productSprite.isEmpty()) {
                productSprite = productCache.alternateSprite(resolvedProductId);
            }
            long stateValue = NumberUtils.parseLong(furnitureState.sign());
            String lowerSprite = productSprite.toLowerCase();
            if ((lowerSprite.startsWith("bb_score_") || lowerSprite.startsWith("es_score_")) && stateValue < 0L) {
                stateValue = 0L;
            }
            return new LocatedFurnitureStateRefresh(
                roomId,
                furnitureId,
                stateValue,
                FurniturePayloads.stateChanged(furnitureId, stateValue),
                productType == 11L || lowerSprite.contains("soundmachine") || lowerSprite.contains("jukebox"));
        } catch (Exception ignored) {
            return LocatedFurnitureStateRefresh.empty();
        }
    }

    /**
     * Original function: Proc_6_149_775C10.
     */
    public static FloorFurnitureStateToggle toggleFloorFurnitureState(
        long furnitureId,
        long roomId,
        long userId,
        FurnitureDao furniture,
        ProductCache productCache,
        CatalogRegistry catalogRegistry,
        String applicationPath
    ) {
        try {
            if (furnitureId <= 0L || roomId <= 0L || userId <= 0L || furniture == null || productCache == null) {
                return FloorFurnitureStateToggle.empty();
            }
            FurnitureDao.FloorStateFurniture stateFurniture = furniture.floorStateFurniture(furnitureId, roomId)
                .orElse(null);
            if (stateFurniture == null || stateFurniture.productId() <= 0L) {
                return FloorFurnitureStateToggle.empty();
            }
            long productId = stateFurniture.productId();
            if (productCache.type(productId) == 9L) {
                return FloorFurnitureStateToggle.empty();
            }
            String productSprite = productCache.primarySprite(productId).toLowerCase();
            if (productSprite.isEmpty()) {
                productSprite = productCache.alternateSprite(productId).toLowerCase();
            }
            long nextState = FurnitureWire.nextState(
                productSprite,
                NumberUtils.parseLong(stateFurniture.sign()),
                productCache.maxState(productId));
            furniture.updateRoomFurnitureState(furnitureId, roomId, userId, nextState);
            String chargePayload = "";
            if (productCache.hasCharges(productId) && catalogRegistry != null) {
                chargePayload = FurnitureCharges.consumeOrPrompt(
                    furnitureId,
                    catalogRegistry.product(productId).orElse(null),
                    applicationPath);
            }
            return new FloorFurnitureStateToggle(
                furnitureId,
                nextState,
                FurniturePayloads.stateChanged(furnitureId, nextState),
                chargePayload);
        } catch (Exception ignored) {
            return FloorFurnitureStateToggle.empty();
        }
    }

    /**
     * Original function: Proc_6_147_76E910.
     */
    public static List<FloorPositionStateRefresh> floorStateRefreshesAtPosition(
        long roomId,
        long positionX,
        long positionY,
        FurnitureDao furniture,
        ProductCache productCache
    ) {
        try {
            if (positionX <= 0L || positionY <= 0L || furniture == null || productCache == null) {
                return List.of();
            }
            List<FurnitureDao.FloorPositionFurniture> rows = roomId > 0L
                ? furniture.floorFurnitureAt(roomId, positionX, positionY)
                : furniture.floorFurnitureAt(positionX, positionY);
            List<FloorPositionStateRefresh> refreshes = new ArrayList<>();
            for (FurnitureDao.FloorPositionFurniture row : rows) {
                long furnitureId = row.furnitureId();
                long rowRoomId = roomId > 0L ? roomId : row.roomId();
                long productId = row.productId();
                if (furnitureId <= 0L || rowRoomId <= 0L || productId <= 0L) {
                    continue;
                }
                String productAction = productCache.interactionAction(productId).toLowerCase();
                String productSprite = productCache.primarySprite(productId).toLowerCase();
                if (productSprite.isEmpty()) {
                    productSprite = productCache.alternateSprite(productId).toLowerCase();
                }
                if (productAction.isEmpty() || productAction.contains("switch") || productAction.contains("click")
                    || productAction.contains("score") || productSprite.contains("score") || productSprite.contains("dice")) {
                    long stateValue = NumberUtils.parseLong(row.sign());
                    refreshes.add(new FloorPositionStateRefresh(
                        rowRoomId,
                        furnitureId,
                        stateValue,
                        FurniturePayloads.stateChanged(furnitureId, stateValue)));
                }
            }
            return List.copyOf(refreshes);
        } catch (Exception ignored) {
            return List.of();
        }
    }

    /**
     * Original function: Proc_6_150_777FA0.
     */
    public static FloorFurniturePackageOpen openFloorFurniturePackage(
        long furnitureId,
        long roomId,
        FurnitureDao furniture,
        PackageDao packages
    ) {
        try {
            if (furnitureId <= 0L || roomId <= 0L || furniture == null || packages == null) {
                return FloorFurniturePackageOpen.empty();
            }
            FurnitureDao.FloorStateFurniture stateFurniture = furniture.floorStateFurniture(furnitureId, roomId)
                .orElse(null);
            if (stateFurniture == null || stateFurniture.productId() <= 0L) {
                return FloorFurniturePackageOpen.empty();
            }
            long productId = stateFurniture.productId();
            PackageDao.PackageRow packageRow = packages.packageByProduct(productId).orElse(null);
            String packageType = packageRow == null ? "" : StringUtils.text(packageRow.secondaryType()).toLowerCase();
            long containedId = packageRow == null ? 0L : packageRow.containedId();
            if ("packages_pets".equals(packageType) && containedId > 0L) {
                return new FloorFurniturePackageOpen(furnitureId, "", true);
            }
            if (!packageType.isEmpty()) {
                return new FloorFurniturePackageOpen(
                    furnitureId,
                    FurniturePayloads.packageOpened(productId, furnitureId, packageType),
                    false);
            }
            return FloorFurniturePackageOpen.empty();
        } catch (Exception ignored) {
            return FloorFurniturePackageOpen.empty();
        }
    }

    /**
     * Original function: Proc_6_70_724190.
     */
    public static String toggleWallFurnitureStatePayload(
        long furnitureId,
        long roomId,
        FurnitureDao furniture,
        CatalogRegistry catalogRegistry,
        ProductCache productCache
    ) {
        try {
            if (furnitureId <= 0L || roomId <= 0L || furniture == null || productCache == null) {
                return "";
            }
            FurnitureDao.WallStateFurniture wallState = furniture.wallState(furnitureId, roomId).orElse(null);
            if (wallState == null || wallState.productId() <= 0L) {
                return "";
            }
            long productId = wallState.productId();
            long currentState = NumberUtils.parseLong(wallState.sign());
            long stateCount = catalogRegistry == null ? 0L : NumberUtils.parseLong(catalogRegistry.productCell(productId, 5));
            if (stateCount <= 0L) {
                stateCount = productCache.stateCount(productId);
            }
            if (stateCount <= 0L) {
                stateCount = 1L;
            }
            long nextState = currentState + 1L;
            if (nextState > stateCount) {
                nextState = 0L;
            }
            if (nextState < 0L) {
                nextState = 0L;
            }
            furniture.updateSign(furnitureId, nextState);
            return FurniturePayloads.wallState(furnitureId, productId, String.valueOf(nextState), "0");
        } catch (Exception ignored) {
            return "";
        }
    }

    private static String presentResponseClass(long openedProductType) {
        if (openedProductType == 2L) {
            return "s";
        }
        if (openedProductType == 3L) {
            return "e";
        }
        return "i";
    }
}
