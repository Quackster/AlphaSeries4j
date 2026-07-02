package com.alphaseries.game.room;

public record RoomSettingsPayload(
    String roomName,
    String roomPassword,
    long doorStatus,
    String roomDescription,
    long visitorsMax,
    long categoryId,
    String tagOne,
    String tagTwo,
    long allowOthersPets,
    long allowFeedPets,
    long allowWalkthrough,
    long disableWalls,
    long thicknessFloor,
    long thicknessWallpaper
) {
    public RoomSettingsPayload withCategoryId(long categoryId) {
        return new RoomSettingsPayload(
            roomName,
            roomPassword,
            doorStatus,
            roomDescription,
            visitorsMax,
            categoryId,
            tagOne,
            tagTwo,
            allowOthersPets,
            allowFeedPets,
            allowWalkthrough,
            disableWalls,
            thicknessFloor,
            thicknessWallpaper);
    }

    public RoomSettingsPayload withDisableWalls(long disableWalls) {
        return new RoomSettingsPayload(
            roomName,
            roomPassword,
            doorStatus,
            roomDescription,
            visitorsMax,
            categoryId,
            tagOne,
            tagTwo,
            allowOthersPets,
            allowFeedPets,
            allowWalkthrough,
            disableWalls,
            thicknessFloor,
            thicknessWallpaper);
    }
}
