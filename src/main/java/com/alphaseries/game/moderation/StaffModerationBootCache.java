package com.alphaseries.game.moderation;

import com.alphaseries.config.AppConfigState;
import com.alphaseries.dao.mysql.StaffModerationDao;
import com.alphaseries.db.Database;
import com.alphaseries.db.MySQL;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class StaffModerationBootCache {
    private StaffModerationBootCache() {
    }

    public static void loadStaffModerationCache() {
        String callForHelpMessages = "";
        String moderatorMessages = "";
        StaffModerationDao moderation = staffModerationDao();
        if (moderation != null) {
            try {
                callForHelpMessages = buildStaffMessageList(moderation.staffMessages(1L));
                moderatorMessages = buildStaffMessageList(moderation.staffMessages(2L));
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        String categoryPayload = "";
        List<StaffSettings.ModerationPayload> values = new ArrayList<StaffSettings.ModerationPayload>();
        for (int rank = 0; rank <= 20; rank++) {
            for (int hc = 0; hc <= 2; hc++) {
                String payload = appendPermissionPayload(rank, hc, "fuse_mod", callForHelpMessages + categoryPayload)
                    + appendPermissionPayload(rank, hc, "fuse_receive_calls_for_help", callForHelpMessages)
                    + appendPermissionPayload(rank, hc, "fuse_chatlog", "")
                    + appendPermissionPayload(rank, hc, "fuse_alert", "")
                    + appendPermissionPayload(rank, hc, "fuse_kick", "")
                    + appendPermissionPayload(rank, hc, "fuse_ban", "")
                    + appendPermissionPayload(rank, hc, "fuse_room_alert", "")
                    + appendPermissionPayload(rank, hc, "fuse_room_kick", "")
                    + appendPermissionPayload(rank, hc, "fuse_edit_localizations", moderatorMessages);
                values.add(StaffSettings.ModerationPayload.fromPayloadText(rank, hc, payload));
            }
        }
        ModerationState.instance().setStaffSettings(StaffSettings.fromPayloadRows(values));
    }

    private static String buildStaffMessageList(List<StaffModerationDao.StaffMessageRow> rows) {
        PacketBuilder payload = PacketBuilder.create();
        if (rows != null) {
            for (StaffModerationDao.StaffMessageRow row : rows) {
                if (row != null && !StringUtils.text(row.message()).isEmpty()) {
                    payload.appendString(row.message());
                }
            }
        }
        return payload.build();
    }

    private static String appendPermissionPayload(long rankIndex, long hcLevel, String permissionName, String payload) {
        if (AppConfigState.instance().permissionMatrix().allows(rankIndex, "", permissionName, hcLevel)) {
            return permissionName + '\2' + StringUtils.text(payload);
        }
        return "";
    }

    private static StaffModerationDao staffModerationDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new StaffModerationDao(database);
    }
}
