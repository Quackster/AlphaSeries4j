package com.alphaseries.game.moderation;

import com.alphaseries.util.StringUtils;

import java.util.List;

public final class StaffSettings {
    private final List<ModerationPayload> moderationPayloads;

    private StaffSettings(List<ModerationPayload> moderationPayloads) {
        this.moderationPayloads = copyPayloadRows(moderationPayloads);
    }

    public static StaffSettings empty() {
        return new StaffSettings(List.of());
    }

    public static StaffSettings fromPayloadRows(List<ModerationPayload> moderationPayloads) {
        return new StaffSettings(moderationPayloads);
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

    private static List<ModerationPayload> copyPayloadRows(List<ModerationPayload> moderationPayloads) {
        return moderationPayloads == null ? List.of() : List.copyOf(moderationPayloads);
    }

}
