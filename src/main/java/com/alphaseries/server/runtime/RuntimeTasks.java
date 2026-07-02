package com.alphaseries.server.runtime;

import com.alphaseries.config.AppPaths;
import com.alphaseries.Handling;
import com.alphaseries.game.pet.PetState;
import com.alphaseries.dao.mysql.FurnitureDao;
import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.dao.mysql.ServerMaintenanceDao;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.db.Database;
import com.alphaseries.db.MySQL;
import com.alphaseries.game.pet.RepresentedBotRegistry;
import com.alphaseries.game.room.FurnitureRoomCache;
import com.alphaseries.game.room.FurnitureStateWrites;
import com.alphaseries.game.room.MovementStep;
import com.alphaseries.game.room.RepresentedRoomCache;
import com.alphaseries.game.room.RoomPositionService;
import com.alphaseries.game.room.RoomRollers;
import com.alphaseries.game.room.RoomState;
import com.alphaseries.game.session.SessionState;
import com.alphaseries.util.FileUtils;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.RandomUtils;
import com.alphaseries.util.StringUtils;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

public final class RuntimeTasks {
    private RuntimeTasks() {
    }

    public static long signerTimer() {
        long processed = 0L;
        try {
            FurnitureDao furniture = furnitureDao();
            FurnitureRoomCache.State cacheState = RoomState.instance().furnitureRoomCache();
            for (long furnitureId : cacheState.pendingFurnitureIds()) {
                FurnitureDao.PendingFurnitureState pendingState = furniture.pendingFurnitureState(furnitureId).orElse(null);
                if (pendingState == null) {
                    cacheState.removePendingFurniture(furnitureId);
                    RoomState.instance().setFurnitureRoomCache(cacheState);
                    continue;
                }
                long roomId = pendingState.roomId();
                long signValue = pendingState.sign();
                if (roomId > 0L && signValue > 0L) {
                    long nextSignValue = signValue - 1L;
                    furniture.updateSignLimited(furnitureId, nextSignValue);
                    cacheState = FurnitureStateWrites.refreshState(cacheState, roomId, furnitureId, nextSignValue);
                    RoomState.instance().setFurnitureRoomCache(cacheState);
                    Handling.broadcastToRoomUsers(roomId, "AX" + furnitureId + '\2' + nextSignValue + '\2');
                    processed++;
                    if (nextSignValue <= 0L) {
                        cacheState.removePendingFurniture(furnitureId);
                        cacheState.removePendingRoom(roomId);
                        RoomState.instance().setFurnitureRoomCache(cacheState);
                    }
                } else {
                    cacheState.removePendingFurniture(furnitureId);
                    RoomState.instance().setFurnitureRoomCache(cacheState);
                }
            }
            RoomState.instance().setFurnitureRoomCache(cacheState);
        } catch (Exception ignored) {
            // VB6 source suppresses timer failures.
        }
        return processed;
    }

    public static long botsTimer() {
        long moved = 0L;
        try {
            for (long entityId : PetState.instance().representedBots().allocatedEntityIds()) {
                RepresentedBotRegistry.RepresentedBotRecord bot = PetState.instance().representedBots().record(entityId);
                if (entityId <= 0L || bot.allowWalk() == 0L) {
                    continue;
                }
                long currentX = bot.positionX();
                long currentY = bot.positionY();
                long targetX = currentX + RandomUtils.longInclusive(-1, 1);
                long targetY = currentY + RandomUtils.longInclusive(-1, 1);
                moveRepresentedBot(entityId, currentX, currentY, targetX, targetY);
                moved++;
            }
        } catch (Exception ignored) {
            // VB6 source suppresses timer failures.
        }
        return moved;
    }

    public static long walkingTimer(long roomSlot) {
        long moved = 0L;
        try {
            RepresentedRoomCache representedRooms = RoomState.instance().representedRooms();
            for (long entityId : representedRooms.userEntityIds(roomSlot)) {
                moveRepresentedUser(entityId, 0L, 0L, 0L, 0L);
                moved++;
            }
            for (long entityId : representedRooms.botEntityIds(roomSlot)) {
                moveRepresentedBot(entityId, 0L, 0L, 0L, 0L);
                moved++;
            }
        } catch (Exception ignored) {
            // VB6 source suppresses timer failures.
        }
        return moved;
    }

    public static long pingTimer(long previousMostActiveCount) {
        long activeCount = 0L;
        try {
            ServerMaintenanceDao maintenanceDao = serverMaintenanceDao();
            if (maintenanceDao == null) {
                return 0L;
            }
            maintenanceDao.markSocketCheckTime();
            for (long socketIndex : Guardian.markedSocketIndexes()) {
                if (Guardian.isSocketConnected(socketIndex)) {
                    activeCount++;
                } else {
                    Handling.disconnectSocket(socketIndex);
                }
            }
            if (activeCount > previousMostActiveCount) {
                maintenanceDao.updateMostActiveSockets(activeCount);
            }
            Handling.expireUserEffects();
        } catch (Exception ignored) {
            // VB6 source suppresses timer failures.
        }
        return activeCount;
    }

