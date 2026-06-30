package com.alphaseries.server.mus;

import com.alphaseries.server.packet.PacketSink;

public final class MusConnectionManager {
    private static final MusConnectionManager INSTANCE = new MusConnectionManager();
    private PacketSink sink = (socketIndex, payload) -> { };

    private MusConnectionManager() {
    }

    public static MusConnectionManager instance() {
        return INSTANCE;
    }

    public void configureSink(PacketSink sink) {
        this.sink = sink == null ? (socketIndex, payload) -> { } : sink;
    }

    /**
     * Original function: HandlingMUS.Proc_12_0_8218C0.
     */
    public void sendShutdown(int socketIndex) {
        sendPayload(socketIndex, MusPayloads.shutdown(socketIndex));
    }

    /**
     * Original function: HandlingMUS.Proc_12_1_821AA0.
     */
    public void sendData(int socketIndex, String messageText) {
        sendPayload(socketIndex, MusPayloads.data(socketIndex, messageText));
    }

    public void sendPayload(int socketIndex, String payload) {
        sink.send(socketIndex, payload);
    }
}
