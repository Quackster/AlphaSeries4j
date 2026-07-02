package com.alphaseries.game.inventory;

import com.alphaseries.config.AppPaths;
import com.alphaseries.dao.mysql.FurnitureDao;
import com.alphaseries.db.Database;
import com.alphaseries.db.MySQL;
import com.alphaseries.game.session.SessionState;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.server.mus.MusConnectionManager;
import com.alphaseries.util.FileUtils;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class InventoryRefreshService {
    private InventoryRefreshService() {
    }

    private static String inventoryAddPayload(long furnitureId, long productId, String itemData, long secondaryValue) {
        return InventoryMessagePayloads.add(furnitureId, productId, itemData, secondaryValue);
    }

    private static String inventoryRemovePayload(long furnitureId) {
        return InventoryMessagePayloads.remove(furnitureId);
    }

    /**
     * Original function: Proc_10_14_80B010.
     */
    public static long sendInventoryAddRefresh(long furnitureId) {
        try {
            if (furnitureId <= 0L) {
                return 0L;
            }
            FurnitureDao.InventoryFurniture furniture = furnitureDao().inventoryFurniture(furnitureId).orElse(null);
            if (furniture == null) {
                return 0L;
            }
            long productId = furniture.productId();
            long ownerId = furniture.ownerId();
            String itemData = StringUtils.text(furniture.itemData());
            long secondaryValue = furniture.secondaryValue();
            if (ownerId <= 0L) {
                return 0L;
            }

            String cachePath = userInventoryCachePath(ownerId);
            String cacheText = FileUtils.readTextFile(cachePath);
            InventoryCache cache = InventoryCache.fromCacheText(cacheText);
            InventoryCache updatedCache = cache.add(new InventoryItem(furnitureId, productId, itemData, secondaryValue));
            if (!updatedCache.equals(cache)) {
                FileUtils.writeTextFile(cachePath, updatedCache.cacheText());
            }
            long socketIndex = SessionState.instance().linkedSocketIndex(ownerId);
            if (socketIndex > 0L) {
                MusConnectionManager.instance().sendData((int) socketIndex,
                    inventoryAddPayload(furnitureId, productId, itemData, secondaryValue));
            }
            return 1L;
        } catch (Exception ex) {
            return 0L;
        }
    }

    /**
     * Original function: Proc_10_15_80BA40.
     */
    public static long sendInventoryRemoveRefresh(long furnitureId) {
        try {
            if (furnitureId <= 0L) {
                return 0L;
            }
            FurnitureDao.InventoryFurniture furniture = furnitureDao().inventoryFurniture(furnitureId).orElse(null);
            if (furniture == null) {
                return 0L;
            }
            long ownerId = furniture.ownerId();
            if (ownerId <= 0L) {
                return 0L;
            }

            String cachePath = userInventoryCachePath(ownerId);
            String cacheText = FileUtils.readTextFile(cachePath);
            InventoryCache cache = InventoryCache.fromCacheText(cacheText);
            InventoryCache updatedCache = cache.remove(furnitureId);
            if (!updatedCache.equals(cache)) {
                FileUtils.writeTextFile(cachePath, updatedCache.cacheText());
            }
            long socketIndex = SessionState.instance().linkedSocketIndex(ownerId);
            if (socketIndex > 0L) {
                MusConnectionManager.instance().sendData((int) socketIndex, inventoryRemovePayload(furnitureId));
            }
            return 1L;
        } catch (Exception ex) {
            return 0L;
        }
    }

    private static String userInventoryCachePath(long ownerId) {
        Path basePath = Paths.get(StringUtils.text(AppPaths.applicationPath()));
        return basePath.resolve("cache").resolve("users").resolve(ownerId + ".cache").toString();
    }

    private record InventoryCache(String leadingText, List<InventoryItem> items) {
        private InventoryCache {
            leadingText = trimCacheText(leadingText);
            items = items == null ? List.of() : List.copyOf(items);
        }

        private static InventoryCache fromCacheText(String cacheText) {
            String cache = trimCacheText(cacheText);
            int recordStart = cache.indexOf('\1');
            String leadingText = recordStart < 0 ? cache : cache.substring(0, recordStart);
            List<InventoryItem> items = new ArrayList<>();
            while (recordStart >= 0) {
                int payloadStart = recordStart + 1;
                int recordEnd = cache.indexOf('\2', payloadStart);
                String recordText = recordEnd < 0 ? cache.substring(payloadStart) : cache.substring(payloadStart, recordEnd);
                InventoryItem item = itemFromCacheRecord(recordText);
                if (item.furnitureId() > 0L) {
                    items.add(item);
                }
                recordStart = recordEnd < 0 ? -1 : cache.indexOf('\1', recordEnd + 1);
            }
            return new InventoryCache(leadingText, items);
        }

        private InventoryCache add(InventoryItem item) {
            if (item == null || item.furnitureId() <= 0L || containsFurniture(item.furnitureId())) {
                return this;
            }
            List<InventoryItem> updatedItems = new ArrayList<>(items);
            updatedItems.add(item);
            return new InventoryCache(leadingText, updatedItems);
        }

        private InventoryCache remove(long furnitureId) {
            if (furnitureId <= 0L || items.isEmpty()) {
                return this;
            }
            List<InventoryItem> updatedItems = new ArrayList<>();
            for (InventoryItem item : items) {
                if (item.furnitureId() != furnitureId) {
                    updatedItems.add(item);
                }
            }
            return updatedItems.size() == items.size() ? this : new InventoryCache(leadingText, updatedItems);
        }

        private String cacheText() {
            PacketBuilder cache = PacketBuilder.create().appendRaw(leadingText);
            for (InventoryItem item : items) {
                cache.appendRaw('\1')
                    .appendRaw(item.cacheRecordText())
                    .appendRaw('\2');
            }
            return cache.build();
        }

        private boolean containsFurniture(long furnitureId) {
            for (InventoryItem item : items) {
                if (item.furnitureId() == furnitureId) {
                    return true;
                }
            }
            return false;
        }

        private static InventoryItem itemFromCacheRecord(String recordText) {
            return InventoryItem.fromCacheRecord(recordText);
        }
    }

    public record InventoryItem(long furnitureId, long productId, String itemData, long secondaryValue) {
        public InventoryItem {
            itemData = StringUtils.text(itemData);
        }

        private static InventoryItem fromCacheRecord(String recordText) {
            InventoryCacheRecord record = InventoryCacheRecord.fromText(recordText);
            return new InventoryItem(
                record.furnitureId(),
                record.productId(),
                record.itemData(),
                record.secondaryValue());
        }

        private String cacheRecordText() {
            return PacketBuilder.create()
                .appendRaw(furnitureId)
                .appendRaw('\t')
                .appendRaw(productId)
                .appendRaw('\t')
                .appendRaw(itemData)
                .appendRaw('\t')
                .appendRaw(secondaryValue)
                .build();
        }
    }

    private record InventoryCacheRecord(
        long furnitureId,
        long productId,
        String itemData,
        long secondaryValue
    ) {
        private InventoryCacheRecord {
            itemData = StringUtils.text(itemData);
        }

        private static InventoryCacheRecord fromText(String recordText) {
            StringUtils.IndexedFields fields = StringUtils.indexedFields(recordText, '\t');
            return new InventoryCacheRecord(
                fields.number(0),
                fields.number(1),
                fields.text(2),
                fields.number(3));
        }
    }

    private static String trimCacheText(String cacheText) {
        String text = StringUtils.text(cacheText);
        while (!text.isEmpty() && (text.charAt(text.length() - 1) == '\r' || text.charAt(text.length() - 1) == '\n')) {
            text = text.substring(0, text.length() - 1);
        }
        return text;
    }

    private static FurnitureDao furnitureDao() throws SQLException {
        return new FurnitureDao(configuredDatabase());
    }

    private static Database configuredDatabase() throws SQLException {
        if (MySQL.configuredDatabase() == null) {
            throw new SQLException("Database is not configured.");
        }
        return MySQL.configuredDatabase();
    }
}
