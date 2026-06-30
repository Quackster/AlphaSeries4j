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

    public record DimmerPresetPayload(long currentPresetId, String payload) {
    }
}
