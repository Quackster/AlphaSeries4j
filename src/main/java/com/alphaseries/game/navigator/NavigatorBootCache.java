package com.alphaseries.game.navigator;

import com.alphaseries.config.AppConfigState;
import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.db.Database;
import com.alphaseries.db.MySQL;
import com.alphaseries.protocol.PacketBuilder;

import java.util.ArrayList;
import java.util.List;

public final class NavigatorBootCache {
    private NavigatorBootCache() {
    }

    /**
     * Original function: Proc_1_2_6BE280.
     */
    public static void loadRecommendedRoomsCache() {
        List<RecommendedRooms.Payload> recommended = new ArrayList<>();
        long count = 0L;
        RoomDao rooms = roomDao();
        if (rooms != null) {
            try {
                for (Long treeIdValue : rooms.recommendedRoomTreeIds()) {
                    long treeId = treeIdValue == null ? 0L : treeIdValue.longValue();
                    if (treeId != 0L) {
                        recommended.add(buildRecommendedRoomsPayloadEntry(count, treeId, rooms.recommendedRoomRows(treeId)));
                        count++;
                    }
                }
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        NavigatorState.instance().setRecommendedRooms(recommended, count);
    }

    /**
     * Original function: Proc_1_11_6C8D10.
     */
    public static void loadRoomCategoryRowsCache() {
        long privateCategoryId = AppConfigState.instance().settingsCache()
            .longValueOrDefault("com.client.navigator.categories.default.private.id", 0);
        long publicCategoryId = AppConfigState.instance().settingsCache()
            .longValueOrDefault("com.client.navigator.categories.default.public.id", 0);
        List<RoomCategoryCache.DefaultCategoryId> defaults = List.of(
            RoomCategoryCache.DefaultCategoryId.of(privateCategoryId),
            RoomCategoryCache.DefaultCategoryId.empty(),
            RoomCategoryCache.DefaultCategoryId.of(publicCategoryId));
        NavigatorState.instance().setRoomCategoryDefaults(defaults);
        long parentCategoryId = privateCategoryId == 0L ? 1L : privateCategoryId;
        List<RoomDao.RoomCategoryRow> categoryRows = List.of();
        RoomDao rooms = roomDao();
        if (rooms != null) {
            try {
                categoryRows = rooms.roomCategoryRows(parentCategoryId);
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        NavigatorState.instance().setRoomCategoryRows(categoryRows);
    }

    /**
     * Original function: Proc_1_12_6C8EF0.
     */
    public static void loadRoomCategoryPayloadCache() {
        List<RoomCategoryCache.CategoryPayload> values = new ArrayList<RoomCategoryCache.CategoryPayload>();
        RoomCategoryCache roomCategoryCache = roomCategoryCache();
        List<RoomDao.RoomCategoryRow> categoryRows = roomCategoryCache.categoryRowList();
        for (int rank = 0; rank <= 20; rank++) {
            for (int hc = 0; hc <= 2; hc++) {
                values.add(buildRoomCategoryPayloadEntry(categoryRows, rank, hc));
            }
        }
        NavigatorState.instance().setRoomCategoryPayloads(values);
    }

    /**
     * Original function: Proc_1_12_6C8EF0.
     */
    public static RoomCategoryCache.CategoryPayload buildRoomCategoryPayloadEntry(
            List<RoomDao.RoomCategoryRow> categoryRows, long rankIndex, long hcLevel) {
        return RoomCategoryCache.CategoryPayload.fromCategoryRows(categoryRows, rankIndex, hcLevel);
    }

    /**
     * Original function: Proc_1_2_6BE280.
     */
    public static RecommendedRooms.Payload buildRecommendedRoomsPayloadEntry(long index, long treeId,
            List<RoomDao.RecommendedRoomRow> roomRows) {
        return RecommendedRooms.Payload.fromRecommendedRooms(index, treeId, roomRows);
    }

    private static RoomCategoryCache roomCategoryCache() {
        return NavigatorState.instance().roomCategoryCache();
    }

    private static RoomDao roomDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new RoomDao(database);
    }
}
