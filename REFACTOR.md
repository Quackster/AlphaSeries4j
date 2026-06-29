# AlphaSeries4j Refactor Progress

Last updated: 2026-06-30

## Goal

Refactor the VB6-port-shaped code into Java packages with stable domain APIs, typed database access, prepared statements, fluent packet builders, and smaller classes while preserving source compatibility and runtime behavior until each compatibility layer can be removed safely.
Keep common string/number helpers in shared utility classes, and move raw `Licence` globals toward typed collections in the relevant `game.*` package.

## Refactor Rules

- Preserve current source compatibility and runtime behavior for every slice; compatibility shims may stay temporarily, but each slice must move the implementation toward typed Java boundaries.
- Keep tests working throughout the refactor. Run `./gradlew test --no-daemon` before committing behavior-affecting slices, and do not mark a milestone complete unless the suite passes or the failure is explicitly documented.
- Use prepared DAO methods for database access. Handlers and services should not concatenate SQL strings or call raw `MySQL.Proc_5_*` helpers once a DAO boundary exists.
- Load database rows into typed classes or records with named fields. Do not map result sets into tab-delimited strings such as `getString(1) + "\t" + getString(2)` except at a deliberate legacy compatibility boundary that is documented and scheduled for removal.
- Keep row parsing, wire parsing, and payload construction separate. Payloads should use fluent `PacketBuilder`/payload classes instead of ad hoc string concatenation where the surrounding protocol support exists.
- Split huge root classes by domain, following the existing package direction: `dao.mysql` for persistence, `game.*` for domain state and collections, `messages.*` for packet payloads, `server.*` for runtime/server concerns, and `util` for shared string/number helpers.
- Do not duplicate common conversion helpers. Use `StringUtils`, `NumberUtils`, and protocol utilities instead of reintroducing VB-style local helper methods.
- Move raw `Licence.global_*` string caches into typed collection-backed state holders under the appropriate `game.*` package, keeping legacy serialization only at explicit compatibility boundaries.
- Each domain module should expose a single module-level manager/registry for its live instances and cached state instead of relying on `Licence.java` globals or scattered static ownership. `Licence` accessors are only temporary compatibility bridges while callers migrate to those managers.
- Commit only verified milestones with `REFACTOR.md` metrics updated when the legacy surface changes.

## Completed Slices

