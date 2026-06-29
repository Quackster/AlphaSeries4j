package com.alphaseries.messages.incoming;

public final class IncomingContext {
    private final int socketIndex;

    public IncomingContext(int socketIndex) {
        this.socketIndex = socketIndex;
    }

    public int socketIndex() {
        return socketIndex;
    }
}
