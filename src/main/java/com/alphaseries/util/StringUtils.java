package com.alphaseries.util;

public final class StringUtils {
    private StringUtils() {
    }

    public static String text(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    public static String field(String[] fields, int index) {
        return fields != null && index >= 0 && index < fields.length ? text(fields[index]) : "";
    }

    public static String left(Object value, int maxLength) {
        String text = text(value);
        if (maxLength <= 0) {
            return "";
        }
        return text.length() <= maxLength ? text : text.substring(0, maxLength);
    }

    public static String mid(Object value, int oneBasedStart) {
        String text = text(value);
        int start = Math.max(0, oneBasedStart - 1);
        return start >= text.length() ? "" : text.substring(start);
    }

    public static String mid(Object value, int oneBasedStart, int maxLength) {
        String text = text(value);
        if (maxLength <= 0) {
            return "";
        }
        int start = Math.max(0, oneBasedStart - 1);
        if (start >= text.length()) {
            return "";
        }
        int end = Math.min(text.length(), start + maxLength);
        return text.substring(start, end);
    }
}
