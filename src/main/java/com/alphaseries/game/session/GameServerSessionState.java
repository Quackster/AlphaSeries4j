package com.alphaseries.game.session;

import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
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
                if (packet != null && packet.socketIndex() > 0L && !packet.payload().isEmpty()) {
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
                .appendRaw(':')
                .appendRaw(packet.payload())
                .appendRaw(']');
        }
        return data.build();
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
            if (packet.socketIndex() == socketIndex) {
                iterator.remove();
                return packet.payload();
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

    public record QueuedPacket(long socketIndex, String payload) {
        public QueuedPacket {
            payload = StringUtils.text(payload);
        }
    }
}
