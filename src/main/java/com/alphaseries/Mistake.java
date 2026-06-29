package com.alphaseries;

public final class Mistake {
    public static final String VB_FORM_NAME = "Mistake";
    public static final String CAPTION = "Please do these steps to run the Emulator correctly!";
    public static final String MESSAGE =
        "The Emulator does not work with your current PC-settings. Please change these settings in your \"Local Settings\"!";

    private Mistake() {
    }

    public static MessageBox formLoad() {
        return new MessageBox(MESSAGE, MessageStyle.CRITICAL);
    }

    public static QueryUnloadResult formQueryUnload(int unloadMode) {
        return new QueryUnloadResult(false, true, unloadMode);
    }

    public enum MessageStyle {
        CRITICAL
    }

    public static final class MessageBox {
        public final String message;
        public final MessageStyle style;

        private MessageBox(String message, MessageStyle style) {
            this.message = message;
            this.style = style;
        }
    }

    public static final class QueryUnloadResult {
        public final boolean cancel;
        public final boolean exitRequested;
        public final int unloadMode;

        private QueryUnloadResult(boolean cancel, boolean exitRequested, int unloadMode) {
            this.cancel = cancel;
            this.exitRequested = exitRequested;
            this.unloadMode = unloadMode;
        }
    }
}
