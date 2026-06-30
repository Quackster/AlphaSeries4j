package com.alphaseries.game.wired;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.function.BiFunction;

public final class WiredPayloads {
    private WiredPayloads() {
    }

    public static final class ApplyResult {
        public long appliedCount;
        public String statePayloads = "";
    }

    public record WiredRecord(
        String code,
        String furnitureId,
        String selectedIds,
        String parameterText,
        String textValue,
        String extraValue
    ) {
    }

    public static String specialState(long itemState) {
        return itemState == 1507L ? "5;1;7;1;5;0;" : "";
    }

    public static String recordText(
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

    public static WiredRecord record(String recordText) {
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

    public static String recordMarker(String recordText) {
        WiredRecord record = record(recordText);
        if (record.code().isEmpty() || record.furnitureId().isEmpty()) {
            return "";
        }
        return "\1" + record.code() + '\2' + record.furnitureId() + '\3';
    }

    public static String cacheWithRecord(String cacheText, String recordText) {
        String record = StringUtils.text(recordText);
        if (record.isEmpty()) {
            return StringUtils.text(cacheText);
        }
        String cache = StringUtils.removeLineRecord(cacheText, recordMarker(record));
        return cache.isEmpty() ? record : record + '\n' + cache;
    }

    public static boolean selectedItemsExist(String selectedIds, String existingIds) {
        String selected = StringUtils.text(selectedIds);
        if (selected.isEmpty()) {
            return true;
        }
        for (String idPart : selected.replace(',', ';').split(";", -1)) {
            long furnitureId = NumberUtils.parseLong(idPart);
            if (furnitureId > 0L && !containsDelimitedId(existingIds, furnitureId)) {
                return false;
            }
        }
        return true;
    }

    public static ApplyResult applySelected(
        String selectedIds,
        String parameterText,
        long selectedFurnitureId,
        String existingIds,
        BiFunction<Long, Long, String> statePayloadBuilder
    ) {
        ApplyResult result = new ApplyResult();
        String effectiveSelectedIds = selectedFurnitureId > 0L ? String.valueOf(selectedFurnitureId) : StringUtils.text(selectedIds);
        if (effectiveSelectedIds.isEmpty()) {
            return result;
        }
        String[] parameterParts = (StringUtils.text(parameterText) + ";").split(";", -1);
        long stateValue = NumberUtils.parseLong(parameterParts.length > 0 ? parameterParts[0] : "");
        for (String idPart : effectiveSelectedIds.replace(',', ';').split(";", -1)) {
            long furnitureId = NumberUtils.parseLong(idPart);
            if (furnitureId > 0L && containsDelimitedId(existingIds, furnitureId)) {
                result.statePayloads += statePayloadBuilder.apply(furnitureId, stateValue);
                result.appliedCount++;
            }
        }
        return result;
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

}
