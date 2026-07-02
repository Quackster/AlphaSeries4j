package com.alphaseries.protocol;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public final class WireRequests {
    private WireRequests() {
    }

    public static long firstLong(String packetPayload, String packetCode) {
        String requestPayload = stripPrefix(packetPayload, packetCode);
        WireReader.Offset offset = new WireReader.Offset(1);
        long value = WireReader.readLong(requestPayload, offset);
        return value > 0L ? value : NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
    }

    public static long id(String packetPayload, String prefix) {
        return firstLong(packetPayload, prefix);
    }

    public static String stripPrefix(String packetPayload, String prefix) {
        String requestPayload = StringUtils.text(packetPayload);
        String prefixText = StringUtils.text(prefix);
        if (!prefixText.isEmpty() && requestPayload.startsWith(prefixText)) {
            return requestPayload.substring(prefixText.length());
        }
        return requestPayload;
    }
}
