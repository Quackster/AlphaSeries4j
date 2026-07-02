package com.alphaseries.game.catalog;

import com.alphaseries.config.AppConfigState;
import com.alphaseries.dao.mysql.CatalogDao;
import com.alphaseries.dao.mysql.ClubDao;
import com.alphaseries.dao.mysql.DaoProvider;
import com.alphaseries.dao.mysql.FurnitureDao;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.dao.mysql.VoucherDao;
import com.alphaseries.game.GameDataCaches;
import com.alphaseries.game.achievement.AchievementPacketHandlers;
import com.alphaseries.game.inventory.InventoryMessagePayloads;
import com.alphaseries.game.inventory.InventoryPacketHandlers;
import com.alphaseries.game.recycler.RecyclerRewards;
import com.alphaseries.game.social.SocialLookups;
import com.alphaseries.game.social.SocialPacketHandlers;
import com.alphaseries.game.user.UserLookups;
import com.alphaseries.game.user.UserRefreshService;
import com.alphaseries.messages.outgoing.CatalogPayloads;
import com.alphaseries.server.runtime.SessionLookups;
import com.alphaseries.server.runtime.SocketDelivery;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class CatalogPacketHandlers {
    private CatalogPacketHandlers() {
    }

    /**
     * Original function: Proc_6_134_765B90.
     */
    public static void sendGiftAvailability(int socketIndex, CatalogWire.GiftAvailabilityRequest request) {
        try {
            long itemId = request.itemId();
            CatalogRegistry.CatalogProduct catalogProduct =
                CatalogState.instance().registry().catalogProduct(itemId).orElse(null);
            long itemType = catalogProduct == null ? 0L : catalogProduct.activityType();
            long giftEnabled = itemType == 1L
                ? AppConfigState.instance().settingsCache().longValueOrDefault("com.client.catalog.gifts.enabled", 0)
                : 0L;
            SocketDelivery.sendToSocket(socketIndex, CatalogPayloads.giftAvailability(itemId, giftEnabled));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_135_765D80.
     */
    public static void sendGiftWrapOptions(int socketIndex) {
        try {
            String defaultPayload = CatalogPayloads.giftWrapPriceFallback(
                AppConfigState.instance().settingsCache().longValueOrDefault("com.client.catalog.gifts.wrap.enabled", 0));
            long giftWrapPrice = NumberUtils.parseLong(AppConfigState.instance().settingsCache()
                .valueOrDefault("com.client.catalog.gifts.wrap.price", defaultPayload));
            SocketDelivery.sendToSocket(socketIndex,
                CatalogPayloads.giftWrapOptions(giftWrapPrice, CatalogState.instance().giftSettings()));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_136_765F10.
     */
    public static void sendPage(int socketIndex, CatalogWire.PageRequest request) {
        try {
            long pageId = request.pageId();
            CatalogPages catalogPages = CatalogState.instance().catalogPages();
            if (!catalogPages.pagePayload(pageId).isEmpty()) {
                SocketDelivery.sendToSocket(socketIndex, CatalogPayloads.page(catalogPages, pageId));
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void sendDefaultIndex(int socketIndex) {
        try {
            String pageTree = CatalogState.instance().catalogPages().defaultPageTree();
            SocketDelivery.sendToSocket(socketIndex, "A~IHHM" + '\2' + pageTree);
        } catch (Exception ignored) {
            // VB6 source suppresses dispatcher helper failures.
        }
    }

    /**
     * Original function: Proc_6_128_756190.
     */
    public static String purchaseProduct(int socketIndex, CatalogWire.ProductPurchaseRequest request) {
        try {
            long catalogProductId = request.catalogProductId();
            String signText = request.signText();
            if (catalogProductId <= 0L) {
                return "";
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (socketIndex <= 0 || userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            CatalogRegistry.CatalogProduct catalogProduct =
                CatalogState.instance().registry().catalogProduct(catalogProductId).orElse(null);
            if (catalogProduct == null) {
                return "";
            }
            long productId = catalogProduct.productId();
            String typeSecondary = catalogProduct.typeSecondary().toLowerCase();
            long creditPrice = catalogProduct.creditPrice();
            long activityPrice = catalogProduct.activityPrice();
            long activityType = catalogProduct.activityType();
            long minClubLevel = catalogProduct.minClubLevel();
            if (productId <= 0L) {
                return "";
            }
            if (activityType < 0L || activityType > 4L) {
                activityType = 0L;
            }
            long userIdValue = NumberUtils.parseLong(userId);
            UserDao users = userDao();
            UserDao.CatalogPurchaseBalance balance = users == null
                ? null
                : users.catalogPurchaseBalance(userIdValue, activityType).orElse(null);
            if (balance == null) {
                return "";
            }
            if (minClubLevel > 0L && balance.clubLevel() < minClubLevel) {
                SocketDelivery.sendToSocket(socketIndex, CatalogPayloads.purchaseError(3));
                return "";
            }
            if (balance.credits() < creditPrice) {
                SocketDelivery.sendToSocket(socketIndex, CatalogPayloads.purchaseError(1));
                return "";
            }
            if (balance.activityPoints() < activityPrice) {
                SocketDelivery.sendToSocket(socketIndex, CatalogPayloads.purchaseError(2));
                return "";
            }
            long grantedFurnitureId = NumberUtils.parseLong(sendPurchaseItems(socketIndex, catalogProductId, signText));
            if (grantedFurnitureId <= 0L) {
                return "";
            }
            if (creditPrice > 0L || activityPrice > 0L) {
                users.spendCatalogPurchaseBalance(userIdValue, creditPrice, activityType, activityPrice);
                if (creditPrice > 0L) {
                    UserRefreshService.sendCreditsRefresh(userIdValue);
                }
                if (activityPrice > 0L) {
                    UserRefreshService.sendActivityPointRefreshes(userIdValue);
                }
            }
            String itemClass = "i";
            if (!"products_deals".equals(typeSecondary)
                && GameDataCaches.productCache().type(productId) == 8L) {
                itemClass = "I";
            }
            String purchasePayload = CatalogPayloads.purchase(catalogProductId, creditPrice, activityPrice,
                activityType, grantedFurnitureId, itemClass);
            SocketDelivery.sendToSocket(socketIndex, purchasePayload);
            InventoryPacketHandlers.sendInventoryToSocket(socketIndex);
            return purchasePayload;
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_132_75D4A0.
     */
    public static String purchaseGift(int socketIndex, CatalogWire.GiftPurchaseRequest request) {
        try {
            long catalogProductId = request.catalogProductId();
            long expectedProductId = request.expectedProductId();
            String recipientName = request.recipientName();
            String giftMessage = request.giftMessage();
            long wrapProductId = request.wrapProductId();
            long ribbonId = request.ribbonId();
            long colorId = request.colorId();
            if (catalogProductId <= 0L || recipientName.isEmpty()) {
                return "";
            }
            String senderUserId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (socketIndex <= 0 || senderUserId.isEmpty() || "0".equals(senderUserId)) {
                return "";
            }
            CatalogRegistry.CatalogProduct catalogProduct =
                CatalogState.instance().registry().catalogProduct(catalogProductId).orElse(null);
            if (catalogProduct == null) {
                return "";
            }
            long productId = catalogProduct.productId();
            long creditPrice = catalogProduct.creditPrice();
            long activityPrice = catalogProduct.activityPrice();
            long activityType = catalogProduct.activityType();
            long allowGifts = catalogProduct.allowGifts();
            long minClubLevel = catalogProduct.minClubLevel();
            if (productId <= 0L || allowGifts == 0L || expectedProductId > 0L && expectedProductId != productId) {
                return "";
            }
            if (activityType < 0L || activityType > 4L) {
                activityType = 0L;
            }
            if (AppConfigState.instance().settingsCache().longValueOrDefault("com.client.catalog.gifts.wrap.enabled", 0) != 0L) {
                long wrapPrice = AppConfigState.instance().settingsCache().longValueOrDefault("com.client.catalog.gifts.wrap.price", 0);
                if (wrapProductId <= 0L) {
                    CatalogDao catalog = catalogDao();
                    if (catalog == null) {
                        return "";
                    }
                    wrapProductId = catalog.firstGiftWrapProductId();
                }
                if (wrapProductId > 0L && !CatalogState.instance().giftSettings().containsGiftWrapProduct(wrapProductId)) {
                    return "";
                }
                creditPrice += wrapPrice;
            }
            long senderUserIdValue = NumberUtils.parseLong(senderUserId);
            UserDao users = userDao();
            UserDao.CatalogPurchaseBalance balance = users == null
                ? null
                : users.catalogPurchaseBalance(senderUserIdValue, activityType).orElse(null);
            if (balance == null) {
                return "";
            }
            if (minClubLevel > 0L && balance.clubLevel() < minClubLevel) {
                SocketDelivery.sendToSocket(socketIndex, CatalogPayloads.purchaseError(3));
                return "";
            }
            if (balance.credits() < creditPrice) {
                SocketDelivery.sendToSocket(socketIndex, CatalogPayloads.purchaseError(1));
                return "";
            }
            if (balance.activityPoints() < activityPrice) {
                SocketDelivery.sendToSocket(socketIndex, CatalogPayloads.purchaseError(2));
                return "";
            }
            String recipientUserId = String.valueOf(users.userIdByName(recipientName));
            if (recipientUserId.isEmpty() || "0".equals(recipientUserId)) {
                recipientUserId = senderUserId;
            }
            long grantedFurnitureId = grantFurniture(socketIndex, catalogProductId, giftMessage).firstFurnitureId();
            if (grantedFurnitureId <= 0L) {
                return "";
            }
            String productSign = GameDataCaches.productCache().defaultSign(productId);
            if ("TROPHY_VAR".equalsIgnoreCase(productSign)) {
                productSign = UserLookups.nameByIdText(senderUserId, userDao()) + '\b' + RecyclerRewards.rewardSign() + '\b' + giftMessage;
            }
            long giftSecondary = colorId * 1000L + ribbonId;
            furnitureDao().updateGiftMetadata(
                grantedFurnitureId,
                StringUtils.singleLineText(giftMessage),
                StringUtils.singleLineText(productSign),
                NumberUtils.parseLong(recipientUserId),
                catalogProductId,
                giftSecondary);
            if (creditPrice > 0L || activityPrice > 0L) {
                users.spendCatalogPurchaseBalance(senderUserIdValue, creditPrice, activityType, activityPrice);
                if (creditPrice > 0L) {
                    UserRefreshService.sendCreditsRefresh(senderUserIdValue);
                }
                if (activityPrice > 0L) {
                    UserRefreshService.sendActivityPointRefreshes(senderUserIdValue);
                }
            }
            users.incrementGiftsGiven(senderUserIdValue);
            if (!recipientUserId.equals(senderUserId)) {
                users.incrementGiftsReceived(NumberUtils.parseLong(recipientUserId));
                AchievementPacketHandlers.advanceProgress(socketIndex, 6);
            }
            String purchasePayload = CatalogPayloads.giftPurchase(
                catalogProduct, creditPrice, activityPrice, activityType, grantedFurnitureId);
            SocketDelivery.sendToSocket(socketIndex, purchasePayload);
            long recipientSocket = SessionLookups.socketFromUserIdText(recipientUserId);
            if (recipientSocket > 0L) {
                SocketDelivery.sendToSocket((int) recipientSocket,
                    InventoryMessagePayloads.roomAdd(grantedFurnitureId, productId, productSign, giftSecondary));
                AchievementPacketHandlers.advanceProgress((int) recipientSocket, 7);
            }
            return purchasePayload;
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_129_7583C0.
     */
    private static String sendPurchaseItems(int socketIndex, long catalogProductId, String signText) {
        try {
            String itemSignText = StringUtils.text(signText);
            if (socketIndex <= 0 || catalogProductId <= 0L) {
                return "";
            }
            CatalogRegistry.CatalogProduct catalogProduct =
                CatalogState.instance().registry().catalogProduct(catalogProductId).orElse(null);
            if (catalogProduct == null) {
                return "";
            }
            long productId = catalogProduct.productId();
            String typeSecondary = catalogProduct.typeSecondary().toLowerCase();
            if (productId <= 0L) {
                return "";
            }
            CatalogGrantResult grantResult = grantFurniture(socketIndex, catalogProductId, signText);
            if (grantResult.isEmpty()) {
                return "";
            }
            List<Long> grantedIds = grantResult.furnitureIds();
            long[] productIds;
            int itemCount = 0;
            if ("products_deals".equals(typeSecondary)) {
                CatalogRegistry.ProductDeal deal = CatalogState.instance().registry().productDeal(productId).orElse(null);
                List<Long> dealProductIds = deal == null ? List.<Long>of() : deal.itemProductIds();
                productIds = new long[dealProductIds.size()];
                for (Long dealProductId : dealProductIds) {
                    if (dealProductId != null && dealProductId > 0L) {
                        productIds[itemCount++] = dealProductId;
                    }
                }
            } else {
                itemCount = Math.max(1, grantedIds.size());
                productIds = new long[itemCount];
                for (int index = 0; index < itemCount; index++) {
                    productIds[index] = productId;
                }
            }
            long firstFurnitureId = 0L;
            for (int index = 0; index < itemCount; index++) {
                long furnitureId = index < grantedIds.size() ? NumberUtils.parseLong(grantedIds.get(index)) : 0L;
                long itemProductId = productIds[index];
                if (furnitureId > 0L && itemProductId > 0L) {
                    if (firstFurnitureId == 0L) {
                        firstFurnitureId = furnitureId;
                    }
                    String itemData = GameDataCaches.productCache().itemData(itemProductId);
                    if (itemData.isEmpty()) {
                        itemData = GameDataCaches.productCache().defaultSign(itemProductId);
                    }
                    long productType = GameDataCaches.productCache().type(itemProductId);
                    SocketDelivery.sendToSocket(socketIndex,
                        InventoryMessagePayloads.roomAdd(furnitureId, itemProductId, itemData, 0));
                    if ("TROPHY_VAR".equalsIgnoreCase(GameDataCaches.productCache().defaultSign(itemProductId))) {
                        String trophySign = UserLookups.nameByIdText(SessionLookups.userIdTextFromSocket(socketIndex), userDao()) + '\b'
                            + RecyclerRewards.rewardSign() + '\b' + itemSignText;
                        furnitureDao().updateSignText(furnitureId, StringUtils.singleLineText(trophySign));
                    }
                    if (productType == 8L) {
                        SocketDelivery.sendToSocket(socketIndex, CatalogPayloads.dimensionMap(furnitureId,
                            GameDataCaches.productCache().dimensionMapId(itemProductId)));
                    }
                }
            }
            return String.valueOf(firstFurnitureId);
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_133_760400.
     */
    private static CatalogGrantResult grantFurniture(int socketIndex, long catalogProductId, String signText) throws Exception {
        String userId = socketIndex > 0 ? SessionLookups.userIdTextFromSocket(socketIndex) : "";
        if (userId.isEmpty() || "0".equals(userId)) {
            return CatalogGrantResult.empty();
        }
        CatalogRegistry.CatalogProduct catalogProduct =
            CatalogState.instance().registry().catalogProduct(catalogProductId).orElse(null);
        if (catalogProduct == null) {
            return CatalogGrantResult.empty();
        }
        long productId = catalogProduct.productId();
        String typeSecondary = catalogProduct.typeSecondary().toLowerCase();
        long amount = catalogProduct.amount();
        if (amount <= 0L) {
            amount = 1L;
        }
        long userIdValue = NumberUtils.parseLong(userId);
        FurnitureDao furniture = furnitureDao();
        UserDao users = userDao();
        ClubDao clubs = clubDao();
        if (furniture == null || users == null || clubs == null) {
            return CatalogGrantResult.empty();
        }
        long grantedCount = 0L;
        if ("products_deals".equals(typeSecondary)) {
            CatalogRegistry.ProductDeal deal = CatalogState.instance().registry().productDeal(productId).orElse(null);
            if (deal == null) {
                return CatalogGrantResult.empty();
            }
            for (Long dealProductId : deal.itemProductIds()) {
                if (dealProductId != null && dealProductId > 0L) {
                    String defaultSign = StringUtils.singleLineText(GameDataCaches.productCache().defaultSign(dealProductId));
                    if (defaultSign.isEmpty()) {
                        defaultSign = StringUtils.singleLineText(GameDataCaches.productCache().fallbackDefaultSign(dealProductId));
                    }
                    furniture.insertCatalogFurniture(dealProductId, userIdValue, defaultSign, catalogProductId);
                    grantedCount++;
                }
            }
        } else {
            ClubDao.ContainedClubProduct containedClub = clubs.containedClubProduct(catalogProductId)
                .orElseGet(() -> {
                    try {
                        return clubs.containedClubProduct(productId).orElse(null);
                    } catch (Exception ignored) {
                        return null;
                    }
                });
            if (containedClub != null) {
                long hcMonths = containedClub.months();
                long hcLevel = containedClub.level();
                if (hcLevel <= 0L) {
                    hcLevel = 1L;
                }
                ClubPeriodService.applyClubPeriod(NumberUtils.parseLong(userId), hcLevel, hcMonths, hcMonths * 31L);
            }
            String badgeId = GameDataCaches.productCache().badgeId(productId).toUpperCase();
            if (badgeId.isEmpty()) {
                badgeId = GameDataCaches.productCache().fallbackBadgeId(productId).toUpperCase();
            }
            if (badgeId.length() > 2) {
                String existingBadge = StringUtils.text(users.badgeId(userIdValue, badgeId)).toUpperCase();
                if (!badgeId.equals(existingBadge)) {
                    users.insertBadge(userIdValue, 0L, badgeId);
                    long badgeRowId = users.badgeRowId(userIdValue, badgeId);
                    SocialLookups.equippedBadgePayload(NumberUtils.parseLong(userId), userDao());
                    SocialPacketHandlers.sendBadgeInventory(socketIndex);
                    if (badgeRowId > 0L) {
                        com.alphaseries.game.user.UserPacketHandlers.sendActivityPointBalance(socketIndex);
                    }
                }
            }
            String defaultSign = signText;
            if (defaultSign.isEmpty()) {
                defaultSign = StringUtils.singleLineText(GameDataCaches.productCache().defaultSign(productId));
            }
            if (defaultSign.isEmpty()) {
                defaultSign = StringUtils.singleLineText(GameDataCaches.productCache().fallbackDefaultSign(productId));
            }
            for (long itemIndex = 1L; itemIndex <= amount; itemIndex++) {
                furniture.insertCatalogFurniture(productId, userIdValue, defaultSign, catalogProductId);
                grantedCount++;
            }
        }
        if (grantedCount <= 0L) {
            return CatalogGrantResult.empty();
        }
        List<Long> grantedIds = new ArrayList<>();
        for (Long newestId : furniture.newestFurnitureIdsByOwner(userIdValue, grantedCount)) {
            if (newestId != null && newestId > 0L) {
                grantedIds.add(newestId);
            }
        }
        long firstGrantedId = grantedIds.isEmpty() ? 0L : NumberUtils.parseLong(grantedIds.get(0));
        if (!"products_deals".equals(typeSecondary)
            && GameDataCaches.productCache().type(productId) == 9L && firstGrantedId > 0L) {
            furniture.insertDefaultDimmerPresets(firstGrantedId);
            furniture.updateDefaultDimmerSign(firstGrantedId);
        }
        return new CatalogGrantResult(grantedIds);
    }

    /**
     * Original function: Proc_6_137_766470.
     */
    public static void redeemVoucher(int socketIndex, VoucherWire.RedeemRequest request) {
        try {
            String voucherCode = request.voucherCode();
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            VoucherRedemption redemption = VoucherRedemption.redeem(
                voucherCode,
                NumberUtils.parseLong(userId),
                voucherDao(),
                userDao(),
                GameDataCaches.productCache());
            if (redemption.redeemed() && redemption.creditsRefreshRequired()) {
                UserRefreshService.sendCreditsRefresh(NumberUtils.parseLong(userId));
            }
            if (redemption.redeemed() && redemption.activityPointRefreshRequired()) {
                UserRefreshService.sendActivityPointRefreshes(NumberUtils.parseLong(userId));
            }
            SocketDelivery.sendToSocket(socketIndex, redemption.responsePayload());
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    private static VoucherDao voucherDao() {
        return DaoProvider.voucherDao();
    }

    private static CatalogDao catalogDao() {
        return DaoProvider.catalogDao();
    }

    private static ClubDao clubDao() {
        return DaoProvider.clubDao();
    }

    private static FurnitureDao furnitureDao() {
        return DaoProvider.furnitureDao();
    }

    private static UserDao userDao() {
        return DaoProvider.userDao();
    }
}
