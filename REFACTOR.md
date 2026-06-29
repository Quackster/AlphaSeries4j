# AlphaSeries4j Refactor Progress

Last updated: 2026-06-29

## Goal

Refactor the VB6-port-shaped code into Java packages with stable domain APIs, typed database access, prepared statements, fluent packet builders, and smaller classes while preserving source compatibility and runtime behavior until each compatibility layer can be removed safely.
Keep common string/number helpers in shared utility classes, and move raw `Licence` globals toward typed collections in the relevant `game.*` package.

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
- Expanded `com.alphaseries.dao.mysql.UserDao` for socket-user and permission-level lookups, removing remaining inline user lookup SQL from `MySQL` helper paths.
- Expanded `com.alphaseries.dao.mysql.UserDao` for credit and activity-point refresh lookups, routing `Functions` refresh helpers through prepared DAO methods.
- Expanded `com.alphaseries.dao.mysql.RoomDao` for active room socket lookups, routing `Functions` ready/room-alert helpers through prepared DAO methods.
- Expanded `com.alphaseries.dao.mysql.RoomDao` for current-room and roller tile lookups, routing matching `Main` helpers through prepared DAO methods.
- Added `com.alphaseries.dao.mysql.ServerMaintenanceDao` and routed boot/query-unload reset SQL plus `Main` socket-user fallback through typed DAO boundaries.
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

Measured on 2026-06-29:

- Unique `Proc_*` symbols under `src/main/java`: 468
- `Vb.` call sites under `src/main/java/com/alphaseries`: 0
- `MySQL.Proc_5_*` call sites under `src/main/java/com/alphaseries`: 444
- `Boot.java`: 1130 lines
- `Handling.java`: 12159 lines
- `Functions.java`: 741 lines
- `MySQL.java`: 316 lines
- `Main.java`: 922 lines

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
