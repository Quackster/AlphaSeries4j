package com.alphaseries.game.pet;

import java.util.List;

public record PetTutorialGuideRemoval(
    long removedCount,
    List<Long> removedEntityIds
) {
    public PetTutorialGuideRemoval {
        removedEntityIds = removedEntityIds == null ? List.of() : List.copyOf(removedEntityIds);
    }

    public boolean hasRemovals() {
        return removedCount > 0L;
    }

    public List<String> removedPayloads() {
        return removedEntityIds.stream()
            .filter(entityId -> entityId != null && entityId > 0L)
            .map(PetPayloads::removedFromRoom)
            .toList();
    }
}
