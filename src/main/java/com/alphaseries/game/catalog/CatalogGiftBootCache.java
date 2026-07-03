package com.alphaseries.game.catalog;

import com.alphaseries.config.AppConfigState;
import com.alphaseries.dao.mysql.CatalogDao;
import com.alphaseries.dao.mysql.ClubDao;
import com.alphaseries.db.Database;
import com.alphaseries.db.MySQL;
import com.alphaseries.game.GameDataCaches;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class CatalogGiftBootCache {
    private CatalogGiftBootCache() {
    }

    /**
     * Original function: Proc_1_13_6C9820.
     */
    public static void loadGiftWrapCache() {
        List<Long> wrapProductIds = List.of();
        CatalogDao catalog = catalogDao();
        if (catalog != null) {
            try {
                wrapProductIds = catalog.giftWrapProductIds();
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        long wrapCount = countNonZeroRows(wrapProductIds);
        long accessoryCount = AppConfigState.instance().settingsCache()
            .longValueOrDefault("com.client.catalog.gifts.wrap.count.accessories", wrapCount);
        long colorCount = AppConfigState.instance().settingsCache()
            .longValueOrDefault("com.client.catalog.gifts.wrap.count.colors", 0);
        mergeGiftWrapIntoCatalogState(buildGiftWrapState(wrapProductIds, accessoryCount, colorCount));
    }

    /**
     * Original function: Proc_1_18_6CE9C0.
     */
    public static void loadClubGiftCache() {
        PacketBuilder payload = PacketBuilder.create();
        List<GiftSettings.ClubGift> gifts = new ArrayList<GiftSettings.ClubGift>();
        long count = 0L;
        ClubDao clubs = clubDao();
        List<ClubDao.ClubGiftRow> giftRows = List.of();
        if (clubs != null) {
            try {
                giftRows = clubs.clubGiftRows();
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        for (ClubDao.ClubGiftRow row : giftRows) {
            long catalogProductId = row.catalogProductId();
            CatalogRegistry.CatalogProduct catalogProduct =
                CatalogState.instance().registry().catalogProduct(catalogProductId).orElse(null);
            long productId = catalogProduct == null ? 0L : catalogProduct.productId();
            if (productId == 0L) {
                productId = catalogProductId;
            }
            CatalogRegistry.Product product = CatalogState.instance().registry().product(productId).orElse(null);
            String giftClass = product != null && product.type() == 9L ? "i" : "s";
            payload
                .appendInt(catalogProductId)
                .appendInt(productId)
                .appendString(GameDataCaches.productCache().displayName(productId))
                .appendString(GameDataCaches.productCache().description(productId))
                .appendRaw("IHHI")
                .appendString(giftClass)
                .appendInt(row.vipOnly())
                .appendInt(row.requiredDays());
            gifts.add(new GiftSettings.ClubGift(catalogProductId, productId, row.requiredDays()));
            count++;
        }
        mergeClubGiftIntoCatalogState(GiftSettings.ClubGiftState.fromPayload(
            PacketBuilder.create().appendInt(count).appendRaw(payload.build()).build(),
            gifts));
    }

    private static void mergeClubGiftIntoCatalogState(GiftSettings.ClubGiftState state) {
        GiftSettings currentSettings = CatalogState.instance().giftSettings();
        CatalogState.instance().setGiftSettings(GiftSettings.fromStates(
            state == null ? GiftSettings.ClubGiftState.empty() : state,
            currentSettings.giftWrapState()));
    }

    private static void mergeGiftWrapIntoCatalogState(GiftSettings.GiftWrapState state) {
        GiftSettings currentSettings = CatalogState.instance().giftSettings();
        CatalogState.instance().setGiftSettings(GiftSettings.fromStates(
            currentSettings.clubGiftState(),
            state == null ? GiftSettings.GiftWrapState.empty() : state));
    }

    /**
     * Original function: Proc_1_13_6C9820.
     */
    public static GiftSettings.GiftWrapState buildGiftWrapState(List<Long> wrapProductIds, long accessoryCount, long colorCount) {
        long wrapCount = 0L;
        PacketBuilder wrapPayload = PacketBuilder.create();
        for (Long productId : wrapProductIds == null ? List.<Long>of() : wrapProductIds) {
            long wrapId = NumberUtils.parseLong(productId);
            if (wrapId != 0L) {
                wrapCount++;
                wrapPayload.appendInt(wrapId);
            }
        }

        PacketBuilder accessoryPayload = PacketBuilder.create();
        for (long optionIndex = 1L; optionIndex <= accessoryCount; optionIndex++) {
            accessoryPayload.appendInt(optionIndex);
        }

        PacketBuilder colorPayload = PacketBuilder.create();
        for (long optionIndex = 1L; optionIndex <= colorCount; optionIndex++) {
            colorPayload.appendInt(optionIndex);
        }

        String payload = PacketBuilder.create()
            .appendInt(accessoryCount)
            .appendRaw(accessoryPayload.build())
            .appendInt(wrapCount)
            .appendRaw(wrapPayload.build())
            .appendInt(colorCount)
            .appendRaw(colorPayload.build())
            .build();
        return GiftSettings.GiftWrapState.fromPayload(payload, wrapProductIds == null ? List.of() : List.copyOf(wrapProductIds));
    }

    /**
     * Original function: Proc_1_18_6CE9C0.
     */
    public static GiftSettings.ClubGiftState buildClubGiftState(List<ClubDao.ClubGiftRow> giftRows, Map<Long, Long> productIdByCatalogProductId,
            Map<Long, Long> productTypeByProductId, Map<Long, String> nameByProductId,
            Map<Long, String> descriptionByProductId) {
        long giftCount = 0L;
        PacketBuilder giftPayload = PacketBuilder.create();
        List<GiftSettings.ClubGift> gifts = new ArrayList<GiftSettings.ClubGift>();
        if (giftRows != null) {
            for (ClubDao.ClubGiftRow row : giftRows) {
                if (row != null) {
                    long catalogProductId = row.catalogProductId();
                    long productId = mapLong(productIdByCatalogProductId, catalogProductId);
                    productId = productId == 0L ? catalogProductId : productId;
                    String giftClass = mapLong(productTypeByProductId, productId) == 9L ? "i" : "s";
                    giftPayload
                        .appendInt(catalogProductId)
                        .appendInt(productId)
                        .appendString(mapString(nameByProductId, productId))
                        .appendString(mapString(descriptionByProductId, productId))
                        .appendRaw("IHHI")
                        .appendString(giftClass)
                        .appendInt(row.vipOnly())
                        .appendInt(row.requiredDays());
                    gifts.add(new GiftSettings.ClubGift(catalogProductId, productId, row.requiredDays()));
                    giftCount++;
                }
            }
        }
        return GiftSettings.ClubGiftState.fromPayload(
            PacketBuilder.create().appendInt(giftCount).appendRaw(giftPayload.build()).build(),
            gifts);
    }

    private static long countNonZeroRows(List<Long> values) {
        long count = 0L;
        for (Long value : values == null ? List.<Long>of() : values) {
            if (NumberUtils.parseLong(value) != 0L) {
                count++;
            }
        }
        return count;
    }

    private static long mapLong(Map<Long, Long> valuesById, long id) {
        if (valuesById == null) {
            return 0L;
        }
        Long value = valuesById.get(id);
        return value == null ? 0L : value.longValue();
    }

    private static String mapString(Map<Long, String> valuesById, long id) {
        if (valuesById == null) {
            return "";
        }
        String value = valuesById.get(id);
        return value == null ? "" : value;
    }

    private static CatalogDao catalogDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new CatalogDao(database);
    }

    private static ClubDao clubDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new ClubDao(database);
    }
}
