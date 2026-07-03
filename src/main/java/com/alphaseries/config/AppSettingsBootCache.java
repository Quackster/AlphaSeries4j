package com.alphaseries.config;

import com.alphaseries.dao.mysql.QuestDao;
import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.dao.mysql.SettingsDao;
import com.alphaseries.db.Database;
import com.alphaseries.db.MySQL;
import com.alphaseries.game.quest.QuestSettings;
import com.alphaseries.game.quest.QuestState;
import com.alphaseries.game.room.RoomPortalSettings;
import com.alphaseries.game.room.RoomState;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class AppSettingsBootCache {
    private AppSettingsBootCache() {
    }

    /**
     * Original function: Proc_1_9_6C6DF0.
     */
    public static void loadServerSettingsCache() {
        RoomDao rooms = roomDao();
        RoomPortalSettings portalSettings = RoomPortalSettings.fromRows(List.of(), List.of());
        if (rooms != null) {
            try {
                portalSettings = RoomPortalSettings.fromRows(rooms.warpSpaceRows(), rooms.specialGateRows());
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        RoomState.instance().setPortalSettings(portalSettings);
        loadPermissionMatrixCache();
        String systemDate = AppConfigState.instance().settingsCache().valueOrDefault("com.system.format.date", "");
        String systemTime = AppConfigState.instance().settingsCache().valueOrDefault("com.system.format.time", "");
        SettingsDao settings = settingsDao();
        List<SettingsDao.SettingRow> settingsRows = List.of();
        if (settings != null) {
            try {
                settingsRows = settings.allSettings();
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        AppConfigState.instance().setSettingsCache(buildSettings(settingsRows, systemDate, systemTime));
        List<QuestSettings.QuestDefinitionRow> questRows = List.of();
        QuestDao quests = questDao();
        if (quests != null) {
            try {
                questRows = questDefinitionRows(quests.questDefinitions());
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        QuestState.instance().setSettings(QuestSettings.fromDefinitions(questRows));
    }

    /**
     * Original function: Proc_1_16_6CCA60.
     */
    public static void loadPermissionMatrixCache() {
        List<PermissionMatrix.PermissionPayload> permissions = new ArrayList<>();
        SettingsDao settings = settingsDao();
        for (int rank = 0; rank <= 20; rank++) {
            for (int hc = 0; hc <= 2; hc++) {
                List<SettingsDao.PrivilegeRow> rows = List.of();
                if (settings != null) {
                    try {
                        rows = settings.levelPrivileges(rank, hc);
                    } catch (Exception ignored) {
                        // Legacy startup cache loading tolerated missing tables or SQL failures.
                    }
                }
                permissions.add(buildPermissionPayloadEntry(rank, hc, rows));
            }
        }
        AppConfigState.instance().setPermissionMatrix(PermissionMatrix.fromPayloadRows(permissions));
    }

    /**
     * Original function: Proc_1_16_6CCA60.
     */
    public static PermissionMatrix.PermissionPayload buildPermissionPayloadEntry(
            long rankIndex, long hcLevel, List<SettingsDao.PrivilegeRow> rows) {
        List<String> permissions = new ArrayList<>();
        if (rows != null) {
            for (SettingsDao.PrivilegeRow row : rows) {
                if (row != null && !StringUtils.text(row.privilege()).isEmpty()) {
                    permissions.add(StringUtils.text(row.privilege()));
                }
            }
        }
        return PermissionMatrix.PermissionPayload.ofPermissions(rankIndex, hcLevel, permissions);
    }

    /**
     * Original function: Proc_1_9_6C6DF0.
     */
    public static AppSettingsCache buildSettings(List<SettingsDao.SettingRow> settingsRows, String systemDateFormat,
            String systemTimeFormat) {
        return AppSettingsCache.fromSettings(buildSettingsMap(settingsRows, systemDateFormat, systemTimeFormat));
    }

    private static Map<String, String> buildSettingsMap(List<SettingsDao.SettingRow> settingsRows, String systemDateFormat,
            String systemTimeFormat) {
        Map<String, String> settings = new LinkedHashMap<>();
        if (settingsRows != null) {
            for (SettingsDao.SettingRow row : settingsRows) {
                if (row != null) {
                    settings.put(StringUtils.text(row.variableName()), StringUtils.text(row.value()));
                }
            }
        }
        settings.put("com.client.format.date", clientDateFormat(systemDateFormat));
        settings.put("com.client.format.time", clientTimeFormat(systemTimeFormat));
        settings.put("com.mysql.format.date", mysqlDateFormat(systemDateFormat));
        settings.put("com.mysql.format.time", mysqlTimeFormat(systemTimeFormat));
        return settings;
    }

    private static String clientDateFormat(String formatText) {
        return StringUtils.text(formatText).replace("d", "dd").replace("Y", "yyyy").replace("m", "mm");
    }

    private static String clientTimeFormat(String formatText) {
        return StringUtils.text(formatText).replace("i", "nn").replace("h", "hh").replace("s", "ss");
    }

    private static String mysqlDateFormat(String formatText) {
        return StringUtils.text(formatText).replace("d", "%d").replace("Y", "%Y").replace("m", "%m");
    }

    private static String mysqlTimeFormat(String formatText) {
        return StringUtils.text(formatText).replace("i", "%i").replace("h", "%H").replace("s", "%s");
    }

    private static List<QuestSettings.QuestDefinitionRow> questDefinitionRows(List<QuestDao.QuestDefinition> rows) {
        List<QuestSettings.QuestDefinitionRow> definitions = new ArrayList<>();
        if (rows != null) {
            for (QuestDao.QuestDefinition row : rows) {
                if (row != null) {
                    definitions.add(QuestSettings.QuestDefinitionRow.fromDefinition(row));
                }
            }
        }
        return definitions;
    }

    private static RoomDao roomDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new RoomDao(database);
    }

    private static QuestDao questDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new QuestDao(database);
    }

    private static SettingsDao settingsDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new SettingsDao(database);
    }
}
