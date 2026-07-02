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

    public static String licenceToken(String sourceValue, long saltValue, long markerValue, String fillerCharacters) {
        String source = StringUtils.text(sourceValue);
        long salt = saltValue == 0L ? 1L : saltValue;
        String fillers = StringUtils.text(fillerCharacters);
        StringBuilder token = new StringBuilder();
        token.append(source.length() + salt).append((char) markerValue);
        for (int index = 0; index < source.length(); index++) {
            char filler = index < fillers.length() ? fillers.charAt(index) : (char) markerValue;
            token.append(filler);
            token.append(source.charAt(index) * salt * markerValue);
        }
        return token.toString();
    }

    public static String shiftedLicenceText(Object encodedValue) {
        String encodedText = StringUtils.text(encodedValue);
        if (encodedText.isEmpty()) {
            return "";
        }

        int shiftValue = encodedText.charAt(0) - 87;
        StringBuilder decoded = new StringBuilder();
        for (int index = 1; index < encodedText.length(); index++) {
            decoded.append((char) (encodedText.charAt(index) - shiftValue));
        }
        return decoded.toString();
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
