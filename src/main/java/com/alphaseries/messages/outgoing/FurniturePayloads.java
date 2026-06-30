package com.alphaseries.messages.outgoing;

import com.alphaseries.dao.mysql.FurnitureDao;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.StringUtils;

import java.util.List;

public final class FurniturePayloads {
    private FurniturePayloads() {
    }

    public static DimmerPresetPayload dimmerPresets(List<FurnitureDao.DimmerPreset> presets) {
        long currentPresetId = 0L;
        PacketBuilder presetRows = PacketBuilder.create();
        for (FurnitureDao.DimmerPreset preset : presets == null ? List.<FurnitureDao.DimmerPreset>of() : presets) {
            if (preset != null) {
                if (preset.stateId() == 2L || currentPresetId == 0L) {
                    currentPresetId = preset.presetId();
                }
                presetRows
                    .appendInt(preset.presetId())
                    .appendInt(preset.backgroundId())
                    .appendInt(preset.lightLevel())
                    .appendString(preset.colour());
            }
        }
        return new DimmerPresetPayload(
            currentPresetId,
            PacketBuilder.message("Em")
                .appendInt(0L)
                .appendInt(currentPresetId)
                .appendRaw(presetRows)
                .build());
    }

    public static String stateChanged(long furnitureId, long stateValue) {
        return "AX" + furnitureId + '\2' + stateValue + '\2';
    }

    public static String simpleFloorUse(long furnitureId, long stateValue) {
        return "0" + PacketBuilder.message("AZ")
            .appendInt(furnitureId)
            .appendInt(stateValue)
            .build();
    }

    public static String chargePrompt(
        long furnitureId,
        long currentCharges,
        long chargeSize,
        long chargePriceCredits,
        long chargePricePoints,
        long chargePointType
    ) {
        return PacketBuilder.message("Iu")
            .appendInt(furnitureId)
            .appendInt(currentCharges)
            .appendInt(chargeSize)
            .appendInt(chargePriceCredits)
            .appendInt(chargePricePoints)
            .appendInt(chargePointType)
            .build();
    }

    public static String wallState(long furnitureId, long productId, String wallPosition, String signText) {
        return PacketBuilder.message("AU")
            .appendRaw(furnitureId)
            .appendRaw('\2')
            .appendInt(productId)
            .appendString(wallPosition)
            .appendString(signText)
            .build();
    }

    public static String stickyNoteUpdated(long furnitureId, long productId, String noteColor) {
        return PacketBuilder.create()
            .appendRaw("AT")
            .appendRaw(furnitureId)
            .appendRaw('\1')
            .appendRaw("AS")
            .appendRaw(furnitureId)
            .appendString("")
            .appendInt(productId)
            .appendRaw(productId)
            .appendString("")
            .appendString(noteColor)
            .build();
    }

    public static String wallInventoryPlacement(
        long furnitureId,
        long productId,
        String wallPosition,
        String itemData,
        long secondaryValue
    ) {
        return PacketBuilder.create()
            .appendRaw('0')
            .appendRaw(furnitureId)
            .appendRaw('\2')
            .appendInt(productId)
            .appendString(wallPosition)
            .appendString(itemData)
            .appendInt(secondaryValue)
            .build();
    }

    public static String floorPlacement(
        long furnitureId,
        long positionX,
        long positionY,
        long rotation,
        long positionZ,
        String stateText,
        String itemData,
        long secondaryValue,
        long productId
    ) {
        String normalizedItemData = StringUtils.text(itemData).replace('\b', '\t').replace("{{9}}", "\t");
        return PacketBuilder.create()
            .appendRaw("000")
            .appendInt(furnitureId)
            .appendInt(positionX)
            .appendInt(positionY)
            .appendInt(rotation)
            .appendInt(positionZ)
            .appendString(stateText)
            .appendInt(secondaryValue)
            .appendRaw(normalizedItemData)
            .appendRaw('\2')
            .appendRaw('M')
            .appendInt(productId)
            .build();
    }

    public static String presentOpened(long productId, String responseClass, String itemData) {
        return PacketBuilder.message("BA")
            .appendString(responseClass)
            .appendInt(productId)
            .appendString(itemData)
            .build();
    }

    public static String packageOpened(long packageProductId, long furnitureId, String packageType) {
        return PacketBuilder.message("L}")
            .appendString(packageType)
            .appendInt(packageProductId)
            .appendInt(furnitureId)
            .appendRaw('H')
            .build();
    }

    public static String floorList(long itemCount, String itemPayload) {
        return PacketBuilder.message("@^")
            .appendInt(itemCount)
            .appendRaw(itemPayload)
            .build();
    }

    public static String wallList(long itemCount, String itemPayload) {
        return PacketBuilder.message("@m")
            .appendInt(itemCount)
            .appendRaw(itemPayload)
            .build();
    }

    public record DimmerPresetPayload(long currentPresetId, String payload) {
    }
}