    public static long rollersTimer(long roomSlot) {
        long moved = 0L;
        try {
            long roomId = mainCurrentRoomIdForSlot(roomSlot);
            if (roomId <= 0L) {
                return 0L;
            }
            FurnitureDao furniture = furnitureDao();
            List<FurnitureDao.RollerFurniture> rollers = furniture.rollerFurnitureInRoom(roomId);
            if (rollers.isEmpty()) {
                return 0L;
            }
            for (FurnitureDao.RollerFurniture roller : rollers) {
                long rollerId = roller.furnitureId();
                long rollerX = roller.positionX();
                long rollerY = roller.positionY();
                String rollerZ = StringUtils.text(roller.positionZ());
                long rollerR = roller.rotation();
                long targetX = rollerX + RoomRollers.deltaX(rollerR);
                long targetY = rollerY + RoomRollers.deltaY(rollerR);
                if (rollerId <= 0L || (targetX == rollerX && targetY == rollerY)
                    || RoomPositionService.roomPositionAvailable(roomId, targetX, targetY) == 0L) {
                    continue;
                }
                long movedId = mainRollerFurnitureOnTile(roomId, rollerId, rollerX, rollerY);
                if (movedId > 0L) {
                    String movedZ = mainRollerTargetHeight(roomId, targetX, targetY, rollerZ);
                    furniture.updateRoomPosition(movedId, roomId, targetX, targetY, movedZ);
                    RoomState.instance().setFurnitureRoomCache(FurnitureStateWrites.refreshState(
                        RoomState.instance().furnitureRoomCache(), roomId, movedId, 0));
                    String payload = RoomRollers.movePayload(movedId, targetX, targetY, movedZ);
                    if (!payload.isEmpty()) {
                        Handling.broadcastToRoomUsers(roomId, payload);
                    }
                    moved++;
                }
                mainRollerMoveOccupants(roomSlot, rollerX, rollerY, targetX, targetY, rollerR);
            }
            FileUtils.deleteFile(Path.of(AppPaths.applicationPath(), "CACHE", "ROOMS", roomId + ".cache").toString());
            FileUtils.deleteFile(Path.of(AppPaths.applicationPath(), "CACHE", "PATHFINDER", roomId + ".cache").toString());
        } catch (Exception ignored) {
            // VB6 source suppresses timer failures.
        }
        return moved;
    }

