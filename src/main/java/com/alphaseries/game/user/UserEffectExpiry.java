package com.alphaseries.game.user;

import com.alphaseries.util.StringUtils;

public record UserEffectExpiry(long socketIndex, long effectId, String payload, String broadcastPayload) {
    public UserEffectExpiry {
        payload = StringUtils.text(payload);
        broadcastPayload = StringUtils.text(broadcastPayload);
    }

    public boolean valid() {
        return socketIndex > 0L && effectId > 0L && !payload.isEmpty();
    }
}
