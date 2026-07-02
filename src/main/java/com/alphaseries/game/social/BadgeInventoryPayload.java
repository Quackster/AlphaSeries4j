package com.alphaseries.game.social;

public record BadgeInventoryPayload(
    String inventoryPayload,
    String displayPayload,
    String equippedPayload
) {
    public BadgeInventoryPayload {
        inventoryPayload = inventoryPayload == null ? "" : inventoryPayload;
        displayPayload = displayPayload == null ? "" : displayPayload;
        equippedPayload = equippedPayload == null ? "" : equippedPayload;
    }
}
