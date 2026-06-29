package com.alphaseries;

import java.nio.file.Path;

public final class AlphaSeriesApp {
    private AlphaSeriesApp() {
    }

    public static void main(String[] args) {
        AppDatabaseConfig.configureDefaultConnector();
        String configText = Handling.Proc_6_239_7FC170(Path.of(Functions.applicationPath, "config.ini").toString(), 0, 0);
        if (Crypto.Proc_3_5_6D3880(configText) != 1L) {
            System.err.println("AlphaSeries database connection failed");
            System.exit(1);
        }
        Main.LifecycleResult lifecycle = Main.formInitialize("AlphaSeries %% [!]");
        System.setProperty("alphaseries.consoleTitle", lifecycle.consoleTitle);
        if (!lifecycle.success) {
            System.err.println("AlphaSeries startup failed");
            System.exit(1);
        }
        if (!Main.runServer(lifecycle)) {
            System.err.println("AlphaSeries server startup failed");
            System.exit(1);
        }
        System.out.println(lifecycle.caption);
    }
}
