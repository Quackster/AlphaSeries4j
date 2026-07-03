package com.alphaseries.game.moderation;

import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.StringUtils;

import java.util.List;
import java.util.Objects;

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

    void appendModerationPayloadTo(PacketBuilder packet, long rankIndex, long hcLevel) {
        packet.appendRaw(moderationPayload(rankIndex, hcLevel).payload());
    }

    private ModerationPayloadValue moderationPayload(long rankIndex, long hcLevel) {
        int rank = (int) Math.max(0L, Math.min(rankIndex, 20L));
        int hc = (int) Math.max(0L, Math.min(hcLevel, 2L));
        for (ModerationPayload payload : moderationPayloads) {
            if (payload.rankIndex() == rank && payload.hcLevel() == hc) {
                return payload.payloadValue();
            }
        }
        return ModerationPayloadValue.empty();
    }

    public static final class ModerationPayload {
        private final long rankIndex;
        private final long hcLevel;
        private final ModerationPayloadValue payload;

        private ModerationPayload(long rankIndex, long hcLevel, ModerationPayloadValue payload) {
            this.rankIndex = rankIndex;
            this.hcLevel = hcLevel;
            this.payload = payload == null ? ModerationPayloadValue.empty() : payload;
        }

        static ModerationPayload fromPayloadText(long rankIndex, long hcLevel, String payload) {
            return new ModerationPayload(rankIndex, hcLevel, new ModerationPayloadValue(payload));
        }

        public static ModerationPayload fromEntries(long rankIndex, long hcLevel, Iterable<ModerationEntry> entries) {
            PacketBuilder payload = PacketBuilder.create();
            if (entries != null) {
                for (ModerationEntry entry : entries) {
                    if (entry != null && !StringUtils.text(entry.permissionName()).isEmpty()) {
                        payload.appendString(entry.permissionName()).appendRaw(entry.content());
                    }
                }
            }
            return new ModerationPayload(rankIndex, hcLevel, new ModerationPayloadValue(payload.build()));
        }

        public long rankIndex() {
            return rankIndex;
        }

        public long hcLevel() {
            return hcLevel;
        }

        private ModerationPayloadValue payloadValue() {
            return payload;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ModerationPayload payloadRecord)) {
                return false;
            }
            return rankIndex == payloadRecord.rankIndex
                && hcLevel == payloadRecord.hcLevel
                && payload.equals(payloadRecord.payload);
        }

        @Override
        public int hashCode() {
            return Objects.hash(rankIndex, hcLevel, payload);
        }

        @Override
        public String toString() {
            return "ModerationPayload[rankIndex=" + rankIndex
                + ", hcLevel=" + hcLevel
                + ", payload=" + payload + "]";
        }
    }

    public record ModerationEntry(String permissionName, String content) {
        public ModerationEntry {
            permissionName = StringUtils.text(permissionName);
            content = StringUtils.text(content);
        }
    }

    private record ModerationPayloadValue(String payload) {
        private ModerationPayloadValue {
            payload = StringUtils.text(payload);
        }

        private static ModerationPayloadValue empty() {
            return new ModerationPayloadValue("");
        }
    }

    private static List<ModerationPayload> copyPayloadRows(List<ModerationPayload> moderationPayloads) {
        return moderationPayloads == null ? List.of() : List.copyOf(moderationPayloads);
    }

}
