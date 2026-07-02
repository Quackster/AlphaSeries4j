package com.alphaseries.game.session;

import com.alphaseries.config.AppConfigState;
import com.alphaseries.dao.mysql.DaoProvider;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.game.social.SocialLookups;
import com.alphaseries.game.user.UserGroupRow;
import com.alphaseries.game.user.UserPacketHandlers;
import com.alphaseries.messages.outgoing.RoomPayloads;
import com.alphaseries.messages.outgoing.UserPayloads;
import com.alphaseries.protocol.WireEncoding;
import com.alphaseries.server.runtime.SocketDelivery;

import java.util.function.IntConsumer;

public final class SessionPacketHandlers {
    private SessionPacketHandlers() {
    }

    /**
     * Original function: Proc_6_163_7B3480.
     */
    public static String handleLoginTicket(
        int socketIndex,
        SessionWire.LoginTicketRequest request,
        IntConsumer disconnectSocket
    ) {
        try {
            String loginTicket = request.loginTicket();
            if (loginTicket.isEmpty() || "NULL".equalsIgnoreCase(loginTicket)) {
                disconnect(socketIndex, disconnectSocket);
                return "";
            }
            UserDao users = userDao();
            if (users == null) {
                return "";
            }
            UserDao.LoginUser loginUser = users.loginUser(loginTicket).orElse(null);
            if (loginUser == null) {
                disconnect(socketIndex, disconnectSocket);
                return "";
            }
            long userIdValue = loginUser.userId();
            String userId = String.valueOf(userIdValue);
            if (userIdValue == 0L) {
                disconnect(socketIndex, disconnectSocket);
                return "";
            }
            int oldSocketIndex = (int) loginUser.oldSocketIndex();
            if (oldSocketIndex > 0 && oldSocketIndex != socketIndex) {
                disconnect(oldSocketIndex, disconnectSocket);
            }
            String userName = loginUser.userName();
            long rankIndex = loginUser.rankIndex();
            long creditsValue = loginUser.credits();
            long homeRoomId = loginUser.homeRoomId();
            long updateAgeDays = loginUser.updateAgeDays();
            long emailValidated = loginUser.emailValidated();
            long[] pointValues = loginUser.activityPointValues();
            users.assignLoginSocket(userIdValue, socketIndex);
            if (updateAgeDays > 0L) {
                users.resetDailyInteractionCounters(userIdValue);
            }
            SessionState.instance().storeSocketSession(socketIndex, userId + '\2' + socketIndex + '\2' + userName + '\2'
                + rankIndex + '\2' + loginTicket + '\2');
            SocketDelivery.sendToSocket(socketIndex, "@C");
            UserPacketHandlers.sendRankAndStaffState(socketIndex);
            SocketDelivery.sendToSocket(socketIndex, UserPayloads.creditsRefresh(creditsValue));
            for (int pointIndex = 0; pointIndex <= 4; pointIndex++) {
                SocketDelivery.sendToSocket(socketIndex, UserPayloads.activityPointRefresh(pointIndex, pointValues[pointIndex]));
            }
            if (homeRoomId > 0L) {
                SocketDelivery.sendToSocket(socketIndex, RoomPayloads.homeRoom(homeRoomId));
            }
            if (emailValidated > 0L) {
                SocketDelivery.sendToSocket(socketIndex, UserPayloads.emailStatus(emailValidated));
            }
            SocketDelivery.sendToSocket(socketIndex, "@a" + "com.server.socket.location" + '\2' + "invalid.location" + '\2');
            if (AppConfigState.instance().settingsCache().longValueOrDefault("com.client.motd.message.enabled", 0) != 0L) {
                String motdMessage = AppConfigState.instance().settingsCache().valueOrDefault("com.client.motd.message", "").replace("\\n", "\n");
                if (!motdMessage.isEmpty()) {
                    SocketDelivery.sendToSocket(socketIndex, WireEncoding.encodeBase64Length(motdMessage.length())
                        + " " + motdMessage + '\2');
                }
            }
            SocketDelivery.sendToSocket(socketIndex, SocialLookups.badgeDisplayPayload(userIdValue, userDao()));
            SocketDelivery.sendToSocket(socketIndex, SocialLookups.tagDisplayPayload(userIdValue, userDao()));
            long favouriteGroupId = loginUser.favouriteGroupId();
            if (favouriteGroupId > 0L) {
                UserGroupRow groupRow = users.userGroup(favouriteGroupId).orElse(null);
                if (groupRow != null) {
                    String groupPayload = UserPayloads.loginGroup(favouriteGroupId, groupRow);
                    SocketDelivery.sendToSocket(socketIndex, groupPayload);
                }
            }
            return userId;
        } catch (Exception ignored) {
            disconnect(socketIndex, disconnectSocket);
            return "";
        }
    }

    private static void disconnect(int socketIndex, IntConsumer disconnectSocket) {
        if (socketIndex > 0 && disconnectSocket != null) {
            disconnectSocket.accept(socketIndex);
        }
    }

    private static UserDao userDao() {
        return DaoProvider.userDao();
    }
}
