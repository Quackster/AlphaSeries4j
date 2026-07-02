package com.alphaseries.server.mus;

import com.alphaseries.util.StringUtils;

public final class MusPayloads {
    private MusPayloads() {
    }

    public record ClientFrame(String command, String socketIndexText, String clientPayload) {
        public ClientFrame {
            command = StringUtils.text(command);
            socketIndexText = StringUtils.text(socketIndexText);
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
            String[] fields = payload.split("\6", 3);
            if (fields.length >= 3) {
                int packetEnd = fields[2].indexOf('\7');
                return new ClientFrame("DATA", fields[1],
                    packetEnd >= 0 ? fields[2].substring(0, packetEnd) : fields[2]);
            }
        }
        if (payload.startsWith("SHUTDOWN\6")) {
            String socketIndexText = payload.substring("SHUTDOWN\6".length()).replace("\7", "");
            return new ClientFrame("SHUTDOWN", socketIndexText, "");
        }
        return new ClientFrame("", "", payload);
    }

    public static String clientPayload(ClientFrame frame) {
        if (frame == null) {
            return "";
        }
        return frame.shutdown() ? "" : frame.clientPayload();
    }
}
