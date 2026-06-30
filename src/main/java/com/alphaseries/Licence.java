package com.alphaseries;

import com.alphaseries.game.advertising.VisitRoomAds;
import com.alphaseries.game.achievement.AchievementSettings;
import com.alphaseries.game.catalog.CatalogPages;
import com.alphaseries.game.catalog.CatalogProductSettings;
import com.alphaseries.game.catalog.CatalogRegistry;
import com.alphaseries.game.catalog.GiftSettings;
import com.alphaseries.game.chat.ChatSettings;
import com.alphaseries.game.help.HelpCenterCache;
import com.alphaseries.game.messenger.MessengerSettings;
import com.alphaseries.game.moderation.StaffSettings;
import com.alphaseries.game.navigator.NewFriendRooms;
import com.alphaseries.game.navigator.RecommendedRooms;
import com.alphaseries.game.navigator.RoomCategoryCache;
import com.alphaseries.game.pet.PetSettings;
import com.alphaseries.game.pet.RepresentedBotRegistry;
import com.alphaseries.game.quest.QuestSettings;
import com.alphaseries.game.recycler.RecyclerSettings;
import com.alphaseries.game.room.FurnitureRoomCache;
import com.alphaseries.game.room.RepresentedRoomCache;
import com.alphaseries.game.room.RepresentedRoomSlots;
import com.alphaseries.game.room.RoomPortalSettings;
import com.alphaseries.game.session.GameServerSessionState;
import com.alphaseries.game.session.RepresentedSocketCache;
import com.alphaseries.game.session.SessionRegistry;
import com.alphaseries.game.session.SocketMarkerSet;
import com.alphaseries.server.lifecycle.LicenceRuntimeState;
import com.alphaseries.server.update.UpdaterSettings;
import com.alphaseries.game.wired.WiredSettings;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

public final class Licence {
    public static Object global_008292BC = "";
    public static Object global_008292C0 = "";
    public static long global_008292C8 = 0L;
    public static Object global_008292CC = "";
    public static Object global_008292D0 = "";
    public static String global_008292D4 = "";
    public static Object global_008292D8 = "";
    public static String global_008291E4 = "";
    public static Object global_008291E8 = "";
    public static Object global_0082927C = "";
    public static String global_0082912C = "";
    public static Object global_0082911C = "";
    public static long global_00829128 = 0L;
    public static Object global_00829140 = "";
    public static Object global_0082915C = "";
    public static long global_00829168 = 0L;
    public static String global_00829178 = "";
    public static String global_0082917C = "";
    public static String global_00829258 = "";
    public static long global_0082916C = 0L;
    public static String global_008291EC = "";
    public static String global_008291F8 = "";
    public static String global_008291FC = "";
    public static String global_00829078 = "";
    public static String global_0082907C = "";
    public static String global_00829084 = "";
    public static String global_00829094 = "";
    public static String global_008290A0 = "";
    public static long global_008290A4 = 0L;
    public static long global_008290A8 = 0L;
    public static String global_0082925C = "";
    public static String global_00829260 = "";
    public static String global_00829268 = "";
    public static String global_00829204 = "";
    public static String global_00829208 = "";
    public static Object global_0082920C = "";
    public static Object global_00829210 = "";
    public static String global_00829290 = "";
    public static String global_00829294 = "";
    public static Object global_00829224 = "";
    public static String global_00829230 = "";
    public static Object global_00829244 = "";
    public static Object global_008292F4 = "";
    public static Object global_00829308 = "";
    public static Object global_008291D4 = "";
    public static long global_008291D8 = 0L;
    public static long global_0082919C = 0L;
    public static String global_008291A0 = "";
    public static boolean global_00829190 = false;
    public static long global_0082904C = 0L;
    public static String global_00829038 = "";
    public static long global_0082903C = 0L;
    public static boolean global_00829034 = false;
    public static long global_008290AC = 0L;
    public static String global_00829040 = "";
    public static String global_00829044 = "";
    public static String global_00829048 = "";
    public static String global_00829080 = "";
    public static String global_0082908C = "";
    public static LocalDateTime global_00829090 = null;
    public static String global_00829098 = "";
    public static String global_0082909C = "";
    public static Object global_0082934C = "";
    public static String global_00829310 = "";
    public static String global_0082930C = "";
    public static String global_00829350 = "";
    public static String global_00829354 = "";
    public static String global_00829358 = "";

