package com.alphaseries;

import com.alphaseries.config.AppDatabaseConfig;
import com.alphaseries.config.AppConfigState;
import com.alphaseries.config.AppPaths;
import com.alphaseries.config.AppSettingsBootCache;
import com.alphaseries.config.AppSettingsCache;
import com.alphaseries.config.FiguredataBootCache;
import com.alphaseries.config.PermissionMatrix;
import com.alphaseries.dao.mysql.AchievementDao;
import com.alphaseries.dao.mysql.AdvertisingDao;
import com.alphaseries.dao.mysql.BotDao;
import com.alphaseries.dao.mysql.CatalogDao;
import com.alphaseries.dao.mysql.ClubDao;
import com.alphaseries.dao.mysql.FurnitureDao;
import com.alphaseries.dao.mysql.HelpDao;
import com.alphaseries.dao.mysql.JukeboxDao;
import com.alphaseries.dao.mysql.MessengerDao;
import com.alphaseries.dao.mysql.PackageDao;
import com.alphaseries.dao.mysql.PollDao;
import com.alphaseries.dao.mysql.QuestDao;
import com.alphaseries.dao.mysql.RecyclerDao;
import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.dao.mysql.SettingsDao;
import com.alphaseries.dao.mysql.StaffModerationDao;
import com.alphaseries.dao.mysql.TradeDao;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.dao.mysql.VoucherDao;
import com.alphaseries.db.Database;
import com.alphaseries.db.MySQL;
import com.alphaseries.game.GameDataCaches;
import com.alphaseries.game.achievement.AchievementBootCache;
import com.alphaseries.game.achievement.AchievementLookups;
import com.alphaseries.game.achievement.AchievementProgress;
import com.alphaseries.game.achievement.AchievementProgressDecision;
import com.alphaseries.game.achievement.AchievementRewardGrant;
import com.alphaseries.game.achievement.AchievementSettings;
import com.alphaseries.game.achievement.AchievementState;
import com.alphaseries.game.advertising.AdvertisingBootCache;
import com.alphaseries.game.advertising.AdvertisingState;
import com.alphaseries.game.advertising.VisitRoomAds;
import com.alphaseries.game.catalog.CatalogPages;
import com.alphaseries.game.catalog.CatalogGiftBootCache;
import com.alphaseries.game.catalog.CatalogPageBootCache;
import com.alphaseries.game.catalog.CatalogProductSettings;
import com.alphaseries.game.catalog.CatalogRegistry;
import com.alphaseries.game.catalog.CatalogState;
import com.alphaseries.game.catalog.CatalogWire;
import com.alphaseries.game.catalog.ClubPeriodService;
import com.alphaseries.game.catalog.ProductCache;
import com.alphaseries.game.catalog.VoucherRedemption;
import com.alphaseries.game.catalog.VoucherWire;
import com.alphaseries.game.help.HelpCenterCache;
import com.alphaseries.game.help.HelpCenterBootCache;
import com.alphaseries.game.help.HelpCenterState;
import com.alphaseries.game.help.HelpWire;
import com.alphaseries.game.inventory.CreditFurniture;
import com.alphaseries.game.inventory.InventoryItemRow;
import com.alphaseries.game.inventory.InventoryMessagePayloads;
import com.alphaseries.game.inventory.InventoryRefreshService;
import com.alphaseries.game.jukebox.JukeboxPlaylistEntry;
import com.alphaseries.game.jukebox.JukeboxAddRequest;
import com.alphaseries.game.jukebox.JukeboxLookups;
import com.alphaseries.game.jukebox.JukeboxRequests;
import com.alphaseries.game.jukebox.SongInfoRequest;
import com.alphaseries.game.catalog.GiftSettings;
import com.alphaseries.game.chat.ChatCommands;
import com.alphaseries.game.jukebox.SongDiskRow;
import com.alphaseries.game.jukebox.SongInfoRow;
import com.alphaseries.game.messenger.AcceptedFriendRequests;
import com.alphaseries.game.messenger.MessengerFriend;
import com.alphaseries.game.messenger.MessengerLookups;
import com.alphaseries.game.messenger.MessengerRoomInvite;
import com.alphaseries.game.messenger.MessengerSearchResult;
import com.alphaseries.game.messenger.MessengerSettings;
import com.alphaseries.game.messenger.MessengerState;
import com.alphaseries.game.messenger.MessengerViews;
import com.alphaseries.game.messenger.MessengerWire;
import com.alphaseries.game.messenger.PendingFriendRequest;
import com.alphaseries.game.navigator.NavigatorBootCache;
import com.alphaseries.game.navigator.NewFriendRooms;
import com.alphaseries.game.navigator.NavigatorRequests;
import com.alphaseries.game.navigator.NavigatorRoom;
import com.alphaseries.game.navigator.NavigatorState;
import com.alphaseries.game.navigator.NavigatorWire;
import com.alphaseries.game.navigator.OfficialNavigatorItem;
import com.alphaseries.game.navigator.RecommendedRooms;
import com.alphaseries.game.navigator.RoomCategoryCache;
import com.alphaseries.game.pet.PetBootCache;
import com.alphaseries.game.pet.PetCommandCacheRow;
import com.alphaseries.game.pet.PetCommandAction;
import com.alphaseries.game.pet.PetExperienceUpdate;
import com.alphaseries.game.pet.PetInventoryRow;
import com.alphaseries.game.pet.PetLookups;
import com.alphaseries.game.pet.PetLevelCacheRow;
import com.alphaseries.game.pet.PetPackagePlacement;
import com.alphaseries.game.pet.PetPayloads;
import com.alphaseries.game.pet.PetProgress;
import com.alphaseries.game.pet.PetRaceCacheRow;
import com.alphaseries.game.pet.PetRaceRow;
import com.alphaseries.game.pet.PetRoomOccupants;
import com.alphaseries.game.pet.PetSettings;
import com.alphaseries.game.pet.PetState;
import com.alphaseries.game.pet.PetStatusRow;
import com.alphaseries.game.pet.PetWire;
import com.alphaseries.game.pet.RepresentedBotEntry;
import com.alphaseries.game.pet.RepresentedBotRegistry;
import com.alphaseries.game.poll.PollAnswerRow;
import com.alphaseries.game.poll.PollAnswerSubmission;
import com.alphaseries.game.poll.PollDefinition;
import com.alphaseries.game.poll.PollHeader;
import com.alphaseries.game.poll.PollLookups;
import com.alphaseries.game.poll.PollPrompt;
import com.alphaseries.game.poll.PollQuestionRow;
import com.alphaseries.game.poll.PollWire;
import com.alphaseries.game.quest.QuestAcceptResult;
import com.alphaseries.game.quest.QuestProgress;
import com.alphaseries.game.quest.QuestProgressDecision;
import com.alphaseries.game.quest.QuestResetResult;
import com.alphaseries.game.quest.QuestWire;
import com.alphaseries.game.chat.ChatSettings;
import com.alphaseries.game.chat.ChatState;
import com.alphaseries.game.user.AvatarNameUpdate;
import com.alphaseries.game.user.UserActivityPoints;
import com.alphaseries.game.user.UserEffectActivation;
import com.alphaseries.game.user.UserEffectExpiry;
import com.alphaseries.game.moderation.StaffCallForHelpRow;
import com.alphaseries.game.moderation.StaffModerationPacketHandlers;
import com.alphaseries.game.moderation.StaffModerationLookups;
import com.alphaseries.game.moderation.StaffPayloads;
import com.alphaseries.game.moderation.StaffModerationBootCache;
import com.alphaseries.game.moderation.StaffRoomChatRow;
import com.alphaseries.game.moderation.StaffRoomChatVisitRow;
import com.alphaseries.game.moderation.StaffRoomVisitRow;
import com.alphaseries.game.moderation.StaffWire;
import com.alphaseries.game.moderation.StaffSettings;
import com.alphaseries.game.moderation.ModerationState;
import com.alphaseries.game.moderation.StaffUserLookup;
import com.alphaseries.game.moderation.StaffUserSummaryRow;
import com.alphaseries.game.quest.QuestSettings;
import com.alphaseries.game.quest.QuestState;
import com.alphaseries.game.recycler.RecyclerBootCache;
import com.alphaseries.game.recycler.RecyclerLookups;
import com.alphaseries.game.recycler.RecyclerSelection;
import com.alphaseries.game.recycler.RecyclerSettings;
import com.alphaseries.game.recycler.RecyclerState;
import com.alphaseries.game.recycler.RecyclerWire;
import com.alphaseries.game.room.CreatedRoom;
import com.alphaseries.game.room.FurnitureCharges;
import com.alphaseries.game.room.FurnitureDimmers;
import com.alphaseries.game.room.FurnitureLookups;
import com.alphaseries.game.room.FurnitureRoomCache;
import com.alphaseries.game.room.FurnitureScoreStates;
import com.alphaseries.game.room.FurnitureStateWrites;
import com.alphaseries.game.room.FurnitureWire;
import com.alphaseries.game.room.MovementStep;
import com.alphaseries.game.room.RepresentedRoomCache;
import com.alphaseries.game.room.RepresentedRoomSlots;
import com.alphaseries.game.room.RoomEventBootCache;
import com.alphaseries.game.room.RoomEventLocales;
import com.alphaseries.game.room.RoomEventPayload;
import com.alphaseries.game.room.RoomLookups;
import com.alphaseries.game.room.RoomModelFurnitureRow;
import com.alphaseries.game.room.RoomObjectEntryPayloadArgs;
import com.alphaseries.game.room.RoomOccupantRow;
import com.alphaseries.game.room.RoomPortalSettings;
import com.alphaseries.game.room.RoomPositionService;
import com.alphaseries.game.room.RoomRefreshService;
import com.alphaseries.game.room.RoomRollers;
import com.alphaseries.game.room.RoomSettingsPayload;
import com.alphaseries.game.room.RoomState;
import com.alphaseries.game.room.RoomWire;
import com.alphaseries.game.room.RoomUserPosition;
import com.alphaseries.game.room.RoomUserEntryPayloadArgs;
import com.alphaseries.game.room.RoomUserEntryRow;
import com.alphaseries.game.room.RoomUserTargetRow;
import com.alphaseries.game.room.StaffPickedToggle;
import com.alphaseries.game.room.WallPlacement;
import com.alphaseries.game.session.GameServerSessionState;
import com.alphaseries.game.session.RepresentedSocketCache;
import com.alphaseries.game.session.SessionRegistry;
import com.alphaseries.game.session.SessionState;
import com.alphaseries.game.session.SessionWire;
import com.alphaseries.game.session.SocketMarkerSet;
import com.alphaseries.game.social.BadgeUpdateSelections;
import com.alphaseries.game.social.BadgeRow;
import com.alphaseries.game.social.SocialLookups;
import com.alphaseries.game.social.SocialRoomOccupants;
import com.alphaseries.game.social.SocialWire;
import com.alphaseries.game.trade.RepresentedTradeOffer;
import com.alphaseries.game.trade.TradeConfirmation;
import com.alphaseries.game.trade.TradeInteractionCloseAction;
import com.alphaseries.game.trade.TradeInteractionRequestAction;
import com.alphaseries.game.trade.TradeInteractionStateAction;
import com.alphaseries.game.trade.TradeLookups;
import com.alphaseries.game.trade.TradeOfferAction;
import com.alphaseries.game.trade.TradePayloads;
import com.alphaseries.game.trade.TradeState;
import com.alphaseries.game.trade.TradeWire;
import com.alphaseries.game.user.OwnProfileRow;
import com.alphaseries.game.user.UserLookups;
import com.alphaseries.game.user.UserRefreshService;
import com.alphaseries.game.user.UserEffectSummaryRow;
import com.alphaseries.game.user.UserGroupRow;
import com.alphaseries.game.user.UserValidation;
import com.alphaseries.game.user.UserWire;
import com.alphaseries.game.wired.WiredPayloads;
import com.alphaseries.game.wired.WiredCache;
import com.alphaseries.game.wired.WiredLookups;
import com.alphaseries.game.wired.WiredSettings;
import com.alphaseries.game.wired.WiredState;
import com.alphaseries.game.wired.WiredWire;
import com.alphaseries.messages.incoming.MessageRegistry;
import com.alphaseries.messages.incoming.ReadyPacketRegistry;
import com.alphaseries.messages.outgoing.AchievementPayloads;
import com.alphaseries.messages.outgoing.CatalogPayloads;
import com.alphaseries.messages.outgoing.ClubPayloads;
import com.alphaseries.messages.outgoing.FurniturePayloads;
import com.alphaseries.messages.outgoing.HelpPayloads;
import com.alphaseries.messages.outgoing.JukeboxPayloads;
import com.alphaseries.messages.outgoing.MessengerPayloads;
import com.alphaseries.messages.outgoing.NavigatorPayloads;
import com.alphaseries.messages.outgoing.PollPayloads;
import com.alphaseries.messages.outgoing.QuestPayloads;
import com.alphaseries.messages.outgoing.RecyclerPayloads;
import com.alphaseries.messages.outgoing.RoomPayloads;
import com.alphaseries.messages.outgoing.SessionPayloads;
import com.alphaseries.messages.outgoing.SocialPayloads;
import com.alphaseries.messages.outgoing.UserPayloads;
import com.alphaseries.messages.outgoing.VoucherPayloads;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.protocol.PacketReader;
import com.alphaseries.protocol.WireEncoding;
import com.alphaseries.protocol.WireReader;
import com.alphaseries.protocol.WireRequests;
import com.alphaseries.server.http.PrivSockHTTP;
import com.alphaseries.server.lifecycle.BootLog;
import com.alphaseries.server.lifecycle.LifecycleState;
import com.alphaseries.server.logging.Console;
import com.alphaseries.server.mus.MusConnectionManager;
import com.alphaseries.server.mus.MusPayloads;
import com.alphaseries.server.lifecycle.LicenceChecker;
import com.alphaseries.server.lifecycle.LicenceCheckState;
import com.alphaseries.server.lifecycle.ServerLifecycle;
import com.alphaseries.server.lifecycle.StartupEnvironmentError;
import com.alphaseries.server.packet.Filesystems;
import com.alphaseries.server.runtime.Guardian;
import com.alphaseries.server.runtime.GameServerBridge;
import com.alphaseries.server.runtime.RuntimeTasks;
import com.alphaseries.server.update.Updater;
import com.alphaseries.server.update.UpdaterSettings;
import com.alphaseries.server.update.UpdaterState;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.FileUtils;
import com.alphaseries.util.IdentityEncoding;
import com.alphaseries.util.RandomUtils;
import com.alphaseries.util.StringUtils;
import com.alphaseries.util.TimeUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class PortedModuleSmokeTest {
    private PortedModuleSmokeTest() {
    }

    public static void main(String[] args) throws Exception {
        run(() -> {
        assertEquals(0L, WireEncoding.decodeVl64(WireEncoding.encodeVl64(0)));
        assertEquals(1L, WireEncoding.decodeVl64(WireEncoding.encodeVl64(1)));
        assertEquals(42L, WireEncoding.decodeVl64(WireEncoding.encodeVl64(42)));
        assertEquals(-42L, WireEncoding.decodeVl64(WireEncoding.encodeVl64(-42)));
        assertEquals(4096L, WireEncoding.decodeVl64(WireEncoding.encodeVl64(4096)));
        assertEquals(1L, WireEncoding.encodedVl64LengthByteCount(WireEncoding.encodeVl64(0)));
        assertEquals(64L, WireEncoding.decodeBase64Length("@\u0080"));
        assertEquals(4096L, WireEncoding.decodeVl64(WireEncoding.encodeVl64(4096)));
        assertEquals("DK" + WireEncoding.encodeVl64(2) + "figure\2",
            PacketBuilder.message("DK").appendInt(2).appendString("figure").build());
        assertEquals("Dk" + encodedVl64(1, null, "")
            + encodedVl64(96, null, ""), UserPayloads.errorCode(1, 96));
        assertEquals(encodedVl64(2, null, "H{") + "NewName\2",
            UserPayloads.avatarNameValidation(2, "NewName"));
        assertEquals(encodedVl64(4, null,
            encodedVl64(77, null, "H|")) + "NewName\2",
            UserPayloads.roomUserNameChanged(77, 4, "NewName"));
        assertEquals(encodedVl64(1, null, "DX"), UserPayloads.emailStatus(1));
        assertEquals(encodedVl64(88, null, "Fx") + encodedVl64(12, null, ""),
            UserPayloads.respectReceived(88, 12));
        assertEquals(UserPayloads.errorCode(1, 96), UserPayloads.errorCode(1, 96));
        PacketReader reader = PacketReader.of(WireEncoding.encodeVl64(7) + "@Dtesttail");
        assertEquals(7L, reader.readInt());
        assertEquals("test", reader.readString());
        assertEquals("tail", reader.remaining());
        MessageRegistry readyRegistry = ReadyPacketRegistry.create();
        assertEquals(true, readyRegistry.headers().contains("CN"));
        assertEquals(true, readyRegistry.headers().contains("F_"));
        assertEquals(true, readyRegistry.headers().contains("CD"));

        String config = "mySQL_host=db\r\nmySQL_port=3307\nmySQL_db=alpha\nmySQL_username=user\nmySQL_password=pass";
        assertEquals("Driver={MySQL ODBC 3.51 Driver};Server=db;Port=3307;Database=alpha;User=user;Password=pass;Option=3;",
            AppDatabaseConfig.buildDatabaseConnectionString(config));
        assertEquals("Driver={Driver};Server=host;Port=3308;Database=db;User=user;Password=pass;Option=3;",
            AppDatabaseConfig.buildDatabaseConnectionString(
                new AppDatabaseConfig.DatabaseConnectionSettings("host", "3308", "db", "user", "pass", "Driver")));
        final List<String> connectionStrings = new ArrayList<>();
        AppDatabaseConfig.configureDatabaseConnector(connectionString -> {
            connectionStrings.add(connectionString);
            return new Database() {
                @Override
                public void execute(String sqlText) {
                }

                @Override
                public List<List<Object>> query(String sqlText) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("connected"));
                }
            };
        });
        assertEquals(1L, AppDatabaseConfig.connectDatabaseFromConfig(config));
        assertEquals(AppDatabaseConfig.buildDatabaseConnectionString(config), connectionStrings.get(0));
        assertEquals("jdbc:mysql://db:3307/alpha?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
            AppDatabaseConfig.jdbcUrl("db", "3307", "alpha"));
        assertEquals("db", AppDatabaseConfig.parseOdbcConnectionString(connectionStrings.get(0)).get("server"));
        assertEquals("pass", AppDatabaseConfig.parseOdbcConnectionString(connectionStrings.get(0)).get("password"));
        assertEquals("connected", MySQL.readSqlRows("SELECT 1"));
        assertEquals(0L, AppDatabaseConfig.connectDatabaseFromConfig(""));
        AppDatabaseConfig.configureDatabaseConnector(connectionString -> {
            throw new IllegalStateException("connect failed");
        });
        assertEquals(0L, AppDatabaseConfig.connectDatabaseFromConfig(config));
        AppDatabaseConfig.configureDatabaseConnector(null);
        MySQL.configureDatabaseConnection(null);

        GameDataCaches.setRoomEventLocales(RoomEventLocales.fromEntries(
            List.of(new RoomEventLocales.LocaleEntry("1", List.of("events", "name", "price")))));
        assertEquals("events", GameDataCaches.roomEventLocales().categoryName(1));
        assertEquals(List.of(new RoomEventLocales.LocaleEntry("1", List.of("events", "name", "price"))),
            GameDataCaches.roomEventLocales().entries());
        GameDataCaches.setProductCache(ProductCache.fromProductRows(List.of(
            productCacheRow(10, "1", "sofa", "2", "5"),
            productCacheRow(11, "1", "table", "2", "7", "14", "Table"))));
        assertEquals("Table", GameDataCaches.productCache().displayName(11));
        List<ProductCache.ProductRow> dataManagerProducts = List.of(productCacheRow(11, "0", "9", "4", "default", "5", "fallback", "7", "switch",
            "10", "6", "12", "4", "13", "Trade", "14", "Name", "15", "Description",
            "17", "present_wrap_basic", "18", "post.it.vd", "20", "77", "24", "payload",
            "26", "ACH", "27", "502", "34", "1"));
        GameDataCaches.setProductCache(ProductCache.fromProductRows(dataManagerProducts));
        assertEquals(9L, GameDataCaches.productCache().type(11));
        assertEquals("default", GameDataCaches.productCache().defaultSign(11));
        assertEquals("fallback", GameDataCaches.productCache().fallbackDefaultSign(11));
        assertEquals("switch", GameDataCaches.productCache().interactionAction(11));
        assertEquals(6L, GameDataCaches.productCache().stateCount(11));
        assertEquals(4L, GameDataCaches.productCache().maxState(11));
        assertEquals("Trade", GameDataCaches.productCache().tradeName(11));
        assertEquals("Name", GameDataCaches.productCache().displayName(11));
        assertEquals("Description", GameDataCaches.productCache().description(11));
        assertEquals("present_wrap_basic", GameDataCaches.productCache().primarySprite(11));
        assertEquals("post.it.vd", GameDataCaches.productCache().alternateSprite(11));
        assertEquals(77L, GameDataCaches.productCache().dimensionMapId(11));
        assertEquals("payload", GameDataCaches.productCache().itemData(11));
        assertEquals("ACH", GameDataCaches.productCache().badgeId(11));
        assertEquals("502", GameDataCaches.productCache().fallbackBadgeId(11));
        assertEquals(502L, GameDataCaches.productCache().wiredCode(11));
        assertEquals(true, GameDataCaches.productCache().hasCharges(11));
        ProductCache typedProductCache = ProductCache.fromRows(List.of(new CatalogDao.ProductCacheRow(List.of(
            "12", "7", "", "", "", "typed", "fallbackTyped"))));
        assertEquals(7L, typedProductCache.type(12));
        assertEquals("typed", typedProductCache.defaultSign(12));
        assertEquals("fallbackTyped", typedProductCache.fallbackDefaultSign(12));
        ProductCache cacheProductCache = ProductCache.fromRows(List.of(
            new CatalogDao.ProductCacheRow(List.of("13", "8", "", "", "", "cache", "fallbackCache"))));
        assertEquals(8L, cacheProductCache.type(13));
        assertEquals("cache", cacheProductCache.defaultSign(13));
        assertEquals("rowCache", ProductCache.fromProductRows(List.of(productCacheRow(14, "4", "rowCache"))).defaultSign(14L));
        assertProductCacheRows(typedProductCache);
        Path dataManagerWritePath = Files.createTempFile("alphaseries-datamanager-write", ".txt");
        FileUtils.writeTextFile(dataManagerWritePath.toString(), "replace");
        assertEquals("replace" + System.lineSeparator(), new String(Files.readAllBytes(dataManagerWritePath), "UTF-8"));
        FileUtils.appendTextFile(dataManagerWritePath.toString(), "append");
        assertEquals("replace" + System.lineSeparator() + "append" + System.lineSeparator(),
            new String(Files.readAllBytes(dataManagerWritePath), "UTF-8"));
        assertEquals("pro", LicenceChecker.extractLicenceSetting("\rrank=7\rmode:pro\r", "mode"));
        assertEquals("rank=7\rmode\nok", LicenceChecker.licenceBlockFromResponse("aFMTbFMTcFMTrank=7--*-mode*-*-ok", "FMT"));
        assertEquals("fallback", LicenceChecker.licenceBlockFromResponse("prefixFMTfallback", "FMT"));
        assertEquals("\rrank=7\rmode=pro\r", LicenceChecker.licenceCacheTextFromBlock("rank=7\nmode=pro"));
        assertEquals("reason text", LicenceChecker.blockedLicenceMessage("{BLOCKED reason%20text}"));
        assertEquals("5AZ12675B12870", LicenceChecker.buildLicenceToken("AB", 3, 65, "ZB"));
        assertEquals("AB", LicenceChecker.decodeShiftedLicenceText("YCD"));
        assertEquals(true, LicenceChecker.applyLicenceResponse("rank=2\r7:2=5\r8:2=1", "FMT", 0));
        assertEquals(2, LicenceChecker.licenceRank());
        assertEquals(5, LicenceChecker.cachedLicenceRankValue(7));
        assertEquals(1, LicenceChecker.cachedLicenceRankValue(8));
        assertEquals("", LicenceChecker.lastLicenceFailureMessage);
        LicenceCheckState typedLicenceState = LicenceCheckState.fromCacheText("\rrank=2\r7:2=5\r9:2=10\r");
        assertEquals(2, typedLicenceState.rank());
        assertEquals(5, typedLicenceState.cachedRankValue(7));
        assertEquals(1, typedLicenceState.cachedRankValue(9));
        assertEquals(true, LicenceChecker.licenceChecksumValid("12345678" + "100000" + "-20-99980", 0));
        assertEquals(false, LicenceChecker.licenceChecksumValid("12345678" + "100000" + "-20-99981", 0));
        assertEquals(false, LicenceChecker.applyLicenceResponse("12345678" + "100000" + "-20-99981", "FMT", 0));
        assertEquals("Das Lizenzsystem ist zurzeit nicht erreichbar. Versuch es sp\u00e4ter wieder!",
            LicenceChecker.lastLicenceFailureMessage);
        assertEquals(false, LicenceChecker.applyLicenceResponse("{BLOCKED no%20licence}", "FMT", 0));
        assertEquals("no licence", LicenceChecker.lastLicenceFailureMessage);
        assertEquals(LicenceChecker.DEFAULT_LICENCE_ENDPOINT, LicenceChecker.licenceEndpointFromEnvironment(new HashMap<>()));
        Map<String, String> licenceEnvironment = new HashMap<>();
        licenceEnvironment.put(LicenceChecker.LICENCE_ENDPOINT_ENV, "http://127.0.0.1:8080/check_product_sep11");
        assertEquals("http://127.0.0.1:8080/check_product_sep11",
            LicenceChecker.licenceEndpointFromEnvironment(licenceEnvironment));
        assertEquals(true, LicenceChecker.buildLicenceRequestUrl(
            new LicenceChecker.LicenceCheckContext("PRODUCT-KEY", "ALPHASERIES_FINAL (PREMIUM)",
                LocalDateTime.of(2026, 6, 29, 14, 50, 0)),
            "http://127.0.0.1:8080/check_product_sep11").startsWith(
                "http://127.0.0.1:8080/check_product_sep11?local_time="));
        final List<String> licenceUrls = new ArrayList<>();
        LicenceChecker.configureLicenceHttpFetcher((requestUrl, action) -> {
            licenceUrls.add(action + ":" + requestUrl);
            return "rank=4\r7:4=1";
        });
        assertEquals(true, LicenceChecker.checkLicence(new LicenceChecker.LicenceCheckContext(
            "PRODUCT-KEY", "ALPHASERIES_FINAL (PREMIUM)", LocalDateTime.of(2026, 6, 29, 14, 50, 0))));
        assertEquals(true, licenceUrls.get(0).startsWith("1:http://www.alpha-series.com/check_product_sep11?local_time="));
        assertEquals(true, licenceUrls.get(0).contains("2026-06-29_14-50-00%3A"));
        assertEquals(true, licenceUrls.get(0).contains("&version=ALPHASERIES_FINAL+%28PREMIUM%29&productKey=PRODUCT-KEY&token="));
        assertEquals("ALPHASERIES_FINAL+%28PREMIUM%29", LicenceChecker.urlEncode("ALPHASERIES_FINAL (PREMIUM)"));
        assertEquals(4, LicenceChecker.licenceRank());
        assertEquals(1, LicenceChecker.cachedLicenceRankValue(7));
        LicenceChecker.configureLicenceHttpFetcher(null);

        AppConfigState.instance().setPermissionMatrix(permissions(
            new PermissionMatrix.PermissionPayload(1L, 0L, "\2fuse_mod\2fuse_chatlog\2fuse_receive_calls_for_help\2")));
        final List<String> mysqlHandlerPayloads = new ArrayList<>();
        MusConnectionManager.instance().configureSink((socketIndex, payload) -> mysqlHandlerPayloads.add(socketIndex + ":" + payload));
        MySQL.configureDatabaseConnection(new Database() {
            @Override
            public void execute(String sqlText) {
            }

            @Override
            public List<List<Object>> query(String sqlText) {
                if (sqlText.contains("SELECT id FROM users WHERE id_socket='4'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(77));
                }
                if (sqlText.contains("SELECT level_hc FROM users")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
                }
                if (sqlText.contains("SELECT level_hc,hc_days,hc2_days,hc_presents")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(2, 40, 80, 1, 10));
                }
                if (sqlText.contains("SELECT level FROM users")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(1));
                }
                if (sqlText.contains("staff_cfh WHERE staff_cfh.id='12'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(9, "Help room", 2, 501, 502, 2000));
                }
                if (sqlText.contains("logs_chat.id_room='9'") && sqlText.contains("logs_chat.timestamp < 2000")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(10, 20, 501, "alice", "help"));
                }
                if (sqlText.contains("rooms.id='12'") && sqlText.contains("models.id=rooms.id_model")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(12, "Chat room", 3));
                }
                if (sqlText.contains("logs_chat.id_room='12'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(11, 21, 502, "bob", "chat"));
                }
                if (sqlText.contains("rooms.id='13'") && sqlText.contains("users.id=rooms.id_owner")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(13, 4, 88, "owner", "Room", "Desc", "tag1", "tag2"));
                }
                if (sqlText.contains("rooms_events WHERE id_room='13'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("Event", "Event desc", "e1", "e2"));
                }
                return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
            }
        });
        String roomId12Field = "B12";
        String roomId13Field = "B13";
        StaffModerationPacketHandlers.sendCallForHelpChatLog(
            4, StaffWire.callForHelpChatLogRequest("GI" + roomId12Field));
        assertEquals(true, mysqlHandlerPayloads.get(0).startsWith("4:DATA\6" + "4\6HV"));
        StaffModerationPacketHandlers.sendRoomChatLog(4, StaffWire.roomChatLogRequest("GH" + roomId12Field));
        assertEquals(true, mysqlHandlerPayloads.get(1).startsWith("4:DATA\6" + "4\6HW"));
        StaffModerationPacketHandlers.sendRoomInfo(4, StaffWire.roomInfoRequest("GK" + roomId13Field));
        assertEquals(true, mysqlHandlerPayloads.get(2).startsWith("4:DATA\6" + "4\6HZ"));
        assertEquals("77", MySQL.mySqlUserIdFromSocket(4));
        assertEquals(true, MySQL.mySqlUserHasPermission("77", "fuse_chatlog"));
        MySQL.configureDatabaseConnection(null);
        MusConnectionManager.instance().configureSink(null);
        List<List<Object>> rows = Arrays.asList(
            new ArrayList<Object>(Arrays.<Object>asList(1, "alice")),
            new ArrayList<Object>(Arrays.<Object>asList(2, "bob")));
        assertEquals("1\talice\r2\tbob", MySQL.formatSqlRows(rows));
        AppConfigState.instance().setSettingsCache(settings("server.port", "1234", "name", "alpha"));
        assertEquals("1234", AppConfigState.instance().settingValueOrDefault("server.port", "0"));
        assertEquals("1234", AppConfigState.instance().settingsCache().valueOrDefault("server.port", "0"));
        assertEquals(1234L, AppConfigState.instance().settingsCache().longValueOrDefault("server.port", 0L));
        assertEquals("fallback", AppConfigState.instance().settingValueOrDefault("missing", "fallback"));
        assertEquals("fallback", AppConfigState.instance().settingsCache().valueOrDefault("missing", "fallback"));
        assertAppSettingsCache();
        AppConfigState.instance().setPermissionMatrix(permissions(
            new PermissionMatrix.PermissionPayload(0L, 0L, "\2base\2"),
            new PermissionMatrix.PermissionPayload(1L, 0L, "\2fuse_mod\2")));
        assertEquals(true, AppConfigState.instance().allowsPermission(1, "", "fuse_mod", 0));
        assertEquals(true, AppConfigState.instance().permissionMatrix().allows(1, "", "fuse_mod", 0));
        assertPermissionMatrix();
        assertEquals("bcd", StringUtils.middleText("abcdef", 2, 3));
        assertEquals("O''Reilly test ", StringUtils.sqlEscapedText("O'Reilly\\rtest\""));
        assertEquals("Line Break", StringUtils.singleLineText("Line\nBreak"));
        String vl64LengthPayload = wireLong(3) + "abc";
        assertEquals("@ab", WireEncoding.readVl64LengthString(vl64LengthPayload));
        assertEquals("packet", WireEncoding.readBase64LengthString(wireString("packet")));
        assertEquals(5L, RandomUtils.longInclusive(5, 5));
        long randomRangeValue = RandomUtils.longInclusive(2, 4);
        assertEquals(true, randomRangeValue >= 2L && randomRangeValue <= 4L);
        assertEquals("a\u00a0b", StringUtils.normalizeNullBytes("a\0b"));
        InventoryRefreshService.InventoryItem inventoryItem =
            new InventoryRefreshService.InventoryItem(123, 45, "data", 6);
        InventoryRefreshService.InventoryCache baseInventoryCache =
            new InventoryRefreshService.InventoryCache("x", List.of());
        InventoryRefreshService.InventoryCache inventoryCache = baseInventoryCache.add(inventoryItem);
        assertEquals(new InventoryRefreshService.InventoryCache("x", List.of(inventoryItem)), inventoryCache);
        assertEquals(inventoryCache, inventoryCache.add(new InventoryRefreshService.InventoryItem(123, 99, "other", 1)));
        assertEquals(baseInventoryCache, inventoryCache.remove(123));
        assertEquals(inventoryCache, inventoryCache.remove(999));
        assertEquals("Ab" + InventoryMessagePayloads.item(123, 45, "data", 6) + '\1',
            InventoryMessagePayloads.add(123, 45, "data", 6));
        assertEquals("Ab" + InventoryMessagePayloads.item(123, 45, "data", 6) + '\2',
            InventoryMessagePayloads.roomAdd(123, 45, "data", 6));
        assertEquals(encodedVl64(123, null, "Ac"), InventoryMessagePayloads.remove(123));
        assertEquals(10L, CreditFurniture.fromSprite("CF_10").value());
        assertEquals(50L, CreditFurniture.fromSprite("CFC_50_extra").value());
        assertEquals(false, CreditFurniture.fromSprite("chair").redeemable());
        assertEquals("A^73\2", FurniturePayloads.floorItemRemoved(73));
        assertEquals("A^73\2H\2", FurniturePayloads.floorItemRemovedWithState(73, "H"));
        Path inventoryRoot = Files.createTempDirectory("alphaseries-inventory");
        String previousApplicationPath = AppPaths.applicationPath();
        AppPaths.setApplicationPath(inventoryRoot.toString());
        final List<String> inventoryRefreshPayloads = new ArrayList<>();
        SessionState.instance().setSessionRegistry(linkedSessionRegistry("200", "33\0"));
        MusConnectionManager.instance().configureSink((socketIndex, payload) -> inventoryRefreshPayloads.add(socketIndex + ":" + payload));
        MySQL.configureDatabaseConnection(new Database() {
            @Override
            public void execute(String sqlText) {
            }

            @Override
            public List<List<Object>> query(String sqlText) {
                if (sqlText.contains("id ='501'") && sqlText.contains("id_secondary")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(45, 200, "data", 6));
                }
                if (sqlText.contains("id ='501'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(45, 200, "data"));
                }
                return Arrays.<List<Object>>asList(Arrays.<Object>asList());
            }
        });
        assertEquals(1L, InventoryRefreshService.sendInventoryAddRefresh(501));
        Path inventoryCachePath = inventoryRoot.resolve("cache").resolve("users").resolve("200.cache");
        assertEquals("\1" + "501\t45\tdata\t6\2" + System.lineSeparator(),
            new String(Files.readAllBytes(inventoryCachePath), "UTF-8"));
        assertEquals("33:DATA\6" + "33\6"
                + InventoryMessagePayloads.add(501, 45, "data", 6) + "\7",
            inventoryRefreshPayloads.get(0));
        assertEquals(1L, InventoryRefreshService.sendInventoryRemoveRefresh(501));
        assertEquals(System.lineSeparator(), new String(Files.readAllBytes(inventoryCachePath), "UTF-8"));
        assertEquals("33:DATA\6" + "33\6" + InventoryMessagePayloads.remove(501) + "\7",
            inventoryRefreshPayloads.get(1));
        MySQL.configureDatabaseConnection(null);
        MusConnectionManager.instance().configureSink(null);
        SessionState.instance().setSessionRegistry(SessionRegistry.empty());
        AppPaths.setApplicationPath(previousApplicationPath);
        assertEquals("@F250.0\2", UserRefreshService.creditsRefreshPayload(250));
        assertEquals(encodedVl64(2, null, encodedVl64(300, null, "Fv") + "H"),
            UserRefreshService.activityPointRefreshPayload(2, 300));
        String expectedPointRefreshes = "";
        for (int pointType = 0; pointType <= 4; pointType++) {
            expectedPointRefreshes += UserRefreshService.activityPointRefreshPayload(pointType, pointType * 10L);
        }
        assertEquals(expectedPointRefreshes, UserRefreshService.activityPointRefreshPayloads(0, 10, 20, 30, 40));
        final List<String> refreshQueries = new ArrayList<>();
        final List<String> refreshPayloads = new ArrayList<>();
        SessionState.instance().setSessionRegistry(linkedSessionRegistry("77", "42\0"));
        MusConnectionManager.instance().configureSink((socketIndex, payload) -> refreshPayloads.add(socketIndex + ":" + payload));
        MySQL.configureDatabaseConnection(new Database() {
            @Override
            public void execute(String sqlText) {
            }

            @Override
            public List<List<Object>> query(String sqlText) {
                refreshQueries.add(sqlText);
                if (sqlText.contains("SELECT credits")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(250));
                }
                for (int pointType = 0; pointType <= 4; pointType++) {
                    if (sqlText.contains("activitypoints_" + pointType)) {
                        return Arrays.<List<Object>>asList(Arrays.<Object>asList(pointType * 10));
                    }
                }
                return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
            }
        });
        assertEquals(1L, UserRefreshService.sendCreditsRefresh("77"));
        assertEquals("42:DATA\6" + "42\6" + UserRefreshService.creditsRefreshPayload(250) + "\7", refreshPayloads.get(0));
        assertEquals(5L, UserRefreshService.sendActivityPointRefreshes("77"));
        assertEquals(6, refreshPayloads.size());
        assertEquals("42:DATA\6" + "42\6" + UserRefreshService.activityPointRefreshPayload(4, 40) + "\7",
            refreshPayloads.get(5));
        assertEquals(0L, UserRefreshService.sendCreditsRefresh("missing"));
        assertEquals(0L, UserRefreshService.sendActivityPointRefreshes("missing"));
        assertEquals(6, refreshPayloads.size());
        MySQL.configureDatabaseConnection(null);
        MusConnectionManager.instance().configureSink(null);
        SessionState.instance().setSessionRegistry(SessionRegistry.empty());
        final List<String> readyPayloads = new ArrayList<>();
        MusConnectionManager.instance().configureSink((socketIndex, payload) -> readyPayloads.add(socketIndex + ":" + payload));
        MySQL.configureDatabaseConnection(new Database() {
            @Override
            public void execute(String sqlText) {
            }

            @Override
            public List<List<Object>> query(String sqlText) {
                if (sqlText.contains("logs_visitedrooms.id_room='77'")) {
                    return Arrays.asList(
                        Arrays.<Object>asList(5),
                        Arrays.<Object>asList(5),
                        Arrays.<Object>asList(0),
                        Arrays.<Object>asList(6));
                }
                return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
            }
        });
        assertEquals(2L, RoomRefreshService.sendRoomReadyRefreshes(77));
        assertEquals("5:DATA\6" + "5\6@R\7", readyPayloads.get(0));
        assertEquals("6:DATA\6" + "6\6@R\7", readyPayloads.get(1));
        assertEquals(0L, RoomRefreshService.sendRoomReadyRefreshes(0));
        MySQL.configureDatabaseConnection(null);
        MusConnectionManager.instance().configureSink(null);
        assertEquals("Baalert\2hello\2", RoomRefreshService.roomAlertPayload("alert", "hello"));
        final List<String> alertPayloads = new ArrayList<>();
        SessionState.instance().setSessionRegistry(linkedSessionRegistry("81", "9\0"));
        MusConnectionManager.instance().configureSink((socketIndex, payload) -> alertPayloads.add(socketIndex + ":" + payload));
        assertEquals(1L, RoomRefreshService.sendUserRoomAlert("81", "notice", "hello"));
        assertEquals("9:DATA\6" + "9\6" + RoomRefreshService.roomAlertPayload("notice", "hello") + "\7",
            alertPayloads.get(0));
        assertEquals(0L, RoomRefreshService.sendUserRoomAlert("missing", "notice", "hello"));
        MySQL.configureDatabaseConnection(new Database() {
            @Override
            public void execute(String sqlText) {
            }

            @Override
            public List<List<Object>> query(String sqlText) {
                if (sqlText.contains("logs_visitedrooms.id_room='55'")) {
                    return Arrays.asList(
                        Arrays.<Object>asList(9),
                        Arrays.<Object>asList(9),
                        Arrays.<Object>asList(0),
                        Arrays.<Object>asList(10));
                }
                return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
            }
        });
        assertEquals(2L, RoomRefreshService.sendRoomAlertToRoom(55, "room", "message"));
        assertEquals("9:DATA\6" + "9\6" + RoomRefreshService.roomAlertPayload("room", "message") + "\7",
            alertPayloads.get(1));
        assertEquals("10:DATA\6" + "10\6" + RoomRefreshService.roomAlertPayload("room", "message") + "\7",
            alertPayloads.get(2));
        assertEquals(0L, RoomRefreshService.sendRoomAlertToRoom(0, "room", "message"));
        MySQL.configureDatabaseConnection(null);
        MusConnectionManager.instance().configureSink(null);
        SessionState.instance().setSessionRegistry(SessionRegistry.empty());
        assertEquals("L}1\2" + encodedVl64(1, null, "") + encodedVl64(1, null, "") + "HH",
            UserRefreshService.emailValidatedPayload(0));
        assertEquals(encodedVl64(7, null, "DJ") + "motto\2M\2figure\2",
            UserRefreshService.userIdentityRefreshPayload(7, "motto", "figure", "M"));
        final List<String> userStateExecutions = new ArrayList<>();
        final List<String> userStatePayloads = new ArrayList<>();
        SessionState.instance().setSessionRegistry(linkedSessionRegistry(
            new SessionRegistry.LinkedSessionSection("91", "14\0"),
            new SessionRegistry.LinkedSessionSection("92", "15\0")));
        AppConfigState.instance().setSettingsCache(settings("com.server.socket.game.club.gifts.hcrank1.amount", "4"));
        MusConnectionManager.instance().configureSink((socketIndex, payload) -> userStatePayloads.add(socketIndex + ":" + payload));
        MySQL.configureDatabaseConnection(new Database() {
            @Override
            public void execute(String sqlText) {
                userStateExecutions.add(sqlText);
            }

            @Override
            public List<List<Object>> query(String sqlText) {
                if (sqlText.contains("SELECT email_validated")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
                }
                if (sqlText.contains("SELECT id,id_socket,motto,figure,gender") && sqlText.contains("id='92'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(92, 0, "motto", "figure", "F"));
                }
                return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
            }
        });
        assertEquals(1L, UserRefreshService.validateEmailAndRefresh(91));
        assertEquals("UPDATE users SET email_validated='1' WHERE id='91' LIMIT 1", userStateExecutions.get(0));
        assertEquals("14:DATA\6" + "14\6" + UserRefreshService.emailValidatedPayload(1) + "\7",
            userStatePayloads.get(0));
        assertEquals(1L, UserRefreshService.sendUserIdentityRefresh("92"));
        assertEquals("15:DATA\6" + "15\6"
                + UserRefreshService.userIdentityRefreshPayload(92, "motto", "figure", "F") + "\7",
            userStatePayloads.get(1));
        assertEquals(1L, ClubPeriodService.applyClubPeriod(93, 1, 0, 0));
        assertEquals("UPDATE users SET hc_startperiod=UNIX_TIMESTAMP(),hc_periods=hc_periods+1,hc_presents=hc_presents+4 WHERE id='93'",
            userStateExecutions.get(1));
        assertEquals(0L, ClubPeriodService.applyClubPeriod(0, 1, 0, 0));
        assertEquals(0L, UserRefreshService.validateEmailAndRefresh(0));
        MySQL.configureDatabaseConnection(null);
        MusConnectionManager.instance().configureSink(null);
        SessionState.instance().setSessionRegistry(SessionRegistry.empty());
        assertEquals("1\0" + "1\0" + "3\0" + "1\0", MovementStep.between(0, 0, 2, 2).frameText());
        assertEquals("0\0" + "0\0" + "0\0" + "0\0", MovementStep.zero().frameText());
        assertEquals(1L, RoomPositionService.representedPositionAvailable(0, 5, 5));
        assertEquals(0L, RoomPositionService.representedPositionAvailable(7, 1, 0));
        assertEquals(0L, RoomPositionService.representedPositionAvailable(7, 0, 1));
        assertEquals(1L, RoomPositionService.representedPositionAvailable(7, 0, 0));
        final List<String> occupancyQueries = new ArrayList<>();
        MySQL.configureDatabaseConnection(new Database() {
            @Override
            public void execute(String sqlText) {
            }

            @Override
            public List<List<Object>> query(String sqlText) {
                occupancyQueries.add(sqlText);
                if (sqlText.contains("FROM furnitures") && sqlText.contains("position_x='2'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(1));
                }
                if (sqlText.contains("FROM bots") && sqlText.contains("position_y='3'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(1));
                }
                return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
            }
        });
        assertEquals(1L, RoomPositionService.roomPositionAvailable(0, 2, 3));
        assertEquals(0, occupancyQueries.size());
        assertEquals(0L, RoomPositionService.roomPositionAvailable(7, 2, 4));
        assertEquals(1, occupancyQueries.size());
        assertEquals(0L, RoomPositionService.roomPositionAvailable(7, 4, 3));
        assertEquals(3, occupancyQueries.size());
        assertEquals(1L, RoomPositionService.roomPositionAvailable(7, 4, 4));
        MySQL.configureDatabaseConnection(null);
        final List<String> botAvailabilityQueries = new ArrayList<>();
        PetState.instance().setRepresentedBots(representedBots(Map.of(
            10L, "3\2" + "501\2room-bot",
            11L, "0\2" + "502\2fallback-bot",
            12L, "0\2" + "0\2self-bot")));
        MySQL.configureDatabaseConnection(new Database() {
            @Override
            public void execute(String sqlText) {
            }

            @Override
            public List<List<Object>> query(String sqlText) {
                botAvailabilityQueries.add(sqlText);
                if (sqlText.contains("FROM rooms") && sqlText.contains("id_slot='3'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(7));
                }
                if (sqlText.contains("id_room FROM bots") && sqlText.contains("id='502'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(8));
                }
                if (sqlText.contains("id_room FROM bots") && sqlText.contains("id='12'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
                }
                return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
            }
        });
        assertEquals(1L, RoomPositionService.representedBotPositionAvailable(10, 4, 4));
        assertEquals(true, botAvailabilityQueries.get(0).contains("FROM rooms"));
        assertEquals(1L, RoomPositionService.representedBotPositionAvailable(11, 4, 4));
        assertEquals(true, botAvailabilityQueries.get(3).contains("id_room FROM bots"));
        assertEquals(1L, RoomPositionService.representedBotPositionAvailable(12, 4, 4));
        assertEquals(0L, RoomPositionService.representedBotPositionAvailable(0, 4, 4));
        MySQL.configureDatabaseConnection(null);
        Path downloadSource = Files.createTempFile("alphaseries-download-source", ".txt");
        Path downloadDestination = Files.createTempFile("alphaseries-download-destination", ".txt");
        Files.write(downloadSource, "update-payload".getBytes("UTF-8"));
        assertEquals(true, FileUtils.downloadFile(downloadSource.toUri().toURL().toString(), downloadDestination.toString()));
        assertEquals("update-payload", new String(Files.readAllBytes(downloadDestination), "UTF-8"));
        assertEquals(false, FileUtils.downloadFile(downloadSource.toUri().toURL().toString(), ""));
        assertEquals("@@", WireEncoding.encodeBase64Length(-1));
        assertEquals("@A", WireEncoding.encodeBase64Length(1));
        assertEquals("A@", WireEncoding.encodeBase64Length(64));
        assertEquals(15.0f, TimeUtils.elapsedSeconds(86395.0f, 10.0f));
        assertEquals(250L, TimeUtils.delayMilliseconds("0.25"));
        assertEquals(250L, TimeUtils.delayMilliseconds("0,25"));
        assertEquals(0L, TimeUtils.delayMilliseconds("-1"));
        TimeUtils.sleepSeconds(0);
        Console.clear();
        Console.logSourceLine("boot", "SYS", 123);
        Console.appendPlainLine("raw", 456);
        Console.appendOptionalSourceLine("secret", "HIDDEN", 789);
        Console.appendOptionalSourceLine("shown", "GAME", 321);
        assertEquals("[SYS] boot", Console.entries().get(0).lineText());
        assertEquals(123L, Console.entries().get(0).foreColor());
        assertEquals("raw", Console.entries().get(1).lineText());
        assertEquals("secret", Console.entries().get(2).lineText());
        assertEquals("[GAME] shown", Console.entries().get(3).lineText());

        String previousFigureApplicationPath = AppPaths.applicationPath();
        Path emptyFigureRoot = Files.createTempDirectory("alphaseries-empty-figuredata");
        AppPaths.setApplicationPath(emptyFigureRoot.toString());
        MySQL.configureDatabaseConnection(new Database() {
            @Override
            public void execute(String sqlText) {
            }

            @Override
            public List<List<Object>> query(String sqlText) {
                return Arrays.<List<Object>>asList(Arrays.<Object>asList(""));
            }
        });
        Console.clear();
        BootLog.runTimed("Figuredata im Cache gespeichert", FiguredataBootCache::writeFiguredataCache);
        assertEquals("[ERROR] \"Figuredata\" Datei konnte nicht gefunden werden!",
            Console.entries().get(0).lineText());
        assertEquals(1, Console.entries().size());
        MySQL.configureDatabaseConnection(new Database() {
            @Override
            public void execute(String sqlText) {
            }

            @Override
            public List<List<Object>> query(String sqlText) {
                return Arrays.<List<Object>>asList(Arrays.<Object>asList("<settype type=\"hd\"/>"));
            }
        });
        Console.clear();
        BootLog.runTimed("Figuredata im Cache gespeichert", FiguredataBootCache::writeFiguredataCache);
        assertEquals(true, Console.entries().get(0).lineText().contains("Figuredata im Cache gespeichert"));
        AppPaths.setApplicationPath(previousFigureApplicationPath);
        MySQL.configureDatabaseConnection(null);

        List<String> sent = new ArrayList<>();
        Filesystems.configurePacketSink((socketIndex, payload) -> sent.add(socketIndex + ":" + payload));
        Filesystems.setActiveSessions(List.of(
            new Filesystems.ActiveSession("Alice", 5),
            new Filesystems.ActiveSession("Bob", 6)));
        assertEquals(2L, Filesystems.broadcastToActiveSessions("hello", ""));
        assertEquals(Arrays.asList("5:hello", "6:hello"), sent);
        sent.clear();
        assertEquals(1L, Filesystems.broadcastToActiveSessions("one", "bob"));
        assertEquals(Arrays.asList("6:one"), sent);
        assertEquals(true, Filesystems.isCrossDomainPolicyRequest("<policy-file-request/>\0"));
        assertEquals(false, Filesystems.isCrossDomainPolicyRequest("normal"));
        String framedPackets = "x@CCN1" + "x@DF_22" + "x@Z";
        assertEquals(Arrays.asList("CN1", "F_22"), Filesystems.readyPacketPayloadsFromBuffer(framedPackets));
        List<Filesystems.ReadyPacket> readyPackets = Filesystems.readyPacketsFromBuffer(framedPackets);
        assertEquals(2, readyPackets.size());
        assertEquals("CN", readyPackets.get(0).code());
        assertEquals("CN1", readyPackets.get(0).payload());
        assertEquals("F_", readyPackets.get(1).code());
        assertEquals("F_22", readyPackets.get(1).payload());
        assertEquals(Arrays.asList(), Filesystems.readyPacketPayloadsFromBuffer("<policy/>\0"));

        sent.clear();
        MusConnectionManager.instance().configureSink((socketIndex, payload) -> sent.add(socketIndex + ":" + payload));
        MusConnectionManager.instance().sendData(4, "payload");
        MusConnectionManager.instance().sendShutdown(4);
        assertEquals(Arrays.asList("4:DATA\6" + "4\6payload\7", "4:SHUTDOWN\6" + "4\7"), sent);
        sent.clear();
        MusConnectionManager.instance().sendData(5, "relay");
        MusConnectionManager.instance().sendShutdown(5);
        assertEquals(Arrays.asList("5:DATA\6" + "5\6relay\7", "5:SHUTDOWN\6" + "5\7"), sent);
        assertEquals("DATA\6" + "6\6named\7", MusPayloads.data(6, "named"));
        MusPayloads.ClientFrame dataFrame = MusPayloads.clientFrame("DATA\6" + "7\6client-payload\7");
        assertEquals("DATA", dataFrame.command());
        assertEquals("7", dataFrame.socketIndexText());
        assertEquals("client-payload", MusPayloads.clientPayload(dataFrame));
        MusPayloads.ClientFrame shutdownFrame = MusPayloads.clientFrame("SHUTDOWN\6" + "7\7");
        assertEquals("SHUTDOWN", shutdownFrame.command());
        assertEquals("7", shutdownFrame.socketIndexText());
        assertEquals("", MusPayloads.clientPayload(shutdownFrame));
        assertEquals("raw", MusPayloads.clientPayload(MusPayloads.clientFrame("raw")));

        });
        run(() -> {
        assertGuardianSocketMarkers();
        Guardian.setSocketMarkers(SocketMarkerSet.fromSocketIndexes(List.of(12L)));
        SessionState.instance().setSocketMarkers(SocketMarkerSet.fromSocketIndexes(List.of(4L, 12L)));
        Handling.disconnectSocket(12);
        assertEquals(Set.of(), Guardian.socketMarkers().socketIndexes());
        assertEquals(Set.of(4L), SessionState.instance().socketMarkers().socketIndexes());

        seedCatalogRegistryProductRows(List.of(
            productDaoRow(10, "0", "100", "1", "chair"),
            productDaoRow(11, "0", "200", "1", "table")));
        seedCatalogRegistryCatalogProductRows(List.of(catalogProductRow(1, "1", "alpha", "2", "7")));
        seedCatalogRegistryDealRows(List.of(
            new CatalogDao.ProductDealRow(5L, "row-five"),
            new CatalogDao.ProductDealRow(6L, "row-six")));
        CatalogRegistry registry = CatalogState.instance().registry();
        assertEquals(100L, NumberUtils.parseLong(registry.productCell(10, 1)));
        assertEquals("alpha", registry.catalogProductCell(1, 1));
        assertEquals(7L, NumberUtils.parseLong(registry.catalogProductCell(1, 2)));
        assertEquals(200L, registry.product(11).orElseThrow().type());
        assertEquals("alpha", registry.catalogProduct(1).orElseThrow().sprite());
        assertEquals(List.of(), registry.productDeal(6L).orElseThrow().itemProductIds());
        seedCatalogRegistryProductRows(List.of(new CatalogDao.ProductCacheRow(List.of("10", "100", "chair"))));
        seedCatalogRegistryCatalogProductRows(List.of(new CatalogDao.CatalogProductCacheRow(List.of("1", "alpha", "7"))));
        registry = CatalogState.instance().registry();
        assertEquals(100L, NumberUtils.parseLong(registry.productCell(10, 1)));
        assertEquals(100L, registry.product(10).orElseThrow().type());
        assertEquals("alpha", registry.catalogProductCell(1, 1));
        seedCatalogRegistryDealRows(List.of(new CatalogDao.ProductDealRow(6L, "10;11")));
        registry = CatalogState.instance().registry();
        assertEquals(List.of(10L, 11L), registry.productDeal(6L).orElseThrow().itemProductIds());
        assertEquals(10L, registry.product(10).orElseThrow().productId());
        assertEquals(1L, registry.catalogProduct(1).orElseThrow().catalogProductId());
        assertEquals(2, registry.productDeal(6L).orElseThrow().itemProductIds().size());
        assertCatalogRegistryRows();
        SessionState.instance().setSessionRegistry(sessionRegistry(
            new SessionRegistry.SessionRecord("0:5", "u5\2sock5"),
            new SessionRegistry.SessionRecord("1:6", "66\2" + "6"),
            new SessionRegistry.SessionRecord("room", "7\2" + "8")));
        assertEquals("u5", SessionState.instance().socketUserId("5"));
        assertEquals(66L, SessionState.instance().sessionUserIdBySocket(6));
        assertEquals(7L, SessionState.instance().sessionCacheLong("room", 0));
        SessionState.instance().setSessionRegistry(linkedSessionRegistry("91", "14\0"));
        assertEquals(14L, SessionState.instance().linkedUserSocketIndex("91"));
        assertEquals(14L, SessionState.instance().linkedSocketIndex("91"));
        SessionRegistry typedSessionRegistry = SessionRegistry.fromEntries(
            List.of(
                new SessionRegistry.SessionRecord("0:5", "u5\2sock5"),
                new SessionRegistry.SessionRecord("1:bob", "bob\2" + "6")),
            List.of(
                new SessionRegistry.LinkedSessionSection("91", "14\0"),
                new SessionRegistry.LinkedSessionSection("92", "15\0")));
        SessionState.instance().setSessionRegistry(typedSessionRegistry);
        assertEquals("u5", SessionState.instance().socketUserId("5"));
        assertEquals(14L, SessionState.instance().linkedSocketIndex("91"));
        assertEquals(15L, SessionState.instance().linkedSocketIndex("92"));
        SessionState.instance().setSessionRegistry(sessionRegistry(new SessionRegistry.SessionRecord("1:4", "77\2" + "4")));
        assertEquals(77L, SessionState.instance().sessionUserIdBySocket(4));
        SessionState.instance().setSessionRegistry(SessionRegistry.empty());
        SessionState.instance().storeSocketSession(9, "99\2" + "9");
        assertEquals(99L, SessionState.instance().socketSessions().get(0).userId());

        });
        run(() -> {
        String bootErrorHeader = BootLog.bootErrorLogHeader("ALPHASERIES_FINAL (PREMIUM)", "2026-06-30T10:15");
        assertEquals(true, bootErrorHeader.contains("Alpha Series [Version ALPHASERIES_FINAL (PREMIUM)"));
        assertEquals(true, bootErrorHeader.contains("Emulator is running since 2026-06-30T10:15, errors are being logged."));
        String bootSlowHeader = BootLog.bootSlowLogHeader("ALPHASERIES_FINAL (PREMIUM)", "2026-06-30T10:15");
        assertEquals(true, bootSlowHeader.contains(
            "slow query are being logged if you are running the development mode."));
        Console.clear();
        BootLog.printStartupNotice("short");
        assertEquals(0, Console.entries().size());
        BootLog.printStartupNotice();
        assertEquals(true, Console.entries().get(0).lineText().contains("ILLEGAL KOMBINATION"));
        assertEquals(true, Console.entries().get(0).lineText().contains("Bitte eigene Software nutzen"));
        assertEquals(49344L, Console.entries().get(0).foreColor());
        assertEquals("Unable to intialize. File may be corrupted!",
            BootLog.initializationIntegrityFailureMessage(true, "Alpha Series [INITIALISIERE] - [%%]"));
        assertEquals("", BootLog.initializationIntegrityFailureMessage(false, "Alpha Series [INITIALISIERE] - [%%]"));
        assertEquals("", BootLog.initializationIntegrityFailureMessage(true, "Alpha Series [RUNNING] - [%%]"));
        String[] startupCreditLines = BootLog.startupCreditLines();
        assertEquals(true, startupCreditLines[0].contains("2 . 0 - \"Meilenstein 2\""));
        assertEquals(true, startupCreditLines[1].contains("Server Autor: Privilege"));
        assertEquals(true, startupCreditLines[1].contains("Deutsche \u00dcbersetzung: Medaillon"));
        assertEquals(true, startupCreditLines[2].contains("Shoutouts: Tweeney, Pure, MoBaT"));
        Console.clear();
        BootLog.printStartupCredits();
        assertEquals(3, Console.entries().size());
        assertEquals(49344L, Console.entries().get(0).foreColor());
        assertEquals("Server has Exit Suburned following error:       socket failed",
            BootLog.serverReturnedErrorMessage("socket failed"));
        AchievementBootCache.AchievementSettingsCache achievementSettings = AchievementBootCache.buildAchievementSettingsCache(List.of(
            new AchievementDao.AchievementSettingsRow(7L, "ACH_ONE", 10L, 2L, 3L, 4L, 1L),
            new AchievementDao.AchievementSettingsRow(8L, "ACH_TWO", 20L, 5L, 6L, 7L, 2L)));
        assertEquals("7\2" + "8\2", achievementSettings.questIdPayload());
        assertEquals("ACH_TWO", achievementSettings.achievements().get(1).badgePrefix());
        assertEquals(20L, achievementSettings.achievements().get(1).progressRequired());
        int[] messengerFriendLimits = AchievementBootCache.buildMessengerFriendLimitCache(50, 75, 100);
        assertEquals(50, messengerFriendLimits[0]);
        assertEquals(0, messengerFriendLimits[1]);
        assertEquals(75, messengerFriendLimits[2]);
        assertEquals(100, messengerFriendLimits[4]);
        assertEquals(
            encodedVl64(2, null, "")
                + encodedVl64(1, null, "") + "public\2"
                + encodedVl64(0, null, "")
                + encodedVl64(3, null, "") + "hc\2"
                + encodedVl64(1, null, ""),
            NavigatorBootCache.buildRoomCategoryPayload(List.of(
                new RoomDao.RoomCategoryRow(1L, "public", 0L, 0L, 0L),
                new RoomDao.RoomCategoryRow(2L, "staff", 1L, 5L, 0L),
                new RoomDao.RoomCategoryRow(3L, "hc", 1L, 2L, 1L)), 2, 1));
        assertRecyclerCacheBuilders();
        assertEquals("[pet_dog\t1\t2\t3\t4\tDog][pet_cat\t5\t6\t7\t8\tCat]",
            PetBootCache.buildPetRaceCache(List.of(
                new PetRaceCacheRow("pet_dog", 1L, 2L, 3L, 4L, "Dog"),
                new PetRaceCacheRow("pet_cat", 5L, 6L, 7L, 8L, "Cat"))));
        assertEquals(List.of(new PetSettings.PetLevelRow(2L, 20L, 30L, 40L, 4)),
            PetBootCache.buildPetLevelRows(List.of(new PetLevelCacheRow(2L, 20L, 30L, 40L))));
        PetBootCache.PetCommandCache petCommandCache = PetBootCache.buildPetCommandCache(List.of(
            new PetCommandCacheRow(1L, 0L, "sit", "idle"),
            new PetCommandCacheRow(2L, 3L, "jump", "move")));
        assertEquals(2L, petCommandCache.commandCount());
        PetSettings.PetCommandRow jumpCommand = petCommandCache.commandById().get(2L);
        assertEquals(2L, jumpCommand.commandId());
        assertEquals(3L, jumpCommand.requiredLevel());
        assertEquals("jump", jumpCommand.command());
        assertEquals("move", jumpCommand.action());
        RoomEventLocales builtRoomEventLocales = RoomEventBootCache.buildRoomEventLocales(List.of(
                new SettingsDao.LocaleRow("roomevent_type_5", "party"),
                new SettingsDao.LocaleRow("roomevent_type_7", "game")),
            RoomEventLocales.empty());
        assertEquals(List.of(
                new RoomEventLocales.LocaleEntry("5", List.of("party", "")),
                new RoomEventLocales.LocaleEntry("7", List.of("game", ""))),
            builtRoomEventLocales.entries());
        assertRoomEventLocaleTypedBuilder();
        assertEquals("[site.name=Alpha][com.client.format.date=dd.mm.yyyy][com.client.format.time=hh:nn:ss]"
                + "[com.mysql.format.date=%d.%m.%Y][com.mysql.format.time=%H:%i:%s]",
            AppSettingsBootCache.buildSettingsCache(
                List.of(new SettingsDao.SettingRow("site.name", "Alpha")), "d.m.Y", "h:i:s"));
        assertEquals(
            encodedVl64(2, null, "")
                + encodedVl64(1, null, "")
                + encodedVl64(2, null, "")
                + encodedVl64(2, null, "")
                + encodedVl64(10, null, "")
                + encodedVl64(12, null, "")
                + encodedVl64(3, null, "")
                + encodedVl64(1, null, "")
                + encodedVl64(2, null, "")
                + encodedVl64(3, null, ""),
            CatalogGiftBootCache.buildGiftWrapPayload(List.of(10L, 0L, 12L), 2, 3));
        Map<Long, Long> giftProductIds = new HashMap<>();
        giftProductIds.put(100L, 200L);
        Map<Long, Long> giftProductTypes = new HashMap<>();
        giftProductTypes.put(200L, 9L);
        giftProductTypes.put(300L, 1L);
        Map<Long, String> giftNames = new HashMap<>();
        giftNames.put(200L, "VIP badge");
        giftNames.put(300L, "Fallback sofa");
        Map<Long, String> giftDescriptions = new HashMap<>();
        giftDescriptions.put(200L, "Badge desc");
        giftDescriptions.put(300L, "Sofa desc");
        CatalogGiftBootCache.ClubGiftCache clubGiftCache = CatalogGiftBootCache.buildClubGiftCache(List.of(
                new ClubDao.ClubGiftRow(100L, 1L, 30L),
                new ClubDao.ClubGiftRow(300L, 0L, 5L)),
            giftProductIds, giftProductTypes, giftNames, giftDescriptions);
        assertEquals(
            encodedVl64(2, null, "")
                + encodedVl64(100, null, "")
                + encodedVl64(200, null, "")
                + "VIP badge\2Badge desc\2IHHIi\2"
                + encodedVl64(1, null, "")
                + encodedVl64(30, null, "")
                + encodedVl64(300, null, "")
                + encodedVl64(300, null, "")
                + "Fallback sofa\2Sofa desc\2IHHIs\2"
                + encodedVl64(0, null, "")
                + encodedVl64(5, null, ""),
            clubGiftCache.giftPayload());
        assertEquals("[100\0" + "200\1" + "30][300\0" + "300\1" + "5]", clubGiftCache.giftLookup());
        Map<Long, List<HelpDao.FaqNameRow>> typedImportantFaqRows = new HashMap<>();
        typedImportantFaqRows.put(1L, List.of(new HelpDao.FaqNameRow(31L, "first")));
        typedImportantFaqRows.put(2L, List.of(new HelpDao.FaqNameRow(41L, "second"), new HelpDao.FaqNameRow(42L, "third")));
        String importantFaqPayload = HelpCenterBootCache.buildImportantFaqPayloadFromRows(typedImportantFaqRows);
        assertEquals(
            encodedVl64(2, null, "")
                + encodedVl64(1, null, "")
                + encodedVl64(31, null, "") + "first\2"
                + encodedVl64(2, null, "")
                + encodedVl64(41, null, "") + "second\2"
                + encodedVl64(42, null, "") + "third\2",
            importantFaqPayload);
        HelpCenterCache importantFaqCache = HelpCenterCache.fromPayloads(importantFaqPayload, "", Map.of(), Map.of());
        assertEquals("HF" + importantFaqPayload, HelpPayloads.importantFaqs(importantFaqCache));
        Map<Long, List<HelpDao.FaqNameRow>> typedFaqRowsByCategory = new HashMap<>();
        typedFaqRowsByCategory.put(7L, List.of(new HelpDao.FaqNameRow(70L, "faq-a"), new HelpDao.FaqNameRow(71L, "faq-b")));
        HelpCenterBootCache.FaqCategoryCache faqCategoryCache = HelpCenterBootCache.buildFaqCategoryCacheFromRows(
            List.of(new HelpDao.FaqNameRow(7L, "cat-a"), new HelpDao.FaqNameRow(9L, "cat-b")),
            typedFaqRowsByCategory);
        assertEquals(
            encodedVl64(2, null, "")
                + encodedVl64(7, null, "") + "cat-a\2"
                + encodedVl64(9, null, "") + "cat-b\2",
            faqCategoryCache.categoryPayload());
        HelpCenterCache categoryHelpCache = HelpCenterCache.fromPayloads(
            "",
            faqCategoryCache.categoryPayload(),
            faqCategoryCache.faqPayloadByCategoryId(),
            Map.of());
        assertEquals("HG" + faqCategoryCache.categoryPayload(), HelpPayloads.categories(categoryHelpCache));
        assertEquals(
            encodedVl64(2, null, "")
                + encodedVl64(70, null, "") + "faq-a\2"
                + encodedVl64(71, null, "") + "faq-b\2",
            faqCategoryCache.faqPayloadByCategoryId().get(7L));
        assertEquals(encodedVl64(7, null, "HJ") + '\2' + faqCategoryCache.faqPayloadByCategoryId().get(7L),
            HelpPayloads.categoryFaqs(categoryHelpCache, 7L));
        assertEquals(encodedVl64(0, null, ""), faqCategoryCache.faqPayloadByCategoryId().get(9L));
        Map<Long, String> faqDescriptions = HelpCenterBootCache.buildFaqDescriptionCache(
            List.of(new HelpDao.FaqDescriptionRow(5L, "line1\nline2")));
        assertEquals(encodedVl64(5, null, "") + "line1\rline2\2",
            faqDescriptions.get(5L));
        HelpCenterCache descriptionHelpCache = HelpCenterCache.fromPayloads("", "", Map.of(), faqDescriptions);
        assertEquals("HH" + encodedVl64(5, null, "") + "line1\rline2\2",
            HelpPayloads.description(descriptionHelpCache, 5L));
        assertEquals(7L, HelpWire.categoryFaqRequest("Fd" + wireLong(7), "Fd").categoryId());
        assertEquals("hotel''help", HelpWire.faqSearchRequest("Fc" + wireString("hotel'help"), "Fc").searchText());
        assertEquals(5L, HelpWire.faqIdRequest("Fb" + wireLong(5), "Fb").faqId());
        assertEquals(encodedVl64(2, null, "HI")
            + encodedVl64(70, null, "") + "faq-a\2"
            + encodedVl64(71, null, "") + "faq-b\2",
            HelpPayloads.searchResults(List.of(new HelpDao.FaqNameRow(70L, "faq-a"), new HelpDao.FaqNameRow(71L, "faq-b"))));
        AdvertisingBootCache.VisitRoomCache visitRoomCache = AdvertisingBootCache.buildAdvertisementVisitRoomCache(
            List.of(
                new AdvertisingDao.VisitRoomAdRow(2L, "/lobby"),
                new AdvertisingDao.VisitRoomAdRow(4L, "/cafe")),
            "/ad/");
        assertEquals(2L, visitRoomCache.count());
        assertEquals("/ad/4\2/cafe\2", visitRoomCache.payloadByVisitRoomId().get(4L));
        AdvertisingBootCache.VisitRoomCache typedVisitRoomCache = AdvertisingBootCache.buildAdvertisementVisitRoomCache(
            List.of(new AdvertisingDao.VisitRoomAdRow(4L, "/cafe")), "/ad/");
        assertEquals(1L, typedVisitRoomCache.count());
        assertEquals("/ad/4\2/cafe\2", typedVisitRoomCache.payloadByVisitRoomId().get(4L));
        RoomDao recommendedRoomDao = new RoomDao(new Database() {
            @Override
            public void execute(String sqlText) {
            }

            @Override
            public List<List<Object>> query(String sqlText) {
                return List.of(Arrays.asList(
                    2L, 1L, 5L, "caption", "caption-2", "caption-3", null,
                    77L, "room", "owner", 0L, 4L, 25L, "description", 1L, null,
                    8L, 9L, "icon", "tag-a", "tag-b", 1L, null, null, null, 3L, 44L));
            }
        });
        RoomDao.RecommendedRoomRow recommendedRoomRow = recommendedRoomDao.recommendedRoomRows(3L).get(0);
        assertEquals(77L, recommendedRoomRow.roomId());
        assertEquals("room", recommendedRoomRow.roomName());
        assertEquals(3L, recommendedRoomRow.treeId());
        RecommendedRooms typedRecommendedRooms = RecommendedRooms.fromPayloads(Map.of(0L, "REC"), 1L);
        assertEquals("REC", typedRecommendedRooms.payload(1L));
        assertEquals(Map.of(0L, "REC"), typedRecommendedRooms.payloadsByIndex());
        NavigatorState.instance().setRecommendedRooms(Map.of(0L, "SET_REC"), 1L);
        assertEquals(1L, NavigatorState.instance().recommendedRooms().count());
        assertEquals("SET_REC", NavigatorState.instance().recommendedRooms().payload(1L));
        assertRecommendedRoomsPayloadMapBridge();
        RecommendedRooms previousRecommendedRooms = NavigatorState.instance().recommendedRooms();
        NavigatorState.instance().setRecommendedRooms(typedRecommendedRooms);
        assertEquals("REC", NavigatorState.instance().recommendedRooms().payload(1L));
        NavigatorState.instance().setRecommendedRooms(previousRecommendedRooms);
        assertRecommendedRoomsPayloadBuilders();
        assertEquals("i", CatalogPageBootCache.catalogProductClass(9));
        assertEquals("s", CatalogPageBootCache.catalogProductClass(0));
        assertEquals(false, CatalogPageBootCache.catalogTextFieldPresent("NULL"));
        assertCatalogPagePayloadBuilder();
        assertCatalogPagePayloadMapBridge();
        assertEquals(true, CatalogPageBootCache.catalogPageVisible(
            new CatalogDao.CatalogPageTreeRow(1L, "name", 1L, 2L, 0L, 1L), 0, 0));
        AppConfigState.instance().setPermissionMatrix(permissions(
            new PermissionMatrix.PermissionPayload(1L, 0L, "\2fuse_developer\2")));
        assertEquals(true, CatalogPageBootCache.catalogPageVisible(
            new CatalogDao.CatalogPageTreeRow(1L, "name", 1L, 2L, 1L, 1L), 1, 0));
        CatalogDao.CatalogPageTreeRow rootPage = new CatalogDao.CatalogPageTreeRow(10L, "Root", 5L, 6L, 0L, 1L);
        CatalogDao.CatalogPageTreeRow childPage = new CatalogDao.CatalogPageTreeRow(11L, "Child A", 1L, 2L, 0L, 1L);
        CatalogDao.CatalogPageTreeRow hiddenRootPage = new CatalogDao.CatalogPageTreeRow(20L, "Hidden", 1L, 2L, 0L, 0L);
        Map<Long, Long> catalogChildCounts = new HashMap<>();
        catalogChildCounts.put(10L, 2L);
        String expectedCatalogTree = encodedVl64(1, null, "")
            + "0"
            + encodedVl64(10, null, "")
            + encodedVl64(5, null, "")
            + encodedVl64(6, null, "")
            + encodedVl64(1, null, "")
            + "Root\2"
            + encodedVl64(2, null, "")
            + "0"
            + encodedVl64(11, null, "")
            + encodedVl64(1, null, "")
            + encodedVl64(2, null, "")
            + encodedVl64(1, null, "")
            + "Child A\2"
            + encodedVl64(0, null, "");
        Map<Long, List<CatalogDao.CatalogPageTreeRow>> typedCatalogChildren = new HashMap<>();
        typedCatalogChildren.put(10L, List.of(
            childPage,
            new CatalogDao.CatalogPageTreeRow(12L, "Child B", 1L, 3L, 0L, 0L)));
        assertEquals(expectedCatalogTree, CatalogPageBootCache.buildCatalogPageTreePayload(
            List.of(rootPage, hiddenRootPage),
            catalogChildCounts, typedCatalogChildren, 1, 0));
        });
        run(() -> {
        AppConfigState.instance().setSettingsCache(settings(
            "com.system.format.date", "d.m.Y",
            "com.system.format.time", "h:i:s",
            "com.client.catalog.gifts.wrap.count.accessories", "2",
            "com.client.catalog.gifts.wrap.count.colors", "3",
            "com.client.navigator.categories.default.private.id", "1",
            "com.client.navigator.categories.default.public.id", "2",
            "com.server.socket.game.advertisement.visitrooms.path", "/ad/",
            "com.client.messenger.maxfriends.hclevel0", "50",
            "com.client.messenger.maxfriends.hclevel1", "75",
            "com.client.messenger.maxfriends.hclevel2", "100"));
        MySQL.configureDatabaseConnection(new Database() {
            @Override
            public void execute(String sqlText) {
            }

            @Override
            public List<List<Object>> query(String sqlText) {
                if (sqlText.contains("settings_recycler.chance")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(80));
                }
                if (sqlText.contains("settings_recycler WHERE chance='80'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(10), Arrays.<Object>asList(11));
                }
                if (sqlText.contains("settings_achievements")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(7, "ACH_ONE", 10, 2, 3, 4, 1));
                }
                if (sqlText.contains("settings_gesture")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(":-)", 5));
                }
                if (sqlText.contains("settings_filter")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("badword"));
                }
                if (sqlText.contains("settings_petraces")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("pet_dog", 1, 2, 3, 4, "Dog"));
                }
                if (sqlText.contains("MAX(id_level)")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(2));
                }
                if (sqlText.contains("bots_petlevels ORDER")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(2, 20, 30, 40));
                }
                if (sqlText.contains("COUNT(id_command)")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(1));
                }
                if (sqlText.contains("MAX(id_command)")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(2));
                }
                if (sqlText.contains("bots_petcommands")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(2, 3, "jump", "move"));
                }
                if (sqlText.contains("locales WHERE category='2'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("roomevent_type_5", "party"));
                }
                if (sqlText.equals("SELECT variable,value FROM settings")) {
                    return Arrays.<List<Object>>asList(
                        Arrays.<Object>asList("com.system.format.date", "d.m.Y"),
                        Arrays.<Object>asList("com.system.format.time", "h:i:s"),
                        Arrays.<Object>asList("com.server.socket.game.advertisement.visitrooms.path", "/ad/"),
                        Arrays.<Object>asList("com.client.messenger.maxfriends.hclevel0", 50),
                        Arrays.<Object>asList("com.client.messenger.maxfriends.hclevel1", 75),
                        Arrays.<Object>asList("com.client.messenger.maxfriends.hclevel2", 100));
                }
                if (sqlText.contains("level_privileges")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("fuse_mod"), Arrays.<Object>asList("fuse_chatlog"));
                }
                if (sqlText.contains("rooms_categories WHERE id_parent='1'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(1, "public", 0, 0, 0));
                }
                if (sqlText.contains("present_wrap")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(10), Arrays.<Object>asList(12));
                }
                if (sqlText.contains("faq WHERE is_important='1'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(5, "important"));
                }
                if (sqlText.contains("faq WHERE is_important='2'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(6, "also"));
                }
                if (sqlText.contains("MAX(id) FROM faq_categories")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(7));
                }
                if (sqlText.equals("SELECT id,name FROM faq_categories")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(7, "cat"));
                }
                if (sqlText.contains("faq WHERE id_category='7'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(5, "faq"));
                }
                if (sqlText.contains("MAX(id) FROM faq")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(5));
                }
                if (sqlText.equals("SELECT id,description FROM faq")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(5, "line1\nline2"));
                }
                if (sqlText.contains("MAX(id) FROM advertisement_visitrooms")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(4));
                }
                if (sqlText.equals("SELECT id,address FROM advertisement_visitrooms")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(4, "/cafe"));
                }
                return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
            }
        });
        RecyclerBootCache.loadRecyclerRewardsCache();
        assertEquals(1, RecyclerState.instance().settings().rewardGroups().size());
        assertEquals(80L, RecyclerState.instance().settings().rewardGroups().get(0).chance());
        assertEquals(List.of(10L, 11L), RecyclerState.instance().settings().rewardGroups().get(0).productIds());
        assertEquals(true, CatalogState.instance().registry().product(10L).isPresent());
        assertEquals(true, CatalogState.instance().registry().catalogProduct(1L).isPresent());
        assertEquals(true, CatalogState.instance().registry().productDeal(6L).isPresent());
        seedCatalogPackageRows(List.of(new PackageDao.PackageRow(10L, "i", 20L, "")));
        assertEquals(List.of(new PackageDao.PackageRow(10L, "i", 20L, "")),
            CatalogState.instance().productSettings().packages());
        seedCatalogPetPackageRows(List.of(new PackageDao.PetPackageRow(7L, 8L, 9L, "ffeeaa")));
        assertEquals(List.of(new PackageDao.PetPackageRow(7L, 8L, 9L, "ffeeaa")),
            CatalogState.instance().productSettings().petPackages());
        seedCatalogClubProductRows(List.of(new ClubDao.ContainedClubProductRow(33L, 2L, 1L)));
        assertEquals(true, CatalogState.instance().productSettings().containsClubProduct(33L));
        assertEquals(List.of(new CatalogProductSettings.ClubProductSetting(33L, 2L, 1L, 3)),
            CatalogState.instance().productSettings().clubProducts());
        CatalogProductSettings typedCatalogProducts = CatalogProductSettings.fromRows(
            List.of(1L, 2L), 10L, 11L,
            List.of(new PackageDao.PackageRow(10L, "i", 20L, "")),
            List.of(new PackageDao.PetPackageRow(7L, 8L, 9L, "ffeeaa")),
            List.of(new ClubDao.ContainedClubProductRow(33L, 2L, 1L)));
        assertEquals(List.of(new PackageDao.PackageRow(10L, "i", 20L, "")), typedCatalogProducts.packages());
        assertEquals(List.of(new PackageDao.PetPackageRow(7L, 8L, 9L, "ffeeaa")), typedCatalogProducts.petPackages());
        assertEquals(List.of(new CatalogProductSettings.ClubProductSetting(33L, 2L, 1L, 3)),
            typedCatalogProducts.clubProducts());
        assertCatalogProductCounterRows(typedCatalogProducts);
        CatalogProductSettings typedSettingProducts = CatalogProductSettings.fromRows(
            List.of(1L, 0L, 2L), 12L, 13L,
            List.of(new PackageDao.PackageRow(12L, "s", 21L, "extra")),
            List.of(new PackageDao.PetPackageRow(8L, 9L, 10L, "aabbcc")),
            List.of(new ClubDao.ContainedClubProductRow(34L, 3L, 2L)));
        assertEquals(List.of(1L, 2L), typedSettingProducts.counterProducts());
        assertEquals(List.of(new PackageDao.PackageRow(12L, "s", 21L, "extra")),
            typedSettingProducts.packages());
        assertEquals(List.of(new PackageDao.PetPackageRow(8L, 9L, 10L, "aabbcc")),
            typedSettingProducts.petPackages());
        assertEquals(List.of(new CatalogProductSettings.ClubProductSetting(34L, 3L, 2L, 3)),
            typedSettingProducts.clubProducts());
        assertEquals(List.of(), CatalogProductSettings.fromRows(
            List.of(), 0L, 0L, List.of(), List.of(), List.of()).clubProducts());
        CatalogProductSettings previousCatalogProductSettings = CatalogState.instance().productSettings();
        CatalogState.instance().setProductSettings(CatalogProductSettings.fromSettings(
            List.of(1L, 2L),
            10L,
            11L,
            List.of(new PackageDao.PackageRow(10L, "i", 20L, "")),
            List.of(new PackageDao.PetPackageRow(7L, 8L, 9L, "ffeeaa")),
            List.of(new CatalogProductSettings.ClubProductSetting(33L, 0L, 0L, 1))));
        CatalogProductSettings mirroredCatalogProducts = CatalogState.instance().productSettings();
        assertEquals(List.of(new PackageDao.PackageRow(10L, "i", 20L, "")), mirroredCatalogProducts.packages());
        assertEquals(List.of(new PackageDao.PetPackageRow(7L, 8L, 9L, "ffeeaa")), mirroredCatalogProducts.petPackages());
        assertEquals(true, mirroredCatalogProducts.containsClubProduct(33L));
        assertEquals(List.of(new CatalogProductSettings.ClubProductSetting(33L, 0L, 0L, 1)),
            mirroredCatalogProducts.clubProducts());
        CatalogState.instance().setProductSettings(previousCatalogProductSettings);
        assertEquals("AD" + encodedVl64(2, null, ""), CatalogPayloads.purchaseError(2));
        assertEquals(encodedVl64(1, null, encodedVl64(81, null, "In")) + '\2',
            CatalogPayloads.giftAvailability(81, 1));
        GiftSettings giftWrapSettings = GiftSettings.fromRows("", List.of(), List.of(), "WRAP");
        assertEquals(encodedVl64(7, null, "") + "WRAP", CatalogPayloads.giftWrapOptions(7, giftWrapSettings));
        assertEquals("0" + encodedVl64(1, null, "Il"), CatalogPayloads.giftWrapPriceFallback(1));
        CatalogPages pagePayloads = CatalogPages.fromPayloadMaps(Map.of(2L, "PAGE"), Map.of());
        assertEquals("A\u007f" + encodedVl64(2, null, "") + "PAGE", CatalogPayloads.page(pagePayloads, 2));
        CatalogWire.ProductPurchaseRequest purchaseRequest =
            CatalogWire.productPurchaseRequest("Ad" + wireLong(81) + wireString("catalog sign"));
        assertEquals(81L, purchaseRequest.catalogProductId());
        assertEquals("catalog sign", purchaseRequest.signText());
        assertEquals("trade_sprite", CatalogWire.clubGiftClaimRequest("G[" + wireString("trade_sprite")).requestedSprite());
        assertEquals("ABCD0000", VoucherWire.redeemRequest("BAABCD    ").voucherCode());
        assertEquals("ABCD0000", VoucherWire.redeemRequest("BA" + wireString("ABCD    ")).voucherCode());
        CatalogWire.GiftPurchaseRequest giftPurchaseRequest = CatalogWire.giftPurchaseRequest("GX" + wireLong(81)
            + wireLong(506) + wireString("Friend") + wireString("Happy birthday") + wireLong(10) + wireLong(2)
            + wireLong(3));
        assertEquals(81L, giftPurchaseRequest.catalogProductId());
        assertEquals(506L, giftPurchaseRequest.expectedProductId());
        assertEquals("Friend", giftPurchaseRequest.recipientName());
        assertEquals("Happy birthday", giftPurchaseRequest.giftMessage());
        assertEquals(10L, giftPurchaseRequest.wrapProductId());
        assertEquals(2L, giftPurchaseRequest.ribbonId());
        assertEquals(3L, giftPurchaseRequest.colorId());
        assertEquals(81L, CatalogWire.giftAvailabilityRequest("oV" + wireLong(81)).itemId());
        assertEquals(2L, CatalogWire.pageRequest("xx" + wireLong(2)).pageId());
        String expectedCatalogPurchase = encodedVl64(81, null, "AC");
        expectedCatalogPurchase = encodedVl64(3, null, expectedCatalogPurchase);
        expectedCatalogPurchase = encodedVl64(2, null, expectedCatalogPurchase);
        expectedCatalogPurchase = encodedVl64(0, null, expectedCatalogPurchase);
        expectedCatalogPurchase = encodedVl64(97, null, expectedCatalogPurchase) + '\2' + "i" + '\2' + "IHH";
        assertEquals(expectedCatalogPurchase, CatalogPayloads.purchase(81, 3, 2, 0, 97, "i"));
        String expectedClubGiftClaim = encodedVl64(506, null, "AC") + "DATA" + '\2' + "HHHII" + '\2';
        expectedClubGiftClaim = encodedVl64(97, null, expectedClubGiftClaim) + '\2' + "IH";
        assertEquals(expectedClubGiftClaim, CatalogPayloads.clubGiftClaim(506, "DATA", "I", 97));
        CatalogRegistry.CatalogProduct giftCatalogProduct = new CatalogRegistry.CatalogProduct(
            81L, "chair", 506L, 2L, "s", 1L, "", 10L, 2L, 0L, 1L, 0L, 0L);
        String expectedGiftPurchase = encodedVl64(81, null, "AC") + "81" + '\2';
        expectedGiftPurchase = encodedVl64(10, null, expectedGiftPurchase);
        expectedGiftPurchase = encodedVl64(2, null, expectedGiftPurchase);
        expectedGiftPurchase = encodedVl64(0, null, expectedGiftPurchase);
        expectedGiftPurchase = encodedVl64(97, null, expectedGiftPurchase) + '\2' + "i" + '\2' + "IH";
        assertEquals(expectedGiftPurchase, CatalogPayloads.giftPurchase(giftCatalogProduct, 10, 2, 0, 97));
        assertEquals("GM" + encodedVl64(97, null, "") + encodedVl64(12, null, ""),
            CatalogPayloads.dimensionMap(97, 12));
        assertEquals("CUABCD0000\2", VoucherPayloads.invalid("ABCD0000"));
        ProductCache voucherProductCache = ProductCache.fromProductRows(
            List.of(productCacheRow(55, "13", "RewardA", "14", "RewardB")));
        assertEquals("CTRewardA\2RewardB\2", VoucherPayloads.redeemed(voucherProductCache, 55L));
        CatalogState.instance().setGiftSettings(GiftSettings.fromRows(
            "GIFTS",
            List.of(new GiftSettings.ClubGift(81L, 506L, 20L)),
            CatalogState.instance().giftSettings().giftWrapProductIds(),
            CatalogState.instance().giftSettings().giftWrapPayload()));
        assertEquals("GIFTS", CatalogState.instance().giftSettings().clubGiftPayload());
        assertEquals(506L, CatalogState.instance().giftSettings().clubGiftByCatalogProductId(81L).productId());
        GiftSettings previousGiftSettingsForTypedFixture = CatalogState.instance().giftSettings();
        CatalogState.instance().setGiftSettings(GiftSettings.fromRows("GIFTS",
            List.of(new GiftSettings.ClubGift(82L, 507L, 30L)),
            List.of(501L, 502L),
            "WRAPS"));
        GiftSettings typedGiftSettingsFixture = CatalogState.instance().giftSettings();
        assertGiftSettingsTypedAccessors(typedGiftSettingsFixture);
        CatalogState.instance().setGiftSettings(previousGiftSettingsForTypedFixture);
        assertEquals("IoM" + encodedVl64(4, null, "")
                + "GIFTS"
                + encodedVl64(1, null, "")
                + encodedVl64(81, null, "")
                + encodedVl64(506, null, "")
                + encodedVl64(20, null, "")
                + encodedVl64(1, null, "")
                + "H",
            ClubPayloads.clubGiftStatus(
                CatalogState.instance().giftSettings(),
                new ClubDao.ClubGiftStatus(2L, 10L, 70L, 4L, 8L)));
        assertEquals("Iq" + encodedVl64(1, null, "")
                + encodedVl64(2, null, "") + "club_vip\2"
                + encodedVl64(3, null, "")
                + encodedVl64(93, null, "")
                + encodedVl64(2, null, "")
                + encodedVl64(60, null, "")
                + encodedVl64(0, null, "")
                + encodedVl64(2, null, "")
                + encodedVl64(62, null, "")
                + encodedVl64(3, null, "")
                + encodedVl64(4, null, ""),
            ClubPayloads.subscriptionOffers(
                List.of(new ClubDao.ClubProductRow(2L, "club_vip", 3L, 2L, 60L)),
                new ClubDao.UserClubStatus(2L, 10L, 70L, 1L, 3L, 4L, 8L)));
        PetBootCache.loadPetRaceCache();
        assertEquals(new PetRaceCacheRow("pet_dog", 1L, 2L, 3L, 4L, "Dog"),
            PetState.instance().settings().races().get(0));
        PetBootCache.loadPetLevelAndCommandCache();
        assertEquals(new PetSettings.PetLevelRow(2L, 20L, 30L, 40L, 4),
            PetState.instance().settings().levels().get(0));
        PetSettings.PetCommandRow cachedCommand = PetState.instance().settings().commands().get(0);
        assertEquals(2L, cachedCommand.commandId());
        assertEquals(3L, cachedCommand.requiredLevel());
        assertEquals("jump", cachedCommand.command());
        assertEquals("move", cachedCommand.action());
        assertEquals(cachedCommand, PetState.instance().settings().commands().get(0));
        PetSettings typedPetSettings = PetSettings.fromRaceRows(
            List.of(),
            List.of(new PetSettings.PetLevelRow(2L, 20L, 30L, 40L, 3)),
            List.of(cachedCommand),
            1L);
        assertEquals(List.of(), typedPetSettings.races());
        assertEquals(List.of(new PetSettings.PetLevelRow(2L, 20L, 30L, 40L, 3)), typedPetSettings.levels());
        assertEquals(List.of(cachedCommand), typedPetSettings.commands());
        assertPetSettingsTypedAccessors(typedPetSettings, cachedCommand);
        PetSettings cachePetSettings = PetSettings.fromRaceRows(
            List.of(new PetRaceCacheRow("pet_cache", 3L, 4L, 5L, 6L, "Cache")),
            List.of(new PetSettings.PetLevelRow(3L, 30L, 40L, 50L, 4)),
            List.of(cachedCommand),
            1L);
        assertEquals("pet_cache", cachePetSettings.races().get(0).productPet());
        assertEquals(new PetSettings.PetLevelRow(3L, 30L, 40L, 50L, 4),
            cachePetSettings.levels().get(0));
        assertEquals(List.of(cachedCommand), cachePetSettings.commands());
        PetSettings typedPetRaceSettings = PetSettings.fromRaceRows(
            List.of(new PetRaceCacheRow("pet_typed", 9L, 8L, 7L, 6L, "Typed")),
            List.of(), List.of(), 0L);
        assertEquals("pet_typed", typedPetRaceSettings.races().get(0).productPet());
        PetState.instance().setSettings(typedPetSettings);
        assertPetSettingsTypedAccessors(PetState.instance().settings(), cachedCommand);
        RoomEventBootCache.loadRoomEventLocalesCache();
        assertEquals("party", GameDataCaches.roomEventLocales().categoryName(5));
        AppSettingsBootCache.loadServerSettingsCache();
        assertRoomPortalSettingsBootRows(RoomState.instance().portalSettings());
        RoomPortalSettings typedRoomPortalSettings = RoomPortalSettings.fromRows(
            List.of(new RoomDao.WarpSpaceRow(12L, 1L, 2L, 34L, 5L, 6L, 1L)),
            List.of(new RoomDao.SpecialGateRow(12L, 1L)));
        assertRoomPortalSettingsTypedRows(typedRoomPortalSettings);
        RoomPortalSettings previousRoomPortalSettings = RoomState.instance().portalSettings();
        RoomState.instance().setPortalSettings(typedRoomPortalSettings);
        RoomPortalSettings mirroredRoomPortalSettings = RoomState.instance().portalSettings();
        assertRoomPortalSettingsTypedRows(mirroredRoomPortalSettings);
        RoomState.instance().setPortalSettings(previousRoomPortalSettings);
        assertEquals("%H:%i:%s", AppConfigState.instance().settingsCache().value("com.mysql.format.time"));
        assertRoomCategoryBootCaches();
        CatalogGiftBootCache.loadGiftWrapCache();
        assertEquals(true, CatalogState.instance().giftSettings().giftWrapPayload().length() > 0);
        assertEquals(true, CatalogState.instance().giftSettings().containsGiftWrapProduct(10L));
        AppSettingsBootCache.loadPermissionMatrixCache();
        assertEquals(true, AppConfigState.instance().permissionMatrix().allows(1, "", "fuse_mod", 0));
        HelpCenterBootCache.loadImportantFaqCache();
        assertEquals(true, HelpCenterState.instance().cache().importantFaqPayload().contains("important"));
        HelpCenterBootCache.loadFaqCategoryCache();
        assertEquals(true, HelpCenterState.instance().cache().categoryPayload().contains("cat"));
        assertEquals(true, HelpCenterState.instance().cache().categoryPayload().contains("cat"));
        HelpCenterBootCache.loadFaqDescriptionCache();
        assertEquals(true, HelpCenterState.instance().cache().descriptionPayload(5L).contains("line1\rline2"));
        assertHelpCenterPayloadMaps();
        AdvertisingBootCache.loadVisitRoomAdsCache();
        assertEquals("/ad/4\2/cafe\2", AdvertisingState.instance().visitRoomAds().payload(4L));
        VisitRoomAds typedVisitRoomAds = VisitRoomAds.fromPayloads(Map.of(8L, "/ad/8\2/lounge\2"), 1L);
        assertEquals("/ad/8\2/lounge\2", typedVisitRoomAds.payload(8L));
        assertEquals(Map.of(8L, "/ad/8\2/lounge\2"), typedVisitRoomAds.payloadsById());
        AdvertisingState.instance().setVisitRoomAds(typedVisitRoomAds);
        assertEquals("/ad/8\2/lounge\2", AdvertisingState.instance().visitRoomAds().payload(8L));
        BootLog.logBootLine("booted", "DEBUG");
        AchievementBootCache.loadBonusSystemCache();
        assertEquals("7\2", AchievementState.instance().settings().questIdPayload());
        assertEquals("ACH_ONE", AchievementState.instance().settings().achievementByIndex(0L).badgePrefix());
        AchievementSettings typedAchievementSettings = AchievementSettings.fromAchievements("42\2",
            List.of(new AchievementSettings.Achievement(42L, "ACH_TYPED", 1L, 2L, 3L, 4L, 5L)));
        AchievementState.instance().setSettings(typedAchievementSettings);
        assertEquals("42\2", AchievementState.instance().settings().questIdPayload());
        assertEquals("ACH_TYPED", AchievementState.instance().settings().achievementByIndex(0L).badgePrefix());
        assertEquals(new ChatSettings.Gesture(":-)", 5L), ChatState.instance().settings().gestures().get(0));
        assertEquals(new ChatSettings.FilterWord("badword"), ChatState.instance().settings().filterWords().get(0));
        assertChatSettingsTypedAccessors(ChatState.instance().settings());
        assertEquals(75L, MessengerState.instance().settings().maxFriends(2));
        MySQL.configureDatabaseConnection(null);

        });
        run(() -> {
        SessionState.instance().setGameServerSession(GameServerSessionState.empty());
        assertEquals(new GameServerBridge.GameServerPacket("DATA", 7L, "A\2B\2C"),
            GameServerBridge.gameServerPacket("DATA\2" + "7\2A\2B\2C"));
        GameServerBridge.appendGameServerPacketPayload(8, "direct");
        assertEquals("direct", GameServerBridge.popGameServerPacketData(8));
        GameServerBridge.appendGameServerPacketPayload(7, "A\2B\2C");
        assertEquals("A\2B\2C", GameServerBridge.popGameServerPacketData(7));
        assertEquals(List.of(), SessionState.instance().gameServerSession().queuedPackets());
        GameServerSessionState typedGameSession = GameServerSessionState.fromState(
            List.of(new GameServerSessionState.QueuedPacket(12L, "typed-packet")),
            Set.of(12L)
        );
        assertEquals(List.of(new GameServerSessionState.QueuedPacket(12L, "typed-packet")),
            typedGameSession.queuedPackets());
        assertEquals(Set.of(12L), typedGameSession.readySocketIndexes());
        assertEquals("[12:typed-packet]", typedGameSession.queuedPacketData());
        assertTypedGameServerSessionState(typedGameSession);
        Guardian.clearSocketMarkers();
        GameServerBridge.processGameServerData("DATA\2" + "7\2queued\2packet\1LISTEN\2" + "9");
        assertEquals("queued\2packet", GameServerBridge.popGameServerPacketData(7));
        assertEquals(true, Guardian.socketMarkers().contains(9L));
        assertEquals("bcd", IdentityEncoding.shift("abc", 1));
        assertEquals("abc", IdentityEncoding.easyDecode(IdentityEncoding.shift("abc", 25)));
        assertEquals("cde", IdentityEncoding.superEasyEncode("abc"));
        assertEquals("abc", IdentityEncoding.superEasyDecode("cde"));
        String encodedIdentity = IdentityEncoding.encode("AZ");
        assertEquals("AZ", IdentityEncoding.decode(encodedIdentity, 0));
        assertEquals("hij", IdentityEncoding.shift("abc", 7));
        ServerLifecycle.runRecoveredStartupNoop();
        assertEquals("AB", IdentityEncoding.premiumDecode(2, "d" + Character.toString((char) 163) + Character.toString((char) 164)));
        assertEquals("AZ", IdentityEncoding.decode("e" + Character.toString((char) 164) + Character.toString((char) 190), 3));
        assertEquals("KEY", ServerLifecycle.productKeyFromConfig("a=b=c=d=e=f=g=KEY\r\nnext"));
        assertEquals("PRODUCT-KEY", ServerLifecycle.productKeyFromConfig("mySQL_db=alphaseries\r\nproductKey=PRODUCT-KEY\r\n"));
        assertEquals("LEGACY-KEY", ServerLifecycle.productKeyFromConfig("mySQL_db=snapshot\r\nlicence=LEGACY-KEY\r\n"));
        SessionState.instance().setGameServerSession(GameServerSessionState.fromState(
            SessionState.instance().gameServerSession().queuedPackets(),
            Set.of(3L, 7L)));
        assertEquals(true, GameServerBridge.isGameSessionReady(7));
        assertEquals(false, GameServerBridge.isGameSessionReady(8));
        Guardian.setSocketConnected(7, true);
        List<String> preSessionPackets = new ArrayList<>();
        GameServerBridge.configurePreSessionPacketSink((socketIndex, payload) -> preSessionPackets.add(socketIndex + ":" + payload));
        SessionState.instance().setGameServerSession(GameServerSessionState.fromState(
            SessionState.instance().gameServerSession().queuedPackets(),
            Set.of()));
        GameServerBridge.appendGameServerPacketPayload(7, "login-data");
        assertEquals(true, GameServerBridge.dataProcessTimer(7));
        assertEquals(Arrays.asList("7:login-data"), preSessionPackets);
        List<String> readyPacketsSent = new ArrayList<>();
        MusConnectionManager.instance().configureSink((socketIndex, payload) -> readyPacketsSent.add(socketIndex + ":" + payload));
        SessionState.instance().setGameServerSession(GameServerSessionState.fromState(
            SessionState.instance().gameServerSession().queuedPackets(),
            Set.of(7L)));
        GameServerBridge.processClientPacket(7, "<policy-file-request/>\0");
        assertEquals(1, readyPacketsSent.size());
        assertEquals(true, readyPacketsSent.get(0).startsWith("7:DATA\6" + "7\6<?xml"));
        GameServerBridge.configurePreSessionPacketSink(null);
        MusConnectionManager.instance().configureSink(null);
        Guardian.setSocketConnected(7, false);
        PetState.instance().setRepresentedBots(representedBots(Map.of(50L, "2\2bot-id\2name")));
        assertEquals(2L, PetState.instance().representedBots().record(50).roomSlot());
        assertEquals("name", PetState.instance().representedBots().record(50).name());
        String representedBotRecord = "3\2" + "501\2Guide\2hello\2speech\2responses\2"
            + "2\2" + "3\2" + "0.5\2" + "4\2" + "1 2 ff\2"
            + "3\2" + "4\2cache\2submit\2" + "1\2" + "6";
        RepresentedBotRegistry previousRepresentedBots = PetState.instance().representedBots();
        PetState.instance().setRepresentedBots(representedBots(new LinkedHashSet<>(List.of(1L, 3L)), Map.of(
            1L, representedBotRecord,
            3L, "4\2" + "601\2Helper")));
        RepresentedBotRegistry representedBots = PetState.instance().representedBots();
        assertEquals(List.of(1L, 3L), representedBots.allocatedEntityIds());
        assertEquals(2, representedBots.recordsByEntityId().size());
        assertEquals(501L, representedBots.recordsByEntityId().get(1L).botId());
        assertEquals(501L, representedBots.record(1).botId());
        assertEquals(601L, representedBots.record(3).botId());
        representedBots.storePosition(1, 5, 6, "1.0", 7);
        assertEquals(5L, representedBots.record(1).positionX());
        assertEquals("1.0", representedBots.record(1).positionZ());
        assertEquals(7L, representedBots.record(1).positionR());
        assertEquals(2L, representedBots.reserveSlot());
        assertEquals(List.of(1L, 3L, 2L), representedBots.allocatedEntityIds());
        PetState.instance().setRepresentedBots(previousRepresentedBots);
        RoomState.instance().setRepresentedRooms(RepresentedRoomCache.empty());
        RuntimeTasks.mainRepresentedRoomOccupantAdd(4, 9, 1);
        assertEquals("\1" + "4\t\1" + "9\2", RoomState.instance().representedRooms().cacheText());
        assertEquals(4L, RoomState.instance().representedRooms().roomSlot(4));
        assertEquals(List.of(9L), RoomState.instance().representedRooms().userEntityIds(4));
        RuntimeTasks.mainRepresentedRoomOccupantMove(4, 9, 1, 2, 3, 4, 1);
        assertEquals(true, RoomState.instance().representedRooms().cacheText().contains("\1" + "9\t2\t3\t4\t1\2"));
        assertEquals(1L, RoomRollers.deltaX(2));
        assertEquals(-1L, RoomRollers.deltaX(6));
        assertEquals(-1L, RoomRollers.deltaY(0));
        assertEquals(1L, RoomRollers.deltaY(4));
        assertEquals("12", RoomRollers.targetHeight("12.5", "7"));
        assertEquals("7", RoomRollers.targetHeight("", "7.9"));
        });
        run(() -> {
        final List<String> mainSql = new ArrayList<>();
        MySQL.configureDatabaseConnection(new Database() {
            @Override
            public void execute(String sqlText) {
                mainSql.add(sqlText);
            }

            @Override
            public List<List<Object>> query(String sqlText) {
                if (sqlText.contains("SELECT id FROM rooms WHERE id_slot='4'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(44));
                }
                if (sqlText.contains("SELECT id_room,sign FROM furnitures WHERE id='101'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(44, 1));
                }
                if (sqlText.contains("SELECT furnitures.id,furnitures.position_x")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(301, 1, 1, "0", 2));
                }
                if (sqlText.contains("SELECT id FROM furnitures WHERE id_room='44'")
                    && sqlText.contains("position_x='1'")
                    && sqlText.contains("position_y='1'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(302));
                }
                if (sqlText.contains("SELECT position_z FROM furnitures WHERE id_room='44'")
                    && sqlText.contains("position_x='2'")
                    && sqlText.contains("position_y='1'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("2.0"));
                }
                if (sqlText.contains("SELECT id FROM users WHERE id_socket='8'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(88));
                }
                if (sqlText.contains("logs_visitedrooms WHERE id_user='88'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(44));
                }
                if (sqlText.contains("SELECT users.id_socket FROM logs_visitedrooms,users WHERE logs_visitedrooms.id_room='44'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(8));
                }
                if (sqlText.contains("COUNT(*) FROM furnitures")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
                }
                if (sqlText.contains("COUNT(*) FROM bots")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
                }
                return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
            }
        });
        assertEquals(44L, RuntimeTasks.mainCurrentRoomIdForSlot(4));
        SessionState.instance().setRepresentedSockets(RepresentedSocketCache.fromRecords(Map.of(
            8L, new RepresentedSocketCache.RepresentedSocketRecord("user\2" + "4", 4L, false))));
        RoomState.instance().setRepresentedRooms(RepresentedRoomCache.empty());
        Guardian.setSocketConnected(8, true);
        RuntimeTasks.attachRepresentedUser(8);
        assertEquals(true, RoomState.instance().representedRooms().cacheText().contains("\1" + "4\t\1" + "8\2"));
        PetState.instance().setRepresentedBots(representedBots(Map.of(70L, "4\2bot-id\2name")));
        RuntimeTasks.attachRepresentedBot(70);
        assertEquals(true, RoomState.instance().representedRooms().cacheText().contains("\1" + "70"));
        RuntimeTasks.moveRepresentedBot(70, 1, 1, 2, 1);
        assertEquals(true, RoomState.instance().representedRooms().cacheText().contains("\1" + "70\t2\t1"));
        RuntimeTasks.moveRepresentedUser(8, 1, 1, 1, 2);
        assertEquals(true, RoomState.instance().representedRooms().cacheText().contains("\1" + "8\t1\t2"));
        RoomState.instance().setFurnitureRoomCache(FurnitureRoomCache.State.from("", "\1" + "101\2", RoomState.instance().representedRooms()));
        assertEquals(1L, RuntimeTasks.signerTimer());
        assertEquals(true, containsSql(mainSql, "UPDATE furnitures SET sign='0' WHERE id='101' LIMIT 1"));
        PetState.instance().setRepresentedBots(representedBots(new LinkedHashSet<>(List.of(70L)), Map.of(
            70L, "4\2bot-id\2name\2a\2b\2c\2" + "1\2" + "1\2x\2x\2x\2x\2x\2x\2x\2" + "1")));
        assertEquals(1L, RuntimeTasks.botsTimer());
        assertEquals(true, RuntimeTasks.walkingTimer(4) >= 1L);
        mainSql.clear();
        Guardian.setSocketMarkers(SocketMarkerSet.fromSocketIndexes(List.of(8L)));
        assertEquals(1L, RuntimeTasks.pingTimer(0));
        assertEquals(true, containsSql(mainSql, "UPDATE settings SET value=UNIX_TIMESTAMP() WHERE variable='com.server.socket.check.time'"));
        assertEquals(true, containsSql(mainSql, "UPDATE settings SET value='1' WHERE variable='com.server.socket.mostactive'"));
        mainSql.clear();
        assertEquals(1L, RuntimeTasks.rollersTimer(4));
        assertEquals(true, containsSql(mainSql, "UPDATE furnitures SET position_x='2',position_y='1',position_z='2' WHERE id='302' AND id_room='44' LIMIT 1"));
        mainSql.clear();
        assertEquals(true, ServerLifecycle.formQueryUnload());
        assertEquals(true, containsSql(mainSql, "UPDATE users SET id_socket=null,lastonline_time=UNIX_TIMESTAMP() WHERE id_socket IS NOT NULL"));
        assertEquals(true, containsSql(mainSql, "UPDATE rooms SET id_slot=null,visitors_now='0' WHERE id_slot IS NOT NULL OR visitors_now!='0'"));
        assertEquals(false, ServerLifecycle.runServer("[!] Alpha", ""));
        Path runServerRoot = Files.createTempDirectory("alphaseries-runserver");
        String oldApplicationPathForRunServer = AppPaths.applicationPath();
        AppPaths.setApplicationPath(runServerRoot.toString());
        assertEquals(true, ServerLifecycle.runServer("Alpha", "rank=2\r7:2=1"));
        assertEquals(true, Files.exists(runServerRoot.resolve("ERR.log")));
        assertEquals(true, Files.exists(runServerRoot.resolve("SLOW.log")));
        AppPaths.setApplicationPath(oldApplicationPathForRunServer);
        ServerLifecycle.StartupResult missingLifecycleStartup = ServerLifecycle.startServer(null);
        assertEquals(false, missingLifecycleStartup.success());
        assertEquals("lifecycle", missingLifecycleStartup.stage());
        ServerLifecycle.LifecycleResult failedLicenceLifecycle =
            ServerLifecycle.LifecycleResult.initialized("", "", "BAD-KEY");
        LicenceChecker.lastLicenceFailureMessage = "licence unavailable";
        LicenceChecker.configureLicenceHttpFetcher((requestUrl, action) -> "");
        ServerLifecycle.StartupResult failedLicenceStartup = ServerLifecycle.startServer(failedLicenceLifecycle);
        assertEquals(false, failedLicenceStartup.success());
        assertEquals("licence", failedLicenceStartup.stage());
        assertEquals("Das Lizenzsystem ist zurzeit nicht erreichbar. Versuch es sp\u00e4ter wieder!", failedLicenceStartup.message());
        LicenceChecker.configureLicenceHttpFetcher(null);
        assertEquals("Server Exit Suburned following error: \r\nfatal",
            ServerLifecycle.serverExitErrorMessage("fatal"));
        assertEquals("Unbekanntes Problem", ServerLifecycle.UNKNOWN_PROBLEM_MESSAGE);
        String[] mainDesignCaptions = ServerLifecycle.designCaptions();
        assertEquals("Bitte warte...", mainDesignCaptions[0]);
        assertEquals("frame :: ADDONS", mainDesignCaptions[1]);
        assertEquals("Server by Privilege", mainDesignCaptions[2]);
        assertEquals("User Voice", mainDesignCaptions[3]);
        assertEquals("Source is only avaible for the author. Please do not share this Source!", mainDesignCaptions[4]);
        assertEquals("ACCEPT 16387", GameServerBridge.gameServerUnknownEventAccept());
        assertEquals(true, Guardian.isSocketConnected(0));
        assertEquals("LISTEN", GameServerBridge.gameServerUnknownEventListen());
        Guardian.setGameServerConnected(false);
        ServerLifecycle.ResizeResult resizeResult = ServerLifecycle.formResize(1000, 1000, 800, 700);
        assertEquals(11085L, resizeResult.width());
        assertEquals(10245L, resizeResult.height());
        assertEquals(800L, resizeResult.logWidth());
        assertEquals(175L, resizeResult.logHeight());
        assertEquals("88", RuntimeTasks.mainUserIdFromSocket(8));
        Guardian.setSocketConnected(8, false);
        MySQL.configureDatabaseConnection(null);
        Path lifecycleRoot = Files.createTempDirectory("alphaseries-main-lifecycle");
        Files.createDirectories(lifecycleRoot.resolve("CACHE").resolve("ROOMS"));
        Files.createDirectories(lifecycleRoot.resolve("CACHE").resolve("PATHFINDER"));
        Files.createDirectories(lifecycleRoot.resolve("CACHE").resolve("USERS"));
        Files.writeString(lifecycleRoot.resolve("config.ini"), "a=b=c=d=e=f=g=PRODUCT-KEY\r\nrest");
        String oldApplicationPathForLifecycle = AppPaths.applicationPath();
        AppPaths.setApplicationPath(lifecycleRoot.toString());
        ServerLifecycle.LifecycleResult lifecycleResult = ServerLifecycle.formInitialize("%% [!]");
        assertEquals(true, lifecycleResult.success());
        assertEquals("ALPHASERIES_FINAL (PREMIUM)", lifecycleResult.caption());
        assertEquals("ALPHASERIES_FINAL (PREMIUM) [!]", lifecycleResult.consoleTitle());
        assertEquals("PRODUCT-KEY", lifecycleResult.productKey());
        ServerLifecycle.LifecycleResult bootTitleLifecycle =
            ServerLifecycle.formInitialize(ServerLifecycle.INITIALIZING_CAPTION_TEMPLATE);
        assertEquals("Alpha Series [INITIALISIERE] - [ALPHASERIES_FINAL (PREMIUM)]",
            bootTitleLifecycle.consoleTitle());
        assertEquals("Alpha Series [INITIALISIERT] - [ALPHASERIES_FINAL (PREMIUM)]",
            ServerLifecycle.initializedConsoleTitle(bootTitleLifecycle.consoleTitle()));
        assertEquals("Alpha Series [RUNNING] - [ALPHASERIES_FINAL (PREMIUM)]",
            ServerLifecycle.initializedConsoleTitle("Alpha Series [INITIALIZING] - [ALPHASERIES_FINAL (PREMIUM)]"));
        assertEquals(false, Files.exists(lifecycleRoot.resolve("CACHE").resolve("ROOMS")));
        assertEquals(0xFFFFFFL, LifecycleState.instance().runtimeState().primaryColor());
        assertEquals(0x17L, LifecycleState.instance().runtimeState().version());
        assertEquals("ALPHASERIES_FINAL (PREMIUM)", LifecycleState.instance().runtimeState().productName());
        AppPaths.setApplicationPath(oldApplicationPathForLifecycle);
        assertEquals(encodedVl64(3, null,
            encodedVl64(2, null,
                encodedVl64(1, null,
                    encodedVl64(99, null, "AZ")))),
            RoomPayloads.rollerMove(99, 1, 2, "3"));
        assertEquals(RoomPayloads.rollerMove(99, 1, 2, "3"),
            RoomRollers.movePayload(99, 1, 2, "3"));
        Updater updater = new Updater();
        updater.queueHeightAnimation(1000, 5);
        assertEquals(1000L, updater.pendingHeightTarget);
        assertEquals(5L, updater.pendingAnimationInterval);
        assertEquals(true, updater.timer1Enabled);
        updater.startHeightAnimationTimer();
        assertEquals(false, updater.timer1Enabled);
        assertEquals(true, updater.timer2Enabled);
        Updater.HeightStep heightStep = updater.applyHeightTimerStep(900);
        assertEquals(950L, heightStep.height());
        assertEquals(true, heightStep.timer2Enabled());
        Updater.HeightStep clampedHeightStep = Updater.heightTimerStep(990, 1000);
        assertEquals(1000L, clampedHeightStep.height());
        assertEquals(false, clampedHeightStep.timer2Enabled());
        Updater.HeightStep shrinkingHeightStep = Updater.heightTimerStep(1100, 1000);
        assertEquals(1050L, shrinkingHeightStep.height());
        assertEquals(true, shrinkingHeightStep.timer2Enabled());
        updater.queueProgressWidth(250);
        assertEquals(250L, updater.pendingProgressWidth);
        assertEquals(true, updater.walkPercentEnabled);
        Updater.ProgressStep progressStep = updater.applyProgressTimerStep(225, true);
        assertEquals(250L, progressStep.width());
        assertEquals(true, progressStep.walkPercentEnabled());
        Updater.ProgressStep completeProgressStep = Updater.progressTimerStep(11500, Updater.PROGRESS_WIDTH_MAX, false);
        assertEquals(Updater.PROGRESS_WIDTH_MAX, completeProgressStep.width());
        assertEquals(true, completeProgressStep.complete());
        assertEquals(false, completeProgressStep.walkPercentEnabled());
        updater.currentUpdateIndex = 0;
        updater.advanceUpdateProgress(4);
        assertEquals(5766L, updater.pendingProgressWidth);
        updater.applyFeatureState(UpdaterSettings.UpdateEntry.fromFields("id", "title", "body", 0L, 99L));
        assertEquals(true, updater.freeFeature.visible);
        assertEquals("Kostenlose Funktion", updater.freeFeature.caption);
        updater.applyFeatureState(UpdaterSettings.UpdateEntry.fromFields("id", "title", "body", 2L, 99L));
        assertEquals(true, updater.unfreeFeature.visible);
        assertEquals("Kostet 99 Punkte", updater.unfreeFeature.caption);
        updater.applyFeatureState(UpdaterSettings.UpdateEntry.fromFields("id", "title", "body", 1L, 0L));
        assertEquals(true, updater.downloadFeature.visible);
        UpdaterSettings.UpdateEntry updateEntry = UpdaterSettings.UpdateEntry.fromFields("x", "42", "body", 3L, 7L);
        assertEquals("42", updateEntry.title());
        assertEquals(3L, updateEntry.featureMode());
        assertEquals(7L, updateEntry.featureCost());
        UpdaterSettings previousUpdaterSettings = UpdaterState.instance().settings();
        UpdaterState.instance().setSettings(UpdaterSettings.fromEntries("updater", List.of(
            UpdaterSettings.UpdateEntry.fromFields("a", "A", "body", 0L, 0L),
            new UpdaterSettings.UpdateEntry("", "", "", "", 0L, 0L, false)), "INSERT INTO a"));
        UpdaterSettings mirroredUpdaterSettings = UpdaterState.instance().settings();
        assertEquals(2, mirroredUpdaterSettings.entryList().size());
        assertEquals(UpdaterSettings.UpdateEntry.fromFields("a", "A", "body", 0L, 0L),
            mirroredUpdaterSettings.entryList().get(0));
        assertEquals("", mirroredUpdaterSettings.entryList().get(1).sourceText());
        assertEquals(1L, mirroredUpdaterSettings.updateCountOrOne());
        UpdaterState.instance().setSettings(previousUpdaterSettings);
        List<UpdaterSettings.UpdateEntry> typedUpdateEntries = new ArrayList<>();
        typedUpdateEntries.add(updateEntry);
        UpdaterSettings typedUpdaterSettings = UpdaterSettings.fromEntries("typed-updater", typedUpdateEntries, "");
        typedUpdateEntries.add(UpdaterSettings.UpdateEntry.fromFields("y", "title", "body", 1L, 2L));
        assertEquals("typed-updater", typedUpdaterSettings.executableName());
        assertEquals(List.of(updateEntry), typedUpdaterSettings.entryList());
        assertEquals("custom", Updater.getUpdaterExecutableName("custom", "app"));
        assertEquals("app", Updater.getUpdaterExecutableName("", "app"));
        assertEquals("INSERT IGNORE INTO a\nINSERT IGNORE INTO b", Updater.normalizedUpdateSql("INSERT INTO a\r\ninsert into b"));
        assertEquals("Downloade Updates...", Updater.FORM_CAPTION);
        assertEquals("Downloade...", Updater.DOWNLOAD_LABEL_CAPTION);
        assertEquals("CMS muss im Store erneut heruntergeladen werden", Updater.CMS_REDOWNLOAD_CAPTION);
        assertEquals("Kostenloses Feature", Updater.DEFAULT_FREE_FEATURE_CAPTION);
        assertEquals("Kostet 10 Punkte", Updater.DEFAULT_COST_FEATURE_CAPTION);
        assertEquals("Es ist ein Fehler aufgetreten. Versuche es erneut!", Updater.RETRY_ERROR_MESSAGE);
        assertEquals("Es kann keine Verbindung zur MySQL Datenbank hergestellt werden.",
            Updater.MYSQL_CONNECTION_ERROR_MESSAGE);
        assertEquals(true, Updater.successfulDownloadMessage("Alpha")
            .contains("Update erfolgreich heruntergeladen. Die Datei wurde nach \"Alpha.exe\" benannt."));
        assertEquals(true, Updater.successfulDownloadMessage("Alpha")
            .contains("Die Webseite wurde automatisch ge\u00f6ffnet."));
        assertEquals(3, Updater.visibleBodyLines("line1\\n\\nline3", 25).length);
        UpdaterState.instance().setSettings(UpdaterSettings.fromEntries("updater", List.of(
            UpdaterSettings.UpdateEntry.fromFields("1", "title", "line1\\nline2", 0L, 0L),
            UpdaterSettings.UpdateEntry.fromFields("2", "other", "body", 1L, 0L)), ""));
        updater.height = 1000;
        updater.currentUpdateIndex = 0;
        Updater.RenderStep renderStep = updater.timer3Step();
        assertEquals(true, renderStep.rendered());
        assertEquals("title", renderStep.title());
        assertEquals("line2", renderStep.bodyLines()[1]);
        assertEquals(true, updater.freeFeature.visible);
        assertEquals(11534L, updater.pendingProgressWidth);
        UpdaterState.instance().setExecutableName("custom-updater");
        assertEquals("custom-updater", UpdaterState.instance().settings().executableName());
        Updater.DownloadPlan downloadPlan = updater.downloadPlan("appname", LocalDateTime.of(2026, 6, 29, 13, 45, 6));
        assertEquals("custom-updater", downloadPlan.executableName());
        assertEquals(true, downloadPlan.destinationPath().endsWith("custom-updater.exe"));
        assertEquals(true, downloadPlan.sourceUrl().contains("/custom-updater/file.database?timestamp="));
        List<String> updaterSql = new ArrayList<>();
        MySQL.configureDatabaseConnection(new Database() {
            @Override
            public void execute(String sqlText) {
                updaterSql.add(sqlText);
            }

            @Override
            public List<List<Object>> query(String sqlText) {
                return Arrays.<List<Object>>asList(Arrays.<Object>asList(1));
            }
        });
        UpdaterState.instance().setUpdateSql("INSERT INTO a\r\ninsert into b\n");
        assertEquals("INSERT INTO a\r\ninsert into b\n", UpdaterState.instance().settings().updateSql());
        assertEquals(true, updater.formLoad(true));
        assertEquals(true, containsSql(updaterSql, "INSERT IGNORE INTO a"));
        assertEquals(true, containsSql(updaterSql, "INSERT IGNORE INTO b"));
        assertEquals(1000L, updater.height);
        assertEquals(0L, updater.imageWidth);
        assertEquals(true, updater.formUnload());
        assertEquals(true, updater.formQueryUnload());
        MySQL.configureDatabaseConnection(null);

        });
        run(() -> {
        String httpRequest = PrivSockHTTP.buildGetRequest("/path", "example.com", "8080");
        assertEquals(true, httpRequest.startsWith("GET /path HTTP/1.1\r\nHost:   example.com:8080\r\n"));
        assertEquals(true, httpRequest.contains("User-Agent:   FireFox/1.0\r\n"));
        assertEquals("", PrivSockHTTP.buildGetRequest("", "example.com", "80"));
        PrivSockHTTP.AliveState aliveState = PrivSockHTTP.AliveState.request("/alive", "example.com", "8080");
        assertEquals(true, PrivSockHTTP.tmrCheckAliveTimer(aliveState).startsWith("GET /alive HTTP/1.1"));
        assertEquals(1L, aliveState.ticks());
        aliveState.setTicks(200L);
        assertEquals("", PrivSockHTTP.tmrCheckAliveTimer(aliveState));
        assertEquals(false, aliveState.enabled());
        StartupEnvironmentError.MessageBox mistakeMessageBox = StartupEnvironmentError.loadMessage();
        assertEquals(StartupEnvironmentError.MESSAGE, mistakeMessageBox.message());
        assertEquals(StartupEnvironmentError.MessageStyle.CRITICAL, mistakeMessageBox.style());
        String[] mistakeInstructionCaptions = StartupEnvironmentError.instructionCaptions();
        assertEquals("1. Click here to customize your regional options!", mistakeInstructionCaptions[0]);
        assertEquals("2. Select the decimal symbol ,", mistakeInstructionCaptions[1]);
        assertEquals("3. Click \"OK\" to apply your changes. You need to restart your Computer/VPS",
            mistakeInstructionCaptions[2]);
        StartupEnvironmentError.QueryUnloadResult mistakeUnload = StartupEnvironmentError.queryUnload(3);
        assertEquals(false, mistakeUnload.cancel());
        assertEquals(true, mistakeUnload.exitRequested());
        assertEquals(3, mistakeUnload.unloadMode());
        assertEquals("", LicenceChecker.readHttp("", 0));

        assertEquals(encodedVl64(2, null, "") + "hd-180-1\2M\2",
            UserPayloads.wardrobeSlot(2, "hd-180-1", "M"));
        UserPayloads.WardrobePayload wardrobePayload = UserPayloads.wardrobeSlots(List.of(
            new UserDao.WardrobeSlotRow(1L, "hd-180-1", "m"),
            new UserDao.WardrobeSlotRow(6L, "ch-255-66", "x")), 5L);
        assertEquals(1L, wardrobePayload.slotCount());
        assertEquals("DK" + encodedVl64(1, null, "")
                + encodedVl64(1, null, "") + "hd-180-1\2M\2",
            wardrobePayload.payload());
        assertEquals("0@B" + encodedVl64(7, null, "")
                + encodedVl64(7, null, "")
                + encodedVl64(1, null, ""),
            UserPayloads.rankAndStaffState(7L, 1L));
        NavigatorPayloads.FavouriteRoomsPayload favouriteRoomsPayload =
            NavigatorPayloads.favouriteRoomIds(List.of(9L, 0L, 12L), 30L);
        assertEquals(2L, favouriteRoomsPayload.roomCount());
        assertEquals("GJ" + encodedVl64(30, null, "")
                + encodedVl64(2, null, "")
                + encodedVl64(9, null, "")
                + encodedVl64(12, null, ""),
            favouriteRoomsPayload.payload());
        assertEquals(true, UserValidation.isValidWardrobeFigure("hd-180-1.ch-255-66", "M"));
        assertEquals(false, UserValidation.isValidWardrobeFigure("bad-1", "M"));
        assertEquals(false, UserValidation.isValidWardrobeFigure("hd-'1", "M"));
        String figureData = "<settype type=\"hd\"><set id=\"180\" gender=\"M\"></set></settype>"
            + "<settype type=\"ch\"><set id=\"255\" gender=\"U\"/></settype>";
        assertEquals(true, UserValidation.isValidWardrobeFigure("hd-180-1.ch-255-66", "M", figureData));
        assertEquals(false, UserValidation.isValidWardrobeFigure("hd-180-1", "F", figureData));
        assertEquals(true, UserValidation.figureSetAllowsGender("<set id=\"1\" gender=\"U\"/>", "<set id=\"1\"", "F"));
        UserWire.WardrobeSlotRequest wardrobeSlotRequest =
            UserWire.wardrobeSlotRequest("Ex" + wireLong(2) + wireString("hd-180-1\nch-255-66") + wireString("m"));
        assertEquals(2L, wardrobeSlotRequest.slotId());
        assertEquals("hd-180-1 ch-255-66", wardrobeSlotRequest.figureText());
        assertEquals("M", wardrobeSlotRequest.genderText());
        UserWire.TutorialClothesRequest tutorialClothesRequest =
            UserWire.tutorialClothesRequest("@l" + wireString("f") + wireString("hd-180-1\rch-255-66"));
        assertEquals("F", tutorialClothesRequest.genderText());
        assertEquals("hd-180-1 ch-255-66", tutorialClothesRequest.figureText());
        assertEquals("NewName", UserWire.avatarNameRequest("GV" + wireString("NewName"), "GV").candidateName());
        assertEquals("CheckName", UserWire.avatarNameRequest("GW" + wireString("CheckName"), "GW").candidateName());
        assertEquals("New motto", UserWire.mottoRequest("Gd" + wireString("New motto")).mottoText());
        assertEquals(77L, UserWire.guideInviteRequest("oLB77").userId());
        assertEquals(encodedVl64(44, null, "DJ") + "hello\2M\2hd-180-1\2",
            UserPayloads.identityRefresh(44, "hello", "hd-180-1", "M"));
        assertEquals(encodedVl64(7, null,
            encodedVl64(3, null, "@Y") + "hi\2"),
            UserPayloads.representedChat(3, "hi", 7, 1));
        assertEquals(true, ChatCommands.commandPayload(":about", "").contains(
            "This is a copy of the unique Alpha Series written in Visual Basic 2006."));
        assertEquals(true, ChatCommands.commandPayload(":entwicklung", "").contains("UNIQUE ID: --"));
        assertEquals(true, ChatCommands.commandPayload(":commands", "").contains("You've following commands avaible:"));
        assertEquals(true, ChatCommands.commandPayload(":commands", "").contains(
            "Please note that some commands require additional syntax"));
        assertEquals("", ChatCommands.commandPayload(":unknown", ""));
        assertEquals("BKActive users:\r\rAlice, Bob\2\2", ChatCommands.activeUsersPayload("Alice, Bob"));
        assertEquals("BKActive users:\r\rAlice, Bob\2\2",
            ChatCommands.dynamicCommandPayload(":whosonline", SessionRegistry.fromEntries(List.of(
                new SessionRegistry.SessionRecord("1:4", "44\2" + "4"),
                new SessionRegistry.SessionRecord("1:5", "55\2" + "5")), List.of()).socketSessions(),
                userId -> "44".equals(userId) ? "Alice" : "Bob"));
        assertEquals("www.example.com;http://alpha;https://beta;",
            ChatCommands.extractUrlList("see www.example.com and http://alpha or https://beta"));
        assertEquals("", ChatCommands.extractUrlList("www bad example.com"));
        List<ChatSettings.FilterWord> filterWords = List.of(
            new ChatSettings.FilterWord("badword"),
            new ChatSettings.FilterWord("xx"));
        ChatSettings filterSettings = ChatSettings.fromRows(filterWords, List.of());
        assertEquals("hello *** and ***", filterSettings.filterText("hello badword and BADWORD", true, "***"));
        assertEquals("***", filterSettings.filterText("xx", true, "***"));
        assertEquals("xx now", ChatSettings.fromRows(List.of(new ChatSettings.FilterWord("xx")), List.of())
            .filterText("xx now", true, "***"));
        List<ChatSettings.Gesture> gestures = List.of(
            new ChatSettings.Gesture(":)", 5L),
            new ChatSettings.Gesture(":(", 6L));
        assertEquals(5L, ChatSettings.fromRows(List.of(), gestures).gestureId("hello :)", true));
        assertEquals(0L, ChatSettings.fromRows(List.of(), List.of(new ChatSettings.Gesture(":)", 5L)))
            .gestureId("hello", false));
        String complexPayload = StaffPayloads.callForHelp(1, 2, 3, 4, "four", 5, "six", "seven", 8, "nine", 10, "eleven");
        assertEquals(true, complexPayload.endsWith("eleven\2"));
        assertEquals(true, complexPayload.contains("four\2"));
        assertEquals("keep\nalso", StringUtils.removeLineRecord("keep\r\nremove-this\nalso", "remove"));
        RepresentedRoomCache representedRoomCache = RepresentedRoomCache.fromCacheText("\1" + "1\talpha\2\1" + "2\tbeta\2");
        assertEquals(List.of(
            new RepresentedRoomCache.RoomRecord(1L, "1\talpha"),
            new RepresentedRoomCache.RoomRecord(2L, "2\tbeta")), representedRoomCache.roomRecords());
        assertEquals("beta", representedRoomCache.roomRecords().get(1).fields().get(1));
        RepresentedRoomCache duplicateRoomCache = RepresentedRoomCache.fromCacheText("\1" + "1\talpha\2\1" + "1\tbeta\2");
        assertEquals(new RepresentedRoomCache.RoomRecord(1L, "1\talpha"), duplicateRoomCache.roomRecords().get(0));
        assertEquals("\1" + "1\talpha\2\1" + "1\tbeta\2", duplicateRoomCache.cacheText());
        assertEquals("\1" + "1\tgamma\2", duplicateRoomCache.setRecord(1, "1\tgamma").cacheText());
        assertEquals(List.of(new RepresentedRoomCache.RoomRecord(3L, "3")), RepresentedRoomCache.fromCacheText("\1" + "3\2").roomRecords());
        assertEquals(0L, RepresentedRoomCache.fromCacheText("\1" + "1\talpha\2").roomSlot(4));
        assertEquals("snapshot-room-cache", RepresentedRoomCache.fromCacheText("snapshot-room-cache").cacheText());
        assertEquals("snapshot-room-cache\1" + "2\tbeta\2",
            RepresentedRoomCache.fromCacheText("snapshot-room-cache").setRecord(2, "2\tbeta").cacheText());
        assertEquals(List.of(new RepresentedRoomCache.RoomRecord(7L, "7\ttyped")),
            RepresentedRoomCache.fromRecords("", List.of(new RepresentedRoomCache.RoomRecord(7L, "7\ttyped")))
                .roomRecords());
        List<Long> mutableRoomSlots = new ArrayList<>();
        mutableRoomSlots.add(5L);
        mutableRoomSlots.add(7L);
        RepresentedRoomSlots typedRoomSlots = RepresentedRoomSlots.fromSlots(mutableRoomSlots);
        mutableRoomSlots.add(9L);
        assertEquals(List.of(5L, 7L), typedRoomSlots.availableSlots());
        RepresentedRoomSlots previousRoomSlots = RoomState.instance().representedRoomSlots();
        RoomState.instance().setRepresentedRoomSlots(RepresentedRoomSlots.fromSlots(List.of(5L, 7L)));
        assertEquals(List.of(5L, 7L), RoomState.instance().representedRoomSlots().availableSlots());
        RoomState.instance().setRepresentedRoomSlots(typedRoomSlots);
        assertEquals(List.of(5L, 7L), RoomState.instance().representedRoomSlots().availableSlots());
        RoomState.instance().setRepresentedRoomSlots(previousRoomSlots);
        RepresentedRoomCache previousRepresentedRooms = RoomState.instance().representedRooms();
        RepresentedRoomCache typedRepresentedRooms = RepresentedRoomCache.fromRecords(
            "typed-prefix",
            List.of(new RepresentedRoomCache.RoomRecord(8L, "8\ttyped")));
        RoomState.instance().setRepresentedRooms(typedRepresentedRooms);
        assertEquals(List.of(new RepresentedRoomCache.RoomRecord(8L, "8\ttyped")),
            RoomState.instance().representedRooms().roomRecords());
        assertEquals("typed-prefix" + "\1" + "8\ttyped\2", RoomState.instance().representedRooms().cacheText());
        RoomState.instance().setRepresentedRooms(previousRepresentedRooms);
        String roomRecordCache = RepresentedRoomCache.fromCacheText("\1" + "1\talpha\2\1" + "2\told\2")
            .setRecord(2, "2\tnew").cacheText();
        assertEquals("\1" + "1\talpha\2\1" + "2\tnew\2", roomRecordCache);
        assertEquals("\1" + "1\talpha\2", RepresentedRoomCache.fromCacheText("\1" + "1\talpha\2")
            .setRecord(0, "0\tignored").cacheText());
        String movementCache = RepresentedRoomCache.fromCacheText("")
            .moveOccupant(4, 9, 2, 3, 4, 1).cacheText();
        assertEquals("\1" + "4\t\t\t0\t\1" + "9\t2\t3\t4\t1\2\2", movementCache);
        RepresentedRoomCache.Position movementPosition = RepresentedRoomCache.fromCacheText(movementCache).movementPosition(4, 9);
        assertEquals(true, movementPosition.found());
        assertEquals(2L, movementPosition.positionX());
        assertEquals(3L, movementPosition.positionY());
        assertEquals(false, RepresentedRoomCache.fromCacheText(movementCache).movementPosition(4, 10).found());
        RoomUserPosition argumentPosition = RoomUserPosition.fromCoordinates(8, 9);
        assertEquals(true, argumentPosition.found());
        assertEquals(8L, argumentPosition.positionX());
        assertEquals(9L, argumentPosition.positionY());
        assertEquals(false, RoomUserPosition.absent().found());
        movementCache = RepresentedRoomCache.fromCacheText(movementCache)
            .moveOccupant(4, 9, 5, 6, 2, 0).cacheText();
        assertEquals("\1" + "4\t\t\t0\t\1" + "9\t5\t6\t2\t0\2\2", movementCache);
        Path tempFile = Files.createTempFile("alphaseries4j", ".cache");
        FileUtils.writeTextFile(tempFile.toString(), "cache-data");
        assertEquals("cache-data" + System.lineSeparator(), FileUtils.readTextFile(tempFile.toString()));
        Path missingCache = Files.createTempDirectory("alphaseries4j-cache").resolve("room.cache");
        assertEquals(System.lineSeparator(), FileUtils.ensureTextFile(missingCache.toString()));
        String userEntryPayload = SocialPayloads.roomUserEntry(new RoomUserEntryPayloadArgs(
            "7", "alice", "hd-1", "motto", "F", "8", "2", "3", "1.0", "4", "5"));
        assertEquals(true, userEntryPayload.contains("alice\2hd-1"));
        assertEquals(true, userEntryPayload.contains("motto\2"));
        assertEquals(SocialPayloads.roomUserEntry(new RoomUserEntryPayloadArgs(
                "7", "alice", "hd-1", "motto", "F", "8", "2", "3", "0.0", "0", "0")),
            SocialLookups.roomUserEntryPayload(
                new RoomUserEntryRow(7L, "alice", "hd-1", "motto", "F", 2L, 3L, 4L), 8L));
        SocialRoomOccupants socialOccupants = SocialLookups.roomOccupantsPayloads(
            List.of(new RoomOccupantRow(9L, 7L, "alice", "hd-1", "motto", "x", 1L, 1L, 4L)),
            4L,
            RepresentedRoomCache.fromCacheText("").moveOccupant(4L, 9L, 2L, 3L, 4L, 1L));
        assertEquals(1L, socialOccupants.occupantCount());
        assertEquals(1L, socialOccupants.statusCount());
        assertEquals(SocialPayloads.roomUserEntry(new RoomUserEntryPayloadArgs(
                "7", "alice", "hd-1", "motto", "M", "9", "2", "3", "0.0", "0", "0")),
            socialOccupants.occupantPayload());
        assertEquals(SocialPayloads.roomOccupantStatus(9L, 2L, 3L, "0.0", 0L),
            socialOccupants.statusPayload());
        SocialRoomOccupants mergedOccupants = socialOccupants.withPetOccupants(
            new PetRoomOccupants(1L, 1L, "pet-entry", "pet-status"));
        assertEquals(2L, mergedOccupants.occupantCount());
        assertEquals(2L, mergedOccupants.statusCount());
        assertEquals(socialOccupants.occupantPayload() + "pet-entry", mergedOccupants.occupantPayload());
        assertEquals(socialOccupants.statusPayload() + "pet-status", mergedOccupants.statusPayload());
        String botPayload = SocialPayloads.roomObjectEntry(new RoomObjectEntryPayloadArgs(
            "9", "bot", "figure", "M", "10", "4", "5", "0.0", "2"));
        assertEquals(true, botPayload.startsWith("Mbot\2figure\2M\2"));
        assertEquals(true, botPayload.endsWith("0.0\2HK"));
        String petPayload = SocialPayloads.roomObjectEntry(new RoomObjectEntryPayloadArgs(
            "11", "pet", "figure", "F", "12", "6", "7", "0.5", "3"));
        assertEquals(true, petPayload.startsWith(encodedVl64(11, null, "") + "pet\2"));
        assertEquals(true, petPayload.endsWith("0.5\2PAJJ"));
        assertEquals(2L, UserValidation.avatarNameValidationCode("ab", "", 0));
        assertEquals(1L, UserValidation.avatarNameValidationCode("abcdefghijklmnop", "", 0));
        assertEquals(2L, UserValidation.avatarNameValidationCode("MOD-user", "", 0));
        assertEquals(2L, UserValidation.avatarNameValidationCode("bad name", "", 0));
        assertEquals(0L, UserValidation.avatarNameValidationCode("Alice_1", "alice_1", 1));
        assertEquals(3L, UserValidation.avatarNameValidationCode("Alice_2", "alice_1", 1));
        assertEquals(0L, UserValidation.avatarNameValidationCode("Alice_2", "alice_1", 0));
        assertEquals("1\0" + "1\0" + "3\0" + "1\0", MovementStep.between(0, 0, 2, 2).frameText());
        assertEquals("0\0" + "0\0" + "0\0" + "0\0", MovementStep.zero().frameText());
        assertEquals(3L, MovementStep.directionCode(1, 1));
        String wireStringPayload = "@Cabc";
        WireReader.Offset wireOffset = new WireReader.Offset(1);
        assertEquals("abc", WireReader.readString(wireStringPayload, wireOffset));
        assertEquals(6L, wireOffset.value());
        String wireLongPayload = encodedVl64(123, null, "") + "tail";
        WireReader.Offset longOffset = new WireReader.Offset(1);
        assertEquals(123L, WireReader.readLong(wireLongPayload, longOffset));
        assertEquals(3L, longOffset.value());
        FurnitureWire.StickyNoteUpdate note = FurnitureWire.stickyNoteUpdate("ATA5" + "9CFF9Chello\nworld");
        assertEquals(5L, note.furnitureId());
        assertEquals("9CFF9C", note.noteColor());
        assertEquals("world", note.noteCaption());
        assertEquals(5L, FurnitureWire.stickyFurnitureId(wireLong(5)));
        assertEquals(70L, FurnitureWire.stickyFurnitureId("AS" + wireLong(70)));
        assertEquals(71L, FurnitureWire.stickyFurnitureId("AN" + wireLong(71)));
        assertEquals(72L, FurnitureWire.stickyFurnitureId("FI" + wireLong(72)));
        assertEquals(79L, FurnitureWire.stickyFurnitureId("AB" + wireLong(79)));
        assertEquals(85L, FurnitureWire.stickyFurnitureId("AC" + wireLong(85)));
        assertEquals(86L, FurnitureWire.stickyFurnitureRequest("AN" + wireLong(86)).furnitureId());
        String wallPlacementWire = "B88" + ":w=1,2 l=3,4";
        FurnitureWire.WallFurniturePlacementRequest wallPlacementRequest =
            FurnitureWire.wallFurniturePlacementRequest("rv" + wallPlacementWire);
        assertEquals(88L, wallPlacementRequest.furnitureId());
        assertEquals(wallPlacementWire, wallPlacementRequest.wallPayload());
        assertEquals(true, FurnitureWire.isStickyNoteColor("ffff33"));
        assertEquals(false, FurnitureWire.isStickyNoteColor("ffffff"));
        assertEquals(true, FurnitureWire.isDimmerColour("#82f349"));
        assertEquals(false, FurnitureWire.isDimmerColour("#ffffff"));
        FurnitureWire.DimmerPresetRequest dimmerPresetRequest =
            FurnitureWire.dimmerPresetRequest("EV" + wireLong(2) + wireLong(1) + wireString("#82f349") + wireLong(100));
        assertEquals(2L, dimmerPresetRequest.presetId());
        assertEquals(1L, dimmerPresetRequest.backgroundId());
        assertEquals("#82F349", dimmerPresetRequest.colourText());
        assertEquals(100L, dimmerPresetRequest.lightLevel());
        FurniturePayloads.DimmerPresetPayload dimmerPresetPayload = FurniturePayloads.dimmerPresets(List.of(
            new FurnitureDao.DimmerPreset(150L, 1L, 1L, "#0053F7", 1L),
            new FurnitureDao.DimmerPreset(100L, 2L, 1L, "#82F349", 2L)));
        assertEquals(2L, dimmerPresetPayload.currentPresetId());
        assertEquals("Em" + encodedVl64(0, null, "")
                + encodedVl64(2, null, "")
                + encodedVl64(1, null, "")
                + encodedVl64(1, null, "")
                + encodedVl64(150, null, "") + "#0053F7\2"
                + encodedVl64(2, null, "")
                + encodedVl64(1, null, "")
                + encodedVl64(100, null, "") + "#82F349\2",
            dimmerPresetPayload.payload());
        assertEquals("AU78\2" + encodedVl64(501, null, "") + ":w=1,2 l=3,4\2"
                + "2,1,1,#82F349,100\2",
            FurniturePayloads.wallState(78, 501, ":w=1,2 l=3,4", "2,1,1,#82F349,100"));
        assertEquals("AU78\2" + encodedVl64(501, null, "") + "1\2" + "0\2",
            FurniturePayloads.wallState(78, 501, "1", "0"));
        assertEquals("AT77\1AS77\2" + encodedVl64(500, null, "") + "500\2FFFF33\2",
            FurniturePayloads.stickyNoteUpdated(77L, 500L, "FFFF33"));
        UserPayloads.EffectListPayload effectListPayload = UserPayloads.effectList(List.of(
            new UserEffectSummaryRow(12L, 3600L, 2L, 1000L, 900L),
            new UserEffectSummaryRow(13L, 120L, 1L, 0L, 900L)));
        assertEquals(2L, effectListPayload.listedEffects());
        assertEquals("GL" + encodedVl64(2, null, "")
                + encodedVl64(12, null, "")
                + encodedVl64(3600, null, "")
                + encodedVl64(2, null, "")
                + encodedVl64(100, null, "")
                + encodedVl64(13, null, "")
                + encodedVl64(120, null, "")
                + encodedVl64(1, null, "")
                + "M",
            effectListPayload.payload());
        assertEquals(encodedVl64(3600, null,
            encodedVl64(12, null, "GN")), UserPayloads.effectActivated(12, 3600));
        assertEquals(encodedVl64(12, null, "GO"), UserPayloads.effectExpired(12));
        WallPlacement placement = RoomWire.wallPlacementFromPayload(":w= 10,20 l= 3,4");
        assertEquals(true, placement.valid());
        assertEquals(10L, placement.wallX());
        assertEquals(20L, placement.wallY());
        assertEquals(3L, placement.localX());
        assertEquals(4L, placement.localY());
        assertEquals(false, RoomWire.wallPlacementFromPayload("bad").valid());
        assertEquals("xxx\r000", RoomWire.normalizeModelMap("xxx\n\n000"));
        String iconWire = "A" + "B" + "A" + "C" + "B";
        String iconPayload = encodedVl64(1, null, "");
        iconPayload = encodedVl64(2, null, iconPayload);
        iconPayload = encodedVl64(1, null, iconPayload);
        iconPayload = encodedVl64(3, null, iconPayload);
        iconPayload = encodedVl64(2, null, iconPayload);
        assertEquals(iconPayload, RoomPayloads.icon(1L, 2L, List.of(new RoomPayloads.RoomIconItem(3L, 2L))));
        assertEquals(iconPayload, RoomWire.roomIconRequest(iconWire).iconPayload());
        assertEquals(iconPayload, RoomWire.roomIconRequest("FB" + iconWire).iconPayload());
        assertEquals(false, RoomWire.roomIconRequest("Z").valid());
        assertEquals(9L, RoomWire.roomIdRequest("@S" + wireLong(9), "@S").roomId());
        assertEquals(9L, RoomWire.roomIdRequest("@T" + wireLong(9), "@T").roomId());
        assertEquals(9L, RoomWire.roomSettingsReadRequest("FF" + wireLong(9)).requestedRoomId());
        assertEquals(1L, RoomWire.roomRatingRequest("DE" + wireLong(1)).voteValue());
        assertEquals(9L, RoomWire.roomEntryRequest("FG9").roomId());
        assertEquals("pw", RoomWire.roomEntryRequest("FG" + wireString("9") + "Bpw").roomPassword());
        RoomWire.CreateRoomRequest createRoomRequest =
            RoomWire.createRoomRequest("@]" + wireString("Created Room") + wireString("model_a"));
        assertEquals("Created Room", createRoomRequest.roomName());
        assertEquals("model_a", createRoomRequest.modelName());
        assertEquals(0L, RoomWire.deleteRoomRequest("@W" + wireLong(0)).requestFlag());
        assertEquals(1L, RoomWire.deleteRoomRequest("@W" + wireLong(1)).requestFlag());
        assertEquals(88L, RoomWire.roomRightGrantRequest("A`" + wireLong(88)).targetUserId());
        assertEquals(88L, RoomWire.roomUserTargetRequest("A_" + wireLong(88), "A_").targetUserId());
        assertEquals(88L, RoomWire.roomUserTargetRequest("E@" + wireLong(88), "E@").targetUserId());
        assertEquals("Target", RoomWire.roomRightNameRequest("D\u007f" + wireString("Target"), "D\u007f").targetName());
        assertEquals("Target", RoomWire.roomRightNameRequest("EB" + wireString("Target"), "EB").targetName());
        assertEquals(List.of(88L, 89L), RoomWire.roomRightRevokeRequest("Aa" + wireLong(2) + wireLong(88) + wireLong(89)).targetUserIds());
        assertEquals(9L, RoomWire.roomIdRequest("F@" + wireLong(9), "F@").roomId());
        assertEquals(9L, RoomWire.roomIdRequest("XX" + wireLong(9), "XX").roomId());
        assertEquals(9L, RoomWire.roomIdRequest("FD" + wireLong(9), "FD").roomId());
        RoomWire.PositionRequest lookRequest = RoomWire.positionRequest("AK" + wireLong(3) + wireLong(4), "AK");
        assertEquals(3L, lookRequest.positionX());
        assertEquals(4L, lookRequest.positionY());
        RoomWire.PositionRequest walkRequest = RoomWire.positionRequest("AO" + wireLong(5) + wireLong(6), "AO");
        assertEquals(5L, walkRequest.positionX());
        assertEquals(6L, walkRequest.positionY());
        GameDataCaches.setRoomEventLocales(RoomEventLocales.fromEntries(
            List.of(new RoomEventLocales.LocaleEntry("1", List.of("events", "")))));
        String eventWire = "A"
            + "@CJam"
            + "@DDesc"
            + "B"
            + "@COne"
            + "@DTwo2";
        RoomEventPayload createdEvent = RoomWire.roomEventCreatePayloadFromWire("EZ" + eventWire);
        assertEquals(false, createdEvent == null);
        assertEquals(1L, createdEvent.categoryId());
        assertEquals("events", createdEvent.categoryName());
        assertEquals("Jam", createdEvent.eventName());
        assertEquals("Desc", createdEvent.eventDescription());
        assertEquals("one", createdEvent.tagOne());
        assertEquals("two2", createdEvent.tagTwo());
        RoomEventPayload editedEvent = RoomWire.roomEventEditPayloadFromWire("E\\@CJam@DDescA@CTag");
        assertEquals(false, editedEvent == null);
        assertEquals("tag", editedEvent.tagOne());
        String roomSettingsWire = "@DRoom"
            + "@Epass1"
            + "A"
            + "@EDesc1"
            + "C"
            + "A"
            + "B"
            + "@CTag"
            + "@EOther"
            + "A"
            + "A"
            + "A"
            + "A"
            + "F"
            + "A";
        RoomSettingsPayload roomSettings = RoomWire.roomSettingsFromWire("FQ" + roomSettingsWire);
        assertEquals(false, roomSettings == null);
        assertEquals("Room", roomSettings.roomName());
        assertEquals(3L, roomSettings.visitorsMax());
        assertEquals("tag", roomSettings.tagOne());
        assertEquals(1L, roomSettings.allowOthersPets());
        assertEquals(1L, roomSettings.allowFeedPets());
        assertEquals(1L, roomSettings.allowWalkthrough());
        assertEquals(-2L, roomSettings.thicknessFloor());
        assertEquals(1L, roomSettings.thicknessWallpaper());
        assertEquals(1L, RoomWire.roomSettingsFlag(99));
        assertEquals(0L, RoomWire.roomSettingsFlag(0));
        assertEquals(-2L, RoomWire.roomSettingsThickness(-99));
        assertEquals(1L, RoomWire.roomSettingsThickness(99));
        assertEquals(25L, NavigatorRequests.listLimit(settings("com.client.navigator.list.limit", "25")));
        assertEquals(50L, NavigatorRequests.listLimit(settings("com.client.navigator.list.limit", "0")));
        assertEquals("100'' ok", NavigatorRequests.searchTerm("100%' ok"));
        assertEquals(2L, NavigatorWire.categoryId("GC" + wireLong(2)));
        assertEquals("100' ok", NavigatorRequests.searchParameter("100%' ok"));
        NavigatorWire.SingleRoomRequest singleRoomRequest =
            NavigatorWire.singleRoomRequest("FA" + wireLong(0) + wireLong(1) + wireLong(9));
        assertEquals(0L, singleRoomRequest.requestMode());
        assertEquals(1L, singleRoomRequest.detailFlag());
        assertEquals(9L, singleRoomRequest.roomId());
        List<String> navigatorSearchSql = new ArrayList<>();
        RoomDao navigatorSearchDao = new RoomDao(new Database() {
            @Override
            public void execute(String sqlText) {
            }

            @Override
            public List<List<Object>> query(String sqlText) {
                navigatorSearchSql.add(sqlText);
                if (sqlText.contains("SELECT rooms.id,rooms.name") && sqlText.contains("rooms_events")) {
                    return List.of(Arrays.asList(
                        88L, "event-room", "owner", 0L, 4L, 25L, "event-room-description", 1L, null,
                        8L, 9L, "event-room-icon", "tag-a", "tag-b", 1L, 0L));
                }
                if (sqlText.contains("rooms_events")) {
                    return List.of(Arrays.asList(
                        12L, "event", "owner", "open", 4L, 25L, "event-description", 1L, 1L,
                        8L, 9L, "event-icon", "tag-a", "tag-b", "12:30"));
                }
                return List.of(Arrays.asList(
                    77L, "room", "owner", 0L, 4L, 25L, "description", 1L, null,
                    8L, 9L, "icon", "tag-a", "tag-b", 1L, 0L));
            }
        });
        List<RoomDao.NavigatorEventRow> navigatorSearchEvents =
            navigatorSearchDao.navigatorSearchEvents("search", "%H:%i", 50L);
        List<NavigatorRoom> navigatorSearchRooms = navigatorSearchDao.navigatorSearchRooms("search", 50L);
        List<NavigatorRoom> topRatedRooms = navigatorSearchDao.topRatedNavigatorRooms(50L);
        List<NavigatorRoom> popularRooms = navigatorSearchDao.popularNavigatorRooms(2L, 50L);
        List<NavigatorRoom> eventCategoryRooms = navigatorSearchDao.eventCategoryNavigatorRooms(2L, 50L);
        List<NavigatorRoom> ownedRooms = navigatorSearchDao.ownedNavigatorRooms(77L, 50L);
        List<NavigatorRoom> friendCurrentRooms = navigatorSearchDao.friendCurrentNavigatorRooms(77L, 50L);
        List<NavigatorRoom> friendOwnedRooms = navigatorSearchDao.friendOwnedNavigatorRooms(77L, 50L);
        List<NavigatorRoom> favouriteRooms = navigatorSearchDao.favouriteNavigatorRooms(77L, 50L);
        List<NavigatorRoom> recentRooms = navigatorSearchDao.recentlyVisitedNavigatorRooms(77L, 50L);
        List<RoomDao.NavigatorEventRow> tagEvents = navigatorSearchDao.navigatorTagEvents("tag-a", "%H:%i", 50L);
        List<NavigatorRoom> tagRooms = navigatorSearchDao.navigatorTagRooms("tag-a", 50L);
        assertEquals("event", navigatorSearchEvents.get(0).eventName());
        assertEquals("room", navigatorSearchRooms.get(0).roomName());
        assertEquals("room", topRatedRooms.get(0).roomName());
        assertEquals("room", popularRooms.get(0).roomName());
        assertEquals("event-room", eventCategoryRooms.get(0).roomName());
        assertEquals("room", ownedRooms.get(0).roomName());
        assertEquals("room", friendCurrentRooms.get(0).roomName());
        assertEquals("room", friendOwnedRooms.get(0).roomName());
        assertEquals("room", favouriteRooms.get(0).roomName());
        assertEquals("room", recentRooms.get(0).roomName());
        assertEquals("event", tagEvents.get(0).eventName());
        assertEquals("room", tagRooms.get(0).roomName());
        assertEquals(12, navigatorSearchSql.size());
        RoomDao officialNavigatorDao = new RoomDao(new Database() {
            @Override
            public void execute(String sqlText) {
            }

            @Override
            public List<List<Object>> query(String sqlText) {
                return List.of(Arrays.asList(
                    2L, 1L, 5L, "caption", "caption-2", "caption-3", null,
                    "77", "room", "owner", "0", "4", "25", "description", "1", null,
                    "8", "9", "icon", "tag-a", "tag-b", "1", null, null, null, 3L, 44L, 7L));
            }
        });
        OfficialNavigatorItem mappedOfficialItem = officialNavigatorDao.officialNavigatorItems().get(0);
        assertEquals(77L, NumberUtils.parseLong(mappedOfficialItem.roomId()));
        assertEquals("room", mappedOfficialItem.roomName());
        assertEquals(3L, mappedOfficialItem.parentId());
        assertEquals(7L, mappedOfficialItem.requiredLevel());
        assertEquals(true, mappedOfficialItem.requiredLevelPresent());
        RoomDao.NavigatorEventRow typedNavigatorEvent = new RoomDao.NavigatorEventRow(
            10L, "room", "owner", "desc", 3L, 25L, "open", 1L, 9L, 4L, "tag1", "tag2", "event", "1");
        String expectedEventFragment = encodedVl64(10, null, "");
        expectedEventFragment = encodedVl64(3, null, expectedEventFragment);
        expectedEventFragment = encodedVl64(25, null, expectedEventFragment);
        expectedEventFragment = encodedVl64(9, null, expectedEventFragment);
        expectedEventFragment = encodedVl64(4, null, expectedEventFragment);
        expectedEventFragment = encodedVl64(1, null, expectedEventFragment)
            + " room\2owner\2desc\2open\2tag1\2tag2\2event\2" + "1\2H";
        assertEquals(expectedEventFragment, NavigatorPayloads.eventFragment(typedNavigatorEvent));
        String expectedRoomFragment = encodedVl64(10, null, "");
        expectedRoomFragment = encodedVl64(3, null, expectedRoomFragment);
        expectedRoomFragment = encodedVl64(25, null, expectedRoomFragment);
        expectedRoomFragment = encodedVl64(9, null, expectedRoomFragment);
        expectedRoomFragment = encodedVl64(4, null, expectedRoomFragment);
        expectedRoomFragment = encodedVl64(1, null, expectedRoomFragment);
        expectedRoomFragment = encodedVl64(1, null, expectedRoomFragment);
        expectedRoomFragment = encodedVl64(0, null, expectedRoomFragment);
        NavigatorRoom typedNavigatorRoom = new NavigatorRoom(
            10L, "room", "owner", 0L, 3L, 25L, "desc", 1L, 9L, 4L, "event", "tag1", "tag2", 1L, 0L);
        expectedRoomFragment += "room\2owner\2" + "0" + "\2desc\2event\2tag1\2tag2\2H";
        assertEquals(encodedVl64(1, null, expectedRoomFragment),
            NavigatorPayloads.roomList(List.of(typedNavigatorRoom)));
        assertEquals(expectedRoomFragment, NavigatorPayloads.roomFragment(typedNavigatorRoom));
        assertEquals(encodedVl64(0, null, "GF") + NavigatorPayloads.singleRoom(typedNavigatorRoom),
            NavigatorPayloads.singleRoomResponse(typedNavigatorRoom));
        assertEquals(encodedVl64(1, null, expectedEventFragment),
            NavigatorPayloads.eventList(List.of(typedNavigatorEvent)));
        assertEquals(encodedVl64(2, null, expectedEventFragment + expectedRoomFragment),
            NavigatorPayloads.combinedRoomList(List.of(typedNavigatorEvent), List.of(typedNavigatorRoom)));
        assertEquals(encodedVl64(0, null, ""), NavigatorPayloads.roomList(List.of()));
        assertEquals(encodedVl64(0, null, ""), NavigatorPayloads.eventList(List.of()));
        OfficialNavigatorItem officialItem = new OfficialNavigatorItem(
            1L, 2L, 3L, "caption", "cap2", "cap3", "7", "8", "9", "10",
            "11", "12", "13", "description", "15", "16", "17", "18", "icon",
            "tag1", "tag2", "22", "model", "files", "250", 5L, 6L, 7L, true);
        String expectedOfficialRow = expectedOfficialNavigatorRow(officialItem);
        assertEquals(expectedOfficialRow, NavigatorPayloads.officialItem(officialItem));
        assertEquals(expectedOfficialRow, NavigatorPayloads.official(List.of(officialItem), false));
        assertEquals(encodedVl64(1, null, "") + expectedOfficialRow,
            NavigatorPayloads.official(List.of(officialItem), true));
        assertEquals(encodedVl64(0, null, ""), NavigatorPayloads.official(List.of(), true));
        assertEquals(encodedVl64(1, null, encodedVl64(12, null, "L\u007f")),
            NavigatorPayloads.newFriendRoom(new com.alphaseries.game.navigator.NewFriendRooms.RoomPick(12L, 1L)));
        assertEquals("GCPC7\2" + encodedVl64(50, null, "") + NavigatorPayloads.roomList(List.of(typedNavigatorRoom)),
            NavigatorPayloads.queryResult("GCPC", "7", 50, List.of(typedNavigatorRoom)));
        assertEquals("GC" + '\0' + '\2' + encodedVl64(50, null, "") + NavigatorPayloads.roomList(List.of()),
            NavigatorPayloads.queryResult("GC", "\0", 50, List.of()));
        assertEquals("GCSAquery\2" + encodedVl64(50, null, "") + expectedEventFragment + expectedRoomFragment
                + encodedVl64(2, null, ""),
            NavigatorPayloads.combinedQueryResult(
                "GCSA",
                "query",
                50,
                List.of(typedNavigatorEvent),
                List.of(typedNavigatorRoom)));
        assertEquals("GCPC7\2" + encodedVl64(50, null, "") + NavigatorPayloads.roomList(List.of(typedNavigatorRoom))
                + "REC",
            NavigatorPayloads.queryResultWithRecommended(
                "GCPC",
                "7",
                50,
                List.of(typedNavigatorRoom),
                RecommendedRooms.fromPayloads(Map.of(0L, "REC"), 1L),
                1L));
        seedCatalogRegistryProductRows(List.of(
            productDaoRow(20, "0", "1", "13", "chair", "14", "seat", "17", "chair_sprite"),
            productDaoRow(21, "0", "9", "13", "poster", "14", "wall", "17", "poster_sprite")));
        String expectedInventoryItem = encodedVl64(100, null, "0") + "S\2"
            + encodedVl64(100, null, "")
            + encodedVl64(20, null, "")
            + encodedVl64(1, null, "")
            + "a\tb\2"
            + encodedVl64(4, null, "")
            + "chair\2seat\2chair_sprite\2M\2"
            + encodedVl64(4, null, "");
        assertEquals(expectedInventoryItem, InventoryMessagePayloads.item(100, 20, "a\bb", 4));
        String iconInventoryItem = InventoryMessagePayloads.item(101, 21, "", 0);
        assertEquals(true, iconInventoryItem.contains("0" + encodedVl64(101, null, "") + "I\2"));
        assertEquals(true, iconInventoryItem.contains("poster\2wall\2poster_sprite\2"));
        GameDataCaches.setProductCache(ProductCache.fromProductRows(List.of(
            productCacheRow(20, "0", "1"),
            productCacheRow(21, "0", "9"))));
        InventoryMessagePayloads.InventoryList inventoryPayloads = InventoryMessagePayloads.listFromItems(List.of(
            new InventoryItemRow(100L, 20L, "a\bb", 4L),
            new InventoryItemRow(101L, 21L, "", 0L)));
        assertEquals(1L, inventoryPayloads.regularCount());
        assertEquals(1L, inventoryPayloads.iconCount());
        assertEquals(expectedInventoryItem, inventoryPayloads.regularPayload());
        assertEquals(iconInventoryItem, inventoryPayloads.iconPayload());
        assertEquals('\2' + encodedVl64(1, null, "BLS" + '\2' + "II") + expectedInventoryItem,
            InventoryMessagePayloads.regularList(inventoryPayloads));
        assertEquals(encodedVl64(1, null, "BL" + '\2' + "II") + iconInventoryItem,
            InventoryMessagePayloads.iconList(inventoryPayloads));
        assertEquals(encodedVl64(0, null,
            encodedVl64(0, null,
                encodedVl64(0, null,
                    encodedVl64(0, null, "Id") + "HHH"))) + "H",
            InventoryMessagePayloads.emptyRentalList());
        List<RepresentedTradeOffer> tradeOffers = TradePayloads.storeOffer(List.of(), 2, 100, 20, "a\rb", 4);
        assertEquals(1, tradeOffers.size());
        assertEquals(2L, tradeOffers.get(0).socketIndex());
        assertEquals(100L, tradeOffers.get(0).furnitureId());
        assertEquals(20L, tradeOffers.get(0).productId());
        assertEquals("ab", tradeOffers.get(0).signText());
        assertEquals(4L, tradeOffers.get(0).secondaryValue());
        tradeOffers = TradePayloads.storeOffer(tradeOffers, 2, 101, 21, "", 0);
        assertEquals(2, tradeOffers.size());
        tradeOffers = TradePayloads.storeOffer(tradeOffers, 3, 102, 20, "x", 5);
        assertEquals(3, tradeOffers.size());
        tradeOffers = TradePayloads.storeOffer(tradeOffers, 2, 100, 20, "new", 6);
        assertEquals(3, tradeOffers.size());
        assertEquals("new", tradeOffers.get(0).signText());
        assertEquals(6L, tradeOffers.get(0).secondaryValue());
        assertEquals(List.of(100L, 101L), TradePayloads.furnitureIds(tradeOffers, 2));
        assertEquals("100:20\1" + "101:21", TradePayloads.logItems(tradeOffers, 2));
        TradePayloads.ItemPayload sourceTradeItems = TradePayloads.itemPayload(tradeOffers, 2);
        String expectedSourceTradeItems = InventoryMessagePayloads.item(100, 20, "new", 6) + iconInventoryItem;
        assertEquals(2L, sourceTradeItems.itemCount());
        assertEquals(expectedSourceTradeItems, sourceTradeItems.payload());
        TradePayloads.ItemPayload targetTradeItems = TradePayloads.itemPayload(tradeOffers, 3);
        String expectedTargetTradeItems = InventoryMessagePayloads.item(102, 20, "x", 5);
        assertEquals(1L, targetTradeItems.itemCount());
        assertEquals(expectedTargetTradeItems, targetTradeItems.payload());
        String expectedTradePayload = encodedVl64(5, null, "Al");
        expectedTradePayload = encodedVl64(6, null, expectedTradePayload);
        expectedTradePayload = encodedVl64(2, null, expectedTradePayload) + expectedSourceTradeItems;
        expectedTradePayload = encodedVl64(1, null, expectedTradePayload) + expectedTargetTradeItems;
        assertEquals(expectedTradePayload, TradePayloads.confirmation(5, 6, 2, expectedSourceTradeItems, 1, expectedTargetTradeItems));
        assertEquals(expectedTradePayload, TradePayloads.offerPayload(tradeOffers, 2, 3, "5", "6"));
        List<RepresentedTradeOffer> removedSingleTradeOffer = TradePayloads.removeOffer(tradeOffers, 2, 101);
        assertEquals(2, removedSingleTradeOffer.size());
        assertEquals(100L, removedSingleTradeOffer.get(0).furnitureId());
        assertEquals(102L, removedSingleTradeOffer.get(1).furnitureId());
        List<RepresentedTradeOffer> removedSocketTradeOffers = TradePayloads.removeOffer(tradeOffers, 2, 0);
        assertEquals(1, removedSocketTradeOffers.size());
        assertEquals(3L, removedSocketTradeOffers.get(0).socketIndex());
        assertEquals(102L, removedSocketTradeOffers.get(0).furnitureId());
        assertEquals(76L, TradeWire.furnitureRequest("FU" + wireLong(76), "FU").furnitureId());
        assertEquals(76L, TradeWire.furnitureRequest("AH" + wireLong(76), "AH").furnitureId());
        FurnitureWire.FurnitureMoveRequest moveRequest = FurnitureWire.moveRequest("A[100\1" + "3\2" + "4\t2");
        assertEquals(100L, moveRequest.furnitureId());
        assertEquals(3L, moveRequest.positionX());
        assertEquals(4L, moveRequest.positionY());
        assertEquals(2L, moveRequest.rotation());
        FurnitureWire.FurnitureMoveRequest wireMoveRequest = FurnitureWire.moveRequest("A");
        assertEquals(1L, wireMoveRequest.furnitureId());
        assertEquals(0L, wireMoveRequest.positionX());
        FurnitureWire.FloorPlacementRequest floorPlacementRequest =
            FurnitureWire.floorPlacementRequest("A[100\1" + "3\2" + "4\t2");
        assertEquals("100\1" + "3\2" + "4\t2", floorPlacementRequest.placementPayload());
        assertEquals(100L, floorPlacementRequest.placement().furnitureId());
        assertEquals(3L, floorPlacementRequest.placement().positionX());
        assertEquals(4L, floorPlacementRequest.placement().positionY());
        assertEquals(2L, floorPlacementRequest.placement().rotation());
        String expectedPointBalance = encodedVl64(4, null, "M@")
            + encodedVl64(1, null, "")
            + encodedVl64(10, null, "")
            + encodedVl64(2, null, "")
            + encodedVl64(20, null, "")
            + encodedVl64(3, null, "")
            + encodedVl64(0, null, "")
            + encodedVl64(4, null, "")
            + encodedVl64(40, null, "");
        assertEquals(expectedPointBalance, UserPayloads.activityPointBalance(10L, 20L, 0L, 40L));
        assertEquals(1L, FurnitureWire.pickupFurnitureId("AZA"));
        assertEquals(1L, FurnitureWire.pickupFurnitureId("A"));
        assertEquals(1L, FurnitureWire.pickupFurnitureRequest("AZA").furnitureId());
        assertEquals(73L, FurnitureWire.creditFurnitureRequest("AT" + wireLong(73)).furnitureId());
        assertEquals(86L, FurnitureWire.floorStateFurnitureId("Ch" + wireLong(86)));
        assertEquals(86L, FurnitureWire.floorStateFurnitureId("FH" + wireLong(86)));
        assertEquals(86L, FurnitureWire.floorStateFurnitureId(wireLong(86)));
        assertEquals(86L, FurnitureWire.floorStateFurnitureRequest("Ch" + wireLong(86)).furnitureId());
        assertEquals(93L, FurnitureWire.simpleFloorItemUseRequest("AM" + wireLong(93), "AM").furnitureId());
        assertEquals(94L, FurnitureWire.simpleFloorItemUseRequest("AL" + wireLong(94), "AL").furnitureId());
        FurnitureWire.FloorFurniturePackageRequest packageRequest =
            FurnitureWire.floorFurniturePackageRequest("FH" + wireLong(95));
        assertEquals(95L, packageRequest.furnitureId());
        assertEquals(wireLong(95), packageRequest.requestPayload());
        FurnitureRoomCache.State tracked = FurnitureRoomCache.trackMarker(
            "\1" + "8\2\1" + "5\tstale\2",
            "\1" + "7\2\1" + "7\told\2",
            "\1" + "7\2\1" + "7\troom\2\1" + "9\2",
            5,
            7);
        assertEquals("\1" + "8\2\1" + "5\2", tracked.pendingRoomCache);
        assertEquals("\1" + "7\2", tracked.pendingFurnitureCache);
        assertEquals("\1" + "9\2", tracked.representedRoomCache);
        FurnitureRoomCache.State removed = FurnitureRoomCache.removeMarker(
            "\1" + "7\2\1" + "7\troom\2\1" + "8\2",
            "\1" + "7\2\1" + "7\told\2",
            "\1" + "7\2\1" + "7\troom\2\1" + "9\2",
            7);
        assertEquals("\1" + "8\2", removed.pendingRoomCache);
        assertEquals("", removed.pendingFurnitureCache);
        assertEquals("\1" + "9\2", removed.representedRoomCache);
        assertEquals(1L, FurnitureWire.nextState("chair", 0, 1));
        assertEquals(0L, FurnitureWire.nextState("chair", 1, 1));
        assertEquals(99L, FurnitureWire.nextState("bb_score_blue", 98, 0));
        assertEquals(0L, FurnitureWire.nextState("scoreboard", 99, 0));
        long diceState = FurnitureWire.nextState("dice_red", 0, 0);
        assertEquals(true, diceState >= 1L && diceState <= 6L);
        assertEquals("AX77\2" + "3\2", FurniturePayloads.stateChanged(77, 3));
        assertEquals("0" + encodedVl64(3, null,
            encodedVl64(77, null, "AZ")), FurniturePayloads.simpleFloorUse(77, 3));
        assertEquals(
            encodedVl64(77, null, "Iu")
                + encodedVl64(0, null, "")
                + encodedVl64(3, null, "")
                + encodedVl64(10, null, "")
                + encodedVl64(2, null, "")
                + encodedVl64(1, null, ""),
            FurniturePayloads.chargePrompt(77, 0, 3, 10, 2, 1));
        FurnitureRoomCache.State stateCache = FurnitureRoomCache.stateCache(
            "\1" + "5\2\1" + "9\told\2",
            "\1" + "77\2",
            "\1" + "5\t77\t1\2\1" + "77\2\1" + "88\2",
            5,
            77,
            4);
        assertEquals("\1" + "9\told\2\1" + "5\2", stateCache.pendingRoomCache);
        assertEquals("\1" + "77\2", stateCache.pendingFurnitureCache);
        assertEquals("\1" + "5\t77\t4\2", stateCache.representedRoomCache);
        FurnitureRoomCache.State stateWrite = FurnitureRoomCache.stateWrite(
            "\1" + "5\2\1" + "8\2",
            "\1" + "77\2\1" + "88\2",
            "\1" + "77\2\1" + "77\t5\told\2\1" + "99\t5\tkeep\2",
            5,
            77,
            "new-state");
        assertEquals("\1" + "8\2\1" + "5\2", stateWrite.pendingRoomCache);
        assertEquals("\1" + "88\2\1" + "77\2", stateWrite.pendingFurnitureCache);
        assertEquals("\1" + "99\t5\tkeep\2\1" + "77\t5\tnew-state\2", stateWrite.representedRoomCache);
        FurnitureRoomCache.State stateWriteNoRoom = FurnitureRoomCache.stateWrite("", "", "", 0, 77, "off");
        assertEquals("", stateWriteNoRoom.pendingRoomCache);
        assertEquals("\1" + "77\2", stateWriteNoRoom.pendingFurnitureCache);
        assertEquals("\1" + "77\t0\toff\2", stateWriteNoRoom.representedRoomCache);
        String expectedWallInventory = "0" + encodedVl64(9, null,
            encodedVl64(20, null, "77\2") + ":w=1,2 l=3,4\2data\2");
        assertEquals(expectedWallInventory,
            FurniturePayloads.wallInventoryPlacement(77, 20, ":w=1,2 l=3,4", "data", 9));
        assertEquals(expectedWallInventory, FurniturePayloads.wallInventoryPlacement(77, 20, ":w=1,2 l=3,4", "data", 9));
        String expectedFloorPlacement = encodedVl64(99, null, "0");
        expectedFloorPlacement = encodedVl64(1, null, expectedFloorPlacement);
        expectedFloorPlacement = "0" + encodedVl64(2, null, expectedFloorPlacement);
        expectedFloorPlacement = "0" + encodedVl64(4, null, expectedFloorPlacement);
        expectedFloorPlacement = encodedVl64(3, null, expectedFloorPlacement) + "state\2";
        expectedFloorPlacement = encodedVl64(7, null, expectedFloorPlacement) + "a\tb\tc\2M";
        expectedFloorPlacement = encodedVl64(20, null, expectedFloorPlacement);
        assertEquals(expectedFloorPlacement,
            FurniturePayloads.floorPlacement(99, 1, 2, 4, 3, "state", "a\bb{{9}}c", 7, 20));
        assertEquals(expectedFloorPlacement, FurniturePayloads.floorPlacement(99, 1, 2, 4, 3, "state", "a\bb{{9}}c", 7, 20));
        assertEquals(encodedVl64(77, null, "BAi\2") + "data\2",
            FurniturePayloads.presentOpened(77, "i", "data"));
        assertEquals(encodedVl64(88, null,
            encodedVl64(77, null, "L}package\2")) + "H",
            FurniturePayloads.packageOpened(77, 88, "package"));
        String expectedModelFloorPlacement = FurniturePayloads.floorPlacement(99, 1, 2, 4, 3, "", "a\bb{{9}}c", 0, 20);
        assertEquals(encodedVl64(2, null, "@^") + expectedModelFloorPlacement + expectedModelFloorPlacement,
            FurniturePayloads.floorList(List.of(
                new RoomModelFurnitureRow(20L, 99L, "sprite", 1L, 2L, 3L, "a\bb{{9}}c", 4L, 7L),
                new RoomModelFurnitureRow(20L, 99L, "sprite", 1L, 2L, 3L, "a\bb{{9}}c", 4L, 7L))));
        String expectedWallListItem = FurniturePayloads.wallInventoryPlacement(100L, 101L, "wallpos", "state", 5L);
        assertEquals(encodedVl64(3, null, "@m") + expectedWallListItem + expectedWallListItem + expectedWallListItem,
            FurniturePayloads.wallList(List.of(
                new FurnitureDao.WallFurniture(100L, 101L, "wallpos", "state", 5L),
                new FurnitureDao.WallFurniture(100L, 101L, "wallpos", "state", 5L),
                new FurnitureDao.WallFurniture(100L, 101L, "wallpos", "state", 5L))));
        assertEquals("0DAQBHHIIKHJHPAHQA\2SAHPBhttp://www.alpha-series.com/\2QBH", SessionPayloads.systemHandshake(""));
        assertEquals("0FMT\2SAHPBhttp://www.alpha-series.com/\2QBH", SessionPayloads.systemHandshake("FMT"));
        assertEquals("ticket one", SessionWire.loginTicket("F_ticket\none"));
        assertEquals("ticket two", SessionWire.loginTicket(" F_ticket two "));
        assertEquals(encodedVl64(2, null, encodedVl64(300, null, "Fv") + "H"),
            UserPayloads.activityPointRefresh(2, 300));
        assertEquals(encodedVl64(2, null, encodedVl64(300, null, "Fv")) + "H",
            UserPayloads.activityPointAward(2, 300));
        UserActivityPoints.Award pointAward = UserActivityPoints.awardDecision(120, 2, 60, 500, 25, 300);
        assertEquals(true, pointAward.shouldAward());
        assertEquals(325L, pointAward.newPoints());
        assertEquals(UserPayloads.activityPointAward(2, 325), pointAward.payload());
        UserActivityPoints.AwardBatch pointAwards = UserActivityPoints.AwardBatch.fromAwards(List.of(pointAward));
        assertEquals(pointAward.payload(), UserActivityPoints.payloadForAwards(List.of(pointAward)));
        assertEquals(List.of(pointAward.payload()), pointAwards.deliveryPayloads());
        assertEquals(false, UserActivityPoints.awardDecision(121, 2, 60, 500, 25, 300).shouldAward());
        RepresentedSocketCache.RepresentedSocketRecord socketRecord =
            new RepresentedSocketCache.RepresentedSocketRecord("a\2" + "7\2c\2d\2e\2" + "1", 7L, true);
        assertEquals(7L, socketRecord.roomSlot());
        assertEquals(true, socketRecord.busy());
        RepresentedSocketCache socketCache = RepresentedSocketCache.fromRecords(Map.of(
            4L, socketRecord,
            5L, new RepresentedSocketCache.RepresentedSocketRecord("a\2b\2c\2d\2e\2" + "0", 0L, false),
            6L, new RepresentedSocketCache.RepresentedSocketRecord("a\2b", 0L, false)
        ));
        assertEquals(3, socketCache.recordsBySocketIndex().size());
        assertEquals(socketRecord, socketCache.recordsBySocketIndex().get(4L));
        assertEquals(7L, socketCache.roomSlot(4));
        assertEquals(true, socketCache.isBusy(4));
        assertEquals(false, socketCache.isBusy(5));
        assertEquals(false, socketCache.isBusy(6));
        assertEquals(false, socketCache.isBusy(9));
        RepresentedSocketCache previousRepresentedSocketCache = SessionState.instance().representedSockets();
        SessionState.instance().setRepresentedSockets(socketCache);
        assertEquals(true, SessionState.instance().representedSockets().isBusy(4));
        SessionState.instance().setRepresentedSockets(previousRepresentedSocketCache);
        String expectedOwnProfile = "@E7\2Alice\2hello\2F\2\2\2H\2HIH";
        expectedOwnProfile = encodedVl64(4, null, expectedOwnProfile);
        expectedOwnProfile = encodedVl64(2, null, expectedOwnProfile);
        assertEquals(expectedOwnProfile,
            UserPayloads.ownProfile(new OwnProfileRow(7L, "Alice", "hello", "female", 4L, 2L)));
        assertEquals(50L, UserWire.soundSettingRequest("Ce50").soundSetting());
        assertEquals(0L, UserWire.soundSettingRequest("Ce101").soundSetting());
        String expectedGroupPayload = encodedVl64(55, null, "Dt")
            + "Group\2Desc\2BADGE\2"
            + encodedVl64(77, null, "")
            + "H";
        assertEquals(expectedGroupPayload, UserPayloads.loginGroup(55, new UserGroupRow("Group", "Desc", "BADGE", 77L)));
        String expectedQuestPayload = encodedVl64(7, null, "") + "Quest\2"
            + encodedVl64(3, null, "")
            + encodedVl64(44, null, "")
            + encodedVl64(2, null, "")
            + encodedVl64(5, null, "")
            + encodedVl64(1, null, "")
            + encodedVl64(0, null, "");
        assertEquals(expectedQuestPayload, QuestPayloads.completion(7, "Quest", 3, 44, 2, 5, 0));
        assertEquals(expectedQuestPayload, QuestPayloads.completion(7, "Quest", 3, 44, 2, 5, 0));
        List<QuestSettings.QuestDefinitionRow> questDefinitions = List.of(
            new QuestSettings.QuestDefinitionRow(10L, 1L, "First", "", 5L, 2L, "visit", 0L, 7L, 3L, 30L, 11),
            new QuestSettings.QuestDefinitionRow(11L, 2L, "Second", "", 6L, 2L, "visit", 0L, 7L, 4L, 0L, 11));
        QuestSettings typedQuestSettings = QuestSettings.fromDefinitions(questDefinitions);
        assertEquals(questDefinitions, typedQuestSettings.definitions());
        QuestSettings previousQuestSettings = QuestState.instance().settings();
        QuestState.instance().setSettings(typedQuestSettings);
        assertEquals(questDefinitions, QuestState.instance().settings().definitions());
        QuestState.instance().setSettings(QuestSettings.fromDefinitions(List.of(new QuestSettings.QuestDefinitionRow(
            12L, 3L, "Short", "", 1L, 2L, "visit", 0L, 9L, 0L, 0L, 9))));
        assertEquals(List.of(new QuestSettings.QuestDefinitionRow(
                12L, 3L, "Short", "", 1L, 2L, "visit", 0L, 9L, 0L, 0L, 9)),
            QuestState.instance().settings().definitions());
        QuestState.instance().setSettings(previousQuestSettings);
        assertEquals("p^" + encodedVl64(10, null, ""), QuestPayloads.request(10));
        assertEquals(10L, WireRequests.id("p^" + encodedVl64(10, null, ""), "p^"));
        assertEquals(10L, QuestWire.questIdRequest("p^" + wireLong(10), "p^").questId());
        assertEquals(11L, QuestProgress.nextQuestId(typedQuestSettings, new QuestDao.UserQuestLevelRow(10L, 1L)));
        assertEquals(10L, QuestProgress.nextQuestId(typedQuestSettings, null));
        QuestProgressDecision waitDecision = QuestProgress.decision(
            new QuestDao.UserQuestProgressRow(10L, 10L, 1L, 0L, ""), typedQuestSettings, 0);
        assertEquals(10L, waitDecision.questId());
        assertEquals(3L, waitDecision.amountRequired());
        assertEquals(true, waitDecision.shouldScheduleWait());
        assertEquals(true, waitDecision.shouldSendList());
        QuestProgressDecision completeDecision = QuestProgress.decision(
            new QuestDao.UserQuestProgressRow(10L, 10L, 3L, 0L, "0"), typedQuestSettings, 0);
        assertEquals(true, completeDecision.shouldComplete());
        String expectedQuestListRow = encodedVl64(7, null, "") + "First\2"
            + encodedVl64(10, null, "")
            + encodedVl64(1, null, "")
            + encodedVl64(1, null, "")
            + encodedVl64(1, null, "")
            + encodedVl64(0, null, "")
            + encodedVl64(1, null, "")
            + encodedVl64(3, null, "")
            + encodedVl64(2, null, "")
            + encodedVl64(5, null, "")
            + "HHH\2\2H\2HHH"
            + encodedVl64(12, null, "");
        String expectedSecondQuestListRow = encodedVl64(7, null, "") + "Second\2"
            + encodedVl64(11, null, "")
            + encodedVl64(2, null, "")
            + encodedVl64(2, null, "")
            + encodedVl64(0, null, "")
            + encodedVl64(0, null, "")
            + encodedVl64(0, null, "")
            + encodedVl64(4, null, "")
            + encodedVl64(2, null, "")
            + encodedVl64(6, null, "")
            + "HHH\2\2H\2HHH"
            + encodedVl64(0, null, "");
        List<QuestSettings.UserQuestListRow> userQuestListRows = List.of(
            new QuestSettings.UserQuestListRow(10L, 0L, "0", "1", "2026-01-01", 1L, 12L, 7));
        assertEquals(encodedVl64(0, null, encodedVl64(2, null, "L`"))
                + expectedQuestListRow + expectedSecondQuestListRow,
            QuestPayloads.list(typedQuestSettings, userQuestListRows));
        assertEquals(encodedVl64(0, null, encodedVl64(2, null, "L`"))
                + expectedQuestListRow + expectedSecondQuestListRow,
            QuestPayloads.list(typedQuestSettings, userQuestListRows));
        AppConfigState.instance().setSettingsCache(settings("com.client.messenger.follow.enabled", "1"));
        String expectedOnlineFriend = encodedVl64(5, null, "0") + "Alice\2";
        expectedOnlineFriend = encodedVl64(3, null, expectedOnlineFriend);
        expectedOnlineFriend = encodedVl64(1, null, expectedOnlineFriend);
        expectedOnlineFriend = encodedVl64(1, null, expectedOnlineFriend) + "motto\2today\2\2";
        assertEquals(expectedOnlineFriend, MessengerViews.friendPayload(5, "Alice", "motto", "fig", 3, 2, 1, "today", 1));
        String expectedOfflineFriend = encodedVl64(6, null, "0") + "Bob\2";
        expectedOfflineFriend = encodedVl64(2, null, expectedOfflineFriend);
        expectedOfflineFriend = encodedVl64(0, null, expectedOfflineFriend)
            + "\2Hfig2\2" + "4\2yesterday\2\2";
        assertEquals(expectedOfflineFriend, MessengerViews.friendPayload(6, "Bob", "motto2", "fig2", 2, 0, 0, "yesterday", 4));
        assertEquals(expectedOnlineFriend,
            MessengerViews.friendSummaryPayload(new MessengerFriend(5L, "Alice", "motto", "fig", 3L, 22L, "today"), 1));
        String expectedSearch = encodedVl64(8, null, "") + "Carol\2hi\2";
        expectedSearch = "1" + encodedVl64(1, null, expectedSearch) + "H\2nick\2fig\2now\2";
        assertEquals(expectedSearch, MessengerPayloads.searchResult("8", "Carol", "fig", "hi", "nick", "now", 1));
        assertEquals(encodedVl64(1, null, "Fs") + expectedSearch
            + encodedVl64(1, null, "") + expectedSearch,
            MessengerPayloads.searchResults(List.of(
                new MessengerSearchResult(8L, "Carol", "fig", "hi", "nick", "now", true, true),
                new MessengerSearchResult(8L, "Carol", "fig", "hi", "nick", "now", true, false))));
        MessengerState.instance().setSettings(MessengerSettings.fromLimits(10L, 0L, 20L, 0L, 30L));
        assertEquals(20L, MessengerState.instance().settings().maxFriends(2));
        assertEquals(20L, MessengerViews.maxFriends(2));
        assertEquals(0L, MessengerViews.maxFriends(99));
        assertMessengerSettingsTypedAccessors();
        assertEquals("hello world", MessengerWire.requestTextFromWirePayload("@i@Khello world", "@i", 50));
        assertEquals("abc", MessengerWire.requestTextFromWirePayload("@g@Cabc", "@g", 2_000));
        assertEquals("he", MessengerWire.requestTextFromWirePayload("@g@Khello", "@g", 2));
        assertEquals("target", MessengerWire.searchRequest("@i@GTarget", "@i").searchText());
        assertEquals("Target", MessengerWire.friendRequest("@g@GTarget", "@g").targetName());
        MessengerWire.FriendTargetList deleteAll = MessengerWire.friendDeleteTargetsFromPayload("@fA");
        assertEquals(true, deleteAll.deleteAllPending());
        MessengerWire.FriendTargetList deleteTargets = MessengerWire.friendDeleteTargetsFromPayload("@fCABA");
        assertEquals(false, deleteTargets.deleteAllPending());
        assertEquals("1,2", deleteTargets.targetList());
        assertEquals(List.of(1L, 2L), deleteTargets.targetIds());
        assertEquals(2L, deleteTargets.targetCount());
        MessengerWire.AcceptFriendRequests acceptRequests =
            MessengerWire.acceptFriendRequests("@e" + wireLong(3) + wireLong(88) + wireLong(88) + wireLong(89));
        assertEquals(3L, acceptRequests.requestedCount());
        assertEquals(List.of(88L, 89L), acceptRequests.targetIds());
        String expectedAcceptedOfflineFriend = encodedVl64(6, null, "0") + "Bob\2";
        expectedAcceptedOfflineFriend = encodedVl64(2, null, expectedAcceptedOfflineFriend);
        expectedAcceptedOfflineFriend = encodedVl64(0, null, expectedAcceptedOfflineFriend)
            + "\2Hfig2\2" + "0\2yesterday\2\2";
        assertEquals(encodedVl64(2, null, "@MH") + "H" + expectedOnlineFriend + "H" + expectedAcceptedOfflineFriend,
            MessengerPayloads.acceptedFriends(List.of(
                new MessengerFriend(5L, "Alice", "motto", "fig", 3L, 22L, "today"),
                new MessengerFriend(6L, "Bob", "motto2", "fig2", 2L, 0L, "yesterday")), true));
        assertEquals(encodedVl64(1, null, "@MM") + encodedVl64(44, null, ""),
            MessengerPayloads.removeFriends(List.of(44L)));
        assertEquals(encodedVl64(88, null, "DD") + "H", MessengerPayloads.requestAcceptedCaller(88));
        assertEquals("DDH\2", MessengerPayloads.requestDenied());
        assertEquals(encodedVl64(5, null, "BD") + "Alice\2" + "5\2",
            MessengerPayloads.requestNotify(5, "Alice"));
        assertEquals(encodedVl64(77, null, "BF") + "hello\2",
            MessengerPayloads.privateChatMessage(77, "hello"));
        assertEquals(encodedVl64(77, null, "BG") + "join\2",
            MessengerPayloads.roomInviteMessage(77, "join"));
        String roomInviteWire = "@b" + wireLong(2) + wireLong(88) + wireLong(88) + wireString("Join me");
        MessengerWire.RoomInviteRequest roomInviteRequest = MessengerWire.roomInviteFromWire(roomInviteWire);
        assertEquals(List.of(88L), roomInviteRequest.targetIds());
        assertEquals(2L, roomInviteRequest.targetCount());
        assertEquals(WireEncoding.readBase64LengthString(roomInviteWire.substring(2)), roomInviteRequest.inviteText());
        assertEquals(88L, MessengerWire.friendFollowRequest("DF" + wireLong(88)).targetUserId());
        assertEquals(encodedVl64(9, null, encodedVl64(61, null, "D^")),
            MessengerPayloads.followRoom(61, 9));
        assertEquals("@MHIH" + expectedOnlineFriend, MessengerPayloads.friendOnlineNotification(
            new MessengerFriend(5L, "Alice", "motto", "fig", 3L, 22L, "today"), 1L, true));
        assertEquals("@MMIM77", MessengerPayloads.friendRemovedNotification(77));
        String expectedPendingRequestRows = "0" + encodedVl64(5, null, "") + "Alice\2Alice\2"
            + "0" + encodedVl64(6, null, "") + "Bob\2Bob\2";
        String expectedPendingPayload = encodedVl64(2, null, "Dz")
            + encodedVl64(2, null, "Dz");
        expectedPendingPayload = encodedVl64(2, null, expectedPendingPayload) + expectedPendingRequestRows;
        assertEquals(expectedPendingPayload, MessengerPayloads.pendingRequests(List.of(
            new PendingFriendRequest(5L, "Alice"),
            new PendingFriendRequest(6L, "Bob"))));
        String expectedListedOfflineFriend = encodedVl64(6, null, "0") + "Bob\2";
        expectedListedOfflineFriend = encodedVl64(2, null, expectedListedOfflineFriend);
        expectedListedOfflineFriend = encodedVl64(0, null, expectedListedOfflineFriend)
            + "\2Hfig2\2" + "1\2yesterday\2\2";
        String expectedFriendList = encodedVl64(10, null, "@L")
            + encodedVl64(20, null, "")
            + encodedVl64(30, null, "")
            + encodedVl64(2, null, "")
            + expectedOnlineFriend
            + expectedListedOfflineFriend
            + "PYH";
        assertEquals(expectedFriendList, MessengerViews.friendListPayload(
            List.of(
                new MessengerFriend(5L, "Alice", "motto", "fig", 3L, 22L, "today"),
                new MessengerFriend(6L, "Bob", "motto2", "fig2", 2L, 0L, "yesterday")),
            10,
            20,
            30));
        assertEquals(expectedFriendList, MessengerPayloads.friendList(
            List.of(
                new MessengerFriend(5L, "Alice", "motto", "fig", 3L, 22L, "today"),
                new MessengerFriend(6L, "Bob", "motto2", "fig2", 2L, 8L, "yesterday")),
            10,
            20,
            30,
            List.of(5L),
            true));
        MessengerWire.FriendTargetList removeTargets = MessengerWire.friendRemoveTargetsFromPayload("@hCBCA", "2");
        assertEquals("3,1", removeTargets.targetList());
        assertEquals(List.of(3L, 1L), removeTargets.targetIds());
        assertEquals(2L, removeTargets.targetCount());
        String expectedRacePayload = encodedVl64(2, null, "L{dog\2")
            + encodedVl64(1, null, "") + "II"
            + encodedVl64(3, null, "") + "II";
        assertEquals(expectedRacePayload, PetPayloads.raceList("dog", List.of(
            new PetRaceRow(1L, 1L, 0L, 0L, "A"),
            new PetRaceRow(2L, 2L, 5L, 0L, "B"),
            new PetRaceRow(3L, 3L, 2L, 1L, "C")), 3, 1));
        String expectedPetRow = "0" + encodedVl64(10, null, "") + "Rex\2"
            + encodedVl64(1, null, "")
            + encodedVl64(2, null, "")
            + "0ff00aa\2"
            + encodedVl64(4, null, "");
        assertEquals(expectedPetRow, PetPayloads.inventoryRow(new PetInventoryRow(10L, "Rex", "1 2 FF00AA", 4L)));
        assertEquals("I[" + expectedPetRow, PetPayloads.inventoryAdd(new PetInventoryRow(10L, "Rex", "1 2 FF00AA", 4L)));
        assertEquals(encodedVl64(10, null, "I\\"), PetPayloads.placed(10L));
        assertEquals("@]10\2", PetPayloads.removedFromRoom(10L));
        String expectedPetList = encodedVl64(2, null, "IX")
            + expectedPetRow
            + "0" + encodedVl64(11, null, "") + "Mia\2"
            + encodedVl64(3, null, "")
            + encodedVl64(0, null, "")
            + "0\2"
            + encodedVl64(0, null, "");
        assertEquals(expectedPetList, PetPayloads.inventoryList(List.of(
            new PetInventoryRow(10L, "Rex", "1 2 FF00AA", 4L),
            new PetInventoryRow(11L, "Mia", "3", 0L))));
        assertEquals(0L, PetPayloads.nameValidationCode("Rex"));
        assertEquals(1L, PetPayloads.nameValidationCode("abcdefghijklmnopqrstuvwxyzabcde"));
        assertEquals(2L, PetPayloads.nameValidationCode(""));
        assertEquals(2L, PetPayloads.nameValidationCode("Rex1"));
        assertEquals(encodedVl64(2, null, "@d"), PetPayloads.nameValidation("Rex1"));
        String expectedPetPreview = encodedVl64(55, null, "Ly");
        expectedPetPreview = encodedVl64(1, null, expectedPetPreview);
        expectedPetPreview = encodedVl64(2, null, expectedPetPreview);
        expectedPetPreview = encodedVl64(12345, null, expectedPetPreview) + "12345\2";
        assertEquals(expectedPetPreview, PetPayloads.packagePreview(55L, 1L, 2L, "12345"));
        assertEquals(75L, PetWire.packagePreviewRequest("p`" + wireLong(75)).furnitureId());
        assertEquals(75L, PetWire.packagePreviewRequest("rt" + wireLong(75)).furnitureId());
        PetWire.PackagePlacementRequest petPlacementRequest =
            PetWire.packagePlacementRequest("n~" + wireLong(93) + wireString("Buddy"));
        assertEquals(93L, petPlacementRequest.furnitureId());
        assertEquals("Buddy", petPlacementRequest.petName());
        assertEquals("dog", PetWire.raceListRequest("n\u007f" + wireString("dog")).productPet());
        PetWire.RoomPlacementRequest petRoomPlacement =
            PetWire.roomPlacementRequest("nz" + wireLong(10) + wireLong(2) + wireLong(3) + wireLong(4));
        assertEquals(10L, petRoomPlacement.petId());
        assertEquals(2L, petRoomPlacement.positionX());
        assertEquals(3L, petRoomPlacement.positionY());
        assertEquals(4L, petRoomPlacement.rotation());
        assertEquals("Rex", PetWire.nameValidationRequest("@c" + wireString("Rex")).petName());
        assertEquals(10L, PetWire.petIdRequest("ny" + wireLong(10), "ny").petId());
        assertEquals(10L, PetWire.petIdRequest("n|" + wireLong(10), "n|").petId());
        assertEquals(10L, PetWire.petIdRequest("n}" + wireLong(10), "n}").petId());
        assertEquals(0L, PetWire.petIdRequest("Fy" + wireLong(0), "Fy").petId());
        PetWire.CommandRequest petCommandRequest = PetWire.commandRequest("n{" + wireLong(10) + wireLong(2));
        assertEquals(10L, petCommandRequest.petId());
        assertEquals(2L, petCommandRequest.commandId());
        assertEquals(encodedVl64(55, null, "Lz") + encodedVl64(2, null, "") + "Rex1\2",
            PetPayloads.packageNameValidation(55L, 2L, "Rex1"));
        List<PetSettings.PetCommandRow> commandRows = List.of(
            new PetSettings.PetCommandRow(1L, 0L, "sit", "gst ok", 4),
            new PetSettings.PetCommandRow(2L, 3L, "jump", "gst jump", 4),
            new PetSettings.PetCommandRow(3L, 5L, "high", "gst high", 4));
        String expectedCommandList = encodedVl64(3, null, "I]")
            + encodedVl64(3, null, "")
            + "0" + encodedVl64(1, null, "")
            + "0" + encodedVl64(2, null, "")
            + "0" + encodedVl64(3, null, "")
            + encodedVl64(2, null, "")
            + "0" + encodedVl64(1, null, "")
            + "0" + encodedVl64(2, null, "");
        assertEquals(expectedCommandList, PetPayloads.commandList(3, commandRows));
        PetCommandAction commandAction = PetLookups.commandAction(2, commandRows, null);
        assertEquals(true, commandAction.found());
        assertEquals(3L, commandAction.requiredLevel());
        assertEquals("gst jump", commandAction.action());
        assertEquals(false, PetLookups.commandAction(9, commandRows, null).found());
        MySQL.configureDatabaseConnection(new Database() {
            @Override
            public void execute(String sqlText) {
            }

            @Override
            public List<List<Object>> query(String sqlText) {
                if (sqlText.contains("bots_petcommands WHERE id_command='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(7, "fallback-action"));
                }
                if (sqlText.contains("bots_petlevels WHERE id_level='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(42));
                }
                return Arrays.<List<Object>>asList(Arrays.<Object>asList());
            }
        });
        BotDao fallbackBots = new BotDao(MySQL.configuredDatabase());
        PetCommandAction fallbackCommandAction = PetLookups.commandAction(9, List.of(), fallbackBots);
        assertEquals(true, fallbackCommandAction.found());
        assertEquals(7L, fallbackCommandAction.requiredLevel());
        assertEquals("fallback-action", fallbackCommandAction.action());
        assertEquals(42L, PetLookups.levelMaxExperience(9, List.of(), fallbackBots));
        MySQL.configureDatabaseConnection(null);
        String expectedPetStatus = "IY" + encodedVl64(50, null, "") + "Rex\2"
            + encodedVl64(2, null, "")
            + encodedVl64(7, null, "")
            + encodedVl64(100, null, "")
            + encodedVl64(90, null, "")
            + encodedVl64(4, null, "") + "1 2 ff\2"
            + encodedVl64(12, null, "")
            + encodedVl64(5, null, "") + "Owner\2";
        PetStatusRow expectedPetStatusRow = new PetStatusRow(10L, "Rex", "1 2 ff", 2L, 7L, 100L, 90L, 4L, 12L, 5L, "Owner");
        assertEquals(expectedPetStatus, PetPayloads.status(50, expectedPetStatusRow));
        List<PetSettings.PetLevelRow> levelRows = List.of(
            new PetSettings.PetLevelRow(1L, 0L, 10L, 0L, 2),
            new PetSettings.PetLevelRow(2L, 0L, 20L, 0L, 2),
            new PetSettings.PetLevelRow(3L, 0L, 30L, 0L, 2));
        assertEquals(20L, PetProgress.levelMaxExperience(2, levelRows));
        PetExperienceUpdate expUpdate = PetProgress.experienceUpdate(50, "Rex", "1 2 ff", 2, 18, 100, 90, 4, 3, levelRows);
        assertEquals(3L, expUpdate.petLevel());
        assertEquals(0L, expUpdate.petExperience());
        assertEquals(true, expUpdate.leveledUp());
        assertEquals("IY" + encodedVl64(50, null, "") + "Rex\2"
            + encodedVl64(3, null, "")
            + encodedVl64(0, null, "")
            + encodedVl64(100, null, "")
            + encodedVl64(90, null, "")
            + encodedVl64(4, null, "") + "1 2 ff\2", expUpdate.statusPayload());
        assertEquals("Ia" + encodedVl64(50, null, "")
            + encodedVl64(3, null, "")
            + encodedVl64(0, null, ""), expUpdate.experiencePayload());
        assertEquals("I^" + encodedVl64(50, null, "")
            + encodedVl64(5, null, "")
            + encodedVl64(6, null, "")
            + "Rex\2" + "1 2 ff\2", PetPayloads.scratch(50, 5, 6, "Rex", "1 2 ff"));
        assertEquals("IZ" + encodedVl64(50, null, "") + "gst jump\2"
            + encodedVl64(2, null, ""), PetPayloads.commandAction(50, "gst jump", 2));
        assertEquals("@X" + encodedVl64(50, null, "") + "gst sml\2H",
            PetPayloads.speech(50, "gst sml"));
        PetState.instance().setRepresentedBots(RepresentedBotRegistry.empty());
        RepresentedBotEntry representedBotEntry = new RepresentedBotEntry(
            501L, "Guide", "hello", "speech", "responses", 2L, 3L, "0.5", 4L, "1 2 ff",
            3L, 4L, "cache", "submit", 1L, 6L);
        long botEntityId = PetState.instance().allocateRepresentedBot(3, representedBotEntry);
        assertEquals(1L, botEntityId);
        assertEquals(true, PetState.instance().representedBots().recordsByEntityId().containsKey(botEntityId));
        assertEquals(List.of(1L), PetState.instance().representedBots().allocatedEntityIds());
        assertEquals(3L, PetState.instance().representedBots().record(botEntityId).roomSlot());
        assertEquals(501L, PetState.instance().representedBots().record(botEntityId).botId());
        assertEquals("Guide", PetState.instance().representedBots().record(botEntityId).name());
        assertEquals(1L, PetState.instance().representedBots().entityFromBotId(501));
        assertEquals(List.of(1L), PetState.instance().representedBots().entityIdsForRoom(3, 501));
        assertEquals(true, !PetState.instance().representedBots().entityIdsForRoom(3, 501).isEmpty());
        PetState.instance().storeRepresentedBotPosition(botEntityId, 5, 6, "1.0", 7);
        assertEquals(5L, PetState.instance().representedBots().record(botEntityId).positionX());
        assertEquals("1.0", PetState.instance().representedBots().record(botEntityId).positionZ());
        String expectedBotEntry = "@\\" + encodedVl64(botEntityId, null, "")
            + "Guide\2" + "5 6 1.0\2" + "7\2" + "1 2 ff\2";
        assertEquals(expectedBotEntry, PetState.instance().representedBotRoomEntryPayload(botEntityId));
        assertEquals(expectedBotEntry, PetPayloads.representedBotRoomEntry(
            botEntityId, "Guide", 5L, 6L, "1.0", 7L, "1 2 ff"));
        PetState.instance().removeRepresentedBotRecord(botEntityId);
        assertEquals(0L, PetState.instance().representedBots().record(botEntityId).botId());
        assertEquals(List.of(), PetState.instance().representedBots().allocatedEntityIds());
        String expectedProfile = encodedVl64(9, null, "Jf")
            + "Alice\2motto\2"
            + encodedVl64(123, null, "")
            + "fig\2";
        assertEquals(expectedProfile, SocialPayloads.roomUserProfile(9, "Alice", "motto", 123, "fig"));
        assertEquals("Ge" + encodedVl64(9, null, "") + encodedVl64(12, null, ""),
            SocialPayloads.roomUserEffect(9L, 12L));
        assertEquals(encodedVl64(9, null, "Ge") + "H", SocialPayloads.roomUserEffectCleared(9L));
        assertEquals("0" + encodedVl64(4, null, encodedVl64(9, null, "Ge")),
            SocialPayloads.roomUserStatus(9L, 4L));
        assertEquals("", SocialPayloads.roomUserStatus(0L, 4L));
        assertEquals(encodedVl64(9, null, "Ga"), SocialPayloads.roomUserWave(9L));
        SocialLookups.RoomUserAction waveAction = SocialLookups.roomUserWaveAction("77", 9L, 9L);
        assertEquals(9L, waveAction.resultValue());
        assertEquals(SocialPayloads.roomUserWave(9L), waveAction.payload());
        assertEquals(false, SocialLookups.roomUserWaveAction("0", 9L, 9L).valid());
        assertEquals(encodedVl64(3, null,
            encodedVl64(9, null, "G`")), SocialPayloads.roomUserDance(9L, 3L));
        SocialLookups.RoomUserAction danceAction =
            SocialLookups.roomUserDanceAction("77", 9L, 9L, SocialWire.danceRequest("A]" + wireLong(3)));
        assertEquals(3L, danceAction.resultValue());
        assertEquals(SocialPayloads.roomUserDance(9L, 3L), danceAction.payload());
        assertEquals(false, SocialLookups.roomUserDanceAction("77", 0L, 9L,
            SocialWire.danceRequest("A]" + wireLong(3))).valid());
        assertEquals(encodedVl64(9, null, "@\\"), SocialPayloads.roomUserRemoved(9L));
        assertEquals(SocialPayloads.roomUserRemoved(9L), SocialLookups.roomUserRemovedPayload(9L));
        assertEquals(encodedVl64(9, null, "Ei") + '\r', SocialPayloads.roomUserPreReadyState(9L));
        assertEquals(encodedVl64(9, null, "") + " 2 3 1.0 4 4/\r",
            SocialPayloads.roomOccupantStatus(9L, 2L, 3L, "1.0", 4L));
        assertEquals("00" + encodedVl64(9, null, "Am") + encodedVl64(1, null, ""),
            SocialPayloads.interactionStateForSource(9L, 1L));
        assertEquals(encodedVl64(9, null, "Am") + encodedVl64(1, null, ""),
            SocialPayloads.interactionStateForTarget(9L, 1L));
        assertEquals(SocialPayloads.interactionStateForSource(9L, 1L),
            SocialLookups.interactionStatePayloads(9L, 1L).sourcePayload());
        assertEquals(SocialPayloads.interactionStateForTarget(9L, 1L),
            SocialLookups.interactionStatePayloads(9L, 1L).targetPayload());
        assertEquals(encodedVl64(88, null, encodedVl64(77, null, "Ah")),
            SocialPayloads.interactionRequest(77L, 88L));
        assertEquals(SocialPayloads.interactionRequest(77L, 88L),
            SocialLookups.interactionRequestPayload(77L, 88L));
        assertEquals("0" + encodedVl64(9, null, "An"), SocialPayloads.interactionClosed(9L));
        assertEquals(SocialPayloads.interactionClosed(9L), SocialLookups.interactionClosedPayload(9L));
        String equippedBadges = "0" + encodedVl64(1, null, "") + "ACH1\2"
            + "0" + encodedVl64(3, null, "") + "VIP\2";
        assertEquals(encodedVl64(2, null, "") + equippedBadges,
            SocialPayloads.equippedBadges(List.of(
                new BadgeRow("ACH1", 1L, 10L),
                new BadgeRow("VIP", 3L, 11L))));
        assertEquals(encodedVl64(0, null, ""), SocialPayloads.equippedBadges(List.of()));
        String expectedBadgeInventory = encodedVl64(2, null, "Ce")
            + "0" + encodedVl64(20, null, "") + "ACH2\2"
            + "0" + encodedVl64(21, null, "") + "MOD\2"
            + encodedVl64(2, null, "") + equippedBadges;
        assertEquals(expectedBadgeInventory, SocialPayloads.badgeInventory(List.of(
                new BadgeRow("ACH2", 0L, 20L),
                new BadgeRow("MOD", 0L, 21L)),
            List.of(new BadgeRow("ACH1", 1L, 10L), new BadgeRow("VIP", 3L, 11L))));
        assertEquals("Cd" + encodedVl64(5, null, "") + encodedVl64(2, null, "") + equippedBadges,
            SocialPayloads.badgeDisplay(5, List.of(new BadgeRow("ACH1", 1L, 10L), new BadgeRow("VIP", 3L, 11L))));
        assertEquals(encodedVl64(2, null, "") + "one\2two\2",
            SocialPayloads.tags(List.of(new UserDao.UserTagRow("one"), new UserDao.UserTagRow("two"))));
        assertEquals(encodedVl64(0, null, ""), SocialPayloads.tags(List.of()));
        assertEquals("E^" + encodedVl64(5, null, "") + encodedVl64(2, null, "") + "one\2two\2",
            SocialPayloads.tagDisplay(5, List.of(new UserDao.UserTagRow("one"), new UserDao.UserTagRow("two"))));
        String badgeWire = "A@CONEA@CTWO";
        BadgeUpdateSelections badgeSlots = SocialWire.badgeUpdateSelections("B^" + badgeWire);
        assertEquals("ONE", badgeSlots.first());
        assertEquals("TWO", badgeSlots.second());
        assertEquals("", badgeSlots.third());
        assertEquals("", badgeSlots.fourth());
        assertEquals("", badgeSlots.fifth());
        assertEquals(61L, SocialWire.roomUserIndexRequest("Cg" + wireLong(61), "Cg").roomUserIndex());
        assertEquals(88L, SocialWire.userIdRequest("Es" + wireLong(88), "Es").userId());
        assertEquals(88L, SocialWire.userIdRequest("DG" + wireLong(88), "DG").userId());
        assertEquals("Target", SocialWire.followUserRequest("AbTarget").targetName());
        assertEquals("Target", SocialWire.followUserRequest("Ab" + wireString("Target")).targetName());
        SocialLookups.FollowRoomAction failedFollowAction =
            SocialLookups.followRoomAction(SocialWire.followUserRequest("Ab"), null);
        assertEquals(false, failedFollowAction.canEnterRoom());
        assertEquals("BC", failedFollowAction.failurePayload());
        assertEquals(61L, SocialWire.roomUserIndexRequest("B_" + wireLong(61), "B_").roomUserIndex());
        assertEquals(3L, SocialWire.danceRequest("A]" + wireLong(3)).danceId());
        assertEquals(4L, SocialWire.danceRequest("A]" + wireLong(9)).danceId());
        assertEquals(61L, SocialWire.roomUserIndexRequest("AG" + wireLong(61), "AG").roomUserIndex());
        assertEquals(12L, SocialWire.effectRequest("Fx" + wireLong(12)).effectId());
        SocialWire.RepresentedChatMessage chatMessage =
            SocialWire.representedChatMessage("@t" + wireString("hello"), 0L);
        assertEquals("", chatMessage.targetName());
        assertEquals("hello", chatMessage.messageText());
        SocialWire.RepresentedChatMessage whisperMessage =
            SocialWire.representedChatMessage("@x" + wireString("Target") + wireString("secret"), 2L);
        assertEquals("Target", whisperMessage.targetName());
        assertEquals("secret", whisperMessage.messageText());
        SocialWire.RepresentedChatMessage fallbackWhisper =
            SocialWire.representedChatMessage("@xTarget raw secret", 2L);
        assertEquals("Target", fallbackWhisper.targetName());
        assertEquals("raw secret", fallbackWhisper.messageText());
        assertEquals(1L, WireRequests.id("CkA", "Ck"));
        assertEquals(encodedVl64(0, null, encodedVl64(1, null, "G{")),
            RecyclerPayloads.status(42, 0));
        assertEquals(encodedVl64(506, null, "G|"), RecyclerPayloads.reward(506));
        assertEquals(7L, PollWire.idFromWire("Ck" + encodedVl64(7, null, ""), "Ck"));
        PollAnswerSubmission pollAnswer = PollWire.answerFromWire("Cl"
            + encodedVl64(7, null, "")
            + encodedVl64(8, null, "")
            + encodedVl64(4, null, "")
            + "@Cyes", "Cl");
        assertEquals(true, pollAnswer.valid());
        assertEquals(7L, pollAnswer.pollId());
        assertEquals(8L, pollAnswer.questionId());
        assertEquals(4L, pollAnswer.answerValue());
        assertEquals("yes", pollAnswer.answerText());
        PollAnswerSubmission numericPollAnswer = PollWire.answerFromWire("Cl"
            + encodedVl64(7, null, "")
            + encodedVl64(8, null, "")
            + encodedVl64(4, null, ""), "Cl");
        assertEquals("4", numericPollAnswer.answerText());
        PollDefinition testPoll = new PollDefinition(
            new PollHeader(7L, "Title", "Thanks"),
            List.of(new PollQuestionRow(
                8L,
                "Question?",
                2L,
                List.of(
                    new PollAnswerRow(1L, 8L, "Yes"),
                    new PollAnswerRow(2L, 8L, "No")))));
        String expectedQuestionPayload = encodedVl64(8, null, "");
        expectedQuestionPayload = encodedVl64(1, null, expectedQuestionPayload);
        expectedQuestionPayload = encodedVl64(2, null, expectedQuestionPayload);
        expectedQuestionPayload += "Question?\2";
        expectedQuestionPayload = encodedVl64(2, null, expectedQuestionPayload);
        expectedQuestionPayload = encodedVl64(0, null, expectedQuestionPayload);
        expectedQuestionPayload = encodedVl64(2, null, expectedQuestionPayload) + "Yes\2No\2";
        String expectedPollPayload = encodedVl64(7, null, "D}") + "Title\2Thanks\2";
        expectedPollPayload = encodedVl64(1, null, expectedPollPayload) + expectedQuestionPayload;
        assertEquals(expectedPollPayload, PollPayloads.poll(testPoll));
        assertEquals(encodedVl64(7, null, "D|") + "Title\2",
            PollPayloads.prompt(new PollPrompt(7L, "Title")));
        String recyclerWire = "F^" + encodedVl64(5, null, "")
            + encodedVl64(10, null, "")
            + encodedVl64(11, null, "")
            + encodedVl64(12, null, "")
            + encodedVl64(13, null, "")
            + encodedVl64(14, null, "");
        RecyclerSelection recyclerSelection = RecyclerWire.selectionFromWire(recyclerWire);
        assertEquals(true, recyclerSelection.valid());
        assertEquals(5L, recyclerSelection.requestedCount());
        assertEquals(List.of(10L, 11L, 12L, 13L, 14L), recyclerSelection.selectedItemIds());
        RecyclerSelection duplicateRecyclerSelection = RecyclerWire.selectionFromWire("F^"
            + encodedVl64(5, null, "")
            + encodedVl64(10, null, "")
            + encodedVl64(11, null, "")
            + encodedVl64(10, null, "")
            + encodedVl64(13, null, "")
            + encodedVl64(14, null, ""));
        assertEquals(false, duplicateRecyclerSelection.valid());
        AchievementSettings.Achievement achievement = new AchievementSettings.Achievement(42L, "ACH_", 10L, 5L, 3L, 7L, 2L);
        List<AchievementSettings.Achievement> achievements = List.of(achievement);
        List<AchievementSettings.IndexedAchievement> indexedAchievements = AchievementSettings.indexedAchievements(achievements);
        AchievementSettings typedAchievements = AchievementSettings.fromAchievements("42\2", achievements);
        assertEquals(achievement, typedAchievements.achievementByIndex(0L));
        assertAchievementRows(typedAchievements, indexedAchievements);
        String expectedAchievementReward = encodedVl64(1, null, "Fu");
        expectedAchievementReward = encodedVl64(42, null, expectedAchievementReward);
        expectedAchievementReward = encodedVl64(99, null, expectedAchievementReward) + "ACH_2\2";
        expectedAchievementReward = encodedVl64(5, null,
            encodedVl64(7, null, expectedAchievementReward)) + "HHH\2" + "3\2";
        assertEquals(expectedAchievementReward, AchievementPayloads.reward(1, achievement, 2, 99));
        assertEquals(encodedVl64(2, null,
            encodedVl64(5, null, encodedVl64(7, null, "Fv"))),
            AchievementPayloads.award(achievement));
        Map<String, Long> achievementLevels = new HashMap<>();
        achievementLevels.put("ACH_", 2L);
        String expectedAchievementEntry = encodedVl64(42, null, "");
        expectedAchievementEntry = encodedVl64(2, null, expectedAchievementEntry);
        expectedAchievementEntry = encodedVl64(20, null, expectedAchievementEntry);
        expectedAchievementEntry = encodedVl64(10, null, expectedAchievementEntry);
        expectedAchievementEntry = encodedVl64(5, null, expectedAchievementEntry);
        expectedAchievementEntry = encodedVl64(7, null, expectedAchievementEntry);
        expectedAchievementEntry = encodedVl64(2, null, expectedAchievementEntry);
        expectedAchievementEntry = encodedVl64(3, null, expectedAchievementEntry) + "ACH_\2" + "2\2";
        assertEquals(encodedVl64(1, null, "Ft") + expectedAchievementEntry,
            AchievementPayloads.list(achievements, achievementLevels));
        AchievementProgressDecision achievementDecision = AchievementProgress.decision(
            indexedAchievements, 42, achievementLevels, 30);
        assertEquals(0L, achievementDecision.achievementIndex());
        assertEquals(3L, achievementDecision.nextLevel());
        assertEquals(30L, achievementDecision.requiredProgress());
        assertEquals(true, achievementDecision.shouldReward());
        assertEquals("5;1;7;1;5;0;", WiredPayloads.specialState(1507));
        assertEquals("", WiredPayloads.specialState(1));
        String wiredRecord = wiredRecordText(502, 44, "100;101", "7;8", "txt", "9");
        assertEquals("\1" + "502\2" + "44\3" + "100;101\4" + "7;8\5" + "txt\6" + "9", wiredRecord);
        String wiredWire = "ok" + encodedVl64(44, null, "")
            + encodedVl64(2, null, "")
            + encodedVl64(7, null, "")
            + encodedVl64(8, null, "")
            + "@Ctxt"
            + encodedVl64(2, null, "")
            + encodedVl64(100, null, "")
            + encodedVl64(101, null, "")
            + encodedVl64(9, null, "");
        assertEquals(wiredRecord, WiredWire.editRecord(wiredWire, "ok", 502, true));
        assertEquals(44L, WiredWire.editFurnitureRequest(wiredWire, "ok").furnitureId());
        assertEquals(44L, WiredWire.snapshotRequest("on" + encodedVl64(44, null, "")).furnitureId());
        String replacementRecord = wiredRecordText(502, 44, "102", "5", "next", "3");
        String otherRecord = wiredRecordText(503, 45, "200", "1", "", "");
        assertEquals(true, WiredPayloads.selectedItemsExist(List.of(100L, 101L), "99,100,101"));
        assertEquals(false, WiredPayloads.selectedItemsExist(List.of(100L, 101L), "99,100"));
        WiredPayloads.ApplyResult wiredApply = WiredPayloads.applySelected(
            List.of(100L, 101L, 102L), "5;ignored", 0, "100;102", FurniturePayloads::stateChanged);
        assertEquals(2L, wiredApply.appliedCount());
        assertEquals(FurniturePayloads.stateChanged(100, 5) + FurniturePayloads.stateChanged(102, 5), wiredApply.statePayloads());
        WiredPayloads.ApplyResult wiredOverride = WiredPayloads.applySelected(
            List.of(100L), "7", 101, "100;101", FurniturePayloads::stateChanged);
        assertEquals(1L, wiredOverride.appliedCount());
        assertEquals(FurniturePayloads.stateChanged(101, 7), wiredOverride.statePayloads());
        Path previousApplicationPathForWired = Path.of(AppPaths.applicationPath());
        ProductCache previousProductCacheForWired = GameDataCaches.productCache();
        Path wiredRoot = Files.createTempDirectory("alphaseries-wired");
        AppPaths.setApplicationPath(wiredRoot.toString());
        List<ProductCache.ProductRow> wiredProducts = List.of(productCacheRow(600, "27", "502"));
        GameDataCaches.setProductCache(ProductCache.fromProductRows(wiredProducts));
        final List<String> wiredSql = new ArrayList<>();
        final List<String> wiredSends = new ArrayList<>();
        MusConnectionManager.instance().configureSink((socketIndex, payload) -> wiredSends.add(socketIndex + ":" + payload));
        MySQL.configureDatabaseConnection(new Database() {
            @Override
            public void execute(String sqlText) {
                wiredSql.add(sqlText);
            }

            @Override
            public List<List<Object>> query(String sqlText) {
                if (sqlText.contains("SELECT id FROM users WHERE id_socket='4'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(77));
                }
                if (sqlText.contains("SELECT id_room FROM logs_visitedrooms WHERE id_user='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(9));
                }
                if (sqlText.contains("rooms WHERE id='9' AND id_owner='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(9));
                }
                if (sqlText.contains("SELECT id_product FROM furnitures WHERE id='44' AND id_room='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(600));
                }
                if (sqlText.contains("SELECT COUNT(*) FROM furnitures WHERE id='100' AND id_room='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(1));
                }
                if (sqlText.contains("SELECT COUNT(*) FROM furnitures WHERE id='101' AND id_room='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(1));
                }
                if (sqlText.contains("SELECT COUNT(*) FROM furnitures WHERE id='102' AND id_room='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(1));
                }
                if (sqlText.contains("SELECT users.id_socket FROM logs_visitedrooms,users WHERE logs_visitedrooms.id_room='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(4));
                }
                return new ArrayList<List<Object>>();
            }
        });
        SessionState.instance().setSessionRegistry(sessionRegistry(
            new SessionRegistry.SessionRecord("1:4", "77\2" + "4"),
            new SessionRegistry.SessionRecord("4", "0\2" + "9")));
        WiredLookups.RoomRequest wiredRequest =
            WiredLookups.roomRequest(4, new UserDao(MySQL.configuredDatabase()), new RoomDao(MySQL.configuredDatabase()));
        assertEquals(true, wiredRequest.valid());
        assertEquals("77", wiredRequest.userId());
        assertEquals(9L, wiredRequest.roomId());
        String liveWiredRecord = WiredLookups.editRecord(
            4,
            wiredRequest,
            wiredWire,
            "ok",
            501,
            1000,
            "wired_action",
            true,
            new FurnitureDao(MySQL.configuredDatabase()),
            new RoomDao(MySQL.configuredDatabase()));
        assertEquals(wiredRecord, liveWiredRecord);
        assertEquals(true, FileUtils.readTextFile(
            wiredRoot.resolve("cache").resolve("wired_action").resolve("9.cache").toString()).contains(wiredRecord));
        WiredPayloads.WiredRecord parsedWiredRecord = WiredCache.records("wired_action", 9).get(0);
        assertEquals("502", parsedWiredRecord.code());
        assertEquals("44", parsedWiredRecord.furnitureId());
        assertEquals("100;101", parsedWiredRecord.selectedIds());
        assertEquals(List.of(100L, 101L), parsedWiredRecord.selectedFurnitureIds());
        assertEquals("7;8", parsedWiredRecord.parameterText());
        assertEquals("txt", parsedWiredRecord.textValue());
        assertEquals("9", parsedWiredRecord.extraValue());
        WiredCache.appendRecord("wired_action", 19, otherRecord);
        WiredCache.appendRecord("wired_action", 19, replacementRecord);
        assertEquals(List.of("502", "503"), WiredCache.records("wired_action", 19).stream()
            .map(WiredPayloads.WiredRecord::code)
            .toList());
        assertEquals("102", WiredCache.records("wired_action", 19).get(0).selectedIds());
        assertEquals(true, FurnitureLookups.selectedItemsExistInRoom(
            9, List.of(100L, 101L), new FurnitureDao(MySQL.configuredDatabase())));
        FurnitureStateWrites.WiredStateApplyResult wiredStateApply = FurnitureStateWrites.applyWiredSelectedStates(
            RoomState.instance().furnitureRoomCache(), 9, List.of(100L, 101L), "7;8", 0,
            new FurnitureDao(MySQL.configuredDatabase()));
        assertEquals(2L, wiredStateApply.appliedCount());
        assertEquals(List.of(FurniturePayloads.stateChanged(100, 7), FurniturePayloads.stateChanged(101, 7)),
            wiredStateApply.broadcastPayloads());
        RoomState.instance().setRepresentedRooms(RepresentedRoomCache.fromCacheText("snapshot-room-cache"));
        String snapshotPath = WiredLookups.createSnapshot(
            4,
            wiredRequest,
            "on" + encodedVl64(44, null, ""),
            new FurnitureDao(MySQL.configuredDatabase()),
            new RoomDao(MySQL.configuredDatabase()));
        assertEquals(wiredRoot.resolve("cache").resolve("wired_snapshots").resolve("44.cache").toString(), snapshotPath);
        assertEquals("snapshot-room-cache" + System.lineSeparator(), new String(Files.readAllBytes(Path.of(snapshotPath)), "UTF-8"));
        String triggerRecord = wiredRecordText(1001, 55, "", "", "", "");
        FileUtils.writeTextFile(wiredRoot.resolve("cache").resolve("wired_trigger").resolve("9.cache").toString(), triggerRecord);
        assertEquals(2L, WiredLookups.trigger(
            9,
            1001,
            0,
            new FurnitureDao(MySQL.configuredDatabase()),
            (roomId, payload) -> wiredSends.add(roomId + ":" + payload)));
        assertEquals(true, containsSql(wiredSql, "UPDATE furnitures SET sign='7' WHERE id='100' LIMIT 1"));
        assertEquals(true, containsSql(wiredSql, "UPDATE furnitures SET sign='7' WHERE id='101' LIMIT 1"));
        wiredSql.clear();
        wiredSends.clear();
        String action503 = wiredRecordText(503, 45, "102", "8", "", "");
        FileUtils.writeTextFile(wiredRoot.resolve("cache").resolve("wired_action").resolve("9.cache").toString(), action503);
        assertEquals(1L, WiredLookups.action(
            9,
            503,
            0,
            new FurnitureDao(MySQL.configuredDatabase()),
            (roomId, payload) -> wiredSends.add(roomId + ":" + payload)));
        assertEquals(true, containsSql(wiredSql, "UPDATE furnitures SET sign='8' WHERE id='102' LIMIT 1"));
        SessionState.instance().setSessionRegistry(SessionRegistry.empty());
        MusConnectionManager.instance().configureSink(null);
        MySQL.configureDatabaseConnection(null);
        AppPaths.setApplicationPath(previousApplicationPathForWired.toString());
        GameDataCaches.setProductCache(previousProductCacheForWired);
        String songInfoWire = "C]" + encodedVl64(2, null, "")
            + encodedVl64(50, null, "")
            + encodedVl64(51, null, "");
        SongInfoRequest songInfoRequest = JukeboxRequests.songInfoFromWire(songInfoWire);
        assertEquals(2L, songInfoRequest.requestedCount());
        assertEquals("50,51", songInfoRequest.requestedIds());
        assertEquals(List.of(50L, 51L), songInfoRequest.requestedIdList());
        String expectedCdPayload = encodedVl64(50, null, "");
        expectedCdPayload = encodedVl64(3, null, expectedCdPayload) + "Song A\2Author A\2sound-a\2";
        expectedCdPayload += encodedVl64(4, null, encodedVl64(51, null, ""))
            + "Song B\2Author B\2sound-b\2";
        assertEquals(encodedVl64(2, null, "Dl") + expectedCdPayload,
            JukeboxPayloads.songInfo(List.of(
                new SongInfoRow("Song A", 3L, "Author A", "sound-a", 50L),
                new SongInfoRow("Song B", 4L, "Author B", "sound-b", 51L))));
        JukeboxAddRequest addRequest = JukeboxRequests.addRequestFromWire("C" + '\177'
            + encodedVl64(77, null, "")
            + encodedVl64(2, null, ""));
        assertEquals(77L, addRequest.diskFurnitureId());
        assertEquals(2L, addRequest.playlistOrder());
        assertEquals(true, JukeboxRequests.canAddDisk(2, "1", 1, 10));
        assertEquals(false, JukeboxRequests.canAddDisk(3, "1", 1, 10));
        assertEquals(true, JukeboxRequests.canAddDisk(0, "", 0, 0));
        assertEquals(3L, JukeboxRequests.removeOrderFromWire("D@" + encodedVl64(3, null, "")));
        String expectedPlaylist = encodedVl64(2, null, "");
        expectedPlaylist += encodedVl64(40, null, "");
        expectedPlaylist += encodedVl64(3, null, "");
        expectedPlaylist += encodedVl64(41, null, "");
        assertEquals(encodedVl64(5, null, encodedVl64(2, null, "EN")) + expectedPlaylist,
            JukeboxPayloads.playlist(5, List.of(
                new JukeboxPlaylistEntry(2L, 40L),
                new JukeboxPlaylistEntry(3L, 41L))));
        assertEquals(encodedVl64(2, null, "EM") + expectedPlaylist,
            JukeboxPayloads.diskInventory(List.of(
                new SongDiskRow(2L, 40L),
                new SongDiskRow(3L, 41L))));
        String expectedPlayback = encodedVl64(10, null, "EG");
        expectedPlayback = encodedVl64(3, null, expectedPlayback);
        expectedPlayback = encodedVl64(40, null, expectedPlayback);
        expectedPlayback = encodedVl64(2, null, expectedPlayback);
        expectedPlayback = encodedVl64(0, null, encodedVl64(0, null, expectedPlayback));
        assertEquals(expectedPlayback, JukeboxPayloads.playback(10, 3, 40, 2));
        RoomDao.RoomSettingsRead settingsReadRow = new RoomDao.RoomSettingsRead(
            7L, "Room", "Desc", 2L, 4L, 25L, 30L, "tag1", "tag2", 1L, 0L, 1L, 0L);
        List<RoomDao.RoomRight> roomRights = List.of(
            new RoomDao.RoomRight(5L, "Alice"),
            new RoomDao.RoomRight(6L, "Bob"));
        String expectedRoomSettings = encodedVl64(7, null, "GQ") + "Room\2Desc\2";
        expectedRoomSettings = encodedVl64(2, null, expectedRoomSettings);
        expectedRoomSettings = encodedVl64(4, null, expectedRoomSettings);
        expectedRoomSettings = encodedVl64(25, null, expectedRoomSettings);
        expectedRoomSettings = encodedVl64(30, null, expectedRoomSettings);
        expectedRoomSettings = encodedVl64(2, null, expectedRoomSettings) + "tag1\2tag2\2";
        expectedRoomSettings = encodedVl64(2, null, expectedRoomSettings);
        expectedRoomSettings += encodedVl64(5, null, "") + "Alice\2";
        expectedRoomSettings += encodedVl64(6, null, "") + "Bob\2H";
        expectedRoomSettings = encodedVl64(1, null, expectedRoomSettings);
        expectedRoomSettings = encodedVl64(0, null, expectedRoomSettings);
        expectedRoomSettings = encodedVl64(1, null, expectedRoomSettings);
        expectedRoomSettings = encodedVl64(0, null, expectedRoomSettings);
        assertEquals(expectedRoomSettings, RoomPayloads.settingsRead(settingsReadRow, roomRights));
        assertEquals(encodedVl64(7, null, "GI") + '\2', RoomPayloads.iconUpdated(7));
        assertEquals(encodedVl64(7, null, "GH"), RoomPayloads.entryUpdated(7));
        assertEquals(encodedVl64(7, null, "GG"), RoomPayloads.homeRoom(7));
        assertEquals(encodedVl64(7, null, "AE") + '\2', RoomPayloads.currentRoom(7));
        assertEquals(encodedVl64(2, null, "@\\") + "entries",
            RoomPayloads.occupantEntries(new SocialRoomOccupants(2L, 3L, "entries", "statuses")));
        assertEquals(encodedVl64(3, null, "Du") + "statuses",
            RoomPayloads.occupantStatuses(new SocialRoomOccupants(2L, 3L, "entries", "statuses")));
        assertEquals(encodedVl64(7, null, "@{") + "Created Room\2",
            RoomPayloads.createdRoom(7, "Created Room"));
        String expectedOfficialRoomModel = encodedVl64(7, null, "GE") + "model.cast\2";
        expectedOfficialRoomModel = encodedVl64(7, null, expectedOfficialRoomModel) + "Caption\2";
        assertEquals(expectedOfficialRoomModel,
            RoomPayloads.officialRoomModel(7, new RoomDao.OfficialRoomModel(7, 2, "model.cast", "Caption")));
        assertEquals(encodedVl64(4, null, encodedVl64(20, null, "H@")),
            RoomPayloads.creatableRoomCount(20, 4));
        assertEquals(encodedVl64(0, null, "Fc"), RoomPayloads.roomRightRemoved());
        assertEquals(encodedVl64(7, null, "GS"), RoomPayloads.settingsUpdated(7));
        assertEquals(encodedVl64(12, null, "EY"), RoomPayloads.rating(12));
        String expectedWallOptions = encodedVl64(1, null, "GX");
        expectedWallOptions = encodedVl64(2, null, expectedWallOptions);
        expectedWallOptions = encodedVl64(3, null, expectedWallOptions);
        assertEquals(expectedWallOptions, RoomPayloads.wallOptions(1, 2, 3));
        assertEquals(encodedVl64(7, null, "GK") + "H", RoomPayloads.favouriteRemoved(7));
        assertEquals(encodedVl64(7, null, "GK") + " ", RoomPayloads.favouriteAdded(7));
        Map<Long, String> staffNames = new HashMap<>();
        staffNames.put(6L, "Partner");
        staffNames.put(9L, "Picker");
        String expectedCallForHelp = StaffPayloads.callForHelp(0, 0, 8, 5, "Caller", 6, "Partner",
            "Need help", 7, "Room", 50, "Picker");
        assertEquals("HR" + expectedCallForHelp, StaffPayloads.callForHelpNotification(
            new StaffCallForHelpRow(50L, 2L, 5L, "Caller", 6L, 7L, 8L, "Need help", 7L, "Room", 9L),
            staffNames));
        assertEquals(encodedVl64(2, null, "H\\"), StaffPayloads.callForHelpClosed(2L));
        assertEquals("E@", StaffPayloads.callForHelpDeleted());
        assertEquals(encodedVl64(50, null, "EA"), StaffPayloads.callForHelpCreated(50L));
        assertEquals("BaCareful\2", StaffPayloads.alert("Careful"));
        StaffSettings moderationPanelSettings = StaffSettings.fromPayloadRows(
            List.of(new StaffSettings.ModerationPayload(2L, 0L, "MOD")));
        assertEquals(encodedVl64(0, null, "HS")
            + encodedVl64(0, null, "") + "MOD", StaffPayloads.moderationPanel(moderationPanelSettings, 2L, 0L));
        String expectedStaffSummary = encodedVl64(5, null, "HU") + "Alice\2";
        expectedStaffSummary = encodedVl64(60, null, expectedStaffSummary);
        expectedStaffSummary = encodedVl64(10, null, expectedStaffSummary);
        expectedStaffSummary = encodedVl64(1, null, expectedStaffSummary);
        expectedStaffSummary = encodedVl64(2, null, expectedStaffSummary);
        expectedStaffSummary = encodedVl64(1, null, expectedStaffSummary);
        expectedStaffSummary = encodedVl64(4, null, expectedStaffSummary);
        expectedStaffSummary = encodedVl64(0, null, expectedStaffSummary);
        assertEquals(expectedStaffSummary,
            StaffPayloads.userSummary(new StaffUserSummaryRow(5L, "Alice", 60L, 10L, 3L), 2, 1, 4, 0));
        String expectedVisit = encodedVl64(1, null, "");
        expectedVisit = encodedVl64(7, null, expectedVisit);
        expectedVisit = encodedVl64(12, null, expectedVisit);
        expectedVisit = encodedVl64(30, null, expectedVisit) + "Room\2";
        assertEquals(expectedVisit, StaffPayloads.roomVisit(new StaffRoomVisitRow(1L, 7L, "Room", 12L, 30L)));
        assertEquals(88L, StaffWire.userId("B88"));
        assertEquals(123L, StaffWire.nestedUserId("@C123"));
        assertEquals(77L, StaffWire.nestedUserId(encodedVl64(77, null, "")));
        assertEquals(88L, StaffWire.userSummaryRequest("GF" + wireLong(88)).targetUserId());
        StaffWire.BanRequest staffBanRequest =
            StaffWire.banRequest("GP" + wireLong(88) + wireString("Ban\nnow") + wireLong(2));
        assertEquals(88L, staffBanRequest.targetUserId());
        assertEquals("Ban now", staffBanRequest.banMessage());
        assertEquals(2L, staffBanRequest.banHours());
        StaffWire.RoomModerationRequest roomModerationRequest =
            StaffWire.roomModerationRequest("CH" + wireLong(1) + wireString("Room\nalert"));
        assertEquals(1L, roomModerationRequest.actionType());
        assertEquals("Room alert", roomModerationRequest.messageText());
        StaffWire.CloseCallForHelpRequest closeCallForHelpRequest =
            StaffWire.closeCallForHelpRequest("GD" + wireLong(2) + wireLong(50));
        assertEquals(2L, closeCallForHelpRequest.closeState());
        assertEquals(50L, closeCallForHelpRequest.callForHelpId());
        assertEquals(1L, StaffWire.roomLockRequest("GL" + wireLong(0) + wireLong(1)).lockFlag());
        StaffWire.DirectMessageRequest directMessageRequest =
            StaffWire.directMessageRequest("GM" + wireLong(88) + wireString("Careful\nnow"), "GM");
        assertEquals(88L, directMessageRequest.targetUserId());
        assertEquals("Careful now", directMessageRequest.messageText());
        StaffWire.SubmitCallForHelpRequest submitCallForHelpRequest =
            StaffWire.submitCallForHelpRequest("GE" + wireString("This is a long\ncall for help description")
                + wireLong(8) + wireLong(88));
        assertEquals("This is a long call for help description", submitCallForHelpRequest.descriptionText());
        assertEquals(8L, submitCallForHelpRequest.categoryId());
        assertEquals(88L, submitCallForHelpRequest.partnerUserId());
        String staffTabWire = encodedVl64(2, null, "")
            + encodedVl64(50, null, "")
            + encodedVl64(51, null, "");
        assertEquals(List.of(50L, 51L), StaffWire.callForHelpTabRequest("GB" + staffTabWire, "GB").callForHelpIds());
        assertEquals(123L, StaffWire.historyRequest("GG@C123", "GG", true).targetUserId());
        assertEquals(88L, StaffWire.historyRequest("GJB88", "GJ", false).targetUserId());
        assertEquals(12L, StaffWire.callForHelpChatLogRequest("GIB12").callForHelpId());
        assertEquals(13L, StaffWire.roomChatLogRequest("GHB13").roomId());
        assertEquals(14L, StaffWire.roomInfoRequest("GKB14").roomId());
        List<StaffRoomChatRow> staffChatRows = List.of(
            new StaffRoomChatRow(10L, 5L, 7L, "Alice", "hello"),
            new StaffRoomChatRow(11L, 6L, 8L, "Bob", "hi"));
        String expectedStaffChatRows = encodedVl64(10, null, "");
        expectedStaffChatRows += encodedVl64(5, null, "");
        expectedStaffChatRows += encodedVl64(7, null, "");
        expectedStaffChatRows += "Alice\2hello\2";
        expectedStaffChatRows += encodedVl64(11, null, "");
        expectedStaffChatRows += encodedVl64(6, null, "");
        expectedStaffChatRows += encodedVl64(8, null, "");
        expectedStaffChatRows += "Bob\2hi\2";
        StaffPayloads.ChatRows staffChat = StaffPayloads.roomChatRows(staffChatRows);
        assertEquals(2L, staffChat.chatCount());
        assertEquals(expectedStaffChatRows, staffChat.payload());
        String expectedCallForHelpChatLogResponse = encodedVl64(50, null, "HV");
        expectedCallForHelpChatLogResponse = encodedVl64(7, null, expectedCallForHelpChatLogResponse);
        expectedCallForHelpChatLogResponse = encodedVl64(1, null, expectedCallForHelpChatLogResponse);
        expectedCallForHelpChatLogResponse = encodedVl64(5, null, expectedCallForHelpChatLogResponse);
        expectedCallForHelpChatLogResponse = encodedVl64(6, null, expectedCallForHelpChatLogResponse);
        expectedCallForHelpChatLogResponse += "Room\2" + expectedStaffChatRows;
        assertEquals(expectedCallForHelpChatLogResponse, StaffPayloads.callForHelpChatLogResponse(
            50L, new StaffModerationDao.CallForHelpRoom(7L, "Room", 1L, 5L, 6L, 1000L), staffChatRows));
        String expectedRoomChatLogResponse = encodedVl64(7, null, "HW");
        expectedRoomChatLogResponse = encodedVl64(1, null, expectedRoomChatLogResponse);
        expectedRoomChatLogResponse += "Room\2" + expectedStaffChatRows;
        assertEquals(expectedRoomChatLogResponse, StaffPayloads.roomChatLogResponse(
            new StaffModerationDao.RoomChatHeader(7L, "Room", 1L), staffChatRows));
        String expectedRoomInfoResponse = encodedVl64(7, null, "HZ");
        expectedRoomInfoResponse = encodedVl64(2, null, expectedRoomInfoResponse);
        expectedRoomInfoResponse = encodedVl64(5, null, expectedRoomInfoResponse);
        expectedRoomInfoResponse += "Owner\2Room\2Desc\2tag1\2tag2\2";
        expectedRoomInfoResponse = encodedVl64(1, null, expectedRoomInfoResponse);
        expectedRoomInfoResponse += "Event\2Event desc\2etag1\2etag2\2";
        assertEquals(expectedRoomInfoResponse, StaffPayloads.roomInfoResponse(
            new StaffModerationDao.RoomInfo(7L, 2L, 5L, "Owner", "Room", "Desc", "tag1", "tag2"),
            new StaffModerationDao.RoomEvent("Event", "Event desc", "etag1", "etag2")));
        String expectedStaffChatHistory = encodedVl64(1, null, "");
        expectedStaffChatHistory = encodedVl64(7, null, expectedStaffChatHistory);
        expectedStaffChatHistory = encodedVl64(2, null, expectedStaffChatHistory) + "Room\2" + expectedStaffChatRows;
        assertEquals(expectedStaffChatHistory,
            StaffPayloads.roomChatHistory(new StaffRoomChatVisitRow(1L, 7L, "Room", 100L, 200L), staffChatRows));
        StaffUserLookup staffTarget = new StaffUserLookup(88L, "Target");
        String expectedRoomChatHistoryResponse = encodedVl64(88, null, "HX")
            + "Target\2" + encodedVl64(1, null, "") + expectedStaffChatHistory;
        assertEquals(expectedRoomChatHistoryResponse,
            StaffPayloads.roomChatHistoryResponse(staffTarget, List.of(new StaffPayloads.ChatHistoryVisit(
                new StaffRoomChatVisitRow(1L, 7L, "Room", 100L, 200L), staffChatRows))));
        String expectedRoomVisitHistoryResponse = encodedVl64(88, null, "HY")
            + "Target\2" + encodedVl64(1, null, "") + expectedVisit;
        assertEquals(expectedRoomVisitHistoryResponse,
            StaffPayloads.roomVisitHistoryResponse(staffTarget, List.of(
                new StaffRoomVisitRow(1L, 7L, "Room", 12L, 30L))));
        assertEquals(true, StaffPayloads.containsUnsafeAlert("cookie plus javascript:"));
        assertEquals(false, StaffPayloads.containsUnsafeAlert("cookie only"));

        });
        run(() -> {
        final List<String> handlingSends = new ArrayList<>();
        final List<String> handlingSql = new ArrayList<>();
        MusConnectionManager.instance().configureSink((socketIndex, payload) -> handlingSends.add(socketIndex + ":" + payload));
        Guardian.setSocketConnected(4, true);
        Guardian.setSocketConnected(8, true);
        SessionState.instance().setSessionRegistry(sessionRegistry(
            new SessionRegistry.SessionRecord("1:4", "77\2" + "4"),
            new SessionRegistry.SessionRecord("1:8", "88\2" + "8")));
        SessionState.instance().setRepresentedSockets(RepresentedSocketCache.empty());
        RecyclerState.instance().setStatusPayload("CACHE");
        assertEquals("CACHE", RecyclerState.instance().settings().statusPayload());
        List<Long> recyclerProductIds = new ArrayList<>();
        recyclerProductIds.add(501L);
        RecyclerSettings typedRecyclerSettings = RecyclerSettings.fromRewardGroups("STATUS",
            List.of(new RecyclerSettings.RewardGroup(7L, recyclerProductIds)), 508L);
        recyclerProductIds.add(502L);
        assertEquals("STATUS", typedRecyclerSettings.statusPayload());
        assertEquals(508L, typedRecyclerSettings.boxProductId());
        assertEquals(7L, typedRecyclerSettings.rewardGroups().get(0).chance());
        assertEquals(List.of(501L), typedRecyclerSettings.rewardGroups().get(0).productIds());
        RecyclerSettings cacheRecyclerSettings = RecyclerSettings.fromRewardGroups("CACHE-STATUS",
            List.of(new RecyclerSettings.RewardGroup(8L, List.of(505L))), 510L);
        assertEquals("CACHE-STATUS", cacheRecyclerSettings.statusPayload());
        assertEquals(510L, cacheRecyclerSettings.boxProductId());
        assertEquals(List.of(505L), cacheRecyclerSettings.rewardGroups().get(0).productIds());
        RecyclerState.instance().setRewards(List.of(new RecyclerSettings.RewardGroup(9L, List.of(503L, 504L))));
        assertEquals(1, RecyclerState.instance().settings().rewardGroups().size());
        assertEquals(9L, RecyclerState.instance().settings().rewardGroups().get(0).chance());
        RecyclerState.instance().setStatusPayload("STATUS-2");
        RecyclerState.instance().setBoxProductId(509L);
        assertEquals("STATUS-2", RecyclerState.instance().settings().statusPayload());
        assertEquals(509L, RecyclerState.instance().settings().boxProductId());
        assertEquals(List.of(503L, 504L), RecyclerState.instance().settings().rewardGroups().get(0).productIds());
        RecyclerState.instance().setStatusPayload("CACHE");
        HelpCenterCache typedHelpCache = HelpCenterCache.fromPayloads(
            "IMPORTANTFAQ", "FAQCATS", Map.of(1L, "CATFAQ", 7L, "CATFAQ7"),
            Map.of(2L, "FAQDESC", 9L, "FAQDESC9"));
        assertEquals("CATFAQ7", typedHelpCache.categoryFaqPayload(7L));
        assertEquals("FAQDESC9", typedHelpCache.descriptionPayload(9L));
        assertEquals(Map.of(1L, "CATFAQ", 7L, "CATFAQ7"), typedHelpCache.categoryFaqPayloads());
        assertEquals(Map.of(2L, "FAQDESC", 9L, "FAQDESC9"), typedHelpCache.descriptionPayloads());
        HelpCenterState.instance().setCache(typedHelpCache);
        assertEquals("CATFAQ7", HelpCenterState.instance().cache().categoryFaqPayload(7L));
        assertEquals("FAQDESC9", HelpCenterState.instance().cache().descriptionPayload(9L));
        ModerationState.instance().setStaffSettings(StaffSettings.fromPayloadRows(
            List.of(new StaffSettings.ModerationPayload(1L, 0L, "STAFFMOD"))));
        assertEquals("STAFFMOD", ModerationState.instance().staffSettings().moderationPayload(1L, 0L));
        assertStaffSettingsTypedAccessors();
        WiredState.instance().setStatePayload("WIREDSTATE");
        assertEquals("WIREDSTATE", WiredState.instance().settings().statePayload());
        WiredState.instance().setStatePayload("TYPED-WIRED");
        assertEquals("TYPED-WIRED", WiredState.instance().settings().statePayload());
        WiredState.instance().setSettings(WiredSettings.fromStatePayload("WIREDSTATE"));
        NavigatorState.instance().setNewFriendRooms(List.of(new NewFriendRooms.RoomPick(12L, 1L)),
                java.time.LocalDateTime.now().plusSeconds(90L));
        assertEquals(false, NavigatorState.instance().newFriendRooms().shouldRefresh(java.time.LocalDateTime.now()));
        NavigatorState.instance().setNewFriendRooms(List.of(
                new NewFriendRooms.RoomPick(12L, 1L),
                NewFriendRooms.RoomPick.empty(),
                new NewFriendRooms.RoomPick(13L, 2L)),
                java.time.LocalDateTime.now().plusSeconds(30L));
        NewFriendRooms mirroredFriendRooms = NavigatorState.instance().newFriendRooms();
        assertEquals(List.of(
                new NewFriendRooms.RoomPick(12L, 1L),
                new NewFriendRooms.RoomPick(0L, 0L),
                new NewFriendRooms.RoomPick(13L, 2L)), mirroredFriendRooms.roomPicks());
        List<NewFriendRooms.RoomPick> mutableFriendRooms = new ArrayList<>();
        mutableFriendRooms.add(new NewFriendRooms.RoomPick(21L, 3L));
        NewFriendRooms typedFriendRooms = NewFriendRooms.fromRoomPicks(mutableFriendRooms,
                java.time.LocalDateTime.now().plusSeconds(30L));
        mutableFriendRooms.add(new NewFriendRooms.RoomPick(22L, 4L));
        assertEquals(List.of(new NewFriendRooms.RoomPick(21L, 3L)), typedFriendRooms.roomPicks());
        NavigatorState.instance().setNewFriendRooms(java.util.List.of(new NewFriendRooms.RoomPick(12L, 1L)),
                java.time.LocalDateTime.now().plusSeconds(90L));
        NewFriendRooms previousNewFriendRooms = NavigatorState.instance().newFriendRooms();
        NavigatorState.instance().setNewFriendRooms(typedFriendRooms);
        assertEquals(List.of(new NewFriendRooms.RoomPick(21L, 3L)), NavigatorState.instance().newFriendRooms().roomPicks());
        NavigatorState.instance().setNewFriendRooms(previousNewFriendRooms);
        GameDataCaches.setRoomEventLocales(RoomEventLocales.fromEntries(
            List.of(new RoomEventLocales.LocaleEntry("1", List.of("events", "")))));
        Path originalApplicationPath = Path.of(AppPaths.applicationPath());
        ProductCache originalProductCache = GameDataCaches.productCache();
        CatalogRegistry originalCatalogRegistry = CatalogState.instance().registry();
        RoomCategoryCache originalRoomCategoryCache = NavigatorState.instance().roomCategoryCache();
        RecommendedRooms originalRecommendedRooms = NavigatorState.instance().recommendedRooms();
        GiftSettings originalGiftSettings = CatalogState.instance().giftSettings();
        CatalogPages originalCatalogPages = CatalogState.instance().catalogPages();
        RecyclerSettings originalRecyclerSettings = RecyclerState.instance().settings();
        AppSettingsCache originalSettingsCache = AppConfigState.instance().settingsCache();
        Path figureCachePath = Files.createTempDirectory("alphaseries-figuredata");
        AppPaths.setApplicationPath(figureCachePath.toString());
        Files.write(figureCachePath.resolve("figuredata.cache"),
            "<settype type=\"hd\"><set id=\"180\" gender=\"M\"/></settype><settype type=\"ch\"><set id=\"255\" gender=\"M\"/></settype>"
                .getBytes());
        Files.createDirectories(figureCachePath.resolve("cache").resolve("wired_trigger"));
        Files.createDirectories(figureCachePath.resolve("cache").resolve("rooms"));
        Files.createDirectories(figureCachePath.resolve("cache").resolve("items_charges"));
        Files.write(figureCachePath.resolve("cache").resolve("wired_trigger").resolve("9.cache"), "trigger-cache".getBytes());
        Files.write(figureCachePath.resolve("cache").resolve("rooms").resolve("9.cache"), "room-cache".getBytes());
        List<ProductCache.ProductRow> stickyProducts = List.of(
            productCacheRow(9, "18", "wall_sprite"),
            productCacheRow(500, "18", "post.it.vd"),
            productCacheRow(501, "17", "present_wrap_basic"),
            productCacheRow(502, "0", "2", "24", "Opened Sofa"),
            productCacheRow(503, "10", "2"),
            productCacheRow(504, "17", "CF_10"),
            productCacheRow(506, "1", "1", "13", "RewardA", "14", "Trade Chair", "15", "Trade Desc", "18", "trade_sprite"),
            productCacheRow(507, "1", "2", "14", "Wallpaper", "15", "Wallpaper Desc", "18", "paper_sprite", "20", "paper1"),
            productCacheRow(508, "12", "1", "18", "charge_sprite", "34", "3", "35", "10", "36", "2", "37", "1"),
            productCacheRow(509, "7", "static", "18", "plain_sprite"),
            productCacheRow(510, "12", "99", "17", "bb_score_blue"),
            productCacheRow(511, "0", "0", "1", "0", "17", "habbowheel", "24", "0"));
        GameDataCaches.setProductCache(ProductCache.fromProductRows(stickyProducts));
        seedCatalogRegistryProductRows(List.of(
            productDaoRow(9, "17", "wall_sprite"),
            productDaoRow(500, "17", "post.it.vd"),
            productDaoRow(501, "16", "present_wrap_basic"),
            productDaoRow(502, "0", "2", "23", "Opened Sofa"),
            productDaoRow(503, "9", "2"),
            productDaoRow(504, "16", "CF_10"),
            productDaoRow(506, "0", "1", "12", "RewardA", "13", "Trade Chair", "14", "Trade Desc", "17", "trade_sprite"),
            productDaoRow(507, "0", "2", "13", "Wallpaper", "14", "Wallpaper Desc", "17", "paper_sprite", "19", "paper1"),
            productDaoRow(508, "11", "1", "17", "charge_sprite", "33", "3", "34", "10", "35", "2", "36", "1"),
            productDaoRow(509, "6", "static", "17", "plain_sprite"),
            productDaoRow(510, "11", "99", "16", "bb_score_blue"),
            productDaoRow(511, "0", "0", "16", "habbowheel", "23", "0")));
        List<CatalogDao.CatalogProductCacheRow> catalogProducts = List.of(catalogProductRow(81, "2", "506", "4", "products",
            "5", "1", "7", "3", "8", "2", "9", "0", "10", "1", "11", "0"));
        seedCatalogRegistryCatalogProductRows(catalogProducts);
        NavigatorState.instance().setRoomCategoryPayloads(
            List.of(new RoomCategoryCache.CategoryPayload(2L, 1L, "CATEGORY_PAYLOAD")));
        NavigatorState.instance().setRecommendedRooms(Map.of(0L, "RECOMMENDED"), 1L);
        CatalogState.instance().setGiftSettings(GiftSettings.fromRows("GIFTS",
            List.of(new GiftSettings.ClubGift(81L, 506L, 20L)),
            List.of(501L),
            "WRAP_PAYLOAD"));
        seedCatalogPagePayloads(Map.of(2L, "PAGE_PAYLOAD"));
        RecyclerState.instance().setSettings(RecyclerSettings.fromRewardGroups(
            RecyclerState.instance().settings().statusPayload(),
            List.of(new RecyclerSettings.RewardGroup(1L, List.of(506L))),
            508L));
        AppConfigState.instance().setSettingsCache(settings(
            "com.server.socket.game.rooms.own.max", "5",
            "com.client.navigator.staff_picked.category.id.default", "2",
            "com.client.navigator.staff_picked.style.default", "3",
            "com.client.navigator.staff_picked.category.icon.default", "4",
            "com.server.socket.game.rooms.favourites.max", "3",
            "com.client.navigator.list.limit", "4",
            "com.mysql.format.time", "%H:%i",
            "com.client.catalog.gifts.enabled", "1",
            "com.client.catalog.gifts.wrap.enabled", "1",
            "com.client.catalog.gifts.wrap.price", "7",
            "com.client.rooms.bots.pets.enabled", "1",
            "com.client.rooms.bots.guide.enabled", "1",
            "com.client.bot.guide.id", "20",
            "com.client.catalog.recycler.enabled", "1",
            "com.server.socket.game.default.songdisk", "700",
            "com.server.socket.game.jukebox.900.soundsets.max", "5",
            "com.server.socket.game.activitypoints_0.interval", "60",
            "com.server.socket.game.activitypoints_0.max", "500",
            "com.server.socket.game.activitypoints_0.amount", "5"));
        AchievementState.instance().setSettings(AchievementSettings.fromAchievements(
            "",
            List.of(new AchievementSettings.Achievement(2L, "ACH_", 10L, 5L, 3L, 7L, 2L))));
        AppConfigState.instance().setPermissionMatrix(permissions(new PermissionMatrix.PermissionPayload(1L, 0L,
            "\2fuse_mod\2fuse_alert\2fuse_kick\2fuse_receive_calls_for_help\2fuse_chatlog\2"
                + "fuse_use_wardrobe\2fuse_larger_wardrobe\2fuse_client_staff\2")));
        MySQL.configureDatabaseConnection(new Database() {
            @Override
            public void execute(String sqlText) {
                handlingSql.add(sqlText);
            }

            @Override
            public List<List<Object>> query(String sqlText) {
                if (sqlText.contains("SELECT id FROM users WHERE id_socket='4'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(77));
                }
                if (sqlText.contains("SELECT id FROM users WHERE id_socket='8'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(88));
                }
                if (sqlText.contains("FROM users WHERE login_ticket = 'login-77'")) {
                    return Arrays.<List<Object>>asList(loginUserRow());
                }
                if (sqlText.contains("SELECT id FROM users WHERE name='Target'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(88));
                }
                if (sqlText.contains("SELECT id,name,motto,figure,level,id_socket,DATE_FORMAT(FROM_UNIXTIME(lastonline_time)")
                    && sqlText.contains("FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(77, "User77", "Ready", "hd-180-1", 1, 4, "today"));
                }
                if (sqlText.contains("SELECT id,name,motto,figure,level,id_socket,DATE_FORMAT(FROM_UNIXTIME(lastonline_time)")
                    && sqlText.contains("FROM users WHERE id='88'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(88, "Target", "Motto", "fig", 1, 8, "now"));
                }
                if (sqlText.contains("SELECT users.id,users.name,users.motto,users.figure,users.level,users.id_socket")
                    && sqlText.contains("friendships.has_accept='0'")
                    && sqlText.contains("friendships.id_user='77'")
                    && sqlText.contains("friendships.id_friend='88'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(88, "Target", "Motto", "fig", 1, 8, "now"));
                }
                if (sqlText.contains("SELECT id,name,id_socket,figure,motto,nickname,DATE_FORMAT(FROM_UNIXTIME(lastonline_time)")
                    && sqlText.contains("LOWER(name) LIKE 'targ%'")) {
                    return Arrays.<List<Object>>asList(
                        Arrays.<Object>asList(88, "Target", 8, "fig", "Motto", "nick", "now"),
                        Arrays.<Object>asList(99, "Targus", 0, "fig2", "Other", "nick2", "later"));
                }
                if (sqlText.contains("SELECT activitypoints_0 FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(70));
                }
                if (sqlText.contains("SELECT online_time FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(60));
                }
                if (sqlText.contains("SELECT id,name,motto,gender,respect_amount,scratch_amount FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(77, "Caller", "Motto", "M", 4, 2));
                }
                if (sqlText.contains("SELECT activitypoints_1,activitypoints_2,activitypoints_3,activitypoints_4 FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(10, 20, 30, 40));
                }
                if (sqlText.contains("SELECT level_hc FROM users")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
                }
                if (sqlText.contains("SELECT level FROM users")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(1));
                }
                if (sqlText.contains("SELECT id_pet,breed,min_rank,min_hcrank,name FROM settings_petraces WHERE product_pet='dog'")) {
                    return Arrays.<List<Object>>asList(
                        Arrays.<Object>asList(1, 1, 0, 0, "A"),
                        Arrays.<Object>asList(2, 2, 2, 0, "B"));
                }
                if (sqlText.contains("SELECT bots.id,bots.name,bots.figure,bots_petdata.scratches FROM bots,bots_petdata WHERE bots.id_user='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(10, "Rex", "1 2 FF00AA", 4));
                }
                if (sqlText.contains("SELECT bots.id,bots.name,bots.figure,bots_petdata.id_level,bots_petdata.experience")
                    && sqlText.contains("WHERE bots.id='10'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(10, "Rex", "1 2 ff", 2, 7, 100, 90, 4, 12, 5, "Owner"));
                }
                if (sqlText.contains("SELECT bots.name,bots.figure,bots_petdata.id_level,bots_petdata.experience")
                    && sqlText.contains("WHERE bots.id='10'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("Rex", "1 2 ff", 2, 18, 100, 90, 4, 9));
                }
                if (sqlText.contains("SELECT bots_petdata.id_level FROM bots,bots_petdata WHERE bots.id='10'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(3));
                }
                if (sqlText.contains("SELECT bots.id,bots.id_room,bots_petdata.id_level,bots_petdata.energy")
                    && sqlText.contains("WHERE bots.id='10'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(10, 9, 3, 300, 300));
                }
                if (sqlText.contains("SELECT id_level,max_exp FROM bots_petlevels ORDER BY id_level ASC")) {
                    return Arrays.<List<Object>>asList(
                        Arrays.<Object>asList(1, 10),
                        Arrays.<Object>asList(2, 20),
                        Arrays.<Object>asList(3, 30));
                }
                if (sqlText.contains("SELECT scratch_amount FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(2));
                }
                if (sqlText.contains("SELECT tutorial_guide FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
                }
                if (sqlText.contains("SELECT bots.id,bots.name,bots.figure,bots_petdata.scratches FROM bots,bots_petdata WHERE bots.id='10'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(10, "Rex", "1 2 ff", 4));
                }
                if (sqlText.contains("SELECT id_slot,figure,gender FROM users_wardrobe")) {
                    return Arrays.<List<Object>>asList(
                        Arrays.<Object>asList(1, "hd-180-1", "M"),
                        Arrays.<Object>asList(6, "ch-255-66", "M"));
                }
                if (sqlText.contains("SELECT id_socket FROM users WHERE id='88'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(8));
                }
                if (sqlText.contains("SELECT id_socket FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(4));
                }
                if (sqlText.contains("SELECT id_badge,id_slot,id FROM users_badges WHERE id_user='77' AND id_slot='0'")) {
                    return Arrays.<List<Object>>asList(
                        Arrays.<Object>asList("ACH1", 0, 201),
                        Arrays.<Object>asList("MOD", 0, 202));
                }
                if (sqlText.contains("SELECT id_badge,id_slot,id FROM users_badges WHERE id_slot != '0' AND id_user='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("VIP", 1, 203));
                }
                if (sqlText.contains("SELECT id_badge,id_slot,id FROM users_badges WHERE id_slot != '0' AND id_user='88'")) {
                    return new ArrayList<List<Object>>();
                }
                if (sqlText.contains("SELECT REPLACE(id_badge,'ACH_','') FROM users_badges WHERE id_user='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(2));
                }
                if (sqlText.contains("SELECT id FROM users_badges WHERE id_user='77' AND id_badge='ACH_3'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(204));
                }
                if (sqlText.contains("SELECT respect_received FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(30));
                }
                if (sqlText.contains("SELECT respect_amount FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(2));
                }
                if (sqlText.contains("SELECT respect_received FROM users WHERE id='88'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(12));
                }
                if (sqlText.contains("SELECT name FROM users_tags WHERE id_user='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("alpha"), Arrays.<Object>asList("beta"));
                }
                if (sqlText.contains("SELECT name FROM users_tags WHERE id_user='88'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("target"));
                }
                if (sqlText.contains("SELECT id,description_title,description_thanks FROM poll WHERE id='7' AND id_room='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(7, "Title", "Thanks"));
                }
                if (sqlText.contains("SELECT id,description_question,id_type FROM poll_questions WHERE id_poll='7'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(8, "Question?", 2));
                }
                if (sqlText.contains("SELECT id,id_question,caption FROM poll_answers WHERE id_question='8'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(1, 8, "Yes"), Arrays.<Object>asList(2, 8, "No"));
                }
                if (sqlText.contains("SELECT title,sequence,author,sound,id FROM soundmachine_cds WHERE id IN ('50','51')")) {
                    return Arrays.<List<Object>>asList(
                        Arrays.<Object>asList("Song A", 3, "Author A", "sound-a", 50),
                        Arrays.<Object>asList("Song B", 4, "Author B", "sound-b", 51));
                }
                if (sqlText.contains("SELECT furnitures.id,furnitures.id_product FROM furnitures,soundmachine_jb_playlist WHERE furnitures.id_room='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(300, 900));
                }
                if (sqlText.contains("SELECT id_destination FROM soundmachine_jb_playlist WHERE id_jukebox='300' AND id_order='0'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(40));
                }
                if (sqlText.contains("SELECT MAX(id_order) FROM soundmachine_jb_playlist WHERE id_jukebox='300'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(1));
                }
                if (sqlText.contains("SELECT COUNT(*) FROM soundmachine_jb_playlist WHERE id_jukebox='300'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(2));
                }
                if (sqlText.contains("SELECT id_destination FROM furnitures WHERE id_owner='77' AND id='4' AND id_product='700'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(50));
                }
                if (sqlText.contains("SELECT id_cd FROM soundmachine_jb_playlist WHERE id_jukebox='300' AND id_order='0'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(4));
                }
                if (sqlText.contains("SELECT id_cd,id_destination FROM soundmachine_jb_playlist WHERE id_jukebox='300'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(2, 40), Arrays.<Object>asList(3, 41));
                }
                if (sqlText.contains("SELECT id,id_destination FROM furnitures WHERE id_owner='77' AND id_product='700'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(4, 50), Arrays.<Object>asList(5, 51));
                }
                if (sqlText.contains("SELECT COUNT(*) FROM furnitures,products WHERE")
                    && sqlText.contains("furnitures.id IN ('1','2','3','4','5')")
                    && sqlText.contains("products.is_recycleable='1'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(5));
                }
                if (sqlText.contains("SELECT id_destination FROM catalog_products WHERE id_product='506'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(81));
                }
                if (sqlText.contains("SELECT soundmachine_jb_playlist.id_destination,soundmachine_jb_playlist.id_cd,soundmachine_cds.sequence")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(40, 2, 3));
                }
                if (sqlText.contains("SELECT id_quest,id_numericquest,progress,id_level,time_next FROM users_quests WHERE id_user='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(10, 10, 1, 0, 0));
                }
                if (sqlText.contains("SELECT id_quest,id_numericquest,progress,id_level FROM users_quests WHERE id_user='77' AND id_quest='10'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(10, 10, 3, 0));
                }
                if (sqlText.contains("SELECT id_quest,id_level FROM users_quests WHERE id_user='77'")
                    && sqlText.contains("timestamp_accepted IS NOT NULL")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(10, 1));
                }
                if (sqlText.contains("SELECT id_quest,id_level,timestamp_done,timestamp_accepted,time_next,progress FROM users_quests WHERE id_user='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(10, 0, 0, 1, 0, 1));
                }
                if (sqlText.contains("SELECT id_level FROM users_quests WHERE id_user='77' AND id_quest='10'")) {
                    return new ArrayList<List<Object>>();
                }
                if (sqlText.contains("SELECT progress FROM users_quests WHERE id_user='77' AND id_quest='10'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(1));
                }
                if (sqlText.contains("SELECT time_next FROM users_quests WHERE id_user='77' AND id_quest='10'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
                }
                if (sqlText.contains("SELECT activitypoints_2 FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(20));
                }
                if (sqlText.contains("FROM quests ORDER BY id_campaign DESC,level ASC")) {
                    return Arrays.<List<Object>>asList(
                        Arrays.<Object>asList(10, 1, "First", "", 5, 2, "visit", 0, 7, 3, 30),
                        Arrays.<Object>asList(11, 2, "Second", "", 6, 2, "visit", 0, 7, 4, 0));
                }
                if (sqlText.contains("SELECT logs_visitedrooms.id,logs_visitedrooms.id_user,users.id_socket")
                    && sqlText.contains("logs_visitedrooms.id='61'")
                    && sqlText.contains("logs_visitedrooms.id_room='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(61, 88, 8));
                }
                if (sqlText.contains("logs_visitedrooms WHERE id_user='88'") && sqlText.contains("id_room")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(9));
                }
                if (sqlText.contains("SELECT id FROM logs_visitedrooms WHERE id_user='88'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(61));
                }
                if (sqlText.contains("logs_visitedrooms WHERE id_user='77'") && sqlText.contains("id_room")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(9));
                }
                if (sqlText.contains("SELECT logs_visitedrooms.id,logs_visitedrooms.id_room,rooms.id_slot")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(61, 9, 4));
                }
                if (sqlText.contains("SELECT users.id_socket FROM logs_visitedrooms,users WHERE logs_visitedrooms.id_room='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(4), Arrays.<Object>asList(8));
                }
                if (sqlText.contains("SELECT id_slot FROM rooms WHERE id='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(4));
                }
                if (sqlText.contains("SELECT heightmap FROM models,rooms WHERE rooms.id='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("0"));
                }
                if (sqlText.contains("SELECT bots.id,bots.name,bots.motto,bots.speech,bots.responses,bots.figure")
                    && sqlText.contains("WHERE bots_petdata.id_bot='10'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(
                        10, "Rex", "pet motto", "speech", "responses", "1 2 ff", 3, 0, "", "submit", 1, 6));
                }
                if (sqlText.contains("SELECT scratches FROM bots_petdata WHERE id_bot='10'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(5));
                }
                if (sqlText.contains("SELECT id,name,motto,speech,responses,position_x,position_y,position_z,position_r,figure")
                    && sqlText.contains("FROM bots WHERE id='20'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(
                        20, "Guide", "guide motto", "speech", "responses", 1, 2, "0", 3, "guide-fig", "", 0, 0, "", "submit", 1, 6));
                }
                if (sqlText.contains("SELECT logs_visitedrooms.id,users.name,users.motto,users.achievement_score,users.figure")
                    && sqlText.contains("logs_visitedrooms.id='61'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(61, "Target", "Motto", 123, "fig"));
                }
                if (sqlText.contains("SELECT id FROM rooms WHERE id_slot='4'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(9));
                }
                if (sqlText.contains("SELECT id FROM rooms WHERE id='9' AND id_owner='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(9));
                }
                if (sqlText.contains("SELECT id_owner FROM rooms WHERE id='9' AND id_owner='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(77));
                }
                if (sqlText.contains("SELECT id FROM rooms_categories WHERE id='1'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(1));
                }
                if (sqlText.contains("SELECT id_user FROM rooms_rates WHERE id_user='77'")) {
                    return new ArrayList<List<Object>>();
                }
                if (sqlText.contains("SELECT rate FROM rooms WHERE id='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(5));
                }
                if (sqlText.contains("SELECT COUNT(id) FROM rooms WHERE id_owner='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(1));
                }
                if (sqlText.contains("SELECT id,visitors_max FROM models WHERE create_min_level_hc")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(20, 25));
                }
                if (sqlText.equals("SELECT MAX(id) FROM rooms")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(90));
                }
                if (sqlText.contains("SELECT visitors_now,visitors_max,status_door,password,id_slot,id_owner FROM rooms WHERE rooms.id='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(0, 25, 0, "", 4, 77));
                }
                if (sqlText.contains("SELECT rooms.id,rooms.name,rooms.description,rooms.status_door")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(
                        9, "Room", "Description", 0, 1, 25, 50, "tag1", "tag2", "", 1, 0, 1, 0));
                }
                if (sqlText.contains("SELECT rooms.id,rooms_events.name")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(
                        9, "Event Room", "Owner", 0, 3, 25, "Event Desc", 1, "", 5, 2, "icon", "tag1", "tag2", "12:00"));
                }
                if (sqlText.contains("ORDER BY 27 ASC LIMIT 255")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(
                        1, 2, 3, "caption", "cap2", "cap3", 7, 8, 9, 10,
                        11, 12, 13, "description", 15, 16, 17, 18, "icon",
                        "tag1", "tag2", 22, "model", "files", 250, 5, 6, 7));
                }
                if (sqlText.contains("SELECT SUM(get_one) as get_one,get_two")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(5, "tag1"));
                }
                if (sqlText.contains("SELECT users.id,users.name FROM rooms_rights")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(88, "Target"));
                }
                if (sqlText.contains("SELECT users.id_socket FROM rooms_rights,users WHERE rooms_rights.id_room='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(8));
                }
                if (sqlText.contains("SELECT users.id_socket FROM friendships,users WHERE friendships.has_accept='1' AND friendships.id_user='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(8));
                }
                if (sqlText.contains("SELECT users.id,users.name,users.id_socket,users.figure,users.motto,users.level,DATE_FORMAT(FROM_UNIXTIME(users.lastonline_time)")
                    && sqlText.contains("FROM friendships,users WHERE friendships.has_accept='1' AND friendships.id_user='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(88, "Target", 8, "fig", "Motto", 1, "now"));
                }
                if (sqlText.contains("SELECT id_friend FROM friendships WHERE id_user='77' AND id_friend='88' AND has_accept='1'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(88));
                }
                if (sqlText.contains("SELECT id_user FROM friendships WHERE has_accept='1'") && sqlText.contains("id_friend='88'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(77));
                }
                if (sqlText.contains("SELECT id_user FROM friendships WHERE has_accept='1'")
                    && sqlText.contains("id_friend='88'")
                    && sqlText.contains("id_user='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(77));
                }
                if (sqlText.contains("SELECT id_user FROM friendships WHERE (id_user='77' AND id_friend='88')")) {
                    return new ArrayList<List<Object>>();
                }
                if (sqlText.contains("SELECT accept_friends FROM users WHERE id='88'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(1));
                }
                if (sqlText.contains("SELECT users.id,users.name FROM users,friendships WHERE friendships.has_accept='0' AND friendships.id_user='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(88, "Target"));
                }
                if (sqlText.contains("SELECT name FROM users WHERE id='88'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("Target"));
                }
                if (sqlText.contains("SELECT id,id_product,sign,caption,position_wall FROM furnitures WHERE id='70'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(70, 500, "FFFF33", "old", "w=1"));
                }
                if (sqlText.contains("SELECT id,id_product,sign,caption FROM furnitures WHERE id='70'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(70, 500, "9CFF9C", "hello\u001fworld"));
                }
                if (sqlText.contains("SELECT id,id_product,id_destination,sign_extra FROM furnitures WHERE id='71'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(71, 501, 502, "gift sign"));
                }
                if (sqlText.contains("SELECT id,id_product,sign,position_wall FROM furnitures WHERE id='72'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(72, 503, 1, "w=1"));
                }
                if (sqlText.contains("SELECT id_product FROM furnitures WHERE id_room='9' AND id='73'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(504));
                }
                if (sqlText.contains("SELECT id,id_product,sign,id_secondary FROM furnitures WHERE id='76'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(76, 506, "trade-state", 4));
                }
                if (sqlText.contains("SELECT id,id_product,sign,id_secondary FROM furnitures WHERE id='86'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(86, 506, "target-state", 5));
                }
                if (sqlText.contains("SELECT id,id_product,sign FROM furnitures WHERE id='76'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(76, 506, "trade-state"));
                }
                if (sqlText.contains("SELECT id,id_product,sign FROM furnitures WHERE id_owner='77' AND id='79'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(79, 507, "paper1"));
                }
                if (sqlText.contains("SELECT id,id_product,sign,id_secondary FROM furnitures WHERE id_owner='77' AND id_room IS NULL LIMIT 1000")) {
                    return Arrays.<List<Object>>asList(
                        Arrays.<Object>asList(76, 506, "trade-state", 4),
                        Arrays.<Object>asList(79, 507, "paper1", 0));
                }
                if (sqlText.contains("SELECT furnitures.id FROM furnitures,products WHERE furnitures.id_room='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(78));
                }
                if (sqlText.contains("SELECT id_light,id_preset,id_background,colour,id_state FROM furnitures_dimmerpresets WHERE id_furni='78'")) {
                    return Arrays.<List<Object>>asList(
                        Arrays.<Object>asList(150, 1, 1, "#0053F7", 1),
                        Arrays.<Object>asList(100, 2, 1, "#82F349", 2));
                }
                if (sqlText.contains("SELECT furnitures_dimmerpresets.id_light,furnitures_dimmerpresets.id_preset")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(100, 2, 1, "#82F349", 503, ":w=5", "2,2,1,#82F349,100"));
                }
                if (sqlText.contains("SELECT id_product,position_wall FROM furnitures WHERE id='78'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(503, ":w=5"));
                }
                if (sqlText.contains("SELECT id,position_x,position_y,id_product FROM furnitures WHERE id='93'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(93, 2, 3, 511));
                }
                if (sqlText.contains("SELECT id_product,id,sign,id_secondary,id_destination FROM furnitures WHERE id='94' AND id_room='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(511, 94, "floor-state", 0, 0));
                }
                if (sqlText.contains("SELECT id_product,id,sign,id_secondary,id_destination FROM furnitures WHERE id='95' AND id_owner='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(511, 95, "placed-state", 0, 0));
                }
                if (sqlText.contains("SELECT status_door FROM rooms WHERE id='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
                }
                if (sqlText.contains("SELECT name FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("OldName"));
                }
                if (sqlText.contains("SELECT gender FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("M"));
                }
                if (sqlText.contains("SELECT COUNT(*) FROM users WHERE name='NewName'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
                }
                if (sqlText.contains("SELECT rooms.id,rooms_events.name,users.name,rooms.status_door")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(
                        9, "Event Room", "Owner", "open", 2, 25, "Description", 1, 1, 5, 3, "icon", "tag1", "tag2", "12:00"));
                }
                if (sqlText.contains("SELECT rooms.id,rooms.name,users.name,rooms.status_door")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(
                        9, "NavRoom", "Owner", 0, 2, 25, "Description", 1, "", 5, 3, "icon", "tag1", "tag2", 1, 0));
                }
                if (sqlText.contains("users.id='88'") && sqlText.contains("ROUND")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(88, "Target", 60, 10, 8));
                }
                if (sqlText.contains("COUNT(id) FROM staff_cfh WHERE id_user='88' AND id_closed='2'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(1));
                }
                if (sqlText.contains("COUNT(id) FROM staff_cfh WHERE id_user='88'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(2));
                }
                if (sqlText.contains("COUNT(id) FROM users_cautions WHERE id_user='88'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(3));
                }
                if (sqlText.contains("COUNT(id) FROM users_bans WHERE id_user='88'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(4));
                }
                if (sqlText.contains("SELECT ip_last FROM users WHERE id='88'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("127.0.0.1"));
                }
                if (sqlText.contains("SELECT id_slot,id_owner FROM rooms WHERE id='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(4, 88));
                }
                if (sqlText.contains("SELECT id_owner FROM rooms WHERE id='9' LIMIT 1")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(77));
                }
                if (sqlText.contains("SELECT is_staff_picked FROM rooms WHERE id='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
                }
                if (sqlText.contains("staff_cfh.id='50'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(50, 88, "Target", 0, 9, 8, "Need help", 9, "Room"));
                }
                if (sqlText.contains("SELECT id FROM staff_cfh WHERE id_user='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(51));
                }
                if (sqlText.contains("SELECT id_closed FROM staff_cfh WHERE id_user='77'")) {
                    return new ArrayList<List<Object>>();
                }
                if (sqlText.contains("SELECT MAX(id) FROM staff_cfh")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(52));
                }
                if (sqlText.contains("SELECT staff_cfh.id,staff_cfh.id_tab")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(51, 0, 77, "Caller", 88, 9, 8, "Need help", 9, "Room", 0));
                }
                if (sqlText.contains("SELECT id_user FROM staff_cfh WHERE id='50'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(88));
                }
                if (sqlText.contains("SELECT id,name FROM faq WHERE name LIKE")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(2, "FAQ Two"), Arrays.<Object>asList(3, "FAQ Three"));
                }
                if (sqlText.contains("SELECT users.id,users.name,rooms_events.id_room")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(77, "Caller", 9, 8, "Party", "Desc", "12:00", "tag1", "tag2"));
                }
                if (sqlText.contains("SELECT motto FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("Motto"));
                }
                if (sqlText.contains("SELECT id,id_socket,motto,figure,gender") && sqlText.contains("id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(77, 0, "New motto", "hd-180-1", "M"));
                }
                if (sqlText.contains("SELECT id_session FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("session-77"));
                }
                if (sqlText.contains("SELECT login_session FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("login-77"));
                }
                if (sqlText.contains("SELECT credits FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(110));
                }
                if (sqlText.contains("SELECT rooms.id,rooms_official.id,models.required_files,rooms_official.caption")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(9, 1, "model.swf", "Official Room"));
                }
                if (sqlText.contains("SELECT rooms.id,rooms.id_slot,NULL,models.name,models.id,rooms.id_floor")
                    && sqlText.contains("FROM rooms,models WHERE rooms.id='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(
                        9, 4, "", "model", 20, "floorA", "wallA", "landA", 5, "000\n111", 2, 3, "", "Room", 0, 1, 1, 1, 0, 77));
                }
                if (sqlText.contains("SELECT id_model FROM rooms WHERE id='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(20));
                }
                if (sqlText.contains("SELECT rooms.id,rooms.id_slot,users.id,models.name,models.id,rooms.id_floor")
                    && sqlText.contains("FROM rooms,models,users WHERE rooms.id='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(
                        9, 4, 77, "model", 20, "floorA", "wallA", "landA", 5, "000\n111", 2, 3, "", "Room", 0, 1, 1, 1, 0, 77, 0, 1, 2));
                }
                if (sqlText.contains("SELECT users.id,users.name,users.figure,users.motto,users.gender,models.position_x,models.position_y,rooms.id_slot")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(77, "Caller", "hd-180-1", "Motto", "M", 2, 3, 4));
                }
                if (sqlText.contains("SELECT rooms.id_slot FROM rooms WHERE rooms.id='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(4));
                }
                if (sqlText.contains("SELECT logs_visitedrooms.id,users.id,users.name,users.figure,users.motto")
                    && sqlText.contains("logs_visitedrooms.id_room='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(61, 77, "Caller", "hd-180-1", "Motto", "M", 2, 3, 4));
                }
                if (sqlText.contains("SELECT logs_visitedrooms.id,users_effects.id_effect")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(61, 12));
                }
                if (sqlText.contains("SELECT id_effect,time_rent,COUNT(id_effect),timestamp_expire,UNIX_TIMESTAMP()")) {
                    return Arrays.<List<Object>>asList(
                        Arrays.<Object>asList(12, 3600, 2, 1000, 900),
                        Arrays.<Object>asList(13, 120, 1, 0, 900));
                }
                if (sqlText.contains("SELECT id,time_rent,timestamp_expire FROM users_effects")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(901, 3600, 0));
                }
                if (sqlText.contains("SELECT users_effects.id_effect,users.id_socket,users_effects.id")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(12, 4, 901));
                }
                if (sqlText.contains("SELECT id_type,id_source,id_sprite,position_x,position_y,position_z,action,action_rotation,action_height FROM models_furnitures WHERE id_model='20'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(503, 901, "", 4, 5, 0, "state", 2, 0));
                }
                if (sqlText.contains("SELECT id,id_product,position_wall,sign,id_secondary FROM furnitures WHERE id_room='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(74, 503, ":w=1,2 l=3,4", "wall-state", 6));
                }
                if (sqlText.contains("SELECT id,id_room,id_product,sign FROM furnitures WHERE id_room='9' AND position_x='2' AND position_y='3'")) {
                    return Arrays.<List<Object>>asList(
                        Arrays.<Object>asList(88, 9, 506, 4),
                        Arrays.<Object>asList(89, 9, 509, 7));
                }
                if (sqlText.contains("SELECT id_product FROM furnitures WHERE id='75' AND id_room='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(505));
                }
                if (sqlText.contains("SELECT id_product,id_owner FROM furnitures WHERE id='80' AND id_room='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(506, 77));
                }
                if (sqlText.contains("SELECT id_room FROM furnitures WHERE id='82' LIMIT 1")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(9));
                }
                if (sqlText.contains("SELECT id_room FROM furnitures WHERE id='83' LIMIT 1")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(9));
                }
                if (sqlText.contains("SELECT id_room,id_product,sign FROM furnitures WHERE id='84' LIMIT 1")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(9, 506, 5));
                }
                if (sqlText.contains("SELECT id_product,id_owner FROM furnitures WHERE id='85' AND id_room='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(506, 77));
                }
                if (sqlText.contains("SELECT id_room FROM furnitures WHERE id='85' LIMIT 1")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(9));
                }
                if (sqlText.contains("SELECT id_product,sign FROM furnitures WHERE id='86' AND id_room='9' AND position_wall IS NULL")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(506, 0));
                }
                if (sqlText.contains("SELECT id_product FROM furnitures WHERE id='86' AND id_room='9' AND position_wall IS NULL")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(506));
                }
                if (sqlText.contains("SELECT id_product,id,sign,id_secondary,id_destination FROM furnitures WHERE id='90' AND id_owner='77' AND id_room IS NULL")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(9, 90, "wall-state", 6, 0));
                }
                if (sqlText.contains("SELECT id_room FROM furnitures WHERE id='91' LIMIT 1")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(9));
                }
                if (sqlText.contains("SELECT models.map,rooms.allow_walkthrough,rooms.id_slot FROM rooms,models WHERE rooms.id='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("000\r0x0\r000", 1, 4));
                }
                if (sqlText.contains("SELECT COUNT(*) FROM furnitures WHERE id_room='9'")
                    && sqlText.contains("position_x='2'")
                    && sqlText.contains("position_y='2'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(1));
                }
                if (sqlText.contains("SELECT id_room,id_product,sign FROM furnitures WHERE id='92' LIMIT 1")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(9, 510, -2));
                }
                if (sqlText.contains("SELECT id_product,id_owner FROM furnitures WHERE id='93' AND id_room='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(505, 77));
                }
                if (sqlText.contains("SELECT id_room FROM furnitures WHERE id='93' LIMIT 1")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(9));
                }
                if (sqlText.contains("SELECT id_product,type_secondary,id_contain,type_check FROM packages WHERE id_product='505'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(505, "packages_pets", 12, ""));
                }
                if (sqlText.contains("SELECT id_pet,id_race,color FROM packages_pets WHERE id='12'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(1, 2, "3"));
                }
                if (sqlText.contains("SELECT id FROM bots WHERE id_user='77' AND id_handle='3' ORDER BY id DESC LIMIT 1")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(30));
                }
                if (sqlText.contains("SELECT id_socket FROM users WHERE name='Target'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(8));
                }
                if (sqlText.contains("SELECT id FROM users WHERE name='Target'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(88));
                }
                if (sqlText.contains("users.name='Target'") && sqlText.contains("logs_visitedrooms.id_room")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(88, 8, 9));
                }
                if (sqlText.contains("SELECT id,sprite_name,months,level,price_credits FROM products_club")) {
                    return Arrays.<List<Object>>asList(
                        Arrays.<Object>asList(1, "club_habbo", 1, 1, 25),
                        Arrays.<Object>asList(2, "club_vip", 3, 2, 60));
                }
                if (sqlText.contains("SELECT level_hc,hc_days,hc2_days,hc_periods,hc2_periods,hc_presents")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(2, 10, 70, 1, 3, 4, 8));
                }
                if (sqlText.contains("SELECT level_hc,hc_days,hc2_days,hc_presents")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(2, 10, 70, 4, 8));
                }
                if (sqlText.contains("SELECT users.id_socket FROM logs_visitedrooms")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(4), Arrays.<Object>asList(8));
                }
                if (sqlText.contains("SELECT users.id,users.name FROM users WHERE users.id='88'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(88, "Target"));
                }
                if (sqlText.contains("SELECT id_room FROM rooms_favourites WHERE id_user='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(9), Arrays.<Object>asList(10));
                }
                if (sqlText.contains("SELECT contain_product,contain_credits,contain_shells FROM vouchers WHERE name='ABCD0000'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("trade_sprite", 5, 7));
                }
                if (sqlText.contains("SELECT id_product FROM catalog_products WHERE sprite='trade_sprite'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(506));
                }
                if (sqlText.contains("SELECT id FROM catalog_products WHERE sprite='trade_sprite'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(81));
                }
                if (sqlText.contains("SELECT id FROM furnitures WHERE id_owner='77' AND id_product='506' ORDER BY id DESC LIMIT 1")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(96));
                }
                if (sqlText.contains("SELECT credits,activitypoints_0,level_hc FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(100, 70, 2));
                }
                if (sqlText.contains("SELECT id FROM furnitures WHERE id_owner='77' ORDER BY id DESC LIMIT 1")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(97));
                }
                if (sqlText.contains("logs_visitedrooms.id_user='88'") && sqlText.contains("models.type")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(1, 9, "Room", 12, 30));
                }
                if (sqlText.contains("FROM logs_chat,users")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(10, 5, 88, "Target", "hello"));
                }
                if (sqlText.equals("SELECT id,id_socket FROM users WHERE id_socket IS NOT NULL")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(77, 4), Arrays.<Object>asList(88, 8));
                }
                return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
            }
        });

        Handling.sendToSocket(4, "PING");
        assertEquals("4:DATA\6" + "4\6PING\1\7", handlingSends.get(0));
        handlingSends.clear();
        int sqlBeforeCommand = handlingSql.size();
        String aboutCommandPayload = Handling.routeChatCommand(4, ":about");
        assertEquals(true, aboutCommandPayload.startsWith("BKAlpha Series"));
        assertEquals(true, handlingSends.get(0).contains(
            "This is a copy of the unique Alpha Series written in Visual Basic 2006."));
        assertEquals(sqlBeforeCommand, handlingSql.size());
        handlingSends.clear();
        String onlineCommandPayload = Handling.routeChatCommand(4, ":whosonline");
        assertEquals(true, onlineCommandPayload.startsWith("BKActive users:"));
        assertEquals(true, onlineCommandPayload.contains("OldName"));
        assertEquals(true, onlineCommandPayload.contains("Target"));
        assertEquals(sqlBeforeCommand, handlingSql.size());
        handlingSends.clear();
        assertEquals(2L, Handling.broadcastToRoomUsers(9, "ROOM"));
        assertEquals(Arrays.asList("4:DATA\6" + "4\6ROOM\1\7", "8:DATA\6" + "8\6ROOM\1\7"), handlingSends);
        handlingSends.clear();
        Handling.sendStaffUserSummary(4, "GF", "GF" + wireLong(88));
        assertEquals(true, handlingSends.get(0).contains("HU"));
        handlingSends.clear();
        Handling.sendStaffCaution(4, "GM", "GM" + wireLong(88) + wireString("Careful"));
        assertEquals("8:DATA\6" + "8\6BaCareful\2\1\7", handlingSends.get(0));
        assertEquals(true, containsSql(handlingSql, "users_cautions"));
        handlingSends.clear();
        Handling.staffKickUser(4, "GO", "GO" + wireLong(88) + wireString("Leave"));
        assertEquals(true, handlingSends.contains("8:DATA\6" + "8\6BaLeave\2\1\7"));
        assertEquals(true, handlingSends.contains("8:DATA\6" + "8\6@R\7"));
        handlingSends.clear();
        Handling.staffBanUser(4, "GP", "GP" + wireLong(88) + wireString("Ban") + wireLong(2));
        assertEquals(true, containsSql(handlingSql, "users_bans"));
        assertEquals(true, containsSql(handlingSql, "UNIX_TIMESTAMP()+7200"));
        Guardian.setSocketConnected(8, true);
        handlingSends.clear();
        assertEquals(1L, Handling.moderateCurrentRoom(4, "CH", "CH" + wireLong(1) + wireString("Room alert")));
        assertEquals(true, handlingSends.contains("4:DATA\6" + "4\6BaRoom alert\2\1\7"));
        assertEquals(true, handlingSends.contains("8:DATA\6" + "8\6BaRoom alert\2\1\7"));
        handlingSends.clear();
        Handling.sendCallForHelpReview(50, 4);
        assertEquals(true, handlingSends.get(0).contains("HR"));
        handlingSql.clear();
        Handling.moveCallForHelpToPickedTab(4, "GB", "GB" + wireLong(1) + wireLong(50));
        assertEquals(true, containsSql(handlingSql, "id_tab='2'"));
        handlingSql.clear();
        Handling.moveCallForHelpToOpenTab(4, "GC", "GC" + wireLong(1) + wireLong(50));
        assertEquals(true, containsSql(handlingSql, "id_tab='1'"));
        handlingSends.clear();
        Handling.closeCallForHelp(4, "GD", "GD" + wireLong(2) + wireLong(50));
        assertEquals(true, handlingSends.get(0).contains("H\\"));
        handlingSql.clear();
        Handling.lockCurrentRoomForModeration(4, "GL", "GL" + wireLong(1) + wireLong(1));
        assertEquals(true, containsSql(handlingSql, "Inappropriate to hotel management"));
        handlingSends.clear();
        Handling.sendStaffRoomChatHistory(4, "GG", "GG" + wireLong(88));
        assertEquals(true, handlingSends.get(0).contains("HX"));
        assertEquals(sentPayload(handlingSends.get(0)),
            StaffModerationLookups.roomChatHistoryResponse(
                new StaffUserLookup(88L, "Target"), new StaffModerationDao(MySQL.configuredDatabase())));
        handlingSends.clear();
        Handling.sendStaffRoomVisitHistory(4, "GJ", "GJB88");
        assertEquals(true, handlingSends.get(0).contains("HY"));
        assertEquals(sentPayload(handlingSends.get(0)),
            StaffModerationLookups.roomVisitHistoryResponse(
                new StaffUserLookup(88L, "Target"), new StaffModerationDao(MySQL.configuredDatabase())));
        handlingSends.clear();
        Handling.sendStaffAlert(4, "GN", "GN" + wireLong(88) + wireString("Direct"));
        assertEquals("8:DATA\6" + "8\6BaDirect\2\1\7", handlingSends.get(0));
        handlingSends.clear();
        assertEquals(4L, Handling.waveCurrentRoomUser(4, "A^", "A^"));
        assertEquals(true, handlingSends.get(0).contains("Ga"));
        handlingSends.clear();
        assertEquals(3L, Handling.danceCurrentRoomUser(4, "A]", "A]" + wireLong(3)));
        assertEquals(true, handlingSends.get(0).contains("G`"));
        handlingSends.clear();
        String wardrobePayload = UserLookups.wardrobeSlotsPayload(
            "77", new UserDao(MySQL.configuredDatabase()), AppConfigState.instance().permissionMatrix());
        assertEquals(true, wardrobePayload.contains("DK"));
        assertEquals(true, wardrobePayload.contains("hd-180-1"));
        handlingSql.clear();
        handlingSends.clear();
        String savedWardrobePayload = UserLookups.saveWardrobeSlotPayload(
            "77",
            "Ex" + wireLong(2) + wireString("hd-180-1.ch-255-66") + wireString("M"),
            Files.readString(figureCachePath.resolve("figuredata.cache")),
            new UserDao(MySQL.configuredDatabase()),
            AppConfigState.instance().permissionMatrix());
        assertEquals(true, containsSql(handlingSql, "DELETE FROM users_wardrobe"));
        assertEquals(true, containsSql(handlingSql, "INSERT INTO users_wardrobe"));
        assertEquals(true, savedWardrobePayload.contains("DK"));
        handlingSql.clear();
        handlingSends.clear();
        String tutorialClothesPayload = UserLookups.updateTutorialClothesPayload(
            "77",
            "@l" + wireString("M") + wireString("hd-180-1.ch-255-66"),
            Files.readString(figureCachePath.resolve("figuredata.cache")),
            new UserDao(MySQL.configuredDatabase()));
        assertEquals(true, containsSql(handlingSql, "tutorial_clothes='1'"));
        assertEquals(true, tutorialClothesPayload.contains("DJ"));
        handlingSends.clear();
        Handling.sendClubSubscriptionOffers(4, "oW", "oW");
        assertEquals(true, handlingSends.get(0).contains("Iq"));
        assertEquals(true, handlingSends.get(0).contains("club_vip"));
        handlingSends.clear();
        assertEquals("GzCACHE", Handling.sendCachedRecyclerStatus(4, "", "Gz"));
        assertEquals("4:DATA\6" + "4\6GzCACHE\1\7", handlingSends.get(0));
        handlingSends.clear();
        String rankAndStaffPayload = UserLookups.rankAndStaffStatePayload(
            "77", new UserDao(MySQL.configuredDatabase()), AppConfigState.instance().permissionMatrix());
        assertEquals(true, rankAndStaffPayload.contains("@B"));
        handlingSql.clear();
        handlingSends.clear();
        String chatPayload = Handling.chatInCurrentRoom(4, "@t", "@t" + wireString("hello"));
        assertEquals(true, chatPayload.contains("@X"));
        assertEquals(true, chatPayload.contains("hello"));
        assertEquals(true, containsSql(handlingSql, "logs_chat"));
        assertEquals(true, containsSql(handlingSql, "'0'"));
        assertEquals(2, handlingSends.size());
        handlingSql.clear();
        handlingSends.clear();
        String shoutPayload = Handling.shoutInCurrentRoom(4, "@w", "@w" + wireString("loud"));
        assertEquals(true, shoutPayload.contains("@Y"));
        assertEquals(true, containsSql(handlingSql, "'1'"));
        handlingSends.clear();
        String whisperPayload = Handling.whisperInCurrentRoom(4, "@x", "@x" + wireString("Target") + wireString("secret"));
        assertEquals(true, whisperPayload.contains("@X"));
        assertEquals(true, whisperPayload.contains("secret"));
        assertEquals(2, handlingSends.size());
        assertEquals(true, handlingSends.get(0).startsWith("8:DATA"));
        assertEquals(true, handlingSends.get(1).startsWith("4:DATA"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.cancelLatestCallForHelp(4, "EG", "EG");
        assertEquals(true, containsSql(handlingSql, "DELETE FROM staff_cfh WHERE id='51'"));
        assertEquals("4:DATA\6" + "4\6E@\1\7", handlingSends.get(0));
        handlingSends.clear();
        Handling.openStaffModerationPanel(4);
        assertEquals(true, handlingSends.get(0).contains("HS"));
        assertEquals(true, handlingSends.get(0).contains("STAFFMOD"));
        assertEquals(true, handlingSends.get(1).contains("HR"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.submitCallForHelp(4, "GE", "GE" + wireString("This is a sufficiently long call for help description") + wireLong(8) + wireLong(88));
        assertEquals(true, containsSql(handlingSql, "INSERT INTO staff_cfh"));
        assertEquals(true, handlingSends.get(0).contains("EA"));
        handlingSends.clear();
        Handling.sendImportantFaqs(4);
        assertEquals("4:DATA\6" + "4\6HFIMPORTANTFAQ\1\7", handlingSends.get(0));
        handlingSends.clear();
        Handling.sendFaqCategories(4);
        assertEquals("4:DATA\6" + "4\6HGFAQCATS\1\7", handlingSends.get(0));
        handlingSends.clear();
        Handling.sendCategoryFaqs(4, "Fd", "Fd" + wireLong(1));
        assertEquals(true, handlingSends.get(0).contains("HJ"));
        assertEquals(true, handlingSends.get(0).contains("CATFAQ"));
        handlingSends.clear();
        Handling.searchFaqs(4, "Fc", "Fc" + "hotel");
        assertEquals(true, handlingSends.get(0).contains("HI"));
        assertEquals(true, handlingSends.get(0).contains("FAQ Two"));
        handlingSends.clear();
        Handling.sendFaqDescription(4, "Fb", "Fb" + wireLong(2));
        assertEquals("4:DATA\6" + "4\6HHFAQDESC\1\7", handlingSends.get(0));
        String eventInfoPayload = Handling.roomEventInfoPayload(9);
        assertEquals(true, eventInfoPayload.contains("Party"));
        assertEquals(eventInfoPayload, RoomLookups.eventInfoPayload(
            9, "%H:%i", new RoomDao(MySQL.configuredDatabase())));
        assertEquals("-1\2", RoomPayloads.eventInfo(null));
        assertEquals("-1\2", Handling.roomEventInfoPayload(0));
        handlingSends.clear();
        AvatarNameUpdate checkName = UserLookups.validateOrChangeAvatarName(
            "77", 4, true, "NewName", new UserDao(MySQL.configuredDatabase()));
        assertEquals(0L, checkName.validationCode());
        assertEquals(true, checkName.validationPayload().contains("H{"));
        assertEquals(true, checkName.validationPayload().contains("NewName"));
        handlingSql.clear();
        handlingSends.clear();
        AvatarNameUpdate changeName = UserLookups.validateOrChangeAvatarName(
            "77", 4, false, "NewName", new UserDao(MySQL.configuredDatabase()));
        assertEquals(0L, changeName.validationCode());
        assertEquals(true, changeName.changed());
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET name='NewName'"));
        assertEquals(true, containsSql(handlingSql, "INSERT INTO logs_identity"));
        handlingSends.clear();
        String settingsPayload = RoomLookups.roomSettingsPayload(9, new RoomDao(MySQL.configuredDatabase()));
        assertEquals(true, settingsPayload.contains("GQ"));
        assertEquals(true, settingsPayload.contains("Room"));
        Handling.sendRoomSettings(4, "FF", "FF" + wireLong(9));
        assertEquals(true, handlingSends.get(0).contains("GQ"));
        assertEquals(true, handlingSends.get(0).contains("Room"));
        handlingSql.clear();
        handlingSends.clear();
        RoomLookups.RoomIconUpdate iconUpdate =
            RoomLookups.updateRoomIcon(9, RoomWire.roomIconRequest("FB" + wireLong(1) + wireLong(2) + wireLong(0)),
                new RoomDao(MySQL.configuredDatabase()));
        assertEquals(true, iconUpdate.valid());
        assertEquals(true, iconUpdate.iconUpdatedPayload().contains("GI"));
        assertEquals(true, iconUpdate.entryUpdatedPayload().contains("GH"));
        assertEquals(true, containsSql(handlingSql, "UPDATE rooms SET icon="));
        handlingSql.clear();
        Handling.updateRoomIcon(4, "FB", "FB" + wireLong(1) + wireLong(2) + wireLong(0));
        assertEquals(true, containsSql(handlingSql, "UPDATE rooms SET icon="));
        assertEquals(true, containsSend(handlingSends, "GI"));
        assertEquals(true, containsSend(handlingSends, "GH"));
        handlingSql.clear();
        handlingSends.clear();
        RoomLookups.RoomEventChange deletedEvent = RoomLookups.deleteRoomEvent(
            9, new RoomDao(MySQL.configuredDatabase()));
        assertEquals("Er-1\2", deletedEvent.directPayload());
        assertEquals(false, deletedEvent.hasBroadcastPayload());
        assertEquals(true, containsSql(handlingSql, "DELETE FROM rooms_events WHERE id_room='9'"));
        handlingSql.clear();
        Handling.deleteRoomEvent(4, "E[", "E[");
        assertEquals(true, containsSql(handlingSql, "DELETE FROM rooms_events WHERE id_room='9'"));
        assertEquals("4:DATA\6" + "4\6Er-1\2\1\7", handlingSends.get(0));
        handlingSends.clear();
        assertEquals("EoIH", RoomLookups.doorStatusPayload(9, new RoomDao(MySQL.configuredDatabase())));
        Handling.sendRoomDoorStatus(4, "EY", "EY");
        assertEquals("4:DATA\6" + "4\6EoIH\1\7", handlingSends.get(0));
        handlingSql.clear();
        handlingSends.clear();
        assertEquals(RoomPayloads.homeRoom(9), RoomLookups.setHomeRoomPayload(
            "77", 9, new UserDao(MySQL.configuredDatabase())));
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET homeroom='9'"));
        handlingSql.clear();
        Handling.setHomeRoom(4, "XX", "XX" + wireLong(9));
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET homeroom='9'"));
        assertEquals(true, handlingSends.get(0).contains("GG"));
        handlingSql.clear();
        handlingSends.clear();
        RoomLookups.RoomEventChange createdEventChange = RoomLookups.createRoomEvent(
            "77",
            9,
            RoomWire.roomEventCreatePayloadFromWire("EZ" + wireLong(1) + wireString("Party")
                + wireString("Description") + wireLong(2) + wireString("TagOne") + wireString("TagTwo")),
            "%H:%i",
            new RoomDao(MySQL.configuredDatabase()));
        assertEquals(false, createdEventChange.hasDirectPayload());
        assertEquals(true, createdEventChange.broadcastPayload().contains("Er"));
        assertEquals(true, containsSql(handlingSql, "INSERT INTO rooms_events"));
        handlingSql.clear();
        Handling.createRoomEvent(4, "EZ", "EZ" + wireLong(1) + wireString("Party") + wireString("Description")
            + wireLong(2) + wireString("TagOne") + wireString("TagTwo"));
        assertEquals(true, containsSql(handlingSql, "INSERT INTO rooms_events"));
        assertEquals(true, containsSend(handlingSends, "Er"));
        handlingSql.clear();
        handlingSends.clear();
        RoomLookups.RoomEventChange editedEventChange = RoomLookups.editRoomEvent(
            "77",
            9,
            RoomWire.roomEventEditPayloadFromWire("E\\" + wireString("Edited") + wireString("Description")
                + wireLong(1) + wireString("TagOne")),
            "%H:%i",
            new RoomDao(MySQL.configuredDatabase()));
        assertEquals(false, editedEventChange.hasDirectPayload());
        assertEquals(true, editedEventChange.broadcastPayload().contains("Er"));
        assertEquals(true, containsSql(handlingSql, "UPDATE rooms_events SET"));
        handlingSql.clear();
        Handling.editRoomEvent(4, "E\\", "E\\" + wireString("Edited") + wireString("Description")
            + wireLong(1) + wireString("TagOne"));
        assertEquals(true, containsSql(handlingSql, "UPDATE rooms_events SET"));
        assertEquals(true, containsSend(handlingSends, "Er"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.followUserToRoom(4, "Ab", "AbTarget");
        assertEquals(true, containsSql(handlingSql, "INSERT INTO logs_visitedrooms"));
        assertEquals(true, containsSend(handlingSends, "@S"));
        assertEquals(true, containsSend(handlingSends, "@R"));
        SocialLookups.FollowRoomAction followAction =
            SocialLookups.followRoomAction(SocialWire.followUserRequest("AbTarget"),
                new UserDao(MySQL.configuredDatabase()));
        assertEquals(true, followAction.canEnterRoom());
        assertEquals(9L, followAction.roomId());
        assertEquals(false, followAction.hasFailurePayload());
        handlingSql.clear();
        handlingSends.clear();
        RoomLookups.RoomSettingsUpdate settingsUpdate = RoomLookups.updateRoomSettings(
            9,
            RoomWire.roomSettingsFromWire("FQ" + wireString("Updated Room") + wireString("secret")
                + wireLong(0) + wireString("Updated description") + wireLong(20) + wireLong(1)
                + wireLong(2) + wireString("TagOne") + wireString("TagTwo")
                + wireLong(1) + wireLong(0) + wireLong(1) + wireLong(0) + wireLong(0) + wireLong(0)),
            1,
            0,
            false,
            new RoomDao(MySQL.configuredDatabase()));
        assertEquals(true, settingsUpdate.valid());
        assertEquals(true, settingsUpdate.settingsUpdatedPayload().contains("GS"));
        assertEquals(true, settingsUpdate.entryUpdatedPayload().contains("GH"));
        assertEquals(true, settingsUpdate.wallOptionsPayload().contains("GX"));
        assertEquals(true, containsSql(handlingSql, "UPDATE rooms SET"));
        assertEquals(true, containsSql(handlingSql, "name='Updated Room'"));
        handlingSql.clear();
        Handling.updateRoomSettings(4, "FQ", "FQ" + wireString("Updated Room") + wireString("secret")
            + wireLong(0) + wireString("Updated description") + wireLong(20) + wireLong(1)
            + wireLong(2) + wireString("TagOne") + wireString("TagTwo")
            + wireLong(1) + wireLong(0) + wireLong(1) + wireLong(0) + wireLong(0) + wireLong(0));
        assertEquals(true, containsSql(handlingSql, "UPDATE rooms SET"));
        assertEquals(true, containsSql(handlingSql, "name='Updated Room'"));
        assertEquals(true, containsSend(handlingSends, "GS"));
        assertEquals(true, containsSend(handlingSends, "GX"));
        handlingSql.clear();
        handlingSends.clear();
        assertEquals(4L, Handling.enterRoom(4, 9, ""));
        assertEquals(true, containsSql(handlingSql, "UPDATE rooms SET id_slot='4'"));
        assertEquals(true, containsSend(handlingSends, "@R"));
        handlingSql.clear();
        handlingSends.clear();
        assertEquals(9L, Handling.leaveCurrentRoom(4));
        assertEquals(true, containsSql(handlingSql, "timestamp_left=UNIX_TIMESTAMP()"));
        assertEquals(true, containsSend(handlingSends, "J|H"));
        handlingSends.clear();
        Handling.sendRoomEntryBootstrap(4, 0);
        assertEquals(true, containsSend(handlingSends, "@S"));
        assertEquals(true, containsSend(handlingSends, "Bf/client.php"));
        assertEquals(true, containsSend(handlingSends, "@i"));
        handlingSends.clear();
        assertEquals(9L, Handling.enterRoomFromPayload(4, "FG", "FG9"));
        assertEquals(true, containsSend(handlingSends, "@R"));
        handlingSends.clear();
        Handling.sendVisitRoomAdvertisement(4, "Bv", "Bv");
        assertEquals("4:DATA\6" + "4\6DB\2\2\1\7", handlingSends.get(0));
        handlingSends.clear();
        String singleRoomPayload = NavigatorRequests.singleRoomResponsePayload(9, new RoomDao(MySQL.configuredDatabase()));
        assertEquals(true, singleRoomPayload.contains("GF"));
        assertEquals(true, singleRoomPayload.contains("NavRoom"));
        Handling.kickRoomUser(4, "A_", "A_" + wireLong(88));
        assertEquals(true, containsSend(handlingSends, "@aXjO"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.banRoomUser(4, "E@", "E@" + wireLong(88));
        assertEquals(true, containsSend(handlingSends, "@aXjO"));
        assertEquals(true, containsSql(handlingSql, "INSERT IGNORE INTO rooms_bans"));
        assertEquals(true, containsSql(handlingSql, "UNIX_TIMESTAMP()+900"));
        handlingSql.clear();
        handlingSends.clear();
        String roomRatingPayload = RoomLookups.rateRoomPayload("77", 9, 1, new RoomDao(MySQL.configuredDatabase()));
        assertEquals(true, containsSql(handlingSql, "INSERT INTO rooms_rates"));
        assertEquals(true, containsSql(handlingSql, "UPDATE rooms SET rate='6'"));
        assertEquals(true, roomRatingPayload.contains("EY"));
        handlingSql.clear();
        handlingSends.clear();
        String roomRightRevokedPayload = RoomLookups.revokeRoomRightByNamePayload(
            "Target", 9, new UserDao(MySQL.configuredDatabase()), new RoomDao(MySQL.configuredDatabase()));
        assertEquals(true, containsSql(handlingSql, "DELETE FROM rooms_rights WHERE id_user='88'"));
        assertEquals(true, roomRightRevokedPayload.contains("Fc"));
        handlingSql.clear();
        handlingSends.clear();
        String roomRightGrantedPayload = RoomLookups.grantRoomRightPayload(
            88, 9, new RoomDao(MySQL.configuredDatabase()));
        assertEquals(true, containsSql(handlingSql, "INSERT IGNORE INTO rooms_rights"));
        assertEquals("@j", roomRightGrantedPayload);
        handlingSql.clear();
        handlingSends.clear();
        FurnitureWire.StickyNoteUpdate stickyNoteUpdate =
            FurnitureWire.stickyNoteUpdate("AT" + wireLong(70) + "9CFF9C\nhello\nworld");
        String stickyNoteUpdatedPayload = FurnitureLookups.updateStickyNotePayload(
            stickyNoteUpdate, 9, new FurnitureDao(MySQL.configuredDatabase()), GameDataCaches.productCache());
        assertEquals(true, containsSql(handlingSql, "UPDATE furnitures SET sign='9CFF9C'"));
        assertEquals(true, containsSql(handlingSql, "caption='hello\u001fworld'"));
        assertEquals(true, stickyNoteUpdatedPayload.contains("AT70\u0001AS70"));
        assertEquals(true, stickyNoteUpdatedPayload.contains("9CFF9C"));
        handlingSends.clear();
        String stickyNotePayload = FurnitureLookups.stickyNotePayload(
            70, 9, new FurnitureDao(MySQL.configuredDatabase()), GameDataCaches.productCache());
        assertEquals(true, stickyNotePayload.contains("@p70\2" + "9CFF9C\rhello\rworld\2"));
        handlingSql.clear();
        handlingSends.clear();
        String deletedStickyNotePayload = FurnitureLookups.deleteStickyNotePayload(
            70, 9, new FurnitureDao(MySQL.configuredDatabase()), GameDataCaches.productCache());
        assertEquals(true, containsSql(handlingSql, "DELETE FROM furnitures WHERE id='70' LIMIT 1"));
        assertEquals(true, deletedStickyNotePayload.contains("AT70"));
        handlingSql.clear();
        handlingSends.clear();
        FurnitureLookups.PresentOpenResult presentOpenResult = FurnitureLookups.openPresent(
            71, 9, 77, new FurnitureDao(MySQL.configuredDatabase()), GameDataCaches.productCache());
        assertEquals(true, presentOpenResult.removedPayload().contains("A^71\2H\2"));
        assertEquals(true, containsSql(handlingSql, "DELETE FROM furnitures WHERE id='71' LIMIT 1"));
        assertEquals(true, containsSql(handlingSql, "INSERT INTO furnitures(id_product,id_owner,sign,task_owner,task_time) VALUES('502','77','gift sign','77',UNIX_TIMESTAMP())"));
        assertEquals(true, presentOpenResult.responsePayload().contains("BAs\2"));
        assertEquals(true, presentOpenResult.responsePayload().contains("Opened Sofa"));
        handlingSql.clear();
        handlingSends.clear();
        String wallStatePayload = FurnitureLookups.toggleWallFurnitureStatePayload(
            72, 9, new FurnitureDao(MySQL.configuredDatabase()), CatalogState.instance().registry(), GameDataCaches.productCache());
        assertEquals(true, containsSql(handlingSql, "UPDATE furnitures SET sign='2' WHERE id='72'"));
        assertEquals(true, wallStatePayload.contains("AU72\2"));
        assertEquals(true, wallStatePayload.contains("2\2" + "0\2"));
        handlingSql.clear();
        handlingSends.clear();
        RoomLookups.RoomRightSocketRevocation allRightsRevocation = RoomLookups.revokeAllRoomRights(
            9, new RoomDao(MySQL.configuredDatabase()));
        assertEquals(true, containsSql(handlingSql, "DELETE FROM rooms_rights WHERE id_room='9'"));
        assertEquals(List.of(8L), allRightsRevocation.socketIndexes());
        assertEquals("@k", allRightsRevocation.notificationPayload());
        handlingSql.clear();
        handlingSends.clear();
        assertEquals(true, RoomLookups.deleteRoom(9, new RoomDao(MySQL.configuredDatabase())));
        assertEquals(true, containsSql(handlingSql, "DELETE FROM rooms WHERE id='9' LIMIT 1"));
        handlingSql.clear();
        handlingSends.clear();
        FurnitureLookups.CreditFurnitureRedemption creditRedemption = FurnitureLookups.redeemCreditFurniture(
            73, 9, 77, new FurnitureDao(MySQL.configuredDatabase()), new UserDao(MySQL.configuredDatabase()),
            GameDataCaches.productCache());
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET credits=credits+10 WHERE id='77'"));
        assertEquals(true, containsSql(handlingSql, "DELETE FROM furnitures WHERE id='73' LIMIT 1"));
        assertEquals("@F110.0\2", creditRedemption.creditsPayload());
        assertEquals("A^73\2H\2", creditRedemption.removedPayload());
        handlingSql.clear();
        handlingSends.clear();
        RoomLookups.RoomRightRevocation roomRightRevocation = RoomLookups.revokeRoomRights(
            List.of(88L), 9, new RoomDao(MySQL.configuredDatabase()));
        assertEquals(true, containsSql(handlingSql, "DELETE FROM rooms_rights WHERE id_user='88' AND id_room='9'"));
        assertEquals(List.of(88L), roomRightRevocation.targetUserIds());
        assertEquals("@k", roomRightRevocation.notificationPayload());
        handlingSql.clear();
        handlingSends.clear();
        String targetNameRightRevokedPayload = RoomLookups.revokeRoomRightByNamePayload(
            "Target", 9, new UserDao(MySQL.configuredDatabase()), new RoomDao(MySQL.configuredDatabase()));
        assertEquals(true, containsSql(handlingSql, "DELETE FROM rooms_rights WHERE id_user='88' AND id_room='9'"));
        assertEquals(true, targetNameRightRevokedPayload.contains("Fc"));
        handlingSends.clear();
        String officialRoomModelPayload = RoomLookups.officialRoomModelPayload(
            9, new RoomDao(MySQL.configuredDatabase()));
        assertEquals(true, officialRoomModelPayload.contains("GE"));
        assertEquals(true, officialRoomModelPayload.contains("model.swf"));
        assertEquals(true, officialRoomModelPayload.contains("Official Room"));
        Handling.sendOfficialRoomModel(4, "FD", "FD" + wireLong(9));
        assertEquals(true, containsSend(handlingSends, "GE"));
        assertEquals(true, containsSend(handlingSends, "model.swf"));
        assertEquals(true, containsSend(handlingSends, "Official Room"));
        handlingSends.clear();
        String roomUserEntryBroadcastPayload = SocialLookups.roomUserEntryBroadcastPayload(
            "77", 9, 4, new RoomDao(MySQL.configuredDatabase()));
        assertEquals(true, roomUserEntryBroadcastPayload.contains("@\\"));
        assertEquals(true, roomUserEntryBroadcastPayload.contains("Caller"));
        assertEquals(true, roomUserEntryBroadcastPayload.contains("hd-180-1"));
        Handling.broadcastCurrentRoomUserEntry(4, 9);
        assertEquals(true, containsSend(handlingSends, "@\\"));
        assertEquals(true, containsSend(handlingSends, "Caller"));
        assertEquals(true, containsSend(handlingSends, "hd-180-1"));
        handlingSends.clear();
        RepresentedBotRegistry originalRepresentedBotsForRoomList = PetState.instance().representedBots();
        PetState.instance().setRepresentedBots(representedBots(Map.of(
            200L, "4\2" + "501\2RoomBot\2hello\2speech\2responses\2" + "5\2" + "6\2" + "0.5\2" + "3\2" + "1 2 ff\2")));
        List<String> roomOccupantListPayloads = SocialLookups.roomOccupantListPayloads(
            9, RoomState.instance().representedRooms(), new RoomDao(MySQL.configuredDatabase()));
        assertEquals(true, roomOccupantListPayloads.stream().anyMatch(payload -> payload.contains("@\\")));
        assertEquals(true, roomOccupantListPayloads.stream().anyMatch(payload -> payload.contains("Caller")));
        assertEquals(true, roomOccupantListPayloads.stream().anyMatch(payload -> payload.contains("RoomBot")));
        assertEquals(true, roomOccupantListPayloads.stream().anyMatch(payload -> payload.contains("Du")));
        Handling.sendRoomOccupantList(4, 9);
        assertEquals(true, containsSend(handlingSends, "@\\"));
        assertEquals(true, containsSend(handlingSends, "Caller"));
        assertEquals(true, containsSend(handlingSends, "RoomBot"));
        assertEquals(true, containsSend(handlingSends, "Du"));
        assertEquals(true, containsSend(handlingSends, "0.5"));
        PetState.instance().setRepresentedBots(originalRepresentedBotsForRoomList);
        handlingSends.clear();
        RoomLookups.RoomModelLoad roomModelLoad =
            RoomLookups.roomModelLoad(9, new RoomDao(MySQL.configuredDatabase()));
        assertEquals(true, roomModelLoad.valid());
        assertEquals(20L, roomModelLoad.modelId());
        assertEquals(true, roomModelLoad.initialPayloads().contains("Bf/client.php\2"));
        assertEquals(true, roomModelLoad.initialPayloads().contains("@_000\r111\2"));
        assertEquals(true, roomModelLoad.initialPayloads().contains("GWH000\r111\2H"));
        Handling.loadCurrentRoomModel(4);
        assertEquals(true, containsSend(handlingSends, "Bf/client.php"));
        assertEquals(true, containsSend(handlingSends, "AE9"));
        assertEquals(true, containsSend(handlingSends, "@_000\r111\2"));
        assertEquals(true, containsSend(handlingSends, "GWH000\r111\2H"));
        assertEquals(true, containsSend(handlingSends, "CP\2\2"));
        handlingSends.clear();
        RoomLookups.RoomPresentationLoad roomPresentationLoad =
            RoomLookups.roomPresentationLoad("77", 9, true,
                Handling.roomEventInfoPayload(9), new RoomDao(MySQL.configuredDatabase()));
        assertEquals(true, roomPresentationLoad.valid());
        assertEquals(20L, roomPresentationLoad.modelId());
        assertEquals(true, roomPresentationLoad.initialPayloads().contains("@nfloor\2floorA\2"));
        assertEquals(true, roomPresentationLoad.initialPayloads().contains("@nwallpaper\2wallA\2"));
        assertEquals(true, roomPresentationLoad.initialPayloads().contains("@nlandscape\2landA\2"));
        assertEquals(true, roomPresentationLoad.initialPayloads().contains("@o"));
        Handling.sendCurrentRoomDecoration(4, "@{", "@{");
        assertEquals(true, containsSend(handlingSends, "@nfloor\2floorA\2"));
        assertEquals(true, containsSend(handlingSends, "@nwallpaper\2wallA\2"));
        assertEquals(true, containsSend(handlingSends, "@nlandscape\2landA\2"));
        assertEquals(true, containsSend(handlingSends, "Er"));
        assertEquals(true, containsSend(handlingSends, "@o"));
        assertEquals(true, containsSend(handlingSends, "GX"));
        handlingSends.clear();
        Handling.processPreSessionPacketBuffer(4, "x@Bpa");
        assertEquals(true, containsSend(handlingSends, "J|H"));
        handlingSends.clear();
        List<String> activeEffectPayloads = SocialLookups.activeRoomEffectPayloads(
            9, new RoomDao(MySQL.configuredDatabase()));
        assertEquals(true, activeEffectPayloads.stream().anyMatch(payload -> payload.contains("Ge")));
        Handling.sendRoomActiveEffects(4, 9);
        assertEquals(true, containsSend(handlingSends, "Ge"));
        handlingSends.clear();
        String modelFurniturePayload = Handling.sendRoomModelFurniture(4, 20);
        assertEquals(true, modelFurniturePayload.contains("@^"));
        assertEquals(true, modelFurniturePayload.contains("state"));
        assertEquals(modelFurniturePayload,
            FurnitureLookups.modelFurniturePayload(20, new RoomDao(MySQL.configuredDatabase())));
        assertEquals(modelFurniturePayload,
            FurnitureLookups.modelFurniturePayloadForRoom(0, 9, new RoomDao(MySQL.configuredDatabase())));
        assertEquals(true, containsSend(handlingSends, "@^"));
        handlingSends.clear();
        String directCachePayload = WiredLookups.roomStartupCachePayload(9, WiredState.instance().settings());
        assertEquals(true, directCachePayload.contains("DiWIREDSTATE"));
        assertEquals(true, directCachePayload.contains("trigger-cache"));
        assertEquals(true, directCachePayload.contains("room-cache"));
        String cachePayload = Handling.sendRoomStartupCache(4, 9);
        assertEquals(true, cachePayload.contains("DiWIREDSTATE"));
        assertEquals(true, cachePayload.contains("trigger-cache"));
        assertEquals(true, cachePayload.contains("room-cache"));
        assertEquals(true, containsSend(handlingSends, "DiWIREDSTATE"));
        handlingSends.clear();
        String wallPayload = Handling.sendRoomWallFurniture(4, 9);
        assertEquals(true, wallPayload.contains("@m"));
        assertEquals(true, wallPayload.contains(":w=1,2 l=3,4"));
        assertEquals(true, wallPayload.contains("wall-state"));
        assertEquals(wallPayload,
            FurnitureLookups.wallFurniturePayload(9, new FurnitureDao(MySQL.configuredDatabase())));
        assertEquals(true, containsSend(handlingSends, "@m"));
        handlingSends.clear();
        String petPreviewPayload = Handling.sendPetPackagePreview(4, "p`", "p`" + wireLong(75));
        assertEquals(true, petPreviewPayload.contains("Ly"));
        assertEquals(true, petPreviewPayload.endsWith("3\2"));
        assertEquals(true, containsSend(handlingSends, "Ly"));
        handlingSends.clear();
        NavigatorState.instance().setNewFriendRooms(List.of(new NewFriendRooms.RoomPick(12L, 1L)),
            LocalDateTime.now().plusSeconds(90L));
        assertEquals(NavigatorPayloads.newFriendRoom(new NewFriendRooms.RoomPick(12L, 1L)),
            NavigatorRequests.newFriendRoomPayload(LocalDateTime.now(), NavigatorState.instance(), null));
        Handling.sendNewFriendRoom(4, "Gj", "Gj");
        assertEquals(true, containsSend(handlingSends, "L\u007f"));
        handlingSends.clear();
        handlingSql.clear();
        FurnitureDimmers.PresetPayload userDimmerPresets = FurnitureDimmers.presetsForUser(
            "77", 9, new RoomDao(MySQL.configuredDatabase()), new FurnitureDao(MySQL.configuredDatabase()));
        assertEquals(2L, userDimmerPresets.currentPresetId());
        assertEquals(true, userDimmerPresets.payload().contains("#82F349"));
        assertEquals(2L, Handling.sendDimmerPresets(4));
        assertEquals(true, containsSend(handlingSends, "Em"));
        assertEquals(true, containsSend(handlingSends, "#82F349"));
        handlingSends.clear();
        handlingSql.clear();
        assertEquals(1L, Handling.toggleDimmerState(4, "EW", "EW"));
        assertEquals(true, containsSql(handlingSql, "UPDATE furnitures SET sign='1,2,1,#82F349,100' WHERE id='78'"));
        assertEquals(true, containsSend(handlingSends, "AU78\2"));
        assertEquals(true, containsSend(handlingSends, "1,2,1,#82F349,100"));
        handlingSends.clear();
        handlingSql.clear();
        long dimmerId = Handling.updateDimmerPreset(4, "EV",
            "EV" + wireLong(2) + wireLong(1) + wireString("#82f349") + wireLong(100));
        assertEquals(78L, dimmerId);
        assertEquals(true, containsSql(handlingSql, "UPDATE furnitures_dimmerpresets SET id_state='1'"));
        assertEquals(true, containsSql(handlingSql, "colour='#82F349'"));
        assertEquals(true, containsSql(handlingSql, "sign='2,2,1,#82F349,100'"));
        assertEquals(true, containsSend(handlingSends, "AU78\2"));
        assertEquals(true, containsSend(handlingSends, "2,2,1,#82F349,100"));
        handlingSql.clear();
        handlingSends.clear();
        UserPayloads.EffectListPayload userEffectList = UserLookups.effectListPayload(
            "77", new UserDao(MySQL.configuredDatabase()));
        assertEquals(2L, userEffectList.listedEffects());
        assertEquals(true, userEffectList.payload().contains("GL"));
        handlingSends.clear();
        UserEffectActivation activatedEffect = UserLookups.activateUserEffect(
            "77", 12L, 4L, new UserDao(MySQL.configuredDatabase()));
        assertEquals(12L, activatedEffect.effectId());
        assertEquals(true, containsSql(handlingSql, "UPDATE users_effects SET timestamp_expire=UNIX_TIMESTAMP()+time_rent"));
        assertEquals(true, activatedEffect.payload().contains("GN"));
        assertEquals(true, activatedEffect.broadcastPayload().contains("Ge"));
        handlingSql.clear();
        handlingSends.clear();
        List<UserEffectExpiry> expiredEffects = UserLookups.expiredUserEffects(new UserDao(MySQL.configuredDatabase()));
        assertEquals(1, expiredEffects.size());
        assertEquals(true, containsSql(handlingSql, "DELETE FROM users_effects WHERE users_effects.timestamp_expire IS NOT NULL"));
        assertEquals(true, expiredEffects.get(0).payload().contains("GO"));
        assertEquals(true, expiredEffects.get(0).broadcastPayload().contains("Ge"));
        handlingSql.clear();
        handlingSends.clear();
        String creatableRoomCountPayload = RoomLookups.creatableRoomCountPayload(
            "77",
            AppConfigState.instance().settingsCache().longValueOrDefault("com.server.socket.game.rooms.own.max", 0),
            new RoomDao(MySQL.configuredDatabase()));
        assertEquals(true, creatableRoomCountPayload.contains("H@"));
        handlingSends.clear();
        CreatedRoom createdRoom = RoomLookups.createRoom(
            "77",
            RoomWire.createRoomRequest("@]" + wireString("Created Room") + wireString("model_a")),
            AppConfigState.instance().settingsCache().longValueOrDefault("com.server.socket.game.rooms.own.max", 0),
            UserLookups.hcLevel("77", new UserDao(MySQL.configuredDatabase())),
            new RoomDao(MySQL.configuredDatabase()));
        assertEquals(true, createdRoom.valid());
        assertEquals(true, containsSql(handlingSql, "INSERT INTO rooms(id_owner,name,visitors_max,id_model,timestamp_created)"));
        assertEquals(true, createdRoom.payload().contains("@{"));
        assertEquals(true, createdRoom.payload().contains("Created Room"));
        assertEquals(2, createdRoom.cacheInvalidationPaths().size());
        assertEquals(true, createdRoom.cacheInvalidationPaths().get(0).endsWith("CACHE/ROOMS/" + createdRoom.roomId() + ".cache"));
        assertEquals(true, createdRoom.cacheInvalidationPaths().get(1).endsWith("CACHE/PATHFINDER/" + createdRoom.roomId() + ".cache"));
        handlingSql.clear();
        handlingSends.clear();
        StaffPickedToggle staffPickedToggle = RoomLookups.toggleStaffPickedRoom(
            9,
            AppConfigState.instance().settingsCache().longValueOrDefault("com.client.navigator.staff_picked.category.id.default", 0),
            AppConfigState.instance().settingsCache().longValueOrDefault("com.client.navigator.staff_picked.style.default", 0),
            AppConfigState.instance().settingsCache().longValueOrDefault("com.client.navigator.staff_picked.category.icon.default", 0),
            new RoomDao(MySQL.configuredDatabase()),
            new UserDao(MySQL.configuredDatabase()));
        assertEquals(true, staffPickedToggle.changed());
        assertEquals(true, containsSql(handlingSql, "INSERT INTO rooms_official"));
        assertEquals(true, containsSql(handlingSql, "UPDATE rooms SET is_staff_picked='1'"));
        handlingSql.clear();
        handlingSends.clear();
        String favouriteRoomIdsPayload = RoomLookups.favouriteRoomIdsPayload(
            "77",
            AppConfigState.instance().settingsCache().longValueOrDefault("com.server.socket.game.rooms.favourites.max", 30),
            new RoomDao(MySQL.configuredDatabase()));
        assertEquals(true, favouriteRoomIdsPayload.contains("GJ"));
        handlingSends.clear();
        String favouriteAddedPayload = RoomLookups.addFavouriteRoomPayload(
            "77", 9, new RoomDao(MySQL.configuredDatabase()));
        assertEquals(true, containsSql(handlingSql, "INSERT INTO rooms_favourites"));
        assertEquals(true, favouriteAddedPayload.contains("GK"));
        handlingSql.clear();
        handlingSends.clear();
        String favouriteRemovedPayload = RoomLookups.removeFavouriteRoomPayload(
            "77", 9, new RoomDao(MySQL.configuredDatabase()));
        assertEquals(true, containsSql(handlingSql, "DELETE FROM rooms_favourites WHERE id_room='9'"));
        assertEquals(true, favouriteRemovedPayload.contains("GK"));
        handlingSql.clear();
        handlingSends.clear();
        assertEquals("C]CATEGORY_PAYLOAD", NavigatorState.instance().roomCategoryCache().rankPayload(2, 1));
        String eventCategoryPayload = NavigatorRequests.eventCategoryQueryPayload(
            2,
            NavigatorRequests.listLimit(AppConfigState.instance().settingsCache()),
            1,
            new RoomDao(MySQL.configuredDatabase()),
            NavigatorState.instance().recommendedRooms());
        assertEquals(true, eventCategoryPayload.contains("GCPC2"));
        assertEquals(true, eventCategoryPayload.contains("RECOMMENDED"));
        String popularCategoryPayload = NavigatorRequests.popularCategoryQueryPayload(
            2,
            NavigatorRequests.listLimit(AppConfigState.instance().settingsCache()),
            1,
            new RoomDao(MySQL.configuredDatabase()),
            NavigatorState.instance().recommendedRooms());
        assertEquals(true, popularCategoryPayload.contains("GC 2"));
        assertEquals(true, popularCategoryPayload.contains("RECOMMENDED"));
        RoomDao navigatorRequestRooms = new RoomDao(MySQL.configuredDatabase());
        long navigatorLimit = NavigatorRequests.listLimit(AppConfigState.instance().settingsCache());
        assertEquals(true, NavigatorRequests.eventCategoryQueryPayload(
            "GC" + wireLong(2), AppConfigState.instance().settingsCache(),
            NavigatorState.instance().recommendedRooms(), navigatorRequestRooms).contains("GCPC2"));
        assertEquals(true, NavigatorRequests.popularCategoryQueryPayload(
            "GC" + wireLong(2), AppConfigState.instance().settingsCache(),
            NavigatorState.instance().recommendedRooms(), navigatorRequestRooms).contains("GC 2"));
        assertEquals(true, NavigatorRequests.friendCurrentQueryPayload(77, navigatorLimit, navigatorRequestRooms).contains("GCQA"));
        assertEquals(true, NavigatorRequests.friendCurrentQueryPayload(
            "77", AppConfigState.instance().settingsCache(), navigatorRequestRooms).contains("GCQA"));
        assertEquals(true, NavigatorRequests.friendOwnedQueryPayload(77, navigatorLimit, navigatorRequestRooms).contains("GC\0"));
        assertEquals(true, NavigatorRequests.friendOwnedQueryPayload(
            "77", AppConfigState.instance().settingsCache(), navigatorRequestRooms).contains("GC\0"));
        assertEquals(true, NavigatorRequests.favouriteQueryPayload(77, navigatorLimit, navigatorRequestRooms).contains("GCRA"));
        assertEquals(true, NavigatorRequests.favouriteQueryPayload(
            "77", AppConfigState.instance().settingsCache(), navigatorRequestRooms).contains("GCRA"));
        assertEquals(true, NavigatorRequests.recentlyVisitedQueryPayload(77, navigatorLimit, navigatorRequestRooms).contains("GCSA"));
        assertEquals(true, NavigatorRequests.recentlyVisitedQueryPayload(
            "77", AppConfigState.instance().settingsCache(), navigatorRequestRooms).contains("GCSA"));
        assertEquals(true, NavigatorRequests.ownedQueryPayload(77, navigatorLimit, navigatorRequestRooms).contains("GCQA"));
        assertEquals(true, NavigatorRequests.ownedQueryPayload(
            "77", AppConfigState.instance().settingsCache(), navigatorRequestRooms).contains("GCQA"));
        String officialNavigatorPayload = NavigatorRequests.officialNavigatorPayload(navigatorRequestRooms);
        assertEquals(true, officialNavigatorPayload.contains("GB"));
        assertEquals(true, officialNavigatorPayload.contains("caption"));
        String popularTagsPayload = NavigatorRequests.popularTagsPayload(navigatorLimit, navigatorRequestRooms);
        assertEquals(true, popularTagsPayload.contains("GD"));
        assertEquals(true, popularTagsPayload.contains("tag1"));
        assertEquals(true, NavigatorRequests.popularTagsPayload(
            AppConfigState.instance().settingsCache(), navigatorRequestRooms).contains("tag1"));
        String navigatorTimeFormat = AppConfigState.instance().settingsCache().valueOrDefault("com.mysql.format.time", "%H:%i");
        String tagResultsPayload = NavigatorRequests.tagResultsQueryPayload(
            "tag1", navigatorTimeFormat, navigatorLimit, navigatorRequestRooms);
        assertEquals(true, tagResultsPayload.contains("GCSAtag1"));
        assertEquals(true, tagResultsPayload.contains("Event Room"));
        assertEquals(true, NavigatorRequests.topRatedQueryPayload(navigatorLimit, navigatorRequestRooms).contains("GC\b"));
        assertEquals(true, NavigatorRequests.topRatedQueryPayload(
            AppConfigState.instance().settingsCache(), navigatorRequestRooms).contains("GC\b"));
        String searchResultsPayload = NavigatorRequests.searchResultsQueryPayload(
            "Nav", navigatorTimeFormat, navigatorLimit, navigatorRequestRooms);
        assertEquals(true, searchResultsPayload.contains("GCSANav"));
        assertEquals(true, searchResultsPayload.contains("Event Room"));
        assertEquals(true, NavigatorRequests.tagResultsQueryPayload(
            "GCtag1", AppConfigState.instance().settingsCache(), navigatorRequestRooms).contains("GCSAtag1"));
        assertEquals(true, NavigatorRequests.searchResultsQueryPayload(
            "GCNav", AppConfigState.instance().settingsCache(), navigatorRequestRooms).contains("GCSANav"));
        handlingSends.clear();
        Handling.sendClubGiftStatus(4, "GZ", "GZ");
        assertEquals(true, containsSend(handlingSends, "IoM"));
        assertEquals(true, containsSend(handlingSends, "GIFTS"));
        handlingSends.clear();
        handlingSql.clear();
        String hcGiftPayload = Handling.claimClubGift(4, "G[", "G[" + wireString("trade_sprite"));
        assertEquals(true, hcGiftPayload.contains("AC"));
        assertEquals(true, containsSql(handlingSql, "INSERT INTO furnitures(id_product,id_ctlgproduct,id_owner,task_owner,task_time,position_r,sign) VALUES('506','81','77','77',UNIX_TIMESTAMP(),'0','')"));
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET hc_presents=hc_presents-1 WHERE id='77'"));
        assertEquals(true, containsSend(handlingSends, "AC"));
        assertEquals(true, containsSend(handlingSends, "BLS"));
        handlingSends.clear();
        handlingSql.clear();
        String purchasePayload = Handling.purchaseCatalogProduct(4, "Ad",
            "Ad" + wireLong(81) + wireString("catalog sign"));
        assertEquals(true, purchasePayload.contains("AC"));
        assertEquals(true, containsSql(handlingSql, "INSERT INTO furnitures(id_product,id_owner,sign,task_owner,task_time,id_ctlgproduct) VALUES('506','77','catalog sign','77',UNIX_TIMESTAMP(),'81')"));
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET credits=credits-3,activitypoints_0=activitypoints_0-2 WHERE id='77'"));
        assertEquals(true, containsSend(handlingSends, "Ab"));
        assertEquals(true, containsSend(handlingSends, "AC"));
        assertEquals(true, containsSend(handlingSends, "BLS"));
        handlingSends.clear();
        handlingSql.clear();
        String giftPayload = Handling.purchaseCatalogGift(4, "GX", "GX" + wireLong(81) + wireLong(506)
            + wireString("Target") + wireString("gift note") + wireLong(501) + wireLong(2) + wireLong(3));
        assertEquals(true, giftPayload.contains("AC"));
        assertEquals(true, containsSql(handlingSql, "INSERT INTO furnitures(id_product,id_owner,sign,task_owner,task_time,id_ctlgproduct) VALUES('506','77','gift note','77',UNIX_TIMESTAMP(),'81')"));
        assertEquals(true, containsSql(handlingSql, "UPDATE furnitures SET sign_extra='gift note',sign='"));
        assertEquals(true, containsSql(handlingSql, "id_owner='88',id_destination='81',id_secondary='3002' WHERE id='97'"));
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET credits=credits-10,activitypoints_0=activitypoints_0-2 WHERE id='77'"));
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET gifts_given=gifts_given+1 WHERE id='77'"));
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET gifts_received=gifts_received+1 WHERE id='88'"));
        assertEquals(true, containsSend(handlingSends, "4:DATA"));
        assertEquals(true, containsSend(handlingSends, "8:DATA"));
        assertEquals(true, containsSend(handlingSends, "Ab"));
        handlingSends.clear();
        Handling.sendCatalogGiftAvailability(4, "oV", "oV" + wireLong(81));
        assertEquals(true, containsSend(handlingSends, "In"));
        handlingSends.clear();
        Handling.sendCatalogGiftWrapOptions(4, "oC", "oC");
        assertEquals(true, containsSend(handlingSends, "WRAP_PAYLOAD"));
        handlingSends.clear();
        Handling.sendCatalogPage(4, "Af", "xx" + wireLong(2));
        assertEquals(true, containsSend(handlingSends, "A\u007f"));
        assertEquals(true, containsSend(handlingSends, "PAGE_PAYLOAD"));
        handlingSends.clear();
        Handling.sendInventoryToSocket(4);
        assertEquals(true, containsSend(handlingSends, "BLS"));
        assertEquals(true, containsSend(handlingSends, "Id"));
        handlingSends.clear();
        handlingSql.clear();
        VoucherRedemption voucherRedemption = VoucherRedemption.redeem(
            "ABCD0000",
            77,
            new VoucherDao(MySQL.configuredDatabase()),
            new UserDao(MySQL.configuredDatabase()),
            GameDataCaches.productCache());
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET credits=credits+5"));
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET activitypoints_0=activitypoints_0+7"));
        assertEquals(true, containsSql(handlingSql, "DELETE FROM vouchers WHERE name='ABCD0000'"));
        assertEquals(true, voucherRedemption.responsePayload().contains("CTRewardA"));
        assertEquals(true, voucherRedemption.creditsRefreshRequired());
        assertEquals(true, voucherRedemption.activityPointRefreshRequired());
        handlingSql.clear();
        handlingSends.clear();
        FurnitureLookups.RoomDecorationApplication decorationApplication =
            FurnitureLookups.applyRoomDecorationFurniture(
                79, 9, 77, new FurnitureDao(MySQL.configuredDatabase()), new RoomDao(MySQL.configuredDatabase()),
                CatalogState.instance().registry());
        assertEquals("@nwallpaper\2paper1\2", decorationApplication.roomPayload());
        assertEquals(InventoryMessagePayloads.remove(79), decorationApplication.inventoryRemovePayload());
        assertEquals(true, containsSql(handlingSql, "UPDATE rooms SET id_wallpaper='paper1'"));
        assertEquals(true, containsSql(handlingSql, "DELETE FROM furnitures WHERE id='79' LIMIT 1"));
        handlingSql.clear();
        handlingSends.clear();
        String activityPointBalancePayload = UserLookups.activityPointBalancePayload(
            "77", new UserDao(MySQL.configuredDatabase()));
        assertEquals(true, activityPointBalancePayload.contains("M@"));
        Handling.sendActivityPointBalanceToSocket(4);
        assertEquals(true, containsSend(handlingSends, "M@"));
        handlingSends.clear();
        handlingSql.clear();
        FurnitureLookups.FurnitureInventoryReturn inventoryReturn =
            FurnitureLookups.returnRoomFurnitureToInventory(
                80, 9, 77, false, true, false, new FurnitureDao(MySQL.configuredDatabase()));
        assertEquals(true, containsSql(handlingSql, "UPDATE furnitures SET id_room=NULL"));
        assertEquals("A^80\2", inventoryReturn.removedPayload());
        handlingSql.clear();
        handlingSends.clear();
        RoomState.instance().setFurnitureRoomCache(FurnitureRoomCache.State.empty());
        RoomState.instance().setFurnitureRoomCache(FurnitureStateWrites.trackMarker(RoomState.instance().furnitureRoomCache(), 9, 81));
        assertEquals(true, RoomState.instance().furnitureRoomCache().pendingRoomCache.contains("\1" + "9\2"));
        assertEquals(true, RoomState.instance().furnitureRoomCache().pendingFurnitureCache.contains("\1" + "81\2"));
        RoomState.instance().setFurnitureRoomCache(FurnitureStateWrites.refreshState(RoomState.instance().furnitureRoomCache(), 9, 82, 3));
        assertEquals(true, RoomState.instance().representedRooms().cacheText().contains("\1" + "9\t82\t3\2"));
        handlingSends.clear();
        assertEquals(93L, FurnitureWire.habbowheelFurnitureId(wireLong(93)));
        assertEquals(93L, FurnitureLookups.habbowheelFurnitureId(
            wireLong(93), 9, new FurnitureDao(MySQL.configuredDatabase()), GameDataCaches.productCache()));
        FurnitureLookups.SimpleFloorUse simpleUse = FurnitureLookups.simpleFloorUse(
            93, 9, new RoomUserPosition(2, 3, true), 0, true, new FurnitureDao(MySQL.configuredDatabase()),
            GameDataCaches.productCache());
        assertEquals(true, simpleUse.payload().contains("AZ"));
        String simpleUsePayload = Handling.handlingSimpleFloorItemUse(
            4, "AM" + wireLong(93), "AM", 0L, true, RoomUserPosition.absent());
        assertEquals(simpleUse.payload(), simpleUsePayload);
        assertEquals(true, containsSend(handlingSends, "AZ"));
        assertEquals(true, RoomState.instance().representedRooms().cacheText().contains("\1" + "9\t93\t0\2"));
        handlingSends.clear();
        FurnitureLookups.SimpleFloorUse simpleReset = FurnitureLookups.simpleFloorUse(
            93, 9, new RoomUserPosition(2, 3, true), -1, false, new FurnitureDao(MySQL.configuredDatabase()),
            GameDataCaches.productCache());
        assertEquals(true, simpleReset.payload().contains("AZ"));
        String simpleResetPayload = Handling.handlingSimpleFloorItemUse(
            4, "AL" + wireLong(93), "AL", -1L, false, RoomUserPosition.absent());
        assertEquals(simpleReset.payload(), simpleResetPayload);
        assertEquals(true, containsSend(handlingSends, "AZ"));
        assertEquals(true, RoomState.instance().furnitureRoomCache().pendingRoomCache.contains("\1" + "9\2"));
        RoomState.instance().setFurnitureRoomCache(FurnitureStateWrites.refreshState(RoomState.instance().furnitureRoomCache(), 9, 82, 3));
        handlingSends.clear();
        handlingSql.clear();
        FurnitureLookups.FloorFurniturePlacement movedFloorPlacement = FurnitureLookups.placeOrMoveFloorFurniture(
            FurnitureWire.floorPlacement("94 5 6 2"),
            9,
            77,
            false,
            new FurnitureDao(MySQL.configuredDatabase()),
            CatalogState.instance().registry());
        assertEquals(true, movedFloorPlacement.roomPayload().contains("A_"));
        assertEquals(true, containsSql(handlingSql, "UPDATE furnitures SET position_x='5',position_y='6',position_z='0',position_r='2'"));
        handlingSends.clear();
        handlingSql.clear();
        String movedFloorWrapperPayload = Handling.moveFloorFurnitureInRoom(4, "AI94 6 7 4");
        assertEquals(true, movedFloorWrapperPayload.contains("A_"));
        assertEquals(true, containsSql(handlingSql, "UPDATE furnitures SET position_x='6',position_y='7',position_z='0',position_r='4'"));
        handlingSends.clear();
        handlingSql.clear();
        FurnitureLookups.FloorFurniturePlacement placedFloorPlacement = FurnitureLookups.placeOrMoveFloorFurniture(
            FurnitureWire.floorPlacement("95 2 3 6"),
            9,
            77,
            true,
            new FurnitureDao(MySQL.configuredDatabase()),
            CatalogState.instance().registry());
        assertEquals(true, placedFloorPlacement.roomPayload().contains("A]"));
        assertEquals(true, containsSql(handlingSql, "UPDATE furnitures SET id_owner=NULL,id_room='9',position_x='2',position_y='3',position_z='0',position_r='6'"));
        assertEquals(InventoryMessagePayloads.remove(95), placedFloorPlacement.inventoryRemovePayload());
        handlingSends.clear();
        handlingSql.clear();
        RoomState.instance().setFurnitureRoomCache(FurnitureStateWrites.removeMarker(RoomState.instance().furnitureRoomCache(), 9, 82));
        assertEquals(false, RoomState.instance().furnitureRoomCache().pendingFurnitureCache.contains("\1" + "82\2"));
        assertEquals(true, RoomState.instance().representedRooms().cacheText().contains("\1" + "9\t82\t3\2"));
        RoomState.instance().setFurnitureRoomCache(FurnitureRoomCache.State.empty());
        FurnitureDao furniture = new FurnitureDao(MySQL.configuredDatabase());
        FurnitureStateWrites.Result writeResult = FurnitureStateWrites.write(
            RoomState.instance().furnitureRoomCache(), 9, 83, "on", furniture);
        RoomState.instance().setFurnitureRoomCache(writeResult.state());
        assertEquals(true, RoomState.instance().representedRooms().cacheText().contains("\1" + "83\t9\ton\2"));
        writeResult = FurnitureStateWrites.write(RoomState.instance().furnitureRoomCache(), 0, 83, "off", furniture);
        RoomState.instance().setFurnitureRoomCache(writeResult.state());
        assertEquals(9L, writeResult.roomId());
        assertEquals(true, RoomState.instance().representedRooms().cacheText().contains("\1" + "83\t9\toff\2"));
        assertEquals(false, RoomState.instance().representedRooms().cacheText().contains("\1" + "83\t9\ton\2"));
        handlingSends.clear();
        FurnitureLookups.LocatedFurnitureStateRefresh locatedRefresh =
            FurnitureLookups.refreshLocatedFurnitureState(
                84, 0, new FurnitureDao(MySQL.configuredDatabase()), GameDataCaches.productCache());
        assertEquals("AX84\2" + "5\2", locatedRefresh.payload());
        String refreshPayload = Handling.refreshLocatedFurnitureState(84, 0);
        assertEquals(locatedRefresh.payload(), refreshPayload);
        assertEquals(true, RoomState.instance().representedRooms().cacheText().contains("\1" + "9\t84\t5\2"));
        assertEquals(true, containsSend(handlingSends, locatedRefresh.payload()));
        handlingSql.clear();
        handlingSends.clear();
        RoomState.instance().setFurnitureRoomCache(FurnitureRoomCache.State.from(
            RoomState.instance().furnitureRoomCache().pendingRoomCache,
            "\1" + "85\2",
            RoomState.instance().representedRooms()));
        RoomState.instance().setFurnitureRoomCache(FurnitureStateWrites.removeMarker(
            RoomState.instance().furnitureRoomCache(), 9, 85));
        FurnitureLookups.RoomFurniturePickup roomPickup = FurnitureLookups.pickUpRoomFurniture(
            85, 9, 77, false, true, false, new FurnitureDao(MySQL.configuredDatabase()));
        assertEquals(false, RoomState.instance().furnitureRoomCache().pendingFurnitureCache.contains("\1" + "85\2"));
        assertEquals(true, containsSql(handlingSql, "UPDATE furnitures SET id_room=NULL"));
        assertEquals(true, containsSql(handlingSql, "WHERE id='85' AND id_room='9' LIMIT 1"));
        assertEquals(InventoryMessagePayloads.remove(85), roomPickup.inventoryRemovePayload());
        assertEquals("A^85\2", roomPickup.removedPayload());
        assertEquals(false, roomPickup.moderationLogRequired());
        handlingSql.clear();
        handlingSends.clear();
        String chargePrompt = FurnitureCharges.consumeOrPrompt(
            87,
            CatalogState.instance().registry().product(508).orElse(null),
            AppPaths.applicationPath());
        assertEquals(true, chargePrompt.contains("Iu"));
        handlingSends.clear();
        FurnitureLookups.FloorFurnitureStateToggle floorToggle = FurnitureLookups.toggleFloorFurnitureState(
            86,
            9,
            77,
            new FurnitureDao(MySQL.configuredDatabase()),
            GameDataCaches.productCache(),
            CatalogState.instance().registry(),
            AppPaths.applicationPath());
        assertEquals("AX86\2" + "1\2", floorToggle.payload());
        assertEquals(true, containsSql(handlingSql, "UPDATE furnitures SET sign='1'"));
        handlingSql.clear();
        handlingSends.clear();
        assertEquals(false, FurnitureLookups.openFloorFurniturePackage(
            86, 9, new FurnitureDao(MySQL.configuredDatabase()), new PackageDao(MySQL.configuredDatabase()))
            .hasPayload());
        assertEquals(Long.valueOf(86L), Handling.openFloorFurniturePackageOrToggleState(
            4, FurnitureWire.floorFurniturePackageRequest("FH" + wireLong(86))));
        assertEquals(true, containsSend(handlingSends, "AX86\2" + "1\2"));
        handlingSql.clear();
        handlingSends.clear();
        RoomState.instance().setRepresentedRooms(RepresentedRoomCache.empty());
        List<FurnitureLookups.FloorPositionStateRefresh> positionRefreshes =
            FurnitureLookups.floorStateRefreshesAtPosition(
                9, 2, 3, new FurnitureDao(MySQL.configuredDatabase()), GameDataCaches.productCache());
        assertEquals(1, positionRefreshes.size());
        assertEquals("AX88\2" + "4\2", positionRefreshes.get(0).payload());
        assertEquals(1L, Handling.refreshFloorFurnitureStatesAtPosition(9, 2, 3));
        assertEquals(true, RoomState.instance().representedRooms().cacheText().contains("\1" + "9\t88\t4\2"));
        assertEquals(true, containsSend(handlingSends, positionRefreshes.get(0).payload()));
        assertEquals(false, containsSend(handlingSends, "AX89\2" + "7\2"));
        handlingSql.clear();
        handlingSends.clear();
        FurnitureLookups.WallFurniturePlacement wallPlacement = FurnitureLookups.placeWallFurnitureFromInventory(
            ":w=1,2 l=3,4",
            90,
            9,
            77,
            new FurnitureDao.InventoryPlacementFurniture(9L, 90L, "wall-state", 6L, 0L),
            new FurnitureDao(MySQL.configuredDatabase()),
            GameDataCaches.productCache());
        assertEquals(true, containsSql(handlingSql, "UPDATE furnitures SET position_wall=':w=1,2 l=3,4'"));
        assertEquals(true, containsSql(handlingSql, "WHERE id='90' AND id_owner='77' AND id_room IS NULL LIMIT 1"));
        assertEquals(InventoryMessagePayloads.remove(90), wallPlacement.inventoryRemovePayload());
        assertEquals(true, wallPlacement.roomPayload().contains("AS0"));
        handlingSql.clear();
        handlingSends.clear();
        FurnitureDao placementFurniture = new FurnitureDao(MySQL.configuredDatabase());
        RoomDao placementRooms = new RoomDao(MySQL.configuredDatabase());
        assertEquals(1L, RoomPositionService.canPlaceFloorFurnitureAt(
            91, 0, 0, 1, 1, placementFurniture, placementRooms, RoomState.instance().representedRooms()));
        assertEquals(0L, RoomPositionService.canPlaceFloorFurnitureAt(
            91, 1, 1, 1, 1, placementFurniture, placementRooms, RoomState.instance().representedRooms()));
        assertEquals(0L, RoomPositionService.canPlaceFloorFurnitureAt(
            91, 2, 2, 1, 1, placementFurniture, placementRooms, RoomState.instance().representedRooms()));
        handlingSql.clear();
        handlingSends.clear();
        FurnitureScoreStates.Target scoreTarget = FurnitureScoreStates.refreshTarget(
            510, 92, new FurnitureDao(MySQL.configuredDatabase()), GameDataCaches.productCache());
        assertEquals(true, scoreTarget.found());
        FurnitureLookups.LocatedFurnitureStateRefresh scoreboardRefresh =
            FurnitureLookups.refreshLocatedFurnitureState(
                scoreTarget.furnitureId(),
                scoreTarget.productId(),
                new FurnitureDao(MySQL.configuredDatabase()),
                GameDataCaches.productCache());
        assertEquals("AX92\2" + "0\2", scoreboardRefresh.payload());
        String scoreboardPayload = Handling.refreshLocatedFurnitureState(scoreTarget.furnitureId(), scoreTarget.productId());
        assertEquals(scoreboardRefresh.payload(), scoreboardPayload);
        assertEquals(true, containsSql(handlingSql, "UPDATE furnitures SET sign='0' WHERE id='92' LIMIT 1"));
        assertEquals(true, containsSend(handlingSends, scoreboardRefresh.payload()));
        handlingSql.clear();
        handlingSends.clear();
        Handling.sendClientDateSettings(4);
        assertEquals(true, containsSend(handlingSends, "DAQBHHIIKHJHPAHQA"));
        assertEquals(true, containsSend(handlingSends, "http://www.alpha-series.com/"));
        handlingSends.clear();
        Filesystems.processReadyPacketBuffer(4, "x@BCN");
        assertEquals(true, containsSend(handlingSends, "DAQBHHIIKHJHPAHQA"));
        handlingSends.clear();
        handlingSql.clear();
        assertEquals("77", Handling.handleLoginTicket(4, "F_login-77"));
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET login_ticket=null,id_socket = '4' WHERE id = '77'"));
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET respect_amount='5',scratch_amount='5',update_time=UNIX_TIMESTAMP() WHERE id='77' LIMIT 1"));
        assertEquals(true, containsSend(handlingSends, "@C"));
        assertEquals(true, containsSend(handlingSends, "@F123.0"));
        assertEquals(true, containsSend(handlingSends, "Fv"));
        assertEquals(true, containsSend(handlingSends, "GG"));
        assertEquals(true, containsSend(handlingSends, "DX"));
        assertEquals(true, containsSend(handlingSends, "Cd"));
        assertEquals(true, containsSend(handlingSends, "E^"));
        assertEquals(77L, SessionState.instance().sessionUserIdBySocket(4));
        handlingSends.clear();
        handlingSql.clear();
        String friendNotifyPayload = Handling.sendMessengerFriendOnlineNotification(4, 0);
        assertEquals(true, friendNotifyPayload.startsWith("@MHIH"));
        assertEquals(true, friendNotifyPayload.contains("User77"));
        assertEquals(true, containsSend(handlingSends, "@MHIH"));
        assertEquals(true, containsSend(handlingSends, "User77"));
        handlingSends.clear();
        handlingSql.clear();
        assertEquals(1L, Handling.deleteMessengerFriendRequests(4, "@f", "@fA"));
        assertEquals(true, containsSql(handlingSql, "DELETE FROM friendships WHERE id_user='77' AND has_accept='0' LIMIT 75"));
        handlingSql.clear();
        assertEquals(1L, Handling.deleteMessengerFriendRequests(4, "@f", "@fCABA"));
        assertEquals(true, containsSql(handlingSql, "id_friend IN ('1','2') LIMIT 75"));
        handlingSql.clear();
        handlingSends.clear();
        String followPayload = MessengerLookups.followRoomPayload(
            77, 88, 61, 9, new MessengerDao(MySQL.configuredDatabase()));
        assertEquals(MessengerPayloads.followRoom(61, 9), followPayload);
        handlingSends.clear();
        handlingSql.clear();
        String inviteWire = wireLong(1) + wireLong(88) + wireString("Join me");
        String inviteText = WireEncoding.readBase64LengthString(inviteWire);
        MessengerWire.RoomInviteRequest inviteRequest = MessengerWire.roomInviteFromWire("@b" + inviteWire);
        MessengerRoomInvite invite = MessengerLookups.roomInvite(
            77,
            61,
            4,
            inviteRequest,
            inviteText,
            new MessengerDao(MySQL.configuredDatabase()),
            targetUserId -> targetUserId == 88L ? 8 : 0,
            targetUserId -> targetUserId == 88L ? "Target" : "");
        assertEquals(encodedVl64(77, null, "BG") + inviteText + "\2", invite.payload());
        assertEquals(1, invite.notifications().size());
        assertEquals(invite.notifications(), invite.deliveryPayloads());
        assertEquals("BG", invite.notifications().get(0).payload().substring(0, 2));
        assertEquals(true, containsSql(handlingSql, "(Invite To: Target) -- " + inviteText));
        handlingSql.clear();
        handlingSends.clear();
        String messengerDateTimeFormat = StringUtils.sqlEscapedText(
            AppConfigState.instance().settingsCache().valueOrDefault("com.mysql.format.date", "%d-%m-%Y")
                + " "
                + AppConfigState.instance().settingsCache().valueOrDefault("com.mysql.format.time", "%H:%i"));
        AcceptedFriendRequests acceptedFriends = MessengerLookups.acceptPendingFriends(
            77, List.of(88L), messengerDateTimeFormat, new MessengerDao(MySQL.configuredDatabase()));
        assertEquals(true, acceptedFriends.valid());
        assertEquals(acceptedFriends.notifications(), acceptedFriends.deliveryPayloads());
        assertEquals(1, acceptedFriends.deliveryPayloads().size());
        handlingSql.clear();
        handlingSends.clear();
        String acceptPayload = Handling.acceptMessengerFriendRequests(
            4, MessengerWire.acceptFriendRequests("@e" + wireLong(1) + wireLong(88)));
        assertEquals(true, acceptPayload.startsWith("@MH"));
        assertEquals(true, acceptPayload.contains("Target"));
        assertEquals(true, containsSql(handlingSql, "INSERT IGNORE INTO friendships(id_user,id_friend,has_accept) VALUES('88','77','0')"));
        assertEquals(true, containsSql(handlingSql, "UPDATE friendships SET has_accept='1'"));
        assertEquals(true, containsSend(handlingSends, "@MHIH"));
        assertEquals(true, containsSend(handlingSends, "User77"));
        assertEquals(true, containsSend(handlingSends, "@MH"));
        handlingSql.clear();
        handlingSends.clear();
        String removePayload = Handling.removeMessengerFriends(4, "@h", "@h" + wireLong(1) + wireLong(88));
        assertEquals(MessengerPayloads.removeFriends(List.of(88L)), removePayload);
        assertEquals(true, containsSql(handlingSql, "DELETE FROM friendships WHERE has_accept='1'"));
        assertEquals(true, containsSql(handlingSql, "id_friend IN ('88')"));
        assertEquals(true, containsSend(handlingSends, "@MMIM77"));
        assertEquals(true, containsSend(handlingSends, "@MM"));
        handlingSql.clear();
        handlingSends.clear();
        String searchPayload = Handling.searchMessengerUsers(4, MessengerWire.searchRequest("@i" + wireString("targ"), "@i"));
        assertEquals(true, searchPayload.startsWith("Fs"));
        assertEquals(true, searchPayload.contains("Target"));
        assertEquals(true, searchPayload.contains("Targus"));
        assertEquals(true, containsSend(handlingSends, "Fs"));
        handlingSends.clear();
        handlingSql.clear();
        String chatWire = wireLong(88) + wireString("Private hello");
        String chatText = WireEncoding.readBase64LengthString(chatWire);
        String privateChatPayload = Handling.sendMessengerPrivateMessage(
            4, MessengerWire.privateMessageFromWire("@a" + chatWire));
        assertEquals(encodedVl64(77, null, "BF") + chatText + "\2", privateChatPayload);
        assertEquals(true, containsSql(handlingSql, "(Chat To:     Target) -- " + chatText));
        assertEquals(true, containsSend(handlingSends, "BF"));
        handlingSql.clear();
        handlingSends.clear();
        String requestPayload = Handling.requestMessengerFriend(
            4, MessengerWire.friendRequest("@g" + wireString("Target"), "@g"));
        assertEquals(MessengerPayloads.requestAcceptedCaller(88), requestPayload);
        assertEquals(true, containsSql(handlingSql, "INSERT IGNORE INTO friendships(id_user,id_friend) VALUES('88','77')"));
        assertEquals(true, containsSend(handlingSends, "BD"));
        assertEquals(true, containsSend(handlingSends, "DD"));
        handlingSql.clear();
        handlingSends.clear();
        String pendingPayload = Handling.sendMessengerPendingRequests(4, "Ci", "Ci");
        assertEquals(MessengerPayloads.pendingRequests(List.of(new PendingFriendRequest(88L, "Target"))), pendingPayload);
        assertEquals(true, containsSend(handlingSends, "Dz"));
        assertEquals(true, containsSend(handlingSends, "Target"));
        handlingSends.clear();
        String friendListPayload = Handling.sendMessengerFriendList(4, "@L", "@L");
        assertEquals(true, friendListPayload.startsWith("@L"));
        assertEquals(true, friendListPayload.contains("Target"));
        assertEquals(true, friendListPayload.endsWith("PYH"));
        assertEquals(true, containsSend(handlingSends, "@MHIH"));
        assertEquals(true, containsSend(handlingSends, "@L"));
        handlingSends.clear();
        String raceListPayload = Handling.sendPetRaceList(4, "n\u007f", "n\u007f" + wireString("dog"));
        assertEquals(PetPayloads.raceList("dog", List.of(
            new PetRaceRow(1L, 1L, 0L, 0L, "A"),
            new PetRaceRow(2L, 2L, 2L, 0L, "B")), 1, 0), raceListPayload);
        assertEquals(true, containsSend(handlingSends, "L{dog\2"));
        handlingSends.clear();
        String petInventoryPayload = Handling.sendPetInventory(4, "nx", "nx");
        assertEquals(PetPayloads.inventoryList(List.of(new PetInventoryRow(10L, "Rex", "1 2 FF00AA", 4L))),
            petInventoryPayload);
        assertEquals(true, containsSend(handlingSends, "IX"));
        assertEquals(true, containsSend(handlingSends, "Rex"));
        handlingSends.clear();
        assertEquals(2L, PetPayloads.nameValidationCode("Rex1"));
        String nameCheckPayload = Handling.validatePetName(4, "@j", "@c" + wireString("Rex"));
        assertEquals(PetPayloads.nameValidation("Rex"), nameCheckPayload);
        assertEquals(true, containsSend(handlingSends, "@d"));
        handlingSends.clear();
        handlingSql.clear();
        assertEquals("30", Handling.placePetFromPackage(4, "n~", "n~" + wireLong(93) + wireString("Buddy")));
        assertEquals(true, containsSql(handlingSql, "INSERT INTO bots(id_user,figure,name,id_handle) VALUES('77','1 2 3','Buddy','3')"));
        assertEquals(true, containsSql(handlingSql, "INSERT INTO bots_petdata(id_bot,timestamp_buy,id_owner,energy,nutrition,scratches) VALUES('30',UNIX_TIMESTAMP(),'77','100','100','0')"));
        assertEquals(true, containsSql(handlingSql, "DELETE FROM furnitures WHERE id='93' LIMIT 1"));
        assertEquals(true, containsSend(handlingSends, "I["));
        assertEquals(true, containsSend(handlingSends, "Buddy"));
        assertEquals(true, containsSend(handlingSends, "A^93"));
        assertEquals(true, containsSend(handlingSends, "Lz"));
        handlingSql.clear();
        PetPackagePlacement packagePlacement = PetLookups.packagePlacementAction(
            93,
            9,
            77,
            "Buddy",
            0,
            new FurnitureDao(MySQL.configuredDatabase()),
            new PackageDao(MySQL.configuredDatabase()),
            new BotDao(MySQL.configuredDatabase()),
            new RoomDao(MySQL.configuredDatabase()));
        assertEquals(30L, packagePlacement.botId());
        assertEquals(true, packagePlacement.inventoryAddPayload().contains("Buddy"));
        assertEquals(true, packagePlacement.nameValidationPayload().contains("Lz"));
        assertEquals(true, containsSql(handlingSql, "INSERT INTO bots(id_user,figure,name,id_handle) VALUES('77','1 2 3','Buddy','3')"));
        handlingSends.clear();
        String petStatusPayload = Handling.sendPetStatus(4, "ny", "ny" + wireLong(10));
        PetStatusRow expectedPetStatusRow = new PetStatusRow(
            10L, "Rex", "1 2 ff", 2L, 7L, 100L, 90L, 4L, 12L, 5L, "Owner");
        assertEquals(PetPayloads.status(10, expectedPetStatusRow), petStatusPayload);
        assertEquals(true, containsSend(handlingSends, "IY"));
        assertEquals(true, containsSend(handlingSends, "Owner"));
        handlingSends.clear();
        List<PetSettings.PetCommandRow> commandRows = List.of(
            new PetSettings.PetCommandRow(1L, 0L, "sit", "gst sit", 4),
            new PetSettings.PetCommandRow(2L, 3L, "jump", "gst jump", 4)
        );
        PetSettings previousPetSettings = PetState.instance().settings();
        PetState.instance().setSettings(PetSettings.fromRaceRows(
            previousPetSettings.races(),
            previousPetSettings.levels(),
            commandRows,
            commandRows.size()));
        String petCommandPayload = Handling.sendPetCommandList(4, 2);
        assertEquals(PetPayloads.commandList(2, commandRows), petCommandPayload);
        assertEquals(true, containsSend(handlingSends, "I]"));
        handlingSends.clear();
        handlingSql.clear();
        String routedCommandPayload = Handling.sendPetCommandListForTarget(4, "n|", "n|" + wireLong(10));
        assertEquals(PetPayloads.commandList(3, commandRows), routedCommandPayload);
        assertEquals(true, containsSend(handlingSends, "I]"));
        handlingSends.clear();
        RepresentedBotRegistry originalRepresentedBotsForCommand = PetState.instance().representedBots();
        PetState.instance().setRepresentedBots(representedBots(Map.of(
            300L, "4\2" + "10\2Rex\2hello\2speech\2responses\2" + "2\2" + "3\2" + "0.0\2" + "0\2" + "1 2 ff\2")));
        handlingSql.clear();
        assertEquals(1L, Handling.performPetCommand(4, "n{", "n{" + wireLong(300) + wireLong(1)));
        assertEquals(true, containsSql(handlingSql, "UPDATE bots_petdata SET id_level='3',experience='0' WHERE id_bot='10'"));
        assertEquals(true, containsSend(handlingSends, "IZ"));
        assertEquals(true, containsSend(handlingSends, "gst sit"));
        PetState.instance().setRepresentedBots(originalRepresentedBotsForCommand);
        PetState.instance().setSettings(previousPetSettings);
        handlingSends.clear();
        handlingSql.clear();
        assertEquals(42L, Handling.guideInviteUserIdFromWire(4, "oL", "oL" + wireString("42")));
        assertEquals("", Handling.ignoreClientReadyPacket());
        handlingSends.clear();
        handlingSql.clear();
        assertEquals(50L, UserLookups.updateSoundSetting(
            "77", UserWire.soundSettingRequest("Ce50"), new UserDao(MySQL.configuredDatabase())));
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET settings_sound='50' WHERE id='77' LIMIT 1"));
        seedCatalogPageTrees(Map.of(new CatalogPages.PageTreeKey(0L, 0L), "CATALOG_TREE"));
        Handling.dispatchPreReadyPacket(4, "Ae", "Ae");
        assertEquals("CATALOG_TREE", CatalogState.instance().catalogPages().defaultPageTree());
        assertCatalogPagesTypedAccessors();
        assertEquals(true, containsSend(handlingSends, "A~IHHM\2CATALOG_TREE"));
        handlingSends.clear();
        Handling.dispatchPreReadyPacket(4, "D}", "D}");
        assertEquals(true, containsSend(handlingSends, "Ei"));
        handlingSends.clear();
        handlingSql.clear();
        assertEquals(3L, Handling.awardPetExperience(10, 3));
        assertEquals(true, containsSql(handlingSql, "UPDATE bots_petdata SET id_level='3',experience='0' WHERE id_bot='10'"));
        assertEquals(true, containsSend(handlingSends, "@X"));
        assertEquals(true, containsSend(handlingSends, "IY"));
        assertEquals(true, containsSend(handlingSends, "Ia"));
        handlingSql.clear();
        handlingSends.clear();
        assertEquals(5L, Handling.scratchPet(4, "n}", "n}" + wireLong(10)));
        assertEquals(true, containsSql(handlingSql, "UPDATE bots_petdata SET scratches='5' WHERE id_bot='10'"));
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET scratch_amount=scratch_amount-1,scratch_given=scratch_given+1 WHERE id='77'"));
        assertEquals(true, containsSend(handlingSends, "I^"));
        assertEquals(true, containsSend(handlingSends, "Rex"));
        handlingSends.clear();
        handlingSql.clear();
        PetState.instance().setRepresentedBots(RepresentedBotRegistry.empty());
        long placedPetEntityId = Handling.placePetInRoom(4, "nz",
            "nz" + wireLong(10) + wireLong(2) + wireLong(3) + wireLong(4));
        assertEquals(true, placedPetEntityId > 0L);
        assertEquals(10L, PetState.instance().representedBots().record(placedPetEntityId).botId());
        assertEquals(true, containsSql(handlingSql, "UPDATE bots SET id_room='9',position_x='2',position_y='3',position_z='0',position_r='4' WHERE id='10'"));
        assertEquals(true, containsSend(handlingSends, "@\\"));
        assertEquals(true, containsSend(handlingSends, "I\\"));
        handlingSql.clear();
        handlingSends.clear();
        assertEquals(10L, Handling.pickUpPetFromRoom(4, placedPetEntityId));
        assertEquals(0L, PetState.instance().representedBots().record(placedPetEntityId).botId());
        assertEquals(true, containsSql(handlingSql, "UPDATE bots SET id_room=null WHERE id='10'"));
        assertEquals(true, containsSql(handlingSql, "UPDATE bots_petdata SET id_level=id_level,energy=energy,experience=experience,nutrition=nutrition,scratches=scratches WHERE id_bot='10'"));
        assertEquals(true, containsSend(handlingSends, "@]"));
        assertEquals(true, containsSend(handlingSends, "I["));
        handlingSends.clear();
        handlingSql.clear();
        PetState.instance().setRepresentedBots(RepresentedBotRegistry.empty());
        long guideEntityId = Handling.spawnTutorialGuideBot(4, "Fx", "Fx");
        assertEquals(true, guideEntityId > 0L);
        assertEquals(20L, PetState.instance().representedBots().record(guideEntityId).botId());
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET tutorial_guide='1' WHERE id='77'"));
        assertEquals(true, containsSend(handlingSends, "@aYjO"));
        handlingSends.clear();
        assertEquals(1L, Handling.removeTutorialGuideBots(4, "Fy", "Fy" + wireLong(0)));
        assertEquals(0L, PetState.instance().representedBots().record(guideEntityId).botId());
        assertEquals(true, containsSend(handlingSends, "@]"));
        handlingSends.clear();
        String profilePayload = Handling.sendRoomUserProfile(4, "Cg", "Cg" + wireLong(61));
        assertEquals(SocialPayloads.roomUserProfile(61, "Target", "Motto", 123, "fig"), profilePayload);
        assertEquals(true, containsSend(handlingSends, "Jf"));
        assertEquals(true, containsSend(handlingSends, "Target"));
        SocialLookups.DirectPayload profileAction =
            SocialLookups.roomUserProfileAction(9L, 61L, new RoomDao(MySQL.configuredDatabase()));
        assertEquals(true, profileAction.hasPayload());
        assertEquals(profilePayload, profileAction.payload());
        assertEquals(false, SocialLookups.roomUserProfileAction(0L, 61L,
            new RoomDao(MySQL.configuredDatabase())).hasPayload());
        handlingSends.clear();
        handlingSql.clear();
        String badgeInventoryPayload = Handling.sendBadgeInventory(4, "B]", "B]");
        assertEquals(SocialPayloads.badgeInventory(
                List.of(new BadgeRow("ACH1", 0L, 201L), new BadgeRow("MOD", 0L, 202L)),
                List.of(new BadgeRow("VIP", 1L, 203L))),
            badgeInventoryPayload);
        assertEquals(true, containsSend(handlingSends, "Ce"));
        assertEquals(true, containsSend(handlingSends, "Cd"));
        handlingSends.clear();
        String badgeUpdateWire = "B^" + wireLong(1) + wireString("VIP")
            + wireLong(0) + wireLong(0) + wireLong(0) + wireLong(0);
        String updatedBadgesPayload = Handling.updateEquippedBadges(4, "B^", badgeUpdateWire);
        assertEquals(SocialPayloads.equippedBadges(List.of(new BadgeRow("VIP", 1L, 203L))), updatedBadgesPayload);
        assertEquals(true, containsSql(handlingSql, "UPDATE users_badges SET id_slot='0' WHERE id_user='77'"));
        assertEquals(true, containsSql(handlingSql, "UPDATE users_badges SET id_slot='1' WHERE id_badge='VIP' AND id_user='77'"));
        assertEquals(true, containsSend(handlingSends, "Cd"));
        SocialLookups.BadgeUpdateResult badgeUpdateResult =
            SocialLookups.updateEquippedBadges("77", SocialWire.badgeUpdateSelections(badgeUpdateWire),
                new UserDao(MySQL.configuredDatabase()));
        assertEquals(SocialPayloads.equippedBadges(List.of(new BadgeRow("VIP", 1L, 203L))),
            badgeUpdateResult.equippedPayload());
        assertEquals(true, badgeUpdateResult.displayPayload().contains("Cd"));
        assertEquals(false, SocialLookups.updateEquippedBadges("0", SocialWire.badgeUpdateSelections(badgeUpdateWire),
            new UserDao(MySQL.configuredDatabase())).hasDisplayPayload());
        handlingSends.clear();
        UserDao socialUsers = new UserDao(MySQL.configuredDatabase());
        assertEquals(SocialPayloads.equippedBadges(List.of(new BadgeRow("VIP", 1L, 203L))),
            SocialLookups.equippedBadgePayload("77", socialUsers));
        assertEquals(SocialPayloads.tags(List.of(new UserDao.UserTagRow("alpha"), new UserDao.UserTagRow("beta"))),
            SocialLookups.tagPayload("77", socialUsers));
        SocialLookups.DirectPayload tagAction =
            SocialLookups.tagDisplayAction("77", 9L, "DG" + wireLong(88), socialUsers);
        assertEquals(true, tagAction.hasPayload());
        assertEquals(SocialPayloads.tagDisplay(88, List.of(new UserDao.UserTagRow("target"))),
            tagAction.payload());
        assertEquals(false, SocialLookups.tagDisplayAction("0", 9L, "DG" + wireLong(88), socialUsers).hasPayload());
        String tagDisplay = Handling.sendUserTags(4, "DG", "DG" + wireLong(88));
        assertEquals(SocialPayloads.tagDisplay(88, List.of(new UserDao.UserTagRow("target"))),
            tagDisplay);
        assertEquals(true, containsSend(handlingSends, "E^"));
        assertEquals(true, containsSend(handlingSends, "target"));
        handlingSends.clear();
        String lookToBadgePayload = Handling.lookAtRoomUserBadge(4, "B_", "B_" + wireLong(61));
        assertEquals(SocialPayloads.badgeDisplay(88, List.of()), lookToBadgePayload);
        assertEquals(true, containsSend(handlingSends, "Cd"));
        RoomUserTargetRow badgeTarget = RoomLookups.activeRoomUserTarget(
            9L, 61L, new RoomDao(MySQL.configuredDatabase())).orElse(null);
        SocialLookups.RoomUserBadgeLook badgeLook =
            SocialLookups.roomUserBadgeLookAction(4L, badgeTarget, new UserDao(MySQL.configuredDatabase()));
        assertEquals(true, badgeLook.hasDirectPayload());
        assertEquals(lookToBadgePayload, badgeLook.directPayload());
        assertEquals(true, badgeLook.statusPayloads().hasCallerPayload());
        assertEquals(true, badgeLook.statusPayloads().hasTargetPayload());
        assertEquals(false, SocialLookups.roomUserBadgeLookAction(0L, badgeTarget,
            new UserDao(MySQL.configuredDatabase())).hasDirectPayload());
        handlingSends.clear();
        RoomState.instance().setRepresentedRooms(RepresentedRoomCache.fromCacheText("")
            .moveOccupant(4, 4, 1, 1, 0, 0));
        Handling.lookTowardRoomPosition(4, "AK", "AK" + wireLong(3) + wireLong(3));
        RepresentedRoomCache.Position lookPosition = RoomState.instance().representedRooms().movementPosition(4, 4);
        assertEquals(true, lookPosition.found());
        assertEquals(1L, lookPosition.positionX());
        assertEquals(1L, lookPosition.positionY());
        assertEquals(true, RoomState.instance().representedRooms().cacheText().contains("\1" + "4\t1\t1\t3\t0\2"));
        Handling.walkTowardRoomPosition(4, "AO", "AO" + wireLong(4) + wireLong(4));
        RepresentedRoomCache.Position walkPosition = RoomState.instance().representedRooms().movementPosition(4, 4);
        assertEquals(true, walkPosition.found());
        assertEquals(2L, walkPosition.positionX());
        assertEquals(2L, walkPosition.positionY());
        assertEquals(true, RoomState.instance().representedRooms().cacheText().contains("\1" + "4\t2\t2\t3\t1\2"));
        RoomState.instance().setRepresentedRooms(RepresentedRoomCache.empty());
        handlingSql.clear();
        PollDao livePollDao = new PollDao(MySQL.configuredDatabase());
        SessionState.instance().setSessionRegistry(sessionRegistry(
            new SessionRegistry.SessionRecord("1:4", "77\2" + "4"),
            new SessionRegistry.SessionRecord("4", "0\2" + "9")));
        PollLookups.RoomRequest pollRequest =
            PollLookups.roomRequest(4, new UserDao(MySQL.configuredDatabase()), new RoomDao(MySQL.configuredDatabase()));
        assertEquals(true, pollRequest.valid());
        assertEquals("77", pollRequest.userId());
        assertEquals(9L, pollRequest.roomId());
        long pollExitId = PollWire.idFromWire("Ck" + wireLong(7), "Ck");
        assertEquals(true, PollLookups.recordExit("77", 9, pollExitId, livePollDao));
        assertEquals(true, containsSql(handlingSql, "INSERT INTO poll_exit(id_user,id_poll) VALUES('77','7')"));
        handlingSql.clear();
        assertEquals(true, PollLookups.submitAnswer(
            "77",
            9,
            PollWire.answerFromWire("Cl" + wireLong(7) + wireLong(8) + wireLong(4) + wireString("yes"), "Cl"),
            livePollDao));
        assertEquals(true, containsSql(handlingSql, "INSERT INTO poll_results(id_poll,id_question,message_answer,id_user,timestamp) VALUES('7','8','yes','77',UNIX_TIMESTAMP())"));
        handlingSends.clear();
        PollDefinition livePoll = new PollDefinition(
            new PollHeader(7L, "Title", "Thanks"),
            List.of(new PollQuestionRow(
                8L,
                "Question?",
                2L,
                List.of(
                    new PollAnswerRow(1L, 8L, "Yes"),
                    new PollAnswerRow(2L, 8L, "No")))));
        String livePollPayload = PollLookups.livePollPayload(
            "77",
            9,
            PollWire.idFromWire("Cj" + wireLong(7), "Cj"),
            livePollDao);
        assertEquals(PollPayloads.poll(livePoll), livePollPayload);
        handlingSends.clear();
        assertEquals(livePollPayload, Handling.sendLivePoll(4, "Cj", "Cj" + wireLong(7)));
        assertEquals(true, containsSend(handlingSends, livePollPayload));
        SessionState.instance().setSessionRegistry(SessionRegistry.empty());
        handlingSends.clear();
        handlingSql.clear();
        String recyclerStatus = RecyclerLookups.statusPayload();
        assertEquals(RecyclerPayloads.status(1, 0), recyclerStatus);
        handlingSends.clear();
        handlingSql.clear();
        RecyclerLookups.SubmitResult recyclerSubmitResult = RecyclerLookups.submitItems(
            "77",
            RecyclerWire.selectionFromWire("F^" + wireLong(5) + wireLong(1) + wireLong(2)
                + wireLong(3) + wireLong(4) + wireLong(5)),
            RecyclerState.instance().settings(),
            new FurnitureDao(MySQL.configuredDatabase()),
            new CatalogDao(MySQL.configuredDatabase()),
            new RecyclerDao(MySQL.configuredDatabase()));
        assertEquals(RecyclerPayloads.reward(506), recyclerSubmitResult.rewardPayload());
        assertEquals(List.of(1L, 2L, 3L, 4L, 5L), recyclerSubmitResult.removedFurnitureIds());
        assertEquals(List.of(
            InventoryMessagePayloads.remove(1),
            InventoryMessagePayloads.remove(2),
            InventoryMessagePayloads.remove(3),
            InventoryMessagePayloads.remove(4),
            InventoryMessagePayloads.remove(5),
            RecyclerPayloads.reward(506)), recyclerSubmitResult.deliveryPayloads());
        assertEquals(true, containsSql(handlingSql, "UPDATE furnitures SET sign='"));
        assertEquals(true, containsSql(handlingSql, "id_owner='77',id_destination='81' WHERE id_owner='77' AND id_product='508'"));
        assertEquals(true, containsSql(handlingSql, "UPDATE furnitures SET id_owner=NULL WHERE id_owner='77' AND id_room IS NULL AND id IN ('1','2','3','4','5')"));
        assertEquals(true, containsSql(handlingSql, "INSERT INTO logs_recycler(id_user,timestamp,items,id_reward,id_session) VALUES('77',UNIX_TIMESTAMP(),'1,2,3,4,5','506','0')"));
        handlingSends.clear();
        handlingSql.clear();
        AchievementRewardGrant achievementGrant = AchievementLookups.grantReward(
            "77", 0, 3, AchievementState.instance().settings(), new UserDao(MySQL.configuredDatabase()));
        String achievementReward = achievementGrant.rewardPayload();
        AchievementSettings.Achievement liveAchievement = new AchievementSettings.Achievement(
            2L, "ACH_", 10L, 5L, 3L, 7L, 2L);
        assertEquals(AchievementPayloads.reward(0, liveAchievement, 3, 204), achievementReward);
        assertEquals(true, containsSql(handlingSql, "DELETE FROM users_badges WHERE id_user='77' AND id_badge LIKE 'ACH_%' LIMIT 1"));
        assertEquals(true, containsSql(handlingSql, "INSERT INTO users_badges(id_user,id_badge) VALUES('77','ACH_3')"));
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET activitypoints_2=activitypoints_2+5,achievement_score=achievement_score+7 WHERE id='77'"));
        assertEquals(true, achievementGrant.awardPayload().startsWith("Fv"));
        assertEquals(List.of(achievementGrant.rewardPayload(), achievementGrant.awardPayload()),
            achievementGrant.deliveryPayloads());
        handlingSql.clear();
        handlingSends.clear();
        AchievementRewardGrant progressedAchievementGrant = AchievementLookups.advanceProgress(
            "77", 2, AchievementState.instance().settings(), new UserDao(MySQL.configuredDatabase()));
        assertEquals(true, progressedAchievementGrant.rewardPayload().startsWith("Fu"));
        assertEquals(progressedAchievementGrant.rewardPayload(),
            progressedAchievementGrant.deliveryPayloads().get(0));
        handlingSql.clear();
        handlingSends.clear();
        String respectLookupPayload = SocialLookups.giveRespectPayload(
            "77", "88", new UserDao(MySQL.configuredDatabase()));
        assertEquals(UserPayloads.respectReceived(88, 12), respectLookupPayload);
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET respect_amount=respect_amount-1,respect_given=respect_given+1 WHERE id='77'"));
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET respect_received=respect_received+1 WHERE id='88'"));
        handlingSql.clear();
        String respectPayload = Handling.giveRespect(4, "Es", "Es" + wireLong(88));
        assertEquals(UserPayloads.respectReceived(88, 12), respectPayload);
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET respect_amount=respect_amount-1,respect_given=respect_given+1 WHERE id='77'"));
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET respect_received=respect_received+1 WHERE id='88'"));
        assertEquals(true, containsSend(handlingSends, "Fx"));
        handlingSends.clear();
        String achievementListPayload = AchievementLookups.listPayload(
            "77", AchievementState.instance().settings(), new UserDao(MySQL.configuredDatabase()));
        Map<String, Long> liveAchievementLevels = new HashMap<>();
        liveAchievementLevels.put("ACH_", 2L);
        assertEquals(AchievementPayloads.list(List.of(liveAchievement), liveAchievementLevels), achievementListPayload);
        handlingSends.clear();
        assertEquals("5;1;7;1;5;0;", WiredPayloads.specialState(1507));
        JukeboxDao liveJukebox = new JukeboxDao(MySQL.configuredDatabase());
        String liveSongInfoWire = "C]" + wireLong(2) + wireLong(50) + wireLong(51);
        String liveSongInfoPayload = JukeboxLookups.songInfoPayload(
            JukeboxRequests.songInfoFromWire(liveSongInfoWire), liveJukebox);
        assertEquals(JukeboxPayloads.songInfo(List.of(
                new SongInfoRow("Song A", 3L, "Author A", "sound-a", 50L),
                new SongInfoRow("Song B", 4L, "Author B", "sound-b", 51L))),
            liveSongInfoPayload);
        handlingSends.clear();
        RoomState.instance().setFurnitureRoomCache(FurnitureRoomCache.State.from(
            RoomState.instance().furnitureRoomCache().pendingRoomCache,
            "\1" + "300\2\1" + "40\2\1" + "999\2",
            RoomState.instance().representedRooms()));
        RoomState.instance().setFurnitureRoomCache(JukeboxLookups.clearSoundMarkers(
            RoomState.instance().furnitureRoomCache(), 9, 0, liveJukebox));
        assertEquals("\1" + "999\2", RoomState.instance().furnitureRoomCache().pendingFurnitureCache);
        handlingSql.clear();
        handlingSends.clear();
        SessionState.instance().setSessionRegistry(sessionRegistry(
            new SessionRegistry.SessionRecord("1:4", "77\2" + "4"),
            new SessionRegistry.SessionRecord("4", "0\2" + "9")));
        JukeboxLookups.RoomRequest jukeboxRequest =
            JukeboxLookups.roomRequest(4, new UserDao(MySQL.configuredDatabase()), new RoomDao(MySQL.configuredDatabase()));
        assertEquals(true, jukeboxRequest.validUser());
        assertEquals(true, jukeboxRequest.validRoom());
        assertEquals("77", jukeboxRequest.userId());
        assertEquals(9L, jukeboxRequest.roomId());
        JukeboxLookups.DiskChangeResult addDiskAction =
            JukeboxLookups.addDiskAction(jukeboxRequest,
                JukeboxRequests.addRequestFromWire("C" + '\177' + wireLong(4) + wireLong(1)), liveJukebox);
        assertEquals(true, addDiskAction.valid());
        String addDiskPayload = addDiskAction.payload();
        assertEquals(encodedVl64(4, null, "Ac"), addDiskPayload);
        assertEquals(true, containsSql(handlingSql, "UPDATE furnitures SET id_owner=NULL WHERE id_owner='77' AND id='4' AND id_product='700' LIMIT 1"));
        assertEquals(true, containsSql(handlingSql, "INSERT INTO soundmachine_jb_playlist(id_jukebox,id_cd,id_order,id_destination) VALUES('300','4','1','50')"));
        assertEquals(addDiskPayload, addDiskAction.deliveryPayloads().get(0));
        assertEquals(3, addDiskAction.deliveryPayloads().size());
        handlingSql.clear();
        handlingSends.clear();
        JukeboxLookups.DiskChangeResult removeDiskAction =
            JukeboxLookups.removeDiskAction(jukeboxRequest,
                JukeboxRequests.removeOrderFromWire("D@" + wireLong(0)), liveJukebox);
        assertEquals(true, removeDiskAction.valid());
        assertEquals(2, removeDiskAction.deliveryPayloads().size());
        assertEquals(true, containsSql(handlingSql, "UPDATE furnitures SET id_owner='77' WHERE id='4' AND id_product='700' LIMIT 1"));
        assertEquals(true, containsSql(handlingSql, "DELETE FROM soundmachine_jb_playlist WHERE id_jukebox='300' AND id_cd='4' LIMIT 1"));
        assertEquals(true, containsSql(handlingSql, "UPDATE soundmachine_jb_playlist SET id_order=id_order-1 WHERE id_jukebox='300' AND id_order>'0'"));
        handlingSends.clear();
        String playlistPayload = JukeboxLookups.playlistPayload(jukeboxRequest, liveJukebox);
        assertEquals(JukeboxPayloads.playlist(5, List.of(
            new JukeboxPlaylistEntry(2L, 40L),
            new JukeboxPlaylistEntry(3L, 41L))), playlistPayload);
        handlingSends.clear();
        String diskInventoryPayload = JukeboxLookups.diskInventoryPayload(jukeboxRequest, liveJukebox);
        assertEquals(JukeboxPayloads.diskInventory(List.of(
            new SongDiskRow(4L, 50L),
            new SongDiskRow(5L, 51L))), diskInventoryPayload);
        handlingSends.clear();
        String playbackPayload = JukeboxLookups.playbackPayload(9, 0, System.currentTimeMillis() / 1000L, liveJukebox);
        assertEquals(true, playbackPayload.startsWith("EG"));
        SessionState.instance().setSessionRegistry(SessionRegistry.empty());
        handlingSends.clear();
        handlingSql.clear();
        SessionState.instance().setSessionRegistry(sessionRegistry(
            new SessionRegistry.SessionRecord("1:4", "77\2" + "4")));
        UserLookups.UserRequest userRequest =
            UserLookups.userRequest(4, new UserDao(MySQL.configuredDatabase()));
        assertEquals(true, userRequest.valid());
        assertEquals("77", userRequest.userId());
        String mottoPayload = UserLookups.updateMottoPayload(
            userRequest, UserWire.mottoRequest("Gd" + wireString("New motto")), new UserDao(MySQL.configuredDatabase()));
        assertEquals(UserPayloads.identityRefresh(77, "New motto", "hd-180-1", "M"), mottoPayload);
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET motto='New motto' WHERE id='77'"));
        SessionState.instance().setSessionRegistry(SessionRegistry.empty());
        handlingSends.clear();
        Handling.sendGuideInvitation(4, "oD", "oD");
        assertEquals(true, containsSend(handlingSends, "IcIQA"));
        handlingSends.clear();
        handlingSql.clear();
        List<QuestSettings.QuestDefinitionRow> liveQuestDefinitions = List.of(
            new QuestSettings.QuestDefinitionRow(10L, 1L, "First", "", 5L, 2L, "visit", 0L, 7L, 3L, 30L, 11),
            new QuestSettings.QuestDefinitionRow(11L, 2L, "Second", "", 6L, 2L, "visit", 0L, 7L, 4L, 0L, 11));
        QuestState.instance().setSettings(QuestSettings.fromDefinitions(liveQuestDefinitions));
        assertEquals(liveQuestDefinitions, QuestState.instance().settings().definitions());
        assertEquals(liveQuestDefinitions, QuestState.instance().settings().definitions());
        assertEquals(QuestPayloads.list(QuestState.instance().settings(), List.of(
                new QuestSettings.UserQuestListRow(10L, 0L, "0", "1", "0", 1L, 0L, 7))),
            QuestProgress.listPayload("77", QuestState.instance().settings(), new QuestDao(MySQL.configuredDatabase())));
        String questListPayload = Handling.sendQuestList(4, "p]", "p]");
        assertEquals(QuestPayloads.list(QuestState.instance().settings(), List.of(
                new QuestSettings.UserQuestListRow(10L, 0L, "0", "1", "0", 1L, 0L, 7))),
            questListPayload);
        assertEquals(true, containsSend(handlingSends, "L`"));
        handlingSends.clear();
        handlingSql.clear();
        QuestResetResult resetResult = QuestProgress.resetQuests(
            "77", QuestState.instance().settings(), new QuestDao(MySQL.configuredDatabase()));
        assertEquals(true, resetResult.reset());
        assertEquals("Lc", resetResult.deliveryPayloads().get(0));
        assertEquals(2, resetResult.deliveryPayloads().size());
        assertEquals(true, containsSql(handlingSql, "UPDATE users_quests SET timestamp_done=NULL,timestamp_accepted=NULL WHERE id_user='77' LIMIT 50"));
        handlingSends.clear();
        handlingSql.clear();
        Handling.resetQuests(4);
        assertEquals(true, containsSql(handlingSql, "UPDATE users_quests SET timestamp_done=NULL,timestamp_accepted=NULL WHERE id_user='77' LIMIT 50"));
        assertEquals(true, containsSend(handlingSends, "Lc"));
        assertEquals(true, containsSend(handlingSends, "L`"));
        handlingSends.clear();
        handlingSql.clear();
        QuestAcceptResult questAcceptResult = QuestProgress.acceptQuest(
            "77", 10, QuestState.instance().settings(), new QuestDao(MySQL.configuredDatabase()));
        assertEquals(true, questAcceptResult.accepted());
        assertEquals(10L, questAcceptResult.questId());
        assertEquals(10L, questAcceptResult.numericQuestId());
        assertEquals(false, questAcceptResult.complete());
        assertEquals(true, containsSql(handlingSql, "INSERT INTO users_quests(id_user,id_quest,id_level,id_numericquest,timestamp_accepted) VALUES('77','10','0','10',UNIX_TIMESTAMP())"));
        assertEquals(true, containsSql(handlingSql, "UPDATE users_quests SET time_next=DATE_ADD(NOW(),INTERVAL 30 SECOND) WHERE id_user='77' AND id_quest='10' LIMIT 1"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.acceptQuest(4, "p^", "p^" + wireLong(10));
        assertEquals(true, containsSql(handlingSql, "INSERT INTO users_quests(id_user,id_quest,id_level,id_numericquest,timestamp_accepted) VALUES('77','10','0','10',UNIX_TIMESTAMP())"));
        assertEquals(true, containsSql(handlingSql, "UPDATE users_quests SET time_next=DATE_ADD(NOW(),INTERVAL 30 SECOND) WHERE id_user='77' AND id_quest='10' LIMIT 1"));
        assertEquals(true, containsSend(handlingSends, "L`"));
        handlingSends.clear();
        handlingSql.clear();
        assertEquals(11L, QuestProgress.nextQuestIdForUser(
            "77", QuestState.instance().settings(), new QuestDao(MySQL.configuredDatabase())));
        Handling.autoAcceptNextQuest(4, "pc", "pc");
        assertEquals(true, containsSql(handlingSql, "id_numericquest='11'"));
        assertEquals(true, containsSend(handlingSends, "L`"));
        handlingSends.clear();
        handlingSql.clear();
        QuestProgressDecision refreshedQuestDecision = QuestProgress.refreshDecision(
            "77", QuestState.instance().settings(), new QuestDao(MySQL.configuredDatabase()));
        assertEquals(true, refreshedQuestDecision.shouldScheduleWait());
        assertEquals(true, refreshedQuestDecision.shouldSendList());
        assertEquals(10L, refreshedQuestDecision.questId());
        assertEquals(10L, refreshedQuestDecision.numericQuestId());
        assertEquals(true, containsSql(handlingSql, "UPDATE users_quests SET time_next=DATE_ADD(NOW(),INTERVAL 30 SECOND) WHERE id_user='77' AND id_quest='10' LIMIT 1"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.refreshQuestProgress(4);
        assertEquals(true, containsSql(handlingSql, "UPDATE users_quests SET time_next=DATE_ADD(NOW(),INTERVAL 30 SECOND) WHERE id_user='77' AND id_quest='10' LIMIT 1"));
        assertEquals(true, containsSend(handlingSends, "L`"));
        handlingSends.clear();
        handlingSql.clear();
        Handling.completeQuest(4, 10, 10);
        assertEquals(true, containsSend(handlingSends, "Lb"));
        assertEquals(true, containsSend(handlingSends, "La"));
        assertEquals(true, containsSend(handlingSends, "Fv"));
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET activitypoints_2=activitypoints_2+5 WHERE id='77' LIMIT 1"));
        assertEquals(true, containsSql(handlingSql, "UPDATE users_quests SET id_level=id_level+1,progress='0',id_numericquest='0',timestamp_done=UNIX_TIMESTAMP() WHERE id_user='77' AND id_quest='10' LIMIT 1"));
        QuestState.instance().setSettings(QuestSettings.empty());
        handlingSends.clear();
        handlingSql.clear();
        SessionState.instance().setSessionRegistry(sessionRegistry(
            new SessionRegistry.SessionRecord("1:4", "77\2" + "4")));
        UserLookups.UserRequest ownProfileRequest =
            UserLookups.userRequest(4, new UserDao(MySQL.configuredDatabase()));
        String ownProfilePayload = UserLookups.ownProfilePayload(
            ownProfileRequest, new UserDao(MySQL.configuredDatabase()));
        assertEquals(UserPayloads.ownProfile(new OwnProfileRow(77L, "Caller", "Motto", "M", 4L, 2L)),
            ownProfilePayload);
        SessionState.instance().setSessionRegistry(SessionRegistry.empty());
        handlingSends.clear();
        UserActivityPoints.AwardBatch pointAwardBatch = UserActivityPoints.timedActivityPointAwardBatch(
            4, "77", AppConfigState.instance().settingsCache(), new UserDao(MySQL.configuredDatabase()));
        assertEquals(UserPayloads.activityPointAward(0, 75), pointAwardBatch.payload());
        assertEquals(List.of(UserPayloads.activityPointAward(0, 75)), pointAwardBatch.deliveryPayloads());
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET activitypoints_0=activitypoints_0+5 WHERE id='77'"));
        handlingSends.clear();
        TradeInteractionRequestAction requestAction = TradeLookups.requestInteractionAction(
            4,
            "77",
            9,
            61,
            new RoomDao(MySQL.configuredDatabase()),
            TradeState.instance(),
            userId -> userId == 88L ? 8 : 0);
        assertEquals(true, requestAction.valid());
        assertEquals(8L, requestAction.targetSocketIndex());
        assertEquals(true, requestAction.sourcePayload().contains("Ah"));
        assertEquals(true, requestAction.targetPayload().contains("Ah"));
        TradeState.instance().removeInteractionPair(4);
        TradeState.instance().removeInteractionPair(8);
        Handling.requestInteraction(4, "AG", "AG" + wireLong(61));
        assertEquals(8, TradeState.instance().interactionPartner(4));
        assertEquals(4, TradeState.instance().interactionPartner(8));
        assertEquals(true, containsSend(handlingSends, "Ah"));
        assertEquals(true, containsSend(handlingSends, "8:DATA"));
        handlingSends.clear();
        TradeInteractionStateAction stateAction = TradeLookups.interactionStateAction(
            4, 61, 0, false, 0, TradeState.instance());
        assertEquals(true, stateAction.valid());
        assertEquals(8L, stateAction.targetSocketIndex());
        assertEquals(1L, stateAction.interactionState());
        assertEquals(true, stateAction.sourcePayload().contains("Am"));
        assertEquals(true, stateAction.targetPayload().contains("Am"));
        assertEquals("Ao", stateAction.completionPayload());
        Handling.sendInteractionState(4, 0, null);
        assertEquals(true, containsSend(handlingSends, "Am"));
        assertEquals(true, containsSend(handlingSends, "Ao"));
        handlingSends.clear();
        TradeState.instance().removeInteractionPair(4);
        TradeState.instance().removeInteractionPair(8);
        TradeState.instance().storeInteractionPair(4, 8, 1);
        TradeOfferAction tradeOfferAction = TradeLookups.addOfferAction(
            4,
            8,
            "77",
            "88",
            76,
            TradeState.instance(),
            new FurnitureDao(MySQL.configuredDatabase()));
        assertEquals(true, tradeOfferAction.sourcePayload().contains("Trade Chair"));
        assertEquals(true, tradeOfferAction.targetPayload().contains("Trade Chair"));
        TradeState.instance().removeInteractionPair(4);
        TradeState.instance().removeInteractionPair(8);
        Handling.requestInteraction(4, "AG", "AG" + wireLong(61));
        String carriedTradePayload = Handling.addTradeFurniture(4, "FU", "FU" + wireLong(76));
        assertEquals(true, carriedTradePayload.contains("Al"));
        assertEquals(true, carriedTradePayload.contains("Trade Chair"));
        assertEquals(true, containsSend(handlingSends, "Al"));
        assertEquals(true, containsSend(handlingSends, "8:DATA"));
        handlingSends.clear();
        String removedTradePayload = Handling.removeTradeFurniture(4, "AH", "AH" + wireLong(76));
        assertEquals(true, removedTradePayload.contains("Al"));
        assertEquals(false, removedTradePayload.contains("Trade Chair"));
        assertEquals(true, containsSend(handlingSends, "Al"));
        handlingSends.clear();
        handlingSql.clear();
        Handling.requestInteraction(4, "AG", "AG" + wireLong(61));
        Handling.addTradeFurniture(4, "FU", "FU" + wireLong(76));
        Handling.addTradeFurniture(8, "FU", "FU" + wireLong(86));
        handlingSends.clear();
        handlingSql.clear();
        TradeConfirmation tradeConfirmation = TradeLookups.confirmTradeAction(
            4,
            "77",
            "88",
            9,
            "session-77",
            TradeState.instance(),
            new TradeDao(MySQL.configuredDatabase()));
        assertEquals(true, tradeConfirmation.valid());
        assertEquals("Ap", tradeConfirmation.payload());
        assertEquals(8L, tradeConfirmation.targetSocketIndex());
        assertEquals(true, containsSql(handlingSql, "INSERT INTO logs_trading(id_user,id_partner,items_user,items_partner,id_room,timestamp,id_session) VALUES('77','88','76:506','86:506','9',UNIX_TIMESTAMP(),'session-77')"));
        assertEquals(0, TradeState.instance().interactionPartner(4));
        handlingSql.clear();
        Handling.requestInteraction(4, "AG", "AG" + wireLong(61));
        Handling.addTradeFurniture(4, "FU", "FU" + wireLong(76));
        Handling.addTradeFurniture(8, "FU", "FU" + wireLong(86));
        handlingSends.clear();
        handlingSql.clear();
        assertEquals("Ap", Handling.confirmTrade(4, "FR", "FR"));
        assertEquals(true, containsSql(handlingSql, "UPDATE furnitures SET id_owner='88' WHERE id IN ('76') AND id_owner='77' AND id_room IS NULL"));
        assertEquals(true, containsSql(handlingSql, "UPDATE furnitures SET id_owner='77' WHERE id IN ('86') AND id_owner='88' AND id_room IS NULL"));
        assertEquals(true, containsSql(handlingSql, "INSERT INTO logs_trading(id_user,id_partner,items_user,items_partner,id_room,timestamp,id_session) VALUES('77','88','76:506','86:506','9',UNIX_TIMESTAMP(),'session-77')"));
        assertEquals(true, containsSend(handlingSends, "Ap"));
        assertEquals(0, TradeState.instance().interactionPartner(4));
        handlingSends.clear();
        TradeState.instance().storeInteractionPair(4, 8, 1);
        TradeInteractionCloseAction closeAction = TradeLookups.closeInteractionAction(
            4, 8, 9, 61, TradeState.instance());
        assertEquals(true, closeAction.valid());
        assertEquals(8L, closeAction.targetSocketIndex());
        assertEquals(true, closeAction.payload().contains("An"));
        assertEquals(0, TradeState.instance().interactionPartner(4));
        Handling.requestInteraction(4, "AG", "AG" + wireLong(61));
        handlingSends.clear();
        Handling.closeInteraction(4, 0);
        assertEquals(true, containsSend(handlingSends, "An"));
        assertEquals(true, containsSend(handlingSends, "8:DATA"));
        assertEquals(0, TradeState.instance().interactionPartner(4));
        handlingSql.clear();
        Handling.clearSocketUser(4);
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET id_socket=null WHERE id = '77'"));
        MySQL.configureDatabaseConnection(null);
        MusConnectionManager.instance().configureSink(null);
        Guardian.setSocketConnected(4, false);
        Guardian.setSocketConnected(8, false);
        GameDataCaches.setProductCache(originalProductCache);
        CatalogState.instance().setRegistry(originalCatalogRegistry);
        NavigatorState.instance().setRoomCategoryCache(originalRoomCategoryCache);
        NavigatorState.instance().setRecommendedRooms(originalRecommendedRooms);
        CatalogState.instance().setGiftSettings(originalGiftSettings);
        CatalogState.instance().setCatalogPages(originalCatalogPages);
        RecyclerState.instance().setSettings(originalRecyclerSettings);
        AppConfigState.instance().setSettingsCache(originalSettingsCache);
        AppPaths.setApplicationPath(originalApplicationPath.toString());
        });
    }

    @FunctionalInterface
    private interface SmokeSection {
        void run() throws Exception;
    }

    private static void run(SmokeSection section) throws Exception {
        section.run();
    }

    private static AppSettingsCache settings(String... keyValuePairs) {
        Map<String, String> settings = new HashMap<>();
        for (int index = 0; index + 1 < keyValuePairs.length; index += 2) {
            settings.put(keyValuePairs[index], keyValuePairs[index + 1]);
        }
        return AppSettingsCache.fromSettings(settings);
    }

    private static PermissionMatrix permissions(PermissionMatrix.PermissionPayload... payloads) {
        return PermissionMatrix.fromPayloadRows(List.of(payloads));
    }

    private static String wireLong(long value) {
        return encodedVl64(value, "");
    }

    private static String encodedVl64(Object value, Object ignored, String prefix) {
        return encodedVl64(WireEncoding.parseLeadingLong(value), prefix);
    }

    private static String encodedVl64(long value, String prefix) {
        return prefix + WireEncoding.encodeVl64(value);
    }

    private static void assertAppSettingsCache() {
        Map<String, String> settingRows = new LinkedHashMap<>();
        settingRows.put("server.port", "1234");
        settingRows.put("SERVER.PORT", "5678");
        settingRows.put("name", "alpha");
        AppSettingsCache parsedSettings = AppSettingsCache.fromSettings(settingRows);
        assertEquals("1234", parsedSettings.value("SERVER.PORT"));
        assertEquals("alpha", parsedSettings.value("name"));
        AppSettingsCache typedSettings = AppSettingsCache.fromSettings(Map.of("Server.Port", "4321"));
        assertEquals("4321", typedSettings.value("server.port"));
        UpdaterSettings.UpdateEntry entry = UpdaterSettings.UpdateEntry.fromFields("x", "42", "body", 3L, 7L);
        UpdaterSettings updaterSettings = UpdaterSettings.fromEntries("typed-updater", List.of(entry), "");
        assertEquals(List.of(entry), updaterSettings.entryList());
    }

    private static void assertPermissionMatrix() {
        assertEquals(true, PermissionMatrix.fromPayloadRows(List.of(
                new PermissionMatrix.PermissionPayload(1L, 2L, "\2typed_perm\2")))
            .allows(1, "", "typed_perm", 2));
        List<PermissionMatrix.PermissionPayload> permissionRows = new ArrayList<>(List.of(
            new PermissionMatrix.PermissionPayload(1L, 1L, "\2hc_perm\2")));
        PermissionMatrix matrix = PermissionMatrix.fromPayloadRows(permissionRows);
        permissionRows.set(0, new PermissionMatrix.PermissionPayload(1L, 1L, ""));
        assertEquals(true, matrix.allows(1, "", "hc_perm", 1));
    }

    private static void assertPetSettingsTypedAccessors(PetSettings settings, PetSettings.PetCommandRow command) {
        assertEquals(List.of(new PetSettings.PetLevelRow(2L, 20L, 30L, 40L, 3)), settings.levels());
        assertEquals(List.of(command), settings.commands());
    }

    private static void assertChatSettingsTypedAccessors(ChatSettings settings) {
        assertEquals(List.of(new ChatSettings.FilterWord("badword")), settings.filterWords());
        assertEquals(List.of(new ChatSettings.Gesture(":-)", 5L)), settings.gestures());
        ChatSettings typedSettings = ChatSettings.fromRows(
            List.of(new ChatSettings.FilterWord("cacheword")),
            List.of(new ChatSettings.Gesture(":cache", 6L)));
        assertEquals(List.of(new ChatSettings.FilterWord("cacheword")), typedSettings.filterWords());
        assertEquals(6L, typedSettings.gestureId("hello :cache", true));
    }

    private static void assertTypedGameServerSessionState(GameServerSessionState sessionState) {
        assertEquals(Set.of(12L), sessionState.readySocketIndexes());
        GameServerSessionState previousSessionState = SessionState.instance().gameServerSession();
        SessionState.instance().setGameServerSession(sessionState);
        assertEquals(Set.of(12L), SessionState.instance().gameServerSession().readySocketIndexes());
        SessionState.instance().setGameServerSession(previousSessionState);
    }

    private static void assertRoomPortalSettingsBootRows(RoomPortalSettings settings) {
        assertEquals(List.of(new RoomDao.WarpSpaceRow(0L, 0L, 0L, 0L, 0L, 0L, 0L)), settings.warpSpaces());
        assertEquals(List.of(new RoomDao.SpecialGateRow(0L, 0L)), settings.specialGates());
    }

    private static void assertRoomPortalSettingsTypedRows(RoomPortalSettings settings) {
        assertEquals(List.of(new RoomDao.WarpSpaceRow(12L, 1L, 2L, 34L, 5L, 6L, 1L)), settings.warpSpaces());
        assertEquals(List.of(new RoomDao.SpecialGateRow(12L, 1L)), settings.specialGates());
    }

    private static String expectedOfficialNavigatorRow(OfficialNavigatorItem item) {
        String expected = encodedVl64(item.typeId(), null, "")
            + encodedVl64(item.styleId(), null, "")
            + encodedVl64(item.iconId(), null, "");
        expected += item.caption() + '\2' + item.captionTwo() + '\2'
            + item.captionThree() + '\2' + item.unusedSlot() + '\2'
            + item.roomId() + '\2' + item.roomName() + '\2'
            + item.ownerName() + '\2' + item.doorStatus() + '\2'
            + item.visitorsNow() + '\2' + item.visitorsMax() + '\2'
            + item.description() + '\2' + item.hasTrading() + '\2'
            + item.unusedTradingSlot() + '\2' + item.roomRate() + '\2'
            + item.categoryId() + '\2' + item.roomIcon() + '\2'
            + item.tagOne() + '\2' + item.tagTwo() + '\2'
            + item.allowOtherPets() + '\2' + item.modelName() + '\2'
            + item.requiredFiles() + '\2' + item.modelVisitorsMax() + '\2';
        return expected
            + encodedVl64(item.parentId(), null, "")
            + encodedVl64(item.officialId(), null, "")
            + encodedVl64(item.requiredLevel(), null, "");
    }

    private static void assertCatalogProductCounterRows(CatalogProductSettings settings) {
        assertEquals(List.of(1L, 2L), settings.counterProducts());
        assertEquals(List.of(new PackageDao.PackageRow(10L, "i", 20L, "")), settings.packages());
        assertEquals(List.of(new PackageDao.PetPackageRow(7L, 8L, 9L, "ffeeaa")), settings.petPackages());
        assertEquals(List.of(new CatalogProductSettings.ClubProductSetting(33L, 2L, 1L, 3)),
            settings.clubProducts());
        assertEquals(true, settings.containsCounterProduct(2L));
        CatalogProductSettings typedCounterProducts = CatalogProductSettings.fromCounterProductIds(
            List.of(4L, 5L), 0L, 0L, List.of(), List.of(), List.of());
        assertEquals(List.of(4L, 5L), typedCounterProducts.counterProducts());
        CatalogProductSettings previousCounterProducts = CatalogState.instance().productSettings();
        seedCatalogCounterProductIds(List.of(6L, 7L));
        assertEquals(List.of(6L, 7L), CatalogState.instance().productSettings().counterProducts());
        CatalogState.instance().setProductSettings(previousCounterProducts);
    }

    private static void assertCatalogPagePayloadBuilder() {
        CatalogRegistry previousRegistry = CatalogState.instance().registry();
        seedCatalogRegistryProductRows(List.of(productDaoRow(77, "0", "9")));
        CatalogDao.CatalogPageRow page = new CatalogDao.CatalogPageRow(
            3L, "Page", 0L, 0L, 1L, "template", "header", "special", "specialTemplate",
            "text1", "", "text3", "", "", "", "", "", "", "", "", "link", 0L);
        CatalogDao.CatalogPageProductRow productRow = new CatalogDao.CatalogPageProductRow(
            51L, 77L, 12L, 3L, "sprite", 4L, 0L, "secondary", 1L, 2L);
        String expectedPagePayload = "Page\2"
            + encodedVl64(1, null, "")
            + "template\2header\2special\2specialTemplate\2"
            + encodedVl64(2, null, "")
            + "text1\2text3\2"
            + encodedVl64(1, null, "")
            + "link\2"
            + encodedVl64(1, null, "")
            + encodedVl64(51, null, "")
            + "sprite\2"
            + encodedVl64(77, null, "")
            + "i\2"
            + encodedVl64(12, null, "")
            + encodedVl64(3, null, "")
            + encodedVl64(4, null, "")
            + encodedVl64(1, null, "")
            + "secondary\2"
            + encodedVl64(1, null, "")
            + encodedVl64(2, null, "");
        assertEquals(expectedPagePayload, CatalogPageBootCache.buildCatalogPagePayload(page, List.of(productRow)));
        CatalogState.instance().setRegistry(previousRegistry);
    }

    private static void assertGiftSettingsTypedAccessors(GiftSettings giftSettings) {
        assertEquals(List.of(new GiftSettings.ClubGift(82L, 507L, 30L)), giftSettings.clubGifts());
        assertEquals(507L, giftSettings.clubGiftByCatalogProductId(82L).productId());
        assertEquals(List.of(501L, 502L), giftSettings.giftWrapProductIds());
        assertEquals(true, giftSettings.containsGiftWrapProduct(502L));
        assertEquals(false, giftSettings.containsGiftWrapProduct(50L));
        GiftSettings typedGiftSettings = GiftSettings.fromRows("TYPED",
            List.of(new GiftSettings.ClubGift(83L, 508L, 40L)),
            List.of(601L, 0L, 602L, 601L),
            "WRAPS");
        assertEquals("TYPED", typedGiftSettings.clubGiftPayload());
        assertEquals(List.of(new GiftSettings.ClubGift(83L, 508L, 40L)), typedGiftSettings.clubGifts());
        assertEquals(List.of(601L, 602L), typedGiftSettings.giftWrapProductIds());
        assertEquals(true, typedGiftSettings.containsGiftWrapProduct(602L));
        GiftSettings previousGiftSettings = CatalogState.instance().giftSettings();
        CatalogState.instance().setGiftSettings(typedGiftSettings);
        assertEquals("TYPED", CatalogState.instance().giftSettings().clubGiftPayload());
        assertEquals(508L, CatalogState.instance().giftSettings().clubGiftByCatalogProductId(83L).productId());
        CatalogState.instance().setGiftSettings(previousGiftSettings);
        CatalogState.instance().setGiftSettings(GiftSettings.fromRows(
            CatalogState.instance().giftSettings().clubGiftPayload(),
            CatalogState.instance().giftSettings().clubGifts(),
            List.of(701L, 0L, 702L),
            "TYPED-WRAPS"));
        assertEquals("TYPED-WRAPS", CatalogState.instance().giftSettings().giftWrapPayload());
        assertEquals(List.of(701L, 702L), CatalogState.instance().giftSettings().giftWrapProductIds());
    }

    private static void assertRecyclerCacheBuilders() {
        RecyclerBootCache.RecyclerCache recyclerCache = RecyclerBootCache.buildRecyclerCache(List.of(
            new RecyclerSettings.RewardGroup(80L, List.of(10L, 11L)),
            new RecyclerSettings.RewardGroup(20L, List.of(12L, 13L))));
        assertEquals(2L, recyclerCache.groupCount());
        assertEquals(80L, recyclerCache.chanceByGroupIndex().get(0L).longValue());
        assertEquals("10\2" + "11\2", recyclerCache.productListByGroupIndex().get(0L));
        assertEquals("12\2" + "13\2", recyclerCache.productListByGroupIndex().get(1L));
        assertEquals(List.of(10L, 11L), recyclerCache.rewardGroups().get(0).productIds());
        RecyclerSettings previousRecyclerSettings = RecyclerState.instance().settings();
        RecyclerState.instance().setSettings(RecyclerSettings.fromRewardGroups(
            "LEGACY-STATUS",
            List.of(new RecyclerSettings.RewardGroup(80L, List.of(10L, 11L))),
            55L));
        assertEquals("LEGACY-STATUS", RecyclerState.instance().settings().statusPayload());
        assertEquals(55L, RecyclerState.instance().settings().boxProductId());
        assertEquals(List.of(10L, 11L), RecyclerState.instance().settings().rewardGroups().get(0).productIds());
        RecyclerState.instance().setSettings(previousRecyclerSettings);
        assertEquals(
            encodedVl64(2, null, "")
                + encodedVl64(80, null, "")
                + encodedVl64(2, null, "")
                + encodedVl64(10, null, "")
                + encodedVl64(11, null, "")
                + encodedVl64(20, null, "")
                + encodedVl64(2, null, "")
                + encodedVl64(12, null, "")
                + encodedVl64(13, null, ""),
            recyclerCache.payload());
    }

    private static void assertRoomEventLocaleTypedBuilder() {
        RoomEventLocales locales = RoomEventBootCache.buildRoomEventLocales(
            List.of(new SettingsDao.LocaleRow("roomevent_type_5", "party")),
            RoomEventLocales.fromEntries(List.of(new RoomEventLocales.LocaleEntry("existing", List.of("old")))));
        assertEquals(true, locales.entries().contains(new RoomEventLocales.LocaleEntry("existing", List.of("old"))));
        assertEquals("party", locales.categoryName(5));
    }

    private static void assertRecommendedRoomsPayloadMapBridge() {
        RecommendedRooms previousRecommendedRooms = NavigatorState.instance().recommendedRooms();
        NavigatorState.instance().setRecommendedRooms(Map.of(0L, "RECOMMENDED_MAP"), 1L);
        assertEquals("RECOMMENDED_MAP", NavigatorState.instance().recommendedRooms().payload(1L));
        NavigatorState.instance().setRecommendedRooms(previousRecommendedRooms);
    }

    private static void assertRecommendedRoomsPayloadBuilders() {
        RoomDao.RecommendedRoomRow row = new RoomDao.RecommendedRoomRow(
            1, 2, 3, "c1", "c2", "c3", "c4", 10, "c6", "c7", 11, 12, 13,
            "c10", 14, "c12", 15, 16, "c15", "c16", "c17", 17, "c19", "c20",
            "c21", 4, 5);
        String expected = encodedVl64(1, null, "")
            + encodedVl64(1, null, "")
            + encodedVl64(2, null, "")
            + encodedVl64(3, null, "")
            + String.join("\2", List.of(
                "c1", "c2", "c3", "c4", "10", "c6", "c7", "11", "12", "13",
                "c10", "14", "c12", "15", "16", "c15", "c16", "c17", "17",
                "c19", "c20", "c21"))
            + "\2"
            + encodedVl64(4, null, "")
            + encodedVl64(5, null, "");
        assertEquals(expected, NavigatorBootCache.buildRecommendedRoomsPayload(List.of(row)));
    }

    private static void assertHelpCenterPayloadMaps() {
        assertEquals(true, HelpCenterState.instance().cache().categoryFaqPayloads().values().stream()
            .anyMatch(value -> StringUtils.text(value).contains("faq")));
        assertEquals(true, HelpCenterState.instance().cache().descriptionPayloads().values().stream()
            .anyMatch(value -> StringUtils.text(value).contains("line1\rline2")));
    }

    private static void assertCatalogPagePayloadMapBridge() {
        CatalogPages previousCatalogPages = CatalogState.instance().catalogPages();
        CatalogState.instance().setCatalogPages(CatalogPages.fromPayloadMaps(
            Map.of(8L, "CATALOG_PAGE_MAP"),
            Map.of(new CatalogPages.PageTreeKey(0L, 0L), "TREE")));
        assertEquals("CATALOG_PAGE_MAP", CatalogState.instance().catalogPages().pagePayload(8L));
        assertEquals("TREE", CatalogState.instance().catalogPages().defaultPageTree());
        CatalogState.instance().setCatalogPages(previousCatalogPages);
    }

    private static void assertCatalogPagesTypedAccessors() {
        Map<CatalogPages.PageTreeKey, String> pageTrees =
            new HashMap<>(Map.of(new CatalogPages.PageTreeKey(0L, 0L), "TREE0"));
        CatalogPages typedCatalogPages = CatalogPages.fromPayloadMaps(Map.of(4L, "PAGE4"), pageTrees);
        pageTrees.put(new CatalogPages.PageTreeKey(0L, 0L), "changed");
        assertEquals("PAGE4", typedCatalogPages.pagePayload(4L));
        assertEquals("TREE0", typedCatalogPages.defaultPageTree());
        assertEquals(Map.of(4L, "PAGE4"), typedCatalogPages.pagePayloads());
        CatalogPages mapCatalogPages = CatalogPages.fromPayloadMaps(
            Map.of(5L, "PAGE5"), Map.of(new CatalogPages.PageTreeKey(1L, 2L), "TREE12"));
        assertEquals("PAGE5", mapCatalogPages.pagePayload(5L));
        assertEquals("TREE12", mapCatalogPages.pageTree(1L, 2L));
        CatalogPages cacheCatalogPages = CatalogPages.fromPayloadMaps(
            Map.of(6L, "PAGE6"), Map.of(new CatalogPages.PageTreeKey(2L, 3L), "TREE23"));
        assertEquals("PAGE6", cacheCatalogPages.pagePayload(6L));
        assertEquals("TREE23", cacheCatalogPages.pageTree(2L, 3L));
        CatalogPages previousCatalogPages = CatalogState.instance().catalogPages();
        CatalogState.instance().setCatalogPages(typedCatalogPages);
        assertEquals("PAGE4", CatalogState.instance().catalogPages().pagePayload(4L));
        CatalogState.instance().setCatalogPages(previousCatalogPages);
    }

    private static void assertStaffSettingsTypedAccessors() {
        List<StaffSettings.ModerationPayload> payloads = new ArrayList<>(List.of(
            new StaffSettings.ModerationPayload(0L, 0L, "ZERO"),
            new StaffSettings.ModerationPayload(1L, 0L, "STAFF"),
            new StaffSettings.ModerationPayload(1L, 1L, "HC")));
        StaffSettings settings = StaffSettings.fromPayloadRows(payloads);
        payloads.set(2, new StaffSettings.ModerationPayload(1L, 1L, "changed"));
        assertEquals("HC", settings.moderationPayload(1L, 1L));
        assertEquals(new StaffSettings.ModerationPayload(1L, 1L, "HC"),
            settings.moderationPayloadRows().get(2));
        List<StaffSettings.ModerationPayload> copiedPayloads = settings.moderationPayloadRows();
        assertEquals(3, copiedPayloads.size());
        assertEquals("HC", settings.moderationPayload(1L, 1L));
        StaffSettings cacheSettings = StaffSettings.fromPayloadRows(List.of(
            new StaffSettings.ModerationPayload(2L, 0L, "CACHE_STAFF")));
        assertEquals("CACHE_STAFF", cacheSettings.moderationPayload(2L, 0L));
        StaffSettings previousStaffSettings = ModerationState.instance().staffSettings();
        ModerationState.instance().setStaffSettings(settings);
        assertEquals("HC", ModerationState.instance().staffSettings().moderationPayload(1L, 1L));

        AppSettingsBootCache.loadPermissionMatrixCache();
        StaffModerationBootCache.loadStaffModerationCache();
        StaffSettings.ModerationPayload bootPayload = ModerationState.instance().staffSettings().moderationPayloadRows().get(3);
        assertEquals(1L, bootPayload.rankIndex());
        assertEquals(0L, bootPayload.hcLevel());
        assertEquals(bootPayload.payload(), ModerationState.instance().staffSettings().moderationPayload(1L, 0L));
        ModerationState.instance().setStaffSettings(previousStaffSettings);
    }

    private static void assertMessengerSettingsTypedAccessors() {
        long[] friendLimits = new long[]{10L, 0L, 20L, 0L, 30L};
        MessengerSettings settings = MessengerSettings.fromLimits(friendLimits);
        friendLimits[4] = 0L;
        assertEquals(30L, settings.maxFriends(4));
        assertEquals(List.of(10L, 0L, 20L, 0L, 30L), settings.friendLimitList());
    }

    private static void assertProductCacheRows(ProductCache productCache) {
        assertEquals(1, productCache.rows().size());
        ProductCache.ProductRow row = productCache.rows().get(0);
        assertEquals(12L, row.productId());
        assertEquals(List.of("7", "", "", "", "typed", "fallbackTyped"), row.fields());
    }

    private static void assertCatalogRegistryRows() {
        CatalogRegistry registry = CatalogRegistry.fromRows(
            List.of(new CatalogDao.ProductCacheRow(List.of("21", "3", "chair"))),
            List.of(new CatalogDao.CatalogProductCacheRow(List.of("31", "sprite", "21"))),
            List.of(new CatalogDao.ProductDealRow(41L, "21;22")));
        assertEquals(List.of(new CatalogRegistry.CatalogRow("21\t3\tchair", List.of("21", "3", "chair"))),
            registry.productRows());
        assertEquals("sprite", registry.catalogProduct(31).orElseThrow().sprite());
        assertEquals("41\t21;22", registry.dealRows().get(0).text());
        assertEquals(List.of(21L, 22L), registry.dealRows().get(0).productDealItemIds());
        assertEquals(List.of(21L, 22L), registry.productDeal(41L).orElseThrow().itemProductIds());
        List<String> copiedFields = registry.productRows().get(0).fields();
        assertEquals("chair", copiedFields.get(2));
        CatalogRegistry mapRegistry = CatalogRegistry.fromRowMaps(
            Map.of(22L, CatalogRegistry.CatalogRow.fromFields(List.of("22", "4", "table"))),
            Map.of(32L, CatalogRegistry.CatalogRow.fromFields(List.of("32", "table_sprite", "22"))),
            Map.of(42L, new CatalogRegistry.CatalogRow("42\t22;23", List.of("42", "22;23"))));
        assertEquals("table", mapRegistry.productRows().get(0).fields().get(2));
        assertEquals("table_sprite", mapRegistry.catalogProduct(32).orElseThrow().sprite());
        assertEquals("42\t22;23", mapRegistry.dealRows().get(0).text());
        assertEquals(List.of(22L, 23L), mapRegistry.productDeal(42L).orElseThrow().itemProductIds());
    }

    private static void assertRoomCategoryBootCaches() {
        NavigatorBootCache.loadRoomCategoryRowsCache();
        assertEquals(List.of("0", "", "0"), NavigatorState.instance().roomCategoryCache().defaultCategoryIdList());
        assertEquals(true, NavigatorState.instance().roomCategoryCache().categoryRowList().size() > 0);
        assertEquals(encodedVl64(1, null, "")
                + encodedVl64(1, null, "") + "public\2"
                + encodedVl64(0, null, ""),
            NavigatorBootCache.buildRoomCategoryPayload(
                List.of(new RoomDao.RoomCategoryRow(1L, "public", 0L, 0L, 0L)), 0L, 0L));

        RoomCategoryCache typedRoomCategories = RoomCategoryCache.fromPayloadRows(List.of("defaults"),
            List.of(new RoomDao.RoomCategoryRow(5L, "typed", 1L, 2L, 3L)),
            List.of(new RoomCategoryCache.CategoryPayload(0L, 0L, "CATEGORY")));
        assertEquals("defaults", typedRoomCategories.privateDefaultCategoryId());
        assertEquals("", typedRoomCategories.publicDefaultCategoryId());
        assertEquals(List.of(new RoomDao.RoomCategoryRow(5L, "typed", 1L, 2L, 3L)),
            typedRoomCategories.categoryRowList());
        assertEquals("CATEGORY", typedRoomCategories.payload(0L, 0L));
        assertEquals(new RoomCategoryCache.CategoryPayload(0L, 0L, "CATEGORY"),
            typedRoomCategories.payloadRows().get(0));
        RoomCategoryCache cacheRoomCategories = RoomCategoryCache.fromPayloadRows(
            List.of("11", "", "22"),
            List.of(new RoomDao.RoomCategoryRow(7L, "cache", 3L, 4L, 5L)),
            List.of(new RoomCategoryCache.CategoryPayload(2L, 1L, "CACHE_CATEGORY")));
        assertEquals(List.of("11", "", "22"), cacheRoomCategories.defaultCategoryIdList());
        assertEquals(List.of(new RoomDao.RoomCategoryRow(7L, "cache", 3L, 4L, 5L)),
            cacheRoomCategories.categoryRowList());
        assertEquals("CACHE_CATEGORY", cacheRoomCategories.payload(2L, 1L));

        RoomCategoryCache previousRoomCategoryCacheForRows = NavigatorState.instance().roomCategoryCache();
        NavigatorState.instance().setRoomCategoryCache(RoomCategoryCache.fromPayloadRows(
            List.of("defaults"),
            List.of(new RoomDao.RoomCategoryRow(6L, "legacy", 0L, 1L, 2L)),
            List.of()));
        RoomCategoryCache mirroredRoomCategories = NavigatorState.instance().roomCategoryCache();
        assertEquals(List.of(new RoomDao.RoomCategoryRow(6L, "legacy", 0L, 1L, 2L)),
            mirroredRoomCategories.categoryRowList());
        NavigatorState.instance().setRoomCategoryCache(previousRoomCategoryCacheForRows);

        List<RoomCategoryCache.CategoryPayload> payloadRows =
            new ArrayList<>(List.of(new RoomCategoryCache.CategoryPayload(0L, 0L, "PAYLOAD")));
        List<String> defaultIds = new ArrayList<>(List.of("11", "", "22"));
        RoomCategoryCache typedRoomCategoryDefaults = RoomCategoryCache.fromPayloadRows(
            defaultIds, List.of(), payloadRows);
        defaultIds.set(0, "changed");
        payloadRows.set(0, new RoomCategoryCache.CategoryPayload(0L, 0L, "changed"));
        assertEquals("11", typedRoomCategoryDefaults.privateDefaultCategoryId());
        assertEquals("22", typedRoomCategoryDefaults.publicDefaultCategoryId());
        assertRoomCategoryDefaults(typedRoomCategoryDefaults);

        RoomCategoryCache previousRoomCategoryDefaults = NavigatorState.instance().roomCategoryCache();
        NavigatorState.instance().setRoomCategoryCache(typedRoomCategoryDefaults);
        assertEquals("PAYLOAD", NavigatorState.instance().roomCategoryCache().payload(0L, 0L));
        NavigatorState.instance().setRoomCategoryCache(previousRoomCategoryDefaults);

        NavigatorBootCache.loadRoomCategoryPayloadCache();
        RoomCategoryCache.CategoryPayload payload = NavigatorState.instance().roomCategoryCache().payloadRows().get(0);
        assertEquals(true, payload.payload().contains("public"));
        assertEquals(true, NavigatorState.instance().roomCategoryCache().payload(0L, 0L).contains("public"));
    }

    private static void assertRoomCategoryDefaults(RoomCategoryCache cache) {
        assertEquals(List.of("11", "", "22"), cache.defaultCategoryIdList());
        assertEquals("PAYLOAD", cache.payload(0L, 0L));
        assertEquals(List.of(new RoomCategoryCache.CategoryPayload(0L, 0L, "PAYLOAD")), cache.payloadRows());
    }

    private static void assertAchievementRows(
        AchievementSettings settings,
        List<AchievementSettings.IndexedAchievement> indexedAchievements
    ) {
        assertEquals(indexedAchievements, settings.indexedAchievements());
    }

    private static void assertGuardianSocketMarkers() {
        Guardian.clearSocketMarkers();
        Guardian.setGameServerConnected(false);
        Guardian.setSocketConnected(8, true);
        assertEquals(true, Guardian.isSocketConnected(8));
        assertEquals(true, Guardian.isSocketConnected(8));
        Guardian.setSocketConnected(8, false);
        assertEquals(false, Guardian.isSocketConnected(8));
        assertEquals(false, Guardian.isSocketConnected(8));
        Guardian.setGameServerConnected(true);
        assertEquals(true, Guardian.isSocketConnected(0));
        Guardian.setGameServerConnected(false);
        SocketMarkerSet typedSocketMarkers = SocketMarkerSet.fromSocketIndexes(List.of(1L, 12L, 0L));
        assertEquals(Set.of(1L, 12L), typedSocketMarkers.socketIndexes());
        SocketMarkerSet previousLicenceMarkers = SessionState.instance().socketMarkers();
        SessionState.instance().setSocketMarkers(typedSocketMarkers);
        assertEquals(Set.of(1L, 12L), SessionState.instance().socketMarkers().socketIndexes());
        SessionState.instance().setSocketMarkers(previousLicenceMarkers);
        Guardian.SocketMarkerState addedMarkerState =
            Guardian.toggleSocketMarkerState(SocketMarkerSet.fromSocketIndexes(List.of(1L)), 1, 12);
        assertEquals(Set.of(1L, 12L), addedMarkerState.markers().socketIndexes());
        assertEquals(12L, addedMarkerState.highestIndex());
        assertEquals(true, addedMarkerState.accepted());
        assertEquals(true, addedMarkerState.added());
        Guardian.SocketMarkerState removedMarkerState =
            Guardian.toggleSocketMarkerState(SocketMarkerSet.fromSocketIndexes(List.of(1L, 12L)), 12, 12);
        assertEquals(Set.of(1L), removedMarkerState.markers().socketIndexes());
        assertEquals(12L, removedMarkerState.highestIndex());
        assertEquals(true, removedMarkerState.accepted());
        assertEquals(false, removedMarkerState.added());
        Guardian.SocketMarkerState rejectedMarkerState =
            Guardian.toggleSocketMarkerState(SocketMarkerSet.fromSocketIndexes(List.of(1L)), 1, 2500);
        assertEquals(Set.of(1L), rejectedMarkerState.markers().socketIndexes());
        assertEquals(1L, rejectedMarkerState.highestIndex());
        assertEquals(false, rejectedMarkerState.accepted());
        Guardian.setSocketMarkers(SocketMarkerSet.fromSocketIndexes(List.of(1L)));
        Guardian.addSocketMarker(12);
        assertEquals(Set.of(1L, 12L), Guardian.socketMarkers().socketIndexes());
        Guardian.clearSocketMarkers();
        Guardian.toggleSocketMarker(12);
        assertEquals(Set.of(12L), Guardian.socketMarkers().socketIndexes());
        assertEquals(12L, Guardian.highestMarkedSocketIndex());
        Guardian.toggleSocketMarker(12);
        assertEquals(Set.of(), Guardian.socketMarkers().socketIndexes());
        assertEquals(12L, Guardian.highestMarkedSocketIndex());
    }

    private static RepresentedBotRegistry representedBots(Map<Long, String> recordsByEntityId) {
        return representedBots(Set.of(), recordsByEntityId);
    }

    private static RepresentedBotRegistry representedBots(Set<Long> allocatedEntityIds, Map<Long, String> recordsByEntityId) {
        Map<Long, RepresentedBotRegistry.RepresentedBotRecord> records = new LinkedHashMap<>();
        for (Map.Entry<Long, String> record : recordsByEntityId.entrySet()) {
            records.put(record.getKey(), representedBotRecord(record.getValue()));
        }
        return RepresentedBotRegistry.fromState(allocatedEntityIds, records);
    }

    private static RepresentedBotRegistry.RepresentedBotRecord representedBotRecord(String recordText) {
        String[] fields = StringUtils.text(recordText).split("\2", -1);
        return new RepresentedBotRegistry.RepresentedBotRecord(
            numberField(fields, 0),
            numberField(fields, 1),
            textField(fields, 2),
            textField(fields, 3),
            textField(fields, 4),
            textField(fields, 5),
            numberField(fields, 6),
            numberField(fields, 7),
            textField(fields, 8),
            numberField(fields, 9),
            textField(fields, 10),
            numberField(fields, 11),
            numberField(fields, 12),
            textField(fields, 13),
            textField(fields, 14),
            numberField(fields, 15),
            numberField(fields, 16),
            List.of(fields));
    }

    private static String textField(String[] fields, int index) {
        return StringUtils.field(fields, index);
    }

    private static long numberField(String[] fields, int index) {
        return NumberUtils.parseLong(textField(fields, index));
    }

    private static SessionRegistry sessionRegistry(SessionRegistry.SessionRecord... records) {
        return SessionRegistry.fromEntries(List.of(records), List.of());
    }

    private static SessionRegistry linkedSessionRegistry(String recordId, String text) {
        return linkedSessionRegistry(new SessionRegistry.LinkedSessionSection(recordId, text));
    }

    private static SessionRegistry linkedSessionRegistry(SessionRegistry.LinkedSessionSection... sections) {
        return SessionRegistry.fromEntries(List.of(), List.of(sections));
    }

    private static ProductCache.ProductRow productCacheRow(long productId, String... columnPairs) {
        return new ProductCache.ProductRow(productId, productFields(productId, columnPairs));
    }

    private static CatalogDao.ProductCacheRow productDaoRow(long productId, String... columnPairs) {
        return new CatalogDao.ProductCacheRow(indexedFields(productId, columnPairs));
    }

    private static CatalogDao.CatalogProductCacheRow catalogProductRow(long productId, String... columnPairs) {
        return new CatalogDao.CatalogProductCacheRow(indexedFields(0, productId, columnPairs));
    }

    private static void seedCatalogRegistryProductRows(List<CatalogDao.ProductCacheRow> productRows) {
        CatalogRegistry current = CatalogState.instance().registry();
        CatalogState.instance().setRegistry(CatalogRegistry.fromRowMaps(
            CatalogRegistry.fromRows(productRows, List.of(), List.of()).productRowMap(),
            current.catalogProductRowMap(),
            current.dealRowMap()));
    }

    private static void seedCatalogRegistryCatalogProductRows(
        List<CatalogDao.CatalogProductCacheRow> catalogProductRows
    ) {
        CatalogRegistry current = CatalogState.instance().registry();
        CatalogState.instance().setRegistry(CatalogRegistry.fromRowMaps(
            current.productRowMap(),
            CatalogRegistry.fromRows(List.of(), catalogProductRows, List.of()).catalogProductRowMap(),
            current.dealRowMap()));
    }

    private static void seedCatalogRegistryDealRows(List<CatalogDao.ProductDealRow> dealRows) {
        CatalogRegistry current = CatalogState.instance().registry();
        CatalogState.instance().setRegistry(CatalogRegistry.fromRowMaps(
            current.productRowMap(),
            current.catalogProductRowMap(),
            CatalogRegistry.fromRows(List.of(), List.of(), dealRows).dealRowMap()));
    }

    private static void seedCatalogPagePayloads(Map<Long, String> pagePayloads) {
        CatalogPages current = CatalogState.instance().catalogPages();
        CatalogState.instance().setCatalogPages(CatalogPages.fromPayloadMaps(pagePayloads, current.pageTrees()));
    }

    private static void seedCatalogPageTrees(Map<CatalogPages.PageTreeKey, String> pageTrees) {
        CatalogPages current = CatalogState.instance().catalogPages();
        CatalogState.instance().setCatalogPages(CatalogPages.fromPayloadMaps(current.pagePayloads(), pageTrees));
    }

    private static void seedCatalogCounterProductIds(List<Long> counterProductIds) {
        CatalogProductSettings current = CatalogState.instance().productSettings();
        CatalogState.instance().setProductSettings(CatalogProductSettings.fromSettings(
            counterProductIds,
            current.teleportProductId(),
            current.moodlightProductId(),
            current.packages(),
            current.petPackages(),
            current.clubProducts()));
    }

    private static void seedCatalogPackageRows(List<PackageDao.PackageRow> packageRows) {
        CatalogProductSettings current = CatalogState.instance().productSettings();
        CatalogState.instance().setProductSettings(CatalogProductSettings.fromSettings(
            current.counterProducts(),
            current.teleportProductId(),
            current.moodlightProductId(),
            packageRows,
            current.petPackages(),
            current.clubProducts()));
    }

    private static void seedCatalogPetPackageRows(List<PackageDao.PetPackageRow> petPackageRows) {
        CatalogProductSettings current = CatalogState.instance().productSettings();
        CatalogState.instance().setProductSettings(CatalogProductSettings.fromSettings(
            current.counterProducts(),
            current.teleportProductId(),
            current.moodlightProductId(),
            current.packages(),
            petPackageRows,
            current.clubProducts()));
    }

    private static void seedCatalogClubProductRows(List<ClubDao.ContainedClubProductRow> clubProductRows) {
        CatalogProductSettings current = CatalogState.instance().productSettings();
        CatalogState.instance().setProductSettings(CatalogProductSettings.fromRows(
            current.counterProducts(),
            current.teleportProductId(),
            current.moodlightProductId(),
            current.packages(),
            current.petPackages(),
            clubProductRows));
    }

    private static List<String> productFields(long productId, String... columnPairs) {
        int maxColumn = 0;
        for (int index = 0; index + 1 < columnPairs.length; index += 2) {
            maxColumn = Math.max(maxColumn, (int) Long.parseLong(columnPairs[index]));
        }
        String[] fields = new String[maxColumn + 1];
        Arrays.fill(fields, "");
        fields[0] = String.valueOf(productId);
        for (int index = 0; index + 1 < columnPairs.length; index += 2) {
            fields[(int) Long.parseLong(columnPairs[index])] = columnPairs[index + 1];
        }
        return List.of(fields);
    }

    private static List<String> indexedFields(long productId, String... columnPairs) {
        return indexedFields(1, productId, columnPairs);
    }

    private static List<String> indexedFields(int columnOffset, long productId, String... columnPairs) {
        int maxColumn = 0;
        for (int index = 0; index + 1 < columnPairs.length; index += 2) {
            maxColumn = Math.max(maxColumn, (int) Long.parseLong(columnPairs[index]));
        }
        String[] fields = new String[maxColumn + columnOffset + 1];
        Arrays.fill(fields, "");
        fields[0] = String.valueOf(productId);
        for (int index = 0; index + 1 < columnPairs.length; index += 2) {
            fields[(int) Long.parseLong(columnPairs[index]) + columnOffset] = columnPairs[index + 1];
        }
        return List.of(fields);
    }

    private static List<Object> loginUserRow() {
        Object[] fields = new Object[48];
        Arrays.fill(fields, 0);
        fields[0] = 77;
        fields[1] = "Caller";
        fields[2] = 2;
        fields[3] = "hd-180-1";
        fields[4] = "Motto";
        fields[5] = "M";
        fields[6] = 11;
        fields[7] = 123;
        fields[12] = 0;
        fields[14] = 9;
        fields[26] = 1;
        fields[35] = 22;
        fields[36] = 0;
        fields[39] = 33;
        fields[41] = 1;
        fields[45] = 44;
        fields[46] = 55;
        return Arrays.asList(fields);
    }

    private static String wireString(String value) {
        String text = value == null ? "" : value;
        long length = text.length();
        return Character.toString((char) (64L + (length / 64L)))
            + Character.toString((char) (64L + (length % 64L)))
            + text;
    }

    private static boolean containsSql(List<String> statements, String needle) {
        for (String statement : statements) {
            if (statement.contains(needle)) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsSend(List<String> sends, String needle) {
        for (String send : sends) {
            if (send.contains(needle)) {
                return true;
            }
        }
        return false;
    }

    private static String sentPayload(String send) {
        String[] parts = StringUtils.text(send).split("\6", -1);
        if (parts.length < 3) {
            return "";
        }
        return parts[2].replace("\1\7", "");
    }

    private static String wiredRecordText(
        long wiredCode,
        long furnitureId,
        String selectedIdsText,
        String parameterValues,
        String textValue,
        String extraValue
    ) {
        String recordText = "\1" + wiredCode + '\2' + furnitureId + '\3' + StringUtils.text(selectedIdsText)
            + '\4' + StringUtils.text(parameterValues) + '\5' + StringUtils.left(textValue, 125) + '\6';
        if (!StringUtils.text(extraValue).isEmpty()) {
            recordText += StringUtils.text(extraValue);
        }
        return recordText;
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("expected <" + expected + "> but got <" + actual + ">");
        }
    }
}
