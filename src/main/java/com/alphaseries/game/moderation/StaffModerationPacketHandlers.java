package com.alphaseries.game.moderation;

import com.alphaseries.config.AppConfigState;
import com.alphaseries.dao.mysql.DaoProvider;
import com.alphaseries.dao.mysql.StaffModerationDao;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.game.room.RoomRefreshService;
import com.alphaseries.game.user.UserLookups;
import com.alphaseries.server.mus.MusConnectionManager;
import com.alphaseries.server.runtime.SessionLookups;
import com.alphaseries.server.runtime.SocketDelivery;
import com.alphaseries.util.NumberUtils;

import java.util.List;
import java.util.function.IntConsumer;

public final class StaffModerationPacketHandlers {
    private StaffModerationPacketHandlers() {
    }

    /**
     * Original function: Proc_6_0_6D7FF0.
     */
    public static void sendUserSummary(int socketIndex, StaffWire.UserSummaryRequest request) {
        try {
            long targetUserId = request.targetUserId();
            if (targetUserId <= 0L) {
                return;
            }
            long callerUserId = userIdFromSocket(socketIndex);
            if (callerUserId <= 0L || !userHasPermission(callerUserId, "fuse_mod")) {
                return;
            }
            StaffModerationDao moderationDao = staffModerationDao();
            if (moderationDao == null) {
                return;
            }
            StaffModerationDao.UserModerationSummary summary = moderationDao.userModerationSummary(targetUserId).orElse(null);
            if (summary == null) {
                return;
            }
            String payload = StaffPayloads.userSummary(summary.userRow(),
                summary.callForHelpCount(),
                summary.pickedCallForHelpCount(),
                summary.cautionCount(),
                summary.banCount());
            if (!payload.isEmpty()) {
                SocketDelivery.sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_1_6D8B70.
     */
    public static void sendCaution(int socketIndex, StaffWire.DirectMessageRequest request) {
        staffDirectMessage(socketIndex, request, "fuse_alert", "4", false, false);
    }

    /**
     * Original function: Proc_6_2_6D9880.
     */
    public static void kickUser(int socketIndex, StaffWire.DirectMessageRequest request) {
        staffDirectMessage(socketIndex, request, "fuse_kick", "5", true, false);
    }

    /**
     * Original function: Proc_6_3_6DA490.
     */
    public static void banUser(int socketIndex, StaffWire.BanRequest request, IntConsumer disconnectSocket) {
        try {
            long targetUserId = request.targetUserId();
            String banMessage = request.banMessage();
            long banHours = request.banHours();
            long callerUserId = userIdFromSocket(socketIndex);
            if (targetUserId <= 0L || banMessage.isEmpty() || banHours <= 0L
                || callerUserId <= 0L
                || !userHasPermission(callerUserId, "fuse_mod")
                || !userHasPermission(callerUserId, "fuse_alert")
                || StaffPayloads.containsUnsafeAlert(banMessage)) {
                return;
            }
            long currentRoomId = SessionLookups.currentRoomId(socketIndex, String.valueOf(callerUserId));
            long banSeconds = banHours * 60L * 60L;
            StaffModerationDao moderationDao = staffModerationDao();
            if (moderationDao != null) {
                String targetIpAddress = moderationDao.userLastIpAddress(targetUserId);
                moderationDao.insertModerationBanLog(callerUserId, targetUserId, currentRoomId, banMessage, socketIndex);
                moderationDao.insertUserBan(targetUserId, callerUserId, banMessage, banSeconds, targetIpAddress);
                moderationDao.clearUserLoginSession(targetUserId);
            }
            int targetSocketIndex = SessionLookups.socketFromUserIdText(String.valueOf(targetUserId));
            if (targetSocketIndex > 0) {
                SocketDelivery.sendToSocket(targetSocketIndex, "@c" + banMessage + '\2');
                if (disconnectSocket != null) {
                    disconnectSocket.accept(targetSocketIndex);
                }
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_4_6DAFB0.
     */
    public static long moderateCurrentRoom(int socketIndex, StaffWire.RoomModerationRequest request) {
        try {
            long actionType = request.actionType();
            String messageText = request.messageText();
            long callerUserId = userIdFromSocket(socketIndex);
            if (callerUserId <= 0L || !userHasPermission(callerUserId, "fuse_mod")) {
                return 0L;
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, String.valueOf(callerUserId));
            if (roomId <= 0L || actionType <= 0L || messageText.isEmpty() || StaffPayloads.containsUnsafeAlert(messageText)) {
                return 0L;
            }
            StaffModerationDao moderationDao = staffModerationDao();
            if (moderationDao == null) {
                return 0L;
            }
            StaffModerationDao.RoomModerationTarget moderationTarget = moderationDao.roomModerationTarget(roomId).orElse(null);
            if (moderationTarget == null || moderationTarget.ownerUserId() <= 0L) {
                return 0L;
            }
            long logType = actionType == 1L ? 1L : 2L;
            moderationDao.insertRoomModerationLog(logType, callerUserId, roomId, messageText, socketIndex);
            SocketDelivery.broadcastToRoomUsers(roomId, StaffPayloads.alert(messageText));
            if (actionType == 1L || actionType == 4L) {
                moderationDao.deleteRoomEvent(roomId);
                RoomRefreshService.sendRoomReadyRefreshes(roomId);
            }
            if (actionType == 1L) {
                moderationDao.insertUserCaution(
                    moderationTarget.ownerUserId(),
                    callerUserId,
                    messageText + " (Room caution of room id: " + roomId + ")");
            }
            return actionType;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_6_6DC9D0.
     */
    public static void moveCallForHelpToPickedTab(int socketIndex, StaffWire.CallForHelpTabRequest request) {
        updateCallForHelpTab(socketIndex, request, "2");
    }

    /**
     * Original function: Proc_6_7_6DD0E0.
     */
    public static void closeCallForHelp(int socketIndex, StaffWire.CloseCallForHelpRequest request) {
        try {
            long callerUserId = userIdFromSocket(socketIndex);
            if (callerUserId <= 0L
                || !userHasPermission(callerUserId, "fuse_mod")
                || !userHasPermission(callerUserId, "fuse_receive_calls_for_help")) {
                return;
            }
            long closeState = request.closeState();
            if (closeState < 1L || closeState > 3L) {
                return;
            }
            long callForHelpId = request.callForHelpId();
            if (callForHelpId <= 0L) {
                return;
            }
            StaffModerationDao moderationDao = staffModerationDao();
            if (moderationDao == null) {
                return;
            }
            String reporterUserId = String.valueOf(moderationDao.callForHelpReporterUserId(callForHelpId));
            int reporterSocketIndex = SessionLookups.socketFromUserIdText(reporterUserId);
            if (reporterSocketIndex > 0) {
                SocketDelivery.sendToSocket(reporterSocketIndex, StaffPayloads.callForHelpClosed(closeState));
            }
            moderationDao.closeCallForHelp(callForHelpId, closeState);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_8_6DD790.
     */
    public static void moveCallForHelpToOpenTab(int socketIndex, StaffWire.CallForHelpTabRequest request) {
        updateCallForHelpTab(socketIndex, request, "1");
    }

    /**
     * Original function: Proc_6_9_6DDD70.
     */
    public static void lockCurrentRoomForModeration(int socketIndex, StaffWire.RoomLockRequest request) {
        try {
            long callerUserId = userIdFromSocket(socketIndex);
            if (callerUserId <= 0L || !userHasPermission(callerUserId, "fuse_mod")) {
                return;
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, String.valueOf(callerUserId));
            long lockFlag = request.lockFlag();
            if (roomId > 0L && lockFlag == 1L) {
                StaffModerationDao moderationDao = staffModerationDao();
                if (moderationDao != null) {
                    moderationDao.lockRoomForModeration(roomId);
                }
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_10_6DE1D0.
     */
    public static void sendRoomChatHistory(int socketIndex, StaffWire.HistoryRequest request) {
        staffRoomHistory(socketIndex, request, true);
    }

    /**
     * Original function: Proc_6_11_6DF4A0.
     */
    public static void sendRoomVisitHistory(int socketIndex, StaffWire.HistoryRequest request) {
        staffRoomHistory(socketIndex, request, false);
    }

    /**
     * Original function: Proc_6_12_6DFE90.
     */
    public static void sendAlert(int socketIndex, StaffWire.DirectMessageRequest request) {
        staffDirectMessage(socketIndex, request, "fuse_alert", "3", false, true);
    }

    /**
     * Original function: Proc_6_30_70DC90.
     */
    public static void cancelLatestCallForHelp(int socketIndex) {
        try {
            long userId = userIdFromSocket(socketIndex);
            if (userId <= 0L) {
                return;
            }
            StaffModerationDao moderationDao = staffModerationDao();
            if (moderationDao == null) {
                return;
            }
            long callForHelpId = moderationDao.latestOpenCallForHelpId(userId);
            if (callForHelpId > 0L) {
                moderationDao.deleteCallForHelp(callForHelpId);
                SocketDelivery.sendToSocket(socketIndex, StaffPayloads.callForHelpDeleted());
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_32_70EAB0.
     */
    public static void submitCallForHelp(int socketIndex, StaffWire.SubmitCallForHelpRequest request) {
        try {
            long userId = userIdFromSocket(socketIndex);
            if (userId <= 0L) {
                return;
            }
            StaffModerationDao moderationDao = staffModerationDao();
            if (moderationDao == null) {
                return;
            }
            java.util.Optional<Long> lastClosedState = moderationDao.recentCallForHelpClosedState(userId);
            if (lastClosedState.isPresent() && lastClosedState.get() == 0L) {
                return;
            }
            String descriptionText = request.descriptionText();
            if (descriptionText.length() < 30) {
                return;
            }
            long categoryId = request.categoryId();
            long partnerUserId = request.partnerUserId();
            if (partnerUserId == userId) {
                partnerUserId = 0L;
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, String.valueOf(userId));
            if (roomId <= 0L) {
                return;
            }
            moderationDao.insertCallForHelp(userId, roomId, categoryId, partnerUserId, descriptionText);
            long callForHelpId = moderationDao.newestCallForHelpId();
            SocketDelivery.sendToSocket(socketIndex, StaffPayloads.callForHelpCreated(callForHelpId));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void sendCallForHelpChatLog(int socketIndex, StaffWire.CallForHelpChatLogRequest request) {
        try {
            long userId = userIdFromSocket(socketIndex);
            if (userId <= 0L
                || !userHasPermission(userId, "fuse_mod")
                || !userHasPermission(userId, "fuse_receive_calls_for_help")) {
                return;
            }
            long callForHelpId = request.callForHelpId();
            if (callForHelpId <= 0L) {
                return;
            }
            StaffModerationDao moderationDao = staffModerationDao();
            StaffModerationDao.CallForHelpRoom room = moderationDao.callForHelpRoom(callForHelpId).orElse(null);
            if (room == null) {
                return;
            }
            List<StaffRoomChatRow> chatRows = moderationDao.recentChatRowsBefore(room.roomId(), room.timestampSent());
            MusConnectionManager.instance().sendData(
                socketIndex,
                StaffPayloads.callForHelpChatLogResponse(callForHelpId, room, chatRows));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void sendRoomChatLog(int socketIndex, StaffWire.RoomChatLogRequest request) {
        try {
            long userId = userIdFromSocket(socketIndex);
            if (userId <= 0L
                || !userHasPermission(userId, "fuse_mod")
                || !userHasPermission(userId, "fuse_chatlog")) {
                return;
            }
            long roomId = request.roomId();
            if (roomId <= 0L) {
                return;
            }
            StaffModerationDao moderationDao = staffModerationDao();
            StaffModerationDao.RoomChatHeader room = moderationDao.roomChatHeader(roomId).orElse(null);
            if (room == null) {
                return;
            }
            List<StaffRoomChatRow> chatRows = moderationDao.recentChatRows(roomId);
            MusConnectionManager.instance().sendData(socketIndex, StaffPayloads.roomChatLogResponse(room, chatRows));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void sendRoomInfo(int socketIndex, StaffWire.RoomInfoRequest request) {
        try {
            long userId = userIdFromSocket(socketIndex);
            if (userId <= 0L || !userHasPermission(userId, "fuse_mod")) {
                return;
            }
            long roomId = request.roomId();
            if (roomId <= 0L) {
                return;
            }
            StaffModerationDao moderationDao = staffModerationDao();
            StaffModerationDao.RoomInfo room = moderationDao.roomInfo(roomId).orElse(null);
            if (room == null) {
                return;
            }
            StaffModerationDao.RoomEvent event = moderationDao.roomEvent(roomId).orElse(null);
            MusConnectionManager.instance().sendData(socketIndex, StaffPayloads.roomInfoResponse(room, event));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    private static void staffDirectMessage(
        int socketIndex,
        StaffWire.DirectMessageRequest request,
        String requiredPermission,
        String logType,
        boolean kickAfterSend,
        boolean requireOnlineTarget
    ) {
        try {
            long targetUserId = request.targetUserId();
            String messageText = request.messageText();
            long callerUserId = userIdFromSocket(socketIndex);
            if (targetUserId <= 0L || messageText.isEmpty()
                || callerUserId <= 0L
                || !userHasPermission(callerUserId, "fuse_mod")
                || !userHasPermission(callerUserId, requiredPermission)
                || StaffPayloads.containsUnsafeAlert(messageText)) {
                return;
            }
            StaffModerationDao moderationDao = staffModerationDao();
            if (moderationDao == null) {
                return;
            }
            int targetSocketIndex = SessionLookups.socketFromUserIdText(String.valueOf(targetUserId));
            if (requireOnlineTarget && targetSocketIndex <= 0) {
                return;
            }
            long currentRoomId = SessionLookups.currentRoomId(socketIndex, String.valueOf(callerUserId));
            moderationDao.insertDirectModerationLog(
                NumberUtils.parseLong(logType),
                callerUserId,
                targetUserId,
                currentRoomId,
                messageText,
                socketIndex);
            if (targetSocketIndex > 0) {
                SocketDelivery.sendToSocket(targetSocketIndex, StaffPayloads.alert(messageText));
                if (kickAfterSend) {
                    RoomRefreshService.sendRoomReady(targetSocketIndex);
                }
            }
            if ("4".equals(logType)) {
                moderationDao.insertUserCaution(targetUserId, callerUserId, messageText);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    private static void updateCallForHelpTab(int socketIndex, StaffWire.CallForHelpTabRequest request, String tabId) {
        try {
            long callerUserId = userIdFromSocket(socketIndex);
            if (callerUserId <= 0L
                || !userHasPermission(callerUserId, "fuse_mod")
                || !userHasPermission(callerUserId, "fuse_receive_calls_for_help")) {
                return;
            }
            List<Long> callForHelpIds = request.callForHelpIds();
            if (callForHelpIds.isEmpty()) {
                return;
            }
            StaffModerationDao moderationDao = staffModerationDao();
            if (moderationDao == null) {
                return;
            }
            moderationDao.moveCallForHelpToTab(callForHelpIds, NumberUtils.parseLong(tabId), callerUserId);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    private static void staffRoomHistory(int socketIndex, StaffWire.HistoryRequest request, boolean includeChatRows) {
        try {
            long callerUserId = userIdFromSocket(socketIndex);
            if (callerUserId <= 0L || !userHasPermission(callerUserId, "fuse_mod")) {
                return;
            }
            if (includeChatRows && !userHasPermission(callerUserId, "fuse_chatlog")) {
                return;
            }
            long targetUserId = request.targetUserId();
            if (targetUserId <= 0L) {
                return;
            }
            StaffModerationDao moderationDao = staffModerationDao();
            if (moderationDao == null) {
                return;
            }
            StaffUserLookup targetUser = moderationDao.staffUserLookup(targetUserId).orElse(null);
            if (targetUser == null) {
                return;
            }
            targetUserId = targetUser.userId();
            if (targetUserId <= 0L) {
                return;
            }
            String responsePayload;
            if (includeChatRows) {
                responsePayload = StaffModerationLookups.roomChatHistoryResponse(targetUser, moderationDao);
            } else {
                responsePayload = StaffModerationLookups.roomVisitHistoryResponse(targetUser, moderationDao);
            }
            if (!responsePayload.isEmpty()) {
                SocketDelivery.sendToSocket(socketIndex, responsePayload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    private static StaffModerationDao staffModerationDao() {
        return DaoProvider.staffModerationDao();
    }

    private static UserDao userDao() {
        return DaoProvider.userDao();
    }

    private static long userIdFromSocket(int socketIndex) {
        try {
            return NumberUtils.parseLong(SessionLookups.userIdTextFromSocket(socketIndex));
        } catch (Exception ignored) {
            return 0L;
        }
    }

    private static boolean userHasPermission(long userId, String permissionName) {
        UserDao users = userDao();
        if (users == null) {
            return false;
        }
        try {
            return UserLookups.hasPermission(userId, permissionName, users, AppConfigState.instance().permissionMatrix());
        } catch (Exception ignored) {
            return false;
        }
    }
}