    private Licence() {
    }

    public static long Proc_9_0_806F70(Object... args) {
        if (args == null || args.length < 2) {
            return 0L;
        }
        return NumberUtils.parseLong(catalogRegistry().productCell(NumberUtils.parseLong(args[0]), NumberUtils.parseLong(args[1])));
    }

    public static String Proc_9_1_8072B0(Object... args) {
        if (args == null || args.length < 2) {
            return "";
        }
        return catalogRegistry().catalogProductCell(NumberUtils.parseLong(args[0]), NumberUtils.parseLong(args[1]));
    }

    public static long Proc_9_2_8075F0(Object... args) {
        if (args == null || args.length < 2) {
            return 0L;
        }
        return NumberUtils.parseLong(catalogRegistry().catalogProductCell(NumberUtils.parseLong(args[0]), NumberUtils.parseLong(args[1])));
    }

    public static String Proc_9_3_807930(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        return catalogRegistry().productRow(NumberUtils.parseLong(args[0]));
    }

    public static String Proc_9_4_807B90(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        return catalogRegistry().catalogProductRow(NumberUtils.parseLong(args[0]));
    }

    public static CatalogRegistry.CatalogProduct catalogProduct(long catalogProductId) {
        return catalogRegistry().catalogProduct(catalogProductId).orElse(null);
    }

    public static String Proc_9_5_807DF0(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        return catalogRegistry().dealRow(NumberUtils.parseLong(args[0]));
    }

    public static GiftSettings giftSettings() {
        return GiftSettings.fromLegacy(global_00829178, global_0082917C, global_0082925C, global_00829260);
    }

    public static void setClubGiftState(String payload, String lookup) {
        global_00829178 = StringUtils.text(payload);
        global_0082917C = StringUtils.text(lookup);
    }

    public static void setGiftWrapState(String lookup, String payload) {
        global_0082925C = StringUtils.text(lookup);
        global_00829260 = StringUtils.text(payload);
    }

    public static HelpCenterCache helpCenterCache() {
        return HelpCenterCache.fromLegacy(global_00829204, global_00829208, global_0082920C, global_00829210);
    }

    public static void setImportantFaqPayload(String payload) {
        global_00829204 = StringUtils.text(payload);
    }

    public static void setFaqCategoryCache(String categoryPayload, Object categoryFaqs) {
        global_00829208 = StringUtils.text(categoryPayload);
        global_0082920C = categoryFaqs == null ? "" : categoryFaqs;
    }

    public static void setFaqDescriptionCache(Object descriptions) {
        global_00829210 = descriptions == null ? "" : descriptions;
    }

    public static ChatSettings chatSettings() {
        return ChatSettings.fromLegacy(global_00829290, global_00829294);
    }

    public static void setChatSettings(String filterRows, String gestureRows) {
        global_00829290 = StringUtils.text(filterRows);
        global_00829294 = StringUtils.text(gestureRows);
    }

    public static VisitRoomAds visitRoomAds() {
        return VisitRoomAds.fromLegacy(global_008291D4, global_008291D8);
    }

    public static void setVisitRoomAds(Object payloadsById, long count) {
        global_008291D4 = payloadsById == null ? "" : payloadsById;
        global_008291D8 = count;
    }

    public static MessengerSettings messengerSettings() {
        return MessengerSettings.fromLegacy(global_0082927C);
    }

    public static void setMessengerFriendLimits(Object friendLimits) {
        global_0082927C = friendLimits == null ? "" : friendLimits;
    }

    public static AchievementSettings achievementSettings() {
        return AchievementSettings.fromLegacy(global_008291E4, global_008291E8);
    }

    public static void setAchievementSettings(String questIdPayload, Object rows) {
        global_008291E4 = StringUtils.text(questIdPayload);
        global_008291E8 = rows == null ? "" : rows;
    }

    public static PetSettings petSettings() {
        return PetSettings.fromLegacy(global_008291EC, global_008292D0, global_008292CC, global_008292C8);
    }

    public static void setPetRaceRows(String raceRows) {
        global_008291EC = StringUtils.text(raceRows);
    }

    public static void setPetLevelRows(Object levelRows) {
        global_008292D0 = levelRows == null ? "" : levelRows;
    }

    public static void setPetCommandRows(Object commandRows, long commandCount) {
        global_008292CC = commandRows == null ? "" : commandRows;
        global_008292C8 = commandCount;
    }

