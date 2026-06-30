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

    public record DimmerPresetPayload(long currentPresetId, String payload) {
    }
}
