package com.alphaseries;

import com.alphaseries.config.PermissionMatrix;
import com.alphaseries.dao.mysql.AdvertisingDao;
import com.alphaseries.dao.mysql.AchievementDao;
import com.alphaseries.dao.mysql.BotDao;
import com.alphaseries.dao.mysql.CatalogDao;
import com.alphaseries.dao.mysql.ChatDao;
import com.alphaseries.dao.mysql.ClubDao;
import com.alphaseries.dao.mysql.HelpDao;
import com.alphaseries.dao.mysql.PackageDao;
import com.alphaseries.dao.mysql.QuestDao;
import com.alphaseries.dao.mysql.RecyclerDao;
import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.dao.mysql.ServerMaintenanceDao;
import com.alphaseries.dao.mysql.StaffModerationDao;
import com.alphaseries.dao.mysql.SettingsDao;
import com.alphaseries.db.Database;
import com.alphaseries.game.achievement.AchievementSettings;
import com.alphaseries.game.catalog.CatalogPages;
import com.alphaseries.game.catalog.GiftSettings;
import com.alphaseries.game.chat.ChatSettings;
import com.alphaseries.game.messenger.MessengerSettings;
import com.alphaseries.game.moderation.StaffSettings;
import com.alphaseries.game.navigator.NavigatorState;
import com.alphaseries.game.navigator.RoomCategoryCache;
import com.alphaseries.game.pet.PetCommandCacheRow;
import com.alphaseries.game.pet.PetLevelCacheRow;
import com.alphaseries.game.pet.PetRaceCacheRow;
import com.alphaseries.game.pet.PetSettings;
import com.alphaseries.game.quest.QuestSettings;
import com.alphaseries.game.recycler.RecyclerSettings;
import com.alphaseries.game.room.RoomEventLocales;
import com.alphaseries.game.room.RoomPortalSettings;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

public final class Boot {
    public static final String DEFAULT_BOOT_NOTICE = "ILLEGAL KOMBINATION: Der Programmierer haftet nicht f\u00fcr das "
        + "Nutzen dieses Servers - Das nutzen mit rechtlich gesch\u00fctzten Bildern ist strafbar - Bitte eigene "
        + "Software nutzen";
    public static final String INITIALIZATION_INTEGRITY_FAILURE_MESSAGE =
        "Unable to intialize. File may be corrupted!";
    private static final String[] STARTUP_CREDIT_LINES = new String[] {
        "                                                           2 . 0 - \"Meilenstein 2\"",
        "         Server Autor: Privilege, Deutsche \u00dcbersetzung: Medaillon",
        "         Shoutouts: Tweeney, Pure, MoBaT, Donkjam, Arths, Jairo, Moogly and Bloopser"
    };
    public static final String SERVER_RETURNED_ERROR_PREFIX = "Server has Exit Suburned following error:       ";

    private Boot() {
    }

