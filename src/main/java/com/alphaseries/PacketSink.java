package com.alphaseries;

public interface PacketSink {
    void send(int socketIndex, String payload);
}
