package com.alphaseries.game.session;

import com.alphaseries.util.StringUtils;

import java.util.List;

public final class SessionState {
    private static final SessionState INSTANCE = new SessionState();

    private GameServerSessionState gameServerSession = GameServerSessionState.empty();
    private RepresentedSocketCache representedSockets = RepresentedSocketCache.empty();
    private SessionRegistry sessionRegistry = SessionRegistry.empty();
    private SocketMarkerSet socketMarkers = SocketMarkerSet.empty();

    private SessionState() {
    }

    public static SessionState instance() {
        return INSTANCE;
    }

    public synchronized RepresentedSocketCache representedSockets() {
        return representedSockets;
    }

    public synchronized SocketMarkerSet socketMarkers() {
        return socketMarkers;
    }

    public synchronized SessionRegistry sessionRegistry() {
        return sessionRegistry;
    }

    public synchronized void setSessionRegistry(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry == null ? SessionRegistry.empty() : sessionRegistry;
    }

    public synchronized void setSocketMarkers(SocketMarkerSet socketMarkers) {
        this.socketMarkers = socketMarkers == null ? SocketMarkerSet.empty() : socketMarkers;
    }

    public synchronized GameServerSessionState gameServerSession() {
        return gameServerSession;
    }

    public synchronized void setGameServerSession(GameServerSessionState gameServerSession) {
        this.gameServerSession = gameServerSession == null ? GameServerSessionState.empty() : gameServerSession;
    }

    public synchronized void setRepresentedSockets(RepresentedSocketCache representedSockets) {
        this.representedSockets = representedSockets == null ? RepresentedSocketCache.empty() : representedSockets;
    }

    /**
     * Original function: Proc_9_6_808080.
     */
    public synchronized String socketUserId(String socketIndex) {
        return sessionRegistry.recordField("0:", socketIndex, 0);
    }

    public synchronized long sessionUserIdBySocket(int socketIndex) {
        return sessionRegistry.userIdBySocket(socketIndex);
    }

    /**
     * Original function: Proc_9_8_8086A0.
     */
    public synchronized long linkedUserSocketIndex(String recordId) {
        return sessionRegistry.linkedLong(StringUtils.text(recordId), true);
    }

    /**
     * Original function: Proc_9_9_808AC0.
     */
    public synchronized long linkedSocketIndex(String recordId) {
        return sessionRegistry.linkedLong(StringUtils.text(recordId), false);
    }

    /**
     * Original function: Proc_9_10_808F30.
     */
    public synchronized long sessionCacheLong(String keyName, long columnIndex) {
        return sessionRegistry.cacheLong(StringUtils.text(keyName), columnIndex);
    }

    public synchronized void storeSocketSession(int socketIndex, String sessionRecord) {
        sessionRegistry.storeSocketSession(socketIndex, sessionRecord);
    }

    public synchronized List<SessionRegistry.SocketSession> socketSessions() {
        return sessionRegistry.socketSessions();
    }
}
