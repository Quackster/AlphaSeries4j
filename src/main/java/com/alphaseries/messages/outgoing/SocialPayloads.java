package com.alphaseries.messages.outgoing;

import com.alphaseries.game.social.BadgeRow;
import com.alphaseries.protocol.PacketBuilder;
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

    public static String badgeInventory(String inventoryRows, String equippedPayload) {
        return badgeInventory(BadgeRow.listFromLegacy(inventoryRows), equippedPayload);
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

    public static String equippedBadges(String badgeRows) {
        return equippedBadges(BadgeRow.listFromLegacy(badgeRows));
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

    public static String tags(String tagRows) {
        long tagCount = 0L;
        PacketBuilder tagPayload = PacketBuilder.create();
        for (String row : StringUtils.text(tagRows).split("\r", -1)) {
            if (!row.isEmpty()) {
                tagPayload.appendString(row);
                tagCount++;
            }
        }
        return PacketBuilder.create()
            .appendInt(tagCount)
            .appendRaw(tagPayload)
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
