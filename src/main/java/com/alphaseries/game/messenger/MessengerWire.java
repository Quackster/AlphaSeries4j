package com.alphaseries.game.messenger;

import com.alphaseries.protocol.PacketReader;
import com.alphaseries.protocol.WireEncoding;
import com.alphaseries.protocol.WireReader;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class MessengerWire {
    private MessengerWire() {
    }

    public record FriendTargetList(boolean deleteAllPending, String targetList, List<Long> targetIds, long targetCount) {
        public FriendTargetList {
            targetList = StringUtils.text(targetList);
            targetIds = targetIds == null ? List.of() : List.copyOf(targetIds);
        }

        public static FriendTargetList empty() {
            return new FriendTargetList(false, "", List.of(), 0L);
        }
    }

    public record RoomInviteRequest(List<Long> targetIds, long targetCount, String inviteText) {
        public RoomInviteRequest {
            targetIds = targetIds == null ? List.of() : List.copyOf(targetIds);
            inviteText = StringUtils.text(inviteText);
        }
    }

    public record FriendFollowRequest(long targetUserId) {
    }

    public record AcceptFriendRequests(List<Long> targetIds, long requestedCount) {
        public AcceptFriendRequests {
            targetIds = targetIds == null ? List.of() : List.copyOf(targetIds);
        }
    }

    public record SearchRequest(String searchText) {
        public SearchRequest {
            searchText = StringUtils.text(searchText).trim().toLowerCase();
        }
    }

    public record FriendRequest(String targetName) {
        public FriendRequest {
            targetName = StringUtils.text(targetName).trim();
        }
    }

    public static String requestTextFromWirePayload(String packetPayload, String prefix, int maxLength) {
        String requestPayload = StringUtils.text(packetPayload);
        if (!StringUtils.text(prefix).isEmpty() && requestPayload.startsWith(prefix)) {
            requestPayload = requestPayload.substring(prefix.length());
        }
        String value = WireEncoding.readBase64LengthString(requestPayload);
        if (value.isEmpty()) {
            value = PacketReader.of(requestPayload).readString();
        }
        if (maxLength >= 0 && value.length() > maxLength) {
            return value.substring(0, maxLength);
        }
        return value;
    }

    public static SearchRequest searchRequest(String packetPayload, String prefix) {
        return new SearchRequest(requestTextFromWirePayload(packetPayload, prefix, -1));
    }

    public static FriendRequest friendRequest(String packetPayload, String prefix) {
        return new FriendRequest(requestTextFromWirePayload(packetPayload, prefix, -1));
    }

    public static FriendTargetList friendDeleteTargetsFromPayload(String packetPayload) {
        String requestPayload = StringUtils.text(packetPayload);
        if (requestPayload.startsWith("@f")) {
            requestPayload = requestPayload.substring(2);
        }
        PacketReader reader = PacketReader.of(requestPayload);
        long firstValue = reader.readInt();
        if (firstValue == 1L) {
            return new FriendTargetList(true, "", List.of(), 0L);
        }

        long maxTargets = firstValue <= 0L ? 75L : Math.min(firstValue, 75L);
        List<Long> targetIds = new ArrayList<>();
        List<String> targetTokens = new ArrayList<>();
        long targetCount = 0L;
        while (!reader.remaining().isEmpty() && targetCount < maxTargets) {
            int previousOffset = reader.offset();
            long targetUserId = reader.readInt();
            if (targetUserId <= 0L || reader.offset() == previousOffset) {
                break;
            }
            String token = String.valueOf(targetUserId);
            if (!targetIds.contains(targetUserId)) {
                targetIds.add(targetUserId);
                targetTokens.add(token);
                targetCount++;
            }
        }
        if (targetIds.isEmpty() && firstValue > 1L) {
            targetIds.add(firstValue);
            targetTokens.add(String.valueOf(firstValue));
            targetCount = 1L;
        }
        return new FriendTargetList(false, String.join(",", targetTokens), targetIds, targetCount);
    }

    public static FriendTargetList friendRemoveTargetsFromPayload(String packetPayload, String callerUserId) {
        String requestPayload = StringUtils.text(packetPayload);
        if (requestPayload.startsWith("@h")) {
            requestPayload = requestPayload.substring(2);
        }
        PacketReader reader = PacketReader.of(requestPayload);
        long removeCount = reader.readInt();
        if (removeCount <= 0L) {
            return FriendTargetList.empty();
        }
        if (removeCount > 75L) {
            removeCount = 75L;
        }
        List<Long> targetIds = new ArrayList<>();
        List<String> targetTokens = new ArrayList<>();
        long targetCount = 0L;
        for (long removeIndex = 1L; removeIndex <= removeCount; removeIndex++) {
            long targetUserId = reader.readInt();
            String token = String.valueOf(targetUserId);
            if (targetUserId > 0L && !token.equals(StringUtils.text(callerUserId)) && !targetIds.contains(targetUserId)) {
                targetIds.add(targetUserId);
                targetTokens.add(token);
                targetCount++;
            }
        }
        return new FriendTargetList(false, String.join(",", targetTokens), targetIds, targetCount);
    }

    /**
     * Original function: Proc_6_167_7BECA0.
     */
    public static AcceptFriendRequests acceptFriendRequests(String packetPayload) {
        String requestPayload = StringUtils.text(packetPayload);
        if (requestPayload.startsWith("@e")) {
            requestPayload = requestPayload.substring(2);
        }
        WireReader.Offset offset = new WireReader.Offset(1);
        long acceptCount = WireReader.readLong(requestPayload, offset);
        if (acceptCount <= 0L) {
            return new AcceptFriendRequests(List.of(), 0L);
        }
        acceptCount = Math.min(acceptCount, 75L);
        List<Long> targetIds = new ArrayList<>();
        for (long acceptIndex = 1L; acceptIndex <= acceptCount; acceptIndex++) {
            long targetUserId = WireReader.readLong(requestPayload, offset);
            if (targetUserId > 0L && !targetIds.contains(targetUserId)) {
                targetIds.add(targetUserId);
            }
        }
        return new AcceptFriendRequests(targetIds, acceptCount);
    }

    public static MessengerPrivateMessage privateMessageFromWire(String packetPayload) {
        String requestPayload = StringUtils.text(packetPayload);
        if (requestPayload.startsWith("@a")) {
            requestPayload = requestPayload.substring(2);
        }
        PacketReader reader = PacketReader.of(requestPayload);
        long targetUserId = reader.readInt();
        if (targetUserId <= 0L) {
            targetUserId = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        }
        String messageText = WireEncoding.readBase64LengthString(requestPayload);
        if (messageText.length() > 122) {
            messageText = messageText.substring(0, 122);
        }
        if (messageText.isEmpty()) {
            messageText = reader.readString();
            if (messageText.length() > 122) {
                messageText = messageText.substring(0, 122);
            }
        }
        return new MessengerPrivateMessage(targetUserId, messageText);
    }

    /**
     * Original function: Proc_6_169_7C0DC0.
     */
    public static FriendFollowRequest friendFollowRequest(String packetPayload) {
        String requestPayload = StringUtils.text(packetPayload);
        if (requestPayload.startsWith("DF")) {
            requestPayload = requestPayload.substring(2);
        }
        long targetUserId = WireReader.readLong(requestPayload, new WireReader.Offset(1));
        if (targetUserId <= 0L) {
            targetUserId = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        }
        return new FriendFollowRequest(targetUserId);
    }

    /**
     * Original function: Proc_6_168_7C05F0.
     */
    public static RoomInviteRequest roomInviteFromWire(String packetPayload) {
        String requestPayload = StringUtils.text(packetPayload);
        if (requestPayload.startsWith("@b")) {
            requestPayload = requestPayload.substring(2);
        }
        WireReader.Offset offset = new WireReader.Offset(1);
        long targetCount = WireReader.readLong(requestPayload, offset);
        if (targetCount <= 0L) {
            return new RoomInviteRequest(List.of(), 0L, "");
        }
        targetCount = Math.min(targetCount, 150L);

        List<Long> targetIds = new ArrayList<>();
        for (long targetIndex = 1L; targetIndex <= targetCount; targetIndex++) {
            long targetUserId = WireReader.readLong(requestPayload, offset);
            if (targetUserId > 0L && !targetIds.contains(targetUserId)) {
                targetIds.add(targetUserId);
            }
        }

        String inviteText = WireEncoding.readBase64LengthString(requestPayload);
        if (inviteText.length() > 122) {
            inviteText = inviteText.substring(0, 122);
        }
        if (inviteText.isEmpty()) {
            inviteText = WireReader.readString(requestPayload, offset);
            if (inviteText.length() > 122) {
                inviteText = inviteText.substring(0, 122);
            }
        }
        return new RoomInviteRequest(targetIds, targetCount, inviteText);
    }
}