    public static RepresentedBotRegistry representedBots() {
        return RepresentedBotRegistry.fromLegacy(global_008292D4, global_00829358);
    }

    public static void setRepresentedBots(RepresentedBotRegistry representedBots) {
        if (representedBots == null) {
            global_008292D4 = "";
            global_00829358 = "";
            return;
        }
        global_008292D4 = representedBots.allocatedEntityMarkers();
        global_00829358 = representedBots.recordCache();
    }

    public static RepresentedRoomSlots representedRoomSlots() {
        return RepresentedRoomSlots.fromLegacy(global_0082930C);
    }

    public static void setRepresentedRoomSlots(RepresentedRoomSlots representedRoomSlots) {
        global_0082930C = representedRoomSlots == null ? "" : representedRoomSlots.availableSlotMarkers();
    }

    public static RepresentedRoomCache representedRooms() {
        return RepresentedRoomCache.fromLegacy(global_00829310);
    }

    public static void setRepresentedRooms(RepresentedRoomCache representedRooms) {
        global_00829310 = representedRooms == null ? "" : representedRooms.cacheText();
    }

    public static FurnitureRoomCache.State furnitureRoomCache() {
        return FurnitureRoomCache.State.from(global_008291F8, global_008291FC, global_00829310);
    }

    public static void setFurnitureRoomCache(FurnitureRoomCache.State state) {
        if (state == null) {
            global_008291F8 = "";
            global_008291FC = "";
            global_00829310 = "";
            return;
        }
        global_008291F8 = StringUtils.text(state.pendingRoomCache);
        global_008291FC = StringUtils.text(state.pendingFurnitureCache);
        global_00829310 = StringUtils.text(state.representedRoomCache);
    }

    public static RoomPortalSettings roomPortalSettings() {
        return RoomPortalSettings.fromLegacy(global_00829098, global_0082909C);
    }

    public static void setRoomPortalSettings(String warpSpaceRows, String specialGateRows) {
        global_00829098 = StringUtils.text(warpSpaceRows);
        global_0082909C = StringUtils.text(specialGateRows);
    }

    public static GameServerSessionState gameServerSessionState() {
        return GameServerSessionState.fromLegacy(global_00829350, global_00829354);
    }

    public static void setGameServerSessionState(GameServerSessionState sessionState) {
        if (sessionState == null) {
            global_00829350 = "";
            global_00829354 = "";
            return;
        }
        global_00829350 = sessionState.queuedPacketData();
        global_00829354 = sessionState.readySessionMarkers();
    }

    public static RepresentedSocketCache representedSockets() {
        return RepresentedSocketCache.fromLegacy(global_0082934C);
    }

    public static SocketMarkerSet socketMarkers() {
        return SocketMarkerSet.fromLegacy(global_008291A0);
    }

    public static void setSocketMarkers(SocketMarkerSet socketMarkers) {
        global_008291A0 = socketMarkers == null ? "" : socketMarkers.toLegacyMarkers();
    }

    public static LicenceRuntimeState runtimeState() {
        return LicenceRuntimeState.fromLegacy(global_0082904C, global_00829038, global_0082903C,
            global_00829034, global_008290AC, global_00829190);
    }

    public static void setRuntimeState(LicenceRuntimeState runtimeState) {
        if (runtimeState == null) {
            global_0082904C = 0L;
            global_00829038 = "";
            global_0082903C = 0L;
            global_00829034 = false;
            global_008290AC = 0L;
            global_00829190 = false;
            return;
        }
        global_0082904C = runtimeState.primaryColor();
        global_00829038 = runtimeState.productName();
        global_0082903C = runtimeState.version();
        global_00829034 = runtimeState.debugLoggingEnabled();
        global_008290AC = runtimeState.secondaryColor();
        global_00829190 = runtimeState.packetTraceEnabled();
    }

    public static void resetRuntimeDefaults() {
        setRuntimeState(LicenceRuntimeState.defaults(global_00829190));
    }

    public static UpdaterSettings updaterSettings() {
        return UpdaterSettings.fromLegacy(global_00829040, global_00829044, global_00829048);
    }

    public static void setUpdaterExecutableName(String executableName) {
        global_00829040 = StringUtils.text(executableName);
    }

    public static void setUpdaterRows(String updateRows) {
        global_00829044 = StringUtils.text(updateRows);
    }

