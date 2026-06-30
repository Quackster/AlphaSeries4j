package com.alphaseries.game.inventory;

import com.alphaseries.DataManager;
import com.alphaseries.Licence;
import com.alphaseries.game.catalog.CatalogRegistry;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.List;

public final class InventoryMessagePayloads {
    private InventoryMessagePayloads() {
    }

    public static final class InventoryList {
        public long regularCount = 0L;
        public String regularPayload = "";
        public long iconCount = 0L;
        public String iconPayload = "";
    }

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

    public static String add(String itemPayload) {
        return PacketBuilder.message("Ab")
            .appendRaw(itemPayload)
            .appendRaw('\1')
            .build();
    }

    public static String roomAdd(String itemPayload) {
        return PacketBuilder.message("Ab")
            .appendRaw(itemPayload)
            .appendRaw('\2')
            .build();
    }

    public static String regularList(long itemCount, String itemPayload) {
        return PacketBuilder.create()
            .appendRaw('\2')
            .appendRaw(PacketBuilder.message("BLS").appendRaw('\2').appendRaw("II").appendInt(itemCount))
            .appendRaw(itemPayload)
            .build();
    }

    public static String iconList(long itemCount, String itemPayload) {
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
        InventoryList result = new InventoryList();
        if (items == null) {
            return result;
        }
        StringBuilder regularPayload = new StringBuilder();
        StringBuilder iconPayload = new StringBuilder();
        for (InventoryItemRow item : items) {
            if (item != null) {
                String itemPayload = item(
                    item.furnitureId(),
                    item.productId(),
                    item.itemData(),
                    item.secondaryValue());
                if (NumberUtils.parseLong(DataManager.Proc_8_12_806C30(item.productId(), 0, 0)) == 9L) {
                    iconPayload.append(itemPayload);
                    result.iconCount++;
                } else {
                    regularPayload.append(itemPayload);
                    result.regularCount++;
                }
            }
        }
        result.regularPayload = regularPayload.toString();
        result.iconPayload = iconPayload.toString();
        return result;
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
            CatalogRegistry.Product product = Licence.product(productId);
            if (product == null) {
                return new ProductMetadata(0L, "", "", "");
            }
            return new ProductMetadata(product.type(), product.name(), product.description(), product.sprite());
        }
    }
}
