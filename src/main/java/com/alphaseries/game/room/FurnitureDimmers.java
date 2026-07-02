package com.alphaseries.game.room;

import com.alphaseries.dao.mysql.FurnitureDao;
import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.messages.outgoing.FurniturePayloads;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public final class FurnitureDimmers {
    private FurnitureDimmers() {
    }

    public static PresetPayload presets(long roomId, FurnitureDao furniture) {
        if (roomId <= 0L || furniture == null) {
            return PresetPayload.empty();
        }
        try {
            long dimmerFurnitureId = FurnitureLookups.dimmerFurnitureId(roomId, furniture);
            if (dimmerFurnitureId <= 0L) {
                return PresetPayload.empty();
            }
            FurniturePayloads.DimmerPresetPayload payload =
                FurniturePayloads.dimmerPresets(furniture.dimmerPresets(dimmerFurnitureId));
            return new PresetPayload(payload.currentPresetId(), payload.payload());
        } catch (Exception ignored) {
            return PresetPayload.empty();
        }
    }

    public static PresetPayload presetsForUser(long userId, long roomId, RoomDao rooms, FurnitureDao furniture) {
        if (!canControlDimmer(userId, roomId, rooms)) {
            return PresetPayload.empty();
        }
        return presets(roomId, furniture);
    }

    public static StatePayload toggleState(long roomId, FurnitureDao furniture) {
        if (roomId <= 0L || furniture == null) {
            return StatePayload.empty();
        }
        try {
            long dimmerFurnitureId = FurnitureLookups.dimmerFurnitureId(roomId, furniture);
            if (dimmerFurnitureId <= 0L) {
                return StatePayload.empty();
            }
            FurnitureDao.ActiveDimmerState dimmer = furniture.activeDimmerState(dimmerFurnitureId).orElse(null);
            if (dimmer == null) {
                return StatePayload.empty();
            }
            String currentSign = StringUtils.text(dimmer.sign());
            long currentState = currentSign.isEmpty() ? 0L : NumberUtils.parseLong(StringUtils.left(currentSign, 1));
            if (currentState <= 0L) {
                currentState = 2L;
            }
            long nextState = currentState - 1L;
            if (nextState < 1L) {
                nextState = 2L;
            }
            String signText = nextState + "," + dimmer.presetId() + "," + dimmer.backgroundId()
                + "," + dimmer.colour() + "," + dimmer.lightLevel();
            furniture.updateSignText(dimmerFurnitureId, signText);
            return new StatePayload(
                dimmerFurnitureId,
                nextState,
                FurniturePayloads.wallState(dimmerFurnitureId, dimmer.productId(), dimmer.wallPosition(), signText));
        } catch (Exception ignored) {
            return StatePayload.empty();
        }
    }

    public static StatePayload toggleStateForUser(long userId, long roomId, RoomDao rooms, FurnitureDao furniture) {
        if (!canControlDimmer(userId, roomId, rooms)) {
            return StatePayload.empty();
        }
        return toggleState(roomId, furniture);
    }

    public static UpdatePayload updatePreset(
        long roomId,
        long presetId,
        long backgroundId,
        String colourText,
        long lightLevel,
        FurnitureDao furniture
    ) {
        String colour = StringUtils.text(colourText).toUpperCase();
        if (roomId <= 0L || furniture == null || presetId < 1L || presetId > 3L || backgroundId < 1L
            || backgroundId > 2L || !FurnitureWire.isDimmerColour(colour) || lightLevel < 76L || lightLevel > 225L) {
            return UpdatePayload.empty();
        }
        try {
            long dimmerFurnitureId = FurnitureLookups.dimmerFurnitureId(roomId, furniture);
            if (dimmerFurnitureId <= 0L) {
                return UpdatePayload.empty();
            }
            String signText = "2," + presetId + "," + backgroundId + "," + colour + "," + lightLevel;
            furniture.resetDimmerPresetStates(dimmerFurnitureId);
            furniture.updateDimmerPreset(dimmerFurnitureId, presetId, lightLevel, backgroundId, colour);
            furniture.updateSignText(dimmerFurnitureId, signText);
            FurnitureDao.WallProductPosition wallPosition = furniture.wallProductPosition(dimmerFurnitureId).orElse(null);
            String payload = wallPosition == null
                ? ""
                : FurniturePayloads.wallState(
                    dimmerFurnitureId,
                    wallPosition.productId(),
                    wallPosition.wallPosition(),
                    signText);
            return new UpdatePayload(dimmerFurnitureId, payload);
        } catch (Exception ignored) {
            return UpdatePayload.empty();
        }
    }

    public static UpdatePayload updatePresetForUser(
        long userId,
        long roomId,
        long presetId,
        long backgroundId,
        String colourText,
        long lightLevel,
        RoomDao rooms,
        FurnitureDao furniture
    ) {
        if (!canControlDimmer(userId, roomId, rooms)) {
            return UpdatePayload.empty();
        }
        return updatePreset(roomId, presetId, backgroundId, colourText, lightLevel, furniture);
    }

    private static boolean canControlDimmer(long userId, long roomId, RoomDao rooms) {
        return roomId > 0L
            && (RoomLookups.userOwnsRoom(userId, roomId, rooms)
                || RoomLookups.userHasRoomRight(userId, roomId, rooms));
    }

    public record PresetPayload(long currentPresetId, String payload) {
        public static PresetPayload empty() {
            return new PresetPayload(0L, "");
        }
    }

    public record StatePayload(long furnitureId, long state, String payload) {
        public static StatePayload empty() {
            return new StatePayload(0L, 0L, "");
        }
    }

    public record UpdatePayload(long furnitureId, String payload) {
        public static UpdatePayload empty() {
            return new UpdatePayload(0L, "");
        }
    }
}
