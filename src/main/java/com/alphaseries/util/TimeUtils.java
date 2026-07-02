package com.alphaseries.util;

public final class TimeUtils {
    private TimeUtils() {
    }

    public static void sleepSeconds(Object secondsValue) {
        long delayMillis = delayMilliseconds(secondsValue);
        if (delayMillis <= 0L) {
            return;
        }
        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public static float elapsedSeconds(float startTime, float endTime) {
        if (endTime < startTime) {
            return (86400.0f - startTime) + endTime;
        }
        return endTime - startTime;
    }

    public static long delayMilliseconds(Object secondsValue) {
        String text = StringUtils.text(secondsValue).trim().replace(',', '.');
        if (text.isEmpty()) {
            return 0L;
        }
        try {
            double seconds = Double.parseDouble(text);
            if (seconds <= 0.0d) {
                return 0L;
            }
            return Math.max(1L, Math.round(seconds * 1000.0d));
        } catch (NumberFormatException ex) {
            return 0L;
        }
    }
}
