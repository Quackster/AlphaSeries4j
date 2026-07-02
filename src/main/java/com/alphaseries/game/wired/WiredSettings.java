package com.alphaseries.game.wired;

import com.alphaseries.util.StringUtils;

public final class WiredSettings {
    private final String statePayload;

    private WiredSettings(String statePayload) {
        this.statePayload = StringUtils.text(statePayload);
    }

    public static WiredSettings fromStatePayload(String statePayload) {
        return new WiredSettings(statePayload);
    }

    public static WiredSettings empty() {
        return new WiredSettings("");
    }

    public String statePayload() {
        return statePayload;
    }
}
