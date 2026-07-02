package com.alphaseries.game.inventory;

import com.alphaseries.dao.mysql.DaoProvider;
import com.alphaseries.dao.mysql.FurnitureDao;
import com.alphaseries.server.runtime.SessionLookups;
import com.alphaseries.server.runtime.SocketDelivery;
import com.alphaseries.util.NumberUtils;

public final class InventoryPacketHandlers {
    private InventoryPacketHandlers() {
    }

    /**
     * Original function: Proc_6_140_769400.
     */
    public static void sendInventoryToSocket(int socketIndex) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty()) {
                return;
            }
            FurnitureDao furniture = furnitureDao();
            if (furniture == null) {
                return;
            }
            InventoryMessagePayloads.InventoryList payloads = InventoryMessagePayloads.listFromItems(
                furniture.inventoryFurnitureForOwner(NumberUtils.parseLong(userId)));
            SocketDelivery.sendToSocket(socketIndex, InventoryMessagePayloads.regularList(payloads));
            SocketDelivery.sendToSocket(socketIndex, InventoryMessagePayloads.iconList(payloads));
            SocketDelivery.sendToSocket(socketIndex, InventoryMessagePayloads.emptyRentalList());
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    private static FurnitureDao furnitureDao() {
        return DaoProvider.furnitureDao();
    }
}
