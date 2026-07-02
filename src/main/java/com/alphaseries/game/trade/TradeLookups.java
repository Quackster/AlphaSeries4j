package com.alphaseries.game.trade;

import com.alphaseries.dao.mysql.FurnitureDao;
import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.dao.mysql.TradeDao;
import com.alphaseries.game.room.RoomLookups;
import com.alphaseries.game.room.RoomUserTargetRow;
import com.alphaseries.game.social.InteractionStatePayloads;
import com.alphaseries.game.social.SocialLookups;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.List;
import java.util.function.LongToIntFunction;

public final class TradeLookups {
    private TradeLookups() {
    }

    /**
     * Original function: Proc_6_93_745D90.
     */
    public static TradeInteractionRequestAction requestInteractionAction(
        long socketIndex,
        String callerUserId,
        long callerRoomId,
        long requestedRoomUserIndex,
        RoomDao rooms,
        TradeState tradeState,
        LongToIntFunction socketByUserId
    )
        throws Exception {

        if (socketIndex <= 0L || callerRoomId <= 0L || requestedRoomUserIndex <= 0L
            || StringUtils.text(callerUserId).isEmpty() || "0".equals(StringUtils.text(callerUserId))
            || rooms == null || tradeState == null) {
            return emptyInteractionRequestAction();
        }
        RoomUserTargetRow target = RoomLookups.activeRoomUserTarget(
            callerRoomId, requestedRoomUserIndex, rooms).orElse(null);
        if (target == null || target.roomUserIndex() <= 0L || target.userId() <= 0L) {
            return emptyInteractionRequestAction();
        }
        long targetUserId = target.userId();
        int targetSocketIndex = (int) target.socketIndex();
        if (targetSocketIndex <= 0 && socketByUserId != null) {
            targetSocketIndex = socketByUserId.applyAsInt(targetUserId);
        }
        if (targetSocketIndex <= 0 || targetSocketIndex == socketIndex
            || tradeState.interactionPartner(targetSocketIndex) > 0) {
            return emptyInteractionRequestAction();
        }
        long numericCallerUserId = NumberUtils.parseLong(callerUserId);
        if (numericCallerUserId <= 0L) {
            return emptyInteractionRequestAction();
        }
        tradeState.storeInteractionPair(socketIndex, targetSocketIndex, 1L);
        return new TradeInteractionRequestAction(
            targetSocketIndex,
            SocialLookups.interactionRequestPayload(numericCallerUserId, targetUserId),
            SocialLookups.interactionRequestPayload(targetUserId, numericCallerUserId));
    }

    /**
     * Original function: Proc_6_90_742E80.
     */
    public static TradeInteractionStateAction interactionStateAction(
        long socketIndex,
        long sourceRoomUserIndex,
        long suppliedTargetSocketIndex,
        boolean hasSuppliedInteractionState,
        long suppliedInteractionState,
        TradeState tradeState
    ) {
        if (socketIndex <= 0L || sourceRoomUserIndex <= 0L || tradeState == null) {
            return emptyInteractionStateAction();
        }
        long targetSocketIndex = suppliedTargetSocketIndex;
        if (targetSocketIndex <= 0L) {
            targetSocketIndex = tradeState.interactionPartner(socketIndex);
        }
        if (targetSocketIndex <= 0L) {
            return emptyInteractionStateAction();
        }
        long interactionState = hasSuppliedInteractionState
            ? suppliedInteractionState
            : tradeState.interactionState(socketIndex);
        InteractionStatePayloads payloads = SocialLookups.interactionStatePayloads(
            sourceRoomUserIndex, interactionState);
        if (payloads.sourcePayload().isEmpty() || payloads.targetPayload().isEmpty()) {
            return emptyInteractionStateAction();
        }
        return new TradeInteractionStateAction(
            targetSocketIndex,
            interactionState,
            payloads.sourcePayload(),
            payloads.targetPayload(),
            interactionState == 1L ? "Ao" : "");
    }

    /**
     * Original function: Proc_6_94_746990.
     */
    public static TradeInteractionCloseAction closeInteractionAction(
        long socketIndex,
        long targetSocketIndex,
        long sourceRoomUserIndex,
        long targetRoomUserIndex,
        TradeState tradeState
    ) {
        if (socketIndex <= 0L || targetSocketIndex <= 0L
            || sourceRoomUserIndex <= 0L || targetRoomUserIndex <= 0L || tradeState == null) {
            return emptyInteractionCloseAction();
        }
        String payload = SocialLookups.interactionClosedPayload(sourceRoomUserIndex);
        if (payload.isEmpty()) {
            return emptyInteractionCloseAction();
        }
        tradeState.removeInteractionPair(socketIndex);
        tradeState.removeInteractionPair(targetSocketIndex);
        return new TradeInteractionCloseAction(targetSocketIndex, payload);
    }

