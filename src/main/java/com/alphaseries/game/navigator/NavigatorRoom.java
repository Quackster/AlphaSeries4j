package com.alphaseries.game.navigator;

public record NavigatorRoom(
    long roomId,
    String roomName,
    String ownerName,
    long doorStatus,
    long visitorsNow,
    long visitorsMax,
    String description,
    long hasTrading,
    long roomRate,
    long categoryId,
    String icon,
    String tagOne,
    String tagTwo,
    long allowOtherPets,
    long staffPicked
) {
}
