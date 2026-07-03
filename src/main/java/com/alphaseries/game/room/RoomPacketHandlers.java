package com.alphaseries.game.room;

import com.alphaseries.config.AppConfigState;
import com.alphaseries.dao.mysql.BotDao;
import com.alphaseries.dao.mysql.DaoProvider;
import com.alphaseries.dao.mysql.FurnitureDao;
import com.alphaseries.dao.mysql.PollDao;
import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.game.navigator.NavigatorRequests;
import com.alphaseries.game.pet.PetLookups;
import com.alphaseries.game.poll.PollLookups;
import com.alphaseries.game.quest.QuestPacketHandlers;
import com.alphaseries.game.social.SocialLookups;
import com.alphaseries.game.user.UserLookups;
import com.alphaseries.game.wired.WiredLookups;
import com.alphaseries.game.wired.WiredSettings;
import com.alphaseries.game.wired.WiredState;
import com.alphaseries.messages.outgoing.RoomPayloads;
import com.alphaseries.messages.outgoing.UserPayloads;
import com.alphaseries.server.runtime.SessionLookups;
import com.alphaseries.server.runtime.SocketDelivery;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.List;

public final class RoomPacketHandlers {
    private RoomPacketHandlers() {
    }

    /**
     * Original function: Proc_6_54_719050.
     */
    private static long enterRepresentedRoom(int socketIndex, long roomId, long preferredSlot) {
        long reservedSlot = 0L;
        try {
            if (socketIndex <= 0 || roomId <= 0L) {
                return 0L;
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            if (SessionLookups.currentRoomId(socketIndex, userId) > 0L) {
                leaveCurrentRoom(socketIndex);
            }
            RoomState.instance().representedRoomSlots();
            reservedSlot = RoomState.instance().reserveRepresentedRoomSlot(preferredSlot);
            RoomState.instance().setRepresentedRoomSlots(RoomState.instance().representedRoomSlots());
            if (reservedSlot <= 0L) {
                SocketDelivery.sendToSocket(socketIndex, UserPayloads.errorCode(1, 0));
                return 0L;
            }
            PetLookups.loadRepresentedRoomBots(
                reservedSlot,
                roomId,
                NumberUtils.parseLong(AppConfigState.instance().settingsCache()
                    .valueOrDefault("com.client.rooms.bots.enabled", "-1")) != 0L,
                botDao());
            UserDao users = userDao();
            RoomDao rooms = roomDao();
            if (users == null || rooms == null) {
                RoomState.instance().representedRoomSlots();
                RoomState.instance().releaseRepresentedRoomSlot(reservedSlot);
                RoomState.instance().setRepresentedRoomSlots(RoomState.instance().representedRoomSlots());
                return 0L;
            }
            long userIdValue = NumberUtils.parseLong(userId);
            String sessionId = users.loginSession(userIdValue);
            rooms.insertVisit(userIdValue, roomId, sessionId);
            rooms.markRoomEntered(roomId, reservedSlot);
            sendRoomEntryBootstrap(socketIndex, 0);
            RoomRefreshService.sendRoomReady(socketIndex);
            return reservedSlot;
        } catch (Exception ignored) {
            if (reservedSlot > 0L) {
                RoomState.instance().representedRoomSlots();
                RoomState.instance().releaseRepresentedRoomSlot(reservedSlot);
                RoomState.instance().setRepresentedRoomSlots(RoomState.instance().representedRoomSlots());
            }
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_55_71A6E0.
     */
    private static long leaveCurrentRoom(int socketIndex) {
        try {
            if (socketIndex <= 0) {
                return 0L;
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            RoomDao rooms = roomDao();
            if (rooms == null) {
                SocketDelivery.sendToSocket(socketIndex, "J|H");
                return 0L;
            }
            long userIdValue = NumberUtils.parseLong(userId);
            RoomDao.ActiveRoomVisit visit = rooms.activeVisitWithRoomSlot(userIdValue).orElse(null);
            if (visit == null) {
                SocketDelivery.sendToSocket(socketIndex, "J|H");
                return 0L;
            }
            long visitId = visit.visitId();
            long roomId = visit.roomId();
            long slotId = visit.slotId();
            if (roomId <= 0L) {
                return 0L;
            }
            long roomUserIndex = SessionLookups.representedRoomUserIndex(socketIndex, userId);
            if (roomUserIndex > 0L) {
                SocketDelivery.broadcastToCurrentRoom(socketIndex, SocialLookups.roomUserRemovedPayload(roomUserIndex));
            }
            if (visitId > 0L) {
                rooms.closeVisitById(visitId);
            } else {
                rooms.closeVisitsByUserRoom(userIdValue, roomId);
            }
            rooms.decrementVisitors(roomId);
            if (slotId > 0L) {
                RoomState.instance().representedRoomSlots();
                RoomState.instance().releaseRepresentedRoomSlot(slotId);
                RoomState.instance().setRepresentedRoomSlots(RoomState.instance().representedRoomSlots());
                rooms.clearRoomSlot(roomId, slotId);
            }
            SocketDelivery.sendToSocket(socketIndex, "J|H");
            return roomId;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_56_71E730.
     */
    private static void sendRoomEntryBootstrap(int socketIndex, long roomMode) {
        try {
            SocketDelivery.sendToSocket(socketIndex, RoomPayloads.entryBootstrap(roomMode));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_57_71E8F0.
     */
    public static long enterRoom(int socketIndex, long roomId, String suppliedPassword) {
        try {
            String roomPassword = StringUtils.text(suppliedPassword);
            if (roomId <= 0L) {
                SocketDelivery.sendToSocket(socketIndex, "C`H");
                return 0L;
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            RoomDao rooms = roomDao();
            if (rooms == null) {
                SocketDelivery.sendToSocket(socketIndex, "C`H");
                return 0L;
            }
            RoomDao.RoomEntryState entryState = rooms.roomEntryState(roomId).orElse(null);
            if (entryState == null) {
                SocketDelivery.sendToSocket(socketIndex, "C`H");
                return 0L;
            }
            long userIdValue = NumberUtils.parseLong(userId);
            boolean isOwner = entryState.ownerUserId() == userIdValue;
            if (!isOwner) {
                if (rooms.userBannedFromRoom(userIdValue, roomId)) {
                    RoomRefreshService.sendRoomReady(socketIndex);
                    SocketDelivery.sendToSocket(socketIndex, "C`PA");
                    return 0L;
                }
                if (entryState.visitorsMax() > 0L && entryState.visitorsNow() >= entryState.visitorsMax()
                    && !UserLookups.hasPermission(NumberUtils.parseLong(userId), "fuse_enter_full_rooms", userDao(), AppConfigState.instance().permissionMatrix())) {
                    RoomRefreshService.sendRoomReady(socketIndex);
                    SocketDelivery.sendToSocket(socketIndex, "C`I");
                    return 0L;
                }
                if (entryState.doorStatus() == 1L && !UserLookups.hasPermission(NumberUtils.parseLong(userId), "fuse_enter_locked_rooms", userDao(), AppConfigState.instance().permissionMatrix())) {
                    RoomRefreshService.sendRoomReady(socketIndex);
                    SocketDelivery.sendToSocket(socketIndex, "C`H");
                    return 0L;
                }
                if (entryState.doorStatus() == 2L && !StringUtils.text(entryState.password()).equals(roomPassword)) {
                    RoomRefreshService.sendRoomReady(socketIndex);
                    SocketDelivery.sendToSocket(socketIndex, "@afhFF");
                    return 0L;
                }
            }
            return enterRepresentedRoom(socketIndex, roomId, entryState.roomSlot());
        } catch (Exception ignored) {
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_58_71FCA0.
     */
    public static long enterRoomFromPayload(int socketIndex, RoomWire.RoomEntryRequest request) {
        try {
            RoomState.instance().representedRoomSlots();
            RoomState.instance().ensureRepresentedRoomSlotPool();
            RoomState.instance().setRepresentedRoomSlots(RoomState.instance().representedRoomSlots());
            if (RoomState.instance().representedRoomSlots().isEmpty()) {
                SocketDelivery.sendToSocket(socketIndex, UserPayloads.errorCode(1, 0));
                return 0L;
            }
            long roomId = request.roomId();
            String roomPassword = request.roomPassword();
            enterRoom(socketIndex, roomId, roomPassword);
            return roomId;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_78_7279A0.
     */
    public static void loadCurrentRoomModel(int socketIndex, QuestPacketHandlers.CompletionHandler questCompletionHandler) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (socketIndex <= 0 || userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            RoomLookups.RoomModelLoad modelLoad = RoomLookups.roomModelLoad(roomId, roomDao());
            if (!modelLoad.valid()) {
                return;
            }
            for (String payload : modelLoad.initialPayloads()) {
                SocketDelivery.sendToSocket(socketIndex, payload);
            }
            sendRoomOccupantList(socketIndex, roomId);
            sendRoomActiveEffects(socketIndex, roomId);
            sendCurrentRoomStartupCache(socketIndex, roomId);
            sendCurrentRoomModelFurniture(socketIndex, modelLoad.modelId());
            sendCurrentRoomWallFurniture(socketIndex, roomId);
            QuestPacketHandlers.refreshProgress(socketIndex, questCompletionHandler);
            broadcastCurrentRoomUserEntry(socketIndex, roomId);
            SocketDelivery.sendToSocket(socketIndex, "CP" + '\2' + '\2');
            String pollPromptPayload = PollLookups.promptPayload(NumberUtils.parseLong(userId), roomId, pollDao());
            if (!pollPromptPayload.isEmpty()) {
                SocketDelivery.sendToSocket(socketIndex, pollPromptPayload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_79_72A430.
     */
    public static void sendCurrentRoomDecoration(int socketIndex, QuestPacketHandlers.CompletionHandler questCompletionHandler) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (socketIndex <= 0 || userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            boolean hasControl = RoomLookups.userHasRoomRight(NumberUtils.parseLong(userId), roomId, roomDao())
                || UserLookups.hasPermission(NumberUtils.parseLong(userId), "fuse_any_room_controller", userDao(), AppConfigState.instance().permissionMatrix());
            RoomLookups.RoomPresentationLoad presentationLoad = RoomLookups.roomPresentationLoad(
                NumberUtils.parseLong(userId), roomId, hasControl, currentRoomEventInfoPayload(roomId), roomDao());
            if (!presentationLoad.valid()) {
                return;
            }
            for (String payload : presentationLoad.initialPayloads()) {
                SocketDelivery.sendToSocket(socketIndex, payload);
            }
            sendRoomOccupantList(socketIndex, roomId);
            sendRoomActiveEffects(socketIndex, roomId);
            sendCurrentRoomModelFurniture(socketIndex, presentationLoad.modelId());
            sendCurrentRoomStartupCache(socketIndex, roomId);
            sendCurrentRoomWallFurniture(socketIndex, roomId);
            QuestPacketHandlers.refreshProgress(socketIndex, questCompletionHandler);
            broadcastCurrentRoomUserEntry(socketIndex, roomId);
            SocketDelivery.sendToSocket(socketIndex, "CP" + '\2' + '\2');
            String pollPromptPayload = PollLookups.promptPayload(NumberUtils.parseLong(userId), roomId, pollDao());
            if (!pollPromptPayload.isEmpty()) {
                SocketDelivery.sendToSocket(socketIndex, pollPromptPayload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_51_716AC0.
     */
    private static String currentRoomEventInfoPayload(long roomId) {
        try {
            if (roomId <= 0L) {
                return "-1" + '\2';
            }
            String timeFormat = AppConfigState.instance().settingsCache().valueOrDefault("com.mysql.format.time", "%H:%i");
            return RoomLookups.eventInfoPayload(roomId, timeFormat, roomDao());
        } catch (Exception ignored) {
            return "-1" + '\2';
        }
    }

    /**
     * Original function: Proc_6_80_72EB60.
     */
    private static void broadcastCurrentRoomUserEntry(int socketIndex, long roomId) {
        try {
            if (socketIndex <= 0) {
                return;
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long effectiveRoomId = roomId > 0L ? roomId : SessionLookups.currentRoomId(socketIndex, userId);
            if (effectiveRoomId <= 0L) {
                return;
            }
            long roomUserIndex = SessionLookups.representedRoomUserIndex(socketIndex, userId);
            String payload = SocialLookups.roomUserEntryBroadcastPayload(
                NumberUtils.parseLong(userId), effectiveRoomId, roomUserIndex, roomDao());
            if (!payload.isEmpty()) {
                SocketDelivery.broadcastToCurrentRoom(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_81_730010.
     */
    private static void sendRoomOccupantList(int socketIndex, long roomId) {
        try {
            long effectiveRoomId = roomId;
            if (effectiveRoomId <= 0L && socketIndex > 0) {
                String userId = SessionLookups.userIdTextFromSocket(socketIndex);
                if (!userId.isEmpty() && !"0".equals(userId)) {
                    effectiveRoomId = SessionLookups.currentRoomId(socketIndex, userId);
                }
            }
            if (socketIndex <= 0 || effectiveRoomId <= 0L) {
                return;
            }
            for (String payload : SocialLookups.roomOccupantListPayloads(
                effectiveRoomId,
                RoomState.instance().representedRooms(),
                roomDao())) {
                SocketDelivery.sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_82_731070.
     */
    private static void sendRoomActiveEffects(int socketIndex, long roomId) {
        try {
            long effectiveRoomId = roomId;
            if (effectiveRoomId <= 0L && socketIndex > 0) {
                String userId = SessionLookups.userIdTextFromSocket(socketIndex);
                if (!userId.isEmpty() && !"0".equals(userId)) {
                    effectiveRoomId = SessionLookups.currentRoomId(socketIndex, userId);
                }
            }
            if (socketIndex <= 0 || effectiveRoomId <= 0L) {
                return;
            }
            for (String payload : SocialLookups.activeRoomEffectPayloads(effectiveRoomId, roomDao())) {
                SocketDelivery.sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_83_732640.
     */
    private static String sendCurrentRoomModelFurniture(int socketIndex, long modelId) {
        try {
            long roomId = 0L;
            if (modelId <= 0L && socketIndex > 0) {
                String userId = SessionLookups.userIdTextFromSocket(socketIndex);
                if (!userId.isEmpty() && !"0".equals(userId)) {
                    roomId = SessionLookups.currentRoomId(socketIndex, userId);
                }
            }
            String payload = FurnitureLookups.modelFurniturePayloadForRoom(modelId, roomId, roomDao());
            if (socketIndex > 0) {
                SocketDelivery.sendToSocket(socketIndex, payload);
            }
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_84_733600.
     */
    private static String sendCurrentRoomStartupCache(int socketIndex, long roomId) {
        String payload = WiredLookups.roomStartupCachePayload(0L, wiredSettings());
        try {
            long effectiveRoomId = roomId;
            if (effectiveRoomId <= 0L && socketIndex > 0) {
                String userId = SessionLookups.userIdTextFromSocket(socketIndex);
                if (!userId.isEmpty() && !"0".equals(userId)) {
                    effectiveRoomId = SessionLookups.currentRoomId(socketIndex, userId);
                }
            }
            if (socketIndex > 0) {
                SocketDelivery.sendToSocket(socketIndex, payload);
            }
            return WiredLookups.roomStartupCachePayload(effectiveRoomId, wiredSettings());
        } catch (Exception ignored) {
            return payload;
        }
    }

    /**
     * Original function: Proc_6_85_73A8E0.
     */
    private static String sendCurrentRoomWallFurniture(int socketIndex, long roomId) {
        try {
            long effectiveRoomId = roomId;
            if (effectiveRoomId <= 0L && socketIndex > 0) {
                String userId = SessionLookups.userIdTextFromSocket(socketIndex);
                if (!userId.isEmpty() && !"0".equals(userId)) {
                    effectiveRoomId = SessionLookups.currentRoomId(socketIndex, userId);
                }
            }
            if (effectiveRoomId <= 0L) {
                return "";
            }
            FurnitureDao furniture = furnitureDao();
            if (furniture == null) {
                return "";
            }
            String payload = FurnitureLookups.wallFurniturePayload(effectiveRoomId, furniture);
            if (socketIndex > 0) {
                SocketDelivery.sendToSocket(socketIndex, payload);
            }
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_197_7D43C0.
     */
    public static String lookTowardRoomPosition(int socketIndex, RoomWire.PositionRequest request) {
        try {
            if (socketIndex <= 0) {
                return "";
            }
            long lookX = request.positionX();
            long lookY = request.positionY();
            if (lookX < 0L || lookY < 0L) {
                return "";
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return "";
            }
            long roomSlot = socketIndex;
            RoomUserPosition current = RoomUserPosition.from(
                RoomState.instance().representedRooms().movementPosition(roomSlot, socketIndex));
            long currentX = current.found() ? current.positionX() : 0L;
            long currentY = current.found() ? current.positionY() : 0L;
            long directionValue = MovementStep.directionCode(Long.compare(lookX, currentX), Long.compare(lookY, currentY));
            RoomState.instance().setRepresentedRooms(
                RoomState.instance().representedRooms().moveOccupant(roomSlot, socketIndex, currentX, currentY, directionValue, 0L));
            RoomCacheFiles.invalidateRoomPayload(roomId);
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_198_7D4B70.
     */
    public static String walkTowardRoomPosition(int socketIndex, RoomWire.PositionRequest request) {
        try {
            if (socketIndex <= 0) {
                return "";
            }
            long targetX = request.positionX();
            long targetY = request.positionY();
            if (targetX < 0L || targetY < 0L) {
                return "";
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            if (roomId <= 0L || RoomPositionService.roomPositionAvailable(roomId, targetX, targetY) == 0L) {
                return "";
            }
            long roomSlot = socketIndex;
            RoomUserPosition current = RoomUserPosition.from(
                RoomState.instance().representedRooms().movementPosition(roomSlot, socketIndex));
            long currentX = current.found() ? current.positionX() : 0L;
            long currentY = current.found() ? current.positionY() : 0L;
            MovementStep movement = MovementStep.between(currentX, currentY, targetX, targetY);
            long nextX = movement.positionX();
            long nextY = movement.positionY();
            long directionValue = movement.directionValue();
            long movingValue = movement.movingValue();
            if (movingValue == 0L && (currentX != targetX || currentY != targetY)) {
                movingValue = 1L;
            }
            RoomState.instance().setRepresentedRooms(
                RoomState.instance().representedRooms().moveOccupant(roomSlot, socketIndex, nextX, nextY, directionValue, movingValue));
            RoomCacheFiles.invalidateRoomPayload(roomId);
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_108_74D800.
     */
    public static void sendFavouriteRoomIds(int socketIndex) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            long maxFavorites = AppConfigState.instance().settingsCache().longValueOrDefault("com.server.socket.game.rooms.favourites.max", 30);
            if (maxFavorites <= 0L) {
                maxFavorites = 30L;
            }
            String payload = RoomLookups.favouriteRoomIdsPayload(NumberUtils.parseLong(userId), maxFavorites, roomDao());
            if (!payload.isEmpty()) {
                SocketDelivery.sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_109_74DBD0.
     */
    public static void removeFavouriteRoom(int socketIndex, RoomWire.RoomIdRequest request) {
        try {
            long roomId = request.roomId();
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            String payload = RoomLookups.removeFavouriteRoomPayload(NumberUtils.parseLong(userId), roomId, roomDao());
            if (!payload.isEmpty()) {
                SocketDelivery.sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_110_74DDA0.
     */
    public static void addFavouriteRoom(int socketIndex, RoomWire.RoomIdRequest request) {
        try {
            long roomId = request.roomId();
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            String payload = RoomLookups.addFavouriteRoomPayload(NumberUtils.parseLong(userId), roomId, roomDao());
            if (!payload.isEmpty()) {
                SocketDelivery.sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_104_74AB60.
     */
    public static void sendCreatableRoomCount(int socketIndex) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            long maxOwnedRooms = AppConfigState.instance().settingsCache().longValueOrDefault("com.server.socket.game.rooms.own.max", 0);
            SocketDelivery.sendToSocket(socketIndex, RoomLookups.creatableRoomCountPayload(NumberUtils.parseLong(userId), maxOwnedRooms, roomDao()));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_105_74AD50.
     */
    public static void createRoom(int socketIndex, RoomWire.CreateRoomRequest request) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long userIdValue = NumberUtils.parseLong(userId);
            long maxOwnedRooms = AppConfigState.instance().settingsCache().longValueOrDefault("com.server.socket.game.rooms.own.max", 0);
            CreatedRoom room = RoomLookups.createRoom(
                userIdValue, request,
                maxOwnedRooms, UserLookups.hcLevel(userIdValue, userDao()), roomDao());
            if (!room.valid()) {
                return;
            }
            room.invalidateCaches();
            SocketDelivery.sendToSocket(socketIndex, room.payload());
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_107_74B7E0.
     */
    public static void toggleStaffPickedRoom(int socketIndex) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long userIdValue = NumberUtils.parseLong(userId);
            if (!UserLookups.hasPermission(userIdValue, "fuse_client_staff", userDao(), AppConfigState.instance().permissionMatrix())) {
                return;
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            long categoryId = AppConfigState.instance().settingsCache().longValueOrDefault("com.client.navigator.staff_picked.category.id.default", 0);
            long styleId = AppConfigState.instance().settingsCache().longValueOrDefault("com.client.navigator.staff_picked.style.default", 0);
            long iconId = AppConfigState.instance().settingsCache().longValueOrDefault("com.client.navigator.staff_picked.category.icon.default", 0);
            StaffPickedToggle toggle = RoomLookups.toggleStaffPickedRoom(
                roomId, categoryId, styleId, iconId, roomDao(), userDao());
            if (!toggle.changed()) {
                return;
            }
            SocketDelivery.broadcastToCurrentRoom(socketIndex, NavigatorRequests.roomListPayload(roomId, roomDao()));
            SocketDelivery.broadcastToCurrentRoom(socketIndex, RoomPayloads.entryUpdated(roomId));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_47_714F60.
     */
    public static void setHomeRoom(int socketIndex, RoomWire.RoomIdRequest request) {
        try {
            long roomId = request.roomId();
            if (roomId <= 0L) {
                return;
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            String payload = RoomLookups.setHomeRoomPayload(NumberUtils.parseLong(userId), roomId, userDao());
            if (!payload.isEmpty()) {
                SocketDelivery.sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_46_714D50.
     */
    public static void sendRoomDoorStatus(int socketIndex) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            SocketDelivery.sendToSocket(socketIndex, RoomLookups.doorStatusPayload(roomId, roomDao()));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_43_713680.
     */
    public static void sendRoomSettings(int socketIndex, RoomWire.RoomSettingsReadRequest request) {
        try {
            long requestedRoomId = request.requestedRoomId();
            String callerUserId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long callerUserIdValue = NumberUtils.parseLong(callerUserId);
            long roomId = requestedRoomId > 0L ? requestedRoomId : SessionLookups.currentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L) {
                return;
            }
            if (!RoomLookups.userOwnsRoom(callerUserIdValue, roomId, roomDao())
                && !UserLookups.hasPermission(callerUserIdValue, "fuse_any_room_controller", userDao(), AppConfigState.instance().permissionMatrix())) {
                return;
            }
            String payload = RoomLookups.roomSettingsPayload(roomId, roomDao());
            if (!payload.isEmpty()) {
                SocketDelivery.sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_44_7145E0.
     */
    public static void updateRoomIcon(int socketIndex, RoomWire.RoomIconRequest request) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            RoomLookups.RoomIconUpdate iconUpdate =
                RoomLookups.updateRoomIcon(roomId, request, roomDao());
            if (!iconUpdate.valid()) {
                return;
            }
            SocketDelivery.broadcastToCurrentRoom(socketIndex, NavigatorRequests.roomListPayload(roomId, roomDao()));
            SocketDelivery.sendToSocket(socketIndex, iconUpdate.iconUpdatedPayload());
            SocketDelivery.sendToSocket(socketIndex, iconUpdate.entryUpdatedPayload());
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_45_714B60.
     */
    public static void deleteRoomEvent(int socketIndex) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            RoomLookups.RoomEventChange change = RoomLookups.deleteRoomEvent(roomId, roomDao());
            if (change.hasDirectPayload()) {
                SocketDelivery.sendToSocket(socketIndex, change.directPayload());
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_48_7151E0.
     */
    public static void createRoomEvent(int socketIndex, RoomEventPayload event) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            String timeFormat = AppConfigState.instance().settingsCache().valueOrDefault("com.mysql.format.time", "%H:%i");
            RoomLookups.RoomEventChange change = RoomLookups.createRoomEvent(
                NumberUtils.parseLong(userId), roomId, event, timeFormat, roomDao());
            if (change.hasDirectPayload()) {
                SocketDelivery.sendToSocket(socketIndex, change.directPayload());
            }
            if (change.hasBroadcastPayload()) {
                SocketDelivery.broadcastToCurrentRoom(socketIndex, change.broadcastPayload());
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_49_715D30.
     */
    public static void editRoomEvent(int socketIndex, RoomEventPayload event) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            String timeFormat = AppConfigState.instance().settingsCache().valueOrDefault("com.mysql.format.time", "%H:%i");
            RoomLookups.RoomEventChange change = RoomLookups.editRoomEvent(
                NumberUtils.parseLong(userId), roomId, event, timeFormat, roomDao());
            if (change.hasDirectPayload()) {
                SocketDelivery.sendToSocket(socketIndex, change.directPayload());
            }
            if (change.hasBroadcastPayload()) {
                SocketDelivery.broadcastToCurrentRoom(socketIndex, change.broadcastPayload());
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_52_7172B0.
     */
    public static void updateRoomSettings(int socketIndex, RoomSettingsPayload settings) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long userIdValue = NumberUtils.parseLong(userId);
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            if (!RoomLookups.userOwnsRoom(userIdValue, roomId, roomDao())
                && !UserLookups.hasPermission(userIdValue, "fuse_any_room_controller", userDao(), AppConfigState.instance().permissionMatrix())) {
                return;
            }
            RoomLookups.RoomSettingsUpdate update = RoomLookups.updateRoomSettings(
                roomId,
                settings,
                UserLookups.rank(userIdValue, userDao()),
                UserLookups.hcLevel(userIdValue, userDao()),
                UserLookups.hasPermission(userIdValue, "fuse_hide_room_walls", userDao(), AppConfigState.instance().permissionMatrix()),
                roomDao());
            if (!update.valid()) {
                return;
            }
            SocketDelivery.broadcastToCurrentRoom(socketIndex, NavigatorRequests.roomListPayload(roomId, roomDao()));
            SocketDelivery.sendToSocket(socketIndex, update.settingsUpdatedPayload());
            SocketDelivery.sendToSocket(socketIndex, update.entryUpdatedPayload());
            SocketDelivery.sendToSocket(socketIndex, update.wallOptionsPayload());
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_72_7250D0.
     */
    public static void deleteCurrentRoom(int socketIndex, RoomWire.DeleteRoomRequest request) {
        try {
            if (request.requestFlag() != 0L) {
                return;
            }
            String callerUserId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !RoomLookups.userOwnsRoom(NumberUtils.parseLong(callerUserId), roomId, roomDao())) {
                return;
            }
            RoomRefreshService.sendRoomReadyRefreshes(roomId);
            RoomLookups.deleteRoom(roomId, roomDao());
            RoomRefreshService.sendRoomReady(socketIndex);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_61_720490.
     */
    public static void kickRoomUser(int socketIndex, RoomWire.RoomUserTargetRequest request) {
        roomKickOrBanUser(socketIndex, request, false);
    }

    /**
     * Original function: Proc_6_62_7209F0.
     */
    public static void banRoomUser(int socketIndex, RoomWire.RoomUserTargetRequest request) {
        roomKickOrBanUser(socketIndex, request, true);
    }

    /**
     * Original function: Proc_6_63_721050.
     */
    public static void rateCurrentRoom(int socketIndex, RoomWire.RoomRatingRequest request) {
        try {
            long voteValue = request.voteValue();
            if (voteValue != 1L) {
                return;
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            String payload = RoomLookups.rateRoomPayload(NumberUtils.parseLong(userId), roomId, voteValue, roomDao());
            if (!payload.isEmpty()) {
                SocketDelivery.sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_64_721650.
     */
    public static void revokeRoomRightByName(int socketIndex, RoomWire.RoomRightNameRequest request) {
        try {
            String targetName = request.targetName();
            if (targetName.isEmpty()) {
                return;
            }
            String callerUserId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !RoomLookups.userHasRoomRight(NumberUtils.parseLong(callerUserId), roomId, roomDao())) {
                return;
            }
            String payload = RoomLookups.revokeRoomRightByNamePayload(targetName, roomId, userDao(), roomDao());
            if (!payload.isEmpty()) {
                SocketDelivery.sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_65_721A10.
     */
    public static void grantRoomRight(int socketIndex, RoomWire.RoomRightGrantRequest request) {
        try {
            String targetUserId = String.valueOf(request.targetUserId());
            if (targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return;
            }
            String callerUserId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !RoomLookups.userHasRoomRight(NumberUtils.parseLong(callerUserId), roomId, roomDao())) {
                return;
            }
            int targetSocketIndex = SessionLookups.socketFromUserIdText(targetUserId);
            if (targetSocketIndex <= 0) {
                return;
            }
            String payload = RoomLookups.grantRoomRightPayload(NumberUtils.parseLong(targetUserId), roomId, roomDao());
            if (!payload.isEmpty()) {
                SocketDelivery.sendToSocket(targetSocketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_71_724CF0.
     */
    public static void revokeAllRoomRights(int socketIndex) {
        try {
            String callerUserId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !RoomLookups.userOwnsRoom(NumberUtils.parseLong(callerUserId), roomId, roomDao())) {
                return;
            }
            RoomLookups.RoomRightSocketRevocation revocation = RoomLookups.revokeAllRoomRights(roomId, roomDao());
            if (!revocation.hasNotifications()) {
                return;
            }
            for (Long activeSocketIndex : revocation.socketIndexes()) {
                int targetSocketIndex = activeSocketIndex == null ? 0 : activeSocketIndex.intValue();
                if (targetSocketIndex > 0) {
                    SocketDelivery.sendToSocket(targetSocketIndex, revocation.notificationPayload());
                }
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_74_7265B0.
     */
    public static void revokeRoomRights(int socketIndex, RoomWire.RoomRightRevokeRequest request) {
        try {
            String callerUserId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !RoomLookups.userHasRoomRight(NumberUtils.parseLong(callerUserId), roomId, roomDao())) {
                return;
            }
            List<Long> targetUserIds = request.targetUserIds();
            if (targetUserIds.isEmpty()) {
                return;
            }
            RoomLookups.RoomRightRevocation revocation =
                RoomLookups.revokeRoomRights(targetUserIds, roomId, roomDao());
            if (!revocation.hasNotifications()) {
                return;
            }
            for (long targetUserId : revocation.targetUserIds()) {
                int targetSocketIndex = SessionLookups.socketFromUserIdText(String.valueOf(targetUserId));
                if (targetSocketIndex > 0) {
                    SocketDelivery.sendToSocket(targetSocketIndex, revocation.notificationPayload());
                }
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_75_7269D0.
     */
    public static void revokeRoomRightByTargetName(int socketIndex, RoomWire.RoomRightNameRequest request) {
        try {
            String targetName = request.targetName();
            if (targetName.isEmpty()) {
                return;
            }
            String callerUserId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !RoomLookups.userHasRoomRight(NumberUtils.parseLong(callerUserId), roomId, roomDao())) {
                return;
            }
            String payload = RoomLookups.revokeRoomRightByNamePayload(targetName, roomId, userDao(), roomDao());
            if (!payload.isEmpty()) {
                SocketDelivery.sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_77_727590.
     */
    public static void sendOfficialRoomModel(int socketIndex, RoomWire.RoomIdRequest request) {
        try {
            long roomId = request.roomId();
            if (roomId <= 0L) {
                return;
            }
            String payload = RoomLookups.officialRoomModelPayload(roomId, roomDao());
            if (!payload.isEmpty()) {
                SocketDelivery.sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    private static void roomKickOrBanUser(int socketIndex, RoomWire.RoomUserTargetRequest request, boolean addRoomBan) {
        try {
            String targetUserId = String.valueOf(request.targetUserId());
            if (targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return;
            }
            String callerUserId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long callerRoomId = SessionLookups.currentRoomId(socketIndex, callerUserId);
            if (callerRoomId <= 0L) {
                return;
            }
            int targetSocketIndex = SessionLookups.socketFromUserIdText(targetUserId);
            if (targetSocketIndex <= 0) {
                return;
            }
            long targetRoomId = SessionLookups.currentRoomId(targetSocketIndex, targetUserId);
            if (targetRoomId != callerRoomId) {
                return;
            }
            if (!UserLookups.hasPermission(NumberUtils.parseLong(callerUserId), "fuse_kick", userDao(), AppConfigState.instance().permissionMatrix())
                || UserLookups.hasPermission(NumberUtils.parseLong(targetUserId), "fuse_unkickable", userDao(), AppConfigState.instance().permissionMatrix())) {
                return;
            }
            SocketDelivery.sendToSocket(targetSocketIndex, "@aXjO");
            RoomRefreshService.sendRoomReady(targetSocketIndex);
            if (addRoomBan) {
                RoomDao rooms = roomDao();
                if (rooms != null) {
                    rooms.insertRoomBan(callerRoomId, NumberUtils.parseLong(targetUserId));
                }
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    private static RoomDao roomDao() {
        return DaoProvider.roomDao();
    }

    private static UserDao userDao() {
        return DaoProvider.userDao();
    }

    private static FurnitureDao furnitureDao() {
        return DaoProvider.furnitureDao();
    }

    private static PollDao pollDao() {
        return DaoProvider.pollDao();
    }

    private static BotDao botDao() {
        return DaoProvider.botDao();
    }

    private static WiredSettings wiredSettings() {
        return WiredState.instance().settings();
    }
}
