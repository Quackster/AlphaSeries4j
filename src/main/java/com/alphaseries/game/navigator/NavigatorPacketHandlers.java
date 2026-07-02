package com.alphaseries.game.navigator;

import com.alphaseries.config.AppConfigState;
import com.alphaseries.dao.mysql.DaoProvider;
import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.server.runtime.SessionLookups;
import com.alphaseries.server.runtime.SocketDelivery;
import com.alphaseries.util.NumberUtils;

public final class NavigatorPacketHandlers {
    private NavigatorPacketHandlers() {
    }

    /**
     * Original function: Proc_6_60_720060.
     */
    public static void sendSingleRoomInfo(int socketIndex, NavigatorWire.SingleRoomRequest request) {
        try {
            long requestMode = request.requestMode();
            long detailFlag = request.detailFlag();
            if (detailFlag == 1L) {
                long roomId = request.roomId();
                if (roomId <= 0L) {
                    return;
                }
                String payload = NavigatorRequests.singleRoomResponsePayload(roomId, roomDao());
                if (!payload.isEmpty()) {
                    SocketDelivery.sendToSocket(socketIndex, payload);
                }
            } else if (requestMode > 0L) {
                // VB6 reads a room id from packed session offsets here; those offsets are not represented yet.
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Original function: Proc_6_88_73E4F0.
     */
    public static void sendNewFriendRoom(int socketIndex) {
        try {
            String payload = NavigatorRequests.newFriendRoomPayload(null, NavigatorState.instance(), roomDao());
            if (!payload.isEmpty()) {
                SocketDelivery.sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Original function: Proc_6_111_74DF70.
     */
    public static void sendRoomCategoryPayload(int socketIndex) {
        try {
            SocketDelivery.sendToSocket(socketIndex, roomCategoryCache().rankPayload(0L, 0L));
        } catch (Exception ignored) {
        }
    }

    /**
     * Original function: Proc_6_115_751220.
     */
    public static void sendEventCategoryRooms(int socketIndex, NavigatorWire.CategoryRequest request) {
        try {
            SocketDelivery.sendToSocket(socketIndex, NavigatorRequests.eventCategoryQueryPayload(
                request, AppConfigState.instance().settingsCache(), recommendedRooms(), roomDao()));
        } catch (Exception ignored) {
        }
    }

    /**
     * Original function: Proc_6_116_751550.
     */
    public static void sendPopularCategoryRooms(int socketIndex, NavigatorWire.CategoryRequest request) {
        try {
            SocketDelivery.sendToSocket(socketIndex, NavigatorRequests.popularCategoryQueryPayload(
                request, AppConfigState.instance().settingsCache(), recommendedRooms(), roomDao()));
        } catch (Exception ignored) {
        }
    }

    /**
     * Original function: Proc_6_117_751880.
     */
    public static void sendFriendCurrentRooms(int socketIndex) {
        try {
            SocketDelivery.sendToSocket(socketIndex, NavigatorRequests.friendCurrentQueryPayload(
                NumberUtils.parseLong(SessionLookups.userIdTextFromSocket(socketIndex)),
                NavigatorRequests.listLimit(AppConfigState.instance().settingsCache()), roomDao()));
        } catch (Exception ignored) {
        }
    }

    /**
     * Original function: Proc_6_118_751A80.
     */
    public static void sendFriendOwnedRooms(int socketIndex) {
        try {
            SocketDelivery.sendToSocket(socketIndex, NavigatorRequests.friendOwnedQueryPayload(
                NumberUtils.parseLong(SessionLookups.userIdTextFromSocket(socketIndex)),
                NavigatorRequests.listLimit(AppConfigState.instance().settingsCache()), roomDao()));
        } catch (Exception ignored) {
        }
    }

    /**
     * Original function: Proc_6_119_751C80.
     */
    public static void sendFavouriteRooms(int socketIndex) {
        try {
            SocketDelivery.sendToSocket(socketIndex, NavigatorRequests.favouriteQueryPayload(
                NumberUtils.parseLong(SessionLookups.userIdTextFromSocket(socketIndex)),
                NavigatorRequests.listLimit(AppConfigState.instance().settingsCache()), roomDao()));
        } catch (Exception ignored) {
        }
    }

    /**
     * Original function: Proc_6_120_751E80.
     */
    public static void sendRecentlyVisitedRooms(int socketIndex) {
        try {
            SocketDelivery.sendToSocket(socketIndex, NavigatorRequests.recentlyVisitedQueryPayload(
                NumberUtils.parseLong(SessionLookups.userIdTextFromSocket(socketIndex)),
                NavigatorRequests.listLimit(AppConfigState.instance().settingsCache()), roomDao()));
        } catch (Exception ignored) {
        }
    }

    /**
     * Original function: Proc_6_121_752080.
     */
    public static void sendOwnedRooms(int socketIndex) {
        try {
            SocketDelivery.sendToSocket(socketIndex, NavigatorRequests.ownedQueryPayload(
                NumberUtils.parseLong(SessionLookups.userIdTextFromSocket(socketIndex)),
                NavigatorRequests.listLimit(AppConfigState.instance().settingsCache()), roomDao()));
        } catch (Exception ignored) {
        }
    }

    /**
     * Original function: Proc_6_123_754020.
     */
    public static void sendOfficialNavigator(int socketIndex) {
        try {
            String payload = NavigatorRequests.officialNavigatorPayload(roomDao());
            if (!payload.isEmpty()) {
                SocketDelivery.sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Original function: Proc_6_124_754D90.
     */
    public static void sendPopularTags(int socketIndex) {
        try {
            String payload = NavigatorRequests.popularTagsPayload(AppConfigState.instance().settingsCache(), roomDao());
            if (!payload.isEmpty()) {
                SocketDelivery.sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Original function: Proc_6_125_755650.
     */
    public static void sendTagResults(int socketIndex, NavigatorWire.QueryRequest request) {
        try {
            SocketDelivery.sendToSocket(socketIndex, NavigatorRequests.tagResultsQueryPayload(
                request, AppConfigState.instance().settingsCache(), roomDao()));
        } catch (Exception ignored) {
        }
    }

    /**
     * Original function: Proc_6_126_755B40.
     */
    public static void sendTopRatedRooms(int socketIndex) {
        try {
            SocketDelivery.sendToSocket(socketIndex, NavigatorRequests.topRatedQueryPayload(AppConfigState.instance().settingsCache(), roomDao()));
        } catch (Exception ignored) {
        }
    }

    /**
     * Original function: Proc_6_127_755D30.
     */
    public static void sendSearchResults(int socketIndex, NavigatorWire.QueryRequest request) {
        try {
            SocketDelivery.sendToSocket(socketIndex, NavigatorRequests.searchResultsQueryPayload(
                request, AppConfigState.instance().settingsCache(), roomDao()));
        } catch (Exception ignored) {
        }
    }

    private static RecommendedRooms recommendedRooms() {
        return NavigatorState.instance().recommendedRooms();
    }

    private static RoomCategoryCache roomCategoryCache() {
        return NavigatorState.instance().roomCategoryCache();
    }

    private static RoomDao roomDao() {
        return DaoProvider.roomDao();
    }
}
