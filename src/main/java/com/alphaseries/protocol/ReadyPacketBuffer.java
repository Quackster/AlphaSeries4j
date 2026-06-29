package com.alphaseries.protocol;

import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class ReadyPacketBuffer {
    private ReadyPacketBuffer() {
    }

    public static final class Frame {
        private final String code;
        private final String payload;

        private Frame(String code, String payload) {
            this.code = code;
            this.payload = payload;
        }

        public String code() {
            return code;
        }

        public String payload() {
            return payload;
        }
    }

    public static boolean isCrossDomainPolicyRequest(String packetBuffer) {
        return StringUtils.text(packetBuffer).indexOf('\0') >= 0;
    }

    public static List<Frame> frames(String packetBuffer) {
        List<Frame> packets = new ArrayList<>();
        String buffer = StringUtils.text(packetBuffer);
        if (isCrossDomainPolicyRequest(buffer)) {
            return packets;
        }
        while (buffer.length() > 2) {
            buffer = buffer.substring(1);
            long packetLength = WireEncoding.decodeBase64Length(StringUtils.left(buffer, 2));
            if (packetLength <= 0L || buffer.length() < packetLength + 2L) {
                break;
            }
            String packetPayload = buffer.substring(2, (int) packetLength + 2);
            packets.add(new Frame(StringUtils.left(packetPayload, 2), packetPayload));
            buffer = buffer.substring((int) packetLength + 2);
        }
        return packets;
    }
}
