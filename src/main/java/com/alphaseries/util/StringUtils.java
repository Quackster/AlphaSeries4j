package com.alphaseries.util;

import java.util.ArrayList;
import java.util.List;

public final class StringUtils {
    private StringUtils() {
    }

    public static String text(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    public static String field(String[] fields, int index) {
        return fields != null && index >= 0 && index < fields.length ? text(fields[index]) : "";
    }

    public static String field(List<String> fields, int index) {
        return fields != null && index >= 0 && index < fields.size() ? text(fields.get(index)) : "";
    }

    public static String delimitedField(Object value, char delimiter, int index) {
        String text = text(value);
        if (index < 0) {
            return "";
        }
        int fieldStart = 0;
        int currentIndex = 0;
        while (currentIndex < index) {
            int delimiterAt = text.indexOf(delimiter, fieldStart);
            if (delimiterAt < 0) {
                return "";
            }
            fieldStart = delimiterAt + 1;
            currentIndex++;
        }
        int fieldEnd = text.indexOf(delimiter, fieldStart);
        return fieldEnd < 0 ? text.substring(fieldStart) : text.substring(fieldStart, fieldEnd);
    }

    public static List<String> delimitedFields(Object value, char delimiter) {
        String text = text(value);
        List<String> fields = new ArrayList<>();
        int fieldStart = 0;
        int delimiterAt = text.indexOf(delimiter);
        while (delimiterAt >= 0) {
            fields.add(text.substring(fieldStart, delimiterAt));
            fieldStart = delimiterAt + 1;
            delimiterAt = text.indexOf(delimiter, fieldStart);
        }
        fields.add(text.substring(fieldStart));
        return List.copyOf(fields);
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

    /**
     * Original function: Proc_10_5_809D80.
     */
    public static String middleText(Object value, int startAt, int fieldLength) {
        int oneBasedStart = Math.max(1, startAt);
        if (fieldLength > 0) {
            return mid(value, oneBasedStart, fieldLength);
        }
        return mid(value, oneBasedStart);
    }

    /**
     * Original function: Proc_10_9_80A680.
     */
    public static String normalizeNullBytes(Object value) {
        return text(value).replace('\0', '\u00a0');
    }

    /**
     * Original function: Proc_10_10_80A7F0.
     */
    public static String singleLineText(Object value) {
        return text(value).replace('\n', ' ').replace('\r', ' ');
    }

    /**
     * Original function: Proc_10_11_80A9C0.
     */
    public static String sqlEscapedText(Object value) {
        return text(value)
            .replace("'", "''")
            .replace("\\r", " ")
            .replace("\\n", " ")
            .replace("\"", " ");
    }

    public static String removeLineRecord(String cacheText, String markerText) {
        String cache = text(cacheText);
        String marker = text(markerText);
        if (cache.isEmpty() || marker.isEmpty()) {
            return cache;
        }
        cache = cache.replace("\r", "");
        StringBuilder rebuilt = new StringBuilder();
        for (String rowText : cache.split("\n", -1)) {
            if (!rowText.isEmpty() && !rowText.contains(marker)) {
                if (rebuilt.length() > 0) {
                    rebuilt.append('\n');
                }
                rebuilt.append(rowText);
            }
        }
        return rebuilt.toString();
    }
}