    public static void setUpdaterSql(String updateSql) {
        global_00829048 = StringUtils.text(updateSql);
    }

    public static CatalogPages catalogPages() {
        return CatalogPages.fromLegacy(global_00829308, global_008292F4);
    }

    public static void setCatalogPagePayloads(Object pagePayloads) {
        global_00829308 = pagePayloads == null ? "" : pagePayloads;
    }

    public static void setCatalogPageTrees(Object pageTrees) {
        global_008292F4 = pageTrees == null ? "" : pageTrees;
    }

    public static RoomCategoryCache roomCategoryCache() {
        return RoomCategoryCache.fromLegacy(global_00829224, global_00829230, global_00829244);
    }

    public static void setRoomCategoryDefaults(Object defaultCategoryIds) {
        global_00829224 = defaultCategoryIds == null ? "" : defaultCategoryIds;
    }

    public static void setRoomCategoryRows(String categoryRows) {
        global_00829230 = StringUtils.text(categoryRows);
    }

    public static void setRoomCategoryPayloads(Object payloads) {
        global_00829244 = payloads == null ? "" : payloads;
    }

    public static QuestSettings questSettings() {
        return QuestSettings.fromLegacy(global_00829080);
    }

    public static void setQuestRows(String questRows) {
        global_00829080 = StringUtils.text(questRows);
    }

    public static WiredSettings wiredSettings() {
        return WiredSettings.fromLegacy(global_00829094);
    }

    public static void setWiredStatePayload(String statePayload) {
        global_00829094 = StringUtils.text(statePayload);
    }

    public static NewFriendRooms newFriendRooms() {
        return NewFriendRooms.fromLegacy(global_0082908C, global_00829090);
    }

    public static void setNewFriendRooms(String rows, LocalDateTime expiresAt) {
        global_0082908C = StringUtils.text(rows);
        global_00829090 = expiresAt;
    }

    public static void setNewFriendRooms(List<NewFriendRooms.RoomPick> rooms, LocalDateTime expiresAt) {
        setNewFriendRooms(NewFriendRooms.fromRoomPicks(rooms, expiresAt).toLegacyRows(), expiresAt);
    }

    public static StaffSettings staffSettings() {
        return StaffSettings.fromLegacy(global_008292D8);
    }

    public static void setStaffModerationPayloads(Object moderationPayloads) {
        global_008292D8 = moderationPayloads == null ? "" : moderationPayloads;
    }

    public static String Proc_9_6_808080(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        return getSessionRecordField("0:", StringUtils.text(args[0]), optionalColumnIndex(args, 1, 0));
    }

    public static long Proc_9_7_808320(Object... args) {
        if (args == null || args.length == 0) {
            return 0L;
        }
        return sessionRegistry().recordLong("1:", StringUtils.text(args[0]), optionalColumnIndex(args, 1, 1));
    }

    public static long Proc_9_8_8086A0(Object... args) {
        if (args == null || args.length == 0) {
            return 0L;
        }
        return sessionRegistry().linkedLong(StringUtils.text(args[0]), true);
    }

    public static long Proc_9_9_808AC0(Object... args) {
        if (args == null || args.length == 0) {
            return 0L;
        }
        return sessionRegistry().linkedLong(StringUtils.text(args[0]), false);
    }

    public static long Proc_9_10_808F30(Object... args) {
        if (args == null || args.length == 0) {
            return 0L;
        }
        return sessionRegistry().cacheLong(StringUtils.text(args[0]), optionalColumnIndex(args, 1, 0));
    }

    public static long Proc_9_11_809220(Object... args) {
        return Proc_9_10_808F30(args);
    }

    public static int optionalColumnIndex(Object[] args, int argumentIndex, int defaultValue) {
        if (args != null && argumentIndex >= 0 && argumentIndex < args.length && !StringUtils.text(args[argumentIndex]).isEmpty()) {
            return NumberUtils.parseInt(args[argumentIndex]);
        }
        return defaultValue;
    }

    public static String getSessionCacheField(String keyName, long columnIndex) {
        return sessionRegistry().cacheField(keyName, columnIndex);
    }

    public static String getSessionRecordField(String recordPrefix, String recordId, long columnIndex) {
        return sessionRegistry().recordField(recordPrefix, recordId, columnIndex);
    }

    public static String getSessionRecordPayload(String recordPrefix, String recordId) {
        return sessionRegistry().recordPayload(recordPrefix, recordId);
    }

