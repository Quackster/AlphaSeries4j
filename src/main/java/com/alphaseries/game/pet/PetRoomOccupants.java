package com.alphaseries.game.pet;

public record PetRoomOccupants(
    long occupantCount,
    long statusCount,
    String occupantPayload,
    String statusPayload
) {
    public PetRoomOccupants {
        occupantPayload = occupantPayload == null ? "" : occupantPayload;
        statusPayload = statusPayload == null ? "" : statusPayload;
    }

    public boolean hasOccupants() {
        return occupantCount > 0L;
    }
}
