package com.alphaseries.messages.outgoing;

import com.alphaseries.game.messenger.PendingFriendRequest;
import com.alphaseries.game.messenger.MessengerFriend;
import com.alphaseries.game.messenger.MessengerSearchResult;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.NumberUtils;

import java.util.List;

public final class MessengerPayloads {
    private MessengerPayloads() {
    }

    public static String friend(
        long userId,
        String userName,
        String motto,
        String figure,
        long rankValue,
        long followCount,
        long isOnline,
        String lastOnlineText,
        long relationshipState,
        boolean followEnabled
    ) {
        PacketBuilder payload = PacketBuilder.create()
            .appendRaw('0')
            .appendInt(userId)
            .appendString(userName)
            .appendInt(rankValue)
            .appendInt(isOnline);

        if (isOnline == 1L) {
            payload.appendBoolean(followEnabled && followCount > isOnline)
                .appendString(motto);
        } else if (isOnline == 0L) {
            payload.appendString("")
                .appendRaw('H')
                .appendString(figure)
                .appendRaw(relationshipState)
                .appendString("");
        }
        return payload.appendString(lastOnlineText)
            .appendString("")
            .build();
    }

    public static String friendSummary(
        MessengerFriend friend,
        long relationshipState,
        boolean followEnabled
    ) {
        if (friend == null) {
            return "";
        }
        long socketIndex = friend.socketIndex();
        return friend(
            friend.userId(),
            friend.userName(),
            friend.motto(),
            friend.figure(),
            friend.level(),
            socketIndex > 0L ? 2L : 0L,
            socketIndex > 0L ? 1L : 0L,
            friend.lastOnline(),
            relationshipState,
            followEnabled);
    }

    public static String searchResult(
        String userId,
        String userName,
        String figureText,
        String mottoText,
        String nicknameText,
        String lastOnlineText,
        long isOnline
    ) {
        return PacketBuilder.create()
            .appendRaw('1')
            .appendInt(NumberUtils.parseLong(userId))
            .appendString(userName)
            .appendString(mottoText)
            .appendInt(isOnline)
            .appendRaw('H')
            .appendString("")
            .appendString(nicknameText)
            .appendString(figureText)
            .appendString(lastOnlineText)
            .build();
    }

    public static String searchResults(List<MessengerSearchResult> results) {
        long friendCount = 0L;
        long otherCount = 0L;
        PacketBuilder friendPayload = PacketBuilder.create();
        PacketBuilder otherPayload = PacketBuilder.create();
        for (MessengerSearchResult result : results == null ? List.<MessengerSearchResult>of() : results) {
            if (result != null && result.userId() > 0L) {
                String resultPayload = searchResult(
                    String.valueOf(result.userId()),
                    result.userName(),
                    result.figure(),
                    result.motto(),
                    result.nickname(),
                    result.lastOnline(),
                    result.online() ? 1L : 0L);
                if (result.acceptedFriend()) {
                    friendPayload.appendRaw(resultPayload);
                    friendCount++;
                } else {
                    otherPayload.appendRaw(resultPayload);
                    otherCount++;
                }
            }
        }
        return PacketBuilder.message("Fs")
            .appendInt(friendCount)
            .appendRaw(friendPayload)
            .appendInt(otherCount)
            .appendRaw(otherPayload)
            .build();
    }

    public static String privateChatMessage(long senderUserId, String messageText) {
        return PacketBuilder.message("BF")
            .appendInt(senderUserId)
            .appendString(messageText)
            .build();
    }

    public static String roomInviteMessage(long senderUserId, String inviteText) {
        return PacketBuilder.message("BG")
            .appendInt(senderUserId)
            .appendString(inviteText)
            .build();
    }

    public static String acceptedFriends(String payloadRows, long acceptedCount) {
        return acceptedCount > 0L
            ? PacketBuilder.message("@MH").appendInt(acceptedCount).appendRaw(payloadRows).build()
            : "";
    }

    public static String removeFriends(String targetIdsPayload, long removedCount) {
        return removedCount > 0L
            ? PacketBuilder.message("@MM").appendInt(removedCount).appendRaw(targetIdsPayload).build()
            : "";
    }

    public static String removedId(long targetUserId) {
        return PacketBuilder.create().appendInt(targetUserId).build();
    }

    public static String requestAcceptedCaller(long targetUserId) {
        return PacketBuilder.message("DD").appendInt(targetUserId).appendRaw('H').build();
    }

    public static String requestDenied() {
        return PacketBuilder.message("DDH").appendString("").build();
    }

    public static String requestNotify(long userId, String userName) {
        return PacketBuilder.message("BD")
            .appendInt(userId)
            .appendString(userName)
            .appendString(userId)
            .build();
    }

    public static String pendingRequests(List<PendingFriendRequest> requests) {
        long requestCount = 0L;
        PacketBuilder requestPayload = PacketBuilder.create();
        for (PendingFriendRequest request : requests == null ? List.<PendingFriendRequest>of() : requests) {
            if (request != null && request.userId() > 0L) {
                requestPayload.appendRaw('0')
                    .appendInt(request.userId())
                    .appendString(request.userName())
                    .appendString(request.userName());
                requestCount++;
            }
        }
        return PacketBuilder.message("Dz")
            .appendInt(requestCount)
            .appendRaw("Dz")
            .appendInt(requestCount)
            .appendInt(requestCount)
            .appendRaw(requestPayload)
            .build();
    }

    public static String friendList(
        List<MessengerFriend> friends,
        long maxFriends0,
        long maxFriends1,
        long maxFriends2,
        boolean followEnabled
    ) {
        return friendList(friends, maxFriends0, maxFriends1, maxFriends2, null, followEnabled);
    }

    public static String friendList(
        List<MessengerFriend> friends,
        long maxFriends0,
        long maxFriends1,
        long maxFriends2,
        List<Long> onlineUserIds,
        boolean followEnabled
    ) {
        long friendCount = 0L;
        PacketBuilder friendPayload = PacketBuilder.create();
        for (MessengerFriend friend : friends == null ? List.<MessengerFriend>of() : friends) {
            if (friend != null) {
                long friendOnline = onlineUserIds == null
                    ? (friend.socketIndex() > 0L ? 1L : 0L)
                    : (containsId(onlineUserIds, friend.userId()) ? 1L : 0L);
                friendPayload.appendRaw(friend(
                    friend.userId(),
                    friend.userName(),
                    friend.motto(),
                    friend.figure(),
                    friend.level(),
                    friendOnline == 1L ? 2L : 0L,
                    friendOnline,
                    friend.lastOnline(),
                    1L,
                    followEnabled));
                friendCount++;
            }
        }
        return PacketBuilder.message("@L")
            .appendInt(maxFriends0)
            .appendInt(maxFriends1)
            .appendInt(maxFriends2)
            .appendInt(friendCount)
            .appendRaw(friendPayload)
            .appendRaw("PYH")
            .build();
    }

    private static boolean containsId(List<Long> ids, long targetId) {
        if (ids == null || targetId <= 0L) {
            return false;
        }
        for (Long id : ids) {
            if (id != null && id.longValue() == targetId) {
                return true;
            }
        }
        return false;
    }

}
