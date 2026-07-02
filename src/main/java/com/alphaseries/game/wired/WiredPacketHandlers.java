package com.alphaseries.game.wired;

import com.alphaseries.dao.mysql.DaoProvider;
import com.alphaseries.dao.mysql.FurnitureDao;
import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.server.runtime.SocketDelivery;

public final class WiredPacketHandlers {
    private WiredPacketHandlers() {
    }

    /**
     * Original function: Proc_6_219_7EA390.
     */
    public static String editTrigger(int socketIndex, WiredWire.EditFurnitureRequest request) {
        return representedEdit(socketIndex, request, 1, 500, "wired_trigger", false);
    }

    /**
     * Original function: Proc_6_220_7EBA50.
     */
    public static String editAction(int socketIndex, WiredWire.EditFurnitureRequest request) {
        return representedEdit(socketIndex, request, 501, 1000, "wired_action", true);
    }

    /**
     * Original function: Proc_6_221_7ED1E0.
     */
    public static String createSnapshot(int socketIndex, WiredWire.SnapshotRequest snapshotRequest) {
        try {
            WiredLookups.RoomRequest request = WiredLookups.roomRequest(socketIndex, userDao(), roomDao());
            return WiredLookups.createSnapshot(
                socketIndex, request, snapshotRequest, furnitureDao(), roomDao());
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_222_7ED710.
     */
    public static String editCondition(int socketIndex, WiredWire.EditFurnitureRequest request) {
        return representedEdit(socketIndex, request, 1001, 1500, "wired_condition", false);
    }

    /**
     * Original function: Proc_6_211_7E1E40.
     * Original function: Proc_6_212_7E36C0.
     * Original function: Proc_6_213_7E3FA0.
     * Original function: Proc_6_214_7E60C0.
     */
    public static long triggerRepresented(int socketIndex, long fallbackRoomId, long triggerCode) {
        try {
            long roomId = WiredLookups.roomRequest(socketIndex, userDao(), roomDao()).roomId();
            if (roomId <= 0L) {
                roomId = fallbackRoomId;
            }
            return WiredLookups.trigger(roomId, triggerCode, 0L, furnitureDao(), SocketDelivery::broadcastToRoomUsers);
        } catch (Exception ignored) {
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_215_7E6770.
     * Original function: Proc_6_216_7E8120.
     * Original function: Proc_6_217_7E9780.
     */
    public static long runRepresentedAction(int socketIndex, long fallbackRoomId, long selectedFurnitureId, long actionCode) {
        try {
            long roomId = WiredLookups.roomRequest(socketIndex, userDao(), roomDao()).roomId();
            if (roomId <= 0L) {
                roomId = fallbackRoomId;
            }
            return WiredLookups.action(roomId, actionCode, selectedFurnitureId, furnitureDao(), SocketDelivery::broadcastToRoomUsers);
        } catch (Exception ignored) {
            return 0L;
        }
    }

    private static String representedEdit(
        int socketIndex,
        WiredWire.EditFurnitureRequest editRequest,
        long minimumCode,
        long maximumCode,
        String cacheFolder,
        boolean includeExtraValue
    ) {
        try {
            WiredLookups.RoomRequest request = WiredLookups.roomRequest(socketIndex, userDao(), roomDao());
            return WiredLookups.editRecord(
                socketIndex,
                request,
                editRequest,
                minimumCode,
                maximumCode,
                cacheFolder,
                includeExtraValue,
                furnitureDao(),
                roomDao());
        } catch (Exception ignored) {
            return "";
        }
    }

    private static FurnitureDao furnitureDao() {
        return DaoProvider.furnitureDao();
    }

    private static RoomDao roomDao() {
        return DaoProvider.roomDao();
    }

    private static UserDao userDao() {
        return DaoProvider.userDao();
    }
}
