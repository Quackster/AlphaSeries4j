package com.alphaseries.game.trade;

import com.alphaseries.protocol.WireEncoding;
import com.alphaseries.protocol.WireReader;
import com.alphaseries.protocol.WireRequests;
import com.alphaseries.util.NumberUtils;

public final class TradeWire {
    private TradeWire() {
    }

    public record FurnitureRequest(long furnitureId) {
    }

    /**
     * Original function: Proc_6_91_743480.
     * Original function: Proc_6_92_744870.
     */
    public static FurnitureRequest furnitureRequest(String packetPayload, String prefix) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, prefix);
        long furnitureId = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        if (furnitureId <= 0L) {
            furnitureId = WireReader.readLong(requestPayload, new WireReader.Offset(1));
        }
        return new FurnitureRequest(furnitureId);
    }
}
