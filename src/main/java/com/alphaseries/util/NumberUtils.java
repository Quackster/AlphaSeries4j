package com.alphaseries.util;

import com.alphaseries.protocol.WireEncoding;

public final class NumberUtils {
    private NumberUtils() {
    }

    public static long parseLong(Object value) {
        return WireEncoding.parseLeadingLong(value);
    }

    public static int parseInt(Object value) {
        return (int) parseLong(value);
    }
}
