package com.alphaseries.server.logging;

import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Console {
    private static final List<Entry> entries = new ArrayList<Entry>();

    private Console() {
    }

    public static void logSourceLine(String messageText, String sourceName, long foreColor) {
        appendConsoleLine(formatConsoleLine(messageText, sourceName, true), foreColor);
    }

    public static void appendPlainLine(String messageText, long foreColor) {
        appendConsoleLine(formatConsoleLine(messageText, "", false), foreColor);
    }

    public static void appendOptionalSourceLine(String messageText, String sourceName, long foreColor) {
        appendConsoleLine(formatConsoleLine(messageText, sourceName, false), foreColor);
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
