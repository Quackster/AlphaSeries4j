package com.alphaseries.game.wired;

import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public final class WiredPayloads {
    private WiredPayloads() {
    }

    public record ApplyResult(long appliedCount, String statePayloads) {
        public ApplyResult {
            statePayloads = statePayloads == null ? "" : statePayloads;
        }

        public static ApplyResult empty() {
            return new ApplyResult(0L, "");
        }
    }

    public record WiredRecord(
        String code,
        String furnitureId,
        String selectedIds,
        String parameterText,
        String textValue,
        String extraValue
    ) {
        public boolean valid() {
            return !StringUtils.text(code).isEmpty() && !StringUtils.text(furnitureId).isEmpty();
        }

        public List<Long> selectedFurnitureIds() {
            return parseIdList(selectedIds);
        }
    }

    /**
     * Original function: Proc_6_218_7EA200.
     */
    public static String specialState(long itemState) {
        return itemState == 1507L ? "5;1;7;1;5;0;" : "";
    }

    public static long stateParameterValue(String parameterText) {
        return StringUtils.indexedFields(parameterText, ';').number(0);
    }

    static String recordText(
        long wiredCode,
        long furnitureId,
        List<Long> selectedFurnitureIds,
        List<Long> parameterValues,
        String textValue,
        String extraValue
    ) {
        return recordText(record(wiredCode, furnitureId, selectedFurnitureIds, parameterValues, textValue, extraValue));
    }

    static WiredRecord record(
        long wiredCode,
        long furnitureId,
        List<Long> selectedFurnitureIds,
        List<Long> parameterValues,
        String textValue,
        String extraValue
    ) {
        if (wiredCode <= 0L || furnitureId <= 0L) {
            return new WiredRecord("", "", "", "", "", "");
        }
        return new WiredRecord(
            String.valueOf(wiredCode),
            String.valueOf(furnitureId),
            joinIds(selectedFurnitureIds),
            joinIds(parameterValues),
            StringUtils.left(textValue, 125),
            StringUtils.text(extraValue));
    }

    static String recordText(
        long wiredCode,
        long furnitureId,
        String selectedIdsText,
        String parameterValues,
        String textValue,
        String extraValue
    ) {
        if (wiredCode <= 0L || furnitureId <= 0L) {
            return "";
        }
        PacketBuilder recordText = PacketBuilder.create()
            .appendRaw('\1')
            .appendRaw(wiredCode)
            .appendRaw('\2')
            .appendRaw(furnitureId)
            .appendRaw('\3')
            .appendRaw(StringUtils.text(selectedIdsText))
            .appendRaw('\4')
            .appendRaw(StringUtils.text(parameterValues))
            .appendRaw('\5')
            .appendRaw(StringUtils.left(textValue, 125))
            .appendRaw('\6');
        if (!StringUtils.text(extraValue).isEmpty()) {
            recordText.appendRaw(StringUtils.text(extraValue));
        }
        return recordText.build();
    }

    static String recordText(WiredRecord record) {
        if (record == null || !record.valid()) {
            return "";
        }
        return recordText(
            NumberUtils.parseLong(record.code()),
            NumberUtils.parseLong(record.furnitureId()),
            record.selectedIds(),
            record.parameterText(),
            record.textValue(),
            record.extraValue());
    }

    static WiredRecord record(String recordText) {
        String bodyText = StringUtils.text(recordText);
        if (bodyText.startsWith("\1")) {
            bodyText = bodyText.substring(1);
        }
        StringUtils.SequentialFields fields = StringUtils.sequentialFields(bodyText, '\2', '\3', '\4', '\5', '\6');
        String code = fields.text(0);
        if (!fields.foundDelimiter(0)) {
            return new WiredRecord(code, "", "", "", "", "");
        }
        String furnitureId = fields.text(1);
        if (!fields.foundDelimiter(1)) {
            return new WiredRecord(code, furnitureId, "", "", "", "");
        }
        String selectedIds = fields.text(2);
        if (!fields.foundDelimiter(2)) {
            return new WiredRecord(code, furnitureId, selectedIds, "", "", "");
        }
        String parameterText = fields.text(3);
        if (!fields.foundDelimiter(3)) {
            return new WiredRecord(code, furnitureId, selectedIds, parameterText, "", "");
        }
        String textValue = fields.text(4);
        String extraValue = fields.foundDelimiter(4) ? fields.rest() : "";
        return new WiredRecord(code, furnitureId, selectedIds, parameterText, textValue, extraValue);
    }

    private static String recordMarker(String recordText) {
        WiredRecord record = record(recordText);
        if (record.code().isEmpty() || record.furnitureId().isEmpty()) {
            return "";
        }
        return "\1" + record.code() + '\2' + record.furnitureId() + '\3';
    }

    static String cacheWithRecord(String cacheText, String recordText) {
        String record = StringUtils.text(recordText);
        if (record.isEmpty()) {
            return StringUtils.text(cacheText);
        }
        String cache = StringUtils.removeLineRecord(cacheText, recordMarker(record));
        return cache.isEmpty() ? record : record + '\n' + cache;
    }

    static String cacheWithRecord(String cacheText, WiredRecord record) {
        return cacheWithRecord(cacheText, recordText(record));
    }

    public static boolean selectedItemsExist(List<Long> selectedFurnitureIds, List<Long> existingIds) {
        List<Long> selected = selectedFurnitureIds == null ? List.of() : selectedFurnitureIds;
        if (selected.isEmpty()) {
            return true;
        }
        List<Long> existing = existingIds == null ? List.of() : existingIds;
        for (long furnitureId : selected) {
            if (furnitureId > 0L && !existing.contains(furnitureId)) {
                return false;
            }
        }
        return true;
    }

    public static ApplyResult applySelected(
        List<Long> selectedFurnitureIds,
        String parameterText,
        long selectedFurnitureId,
        List<Long> existingIds,
        BiFunction<Long, Long, String> statePayloadBuilder
    ) {
        List<Long> effectiveSelectedIds = selectedFurnitureId > 0L
            ? List.of(selectedFurnitureId)
            : selectedFurnitureIds == null ? List.of() : selectedFurnitureIds;
        if (effectiveSelectedIds.isEmpty()) {
            return ApplyResult.empty();
        }
        long stateValue = stateParameterValue(parameterText);
        long appliedCount = 0L;
        PacketBuilder statePayloads = PacketBuilder.create();
        List<Long> existing = existingIds == null ? List.of() : existingIds;
        for (long furnitureId : effectiveSelectedIds) {
            if (furnitureId > 0L && existing.contains(furnitureId)) {
                statePayloads.appendRaw(statePayloadBuilder.apply(furnitureId, stateValue));
                appliedCount++;
            }
        }
        return new ApplyResult(appliedCount, statePayloads.build());
    }

    private static List<Long> parseIdList(String idText) {
        return StringUtils.positiveLongFields(StringUtils.text(idText).replace(',', ';'), ';');
    }

    private static String joinIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return "";
        }
        List<String> textIds = new ArrayList<>();
        for (long id : ids) {
            if (id > 0L) {
                textIds.add(String.valueOf(id));
            }
        }
        return StringUtils.delimitedText(textIds, ';');
    }

}
