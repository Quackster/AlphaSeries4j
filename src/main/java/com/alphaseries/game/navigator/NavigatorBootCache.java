package com.alphaseries.game.navigator;

import com.alphaseries.config.AppConfigState;
import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.db.Database;
import com.alphaseries.db.MySQL;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.protocol.WireEncoding;

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
                        recommended.add(new RecommendedRooms.Payload(count,
                            WireEncoding.encodeVl64(treeId) + buildRecommendedRoomsPayload(rooms.recommendedRoomRows(treeId))));
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
                String payload = buildRoomCategoryPayload(categoryRows, rank, hc);
                values.add(new RoomCategoryCache.CategoryPayload(rank, hc, payload));
            }
        }
        NavigatorState.instance().setRoomCategoryPayloads(values);
    }

    /**
     * Original function: Proc_1_12_6C8EF0.
     */
    public static String buildRoomCategoryPayload(List<RoomDao.RoomCategoryRow> categoryRows, long rankIndex, long hcLevel) {
        long categoryCount = 0L;
        PacketBuilder categoryPayload = PacketBuilder.create();
        if (categoryRows != null) {
            for (RoomDao.RoomCategoryRow row : categoryRows) {
                if (row != null && rankIndex >= row.minimumRank() && hcLevel >= row.minimumHcRank()) {
                    categoryPayload
                        .appendInt(row.categoryId())
                        .appendString(row.name())
                        .appendInt(row.trading());
                    categoryCount++;
                }
            }
        }
        return PacketBuilder.create().appendInt(categoryCount).appendRaw(categoryPayload.build()).build();
    }

    /**
     * Original function: Proc_1_2_6BE280.
     */
    public static String buildRecommendedRoomsPayload(List<RoomDao.RecommendedRoomRow> roomRows) {
        long roomCount = 0L;
        PacketBuilder payload = PacketBuilder.create();
        if (roomRows != null) {
            for (RoomDao.RecommendedRoomRow row : roomRows) {
                if (row != null) {
                    roomCount++;
                    payload
                        .appendInt(row.type())
                        .appendInt(row.style())
                        .appendInt(row.icon())
                        .appendString(row.caption())
                        .appendString(row.captionTwo())
                        .appendString(row.captionThree())
                        .appendString(row.reservedSlot())
                        .appendString(row.roomId())
                        .appendString(row.roomName())
                        .appendString(row.ownerName())
                        .appendString(row.doorStatus())
                        .appendString(row.visitorsNow())
                        .appendString(row.visitorsMax())
                        .appendString(row.description())
                        .appendString(row.trading())
                        .appendString(row.reservedSecondSlot())
                        .appendString(row.rating())
                        .appendString(row.categoryId())
                        .appendString(row.roomIcon())
                        .appendString(row.tagOne())
                        .appendString(row.tagTwo())
                        .appendString(row.allowOtherPets())
                        .appendString(row.modelName())
                        .appendString(row.requiredFiles())
                        .appendString(row.modelVisitorsMax())
                        .appendInt(row.treeId())
                        .appendInt(row.recommendedId());
                }
            }
        }
        return PacketBuilder.create().appendInt(roomCount).appendRaw(payload.build()).build();
    }

    private static RoomCategoryCache roomCategoryCache() {
        return NavigatorState.instance().roomCategoryCache();
    }

    private static RoomDao roomDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new RoomDao(database);
    }
}
