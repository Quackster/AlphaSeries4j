package com.alphaseries.game.wired;

import com.alphaseries.protocol.WireEncoding;
import com.alphaseries.protocol.WireReader;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class WiredWire {
    private WiredWire() {
    }

    public record SnapshotRequest(String packetPayload, long furnitureId) {
        public SnapshotRequest {
            packetPayload = StringUtils.text(packetPayload);
        }
    }

    public record EditFurnitureRequest(String packetPayload, String packetCode, long furnitureId) {
        public EditFurnitureRequest {
            packetPayload = StringUtils.text(packetPayload);
            packetCode = StringUtils.text(packetCode);
        }
    }

    /**
     * Original function: Proc_6_221_7ED1E0.
     */
    public static SnapshotRequest snapshotRequest(String packetPayload) {
        String requestPayload = StringUtils.withoutPrefix(packetPayload, "on");
        long furnitureId = WireReader.readLong(requestPayload, new WireReader.Offset(1));
        if (furnitureId <= 0L) {
            furnitureId = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        }
        return new SnapshotRequest(packetPayload, furnitureId);
    }

    /**
     * Original function: Proc_6_219_7EA390.
     * Original function: Proc_6_220_7EBA50.
     * Original function: Proc_6_223_7EEDD0.
     */
    public static EditFurnitureRequest editFurnitureRequest(String packetPayload, String packetCode) {
        String requestPayload = StringUtils.withoutPrefix(packetPayload, packetCode);
        long furnitureId = WireReader.readLong(requestPayload, new WireReader.Offset(1));
        if (furnitureId <= 0L) {
            furnitureId = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        }
        return new EditFurnitureRequest(packetPayload, packetCode, furnitureId);
    }

    /**
     * Original function: Proc_6_211_7E1E40.
     * Original function: Proc_6_212_7E36C0.
     * Original function: Proc_6_213_7E3FA0.
     * Original function: Proc_6_214_7E60C0.
     */
    public static WiredPayloads.WiredRecord editRecordRequest(
        String packetPayload,
        String packetCode,
        long wiredCode,
        boolean includeExtraValue
    ) {
        return parseEditRecord(packetPayload, packetCode, wiredCode, includeExtraValue);
    }

    private static WiredPayloads.WiredRecord parseEditRecord(
        String packetPayload,
        String packetCode,
        long wiredCode,
        boolean includeExtraValue
    ) {
        String requestPayload = StringUtils.withoutPrefix(packetPayload, packetCode);
        WireReader.Offset offset = new WireReader.Offset(1);
        long furnitureId = WireReader.readLong(requestPayload, offset);
        if (furnitureId <= 0L) {
            furnitureId = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        }
        if (furnitureId <= 0L || wiredCode <= 0L) {
            return new WiredPayloads.WiredRecord("", "", "", "", "", "");
        }
        long parameterCount = WireReader.readLong(requestPayload, offset);
        if (parameterCount < 0L || parameterCount > 100L) {
            return new WiredPayloads.WiredRecord("", "", "", "", "", "");
        }
        List<Long> parameterValues = new ArrayList<>();
        for (long parameterIndex = 0L; parameterIndex < parameterCount; parameterIndex++) {
            long parameterValue = WireReader.readLong(requestPayload, offset);
            parameterValues.add(parameterValue);
        }
        String textValue = StringUtils.left(StringUtils.singleLineText(WireReader.readString(requestPayload, offset)), 125);
        if (textValue.isEmpty()) {
            textValue = StringUtils.left(StringUtils.singleLineText(WireEncoding.readBase64LengthString(requestPayload)), 125);
        }
        long selectedCount = WireReader.readLong(requestPayload, offset);
        if (selectedCount < 0L || selectedCount > 100L) {
            return new WiredPayloads.WiredRecord("", "", "", "", "", "");
        }
        List<Long> selectedFurnitureIds = new ArrayList<>();
        for (long selectedIndex = 0L; selectedIndex < selectedCount; selectedIndex++) {
            long selectedFurnitureId = WireReader.readLong(requestPayload, offset);
            if (selectedFurnitureId <= 0L) {
                return new WiredPayloads.WiredRecord("", "", "", "", "", "");
            }
            selectedFurnitureIds.add(selectedFurnitureId);
        }
        long extraValue = includeExtraValue ? WireReader.readLong(requestPayload, offset) : 0L;
        return WiredPayloads.record(wiredCode, furnitureId, selectedFurnitureIds, parameterValues, textValue,
            includeExtraValue ? String.valueOf(extraValue) : "");
    }
}
