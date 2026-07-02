package com.alphaseries.server.runtime;

import com.alphaseries.Handling;
import com.alphaseries.config.AppConfigState;
import com.alphaseries.dao.mysql.ServerMaintenanceDao;
import com.alphaseries.db.Database;
import com.alphaseries.db.MySQL;
import com.alphaseries.server.lifecycle.BootLog;
import com.alphaseries.server.lifecycle.LifecycleState;
import com.alphaseries.server.logging.Console;
import com.alphaseries.server.mus.MusConnectionManager;
import com.alphaseries.server.mus.MusPayloads;
import com.alphaseries.server.packet.Filesystems;
import com.alphaseries.server.packet.PacketSink;
import com.alphaseries.util.NumberUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class AlphaSeriesRuntime implements AutoCloseable {
    private final AtomicInteger nextSocketIndex = new AtomicInteger(1);
    private final Map<Integer, Socket> gameSockets = new ConcurrentHashMap<Integer, Socket>();
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);
    private final CountDownLatch stopLatch = new CountDownLatch(1);
    private ServerSocket gameServer;
    private ServerSocket musServer;

    private AlphaSeriesRuntime() {
    }

    public static AlphaSeriesRuntime start() throws IOException {
        AlphaSeriesRuntime runtime = new AlphaSeriesRuntime();
        runtime.configurePacketSinks();
        runtime.configureProtocolLogging();
        runtime.startMusServer(configuredPort("com.server.socket.mus.port"));
        runtime.startGameServer(configuredPort("com.server.socket.game.port"));
        markSocketStartupTimes();
        runtime.startTimers();
        Console.logSourceLine("Server wurde erfolgreich gestartet.", "INITIALIZE", 16776960L);
        return runtime;
    }

    public void await() throws InterruptedException {
        stopLatch.await();
    }

    @Override
    public void close() {
        closeQuietly(gameServer);
        closeQuietly(musServer);
        executor.shutdownNow();
        stopLatch.countDown();
    }

    private void configurePacketSinks() {
        PacketSink sink = this::sendToGameSocket;
        MusConnectionManager.instance().configureSink(sink);
        Filesystems.configurePacketSink(sink);
        GameServerBridge.configurePreSessionPacketSink(Handling::processPreSessionPacketBuffer);
    }

    private void configureProtocolLogging() {
        long musLog = AppConfigState.instance().settingsCache().longValueOrDefault("com.server.socket.mus.log", 0);
        BootLog.runTimed("MUS Server Protokollierer " + (musLog == 0L ? "deaktiviert" : "aktiviert"), () -> { });
        boolean gameLog = AppConfigState.instance().settingsCache().longValueOrDefault("com.server.socket.game.log", 0) != 0L;
        LifecycleState.instance().setPacketTraceEnabled(gameLog);
        Filesystems.configurePacketTracing(gameLog);
        BootLog.runTimed("Game Server Protokollierer aktiviert", () -> { });
    }

    private void startGameServer(int port) throws IOException {
        long startedAt = System.nanoTime();
        gameServer = new ServerSocket(port);
        executor.execute(() -> acceptGameClients(gameServer));
        logTimed("Game Server gestartet", startedAt);
    }

    private void startMusServer(int port) throws IOException {
        long startedAt = System.nanoTime();
        musServer = new ServerSocket(port);
        executor.execute(() -> acceptMusClients(musServer));
        logTimed("MUS Server gestartet", startedAt);
    }

    private void startTimers() {
        executor.scheduleAtFixedRate(() -> RuntimeTasks.pingTimer(0), 30, 30, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(RuntimeTasks::signerTimer, 1, 1, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(RuntimeTasks::botsTimer, 1, 1, TimeUnit.SECONDS);
    }

    private void acceptGameClients(ServerSocket serverSocket) {
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                int socketIndex = nextSocketIndex.getAndIncrement();
                gameSockets.put(socketIndex, socket);
                Guardian.setSocketConnected(socketIndex, true);
                Guardian.addSocketMarker(socketIndex);
                executor.execute(() -> readGameClient(socketIndex, socket));
            } catch (IOException ignored) {
                if (!serverSocket.isClosed()) {
                    close();
                }
            }
        }
    }

    private void readGameClient(int socketIndex, Socket socket) {
        try (Socket client = socket; InputStream input = client.getInputStream()) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = input.read(buffer)) != -1) {
                String packetBuffer = new String(buffer, 0, read, StandardCharsets.ISO_8859_1);
                GameServerBridge.processClientPacket(socketIndex, packetBuffer);
            }
        } catch (IOException ignored) {
            // Socket disconnects are part of normal client lifecycle.
        } finally {
            gameSockets.remove(socketIndex);
            Handling.disconnectSocket(socketIndex);
        }
    }

    private void acceptMusClients(ServerSocket serverSocket) {
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                executor.execute(() -> readMusClient(socket));
            } catch (IOException ignored) {
                if (!serverSocket.isClosed()) {
                    close();
                }
            }
        }
    }

    private void readMusClient(Socket socket) {
        try (Socket client = socket; InputStream input = client.getInputStream()) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = input.read(buffer)) != -1) {
                String packetBuffer = new String(buffer, 0, read, StandardCharsets.ISO_8859_1);
                if (AppConfigState.instance().settingsCache().longValueOrDefault("com.server.socket.mus.log", 0) != 0L) {
                    Console.logSourceLine(packetBuffer, "MUS", 16711680L);
                }
                GameServerBridge.processGameServerData(packetBuffer);
            }
        } catch (IOException ignored) {
            // Socket disconnects are part of normal client lifecycle.
        }
    }

    private void sendToGameSocket(int socketIndex, String payload) {
        Socket socket = gameSockets.get(socketIndex);
        if (socket == null || socket.isClosed()) {
            return;
        }
        try {
            OutputStream output = socket.getOutputStream();
            output.write(MusPayloads.clientPayload(MusPayloads.clientFrame(payload)).getBytes(StandardCharsets.ISO_8859_1));
            output.flush();
        } catch (IOException ignored) {
            closeGameSocket(socketIndex);
        }
    }

    private void closeGameSocket(int socketIndex) {
        Socket socket = gameSockets.remove(socketIndex);
        if (socket != null) {
            closeQuietly(socket);
        }
        Handling.disconnectSocket(socketIndex);
    }

    public static int configuredPort(String settingName) {
        long value = AppConfigState.instance().settingsCache().longValueOrDefault(settingName, 0);
        if (value <= 0L || value > 65535L) {
            throw new IllegalStateException("Invalid port for " + settingName + ": " + value);
        }
        return (int) value;
    }

    private static void markSocketStartupTimes() {
        Database database = MySQL.configuredDatabase();
        if (database == null) {
            return;
        }
        try {
            new ServerMaintenanceDao(database).markSocketStartupTimes();
        } catch (Exception ignored) {
            // The original startup SQL path suppresses failures.
        }
    }

    private static void logTimed(String messageText, long startedAt) {
        long elapsedMillis = Math.max(0L, (System.nanoTime() - startedAt) / 1_000_000L);
        BootLog.logBootLine(messageText, "DEBUG, time: " + elapsedMillis + " ms");
    }

    private static void closeQuietly(ServerSocket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ignored) {
                // Closing during shutdown is best effort.
            }
        }
    }

    private static void closeQuietly(Socket socket) {
        try {
            socket.close();
        } catch (IOException ignored) {
            // Closing during shutdown is best effort.
        }
    }
}
