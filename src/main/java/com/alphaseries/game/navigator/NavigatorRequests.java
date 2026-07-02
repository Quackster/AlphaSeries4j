package com.alphaseries.game.navigator;

import com.alphaseries.config.AppSettingsCache;
import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.messages.outgoing.NavigatorPayloads;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.RandomUtils;
import com.alphaseries.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

public final class NavigatorRequests {
    private static final String LIST_LIMIT_SETTING = "com.client.navigator.list.limit";
    private static final long DEFAULT_LIST_LIMIT = 50L;

    private NavigatorRequests() {
    }

    public static long listLimit(AppSettingsCache settingsCache) {
        long limit = settingsCache == null
            ? DEFAULT_LIST_LIMIT
            : settingsCache.longValueOrDefault(LIST_LIMIT_SETTING, DEFAULT_LIST_LIMIT);
        return limit <= 0L ? DEFAULT_LIST_LIMIT : limit;
    }

    public static String searchTerm(String rawText) {
        return StringUtils.sqlEscapedText(rawText).replace("%", "");
    }

    public static String searchParameter(String rawText) {
        return StringUtils.text(rawText).replace("%", "");
    }

    public static String eventCategoryQueryPayload(
        String packetPayload,
        AppSettingsCache settingsCache,
        RecommendedRooms recommendedRooms,
        RoomDao rooms
    ) {
        long recommendedTree = randomRecommendedTree(recommendedRooms);
        return eventCategoryQueryPayload(
            NavigatorWire.categoryId(packetPayload),
            listLimit(settingsCache),
            recommendedTree,
            rooms,
            recommendedRooms);
    }

    public static String popularCategoryQueryPayload(
        String packetPayload,
        AppSettingsCache settingsCache,
        RecommendedRooms recommendedRooms,
        RoomDao rooms
    ) {
        long recommendedTree = randomRecommendedTree(recommendedRooms);
        return popularCategoryQueryPayload(
            NavigatorWire.categoryId(packetPayload),
            listLimit(settingsCache),
            recommendedTree,
            rooms,
            recommendedRooms);
    }

    /**
     * Original function: Proc_6_115_751220.
     */
    public static String eventCategoryQueryPayload(
        long categoryId,
        long limit,
        long recommendedTree,
        RoomDao rooms,
        RecommendedRooms recommendedRooms
    ) {
        try {
            return NavigatorPayloads.queryResultWithRecommended(
                "GCPC",
                String.valueOf(categoryId),
                limit,
                rooms == null ? List.of() : rooms.eventCategoryNavigatorRooms(categoryId, limit),
                recommendedRooms,
                recommendedTree);
        } catch (Exception ignored) {
            return NavigatorPayloads.queryResultWithRecommended(
                "GCPC",
                String.valueOf(categoryId),
                limit,
                List.of(),
                recommendedRooms,
                recommendedTree);
        }
    }

    /**
     * Original function: Proc_6_116_751550.
     */
    public static String popularCategoryQueryPayload(
        long categoryId,
        long limit,
        long recommendedTree,
        RoomDao rooms,
        RecommendedRooms recommendedRooms
    ) {
        try {
            return NavigatorPayloads.queryResultWithRecommended(
                "GC ",
                String.valueOf(categoryId),
                limit,
                rooms == null ? List.of() : rooms.popularNavigatorRooms(categoryId, limit),
                recommendedRooms,
                recommendedTree);
        } catch (Exception ignored) {
            return NavigatorPayloads.queryResultWithRecommended(
                "GC ",
                String.valueOf(categoryId),
                limit,
                List.of(),
                recommendedRooms,
                recommendedTree);
        }
    }

    /**
     * Original function: Proc_6_117_751880.
     */
    public static String friendCurrentQueryPayload(long userId, long limit, RoomDao rooms) {
        try {
            return roomQueryPayload("GCQA", "", limit,
                rooms == null ? List.of() : rooms.friendCurrentNavigatorRooms(userId, limit));
        } catch (Exception ignored) {
            return roomQueryPayload("GCQA", "", limit, List.of());
        }
    }

    public static String friendCurrentQueryPayload(String userId, AppSettingsCache settingsCache, RoomDao rooms) {
        return friendCurrentQueryPayload(NumberUtils.parseLong(userId), listLimit(settingsCache), rooms);
    }

