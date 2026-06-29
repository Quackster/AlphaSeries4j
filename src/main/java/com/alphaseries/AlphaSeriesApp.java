package com.alphaseries;

import com.alphaseries.config.AppDatabaseConfig;

import java.nio.file.Path;

public final class AlphaSeriesApp {
    private AlphaSeriesApp() {
    }

    public static void main(String[] args) {
        AppDatabaseConfig.configureDefaultConnector();
        String configText = Handling.Proc_6_239_7FC170(Path.of(Functions.applicationPath, "config.ini").toString(), 0, 0);
        long databaseStartedAt = System.nanoTime();
        if (Crypto.Proc_3_5_6D3880(configText) != 1L) {
            System.err.println("AlphaSeries database connection failed");
            System.exit(1);
        }
        Boot.Proc_1_23_6D1480("Verbindung zum MySQL Server hergestellt",
            "DEBUG, time: " + elapsedMillis(databaseStartedAt) + " ms");
        Main.LifecycleResult lifecycle = Main.formInitialize(Main.INITIALIZING_CAPTION_TEMPLATE);
        System.setProperty("alphaseries.consoleTitle", lifecycle.consoleTitle);
        if (!lifecycle.success) {
            System.err.println("AlphaSeries startup failed");
            System.exit(1);
        }
        Console.Proc_2_0_6D1510("Verbunden mit folgendem Serial: " + lifecycle.productKey,
            "INITIALIZE", "16776960");
        Main.StartupResult startup = Main.startServer(lifecycle);
        if (!startup.success) {
            System.err.println("AlphaSeries server startup failed at " + startup.stage + ": " + startup.message);
            System.exit(1);
        }
        lifecycle.consoleTitle = Main.initializedConsoleTitle(lifecycle.consoleTitle);
        lifecycle.caption = Main.javaCaptionFromConsoleTitle(lifecycle.consoleTitle);
        System.setProperty("alphaseries.consoleTitle", lifecycle.consoleTitle);
        System.out.println(lifecycle.caption);
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
