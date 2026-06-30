package com.alphaseries.game.room;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public final class RepresentedRoomSlots {
    private final Set<Long> availableSlots = new LinkedHashSet<>();

    private RepresentedRoomSlots(String availableSlotMarkers) {
        parse(StringUtils.text(availableSlotMarkers));
    }

    public static RepresentedRoomSlots fromLegacy(String availableSlotMarkers) {
        return new RepresentedRoomSlots(availableSlotMarkers);
    }

    public static RepresentedRoomSlots empty() {
        return new RepresentedRoomSlots("");
    }

    public String availableSlotMarkers() {
        StringBuilder markers = new StringBuilder();
        for (Long slotId : availableSlots) {
            markers.append(marker(slotId));
        }
        return markers.toString();
    }

    public boolean isEmpty() {
        return availableSlots.isEmpty();
    }

    public void ensureInitialized() {
        if (!availableSlots.isEmpty()) {
            return;
        }
        for (long slotIndex = 1L; slotIndex <= 500L; slotIndex++) {
            availableSlots.add(slotIndex);
        }
    }

    public long reserve(long preferredSlot) {
        ensureInitialized();
        if (preferredSlot > 0L && availableSlots.remove(preferredSlot)) {
            return preferredSlot;
        }
        Iterator<Long> iterator = availableSlots.iterator();
        if (iterator.hasNext()) {
            long candidateSlot = iterator.next();
            iterator.remove();
            return candidateSlot;
        }
        return 0L;
    }

    public void release(long slotId) {
        if (slotId <= 0L) {
            return;
        }
        availableSlots.add(slotId);
    }

    private void parse(String availableSlotMarkers) {
        for (String part : availableSlotMarkers.split("\\]", -1)) {
            long slotId = NumberUtils.parseLong(part.replace("[", ""));
            if (slotId > 0L) {
                availableSlots.add(slotId);
            }
        }
    }

    private static String marker(long slotId) {
        return "[" + slotId + "]";
    }
}
