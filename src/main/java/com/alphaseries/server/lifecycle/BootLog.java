package com.alphaseries.server.lifecycle;

import com.alphaseries.config.AppPaths;
import com.alphaseries.server.logging.Console;
import com.alphaseries.util.FileUtils;
import com.alphaseries.util.StringUtils;

import java.util.function.BooleanSupplier;

public final class BootLog {
    public static final String DEFAULT_BOOT_NOTICE = "ILLEGAL KOMBINATION: Der Programmierer haftet nicht f\u00fcr das "
        + "Nutzen dieses Servers - Das nutzen mit rechtlich gesch\u00fctzten Bildern ist strafbar - Bitte eigene "
        + "Software nutzen";
    public static final String INITIALIZATION_INTEGRITY_FAILURE_MESSAGE =
        "Unable to intialize. File may be corrupted!";
    public static final String SERVER_RETURNED_ERROR_PREFIX = "Server has Exit Suburned following error:       ";
    private static final String[] STARTUP_CREDIT_LINES = new String[] {
        "                                                           2 . 0 - \"Meilenstein 2\"",
        "         Server Autor: Privilege, Deutsche \u00dcbersetzung: Medaillon",
        "         Shoutouts: Tweeney, Pure, MoBaT, Donkjam, Arths, Jairo, Moogly and Bloopser"
    };

    private BootLog() {
    }

    public static void logBootLine(String messageText, String logChannel) {
        Console.logSourceLine(StringUtils.text(messageText), StringUtils.text(logChannel), 65280L);
    }

    public static void initializeBootLogFiles() {
        String productName = LifecycleState.instance().runtimeState().productName();
        String nowText = java.time.LocalDateTime.now().toString();
        java.nio.file.Path appPath = java.nio.file.Path.of(AppPaths.applicationPath());
        FileUtils.appendTextFile(appPath.resolve("ERR.log").toString(), bootErrorLogHeader(productName, nowText));
        FileUtils.appendTextFile(appPath.resolve("SLOW.log").toString(), bootSlowLogHeader(productName, nowText));
    }

    public static void printStartupNotice() {
        printStartupNotice(DEFAULT_BOOT_NOTICE);
    }

    public static void printStartupNotice(String messageText) {
        String message = StringUtils.text(messageText);
        if (message.length() <= 10) {
            return;
        }
        Console.appendPlainLine(message, 49344L);
        Console.appendOptionalSourceLine("", "HIDDEN", 262144L);
    }

    public static String[] startupCreditLines() {
        return STARTUP_CREDIT_LINES.clone();
    }

    public static void printStartupCredits() {
        for (String line : STARTUP_CREDIT_LINES) {
            Console.appendPlainLine(line, 49344L);
        }
    }

    public static String initializationIntegrityFailureMessage(boolean failed, String caption) {
        if (failed && StringUtils.text(caption).contains("INITIALISIERE")) {
            return INITIALIZATION_INTEGRITY_FAILURE_MESSAGE;
        }
        return "";
    }

    public static String serverReturnedErrorMessage(String description) {
        return SERVER_RETURNED_ERROR_PREFIX + StringUtils.text(description);
    }

    public static String bootErrorLogHeader(String productName, String nowText) {
        return bootLogHeader(productName,
            " Emulator is running since " + StringUtils.text(nowText) + ", errors are being logged.");
    }

    public static String bootSlowLogHeader(String productName, String nowText) {
        return bootLogHeader(productName,
            " Emulator is running since " + StringUtils.text(nowText)
                + ", slow query are being logged if you are running the development mode.");
    }

    public static String bootLogHeader(String productName, String runningLine) {
        String separator = "-------------------------------------------------------------------------------------------------------------------------------------------------------";
        return separator + "\r\n"
            + " Alpha Series [Version " + StringUtils.text(productName) + "\r\n"
            + StringUtils.text(runningLine) + "\r\n"
            + separator + "\r\n";
    }

    public static void runTimed(String messageText, Runnable action) {
        long startedAt = System.nanoTime();
        if (action != null) {
            action.run();
        }
        long elapsedMillis = Math.max(0L, (System.nanoTime() - startedAt) / 1_000_000L);
        logBootLine(messageText, "DEBUG, time: " + elapsedMillis + " ms");
    }

    public static void runTimed(String messageText, BooleanSupplier action) {
        long startedAt = System.nanoTime();
        boolean success = action == null || action.getAsBoolean();
        if (!success) {
            return;
        }
        long elapsedMillis = Math.max(0L, (System.nanoTime() - startedAt) / 1_000_000L);
        logBootLine(messageText, "DEBUG, time: " + elapsedMillis + " ms");
    }
}
