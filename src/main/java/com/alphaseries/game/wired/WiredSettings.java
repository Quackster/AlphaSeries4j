package com.alphaseries.game.wired;

import com.alphaseries.util.StringUtils;

public final class WiredSettings {
    private final String statePayload;

    private WiredSettings(String statePayload) {
        this.statePayload = StringUtils.text(statePayload);
    }

    public static WiredSettings fromLegacy(String statePayload) {
        return new WiredSettings(statePayload);
    }

    public String statePayload() {
        return statePayload;
    }
}
