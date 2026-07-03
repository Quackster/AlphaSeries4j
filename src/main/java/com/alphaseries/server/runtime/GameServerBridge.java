package com.alphaseries.server.runtime;

import com.alphaseries.config.AppPaths;
import com.alphaseries.game.session.GameServerSessionState;
import com.alphaseries.game.session.SessionState;
import com.alphaseries.protocol.PacketBuilder;
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

    public static final class GameServerPacket {
        private final String commandName;
        private final long socketIndex;
        private final String payload;

        private GameServerPacket(String commandName, long socketIndex, String payload) {
            this.commandName = StringUtils.text(commandName);
            this.socketIndex = socketIndex;
            this.payload = StringUtils.text(payload);
        }

        public String commandName() {
            return commandName;
        }

        public long socketIndex() {
            return socketIndex;
        }

        public boolean hasPayload() {
            return !payload.isEmpty();
        }

        public void appendPayloadTo(PacketBuilder packet) {
            if (packet != null) {
                packet.appendRaw(payload);
            }
        }
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
            for (String packet : StringUtils.delimitedFields(incomingData, '\1')) {
                if (packet.isEmpty()) {
                    continue;
                }
                GameServerPacket gamePacket = gameServerPacket(packet);
                if ("SHUTDOWN".equals(gamePacket.commandName())) {
                    if (gamePacket.socketIndex() > 0L) {
                        SocketLifecycle.disconnectSocket(gamePacket.socketIndex());
                    }
                } else if ("LISTEN".equals(gamePacket.commandName())) {
                    if (gamePacket.socketIndex() > 0L) {
                        Guardian.toggleSocketMarker(gamePacket.socketIndex());
                    }
                } else if ("DATA".equals(gamePacket.commandName())) {
                    if (gamePacket.socketIndex() > 0L && gamePacket.hasPayload()) {
                        appendGameServerPacket(gamePacket.socketIndex(), packetPayload(gamePacket));
                    }
                } else if (gamePacket.socketIndex() > 0L) {
                    SocketLifecycle.disconnectSocket(gamePacket.socketIndex());
                }
            }
        } catch (Exception ignored) {
            // VB6 source suppresses game-server read failures.
        }
    }

    private static void appendGameServerPacket(long socketIndex, String packetPayload) {
        GameServerSessionState sessionState = SessionState.instance().gameServerSession();
        sessionState.appendPacketPayload(socketIndex, packetPayload);
        SessionState.instance().setGameServerSession(sessionState);
    }

    public static GameServerPacket gameServerPacket(String packetText) {
        String text = StringUtils.text(packetText);
        StringUtils.IndexedFields fields = StringUtils.indexedFields(text, '\2');
        String commandName = fields.text(0).toUpperCase(Locale.ROOT);
        String socketText = fields.text(1);
        long socketIndex = socketText.isEmpty() ? NumberUtils.parseLong(commandName) : NumberUtils.parseLong(socketText);
        String payload = "DATA".equals(commandName) ? gameServerPacketPayload(text) : "";
        return new GameServerPacket(commandName, socketIndex, payload);
    }

    private static String gameServerPacketPayload(String packetText) {
        String text = StringUtils.text(packetText);
        int firstDelimiter = text.indexOf('\2');
        if (firstDelimiter < 0) {
            return "";
        }
        int secondDelimiter = text.indexOf('\2', firstDelimiter + 1);
        if (secondDelimiter < 0 || secondDelimiter + 1 >= text.length()) {
            return "";
        }
        return text.substring(secondDelimiter + 1);
    }

    private static String packetPayload(GameServerPacket packet) {
        PacketBuilder payload = PacketBuilder.create();
        if (packet != null) {
            packet.appendPayloadTo(payload);
        }
        return payload.build();
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
