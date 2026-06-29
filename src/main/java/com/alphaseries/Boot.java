package com.alphaseries;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public final class Boot {
    private Boot() {
    }

    public static void Proc_1_0_6BA9D0(Object... args) {
        Map<Long, String> productsByChance = new LinkedHashMap<Long, String>();
        String chanceRows = MySQL.Proc_5_2_6D4690(
            "SELECT chance FROM settings_recycler GROUP BY settings_recycler.chance ORDER BY settings_recycler.chance DESC LIMIT 50", 0, 0);
        for (String row : StringUtils.text(chanceRows).split("\r", -1)) {
            long chance = NumberUtils.parseLong(row);
            if (chance != 0L) {
                productsByChance.put(chance, MySQL.Proc_5_2_6D4690(
                    "SELECT id_product FROM settings_recycler WHERE chance='" + chance + "' LIMIT 100", 0, 0));
            }
        }
        RecyclerCache cache = buildRecyclerCache(chanceRows, productsByChance);
        Licence.setRecyclerStatusPayload(cache.payload);
        String[] productLists = new String[50];
        String[] chances = new String[50];
        for (Map.Entry<Long, String> entry : cache.productListByGroupIndex.entrySet()) {
            int index = entry.getKey().intValue();
            if (index >= 0 && index < productLists.length) {
                productLists[index] = entry.getValue();
                Long chance = cache.chanceByGroupIndex.get(entry.getKey());
                chances[index] = chance == null ? "" : String.valueOf(chance.longValue());
            }
        }
        Licence.setRecyclerRewards(productLists, chances, cache.groupCount);
    }

    public static void Proc_1_1_6BB340(Object... args) {
        long maxProductId = Math.max(0L, NumberUtils.parseLong(MySQL.Proc_5_2_6D4690("SELECT MAX(id) FROM products", 0, 0)));
        String[] products = new String[(int) maxProductId + 1];
        String productQuery = "SELECT id,id_type,action,NULL,NULL,default_sign,status_max,handitems,distance_allowed,"
            + "is_tradeable,is_recycleable,is_signable,default_sign,min_roomrights,name,description,NULL,NULL,sprite,"
            + "is_iconstack,id_deco,time_rent,square_x,square_y,square_z,NULL,effect,receive_badge,wire,id_counter,"
            + "square_rotation,status_walkon,status_walkoff,NULL,has_charge,charge_price_credits,"
            + "charge_price_activitypoints,charge_price_activitypoints_type,charge_size,NULL,is_marketofferable,is_badgeshop "
            + "FROM products ORDER BY id ASC";
        cacheRowsById(products, MySQL.Proc_5_2_6D4690(productQuery, 0, 0));
        Licence.setProductRows(products);
        DataManager.setProductRows(products);

        long maxCatalogId = Math.max(0L, NumberUtils.parseLong(MySQL.Proc_5_2_6D4690("SELECT MAX(id) FROM catalog_products", 0, 0)));
        String[] catalogProducts = new String[(int) maxCatalogId + 1];
        String catalogQuery = "SELECT id,sprite,id_product,ctlg_pageid,type_secondary,amount,receive_badge,price_credits,"
            + "price_activitypoints,type_activitypoints,allow_gifts,min_hc_level_required,replace_defaultsign "
            + "FROM catalog_products ORDER BY id ASC";
        cacheRowsById(catalogProducts, MySQL.Proc_5_2_6D4690(catalogQuery, 0, 0));
        Licence.setCatalogProductRows(catalogProducts);
        Licence.setDealRows("\r" + MySQL.Proc_5_2_6D4690("SELECT id,items FROM products_deals ORDER BY id ASC", "\r", 0) + "\r");
        Licence.setRecyclerBoxProductId(NumberUtils.parseLong(MySQL.Proc_5_2_6D4690("SELECT id FROM products WHERE sprite='ecotron_box' LIMIT 1", 0, 0)));
        Licence.setCounterProductIds(MySQL.Proc_5_2_6D4690("SELECT id FROM products WHERE id_counter IS NOT NULL", 0, 0).replace('\r', '\t'));
        Licence.setTeleportProductId(NumberUtils.parseLong(MySQL.Proc_5_2_6D4690("SELECT id FROM products WHERE id_type='11' LIMIT 1", 0, 0)));
        Licence.setMoodlightProductId(NumberUtils.parseLong(MySQL.Proc_5_2_6D4690("SELECT id FROM products WHERE id_type='19' LIMIT 1", 0, 0)));
        Licence.setPackageRows(MySQL.Proc_5_2_6D4690("SELECT id_product,type_secondary,id_contain,type_check FROM packages", 0, 0));
        Licence.setPetPackageRows(MySQL.Proc_5_2_6D4690("SELECT id,id_pet,id_race,color FROM packages_pets", 0, 0));
        Licence.setClubProductRows("\r" + MySQL.Proc_5_2_6D4690("SELECT id_product,months,level FROM products_containshc", "\r", 0) + "\r");
        Proc_1_17_6CCDC0(0, 0, 0);
        Proc_1_15_6CA000(0, 0, 0);
        Proc_1_18_6CE9C0(0, 0, 0);
        Proc_1_13_6C9820(0, 0, 0);
        Proc_1_0_6BA9D0(0, 0, 0);
    }

    public static void Proc_1_2_6BE280(Object... args) {
        String[] recommended = new String[100];
        long count = 0L;
        for (String row : MySQL.Proc_5_2_6D4690("SELECT id_tree FROM rooms_recommented GROUP BY id_tree", 0, 0).split("\r", -1)) {
            long treeId = NumberUtils.parseLong(row);
            if (treeId != 0L && count < recommended.length) {
                String roomRows = MySQL.Proc_5_2_6D4690(buildRecommendedRoomsQuery(treeId), 0, 0);
                recommended[(int) count] = Crypto.Proc_3_0_6D2AF0(treeId, null, "") + buildRecommendedRoomsPayload(roomRows);
                count++;
            }
        }
        Licence.setRecommendedRooms(count == 0L ? Crypto.Proc_3_0_6D2AF0(0, null, "") : recommended, count);
    }

    public static void Proc_1_3_6BEBA0(Object... args) {
        Proc_1_2_6BE280(0, 0, 0);
        Proc_1_5_6C4F80(0, 0, 0);
        Proc_1_7_6C5E10(0, 0, 0);
        MySQL.Proc_5_0_6D3CD0("UPDATE users SET id_socket=null,lastonline_time=UNIX_TIMESTAMP() WHERE id_socket IS NOT NULL", 0, 0);
        MySQL.Proc_5_0_6D3CD0("UPDATE rooms SET id_slot=null, visitors_now='0' WHERE visitors_now != 0", 0, 0);
        MySQL.Proc_5_0_6D3CD0("DELETE FROM logs_visitedrooms WHERE timestamp_left IS NULL", 0, 0);
        MySQL.Proc_5_0_6D3CD0("DELETE FROM rooms_events", 0, 0);
        Proc_1_9_6C6DF0(0, 0, 0);
        Proc_1_8_6C6850(0, 0, 0);
        Proc_1_11_6C8D10(0, 0, 0);
        Proc_1_12_6C8EF0(0, 0, 0);
        Proc_1_22_6D0F00(0, 0, 0);
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
        AchievementSettingsCache achievementCache = buildAchievementSettingsCache(MySQL.Proc_5_2_6D4690(
            "SELECT id_quest,id_badge,progress,reward_increase,level_total,score_increase,type_reward "
                + "FROM settings_achievements WHERE is_enabled='1' LIMIT 100", 0, 0));
        Licence.setAchievementSettings(achievementCache.questIdPayload, achievementRowsByIndex(achievementCache));
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
        Licence.setMessengerFriendLimits(buildMessengerFriendLimitCache(
            NumberUtils.parseLong(Functions.Proc_10_0_809570("com.client.messenger.maxfriends.hclevel0", 0, 0)),
            NumberUtils.parseLong(Functions.Proc_10_0_809570("com.client.messenger.maxfriends.hclevel1", 0, 0)),
            NumberUtils.parseLong(Functions.Proc_10_0_809570("com.client.messenger.maxfriends.hclevel2", 0, 0))));
    }

    public static void Proc_1_6_6C5830(Object... args) {
        Licence.setPetRaceRows(buildPetRaceCache(MySQL.Proc_5_2_6D4690(
            "SELECT product_pet,id_pet,breed,min_rank,min_hcrank,name FROM settings_petraces", 0, 0)));
    }

    public static void Proc_1_7_6C5E10(Object... args) {
        long maxLevelId = Math.max(0L, NumberUtils.parseLong(MySQL.Proc_5_2_6D4690("SELECT MAX(id_level) FROM bots_petlevels", 0, 0)));
        String[] levels = new String[(int) maxLevelId + 1];
        for (Map.Entry<Long, String> entry : buildPetLevelCache(MySQL.Proc_5_2_6D4690(
                "SELECT id_level,max_energy,max_exp,max_nutrition FROM bots_petlevels ORDER BY id_level ASC", 0, 0)).entrySet()) {
            if (entry.getKey() >= 0L && entry.getKey() < levels.length) {
                levels[entry.getKey().intValue()] = entry.getValue();
            }
        }
        Licence.setPetLevelRows(levels);
        long commandCount = NumberUtils.parseLong(MySQL.Proc_5_2_6D4690("SELECT COUNT(id_command) FROM bots_petcommands", 0, 0));
        long maxCommandId = Math.max(commandCount,
            NumberUtils.parseLong(MySQL.Proc_5_2_6D4690("SELECT MAX(id_command) FROM bots_petcommands", 0, 0)));
        String[] commands = new String[(int) Math.max(0L, maxCommandId) + 1];
        PetCommandCache cache = buildPetCommandCache(MySQL.Proc_5_2_6D4690(
            "SELECT id_command,petlevel_required,command,command_action FROM bots_petcommands", 0, 0));
        for (Map.Entry<Long, String> entry : cache.commandById.entrySet()) {
            if (entry.getKey() >= 0L && entry.getKey() < commands.length) {
                commands[entry.getKey().intValue()] = entry.getValue();
            }
        }
        Licence.setPetCommandRows(commands, commandCount);
    }

    public static void Proc_1_8_6C6850(Object... args) {
        DataManager.setRoomEventLocaleCache(buildRoomEventLocaleCache(MySQL.Proc_5_2_6D4690(
            "SELECT variable,value FROM locales WHERE category='2' AND variable LIKE 'roomevent_type_%'", 0, 0),
            DataManager.roomEventLocales().cacheText()));
    }

    public static void Proc_1_9_6C6DF0(Object... args) {
        Licence.setRoomPortalSettings(
            "0" + MySQL.Proc_5_2_6D4690(
                "SELECT id_room,position_x,position_y,id_warp_room,warp_x,warp_y,is_special FROM rooms_warpspaces", "\r", 0) + "\r",
            MySQL.Proc_5_2_6D4690("SELECT  id_room,is_open FROM  rooms_specialgates", "\r", 0) + "\r");
        Proc_1_16_6CCA60(0, 0, 0);
        String systemDate = Functions.Proc_10_0_809570("com.system.format.date", "", 0);
        String systemTime = Functions.Proc_10_0_809570("com.system.format.time", "", 0);
        Functions.setSettingsCache(buildSettingsCache(MySQL.Proc_5_2_6D4690("SELECT variable,value FROM settings", 0, 0),
            systemDate, systemTime));
        Licence.setQuestRows(MySQL.Proc_5_2_6D4690(
            "SELECT id,level,name,NULL,reward,reward_type,require_action,id_additional,id_campaign,amount_activities,waitamount FROM quests ORDER BY id_campaign DESC,level ASC", 0, 0));
    }

    public static void Proc_1_10_6C7690(Object... args) {
        String callForHelpMessages = buildStaffMessageList(MySQL.Proc_5_2_6D4690(
            "SELECT message FROM staff_messages WHERE type='1' ORDER BY id ASC", 0, 0));
        String moderatorMessages = buildStaffMessageList(MySQL.Proc_5_2_6D4690(
            "SELECT message FROM staff_messages WHERE type='2' ORDER BY id ASC", 0, 0));
        String categoryPayload = "";
        String[][] values = new String[21][3];
        for (int rank = 0; rank <= 20; rank++) {
            for (int hc = 0; hc <= 2; hc++) {
                values[rank][hc] = appendPermissionPayload(rank, hc, "fuse_mod", callForHelpMessages + categoryPayload)
                    + appendPermissionPayload(rank, hc, "fuse_receive_calls_for_help", callForHelpMessages)
                    + appendPermissionPayload(rank, hc, "fuse_chatlog", "")
                    + appendPermissionPayload(rank, hc, "fuse_alert", "")
                    + appendPermissionPayload(rank, hc, "fuse_kick", "")
                    + appendPermissionPayload(rank, hc, "fuse_ban", "")
                    + appendPermissionPayload(rank, hc, "fuse_room_alert", "")
                    + appendPermissionPayload(rank, hc, "fuse_room_kick", "")
                    + appendPermissionPayload(rank, hc, "fuse_edit_localizations", moderatorMessages);
            }
        }
        Licence.setStaffModerationPayloads(values);
    }

    public static void Proc_1_11_6C8D10(Object... args) {
        long privateCategoryId = NumberUtils.parseLong(Functions.Proc_10_0_809570("com.client.navigator.categories.default.private.id", 0, 0));
        long publicCategoryId = NumberUtils.parseLong(Functions.Proc_10_0_809570("com.client.navigator.categories.default.public.id", 0, 0));
        String[] defaults = new String[3];
        defaults[0] = String.valueOf(privateCategoryId);
        defaults[2] = String.valueOf(publicCategoryId);
        Licence.setRoomCategoryDefaults(defaults);
        long parentCategoryId = privateCategoryId == 0L ? 1L : privateCategoryId;
        Licence.setRoomCategoryRows(MySQL.Proc_5_2_6D4690("SELECT id,name,has_trading,level_minrequired,hclevel_minrequired "
            + "FROM rooms_categories WHERE id_parent='" + parentCategoryId + "' ORDER BY id ASC", 0, 0));
    }

    public static void Proc_1_12_6C8EF0(Object... args) {
        String[][] values = new String[21][3];
        for (int rank = 0; rank <= 20; rank++) {
            for (int hc = 0; hc <= 2; hc++) {
                values[rank][hc] = buildRoomCategoryPayload(Licence.roomCategoryCache().categoryRows(), rank, hc);
            }
        }
        Licence.setRoomCategoryPayloads(values);
    }

    public static void Proc_1_13_6C9820(Object... args) {
        String wrapRows = MySQL.Proc_5_2_6D4690("SELECT id FROM products WHERE sprite LIKE 'present_wrap*%'", "\r", 0);
        long wrapCount = countNonZeroRows(wrapRows);
        long accessoryCount = NumberUtils.parseLong(Functions.Proc_10_0_809570("com.client.catalog.gifts.wrap.count.accessories", wrapCount, 0));
        long colorCount = NumberUtils.parseLong(Functions.Proc_10_0_809570("com.client.catalog.gifts.wrap.count.colors", 0, 0));
        Licence.setGiftWrapState("\r" + wrapRows + "\r", buildGiftWrapPayload(wrapRows, accessoryCount, colorCount));
    }

    public static void Proc_1_15_6CA000(Object... args) {
        long maxPageId = Math.max(0L, NumberUtils.parseLong(MySQL.Proc_5_2_6D4690("SELECT MAX(id) FROM catalog_pages", 0, 0)));
        String[] pages = new String[(int) maxPageId + 1];
        String pageQuery = "SELECT id,name,level_minrequired,hclevel_minrequired,is_clickable,ctlg_template,"
            + "ctlg_header_img,ctlg_special_img,ctlg_special_template,ctlg_txt1,ctlg_txt2,ctlg_txt3,ctlg_txt4,"
            + "ctlg_txt5,ctlg_txt6,ctlg_txt7,ctlg_txt8,ctlg_txt9,ctlg_txt10,ctlg_txt11,ctlg_link,is_develop "
            + "FROM catalog_pages ORDER BY id_order ASC";
        for (String row : MySQL.Proc_5_2_6D4690(pageQuery, 0, 0).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                long pageId = NumberUtils.parseLong(fields.length > 0 ? fields[0] : "");
                if (fields.length >= 21 && pageId >= 0L && pageId < pages.length) {
                    pages[(int) pageId] = buildCatalogPagePayload(fields, MySQL.Proc_5_2_6D4690(buildCatalogProductQuery(pageId), 0, 0));
                }
            }
        }
        Licence.setCatalogPagePayloads(pages);
    }

    public static void Proc_1_16_6CCA60(Object... args) {
        String[][] permissions = new String[21][3];
        for (int rank = 0; rank <= 20; rank++) {
            for (int hc = 0; hc <= 2; hc++) {
                String rows = MySQL.Proc_5_2_6D4690("SELECT privilege FROM level_privileges WHERE min_level <= '"
                    + rank + "' AND min_level_hc <= '" + hc + "'", 0, 0);
                permissions[rank][hc] = rows.isEmpty() ? "\2" : "\2" + rows.replace('\r', '\2') + "\2";
            }
        }
        Functions.setPermissions(permissions);
    }

    public static void Proc_1_17_6CCDC0(Object... args) {
        String[][] trees = new String[21][3];
        for (int rank = 0; rank <= 20; rank++) {
            for (int hc = 0; hc <= 2; hc++) {
                String rootRows = MySQL.Proc_5_2_6D4690(buildCatalogPageTreeQuery(0, rank, hc), 0, 0);
                Map<Long, Long> childCounts = new LinkedHashMap<Long, Long>();
                Map<Long, String> children = new LinkedHashMap<Long, String>();
                for (String row : rootRows.split("\r", -1)) {
                    if (!row.isEmpty()) {
                        String[] fields = row.split("\t", -1);
                        long pageId = NumberUtils.parseLong(fields.length > 0 ? fields[0] : "");
                        childCounts.put(pageId, NumberUtils.parseLong(MySQL.Proc_5_2_6D4690(buildCatalogPageChildCountQuery(pageId, rank, hc), 0, 0)));
                        children.put(pageId, MySQL.Proc_5_2_6D4690(buildCatalogPageTreeQuery(pageId, rank, hc), 0, 0));
                    }
                }
                trees[rank][hc] = buildCatalogPageTreePayload(rootRows, childCounts, children, rank, hc);
            }
        }
        Licence.setCatalogPageTrees(trees);
    }

    public static void Proc_1_18_6CE9C0(Object... args) {
        StringBuilder payload = new StringBuilder();
        StringBuilder lookup = new StringBuilder();
        long count = 0L;
        for (String row : MySQL.Proc_5_2_6D4690("SELECT id_product,is_vip,required_days FROM club_gifts ORDER by id ASC", 0, 0).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 3) {
                    long catalogProductId = NumberUtils.parseLong(fields[0]);
                    long productId = Licence.Proc_9_2_8075F0(catalogProductId, 2, 0);
                    if (productId == 0L) {
                        productId = catalogProductId;
                    }
                    String giftClass = Licence.Proc_9_0_806F70(productId, 1, 0) == 9L ? "i" : "s";
                    payload.append(Crypto.Proc_3_0_6D2AF0(catalogProductId, null, ""));
                    payload.append(Crypto.Proc_3_0_6D2AF0(productId, null, ""));
                    payload.append(DataManager.Proc_8_12_806C30(productId, 14, 0)).append('\2');
                    payload.append(DataManager.Proc_8_12_806C30(productId, 15, 0)).append('\2');
                    payload.append("IHHI").append(giftClass).append('\2');
                    payload.append(Crypto.Proc_3_0_6D2AF0(NumberUtils.parseLong(fields[1]), null, ""));
                    payload.append(Crypto.Proc_3_0_6D2AF0(NumberUtils.parseLong(fields[2]), null, ""));
                    lookup.append('[').append(catalogProductId).append('\0').append(productId).append('\1').append(NumberUtils.parseLong(fields[2])).append(']');
                    count++;
                }
            }
        }
        Licence.setClubGiftState(Crypto.Proc_3_0_6D2AF0(count, null, "") + payload, lookup.toString());
    }

    public static void Proc_1_19_6CF190(Object... args) {
        Map<Long, String> rows = new LinkedHashMap<Long, String>();
        rows.put(1L, MySQL.Proc_5_2_6D4690("SELECT id,name FROM faq WHERE is_important='1' ORDER BY id DESC LIMIT 1", 0, 0));
        rows.put(2L, MySQL.Proc_5_2_6D4690("SELECT id,name FROM faq WHERE is_important='2' ORDER BY id DESC LIMIT 1", 0, 0));
        Licence.setImportantFaqPayload(buildImportantFaqPayload(rows));
    }

    public static void Proc_1_20_6CF830(Object... args) {
        long maxCategoryId = Math.max(0L, NumberUtils.parseLong(MySQL.Proc_5_2_6D4690("SELECT MAX(id) FROM faq_categories", 0, 0)));
        String[] categoryFaqs = new String[(int) maxCategoryId + 1];
        String categoryRows = MySQL.Proc_5_2_6D4690("SELECT id,name FROM faq_categories", 0, 0);
        Map<Long, String> faqRows = new LinkedHashMap<Long, String>();
        for (String row : categoryRows.split("\r", -1)) {
            long categoryId = NumberUtils.parseLong(row);
            if (categoryId >= 0L && categoryId < categoryFaqs.length) {
                faqRows.put(categoryId, MySQL.Proc_5_2_6D4690("SELECT id,name FROM faq WHERE id_category='" + categoryId + "'", 0, 0));
            }
        }
        FaqCategoryCache cache = buildFaqCategoryCache(categoryRows, faqRows);
        for (Map.Entry<Long, String> entry : cache.faqPayloadByCategoryId.entrySet()) {
            if (entry.getKey() >= 0L && entry.getKey() < categoryFaqs.length) {
                categoryFaqs[entry.getKey().intValue()] = entry.getValue();
            }
        }
        Licence.setFaqCategoryCache(cache.categoryPayload, categoryFaqs);
    }

    public static void Proc_1_21_6D08C0(Object... args) {
        long maxFaqId = Math.max(0L, NumberUtils.parseLong(MySQL.Proc_5_2_6D4690("SELECT MAX(id) FROM faq", 0, 0)));
        String[] descriptions = new String[(int) maxFaqId + 1];
        Map<Long, String> cache = buildFaqDescriptionCache(MySQL.Proc_5_2_6D4690("SELECT id,description FROM faq", 0, 0));
        for (Map.Entry<Long, String> entry : cache.entrySet()) {
            if (entry.getKey() >= 0L && entry.getKey() < descriptions.length) {
                descriptions[entry.getKey().intValue()] = entry.getValue();
            }
        }
        Licence.setFaqDescriptionCache(descriptions);
    }

    public static void Proc_1_22_6D0F00(Object... args) {
        long maxId = Math.max(0L, NumberUtils.parseLong(MySQL.Proc_5_2_6D4690("SELECT MAX(id) FROM advertisement_visitrooms", 0, 0)));
        String[] visitRooms = new String[(int) maxId + 1];
        VisitRoomCache cache = buildAdvertisementVisitRoomCache(
            MySQL.Proc_5_2_6D4690("SELECT id,address FROM advertisement_visitrooms", 0, 0),
            Functions.Proc_10_0_809570("com.server.socket.game.advertisement.visitrooms.path", "", 0));
        for (Map.Entry<Long, String> entry : cache.payloadByVisitRoomId.entrySet()) {
            if (entry.getKey() >= 0L && entry.getKey() < visitRooms.length) {
                visitRooms[entry.getKey().intValue()] = entry.getValue();
            }
        }
        Licence.setVisitRoomAds(visitRooms, cache.count);
    }

    public static void Proc_1_23_6D1480(Object... args) {
        String messageText = args != null && args.length >= 1 ? StringUtils.text(args[0]) : "";
        String logChannel = args != null && args.length >= 2 ? StringUtils.text(args[1]) : "";
        Console.Proc_2_0_6D1510(messageText, logChannel, "65280");
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
        return Crypto.Proc_3_0_6D2AF0(replacementCount, null, "") + payload;
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
                                childPayload.append(Crypto.Proc_3_0_6D2AF0(NumberUtils.parseLong(childFields[0]), null, ""));
                                childPayload.append(childFields[1]).append('\2');
                                childCount++;
                            }
                        }
                    }
                    payload.append(Crypto.Proc_3_0_6D2AF0(rootId, null, ""));
                    payload.append(rootFields[1]).append('\2');
                    payload.append(Crypto.Proc_3_0_6D2AF0(childCount, null, ""));
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
        public Map<Long, String> commandById = new LinkedHashMap<Long, String>();
    }

    public static final class ClubGiftCache {
        public String giftPayload = "";
        public String giftLookup = "";
    }

    public static final class AchievementSettingsCache {
        public String questIdPayload = "";
        public Map<Long, String[]> rowsByIndex = new LinkedHashMap<Long, String[]>();
    }

    public static final class RecyclerCache {
        public String payload = "";
        public long groupCount;
        public Map<Long, String> productListByGroupIndex = new LinkedHashMap<Long, String>();
        public Map<Long, Long> chanceByGroupIndex = new LinkedHashMap<Long, Long>();
    }

    public static RecyclerCache buildRecyclerCache(String chanceRows, Map<Long, String> productRowsByChance) {
        RecyclerCache cache = new RecyclerCache();
        StringBuilder payload = new StringBuilder();
        for (String chanceRow : StringUtils.text(chanceRows).split("\r", -1)) {
            if (cache.groupCount > 49L) {
                break;
            }
            if (!chanceRow.isEmpty()) {
                long chanceValue = NumberUtils.parseLong(chanceRow);
                long groupIndex = cache.groupCount;
                cache.chanceByGroupIndex.put(groupIndex, chanceValue);

                long productCount = 0L;
                StringBuilder productList = new StringBuilder();
                StringBuilder groupPayload = new StringBuilder();
                String productRows = productRowsByChance == null ? "" : productRowsByChance.get(chanceValue);
                for (String productRow : StringUtils.text(productRows).split("\r", -1)) {
                    if (!productRow.isEmpty()) {
                        long productId = NumberUtils.parseLong(productRow);
                        if (productId > 0L) {
                            productList.append(productId).append('\2');
                            groupPayload.append(Crypto.Proc_3_0_6D2AF0(productId, null, ""));
                            productCount++;
                        }
                    }
                }

                cache.productListByGroupIndex.put(groupIndex, productList.toString());
                payload.append(Crypto.Proc_3_0_6D2AF0(chanceValue, null, ""));
                payload.append(Crypto.Proc_3_0_6D2AF0(productCount, null, ""));
                payload.append(groupPayload);
                cache.groupCount++;
            }
        }
        cache.payload = Crypto.Proc_3_0_6D2AF0(cache.groupCount, null, "") + payload;
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

    public static PetCommandCache buildPetCommandCache(String commandRows) {
        PetCommandCache cache = new PetCommandCache();
        for (String row : StringUtils.text(commandRows).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 4) {
                    long commandId = NumberUtils.parseLong(fields[0]);
                    cache.commandById.put(commandId, commandId + "\t" + NumberUtils.parseLong(fields[1]) + "\t" + fields[2] + "\t" + fields[3]);
                    cache.commandCount++;
                }
            }
        }
        return cache;
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

    public static String buildGiftWrapPayload(String wrapRows, long accessoryCount, long colorCount) {
        long wrapCount = 0L;
        StringBuilder wrapPayload = new StringBuilder();
        for (String row : StringUtils.text(wrapRows).split("\r", -1)) {
            long wrapId = NumberUtils.parseLong(row);
            if (wrapId != 0L) {
                wrapCount++;
                wrapPayload.append(Crypto.Proc_3_0_6D2AF0(wrapId, null, ""));
            }
        }

        StringBuilder accessoryPayload = new StringBuilder();
        for (long optionIndex = 1L; optionIndex <= accessoryCount; optionIndex++) {
            accessoryPayload.append(Crypto.Proc_3_0_6D2AF0(optionIndex, null, ""));
        }

        StringBuilder colorPayload = new StringBuilder();
        for (long optionIndex = 1L; optionIndex <= colorCount; optionIndex++) {
            colorPayload.append(Crypto.Proc_3_0_6D2AF0(optionIndex, null, ""));
        }

        return Crypto.Proc_3_0_6D2AF0(accessoryCount, null, "")
            + accessoryPayload
            + Crypto.Proc_3_0_6D2AF0(wrapCount, null, "")
            + wrapPayload
            + Crypto.Proc_3_0_6D2AF0(colorCount, null, "")
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

                    giftPayload.append(Crypto.Proc_3_0_6D2AF0(catalogProductId, null, ""));
                    giftPayload.append(Crypto.Proc_3_0_6D2AF0(productId, null, ""));
                    giftPayload.append(giftName).append('\2').append(giftDescription).append('\2');
                    giftPayload.append("IHHI").append(giftClass).append('\2');
                    giftPayload.append(Crypto.Proc_3_0_6D2AF0(isVip, null, ""));
                    giftPayload.append(Crypto.Proc_3_0_6D2AF0(requiredDays, null, ""));

                    giftLookup.append('[').append(catalogProductId).append('\0').append(productId)
                        .append('\1').append(requiredDays).append(']');
                    giftCount++;
                }
            }
        }
        cache.giftPayload = Crypto.Proc_3_0_6D2AF0(giftCount, null, "") + giftPayload;
        cache.giftLookup = giftLookup.toString();
        return cache;
    }

    public static AchievementSettingsCache buildAchievementSettingsCache(String achievementRows) {
        AchievementSettingsCache cache = new AchievementSettingsCache();
        long achievementIndex = 0L;
        StringBuilder questIds = new StringBuilder();
        for (String row : StringUtils.text(achievementRows).split("\r", -1)) {
            if (achievementIndex > 100L) {
                break;
            }
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 7) {
                    questIds.append(NumberUtils.parseLong(fields[0])).append('\2');
                    cache.rowsByIndex.put(achievementIndex, new String[] {
                        String.valueOf(NumberUtils.parseLong(fields[0])),
                        fields[1],
                        String.valueOf(NumberUtils.parseLong(fields[2])),
                        String.valueOf(NumberUtils.parseLong(fields[3])),
                        String.valueOf(NumberUtils.parseLong(fields[4])),
                        String.valueOf(NumberUtils.parseLong(fields[5])),
                        String.valueOf(NumberUtils.parseLong(fields[6]))
                    });
                    achievementIndex++;
                }
            }
        }
        cache.questIdPayload = questIds.toString();
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
        Licence.setChatSettings(
            MySQL.Proc_5_2_6D4690("SELECT word FROM settings_filter LIMIT 100", 0, 0),
            MySQL.Proc_5_2_6D4690("SELECT smiley,gesture FROM settings_gesture LIMIT 100", 0, 0));
    }

    private static String[][] achievementRowsByIndex(AchievementSettingsCache cache) {
        String[][] rows = new String[cache.rowsByIndex.size()][];
        for (Map.Entry<Long, String[]> entry : cache.rowsByIndex.entrySet()) {
            int index = entry.getKey().intValue();
            if (index >= 0 && index < rows.length) {
                rows[index] = entry.getValue();
            }
        }
        return rows;
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
                        categoryPayload.append(Crypto.Proc_3_0_6D2AF0(categoryId, null, ""));
                        categoryPayload.append(fields[1]).append('\2');
                        categoryPayload.append(Crypto.Proc_3_0_6D2AF0(hasTrading, null, ""));
                        categoryCount++;
                    }
                }
            }
        }
        return Crypto.Proc_3_0_6D2AF0(categoryCount, null, "") + categoryPayload;
    }

    public static String buildImportantFaqPayload(Map<Long, String> faqRowsByImportance) {
        StringBuilder payload = new StringBuilder();
        for (long importanceLevel = 1L; importanceLevel <= 2L; importanceLevel++) {
            String groupPayload = buildFaqNamePayload(faqRowsByImportance == null ? "" : faqRowsByImportance.get(importanceLevel));
            long groupCount = countRowsWithFields(faqRowsByImportance == null ? "" : faqRowsByImportance.get(importanceLevel), 2);
            payload.append(Crypto.Proc_3_0_6D2AF0(groupCount, null, ""));
            payload.append(groupPayload);
        }
        return Crypto.Proc_3_0_6D2AF0(2, null, "") + payload;
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
                    cache.faqPayloadByCategoryId.put(categoryId, Crypto.Proc_3_0_6D2AF0(faqCount, null, "") + faqPayload);
                    categoryPayload.append(Crypto.Proc_3_0_6D2AF0(categoryId, null, ""));
                    categoryPayload.append(categoryFields[1]).append('\2');
                    categoryCount++;
                }
            }
        }
        cache.categoryPayload = Crypto.Proc_3_0_6D2AF0(categoryCount, null, "") + categoryPayload;
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
                    cache.put(faqId, Crypto.Proc_3_0_6D2AF0(faqId, null, "") + descriptionText + '\2');
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
                    payload.append(Crypto.Proc_3_0_6D2AF0(NumberUtils.parseLong(fields[0]), null, ""));
                    payload.append(Crypto.Proc_3_0_6D2AF0(NumberUtils.parseLong(fields[1]), null, ""));
                    payload.append(Crypto.Proc_3_0_6D2AF0(NumberUtils.parseLong(fields[2]), null, ""));
                    for (int fieldIndex = 3; fieldIndex <= 24; fieldIndex++) {
                        payload.append(fields[fieldIndex]).append('\2');
                    }
                    payload.append(Crypto.Proc_3_0_6D2AF0(NumberUtils.parseLong(fields[25]), null, ""));
                    payload.append(Crypto.Proc_3_0_6D2AF0(NumberUtils.parseLong(fields[26]), null, ""));
                }
            }
        }
        return Crypto.Proc_3_0_6D2AF0(roomCount, null, "") + payload;
    }

    public static String Proc_1_14_6C9DD0(Object... args) {
        long pageId = args != null && args.length >= 1 ? NumberUtils.parseLong(args[0]) : 0L;
        long parentId = args != null && args.length >= 2 ? NumberUtils.parseLong(args[1]) : 0L;
        String caption = args != null && args.length >= 3 ? StringUtils.text(args[2]) : "";
        long visibleState = args != null && args.length >= 4 ? NumberUtils.parseLong(args[3]) : 0L;
        long iconId = args != null && args.length >= 5 ? NumberUtils.parseLong(args[4]) : 0L;
        long childCount = args != null && args.length >= 6 ? NumberUtils.parseLong(args[5]) : 0L;

        return "0"
            + Crypto.Proc_3_0_6D2AF0(pageId, null, "")
            + Crypto.Proc_3_0_6D2AF0(parentId, null, "")
            + Crypto.Proc_3_0_6D2AF0(iconId, null, "")
            + Crypto.Proc_3_0_6D2AF0(visibleState, null, "")
            + caption + '\2'
            + Crypto.Proc_3_0_6D2AF0(childCount, null, "");
    }

    public static String buildCatalogPagePayload(String[] fields, String productRows) {
        if (fields == null || fields.length < 21) {
            return "";
        }
        long pageId = NumberUtils.parseLong(fields[0]);
        StringBuilder payload = new StringBuilder();
        payload.append(fields[1]).append('\2');
        payload.append(Crypto.Proc_3_0_6D2AF0(NumberUtils.parseLong(fields[4]), null, ""));
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
        payload.append(Crypto.Proc_3_0_6D2AF0(textCount, null, "")).append(textPayload);

        if (catalogTextFieldPresent(fields[20])) {
            payload.append(Crypto.Proc_3_0_6D2AF0(1, null, "")).append(fields[20]).append('\2');
        } else {
            payload.append(Crypto.Proc_3_0_6D2AF0(0, null, ""));
        }
        return payload.append(buildCatalogProductPayload(pageId, productRows)).toString();
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
        return Crypto.Proc_3_0_6D2AF0(productCount, null, "") + productPayload;
    }

    public static String buildCatalogProductEntry(String[] fields) {
        if (fields == null || fields.length < 10) {
            return "";
        }
        long catalogProductId = NumberUtils.parseLong(fields[0]);
        long productId = NumberUtils.parseLong(fields[1]);
        long productType = Licence.Proc_9_0_806F70(productId, 1, 0);
        String productClass = catalogProductClass(productType);
        long amountValue = NumberUtils.parseLong(fields[6]);
        if (amountValue <= 0L) {
            amountValue = 1L;
        }

        return Crypto.Proc_3_0_6D2AF0(catalogProductId, null, "")
            + fields[4] + '\2'
            + Crypto.Proc_3_0_6D2AF0(productId, null, "")
            + productClass + '\2'
            + Crypto.Proc_3_0_6D2AF0(NumberUtils.parseLong(fields[2]), null, "")
            + Crypto.Proc_3_0_6D2AF0(NumberUtils.parseLong(fields[3]), null, "")
            + Crypto.Proc_3_0_6D2AF0(NumberUtils.parseLong(fields[5]), null, "")
            + Crypto.Proc_3_0_6D2AF0(amountValue, null, "")
            + fields[7] + '\2'
            + Crypto.Proc_3_0_6D2AF0(NumberUtils.parseLong(fields[8]), null, "")
            + Crypto.Proc_3_0_6D2AF0(NumberUtils.parseLong(fields[9]), null, "");
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
        return Crypto.Proc_3_0_6D2AF0(rootCount, null, "") + payload;
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
            visible = Functions.Proc_10_1_809790(rankIndex, "", "fuse_developer", hcLevel);
        }
        return visible;
    }

    public static String appendPermissionPayload(long rankIndex, long hcLevel, String permissionName, String payload) {
        if (Functions.Proc_10_1_809790(rankIndex, "", permissionName, hcLevel)) {
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
                    payload.append(Crypto.Proc_3_0_6D2AF0(NumberUtils.parseLong(fields[0]), null, ""));
                    payload.append(fields[1]).append('\2');
                }
            }
        }
        return payload.toString();
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

    private static long countNonZeroRows(String rowText) {
        long count = 0L;
        for (String row : StringUtils.text(rowText).split("\r", -1)) {
            if (NumberUtils.parseLong(row) != 0L) {
                count++;
            }
        }
        return count;
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
}
