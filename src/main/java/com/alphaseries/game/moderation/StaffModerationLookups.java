package com.alphaseries.game.moderation;

import java.util.ArrayList;
import java.util.List;

import com.alphaseries.dao.mysql.StaffModerationDao;

public final class StaffModerationLookups {
    private StaffModerationLookups() {
    }

    public static String roomChatHistoryResponse(
        StaffUserLookup targetUser,
        StaffModerationDao moderationDao
    ) {
        if (targetUser == null || targetUser.userId() <= 0L || moderationDao == null) {
            return "";
        }
        try {
            List<StaffRoomChatVisitRow> visitRows = moderationDao.recentChatHistoryVisits(targetUser.userId());
            List<StaffPayloads.ChatHistoryVisit> visits = new ArrayList<>();
            for (StaffRoomChatVisitRow row : visitRows) {
                List<StaffRoomChatRow> chatRows = moderationDao.chatRowsForVisit(
                    row.roomId(),
                    targetUser.userId(),
                    row.timestampEnter(),
                    row.timestampLeft());
                visits.add(new StaffPayloads.ChatHistoryVisit(row, chatRows));
            }
            return StaffPayloads.roomChatHistoryResponse(targetUser, visits);
        } catch (Exception ignored) {
            return "";
        }
    }

    public static String roomVisitHistoryResponse(
        StaffUserLookup targetUser,
        StaffModerationDao moderationDao
    ) {
        if (targetUser == null || targetUser.userId() <= 0L || moderationDao == null) {
            return "";
        }
        try {
            List<StaffRoomVisitRow> visitRows = moderationDao.recentRoomVisits(targetUser.userId());
            return StaffPayloads.roomVisitHistoryResponse(targetUser, visitRows);
        } catch (Exception ignored) {
            return "";
        }
    }
}
