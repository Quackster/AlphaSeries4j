package com.alphaseries.game.moderation;

import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class StaffSettings {
    private final List<ModerationPayload> moderationPayloads;

    private StaffSettings(Object moderationPayloads) {
        this.moderationPayloads = parseModerationPayloads(moderationPayloads);
    }

    private StaffSettings(List<ModerationPayload> moderationPayloads) {
        this.moderationPayloads = copyPayloadRows(moderationPayloads);
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
        return new StaffSettings(payloadRows(moderationPayloads));
    }

    public static StaffSettings fromPayloadRows(List<ModerationPayload> moderationPayloads) {
        return new StaffSettings(moderationPayloads);
    }

    public String[][] moderationPayloads() {
        return payloadsAsArray(moderationPayloads);
    }

    public List<ModerationPayload> moderationPayloadRows() {
        return List.copyOf(moderationPayloads);
    }

    public String moderationPayload(long rankIndex, long hcLevel) {
        int rank = (int) Math.max(0L, Math.min(rankIndex, 20L));
        int hc = (int) Math.max(0L, Math.min(hcLevel, 2L));
        for (ModerationPayload payload : moderationPayloads) {
            if (payload.rankIndex() == rank && payload.hcLevel() == hc) {
                return payload.payload();
            }
        }
        return "";
    }

    public record ModerationPayload(long rankIndex, long hcLevel, String payload) {
        public ModerationPayload {
            payload = StringUtils.text(payload);
        }
    }

    private static List<ModerationPayload> parseModerationPayloads(Object moderationPayloads) {
        if (moderationPayloads instanceof List<?> values) {
            List<ModerationPayload> parsedPayloads = new ArrayList<>();
            for (Object value : values) {
                if (value instanceof ModerationPayload payload) {
                    parsedPayloads.add(payload);
                }
            }
            return List.copyOf(parsedPayloads);
        }
        if (moderationPayloads instanceof String[][] values) {
            return payloadRows(values);
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
            return payloadRows(parsedPayloads);
        }
        return List.of();
    }

    private static List<ModerationPayload> copyPayloadRows(List<ModerationPayload> moderationPayloads) {
        return moderationPayloads == null ? List.of() : List.copyOf(moderationPayloads);
    }

    private static List<ModerationPayload> payloadRows(String[][] moderationPayloads) {
        if (moderationPayloads == null) {
            return List.of();
        }
        List<ModerationPayload> records = new ArrayList<>();
        for (int rank = 0; rank < moderationPayloads.length; rank++) {
            if (moderationPayloads[rank] == null) {
                continue;
            }
            for (int hc = 0; hc < moderationPayloads[rank].length; hc++) {
                records.add(new ModerationPayload(rank, hc, moderationPayloads[rank][hc]));
            }
        }
        return List.copyOf(records);
    }

    private static String[][] payloadsAsArray(List<ModerationPayload> moderationPayloads) {
        long maxRank = -1L;
        long maxHc = -1L;
        for (ModerationPayload payload : moderationPayloads == null ? List.<ModerationPayload>of() : moderationPayloads) {
            maxRank = Math.max(maxRank, payload.rankIndex());
            maxHc = Math.max(maxHc, payload.hcLevel());
        }
        if (maxRank < 0L || maxHc < 0L) {
            return new String[0][];
        }
        String[][] values = new String[(int) maxRank + 1][(int) maxHc + 1];
        for (ModerationPayload payload : moderationPayloads) {
            if (payload.rankIndex() >= 0L && payload.hcLevel() >= 0L) {
                values[(int) payload.rankIndex()][(int) payload.hcLevel()] = payload.payload();
            }
        }
        return values;
    }
}
