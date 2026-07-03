package com.alphaseries.game.jukebox;

import com.alphaseries.dao.mysql.DaoProvider;
import com.alphaseries.dao.mysql.JukeboxDao;
import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.game.room.RoomState;
import com.alphaseries.server.runtime.SocketDelivery;

public final class JukeboxPacketHandlers {
    private JukeboxPacketHandlers() {
    }

    /**
     * Original function: Proc_6_223_7EEDD0.
     */
    public static String sendSongInfo(int socketIndex, SongInfoRequest request) {
        try {
            if (socketIndex <= 0) {
                return "";
            }
            String payload = JukeboxLookups.songInfoPayload(request, jukeboxDao());
            SocketDelivery.sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_224_7EF5A0.
     */
    public static String clearSoundMarkers(int socketIndex, long roomId, long jukeboxId) {
        try {
            long effectiveRoomId = roomId > 0L
                ? roomId
                : JukeboxLookups.roomRequest(socketIndex, userDao(), roomDao()).roomId();
            RoomState.instance().setFurnitureRoomCache(JukeboxLookups.clearSoundMarkers(
                RoomState.instance().furnitureRoomCache(), effectiveRoomId, jukeboxId, jukeboxDao()));
            return "";
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_225_7EFBD0.
     */
    public static String addDisk(int socketIndex, JukeboxAddRequest addRequest) {
        try {
            if (socketIndex <= 0) {
                return "";
            }
            JukeboxLookups.RoomRequest request = JukeboxLookups.roomRequest(socketIndex, userDao(), roomDao());
            JukeboxLookups.DiskChangeResult result = JukeboxLookups.addDiskAction(
                request, addRequest, jukeboxDao());
            if (!result.valid()) {
                return "";
            }
            SocketDelivery.sendToSocket(socketIndex, result.deliveryPayloads());
            return result.payload();
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_226_7F0B20.
     */
    public static String removeDisk(int socketIndex, JukeboxRemoveRequest removeRequest) {
        try {
            if (socketIndex <= 0) {
                return "";
            }
            JukeboxLookups.RoomRequest request = JukeboxLookups.roomRequest(socketIndex, userDao(), roomDao());
            JukeboxLookups.DiskChangeResult result = JukeboxLookups.removeDiskAction(
                request, removeRequest, jukeboxDao());
            if (!result.valid()) {
                return "";
            }
            SocketDelivery.sendToSocket(socketIndex, result.deliveryPayloads());
            return "";
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_227_7F2400.
     */
    public static String sendPlaylist(int socketIndex) {
        try {
            if (socketIndex <= 0) {
                return "";
            }
            JukeboxLookups.RoomRequest request = JukeboxLookups.roomRequest(socketIndex, userDao(), roomDao());
            String payload = JukeboxLookups.playlistPayload(request, jukeboxDao());
            SocketDelivery.sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_228_7F2AF0.
     */
    public static String sendDiskInventory(int socketIndex) {
        try {
            JukeboxLookups.RoomRequest request = JukeboxLookups.roomRequest(socketIndex, userDao(), roomDao());
            String payload = JukeboxLookups.diskInventoryPayload(request, jukeboxDao());
            SocketDelivery.sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_229_7F3070.
     */
    public static String broadcastPlayback(int socketIndex, long roomId, long jukeboxId) {
        try {
            if (socketIndex <= 0) {
                return "";
            }
            long effectiveRoomId = roomId > 0L
                ? roomId
                : JukeboxLookups.roomRequest(socketIndex, userDao(), roomDao()).roomId();
            String payload = JukeboxLookups.playbackPayload(
                effectiveRoomId, jukeboxId, System.currentTimeMillis() / 1000L, jukeboxDao());
            if (payload.isEmpty()) {
                return "";
            }
            if (effectiveRoomId > 0L) {
                SocketDelivery.broadcastToRoomUsers(effectiveRoomId, payload);
            } else {
                SocketDelivery.broadcastToCurrentRoom(socketIndex, payload);
            }
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    private static UserDao userDao() {
        return DaoProvider.userDao();
    }

    private static RoomDao roomDao() {
        return DaoProvider.roomDao();
    }

    private static JukeboxDao jukeboxDao() {
        return DaoProvider.jukeboxDao();
    }
}
