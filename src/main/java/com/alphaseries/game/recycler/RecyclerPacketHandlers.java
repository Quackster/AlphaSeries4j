package com.alphaseries.game.recycler;

import com.alphaseries.dao.mysql.CatalogDao;
import com.alphaseries.dao.mysql.DaoProvider;
import com.alphaseries.dao.mysql.FurnitureDao;
import com.alphaseries.dao.mysql.RecyclerDao;
import com.alphaseries.server.runtime.SessionLookups;
import com.alphaseries.server.runtime.SocketDelivery;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public final class RecyclerPacketHandlers {
    private RecyclerPacketHandlers() {
    }

    /**
     * Original function: Proc_6_202_7D6760.
     */
    public static String submitItems(int socketIndex, RecyclerSelection selection) {
        try {
            if (socketIndex <= 0) {
                return "";
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            RecyclerLookups.SubmitResult result = RecyclerLookups.submitItems(
                NumberUtils.parseLong(userId), selection,
                RecyclerState.instance().settings(), furnitureDao(), catalogDao(), recyclerDao());
            if (!result.valid()) {
                return "";
            }
            SocketDelivery.sendToSocket(socketIndex, result.deliveryPayloads());
            return result.rewardPayload();
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_203_7D7F80.
     */
    public static String sendStatus(int socketIndex) {
        try {
            String payload = RecyclerLookups.statusPayload();
            SocketDelivery.sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_19_6E8040.
     */
    public static String sendCachedStatus(int socketIndex, String cachedPayload, String packetPrefix) {
        try {
            if (socketIndex <= 0) {
                return "";
            }
            cachedPayload = StringUtils.text(cachedPayload);
            packetPrefix = StringUtils.text(packetPrefix);
            if (packetPrefix.isEmpty()) {
                packetPrefix = "Gz";
            }
            if (cachedPayload.isEmpty()) {
                cachedPayload = RecyclerState.instance().settings().statusPayload();
            }
            String payload = packetPrefix + cachedPayload;
            SocketDelivery.sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    private static FurnitureDao furnitureDao() {
        return DaoProvider.furnitureDao();
    }

    private static CatalogDao catalogDao() {
        return DaoProvider.catalogDao();
    }

    private static RecyclerDao recyclerDao() {
        return DaoProvider.recyclerDao();
    }
}
