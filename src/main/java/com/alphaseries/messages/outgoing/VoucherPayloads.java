package com.alphaseries.messages.outgoing;

import com.alphaseries.game.catalog.ProductCache;
import com.alphaseries.protocol.PacketBuilder;

public final class VoucherPayloads {
    private VoucherPayloads() {
    }

    public static String invalid(String voucherCode) {
        return PacketBuilder.message("CU")
            .appendString(voucherCode)
            .build();
    }

    public static String redeemed(ProductCache productCache, long productId) {
        return redeemedPayload(rewardPayload(productCache, productId));
    }

    private static String redeemedPayload(String rewardPayload) {
        return PacketBuilder.message("CT")
            .appendRaw(rewardPayload)
            .build();
    }

    private static String rewardPayload(ProductCache productCache, long productId) {
        if (productCache == null || productId <= 0L) {
            return "";
        }
        return productCache.tradeName(productId) + '\2' + productCache.displayName(productId) + '\2';
    }
}
