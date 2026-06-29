package com.alphaseries;

public final class AlphaSeriesApp {
    private AlphaSeriesApp() {
    }

    public static void main(String[] args) {
        Main.LifecycleResult lifecycle = Main.formInitialize("AlphaSeries %% [!]");
        if (!lifecycle.success) {
            System.err.println("AlphaSeries startup failed");
            System.exit(1);
        }
        if (!Main.runServer(lifecycle.caption, "")) {
            System.err.println("AlphaSeries server startup failed");
            System.exit(1);
        }
        System.out.println(lifecycle.caption);
    }
}
