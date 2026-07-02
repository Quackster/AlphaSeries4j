package com.alphaseries.game.pet;

public record PetPackagePlacement(
    long botId,
    String inventoryAddPayload,
    String nameValidationPayload
) {
    public PetPackagePlacement {
        inventoryAddPayload = inventoryAddPayload == null ? "" : inventoryAddPayload;
        nameValidationPayload = nameValidationPayload == null ? "" : nameValidationPayload;
    }

    public boolean valid() {
        return botId > 0L;
    }

    public boolean hasInventoryAddPayload() {
        return !inventoryAddPayload.isEmpty();
    }
}
