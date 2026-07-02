package com.alphaseries.util;

public final class IdentityEncoding {
    private IdentityEncoding() {
    }

    public static String shift(String sourceText, long shiftAmount) {
        String source = StringUtils.text(sourceText);
        StringBuilder output = new StringBuilder(source.length());
        for (int index = 0; index < source.length(); index++) {
            output.append((char) ((source.charAt(index) + shiftAmount) & 0xFF));
        }
        return output.toString();
    }

    public static String easyDecode(Object value) {
        return shift(StringUtils.text(value), -25L);
    }

    public static String superEasyEncode(Object value) {
        return shift(StringUtils.text(value), 2L);
    }

    public static String superEasyDecode(Object value) {
        return shift(StringUtils.text(value), -2L);
    }

    public static String premiumDecode(Object valueOffset, Object encodedValue) {
        String encodedText = StringUtils.text(encodedValue);
        if (encodedText.isEmpty()) {
            return "";
        }
        long seedValue = encodedText.charAt(0);
        long offset = NumberUtils.parseLong(valueOffset);
        StringBuilder output = new StringBuilder(encodedText.length() - 1);
        for (int index = 1; index < encodedText.length(); index++) {
            output.append((char) (((encodedText.charAt(index) - seedValue) + offset) & 0xFF));
        }
        return output.toString();
    }

    public static String decode(Object encodedValue, Object seedOffset) {
        String encodedText = StringUtils.text(encodedValue);
        if (encodedText.isEmpty()) {
            return "";
        }
        long seedValue = encodedText.charAt(0) - NumberUtils.parseLong(seedOffset);
        StringBuilder output = new StringBuilder(encodedText.length() - 1);
        for (int index = 1; index < encodedText.length(); index++) {
            output.append((char) (((encodedText.charAt(index) - index) - seedValue) & 0xFF));
        }
        return output.toString();
    }

    public static String encode(String sourceText) {
        try {
            long seedValue = RandomUtils.longInclusive(0x41, 0x5A);
            StringBuilder output = new StringBuilder(sourceText.length() + 1);
            output.append((char) (seedValue & 0xFF));
            for (int index = 0; index < sourceText.length(); index++) {
                output.append((char) ((sourceText.charAt(index) + index + 1L + seedValue) & 0xFF));
            }
            return output.toString();
        } catch (Exception ex) {
            return "";
        }
    }
}