    /**
     * Original function: Proc_0_26_6ACF30.
     */
    public static void attachRepresentedUser(long socketIndex) {
        try {
            if (socketIndex <= 0L || !Guardian.isSocketConnected(socketIndex)) {
                return;
            }
            long roomSlot = mainRepresentedSocketRoomSlot(socketIndex);
            if (roomSlot > 0L) {
                mainRepresentedRoomOccupantAdd(roomSlot, socketIndex, 1);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses attachment failures.
        }
    }

    /**
     * Original function: Proc_0_27_6AD400.
     */
    public static void attachRepresentedBot(long entityIndex) {
        try {
            if (entityIndex <= 0L) {
                return;
            }
            long roomSlot = mainRepresentedBotRoomSlot(entityIndex);
            if (roomSlot > 0L) {
                mainRepresentedRoomOccupantAdd(roomSlot, entityIndex, 2);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses attachment failures.
        }
    }

    /**
     * Original function: Proc_0_28_6AD850.
     */
    public static void moveRepresentedBot(long entityIndex, long currentX, long currentY, long targetX, long targetY) {
        try {
            if (entityIndex <= 0L) {
                return;
            }
            long roomSlot = mainRepresentedBotRoomSlot(entityIndex);
            if (roomSlot <= 0L) {
                return;
            }
            long roomId = mainCurrentRoomIdForSlot(roomSlot);
            MovementStep movement = MovementStep.between(currentX, currentY, targetX, targetY);
            if (roomId <= 0L
                || RoomPositionService.representedBotPositionAvailable(
                    entityIndex, movement.positionX(), movement.positionY()) != 0L) {
                mainRepresentedRoomOccupantMove(roomSlot, entityIndex, 2,
                    movement.positionX(), movement.positionY(), movement.directionValue(), movement.movingValue());
            }
        } catch (Exception ignored) {
            // VB6 source suppresses walk failures.
        }
    }

    /**
     * Original function: Proc_0_29_6B0E10.
     */
    public static void moveRepresentedUser(long socketIndex, long currentX, long currentY, long targetX, long targetY) {
        try {
            if (socketIndex <= 0L || !Guardian.isSocketConnected(socketIndex)) {
                return;
            }
            long roomSlot = mainRepresentedSocketRoomSlot(socketIndex);
            if (roomSlot <= 0L) {
                roomSlot = socketIndex;
            }
            long roomId = mainCurrentRoomIdForSocket(socketIndex);
            MovementStep movement = MovementStep.between(currentX, currentY, targetX, targetY);
            if (roomId <= 0L
                || RoomPositionService.roomPositionAvailable(roomId, movement.positionX(), movement.positionY()) != 0L) {
                mainRepresentedRoomOccupantMove(roomSlot, socketIndex, 1,
                    movement.positionX(), movement.positionY(), movement.directionValue(), movement.movingValue());
            }
        } catch (Exception ignored) {
            // VB6 source suppresses walk failures.
        }
    }

    public static long mainRepresentedSocketRoomSlot(long socketIndex) {
        long roomSlot = SessionState.instance().sessionCacheLong(String.valueOf(socketIndex), 1);
        if (roomSlot > 0L) {
            return roomSlot;
        }
        return SessionState.instance().representedSockets().roomSlot(socketIndex);
    }

    public static long mainRepresentedBotRoomSlot(long entityIndex) {
        return PetState.instance().representedBots().record(entityIndex).roomSlot();
    }

    public static void mainRepresentedRoomOccupantAdd(long roomSlot, long entityIndex, long occupantType) {
        RoomState.instance().setRepresentedRooms(RoomState.instance().representedRooms().addOccupant(roomSlot, entityIndex, occupantType));
    }

    public static void mainRepresentedRoomOccupantMove(
        long roomSlot,
        long entityIndex,
        long occupantType,
        long positionX,
        long positionY,
        long directionValue,
        long movingValue
    ) {
        RoomState.instance().setRepresentedRooms(RoomState.instance().representedRooms()
            .moveOccupant(roomSlot, entityIndex, occupantType, positionX, positionY, directionValue, movingValue));
    }

    public static long mainRollerFurnitureOnTile(long roomId, long rollerId, long positionX, long positionY) {
        try {
            return roomDao().furnitureIdAtExcluding(roomId, rollerId, positionX, positionY);
        } catch (SQLException ignored) {
            return 0L;
        }
    }

    public static String mainRollerTargetHeight(long roomId, long positionX, long positionY, String fallbackHeight) {
        try {
            return RoomRollers.targetHeight(roomDao().topFurnitureHeightAt(roomId, positionX, positionY), fallbackHeight);
        } catch (SQLException ignored) {
            return RoomRollers.targetHeight("", fallbackHeight);
        }
    }

    public static long mainCurrentRoomIdForSlot(long roomSlot) {
        if (roomSlot <= 0L) {
            return 0L;
        }
        try {
            return roomDao().roomIdBySlot(roomSlot);
        } catch (SQLException ignored) {
            return 0L;
        }
    }

    public static long mainCurrentRoomIdForSocket(long socketIndex) {
        if (socketIndex <= 0L) {
            return 0L;
        }
        long roomId = SessionState.instance().sessionCacheLong(String.valueOf(socketIndex), 1);
        if (roomId > 0L) {
            return roomId;
        }
        String userId = MySQL.mySqlUserIdFromSocket((int) socketIndex);
        if (userId.isEmpty() || "0".equals(userId)) {
            return 0L;
        }
        try {
            return roomDao().currentRoomIdByUser(NumberUtils.parseLong(userId));
        } catch (SQLException ignored) {
            return 0L;
        }
    }

    public static String mainUserIdFromSocket(long socketIndex) {
        String userId = SessionState.instance().socketUserId(String.valueOf(socketIndex));
        if (userId.isEmpty() || "0".equals(userId)) {
            try {
                long databaseUserId = userDao().userIdBySocket(socketIndex);
                userId = databaseUserId <= 0L ? "0" : String.valueOf(databaseUserId);
            } catch (SQLException ignored) {
                userId = "";
            }
        }
        return userId;
    }

    public static void mainRollerMoveOccupants(long roomSlot, long fromX, long fromY, long toX, long toY, long directionValue) {
        RepresentedRoomCache representedRooms = RoomState.instance().representedRooms()
            .moveOccupantsAt(roomSlot, 1, fromX, fromY, toX, toY, directionValue)
            .moveOccupantsAt(roomSlot, 2, fromX, fromY, toX, toY, directionValue);
        RoomState.instance().setRepresentedRooms(representedRooms);
    }

    private static RoomDao roomDao() throws SQLException {
        return new RoomDao(configuredDatabase());
    }

    private static UserDao userDao() throws SQLException {
        return new UserDao(configuredDatabase());
    }

    private static FurnitureDao furnitureDao() throws SQLException {
        return new FurnitureDao(configuredDatabase());
    }

    private static ServerMaintenanceDao serverMaintenanceDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new ServerMaintenanceDao(database);
    }

    private static Database configuredDatabase() throws SQLException {
        if (MySQL.configuredDatabase() == null) {
            throw new SQLException("Database is not configured.");
        }
        return MySQL.configuredDatabase();
    }
}
