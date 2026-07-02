package com.alphaseries.game.pet;

import java.util.List;

public record PetTutorialGuideRemoval(
    long removedCount,
    List<String> removedPayloads
) {
    public PetTutorialGuideRemoval {
        removedPayloads = removedPayloads == null ? List.of() : List.copyOf(removedPayloads);
    }

    public boolean hasRemovals() {
        return removedCount > 0L;
    }
}
