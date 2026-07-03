package com.alphaseries.messages.outgoing;

import com.alphaseries.game.catalog.CatalogPages;
import com.alphaseries.game.catalog.CatalogRegistry;
import com.alphaseries.game.catalog.GiftSettings;
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

    public static String giftWrapOptions(long giftWrapPrice, GiftSettings giftSettings) {
        PacketBuilder payload = PacketBuilder.create().appendInt(giftWrapPrice);
        if (giftSettings != null) {
            giftSettings.appendGiftWrapPayloadTo(payload);
        }
        return payload.build();
    }

    public static String giftWrapPriceFallback(long giftWrapEnabled) {
        return PacketBuilder.create()
            .appendRaw('0')
            .appendRaw("Il")
            .appendInt(giftWrapEnabled)
            .build();
    }

    public static String page(CatalogPages catalogPages, long pageId) {
        PacketBuilder payload = PacketBuilder.message("A\u007f").appendInt(pageId);
        if (catalogPages != null) {
            catalogPages.appendPagePayloadTo(payload, pageId);
        }
        return payload.build();
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

    public static String giftPurchase(CatalogRegistry.CatalogProduct catalogProduct, long creditPrice,
                                      long activityPrice, long activityType, long furnitureId) {
        long catalogProductId = catalogProduct == null ? 0L : catalogProduct.catalogProductId();
        return giftPurchasePayload(catalogProductId, String.valueOf(catalogProductId), creditPrice,
            activityPrice, activityType, furnitureId);
    }

    private static String giftPurchasePayload(long catalogProductId, String productPayload, long creditPrice,
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
