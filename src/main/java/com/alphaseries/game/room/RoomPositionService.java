package com.alphaseries.game.room;

import com.alphaseries.game.pet.PetState;
import com.alphaseries.dao.mysql.FurnitureDao;
import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.db.Database;
import com.alphaseries.db.MySQL;
import com.alphaseries.util.StringUtils;

import java.sql.SQLException;

public final class RoomPositionService {
    private RoomPositionService() {
    }

    /**
     * Original function: Proc_10_25_80F5D0.
     */
    public static long roomPositionAvailable(long roomId, long positionX, long positionY) {
        try {
            if (roomId <= 0L) {
                return 1L;
            }

            RoomDao roomDao = roomDao();
            long occupiedCount = roomDao.furnitureCountAt(roomId, positionX, positionY);
            if (occupiedCount > 0L) {
                return 0L;
            }

            long botCount = roomDao.botCountAt(roomId, positionX, positionY);
            return representedPositionAvailable(roomId, occupiedCount, botCount);
        } catch (Exception ex) {
            return 0L;
        }
    }

    /**
     * Original function: Proc_10_27_81F1A0.
     */
    public static long representedBotPositionAvailable(long botEntityId, long positionX, long positionY) {
        try {
            if (botEntityId <= 0L) {
                return 0L;
            }

            long roomSlot = PetState.instance().representedBots().record(botEntityId).roomSlot();
            long botId = PetState.instance().representedBots().identityFromEntityOrBotId(botEntityId).botId();
            long roomId = 0L;
            RoomDao roomDao = roomDao();
            if (roomSlot > 0L) {
                roomId = roomDao.roomIdBySlot(roomSlot);
            }
            if (roomId <= 0L) {
                if (botId <= 0L) {
                    botId = botEntityId;
                }
                roomId = roomDao.roomIdByBot(botId);
            }
            if (roomId <= 0L) {
                return 1L;
            }
            return roomPositionAvailable(roomId, positionX, positionY);
        } catch (Exception ex) {
            return 0L;
        }
    }

    public static long representedPositionAvailable(long roomId, long furnitureCount, long botCount) {
        if (roomId <= 0L) {
            return 1L;
        }
        if (furnitureCount > 0L) {
            return 0L;
        }
        return botCount == 0L ? 1L : 0L;
    }

    public static long canPlaceFloorFurnitureAt(
        long furnitureId,
        long positionX,
        long positionY,
        long footprintX,
        long footprintY,
        FurnitureDao furniture,
        RoomDao rooms,
        RepresentedRoomCache representedRooms
    ) {
        try {
            if (furniture == null || rooms == null) {
                return 0L;
            }
            long resolvedFootprintX = footprintX <= 0L ? 1L : footprintX;
            long resolvedFootprintY = footprintY <= 0L ? 1L : footprintY;
            long roomId = furniture.roomIdByFurniture(furnitureId);
            long excludedFurnitureId = furnitureId;
            if (roomId <= 0L) {
                roomId = furnitureId;
                excludedFurnitureId = 0L;
            }
            if (roomId <= 0L || positionX < 0L || positionY < 0L) {
                return 0L;
            }
            RoomDao.RoomPlacementState placementState = rooms.roomPlacementState(roomId).orElse(null);
            if (placementState == null) {
                return 0L;
            }
            String[] mapRows = placementMapRows(placementState.modelMap());
            long allowWalkthrough = placementState.allowWalkthrough();
            long roomSlot = placementState.roomSlot();
            for (long tileY = positionY; tileY <= positionY + resolvedFootprintY - 1L; tileY++) {
                if (tileY < 0L || tileY >= mapRows.length) {
                    return 0L;
                }
                String mapRow = mapRows[(int) tileY];
                for (long tileX = positionX; tileX <= positionX + resolvedFootprintX - 1L; tileX++) {
                    if (tileX < 0L || tileX + 1L > mapRow.length()) {
                        return 0L;
                    }
                    String mapCell = mapRow.substring((int) tileX, (int) tileX + 1).toLowerCase();
                    if (mapCell.isEmpty() || "x".equals(mapCell)) {
                        return 0L;
                    }
                    if (furniture.floorFurnitureCountAtExcluding(roomId, excludedFurnitureId, tileX, tileY) > 0L) {
                        return 0L;
                    }
                    if (rooms.botCountAtLimited(roomId, tileX, tileY) > 0L) {
                        return 0L;
                    }
                    if (allowWalkthrough == 0L && roomSlot > 0L && representedRooms != null) {
                        for (long occupantRoomUserIndex : rooms.activeVisitIdsByRoom(roomId)) {
                            if (occupantRoomUserIndex > 0L) {
                                RoomUserPosition movementPosition = RoomUserPosition.from(
                                    representedRooms.movementPosition(roomSlot, occupantRoomUserIndex));
                                if (movementPosition.found() && movementPosition.positionX() == tileX
                                    && movementPosition.positionY() == tileY) {
                                    return 0L;
                                }
                            }
                        }
                    }
                }
            }
            return 1L;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    private static String[] placementMapRows(String modelMapText) {
        String modelMap = StringUtils.text(modelMapText).replace('\n', '\r');
        while (modelMap.contains("\r\r")) {
            modelMap = modelMap.replace("\r\r", "\r");
        }
        if (modelMap.endsWith("\r")) {
            modelMap = modelMap.substring(0, modelMap.length() - 1);
        }
        return modelMap.split("\r", -1);
    }

    private static RoomDao roomDao() throws SQLException {
        return new RoomDao(configuredDatabase());
    }

    private static Database configuredDatabase() throws SQLException {
        if (MySQL.configuredDatabase() == null) {
            throw new SQLException("Database is not configured.");
        }
        return MySQL.configuredDatabase();
    }
}
