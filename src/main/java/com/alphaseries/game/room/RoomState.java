package com.alphaseries.game.room;

public final class RoomState {
    private static final RoomState INSTANCE = new RoomState();

    private RepresentedRoomCache representedRooms = RepresentedRoomCache.empty();
    private RepresentedRoomSlots representedRoomSlots = RepresentedRoomSlots.empty();

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

    public synchronized void setRepresentedRoomSlots(RepresentedRoomSlots representedRoomSlots) {
        this.representedRoomSlots = representedRoomSlots == null ? RepresentedRoomSlots.empty() : representedRoomSlots;
    }

    public synchronized void setRepresentedRoomSlotsFromLegacy(String availableSlotMarkers) {
        representedRoomSlots = RepresentedRoomSlots.fromLegacy(availableSlotMarkers);
    }

    public synchronized void setRepresentedRooms(RepresentedRoomCache representedRooms) {
        this.representedRooms = representedRooms == null ? RepresentedRoomCache.empty() : representedRooms;
    }

    public synchronized void setRepresentedRoomsFromLegacy(String cacheText) {
        representedRooms = RepresentedRoomCache.fromLegacy(cacheText);
    }
}