    /**
     * Original function: Proc_6_118_751A80.
     */
    public static String friendOwnedQueryPayload(long userId, long limit, RoomDao rooms) {
        try {
            return roomQueryPayload("GC", "\0", limit,
                rooms == null ? List.of() : rooms.friendOwnedNavigatorRooms(userId, limit));
        } catch (Exception ignored) {
            return roomQueryPayload("GC", "\0", limit, List.of());
        }
    }

    public static String friendOwnedQueryPayload(String userId, AppSettingsCache settingsCache, RoomDao rooms) {
        return friendOwnedQueryPayload(NumberUtils.parseLong(userId), listLimit(settingsCache), rooms);
    }

    /**
     * Original function: Proc_6_119_751C80.
     */
    public static String favouriteQueryPayload(long userId, long limit, RoomDao rooms) {
        try {
            return roomQueryPayload("GCRA", "", limit,
                rooms == null ? List.of() : rooms.favouriteNavigatorRooms(userId, limit));
        } catch (Exception ignored) {
            return roomQueryPayload("GCRA", "", limit, List.of());
        }
    }

    public static String favouriteQueryPayload(String userId, AppSettingsCache settingsCache, RoomDao rooms) {
        return favouriteQueryPayload(NumberUtils.parseLong(userId), listLimit(settingsCache), rooms);
    }

    /**
     * Original function: Proc_6_120_751E80.
     */
    public static String recentlyVisitedQueryPayload(long userId, long limit, RoomDao rooms) {
        try {
            return roomQueryPayload("GCSA", "", limit,
                rooms == null ? List.of() : rooms.recentlyVisitedNavigatorRooms(userId, limit));
        } catch (Exception ignored) {
            return roomQueryPayload("GCSA", "", limit, List.of());
        }
    }

    public static String recentlyVisitedQueryPayload(String userId, AppSettingsCache settingsCache, RoomDao rooms) {
        return recentlyVisitedQueryPayload(NumberUtils.parseLong(userId), listLimit(settingsCache), rooms);
    }

    /**
     * Original function: Proc_6_121_752080.
     */
    public static String ownedQueryPayload(long userId, long limit, RoomDao rooms) {
        try {
            return roomQueryPayload("GCQA", "", limit,
                rooms == null ? List.of() : rooms.ownedNavigatorRooms(userId, limit));
        } catch (Exception ignored) {
            return roomQueryPayload("GCQA", "", limit, List.of());
        }
    }

    public static String ownedQueryPayload(String userId, AppSettingsCache settingsCache, RoomDao rooms) {
        return ownedQueryPayload(NumberUtils.parseLong(userId), listLimit(settingsCache), rooms);
    }

    /**
     * Original function: Proc_6_126_755B40.
     */
    public static String topRatedQueryPayload(long limit, RoomDao rooms) {
        try {
            return roomQueryPayload("GC", "\b", limit,
                rooms == null ? List.of() : rooms.topRatedNavigatorRooms(limit));
        } catch (Exception ignored) {
            return roomQueryPayload("GC", "\b", limit, List.of());
        }
    }

    public static String topRatedQueryPayload(AppSettingsCache settingsCache, RoomDao rooms) {
        return topRatedQueryPayload(listLimit(settingsCache), rooms);
    }

