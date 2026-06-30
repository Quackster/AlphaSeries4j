package com.alphaseries.messages.outgoing;

import com.alphaseries.protocol.PacketBuilder;

public final class VoucherPayloads {
    private VoucherPayloads() {
    }

    public static String invalid(String voucherCode) {
        return PacketBuilder.message("CU")
            .appendString(voucherCode)
            .build();
    }

    public static String redeemed(String rewardPayload) {
        return PacketBuilder.message("CT")
            .appendRaw(rewardPayload)
            .build();
    }
}
