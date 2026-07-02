package com.alphaseries.game.pet;

public record PetPlacementAction(
    long botId,
    long botEntityId,
    long roomId,
    long positionX,
    long positionY,
    String positionZ,
    long positionR,
    String roomEntryPayload,
    String placedPayload
) {
    public PetPlacementAction {
        positionZ = positionZ == null ? "" : positionZ;
        roomEntryPayload = roomEntryPayload == null ? "" : roomEntryPayload;
        placedPayload = placedPayload == null ? "" : placedPayload;
    }

    public boolean valid() {
        return botId > 0L && botEntityId > 0L && roomId > 0L;
    }

    public boolean hasRoomEntryPayload() {
        return !roomEntryPayload.isEmpty();
    }
}
