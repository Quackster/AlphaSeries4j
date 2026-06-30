package com.alphaseries.game.moderation;

public record StaffCallForHelpRow(
    long callForHelpId,
    long tabId,
    long callerUserId,
    String callerName,
    long partnerUserId,
    long roomId,
    long categoryId,
    String description,
    long duplicateRoomId,
    String roomName,
    long pickerUserId
) {
}
