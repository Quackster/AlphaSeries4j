package com.alphaseries.game.messenger;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public final class MessengerSettings {
    private final long[] friendLimits;

    private MessengerSettings(long[] friendLimits) {
        this.friendLimits = friendLimits == null ? new long[0] : Arrays.copyOf(friendLimits, friendLimits.length);
    }

    public static MessengerSettings empty() {
        return new MessengerSettings((long[]) null);
    }

    public static MessengerSettings fromLimits(long... friendLimits) {
        return new MessengerSettings(friendLimits);
    }

    public List<Long> friendLimitList() {
        List<Long> limits = new ArrayList<>();
        for (long friendLimit : friendLimits) {
            limits.add(friendLimit);
        }
        return List.copyOf(limits);
    }

    public long maxFriends(long configIndex) {
        return configIndex >= 0 && configIndex < friendLimits.length ? friendLimits[(int) configIndex] : 0L;
    }

}
