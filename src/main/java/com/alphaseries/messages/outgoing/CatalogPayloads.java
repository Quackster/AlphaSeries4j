package com.alphaseries.messages.outgoing;

import com.alphaseries.protocol.PacketBuilder;

public final class CatalogPayloads {
    private CatalogPayloads() {
    }

    public static String purchaseError(long errorCode) {
        return PacketBuilder.message("AD")
            .appendInt(errorCode)
            .build();
    }

    public static String giftAvailability(long itemId, long giftEnabled) {
        return PacketBuilder.message("In")
            .appendInt(itemId)
            .appendInt(giftEnabled)
            .appendRaw('\2')
            .build();
    }

    public static String giftWrapOptions(long giftWrapPrice, String giftWrapPayload) {
        return PacketBuilder.create()
            .appendInt(giftWrapPrice)
            .appendRaw(giftWrapPayload)
            .build();
    }

    public static String page(long pageId, String pagePayload) {
        return PacketBuilder.message("A\u007f")
            .appendInt(pageId)
            .appendRaw(pagePayload)
            .build();
    }

    public static String purchase(long catalogProductId, long creditPrice, long activityPrice, long activityType,
                                  long furnitureId, String itemClass) {
        return PacketBuilder.message("AC")
            .appendInt(catalogProductId)
            .appendInt(creditPrice)
            .appendInt(activityPrice)
            .appendInt(activityType)
            .appendInt(furnitureId)
            .appendRaw('\2')
            .appendString(itemClass)
            .appendRaw("IHH")
            .build();
    }

    public static String clubGiftClaim(long productId, String itemData, String itemClass, long furnitureId) {
        return PacketBuilder.message("AC")
            .appendInt(productId)
            .appendString(itemData)
            .appendRaw("HHHI")
            .appendString(itemClass)
            .appendInt(furnitureId)
            .appendRaw('\2')
            .appendRaw("IH")
            .build();
    }

    public static String giftPurchase(long catalogProductId, String productPayload, long creditPrice,
                                      long activityPrice, long activityType, long furnitureId) {
        return PacketBuilder.message("AC")
            .appendInt(catalogProductId)
            .appendString(productPayload)
            .appendInt(creditPrice)
            .appendInt(activityPrice)
            .appendInt(activityType)
            .appendInt(furnitureId)
            .appendRaw('\2')
            .appendString("i")
            .appendRaw("IH")
            .build();
    }

    public static String dimensionMap(long furnitureId, long destinationId) {
        return PacketBuilder.message("GM")
            .appendInt(furnitureId)
            .appendInt(destinationId)
            .build();
    }
}
