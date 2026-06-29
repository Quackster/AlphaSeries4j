package com.alphaseries.messages.outgoing;

import com.alphaseries.protocol.PacketBuilder;

public final class RecyclerPayloads {
    private RecyclerPayloads() {
    }

    public static String status(long enabledValue, long remainingBlockTime) {
        long normalizedEnabled = enabledValue != 0L ? 1L : 0L;
        return PacketBuilder.message("G{")
            .appendInt(normalizedEnabled)
            .appendInt(remainingBlockTime)
            .build();
    }
}
