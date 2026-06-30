package com.alphaseries.game.session;

public final class SessionState {
    private static final SessionState INSTANCE = new SessionState();

    private GameServerSessionState gameServerSession = GameServerSessionState.empty();
    private RepresentedSocketCache representedSockets = RepresentedSocketCache.empty();

    private SessionState() {
    }

    public static SessionState instance() {
        return INSTANCE;
    }

    public synchronized RepresentedSocketCache representedSockets() {
        return representedSockets;
    }

    public synchronized GameServerSessionState gameServerSession() {
        return gameServerSession;
    }

    public synchronized void setGameServerSession(GameServerSessionState gameServerSession) {
        this.gameServerSession = gameServerSession == null ? GameServerSessionState.empty() : gameServerSession;
    }

    public synchronized void setGameServerSessionFromLegacy(String queuedPacketData, String readySessionMarkers) {
        gameServerSession = GameServerSessionState.fromLegacy(queuedPacketData, readySessionMarkers);
    }

    public synchronized void setRepresentedSockets(RepresentedSocketCache representedSockets) {
        this.representedSockets = representedSockets == null ? RepresentedSocketCache.empty() : representedSockets;
    }

    public synchronized void setRepresentedSocketsFromLegacy(Object cacheText) {
        representedSockets = RepresentedSocketCache.fromLegacy(cacheText);
    }
}
