package com.alphaseries.game.pet;

import java.util.List;

public record PetTutorialGuideRemoval(
    long removedCount,
    List<Long> removedEntityIds
) implements Iterable<String> {
    public PetTutorialGuideRemoval {
        removedEntityIds = removedEntityIds == null ? List.of() : List.copyOf(removedEntityIds);
    }

    public boolean hasRemovals() {
        return removedCount > 0L;
    }

    @Override
    public java.util.Iterator<String> iterator() {
        return payloadList().iterator();
    }

    private List<String> payloadList() {
        return removedEntityIds.stream()
            .filter(entityId -> entityId != null && entityId > 0L)
            .map(PetPayloads::removedFromRoom)
            .toList();
    }
}
