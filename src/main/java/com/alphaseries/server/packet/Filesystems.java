package com.alphaseries.server.packet;

import com.alphaseries.messages.incoming.IncomingContext;
import com.alphaseries.messages.incoming.MessageRegistry;
import com.alphaseries.messages.incoming.ReadyPacketRegistry;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.protocol.ReadyPacketBuffer;
import com.alphaseries.server.logging.Console;
import com.alphaseries.server.mus.MusConnectionManager;
import com.alphaseries.server.runtime.Guardian;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public final class Filesystems {
    private static List<ActiveSession> activeSessions = List.of();
    private static PacketSink packetSink = (socketIndex, payload) -> { };
    private static MessageRegistry readyPacketRegistry = ReadyPacketRegistry.create();
    private static boolean packetTracingEnabled = false;

    private Filesystems() {
    }

    public static final class ReadyPacket {
        private final String code;
        private final String payload;

        private ReadyPacket(String code, String payload) {
            this.code = StringUtils.text(code);
            this.payload = StringUtils.text(payload);
        }

        public String code() {
            return code;
        }

        public void appendPayloadTo(PacketBuilder packet) {
            if (packet != null) {
                packet.appendRaw(payload);
            }
        }
    }

    public static final class ReadyPacketPayloads implements Iterable<String> {
        private final List<String> values;

        private ReadyPacketPayloads(List<String> values) {
            this.values = List.copyOf(values == null ? List.of() : values);
        }

        public static ReadyPacketPayloads empty() {
            return new ReadyPacketPayloads(List.of());
        }

        public static ReadyPacketPayloads fromReadyPackets(Iterable<ReadyPacket> packets) {
            if (packets == null) {
                return empty();
            }
            List<String> payloads = new ArrayList<>();
            for (ReadyPacket packet : packets) {
                if (packet != null) {
                    PacketBuilder payload = PacketBuilder.create();
                    packet.appendPayloadTo(payload);
                    payloads.add(payload.build());
                }
            }
            return new ReadyPacketPayloads(payloads);
        }

        public int size() {
            return values.size();
        }

        @Override
        public Iterator<String> iterator() {
            return values.iterator();
        }
    }

    public record ActiveSession(String userName, int socketIndex) {
        public ActiveSession {
            userName = StringUtils.text(userName);
        }
    }

    public static void configurePacketSink(PacketSink sink) {
        packetSink = sink == null ? (socketIndex, payload) -> { } : sink;
    }

    public static void configureReadyPacketRegistry(MessageRegistry registry) {
        readyPacketRegistry = registry == null ? ReadyPacketRegistry.create() : registry;
    }

    public static void configurePacketTracing(boolean enabled) {
        packetTracingEnabled = enabled;
    }

    public static void processReadyPacketBuffer(long socketIndex, String packetBuffer) {
        if (!Guardian.isSocketConnected(socketIndex)) {
            return;
        }
        if (ReadyPacketBuffer.isCrossDomainPolicyRequest(packetBuffer)) {
            MusConnectionManager.instance().sendData((int) socketIndex, buildCrossDomainPolicy());
            return;
        }

        for (ReadyPacketBuffer.Frame packet : ReadyPacketBuffer.frames(packetBuffer)) {
            String packetPayload = framePayload(packet);
            if (packetTracingEnabled) {
                Console.logSourceLine("[" + socketIndex + "] " + packetPayload, "GAME", 16711680L);
            }
            dispatchReadyPacket(socketIndex, packet.code(), packetPayload);
        }
    }

    public static String buildCrossDomainPolicy() {
        return "<?xml version=\"1.0\"?>\r\n"
            + "<!DOCTYPE cross-domain-policy SYSTEM \"/xml/dtds/cross-domain-policy.dtd\">\r\n"
            + "<cross-domain-policy>\r\n"
            + "<allow-access-from domain=\"images.habbo.com\" to-ports=\"1-50000\" />\r\n"
            + "<allow-access-from domain=\"*\" to-ports=\"1-50000\" />\r\n"
            + "</cross-domain-policy>\0";
    }

    public static boolean isCrossDomainPolicyRequest(String packetBuffer) {
        return ReadyPacketBuffer.isCrossDomainPolicyRequest(packetBuffer);
    }

    public static ReadyPacketPayloads readyPacketPayloadsFromBuffer(String packetBuffer) {
        return ReadyPacketPayloads.fromReadyPackets(readyPacketsFromBuffer(packetBuffer));
    }

    public static List<ReadyPacket> readyPacketsFromBuffer(String packetBuffer) {
        List<ReadyPacket> packets = new ArrayList<ReadyPacket>();
        for (ReadyPacketBuffer.Frame frame : ReadyPacketBuffer.frames(packetBuffer)) {
            packets.add(new ReadyPacket(frame.code(), framePayload(frame)));
        }
        return packets;
    }

    public static long broadcastToActiveSessions(String payload, String onlyUserName) {
        long sentCount = 0L;
        String userFilter = StringUtils.text(onlyUserName);
        for (ActiveSession session : activeSessions()) {
            String userName = session.userName().toLowerCase(Locale.ROOT);
            if (userFilter.isEmpty() || userName.equals(userFilter)) {
                packetSink.send(session.socketIndex(), payload);
                sentCount++;
            }
        }
        return sentCount;
    }

    public static void setActiveSessions(List<ActiveSession> activeSessions) {
        Filesystems.activeSessions = activeSessions == null ? List.of() : List.copyOf(activeSessions);
    }

    public static List<ActiveSession> activeSessions() {
        return activeSessions;
    }

    private static void dispatchReadyPacket(long socketIndex, String packetCode, String packetPayload) {
        readyPacketRegistry.dispatch(new IncomingContext((int) socketIndex), StringUtils.text(packetCode), packetPayload);
    }

    private static String framePayload(ReadyPacketBuffer.Frame frame) {
        PacketBuilder payload = PacketBuilder.create();
        if (frame != null) {
            frame.appendPayloadTo(payload);
        }
        return payload.build();
    }
}
