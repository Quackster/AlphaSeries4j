package com.alphaseries.game.navigator;

import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class RoomCategoryCache {
    private final List<String> defaultCategoryIds;
    private final List<RoomDao.RoomCategoryRow> categoryRows;
    private final List<CategoryPayload> payloads;

    private RoomCategoryCache(List<String> defaultCategoryIds, List<RoomDao.RoomCategoryRow> categoryRows,
                              List<CategoryPayload> payloads) {
        this.defaultCategoryIds = copyTexts(defaultCategoryIds);
        this.categoryRows = copyCategoryRows(categoryRows);
        this.payloads = copyPayloadRecords(payloads);
    }

    public static RoomCategoryCache empty() {
        return new RoomCategoryCache(List.of(), List.of(), List.of());
    }

    public static RoomCategoryCache fromPayloadRows(List<String> defaultCategoryIds,
            List<RoomDao.RoomCategoryRow> categoryRows, List<CategoryPayload> payloads) {
        return new RoomCategoryCache(defaultCategoryIds, categoryRows, payloads);
    }

    public List<String> defaultCategoryIdList() {
        return List.copyOf(defaultCategoryIds);
    }

    public String privateDefaultCategoryId() {
        return defaultCategoryId(0);
    }

    public String publicDefaultCategoryId() {
        return defaultCategoryId(2);
    }

    public List<RoomDao.RoomCategoryRow> categoryRowList() {
        return List.copyOf(categoryRows);
    }

    public String payload(long rankIndex, long hcLevel) {
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
    public String rankPayload(long rankIndex, long hcLevel) {
        long rank = rankIndex < 0L ? 0L : Math.min(rankIndex, 20L);
        long hc = hcLevel < 0L ? 0L : Math.min(hcLevel, 2L);
        return "C]" + payload(rank, hc);
    }

    public List<CategoryPayload> payloadRows() {
        return List.copyOf(payloads);
    }

    public record CategoryPayload(long rankIndex, long hcLevel, String payload) {
        public CategoryPayload {
            payload = StringUtils.text(payload);
        }
    }

    private static List<RoomDao.RoomCategoryRow> copyCategoryRows(List<RoomDao.RoomCategoryRow> rows) {
        return rows == null ? List.of() : List.copyOf(rows);
    }

    private static List<String> copyTexts(List<String> values) {
        if (values == null) {
            return List.of();
        }
        List<String> copiedValues = new ArrayList<>();
        for (String value : values) {
            copiedValues.add(StringUtils.text(value));
        }
        return List.copyOf(copiedValues);
    }

    private static List<CategoryPayload> copyPayloadRecords(List<CategoryPayload> payloads) {
        return payloads == null ? List.of() : List.copyOf(payloads);
    }

    private String defaultCategoryId(int index) {
        return index >= 0 && index < defaultCategoryIds.size() ? StringUtils.text(defaultCategoryIds.get(index)) : "";
    }
}
