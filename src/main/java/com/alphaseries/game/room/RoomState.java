package com.alphaseries.game.room;

public final class RoomState {
    private static final RoomState INSTANCE = new RoomState();

    private FurnitureRoomCache.State furnitureRoomCache = FurnitureRoomCache.State.empty();
    private RepresentedRoomCache representedRooms = RepresentedRoomCache.empty();
    private RepresentedRoomSlots representedRoomSlots = RepresentedRoomSlots.empty();
    private RoomPortalSettings portalSettings = RoomPortalSettings.empty();
    private RoomEventLocales eventLocales = RoomEventLocales.empty();

    private RoomState() {
    }

    public static RoomState instance() {
        return INSTANCE;
    }

    public synchronized RepresentedRoomCache representedRooms() {
        return representedRooms;
    }

    public synchronized RepresentedRoomSlots representedRoomSlots() {
        return representedRoomSlots;
    }

    public synchronized RoomPortalSettings portalSettings() {
        return portalSettings;
    }

    public synchronized RoomEventLocales eventLocales() {
        return eventLocales;
    }

    public synchronized FurnitureRoomCache.State furnitureRoomCache() {
        return FurnitureRoomCache.State.from(furnitureRoomCache, representedRooms);
    }

    public synchronized void setFurnitureRoomCache(FurnitureRoomCache.State state) {
        if (state == null) {
            furnitureRoomCache = FurnitureRoomCache.State.empty();
            representedRooms = RepresentedRoomCache.empty();
            return;
        }
        furnitureRoomCache = FurnitureRoomCache.State.markerStateFrom(state);
        representedRooms = state.representedRooms();
    }

    public synchronized void setEventLocales(RoomEventLocales eventLocales) {
        this.eventLocales = eventLocales == null ? RoomEventLocales.empty() : eventLocales;
    }

    public synchronized void setPortalSettings(RoomPortalSettings portalSettings) {
        this.portalSettings = portalSettings == null ? RoomPortalSettings.empty() : portalSettings;
    }

    public synchronized void setRepresentedRoomSlots(RepresentedRoomSlots representedRoomSlots) {
        this.representedRoomSlots = representedRoomSlots == null ? RepresentedRoomSlots.empty() : representedRoomSlots;
    }

    public synchronized void setRepresentedRooms(RepresentedRoomCache representedRooms) {
        this.representedRooms =
            representedRooms == null ? RepresentedRoomCache.empty() : representedRooms.normalizedForCacheMirror();
    }

    public synchronized void ensureRepresentedRoomSlotPool() {
        representedRoomSlots.ensureInitialized();
    }

    public synchronized long reserveRepresentedRoomSlot(long preferredSlot) {
        return representedRoomSlots.reserve(preferredSlot);
    }

    public synchronized void releaseRepresentedRoomSlot(long slotId) {
        representedRoomSlots.release(slotId);
    }
}
