package com.alphaseries.protocol;

public interface OutgoingPayload {
    void compose(PacketBuilder packet);
}
