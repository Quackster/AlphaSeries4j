package com.alphaseries.game.session;

public final class SessionState {
    private static final SessionState INSTANCE = new SessionState();

    private RepresentedSocketCache representedSockets = RepresentedSocketCache.empty();

    private SessionState() {
    }

    public static SessionState instance() {
        return INSTANCE;
    }

    public synchronized RepresentedSocketCache representedSockets() {
        return representedSockets;
    }

    public synchronized void setRepresentedSockets(RepresentedSocketCache representedSockets) {
        this.representedSockets = representedSockets == null ? RepresentedSocketCache.empty() : representedSockets;
    }

    public synchronized void setRepresentedSocketsFromLegacy(Object cacheText) {
        representedSockets = RepresentedSocketCache.fromLegacy(cacheText);
    }
}
