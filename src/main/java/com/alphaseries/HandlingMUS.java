package com.alphaseries;

import com.alphaseries.vb.Vb;

public final class HandlingMUS {
    private static PacketSink musSink = (socketIndex, payload) -> { };

    private HandlingMUS() {
    }

    public static void configureMusSink(PacketSink sink) {
        musSink = sink == null ? (socketIndex, payload) -> { } : sink;
    }

    public static void Proc_12_0_8218C0(Object... args) {
        int socketIndex = musSocketIndex(args);
        sendMusPayload(socketIndex, "SHUTDOWN" + '\6' + socketIndex + '\7');
    }

    public static void Proc_12_1_821AA0(Object... args) {
        int socketIndex = musSocketIndex(args);
        String messageText = args != null && args.length >= 2 ? Vb.cStr(args[1]) : "";
        sendMusPayload(socketIndex, "DATA" + '\6' + socketIndex + '\6' + messageText + '\7');
    }

    public static int musSocketIndex(Object... args) {
        if (args != null && args.length >= 1) {
            return (int) Vb.val(args[0]);
        }
        return 0;
    }

    public static void sendMusPayload(int socketIndex, String payload) {
        musSink.send(socketIndex, payload);
    }
}
