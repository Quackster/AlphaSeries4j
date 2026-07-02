package com.alphaseries.game.trade;

import com.alphaseries.dao.mysql.DaoProvider;
import com.alphaseries.dao.mysql.FurnitureDao;
import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.dao.mysql.TradeDao;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.game.inventory.InventoryPacketHandlers;
import com.alphaseries.game.social.SocialWire;
import com.alphaseries.game.user.UserLookups;
import com.alphaseries.server.runtime.SessionLookups;
import com.alphaseries.server.runtime.SocketDelivery;
import com.alphaseries.util.NumberUtils;

public final class TradePacketHandlers {
    private TradePacketHandlers() {
    }

    /**
     * Original function: Proc_6_89_73EA10.
     */
    public static String confirmTrade(int socketIndex) {
        try {
            if (socketIndex <= 0) {
                return "";
            }
            TradeState tradeState = TradeState.instance();
            int targetSocketIndex = tradeState.interactionPartner(socketIndex);
            if (targetSocketIndex <= 0) {
                return "";
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            String targetUserId = SessionLookups.userIdTextFromSocket(targetSocketIndex);
            if (userId.isEmpty() || "0".equals(userId) || targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return "";
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            String sessionId = UserLookups.sessionId(NumberUtils.parseLong(userId), userDao());
            TradeConfirmation confirmation = TradeLookups.confirmTradeAction(
                socketIndex,
                NumberUtils.parseLong(userId),
                NumberUtils.parseLong(targetUserId),
                roomId,
                sessionId,
                tradeState,
                tradeDao());
            if (!confirmation.valid()) {
                return "";
            }
            SocketDelivery.sendToSocket(socketIndex, confirmation.payload());
            SocketDelivery.sendToSocket(targetSocketIndex, confirmation.payload());
            InventoryPacketHandlers.sendInventoryToSocket(socketIndex);
            InventoryPacketHandlers.sendInventoryToSocket(targetSocketIndex);
            return confirmation.payload();
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_90_742E80.
     */
    public static void sendInteractionState(int socketIndex, long suppliedTargetSocketIndex, Long suppliedInteractionState) {
        try {
            if (socketIndex <= 0) {
                return;
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long sourceRoomUserIndex = SessionLookups.representedRoomUserIndex(socketIndex, userId);
            if (sourceRoomUserIndex <= 0L) {
                return;
            }
            TradeInteractionStateAction action = TradeLookups.interactionStateAction(
                socketIndex,
                sourceRoomUserIndex,
                suppliedTargetSocketIndex,
                suppliedInteractionState != null,
                suppliedInteractionState == null ? 0L : suppliedInteractionState,
                TradeState.instance());
            if (!action.valid()) {
                return;
            }
            int targetSocketIndex = (int) action.targetSocketIndex();
            String targetUserId = SessionLookups.userIdTextFromSocket(targetSocketIndex);
            if (targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return;
            }
            SocketDelivery.sendToSocket(socketIndex, action.sourcePayload());
            SocketDelivery.sendToSocket(targetSocketIndex, action.targetPayload());
            if (!action.completionPayload().isEmpty()) {
                SocketDelivery.sendToSocket(socketIndex, action.completionPayload());
                SocketDelivery.sendToSocket(targetSocketIndex, action.completionPayload());
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_91_743480.
     */
    public static String addTradeFurniture(int socketIndex, TradeWire.FurnitureRequest request) {
        try {
            if (socketIndex <= 0) {
                return "";
            }
            TradeState tradeState = TradeState.instance();
            int targetSocketIndex = tradeState.interactionPartner(socketIndex);
            if (targetSocketIndex <= 0) {
                return "";
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            String targetUserId = SessionLookups.userIdTextFromSocket(targetSocketIndex);
            if (userId.isEmpty() || "0".equals(userId) || targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return "";
            }
            long furnitureId = request.furnitureId();
            if (furnitureId <= 0L) {
                return "";
            }
            TradeOfferAction action = TradeLookups.addOfferAction(
                socketIndex,
                targetSocketIndex,
                NumberUtils.parseLong(userId),
                NumberUtils.parseLong(targetUserId),
                furnitureId,
                tradeState,
                furnitureDao());
            if (!action.sourcePayload().isEmpty()) {
                SocketDelivery.sendToSocket(socketIndex, action.sourcePayload());
            }
            if (!action.targetPayload().isEmpty()) {
                SocketDelivery.sendToSocket(targetSocketIndex, action.targetPayload());
            }
            return action.sourcePayload();
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_92_744870.
     */
    public static String removeTradeFurniture(int socketIndex, TradeWire.FurnitureRequest request) {
        try {
            if (socketIndex <= 0) {
                return "";
            }
            TradeState tradeState = TradeState.instance();
            int targetSocketIndex = tradeState.interactionPartner(socketIndex);
            if (targetSocketIndex <= 0) {
                return "";
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            String targetUserId = SessionLookups.userIdTextFromSocket(targetSocketIndex);
            if (userId.isEmpty() || "0".equals(userId) || targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return "";
            }
            long furnitureId = request.furnitureId();
            if (furnitureId <= 0L) {
                return "";
            }
            TradeOfferAction action = TradeLookups.removeOfferAction(
                socketIndex,
                targetSocketIndex,
                NumberUtils.parseLong(userId),
                NumberUtils.parseLong(targetUserId),
                furnitureId,
                tradeState,
                furnitureDao());
            if (!action.sourcePayload().isEmpty()) {
                SocketDelivery.sendToSocket(socketIndex, action.sourcePayload());
            }
            if (!action.targetPayload().isEmpty()) {
                SocketDelivery.sendToSocket(targetSocketIndex, action.targetPayload());
            }
            return action.sourcePayload();
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_93_745D90.
     */
    public static void requestInteraction(int socketIndex, SocialWire.RoomUserIndexRequest request) {
        try {
            if (socketIndex <= 0) {
                return;
            }
            long requestedRoomUserIndex = request.roomUserIndex();
            if (requestedRoomUserIndex <= 0L) {
                return;
            }
            String callerUserId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long callerRoomId = SessionLookups.currentRoomId(socketIndex, callerUserId);
            if (callerRoomId <= 0L) {
                return;
            }
            TradeInteractionRequestAction action = TradeLookups.requestInteractionAction(
                socketIndex,
                NumberUtils.parseLong(callerUserId),
                callerRoomId,
                requestedRoomUserIndex,
                roomDao(),
                TradeState.instance(),
                userId -> SessionLookups.socketFromUserIdText(String.valueOf(userId)));
            if (!action.valid()) {
                return;
            }
            SocketDelivery.sendToSocket(socketIndex, action.sourcePayload());
            SocketDelivery.sendToSocket((int) action.targetSocketIndex(), action.targetPayload());
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_94_746990.
     */
    public static void closeInteraction(int socketIndex, int suppliedTargetSocketIndex) {
        try {
            if (socketIndex <= 0) {
                return;
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long sourceRoomUserIndex = SessionLookups.representedRoomUserIndex(socketIndex, userId);
            if (sourceRoomUserIndex <= 0L) {
                return;
            }
            int targetSocketIndex = suppliedTargetSocketIndex;
            if (targetSocketIndex <= 0) {
                targetSocketIndex = TradeState.instance().interactionPartner(socketIndex);
            }
            if (targetSocketIndex <= 0) {
                return;
            }
            String targetUserId = SessionLookups.userIdTextFromSocket(targetSocketIndex);
            if (targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return;
            }
            long targetRoomUserIndex = SessionLookups.representedRoomUserIndex(targetSocketIndex, targetUserId);
            if (targetRoomUserIndex <= 0L) {
                return;
            }
            TradeInteractionCloseAction action = TradeLookups.closeInteractionAction(
                socketIndex, targetSocketIndex, sourceRoomUserIndex, targetRoomUserIndex, TradeState.instance());
            if (!action.valid()) {
                return;
            }
            SocketDelivery.sendToSocket(socketIndex, action.payload());
            SocketDelivery.sendToSocket((int) action.targetSocketIndex(), action.payload());
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    private static FurnitureDao furnitureDao() {
        return DaoProvider.furnitureDao();
    }

    private static RoomDao roomDao() {
        return DaoProvider.roomDao();
    }

    private static TradeDao tradeDao() {
        return DaoProvider.tradeDao();
    }

    private static UserDao userDao() {
        return DaoProvider.userDao();
    }
}
