package com.alphaseries.game.jukebox;

import java.util.List;
import java.util.Optional;

import com.alphaseries.config.AppConfigState;
import com.alphaseries.dao.mysql.JukeboxDao;
import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.game.room.FurnitureRoomCache;
import com.alphaseries.game.inventory.InventoryMessagePayloads;
import com.alphaseries.game.session.SessionState;
import com.alphaseries.messages.outgoing.JukeboxPayloads;

public final class JukeboxLookups {
    private JukeboxLookups() {
    }

    public record RoomRequest(long userId, long roomId) {
        public boolean validUser() {
            return userId > 0L;
        }

        public boolean validRoom() {
            return roomId > 0L;
        }
    }

    public record DiskChangeResult(boolean valid, String payload, DeliveryPayloads deliveryPayloads) {
        public DiskChangeResult {
            payload = payload == null ? "" : payload;
            deliveryPayloads = deliveryPayloads == null ? DeliveryPayloads.empty() : deliveryPayloads;
        }

        public static DiskChangeResult empty() {
            return new DiskChangeResult(false, "", DeliveryPayloads.empty());
        }
    }

    public record DeliveryPayloads(String diskChangePayload, String playlistPayload, String diskInventoryPayload) {
        public DeliveryPayloads {
            diskChangePayload = diskChangePayload == null ? "" : diskChangePayload;
            playlistPayload = playlistPayload == null ? "" : playlistPayload;
            diskInventoryPayload = diskInventoryPayload == null ? "" : diskInventoryPayload;
        }

        public static DeliveryPayloads empty() {
            return new DeliveryPayloads("", "", "");
        }

        public List<String> payloads() {
            java.util.ArrayList<String> result = new java.util.ArrayList<>();
            addPayload(result, diskChangePayload);
            addPayload(result, playlistPayload);
            addPayload(result, diskInventoryPayload);
            return List.copyOf(result);
        }

        private static void addPayload(List<String> payloads, String payload) {
            if (payload != null && !payload.isEmpty()) {
                payloads.add(payload);
            }
        }
    }

    public static RoomRequest roomRequest(int socketIndex, UserDao users, RoomDao rooms) {
        if (socketIndex <= 0) {
            return new RoomRequest(0L, 0L);
        }
        long userId = SessionState.instance().sessionUserIdBySocket(socketIndex);
        if (userId <= 0L && users != null) {
            try {
                userId = users.userIdBySocket(socketIndex);
            } catch (Exception ignored) {
                userId = 0L;
            }
        }
        long roomId = SessionState.instance().sessionCacheLong(socketIndex, 1);
        if (roomId <= 0L && userId > 0L && rooms != null) {
            try {
                roomId = rooms.currentRoomIdByUser(userId);
            } catch (Exception ignored) {
                roomId = 0L;
            }
        }
        if (roomId <= 0L && rooms != null) {
            try {
                roomId = rooms.roomIdBySlot(socketIndex);
            } catch (Exception ignored) {
                roomId = 0L;
            }
        }
        return new RoomRequest(userId, roomId);
    }

