package com.alphaseries.game.session;

import com.alphaseries.util.StringUtils;

public final class SessionWire {
    private SessionWire() {
    }

    /**
     * Original function: Proc_6_101_746B60.
     */
    public static String loginTicket(String packetPayload) {
        String requestPayload = StringUtils.text(packetPayload);
        if (requestPayload.startsWith("F_")) {
            requestPayload = requestPayload.substring(2);
        }
        requestPayload = StringUtils.singleLineText(requestPayload).trim();
        if (requestPayload.startsWith("F_")) {
            requestPayload = requestPayload.substring(2);
        }
        return requestPayload;
    }
}
