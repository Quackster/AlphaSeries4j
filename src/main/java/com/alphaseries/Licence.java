package com.alphaseries;

import com.alphaseries.dao.mysql.CatalogDao;
import com.alphaseries.game.advertising.VisitRoomAds;
import com.alphaseries.game.advertising.AdvertisingState;
import com.alphaseries.game.achievement.AchievementSettings;
import com.alphaseries.game.achievement.AchievementState;
import com.alphaseries.game.catalog.CatalogPages;
import com.alphaseries.game.catalog.CatalogProductSettings;
import com.alphaseries.game.catalog.CatalogRegistry;
import com.alphaseries.game.catalog.CatalogState;
import com.alphaseries.game.catalog.GiftSettings;
import com.alphaseries.game.chat.ChatSettings;
import com.alphaseries.game.chat.ChatState;
import com.alphaseries.game.help.HelpCenterCache;
import com.alphaseries.game.help.HelpCenterState;
import com.alphaseries.game.messenger.MessengerSettings;
import com.alphaseries.game.messenger.MessengerState;
import com.alphaseries.game.moderation.ModerationState;
import com.alphaseries.game.moderation.StaffSettings;
import com.alphaseries.game.navigator.NewFriendRooms;
import com.alphaseries.game.navigator.NavigatorState;
import com.alphaseries.game.navigator.RecommendedRooms;
import com.alphaseries.game.navigator.RoomCategoryCache;
import com.alphaseries.game.pet.PetSettings;
import com.alphaseries.game.pet.PetState;
import com.alphaseries.game.pet.RepresentedBotRegistry;
import com.alphaseries.game.quest.QuestSettings;
import com.alphaseries.game.quest.QuestState;
import com.alphaseries.game.recycler.RecyclerSettings;
import com.alphaseries.game.recycler.RecyclerState;
import com.alphaseries.game.room.FurnitureRoomCache;
import com.alphaseries.game.room.RepresentedRoomCache;
import com.alphaseries.game.room.RepresentedRoomSlots;
import com.alphaseries.game.room.RoomPortalSettings;
import com.alphaseries.game.room.RoomState;
import com.alphaseries.game.session.GameServerSessionState;
import com.alphaseries.game.session.RepresentedSocketCache;
import com.alphaseries.game.session.SessionRegistry;
import com.alphaseries.game.session.SessionState;
import com.alphaseries.game.session.SocketMarkerSet;
import com.alphaseries.server.lifecycle.LifecycleState;
import com.alphaseries.server.lifecycle.LicenceRuntimeState;
import com.alphaseries.server.update.UpdaterSettings;
import com.alphaseries.server.update.UpdaterState;
import com.alphaseries.game.wired.WiredSettings;
import com.alphaseries.game.wired.WiredState;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class Licence {
    public static Object global_008292BC = "";
    public static Object global_008292C0 = "";
    public static long global_008292C8 = 0L;
    public static Object global_008292CC = "";
    public static Object global_008292D0 = "";
    public static Object global_008292D4 = "";
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
    public static Object global_00829178 = "";
    public static Object global_0082917C = "";
    public static Object global_00829258 = "";
    public static long global_0082916C = 0L;
    public static Object global_008291EC = "";
    public static String global_008291F8 = "";
    public static String global_008291FC = "";
    public static Object global_00829078 = "";
    public static Object global_0082907C = "";
    public static Object global_00829084 = "";
    public static String global_00829094 = "";
    public static Object global_008290A0 = "";
    public static long global_008290A4 = 0L;
    public static long global_008290A8 = 0L;
    public static Object global_0082925C = "";
    public static String global_00829260 = "";
    public static String global_00829268 = "";
    public static String global_00829204 = "";
    public static String global_00829208 = "";
    public static Object global_0082920C = "";
    public static Object global_00829210 = "";
    public static Object global_00829290 = "";
    public static Object global_00829294 = "";
    public static Object global_00829224 = "";
    public static Object global_00829230 = "";
    public static Object global_00829244 = "";
    public static Object global_008292F4 = "";
    public static Object global_00829308 = "";
    public static Object global_008291D4 = "";
    public static long global_008291D8 = 0L;
    public static long global_0082919C = 0L;
    public static Object global_008291A0 = "";
    public static boolean global_00829190 = false;
    public static long global_0082904C = 0L;
    public static String global_00829038 = "";
    public static long global_0082903C = 0L;
    public static boolean global_00829034 = false;
    public static long global_008290AC = 0L;
    public static String global_00829040 = "";
    public static String global_00829044 = "";
    public static String global_00829048 = "";
    public static Object global_00829080 = "";
    public static Object global_0082908C = "";
    public static LocalDateTime global_00829090 = null;
    public static Object global_00829098 = "";
    public static Object global_0082909C = "";
    public static Object global_0082934C = "";
    public static Object global_00829310 = "";
    public static Object global_0082930C = "";
    public static String global_00829350 = "";
    public static Object global_00829354 = "";
    public static Object global_00829358 = "";

    private Licence() {
    }

    public static long Proc_9_0_806F70(Object... args) {
        if (args == null || args.length < 2) {
            return 0L;
        }
        return productFieldLong(NumberUtils.parseLong(args[0]), NumberUtils.parseLong(args[1]));
    }

    public static String Proc_9_1_8072B0(Object... args) {
        if (args == null || args.length < 2) {
            return "";
        }
        return catalogProductField(NumberUtils.parseLong(args[0]), NumberUtils.parseLong(args[1]));
    }

    public static long Proc_9_2_8075F0(Object... args) {
        if (args == null || args.length < 2) {
            return 0L;
        }
        return catalogProductFieldLong(NumberUtils.parseLong(args[0]), NumberUtils.parseLong(args[1]));
    }

    public static String Proc_9_3_807930(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        return catalogRegistry().productRow(NumberUtils.parseLong(args[0]));
    }

    public static CatalogRegistry.Product product(long productId) {
        return catalogRegistry().product(productId).orElse(null);
    }

    /**
     * Original function: Proc_9_0_806F70.
     */
    public static long productFieldLong(long productId, long columnIndex) {
        return NumberUtils.parseLong(catalogRegistry().productCell(productId, columnIndex));
    }

    /**
     * Original function: Proc_9_0_806F70.
     */
    public static long productType(long productId) {
        CatalogRegistry.Product product = product(productId);
        return product == null ? 0L : product.type();
    }

    /**
     * Original function: Proc_9_0_806F70.
     */
    public static long productStateCount(long productId) {
        return productFieldLong(productId, 5);
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

    /**
     * Original function: Proc_9_1_8072B0.
     */
    public static String catalogProductField(long catalogProductId, long columnIndex) {
        return catalogRegistry().catalogProductCell(catalogProductId, columnIndex);
    }

    /**
     * Original function: Proc_9_2_8075F0.
     */
    public static long catalogProductFieldLong(long catalogProductId, long columnIndex) {
        return NumberUtils.parseLong(catalogProductField(catalogProductId, columnIndex));
    }

    public static String Proc_9_5_807DF0(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        return catalogRegistry().dealRow(NumberUtils.parseLong(args[0]));
    }

    public static CatalogRegistry.ProductDeal productDeal(long productId) {
        return catalogRegistry().productDeal(productId).orElse(null);
    }

    public static GiftSettings giftSettings() {
        CatalogState.instance().setGiftSettingsFromLegacy(global_00829178, global_0082917C,
            global_0082925C, global_00829260);
        return CatalogState.instance().giftSettings();
    }

    public static void setClubGiftState(String payload, String lookup) {
        global_00829178 = StringUtils.text(payload);
        global_0082917C = StringUtils.text(lookup);
        CatalogState.instance().setGiftSettingsFromLegacy(global_00829178, global_0082917C,
            global_0082925C, global_00829260);
    }

    public static void setClubGiftState(GiftSettings.ClubGiftState state) {
        global_00829178 = state == null ? "" : state;
        global_0082917C = "";
        CatalogState.instance().setGiftSettingsFromLegacy(global_00829178, global_0082917C,
            global_0082925C, global_00829260);
    }

    public static void setGiftWrapState(String lookup, String payload) {
        global_0082925C = StringUtils.text(lookup);
        global_00829260 = StringUtils.text(payload);
        CatalogState.instance().setGiftSettingsFromLegacy(global_00829178, global_0082917C,
            global_0082925C, global_00829260);
    }

    public static void setGiftWrapState(List<Long> productIds, String payload) {
        GiftSettings currentSettings = giftSettings();
        List<Long> typedProductIds = copyGiftWrapProductIds(productIds);
        global_0082925C = typedProductIds;
        global_00829260 = StringUtils.text(payload);
        CatalogState.instance().setGiftSettings(GiftSettings.fromRows(
            currentSettings.clubGiftPayload(),
            currentSettings.clubGifts(),
            typedProductIds,
            global_00829260));
    }

    private static List<Long> copyGiftWrapProductIds(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return List.of();
        }
        List<Long> copiedProductIds = new ArrayList<>(productIds.size());
        for (Long productId : productIds) {
            copiedProductIds.add(NumberUtils.parseLong(productId));
        }
        return List.copyOf(copiedProductIds);
    }

    public static HelpCenterCache helpCenterCache() {
        HelpCenterState.instance().setCacheFromLegacy(global_00829204, global_00829208, global_0082920C, global_00829210);
        return HelpCenterState.instance().cache();
    }

    public static void setImportantFaqPayload(String payload) {
        global_00829204 = StringUtils.text(payload);
        HelpCenterState.instance().setCacheFromLegacy(global_00829204, global_00829208, global_0082920C, global_00829210);
    }

    public static void setFaqCategoryCache(String categoryPayload, Object categoryFaqs) {
        global_00829208 = StringUtils.text(categoryPayload);
        global_0082920C = categoryFaqs == null ? "" : categoryFaqs;
        HelpCenterState.instance().setCacheFromLegacy(global_00829204, global_00829208, global_0082920C, global_00829210);
    }

    public static void setFaqDescriptionCache(Object descriptions) {
        global_00829210 = descriptions == null ? "" : descriptions;
        HelpCenterState.instance().setCacheFromLegacy(global_00829204, global_00829208, global_0082920C, global_00829210);
    }

    public static ChatSettings chatSettings() {
        refreshChatSettings();
        return ChatState.instance().settings();
    }

    public static void setChatSettings(List<ChatSettings.FilterWord> filterRows, List<ChatSettings.Gesture> gestureRows) {
        global_00829290 = filterRows == null ? List.of() : List.copyOf(filterRows);
        global_00829294 = gestureRows == null ? List.of() : List.copyOf(gestureRows);
        refreshChatSettings();
    }

    private static void refreshChatSettings() {
        List<ChatSettings.FilterWord> filterRows = typedRows(global_00829290, ChatSettings.FilterWord.class);
        List<ChatSettings.Gesture> gestureRows = typedRows(global_00829294, ChatSettings.Gesture.class);
        if (filterRows != null && gestureRows != null) {
            ChatState.instance().setRows(filterRows, gestureRows);
            return;
        }
        ChatState.instance().setSettingsFromLegacy(global_00829290, global_00829294);
    }

    public static List<ChatSettings.FilterWord> chatFilterWords() {
        return ChatSettings.filterWordsFromLegacy(global_00829290);
    }

    public static List<ChatSettings.Gesture> chatGestures() {
        return ChatSettings.gesturesFromLegacy(global_00829294);
    }

    public static VisitRoomAds visitRoomAds() {
        refreshVisitRoomAds();
        return AdvertisingState.instance().visitRoomAds();
    }

    public static void setVisitRoomAds(Object payloadsById, long count) {
        global_008291D4 = payloadsById == null ? "" : payloadsById;
        global_008291D8 = count;
        refreshVisitRoomAds();
    }

    private static void refreshVisitRoomAds() {
        if (global_008291D4 instanceof VisitRoomAds ads) {
            AdvertisingState.instance().setVisitRoomAds(ads);
            return;
        }
        AdvertisingState.instance().setVisitRoomAdsFromLegacy(global_008291D4, global_008291D8);
    }

    public static MessengerSettings messengerSettings() {
        refreshMessengerSettings();
        return MessengerState.instance().settings();
    }

    public static void setMessengerFriendLimits(Object friendLimits) {
        global_0082927C = friendLimits == null ? "" : friendLimits;
        refreshMessengerSettings();
    }

    private static void refreshMessengerSettings() {
        if (global_0082927C instanceof MessengerSettings settings) {
            MessengerState.instance().setFriendLimits(settings);
            return;
        }
        MessengerState.instance().setFriendLimits(global_0082927C);
    }

    public static AchievementSettings achievementSettings() {
        AchievementState.instance().setSettingsFromLegacy(global_008291E4, global_008291E8);
        return AchievementState.instance().settings();
    }

    public static void setAchievementSettings(String questIdPayload, Object rows) {
        AchievementState.instance().setSettingsFromLegacy(questIdPayload, rows);
        global_008291E4 = StringUtils.text(questIdPayload);
        global_008291E8 = rows == null ? "" : rows;
    }

    public static PetSettings petSettings() {
        PetState.instance().setSettingsFromLegacy(global_008291EC, global_008292D0, global_008292CC, global_008292C8);
        return PetState.instance().settings();
    }

    public static void setPetRaceRows(Object raceRows) {
        global_008291EC = raceRows == null ? "" : raceRows;
        PetState.instance().setSettingsFromLegacy(global_008291EC, global_008292D0, global_008292CC, global_008292C8);
    }

    public static void setPetLevelRows(Object levelRows) {
        global_008292D0 = levelRows == null ? "" : levelRows;
        PetState.instance().setSettingsFromLegacy(global_008291EC, global_008292D0, global_008292CC, global_008292C8);
    }

    public static void setPetCommandRows(Object commandRows, long commandCount) {
        global_008292CC = commandRows == null ? "" : commandRows;
        global_008292C8 = commandCount;
        PetState.instance().setSettingsFromLegacy(global_008291EC, global_008292D0, global_008292CC, global_008292C8);
    }

    public static RepresentedBotRegistry representedBots() {
        PetState.instance().setRepresentedBotsFromLegacy(global_008292D4, global_00829358);
        return PetState.instance().representedBots();
    }

    public static void setRepresentedBots(RepresentedBotRegistry representedBots) {
        PetState.instance().setRepresentedBots(representedBots);
        if (representedBots == null) {
            global_008292D4 = "";
            global_00829358 = "";
            return;
        }
        global_008292D4 = representedBots;
        global_00829358 = representedBots;
    }

    public static RepresentedRoomSlots representedRoomSlots() {
        refreshRepresentedRoomSlots();
        return RoomState.instance().representedRoomSlots();
    }

    public static void setRepresentedRoomSlots(RepresentedRoomSlots representedRoomSlots) {
        global_0082930C = representedRoomSlots == null ? "" : representedRoomSlots;
        refreshRepresentedRoomSlots();
    }

    private static void refreshRepresentedRoomSlots() {
        if (global_0082930C instanceof RepresentedRoomSlots representedRoomSlots) {
            RoomState.instance().setRepresentedRoomSlots(representedRoomSlots);
            return;
        }
        RoomState.instance().setRepresentedRoomSlotsFromLegacy(global_0082930C);
    }

    public static RepresentedRoomCache representedRooms() {
        RoomState.instance().setRepresentedRoomsFromLegacy(global_00829310);
        return RoomState.instance().representedRooms();
    }

    public static void setRepresentedRooms(RepresentedRoomCache representedRooms) {
        if (representedRooms == null) {
            RoomState.instance().setRepresentedRooms(RepresentedRoomCache.empty());
            global_00829310 = "";
            return;
        }
        RepresentedRoomCache normalizedRooms = RepresentedRoomCache.fromLegacy(representedRooms.cacheText());
        RoomState.instance().setRepresentedRooms(normalizedRooms);
        global_00829310 = normalizedRooms;
    }

    public static FurnitureRoomCache.State furnitureRoomCache() {
        return FurnitureRoomCache.State.from(global_008291F8, global_008291FC, global_00829310);
    }

    public static void setFurnitureRoomCache(FurnitureRoomCache.State state) {
        if (state == null) {
            global_008291F8 = "";
            global_008291FC = "";
            global_00829310 = "";
            RoomState.instance().setRepresentedRooms(RepresentedRoomCache.empty());
            return;
        }
        global_008291F8 = StringUtils.text(state.pendingRoomCache);
        global_008291FC = StringUtils.text(state.pendingFurnitureCache);
        RoomState.instance().setRepresentedRoomsFromLegacy(state.representedRoomCache);
        global_00829310 = RoomState.instance().representedRooms();
    }

    public static RoomPortalSettings roomPortalSettings() {
        RoomState.instance().setPortalSettingsFromLegacy(global_00829098, global_0082909C);
        return RoomState.instance().portalSettings();
    }

    public static void setRoomPortalSettings(String warpSpaceRows, String specialGateRows) {
        RoomPortalSettings settings = RoomPortalSettings.fromLegacy(warpSpaceRows, specialGateRows);
        RoomState.instance().setPortalSettings(settings);
        global_00829098 = settings;
        global_0082909C = settings;
    }

    public static void setRoomPortalSettings(RoomPortalSettings settings) {
        RoomPortalSettings normalized = settings == null ? RoomPortalSettings.empty() : settings;
        RoomState.instance().setPortalSettings(normalized);
        global_00829098 = normalized;
        global_0082909C = normalized;
    }

    public static GameServerSessionState gameServerSessionState() {
        SessionState.instance().setGameServerSessionFromLegacy(global_00829350, global_00829354);
        return SessionState.instance().gameServerSession();
    }

    public static void setGameServerSessionState(GameServerSessionState sessionState) {
        SessionState.instance().setGameServerSession(sessionState);
        if (sessionState == null) {
            global_00829350 = "";
            global_00829354 = "";
            return;
        }
        global_00829350 = sessionState.queuedPacketData();
        global_00829354 = sessionState.readySocketIndexes();
    }

    public static RepresentedSocketCache representedSockets() {
        SessionState.instance().setRepresentedSocketsFromLegacy(global_0082934C);
        return SessionState.instance().representedSockets();
    }

    public static SocketMarkerSet socketMarkers() {
        SessionState.instance().setSocketMarkersFromLegacy(global_008291A0);
        return SessionState.instance().socketMarkers();
    }

    public static void setSocketMarkers(SocketMarkerSet socketMarkers) {
        SessionState.instance().setSocketMarkers(socketMarkers);
        global_008291A0 = socketMarkers == null ? "" : socketMarkers;
    }

    public static LicenceRuntimeState runtimeState() {
        LifecycleState.instance().setRuntimeStateFromLegacy(global_0082904C, global_00829038, global_0082903C,
            global_00829034, global_008290AC, global_00829190);
        return LifecycleState.instance().runtimeState();
    }

    public static void setRuntimeState(LicenceRuntimeState runtimeState) {
        LifecycleState.instance().setRuntimeState(runtimeState);
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
        UpdaterState.instance().setSettingsFromLegacy(global_00829040, global_00829044, global_00829048);
        return UpdaterState.instance().settings();
    }

    public static void setUpdaterExecutableName(String executableName) {
        global_00829040 = StringUtils.text(executableName);
        UpdaterState.instance().setSettingsFromLegacy(global_00829040, global_00829044, global_00829048);
    }

    public static void setUpdaterRows(String updateRows) {
        global_00829044 = StringUtils.text(updateRows);
        UpdaterState.instance().setSettingsFromLegacy(global_00829040, global_00829044, global_00829048);
    }

    public static void setUpdaterSql(String updateSql) {
        global_00829048 = StringUtils.text(updateSql);
        UpdaterState.instance().setSettingsFromLegacy(global_00829040, global_00829044, global_00829048);
    }

    public static CatalogPages catalogPages() {
        refreshCatalogPages();
        return CatalogState.instance().catalogPages();
    }

    public static void setCatalogPagePayloads(Object pagePayloads) {
        global_00829308 = pagePayloads == null ? "" : pagePayloads;
        refreshCatalogPages();
    }

    public static void setCatalogPageTrees(Object pageTrees) {
        global_008292F4 = pageTrees == null ? "" : pageTrees;
        refreshCatalogPages();
    }

    private static void refreshCatalogPages() {
        if (global_00829308 instanceof CatalogPages catalogPages) {
            CatalogState.instance().setCatalogPages(catalogPages);
            return;
        }
        CatalogState.instance().setCatalogPagesFromLegacy(global_00829308, global_008292F4);
    }

    public static RoomCategoryCache roomCategoryCache() {
        refreshRoomCategoryCache();
        return NavigatorState.instance().roomCategoryCache();
    }

    public static void setRoomCategoryDefaults(Object defaultCategoryIds) {
        global_00829224 = defaultCategoryIds == null ? "" : defaultCategoryIds;
        refreshRoomCategoryCache();
    }

    public static void setRoomCategoryRows(String categoryRows) {
        global_00829230 = StringUtils.text(categoryRows);
        refreshRoomCategoryCache();
    }

    public static void setRoomCategoryRows(Object categoryRows) {
        global_00829230 = categoryRows == null ? "" : categoryRows;
        refreshRoomCategoryCache();
    }

    public static void setRoomCategoryPayloads(Object payloads) {
        global_00829244 = payloads == null ? "" : payloads;
        refreshRoomCategoryCache();
    }

    private static void refreshRoomCategoryCache() {
        if (global_00829224 instanceof RoomCategoryCache cache) {
            NavigatorState.instance().setRoomCategoryCache(cache);
            return;
        }
        NavigatorState.instance().setRoomCategoryCacheFromLegacy(global_00829224, global_00829230, global_00829244);
    }

    public static QuestSettings questSettings() {
        refreshQuestSettings();
        return QuestState.instance().settings();
    }

    public static void setQuestRows(String questRows) {
        QuestSettings settings = QuestSettings.fromLegacy(questRows);
        QuestState.instance().setSettings(settings);
        global_00829080 = settings;
    }

    public static void setQuestDefinitions(List<QuestSettings.QuestDefinitionRow> questDefinitions) {
        QuestSettings settings = QuestSettings.fromDefinitions(questDefinitions);
        QuestState.instance().setSettings(settings);
        global_00829080 = settings;
    }

    private static void refreshQuestSettings() {
        if (global_00829080 instanceof QuestSettings settings) {
            QuestState.instance().setQuestSettings(settings);
            return;
        }
        QuestState.instance().setSettingsFromLegacy(global_00829080);
    }

    public static WiredSettings wiredSettings() {
        WiredState.instance().setStatePayload(global_00829094);
        return WiredState.instance().settings();
    }

    public static void setWiredStatePayload(String statePayload) {
        global_00829094 = StringUtils.text(statePayload);
        WiredState.instance().setStatePayload(global_00829094);
    }

    public static NewFriendRooms newFriendRooms() {
        NavigatorState.instance().setNewFriendRoomsFromLegacy(global_0082908C, global_00829090);
        return NavigatorState.instance().newFriendRooms();
    }

    public static void setNewFriendRooms(String rows, LocalDateTime expiresAt) {
        NavigatorState.instance().setNewFriendRoomsFromLegacy(rows, expiresAt);
        global_0082908C = NavigatorState.instance().newFriendRooms();
        global_00829090 = expiresAt;
    }

    public static void setNewFriendRooms(List<NewFriendRooms.RoomPick> rooms, LocalDateTime expiresAt) {
        NavigatorState.instance().setNewFriendRooms(rooms, expiresAt);
        global_0082908C = NavigatorState.instance().newFriendRooms();
        global_00829090 = expiresAt;
    }

    public static StaffSettings staffSettings() {
        ModerationState.instance().setStaffModerationPayloads(global_008292D8);
        return ModerationState.instance().staffSettings();
    }

    public static void setStaffModerationPayloads(Object moderationPayloads) {
        ModerationState.instance().setStaffModerationPayloads(moderationPayloads);
        global_008292D8 = moderationPayloads == null ? "" : moderationPayloads;
    }

    public static String Proc_9_6_808080(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        return socketUserId(StringUtils.text(args[0]));
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
        return linkedUserSocketIndex(StringUtils.text(args[0]));
    }

    public static long Proc_9_9_808AC0(Object... args) {
        if (args == null || args.length == 0) {
            return 0L;
        }
        return linkedSocketIndex(StringUtils.text(args[0]));
    }

    public static long Proc_9_10_808F30(Object... args) {
        if (args == null || args.length == 0) {
            return 0L;
        }
        return sessionCacheLong(StringUtils.text(args[0]), optionalColumnIndex(args, 1, 0));
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

    /**
     * Original function: Proc_9_6_808080.
     */
    public static String socketUserId(String socketIndex) {
        return getSessionRecordField("0:", socketIndex, 0);
    }

    /**
     * Original function: Proc_9_8_8086A0.
     */
    public static long linkedUserSocketIndex(String recordId) {
        return sessionRegistry().linkedLong(StringUtils.text(recordId), true);
    }

    /**
     * Original function: Proc_9_9_808AC0.
     */
    public static long linkedSocketIndex(String recordId) {
        return sessionRegistry().linkedLong(StringUtils.text(recordId), false);
    }

    /**
     * Original function: Proc_9_10_808F30.
     */
    public static long sessionCacheLong(String keyName, long columnIndex) {
        return sessionRegistry().cacheLong(StringUtils.text(keyName), columnIndex);
    }

    public static void storeSocketSession(int socketIndex, String sessionRecord) {
        SessionRegistry registry = sessionRegistry();
        registry.storeSocketSession(socketIndex, sessionRecord);
        SessionState.instance().setSessionRegistry(registry);
        global_00829268 = registry.toLegacyCache();
    }

    public static List<SessionRegistry.SocketSession> socketSessions() {
        return sessionRegistry().socketSessions();
    }

    private static SessionRegistry sessionRegistry() {
        SessionState.instance().setSessionRegistryFromLegacy(global_00829268);
        return SessionState.instance().sessionRegistry();
    }

    public static void setProductRows(Object productRows) {
        global_008292BC = productRows == null ? "" : productRows;
        refreshCatalogRegistry();
    }

    public static void setCatalogProductRows(Object catalogProductRows) {
        global_008292C0 = catalogProductRows == null ? "" : catalogProductRows;
        refreshCatalogRegistry();
    }

    public static void setDealRows(String dealRows) {
        global_00829258 = StringUtils.text(dealRows);
        refreshCatalogRegistry();
    }

    public static void setDealRows(Object dealRows) {
        global_00829258 = dealRows == null ? "" : dealRows;
        refreshCatalogRegistry();
    }

    public static CatalogProductSettings catalogProductSettings() {
        CatalogState.instance().setProductSettingsFromLegacy(global_008290A0, global_008290A4, global_008290A8,
            global_00829078, global_0082907C, global_00829084);
        return CatalogState.instance().productSettings();
    }

    public static void setCounterProductIds(Object counterProductIds) {
        global_008290A0 = counterProductIds == null ? "" : counterProductIds;
        CatalogState.instance().setProductSettingsFromLegacy(global_008290A0, global_008290A4, global_008290A8,
            global_00829078, global_0082907C, global_00829084);
    }

    public static void setTeleportProductId(long productId) {
        global_008290A4 = Math.max(0L, productId);
        CatalogState.instance().setProductSettingsFromLegacy(global_008290A0, global_008290A4, global_008290A8,
            global_00829078, global_0082907C, global_00829084);
    }

    public static void setMoodlightProductId(long productId) {
        global_008290A8 = Math.max(0L, productId);
        CatalogState.instance().setProductSettingsFromLegacy(global_008290A0, global_008290A4, global_008290A8,
            global_00829078, global_0082907C, global_00829084);
    }

    public static void setPackageRows(String packageRows) {
        global_00829078 = StringUtils.text(packageRows);
        CatalogState.instance().setProductSettingsFromLegacy(global_008290A0, global_008290A4, global_008290A8,
            global_00829078, global_0082907C, global_00829084);
    }

    public static void setPackageRows(Object packageRows) {
        global_00829078 = packageRows == null ? "" : packageRows;
        CatalogState.instance().setProductSettingsFromLegacy(global_008290A0, global_008290A4, global_008290A8,
            global_00829078, global_0082907C, global_00829084);
    }

    public static void setPetPackageRows(String petPackageRows) {
        global_0082907C = StringUtils.text(petPackageRows);
        CatalogState.instance().setProductSettingsFromLegacy(global_008290A0, global_008290A4, global_008290A8,
            global_00829078, global_0082907C, global_00829084);
    }

    public static void setPetPackageRows(Object petPackageRows) {
        global_0082907C = petPackageRows == null ? "" : petPackageRows;
        CatalogState.instance().setProductSettingsFromLegacy(global_008290A0, global_008290A4, global_008290A8,
            global_00829078, global_0082907C, global_00829084);
    }

    public static void setClubProductRows(String clubProductRows) {
        global_00829084 = StringUtils.text(clubProductRows);
        CatalogState.instance().setProductSettingsFromLegacy(global_008290A0, global_008290A4, global_008290A8,
            global_00829078, global_0082907C, global_00829084);
    }

    public static void setClubProductRows(Object clubProductRows) {
        global_00829084 = clubProductRows == null ? "" : clubProductRows;
        CatalogState.instance().setProductSettingsFromLegacy(global_008290A0, global_008290A4, global_008290A8,
            global_00829078, global_0082907C, global_00829084);
    }

    public static void setRecyclerRewards(Object productLists, Object chances, long groupCount) {
        global_00829140 = productLists == null ? "" : productLists;
        global_0082915C = chances == null ? "" : chances;
        global_00829168 = Math.max(0L, groupCount);
        RecyclerState.instance().setSettingsFromLegacy(global_0082912C, global_00829140, global_0082915C,
            global_00829168, global_0082916C);
    }

    public static void setRecyclerRewards(List<RecyclerSettings.RewardGroup> rewardGroups) {
        List<RecyclerSettings.RewardGroup> copiedGroups = rewardGroups == null ? List.of() : List.copyOf(rewardGroups);
        global_00829140 = RecyclerSettings.fromRewardGroups(global_0082912C, copiedGroups, global_0082916C);
        global_0082915C = recyclerChanceRows(copiedGroups);
        global_00829168 = copiedGroups.size();
        RecyclerState.instance().setSettings((RecyclerSettings) global_00829140);
    }

    public static void setRecyclerStatusPayload(String statusPayload) {
        global_0082912C = StringUtils.text(statusPayload);
        RecyclerState.instance().setSettingsFromLegacyRewardState(global_0082912C, global_00829140, global_0082915C,
            global_00829168, global_0082916C);
        global_00829140 = RecyclerSettings.compatibilityRewardSource(global_00829140, RecyclerState.instance().settings());
    }

    public static void setRecyclerBoxProductId(long boxProductId) {
        global_0082916C = Math.max(0L, boxProductId);
        RecyclerState.instance().setSettingsFromLegacyRewardState(global_0082912C, global_00829140, global_0082915C,
            global_00829168, global_0082916C);
        global_00829140 = RecyclerSettings.compatibilityRewardSource(global_00829140, RecyclerState.instance().settings());
    }

    public static RecyclerSettings recyclerSettings() {
        RecyclerState.instance().setSettingsFromLegacy(global_0082912C, global_00829140, global_0082915C,
            global_00829168, global_0082916C);
        return RecyclerState.instance().settings();
    }

    private static String[] recyclerChanceRows(List<RecyclerSettings.RewardGroup> rewardGroups) {
        String[] chanceRows = new String[50];
        int index = 0;
        for (RecyclerSettings.RewardGroup rewardGroup : rewardGroups == null
            ? List.<RecyclerSettings.RewardGroup>of()
            : rewardGroups) {
            if (index >= chanceRows.length) {
                break;
            }
            chanceRows[index] = String.valueOf(NumberUtils.parseLong(rewardGroup.chance()));
            index++;
        }
        return chanceRows;
    }

    public static void setRecommendedRooms(Object payloads, long count) {
        global_0082911C = payloads == null ? "" : payloads;
        global_00829128 = Math.max(0L, count);
        refreshRecommendedRooms();
        global_0082911C = NavigatorState.instance().recommendedRooms();
    }

    public static RecommendedRooms recommendedRooms() {
        refreshRecommendedRooms();
        return NavigatorState.instance().recommendedRooms();
    }

    private static void refreshRecommendedRooms() {
        if (global_0082911C instanceof RecommendedRooms rooms) {
            NavigatorState.instance().setRecommendedRooms(rooms);
            return;
        }
        NavigatorState.instance().setRecommendedRoomsFromLegacy(global_0082911C, global_00829128);
    }

    private static CatalogRegistry catalogRegistry() {
        refreshCatalogRegistry();
        return CatalogState.instance().registry();
    }

    private static void refreshCatalogRegistry() {
        List<CatalogDao.ProductCacheRow> products = typedRows(global_008292BC, CatalogDao.ProductCacheRow.class);
        List<CatalogDao.CatalogProductCacheRow> catalogProducts =
            typedRows(global_008292C0, CatalogDao.CatalogProductCacheRow.class);
        List<CatalogDao.ProductDealRow> deals = typedRows(global_00829258, CatalogDao.ProductDealRow.class);
        if (products != null && catalogProducts != null && deals != null) {
            CatalogState.instance().setRegistryFromRows(products, catalogProducts, deals);
            return;
        }
        CatalogState.instance().setRegistryFromLegacy(global_008292BC, global_008292C0, global_00829258);
    }

    private static <T> List<T> typedRows(Object rows, Class<T> rowType) {
        if (!(rows instanceof Iterable<?> values)) {
            return null;
        }
        List<T> typedRows = new ArrayList<>();
        for (Object value : values) {
            if (!rowType.isInstance(value)) {
                return null;
            }
            typedRows.add(rowType.cast(value));
        }
        return List.copyOf(typedRows);
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
