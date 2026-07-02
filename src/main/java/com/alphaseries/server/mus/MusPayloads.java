package com.alphaseries.server.mus;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public final class MusPayloads {
    private MusPayloads() {
    }

    public record ClientFrame(String command, long socketIndex, String clientPayload) {
        public ClientFrame {
            command = StringUtils.text(command);
            clientPayload = StringUtils.text(clientPayload);
        }

        public boolean shutdown() {
            return "SHUTDOWN".equals(command);
        }
    }

    public static String shutdown(int socketIndex) {
        return "SHUTDOWN" + '\6' + socketIndex + '\7';
    }

    public static String data(int socketIndex, String messageText) {
        return "DATA" + '\6' + socketIndex + '\6' + StringUtils.text(messageText) + '\7';
    }

    public static ClientFrame clientFrame(String frameText) {
        String payload = StringUtils.text(frameText);
        if (payload.startsWith("DATA\6")) {
            String socketIndexValue = StringUtils.indexedFields(payload, '\6').text(1);
            int payloadStart = payload.indexOf('\6', "DATA\6".length());
            if (payloadStart >= 0) {
                String clientPayload = payload.substring(payloadStart + 1);
                int packetEnd = clientPayload.indexOf('\7');
                return new ClientFrame("DATA", NumberUtils.parseLong(socketIndexValue),
                    packetEnd >= 0 ? clientPayload.substring(0, packetEnd) : clientPayload);
            }
        }
        if (payload.startsWith("SHUTDOWN\6")) {
            String socketIndexValue = payload.substring("SHUTDOWN\6".length()).replace("\7", "");
            return new ClientFrame("SHUTDOWN", NumberUtils.parseLong(socketIndexValue), "");
        }
        return new ClientFrame("", 0L, payload);
    }

    public static String clientPayload(ClientFrame frame) {
        if (frame == null) {
            return "";
        }
        return frame.shutdown() ? "" : frame.clientPayload();
    }
}
