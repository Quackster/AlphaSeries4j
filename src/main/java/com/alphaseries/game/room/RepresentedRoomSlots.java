package com.alphaseries.game.room;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.regex.Pattern;

public final class RepresentedRoomSlots {
    private String availableSlotMarkers;

    private RepresentedRoomSlots(String availableSlotMarkers) {
        this.availableSlotMarkers = StringUtils.text(availableSlotMarkers);
    }

    public static RepresentedRoomSlots fromLegacy(String availableSlotMarkers) {
        return new RepresentedRoomSlots(availableSlotMarkers);
    }

    public String availableSlotMarkers() {
        return availableSlotMarkers;
    }

    public boolean isEmpty() {
        return availableSlotMarkers.isEmpty();
    }

    public void ensureInitialized() {
        if (!availableSlotMarkers.isEmpty()) {
            return;
        }
        StringBuilder slots = new StringBuilder();
        for (long slotIndex = 1L; slotIndex <= 500L; slotIndex++) {
            slots.append('[').append(slotIndex).append(']');
        }
        availableSlotMarkers = slots.toString();
    }

    public long reserve(long preferredSlot) {
        ensureInitialized();
        if (preferredSlot > 0L && reserveMarker(preferredSlot)) {
            return preferredSlot;
        }
        for (String part : availableSlotMarkers.split("\\]", -1)) {
            long candidateSlot = NumberUtils.parseLong(part.replace("[", ""));
            if (candidateSlot > 0L && reserveMarker(candidateSlot)) {
                return candidateSlot;
            }
        }
        return 0L;
    }

    public void release(long slotId) {
        if (slotId <= 0L) {
            return;
        }
        String marker = marker(slotId);
        if (!availableSlotMarkers.contains(marker)) {
            availableSlotMarkers += marker;
        }
    }

    private boolean reserveMarker(long slotId) {
        String marker = marker(slotId);
        if (!availableSlotMarkers.contains(marker)) {
            return false;
        }
        availableSlotMarkers = availableSlotMarkers.replaceFirst(Pattern.quote(marker), "");
        return true;
    }

    private static String marker(long slotId) {
        return "[" + slotId + "]";
    }
}
