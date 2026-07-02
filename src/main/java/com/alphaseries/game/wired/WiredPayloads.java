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

    static String recordText(
        long wiredCode,
        long furnitureId,
        List<Long> selectedFurnitureIds,
        List<Long> parameterValues,
        String textValue,
        String extraValue
    ) {
        return recordText(wiredCode, furnitureId, joinIds(selectedFurnitureIds), joinIds(parameterValues), textValue, extraValue);
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
        String recordText = "\1" + wiredCode + '\2' + furnitureId + '\3' + StringUtils.text(selectedIdsText)
            + '\4' + StringUtils.text(parameterValues) + '\5' + StringUtils.left(textValue, 125) + '\6';
        if (!StringUtils.text(extraValue).isEmpty()) {
            recordText += StringUtils.text(extraValue);
        }
        return recordText;
    }

    static WiredRecord record(String recordText) {
        String bodyText = StringUtils.text(recordText);
        if (bodyText.startsWith("\1")) {
            bodyText = bodyText.substring(1);
        }
        String[] parts = bodyText.split("\2", 2);
        String code = parts.length >= 1 ? parts[0] : "";
        if (parts.length < 2) {
            return new WiredRecord(code, "", "", "", "", "");
        }
        String restText = parts[1];
        parts = restText.split("\3", 2);
        String furnitureId = parts.length >= 1 ? parts[0] : "";
        if (parts.length < 2) {
            return new WiredRecord(code, furnitureId, "", "", "", "");
        }
        restText = parts[1];
        parts = restText.split("\4", 2);
        String selectedIds = parts.length >= 1 ? parts[0] : "";
        if (parts.length < 2) {
            return new WiredRecord(code, furnitureId, selectedIds, "", "", "");
        }
        restText = parts[1];
        parts = restText.split("\5", 2);
        String parameterText = parts.length >= 1 ? parts[0] : "";
        if (parts.length < 2) {
            return new WiredRecord(code, furnitureId, selectedIds, parameterText, "", "");
        }
        restText = parts[1];
        parts = restText.split("\6", 2);
        String textValue = parts.length >= 1 ? parts[0] : "";
        String extraValue = parts.length >= 2 ? parts[1] : "";
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

    public static boolean selectedItemsExist(List<Long> selectedFurnitureIds, String existingIds) {
        List<Long> selected = selectedFurnitureIds == null ? List.of() : selectedFurnitureIds;
        if (selected.isEmpty()) {
            return true;
        }
        for (long furnitureId : selected) {
            if (furnitureId > 0L && !containsDelimitedId(existingIds, furnitureId)) {
                return false;
            }
        }
        return true;
    }

    public static ApplyResult applySelected(
        List<Long> selectedFurnitureIds,
        String parameterText,
        long selectedFurnitureId,
        String existingIds,
        BiFunction<Long, Long, String> statePayloadBuilder
    ) {
        List<Long> effectiveSelectedIds = selectedFurnitureId > 0L
            ? List.of(selectedFurnitureId)
            : selectedFurnitureIds == null ? List.of() : selectedFurnitureIds;
        if (effectiveSelectedIds.isEmpty()) {
            return ApplyResult.empty();
        }
        String[] parameterParts = (StringUtils.text(parameterText) + ";").split(";", -1);
        long stateValue = NumberUtils.parseLong(parameterParts.length > 0 ? parameterParts[0] : "");
        long appliedCount = 0L;
        PacketBuilder statePayloads = PacketBuilder.create();
        for (long furnitureId : effectiveSelectedIds) {
            if (furnitureId > 0L && containsDelimitedId(existingIds, furnitureId)) {
                statePayloads.appendRaw(statePayloadBuilder.apply(furnitureId, stateValue));
                appliedCount++;
            }
        }
        return new ApplyResult(appliedCount, statePayloads.build());
    }

    private static boolean containsDelimitedId(String idText, long wantedId) {
        String wanted = String.valueOf(wantedId);
        for (String idPart : StringUtils.text(idText).replace(',', ';').split(";", -1)) {
            if (wanted.equals(String.valueOf(NumberUtils.parseLong(idPart)))) {
                return true;
            }
        }
        return false;
    }

    private static List<Long> parseIdList(String idText) {
        List<Long> ids = new ArrayList<>();
        for (String idPart : StringUtils.text(idText).replace(',', ';').split(";", -1)) {
            long id = NumberUtils.parseLong(idPart);
            if (id > 0L) {
                ids.add(id);
            }
        }
        return List.copyOf(ids);
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
        return String.join(";", textIds);
    }

}
