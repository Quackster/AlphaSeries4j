package com.alphaseries.game.pet;

public record PetPickupAction(
    long botId,
    long botEntityId,
    String removedPayload,
    String inventoryAddPayload
) {
    public PetPickupAction {
        removedPayload = removedPayload == null ? "" : removedPayload;
        inventoryAddPayload = inventoryAddPayload == null ? "" : inventoryAddPayload;
    }

    public boolean valid() {
        return botId > 0L && botEntityId > 0L;
    }

    public boolean hasInventoryAddPayload() {
        return !inventoryAddPayload.isEmpty();
    }
}
