package com.alphaseries.game.quest;

import com.alphaseries.protocol.WireEncoding;
import com.alphaseries.protocol.WireReader;
import com.alphaseries.protocol.WireRequests;
import com.alphaseries.util.NumberUtils;

public final class QuestWire {
    private QuestWire() {
    }

    public record QuestIdRequest(long questId) {
    }

    /**
     * Original function: Proc_6_232_7F45A0.
     */
    public static QuestIdRequest questIdRequest(String packetPayload, String prefix) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, prefix);
        long questId = WireRequests.id(packetPayload, prefix);
        if (questId <= 0L) {
            questId = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        }
        if (questId <= 0L) {
            questId = WireReader.readLong(requestPayload, new WireReader.Offset(1));
        }
        return new QuestIdRequest(questId);
    }
}
