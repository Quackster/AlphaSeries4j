package com.alphaseries.game.social;

import com.alphaseries.game.pet.PetRoomOccupants;
import com.alphaseries.protocol.PacketBuilder;

public record SocialRoomOccupants(
    long occupantCount,
    long statusCount,
    String occupantPayload,
    String statusPayload
) {
    public SocialRoomOccupants {
        occupantPayload = occupantPayload == null ? "" : occupantPayload;
        statusPayload = statusPayload == null ? "" : statusPayload;
    }

    public SocialRoomOccupants withPetOccupants(PetRoomOccupants petOccupants) {
        if (petOccupants == null || !petOccupants.hasOccupants()) {
            return this;
        }
        return new SocialRoomOccupants(
            occupantCount + petOccupants.occupantCount(),
            statusCount + petOccupants.statusCount(),
            PacketBuilder.create().appendRaw(occupantPayload).appendRaw(petOccupants.occupantPayload()).build(),
            PacketBuilder.create().appendRaw(statusPayload).appendRaw(petOccupants.statusPayload()).build());
    }
}
