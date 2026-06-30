package com.alphaseries;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Console {
    private static final List<Entry> entries = new ArrayList<Entry>();

    private Console() {
    }

    /**
     * Original function: Proc_2_0_6D1510.
     */
    public static void Proc_2_0_6D1510(Object... args) {
        String messageText = args != null && args.length >= 1 ? StringUtils.text(args[0]) : "";
        String sourceName = args != null && args.length >= 2 ? StringUtils.text(args[1]) : "";
        long foreColor = args != null && args.length >= 3 ? NumberUtils.parseLong(args[2]) : 0xFFFFFFL;
        logSourceLine(messageText, sourceName, foreColor);
    }

    /**
     * Original function: Proc_2_1_6D1B60.
     */
    public static void Proc_2_1_6D1B60(Object... args) {
        String messageText = args != null && args.length >= 1 ? StringUtils.text(args[0]) : "";
        long foreColor = args != null && args.length >= 3 ? NumberUtils.parseLong(args[2]) : 0xFFFFFFL;
        appendPlainLine(messageText, foreColor);
    }

    /**
     * Original function: Proc_2_2_6D21D0.
     */
    public static void Proc_2_2_6D21D0(Object... args) {
        String messageText = args != null && args.length >= 1 ? StringUtils.text(args[0]) : "";
        String sourceName = args != null && args.length >= 2 ? StringUtils.text(args[1]) : "";
        long foreColor = args != null && args.length >= 3 ? NumberUtils.parseLong(args[2]) : 0xFFFFFFL;
        appendOptionalSourceLine(messageText, sourceName, foreColor);
    }

    /**
     * Original function: Proc_2_3_6D27D0.
     */
    public static void Proc_2_3_6D27D0(Object... args) {
        if (args == null || args.length == 0) {
            return;
        }
        sleepSeconds(args[0]);
    }

    /**
     * Original function: Proc_2_4_6D28B0.
     */
    public static String Proc_2_4_6D28B0(Object... args) {
        long value = args != null && args.length >= 1 ? NumberUtils.parseLong(args[0]) : 0L;
        return encodeBase64Length(value);
    }

    /**
     * Original function: Proc_2_0_6D1510.
     */
    public static void logSourceLine(String messageText, String sourceName, long foreColor) {
        appendConsoleLine(formatConsoleLine(messageText, sourceName, true), foreColor);
    }

    /**
     * Original function: Proc_2_1_6D1B60.
     */
    public static void appendPlainLine(String messageText, long foreColor) {
        appendConsoleLine(formatConsoleLine(messageText, "", false), foreColor);
    }

    /**
     * Original function: Proc_2_2_6D21D0.
     */
    public static void appendOptionalSourceLine(String messageText, String sourceName, long foreColor) {
        appendConsoleLine(formatConsoleLine(messageText, sourceName, false), foreColor);
    }

    /**
     * Original function: Proc_2_3_6D27D0.
     */
    public static void sleepSeconds(Object secondsValue) {
        long delayMillis = delayMilliseconds(secondsValue);
        if (delayMillis <= 0L) {
            return;
        }
        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Original function: Proc_2_4_6D28B0.
     */
    public static String encodeBase64Length(long value) {
        if (value < 0L) {
            value = 0L;
        }
        long highValue = value / 0x40L;
        long lowValue = value % 0x40L;
        return Character.toString((char) (highValue + 64L)) + (char) (lowValue + 64L);
    }

    public static float elapsedSeconds(float startTime, float endTime) {
        if (endTime < startTime) {
            return (86400.0f - startTime) + endTime;
        }
        return endTime - startTime;
    }

    public static long delayMilliseconds(Object secondsValue) {
        String text = StringUtils.text(secondsValue).trim().replace(',', '.');
        if (text.isEmpty()) {
            return 0L;
        }
        try {
            double seconds = Double.parseDouble(text);
            if (seconds <= 0.0d) {
                return 0L;
            }
            return Math.max(1L, Math.round(seconds * 1000.0d));
        } catch (NumberFormatException ex) {
            return 0L;
        }
    }

    public static void appendConsoleLine(String lineText, long foreColor) {
        entries.add(new Entry(lineText, foreColor));
        System.out.println(lineText);
    }

    public static String formatConsoleLine(String messageText, String sourceName, boolean alwaysPrefix) {
        String message = StringUtils.text(messageText);
        String source = StringUtils.text(sourceName);
        if (alwaysPrefix || (!source.isEmpty() && !"HIDDEN".equalsIgnoreCase(source))) {
            return "[" + source + "] " + message;
        }
        return message;
    }

    public static List<Entry> entries() {
        return Collections.unmodifiableList(entries);
    }

    public static void clear() {
        entries.clear();
    }

    public static final class Entry {
        private final String lineText;
        private final long foreColor;

        private Entry(String lineText, long foreColor) {
            this.lineText = lineText;
            this.foreColor = foreColor;
        }

        public String lineText() {
            return lineText;
        }

        public long foreColor() {
            return foreColor;
        }
    }
}
