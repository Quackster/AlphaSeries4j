package com.alphaseries.game.navigator;

import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class RoomCategoryCache {
    private final List<DefaultCategoryId> defaultCategoryIds;
    private final List<RoomDao.RoomCategoryRow> categoryRows;
    private final List<CategoryPayload> payloads;

    private RoomCategoryCache(List<DefaultCategoryId> defaultCategoryIds, List<RoomDao.RoomCategoryRow> categoryRows,
                              List<CategoryPayload> payloads) {
        this.defaultCategoryIds = copyDefaultCategoryIds(defaultCategoryIds);
        this.categoryRows = copyCategoryRows(categoryRows);
        this.payloads = copyPayloadRecords(payloads);
    }

    public static RoomCategoryCache empty() {
        return new RoomCategoryCache(List.of(), List.of(), List.of());
    }

    public static RoomCategoryCache fromPayloadRows(List<DefaultCategoryId> defaultCategoryIds,
            List<RoomDao.RoomCategoryRow> categoryRows, List<CategoryPayload> payloads) {
        return new RoomCategoryCache(defaultCategoryIds, categoryRows, payloads);
    }

    public List<DefaultCategoryId> defaultCategoryIdRows() {
        return List.copyOf(defaultCategoryIds);
    }

    public List<Long> defaultCategoryIds() {
        List<Long> values = new ArrayList<>();
        for (DefaultCategoryId categoryId : defaultCategoryIds) {
            values.add(categoryId.categoryId());
        }
        return List.copyOf(values);
    }

    public String privateDefaultCategoryId() {
        return defaultCategoryId(0);
    }

    public long privateDefaultCategoryIdValue() {
        return defaultCategoryIdValue(0);
    }

    public String publicDefaultCategoryId() {
        return defaultCategoryId(2);
    }

    public long publicDefaultCategoryIdValue() {
        return defaultCategoryIdValue(2);
    }

    public List<RoomDao.RoomCategoryRow> categoryRowList() {
        return List.copyOf(categoryRows);
    }

    String payload(long rankIndex, long hcLevel) {
        int rank = (int) rankIndex;
        int hc = (int) hcLevel;
        if (rank < 0 || hc < 0) {
            return "";
        }
        for (CategoryPayload payload : payloads) {
            if (payload.rankIndex() == rank && payload.hcLevel() == hc) {
                return payload.payload();
            }
        }
        return "";
    }

    /**
     * Original function: Proc_6_111_74DF70.
     */
    public boolean appendRankPayloadTo(PacketBuilder packet, long rankIndex, long hcLevel) {
        if (packet == null) {
            return false;
        }
        long rank = rankIndex < 0L ? 0L : Math.min(rankIndex, 20L);
        long hc = hcLevel < 0L ? 0L : Math.min(hcLevel, 2L);
        packet.appendRaw("C]").appendRaw(payload(rank, hc));
        return true;
    }

    List<CategoryPayload> payloadRows() {
        return List.copyOf(payloads);
    }

    public static final class CategoryPayload {
        private final long rankIndex;
        private final long hcLevel;
        private final String payload;

        private CategoryPayload(long rankIndex, long hcLevel, String payload) {
            this.rankIndex = rankIndex;
            this.hcLevel = hcLevel;
            this.payload = StringUtils.text(payload);
        }

        public static CategoryPayload fromCategoryRows(List<RoomDao.RoomCategoryRow> categoryRows, long rankIndex, long hcLevel) {
            long categoryCount = 0L;
            PacketBuilder categoryPayload = PacketBuilder.create();
            if (categoryRows != null) {
                for (RoomDao.RoomCategoryRow row : categoryRows) {
                    if (row != null && rankIndex >= row.minimumRank() && hcLevel >= row.minimumHcRank()) {
                        categoryPayload
                            .appendInt(row.categoryId())
                            .appendString(row.name())
                            .appendInt(row.trading());
                        categoryCount++;
                    }
                }
            }
            return fromPayloadText(rankIndex, hcLevel,
                PacketBuilder.create().appendInt(categoryCount).appendRaw(categoryPayload.build()).build());
        }

        static CategoryPayload fromPayloadText(long rankIndex, long hcLevel, String payload) {
            return new CategoryPayload(rankIndex, hcLevel, payload);
        }

        public long rankIndex() {
            return rankIndex;
        }

        public long hcLevel() {
            return hcLevel;
        }

        String payload() {
            return payload;
        }
    }

    public record DefaultCategoryId(long categoryId, boolean present) {
        public DefaultCategoryId {
            categoryId = present ? categoryId : 0L;
        }

        public static DefaultCategoryId of(long categoryId) {
            return new DefaultCategoryId(categoryId, true);
        }

        public static DefaultCategoryId empty() {
            return new DefaultCategoryId(0L, false);
        }

        private String text() {
            return present ? String.valueOf(categoryId) : "";
        }
    }

    private static List<RoomDao.RoomCategoryRow> copyCategoryRows(List<RoomDao.RoomCategoryRow> rows) {
        return rows == null ? List.of() : List.copyOf(rows);
    }

    private static List<DefaultCategoryId> copyDefaultCategoryIds(List<DefaultCategoryId> values) {
        if (values == null) {
            return List.of();
        }
        List<DefaultCategoryId> copiedValues = new ArrayList<>();
        for (DefaultCategoryId value : values) {
            copiedValues.add(value == null ? DefaultCategoryId.empty() : value);
        }
        return List.copyOf(copiedValues);
    }

    private static List<CategoryPayload> copyPayloadRecords(List<CategoryPayload> payloads) {
        return payloads == null ? List.of() : List.copyOf(payloads);
    }

    private String defaultCategoryId(int index) {
        return index >= 0 && index < defaultCategoryIds.size() ? defaultCategoryIds.get(index).text() : "";
    }

    private long defaultCategoryIdValue(int index) {
        return index >= 0 && index < defaultCategoryIds.size() ? defaultCategoryIds.get(index).categoryId() : 0L;
    }
}
