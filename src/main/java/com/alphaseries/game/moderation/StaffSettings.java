package com.alphaseries.game.moderation;

import com.alphaseries.util.StringUtils;

public final class StaffSettings {
    private final String[][] moderationPayloads;

    private StaffSettings(Object moderationPayloads) {
        this.moderationPayloads = parseModerationPayloads(moderationPayloads);
    }

    private StaffSettings(String[][] moderationPayloads) {
        this.moderationPayloads = copyPayloads(moderationPayloads);
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

    public static StaffSettings fromPayloads(String[][] moderationPayloads) {
        return new StaffSettings(moderationPayloads);
    }

    public String moderationPayload(long rankIndex, long hcLevel) {
        int rank = (int) Math.max(0L, Math.min(rankIndex, 20L));
        int hc = (int) Math.max(0L, Math.min(hcLevel, 2L));
        return rank < moderationPayloads.length && moderationPayloads[rank] != null && hc < moderationPayloads[rank].length
            ? StringUtils.text(moderationPayloads[rank][hc]) : "";
    }

    private static String[][] parseModerationPayloads(Object moderationPayloads) {
        if (moderationPayloads instanceof String[][] values) {
            return copyPayloads(values);
        }
        if (moderationPayloads instanceof Object[][] values) {
            String[][] parsedPayloads = new String[values.length][];
            for (int rank = 0; rank < values.length; rank++) {
                if (values[rank] == null) {
                    continue;
                }
                parsedPayloads[rank] = new String[values[rank].length];
                for (int hc = 0; hc < values[rank].length; hc++) {
                    parsedPayloads[rank][hc] = StringUtils.text(values[rank][hc]);
                }
            }
            return parsedPayloads;
        }
        return new String[0][];
    }

    private static String[][] copyPayloads(String[][] moderationPayloads) {
        if (moderationPayloads == null) {
            return new String[0][];
        }
        String[][] copiedPayloads = new String[moderationPayloads.length][];
        for (int rank = 0; rank < moderationPayloads.length; rank++) {
            if (moderationPayloads[rank] == null) {
                continue;
            }
            copiedPayloads[rank] = new String[moderationPayloads[rank].length];
            for (int hc = 0; hc < moderationPayloads[rank].length; hc++) {
                copiedPayloads[rank][hc] = StringUtils.text(moderationPayloads[rank][hc]);
            }
        }
        return copiedPayloads;
    }
}
