package com.alphaseries.game.wired;

import com.alphaseries.util.StringUtils;

import java.util.Arrays;
import java.util.List;

public final class WiredSettings {
    private final String statePayload;

    private WiredSettings(String statePayload) {
        this.statePayload = StringUtils.text(statePayload);
    }

    static WiredSettings fromStatePayload(String statePayload) {
        return new WiredSettings(statePayload);
    }

    public static WiredSettings empty() {
        return new WiredSettings("");
    }

    public static WiredSettings fromStateRecords(Iterable<WiredPayloads.WiredRecord> records) {
        if (records == null) {
            return empty();
        }
        StringBuilder payload = new StringBuilder();
        for (WiredPayloads.WiredRecord record : records) {
            String recordText = WiredPayloads.recordText(record);
            if (!recordText.isEmpty()) {
                payload.append(recordText);
            }
        }
        return new WiredSettings(payload.toString());
    }

    public static WiredSettings fromStateRecords(WiredPayloads.WiredRecord... records) {
        return fromStateRecords(records == null ? List.of() : Arrays.asList(records));
    }

    String statePayload() {
        return statePayload;
    }
}
