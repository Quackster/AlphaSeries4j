package com.alphaseries.game.inventory;

import com.alphaseries.DataManager;
import com.alphaseries.Licence;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.protocol.WireEncoding;

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
        String normalizedItemData = text(itemData).replace('\b', '\t');

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

    public static InventoryList listFromRows(String rowText) {
        InventoryList result = new InventoryList();
        for (String row : text(rowText).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                long furnitureId = number(field(fields, 0));
                long productId = number(field(fields, 1));
                String itemData = field(fields, 2);
                long secondaryValue = number(field(fields, 3));
                String itemPayload = item(furnitureId, productId, itemData, secondaryValue);
                if (number(DataManager.Proc_8_12_806C30(productId, 0, 0)) == 9L) {
                    result.iconPayload += itemPayload;
                    result.iconCount++;
                } else {
                    result.regularPayload += itemPayload;
                    result.regularCount++;
                }
            }
        }
        return result;
    }

    private static String field(String[] fields, int index) {
        return fields != null && index >= 0 && index < fields.length ? text(fields[index]) : "";
    }

    private static long number(Object value) {
        return WireEncoding.parseLeadingLong(value);
    }

    private static String text(Object value) {
        return value == null ? "" : String.valueOf(value);
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
            String[] fields = text(Licence.Proc_9_3_807930(productId, 0, 0)).split("\t", -1);
            if (fields.length < 19) {
                return new ProductMetadata(0L, "", "", "");
            }
            return new ProductMetadata(number(fields[1]), fields[14], fields[15], fields[18]);
        }
    }
}
