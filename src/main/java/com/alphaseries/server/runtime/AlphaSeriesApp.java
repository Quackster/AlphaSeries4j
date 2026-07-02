package com.alphaseries.server.runtime;

import com.alphaseries.config.AppPaths;
import com.alphaseries.config.AppDatabaseConfig;
import com.alphaseries.server.lifecycle.BootLog;
import com.alphaseries.server.lifecycle.ServerLifecycle;
import com.alphaseries.server.logging.Console;
import com.alphaseries.util.FileUtils;

import java.nio.file.Path;

public final class AlphaSeriesApp {
    private AlphaSeriesApp() {
    }

    public static void main(String[] args) {
        AppDatabaseConfig.configureDefaultConnector();
        String configText = FileUtils.readTextFile(Path.of(AppPaths.applicationPath(), "config.ini").toString());
        long databaseStartedAt = System.nanoTime();
        if (AppDatabaseConfig.connectDatabaseFromConfig(configText) != 1L) {
            System.err.println("AlphaSeries database connection failed");
            System.exit(1);
        }
        BootLog.logBootLine("Verbindung zum MySQL Server hergestellt",
            "DEBUG, time: " + elapsedMillis(databaseStartedAt) + " ms");
        ServerLifecycle.LifecycleResult lifecycle =
            ServerLifecycle.formInitialize(ServerLifecycle.INITIALIZING_CAPTION_TEMPLATE);
        System.setProperty("alphaseries.consoleTitle", lifecycle.consoleTitle());
        if (!lifecycle.success()) {
            System.err.println("AlphaSeries startup failed");
            System.exit(1);
        }
        BootLog.printStartupNotice();
        Console.logSourceLine("Verbunden mit folgendem Serial: " + lifecycle.productKey(),
            "INITIALIZE", 16776960L);
        ServerLifecycle.StartupResult startup = ServerLifecycle.startServer(lifecycle);
        if (!startup.success()) {
            System.err.println("AlphaSeries server startup failed at " + startup.stage() + ": " + startup.message());
            System.exit(1);
        }
        String initializedConsoleTitle = ServerLifecycle.initializedConsoleTitle(lifecycle.consoleTitle());
        String initializedCaption = ServerLifecycle.javaCaptionFromConsoleTitle(initializedConsoleTitle);
        System.setProperty("alphaseries.consoleTitle", initializedConsoleTitle);
        System.out.println(initializedCaption);
        try (AlphaSeriesRuntime runtime = AlphaSeriesRuntime.start()) {
            runtime.await();
        } catch (Exception ex) {
            System.err.println("AlphaSeries runtime failed: " + ex.getMessage());
            System.exit(1);
        }
    }

    private static long elapsedMillis(long startedAt) {
        return Math.max(0L, (System.nanoTime() - startedAt) / 1_000_000L);
    }
}
