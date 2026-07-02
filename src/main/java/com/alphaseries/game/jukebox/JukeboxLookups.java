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
import com.alphaseries.util.NumberUtils;

public final class JukeboxLookups {
    private JukeboxLookups() {
    }

    public record RoomRequest(String userId, long roomId) {
        public boolean validUser() {
            return NumberUtils.parseLong(userId) > 0L;
        }

        public boolean validRoom() {
            return roomId > 0L;
        }
    }

    public record DiskChangeResult(boolean valid, String payload, List<String> deliveryPayloads) {
        public DiskChangeResult {
            payload = payload == null ? "" : payload;
            deliveryPayloads = deliveryPayloads == null ? List.of() : List.copyOf(deliveryPayloads);
        }

        public static DiskChangeResult empty() {
            return new DiskChangeResult(false, "", List.of());
        }
    }

    public static RoomRequest roomRequest(int socketIndex, UserDao users, RoomDao rooms) {
        if (socketIndex <= 0) {
            return new RoomRequest("", 0L);
        }
        long userId = SessionState.instance().sessionUserIdBySocket(socketIndex);
        if (userId <= 0L && users != null) {
            try {
                userId = users.userIdBySocket(socketIndex);
            } catch (Exception ignored) {
                userId = 0L;
            }
        }
        long roomId = SessionState.instance().sessionCacheLong(String.valueOf(socketIndex), 1);
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
        return new RoomRequest(userId > 0L ? String.valueOf(userId) : "", roomId);
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
    public static String addDiskPayload(String userId, long roomId, JukeboxAddRequest request, JukeboxDao jukebox) {
        try {
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
            long userIdValue = NumberUtils.parseLong(userId);
            long destinationId = jukebox.diskDestinationForOwner(userIdValue, request.diskFurnitureId(), songDiskProductId);
            if (destinationId <= 0L) {
                return "";
            }
            jukebox.removeDiskFromOwner(userIdValue, request.diskFurnitureId(), songDiskProductId);
            jukebox.addPlaylistEntry(jukeboxId, request.diskFurnitureId(), request.playlistOrder(), destinationId);
            return InventoryMessagePayloads.remove(request.diskFurnitureId());
        } catch (Exception ignored) {
            return "";
        }
    }

    public static DiskChangeResult addDiskAction(String userId, long roomId, JukeboxAddRequest request, JukeboxDao jukebox) {
        String payload = addDiskPayload(userId, roomId, request, jukebox);
        if (payload.isEmpty()) {
            return DiskChangeResult.empty();
        }
        return new DiskChangeResult(true, payload, deliveryPayloads(
            payload,
            playlistPayload(roomId, jukebox),
            diskInventoryPayload(userId, jukebox)));
    }

    public static DiskChangeResult addDiskAction(RoomRequest roomRequest, JukeboxAddRequest request, JukeboxDao jukebox) {
        if (roomRequest == null || !roomRequest.validUser() || !roomRequest.validRoom()) {
            return DiskChangeResult.empty();
        }
        return addDiskAction(roomRequest.userId(), roomRequest.roomId(), request, jukebox);
    }

    /**
     * Original function: Proc_6_226_7F0B20.
     */
    public static boolean removeDisk(String userId, long roomId, long playlistOrder, JukeboxDao jukebox) {
        try {
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
            jukebox.returnDiskToOwner(NumberUtils.parseLong(userId), cdFurnitureId, songDiskProductId);
            jukebox.deletePlaylistEntry(jukeboxId, cdFurnitureId);
            jukebox.decrementOrdersAfter(jukeboxId, playlistOrder);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static DiskChangeResult removeDiskAction(String userId, long roomId, long playlistOrder, JukeboxDao jukebox) {
        if (!removeDisk(userId, roomId, playlistOrder, jukebox)) {
            return DiskChangeResult.empty();
        }
        return new DiskChangeResult(true, "", deliveryPayloads(
            playlistPayload(roomId, jukebox),
            diskInventoryPayload(userId, jukebox)));
    }

    public static DiskChangeResult removeDiskAction(RoomRequest request, long playlistOrder, JukeboxDao jukebox) {
        if (request == null || !request.validUser() || !request.validRoom()) {
            return DiskChangeResult.empty();
        }
        return removeDiskAction(request.userId(), request.roomId(), playlistOrder, jukebox);
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
    public static String diskInventoryPayload(String userId, JukeboxDao jukebox) {
        try {
            long songDiskProductId = defaultSongDiskProductId();
            if (songDiskProductId <= 0L || jukebox == null) {
                return "";
            }
            return JukeboxPayloads.diskInventory(jukebox.songDisks(NumberUtils.parseLong(userId), songDiskProductId));
        } catch (Exception ignored) {
            return "";
        }
    }

    public static String diskInventoryPayload(RoomRequest request, JukeboxDao jukebox) {
        if (request == null || !request.validUser()) {
            return "";
        }
        return diskInventoryPayload(request.userId(), jukebox);
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
            state.pendingFurnitureCache = JukeboxRequests.removeSoundMachineMarkers(
                state.pendingFurnitureCache, resolvedJukeboxId, activeDestinationId);
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

    private static List<String> deliveryPayloads(String... payloads) {
        if (payloads == null || payloads.length == 0) {
            return List.of();
        }
        java.util.ArrayList<String> result = new java.util.ArrayList<>();
        for (String payload : payloads) {
            if (payload != null && !payload.isEmpty()) {
                result.add(payload);
            }
        }
        return List.copyOf(result);
    }
}