    public static Optional<JukeboxRow> rowForRoom(long roomId, JukeboxDao jukebox) {
        if (roomId <= 0L || jukebox == null) {
            return Optional.empty();
        }
        try {
            return jukebox.jukeboxInRoom(roomId);
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    public static String songInfoPayload(SongInfoRequest request, JukeboxDao jukebox) {
        try {
            if (request == null || jukebox == null) {
                return JukeboxPayloads.songInfo(List.of());
            }
            return JukeboxPayloads.songInfo(jukebox.songInfoRows(request.requestedIdList(), request.requestedCount()));
        } catch (Exception ignored) {
            return JukeboxPayloads.songInfo(List.of());
        }
    }

    /**
     * Original function: Proc_6_225_7EFBD0.
     */
    private static String addDiskPayload(RoomRequest roomRequest, JukeboxAddRequest request, JukeboxDao jukebox) {
        try {
            if (roomRequest == null || !roomRequest.validUser() || !roomRequest.validRoom()) {
                return "";
            }
            long userId = roomRequest.userId();
            long roomId = roomRequest.roomId();
            if (request == null || request.diskFurnitureId() <= 0L || roomId <= 0L || jukebox == null) {
                return "";
            }
            JukeboxRow jukeboxRow = rowForRoom(roomId, jukebox).orElse(null);
            if (jukeboxRow == null || jukeboxRow.id() <= 0L) {
                return "";
            }
            long jukeboxId = jukeboxRow.id();
            long jukeboxProductId = jukeboxRow.productId();
            String maxOrderText = jukebox.maxPlaylistOrderText(jukeboxId);
            long playlistCount = jukebox.playlistCount(jukeboxId);
            long playlistLimit = settingsLong("com.server.socket.game.jukebox." + jukeboxProductId + ".soundsets.max");
            if (!JukeboxRequests.canAddDisk(request.playlistOrder(), maxOrderText, playlistCount, playlistLimit)) {
                return "";
            }
            long songDiskProductId = defaultSongDiskProductId();
            if (songDiskProductId <= 0L) {
                return "";
            }
            long destinationId = jukebox.diskDestinationForOwner(userId, request.diskFurnitureId(), songDiskProductId);
            if (destinationId <= 0L) {
                return "";
            }
            jukebox.removeDiskFromOwner(userId, request.diskFurnitureId(), songDiskProductId);
            jukebox.addPlaylistEntry(jukeboxId, request.diskFurnitureId(), request.playlistOrder(), destinationId);
            return InventoryMessagePayloads.remove(request.diskFurnitureId());
        } catch (Exception ignored) {
            return "";
        }
    }

    public static DiskChangeResult addDiskAction(RoomRequest roomRequest, JukeboxAddRequest request, JukeboxDao jukebox) {
        String payload = addDiskPayload(roomRequest, request, jukebox);
        if (payload.isEmpty()) {
            return DiskChangeResult.empty();
        }
        return new DiskChangeResult(true, payload, new DeliveryPayloads(
            payload,
            playlistPayload(roomRequest, jukebox),
            diskInventoryPayload(roomRequest, jukebox)));
    }

    /**
     * Original function: Proc_6_226_7F0B20.
     */
    private static boolean removeDisk(RoomRequest request, JukeboxRemoveRequest removeRequest, JukeboxDao jukebox) {
        try {
            if (request == null || !request.validUser() || !request.validRoom() || removeRequest == null) {
                return false;
            }
            long userId = request.userId();
            long roomId = request.roomId();
            long playlistOrder = removeRequest.playlistOrder();
            if (roomId <= 0L || jukebox == null) {
                return false;
            }
            long jukeboxId = rowForRoom(roomId, jukebox).map(JukeboxRow::id).orElse(0L);
            if (jukeboxId <= 0L) {
                return false;
            }
            long cdFurnitureId = jukebox.diskFurnitureIdAtOrder(jukeboxId, playlistOrder);
            if (cdFurnitureId <= 0L) {
                return false;
            }
            long songDiskProductId = defaultSongDiskProductId();
            jukebox.returnDiskToOwner(userId, cdFurnitureId, songDiskProductId);
            jukebox.deletePlaylistEntry(jukeboxId, cdFurnitureId);
            jukebox.decrementOrdersAfter(jukeboxId, playlistOrder);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static DiskChangeResult removeDiskActionResult(RoomRequest request, JukeboxRemoveRequest removeRequest, JukeboxDao jukebox) {
        if (!removeDisk(request, removeRequest, jukebox)) {
            return DiskChangeResult.empty();
        }
        return new DiskChangeResult(true, "", new DeliveryPayloads(
            "",
            playlistPayload(request, jukebox),
            diskInventoryPayload(request, jukebox)));
    }

    public static DiskChangeResult removeDiskAction(RoomRequest request, long playlistOrder, JukeboxDao jukebox) {
        return removeDiskAction(request, new JukeboxRemoveRequest(playlistOrder), jukebox);
    }

    public static DiskChangeResult removeDiskAction(RoomRequest request, JukeboxRemoveRequest removeRequest, JukeboxDao jukebox) {
        return removeDiskActionResult(request, removeRequest, jukebox);
    }

    /**
     * Original function: Proc_6_227_7F2400.
     */
    public static String playlistPayload(long roomId, JukeboxDao jukebox) {
        try {
            if (roomId <= 0L || jukebox == null) {
                return "";
            }
            JukeboxRow jukeboxRow = rowForRoom(roomId, jukebox).orElse(null);
            if (jukeboxRow == null || jukeboxRow.id() <= 0L) {
                return "";
            }
            long jukeboxId = jukeboxRow.id();
            long jukeboxProductId = jukeboxRow.productId();
            long playlistLimit = settingsLong("com.server.socket.game.jukebox." + jukeboxProductId + ".soundsets.max");
            if (playlistLimit <= 0L) {
                playlistLimit = jukebox.playlistLimitFromEntries(jukeboxId);
            }
            if (playlistLimit <= 0L) {
                playlistLimit = 100L;
            }
            return JukeboxPayloads.playlist(playlistLimit, jukebox.playlistEntries(jukeboxId, playlistLimit));
        } catch (Exception ignored) {
            return "";
        }
    }

    public static String playlistPayload(RoomRequest request, JukeboxDao jukebox) {
        if (request == null || !request.validRoom()) {
            return "";
        }
        return playlistPayload(request.roomId(), jukebox);
    }

    /**
     * Original function: Proc_6_228_7F2AF0.
     */
    public static String diskInventoryPayload(RoomRequest request, JukeboxDao jukebox) {
        if (request == null || !request.validUser()) {
            return "";
        }
        try {
            long songDiskProductId = defaultSongDiskProductId();
            if (songDiskProductId <= 0L || jukebox == null) {
                return "";
            }
            return JukeboxPayloads.diskInventory(jukebox.songDisks(request.userId(), songDiskProductId));
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_229_7F3070.
     */
    public static String playbackPayload(long roomId, long jukeboxId, long nowSeconds, JukeboxDao jukebox) {
        try {
            long resolvedJukeboxId = jukeboxId;
            if (resolvedJukeboxId <= 0L && roomId > 0L) {
                resolvedJukeboxId = rowForRoom(roomId, jukebox).map(JukeboxRow::id).orElse(0L);
            }
            if (resolvedJukeboxId <= 0L || jukebox == null) {
                return "";
            }
            JukeboxPlaybackRow row = jukebox.playbackRow(resolvedJukeboxId).orElse(null);
            if (row == null) {
                return "";
            }
            return JukeboxPayloads.playback(nowSeconds, row.sequenceId(), row.destinationId(), row.diskFurnitureId());
        } catch (Exception ignored) {
            return "";
        }
    }

    public static FurnitureRoomCache.State clearSoundMarkers(
        FurnitureRoomCache.State cacheState,
        long roomId,
        long jukeboxId,
        JukeboxDao jukebox
    ) {
        FurnitureRoomCache.State state = cacheState == null
            ? FurnitureRoomCache.State.empty()
            : cacheState;
        try {
            long resolvedJukeboxId = jukeboxId;
            if (resolvedJukeboxId <= 0L && roomId > 0L) {
                resolvedJukeboxId = rowForRoom(roomId, jukebox).map(JukeboxRow::id).orElse(0L);
            }
            if (resolvedJukeboxId <= 0L) {
                return state;
            }
            long activeDestinationId = jukebox == null ? 0L : jukebox.activeDestinationId(resolvedJukeboxId);
            state.removePendingFurnitureMarkers(activeDestinationId, resolvedJukeboxId);
            return state;
        } catch (Exception ignored) {
            return state;
        }
    }

    private static long defaultSongDiskProductId() {
        return settingsLong("com.server.socket.game.default.songdisk");
    }

    private static long settingsLong(String key) {
        return AppConfigState.instance().settingsCache().longValueOrDefault(key, 0);
    }

}
