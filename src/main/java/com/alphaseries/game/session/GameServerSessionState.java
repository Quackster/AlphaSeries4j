package com.alphaseries.game.session;

import com.alphaseries.util.StringUtils;

public final class GameServerSessionState {
    private String queuedPacketData;
    private String readySessionMarkers;

    private GameServerSessionState(String queuedPacketData, String readySessionMarkers) {
        this.queuedPacketData = StringUtils.text(queuedPacketData);
        this.readySessionMarkers = StringUtils.text(readySessionMarkers);
    }

    public static GameServerSessionState fromLegacy(String queuedPacketData, String readySessionMarkers) {
        return new GameServerSessionState(queuedPacketData, readySessionMarkers);
    }

    public String queuedPacketData() {
        return queuedPacketData;
    }

    public String readySessionMarkers() {
        return readySessionMarkers;
    }

    public void appendPacketData(long socketIndex, String[] fields) {
        if (fields == null || fields.length <= 2) {
            return;
        }
        StringBuilder payload = new StringBuilder();
        for (int fieldIndex = 2; fieldIndex < fields.length; fieldIndex++) {
            if (payload.length() > 0) {
                payload.append('\2');
            }
            payload.append(StringUtils.text(fields[fieldIndex]));
        }
        if (payload.length() > 0) {
            queuedPacketData += "[" + socketIndex + ":" + payload + "]";
        }
    }

    public String popPacketData(long socketIndex) {
        String marker = "[" + socketIndex + ":";
        int recordStart = queuedPacketData.indexOf(marker);
        if (recordStart < 0) {
            return "";
        }
        int payloadStart = recordStart + marker.length();
        int recordEnd = queuedPacketData.indexOf(']', payloadStart);
        if (recordEnd < 0) {
            return "";
        }
        String payload = queuedPacketData.substring(payloadStart, recordEnd);
        queuedPacketData = queuedPacketData.substring(0, recordStart) + queuedPacketData.substring(recordEnd + 1);
        return payload;
    }

    public boolean isReady(long socketIndex) {
        return readySessionMarkers.contains(marker(socketIndex));
    }

    public void removeSocket(long socketIndex) {
        String marker = marker(socketIndex);
        queuedPacketData = queuedPacketData.replace(marker, "");
        readySessionMarkers = readySessionMarkers.replace(marker, "");
    }

    private static String marker(long socketIndex) {
        return "[" + socketIndex + "]";
    }
}
