package com.alphaseries;

import com.alphaseries.vb.Vb;

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
        MySQL.Proc_5_0_6D3CD0("UPDATE settings SET value=UNIX_TIMESTAMP() "
            + "WHERE variable='com.server.socket.check.time' OR variable='com.server.socket.listen.time' LIMIT 2");
        runtime.startTimers();
        Console.Proc_2_0_6D1510("Server wurde erfolgreich gestartet.", "INITIALIZE", "16776960");
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
        HandlingMUS.configureMusSink(sink);
        Filesystems.configurePacketSink(sink);
        Main.configurePreSessionPacketSink((socketIndex, payload) -> Handling.Proc_6_241_7FC380(socketIndex, payload, 0));
    }

    private void configureProtocolLogging() {
        long musLog = Vb.val(Functions.Proc_10_0_809570("com.server.socket.mus.log", 0, 0));
        Boot.runTimed("MUS Server Protokollierer " + (musLog == 0L ? "deaktiviert" : "aktiviert"), () -> { });
        boolean gameLog = Vb.val(Functions.Proc_10_0_809570("com.server.socket.game.log", 0, 0)) != 0L;
        Licence.global_00829190 = gameLog;
        Filesystems.global_00829190 = gameLog;
        Boot.runTimed("Game Server Protokollierer aktiviert", () -> { });
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
        executor.scheduleAtFixedRate(() -> Main.pingTimer(0), 30, 30, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(Main::signerTimer, 1, 1, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(Main::botsTimer, 1, 1, TimeUnit.SECONDS);
    }

    private void acceptGameClients(ServerSocket serverSocket) {
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                int socketIndex = nextSocketIndex.getAndIncrement();
                gameSockets.put(socketIndex, socket);
                Guardian.setSocketConnected(socketIndex, true);
                Guardian.global_008291A0 += "[" + socketIndex + "]";
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
                Main.Proc_0_25_68FBC0(socketIndex, packetBuffer, 0);
            }
        } catch (IOException ignored) {
            // Socket disconnects are part of normal client lifecycle.
        } finally {
            gameSockets.remove(socketIndex);
            Handling.Proc_6_243_7FFEB0(socketIndex, 0, 0);
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
                if (Vb.val(Functions.Proc_10_0_809570("com.server.socket.mus.log", 0, 0)) != 0L) {
                    Console.Proc_2_0_6D1510(packetBuffer, "MUS", "16711680");
                }
                Main.processGameServerData(packetBuffer);
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
            output.write(clientPayload(payload).getBytes(StandardCharsets.ISO_8859_1));
            output.flush();
        } catch (IOException ignored) {
            closeGameSocket(socketIndex);
        }
    }

    public static String clientPayload(String musPayload) {
        String payload = Vb.cStr(musPayload);
        if (payload.startsWith("DATA\6")) {
            String[] fields = payload.split("\6", 3);
            if (fields.length >= 3) {
                int packetEnd = fields[2].indexOf('\7');
                return packetEnd >= 0 ? fields[2].substring(0, packetEnd) : fields[2];
            }
        }
        if (payload.startsWith("SHUTDOWN\6")) {
            return "";
        }
        return payload;
    }

    private void closeGameSocket(int socketIndex) {
        Socket socket = gameSockets.remove(socketIndex);
        if (socket != null) {
            closeQuietly(socket);
        }
        Handling.Proc_6_243_7FFEB0(socketIndex, 0, 0);
    }

    public static int configuredPort(String settingName) {
        long value = Vb.val(Functions.Proc_10_0_809570(settingName, 0, 0));
        if (value <= 0L || value > 65535L) {
            throw new IllegalStateException("Invalid port for " + settingName + ": " + value);
        }
        return (int) value;
    }

    private static void logTimed(String messageText, long startedAt) {
        long elapsedMillis = Math.max(0L, (System.nanoTime() - startedAt) / 1_000_000L);
        Boot.Proc_1_23_6D1480(messageText, "DEBUG, time: " + elapsedMillis + " ms");
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
