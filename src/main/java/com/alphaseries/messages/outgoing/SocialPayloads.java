package com.alphaseries.messages.outgoing;

import com.alphaseries.game.social.BadgeRow;
import com.alphaseries.game.room.RoomObjectEntryPayloadArgs;
import com.alphaseries.game.room.RoomUserEntryPayloadArgs;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.List;

public final class SocialPayloads {
    private SocialPayloads() {
    }

    public static String roomUserProfile(
        long roomUserIndex,
        String userName,
        String mottoText,
        long achievementScore,
        String figureText
    ) {
        if (roomUserIndex <= 0L) {
            return "";
        }
        return PacketBuilder.message("Jf")
            .appendInt(roomUserIndex)
            .appendString(userName)
            .appendString(mottoText)
            .appendInt(achievementScore)
            .appendString(figureText)
            .build();
    }

    public static String roomUserEffect(long roomUserIndex, long effectId) {
        return PacketBuilder.message("Ge")
            .appendInt(roomUserIndex)
            .appendInt(effectId)
            .build();
    }

    public static String roomUserEffectCleared(long roomUserIndex) {
        return PacketBuilder.message("Ge")
            .appendInt(roomUserIndex)
            .appendRaw('H')
            .build();
    }

    public static String roomUserStatus(long roomUserIndex, long statusCode) {
        if (roomUserIndex <= 0L) {
            return "";
        }
        return PacketBuilder.create()
            .appendRaw('0')
            .appendRaw("Ge")
            .appendInt(roomUserIndex)
            .appendInt(Math.max(0L, statusCode))
            .build();
    }

    public static String roomUserWave(long roomUserIndex) {
        return PacketBuilder.message("Ga")
            .appendInt(roomUserIndex)
            .build();
    }

    public static String roomUserDance(long roomUserIndex, long danceId) {
        return PacketBuilder.message("G`")
            .appendInt(roomUserIndex)
            .appendInt(danceId)
            .build();
    }

    public static String roomUserRemoved(long roomUserIndex) {
        return PacketBuilder.message("@\\")
            .appendInt(roomUserIndex)
            .build();
    }

    public static String roomUserPreReadyState(long roomUserIndex) {
        return PacketBuilder.message("Ei")
            .appendInt(roomUserIndex)
            .appendRaw('\r')
            .build();
    }

    public static String roomUserEntry(RoomUserEntryPayloadArgs values) {
        if (values == null) {
            return "";
        }
        long userId = NumberUtils.parseLong(values.userId());
        long roomUserIndex = NumberUtils.parseLong(values.roomUserIndex());
        if (roomUserIndex <= 0L) {
            roomUserIndex = userId;
        }
        return PacketBuilder.create()
            .appendRaw('0')
            .appendRaw('0')
            .appendInt(userId)
            .appendString(values.userName())
            .appendString(values.figure())
            .appendString(values.motto())
            .appendInt(roomUserIndex)
            .appendInt(NumberUtils.parseLong(values.positionX()))
            .appendInt(NumberUtils.parseLong(values.positionY()))
            .appendString(values.positionZ())
            .appendRaw("JI")
            .appendString(values.gender())
            .appendRaw('M')
            .appendInt(NumberUtils.parseLong(values.firstState()))
            .appendString("M")
            .appendInt(NumberUtils.parseLong(values.secondState()))
            .build();
    }

    public static String roomObjectEntry(RoomObjectEntryPayloadArgs values) {
        if (values == null) {
            return "";
        }
        long entityId = NumberUtils.parseLong(values.entityId());
        long roomUserIndex = NumberUtils.parseLong(values.roomUserIndex());
        if (roomUserIndex <= 0L) {
            roomUserIndex = entityId;
        }
        long objectType = NumberUtils.parseLong(values.objectType());
        PacketBuilder payload = PacketBuilder.create();
        String tailMarker;
        if (objectType == 3L) {
            payload.appendInt(entityId);
            tailMarker = "PAJJ";
        } else {
            payload.appendRaw('M');
            tailMarker = "HK";
        }
        return payload.appendString(values.displayName())
            .appendString(values.figure())
            .appendString(values.gender())
            .appendInt(roomUserIndex)
            .appendInt(NumberUtils.parseLong(values.positionX()))
            .appendInt(NumberUtils.parseLong(values.positionY()))
            .appendString(values.positionZ())
            .appendRaw(tailMarker)
            .build();
    }

