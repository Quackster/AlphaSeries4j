package com.alphaseries.game.user;

import com.alphaseries.util.StringUtils;

public record UserEffectActivation(long effectId, long rentSeconds, String payload, String broadcastPayload) {
    public UserEffectActivation {
        payload = StringUtils.text(payload);
        broadcastPayload = StringUtils.text(broadcastPayload);
    }

    public static UserEffectActivation empty() {
        return new UserEffectActivation(0L, 0L, "", "");
    }

    public boolean valid() {
        return effectId > 0L && rentSeconds > 0L && !payload.isEmpty();
    }
}
