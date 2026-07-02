package com.alphaseries.game.moderation;

import com.alphaseries.protocol.PacketReader;
import com.alphaseries.protocol.WireEncoding;
import com.alphaseries.protocol.WireReader;
import com.alphaseries.protocol.WireRequests;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class StaffWire {
    private StaffWire() {
    }

    public static long userId(String packetPayload) {
        return NumberUtils.parseLong(WireEncoding.readVl64LengthString(packetPayload));
    }

    public static long nestedUserId(String packetPayload) {
        String requestPayload = StringUtils.text(packetPayload);
        long directValue = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        if (directValue > 0L) {
            return directValue;
        }
        WireReader.Offset offset = new WireReader.Offset(1);
        String nestedPayload = WireReader.readString(requestPayload, offset);
        long nestedValue = NumberUtils.parseLong(WireEncoding.readVl64LengthString(nestedPayload));
        if (nestedValue <= 0L) {
            nestedValue = NumberUtils.parseLong(nestedPayload);
        }
        if (nestedValue > 0L) {
            return nestedValue;
        }
        offset.setValue(1L);
        return WireReader.readLong(requestPayload, offset);
    }

    public record UserSummaryRequest(long targetUserId) {
    }

    public record BanRequest(long targetUserId, String banMessage, long banHours) {
    }

    public record RoomModerationRequest(long actionType, String messageText) {
    }

    public record CloseCallForHelpRequest(long closeState, long callForHelpId) {
    }

    public record RoomLockRequest(long lockFlag) {
    }

    public record DirectMessageRequest(long targetUserId, String messageText) {
    }

    public record SubmitCallForHelpRequest(String descriptionText, long categoryId, long partnerUserId) {
    }

    public record CallForHelpTabRequest(List<Long> callForHelpIds) {
        public CallForHelpTabRequest {
            callForHelpIds = callForHelpIds == null ? List.of() : List.copyOf(callForHelpIds);
        }
    }

    public record HistoryRequest(long targetUserId) {
    }

    public record CallForHelpChatLogRequest(long callForHelpId) {
    }

    public record RoomChatLogRequest(long roomId) {
    }

    public record RoomInfoRequest(long roomId) {
    }

    /**
     * Original function: Proc_6_0_6D7FF0.
     */
    public static UserSummaryRequest userSummaryRequest(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "GF");
        long targetUserId = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        if (targetUserId <= 0L) {
            targetUserId = WireReader.readLong(requestPayload, new WireReader.Offset(1));
        }
        return new UserSummaryRequest(targetUserId);
    }

    /**
     * Original function: Proc_6_3_6DA490.
     */
    public static BanRequest banRequest(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "GP");
        WireReader.Offset offset = new WireReader.Offset(1);
        long targetUserId = WireReader.readLong(requestPayload, offset);
        if (targetUserId <= 0L) {
            targetUserId = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        }
        String banMessage = StringUtils.singleLineText(WireReader.readString(requestPayload, offset));
        if (banMessage.isEmpty()) {
            banMessage = StringUtils.singleLineText(WireEncoding.readBase64LengthString(requestPayload));
        }
        long banHours = WireReader.readLong(requestPayload, offset);
        if (banHours <= 0L) {
            banHours = NumberUtils.parseLong(WireEncoding.readVl64LengthString(StringUtils.mid(requestPayload, (int) offset.value())));
        }
        return new BanRequest(targetUserId, banMessage, banHours);
    }

    /**
     * Original function: Proc_6_4_6DAFB0.
     */
    public static RoomModerationRequest roomModerationRequest(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "CH");
        WireReader.Offset offset = new WireReader.Offset(1);
        long actionType = WireReader.readLong(requestPayload, offset);
        if (actionType <= 0L) {
            actionType = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        }
        String messageText = WireReader.readString(requestPayload, offset);
        if (messageText.isEmpty()) {
            messageText = WireEncoding.readBase64LengthString(requestPayload);
        }
        return new RoomModerationRequest(actionType, StringUtils.singleLineText(messageText));
    }

    /**
     * Original function: Proc_6_7_6DD0E0.
     */
    public static CloseCallForHelpRequest closeCallForHelpRequest(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "GD");
        WireReader.Offset offset = new WireReader.Offset(1);
        long closeState = WireReader.readLong(requestPayload, offset);
        long callForHelpId = WireReader.readLong(requestPayload, offset);
        if (callForHelpId <= 0L) {
            callForHelpId = WireReader.readLong(requestPayload, offset);
        }
        return new CloseCallForHelpRequest(closeState, callForHelpId);
    }

    /**
     * Original function: Proc_6_9_6DDD70.
     */
    public static RoomLockRequest roomLockRequest(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "GL");
        WireReader.Offset offset = new WireReader.Offset(1);
        WireReader.readLong(requestPayload, offset);
        return new RoomLockRequest(WireReader.readLong(requestPayload, offset));
    }

    /**
     * Original functions: Proc_6_1_6D8B70, Proc_6_2_6D9880, and Proc_6_12_6DFE90.
     */
    public static DirectMessageRequest directMessageRequest(String packetPayload, String prefix) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, prefix);
        WireReader.Offset offset = new WireReader.Offset(1);
        long targetUserId = WireReader.readLong(requestPayload, offset);
        if (targetUserId <= 0L) {
            targetUserId = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        }
        String messageText = StringUtils.singleLineText(WireReader.readString(requestPayload, offset));
        if (messageText.isEmpty()) {
            messageText = StringUtils.singleLineText(WireEncoding.readBase64LengthString(requestPayload));
        }
        return new DirectMessageRequest(targetUserId, messageText);
    }

    /**
     * Original function: Proc_6_32_70EAB0.
     */
    public static SubmitCallForHelpRequest submitCallForHelpRequest(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "GE");
        WireReader.Offset offset = new WireReader.Offset(1);
        String descriptionText = StringUtils.singleLineText(WireReader.readString(requestPayload, offset));
        long categoryId = WireReader.readLong(requestPayload, offset);
        if (categoryId <= 0L) {
            categoryId = WireReader.readLong(requestPayload, offset);
        }
        long partnerUserId = WireReader.readLong(requestPayload, offset);
        return new SubmitCallForHelpRequest(descriptionText, categoryId, partnerUserId);
    }

    /**
     * Original functions: Proc_6_6_6DC9D0 and Proc_6_8_6DD790.
     */
    public static CallForHelpTabRequest callForHelpTabRequest(String packetPayload, String prefix) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, prefix);
        PacketReader reader = PacketReader.of(requestPayload);
        long requestedCount = reader.readInt();
        if (requestedCount < 1L || requestedCount > 150L) {
            return new CallForHelpTabRequest(List.of());
        }
        List<Long> callForHelpIds = new ArrayList<>();
        for (long requestIndex = 0L; requestIndex < requestedCount; requestIndex++) {
            long callForHelpId = reader.readInt();
            if (callForHelpId <= 0L) {
                return new CallForHelpTabRequest(List.of());
            }
            callForHelpIds.add(callForHelpId);
        }
        return new CallForHelpTabRequest(callForHelpIds);
    }

    /**
     * Original functions: Proc_6_10_6DE1D0 and Proc_6_11_6DF4A0.
     */
    public static HistoryRequest historyRequest(String packetPayload, String prefix, boolean includeChatRows) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, prefix);
        long targetUserId = includeChatRows ? nestedUserId(requestPayload) : userId(requestPayload);
        return new HistoryRequest(targetUserId);
    }

    public static CallForHelpChatLogRequest callForHelpChatLogRequest(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "GI");
        return new CallForHelpChatLogRequest(NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload)));
    }

    public static RoomChatLogRequest roomChatLogRequest(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "GH");
        return new RoomChatLogRequest(NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload)));
    }

    public static RoomInfoRequest roomInfoRequest(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "GK");
        return new RoomInfoRequest(NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload)));
    }
}
