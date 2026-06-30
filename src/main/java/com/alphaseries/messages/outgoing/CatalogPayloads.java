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
}