    /**
     * Original function: Proc_6_123_754020.
     */
    public static String officialNavigatorPayload(RoomDao rooms) {
        try {
            return rooms == null ? "" : "GB" + NavigatorPayloads.official(rooms.officialNavigatorItems(), true);
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_124_754D90.
     */
    public static String popularTagsPayload(long limit, RoomDao rooms) {
        try {
            return rooms == null ? "" : "GD" + NavigatorPayloads.tagPopularity(rooms.navigatorTagPopularities(limit));
        } catch (Exception ignored) {
            return "";
        }
    }

    public static String popularTagsPayload(AppSettingsCache settingsCache, RoomDao rooms) {
        return popularTagsPayload(listLimit(settingsCache), rooms);
    }

    /**
     * Original function: Proc_6_88_73E4F0.
     */
    public static String newFriendRoomPayload(LocalDateTime now, NavigatorState state, RoomDao rooms) {
        try {
            NavigatorState navigatorState = state == null ? NavigatorState.instance() : state;
            LocalDateTime currentTime = now == null ? LocalDateTime.now() : now;
            if (navigatorState.newFriendRooms().shouldRefresh(currentTime)) {
                if (rooms == null) {
                    return "";
                }
                navigatorState.setNewFriendRooms(rooms.newFriendRoomPicks(), currentTime.plusSeconds(90L));
            }
            return NavigatorPayloads.newFriendRoom(navigatorState.newFriendRooms().randomRoom());
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_60_720060.
     */
    public static String singleRoomResponsePayload(long roomId, RoomDao rooms) {
        try {
            if (rooms == null || roomId <= 0L) {
                return "";
            }
            return NavigatorPayloads.singleRoomResponse(rooms.navigatorRoom(roomId).orElse(null));
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_112_74E0C0.
     */
    public static String roomListPayload(long roomId, RoomDao rooms) {
        try {
            if (rooms == null || roomId <= 0L) {
                return NavigatorPayloads.roomList(List.of());
            }
            return NavigatorPayloads.roomList(
                rooms.navigatorRoom(roomId).<List<NavigatorRoom>>map(value -> List.of(value)).orElseGet(List::of));
        } catch (Exception ignored) {
            return NavigatorPayloads.roomList(List.of());
        }
    }

    /**
     * Original function: Proc_6_125_755650.
     */
    public static String tagResultsQueryPayload(String rawTagText, String timeFormat, long limit, RoomDao rooms) {
        String tagText = StringUtils.sqlEscapedText(rawTagText);
        try {
            return combinedRoomQueryPayload("GCSA", tagText, limit,
                rooms == null ? List.of() : rooms.navigatorTagEvents(rawTagText, timeFormat, limit),
                rooms == null ? List.of() : rooms.navigatorTagRooms(rawTagText, limit));
        } catch (Exception ignored) {
            return combinedRoomQueryPayload("GCSA", tagText, limit, List.of(), List.of());
        }
    }

    public static String tagResultsQueryPayload(String packetPayload, AppSettingsCache settingsCache, RoomDao rooms) {
        return tagResultsQueryPayload(
            NavigatorWire.queryText(packetPayload),
            timeFormat(settingsCache),
            listLimit(settingsCache),
            rooms);
    }

    /**
     * Original function: Proc_6_127_755D30.
     */
    public static String searchResultsQueryPayload(String rawSearchText, String timeFormat, long limit, RoomDao rooms) {
        String searchText = searchTerm(rawSearchText);
        String databaseSearchText = searchParameter(rawSearchText);
        try {
            return combinedRoomQueryPayload("GCSA", searchText, limit,
                rooms == null ? List.of() : rooms.navigatorSearchEvents(databaseSearchText, timeFormat, limit),
                rooms == null ? List.of() : rooms.navigatorSearchRooms(databaseSearchText, limit));
        } catch (Exception ignored) {
            return combinedRoomQueryPayload("GCSA", searchText, limit, List.of(), List.of());
        }
    }

    public static String searchResultsQueryPayload(String packetPayload, AppSettingsCache settingsCache, RoomDao rooms) {
        return searchResultsQueryPayload(
            NavigatorWire.queryText(packetPayload),
            timeFormat(settingsCache),
            listLimit(settingsCache),
            rooms);
    }

    private static String timeFormat(AppSettingsCache settingsCache) {
        return settingsCache == null ? "%H:%i" : settingsCache.valueOrDefault("com.mysql.format.time", "%H:%i");
    }

    private static long randomRecommendedTree(RecommendedRooms recommendedRooms) {
        return recommendedRooms == null || recommendedRooms.count() <= 0L
            ? 0L
            : RandomUtils.longInclusive(1, recommendedRooms.count());
    }

    private static String roomQueryPayload(String queryCode, String queryParameter, long limit, List<NavigatorRoom> roomRows) {
        return NavigatorPayloads.queryResult(queryCode, queryParameter, limit, roomRows);
    }

    private static String combinedRoomQueryPayload(
        String queryCode,
        String queryParameter,
        long limit,
        List<RoomDao.NavigatorEventRow> eventRows,
        List<NavigatorRoom> roomRows
    ) {
        return NavigatorPayloads.combinedQueryResult(queryCode, queryParameter, limit, eventRows, roomRows);
    }
}
