package com.alphaseries.game.recycler;

import java.util.List;

public record RecyclerSelection(long requestedCount, List<Long> selectedItemIds, boolean valid) {
    public RecyclerSelection {
        selectedItemIds = selectedItemIds == null ? List.of() : List.copyOf(selectedItemIds);
    }

    public static RecyclerSelection empty(long requestedCount) {
        return new RecyclerSelection(requestedCount, List.of(), false);
    }
}
