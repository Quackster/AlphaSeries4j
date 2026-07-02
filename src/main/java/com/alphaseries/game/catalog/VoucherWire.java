package com.alphaseries.game.catalog;

import com.alphaseries.protocol.WireReader;
import com.alphaseries.protocol.WireRequests;

public final class VoucherWire {
    private VoucherWire() {
    }

    public record RedeemRequest(String voucherCode) {
        public RedeemRequest {
            voucherCode = voucherCode == null ? "" : voucherCode.replace(' ', '0');
        }
    }

    /**
     * Original function: Proc_6_137_766470.
     */
    public static RedeemRequest redeemRequest(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "BA");
        String voucherCode = requestPayload.startsWith("@")
            ? WireReader.readString(requestPayload, new WireReader.Offset(1))
            : requestPayload;
        return new RedeemRequest(voucherCode);
    }
}
