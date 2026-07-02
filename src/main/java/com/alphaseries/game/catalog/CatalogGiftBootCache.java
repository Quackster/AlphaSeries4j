package com.alphaseries.game.catalog;

import com.alphaseries.config.AppConfigState;
import com.alphaseries.dao.mysql.CatalogDao;
import com.alphaseries.dao.mysql.ClubDao;
import com.alphaseries.db.Database;
import com.alphaseries.db.MySQL;
import com.alphaseries.game.GameDataCaches;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CatalogGiftBootCache {
    private CatalogGiftBootCache() {
    }

    public record ClubGiftCache(String giftPayload, String giftLookup) {
        public ClubGiftCache {
            giftPayload = StringUtils.text(giftPayload);
            giftLookup = StringUtils.text(giftLookup);
        }
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
        mergeGiftWrapIntoCatalogState(wrapProductIds, buildGiftWrapPayload(wrapProductIds, accessoryCount, colorCount));
    }

    /**
     * Original function: Proc_1_18_6CE9C0.
     */
    public static void loadClubGiftCache() {
        PacketBuilder payload = PacketBuilder.create();
        PacketBuilder lookup = PacketBuilder.create();
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
            appendClubGiftLookup(lookup, catalogProductId, productId, row.requiredDays());
            gifts.add(new GiftSettings.ClubGift(catalogProductId, productId, row.requiredDays()));
            count++;
        }
        mergeClubGiftIntoCatalogState(new GiftSettings.ClubGiftState(
            PacketBuilder.create().appendInt(count).appendRaw(payload.build()).build(),
            gifts));
    }

    private static void mergeClubGiftIntoCatalogState(GiftSettings.ClubGiftState state) {
        GiftSettings currentSettings = CatalogState.instance().giftSettings();
        CatalogState.instance().setGiftSettings(GiftSettings.fromRows(
            state == null ? "" : state.payload(),
            state == null ? List.of() : state.gifts(),
            currentSettings.giftWrapProductIds(),
            currentSettings.giftWrapPayload()));
    }

    private static void mergeGiftWrapIntoCatalogState(List<Long> productIds, String payload) {
        GiftSettings currentSettings = CatalogState.instance().giftSettings();
        CatalogState.instance().setGiftSettings(GiftSettings.fromRows(
            currentSettings.clubGiftPayload(),
            currentSettings.clubGifts(),
            productIds == null ? List.of() : List.copyOf(productIds),
            payload));
    }

    /**
     * Original function: Proc_1_13_6C9820.
     */
    public static String buildGiftWrapPayload(List<Long> wrapProductIds, long accessoryCount, long colorCount) {
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

        return PacketBuilder.create()
            .appendInt(accessoryCount)
            .appendRaw(accessoryPayload.build())
            .appendInt(wrapCount)
            .appendRaw(wrapPayload.build())
            .appendInt(colorCount)
            .appendRaw(colorPayload.build())
            .build();
    }

    /**
     * Original function: Proc_1_18_6CE9C0.
     */
    public static ClubGiftCache buildClubGiftCache(List<ClubDao.ClubGiftRow> giftRows, Map<Long, Long> productIdByCatalogProductId,
            Map<Long, Long> productTypeByProductId, Map<Long, String> nameByProductId,
            Map<Long, String> descriptionByProductId) {
        long giftCount = 0L;
        PacketBuilder giftPayload = PacketBuilder.create();
        PacketBuilder giftLookup = PacketBuilder.create();
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
                    appendClubGiftLookup(giftLookup, catalogProductId, productId, row.requiredDays());
                    giftCount++;
                }
            }
        }
        return new ClubGiftCache(
            PacketBuilder.create().appendInt(giftCount).appendRaw(giftPayload.build()).build(),
            giftLookup.build());
    }

    private static void appendClubGiftLookup(PacketBuilder lookup, long catalogProductId, long productId, long requiredDays) {
        lookup
            .appendRaw('[')
            .appendRaw(catalogProductId)
            .appendRaw('\0')
            .appendRaw(productId)
            .appendRaw('\1')
            .appendRaw(requiredDays)
            .appendRaw(']');
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
