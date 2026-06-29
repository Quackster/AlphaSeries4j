package com.alphaseries;

import com.alphaseries.messages.incoming.IncomingContext;
import com.alphaseries.messages.incoming.MessageRegistry;
import com.alphaseries.messages.incoming.ReadyPacketRegistry;
import com.alphaseries.server.packet.PacketSink;
import com.alphaseries.vb.Vb;

import java.util.ArrayList;
import java.util.List;

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
        return broadcastToActiveSessions(Vb.cStr(args[0]), "");
    }

    public static void Proc_7_2_803D60(Object... args) {
        if (args == null || args.length < 2) {
            return;
        }
        long socketIndex = Vb.val(args[0]);
        String packetBuffer = Vb.cStr(args[1]);
        if (Guardian.Proc_11_2_821390(socketIndex) != 1) {
            return;
        }
        if (packetBuffer.indexOf('\0') >= 0) {
            HandlingMUS.Proc_12_1_821AA0(socketIndex, buildCrossDomainPolicy());
            return;
        }

        while (packetBuffer.length() > 2) {
            packetBuffer = packetBuffer.substring(1);
            long packetLength = Crypto.Proc_3_4_6D3620(Vb.left(packetBuffer, 2));
            if (packetLength <= 0L || packetBuffer.length() < packetLength + 2L) {
                break;
            }
            String packetPayload = Vb.mid(packetBuffer, 3, (int) packetLength);
            String packetCode = Vb.left(packetPayload, 2);
            if (global_00829190) {
                Console.Proc_2_0_6D1510("[" + socketIndex + "] " + packetPayload, "GAME", "16711680");
            }
            dispatchReadyPacket(socketIndex, packetCode, packetPayload);
            packetBuffer = Vb.mid(packetBuffer, (int) packetLength + 3);
        }
    }

    public static long Proc_7_1_8038A0(Object... args) {
        if (args == null || args.length < 2) {
            return 0L;
        }
        return broadcastToActiveSessions(Vb.cStr(args[1]), Vb.lcase(Vb.cStr(args[0])));
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
        return Vb.cStr(packetBuffer).indexOf('\0') >= 0;
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
        String buffer = Vb.cStr(packetBuffer);
        if (isCrossDomainPolicyRequest(buffer)) {
            return packets;
        }
        while (buffer.length() > 2) {
            buffer = buffer.substring(1);
            long packetLength = Crypto.Proc_3_4_6D3620(Vb.left(buffer, 2));
            if (packetLength <= 0L || buffer.length() < packetLength + 2L) {
                break;
            }
            String packetPayload = Vb.mid(buffer, 3, (int) packetLength);
            ReadyPacket packet = new ReadyPacket();
            packet.payload = packetPayload;
            packet.code = Vb.left(packetPayload, 2);
            packets.add(packet);
            buffer = Vb.mid(buffer, (int) packetLength + 3);
        }
        return packets;
    }

    public static long broadcastToActiveSessions(String payload, String onlyUserName) {
        long sentCount = 0L;
        if (global_00829268 == null || global_00829268.isEmpty()) {
            return 0L;
        }
        String userFilter = Vb.cStr(onlyUserName);
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
                        String userName = Vb.lcase(fields[0]);
                        if (userFilter.isEmpty() || userName.equals(userFilter)) {
                            int socketIndex = (int) Vb.val(fields[1]);
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
        readyPacketRegistry.dispatch(new IncomingContext((int) socketIndex), Vb.cStr(packetCode), packetPayload);
    }
}
