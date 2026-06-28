package com.alphaseries.vb;

import java.util.Locale;

public final class Vb {
    public static final char CR = '\r';
    public static final char LF = '\n';
    public static final char NUL = '\0';
    public static final char SOH = '\1';
    public static final char STX = '\2';
    public static final char TAB = '\t';

    private Vb() {
    }

    public static String cStr(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    public static long val(Object value) {
        String text = cStr(value).trim();
        if (text.isEmpty()) {
            return 0L;
        }

        int index = 0;
        boolean seenDigit = false;
        StringBuilder numeric = new StringBuilder();
        if (text.charAt(0) == '+' || text.charAt(0) == '-') {
            numeric.append(text.charAt(0));
            index++;
        }

        for (; index < text.length(); index++) {
            char ch = text.charAt(index);
            if (Character.isDigit(ch)) {
                seenDigit = true;
                numeric.append(ch);
            } else if (ch == '.' || ch == ',') {
                break;
            } else if (Character.isWhitespace(ch)) {
                break;
            } else {
                break;
            }
        }

        if (!seenDigit) {
            return 0L;
        }
        return Long.parseLong(numeric.toString());
    }

    public static int cint(Object value) {
        return (int) val(value);
    }

    public static String lcase(String value) {
        return cStr(value).toLowerCase(Locale.ROOT);
    }

    public static String mid(String value, int startOneBased) {
        String text = cStr(value);
        int start = Math.max(0, startOneBased - 1);
        if (start >= text.length()) {
            return "";
        }
        return text.substring(start);
    }

    public static String mid(String value, int startOneBased, int length) {
        String text = cStr(value);
        int start = Math.max(0, startOneBased - 1);
        if (start >= text.length() || length <= 0) {
            return "";
        }
        int end = Math.min(text.length(), start + length);
        return text.substring(start, end);
    }

    public static String left(String value, int length) {
        String text = cStr(value);
        if (length <= 0) {
            return "";
        }
        return text.substring(0, Math.min(length, text.length()));
    }

    public static String chr(int value) {
        return Character.toString((char) value);
    }

    public static int asc(String value) {
        if (value == null || value.isEmpty()) {
            return 0;
        }
        return value.charAt(0);
    }

    public static Object arg(Object[] args, int index, Object defaultValue) {
        if (args == null || index < 0 || index >= args.length || args[index] == null) {
            return defaultValue;
        }
        return args[index];
    }
}
