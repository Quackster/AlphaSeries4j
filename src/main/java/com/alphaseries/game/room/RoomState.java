package com.alphaseries.game.room;

public final class RoomState {
    private static final RoomState INSTANCE = new RoomState();

    private RepresentedRoomCache representedRooms = RepresentedRoomCache.empty();
    private RepresentedRoomSlots representedRoomSlots = RepresentedRoomSlots.empty();
    private RoomPortalSettings portalSettings = RoomPortalSettings.empty();
    private RoomEventLocales eventLocales = RoomEventLocales.fromLegacy("");

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

    public synchronized void setEventLocales(RoomEventLocales eventLocales) {
        this.eventLocales = eventLocales == null ? RoomEventLocales.fromLegacy("") : eventLocales;
    }

    public synchronized void setEventLocalesFromLegacy(Object cacheText) {
        eventLocales = RoomEventLocales.fromLegacy(cacheText);
    }

    public synchronized void setPortalSettings(RoomPortalSettings portalSettings) {
        this.portalSettings = portalSettings == null ? RoomPortalSettings.empty() : portalSettings;
    }

    public synchronized void setPortalSettingsFromLegacy(Object warpSpaceRows, Object specialGateRows) {
        portalSettings = RoomPortalSettings.fromLegacy(warpSpaceRows, specialGateRows);
    }

    public synchronized void setRepresentedRoomSlots(RepresentedRoomSlots representedRoomSlots) {
        this.representedRoomSlots = representedRoomSlots == null ? RepresentedRoomSlots.empty() : representedRoomSlots;
    }

    public synchronized void setRepresentedRoomSlotsFromLegacy(Object availableSlotMarkers) {
        representedRoomSlots = RepresentedRoomSlots.fromLegacy(availableSlotMarkers);
    }

    public synchronized void setRepresentedRooms(RepresentedRoomCache representedRooms) {
        this.representedRooms = representedRooms == null ? RepresentedRoomCache.empty() : representedRooms;
    }

    public synchronized void setRepresentedRoomsFromLegacy(String cacheText) {
        representedRooms = RepresentedRoomCache.fromLegacy(cacheText);
    }
}
