package com.alphaseries.game.session;

import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class GameServerSessionState {
    private final List<QueuedPacket> queuedPackets = new ArrayList<>();
    private final Set<Long> readySocketIndexes = new LinkedHashSet<>();

    private GameServerSessionState() {
    }

    public static GameServerSessionState empty() {
        return new GameServerSessionState();
    }

    public static GameServerSessionState fromState(List<QueuedPacket> queuedPackets, Set<Long> readySocketIndexes) {
        GameServerSessionState state = empty();
        if (queuedPackets != null) {
            for (QueuedPacket packet : queuedPackets) {
                if (packet != null && packet.socketIndex() > 0L && packet.hasPayload()) {
                    state.queuedPackets.add(packet);
                }
            }
        }
        if (readySocketIndexes != null) {
            for (Long socketIndex : readySocketIndexes) {
                if (socketIndex != null && socketIndex > 0L) {
                    state.readySocketIndexes.add(socketIndex);
                }
            }
        }
        return state;
    }

    public List<QueuedPacket> queuedPackets() {
        return Collections.unmodifiableList(new ArrayList<>(queuedPackets));
    }

    public Set<Long> readySocketIndexes() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(readySocketIndexes));
    }

    public String queuedPacketData() {
        PacketBuilder data = PacketBuilder.create();
        for (QueuedPacket packet : queuedPackets) {
            data
                .appendRaw('[')
                .appendRaw(packet.socketIndex())
                .appendRaw(':');
            packet.appendPayloadTo(data);
            data.appendRaw(']');
        }
        return data.build();
    }

    public void appendPacketPayload(long socketIndex, String packetPayload) {
        String payload = StringUtils.text(packetPayload);
        if (socketIndex <= 0L || payload.isEmpty()) {
            return;
        }
        queuedPackets.add(QueuedPacket.of(socketIndex, payload));
    }

    public String popPacketData(long socketIndex) {
        for (Iterator<QueuedPacket> iterator = queuedPackets.iterator(); iterator.hasNext();) {
            QueuedPacket packet = iterator.next();
            if (packet.socketIndex() == socketIndex) {
                iterator.remove();
                return packet.payloadText();
            }
        }
        return "";
    }

    public boolean isReady(long socketIndex) {
        return readySocketIndexes.contains(socketIndex);
    }

    public void removeSocket(long socketIndex) {
        queuedPackets.removeIf(packet -> packet.socketIndex() == socketIndex);
        readySocketIndexes.remove(socketIndex);
    }

    public static final class QueuedPacket {
        private final long socketIndex;
        private final String payload;

        private QueuedPacket(long socketIndex, String payload) {
            this.socketIndex = socketIndex;
            this.payload = StringUtils.text(payload);
        }

        public static QueuedPacket of(long socketIndex, Object payload) {
            return new QueuedPacket(socketIndex, StringUtils.text(payload));
        }

        public long socketIndex() {
            return socketIndex;
        }

        public void appendPayloadTo(PacketBuilder packet) {
            if (packet != null) {
                packet.appendRaw(payload);
            }
        }

        private boolean hasPayload() {
            return !payload.isEmpty();
        }

        private String payloadText() {
            return payload;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof QueuedPacket packet)) {
                return false;
            }
            return socketIndex == packet.socketIndex && payload.equals(packet.payload);
        }

        @Override
        public int hashCode() {
            return Objects.hash(socketIndex, payload);
        }

        @Override
        public String toString() {
            return "QueuedPacket[socketIndex=" + socketIndex + ", payload=" + payload + ']';
        }
    }
}
