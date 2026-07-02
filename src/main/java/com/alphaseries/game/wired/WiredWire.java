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

    public record SnapshotRequest(long furnitureId) {
    }

    public record EditFurnitureRequest(long furnitureId) {
    }

    /**
     * Original function: Proc_6_221_7ED1E0.
     */
    public static SnapshotRequest snapshotRequest(String packetPayload) {
        String requestPayload = StringUtils.text(packetPayload);
        if (requestPayload.startsWith("on")) {
            requestPayload = requestPayload.substring(2);
        }
        long furnitureId = WireReader.readLong(requestPayload, new WireReader.Offset(1));
        if (furnitureId <= 0L) {
            furnitureId = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        }
        return new SnapshotRequest(furnitureId);
    }

    /**
     * Original function: Proc_6_219_7EA390.
     * Original function: Proc_6_220_7EBA50.
     * Original function: Proc_6_223_7EEDD0.
     */
    public static EditFurnitureRequest editFurnitureRequest(String packetPayload, String packetCode) {
        String requestPayload = StringUtils.text(packetPayload);
        if (!StringUtils.text(packetCode).isEmpty() && requestPayload.startsWith(packetCode)) {
            requestPayload = requestPayload.substring(packetCode.length());
        }
        long furnitureId = WireReader.readLong(requestPayload, new WireReader.Offset(1));
        if (furnitureId <= 0L) {
            furnitureId = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        }
        return new EditFurnitureRequest(furnitureId);
    }

    /**
     * Original function: Proc_6_211_7E1E40.
     * Original function: Proc_6_212_7E36C0.
     * Original function: Proc_6_213_7E3FA0.
     * Original function: Proc_6_214_7E60C0.
     */
    public static String editRecord(String packetPayload, String packetCode, long wiredCode, boolean includeExtraValue) {
        String requestPayload = StringUtils.text(packetPayload);
        if (!StringUtils.text(packetCode).isEmpty() && requestPayload.startsWith(packetCode)) {
            requestPayload = requestPayload.substring(packetCode.length());
        }
        WireReader.Offset offset = new WireReader.Offset(1);
        long furnitureId = WireReader.readLong(requestPayload, offset);
        if (furnitureId <= 0L) {
            furnitureId = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        }
        if (furnitureId <= 0L || wiredCode <= 0L) {
            return "";
        }
        long parameterCount = WireReader.readLong(requestPayload, offset);
        if (parameterCount < 0L || parameterCount > 100L) {
            return "";
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
            return "";
        }
        List<Long> selectedFurnitureIds = new ArrayList<>();
        for (long selectedIndex = 0L; selectedIndex < selectedCount; selectedIndex++) {
            long selectedFurnitureId = WireReader.readLong(requestPayload, offset);
            if (selectedFurnitureId <= 0L) {
                return "";
            }
            selectedFurnitureIds.add(selectedFurnitureId);
        }
        long extraValue = includeExtraValue ? WireReader.readLong(requestPayload, offset) : 0L;
        return WiredPayloads.recordText(wiredCode, furnitureId, selectedFurnitureIds, parameterValues, textValue,
            includeExtraValue ? String.valueOf(extraValue) : "");
    }
}
