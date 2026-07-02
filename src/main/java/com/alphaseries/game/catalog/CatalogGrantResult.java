package com.alphaseries.game.catalog;

import com.alphaseries.util.NumberUtils;

import java.util.List;

public record CatalogGrantResult(List<Long> furnitureIds) {
    public static CatalogGrantResult empty() {
        return new CatalogGrantResult(List.of());
    }

    public CatalogGrantResult {
        furnitureIds = furnitureIds == null ? List.of() : List.copyOf(furnitureIds);
    }

    public boolean isEmpty() {
        return furnitureIds.isEmpty();
    }

    public long firstFurnitureId() {
        return furnitureIds.isEmpty() ? 0L : NumberUtils.parseLong(furnitureIds.get(0));
    }
}