    public static String roomOccupantStatus(
        long roomUserIndex,
        long positionX,
        long positionY,
        String positionZ,
        long direction
    ) {
        return PacketBuilder.create()
            .appendInt(roomUserIndex)
            .appendRaw(' ')
            .appendRaw(positionX)
            .appendRaw(' ')
            .appendRaw(positionY)
            .appendRaw(' ')
            .appendRaw(positionZ)
            .appendRaw(' ')
            .appendRaw(direction)
            .appendRaw(' ')
            .appendRaw(direction)
            .appendRaw('/')
            .appendRaw('\r')
            .build();
    }

    public static String interactionStateForSource(long sourceRoomUserIndex, long interactionState) {
        return PacketBuilder.create()
            .appendRaw('0')
            .appendRaw('0')
            .appendRaw("Am")
            .appendInt(sourceRoomUserIndex)
            .appendInt(interactionState)
            .build();
    }

    public static String interactionStateForTarget(long sourceRoomUserIndex, long interactionState) {
        return PacketBuilder.message("Am")
            .appendInt(sourceRoomUserIndex)
            .appendInt(interactionState)
            .build();
    }

    public static String interactionRequest(long sourceUserId, long targetUserId) {
        return PacketBuilder.message("Ah")
            .appendInt(sourceUserId)
            .appendInt(targetUserId)
            .build();
    }

    public static String interactionClosed(long sourceRoomUserIndex) {
        return PacketBuilder.create()
            .appendRaw('0')
            .appendRaw("An")
            .appendInt(sourceRoomUserIndex)
            .build();
    }

    public static String badgeInventory(List<BadgeRow> inventoryRows, String equippedPayload) {
        long inventoryCount = 0L;
        PacketBuilder inventoryPayload = PacketBuilder.create();
        if (inventoryRows != null) {
            for (BadgeRow row : inventoryRows) {
                if (row != null && !StringUtils.text(row.badgeId()).isEmpty()) {
                    inventoryPayload.appendRaw('0').appendInt(row.rowId()).appendString(row.badgeId());
                    inventoryCount++;
                }
            }
        }
        return PacketBuilder.message("Ce")
            .appendInt(inventoryCount)
            .appendRaw(inventoryPayload)
            .appendRaw(equippedPayload)
            .build();
    }

    public static String equippedBadges(List<BadgeRow> badgeRows) {
        long equippedCount = 0L;
        PacketBuilder equippedPayload = PacketBuilder.create();
        if (badgeRows != null) {
            for (BadgeRow row : badgeRows) {
                if (row != null && !StringUtils.text(row.badgeId()).isEmpty()) {
                    equippedPayload.appendRaw('0').appendInt(row.slot()).appendString(row.badgeId());
                    equippedCount++;
                }
            }
        }
        return PacketBuilder.create()
            .appendInt(equippedCount)
            .appendRaw(equippedPayload)
            .build();
    }

    public static String badgeDisplay(long userId, String equippedPayload) {
        return PacketBuilder.message("Cd")
            .appendInt(userId)
            .appendRaw(equippedPayload)
            .build();
    }

    public static String tags(List<String> tagRows) {
        long tagCount = 0L;
        PacketBuilder tagPayload = PacketBuilder.create();
        if (tagRows != null) {
            for (String row : tagRows) {
                String tag = StringUtils.text(row);
                if (!tag.isEmpty()) {
                    tagPayload.appendString(tag);
                    tagCount++;
                }
            }
        }
        return PacketBuilder.create()
            .appendInt(tagCount)
            .appendRaw(tagPayload)
            .build();
    }

    public static String tagDisplay(long userId, String tagPayload) {
        return PacketBuilder.message("E^")
            .appendInt(userId)
            .appendRaw(tagPayload)
            .build();
    }

}
