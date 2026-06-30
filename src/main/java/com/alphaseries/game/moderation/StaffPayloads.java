package com.alphaseries.game.moderation;

import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.protocol.PacketReader;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class StaffPayloads {
    private StaffPayloads() {
    }

    public static final class ChatRows {
        public long chatCount;
        public String payload = "";
    }

    public static String callForHelpRow(StaffCallForHelpRow row, Map<Long, String> userNamesById) {
        long callForHelpId = row.callForHelpId();
        long callerId = row.callerUserId();
        String callerName = row.callerName();
        long partnerId = row.partnerUserId();
        long roomId = row.roomId();
        long categoryId = row.categoryId();
        String descriptionText = row.description();
        String roomName = row.roomName();
        long pickerId = row.pickerUserId();
        String partnerName = userNamesById != null && partnerId > 0L ? StringUtils.text(userNamesById.get(partnerId)) : "";
        String pickerName = userNamesById != null && pickerId > 0L ? StringUtils.text(userNamesById.get(pickerId)) : "";
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
        List<Long> callForHelpIds = callForHelpIds(packetPayload);
        if (callForHelpIds.isEmpty()) {
            return "";
        }
        StringBuilder whereClause = new StringBuilder();
        for (long callForHelpId : callForHelpIds) {
            if (whereClause.length() > 0) {
                whereClause.append(" OR ");
            }
            whereClause.append("id='").append(callForHelpId).append('\'');
        }
        return whereClause.toString();
    }

    public static List<Long> callForHelpIds(String packetPayload) {
        PacketReader reader = PacketReader.of(packetPayload);
        long requestedCount = reader.readInt();
        if (requestedCount < 1L || requestedCount > 150L) {
            return List.of();
        }
        List<Long> callForHelpIds = new ArrayList<>();
        for (long requestIndex = 0L; requestIndex < requestedCount; requestIndex++) {
            long callForHelpId = reader.readInt();
            if (callForHelpId <= 0L) {
                return List.of();
            }
            callForHelpIds.add(callForHelpId);
        }
        return callForHelpIds;
    }

    public static String userSummary(
        StaffUserSummaryRow row,
        long callForHelpCount,
        long pickedCallForHelpCount,
        long cautionCount,
        long banCount
    ) {
        return PacketBuilder.message("HU")
            .appendInt(row.userId())
            .appendString(row.userName())
            .appendInt(row.createdMinutes())
            .appendInt(row.lastOnlineMinutes())
            .appendBoolean(row.socketIndex() > 0L)
            .appendInt(callForHelpCount)
            .appendInt(pickedCallForHelpCount)
            .appendInt(cautionCount)
            .appendInt(banCount)
            .build();
    }

    public static String roomVisit(StaffRoomVisitRow row) {
        return PacketBuilder.create()
            .appendInt(row.modelType())
            .appendInt(row.roomId())
            .appendInt(row.hour())
            .appendInt(row.minute())
            .appendString(row.roomName())
            .build();
    }

    public static ChatRows roomChatRows(String chatRows) {
        List<StaffRoomChatRow> rows = new ArrayList<>();
        for (String row : StringUtils.text(chatRows).split("\r", -1)) {
            String rowValue = row.trim();
            if (!rowValue.isEmpty()) {
                StaffRoomChatRow chatRow = StaffRoomChatRow.fromLegacy(rowValue);
                if (chatRow != null) {
                    rows.add(chatRow);
                }
            }
        }
        return roomChatRows(rows);
    }

    public static ChatRows roomChatRows(List<StaffRoomChatRow> rows) {
        ChatRows result = new ChatRows();
        PacketBuilder chatPayload = PacketBuilder.create();
        for (StaffRoomChatRow row : rows) {
            chatPayload.appendInt(row.hour())
                .appendInt(row.minute())
                .appendInt(row.userId())
                .appendString(row.userName())
                .appendString(row.description());
            result.chatCount++;
        }
        result.payload = chatPayload.build();
        return result;
    }

    public static String roomChatHistory(String visitRowText, String chatRows) {
        String[] fields = StringUtils.text(visitRowText).split("\t", -1);
        ChatRows chatRowsPayload = roomChatRows(chatRows);
        return PacketBuilder.create()
            .appendInt(NumberUtils.parseLong(StringUtils.field(fields, 0)))
            .appendInt(NumberUtils.parseLong(StringUtils.field(fields, 1)))
            .appendInt(chatRowsPayload.chatCount)
            .appendString(StringUtils.field(fields, 2))
            .appendRaw(chatRowsPayload.payload)
            .build();
    }

    public static String roomChatHistory(StaffRoomChatVisitRow visitRow, List<StaffRoomChatRow> chatRows) {
        ChatRows chatRowsPayload = roomChatRows(chatRows);
        return PacketBuilder.create()
            .appendInt(visitRow.modelType())
            .appendInt(visitRow.roomId())
            .appendInt(chatRowsPayload.chatCount)
            .appendString(visitRow.roomName())
            .appendRaw(chatRowsPayload.payload)
            .build();
    }

    public static boolean containsUnsafeAlert(String messageText) {
        String lowerMessage = StringUtils.text(messageText).toLowerCase();
        return lowerMessage.contains("cookie") && lowerMessage.contains("javascript:");
    }
}
