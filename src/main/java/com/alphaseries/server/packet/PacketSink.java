package com.alphaseries.server.packet;

@FunctionalInterface
public interface PacketSink {
    void send(int socketIndex, String payload);
}
