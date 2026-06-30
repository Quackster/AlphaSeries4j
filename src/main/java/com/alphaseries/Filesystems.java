package com.alphaseries;

import com.alphaseries.messages.incoming.IncomingContext;
import com.alphaseries.messages.incoming.MessageRegistry;
import com.alphaseries.messages.incoming.ReadyPacketRegistry;
import com.alphaseries.protocol.ReadyPacketBuffer;
import com.alphaseries.server.mus.MusConnectionManager;
import com.alphaseries.server.packet.PacketSink;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class Filesystems {
    public static String global_00829268 = "";
    public static boolean global_00829190 = false;
    private static PacketSink packetSink = (socketIndex, payload) -> { };
    private static MessageRegistry readyPacketRegistry = ReadyPacketRegistry.create();

    private Filesystems() {
    }

    public static final class ReadyPacket {
        public String code = "";
        public String payload = "";
    }

    public static void configurePacketSink(PacketSink sink) {
        packetSink = sink == null ? (socketIndex, payload) -> { } : sink;
    }

    public static void configureReadyPacketRegistry(MessageRegistry registry) {
        readyPacketRegistry = registry == null ? ReadyPacketRegistry.create() : registry;
    }

    public static long Proc_7_0_8034A0(Object... args) {
        if (args == null || args.length == 0) {
            return 0L;
        }
        return broadcastToActiveSessions(StringUtils.text(args[0]), "");
    }

    public static void Proc_7_2_803D60(Object... args) {
        if (args == null || args.length < 2) {
            return;
        }
        long socketIndex = NumberUtils.parseLong(args[0]);
        String packetBuffer = StringUtils.text(args[1]);
        if (Guardian.Proc_11_2_821390(socketIndex) != 1) {
            return;
        }
        if (ReadyPacketBuffer.isCrossDomainPolicyRequest(packetBuffer)) {
            MusConnectionManager.instance().sendData((int) socketIndex, buildCrossDomainPolicy());
            return;
        }

        for (ReadyPacketBuffer.Frame packet : ReadyPacketBuffer.frames(packetBuffer)) {
            if (global_00829190) {
                Console.Proc_2_0_6D1510("[" + socketIndex + "] " + packet.payload(), "GAME", "16711680");
            }
            dispatchReadyPacket(socketIndex, packet.code(), packet.payload());
        }
    }

    public static long Proc_7_1_8038A0(Object... args) {
        if (args == null || args.length < 2) {
            return 0L;
        }
        return broadcastToActiveSessions(StringUtils.text(args[1]), StringUtils.text(args[0]).toLowerCase(Locale.ROOT));
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

    public static List<String> readyPacketPayloadsFromBuffer(String packetBuffer) {
        List<String> payloads = new ArrayList<String>();
        for (ReadyPacket packet : readyPacketsFromBuffer(packetBuffer)) {
            payloads.add(packet.payload);
        }
        return payloads;
    }

    public static List<ReadyPacket> readyPacketsFromBuffer(String packetBuffer) {
        List<ReadyPacket> packets = new ArrayList<ReadyPacket>();
        for (ReadyPacketBuffer.Frame frame : ReadyPacketBuffer.frames(packetBuffer)) {
            ReadyPacket packet = new ReadyPacket();
            packet.payload = frame.payload();
            packet.code = frame.code();
            packets.add(packet);
        }
        return packets;
    }

    public static long broadcastToActiveSessions(String payload, String onlyUserName) {
        long sentCount = 0L;
        if (global_00829268 == null || global_00829268.isEmpty()) {
            return 0L;
        }
        String userFilter = StringUtils.text(onlyUserName);
        for (String recordText : global_00829268.split("\\[", -1)) {
            if (recordText.startsWith("1:")) {
                int payloadStart = recordText.indexOf('\1');
                if (payloadStart > 0) {
                    int payloadEnd = recordText.indexOf(']', payloadStart + 1);
                    if (payloadEnd == -1) {
                        payloadEnd = recordText.length();
                    }
                    String[] fields = recordText.substring(payloadStart + 1, payloadEnd).split("\2", -1);
                    if (fields.length >= 2) {
                        String userName = fields[0].toLowerCase(Locale.ROOT);
                        if (userFilter.isEmpty() || userName.equals(userFilter)) {
                            int socketIndex = NumberUtils.parseInt(fields[1]);
                            packetSink.send(socketIndex, payload);
                            sentCount++;
                        }
                    }
                }
            }
        }
        return sentCount;
    }

    private static void dispatchReadyPacket(long socketIndex, String packetCode, String packetPayload) {
        readyPacketRegistry.dispatch(new IncomingContext((int) socketIndex), StringUtils.text(packetCode), packetPayload);
    }
}
