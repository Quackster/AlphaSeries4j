package com.alphaseries.game.messenger;

import java.util.Arrays;

import com.alphaseries.util.NumberUtils;

public final class MessengerSettings {
    private final long[] friendLimits;

    private MessengerSettings(Object friendLimits) {
        this.friendLimits = parseFriendLimits(friendLimits);
    }

    private MessengerSettings(long[] friendLimits) {
        this.friendLimits = friendLimits == null ? new long[0] : Arrays.copyOf(friendLimits, friendLimits.length);
    }

    public static MessengerSettings fromLegacy(Object friendLimits) {
        if (friendLimits instanceof MessengerSettings messengerSettings) {
            return messengerSettings;
        }
        return new MessengerSettings(friendLimits);
    }

    public static MessengerSettings empty() {
        return new MessengerSettings("");
    }

    public static MessengerSettings fromLimits(long... friendLimits) {
        return new MessengerSettings(friendLimits);
    }

    public long[] friendLimits() {
        return Arrays.copyOf(friendLimits, friendLimits.length);
    }

    public long maxFriends(long configIndex) {
        return configIndex >= 0 && configIndex < friendLimits.length ? friendLimits[(int) configIndex] : 0L;
    }

    private static long[] parseFriendLimits(Object friendLimits) {
        if (friendLimits instanceof long[] values) {
            return Arrays.copyOf(values, values.length);
        }
        if (friendLimits instanceof String[] values) {
            long[] parsedValues = new long[values.length];
            for (int index = 0; index < values.length; index++) {
                parsedValues[index] = NumberUtils.parseLong(values[index]);
            }
            return parsedValues;
        }
        return new long[0];
    }
}
