package com.alphaseries.game.moderation;

import com.alphaseries.dao.mysql.StaffModerationDao;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.StringUtils;

import java.util.List;
import java.util.Map;

public final class StaffPayloads {
    private StaffPayloads() {
    }

    public record ChatRows(long chatCount, String payload) {
        public ChatRows {
            payload = StringUtils.text(payload);
        }
    }

    public record ChatHistoryVisit(StaffRoomChatVisitRow visitRow, List<StaffRoomChatRow> chatRows) {
        public ChatHistoryVisit {
            chatRows = chatRows == null ? List.of() : List.copyOf(chatRows);
        }
    }

    public static String callForHelpNotification(StaffCallForHelpRow row, Map<Long, String> userNamesById) {
        return callForHelpNotification(callForHelpRow(row, userNamesById));
    }

    private static String callForHelpNotification(String rowPayload) {
        return PacketBuilder.message("HR")
            .appendRaw(rowPayload)
            .build();
    }

    public static String callForHelpClosed(long closeState) {
        return PacketBuilder.message("H\\")
            .appendInt(closeState)
            .build();
    }

    public static String callForHelpDeleted() {
        return "E@";
    }

    public static String callForHelpCreated(long callForHelpId) {
        return PacketBuilder.message("EA")
            .appendInt(callForHelpId)
            .build();
    }

    public static String alert(String messageText) {
        return PacketBuilder.message("Ba")
            .appendString(messageText)
            .build();
    }

    public static String moderationPanel(StaffSettings staffSettings, long rankIndex, long hcLevel) {
        return moderationPanelPayload(staffSettings == null ? "" : staffSettings.moderationPayload(rankIndex, hcLevel));
    }

    private static String moderationPanelPayload(String moderationPayload) {
        return PacketBuilder.message("HS")
            .appendInt(0L)
            .appendInt(0L)
            .appendRaw(moderationPayload)
            .build();
    }

    public static String callForHelpChatLogResponse(
        long callForHelpId,
        StaffModerationDao.CallForHelpRoom room,
        List<StaffRoomChatRow> chatRows
    ) {
        return PacketBuilder.message("HV")
            .appendInt(callForHelpId)
            .appendInt(room.roomId())
            .appendInt(room.modelType())
            .appendInt(room.userId())
            .appendInt(room.partnerId())
            .appendString(room.roomName())
            .appendRaw(roomChatRows(chatRows == null ? List.of() : chatRows).payload())
            .build();
    }

    public static String roomChatLogResponse(StaffModerationDao.RoomChatHeader room, List<StaffRoomChatRow> chatRows) {
        return PacketBuilder.message("HW")
            .appendInt(room.roomId())
            .appendInt(room.modelType())
            .appendString(room.roomName())
            .appendRaw(roomChatRows(chatRows == null ? List.of() : chatRows).payload())
            .build();
    }

    public static String roomInfoResponse(StaffModerationDao.RoomInfo room, StaffModerationDao.RoomEvent event) {
        PacketBuilder payload = PacketBuilder.message("HZ")
            .appendInt(room.roomId())
            .appendInt(room.visitorsNow())
            .appendInt(room.ownerId())
            .appendString(room.ownerName())
            .appendString(room.roomName())
            .appendString(room.description())
            .appendString(room.tag1())
            .appendString(room.tag2());

        boolean hasEvent = event != null;
        payload.appendBoolean(hasEvent);
        if (hasEvent) {
            payload.appendString(event.name())
                .appendString(event.description())
                .appendString(event.tag1())
                .appendString(event.tag2());
        }
        return payload.build();
    }

    private static String callForHelpRow(StaffCallForHelpRow row, Map<Long, String> userNamesById) {
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

    /**
     * Original function: Proc_6_29_70D800.
     */
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

    public static ChatRows roomChatRows(List<StaffRoomChatRow> rows) {
        PacketBuilder chatPayload = PacketBuilder.create();
        long chatCount = 0L;
        if (rows == null) {
            return new ChatRows(chatCount, chatPayload.build());
        }
        for (StaffRoomChatRow row : rows) {
            chatPayload.appendInt(row.hour())
                .appendInt(row.minute())
                .appendInt(row.userId())
                .appendString(row.userName())
                .appendString(row.description());
            chatCount++;
        }
        return new ChatRows(chatCount, chatPayload.build());
    }

    public static String roomChatHistory(StaffRoomChatVisitRow visitRow, List<StaffRoomChatRow> chatRows) {
        ChatRows chatRowsPayload = roomChatRows(chatRows);
        return PacketBuilder.create()
            .appendInt(visitRow.modelType())
            .appendInt(visitRow.roomId())
            .appendInt(chatRowsPayload.chatCount())
            .appendString(visitRow.roomName())
            .appendRaw(chatRowsPayload.payload())
            .build();
    }

    public static String roomChatHistoryResponse(StaffUserLookup targetUser, List<ChatHistoryVisit> visits) {
        PacketBuilder rowPayload = PacketBuilder.create();
        long rowCount = 0L;
        if (visits != null) {
            for (ChatHistoryVisit visit : visits) {
                if (visit != null && visit.visitRow() != null) {
                    rowPayload.appendRaw(roomChatHistory(visit.visitRow(), visit.chatRows()));
                    rowCount++;
                }
            }
        }
        return roomChatHistoryResponse(targetUser, rowCount, rowPayload.build());
    }

    private static String roomChatHistoryResponse(StaffUserLookup targetUser, long rowCount, String rowPayload) {
        return PacketBuilder.message("HX")
            .appendInt(targetUser.userId())
            .appendString(targetUser.userName())
            .appendInt(rowCount)
            .appendRaw(rowPayload)
            .build();
    }

    public static String roomVisitHistoryResponse(StaffUserLookup targetUser, List<StaffRoomVisitRow> visitRows) {
        PacketBuilder rowPayload = PacketBuilder.create();
        long rowCount = 0L;
        if (visitRows != null) {
            for (StaffRoomVisitRow row : visitRows) {
                if (row != null) {
                    rowPayload.appendRaw(roomVisit(row));
                    rowCount++;
                }
            }
        }
        return roomVisitHistoryResponse(targetUser, rowCount, rowPayload.build());
    }

    private static String roomVisitHistoryResponse(StaffUserLookup targetUser, long rowCount, String rowPayload) {
        return PacketBuilder.message("HY")
            .appendInt(targetUser.userId())
            .appendString(targetUser.userName())
            .appendInt(rowCount)
            .appendRaw(rowPayload)
            .build();
    }

    public static boolean containsUnsafeAlert(String messageText) {
        String lowerMessage = StringUtils.text(messageText).toLowerCase();
        return lowerMessage.contains("cookie") && lowerMessage.contains("javascript:");
    }
}
