package com.alphaseries.game.session;

import com.alphaseries.util.StringUtils;

public final class SessionWire {
    private SessionWire() {
    }

    public record LoginTicketRequest(String loginTicket) {
        public LoginTicketRequest {
            loginTicket = StringUtils.text(loginTicket);
        }
    }

    /**
     * Original function: Proc_6_101_746B60.
     */
    public static LoginTicketRequest loginTicketRequest(String packetPayload) {
        String requestPayload = StringUtils.withoutPrefix(packetPayload, "F_");
        requestPayload = StringUtils.singleLineText(requestPayload).trim();
        requestPayload = StringUtils.withoutPrefix(requestPayload, "F_");
        return new LoginTicketRequest(requestPayload);
    }
}