- Added `com.alphaseries.config` and moved database config into `AppDatabaseConfig`.
- Added `com.alphaseries.config.AppSettingsCache` and `PermissionMatrix` as typed adapters around settings and rank/HC permission caches previously parsed inline in `Functions`.
- Added `com.alphaseries.db` with `Database`, `JdbcDatabase`, and `RowMapper`.
- Switched `JdbcDatabase` to `PreparedStatement` for query and execute paths.
- Added typed prepared-query defaults for legacy `Database` test doubles while preserving JDBC prepared statements.
- Added `com.alphaseries.dao.mysql.UserDao` and migrated email-validation and identity-refresh user queries in `Functions` to typed DAO methods.
- Added `com.alphaseries.dao.mysql.RoomDao` and migrated position-availability, room-slot, and bot-room lookups in `Functions` to typed DAO methods.
- Added `com.alphaseries.protocol` with `WireEncoding`, `PacketBuilder`, `PacketReader`, and `OutgoingPayload`.
- Moved VL64/base64 length encoding and leading numeric wire parsing out of `Crypto`.
- Removed `Crypto`'s dependency on `com.alphaseries.vb.Vb`.
- Added incoming-message registry types in `com.alphaseries.messages.incoming`.
- Replaced the small ready-packet switch in `Filesystems` with `ReadyPacketRegistry`.
- Added `com.alphaseries.messages.outgoing.UserPayloads` for user-state outgoing payloads.
- Added `com.alphaseries.messages.outgoing.SocialPayloads` for room-user profile, badge, and tag outgoing payloads.
- Added `com.alphaseries.messages.outgoing.PollPayloads` for poll outgoing payloads.
- Added `com.alphaseries.messages.outgoing.AchievementPayloads` for achievement reward, award, and list outgoing payloads.
- Added `com.alphaseries.messages.outgoing.RecyclerPayloads` for recycler status outgoing payloads.
- Added `com.alphaseries.messages.outgoing.JukeboxPayloads` for song info, jukebox playlist, disk inventory, and playback outgoing payloads.
- Added `com.alphaseries.game.achievement.AchievementSettings` as a typed adapter around achievement quest and row state previously decoded directly from `Licence` globals.
- Added `com.alphaseries.game.advertising.VisitRoomAds` as a typed adapter around advertisement visit-room payload state previously read directly from `Licence` globals.
- Added `com.alphaseries.game.chat.ChatSettings` for chat word filtering and gesture lookup state previously read directly from `Licence` globals.
- Added `com.alphaseries.game.help.HelpCenterCache` as a typed adapter around important FAQ, FAQ category, and FAQ description payload caches previously read directly from `Licence` globals.
- Added `com.alphaseries.game.inventory.InventoryMessagePayloads` for inventory item/list payload building.
- Added `com.alphaseries.game.catalog.CatalogPages` as a typed adapter around catalog page payload and page-tree caches previously read directly from `Licence` globals.
- Added `com.alphaseries.game.catalog.CatalogProductSettings` as a typed adapter around catalog package, pet package, club product, counter product, teleport, and moodlight state previously written through raw `Licence` globals.
- Added `com.alphaseries.game.catalog.CatalogRegistry` as a typed adapter around product, catalog-product, and deal row caches previously parsed directly from `Licence` globals.
- Added `com.alphaseries.game.catalog.ProductCache` as a typed adapter around DataManager product rows previously read from raw global cache storage.
- Added `com.alphaseries.game.catalog.GiftSettings` as a typed adapter around club-gift and gift-wrap state previously read directly from `Licence` globals.
- Added `com.alphaseries.game.moderation.StaffPayloads` for call-for-help rows, staff user summaries, room visits, room chat history, and unsafe staff-alert checks.
- Added `com.alphaseries.game.moderation.StaffSettings` as a typed adapter around staff moderation payloads previously read directly from `Licence` globals.
- Added `com.alphaseries.game.messenger.MessengerSettings` as a typed adapter around messenger friend-limit state previously decoded directly from `Licence` globals.
- Added `com.alphaseries.game.navigator.NewFriendRooms` as a typed adapter around the cached new-friend room rows and expiry previously managed directly through `Licence` globals.
- Added `com.alphaseries.game.navigator.RecommendedRooms` as a typed adapter around the recommended-room payload cache previously read through `Licence` globals.
- Added `com.alphaseries.game.navigator.RoomCategoryCache` as a typed adapter around room-category defaults, raw rows, and rank/HC payloads previously read through `Licence` globals.
- Added `com.alphaseries.game.pet.PetPayloads` for pet race, inventory, name-validation, command, status, scratch, and action outgoing payloads.
- Added `com.alphaseries.game.pet.PetSettings` as a typed adapter around pet race, level, and command state previously read and written through raw `Licence` globals.
- Added `com.alphaseries.game.pet.RepresentedBotRegistry` as a typed adapter around represented bot allocation markers and bot record caches previously manipulated as raw `Licence` strings.
- Added `com.alphaseries.game.quest.QuestSettings` as a typed adapter around cached quest rows previously read directly from `Licence` globals.
- Added `com.alphaseries.game.recycler.RecyclerSettings` as a typed adapter around recycler status payload, reward groups, and ecotron box product state previously walked through `Licence` globals.
- Added `com.alphaseries.game.room.FurnitureRoomCache` for pending room, pending furniture, and represented furniture state cache mutations previously implemented inline in `Handling`.
- Added `Licence.furnitureRoomCache()`/`setFurnitureRoomCache(...)` as the compatibility boundary for pending room, pending furniture, and represented room cache state.
- Routed room look/walk movement handlers through `RepresentedRoomCache` instead of writing the represented room cache string directly.
- Routed remaining represented room cache reads and pending furniture marker cleanup in `Handling` through `RepresentedRoomCache`/`FurnitureRoomCache` boundaries.
- Added `com.alphaseries.game.room.RoomPortalSettings` as a typed adapter around room warp-space and special-gate caches previously written directly through raw `Licence` globals.
- Added `com.alphaseries.game.room.RepresentedRoomCache` for represented room record lookup, replacement, and occupant movement state previously implemented inline in `Handling` and `Main`.
- Added `com.alphaseries.game.room.RepresentedRoomSlots` as a typed adapter around represented room slot allocation markers previously manipulated as a raw `Licence` string.
- Migrated `RepresentedRoomSlots` internals from raw marker-string mutation to collection-backed available slot storage, keeping legacy serialization only at the compatibility boundary.
- Added `com.alphaseries.game.room.RoomEventLocales` as a typed adapter around DataManager room-event locale cache lookups, and routed `Boot` cache refreshes through named DataManager setters.
- Added `com.alphaseries.game.session.GameServerSessionState` as a typed adapter around queued game-server packet data and ready-session markers previously manipulated as raw `Licence` strings.
- Migrated `GameServerSessionState` internals from raw string mutation to collection-backed queued packets and ready socket marker storage, keeping legacy serialization only at the compatibility boundary.
- Added `com.alphaseries.game.session.RepresentedSocketCache` as a typed adapter around represented socket records, room slots, and busy checks previously parsed directly from a raw `Licence` global.
- Added `com.alphaseries.game.session.SessionRegistry` as a typed adapter around the legacy `Licence.global_00829268` session cache.
- Added typed `SessionRegistry.SocketSession` iteration and migrated staff broadcast away from direct session-cache string parsing.
- Added `com.alphaseries.game.session.SocketMarkerSet` as a set-backed adapter for `Licence` socket marker state previously updated through raw string replacement.
- Routed Guardian socket marker toggling, removal, and ping iteration through `SocketMarkerSet`/named Guardian methods instead of raw bracket-string edits in callers, and migrated `Guardian` off `Vb` helpers.
- Added `com.alphaseries.game.wired.WiredSettings` as a typed adapter around wired state payload previously read directly from `Licence` globals.
- Added `com.alphaseries.game.wired.WiredPayloads` for wired record formatting, cache replacement, selected-item checks, and state payload aggregation.
- Added `com.alphaseries.messages.outgoing.MessengerPayloads` for friend, request, search, pending-request, and friend-list outgoing payloads.
- Added `com.alphaseries.server.lifecycle.LicenceRuntimeState` as a typed adapter around startup product name, colors, version, debug logging, and packet tracing state previously read/written through raw `Licence` globals.
- Added `com.alphaseries.server.update.UpdaterSettings` as a typed adapter around updater executable name, update rows, and update SQL previously read directly from `Licence` globals, and migrated `Updater` off `Vb` helpers.
- Added `com.alphaseries.server.packet.PacketSink` and kept the root `PacketSink` as a deprecated compatibility alias.
- Added `com.alphaseries.dao.mysql.StaffModerationDao` and routed staff chat-log/room-info moderation reads through typed prepared DAO methods instead of inline SQL concatenation in `MySQL`.
- Expanded `com.alphaseries.dao.mysql.StaffModerationDao` for user moderation summaries, routing the staff user-summary handler through prepared DAO methods.
- Expanded `com.alphaseries.dao.mysql.StaffModerationDao` for staff ban logging, ban insertion, target IP lookup, and login-session clearing.
- Expanded `com.alphaseries.dao.mysql.StaffModerationDao` for room moderation target lookup, logging, event cleanup, and room-owner cautions.
- Expanded `com.alphaseries.dao.mysql.StaffModerationDao` for open call-for-help review rows, reporter lookup, and close-state updates.
- Expanded `com.alphaseries.dao.mysql.StaffModerationDao` for moderation room-lock updates.
- Expanded `com.alphaseries.dao.mysql.StaffModerationDao` for call-for-help cancel, staff list, duplicate-check, submit, and newest-id lookups.
- Added `com.alphaseries.dao.mysql.HelpDao` and routed FAQ search through a prepared DAO method.
- Added `com.alphaseries.dao.mysql.ClubDao` and routed subscription offer/user club-status reads through prepared DAO methods.
- Expanded `ClubDao` with club-period updates, clearing remaining direct `MySQL.Proc_5_*` usage from `Functions`.
- Added `com.alphaseries.dao.mysql.PackageDao` and routed package pet preview lookups through typed prepared DAO methods.
- Added `com.alphaseries.dao.mysql.BotDao` and routed package pet creation bot inserts/pet-data writes through typed prepared DAO methods.
- Expanded `BotDao` with typed pet race and inventory rows, and routed pet race/inventory payload handlers through typed records instead of tab-delimited database rows.
- Expanded `BotDao` for pet pickup room clearing, pet-data refresh, and scratch count reads, replacing raw pet pickup SQL in `Handling`.
- Expanded `BotDao`/`RoomDao` for pet placement room-slot, heightmap, and bot placement updates, replacing the matching raw placement SQL in `Handling`.
- Expanded `BotDao` with typed pet status rows and routed represented pet status payloads through typed records instead of tab-delimited database rows.
- Expanded `BotDao` with typed pet command action fallback rows, replacing the raw command-action SQL fallback in `Handling`.
- Added `com.alphaseries.dao.mysql.TradeDao` and routed trade ownership updates/log insertion through prepared DAO methods.
- Expanded `com.alphaseries.dao.mysql.RoomDao` for room settings, rights, icon, door-status, and event create/edit/delete operations, and routed homeroom updates through `UserDao`.
- Expanded `RoomDao`/`UserDao` for shared `Handling` socket, active-room, permission, room-right, category visibility, and room-ban helper queries.
- Expanded `RoomDao`/`UserDao` for room-control active user lookup, room settings updates, and room-event payload row retrieval.
- Expanded `RoomDao`/`UserDao` for room enter/leave lifecycle queries and writes, including visit logs, visitor counts, and represented room slot clearing.
- Expanded `RoomDao`/`UserDao` for room rating and room-right grant/revoke operations.
- Expanded `RoomDao` with prepared room-rating existence checks for room entry payloads.
- Expanded `RoomDao` with typed active room effect rows for room effect payload broadcasts.
- Expanded `RoomDao` with prepared room model-id lookup for model furniture payload fallback.
- Expanded `RoomDao` for room-right wipe notifications, batch right revocation, and room deletion operations.
- Expanded `RoomDao` with typed `OfficialRoomModel` loading for official-room model/caption payloads.
- Expanded `RoomDao` with typed `RoomModelEntry` loading for room model entry payload setup.
- Added `com.alphaseries.dao.mysql.MessengerDao` and routed accepted-friend socket notifications plus pending friend-request deletion through prepared DAO methods.
- Expanded `MessengerDao` for accepted-friendship existence checks and accepted-friend removal, replacing raw friend-removal SQL in `Handling`.
- Expanded `MessengerDao` for pending-request rows and accepted-friend list rows, replacing raw messenger list SQL in `Handling` while keeping row-text payload compatibility at the DAO boundary.
- Expanded `MessengerDao` for friend-request target lookup, existing friendship checks, accept-friends reads, and request inserts, replacing raw friend-request creation SQL in `Handling`.
- Expanded `MessengerDao` with typed messenger search result records and routed search friendship checks through prepared DAO methods instead of raw handler SQL.
- Expanded `MessengerDao` for private-message chat log insertion, replacing raw messenger chat-log SQL in `Handling`.
- Added `com.alphaseries.dao.mysql.FurnitureDao` with typed row records for sticky-note, gift-box, and wall-state furniture handlers, avoiding tab-delimited DAO row strings.
- Expanded `FurnitureDao` with typed wall-furniture room rows for room wall-item payloads.
- Expanded `FurnitureDao` with typed product lookup for credit-furniture redemption and routed the furniture delete through the DAO.
- Expanded `UserDao` with prepared credit increments and routed credit-furniture redemption balance updates through typed DAO methods.
- Expanded `FurnitureDao` with typed pending-furniture timer state and routed `Main.signerTimer` through prepared DAO methods.
- Expanded `FurnitureDao` with typed roller furniture rows and prepared room-position updates for `Main.rollersTimer`.
- Expanded `FurnitureDao` with typed inventory furniture rows and routed `Functions` inventory cache add/remove refreshes through prepared DAO methods.
- Expanded `FurnitureDao` with typed trade furniture lookups for trade offer add/remove handlers.
- Expanded `FurnitureDao` with typed owner inventory list rows and routed the inventory list handler through typed inventory payload aggregation instead of tab-delimited DAO row strings.
- Expanded `FurnitureDao`/`RoomDao` for room decoration item application, replacing tab-delimited furniture rows and raw room/furniture update SQL with typed prepared DAO methods.
- Expanded `FurnitureDao` for floor-furniture pickup, replacing tab-delimited ownership rows and raw pickup update SQL with typed prepared DAO methods.
- Expanded `FurnitureDao` for room-right floor-furniture pickup, replacing tab-delimited ownership rows and room-guarded raw pickup update SQL with typed prepared DAO methods.
- Expanded `FurnitureDao` for floor-furniture state toggles, replacing tab-delimited state rows and raw state update SQL with typed prepared DAO methods.
- Expanded `FurnitureDao` for room furniture state refreshes, replacing tab-delimited state rows with typed prepared DAO reads.
- Routed furniture cache marker cleanup through typed room furniture state reads instead of separate raw product/room SQL lookups.
- Expanded `FurnitureDao` for wall furniture placement, replacing inventory placement row strings and raw wall-placement update SQL with typed prepared DAO methods.
- Expanded `FurnitureDao` with typed floor-position furniture rows and routed package-open product/package checks through typed `FurnitureDao`/`PackageDao` records instead of tab-delimited handler parsing.
- Expanded `FurnitureDao`/`RoomDao` for floor-placement validation, replacing raw room-model, floor-occupancy, bot-occupancy, and active-occupant SQL with typed prepared DAO methods.
- Expanded `FurnitureDao` for located furniture state, product-based state refresh, room/product id lookup, room existence checks, and wired sign updates, replacing raw furniture state SQL in `Handling` helpers.
- Expanded `RoomDao` with typed `RoomEntryState` loading and room-ban checks for room entry validation.
- Expanded `UserDao` for respect balance reads and respect-give updates, routing the respect handler through prepared DAO methods.
- Expanded `UserDao` with typed activity-point balance rows and routed the balance payload through fluent `UserPayloads` construction.
- Expanded `com.alphaseries.dao.mysql.UserDao` for socket-user and permission-level lookups, removing remaining inline user lookup SQL from `MySQL` helper paths.
- Expanded `com.alphaseries.dao.mysql.UserDao` for credit and activity-point refresh lookups, routing `Functions` refresh helpers through prepared DAO methods.
- Expanded `com.alphaseries.dao.mysql.UserDao` for wardrobe rows, wardrobe slot replacement, tutorial-clothes updates, and motto lookup.
- Expanded `com.alphaseries.dao.mysql.UserDao` for avatar-name validation, rename updates, and identity-log insertion.
- Expanded `com.alphaseries.dao.mysql.RoomDao` for active room socket lookups, routing `Functions` ready/room-alert helpers through prepared DAO methods.
- Expanded `com.alphaseries.dao.mysql.RoomDao` for current-room and roller tile lookups, routing matching `Main` helpers through prepared DAO methods.
- Added `com.alphaseries.dao.mysql.ServerMaintenanceDao` and routed boot/query-unload reset SQL plus `Main` socket-user fallback through typed DAO boundaries.
- Expanded `ServerMaintenanceDao` with ping-timer setting updates, clearing remaining direct `MySQL.Proc_5_*` usage from `Main`.
- Migrated remaining `Functions` conversions for email validation, identity refresh, club period, occupancy, download, inventory path, movement, and random helpers off `Vb`.
- Migrated remaining `MySQL` helper conversions and staff moderation payload builders off `Vb` helpers and onto shared `StringUtils`/`NumberUtils`.
- Added `com.alphaseries.util.StringUtils` and `NumberUtils`; migrated new staff/session code away from duplicated local helper methods.
- Added `com.alphaseries.protocol.ReadyPacketBuffer` for game ready-packet frame parsing and routed `Filesystems` through it instead of inline VB-style substring parsing.
- Migrated extracted `game.*`, `messages.outgoing`, and `protocol` packages off duplicated local `text`/`number`/`field` helpers and onto shared utilities.
- Migrated wardrobe slot, identity refresh, and represented chat payloads from `Handling` string/Crypto concatenation to fluent `UserPayloads` builders.
- Migrated adjacent wardrobe validation, URL extraction, chat filter/gesture, and call-for-help payload shims in `Handling` from `Vb` conversions to shared utility helpers.
- Migrated `HandlingMUS` off `Vb` helpers and onto shared `StringUtils`/`NumberUtils` conversions.
- Migrated `Console` and `PrivSockHTTP` off `Vb` helpers and onto shared `StringUtils`/`NumberUtils` conversions.
- Migrated `DataManager` licence/cache/file helpers off `Vb` conversions and added shared `StringUtils.mid` helpers for VB-style one-based substring compatibility.
- Migrated core `Functions` setting/string/wire-field/inventory-cache helpers off `Vb` conversions and onto shared utility helpers/fluent alert payloads.
- Migrated `Licence.Proc_9_0` through `Proc_9_5` to `CatalogRegistry`, added named cache setters, and removed `Licence`'s dependency on `Vb`.
- Migrated `Main` identity/startup/game-server/session-cache helpers off `Vb` conversions and onto shared `StringUtils`/`NumberUtils`.
- Migrated `Main` roller movement payload construction from chained `Crypto` calls to fluent `PacketBuilder`.
- Migrated `Boot` cache refresh/build helper conversions off `Vb` and onto shared `StringUtils`/`NumberUtils`.
- Migrated remaining `Handling` conversions off `Vb` and onto shared `StringUtils`/`NumberUtils`, then removed the unused `com.alphaseries.vb.Vb` compatibility class.
- Migrated several payload builders from string concatenation to fluent `PacketBuilder`.

