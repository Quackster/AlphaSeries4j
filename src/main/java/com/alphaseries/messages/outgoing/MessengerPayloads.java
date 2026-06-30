package com.alphaseries.messages.outgoing;

import com.alphaseries.game.messenger.PendingFriendRequest;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

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

    public static String friendSummaryFromRow(String rowText, long relationshipState, boolean followEnabled) {
        String[] fields = StringUtils.text(rowText).split("\t", -1);
        if (fields.length < 7) {
            return "";
        }
        long socketIndex = NumberUtils.parseLong(fields[5]);
        return friend(
            NumberUtils.parseLong(fields[0]),
            fields[1],
            fields[2],
            fields[3],
            NumberUtils.parseLong(fields[4]),
            socketIndex > 0L ? 2L : 0L,
            socketIndex > 0L ? 1L : 0L,
            fields[6],
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

    public static String pendingRequests(String rowText) {
        return pendingRequests(PendingFriendRequest.listFromLegacy(rowText));
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
        String rowText,
        long maxFriends0,
        long maxFriends1,
        long maxFriends2,
        boolean followEnabled
    ) {
        long friendCount = 0L;
        PacketBuilder friendPayload = PacketBuilder.create();
        for (String row : StringUtils.text(rowText).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 7) {
                    long friendSocketIndex = NumberUtils.parseLong(fields[2]);
                    long friendOnline = friendSocketIndex > 0L ? 1L : 0L;
                    friendPayload.appendRaw(friend(
                        NumberUtils.parseLong(fields[0]),
                        fields[1],
                        fields[4],
                        fields[3],
                        NumberUtils.parseLong(fields[5]),
                        friendOnline == 1L ? 2L : 0L,
                        friendOnline,
                        fields[6],
                        1L,
                        followEnabled));
                    friendCount++;
                }
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

}
