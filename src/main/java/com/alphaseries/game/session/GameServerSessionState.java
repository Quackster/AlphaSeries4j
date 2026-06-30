package com.alphaseries.game.session;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class GameServerSessionState {
    private final List<QueuedPacket> queuedPackets = new ArrayList<>();
    private final Set<Long> readySocketIndexes = new LinkedHashSet<>();

    private GameServerSessionState(String queuedPacketData, String readySessionMarkers) {
        parseQueuedPackets(StringUtils.text(queuedPacketData));
        parseReadyMarkers(StringUtils.text(readySessionMarkers));
    }

    public static GameServerSessionState fromLegacy(String queuedPacketData, String readySessionMarkers) {
        return new GameServerSessionState(queuedPacketData, readySessionMarkers);
    }

    public static GameServerSessionState empty() {
        return new GameServerSessionState("", "");
    }

    public String queuedPacketData() {
        StringBuilder data = new StringBuilder();
        for (QueuedPacket packet : queuedPackets) {
            data.append('[').append(packet.socketIndex).append(':').append(packet.payload).append(']');
        }
        return data.toString();
    }

    public String readySessionMarkers() {
        StringBuilder markers = new StringBuilder();
        for (Long socketIndex : readySocketIndexes) {
            markers.append('[').append(socketIndex).append(']');
        }
        return markers.toString();
    }

    public void appendPacketPayload(long socketIndex, String packetPayload) {
        String payload = StringUtils.text(packetPayload);
        if (socketIndex <= 0L || payload.isEmpty()) {
            return;
        }
        queuedPackets.add(new QueuedPacket(socketIndex, payload));
    }

    public String popPacketData(long socketIndex) {
        for (Iterator<QueuedPacket> iterator = queuedPackets.iterator(); iterator.hasNext();) {
            QueuedPacket packet = iterator.next();
            if (packet.socketIndex == socketIndex) {
                iterator.remove();
                return packet.payload;
            }
        }
        return "";
    }

    public boolean isReady(long socketIndex) {
        return readySocketIndexes.contains(socketIndex);
    }

    public void removeSocket(long socketIndex) {
        queuedPackets.removeIf(packet -> packet.socketIndex == socketIndex);
        readySocketIndexes.remove(socketIndex);
    }

    private void parseQueuedPackets(String queuedPacketData) {
        for (String record : queuedPacketData.split("\\[", -1)) {
            int payloadAt = record.indexOf(':');
            int endAt = record.indexOf(']', payloadAt + 1);
            if (payloadAt > 0 && endAt > payloadAt) {
                long socketIndex = NumberUtils.parseLong(record.substring(0, payloadAt));
                if (socketIndex > 0L) {
                    queuedPackets.add(new QueuedPacket(socketIndex, record.substring(payloadAt + 1, endAt)));
                }
            }
        }
    }

    private void parseReadyMarkers(String readySessionMarkers) {
        for (String part : readySessionMarkers.split("\\]", -1)) {
            String marker = part.replace("[", "");
            if (!marker.isEmpty()) {
                long socketIndex = NumberUtils.parseLong(marker);
                if (socketIndex > 0L) {
                    readySocketIndexes.add(socketIndex);
                }
            }
        }
    }

    private static final class QueuedPacket {
        private final long socketIndex;
        private final String payload;

        private QueuedPacket(long socketIndex, String payload) {
            this.socketIndex = socketIndex;
            this.payload = StringUtils.text(payload);
        }
    }
}