## VB Compatibility Class Removal Checklist

Compared with `main`, the VB helper artifact has been removed:

- Removed `src/main/java/com/alphaseries/vb/Vb.java` after all direct `Vb.` call sites and imports reached zero.

## Current Legacy Surface

Measured on 2026-06-30:

- Unique `Proc_*` symbols under `src/main/java`: 463
- `Vb.` call sites under `src/main/java/com/alphaseries`: 0
- `MySQL.Proc_5_*` call sites under `src/main/java/com/alphaseries`: 269
- `Boot.java`: 1130 lines
- `Handling.java`: 12371 lines
- `Functions.java`: 746 lines
- `MySQL.java`: 316 lines
- `Main.java`: 920 lines

## Next Targets

- Continue migrating raw `MySQL.Proc_5_*` call clusters into `dao.mysql` classes with typed prepared methods.
- Replace `Functions` and `Handling` user/account operations with domain services under `game.user` or `runtime.session`.
- Move MUS handling into a `server.mus` package with compatibility shims for old entry points.
- Extract navigator, room, moderation, pet, badge, poll, recycler, jukebox, and wired payload builders from `Handling`.
- Replace remaining `Crypto.Proc_3_*` usage with `WireEncoding`, `PacketReader`, `PacketBuilder`, and local typed helpers.
- Continue replacing duplicated local string/number helpers in root compatibility classes with `StringUtils` and `NumberUtils`.
- Move remaining raw `Licence.global_*` caches into typed state holders under the appropriate `game.*` package.
- Delete remaining deprecated compatibility aliases only after their call sites reach zero and tests pass.

## Verification

- Latest full verification command: `./gradlew test --no-daemon`
- Latest result: passing
