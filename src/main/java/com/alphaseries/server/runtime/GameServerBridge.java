package com.alphaseries.server.runtime;

import com.alphaseries.config.AppPaths;
import com.alphaseries.Handling;
import com.alphaseries.game.session.GameServerSessionState;
import com.alphaseries.game.session.SessionState;
import com.alphaseries.server.lifecycle.LifecycleState;
import com.alphaseries.server.logging.Console;
import com.alphaseries.server.packet.Filesystems;
import com.alphaseries.server.packet.PacketSink;
import com.alphaseries.util.FileUtils;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.Locale;

public final class GameServerBridge {
    private static PacketSink preSessionPacketSink = (socketIndex, payload) -> { };

    private GameServerBridge() {
    }

    public record GameServerPacket(String commandName, long socketIndex, String payload) {
    }

    public static void configurePreSessionPacketSink(PacketSink sink) {
        preSessionPacketSink = sink == null ? (socketIndex, payload) -> { } : sink;
    }

    /**
     * Original function: Proc_0_25_68FBC0.
     */
    public static void processClientPacket(long socketIndex, String packetData) {
        try {
            if (!Guardian.isSocketConnected(socketIndex)) {
                return;
            }
            if (isGameSessionReady(socketIndex)) {
                Filesystems.processReadyPacketBuffer(socketIndex, packetData);
            } else {
                preSessionPacketSink.send((int) socketIndex, packetData);
            }
        } catch (Exception ex) {
            if (LifecycleState.instance().runtimeState().debugLoggingEnabled()) {
                Console.logSourceLine("[" + socketIndex + "] " + ex.getMessage() + " -> " + packetData,
                    "ERROR", 255L);
                FileUtils.appendTextFile(AppPaths.applicationPath() + "/ERR.log",
                    "ERROR] " + packetData + " (" + ex.getMessage() + ")\r\n0\r\n\r\n\r\n");
            }
        }
    }

    public static String gameServerUnknownEventAccept() {
        Guardian.setGameServerConnected(true);
        return "ACCEPT 16387";
    }

    public static String gameServerUnknownEventListen() {
        Guardian.setGameServerConnected(true);
        return "LISTEN";
    }

    public static boolean dataProcessTimer(long socketIndex) {
        try {
            if (!Guardian.isSocketConnected(socketIndex)) {
                return false;
            }
            String packetData = popGameServerPacketData(socketIndex);
            if (packetData.isEmpty()) {
                return false;
            }
            processClientPacket(socketIndex, packetData);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static void processGameServerData(String incomingData) {
        try {
            for (String packet : StringUtils.text(incomingData).split("\1", -1)) {
                if (packet.isEmpty()) {
                    continue;
                }
                GameServerPacket gamePacket = gameServerPacket(packet);
                if ("SHUTDOWN".equals(gamePacket.commandName())) {
                    if (gamePacket.socketIndex() > 0L) {
                        Handling.disconnectSocket(gamePacket.socketIndex());
                    }
                } else if ("LISTEN".equals(gamePacket.commandName())) {
                    if (gamePacket.socketIndex() > 0L) {
                        Guardian.toggleSocketMarker(gamePacket.socketIndex());
                    }
                } else if ("DATA".equals(gamePacket.commandName())) {
                    if (gamePacket.socketIndex() > 0L && !gamePacket.payload().isEmpty()) {
                        appendGameServerPacketPayload(gamePacket.socketIndex(), gamePacket.payload());
                    }
                } else if (gamePacket.socketIndex() > 0L) {
                    Handling.disconnectSocket(gamePacket.socketIndex());
                }
            }
        } catch (Exception ignored) {
            // VB6 source suppresses game-server read failures.
        }
    }

    public static void appendGameServerPacketPayload(long socketIndex, String packetPayload) {
        GameServerSessionState sessionState = SessionState.instance().gameServerSession();
        sessionState.appendPacketPayload(socketIndex, packetPayload);
        SessionState.instance().setGameServerSession(sessionState);
    }

    public static GameServerPacket gameServerPacket(String packetText) {
        String[] fields = StringUtils.text(packetText).split("\2", -1);
        String commandName = fields.length > 0 ? StringUtils.text(fields[0]).toUpperCase(Locale.ROOT) : "";
        long socketIndex = fields.length >= 2 ? NumberUtils.parseLong(fields[1]) : NumberUtils.parseLong(commandName);
        String payload = "DATA".equals(commandName) ? gameServerPacketPayload(fields) : "";
        return new GameServerPacket(commandName, socketIndex, payload);
    }

    private static String gameServerPacketPayload(String[] fields) {
        if (fields == null || fields.length <= 2) {
            return "";
        }
        StringBuilder payload = new StringBuilder();
        for (int fieldIndex = 2; fieldIndex < fields.length; fieldIndex++) {
            if (payload.length() > 0) {
                payload.append('\2');
            }
            payload.append(StringUtils.text(fields[fieldIndex]));
        }
        return payload.toString();
    }

    public static String popGameServerPacketData(long socketIndex) {
        GameServerSessionState sessionState = SessionState.instance().gameServerSession();
        String payload = sessionState.popPacketData(socketIndex);
        SessionState.instance().setGameServerSession(sessionState);
        return payload;
    }

    public static boolean isGameSessionReady(long socketIndex) {
        return SessionState.instance().gameServerSession().isReady(socketIndex);
    }
}