    /**
     * Original function: Proc_6_89_73EA10.
     */
    public static TradeConfirmation confirmTradeAction(
        long socketIndex,
        String userId,
        String targetUserId,
        long roomId,
        String sessionId,
        TradeState tradeState,
        TradeDao trades
    )
        throws Exception {

        if (socketIndex <= 0L
            || StringUtils.text(userId).isEmpty() || "0".equals(StringUtils.text(userId))
            || StringUtils.text(targetUserId).isEmpty() || "0".equals(StringUtils.text(targetUserId))
            || tradeState == null || trades == null) {
            return emptyConfirmation();
        }
        int targetSocketIndex = tradeState.interactionPartner(socketIndex);
        if (targetSocketIndex <= 0) {
            return emptyConfirmation();
        }
        List<Long> sourceFurnitureIds = tradeState.tradeOfferFurnitureIds(socketIndex);
        List<Long> targetFurnitureIds = tradeState.tradeOfferFurnitureIds(targetSocketIndex);
        if (sourceFurnitureIds.isEmpty() && targetFurnitureIds.isEmpty()) {
            return emptyConfirmation();
        }
        long numericUserId = NumberUtils.parseLong(userId);
        long numericTargetUserId = NumberUtils.parseLong(targetUserId);
        if (!sourceFurnitureIds.isEmpty()) {
            trades.transferInventoryFurniture(sourceFurnitureIds, numericUserId, numericTargetUserId);
        }
        if (!targetFurnitureIds.isEmpty()) {
            trades.transferInventoryFurniture(targetFurnitureIds, numericTargetUserId, numericUserId);
        }
        trades.insertTradeLog(
            numericUserId,
            numericTargetUserId,
            tradeState.tradeOfferLogItems(socketIndex),
            tradeState.tradeOfferLogItems(targetSocketIndex),
            roomId,
            sessionId);
        tradeState.removeInteractionPair(socketIndex);
        tradeState.removeInteractionPair(targetSocketIndex);
        return new TradeConfirmation(targetSocketIndex, "Ap");
    }

    /**
     * Original function: Proc_6_91_743480.
     */
    public static TradeOfferAction addOfferAction(
        long socketIndex,
        long targetSocketIndex,
        String userId,
        String targetUserId,
        long furnitureId,
        TradeState tradeState,
        FurnitureDao furniture
    )
        throws Exception {

        if (socketIndex <= 0L || targetSocketIndex <= 0L || furnitureId <= 0L
            || StringUtils.text(userId).isEmpty() || "0".equals(StringUtils.text(userId))
            || StringUtils.text(targetUserId).isEmpty() || "0".equals(StringUtils.text(targetUserId))
            || tradeState == null || furniture == null) {
            return emptyOfferAction();
        }
        FurnitureDao.TradeFurniture tradeFurniture =
            furniture.tradeFurniture(furnitureId, NumberUtils.parseLong(userId)).orElse(null);
        if (tradeFurniture == null || tradeFurniture.productId() <= 0L) {
            return emptyOfferAction();
        }
        tradeState.storeTradeOffer(
            socketIndex,
            furnitureId,
            tradeFurniture.productId(),
            StringUtils.text(tradeFurniture.sign()),
            tradeFurniture.secondaryValue());
        return offerAction(socketIndex, targetSocketIndex, userId, targetUserId, tradeState);
    }

    /**
     * Original function: Proc_6_92_744870.
     */
    public static TradeOfferAction removeOfferAction(
        long socketIndex,
        long targetSocketIndex,
        String userId,
        String targetUserId,
        long furnitureId,
        TradeState tradeState,
        FurnitureDao furniture
    )
        throws Exception {

        if (socketIndex <= 0L || targetSocketIndex <= 0L || furnitureId <= 0L
            || StringUtils.text(userId).isEmpty() || "0".equals(StringUtils.text(userId))
            || StringUtils.text(targetUserId).isEmpty() || "0".equals(StringUtils.text(targetUserId))
            || tradeState == null || furniture == null) {
            return emptyOfferAction();
        }
        if (furniture.tradeFurnitureForRemoval(furnitureId, NumberUtils.parseLong(userId)).isEmpty()) {
            return emptyOfferAction();
        }
        tradeState.removeTradeOffer(socketIndex, furnitureId);
        return offerAction(socketIndex, targetSocketIndex, userId, targetUserId, tradeState);
    }

    private static TradeOfferAction offerAction(
        long socketIndex,
        long targetSocketIndex,
        String userId,
        String targetUserId,
        TradeState tradeState
    ) {
        return new TradeOfferAction(
            tradeState.tradeOfferPayload(socketIndex, targetSocketIndex, userId, targetUserId),
            tradeState.tradeOfferPayload(targetSocketIndex, socketIndex, targetUserId, userId));
    }

    private static TradeOfferAction emptyOfferAction() {
        return new TradeOfferAction("", "");
    }

    private static TradeConfirmation emptyConfirmation() {
        return new TradeConfirmation(0L, "");
    }

    private static TradeInteractionRequestAction emptyInteractionRequestAction() {
        return new TradeInteractionRequestAction(0L, "", "");
    }

    private static TradeInteractionStateAction emptyInteractionStateAction() {
        return new TradeInteractionStateAction(0L, 0L, "", "", "");
    }

    private static TradeInteractionCloseAction emptyInteractionCloseAction() {
        return new TradeInteractionCloseAction(0L, "");
    }
}
