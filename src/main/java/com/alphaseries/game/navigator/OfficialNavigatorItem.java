package com.alphaseries.game.navigator;

import java.util.Arrays;
import java.util.List;

public record OfficialNavigatorItem(
    long typeId,
    long styleId,
    long iconId,
    String caption,
    String captionTwo,
    String captionThree,
    String unusedSlot,
    String roomId,
    String roomName,
    String ownerName,
    String doorStatus,
    String visitorsNow,
    String visitorsMax,
    String description,
    String hasTrading,
    String unusedTradingSlot,
    String roomRate,
    String categoryId,
    String roomIcon,
    String tagOne,
    String tagTwo,
    String allowOtherPets,
    String modelName,
    String requiredFiles,
    String modelVisitorsMax,
    long parentId,
    long officialId,
    long requiredLevel
) {
    public List<String> textFields() {
        return Arrays.asList(
            caption,
            captionTwo,
            captionThree,
            unusedSlot,
            roomId,
            roomName,
            ownerName,
            doorStatus,
            visitorsNow,
            visitorsMax,
            description,
            hasTrading,
            unusedTradingSlot,
            roomRate,
            categoryId,
            roomIcon,
            tagOne,
            tagTwo,
            allowOtherPets,
            modelName,
            requiredFiles,
            modelVisitorsMax);
    }
}
