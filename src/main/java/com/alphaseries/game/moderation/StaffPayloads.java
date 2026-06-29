package com.alphaseries.game.moderation;

import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.protocol.PacketReader;
import com.alphaseries.protocol.WireEncoding;

import java.util.Map;

public final class StaffPayloads {
    private StaffPayloads() {
    }

    public static final class ChatRows {
        public long chatCount;
        public String payload = "";
    }

    public static String callForHelpRow(String rowText, Map<Long, String> userNamesById) {
        String[] fields = text(rowText).split("\t", -1);
        long callForHelpId = number(field(fields, 0));
        long callerId = number(field(fields, 2));
        String callerName = field(fields, 3);
        long partnerId = number(field(fields, 4));
        long roomId = number(field(fields, 5));
        long categoryId = number(field(fields, 6));
        String descriptionText = field(fields, 7);
        String roomName = field(fields, 9);
        long pickerId = number(field(fields, 10));
        String partnerName = userNamesById != null && partnerId > 0L ? text(userNamesById.get(partnerId)) : "";
        String pickerName = userNamesById != null && pickerId > 0L ? text(userNamesById.get(pickerId)) : "";
        return callForHelp(0, 0, categoryId, callerId, callerName, partnerId, partnerName,
            descriptionText, roomId, roomName, callForHelpId, pickerName);
    }

    public static String callForHelp(
        long baseValue,
        long firstValue,
        long secondValue,
        long thirdValue,
        String fourthValue,
        long fifthValue,
        String sixthValue,
        String seventhValue,
        long eighthValue,
        String ninthValue,
        long tenthValue,
        String eleventhValue
    ) {
        return PacketBuilder.create()
            .appendRaw('0')
            .appendRaw('0')
            .appendInt(baseValue)
            .appendInt(firstValue)
            .appendRaw('H')
            .appendInt(secondValue)
            .appendRaw('H')
            .appendInt(baseValue)
            .appendInt(thirdValue)
            .appendString(fourthValue)
            .appendInt(fifthValue)
            .appendString(sixthValue)
            .appendInt(eighthValue)
            .appendString(ninthValue)
            .appendString(seventhValue)
            .appendInt(tenthValue)
            .appendString(eleventhValue)
            .build();
    }

    public static String callForHelpWhereClause(String packetPayload) {
        PacketReader reader = PacketReader.of(packetPayload);
        long requestedCount = reader.readInt();
        if (requestedCount < 1L || requestedCount > 150L) {
            return "";
        }
        StringBuilder whereClause = new StringBuilder();
        for (long requestIndex = 0L; requestIndex < requestedCount; requestIndex++) {
            long callForHelpId = reader.readInt();
            if (callForHelpId <= 0L) {
                return "";
            }
            if (whereClause.length() > 0) {
                whereClause.append(" OR ");
            }
            whereClause.append("id='").append(callForHelpId).append('\'');
        }
        return whereClause.toString();
    }

    public static String userSummary(
        String rowText,
        long callForHelpCount,
        long pickedCallForHelpCount,
        long cautionCount,
        long banCount
    ) {
        String[] fields = text(rowText).split("\t", -1);
        long userId = number(field(fields, 0));
        String userName = field(fields, 1);
        long createdMinutes = number(field(fields, 2));
        long lastOnlineMinutes = number(field(fields, 3));
        long socketIndex = number(field(fields, 4));
        return PacketBuilder.message("HU")
            .appendInt(userId)
            .appendString(userName)
            .appendInt(createdMinutes)
            .appendInt(lastOnlineMinutes)
            .appendBoolean(socketIndex > 0L)
            .appendInt(callForHelpCount)
            .appendInt(pickedCallForHelpCount)
            .appendInt(cautionCount)
            .appendInt(banCount)
            .build();
    }

    public static String roomVisit(String rowText) {
        String[] fields = text(rowText).split("\t", -1);
        return PacketBuilder.create()
            .appendInt(number(field(fields, 0)))
            .appendInt(number(field(fields, 1)))
            .appendInt(number(field(fields, 3)))
            .appendInt(number(field(fields, 4)))
            .appendString(field(fields, 2))
            .build();
    }

    public static ChatRows roomChatRows(String chatRows) {
        ChatRows result = new ChatRows();
        PacketBuilder chatPayload = PacketBuilder.create();
        for (String row : text(chatRows).split("\r", -1)) {
            String rowValue = row.trim();
            if (!rowValue.isEmpty()) {
                String[] fields = rowValue.split("\t", -1);
                if (fields.length >= 5) {
                    chatPayload.appendInt(number(field(fields, 0)))
                        .appendInt(number(field(fields, 1)))
                        .appendInt(number(field(fields, 2)))
                        .appendString(field(fields, 3))
                        .appendString(field(fields, 4));
                    result.chatCount++;
                }
            }
        }
        result.payload = chatPayload.build();
        return result;
    }

    public static String roomChatHistory(String visitRowText, String chatRows) {
        String[] fields = text(visitRowText).split("\t", -1);
        ChatRows chatRowsPayload = roomChatRows(chatRows);
        return PacketBuilder.create()
            .appendInt(number(field(fields, 0)))
            .appendInt(number(field(fields, 1)))
            .appendInt(chatRowsPayload.chatCount)
            .appendString(field(fields, 2))
            .appendRaw(chatRowsPayload.payload)
            .build();
    }

    public static boolean containsUnsafeAlert(String messageText) {
        String lowerMessage = text(messageText).toLowerCase();
        return lowerMessage.contains("cookie") && lowerMessage.contains("javascript:");
    }

    private static String field(String[] fields, int index) {
        return fields != null && index >= 0 && index < fields.length ? text(fields[index]) : "";
    }

    private static long number(Object value) {
        return WireEncoding.parseLeadingLong(value);
    }

    private static String text(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
