package com.alphaseries.game.recycler;

import com.alphaseries.protocol.WireReader;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class RecyclerWire {
    private RecyclerWire() {
    }

    public static RecyclerSelection selectionFromWire(String packetPayload) {
        String requestPayload = StringUtils.withoutPrefix(packetPayload, "F^");
        WireReader.Offset offset = new WireReader.Offset(1);
        long requestedCount = WireReader.readLong(requestPayload, offset);
        if (requestedCount != 5L) {
            return RecyclerSelection.empty(requestedCount);
        }
        List<Long> selectedItemIds = new ArrayList<>();
        for (long itemIndex = 0L; itemIndex < requestedCount; itemIndex++) {
            long furnitureId = WireReader.readLong(requestPayload, offset);
            if (furnitureId <= 0L || selectedItemIds.contains(furnitureId)) {
                return RecyclerSelection.empty(requestedCount);
            }
            selectedItemIds.add(furnitureId);
        }
        return new RecyclerSelection(requestedCount, selectedItemIds, true);
    }

}
