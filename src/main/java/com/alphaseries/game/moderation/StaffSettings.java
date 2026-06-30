package com.alphaseries.game.moderation;

import com.alphaseries.util.StringUtils;

public final class StaffSettings {
    private final Object moderationPayloads;

    private StaffSettings(Object moderationPayloads) {
        this.moderationPayloads = moderationPayloads == null ? "" : moderationPayloads;
    }

    public static StaffSettings fromLegacy(Object moderationPayloads) {
        if (moderationPayloads instanceof StaffSettings staffSettings) {
            return staffSettings;
        }
        return new StaffSettings(moderationPayloads);
    }

    public static StaffSettings empty() {
        return new StaffSettings("");
    }

    public String moderationPayload(long rankIndex, long hcLevel) {
        int rank = (int) Math.max(0L, Math.min(rankIndex, 20L));
        int hc = (int) Math.max(0L, Math.min(hcLevel, 2L));
        if (moderationPayloads instanceof String[][] values) {
            return rank < values.length && values[rank] != null && hc < values[rank].length
                ? StringUtils.text(values[rank][hc]) : "";
        }
        if (moderationPayloads instanceof Object[][] values) {
            return rank < values.length && values[rank] != null && hc < values[rank].length
                ? StringUtils.text(values[rank][hc]) : "";
        }
        return "";
    }
}
