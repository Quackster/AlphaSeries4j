package com.alphaseries.game.catalog;

import com.alphaseries.dao.mysql.CatalogDao;
import com.alphaseries.dao.mysql.ClubDao;
import com.alphaseries.dao.mysql.DaoProvider;
import com.alphaseries.dao.mysql.FurnitureDao;
import com.alphaseries.game.GameDataCaches;
import com.alphaseries.game.inventory.InventoryPacketHandlers;
import com.alphaseries.messages.outgoing.CatalogPayloads;
import com.alphaseries.messages.outgoing.ClubPayloads;
import com.alphaseries.server.runtime.SessionLookups;
import com.alphaseries.server.runtime.SocketDelivery;
import com.alphaseries.util.NumberUtils;

public final class ClubPacketHandlers {
    private ClubPacketHandlers() {
    }

    /**
     * Original function: Proc_6_18_6E7480.
     */
    public static void sendSubscriptionOffers(int socketIndex) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (socketIndex <= 0 || userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            ClubDao clubDao = clubDao();
            if (clubDao == null) {
                return;
            }
            ClubDao.UserClubStatus status = clubDao.userClubStatus(NumberUtils.parseLong(userId))
                .orElse(new ClubDao.UserClubStatus(0L, 0L, 0L, 0L, 0L, 0L, 0L));
            SocketDelivery.sendToSocket(socketIndex, ClubPayloads.subscriptionOffers(clubDao.clubProductRows(), status));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_131_75C700.
     */
    public static void sendGiftStatus(int socketIndex) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (socketIndex <= 0 || userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            ClubDao clubs = clubDao();
            ClubDao.ClubGiftStatus status = clubs == null
                ? new ClubDao.ClubGiftStatus(0L, 0L, 0L, 0L, 0L)
                : clubs.clubGiftStatus(NumberUtils.parseLong(userId))
                    .orElse(new ClubDao.ClubGiftStatus(0L, 0L, 0L, 0L, 0L));
            SocketDelivery.sendToSocket(socketIndex,
                ClubPayloads.clubGiftStatus(CatalogState.instance().giftSettings(), status));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_130_75B770.
     */
    public static String claimGift(int socketIndex, CatalogWire.ClubGiftClaimRequest request) {
        try {
            String requestedSprite = request.requestedSprite();
            if (requestedSprite.isEmpty()) {
                return "";
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (socketIndex <= 0 || userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            CatalogDao catalog = catalogDao();
            long catalogProductId = catalog == null ? 0L : catalog.idBySprite(requestedSprite);
            if (catalogProductId <= 0L) {
                return "";
            }
            GiftSettings.ClubGift gift = CatalogState.instance().giftSettings().clubGiftByCatalogProductId(catalogProductId);
            long productId = gift.productId();
            long requiredDays = gift.requiredDays();
            if (productId <= 0L) {
                return "";
            }
            long userIdValue = NumberUtils.parseLong(userId);
            ClubDao clubs = clubDao();
            ClubDao.ClubGiftStatus status = clubs == null ? null : clubs.clubGiftStatus(userIdValue).orElse(null);
            if (status == null) {
                return "";
            }
            if (status.presentsAvailable() <= 0L || status.activeDays() < requiredDays) {
                return "";
            }
            String itemData = GameDataCaches.productCache().itemData(productId);
            FurnitureDao furniture = furnitureDao();
            if (furniture == null) {
                return "";
            }
            furniture.insertClubGiftFurniture(productId, catalogProductId, userIdValue, itemData);
            long insertedFurnitureId = furniture.newestFurnitureIdByOwnerAndProduct(userIdValue, productId);
            String itemClass = GameDataCaches.productCache().type(productId) == 9L ? "I" : "i";
            String responsePayload = CatalogPayloads.clubGiftClaim(productId,
                GameDataCaches.productCache().itemData(productId), itemClass, insertedFurnitureId);
            SocketDelivery.sendToSocket(socketIndex, responsePayload);
            clubs.decrementPresents(userIdValue);
            InventoryPacketHandlers.sendInventoryToSocket(socketIndex);
            return responsePayload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    private static ClubDao clubDao() {
        return DaoProvider.clubDao();
    }

    private static CatalogDao catalogDao() {
        return DaoProvider.catalogDao();
    }

    private static FurnitureDao furnitureDao() {
        return DaoProvider.furnitureDao();
    }
}
