package com.alphaseries.server.lifecycle;

public final class StartupEnvironmentError {
    public static final String CAPTION = "Please do these steps to run the Emulator correctly!";
    public static final String MESSAGE =
        "The Emulator does not work with your current PC-settings. Please change these settings in your \"Local Settings\"!";
    private static final String[] INSTRUCTION_CAPTIONS = new String[] {
        "1. Click here to customize your regional options!",
        "2. Select the decimal symbol ,",
        "3. Click \"OK\" to apply your changes. You need to restart your Computer/VPS"
    };

    private StartupEnvironmentError() {
    }

    public static MessageBox loadMessage() {
        return new MessageBox(MESSAGE, MessageStyle.CRITICAL);
    }

    public static String[] instructionCaptions() {
        return INSTRUCTION_CAPTIONS.clone();
    }

    public static QueryUnloadResult queryUnload(int unloadMode) {
        return new QueryUnloadResult(false, true, unloadMode);
    }

    public enum MessageStyle {
        CRITICAL
    }

    public record MessageBox(String message, MessageStyle style) {
    }

    public record QueryUnloadResult(boolean cancel, boolean exitRequested, int unloadMode) {
    }
}
