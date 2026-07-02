package com.alphaseries.game.social;

import com.alphaseries.protocol.WireEncoding;
import com.alphaseries.protocol.WireReader;
import com.alphaseries.protocol.WireRequests;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public final class SocialWire {
    private SocialWire() {
    }

    public record RepresentedChatMessage(String targetName, String messageText) {
        public RepresentedChatMessage {
            targetName = StringUtils.text(targetName).trim();
            messageText = StringUtils.left(StringUtils.singleLineText(messageText).trim(), 122);
        }
    }

    public record RoomUserIndexRequest(long roomUserIndex) {
    }

    public record UserIdRequest(long userId) {
    }

    public record FollowUserRequest(String targetName) {
        public FollowUserRequest {
            targetName = StringUtils.text(targetName).trim();
        }
    }

    public record DanceRequest(long danceId) {
        public DanceRequest {
            if (danceId < 0L) {
                danceId = 0L;
            }
            if (danceId > 4L) {
                danceId = 4L;
            }
        }
    }

    public record EffectRequest(long effectId) {
    }

    /**
     * Original function: Proc_6_14_6E10C0.
     */
    public static DanceRequest danceRequest(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "A]");
        long danceId = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        if (danceId <= 0L) {
            danceId = WireReader.readLong(requestPayload, new WireReader.Offset(1));
        }
        return new DanceRequest(danceId);
    }

    /**
     * Original function: Proc_6_190_7D11D0.
     * Original function: Proc_6_192_7D1B80.
     * Original function: Proc_6_93_745D90.
     */
    public static RoomUserIndexRequest roomUserIndexRequest(String packetPayload, String prefix) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, prefix);
        long roomUserIndex = WireReader.readLong(requestPayload, new WireReader.Offset(1));
        if (roomUserIndex <= 0L) {
            roomUserIndex = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        }
        return new RoomUserIndexRequest(roomUserIndex);
    }

    /**
     * Original function: Proc_6_191_7D18B0.
     * Original function: Proc_6_76_726CE0.
     */
    public static UserIdRequest userIdRequest(String packetPayload, String prefix) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, prefix);
        return new UserIdRequest(WireReader.readLong(requestPayload, new WireReader.Offset(1)));
    }

    /**
     * Original function: Proc_6_50_7166B0.
     */
    public static FollowUserRequest followUserRequest(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "Ab");
        String targetName = WireEncoding.readBase64LengthString(requestPayload).trim();
        if (targetName.isEmpty() || !requestPayload.startsWith("@")) {
            targetName = requestPayload.trim();
        }
        return new FollowUserRequest(targetName);
    }

    /**
     * Original function: Proc_6_102_749C50.
     */
    public static EffectRequest effectRequest(String packetPayload) {
        String requestPayload = StringUtils.text(packetPayload);
        if (requestPayload.length() >= 3) {
            requestPayload = requestPayload.substring(2);
        }
        long effectId = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        if (effectId <= 0L) {
            effectId = WireReader.readLong(requestPayload, new WireReader.Offset(1));
        }
        return new EffectRequest(effectId);
    }

    public static BadgeUpdateSelections badgeUpdateSelections(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "B^");
        WireReader.Offset offset = new WireReader.Offset(1);
        String first = badgeUpdateSelection(requestPayload, offset);
        String second = badgeUpdateSelection(requestPayload, offset);
        String third = badgeUpdateSelection(requestPayload, offset);
        String fourth = badgeUpdateSelection(requestPayload, offset);
        String fifth = badgeUpdateSelection(requestPayload, offset);
        return new BadgeUpdateSelections(first, second, third, fourth, fifth);
    }

    private static String badgeUpdateSelection(String requestPayload, WireReader.Offset offset) {
        long hasBadge = WireReader.readLong(requestPayload, offset);
        if (hasBadge == 1L) {
            return StringUtils.sqlEscapedText(WireReader.readString(requestPayload, offset));
        }
        return "";
    }

    /**
     * Original function: Proc_6_8_6E1C60.
     * Original function: Proc_6_9_6E2390.
     * Original function: Proc_6_10_6E2AC0.
     * Original function: Proc_6_242_7FFC60.
     */
    public static RepresentedChatMessage representedChatMessage(String packetPayload, long chatType) {
        String requestPayload = StringUtils.text(packetPayload);
        if (requestPayload.startsWith("@t") || requestPayload.startsWith("@w") || requestPayload.startsWith("@x")) {
            requestPayload = requestPayload.substring(2);
        }
        if (requestPayload.startsWith("H") || requestPayload.startsWith("I")) {
            requestPayload = requestPayload.substring(1);
        }

        String targetName = "";
        String messageText;
        WireReader.Offset offset = new WireReader.Offset(1);
        if (chatType == 2L) {
            targetName = WireReader.readString(requestPayload, offset).trim();
            messageText = WireReader.readString(requestPayload, offset);
            if (messageText.isEmpty()) {
                messageText = requestPayload;
                int spaceAt = messageText.indexOf(' ');
                if (spaceAt >= 0) {
                    targetName = messageText.substring(0, spaceAt).trim();
                    messageText = messageText.substring(spaceAt + 1);
                }
            }
        } else {
            messageText = WireReader.readString(requestPayload, offset);
            if (messageText.isEmpty()) {
                messageText = requestPayload;
            }
        }

        return new RepresentedChatMessage(targetName, messageText);
    }
}
