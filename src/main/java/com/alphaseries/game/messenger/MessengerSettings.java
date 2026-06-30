package com.alphaseries.game.messenger;

import com.alphaseries.util.NumberUtils;

public final class MessengerSettings {
    private final Object friendLimits;

    private MessengerSettings(Object friendLimits) {
        this.friendLimits = friendLimits == null ? "" : friendLimits;
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

    public Object friendLimits() {
        return friendLimits;
    }

    public long maxFriends(long configIndex) {
        if (friendLimits instanceof long[] values) {
            return configIndex >= 0 && configIndex < values.length ? values[(int) configIndex] : 0L;
        }
        if (friendLimits instanceof String[] values) {
            return configIndex >= 0 && configIndex < values.length ? NumberUtils.parseLong(values[(int) configIndex]) : 0L;
        }
        return 0L;
    }
}
