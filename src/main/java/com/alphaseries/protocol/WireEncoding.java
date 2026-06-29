package com.alphaseries.protocol;

public final class WireEncoding {
    private WireEncoding() {
    }

    public static String encodeVl64(long value) {
        long absoluteValue;
        long negativeFlag = 0L;
        if (value < 0) {
            negativeFlag = 4L;
            absoluteValue = Math.abs(value);
        } else {
            absoluteValue = value;
        }

        long lowBits = absoluteValue & 3L;
        absoluteValue /= 4L;

        StringBuilder encodedTail = new StringBuilder();
        long encodedLength = 0L;
        do {
            encodedTail.append((char) ((absoluteValue & 0x3FL) + 64L));
            absoluteValue /= 64L;
            encodedLength++;
        } while (absoluteValue > 0L && encodedLength < 5L);

        return Character.toString((char) (64L + (encodedLength * 8L) + negativeFlag + lowBits)) + encodedTail;
    }

    public static long decodeVl64(String encodedValue) {
        if (encodedValue == null || encodedValue.isEmpty()) {
            return 0L;
        }

        long firstByte = encodedValue.charAt(0) - 64L;
        long byteCount = (firstByte & 0x38L) / 8L;
        boolean negativeValue = (firstByte & 4L) != 0L;
        long decodedValue = firstByte & 3L;
        long multiplier = 4L;

        for (int index = 1; index <= byteCount; index++) {
            if (index + 1 > encodedValue.length()) {
                break;
            }
            decodedValue += (encodedValue.charAt(index) - 64L) * multiplier;
            multiplier *= 64L;
        }

        return negativeValue ? -decodedValue : decodedValue;
    }

    public static long vl64FieldLength(String encodedValue) {
        if (encodedValue == null || encodedValue.isEmpty()) {
            return 0L;
        }
        long firstByte = encodedValue.charAt(0) - 64L;
        long tailLength = (firstByte & 0x38L) / 8L;
        if (tailLength <= 0L) {
            return 1L;
        }
        return Math.min(encodedValue.length(), tailLength + 1L);
    }

    public static long decodeBase64Length(String encodedValue) {
        String value = encodedValue == null ? "" : encodedValue;
        if (value.length() == 1) {
            value = "@" + value;
        }
        if (value.length() < 2) {
            return 0L;
        }
        long firstValue = value.charAt(0) - 64L;
        long secondValue = value.charAt(1) - 64L;
        return (firstValue * 0x40L) + secondValue;
    }

    public static long parseLeadingLong(Object value) {
        String text = value == null ? "" : String.valueOf(value).trim();
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
            } else {
                break;
            }
        }

        if (!seenDigit) {
            return 0L;
        }
        return Long.parseLong(numeric.toString());
    }
}
