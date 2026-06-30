package com.alphaseries.messages.outgoing;

import com.alphaseries.dao.mysql.FurnitureDao;
import com.alphaseries.protocol.PacketBuilder;

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

    public record DimmerPresetPayload(long currentPresetId, String payload) {
    }
}
