package com.alphaseries.util;

import java.util.concurrent.ThreadLocalRandom;

public final class RandomUtils {
    private RandomUtils() {
    }

    /**
     * Original function: Proc_10_2_8099D0.
     */
    public static String alphaNumericString(long requestedLength) {
        if (requestedLength > 100L) {
            requestedLength = 100L;
        }
        if (requestedLength < 0L) {
            requestedLength = 0L;
        }

        StringBuilder outputValue = new StringBuilder();
        for (long index = 1L; index <= requestedLength; index++) {
            if (longInclusive(0, 1) == 1L) {
                outputValue.append((char) longInclusive(48, 57));
            } else {
                outputValue.append((char) longInclusive(97, 122));
            }
        }
        return outputValue.toString();
    }

    /**
     * Original function: Proc_10_4_809CA0.
     */
    public static long longInclusive(long lower, long upper) {
        long min = Math.min(lower, upper);
        long max = Math.max(lower, upper);
        if (min == max) {
            return min;
        }
        return ThreadLocalRandom.current().nextLong(min, max + 1L);
    }
}
