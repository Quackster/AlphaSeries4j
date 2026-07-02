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

    public record FriendTargetList(boolean deleteAllPending, List<Long> targetIds, long targetCount) {
        public FriendTargetList {
            targetIds = targetIds == null ? List.of() : List.copyOf(targetIds);
        }

        public static FriendTargetList empty() {
            return new FriendTargetList(false, List.of(), 0L);
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

    private static String requestTextFromWirePayload(String packetPayload, String prefix, int maxLength) {
        String requestPayload = StringUtils.withoutPrefix(packetPayload, prefix);
        String value = WireEncoding.readBase64LengthString(requestPayload);
        if (value.isEmpty()) {
            value = PacketReader.of(requestPayload).readString();
        }
        return maxLength >= 0 ? StringUtils.left(value, maxLength) : value;
    }

    public static SearchRequest searchRequest(String packetPayload, String prefix) {
        return new SearchRequest(requestTextFromWirePayload(packetPayload, prefix, -1));
    }

    public static FriendRequest friendRequest(String packetPayload, String prefix) {
        return new FriendRequest(requestTextFromWirePayload(packetPayload, prefix, -1));
    }

    public static FriendTargetList friendDeleteTargetsFromPayload(String packetPayload) {
        String requestPayload = StringUtils.withoutPrefix(packetPayload, "@f");
        PacketReader reader = PacketReader.of(requestPayload);
        long firstValue = reader.readInt();
        if (firstValue == 1L) {
            return new FriendTargetList(true, List.of(), 0L);
        }

        long maxTargets = firstValue <= 0L ? 75L : Math.min(firstValue, 75L);
        List<Long> targetIds = new ArrayList<>();
        long targetCount = 0L;
        while (!reader.remaining().isEmpty() && targetCount < maxTargets) {
            int previousOffset = reader.offset();
            long targetUserId = reader.readInt();
            if (targetUserId <= 0L || reader.offset() == previousOffset) {
                break;
            }
            if (!targetIds.contains(targetUserId)) {
                targetIds.add(targetUserId);
                targetCount++;
            }
        }
        if (targetIds.isEmpty() && firstValue > 1L) {
            targetIds.add(firstValue);
            targetCount = 1L;
        }
        return new FriendTargetList(false, targetIds, targetCount);
    }

    public static FriendTargetList friendRemoveTargetsFromPayload(String packetPayload, long callerUserId) {
        String requestPayload = StringUtils.withoutPrefix(packetPayload, "@h");
        PacketReader reader = PacketReader.of(requestPayload);
        long removeCount = reader.readInt();
        if (removeCount <= 0L) {
            return FriendTargetList.empty();
        }
        if (removeCount > 75L) {
            removeCount = 75L;
        }
        List<Long> targetIds = new ArrayList<>();
        long targetCount = 0L;
        for (long removeIndex = 1L; removeIndex <= removeCount; removeIndex++) {
            long targetUserId = reader.readInt();
            if (targetUserId > 0L && targetUserId != callerUserId && !targetIds.contains(targetUserId)) {
                targetIds.add(targetUserId);
                targetCount++;
            }
        }
        return new FriendTargetList(false, targetIds, targetCount);
    }

    /**
     * Original function: Proc_6_167_7BECA0.
     */
    public static AcceptFriendRequests acceptFriendRequests(String packetPayload) {
        String requestPayload = StringUtils.withoutPrefix(packetPayload, "@e");
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
        String requestPayload = StringUtils.withoutPrefix(packetPayload, "@a");
        PacketReader reader = PacketReader.of(requestPayload);
        long targetUserId = reader.readInt();
        if (targetUserId <= 0L) {
            targetUserId = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        }
        String messageText = WireEncoding.readBase64LengthString(requestPayload);
        messageText = StringUtils.left(messageText, 122);
        if (messageText.isEmpty()) {
            messageText = StringUtils.left(reader.readString(), 122);
        }
        return new MessengerPrivateMessage(targetUserId, messageText);
    }

    /**
     * Original function: Proc_6_169_7C0DC0.
     */
    public static FriendFollowRequest friendFollowRequest(String packetPayload) {
        String requestPayload = StringUtils.withoutPrefix(packetPayload, "DF");
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
        String requestPayload = StringUtils.withoutPrefix(packetPayload, "@b");
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
        inviteText = StringUtils.left(inviteText, 122);
        if (inviteText.isEmpty()) {
            inviteText = StringUtils.left(WireReader.readString(requestPayload, offset), 122);
        }
        return new RoomInviteRequest(targetIds, targetCount, inviteText);
    }
}
