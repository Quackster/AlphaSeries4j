package com.alphaseries;

public final class Handling_MUS {
    private Handling_MUS() {
    }

    public static void configureMusSink(PacketSink sink) {
        HandlingMUS.configureMusSink(sink);
    }

    public static void Proc_12_0_8218C0(Object... args) {
        HandlingMUS.Proc_12_0_8218C0(args);
    }

    public static void Proc_12_1_821AA0(Object... args) {
        HandlingMUS.Proc_12_1_821AA0(args);
    }

    public static int musSocketIndex(Object... args) {
        return HandlingMUS.musSocketIndex(args);
    }

    public static void sendMusPayload(int socketIndex, String payload) {
        HandlingMUS.sendMusPayload(socketIndex, payload);
    }
}
