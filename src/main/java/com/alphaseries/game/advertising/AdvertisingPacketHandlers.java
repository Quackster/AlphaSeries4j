package com.alphaseries.game.advertising;

import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.server.runtime.SocketDelivery;

public final class AdvertisingPacketHandlers {
    private AdvertisingPacketHandlers() {
    }

    /**
     * Original function: Proc_6_59_71FEE0.
     */
    public static void sendVisitRoomAdvertisement(int socketIndex) {
        try {
            PacketBuilder advertisementPayload = PacketBuilder.create();
            VisitRoomAds visitRoomAds = AdvertisingState.instance().visitRoomAds();
            if (!visitRoomAds.appendRandomPayloadTo(advertisementPayload)) {
                advertisementPayload.appendRaw("\2\2");
            }
            SocketDelivery.sendToSocket(socketIndex, PacketBuilder.message("DB").appendRaw(advertisementPayload).build());
        } catch (Exception ignored) {
        }
    }
}
