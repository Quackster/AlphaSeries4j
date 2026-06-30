package com.alphaseries;

import com.alphaseries.server.mus.MusConnectionManager;
import com.alphaseries.server.packet.PacketSink;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public final class HandlingMUS {
    private HandlingMUS() {
    }

    public static void configureMusSink(PacketSink sink) {
        MusConnectionManager.instance().configureSink(sink);
    }

    public static void Proc_12_0_8218C0(Object... args) {
        int socketIndex = musSocketIndex(args);
        MusConnectionManager.instance().sendShutdown(socketIndex);
    }

    public static void Proc_12_1_821AA0(Object... args) {
        int socketIndex = musSocketIndex(args);
        String messageText = args != null && args.length >= 2 ? StringUtils.text(args[1]) : "";
        MusConnectionManager.instance().sendData(socketIndex, messageText);
    }

    public static int musSocketIndex(Object... args) {
        if (args != null && args.length >= 1) {
            return NumberUtils.parseInt(args[0]);
        }
        return 0;
    }

    public static void sendMusPayload(int socketIndex, String payload) {
        MusConnectionManager.instance().sendPayload(socketIndex, payload);
    }
}