    public static String getSessionLinkedValue(String recordId, boolean useBracketCount) {
        return sessionRegistry().linkedValue(recordId, useBracketCount);
    }

    public static void storeSocketSession(int socketIndex, String sessionRecord) {
        SessionRegistry registry = sessionRegistry();
        registry.storeSocketSession(socketIndex, sessionRecord);
        global_00829268 = registry.toLegacyCache();
    }

    public static List<SessionRegistry.SocketSession> socketSessions() {
        return sessionRegistry().socketSessions();
    }

    private static SessionRegistry sessionRegistry() {
        return SessionRegistry.fromLegacyCache(global_00829268);
    }

    public static void setProductRows(Object productRows) {
        global_008292BC = productRows == null ? "" : productRows;
    }

    public static void setCatalogProductRows(Object catalogProductRows) {
        global_008292C0 = catalogProductRows == null ? "" : catalogProductRows;
    }

    public static void setDealRows(String dealRows) {
        global_00829258 = StringUtils.text(dealRows);
    }

    public static CatalogProductSettings catalogProductSettings() {
        return CatalogProductSettings.fromLegacy(global_008290A0, global_008290A4, global_008290A8,
            global_00829078, global_0082907C, global_00829084);
    }

    public static void setCounterProductIds(String counterProductIds) {
        global_008290A0 = StringUtils.text(counterProductIds);
    }

    public static void setTeleportProductId(long productId) {
        global_008290A4 = Math.max(0L, productId);
    }

    public static void setMoodlightProductId(long productId) {
        global_008290A8 = Math.max(0L, productId);
    }

    public static void setPackageRows(String packageRows) {
        global_00829078 = StringUtils.text(packageRows);
    }

    public static void setPetPackageRows(String petPackageRows) {
        global_0082907C = StringUtils.text(petPackageRows);
    }

    public static void setClubProductRows(String clubProductRows) {
        global_00829084 = StringUtils.text(clubProductRows);
    }

    public static void setRecyclerRewards(Object productLists, Object chances, long groupCount) {
        global_00829140 = productLists == null ? "" : productLists;
        global_0082915C = chances == null ? "" : chances;
        global_00829168 = Math.max(0L, groupCount);
    }

    public static void setRecyclerStatusPayload(String statusPayload) {
        global_0082912C = StringUtils.text(statusPayload);
    }

    public static void setRecyclerBoxProductId(long boxProductId) {
        global_0082916C = Math.max(0L, boxProductId);
    }

    public static RecyclerSettings recyclerSettings() {
        return RecyclerSettings.fromLegacy(global_0082912C, global_00829140, global_0082915C, global_00829168, global_0082916C);
    }

    public static void setRecommendedRooms(Object payloads, long count) {
        global_0082911C = payloads == null ? "" : payloads;
        global_00829128 = Math.max(0L, count);
    }

    public static RecommendedRooms recommendedRooms() {
        return RecommendedRooms.fromLegacy(global_0082911C, global_00829128);
    }

    private static CatalogRegistry catalogRegistry() {
        return CatalogRegistry.fromLegacyCaches(global_008292BC, global_008292C0, global_00829258);
    }

    public static String getTableCell(Object tableCache, long rowId, long columnIndex) {
        String rowValue = getTableRow(tableCache, rowId);
        if (rowValue.isEmpty()) {
            return "";
        }
        String[] columns = rowValue.split("\t", -1);
        if (columnIndex < 0 || columnIndex >= columns.length) {
            return "";
        }
        return columns[(int) columnIndex];
    }

    public static String getTableRow(Object tableCache, long rowId) {
        if (rowId < 0) {
            return "";
        }
        if (tableCache instanceof String[]) {
            String[] rows = (String[]) tableCache;
            return rowId < rows.length ? StringUtils.text(rows[(int) rowId]) : "";
        }
        return getDelimitedRow(StringUtils.text(tableCache), rowId);
    }

    public static String getDelimitedRow(String tableText, long rowId) {
        if (tableText == null || tableText.isEmpty()) {
            return "";
        }
        String[] rows = ("\r" + tableText + "\r").split("\r", -1);
        for (String rowText : rows) {
            if (!rowText.isEmpty()) {
                String[] columns = rowText.split("\t", -1);
                if (columns.length > 0 && NumberUtils.parseLong(columns[0]) == rowId) {
                    return rowText;
                }
            }
        }
        return "";
    }
}
