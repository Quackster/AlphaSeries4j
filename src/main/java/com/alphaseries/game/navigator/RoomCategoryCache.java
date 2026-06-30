package com.alphaseries.game.navigator;

import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class RoomCategoryCache {
    private final List<String> defaultCategoryIds;
    private final List<RoomDao.RoomCategoryRow> categoryRows;
    private final List<CategoryPayload> payloads;

    private RoomCategoryCache(Object defaultCategoryIds, Object categoryRows, Object payloads) {
        this.defaultCategoryIds = parseDefaultCategoryIds(defaultCategoryIds);
        this.categoryRows = parseCategoryRows(categoryRows);
        this.payloads = parsePayloads(payloads);
    }

    private RoomCategoryCache(Object defaultCategoryIds, List<RoomDao.RoomCategoryRow> categoryRows,
                              List<CategoryPayload> payloads) {
        this.defaultCategoryIds = parseDefaultCategoryIds(defaultCategoryIds);
        this.categoryRows = copyCategoryRows(categoryRows);
        this.payloads = copyPayloadRecords(payloads);
    }

    public static RoomCategoryCache fromLegacy(Object defaultCategoryIds, Object categoryRows, Object payloads) {
        if (defaultCategoryIds instanceof RoomCategoryCache roomCategoryCache) {
            return roomCategoryCache;
        }
        return new RoomCategoryCache(defaultCategoryIds, categoryRows, payloads);
    }

    public static RoomCategoryCache empty() {
        return new RoomCategoryCache("", "", "");
    }

    public static RoomCategoryCache fromRows(Object defaultCategoryIds, List<RoomDao.RoomCategoryRow> categoryRows,
                                             String[][] payloads) {
        return new RoomCategoryCache(defaultCategoryIds, categoryRows, payloadRecords(payloads));
    }

    public static RoomCategoryCache fromPayloadRows(Object defaultCategoryIds,
            List<RoomDao.RoomCategoryRow> categoryRows, List<CategoryPayload> payloads) {
        return new RoomCategoryCache(defaultCategoryIds, categoryRows, payloads);
    }

    public String[] defaultCategoryIds() {
        return defaultCategoryIds.toArray(String[]::new);
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

    public String categoryRows() {
        if (!categoryRows.isEmpty()) {
            StringBuilder joined = new StringBuilder();
            for (RoomDao.RoomCategoryRow row : categoryRows) {
                appendRow(joined, row.categoryId() + "\t" + StringUtils.text(row.name()) + "\t"
                    + row.trading() + "\t" + row.minimumRank() + "\t" + row.minimumHcRank());
            }
            return joined.toString();
        }
        return "";
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

    public String[][] payloads() {
        return payloadsAsArray(payloads);
    }

    public List<CategoryPayload> payloadRows() {
        return List.copyOf(payloads);
    }

    public record CategoryPayload(long rankIndex, long hcLevel, String payload) {
        public CategoryPayload {
            payload = StringUtils.text(payload);
        }
    }

    private static List<RoomDao.RoomCategoryRow> parseCategoryRows(Object categoryRows) {
        if (categoryRows instanceof List<?> rows) {
            List<RoomDao.RoomCategoryRow> parsedRows = new ArrayList<>();
            for (Object value : rows) {
                if (value instanceof RoomDao.RoomCategoryRow row) {
                    parsedRows.add(row);
                }
            }
            return List.copyOf(parsedRows);
        }
        List<RoomDao.RoomCategoryRow> parsedRows = new ArrayList<>();
        for (String row : StringUtils.text(categoryRows).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 5) {
                    parsedRows.add(new RoomDao.RoomCategoryRow(
                        NumberUtils.parseLong(StringUtils.field(fields, 0)),
                        StringUtils.field(fields, 1),
                        NumberUtils.parseLong(StringUtils.field(fields, 2)),
                        NumberUtils.parseLong(StringUtils.field(fields, 3)),
                        NumberUtils.parseLong(StringUtils.field(fields, 4))));
                }
            }
        }
        if (!parsedRows.isEmpty()) {
            return List.copyOf(parsedRows);
        }
        return List.of();
    }

    private static List<CategoryPayload> parsePayloads(Object payloads) {
        if (payloads instanceof List<?> values) {
            List<CategoryPayload> parsedPayloads = new ArrayList<>();
            for (Object value : values) {
                if (value instanceof CategoryPayload payload) {
                    parsedPayloads.add(payload);
                }
            }
            return List.copyOf(parsedPayloads);
        }
        if (payloads instanceof String[][] values) {
            return payloadRecords(values);
        }
        if (payloads instanceof Object[][] values) {
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
            return payloadRecords(parsedPayloads);
        }
        return List.of();
    }

    private static List<String> parseDefaultCategoryIds(Object defaultCategoryIds) {
        if (defaultCategoryIds instanceof String[] values) {
            return copyTexts(values);
        }
        if (defaultCategoryIds instanceof Object[] values) {
            List<String> parsedValues = new ArrayList<>();
            for (int index = 0; index < values.length; index++) {
                parsedValues.add(StringUtils.text(values[index]));
            }
            return List.copyOf(parsedValues);
        }
        String text = StringUtils.text(defaultCategoryIds);
        if (text.isEmpty()) {
            return List.of();
        }
        List<String> parsedValues = new ArrayList<>();
        for (String value : text.split("\t", -1)) {
            parsedValues.add(StringUtils.text(value));
        }
        return List.copyOf(parsedValues);
    }

    private static List<RoomDao.RoomCategoryRow> copyCategoryRows(List<RoomDao.RoomCategoryRow> rows) {
        return rows == null ? List.of() : List.copyOf(rows);
    }

    private static List<String> copyTexts(String[] values) {
        List<String> copiedValues = new ArrayList<>();
        for (int index = 0; index < values.length; index++) {
            copiedValues.add(StringUtils.text(values[index]));
        }
        return List.copyOf(copiedValues);
    }

    private static List<CategoryPayload> copyPayloadRecords(List<CategoryPayload> payloads) {
        return payloads == null ? List.of() : List.copyOf(payloads);
    }

    private static List<CategoryPayload> payloadRecords(String[][] payloads) {
        if (payloads == null) {
            return List.of();
        }
        List<CategoryPayload> records = new ArrayList<>();
        for (int rank = 0; rank < payloads.length; rank++) {
            if (payloads[rank] == null) {
                continue;
            }
            for (int hc = 0; hc < payloads[rank].length; hc++) {
                records.add(new CategoryPayload(rank, hc, payloads[rank][hc]));
            }
        }
        return List.copyOf(records);
    }

    private static String[][] payloadsAsArray(List<CategoryPayload> payloads) {
        long maxRank = -1L;
        long maxHc = -1L;
        for (CategoryPayload payload : payloads == null ? List.<CategoryPayload>of() : payloads) {
            maxRank = Math.max(maxRank, payload.rankIndex());
            maxHc = Math.max(maxHc, payload.hcLevel());
        }
        if (maxRank < 0L || maxHc < 0L) {
            return new String[0][];
        }
        String[][] values = new String[(int) maxRank + 1][(int) maxHc + 1];
        for (CategoryPayload payload : payloads) {
            if (payload.rankIndex() >= 0L && payload.hcLevel() >= 0L) {
                values[(int) payload.rankIndex()][(int) payload.hcLevel()] = payload.payload();
            }
        }
        return values;
    }

    private static void appendRow(StringBuilder rows, String rowText) {
        if (rows.length() > 0) {
            rows.append('\r');
        }
        rows.append(rowText);
    }

    private String defaultCategoryId(int index) {
        return index >= 0 && index < defaultCategoryIds.size() ? StringUtils.text(defaultCategoryIds.get(index)) : "";
    }
}
