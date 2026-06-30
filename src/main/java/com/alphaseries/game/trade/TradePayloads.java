package com.alphaseries.game.trade;

import com.alphaseries.protocol.PacketBuilder;

public final class TradePayloads {
    private TradePayloads() {
    }

    public static String confirmation(
        long sourceUserId,
        long targetUserId,
        long sourceItemCount,
        String sourceItemPayload,
        long targetItemCount,
        String targetItemPayload
    ) {
        return PacketBuilder.message("Al")
            .appendInt(sourceUserId)
            .appendInt(targetUserId)
            .appendInt(sourceItemCount)
            .appendRaw(sourceItemPayload)
            .appendInt(targetItemCount)
            .appendRaw(targetItemPayload)
            .build();
    }
}
