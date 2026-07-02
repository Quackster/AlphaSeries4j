package com.alphaseries.game.achievement;

import com.alphaseries.config.AppConfigState;
import com.alphaseries.config.AppSettingsBootCache;
import com.alphaseries.dao.mysql.AchievementDao;
import com.alphaseries.db.Database;
import com.alphaseries.db.MySQL;
import com.alphaseries.game.advertising.AdvertisingBootCache;
import com.alphaseries.game.catalog.CatalogGiftBootCache;
import com.alphaseries.game.chat.ChatBootCache;
import com.alphaseries.game.help.HelpCenterBootCache;
import com.alphaseries.game.messenger.MessengerSettings;
import com.alphaseries.game.messenger.MessengerState;
import com.alphaseries.game.moderation.StaffModerationBootCache;
import com.alphaseries.game.pet.PetBootCache;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class AchievementBootCache {
    private AchievementBootCache() {
    }

    public record AchievementSettingsCache(String questIdPayload, List<AchievementSettings.Achievement> achievements) {
        public AchievementSettingsCache {
            questIdPayload = StringUtils.text(questIdPayload);
            achievements = achievements == null ? List.of() : List.copyOf(achievements);
        }

        public static AchievementSettingsCache empty() {
            return new AchievementSettingsCache("", List.of());
        }
    }

    /**
     * Original function: Proc_1_5_6C4F80.
     */
    public static void loadBonusSystemCache() {
        AchievementSettingsCache achievementCache = AchievementSettingsCache.empty();
        AchievementDao achievements = achievementDao();
        if (achievements != null) {
            try {
                achievementCache = buildAchievementSettingsCache(achievements.enabledSettingsRows());
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        AchievementState.instance().setSettings(AchievementSettings.fromAchievements(
            achievementCache.questIdPayload(),
            achievementCache.achievements()));
        AppSettingsBootCache.loadServerSettingsCache();
        PetBootCache.loadPetLevelAndCommandCache();
        CatalogGiftBootCache.loadClubGiftCache();
        AppSettingsBootCache.loadPermissionMatrixCache();
        PetBootCache.loadPetRaceCache();
        StaffModerationBootCache.loadStaffModerationCache();
        HelpCenterBootCache.loadImportantFaqCache();
        HelpCenterBootCache.loadFaqCategoryCache();
        HelpCenterBootCache.loadFaqDescriptionCache();
        CatalogGiftBootCache.loadGiftWrapCache();
        AdvertisingBootCache.loadVisitRoomAdsCache();
        ChatBootCache.buildChatSettingsCache();
        MessengerState.instance().setSettings(MessengerSettings.fromLimits(
            AppConfigState.instance().settingsCache().longValueOrDefault("com.client.messenger.maxfriends.hclevel0", 0),
            0L,
            AppConfigState.instance().settingsCache().longValueOrDefault("com.client.messenger.maxfriends.hclevel1", 0),
            0L,
            AppConfigState.instance().settingsCache().longValueOrDefault("com.client.messenger.maxfriends.hclevel2", 0)));
    }

    /**
     * Original function: Proc_1_5_6C4F80.
     */
    public static AchievementSettingsCache buildAchievementSettingsCache(
            List<AchievementDao.AchievementSettingsRow> achievementRows) {
        long achievementIndex = 0L;
        PacketBuilder questIds = PacketBuilder.create();
        List<AchievementSettings.Achievement> achievements = new ArrayList<AchievementSettings.Achievement>();
        if (achievementRows != null) {
            for (AchievementDao.AchievementSettingsRow row : achievementRows) {
                if (achievementIndex > 100L) {
                    break;
                }
                if (row != null) {
                    questIds.appendString(row.questId());
                    achievements.add(new AchievementSettings.Achievement(row.questId(), StringUtils.text(row.badgeId()),
                        row.progress(), row.rewardIncrease(), row.levelTotal(), row.scoreIncrease(), row.rewardType()));
                    achievementIndex++;
                }
            }
        }
        return new AchievementSettingsCache(questIds.build(), achievements);
    }

    /**
     * Original function: Proc_1_5_6C4F80.
     */
    public static int[] buildMessengerFriendLimitCache(long hcLevel0, long hcLevel1, long hcLevel2) {
        int[] limits = new int[5];
        limits[0] = (int) hcLevel0;
        limits[2] = (int) hcLevel1;
        limits[4] = (int) hcLevel2;
        return limits;
    }

    private static AchievementDao achievementDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new AchievementDao(database);
    }
}
