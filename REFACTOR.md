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
- Added `com.alphaseries.game.room.RepresentedRoomSlots` as a typed adapter around represented room slot allocation markers previously manipulated as a raw `Licence` string.
- Added `com.alphaseries.game.session.GameServerSessionState` as a typed adapter around queued game-server packet data and ready-session markers previously manipulated as raw `Licence` strings.
- Added `com.alphaseries.game.session.SessionRegistry` as a typed adapter around the legacy `Licence.global_00829268` session cache.
- Added `com.alphaseries.game.wired.WiredSettings` as a typed adapter around wired state payload previously read directly from `Licence` globals.
- Added `com.alphaseries.game.wired.WiredPayloads` for wired record formatting, cache replacement, selected-item checks, and state payload aggregation.
- Added `com.alphaseries.messages.outgoing.MessengerPayloads` for friend, request, search, pending-request, and friend-list outgoing payloads.
- Added `com.alphaseries.server.packet.PacketSink` and kept the root `PacketSink` as a deprecated compatibility alias.
- Added `com.alphaseries.util.StringUtils` and `NumberUtils`; migrated new staff/session code away from duplicated local helper methods.
- Migrated extracted `game.*`, `messages.outgoing`, and `protocol` packages off duplicated local `text`/`number`/`field` helpers and onto shared utilities.
- Migrated `Licence.Proc_9_0` through `Proc_9_5` to `CatalogRegistry`, added named cache setters, and removed `Licence`'s dependency on `Vb`.
- Migrated several payload builders from string concatenation to fluent `PacketBuilder`.

## VB Compatibility Class Removal Checklist

Compared with `main`, the VB helper artifact currently present is:

- `src/main/java/com/alphaseries/vb/Vb.java`

Removal is blocked until all `Vb.` call sites are replaced with domain-specific APIs, protocol helpers, JDK calls, or typed parsing methods. Do not delete this class while root modules still import it.

## Current Legacy Surface

Measured on 2026-06-29:

- Unique `Proc_*` symbols under `src/main/java`: 472
- `Vb.` call sites under `src/main/java/com/alphaseries`: 1360
- `MySQL.Proc_5_*` call sites under `src/main/java/com/alphaseries`: 476
- `Handling.java`: 12342 lines
- `Functions.java`: 756 lines
- `MySQL.java`: 301 lines
- `Vb.java`: 106 lines

## Next Targets

- Continue migrating raw `MySQL.Proc_5_*` call clusters into `dao.mysql` classes with typed prepared methods.
- Replace `Functions` and `Handling` user/account operations with domain services under `game.user` or `runtime.session`.
- Move MUS handling into a `server.mus` package with compatibility shims for old entry points.
- Extract navigator, room, moderation, pet, badge, poll, recycler, jukebox, and wired payload builders from `Handling`.
- Replace remaining `Crypto.Proc_3_*` and direct `Vb.val`/`Vb.cStr` usage with `WireEncoding`, `PacketReader`, `PacketBuilder`, and local typed helpers.
- Continue replacing duplicated local string/number helpers in root compatibility classes with `StringUtils` and `NumberUtils`.
- Move remaining raw `Licence.global_*` caches into typed state holders under the appropriate `game.*` package.
- Delete deprecated compatibility aliases and `com.alphaseries.vb.Vb` only after call-site count reaches zero and tests pass.

## Verification

- Latest full verification command: `./gradlew test --no-daemon`
- Latest result: passing
