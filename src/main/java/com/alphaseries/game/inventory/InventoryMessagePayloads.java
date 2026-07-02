package com.alphaseries.game.inventory;

import com.alphaseries.game.GameDataCaches;
import com.alphaseries.game.catalog.CatalogRegistry;
import com.alphaseries.game.catalog.CatalogState;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.StringUtils;

import java.util.List;

public final class InventoryMessagePayloads {
    private InventoryMessagePayloads() {
    }

    public record InventoryList(
        long regularCount,
        String regularPayload,
        long iconCount,
        String iconPayload
    ) {
        public InventoryList {
            regularPayload = regularPayload == null ? "" : regularPayload;
            iconPayload = iconPayload == null ? "" : iconPayload;
        }

        public static InventoryList empty() {
            return new InventoryList(0L, "", 0L, "");
        }
    }

    /**
     * Original function: Proc_6_138_7678A0.
     */
    public static String item(long itemId, long productId, String itemData, long extraValue) {
        ProductMetadata product = ProductMetadata.lookup(productId);
        String itemClass = product.type == 9L ? "I" : "S";
        String normalizedItemData = StringUtils.text(itemData).replace('\b', '\t');

        return PacketBuilder.message("0")
            .appendInt(itemId)
            .appendString(itemClass)
            .appendInt(itemId)
            .appendInt(productId)
            .appendInt(product.type)
            .appendString(normalizedItemData)
            .appendInt(extraValue)
            .appendString(product.name)
            .appendString(product.description)
            .appendString(product.sprite)
            .appendString("M")
            .appendInt(extraValue)
            .build();
    }

    public static String remove(long furnitureId) {
        return PacketBuilder.message("Ac")
            .appendInt(furnitureId)
            .build();
    }

    public static String add(long itemId, long productId, String itemData, long extraValue) {
        return addPayload(item(itemId, productId, itemData, extraValue));
    }

    private static String addPayload(String itemPayload) {
        return PacketBuilder.message("Ab")
            .appendRaw(itemPayload)
            .appendRaw('\1')
            .build();
    }

    public static String roomAdd(long itemId, long productId, String itemData, long extraValue) {
        return roomAddPayload(item(itemId, productId, itemData, extraValue));
    }

    private static String roomAddPayload(String itemPayload) {
        return PacketBuilder.message("Ab")
            .appendRaw(itemPayload)
            .appendRaw('\2')
            .build();
    }

    public static String regularList(InventoryList inventoryList) {
        return regularListPayload(
            inventoryList == null ? 0L : inventoryList.regularCount(),
            inventoryList == null ? "" : inventoryList.regularPayload());
    }

    private static String regularListPayload(long itemCount, String itemPayload) {
        return PacketBuilder.create()
            .appendRaw('\2')
            .appendRaw(PacketBuilder.message("BLS").appendRaw('\2').appendRaw("II").appendInt(itemCount))
            .appendRaw(itemPayload)
            .build();
    }

    public static String iconList(InventoryList inventoryList) {
        return iconListPayload(
            inventoryList == null ? 0L : inventoryList.iconCount(),
            inventoryList == null ? "" : inventoryList.iconPayload());
    }

    private static String iconListPayload(long itemCount, String itemPayload) {
        return PacketBuilder.message("BL")
            .appendRaw('\2')
            .appendRaw("II")
            .appendInt(itemCount)
            .appendRaw(itemPayload)
            .build();
    }

    public static String emptyRentalList() {
        return PacketBuilder.message("Id")
            .appendInt(0)
            .appendRaw("HHH")
            .appendInt(0)
            .appendInt(0)
            .appendInt(0)
            .appendRaw("H")
            .build();
    }

    public static InventoryList listFromItems(List<InventoryItemRow> items) {
        if (items == null) {
            return InventoryList.empty();
        }
        long regularCount = 0L;
        long iconCount = 0L;
        PacketBuilder regularPayload = PacketBuilder.create();
        PacketBuilder iconPayload = PacketBuilder.create();
        for (InventoryItemRow item : items) {
            if (item != null) {
                String itemPayload = item(
                    item.furnitureId(),
                    item.productId(),
                    item.itemData(),
                    item.secondaryValue());
                if (GameDataCaches.productCache().type(item.productId()) == 9L) {
                    iconPayload.appendRaw(itemPayload);
                    iconCount++;
                } else {
                    regularPayload.appendRaw(itemPayload);
                    regularCount++;
                }
            }
        }
        return new InventoryList(regularCount, regularPayload.build(), iconCount, iconPayload.build());
    }

    private static final class ProductMetadata {
        private final long type;
        private final String name;
        private final String description;
        private final String sprite;

        private ProductMetadata(long type, String name, String description, String sprite) {
            this.type = type;
            this.name = name;
            this.description = description;
            this.sprite = sprite;
        }

        private static ProductMetadata lookup(long productId) {
            CatalogRegistry.Product product = CatalogState.instance().registry().product(productId).orElse(null);
            if (product == null) {
                return new ProductMetadata(0L, "", "", "");
            }
            return new ProductMetadata(product.type(), product.name(), product.description(), product.sprite());
        }
    }
}
