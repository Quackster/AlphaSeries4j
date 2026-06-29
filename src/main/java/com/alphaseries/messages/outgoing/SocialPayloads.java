package com.alphaseries.messages.outgoing;

import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

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
        long inventoryCount = 0L;
        PacketBuilder inventoryPayload = PacketBuilder.create();
        for (String row : StringUtils.text(inventoryRows).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                String badgeId = StringUtils.field(fields, 0);
                long badgeRowId = NumberUtils.parseLong(StringUtils.field(fields, 2));
                if (!badgeId.isEmpty()) {
                    inventoryPayload.appendRaw('0').appendInt(badgeRowId).appendString(badgeId);
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
        long equippedCount = 0L;
        PacketBuilder equippedPayload = PacketBuilder.create();
        for (String row : StringUtils.text(badgeRows).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                String badgeId = StringUtils.field(fields, 0);
                long badgeSlot = NumberUtils.parseLong(StringUtils.field(fields, 1));
                if (!badgeId.isEmpty()) {
                    equippedPayload.appendRaw('0').appendInt(badgeSlot).appendString(badgeId);
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

    public static String tagDisplay(long userId, String tagPayload) {
        return PacketBuilder.message("E^")
            .appendInt(userId)
            .appendRaw(tagPayload)
            .build();
    }

}
