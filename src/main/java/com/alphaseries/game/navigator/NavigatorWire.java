package com.alphaseries.game.navigator;

import com.alphaseries.protocol.WireEncoding;
import com.alphaseries.protocol.WireReader;
import com.alphaseries.protocol.WireRequests;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public final class NavigatorWire {
    private NavigatorWire() {
    }

    public record SingleRoomRequest(long requestMode, long detailFlag, long roomId) {
    }

    public static long categoryId(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "GC");
        long categoryId = NumberUtils.parseLong(WireEncoding.readBase64LengthString(requestPayload));
        if (categoryId <= 0L) {
            categoryId = WireReader.readLong(requestPayload, new WireReader.Offset(1));
        }
        return categoryId;
    }

    public static String queryText(String packetPayload) {
        String requestPayload = StringUtils.text(packetPayload);
        if (requestPayload.length() >= 3) {
            requestPayload = requestPayload.substring(2);
        }
        if (requestPayload.startsWith("@")) {
            String value = WireReader.readString(requestPayload, new WireReader.Offset(1));
            if (!value.isEmpty()) {
                return value;
            }
        }
        return requestPayload;
    }

    /**
     * Original function: Proc_6_60_720060.
     */
    public static SingleRoomRequest singleRoomRequest(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "FA");
        WireReader.Offset offset = new WireReader.Offset(1);
        long requestMode = WireReader.readLong(requestPayload, offset);
        long detailFlag = WireReader.readLong(requestPayload, offset);
        long roomId = detailFlag == 1L ? WireReader.readLong(requestPayload, offset) : 0L;
        return new SingleRoomRequest(requestMode, detailFlag, roomId);
    }
}