    public static void Proc_1_0_6BA9D0(Object... args) {
        List<RecyclerSettings.RewardGroup> rewardGroups = List.of();
        RecyclerDao recycler = recyclerDao();
        if (recycler != null) {
            try {
                List<RecyclerSettings.RewardGroup> loadedGroups = new ArrayList<RecyclerSettings.RewardGroup>();
                List<Long> chances = recycler.recyclerChances();
                for (Long chanceValue : chances) {
                    long chance = chanceValue == null ? 0L : chanceValue.longValue();
                    if (chance != 0L) {
                        loadedGroups.add(new RecyclerSettings.RewardGroup(chance,
                            recycler.rewardProductIdsByChance(chance)));
                    }
                }
                rewardGroups = List.copyOf(loadedGroups);
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        RecyclerCache cache = buildRecyclerCache(rewardGroups);
        Licence.setRecyclerStatusPayload(cache.payload);
        Licence.setRecyclerRewards(cache.rewardGroups);
    }

    public static void Proc_1_1_6BB340(Object... args) {
        CatalogDao catalog = catalogDao();
        Object products = "";
        if (catalog != null) {
            try {
                products = catalog.productCacheRows();
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        Licence.setProductRows(products);
        DataManager.setProductRows(products);

        Object catalogProducts = "";
        if (catalog != null) {
            try {
                catalogProducts = catalog.catalogProductCacheRows();
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        Licence.setCatalogProductRows(catalogProducts);
        if (catalog != null) {
            try {
                Licence.setDealRows(catalog.productDealRows());
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
                Licence.setDealRows("\r\r");
            }
        } else {
            Licence.setDealRows("\r\r");
        }
        PackageDao packages = packageDao();
        ClubDao clubs = clubDao();
        if (catalog != null) {
            try {
                Licence.setRecyclerBoxProductId(catalog.productIdBySprite("ecotron_box"));
                Licence.setCounterProductIds(catalog.counterProductIds());
                Licence.setTeleportProductId(catalog.firstProductIdByType(11L));
                Licence.setMoodlightProductId(catalog.firstProductIdByType(19L));
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        if (packages != null) {
            try {
                Licence.setPackageRows(packages.packageRows());
                Licence.setPetPackageRows(packages.petPackageRows());
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        if (clubs != null) {
            try {
                Licence.setClubProductRows(clubs.containedClubProductRows());
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        Proc_1_17_6CCDC0(0, 0, 0);
        Proc_1_15_6CA000(0, 0, 0);
        Proc_1_18_6CE9C0(0, 0, 0);
        Proc_1_13_6C9820(0, 0, 0);
        Proc_1_0_6BA9D0(0, 0, 0);
    }

    public static void Proc_1_2_6BE280(Object... args) {
        Map<Long, String> recommended = new LinkedHashMap<>();
        long count = 0L;
        RoomDao rooms = roomDao();
        if (rooms != null) {
            try {
                for (Long treeIdValue : rooms.recommendedRoomTreeIds()) {
                    long treeId = treeIdValue == null ? 0L : treeIdValue.longValue();
                    if (treeId != 0L) {
                        recommended.put(count, Crypto.encodeVl64(treeId)
                            + buildRecommendedRoomsPayload(rooms.recommendedRoomRows(treeId)));
                        count++;
                    }
                }
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        Licence.setRecommendedRooms(recommended, count);
    }

    /**
     * Original function: Proc_1_3_6BEBA0.
     */
    public static void Proc_1_3_6BEBA0(Object... args) {
        initializeStartupCaches();
    }

    /**
     * Original function: Proc_1_3_6BEBA0.
     */
    public static void initializeStartupCaches() {
        initializeBootLogFiles();
        runTimed("Empfohlene Räume im Cache gespeichert", () -> Proc_1_2_6BE280(0, 0, 0));
        runTimed("Mögliche Badgevergabe im Cache gespeichert", () -> Proc_1_16_6CCA60(0, 0, 0));
        runTimed("Haustiere im Cache gespeichert", () -> Proc_1_7_6C5E10(0, 0, 0));
        runTimed("Figuredata im Cache gespeichert", Boot::writeFiguredataCache);
        runTimed("Server Einstellungen im Cache gespeichert", () -> Proc_1_9_6C6DF0(0, 0, 0));
        runTimed("Event Kategorien im Cache gespeichert", () -> Proc_1_8_6C6850(0, 0, 0));
        runTimed("Navigator Kategorien im Cache gespeichert", () -> {
            Proc_1_11_6C8D10(0, 0, 0);
            Proc_1_12_6C8EF0(0, 0, 0);
        });
        runTimed("Raumwerbung im Cache gespeichert", () -> Proc_1_22_6D0F00(0, 0, 0));
        runTimed("Bonussystem im Cache gespeichert", () -> Proc_1_5_6C4F80(0, 0, 0));
        runTimed("Katalog im Cache gespeichert", () -> Proc_1_1_6BB340(0, 0, 0));
        runTimed("Chat Einstellungen im Cache gespeichert", Boot::buildChatSettingsCache);
        runTimed("Haustierrassen im Cache gespeichert", () -> Proc_1_6_6C5830(0, 0, 0));
        runTimed("FAQ im Cache gespeichert", () -> {
            Proc_1_19_6CF190(0, 0, 0);
            Proc_1_20_6CF830(0, 0, 0);
            Proc_1_21_6D08C0(0, 0, 0);
        });
        runTimed("Aktive Serverdaten zurückgesetzt", () -> {
            ServerMaintenanceDao maintenanceDao = serverMaintenanceDao();
            if (maintenanceDao == null) {
                return;
            }
            try {
                maintenanceDao.resetConnectedUsers();
                maintenanceDao.resetVisitedRoomSlots();
                maintenanceDao.clearActiveVisitedRooms();
                maintenanceDao.clearRoomEvents();
            } catch (Exception ignored) {
                // VB6 source suppresses boot-time maintenance failures.
            }
        });
    }

    public static void Proc_1_4_6C4F00(Object... args) {
        Proc_1_8_6C6850(0, 0, 0);
        Proc_1_9_6C6DF0(0, 0, 0);
        Proc_1_19_6CF190(0, 0, 0);
        Proc_1_20_6CF830(0, 0, 0);
        Proc_1_21_6D08C0(0, 0, 0);
        Proc_1_22_6D0F00(0, 0, 0);
        Proc_1_11_6C8D10(0, 0, 0);
        Proc_1_12_6C8EF0(0, 0, 0);
        Proc_1_2_6BE280(0);
    }

    public static void Proc_1_5_6C4F80(Object... args) {
        AchievementSettingsCache achievementCache = new AchievementSettingsCache();
        AchievementDao achievements = achievementDao();
        if (achievements != null) {
            try {
                achievementCache = buildAchievementSettingsCache(achievements.enabledSettingsRows());
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        Licence.setAchievementSettings(achievementCache.questIdPayload, achievementCache.achievements);
        Proc_1_9_6C6DF0(0, 0, 0);
        Proc_1_7_6C5E10(0, 0, 0);
        Proc_1_18_6CE9C0(0, 0, 0);
        Proc_1_16_6CCA60(0, 0, 0);
        Proc_1_6_6C5830(0, 0, 0);
        Proc_1_10_6C7690(0, 0, 0);
        Proc_1_19_6CF190(0, 0, 0);
        Proc_1_20_6CF830(0, 0, 0);
        Proc_1_21_6D08C0(0, 0, 0);
        Proc_1_13_6C9820(0, 0, 0);
        Proc_1_22_6D0F00(0, 0, 0);
        buildChatSettingsCache();
        Licence.setMessengerFriendLimits(MessengerSettings.fromLimits(
            NumberUtils.parseLong(Functions.settingsCache().valueOrDefault("com.client.messenger.maxfriends.hclevel0", 0)),
            0L,
            NumberUtils.parseLong(Functions.settingsCache().valueOrDefault("com.client.messenger.maxfriends.hclevel1", 0)),
            0L,
            NumberUtils.parseLong(Functions.settingsCache().valueOrDefault("com.client.messenger.maxfriends.hclevel2", 0))));
    }

    public static void Proc_1_6_6C5830(Object... args) {
        try {
            BotDao bots = botDao();
            if (bots == null) {
                return;
            }
            Licence.setPetRaceRows(bots.petRaceCacheRows());
        } catch (Exception ignored) {
            // VB6 source suppresses boot cache failures.
        }
    }

    public static void Proc_1_7_6C5E10(Object... args) {
        try {
            BotDao bots = botDao();
            if (bots == null) {
                return;
            }
            Licence.setPetLevelRows(buildPetLevelRows(bots.petLevelCacheRows()));
            long commandCount = bots.petCommandCount();
            Licence.setPetCommandRows(buildPetCommandRows(bots.petCommandCacheRows()), commandCount);
        } catch (Exception ignored) {
            // VB6 source suppresses boot cache failures.
        }
    }

    public static void Proc_1_8_6C6850(Object... args) {
        SettingsDao settings = settingsDao();
        RoomEventLocales locales = DataManager.roomEventLocales();
        if (settings != null) {
            try {
                locales = buildRoomEventLocales(settings.roomEventLocaleRows(), locales);
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        DataManager.setRoomEventLocales(locales);
    }

    public static void Proc_1_9_6C6DF0(Object... args) {
        RoomDao rooms = roomDao();
        RoomPortalSettings portalSettings = RoomPortalSettings.fromRows(List.of(), List.of());
        if (rooms != null) {
            try {
                portalSettings = RoomPortalSettings.fromRows(rooms.warpSpaceRows(), rooms.specialGateRows());
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        Licence.setRoomPortalSettings(portalSettings);
        Proc_1_16_6CCA60(0, 0, 0);
        String systemDate = Functions.settingsCache().valueOrDefault("com.system.format.date", "");
        String systemTime = Functions.settingsCache().valueOrDefault("com.system.format.time", "");
        SettingsDao settings = settingsDao();
        List<SettingsDao.SettingRow> settingsRows = List.of();
        if (settings != null) {
            try {
                settingsRows = settings.allSettings();
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        Functions.setSettingsCache(buildSettingsCache(settingsRows, systemDate, systemTime));
        List<QuestSettings.QuestDefinitionRow> questRows = List.of();
        QuestDao quests = questDao();
        if (quests != null) {
            try {
                questRows = questDefinitionRows(quests.questDefinitions());
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        Licence.setQuestDefinitions(questRows);
    }

    public static void Proc_1_10_6C7690(Object... args) {
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
                values.add(new StaffSettings.ModerationPayload(rank, hc, payload));
            }
        }
        Licence.setStaffModerationPayloads(values);
    }

    public static void Proc_1_11_6C8D10(Object... args) {
        long privateCategoryId = NumberUtils.parseLong(
            Functions.settingsCache().valueOrDefault("com.client.navigator.categories.default.private.id", 0));
        long publicCategoryId = NumberUtils.parseLong(
            Functions.settingsCache().valueOrDefault("com.client.navigator.categories.default.public.id", 0));
        List<String> defaults = new ArrayList<String>();
        defaults.add(String.valueOf(privateCategoryId));
        defaults.add("");
        defaults.add(String.valueOf(publicCategoryId));
        Licence.setRoomCategoryDefaults(defaults);
        long parentCategoryId = privateCategoryId == 0L ? 1L : privateCategoryId;
        List<RoomDao.RoomCategoryRow> categoryRows = List.of();
        RoomDao rooms = roomDao();
        if (rooms != null) {
            try {
                categoryRows = rooms.roomCategoryRows(parentCategoryId);
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        Licence.setRoomCategoryRows(categoryRows);
    }

    public static void Proc_1_12_6C8EF0(Object... args) {
        List<RoomCategoryCache.CategoryPayload> values = new ArrayList<RoomCategoryCache.CategoryPayload>();
        RoomCategoryCache roomCategoryCache = roomCategoryCache();
        List<RoomDao.RoomCategoryRow> categoryRows = roomCategoryCache.categoryRowList();
        for (int rank = 0; rank <= 20; rank++) {
            for (int hc = 0; hc <= 2; hc++) {
                String payload = categoryRows.isEmpty()
                    ? buildRoomCategoryPayload(roomCategoryCache.categoryRows(), rank, hc)
                    : buildRoomCategoryPayload(categoryRows, rank, hc);
                values.add(new RoomCategoryCache.CategoryPayload(rank, hc, payload));
            }
        }
        Licence.setRoomCategoryPayloads(values);
    }

    private static RoomCategoryCache roomCategoryCache() {
        Licence.roomCategoryCache();
        return NavigatorState.instance().roomCategoryCache();
    }

    public static void Proc_1_13_6C9820(Object... args) {
        List<Long> wrapProductIds = List.of();
        CatalogDao catalog = catalogDao();
        if (catalog != null) {
            try {
                wrapProductIds = catalog.giftWrapProductIds();
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        long wrapCount = countNonZeroRows(wrapProductIds);
        long accessoryCount = NumberUtils.parseLong(
            Functions.settingsCache().valueOrDefault("com.client.catalog.gifts.wrap.count.accessories", wrapCount));
        long colorCount = NumberUtils.parseLong(
            Functions.settingsCache().valueOrDefault("com.client.catalog.gifts.wrap.count.colors", 0));
        Licence.setGiftWrapState(wrapProductIds, buildGiftWrapPayload(wrapProductIds, accessoryCount, colorCount));
    }

    public static void Proc_1_15_6CA000(Object... args) {
        CatalogDao catalog = catalogDao();
        Map<Long, String> pages = new LinkedHashMap<>();
        if (catalog != null) {
            try {
                for (CatalogDao.CatalogPageRow row : catalog.catalogPageRows()) {
                    long pageId = row.pageId();
                    if (pageId >= 0L) {
                        pages.put(pageId, buildCatalogPagePayload(row, catalog.catalogPageProductRows(pageId)));
                    }
                }
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        Licence.setCatalogPagePayloads(pages);
    }

    public static void Proc_1_16_6CCA60(Object... args) {
        String[][] permissions = new String[21][3];
        SettingsDao settings = settingsDao();
        for (int rank = 0; rank <= 20; rank++) {
            for (int hc = 0; hc <= 2; hc++) {
                String rows = "";
                if (settings != null) {
                    try {
                        rows = joinPrivilegeRows(settings.levelPrivileges(rank, hc));
                    } catch (Exception ignored) {
                        // Legacy startup cache loading tolerated missing tables or SQL failures.
                    }
                }
                permissions[rank][hc] = permissionPayload(rows);
            }
        }
        Functions.setPermissions(PermissionMatrix.fromRows(permissions));
    }

    public static void Proc_1_17_6CCDC0(Object... args) {
        Map<CatalogPages.PageTreeKey, String> trees = new LinkedHashMap<>();
        CatalogDao catalog = catalogDao();
        for (int rank = 0; rank <= 20; rank++) {
            for (int hc = 0; hc <= 2; hc++) {
                if (catalog != null) {
                    try {
                        List<CatalogDao.CatalogPageTreeRow> rootRows = catalog.catalogPageTreeRows(0L, rank, hc);
                        Map<Long, Long> childCounts = new LinkedHashMap<Long, Long>();
                        Map<Long, List<CatalogDao.CatalogPageTreeRow>> children = new LinkedHashMap<Long, List<CatalogDao.CatalogPageTreeRow>>();
                        for (CatalogDao.CatalogPageTreeRow row : rootRows) {
                            long pageId = row.pageId();
                            childCounts.put(pageId, catalog.catalogPageChildCount(pageId, rank, hc));
                            children.put(pageId, catalog.catalogPageTreeRows(pageId, rank, hc));
                        }
                        trees.put(new CatalogPages.PageTreeKey(rank, hc),
                            buildCatalogPageTreePayload(rootRows, childCounts, children, rank, hc));
                    } catch (Exception ignored) {
                        // Legacy startup cache loading tolerated missing tables or SQL failures.
                    }
                }
            }
        }
        Licence.setCatalogPageTrees(trees);
    }

    public static void Proc_1_18_6CE9C0(Object... args) {
        StringBuilder payload = new StringBuilder();
        StringBuilder lookup = new StringBuilder();
        List<GiftSettings.ClubGift> gifts = new ArrayList<GiftSettings.ClubGift>();
        long count = 0L;
        ClubDao clubs = clubDao();
        List<ClubDao.ClubGiftRow> giftRows = List.of();
        if (clubs != null) {
            try {
                giftRows = clubs.clubGiftRows();
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        for (ClubDao.ClubGiftRow row : giftRows) {
            long catalogProductId = row.catalogProductId();
            long productId = Licence.catalogProductFieldLong(catalogProductId, 2);
            if (productId == 0L) {
                productId = catalogProductId;
            }
            String giftClass = Licence.productType(productId) == 9L ? "i" : "s";
            payload.append(Crypto.encodeVl64(catalogProductId));
            payload.append(Crypto.encodeVl64(productId));
            payload.append(DataManager.productCache().displayName(productId)).append('\2');
            payload.append(DataManager.productCache().description(productId)).append('\2');
            payload.append("IHHI").append(giftClass).append('\2');
            payload.append(Crypto.encodeVl64(row.vipOnly()));
            payload.append(Crypto.encodeVl64(row.requiredDays()));
            lookup.append('[').append(catalogProductId).append('\0').append(productId).append('\1').append(row.requiredDays()).append(']');
            gifts.add(new GiftSettings.ClubGift(catalogProductId, productId, row.requiredDays()));
            count++;
        }
        Licence.setClubGiftState(new GiftSettings.ClubGiftState(
            Crypto.encodeVl64(count) + payload,
            lookup.toString(),
            gifts));
    }

    public static void Proc_1_19_6CF190(Object... args) {
        Map<Long, List<HelpDao.FaqNameRow>> rows = new LinkedHashMap<Long, List<HelpDao.FaqNameRow>>();
        HelpDao help = helpDao();
        if (help != null) {
            try {
                rows.put(1L, help.importantFaqRows(1L));
                rows.put(2L, help.importantFaqRows(2L));
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        Licence.setImportantFaqPayload(buildImportantFaqPayloadFromRows(rows));
    }

    public static void Proc_1_20_6CF830(Object... args) {
        HelpDao help = helpDao();
        List<HelpDao.FaqNameRow> categoryRows = List.of();
        Map<Long, List<HelpDao.FaqNameRow>> faqRows = new LinkedHashMap<Long, List<HelpDao.FaqNameRow>>();
        if (help != null) {
            try {
                List<HelpDao.FaqNameRow> categories = help.categoryRows();
                categoryRows = categories;
                for (HelpDao.FaqNameRow category : categories) {
                    long categoryId = category.id();
                    if (categoryId >= 0L) {
                        faqRows.put(categoryId, help.faqRowsByCategory(categoryId));
                    }
                }
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        FaqCategoryCache cache = buildFaqCategoryCacheFromRows(categoryRows, faqRows);
        Licence.setFaqCategoryCache(cache.categoryPayload, cache.faqPayloadByCategoryId);
    }

    public static void Proc_1_21_6D08C0(Object... args) {
        HelpDao help = helpDao();
        Map<Long, String> cache = new LinkedHashMap<Long, String>();
        if (help != null) {
            try {
                cache = buildFaqDescriptionCache(help.descriptionRows());
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        Licence.setFaqDescriptionCache(cache);
    }

    public static void Proc_1_22_6D0F00(Object... args) {
        AdvertisingDao advertising = advertisingDao();
        List<AdvertisingDao.VisitRoomAdRow> visitRoomRows = List.of();
        if (advertising != null) {
            try {
                visitRoomRows = advertising.visitRoomAds();
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        VisitRoomCache cache = buildAdvertisementVisitRoomCache(
            visitRoomRows,
            Functions.settingsCache().valueOrDefault("com.server.socket.game.advertisement.visitrooms.path", ""));
        Licence.setVisitRoomAds(cache.payloadByVisitRoomId, cache.count);
    }

    /**
     * Original function: Proc_1_23_6D1480.
     */
    public static void Proc_1_23_6D1480(Object... args) {
        String messageText = args != null && args.length >= 1 ? StringUtils.text(args[0]) : "";
        String logChannel = args != null && args.length >= 2 ? StringUtils.text(args[1]) : "";
        logBootLine(messageText, logChannel);
    }

    /**
     * Original function: Proc_1_23_6D1480.
     */
    public static void logBootLine(String messageText, String logChannel) {
        Console.logSourceLine(messageText, logChannel, 65280L);
    }

    public static void initializeBootLogFiles() {
        String productName = Licence.runtimeState().productName();
        String nowText = java.time.LocalDateTime.now().toString();
        java.nio.file.Path appPath = java.nio.file.Path.of(Functions.applicationPath);
        DataManager.appendTextFile(appPath.resolve("ERR.log").toString(), bootErrorLogHeader(productName, nowText));
        DataManager.appendTextFile(appPath.resolve("SLOW.log").toString(), bootSlowLogHeader(productName, nowText));
    }

    public static void printStartupNotice() {
        printStartupNotice(DEFAULT_BOOT_NOTICE);
    }

    public static void printStartupNotice(String messageText) {
        String message = StringUtils.text(messageText);
        if (message.length() <= 10) {
            return;
        }
        Console.appendPlainLine(message, 49344L);
        Console.appendOptionalSourceLine("", "HIDDEN", 262144L);
    }

    public static String[] startupCreditLines() {
        return STARTUP_CREDIT_LINES.clone();
    }

    public static void printStartupCredits() {
        for (String line : STARTUP_CREDIT_LINES) {
            Console.appendPlainLine(line, 49344L);
        }
    }

    public static String initializationIntegrityFailureMessage(boolean integrityFlag, String caption) {
        if (integrityFlag && StringUtils.text(caption).contains("INITIALISIERE")) {
            return INITIALIZATION_INTEGRITY_FAILURE_MESSAGE;
        }
        return "";
    }

    public static String serverReturnedErrorMessage(String description) {
        return SERVER_RETURNED_ERROR_PREFIX + StringUtils.text(description);
    }

    public static String bootErrorLogHeader(String productName, String nowText) {
        return bootLogHeader(productName,
            " Emulator is running since " + StringUtils.text(nowText) + ", errors are being logged.");
    }

    public static String bootSlowLogHeader(String productName, String nowText) {
        return bootLogHeader(productName,
            " Emulator is running since " + StringUtils.text(nowText)
                + ", slow query are being logged if you are running the development mode.");
    }

    public static String bootLogHeader(String productName, String runningLine) {
        String separator = "-------------------------------------------------------------------------------------------------------------------------------------------------------";
        return separator + "\r\n"
            + " Alpha Series [Version " + StringUtils.text(productName) + "\r\n"
            + StringUtils.text(runningLine) + "\r\n"
            + separator + "\r\n";
    }

    public static void runTimed(String messageText, Runnable action) {
        long startedAt = System.nanoTime();
        if (action != null) {
            action.run();
        }
        long elapsedMillis = Math.max(0L, (System.nanoTime() - startedAt) / 1_000_000L);
        logBootLine(messageText, "DEBUG, time: " + elapsedMillis + " ms");
    }

    public static void runTimed(String messageText, BooleanSupplier action) {
        long startedAt = System.nanoTime();
        boolean success = action == null || action.getAsBoolean();
        if (!success) {
            return;
        }
        long elapsedMillis = Math.max(0L, (System.nanoTime() - startedAt) / 1_000_000L);
        logBootLine(messageText, "DEBUG, time: " + elapsedMillis + " ms");
    }

    public static boolean writeFiguredataCache() {
        String figureData = "";
        SettingsDao settings = settingsDao();
        if (settings != null) {
            try {
                figureData = settings.value("com.cache.figuredata");
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        String cachePath = java.nio.file.Path.of(Functions.applicationPath, "figuredata.cache").toString();
        Handling.writeFile(cachePath, figureData);
        if (Handling.readFile(cachePath).trim().isEmpty()) {
            Console.logSourceLine("\"Figuredata\" Datei konnte nicht gefunden werden!", "ERROR", 255L);
            return false;
        }
        return true;
    }

    public static void cacheRowsById(String[] targetCache, String rowText) {
        if (targetCache == null) {
            return;
        }
        String[] rows = StringUtils.text(rowText).split("\r", -1);
        for (String row : rows) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                long cacheIndex = NumberUtils.parseLong(fields[0]);
                if (cacheIndex >= 0 && cacheIndex < targetCache.length) {
                    targetCache[(int) cacheIndex] = row;
                }
            }
        }
    }

    public static String buildCampaignReplacementCache(String rowText) {
        long replacementCount = 0L;
        StringBuilder payload = new StringBuilder();
        for (String row : StringUtils.text(rowText).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 2) {
                    payload.append(fields[0]).append('\2').append(fields[1]).append('\2');
                    replacementCount++;
                }
            }
        }
        return Crypto.encodeVl64(replacementCount) + payload;
    }

    public static String buildStaffMessageList(String rowText) {
        StringBuilder payload = new StringBuilder();
        for (String row : StringUtils.text(rowText).split("\r", -1)) {
            if (!row.isEmpty()) {
                payload.append(row).append('\2');
            }
        }
        return payload.toString();
    }

    public static String buildStaffMessageList(List<StaffModerationDao.StaffMessageRow> rows) {
        StringBuilder payload = new StringBuilder();
        if (rows != null) {
            for (StaffModerationDao.StaffMessageRow row : rows) {
                if (row != null && !StringUtils.text(row.message()).isEmpty()) {
                    payload.append(row.message()).append('\2');
                }
            }
        }
        return payload.toString();
    }

    public static String permissionPayload(String rows) {
        return StringUtils.text(rows).isEmpty() ? "\2" : "\2" + StringUtils.text(rows).replace('\r', '\2') + "\2";
    }

    public static String buildStaffCategoryPayload(String rootRows, Map<Long, String> childRowsByParentId) {
        StringBuilder payload = new StringBuilder();
        for (String rootRow : StringUtils.text(rootRows).split("\r", -1)) {
            if (!rootRow.isEmpty()) {
                String[] rootFields = rootRow.split("\t", -1);
                if (rootFields.length >= 2) {
                    long rootId = NumberUtils.parseLong(rootFields[0]);
                    String childRows = childRowsByParentId == null ? "" : childRowsByParentId.get(rootId);
                    StringBuilder childPayload = new StringBuilder();
                    long childCount = 0L;
                    for (String childRow : StringUtils.text(childRows).split("\r", -1)) {
                        if (!childRow.isEmpty()) {
                            String[] childFields = childRow.split("\t", -1);
                            if (childFields.length >= 2) {
                                childPayload.append(Crypto.encodeVl64(NumberUtils.parseLong(childFields[0])));
                                childPayload.append(childFields[1]).append('\2');
                                childCount++;
                            }
                        }
                    }
                    payload.append(Crypto.encodeVl64(rootId));
                    payload.append(rootFields[1]).append('\2');
                    payload.append(Crypto.encodeVl64(childCount));
                    payload.append(childPayload);
                }
            }
        }
        return payload.toString();
    }

    public static final class FaqCategoryCache {
        public String categoryPayload = "";
        public Map<Long, String> faqPayloadByCategoryId = new LinkedHashMap<Long, String>();
    }

    public static final class VisitRoomCache {
        public long count;
        public Map<Long, String> payloadByVisitRoomId = new LinkedHashMap<Long, String>();
    }

    public static final class PetCommandCache {
        public long commandCount;
        public Map<Long, PetSettings.PetCommandRow> commandById = new LinkedHashMap<Long, PetSettings.PetCommandRow>();
    }

    public static final class ClubGiftCache {
        public String giftPayload = "";
        public String giftLookup = "";
    }

    public static final class AchievementSettingsCache {
        public String questIdPayload = "";
        public List<AchievementSettings.Achievement> achievements = List.of();
    }

    public static final class RecyclerCache {
        public String payload = "";
        public long groupCount;
        public List<RecyclerSettings.RewardGroup> rewardGroups = List.of();
        public Map<Long, String> productListByGroupIndex = new LinkedHashMap<Long, String>();
        public Map<Long, Long> chanceByGroupIndex = new LinkedHashMap<Long, Long>();
    }

    public static RecyclerCache buildRecyclerCache(String chanceRows, Map<Long, String> productRowsByChance) {
        List<RecyclerSettings.RewardGroup> rewardGroups = new ArrayList<RecyclerSettings.RewardGroup>();
        for (String chanceRow : StringUtils.text(chanceRows).split("\r", -1)) {
            long chanceValue = NumberUtils.parseLong(chanceRow);
            if (!chanceRow.isEmpty()) {
                String productRows = productRowsByChance == null ? "" : productRowsByChance.get(chanceValue);
                rewardGroups.add(new RecyclerSettings.RewardGroup(chanceValue, recyclerProductIds(productRows)));
            }
        }
        return buildRecyclerCache(rewardGroups);
    }

    public static RecyclerCache buildRecyclerCache(List<RecyclerSettings.RewardGroup> rewardGroups) {
        RecyclerCache cache = new RecyclerCache();
        StringBuilder payload = new StringBuilder();
        List<RecyclerSettings.RewardGroup> groups = new ArrayList<RecyclerSettings.RewardGroup>();
        for (RecyclerSettings.RewardGroup rewardGroup : rewardGroups == null
            ? List.<RecyclerSettings.RewardGroup>of()
            : rewardGroups) {
            if (cache.groupCount > 49L) {
                break;
            }
            if (rewardGroup != null) {
                long chanceValue = NumberUtils.parseLong(rewardGroup.chance());
                long groupIndex = cache.groupCount;
                cache.chanceByGroupIndex.put(groupIndex, chanceValue);

                long productCount = 0L;
                StringBuilder productList = new StringBuilder();
                StringBuilder groupPayload = new StringBuilder();
                List<Long> productIds = new ArrayList<Long>();
                for (Long productIdValue : rewardGroup.productIds()) {
                    long productId = NumberUtils.parseLong(productIdValue);
                    if (productId > 0L) {
                        productIds.add(productId);
                        productList.append(productId).append('\2');
                        groupPayload.append(Crypto.encodeVl64(productId));
                        productCount++;
                    }
                }

                cache.productListByGroupIndex.put(groupIndex, productList.toString());
                groups.add(new RecyclerSettings.RewardGroup(chanceValue, productIds));
                payload.append(Crypto.encodeVl64(chanceValue));
                payload.append(Crypto.encodeVl64(productCount));
                payload.append(groupPayload);
                cache.groupCount++;
            }
        }
        cache.rewardGroups = List.copyOf(groups);
        cache.payload = Crypto.encodeVl64(cache.groupCount) + payload;
        return cache;
    }

    public static String buildPetRaceCache(String raceRows) {
        StringBuilder payload = new StringBuilder();
        for (String row : StringUtils.text(raceRows).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 6) {
                    payload.append('[').append(fields[0]).append('\t');
                    payload.append(NumberUtils.parseLong(fields[1])).append('\t');
                    payload.append(NumberUtils.parseLong(fields[2])).append('\t');
                    payload.append(NumberUtils.parseLong(fields[3])).append('\t');
                    payload.append(NumberUtils.parseLong(fields[4])).append('\t');
                    payload.append(fields[5]).append(']');
                }
            }
        }
        return payload.toString();
    }

    public static String buildPetRaceCache(List<PetRaceCacheRow> raceRows) {
        StringBuilder payload = new StringBuilder();
        if (raceRows != null) {
            for (PetRaceCacheRow row : raceRows) {
                if (row != null) {
                    payload.append('[').append(StringUtils.text(row.productPet())).append('\t');
                    payload.append(row.petId()).append('\t');
                    payload.append(row.breed()).append('\t');
                    payload.append(row.minRank()).append('\t');
                    payload.append(row.minHcRank()).append('\t');
                    payload.append(StringUtils.text(row.name())).append(']');
                }
            }
        }
        return payload.toString();
    }

    public static Map<Long, String> buildPetLevelCache(String levelRows) {
        Map<Long, String> cache = new LinkedHashMap<Long, String>();
        for (String row : StringUtils.text(levelRows).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 4) {
                    long levelId = NumberUtils.parseLong(fields[0]);
                    cache.put(levelId, NumberUtils.parseLong(fields[1]) + "\t" + NumberUtils.parseLong(fields[2]) + "\t" + NumberUtils.parseLong(fields[3]));
                }
            }
        }
        return cache;
    }

    public static Map<Long, String> buildPetLevelCache(List<PetLevelCacheRow> levelRows) {
        Map<Long, String> cache = new LinkedHashMap<Long, String>();
        if (levelRows != null) {
            for (PetLevelCacheRow row : levelRows) {
                if (row != null) {
                    cache.put(row.level(), row.maxEnergy() + "\t" + row.maxExperience() + "\t" + row.maxNutrition());
                }
            }
        }
        return cache;
    }

    public static List<PetSettings.PetLevelRow> buildPetLevelRows(List<PetLevelCacheRow> levelRows) {
        List<PetSettings.PetLevelRow> rows = new ArrayList<>();
        if (levelRows != null) {
            for (PetLevelCacheRow row : levelRows) {
                if (row != null) {
                    rows.add(new PetSettings.PetLevelRow(
                        row.level(),
                        row.maxEnergy(),
                        row.maxExperience(),
                        row.maxNutrition(),
                        4));
                }
            }
        }
        return rows;
    }

    public static PetCommandCache buildPetCommandCache(String commandRows) {
        PetCommandCache cache = new PetCommandCache();
        for (String row : StringUtils.text(commandRows).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 4) {
                    long commandId = NumberUtils.parseLong(fields[0]);
                    cache.commandById.put(commandId, new PetSettings.PetCommandRow(
                        commandId,
                        NumberUtils.parseLong(fields[1]),
                        fields[2],
                        fields[3],
                        fields.length));
                    cache.commandCount++;
                }
            }
        }
        return cache;
    }

    public static PetCommandCache buildPetCommandCache(List<PetCommandCacheRow> commandRows) {
        PetCommandCache cache = new PetCommandCache();
        for (PetSettings.PetCommandRow row : buildPetCommandRows(commandRows)) {
            cache.commandById.put(row.commandId(), row);
            cache.commandCount++;
        }
        return cache;
    }

    public static List<PetSettings.PetCommandRow> buildPetCommandRows(List<PetCommandCacheRow> commandRows) {
        List<PetSettings.PetCommandRow> rows = new ArrayList<>();
        if (commandRows != null) {
            for (PetCommandCacheRow row : commandRows) {
                if (row != null) {
                    rows.add(new PetSettings.PetCommandRow(
                        row.commandId(),
                        row.requiredLevel(),
                        StringUtils.text(row.command()),
                        StringUtils.text(row.action()),
                        4));
                }
            }
        }
        return rows;
    }

    public static String buildRoomEventLocaleCache(String localeRows, String existingCache) {
        StringBuilder payload = new StringBuilder(StringUtils.text(existingCache));
        for (String row : StringUtils.text(localeRows).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 2) {
                    String cacheKey = fields[0].replaceFirst("roomevent_type_", "");
                    if (!cacheKey.isEmpty()) {
                        payload.append('\0').append(NumberUtils.parseLong(cacheKey)).append('\1').append(fields[1]).append('\2');
                    }
                }
            }
        }
        return payload.toString();
    }

    public static String buildRoomEventLocaleCache(List<SettingsDao.LocaleRow> localeRows, String existingCache) {
        return buildRoomEventLocales(localeRows, RoomEventLocales.fromLegacy(existingCache)).cacheText();
    }

    public static RoomEventLocales buildRoomEventLocales(List<SettingsDao.LocaleRow> localeRows, RoomEventLocales existingLocales) {
        return (existingLocales == null ? RoomEventLocales.fromLegacy("") : existingLocales)
            .withEntries(roomEventLocaleEntries(localeRows));
    }

    public static List<RoomEventLocales.LocaleEntry> roomEventLocaleEntries(List<SettingsDao.LocaleRow> localeRows) {
        List<RoomEventLocales.LocaleEntry> entries = new ArrayList<>();
        if (localeRows != null) {
            for (SettingsDao.LocaleRow row : localeRows) {
                if (row != null) {
                    String cacheKey = StringUtils.text(row.variableName()).replaceFirst("roomevent_type_", "");
                    if (!cacheKey.isEmpty()) {
                        entries.add(new RoomEventLocales.LocaleEntry(
                            String.valueOf(NumberUtils.parseLong(cacheKey)),
                            List.of(StringUtils.text(row.value()), "")));
                    }
                }
            }
        }
        return entries;
    }

    public static String buildSettingsCache(String settingsRows, String systemDateFormat, String systemTimeFormat) {
        StringBuilder payload = new StringBuilder();
        for (String row : StringUtils.text(settingsRows).split("\r", -1)) {
            if (!row.isEmpty()) {
                payload.append('[').append(row.replace('\t', '=')).append(']');
            }
        }
        payload.append("[com.client.format.date=").append(clientDateFormat(systemDateFormat)).append(']');
        payload.append("[com.client.format.time=").append(clientTimeFormat(systemTimeFormat)).append(']');
        payload.append("[com.mysql.format.date=").append(mysqlDateFormat(systemDateFormat)).append(']');
        payload.append("[com.mysql.format.time=").append(mysqlTimeFormat(systemTimeFormat)).append(']');
        return payload.toString();
    }

    public static String buildSettingsCache(List<SettingsDao.SettingRow> settingsRows, String systemDateFormat,
            String systemTimeFormat) {
        StringBuilder payload = new StringBuilder();
        if (settingsRows != null) {
            for (SettingsDao.SettingRow row : settingsRows) {
                if (row != null) {
                    payload.append('[')
                        .append(StringUtils.text(row.variableName()))
                        .append('=')
                        .append(StringUtils.text(row.value()))
                        .append(']');
                }
            }
        }
        payload.append("[com.client.format.date=").append(clientDateFormat(systemDateFormat)).append(']');
        payload.append("[com.client.format.time=").append(clientTimeFormat(systemTimeFormat)).append(']');
        payload.append("[com.mysql.format.date=").append(mysqlDateFormat(systemDateFormat)).append(']');
        payload.append("[com.mysql.format.time=").append(mysqlTimeFormat(systemTimeFormat)).append(']');
        return payload.toString();
    }

    public static String buildGiftWrapPayload(String wrapRows, long accessoryCount, long colorCount) {
        return buildGiftWrapPayload(giftWrapProductIds(wrapRows), accessoryCount, colorCount);
    }

    public static String buildGiftWrapPayload(List<Long> wrapProductIds, long accessoryCount, long colorCount) {
        long wrapCount = 0L;
        StringBuilder wrapPayload = new StringBuilder();
        for (Long productId : wrapProductIds == null ? List.<Long>of() : wrapProductIds) {
            long wrapId = NumberUtils.parseLong(productId);
            if (wrapId != 0L) {
                wrapCount++;
                wrapPayload.append(Crypto.encodeVl64(wrapId));
            }
        }

        StringBuilder accessoryPayload = new StringBuilder();
        for (long optionIndex = 1L; optionIndex <= accessoryCount; optionIndex++) {
            accessoryPayload.append(Crypto.encodeVl64(optionIndex));
        }

        StringBuilder colorPayload = new StringBuilder();
        for (long optionIndex = 1L; optionIndex <= colorCount; optionIndex++) {
            colorPayload.append(Crypto.encodeVl64(optionIndex));
        }

        return Crypto.encodeVl64(accessoryCount)
            + accessoryPayload
            + Crypto.encodeVl64(wrapCount)
            + wrapPayload
            + Crypto.encodeVl64(colorCount)
            + colorPayload;
    }

    public static ClubGiftCache buildClubGiftCache(String giftRows, Map<Long, Long> productIdByCatalogProductId,
            Map<Long, Long> productTypeByProductId, Map<Long, String> nameByProductId,
            Map<Long, String> descriptionByProductId) {
        ClubGiftCache cache = new ClubGiftCache();
        long giftCount = 0L;
        StringBuilder giftPayload = new StringBuilder();
        StringBuilder giftLookup = new StringBuilder();
        for (String row : StringUtils.text(giftRows).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 3) {
                    long catalogProductId = NumberUtils.parseLong(fields[0]);
                    long productId = mapLong(productIdByCatalogProductId, catalogProductId);
                    if (productId == 0L) {
                        productId = catalogProductId;
                    }
                    long isVip = NumberUtils.parseLong(fields[1]);
                    long requiredDays = NumberUtils.parseLong(fields[2]);
                    String giftClass = mapLong(productTypeByProductId, productId) == 9L ? "i" : "s";
                    String giftName = mapString(nameByProductId, productId);
                    String giftDescription = mapString(descriptionByProductId, productId);

                    giftPayload.append(Crypto.encodeVl64(catalogProductId));
                    giftPayload.append(Crypto.encodeVl64(productId));
                    giftPayload.append(giftName).append('\2').append(giftDescription).append('\2');
                    giftPayload.append("IHHI").append(giftClass).append('\2');
                    giftPayload.append(Crypto.encodeVl64(isVip));
                    giftPayload.append(Crypto.encodeVl64(requiredDays));

                    giftLookup.append('[').append(catalogProductId).append('\0').append(productId)
                        .append('\1').append(requiredDays).append(']');
                    giftCount++;
                }
            }
        }
        cache.giftPayload = Crypto.encodeVl64(giftCount) + giftPayload;
        cache.giftLookup = giftLookup.toString();
        return cache;
    }

    public static AchievementSettingsCache buildAchievementSettingsCache(String achievementRows) {
        AchievementSettingsCache cache = new AchievementSettingsCache();
        long achievementIndex = 0L;
        StringBuilder questIds = new StringBuilder();
        List<AchievementSettings.Achievement> achievements = new ArrayList<AchievementSettings.Achievement>();
        for (String row : StringUtils.text(achievementRows).split("\r", -1)) {
            if (achievementIndex > 100L) {
                break;
            }
            if (!row.isEmpty()) {
                AchievementSettings.Achievement achievement = AchievementSettings.achievement(row);
                if (achievement != null) {
                    questIds.append(achievement.achievementId()).append('\2');
                    achievements.add(achievement);
                    achievementIndex++;
                }
            }
        }
        cache.questIdPayload = questIds.toString();
        cache.achievements = List.copyOf(achievements);
        return cache;
    }

    public static AchievementSettingsCache buildAchievementSettingsCache(List<AchievementDao.AchievementSettingsRow> achievementRows) {
        AchievementSettingsCache cache = new AchievementSettingsCache();
        long achievementIndex = 0L;
        StringBuilder questIds = new StringBuilder();
        List<AchievementSettings.Achievement> achievements = new ArrayList<AchievementSettings.Achievement>();
        if (achievementRows != null) {
            for (AchievementDao.AchievementSettingsRow row : achievementRows) {
                if (achievementIndex > 100L) {
                    break;
                }
                if (row != null) {
                    questIds.append(row.questId()).append('\2');
                    achievements.add(new AchievementSettings.Achievement(row.questId(), StringUtils.text(row.badgeId()),
                        row.progress(), row.rewardIncrease(), row.levelTotal(), row.scoreIncrease(), row.rewardType()));
                    achievementIndex++;
                }
            }
        }
        cache.questIdPayload = questIds.toString();
        cache.achievements = List.copyOf(achievements);
        return cache;
    }

    public static int[] buildMessengerFriendLimitCache(long hcLevel0, long hcLevel1, long hcLevel2) {
        int[] limits = new int[5];
        limits[0] = (int) hcLevel0;
        limits[2] = (int) hcLevel1;
        limits[4] = (int) hcLevel2;
        return limits;
    }

    public static void buildChatSettingsCache() {
        ChatDao chat = chatDao();
        List<ChatSettings.FilterWord> filterRows = List.of();
        List<ChatSettings.Gesture> gestureRows = List.of();
        if (chat != null) {
            try {
                filterRows = chat.filterWords();
                gestureRows = chat.gestureRows();
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        Licence.setChatSettings(filterRows, gestureRows);
    }

    public static String buildRoomCategoryPayload(String categoryRows, long rankIndex, long hcLevel) {
        long categoryCount = 0L;
        StringBuilder categoryPayload = new StringBuilder();
        for (String row : StringUtils.text(categoryRows).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 5) {
                    long categoryId = NumberUtils.parseLong(fields[0]);
                    long hasTrading = NumberUtils.parseLong(fields[2]);
                    long minRank = NumberUtils.parseLong(fields[3]);
                    long minHcLevel = NumberUtils.parseLong(fields[4]);
                    if (rankIndex >= minRank && hcLevel >= minHcLevel) {
                        categoryPayload.append(Crypto.encodeVl64(categoryId));
                        categoryPayload.append(fields[1]).append('\2');
                        categoryPayload.append(Crypto.encodeVl64(hasTrading));
                        categoryCount++;
                    }
                }
            }
        }
        return Crypto.encodeVl64(categoryCount) + categoryPayload;
    }

    public static String buildRoomCategoryPayload(List<RoomDao.RoomCategoryRow> categoryRows, long rankIndex, long hcLevel) {
        long categoryCount = 0L;
        PacketBuilder categoryPayload = PacketBuilder.create();
        if (categoryRows != null) {
            for (RoomDao.RoomCategoryRow row : categoryRows) {
                if (row != null && rankIndex >= row.minimumRank() && hcLevel >= row.minimumHcRank()) {
                    categoryPayload
                        .appendInt(row.categoryId())
                        .appendString(row.name())
                        .appendInt(row.trading());
                    categoryCount++;
                }
            }
        }
        return PacketBuilder.create().appendInt(categoryCount).appendRaw(categoryPayload.build()).build();
    }

    public static String buildImportantFaqPayload(Map<Long, String> faqRowsByImportance) {
        StringBuilder payload = new StringBuilder();
        for (long importanceLevel = 1L; importanceLevel <= 2L; importanceLevel++) {
            String groupPayload = buildFaqNamePayload(faqRowsByImportance == null ? "" : faqRowsByImportance.get(importanceLevel));
            long groupCount = countRowsWithFields(faqRowsByImportance == null ? "" : faqRowsByImportance.get(importanceLevel), 2);
            payload.append(Crypto.encodeVl64(groupCount));
            payload.append(groupPayload);
        }
        return Crypto.encodeVl64(2) + payload;
    }

    public static String buildImportantFaqPayloadFromRows(Map<Long, List<HelpDao.FaqNameRow>> faqRowsByImportance) {
        StringBuilder payload = new StringBuilder();
        for (long importanceLevel = 1L; importanceLevel <= 2L; importanceLevel++) {
            List<HelpDao.FaqNameRow> rows = faqRowsByImportance == null ? List.of() : faqRowsByImportance.get(importanceLevel);
            payload.append(Crypto.encodeVl64(countFaqNameRows(rows)));
            payload.append(buildFaqNamePayloadFromRows(rows));
        }
        return Crypto.encodeVl64(2) + payload;
    }

    public static FaqCategoryCache buildFaqCategoryCache(String categoryRows, Map<Long, String> faqRowsByCategoryId) {
        FaqCategoryCache cache = new FaqCategoryCache();
        long categoryCount = 0L;
        StringBuilder categoryPayload = new StringBuilder();
        for (String categoryRow : StringUtils.text(categoryRows).split("\r", -1)) {
            if (!categoryRow.isEmpty()) {
                String[] categoryFields = categoryRow.split("\t", -1);
                if (categoryFields.length >= 2) {
                    long categoryId = NumberUtils.parseLong(categoryFields[0]);
                    String faqRows = faqRowsByCategoryId == null ? "" : faqRowsByCategoryId.get(categoryId);
                    String faqPayload = buildFaqNamePayload(faqRows);
                    long faqCount = countRowsWithFields(faqRows, 2);
                    cache.faqPayloadByCategoryId.put(categoryId, Crypto.encodeVl64(faqCount) + faqPayload);
                    categoryPayload.append(Crypto.encodeVl64(categoryId));
                    categoryPayload.append(categoryFields[1]).append('\2');
                    categoryCount++;
                }
            }
        }
        cache.categoryPayload = Crypto.encodeVl64(categoryCount) + categoryPayload;
        return cache;
    }

    public static FaqCategoryCache buildFaqCategoryCacheFromRows(List<HelpDao.FaqNameRow> categoryRows,
            Map<Long, List<HelpDao.FaqNameRow>> faqRowsByCategoryId) {
        FaqCategoryCache cache = new FaqCategoryCache();
        long categoryCount = 0L;
        StringBuilder categoryPayload = new StringBuilder();
        if (categoryRows != null) {
            for (HelpDao.FaqNameRow category : categoryRows) {
                if (category != null) {
                    long categoryId = category.id();
                    List<HelpDao.FaqNameRow> faqRows = faqRowsByCategoryId == null ? List.of() : faqRowsByCategoryId.get(categoryId);
                    cache.faqPayloadByCategoryId.put(categoryId,
                        Crypto.encodeVl64(countFaqNameRows(faqRows)) + buildFaqNamePayloadFromRows(faqRows));
                    categoryPayload.append(Crypto.encodeVl64(categoryId));
                    categoryPayload.append(StringUtils.text(category.name())).append('\2');
                    categoryCount++;
                }
            }
        }
        cache.categoryPayload = Crypto.encodeVl64(categoryCount) + categoryPayload;
        return cache;
    }

    public static Map<Long, String> buildFaqDescriptionCache(String faqRows) {
        Map<Long, String> cache = new LinkedHashMap<Long, String>();
        for (String row : StringUtils.text(faqRows).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 2) {
                    long faqId = NumberUtils.parseLong(fields[0]);
                    String descriptionText = fields[1].replace('\n', '\r');
                    cache.put(faqId, Crypto.encodeVl64(faqId) + descriptionText + '\2');
                }
            }
        }
        return cache;
    }

    public static Map<Long, String> buildFaqDescriptionCache(List<HelpDao.FaqDescriptionRow> faqRows) {
        Map<Long, String> cache = new LinkedHashMap<Long, String>();
        if (faqRows != null) {
            for (HelpDao.FaqDescriptionRow row : faqRows) {
                if (row != null) {
                    long faqId = row.id();
                    String descriptionText = StringUtils.text(row.description()).replace('\n', '\r');
                    cache.put(faqId, Crypto.encodeVl64(faqId) + descriptionText + '\2');
                }
            }
        }
        return cache;
    }

    public static VisitRoomCache buildAdvertisementVisitRoomCache(String visitRoomRows, String assetPath) {
        VisitRoomCache cache = new VisitRoomCache();
        for (String row : StringUtils.text(visitRoomRows).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 2) {
                    long visitRoomId = NumberUtils.parseLong(fields[0]);
                    cache.payloadByVisitRoomId.put(visitRoomId, StringUtils.text(assetPath) + visitRoomId + '\2' + fields[1] + '\2');
                    cache.count++;
                }
            }
        }
        return cache;
    }

    public static VisitRoomCache buildAdvertisementVisitRoomCache(List<AdvertisingDao.VisitRoomAdRow> visitRoomRows,
            String assetPath) {
        VisitRoomCache cache = new VisitRoomCache();
        if (visitRoomRows != null) {
            for (AdvertisingDao.VisitRoomAdRow row : visitRoomRows) {
                if (row != null) {
                    long visitRoomId = row.visitRoomId();
                    cache.payloadByVisitRoomId.put(visitRoomId,
                        StringUtils.text(assetPath) + visitRoomId + '\2' + StringUtils.text(row.address()) + '\2');
                    cache.count++;
                }
            }
        }
        return cache;
    }

    public static String buildRecommendedRoomsQuery(long treeId) {
        String treeText = String.valueOf(treeId);
        String separator = " UNION ALL ";
        StringBuilder queryText = new StringBuilder();

        queryText.append("SELECT rooms_recommented.id_type,rooms_recommented.id_style,rooms_recommented.icon,");
        queryText.append("rooms_recommented.caption,rooms_recommented.caption_2,rooms_recommented.caption_3,");
        queryText.append("NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,");
        queryText.append("rooms_recommented.id_tree,rooms_recommented.id FROM rooms_recommented WHERE id_tree='");
        queryText.append(treeText).append("' AND rooms_recommented.id_type='1' GROUP BY rooms_recommented.id");

        queryText.append(separator).append("SELECT rooms_recommented.id_type,rooms_recommented.id_style,rooms_recommented.icon,");
        queryText.append("rooms_recommented.caption,rooms_recommented.caption_2,rooms_recommented.caption_3,NULL,");
        queryText.append("rooms.id,rooms.name,users.name,rooms.status_door,rooms.visitors_now,rooms.visitors_max,");
        queryText.append("rooms.description,rooms_categories.has_trading,NULL,rooms.rate,rooms.id_category,rooms.icon,");
        queryText.append("rooms.tag_1,rooms.tag_2,rooms.allow_otherspets,NULL,NULL,NULL,");
        queryText.append("rooms_recommented.id_tree,rooms_recommented.id FROM users,rooms,rooms_categories,rooms_recommented ");
        queryText.append("WHERE id_tree='").append(treeText).append("' AND rooms_recommented.id_type='2' ");
        queryText.append("AND rooms_recommented.id_room IS NOT NULL AND rooms.id=rooms_recommented.id_room ");
        queryText.append("AND users.id=rooms.id_owner AND rooms_categories.id=rooms.id_category GROUP BY rooms_recommented.id");

        queryText.append(separator).append("SELECT rooms_recommented.id_type,rooms_recommented.id_style,rooms_recommented.icon,");
        queryText.append("rooms_recommented.caption,rooms_recommented.caption_2,rooms_recommented.caption_3,NULL,");
        queryText.append("rooms.id,rooms.name,NULL,rooms.status_door,rooms.visitors_now,rooms.visitors_max,");
        queryText.append("rooms.description,rooms_categories.has_trading,NULL,rooms.rate,rooms.id_category,rooms.icon,");
        queryText.append("rooms.tag_1,rooms.tag_2,rooms.allow_otherspets,models.name,models.required_files,models.visitors_max,");
        queryText.append("rooms_recommented.id_tree,rooms_recommented.id FROM models,rooms,rooms_categories,rooms_recommented ");
        queryText.append("WHERE id_tree='").append(treeText).append("' AND rooms_recommented.id_type='3' ");
        queryText.append("AND rooms_recommented.id_room IS NOT NULL AND rooms.id=rooms_recommented.id_room ");
        queryText.append("AND models.id=rooms.id_model AND rooms_categories.id=rooms.id_category GROUP BY rooms_recommented.id");

        queryText.append(separator).append("SELECT rooms_recommented.id_type,rooms_recommented.id_style,rooms_recommented.icon,");
        queryText.append("rooms_recommented.caption,rooms_recommented.caption_2,rooms_recommented.caption_3,");
        queryText.append("NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,");
        queryText.append("rooms_recommented.id_tree,rooms_recommented.id FROM rooms_recommented WHERE id_tree='");
        queryText.append(treeText).append("' AND rooms_recommented.id_type='4' GROUP BY rooms_recommented.id ORDER BY 27 ASC LIMIT 255");

        return queryText.toString();
    }

    public static String buildRecommendedRoomsPayload(String roomRows) {
        long roomCount = 0L;
        StringBuilder payload = new StringBuilder();
        for (String row : StringUtils.text(roomRows).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 27) {
                    roomCount++;
                    payload.append(Crypto.encodeVl64(NumberUtils.parseLong(fields[0])));
                    payload.append(Crypto.encodeVl64(NumberUtils.parseLong(fields[1])));
                    payload.append(Crypto.encodeVl64(NumberUtils.parseLong(fields[2])));
                    for (int fieldIndex = 3; fieldIndex <= 24; fieldIndex++) {
                        payload.append(fields[fieldIndex]).append('\2');
                    }
                    payload.append(Crypto.encodeVl64(NumberUtils.parseLong(fields[25])));
                    payload.append(Crypto.encodeVl64(NumberUtils.parseLong(fields[26])));
                }
            }
        }
        return Crypto.encodeVl64(roomCount) + payload;
    }

    public static String buildRecommendedRoomsPayload(List<RoomDao.RecommendedRoomRow> roomRows) {
        long roomCount = 0L;
        StringBuilder payload = new StringBuilder();
        if (roomRows != null) {
            for (RoomDao.RecommendedRoomRow row : roomRows) {
                if (row != null) {
                    roomCount++;
                    payload.append(Crypto.encodeVl64(row.type()));
                    payload.append(Crypto.encodeVl64(row.style()));
                    payload.append(Crypto.encodeVl64(row.icon()));
                    payload.append(StringUtils.text(row.caption())).append('\2');
                    payload.append(StringUtils.text(row.captionTwo())).append('\2');
                    payload.append(StringUtils.text(row.captionThree())).append('\2');
                    payload.append(StringUtils.text(row.legacyNullSlot())).append('\2');
                    payload.append(row.roomId()).append('\2');
                    payload.append(StringUtils.text(row.roomName())).append('\2');
                    payload.append(StringUtils.text(row.ownerName())).append('\2');
                    payload.append(row.doorStatus()).append('\2');
                    payload.append(row.visitorsNow()).append('\2');
                    payload.append(row.visitorsMax()).append('\2');
                    payload.append(StringUtils.text(row.description())).append('\2');
                    payload.append(row.trading()).append('\2');
                    payload.append(StringUtils.text(row.legacySecondNullSlot())).append('\2');
                    payload.append(row.rating()).append('\2');
                    payload.append(row.categoryId()).append('\2');
                    payload.append(StringUtils.text(row.roomIcon())).append('\2');
                    payload.append(StringUtils.text(row.tagOne())).append('\2');
                    payload.append(StringUtils.text(row.tagTwo())).append('\2');
                    payload.append(row.allowOtherPets()).append('\2');
                    payload.append(StringUtils.text(row.modelName())).append('\2');
                    payload.append(StringUtils.text(row.requiredFiles())).append('\2');
                    payload.append(StringUtils.text(row.modelVisitorsMax())).append('\2');
                    payload.append(Crypto.encodeVl64(row.treeId()));
                    payload.append(Crypto.encodeVl64(row.recommendedId()));
                }
            }
        }
        return Crypto.encodeVl64(roomCount) + payload;
    }

    public static String Proc_1_14_6C9DD0(Object... args) {
        long pageId = args != null && args.length >= 1 ? NumberUtils.parseLong(args[0]) : 0L;
        long parentId = args != null && args.length >= 2 ? NumberUtils.parseLong(args[1]) : 0L;
        String caption = args != null && args.length >= 3 ? StringUtils.text(args[2]) : "";
        long visibleState = args != null && args.length >= 4 ? NumberUtils.parseLong(args[3]) : 0L;
        long iconId = args != null && args.length >= 5 ? NumberUtils.parseLong(args[4]) : 0L;
        long childCount = args != null && args.length >= 6 ? NumberUtils.parseLong(args[5]) : 0L;

        return "0"
            + Crypto.encodeVl64(pageId)
            + Crypto.encodeVl64(parentId)
            + Crypto.encodeVl64(iconId)
            + Crypto.encodeVl64(visibleState)
            + caption + '\2'
            + Crypto.encodeVl64(childCount);
    }

    public static String buildCatalogPagePayload(String[] fields, String productRows) {
        if (fields == null || fields.length < 21) {
            return "";
        }
        long pageId = NumberUtils.parseLong(fields[0]);
        StringBuilder payload = new StringBuilder();
        payload.append(fields[1]).append('\2');
        payload.append(Crypto.encodeVl64(NumberUtils.parseLong(fields[4])));
        payload.append(fields[5]).append('\2');
        payload.append(fields[6]).append('\2');
        payload.append(fields[7]).append('\2');
        payload.append(fields[8]).append('\2');

        long textCount = 0L;
        StringBuilder textPayload = new StringBuilder();
        for (int textIndex = 9; textIndex <= 19; textIndex++) {
            if (catalogTextFieldPresent(fields[textIndex])) {
                textCount++;
                textPayload.append(fields[textIndex]).append('\2');
            }
        }
        payload.append(Crypto.encodeVl64(textCount)).append(textPayload);

        if (catalogTextFieldPresent(fields[20])) {
            payload.append(Crypto.encodeVl64(1)).append(fields[20]).append('\2');
        } else {
            payload.append(Crypto.encodeVl64(0));
        }
        return payload.append(buildCatalogProductPayload(pageId, productRows)).toString();
    }

    public static String buildCatalogPagePayload(CatalogDao.CatalogPageRow page, List<CatalogDao.CatalogPageProductRow> productRows) {
        if (page == null) {
            return "";
        }
        StringBuilder payload = new StringBuilder();
        payload.append(StringUtils.text(page.name())).append('\2');
        payload.append(Crypto.encodeVl64(page.clickable()));
        payload.append(StringUtils.text(page.template())).append('\2');
        payload.append(StringUtils.text(page.headerImage())).append('\2');
        payload.append(StringUtils.text(page.specialImage())).append('\2');
        payload.append(StringUtils.text(page.specialTemplate())).append('\2');

        String[] textFields = new String[] {
            page.textOne(),
            page.textTwo(),
            page.textThree(),
            page.textFour(),
            page.textFive(),
            page.textSix(),
            page.textSeven(),
            page.textEight(),
            page.textNine(),
            page.textTen(),
            page.textEleven()
        };
        long textCount = 0L;
        StringBuilder textPayload = new StringBuilder();
        for (String textField : textFields) {
            if (catalogTextFieldPresent(textField)) {
                textCount++;
                textPayload.append(textField).append('\2');
            }
        }
        payload.append(Crypto.encodeVl64(textCount)).append(textPayload);

        if (catalogTextFieldPresent(page.link())) {
            payload.append(Crypto.encodeVl64(1)).append(page.link()).append('\2');
        } else {
            payload.append(Crypto.encodeVl64(0));
        }
        return payload.append(buildCatalogProductPayload(page.pageId(), productRows)).toString();
    }

    public static String buildCatalogProductPayload(long pageId, String productRows) {
        long productCount = 0L;
        StringBuilder productPayload = new StringBuilder();
        for (String row : StringUtils.text(productRows).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 10) {
                    productPayload.append(buildCatalogProductEntry(fields));
                    productCount++;
                }
            }
        }
        return Crypto.encodeVl64(productCount) + productPayload;
    }

    public static String buildCatalogProductPayload(long pageId, List<CatalogDao.CatalogPageProductRow> productRows) {
        long productCount = 0L;
        StringBuilder productPayload = new StringBuilder();
        if (productRows != null) {
            for (CatalogDao.CatalogPageProductRow row : productRows) {
                if (row != null) {
                    productPayload.append(buildCatalogProductEntry(row));
                    productCount++;
                }
            }
        }
        return Crypto.encodeVl64(productCount) + productPayload;
    }

    public static String buildCatalogProductEntry(String[] fields) {
        if (fields == null || fields.length < 10) {
            return "";
        }
        long catalogProductId = NumberUtils.parseLong(fields[0]);
        long productId = NumberUtils.parseLong(fields[1]);
        long productType = Licence.productType(productId);
        String productClass = catalogProductClass(productType);
        long amountValue = NumberUtils.parseLong(fields[6]);
        if (amountValue <= 0L) {
            amountValue = 1L;
        }

        return Crypto.encodeVl64(catalogProductId)
            + fields[4] + '\2'
            + Crypto.encodeVl64(productId)
            + productClass + '\2'
            + Crypto.encodeVl64(NumberUtils.parseLong(fields[2]))
            + Crypto.encodeVl64(NumberUtils.parseLong(fields[3]))
            + Crypto.encodeVl64(NumberUtils.parseLong(fields[5]))
            + Crypto.encodeVl64(amountValue)
            + fields[7] + '\2'
            + Crypto.encodeVl64(NumberUtils.parseLong(fields[8]))
            + Crypto.encodeVl64(NumberUtils.parseLong(fields[9]));
    }

    public static String buildCatalogProductEntry(CatalogDao.CatalogPageProductRow row) {
        if (row == null) {
            return "";
        }
        long productType = Licence.productType(row.productId());
        String productClass = catalogProductClass(productType);
        long amountValue = row.amount();
        if (amountValue <= 0L) {
            amountValue = 1L;
        }

        return Crypto.encodeVl64(row.catalogProductId())
            + StringUtils.text(row.sprite()) + '\2'
            + Crypto.encodeVl64(row.productId())
            + productClass + '\2'
            + Crypto.encodeVl64(row.creditPrice())
            + Crypto.encodeVl64(row.activityPointPrice())
            + Crypto.encodeVl64(row.activityPointType())
            + Crypto.encodeVl64(amountValue)
            + StringUtils.text(row.secondaryType()) + '\2'
            + Crypto.encodeVl64(row.replaceDefaultSign())
            + Crypto.encodeVl64(row.minimumHcRank());
    }

    public static String buildCatalogProductQuery(long pageId) {
        return "SELECT id,id_product,price_credits,price_activitypoints,sprite,type_activitypoints,"
            + "amount,type_secondary,replace_defaultsign,min_hc_level_required "
            + "FROM catalog_products WHERE ctlg_pageid='" + pageId + "' "
            + "ORDER BY id_order,sprite ASC";
    }

    public static boolean catalogTextFieldPresent(String fieldValue) {
        return fieldValue != null && !fieldValue.isEmpty() && !"NULL".equalsIgnoreCase(fieldValue);
    }

    public static String catalogProductClass(long productType) {
        return productType == 9L ? "i" : "s";
    }

    public static String buildCatalogPageTreePayload(String rootRows, Map<Long, Long> childCountByPageId,
            Map<Long, String> childRowsByParentId, long rankIndex, long hcLevel) {
        long rootCount = 0L;
        StringBuilder payload = new StringBuilder();
        for (String row : StringUtils.text(rootRows).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 6 && catalogPageVisible(fields, rankIndex, hcLevel)) {
                    long pageId = NumberUtils.parseLong(fields[0]);
                    long childCount = mapLong(childCountByPageId, pageId);
                    payload.append(buildCatalogPageTreeEntry(fields, childCount));
                    payload.append(buildCatalogPageChildPayload(childRowsByParentId == null ? "" : childRowsByParentId.get(pageId),
                        rankIndex, hcLevel));
                    rootCount++;
                }
            }
        }
        return Crypto.encodeVl64(rootCount) + payload;
    }

    public static String buildCatalogPageTreePayload(List<CatalogDao.CatalogPageTreeRow> rootRows,
            Map<Long, Long> childCountByPageId, Map<Long, List<CatalogDao.CatalogPageTreeRow>> childRowsByParentId,
            long rankIndex, long hcLevel) {
        long rootCount = 0L;
        StringBuilder payload = new StringBuilder();
        if (rootRows != null) {
            for (CatalogDao.CatalogPageTreeRow row : rootRows) {
                if (row != null && catalogPageVisible(row, rankIndex, hcLevel)) {
                    long pageId = row.pageId();
                    long childCount = mapLong(childCountByPageId, pageId);
                    payload.append(buildCatalogPageTreeEntry(row, childCount));
                    payload.append(buildCatalogPageChildPayload(
                        childRowsByParentId == null ? null : childRowsByParentId.get(pageId),
                        rankIndex,
                        hcLevel));
                    rootCount++;
                }
            }
        }
        return Crypto.encodeVl64(rootCount) + payload;
    }

    public static String buildCatalogPageChildPayload(String childRows, long rankIndex, long hcLevel) {
        StringBuilder payload = new StringBuilder();
        for (String row : StringUtils.text(childRows).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 6 && catalogPageVisible(fields, rankIndex, hcLevel)) {
                    payload.append(buildCatalogPageTreeEntry(fields, 0));
                }
            }
        }
        return payload.toString();
    }

    public static String buildCatalogPageChildPayload(List<CatalogDao.CatalogPageTreeRow> childRows, long rankIndex, long hcLevel) {
        StringBuilder payload = new StringBuilder();
        if (childRows != null) {
            for (CatalogDao.CatalogPageTreeRow row : childRows) {
                if (row != null && catalogPageVisible(row, rankIndex, hcLevel)) {
                    payload.append(buildCatalogPageTreeEntry(row, 0));
                }
            }
        }
        return payload.toString();
    }

    public static String buildCatalogPageTreeEntry(String[] fields, long childCount) {
        if (fields == null || fields.length < 6) {
            return "";
        }
        long pageId = NumberUtils.parseLong(fields[0]);
        String pageName = fields[1];
        long colorId = NumberUtils.parseLong(fields[2]);
        long iconId = NumberUtils.parseLong(fields[3]);
        long visibleState = NumberUtils.parseLong(fields[5]);
        return Proc_1_14_6C9DD0(pageId, colorId, pageName, visibleState, iconId, childCount);
    }

    public static String buildCatalogPageTreeEntry(CatalogDao.CatalogPageTreeRow row, long childCount) {
        if (row == null) {
            return "";
        }
        return Proc_1_14_6C9DD0(row.pageId(), row.color(), StringUtils.text(row.name()), row.visible(), row.icon(), childCount);
    }

    public static String buildCatalogPageTreeQuery(long parentId, long rankIndex, long hcLevel) {
        return "SELECT id,name,ctlg_color,ctlg_icon,is_develop,is_visible FROM catalog_pages "
            + "WHERE id_parent='" + parentId + "' "
            + "AND level_minrequired <= '" + rankIndex + "' "
            + "AND hclevel_minrequired <= '" + hcLevel + "' "
            + "ORDER BY id_order ASC";
    }

    public static String buildCatalogPageChildCountQuery(long parentId, long rankIndex, long hcLevel) {
        return "SELECT COUNT(id) FROM catalog_pages WHERE id_parent='" + parentId + "' "
            + "AND level_minrequired <= '" + rankIndex + "' "
            + "AND hclevel_minrequired <= '" + hcLevel + "'";
    }

    public static boolean catalogPageVisible(String[] fields, long rankIndex, long hcLevel) {
        if (fields == null || fields.length < 6) {
            return false;
        }
        boolean visible = NumberUtils.parseLong(fields[5]) != 0L;
        if (NumberUtils.parseLong(fields[4]) != 0L) {
            visible = Functions.permissionMatrix().allows(rankIndex, "", "fuse_developer", hcLevel);
        }
        return visible;
    }

    public static boolean catalogPageVisible(CatalogDao.CatalogPageTreeRow row, long rankIndex, long hcLevel) {
        if (row == null) {
            return false;
        }
        boolean visible = row.visible() != 0L;
        if (row.develop() != 0L) {
            visible = Functions.permissionMatrix().allows(rankIndex, "", "fuse_developer", hcLevel);
        }
        return visible;
    }

    public static String appendPermissionPayload(long rankIndex, long hcLevel, String permissionName, String payload) {
        if (Functions.permissionMatrix().allows(rankIndex, "", permissionName, hcLevel)) {
            return permissionName + '\2' + StringUtils.text(payload);
        }
        return "";
    }

    private static String buildFaqNamePayload(String faqRows) {
        StringBuilder payload = new StringBuilder();
        for (String row : StringUtils.text(faqRows).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 2) {
                    payload.append(Crypto.encodeVl64(NumberUtils.parseLong(fields[0])));
                    payload.append(fields[1]).append('\2');
                }
            }
        }
        return payload.toString();
    }

    private static String buildFaqNamePayloadFromRows(List<HelpDao.FaqNameRow> faqRows) {
        StringBuilder payload = new StringBuilder();
        if (faqRows != null) {
            for (HelpDao.FaqNameRow row : faqRows) {
                if (row != null) {
                    payload.append(Crypto.encodeVl64(row.id()));
                    payload.append(StringUtils.text(row.name())).append('\2');
                }
            }
        }
        return payload.toString();
    }

    private static long countFaqNameRows(List<HelpDao.FaqNameRow> rows) {
        long count = 0L;
        if (rows != null) {
            for (HelpDao.FaqNameRow row : rows) {
                if (row != null) {
                    count++;
                }
            }
        }
        return count;
    }

    private static long countRowsWithFields(String rowText, int minimumFieldCount) {
        long count = 0L;
        for (String row : StringUtils.text(rowText).split("\r", -1)) {
            if (!row.isEmpty() && row.split("\t", -1).length >= minimumFieldCount) {
                count++;
            }
        }
        return count;
    }

    private static long countNonZeroRows(List<Long> values) {
        long count = 0L;
        for (Long value : values == null ? List.<Long>of() : values) {
            if (NumberUtils.parseLong(value) != 0L) {
                count++;
            }
        }
        return count;
    }

    private static List<Long> giftWrapProductIds(String wrapRows) {
        List<Long> productIds = new ArrayList<Long>();
        for (String row : StringUtils.text(wrapRows).split("\r", -1)) {
            productIds.add(NumberUtils.parseLong(row));
        }
        return List.copyOf(productIds);
    }

    private static List<Long> recyclerProductIds(String productRows) {
        List<Long> productIds = new ArrayList<Long>();
        for (String productRow : StringUtils.text(productRows).split("\r", -1)) {
            long productId = NumberUtils.parseLong(productRow);
            if (productId > 0L) {
                productIds.add(productId);
            }
        }
        return List.copyOf(productIds);
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

    private static long mapLong(Map<Long, Long> valuesById, long id) {
        if (valuesById == null) {
            return 0L;
        }
        Long value = valuesById.get(id);
        return value == null ? 0L : value.longValue();
    }

    private static String mapString(Map<Long, String> valuesById, long id) {
        if (valuesById == null) {
            return "";
        }
        String value = valuesById.get(id);
        return value == null ? "" : value;
    }

    private static String joinPrivilegeRows(List<SettingsDao.PrivilegeRow> rows) {
        StringBuilder joined = new StringBuilder();
        for (SettingsDao.PrivilegeRow row : rows == null ? List.<SettingsDao.PrivilegeRow>of() : rows) {
            if (row != null) {
                appendLegacyRow(joined, row.privilege());
            }
        }
        return joined.toString();
    }

    private static void appendLegacyRow(StringBuilder joined, String rowText) {
        if (joined.length() > 0) {
            joined.append('\r');
        }
        joined.append(StringUtils.text(rowText));
    }

    private static List<QuestSettings.QuestDefinitionRow> questDefinitionRows(List<QuestDao.QuestDefinition> rows) {
        List<QuestSettings.QuestDefinitionRow> definitions = new ArrayList<>();
        if (rows != null) {
            for (QuestDao.QuestDefinition row : rows) {
                if (row != null) {
                    definitions.add(new QuestSettings.QuestDefinitionRow(
                        row.questId(),
                        row.level(),
                        row.name(),
                        row.legacyNullSlot(),
                        row.reward(),
                        row.rewardType(),
                        row.requiredAction(),
                        row.additionalId(),
                        row.campaignId(),
                        row.activityAmount(),
                        row.waitAmount(),
                        11));
                }
            }
        }
        return definitions;
    }

    private static CatalogDao catalogDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new CatalogDao(database);
    }

    private static PackageDao packageDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new PackageDao(database);
    }

    private static ClubDao clubDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new ClubDao(database);
    }

    private static HelpDao helpDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new HelpDao(database);
    }

    private static RoomDao roomDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new RoomDao(database);
    }

    private static QuestDao questDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new QuestDao(database);
    }

    private static StaffModerationDao staffModerationDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new StaffModerationDao(database);
    }

    private static AdvertisingDao advertisingDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new AdvertisingDao(database);
    }

    private static AchievementDao achievementDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new AchievementDao(database);
    }

    private static ChatDao chatDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new ChatDao(database);
    }

    private static SettingsDao settingsDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new SettingsDao(database);
    }

    private static RecyclerDao recyclerDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new RecyclerDao(database);
    }

    private static ServerMaintenanceDao serverMaintenanceDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new ServerMaintenanceDao(database);
    }

    private static BotDao botDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new BotDao(database);
    }
}
