package com.alphaseries.util;

import java.util.ArrayList;
import java.util.List;

public final class StringUtils {
    private StringUtils() {
    }

    public static String text(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    public static String field(List<String> fields, int index) {
        return fields != null && index >= 0 && index < fields.size() ? text(fields.get(index)) : "";
    }

    public static String withoutPrefix(Object value, String prefix) {
        String text = text(value);
        String prefixText = text(prefix);
        return !prefixText.isEmpty() && text.startsWith(prefixText) ? text.substring(prefixText.length()) : text;
    }

    public static String withoutAnyPrefix(Object value, String... prefixes) {
        String text = text(value);
        if (prefixes == null) {
            return text;
        }
        for (String prefix : prefixes) {
            String prefixText = text(prefix);
            if (!prefixText.isEmpty() && text.startsWith(prefixText)) {
                return text.substring(prefixText.length());
            }
        }
        return text;
    }

    public static String withoutPacketCode(Object value) {
        String text = text(value);
        return text.length() >= 3 ? text.substring(2) : text;
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

    public static List<String> delimitedFields(Object value, String delimiter) {
        String text = text(value);
        String marker = text(delimiter);
        if (marker.isEmpty()) {
            return List.of(text);
        }
        List<String> fields = new ArrayList<>();
        int fieldStart = 0;
        int delimiterAt = text.indexOf(marker);
        while (delimiterAt >= 0) {
            fields.add(text.substring(fieldStart, delimiterAt));
            fieldStart = delimiterAt + marker.length();
            delimiterAt = text.indexOf(marker, fieldStart);
        }
        fields.add(text.substring(fieldStart));
        return List.copyOf(fields);
    }

    public static IndexedFields indexedFields(Object value, char delimiter) {
        return new IndexedFields(delimitedFields(value, delimiter));
    }

    public static SequentialFields sequentialFields(Object value, char... delimiters) {
        String remaining = text(value);
        List<String> fields = new ArrayList<>();
        int delimitersFound = 0;
        if (delimiters == null) {
            return new SequentialFields(List.of(remaining), "", 0);
        }
        for (char delimiter : delimiters) {
            int delimiterAt = remaining.indexOf(delimiter);
            if (delimiterAt < 0) {
                fields.add(remaining);
                return new SequentialFields(fields, "", delimitersFound);
            }
            fields.add(remaining.substring(0, delimiterAt));
            remaining = remaining.substring(delimiterAt + 1);
            delimitersFound++;
        }
        return new SequentialFields(fields, remaining, delimitersFound);
    }

    public record IndexedFields(List<String> values) {
        public IndexedFields {
            values = values == null ? List.of() : List.copyOf(values);
        }

        public String text(int index) {
            return field(values, index);
        }

        public long number(int index) {
            return NumberUtils.parseLong(text(index));
        }

        public int fieldCount() {
            return values.size();
        }

        public List<String> fieldsFrom(int firstIndex) {
            if (firstIndex >= values.size()) {
                return List.of();
            }
            return values.subList(Math.max(0, firstIndex), values.size());
        }
    }

    public record SequentialFields(List<String> values, String rest, int delimitersFound) {
        public SequentialFields {
            values = values == null ? List.of() : List.copyOf(values);
            rest = StringUtils.text(rest);
            delimitersFound = Math.max(0, delimitersFound);
        }

        public String text(int index) {
            return field(values, index);
        }

        public boolean foundDelimiter(int index) {
            return index >= 0 && delimitersFound > index;
        }
    }

    public static String delimitedText(List<?> fields, char delimiter) {
        if (fields == null || fields.isEmpty()) {
            return "";
        }
        StringBuilder text = new StringBuilder();
        for (Object field : fields) {
            if (text.length() > 0) {
                text.append(delimiter);
            }
            text.append(text(field));
        }
        return text.toString();
    }

    public static List<String> spaceSeparatedWords(Object value) {
        List<String> words = new ArrayList<>();
        for (String field : delimitedFields(value, ' ')) {
            String word = field.trim();
            if (!word.isEmpty()) {
                words.add(word);
            }
        }
        return List.copyOf(words);
    }

    public static List<Long> positiveLongFields(Object value, char delimiter) {
        List<Long> values = new ArrayList<>();
        for (String field : delimitedFields(value, delimiter)) {
            long parsedValue = NumberUtils.parseLong(field);
            if (parsedValue > 0L) {
                values.add(parsedValue);
            }
        }
        return List.copyOf(values);
    }

    public static boolean isUnsignedLongFieldList(Object value, char delimiter) {
        String text = text(value);
        if (text.isEmpty()) {
            return false;
        }
        for (String field : delimitedFields(text, delimiter)) {
            if (field.isEmpty()) {
                return false;
            }
            for (int index = 0; index < field.length(); index++) {
                if (!Character.isDigit(field.charAt(index))) {
                    return false;
                }
            }
        }
        return true;
    }

    public static List<Long> allPositiveLongFields(Object value, char delimiter) {
        if (!isUnsignedLongFieldList(value, delimiter)) {
            return List.of();
        }
        List<Long> values = new ArrayList<>();
        for (String field : delimitedFields(value, delimiter)) {
            long parsedValue = NumberUtils.parseLong(field);
            if (parsedValue <= 0L) {
                return List.of();
            }
            values.add(parsedValue);
        }
        return List.copyOf(values);
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

    public static String normalizedNewlines(Object value) {
        return text(value).replace("\r\n", "\n").replace('\r', '\n');
    }

    public static String withoutCarriageReturns(Object value) {
        return text(value).replace("\r", "");
    }

    public static String newlinesAsCarriageReturns(Object value) {
        return text(value).replace('\n', '\r');
    }

    public static String collapsedCarriageReturnRows(Object value) {
        String text = newlinesAsCarriageReturns(value);
        while (text.contains("\r\r")) {
            text = text.replace("\r\r", "\r");
        }
        return text;
    }

    public static String compactPacketWhitespace(Object value) {
        String text = text(value)
            .replace('\1', ' ')
            .replace('\2', ' ')
            .replace('\t', ' ')
            .replace('\r', ' ')
            .replace('\n', ' ');
        while (text.contains("  ")) {
            text = text.replace("  ", " ");
        }
        return text.trim();
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
        cache = withoutCarriageReturns(cache);
        StringBuilder rebuilt = new StringBuilder();
        for (String rowText : delimitedFields(cache, '\n')) {
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
