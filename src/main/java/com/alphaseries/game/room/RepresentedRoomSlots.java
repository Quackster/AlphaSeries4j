package com.alphaseries.game.room;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class RepresentedRoomSlots {
    private final Set<Long> availableSlots;

    private RepresentedRoomSlots(Collection<Long> availableSlots) {
        this.availableSlots = new LinkedHashSet<>();
        for (Long slotId : availableSlots) {
            if (slotId != null && slotId > 0L) {
                this.availableSlots.add(slotId);
            }
        }
    }

    public static RepresentedRoomSlots fromSlots(Collection<Long> availableSlots) {
        return new RepresentedRoomSlots(availableSlots == null ? List.of() : availableSlots);
    }

    public static RepresentedRoomSlots empty() {
        return new RepresentedRoomSlots(List.of());
    }

    public boolean isEmpty() {
        return availableSlots.isEmpty();
    }

    public List<Long> availableSlots() {
        return List.copyOf(availableSlots);
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

}
