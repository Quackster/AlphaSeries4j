package com.alphaseries;

import com.alphaseries.config.AppConfigState;
import com.alphaseries.config.AppPaths;
import com.alphaseries.dao.mysql.StaffModerationDao;
import com.alphaseries.dao.mysql.CatalogDao;
import com.alphaseries.dao.mysql.HelpDao;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.dao.mysql.ClubDao;
import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.dao.mysql.FurnitureDao;
import com.alphaseries.dao.mysql.PackageDao;
import com.alphaseries.dao.mysql.BotDao;
import com.alphaseries.dao.mysql.TradeDao;
import com.alphaseries.dao.mysql.MessengerDao;
import com.alphaseries.dao.mysql.VoucherDao;
import com.alphaseries.dao.mysql.PollDao;
import com.alphaseries.dao.mysql.JukeboxDao;
import com.alphaseries.dao.mysql.QuestDao;
import com.alphaseries.dao.mysql.RecyclerDao;
import com.alphaseries.dao.mysql.DaoProvider;
import com.alphaseries.game.GameDataCaches;
import com.alphaseries.game.advertising.AdvertisingState;
import com.alphaseries.game.advertising.VisitRoomAds;
import com.alphaseries.game.jukebox.JukeboxLookups;
import com.alphaseries.game.jukebox.JukeboxRequests;
import com.alphaseries.game.pet.PetCommandExecution;
import com.alphaseries.game.pet.PetExperienceAward;
import com.alphaseries.game.pet.PetLookups;
import com.alphaseries.game.pet.PetPackagePlacement;
import com.alphaseries.game.pet.PetPickupAction;
import com.alphaseries.game.pet.PetPlacementAction;
import com.alphaseries.game.pet.PetSettings;
import com.alphaseries.game.pet.PetScratchAction;
import com.alphaseries.game.pet.PetState;
import com.alphaseries.game.pet.PetStatusRow;
import com.alphaseries.game.pet.PetTutorialGuideRemoval;
import com.alphaseries.game.pet.PetTutorialGuideSpawn;
import com.alphaseries.game.pet.PetWire;
import com.alphaseries.game.poll.PollLookups;
import com.alphaseries.game.poll.PollWire;
import com.alphaseries.game.quest.QuestCompletion;
import com.alphaseries.game.quest.QuestProgress;
import com.alphaseries.game.quest.QuestProgressDecision;
import com.alphaseries.game.quest.QuestSettings;
import com.alphaseries.game.quest.QuestState;
import com.alphaseries.game.quest.QuestWire;
import com.alphaseries.game.quest.QuestAcceptResult;
import com.alphaseries.game.quest.QuestResetResult;
import com.alphaseries.game.room.CreatedRoom;
import com.alphaseries.game.room.FurnitureRoomCache;
import com.alphaseries.game.room.FurnitureLookups;
import com.alphaseries.game.room.FurnitureWire;
import com.alphaseries.game.room.FurnitureCharges;
import com.alphaseries.game.room.FurnitureDimmers;
import com.alphaseries.game.room.MovementStep;
import com.alphaseries.game.room.RoomCacheFiles;
import com.alphaseries.game.room.RoomLookups;
import com.alphaseries.game.room.RoomPositionService;
import com.alphaseries.game.room.RoomRefreshService;
import com.alphaseries.game.room.FurnitureStateWrites;
import com.alphaseries.game.room.RoomUserPosition;
import com.alphaseries.game.room.RepresentedRoomCache;
import com.alphaseries.game.room.RoomWire;
import com.alphaseries.game.room.RoomUserEntryRow;
import com.alphaseries.game.room.RoomUserTargetRow;
import com.alphaseries.game.room.RoomState;
import com.alphaseries.game.room.StaffPickedToggle;
import com.alphaseries.game.session.GameServerSessionState;
import com.alphaseries.game.session.SessionRegistry;
import com.alphaseries.game.session.SessionState;
import com.alphaseries.game.session.SessionWire;
import com.alphaseries.game.session.SocketMarkerSet;
import com.alphaseries.game.social.BadgeInventoryPayload;
import com.alphaseries.game.social.RoomUserStatusPayloads;
import com.alphaseries.game.social.SocialLookups;
import com.alphaseries.game.social.SocialRoomOccupants;
import com.alphaseries.game.social.SocialWire;
import com.alphaseries.game.trade.TradeConfirmation;
import com.alphaseries.game.trade.TradeInteractionCloseAction;
import com.alphaseries.game.trade.TradeInteractionRequestAction;
import com.alphaseries.game.trade.TradeInteractionStateAction;
import com.alphaseries.game.trade.TradeLookups;
import com.alphaseries.game.trade.TradeOfferAction;
import com.alphaseries.game.trade.TradeState;
import com.alphaseries.game.trade.TradeWire;
import com.alphaseries.game.user.AvatarNameUpdate;
import com.alphaseries.game.user.UserActivityPoints;
import com.alphaseries.game.user.UserEffectActivation;
import com.alphaseries.game.user.UserEffectExpiry;
import com.alphaseries.game.user.UserRefreshService;
import com.alphaseries.game.user.UserGroupRow;
import com.alphaseries.game.user.UserLookups;
import com.alphaseries.game.user.UserValidation;
import com.alphaseries.game.user.UserWire;
import com.alphaseries.game.inventory.InventoryMessagePayloads;
import com.alphaseries.server.logging.Console;
import com.alphaseries.server.lifecycle.LifecycleState;
import com.alphaseries.server.mus.MusConnectionManager;
import com.alphaseries.server.runtime.Guardian;
import com.alphaseries.game.achievement.AchievementLookups;
import com.alphaseries.game.achievement.AchievementRewardGrant;
import com.alphaseries.game.achievement.AchievementSettings;
import com.alphaseries.game.achievement.AchievementState;
import com.alphaseries.game.catalog.CatalogPages;
import com.alphaseries.game.catalog.CatalogRegistry;
import com.alphaseries.game.catalog.CatalogState;
import com.alphaseries.game.catalog.CatalogWire;
import com.alphaseries.game.catalog.ClubPeriodService;
import com.alphaseries.game.catalog.GiftSettings;
import com.alphaseries.game.catalog.VoucherRedemption;
import com.alphaseries.game.catalog.VoucherWire;
import com.alphaseries.game.chat.ChatCommands;
import com.alphaseries.game.chat.ChatLookups;
import com.alphaseries.game.help.HelpCenterCache;
import com.alphaseries.game.help.HelpCenterState;
import com.alphaseries.game.help.HelpWire;
import com.alphaseries.game.messenger.AcceptedFriendRequests;
import com.alphaseries.game.messenger.MessengerFriend;
import com.alphaseries.game.messenger.MessengerFriendList;
import com.alphaseries.game.messenger.MessengerNotification;
import com.alphaseries.game.messenger.MessengerFriendRequest;
import com.alphaseries.game.messenger.MessengerPrivateMessage;
import com.alphaseries.game.messenger.MessengerRoomInvite;
import com.alphaseries.game.messenger.MessengerSettings;
import com.alphaseries.game.messenger.MessengerLookups;
import com.alphaseries.game.messenger.MessengerState;
import com.alphaseries.game.messenger.MessengerViews;
import com.alphaseries.game.messenger.MessengerWire;
import com.alphaseries.game.messenger.PendingFriendRequest;
import com.alphaseries.game.messenger.RemovedFriendships;
import com.alphaseries.game.navigator.NavigatorRequests;
import com.alphaseries.game.navigator.NavigatorState;
import com.alphaseries.game.navigator.NavigatorWire;
import com.alphaseries.game.navigator.RecommendedRooms;
import com.alphaseries.game.navigator.RoomCategoryCache;
import com.alphaseries.game.moderation.StaffCallForHelpRow;
import com.alphaseries.game.moderation.ModerationState;
import com.alphaseries.game.moderation.StaffModerationLookups;
import com.alphaseries.game.moderation.StaffPayloads;
import com.alphaseries.game.moderation.StaffModerationPacketHandlers;
import com.alphaseries.game.moderation.StaffSettings;
import com.alphaseries.game.moderation.StaffUserLookup;
import com.alphaseries.game.moderation.StaffUserSummaryRow;
import com.alphaseries.game.moderation.StaffWire;
import com.alphaseries.game.recycler.RecyclerRewards;
import com.alphaseries.game.recycler.RecyclerLookups;
import com.alphaseries.game.recycler.RecyclerSettings;
import com.alphaseries.game.recycler.RecyclerState;
import com.alphaseries.game.recycler.RecyclerWire;
import com.alphaseries.game.wired.WiredLookups;
import com.alphaseries.game.wired.WiredSettings;
import com.alphaseries.game.wired.WiredState;
import com.alphaseries.messages.outgoing.CatalogPayloads;
import com.alphaseries.messages.outgoing.ClubPayloads;
import com.alphaseries.messages.outgoing.FurniturePayloads;
import com.alphaseries.messages.outgoing.HelpPayloads;
import com.alphaseries.messages.outgoing.MessengerPayloads;
import com.alphaseries.messages.outgoing.QuestPayloads;
import com.alphaseries.messages.outgoing.RoomPayloads;
import com.alphaseries.messages.outgoing.UserPayloads;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.protocol.WireEncoding;
import com.alphaseries.util.FileUtils;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class Handling {
    private Handling() {
    }

    /**
     * Original function: Proc_6_0_6D7FF0.
     */
    public static void sendStaffUserSummary(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            StaffWire.UserSummaryRequest request = StaffWire.userSummaryRequest(packetPayload);
            long targetUserId = request.targetUserId();
            if (targetUserId <= 0L) {
                return;
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId) || !UserLookups.hasPermission(callerUserId, "fuse_mod", userDao(), AppConfigState.instance().permissionMatrix())) {
                return;
            }
            StaffModerationDao moderationDao = staffModerationDao();
            if (moderationDao == null) {
                return;
            }
            StaffModerationDao.UserModerationSummary summary = moderationDao.userModerationSummary(targetUserId).orElse(null);
            if (summary == null) {
                return;
            }
            String payload = StaffPayloads.userSummary(summary.userRow(),
                summary.callForHelpCount(),
                summary.pickedCallForHelpCount(),
                summary.cautionCount(),
                summary.banCount());
            if (!payload.isEmpty()) {
                sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_1_6D8B70.
     */
    public static void sendStaffCaution(int socketIndex, String packetPrefix, String packetPayload) {
        staffDirectMessage(socketIndex, packetPrefix, packetPayload, "fuse_alert", "4", false, false);
    }

    /**
     * Original function: Proc_6_2_6D9880.
     */
    public static void staffKickUser(int socketIndex, String packetPrefix, String packetPayload) {
        staffDirectMessage(socketIndex, packetPrefix, packetPayload, "fuse_kick", "5", true, false);
    }

    /**
     * Original function: Proc_6_3_6DA490.
     */
    public static void staffBanUser(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            StaffWire.BanRequest request = StaffWire.banRequest(packetPayload);
            long targetUserId = request.targetUserId();
            String banMessage = request.banMessage();
            long banHours = request.banHours();
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (targetUserId <= 0L || banMessage.isEmpty() || banHours <= 0L
                || callerUserId.isEmpty() || "0".equals(callerUserId)
                || !UserLookups.hasPermission(callerUserId, "fuse_mod", userDao(), AppConfigState.instance().permissionMatrix())
                || !UserLookups.hasPermission(callerUserId, "fuse_alert", userDao(), AppConfigState.instance().permissionMatrix())
                || StaffPayloads.containsUnsafeAlert(banMessage)) {
                return;
            }
            long currentRoomId = handlingCurrentRoomId(socketIndex, callerUserId);
            long banSeconds = banHours * 60L * 60L;
            StaffModerationDao moderationDao = staffModerationDao();
            if (moderationDao != null) {
                String targetIpAddress = moderationDao.userLastIpAddress(targetUserId);
                moderationDao.insertModerationBanLog(
                    NumberUtils.parseLong(callerUserId),
                    targetUserId,
                    currentRoomId,
                    banMessage,
                    socketIndex);
                moderationDao.insertUserBan(
                    targetUserId,
                    NumberUtils.parseLong(callerUserId),
                    banMessage,
                    banSeconds,
                    targetIpAddress);
                moderationDao.clearUserLoginSession(targetUserId);
            }
            int targetSocketIndex = handlingSocketFromUserId(String.valueOf(targetUserId));
            if (targetSocketIndex > 0) {
                sendToSocket(targetSocketIndex, "@c" + banMessage + '\2');
                disconnectSocket(targetSocketIndex);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_4_6DAFB0.
     */
    public static long moderateCurrentRoom(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            StaffWire.RoomModerationRequest request = StaffWire.roomModerationRequest(packetPayload);
            long actionType = request.actionType();
            String messageText = request.messageText();
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId) || !UserLookups.hasPermission(callerUserId, "fuse_mod", userDao(), AppConfigState.instance().permissionMatrix())) {
                return 0L;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || actionType <= 0L || messageText.isEmpty() || StaffPayloads.containsUnsafeAlert(messageText)) {
                return 0L;
            }
            StaffModerationDao moderationDao = staffModerationDao();
            if (moderationDao == null) {
                return 0L;
            }
            StaffModerationDao.RoomModerationTarget moderationTarget = moderationDao.roomModerationTarget(roomId).orElse(null);
            if (moderationTarget == null || moderationTarget.ownerUserId() <= 0L) {
                return 0L;
            }
            long logType = actionType == 1L ? 1L : 2L;
            moderationDao.insertRoomModerationLog(logType, NumberUtils.parseLong(callerUserId), roomId, messageText, socketIndex);
            broadcastToRoomUsers(roomId, StaffPayloads.alert(messageText));
            if (actionType == 1L || actionType == 4L) {
                moderationDao.deleteRoomEvent(roomId);
                RoomRefreshService.sendRoomReadyRefreshes(roomId);
            }
            if (actionType == 1L) {
                moderationDao.insertUserCaution(
                    moderationTarget.ownerUserId(),
                    NumberUtils.parseLong(callerUserId),
                    messageText + " (Room caution of room id: " + roomId + ")");
            }
            return actionType;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_5_6DC340.
     */
    public static void sendCallForHelpReview(long callForHelpId, int socketIndex) {
        try {
            if (callForHelpId <= 0L) {
                return;
            }
            StaffModerationDao moderationDao = staffModerationDao();
            if (moderationDao == null) {
                return;
            }
            StaffModerationDao.OpenCallForHelpReviewRow reviewRow = moderationDao.openCallForHelpReview(callForHelpId)
                .orElse(null);
            if (reviewRow == null) {
                return;
            }
            String payload = StaffPayloads.callForHelpNotification(reviewRow.toPayloadRow(), null);
            if (socketIndex > 0) {
                sendToSocket(socketIndex, payload);
            } else {
                broadcastToStaffModerators(payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_6_6DC9D0.
     */
    public static void moveCallForHelpToPickedTab(int socketIndex, String packetPrefix, String packetPayload) {
        updateCallForHelpTab(socketIndex, packetPrefix, packetPayload, "2");
    }

    /**
     * Original function: Proc_6_7_6DD0E0.
     */
    public static void closeCallForHelp(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            StaffWire.CloseCallForHelpRequest request = StaffWire.closeCallForHelpRequest(packetPayload);
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)
                || !UserLookups.hasPermission(callerUserId, "fuse_mod", userDao(), AppConfigState.instance().permissionMatrix())
                || !UserLookups.hasPermission(callerUserId, "fuse_receive_calls_for_help", userDao(), AppConfigState.instance().permissionMatrix())) {
                return;
            }
            long closeState = request.closeState();
            if (closeState < 1L || closeState > 3L) {
                return;
            }
            long callForHelpId = request.callForHelpId();
            if (callForHelpId <= 0L) {
                return;
            }
            StaffModerationDao moderationDao = staffModerationDao();
            if (moderationDao == null) {
                return;
            }
            String reporterUserId = String.valueOf(moderationDao.callForHelpReporterUserId(callForHelpId));
            int reporterSocketIndex = handlingSocketFromUserId(reporterUserId);
            if (reporterSocketIndex > 0) {
                sendToSocket(reporterSocketIndex, StaffPayloads.callForHelpClosed(closeState));
            }
            moderationDao.closeCallForHelp(callForHelpId, closeState);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_8_6DD790.
     */
    public static void moveCallForHelpToOpenTab(int socketIndex, String packetPrefix, String packetPayload) {
        updateCallForHelpTab(socketIndex, packetPrefix, packetPayload, "1");
    }

    /**
     * Original function: Proc_6_9_6DDD70.
     */
    public static void lockCurrentRoomForModeration(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            StaffWire.RoomLockRequest request = StaffWire.roomLockRequest(packetPayload);
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId) || !UserLookups.hasPermission(callerUserId, "fuse_mod", userDao(), AppConfigState.instance().permissionMatrix())) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            long lockFlag = request.lockFlag();
            if (roomId > 0L && lockFlag == 1L) {
                StaffModerationDao moderationDao = staffModerationDao();
                if (moderationDao != null) {
                    moderationDao.lockRoomForModeration(roomId);
                }
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_10_6DE1D0.
     */
    public static void sendStaffRoomChatHistory(int socketIndex, String packetPrefix, String packetPayload) {
        staffRoomHistory(socketIndex, packetPrefix, packetPayload, true);
    }

    /**
     * Original function: Proc_6_11_6DF4A0.
     */
    public static void sendStaffRoomVisitHistory(int socketIndex, String packetPrefix, String packetPayload) {
        staffRoomHistory(socketIndex, packetPrefix, packetPayload, false);
    }

    /**
     * Original function: Proc_6_12_6DFE90.
     */
    public static void sendStaffAlert(int socketIndex, String packetPrefix, String packetPayload) {
        staffDirectMessage(socketIndex, packetPrefix, packetPayload, "fuse_alert", "3", false, true);
    }

    /**
     * Original function: Proc_6_13_6E0A80.
     */
    public static long waveCurrentRoomUser(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            long roomUserIndex = representedRoomUserIndex(socketIndex, userId);
            SocialLookups.RoomUserAction action = SocialLookups.roomUserWaveAction(userId, roomId, roomUserIndex);
            if (!action.valid()) {
                return 0L;
            }
            broadcastToCurrentRoom(socketIndex, action.payload());
            return action.resultValue();
        } catch (Exception ignored) {
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_14_6E10C0.
     */
    public static long danceCurrentRoomUser(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            long roomUserIndex = representedRoomUserIndex(socketIndex, userId);
            SocialLookups.RoomUserAction action = SocialLookups.roomUserDanceAction(
                userId, roomId, roomUserIndex, SocialWire.danceRequest(packetPayload));
            if (!action.valid()) {
                return 0L;
            }
            broadcastToCurrentRoom(socketIndex, action.payload());
            return action.resultValue();
        } catch (Exception ignored) {
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_15_6E1900.
     */
    public static void sendWardrobeSlots(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            String payload = UserLookups.wardrobeSlotsPayload(
                userId, userDao(), AppConfigState.instance().permissionMatrix());
            if (payload.isEmpty()) {
                return;
            }
            sendToSocket(socketIndex, payload);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_16_6E2320.
     */
    public static void saveWardrobeSlot(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            String figureData = FileUtils.readTextFile(AppPaths.applicationPath() + "/figuredata.cache");
            String payload = UserLookups.saveWardrobeSlotPayload(
                userId, packetPayload, figureData, userDao(), AppConfigState.instance().permissionMatrix());
            if (!payload.isEmpty()) {
                sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_17_6E48D0.
     */
    public static void updateTutorialClothes(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            String figureData = FileUtils.readTextFile(AppPaths.applicationPath() + "/figuredata.cache");
            String payload = UserLookups.updateTutorialClothesPayload(
                userId, packetPayload, figureData, userDao());
            if (!payload.isEmpty()) {
                sendToSocket(socketIndex, payload);
                broadcastToCurrentRoom(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_18_6E7480.
     */
    public static void sendClubSubscriptionOffers(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (socketIndex <= 0 || userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            ClubDao clubDao = clubDao();
            if (clubDao == null) {
                return;
            }
            ClubDao.UserClubStatus status = clubDao.userClubStatus(NumberUtils.parseLong(userId))
                .orElse(new ClubDao.UserClubStatus(0L, 0L, 0L, 0L, 0L, 0L, 0L));
            sendToSocket(socketIndex, ClubPayloads.subscriptionOffers(clubDao.clubProductRows(), status));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_19_6E8040.
     */
    public static String sendCachedRecyclerStatus(int socketIndex, String cachedPayload, String packetPrefix) {
        try {
            if (socketIndex <= 0) {
                return "";
            }
            cachedPayload = StringUtils.text(cachedPayload);
            packetPrefix = StringUtils.text(packetPrefix);
            if (packetPrefix.isEmpty()) {
                packetPrefix = "Gz";
            }
            if (cachedPayload.isEmpty()) {
                cachedPayload = recyclerSettings().statusPayload();
            }
            String payload = packetPrefix + cachedPayload;
            sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_20_6E88E0.
     */
    public static void sendRankAndStaffState(int socketIndex) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            String payload = UserLookups.rankAndStaffStatePayload(
                userId, userDao(), AppConfigState.instance().permissionMatrix());
            if (payload.isEmpty()) {
                return;
            }
            sendToSocket(socketIndex, payload);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_24_6EA010.
     */
    public static String routeChatMessage(int socketIndex, String packetPrefix, String packetPayload) {
        return handlingRepresentedChatRoute(socketIndex, packetPayload, 0L);
    }

    /**
     * Original function: Proc_6_25_6EEAC0.
     */
    public static String routeChatCommand(int socketIndex, String packetPayload) {
        return handlingRepresentedChatRoute(socketIndex, packetPayload, 0L);
    }

    /**
     * Original function: Proc_6_26_7034C0.
     */
    public static String chatInCurrentRoom(int socketIndex, String packetPrefix, String packetPayload) {
        return handlingRepresentedChatRoute(socketIndex, packetPayload, 0L);
    }

    /**
     * Original function: Proc_6_27_706920.
     */
    public static String shoutInCurrentRoom(int socketIndex, String packetPrefix, String packetPayload) {
        return handlingRepresentedChatRoute(socketIndex, packetPayload, 1L);
    }

    /**
     * Original function: Proc_6_28_709DA0.
     */
    public static String whisperInCurrentRoom(int socketIndex, String packetPrefix, String packetPayload) {
        return handlingRepresentedChatRoute(socketIndex, packetPayload, 2L);
    }

    /**
     * Original function: Proc_6_53_718E00.
     */
    public static void sendRoomReady(int socketIndex) {
        if (socketIndex <= 0) {
            return;
        }
        MusConnectionManager.instance().sendData(socketIndex, "@R");
    }

    public record CatalogGrantResult(List<Long> furnitureIds) {
        public static CatalogGrantResult empty() {
            return new CatalogGrantResult(List.of());
        }

        public CatalogGrantResult {
            furnitureIds = furnitureIds == null ? List.of() : List.copyOf(furnitureIds);
        }

        public boolean isEmpty() {
            return furnitureIds.isEmpty();
        }

        public long firstFurnitureId() {
            return furnitureIds.isEmpty() ? 0L : NumberUtils.parseLong(furnitureIds.get(0));
        }

    }

    /**
     * Original function: Proc_6_30_70DC90.
     */
    public static void cancelLatestCallForHelp(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty()) {
                return;
            }
            StaffModerationDao moderationDao = staffModerationDao();
            if (moderationDao == null) {
                return;
            }
            long callForHelpId = moderationDao.latestOpenCallForHelpId(NumberUtils.parseLong(userId));
            if (callForHelpId > 0L) {
                moderationDao.deleteCallForHelp(callForHelpId);
                sendToSocket(socketIndex, StaffPayloads.callForHelpDeleted());
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_31_70DE80.
     */
    public static void openStaffModerationPanel(int socketIndex) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long rankIndex = UserLookups.rank(userId, userDao());
            long hcLevel = UserLookups.hcLevel(userId, userDao());
            if (!AppConfigState.instance().permissionMatrix().allows(rankIndex, "", "fuse_mod", hcLevel)) {
                return;
            }
            String payload = StaffPayloads.moderationPanel(staffSettings(), rankIndex, hcLevel);
            sendToSocket(socketIndex, payload);

            StaffModerationDao moderationDao = staffModerationDao();
            if (moderationDao == null) {
                return;
            }
            for (StaffCallForHelpRow row : moderationDao.openStaffCallRows()) {
                sendToSocket(socketIndex,
                    StaffPayloads.callForHelpNotification(row, null));
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_32_70EAB0.
     */
    public static void submitCallForHelp(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            StaffWire.SubmitCallForHelpRequest request = StaffWire.submitCallForHelpRequest(packetPayload);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            StaffModerationDao moderationDao = staffModerationDao();
            if (moderationDao == null) {
                return;
            }
            java.util.Optional<Long> lastClosedState = moderationDao.recentCallForHelpClosedState(NumberUtils.parseLong(userId));
            if (lastClosedState.isPresent() && lastClosedState.get() == 0L) {
                return;
            }
            String descriptionText = request.descriptionText();
            if (descriptionText.length() < 30) {
                return;
            }
            long categoryId = request.categoryId();
            long partnerUserId = request.partnerUserId();
            if (partnerUserId == NumberUtils.parseLong(userId)) {
                partnerUserId = 0L;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            moderationDao.insertCallForHelp(NumberUtils.parseLong(userId), roomId, categoryId, partnerUserId, descriptionText);
            long callForHelpId = moderationDao.newestCallForHelpId();
            sendToSocket(socketIndex, StaffPayloads.callForHelpCreated(callForHelpId));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_33_70F4F0.
     */
    public static void sendImportantFaqs(int socketIndex) {
        try {
            sendToSocket(socketIndex, HelpPayloads.importantFaqs(helpCenterCache()));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_34_70F590.
     */
    public static void sendFaqCategories(int socketIndex) {
        try {
            sendToSocket(socketIndex, HelpPayloads.categories(helpCenterCache()));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_35_70F630.
     */
    public static void sendCategoryFaqs(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            HelpWire.CategoryFaqRequest request = HelpWire.categoryFaqRequest(packetPayload, packetPrefix);
            long categoryId = request.categoryId();
            sendToSocket(socketIndex, HelpPayloads.categoryFaqs(helpCenterCache(), categoryId));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_36_70F7B0.
     */
    public static void searchFaqs(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            HelpWire.FaqSearchRequest request = HelpWire.faqSearchRequest(packetPayload, packetPrefix);
            String searchText = request.searchText();
            if (searchText.length() < 3) {
                disconnectSocket(socketIndex);
                return;
            }
            HelpDao helpDao = helpDao();
            if (helpDao == null) {
                return;
            }
            List<HelpDao.FaqNameRow> rows = helpDao.searchFaqs(searchText);
            sendToSocket(socketIndex, HelpPayloads.searchResults(rows));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_37_70FC20.
     */
    public static void sendFaqDescription(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            HelpWire.FaqIdRequest request = HelpWire.faqIdRequest(packetPayload, packetPrefix);
            long faqId = request.faqId();
            sendToSocket(socketIndex, HelpPayloads.description(helpCenterCache(), faqId));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_38_70FD10.
     */
    public static long changeAvatarName(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            UserWire.AvatarNameRequest request = UserWire.avatarNameRequest(packetPayload, packetPrefix);
            return validateOrChangeAvatarName(socketIndex, false, request.candidateName());
        } catch (Exception ignored) {
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_39_711650.
     */
    public static long checkAvatarName(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            UserWire.AvatarNameRequest request = UserWire.avatarNameRequest(packetPayload, packetPrefix);
            return validateOrChangeAvatarName(socketIndex, true, request.candidateName());
        } catch (Exception ignored) {
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_40_711770.
     */
    public static long validateOrChangeAvatarName(int socketIndex, boolean checkOnly, String candidateName) {
        try {
            candidateName = StringUtils.text(candidateName).trim();
            if (socketIndex <= 0) {
                return 0L;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            long numericUserId = NumberUtils.parseLong(userId);
            AvatarNameUpdate update = UserLookups.validateOrChangeAvatarName(
                userId, socketIndex, checkOnly, candidateName, userDao());
            if (update.validationPayload().isEmpty()) {
                return 0L;
            }
            sendToSocket(socketIndex, update.validationPayload());
            long roomId = update.changed() ? handlingCurrentRoomId(socketIndex, userId) : 0L;
            if (roomId > 0L) {
                long roomUserIndex = representedRoomUserIndex(socketIndex, userId);
                broadcastToCurrentRoom(socketIndex,
                    UserPayloads.roomUserNameChanged(numericUserId, roomUserIndex, update.candidateName()));
                broadcastToCurrentRoom(socketIndex, NavigatorRequests.roomListPayload(roomId, roomDao()));
                broadcastToCurrentRoom(socketIndex, RoomPayloads.entryUpdated(roomId));
            }
            return update.validationCode();
        } catch (Exception ignored) {
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_43_713680.
     */
    public static void sendRoomSettings(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            RoomWire.RoomSettingsReadRequest request = RoomWire.roomSettingsReadRequest(packetPayload);
            long requestedRoomId = request.requestedRoomId();
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = requestedRoomId > 0L ? requestedRoomId : handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L) {
                return;
            }
            if (!RoomLookups.userOwnsRoom(callerUserId, roomId, roomDao()) && !UserLookups.hasPermission(callerUserId, "fuse_any_room_controller", userDao(), AppConfigState.instance().permissionMatrix())) {
                return;
            }
            String payload = RoomLookups.roomSettingsPayload(roomId, roomDao());
            if (!payload.isEmpty()) {
                sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_44_7145E0.
     */
    public static void updateRoomIcon(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            RoomLookups.RoomIconUpdate iconUpdate =
                RoomLookups.updateRoomIcon(roomId, RoomWire.roomIconRequest(packetPayload), roomDao());
            if (!iconUpdate.valid()) {
                return;
            }
            broadcastToCurrentRoom(socketIndex, NavigatorRequests.roomListPayload(roomId, roomDao()));
            sendToSocket(socketIndex, iconUpdate.iconUpdatedPayload());
            sendToSocket(socketIndex, iconUpdate.entryUpdatedPayload());
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_45_714B60.
     */
    public static void deleteRoomEvent(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            RoomLookups.RoomEventChange change = RoomLookups.deleteRoomEvent(roomId, roomDao());
            if (change.hasDirectPayload()) {
                sendToSocket(socketIndex, change.directPayload());
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_46_714D50.
     */
    public static void sendRoomDoorStatus(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            sendToSocket(socketIndex, RoomLookups.doorStatusPayload(roomId, roomDao()));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_47_714F60.
     */
    public static void setHomeRoom(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            RoomWire.RoomIdRequest request = RoomWire.roomIdRequest(packetPayload, StringUtils.text(packetPrefix));
            long roomId = request.roomId();
            if (roomId <= 0L) {
                return;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            String payload = RoomLookups.setHomeRoomPayload(userId, roomId, userDao());
            if (!payload.isEmpty()) {
                sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_48_7151E0.
     */
    public static void createRoomEvent(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            String timeFormat = AppConfigState.instance().settingsCache().valueOrDefault("com.mysql.format.time", "%H:%i");
            RoomLookups.RoomEventChange change = RoomLookups.createRoomEvent(
                userId, roomId, RoomWire.roomEventCreatePayloadFromWire(packetPayload), timeFormat, roomDao());
            if (change.hasDirectPayload()) {
                sendToSocket(socketIndex, change.directPayload());
            }
            if (change.hasBroadcastPayload()) {
                broadcastToCurrentRoom(socketIndex, change.broadcastPayload());
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_49_715D30.
     */
    public static void editRoomEvent(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            String timeFormat = AppConfigState.instance().settingsCache().valueOrDefault("com.mysql.format.time", "%H:%i");
            RoomLookups.RoomEventChange change = RoomLookups.editRoomEvent(
                userId, roomId, RoomWire.roomEventEditPayloadFromWire(packetPayload), timeFormat, roomDao());
            if (change.hasDirectPayload()) {
                sendToSocket(socketIndex, change.directPayload());
            }
            if (change.hasBroadcastPayload()) {
                broadcastToCurrentRoom(socketIndex, change.broadcastPayload());
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_50_7166B0.
     */
    public static void followUserToRoom(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            SocialLookups.FollowRoomAction action =
                SocialLookups.followRoomAction(SocialWire.followUserRequest(packetPayload), userDao());
            if (action.hasFailurePayload()) {
                sendToSocket(socketIndex, action.failurePayload());
                return;
            }
            if (action.canEnterRoom()) {
                enterRoom(socketIndex, action.roomId(), "");
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_52_7172B0.
     */
    public static void updateRoomSettings(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            if (!RoomLookups.userOwnsRoom(userId, roomId, roomDao()) && !UserLookups.hasPermission(userId, "fuse_any_room_controller", userDao(), AppConfigState.instance().permissionMatrix())) {
                return;
            }
            RoomLookups.RoomSettingsUpdate update = RoomLookups.updateRoomSettings(
                roomId,
                RoomWire.roomSettingsFromWire(packetPayload),
                UserLookups.rank(userId, userDao()),
                UserLookups.hcLevel(userId, userDao()),
                UserLookups.hasPermission(userId, "fuse_hide_room_walls", userDao(), AppConfigState.instance().permissionMatrix()),
                roomDao());
            if (!update.valid()) {
                return;
            }
            broadcastToCurrentRoom(socketIndex, NavigatorRequests.roomListPayload(roomId, roomDao()));
            sendToSocket(socketIndex, update.settingsUpdatedPayload());
            sendToSocket(socketIndex, update.entryUpdatedPayload());
            sendToSocket(socketIndex, update.wallOptionsPayload());
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_51_716AC0.
     */
    public static String roomEventInfoPayload(long roomId) {
        try {
            if (roomId <= 0L) {
                return "-1" + '\2';
            }
            String timeFormat = AppConfigState.instance().settingsCache().valueOrDefault("com.mysql.format.time", "%H:%i");
            RoomDao rooms = roomDao();
            return RoomLookups.eventInfoPayload(roomId, timeFormat, rooms);
        } catch (Exception ignored) {
            return "-1" + '\2';
        }
    }

    /**
     * Original function: Proc_6_54_719050.
     */
    public static long enterRepresentedRoom(int socketIndex, long roomId, long preferredSlot) {
        long reservedSlot = 0L;
        try {
            if (socketIndex <= 0 || roomId <= 0L) {
                return 0L;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            if (handlingCurrentRoomId(socketIndex, userId) > 0L) {
                leaveCurrentRoom(socketIndex);
            }
            RoomState.instance().representedRoomSlots();
            reservedSlot = RoomState.instance().reserveRepresentedRoomSlot(preferredSlot);
            RoomState.instance().setRepresentedRoomSlots(RoomState.instance().representedRoomSlots());
            if (reservedSlot <= 0L) {
                sendToSocket(socketIndex, UserPayloads.errorCode(1, 0));
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
            sendRoomReady(socketIndex);
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
    public static long leaveCurrentRoom(int socketIndex) {
        try {
            if (socketIndex <= 0) {
                return 0L;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            RoomDao rooms = roomDao();
            if (rooms == null) {
                sendToSocket(socketIndex, "J|H");
                return 0L;
            }
            long userIdValue = NumberUtils.parseLong(userId);
            RoomDao.ActiveRoomVisit visit = rooms.activeVisitWithRoomSlot(userIdValue).orElse(null);
            if (visit == null) {
                sendToSocket(socketIndex, "J|H");
                return 0L;
            }
            long visitId = visit.visitId();
            long roomId = visit.roomId();
            long slotId = visit.slotId();
            if (roomId <= 0L) {
                return 0L;
            }
            long roomUserIndex = representedRoomUserIndex(socketIndex, userId);
            if (roomUserIndex > 0L) {
                broadcastToCurrentRoom(socketIndex, SocialLookups.roomUserRemovedPayload(roomUserIndex));
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
            sendToSocket(socketIndex, "J|H");
            return roomId;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_56_71E730.
     */
    public static void sendRoomEntryBootstrap(int socketIndex, long roomMode) {
        try {
            sendToSocket(socketIndex, "@S");
            sendToSocket(socketIndex, "Bf/client.php" + '\2');
            sendToSocket(socketIndex, roomMode == 0L ? "@i" : "@{");
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
                sendToSocket(socketIndex, "C`H");
                return 0L;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            RoomDao rooms = roomDao();
            if (rooms == null) {
                sendToSocket(socketIndex, "C`H");
                return 0L;
            }
            RoomDao.RoomEntryState entryState = rooms.roomEntryState(roomId).orElse(null);
            if (entryState == null) {
                sendToSocket(socketIndex, "C`H");
                return 0L;
            }
            long userIdValue = NumberUtils.parseLong(userId);
            boolean isOwner = entryState.ownerUserId() == userIdValue;
            if (!isOwner) {
                if (rooms.userBannedFromRoom(userIdValue, roomId)) {
                    sendRoomReady(socketIndex);
                    sendToSocket(socketIndex, "C`PA");
                    return 0L;
                }
                if (entryState.visitorsMax() > 0L && entryState.visitorsNow() >= entryState.visitorsMax()
                    && !UserLookups.hasPermission(userId, "fuse_enter_full_rooms", userDao(), AppConfigState.instance().permissionMatrix())) {
                    sendRoomReady(socketIndex);
                    sendToSocket(socketIndex, "C`I");
                    return 0L;
                }
                if (entryState.doorStatus() == 1L && !UserLookups.hasPermission(userId, "fuse_enter_locked_rooms", userDao(), AppConfigState.instance().permissionMatrix())) {
                    sendRoomReady(socketIndex);
                    sendToSocket(socketIndex, "C`H");
                    return 0L;
                }
                if (entryState.doorStatus() == 2L && !StringUtils.text(entryState.password()).equals(roomPassword)) {
                    sendRoomReady(socketIndex);
                    sendToSocket(socketIndex, "@afhFF");
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
    public static long enterRoomFromPayload(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            RoomWire.RoomEntryRequest request = RoomWire.roomEntryRequest(packetPayload);
            RoomState.instance().representedRoomSlots();
            RoomState.instance().ensureRepresentedRoomSlotPool();
            RoomState.instance().setRepresentedRoomSlots(RoomState.instance().representedRoomSlots());
            if (RoomState.instance().representedRoomSlots().isEmpty()) {
                sendToSocket(socketIndex, UserPayloads.errorCode(1, 0));
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
     * Original function: Proc_6_59_71FEE0.
     */
    public static void sendVisitRoomAdvertisement(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String advertisementPayload = "\2\2";
            VisitRoomAds visitRoomAds = visitRoomAds();
            if (visitRoomAds.count() > 0L) {
                String candidate = visitRoomAds.randomPayload();
                if (!candidate.isEmpty()) {
                    advertisementPayload = candidate;
                }
            }
            sendToSocket(socketIndex, "DB" + advertisementPayload);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_60_720060.
     */
    public static void sendSingleRoomNavigatorInfo(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            NavigatorWire.SingleRoomRequest request =
                NavigatorWire.singleRoomRequest(packetPayload);
            long requestMode = request.requestMode();
            long detailFlag = request.detailFlag();
            if (detailFlag == 1L) {
                long roomId = request.roomId();
                if (roomId <= 0L) {
                    return;
                }
                String payload = NavigatorRequests.singleRoomResponsePayload(roomId, roomDao());
                if (!payload.isEmpty()) {
                    sendToSocket(socketIndex, payload);
                }
            } else if (requestMode > 0L) {
                // VB6 reads a room id from packed session offsets here; those offsets are not represented yet.
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_61_720490.
     */
    public static void kickRoomUser(int socketIndex, String packetPrefix, String packetPayload) {
        roomKickOrBanUser(socketIndex, packetPrefix, packetPayload, false);
    }

    /**
     * Original function: Proc_6_62_7209F0.
     */
    public static void banRoomUser(int socketIndex, String packetPrefix, String packetPayload) {
        roomKickOrBanUser(socketIndex, packetPrefix, packetPayload, true);
    }

    /**
     * Original function: Proc_6_63_721050.
     */
    public static void rateCurrentRoom(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            RoomWire.RoomRatingRequest request = RoomWire.roomRatingRequest(packetPayload);
            long voteValue = request.voteValue();
            if (voteValue != 1L) {
                return;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            String payload = RoomLookups.rateRoomPayload(userId, roomId, voteValue, roomDao());
            if (!payload.isEmpty()) {
                sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_64_721650.
     */
    public static void revokeRoomRightByName(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            RoomWire.RoomRightNameRequest request =
                RoomWire.roomRightNameRequest(packetPayload, packetPrefix);
            String targetName = request.targetName();
            if (targetName.isEmpty()) {
                return;
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !RoomLookups.userHasRoomRight(callerUserId, roomId, roomDao())) {
                return;
            }
            String payload = RoomLookups.revokeRoomRightByNamePayload(targetName, roomId, userDao(), roomDao());
            if (!payload.isEmpty()) {
                sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_65_721A10.
     */
    public static void grantRoomRight(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            RoomWire.RoomRightGrantRequest request = RoomWire.roomRightGrantRequest(packetPayload);
            String targetUserId = String.valueOf(request.targetUserId());
            if (targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return;
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !RoomLookups.userHasRoomRight(callerUserId, roomId, roomDao())) {
                return;
            }
            int targetSocketIndex = handlingSocketFromUserId(targetUserId);
            if (targetSocketIndex <= 0) {
                return;
            }
            String payload = RoomLookups.grantRoomRightPayload(NumberUtils.parseLong(targetUserId), roomId, roomDao());
            if (!payload.isEmpty()) {
                sendToSocket(targetSocketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_66_721D60.
     */
    public static void updateStickyNote(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            FurnitureWire.StickyNoteUpdate note = FurnitureWire.stickyNoteUpdate(packetPayload);
            if (note.furnitureId() <= 0L) {
                return;
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !RoomLookups.userHasRoomRight(callerUserId, roomId, roomDao())) {
                return;
            }
            String payload = FurnitureLookups.updateStickyNotePayload(
                note, roomId, furnitureDao(), GameDataCaches.productCache());
            if (!payload.isEmpty()) {
                broadcastToCurrentRoom(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_67_722940.
     */
    public static void sendStickyNote(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            long furnitureId = FurnitureWire.stickyFurnitureId(packetPayload);
            if (furnitureId <= 0L) {
                return;
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L) {
                return;
            }
            String payload = FurnitureLookups.stickyNotePayload(
                furnitureId, roomId, furnitureDao(), GameDataCaches.productCache());
            if (!payload.isEmpty()) {
                sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_68_723170.
     */
    public static void deleteStickyNote(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            long furnitureId = FurnitureWire.stickyFurnitureId(packetPayload);
            if (furnitureId <= 0L) {
                return;
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !RoomLookups.userOwnsRoom(callerUserId, roomId, roomDao())) {
                return;
            }
            String payload = FurnitureLookups.deleteStickyNotePayload(
                furnitureId, roomId, furnitureDao(), GameDataCaches.productCache());
            if (!payload.isEmpty()) {
                broadcastToCurrentRoom(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_69_723630.
     */
    public static void openPresent(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            long furnitureId = FurnitureWire.stickyFurnitureId(packetPayload);
            if (furnitureId <= 0L) {
                return;
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !RoomLookups.userHasRoomRight(callerUserId, roomId, roomDao())) {
                return;
            }
            FurnitureLookups.PresentOpenResult result = FurnitureLookups.openPresent(
                furnitureId, roomId, NumberUtils.parseLong(callerUserId), furnitureDao(), GameDataCaches.productCache());
            if (result.valid()) {
                broadcastToCurrentRoom(socketIndex, result.removedPayload());
                sendToSocket(socketIndex, result.responsePayload());
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_70_724190.
     */
    public static void toggleWallFurnitureState(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            long furnitureId = FurnitureWire.stickyFurnitureId(packetPayload);
            if (furnitureId <= 0L) {
                return;
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !RoomLookups.userHasRoomRight(callerUserId, roomId, roomDao())) {
                return;
            }
            String payload = FurnitureLookups.toggleWallFurnitureStatePayload(
                furnitureId, roomId, furnitureDao(), CatalogState.instance().registry(), GameDataCaches.productCache());
            if (!payload.isEmpty()) {
                broadcastToCurrentRoom(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_71_724CF0.
     */
    public static void revokeAllRoomRights(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !RoomLookups.userOwnsRoom(callerUserId, roomId, roomDao())) {
                return;
            }
            RoomLookups.RoomRightSocketRevocation revocation = RoomLookups.revokeAllRoomRights(roomId, roomDao());
            if (!revocation.hasNotifications()) {
                return;
            }
            for (Long activeSocketIndex : revocation.socketIndexes()) {
                int targetSocketIndex = activeSocketIndex == null ? 0 : activeSocketIndex.intValue();
                if (targetSocketIndex > 0) {
                    sendToSocket(targetSocketIndex, revocation.notificationPayload());
                }
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_72_7250D0.
     */
    public static void deleteCurrentRoom(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            RoomWire.DeleteRoomRequest request = RoomWire.deleteRoomRequest(packetPayload);
            if (request.requestFlag() != 0L) {
                return;
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !RoomLookups.userOwnsRoom(callerUserId, roomId, roomDao())) {
                return;
            }
            RoomRefreshService.sendRoomReadyRefreshes(roomId);
            RoomLookups.deleteRoom(roomId, roomDao());
            sendRoomReady(socketIndex);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_73_725540.
     */
    public static void redeemCreditFurniture(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L || (!RoomLookups.userHasRoomRight(userId, roomId, roomDao())
                && !UserLookups.hasPermission(userId, "fuse_pick_up_any_furni", userDao(), AppConfigState.instance().permissionMatrix()))) {
                return;
            }
            FurnitureWire.CreditFurnitureRequest request =
                FurnitureWire.creditFurnitureRequest(packetPayload);
            long furnitureId = request.furnitureId();
            if (furnitureId <= 0L) {
                return;
            }
            long numericUserId = NumberUtils.parseLong(userId);
            FurnitureLookups.CreditFurnitureRedemption redemption = FurnitureLookups.redeemCreditFurniture(
                furnitureId, roomId, numericUserId, furnitureDao(), userDao(), GameDataCaches.productCache());
            if (!redemption.valid()) {
                return;
            }
            sendToSocket(socketIndex, redemption.creditsPayload());
            broadcastToCurrentRoom(socketIndex, redemption.removedPayload());
            RoomCacheFiles.invalidateRoom(roomId);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_74_7265B0.
     */
    public static void revokeRoomRights(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            RoomWire.RoomRightRevokeRequest request = RoomWire.roomRightRevokeRequest(packetPayload);
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !RoomLookups.userHasRoomRight(callerUserId, roomId, roomDao())) {
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
                int targetSocketIndex = handlingSocketFromUserId(String.valueOf(targetUserId));
                if (targetSocketIndex > 0) {
                    sendToSocket(targetSocketIndex, revocation.notificationPayload());
                }
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_75_7269D0.
     */
    public static void revokeRoomRightByTargetName(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            RoomWire.RoomRightNameRequest request =
                RoomWire.roomRightNameRequest(packetPayload, packetPrefix);
            String targetName = request.targetName();
            if (targetName.isEmpty()) {
                return;
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !RoomLookups.userHasRoomRight(callerUserId, roomId, roomDao())) {
                return;
            }
            String payload = RoomLookups.revokeRoomRightByNamePayload(targetName, roomId, userDao(), roomDao());
            if (!payload.isEmpty()) {
                sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_76_726CE0.
     */
    public static String giveRespect(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            SocialWire.UserIdRequest request = SocialWire.userIdRequest(packetPayload, packetPrefix);
            String targetUserId = String.valueOf(request.userId());
            if (targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return "";
            }
            String giverUserId = handlingUserIdFromSocket(socketIndex);
            if (giverUserId.isEmpty() || "0".equals(giverUserId) || giverUserId.equals(targetUserId)) {
                return "";
            }
            int targetSocketIndex = handlingSocketFromUserId(targetUserId);
            if (targetSocketIndex <= 0) {
                return "";
            }
            String payload = SocialLookups.giveRespectPayload(giverUserId, targetUserId, userDao());
            if (payload.isEmpty()) {
                return "";
            }
            advanceAchievementProgress(socketIndex, 3);
            advanceAchievementProgress(targetSocketIndex, 2);
            broadcastToCurrentRoom(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_77_727590.
     */
    public static void sendOfficialRoomModel(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            RoomWire.RoomIdRequest request = RoomWire.roomIdRequest(packetPayload, packetPrefix);
            long roomId = request.roomId();
            if (roomId <= 0L) {
                return;
            }
            String payload = RoomLookups.officialRoomModelPayload(roomId, roomDao());
            if (!payload.isEmpty()) {
                sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_78_7279A0.
     */
    public static void loadCurrentRoomModel(int socketIndex) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (socketIndex <= 0 || userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            RoomLookups.RoomModelLoad modelLoad = RoomLookups.roomModelLoad(roomId, roomDao());
            if (!modelLoad.valid()) {
                return;
            }
            for (String payload : modelLoad.initialPayloads()) {
                sendToSocket(socketIndex, payload);
            }
            sendRoomOccupantList(socketIndex, roomId);
            sendRoomActiveEffects(socketIndex, roomId);
            sendRoomStartupCache(socketIndex, roomId);
            sendRoomModelFurniture(socketIndex, modelLoad.modelId());
            sendRoomWallFurniture(socketIndex, roomId);
            refreshQuestProgress(socketIndex);
            broadcastCurrentRoomUserEntry(socketIndex, roomId);
            sendToSocket(socketIndex, "CP" + '\2' + '\2');
            String pollPromptPayload = PollLookups.promptPayload(userId, roomId, pollDao());
            if (!pollPromptPayload.isEmpty()) {
                sendToSocket(socketIndex, pollPromptPayload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_79_72A430.
     */
    public static void sendCurrentRoomDecoration(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (socketIndex <= 0 || userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            boolean hasControl = RoomLookups.userHasRoomRight(userId, roomId, roomDao())
                || UserLookups.hasPermission(userId, "fuse_any_room_controller", userDao(), AppConfigState.instance().permissionMatrix());
            RoomLookups.RoomPresentationLoad presentationLoad = RoomLookups.roomPresentationLoad(
                userId, roomId, hasControl, roomEventInfoPayload(roomId), roomDao());
            if (!presentationLoad.valid()) {
                return;
            }
            for (String payload : presentationLoad.initialPayloads()) {
                sendToSocket(socketIndex, payload);
            }
            sendRoomOccupantList(socketIndex, roomId);
            sendRoomActiveEffects(socketIndex, roomId);
            sendRoomModelFurniture(socketIndex, presentationLoad.modelId());
            sendRoomStartupCache(socketIndex, roomId);
            sendRoomWallFurniture(socketIndex, roomId);
            refreshQuestProgress(socketIndex);
            broadcastCurrentRoomUserEntry(socketIndex, roomId);
            sendToSocket(socketIndex, "CP" + '\2' + '\2');
            String pollPromptPayload = PollLookups.promptPayload(userId, roomId, pollDao());
            if (!pollPromptPayload.isEmpty()) {
                sendToSocket(socketIndex, pollPromptPayload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_80_72EB60.
     */
    public static void broadcastCurrentRoomUserEntry(int socketIndex, long roomId) {
        try {
            if (socketIndex <= 0) {
                return;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long effectiveRoomId = roomId > 0L ? roomId : handlingCurrentRoomId(socketIndex, userId);
            if (effectiveRoomId <= 0L) {
                return;
            }
            long roomUserIndex = representedRoomUserIndex(socketIndex, userId);
            String payload = SocialLookups.roomUserEntryBroadcastPayload(userId, effectiveRoomId, roomUserIndex, roomDao());
            if (!payload.isEmpty()) {
                broadcastToCurrentRoom(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_81_730010.
     */
    public static void sendRoomOccupantList(int socketIndex, long roomId) {
        try {
            long effectiveRoomId = roomId;
            if (effectiveRoomId <= 0L && socketIndex > 0) {
                String userId = handlingUserIdFromSocket(socketIndex);
                if (!userId.isEmpty() && !"0".equals(userId)) {
                    effectiveRoomId = handlingCurrentRoomId(socketIndex, userId);
                }
            }
            if (socketIndex <= 0 || effectiveRoomId <= 0L) {
                return;
            }
            for (String payload : SocialLookups.roomOccupantListPayloads(
                effectiveRoomId,
                RoomState.instance().representedRooms(),
                roomDao())) {
                sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_82_731070.
     */
    public static void sendRoomActiveEffects(int socketIndex, long roomId) {
        try {
            long effectiveRoomId = roomId;
            if (effectiveRoomId <= 0L && socketIndex > 0) {
                String userId = handlingUserIdFromSocket(socketIndex);
                if (!userId.isEmpty() && !"0".equals(userId)) {
                    effectiveRoomId = handlingCurrentRoomId(socketIndex, userId);
                }
            }
            if (socketIndex <= 0 || effectiveRoomId <= 0L) {
                return;
            }
            for (String payload : SocialLookups.activeRoomEffectPayloads(effectiveRoomId, roomDao())) {
                sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_83_732640.
     */
    public static String sendRoomModelFurniture(int socketIndex, long modelId) {
        try {
            long roomId = 0L;
            if (modelId <= 0L && socketIndex > 0) {
                String userId = handlingUserIdFromSocket(socketIndex);
                if (!userId.isEmpty() && !"0".equals(userId)) {
                    roomId = handlingCurrentRoomId(socketIndex, userId);
                }
            }
            String payload = FurnitureLookups.modelFurniturePayloadForRoom(modelId, roomId, roomDao());
            if (socketIndex > 0) {
                sendToSocket(socketIndex, payload);
            }
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_84_733600.
     */
    public static String sendRoomStartupCache(int socketIndex, long roomId) {
        String payload = WiredLookups.roomStartupCachePayload(0L, wiredSettings());
        try {
            long effectiveRoomId = roomId;
            if (effectiveRoomId <= 0L && socketIndex > 0) {
                String userId = handlingUserIdFromSocket(socketIndex);
                if (!userId.isEmpty() && !"0".equals(userId)) {
                    effectiveRoomId = handlingCurrentRoomId(socketIndex, userId);
                }
            }
            if (socketIndex > 0) {
                sendToSocket(socketIndex, payload);
            }
            return WiredLookups.roomStartupCachePayload(effectiveRoomId, wiredSettings());
        } catch (Exception ignored) {
            return payload;
        }
    }

    /**
     * Original function: Proc_6_85_73A8E0.
     */
    public static String sendRoomWallFurniture(int socketIndex, long roomId) {
        try {
            long effectiveRoomId = roomId;
            if (effectiveRoomId <= 0L && socketIndex > 0) {
                String userId = handlingUserIdFromSocket(socketIndex);
                if (!userId.isEmpty() && !"0".equals(userId)) {
                    effectiveRoomId = handlingCurrentRoomId(socketIndex, userId);
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
                sendToSocket(socketIndex, payload);
            }
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_86_73B0D0.
     */
    public static String sendPetPackagePreview(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            PetWire.PackagePreviewRequest request = PetWire.packagePreviewRequest(packetPayload);
            long furnitureId = request.furnitureId();
            if (socketIndex <= 0 || furnitureId <= 0L) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return "";
            }
            FurnitureDao furniture = furnitureDao();
            PackageDao packages = packageDao();
            if (furniture == null || packages == null) {
                return "";
            }
            String payload = PetLookups.packagePreviewPayload(furnitureId, roomId, furniture, packages);
            if (payload.isEmpty()) {
                return "";
            }
            sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_87_73C120.
     */
    public static String placePetFromPackage(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            PetWire.PackagePlacementRequest request = PetWire.packagePlacementRequest(packetPayload);
            long furnitureId = request.furnitureId();
            String petName = request.petName();
            long validationCode = PetLookups.nameValidationCode(petName);
            if (validationCode > 0L) {
                sendToSocket(socketIndex, PetLookups.packageNameValidationPayload(furnitureId, validationCode, petName));
                return "";
            }
            if (socketIndex <= 0 || furnitureId <= 0L) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return "";
            }
            long numericUserId = NumberUtils.parseLong(userId);
            FurnitureDao furniture = furnitureDao();
            PetPackagePlacement placement = PetLookups.packagePlacementAction(
                furnitureId,
                roomId,
                numericUserId,
                petName,
                validationCode,
                furniture,
                packageDao(),
                botDao(),
                roomDao());
            if (!placement.valid()) {
                return "";
            }
            if (placement.hasInventoryAddPayload()) {
                sendToSocket(socketIndex, placement.inventoryAddPayload());
            }
            RoomState.instance().setFurnitureRoomCache(FurnitureStateWrites.removeMarker(
                RoomState.instance().furnitureRoomCache(), roomId, furnitureId));
            broadcastToCurrentRoom(socketIndex, FurniturePayloads.floorItemRemovedWithState(furnitureId, "H"));
            if (furniture != null) {
                furniture.deleteFurniture(furnitureId);
            }
            sendToSocket(socketIndex, placement.nameValidationPayload());
            return String.valueOf(placement.botId());
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_88_73E4F0.
     */
    public static void sendNewFriendRoom(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String payload = NavigatorRequests.newFriendRoomPayload(null, NavigatorState.instance(), roomDao());
            if (!payload.isEmpty()) {
                sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_89_73EA10.
     */
    public static String confirmTrade(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            if (socketIndex <= 0) {
                return "";
            }
            TradeState tradeState = TradeState.instance();
            int targetSocketIndex = tradeState.interactionPartner(socketIndex);
            if (targetSocketIndex <= 0) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            String targetUserId = handlingUserIdFromSocket(targetSocketIndex);
            if (userId.isEmpty() || "0".equals(userId) || targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return "";
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            String sessionId = UserLookups.sessionId(userId, userDao());
            TradeConfirmation confirmation = TradeLookups.confirmTradeAction(
                socketIndex, userId, targetUserId, roomId, sessionId, tradeState, tradeDao());
            if (!confirmation.valid()) {
                return "";
            }
            sendToSocket(socketIndex, confirmation.payload());
            sendToSocket(targetSocketIndex, confirmation.payload());
            sendInventoryToSocket(socketIndex);
            sendInventoryToSocket(targetSocketIndex);
            return confirmation.payload();
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_90_742E80.
     */
    public static void sendInteractionState(int socketIndex, long suppliedTargetSocketIndex, Long suppliedInteractionState) {
        try {
            if (socketIndex <= 0) {
                return;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long sourceRoomUserIndex = representedRoomUserIndex(socketIndex, userId);
            if (sourceRoomUserIndex <= 0L) {
                return;
            }
            TradeInteractionStateAction action = TradeLookups.interactionStateAction(
                socketIndex,
                sourceRoomUserIndex,
                suppliedTargetSocketIndex,
                suppliedInteractionState != null,
                suppliedInteractionState == null ? 0L : suppliedInteractionState,
                TradeState.instance());
            if (!action.valid()) {
                return;
            }
            int targetSocketIndex = (int) action.targetSocketIndex();
            String targetUserId = handlingUserIdFromSocket(targetSocketIndex);
            if (targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return;
            }
            sendToSocket(socketIndex, action.sourcePayload());
            sendToSocket(targetSocketIndex, action.targetPayload());
            if (!action.completionPayload().isEmpty()) {
                sendToSocket(socketIndex, action.completionPayload());
                sendToSocket(targetSocketIndex, action.completionPayload());
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_91_743480.
     */
    public static String addTradeFurniture(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            if (socketIndex <= 0) {
                return "";
            }
            TradeState tradeState = TradeState.instance();
            int targetSocketIndex = tradeState.interactionPartner(socketIndex);
            if (targetSocketIndex <= 0) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            String targetUserId = handlingUserIdFromSocket(targetSocketIndex);
            if (userId.isEmpty() || "0".equals(userId) || targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return "";
            }
            TradeWire.FurnitureRequest request = TradeWire.furnitureRequest(packetPayload, packetPrefix);
            long furnitureId = request.furnitureId();
            if (furnitureId <= 0L) {
                return "";
            }
            TradeOfferAction action = TradeLookups.addOfferAction(
                socketIndex, targetSocketIndex, userId, targetUserId, furnitureId, tradeState, furnitureDao());
            if (!action.sourcePayload().isEmpty()) {
                sendToSocket(socketIndex, action.sourcePayload());
            }
            if (!action.targetPayload().isEmpty()) {
                sendToSocket(targetSocketIndex, action.targetPayload());
            }
            return action.sourcePayload();
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_92_744870.
     */
    public static String removeTradeFurniture(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            if (socketIndex <= 0) {
                return "";
            }
            TradeState tradeState = TradeState.instance();
            int targetSocketIndex = tradeState.interactionPartner(socketIndex);
            if (targetSocketIndex <= 0) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            String targetUserId = handlingUserIdFromSocket(targetSocketIndex);
            if (userId.isEmpty() || "0".equals(userId) || targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return "";
            }
            TradeWire.FurnitureRequest request = TradeWire.furnitureRequest(packetPayload, packetPrefix);
            long furnitureId = request.furnitureId();
            if (furnitureId <= 0L) {
                return "";
            }
            TradeOfferAction action = TradeLookups.removeOfferAction(
                socketIndex, targetSocketIndex, userId, targetUserId, furnitureId, tradeState, furnitureDao());
            if (!action.sourcePayload().isEmpty()) {
                sendToSocket(socketIndex, action.sourcePayload());
            }
            if (!action.targetPayload().isEmpty()) {
                sendToSocket(targetSocketIndex, action.targetPayload());
            }
            return action.sourcePayload();
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_93_745D90.
     */
    public static void requestInteraction(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            if (socketIndex <= 0) {
                return;
            }
            SocialWire.RoomUserIndexRequest request =
                SocialWire.roomUserIndexRequest(packetPayload, packetPrefix);
            long requestedRoomUserIndex = request.roomUserIndex();
            if (requestedRoomUserIndex <= 0L) {
                return;
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long callerRoomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (callerRoomId <= 0L) {
                return;
            }
            TradeInteractionRequestAction action = TradeLookups.requestInteractionAction(
                socketIndex,
                callerUserId,
                callerRoomId,
                requestedRoomUserIndex,
                roomDao(),
                TradeState.instance(),
                userId -> handlingSocketFromUserId(String.valueOf(userId)));
            if (!action.valid()) {
                return;
            }
            sendToSocket(socketIndex, action.sourcePayload());
            sendToSocket((int) action.targetSocketIndex(), action.targetPayload());
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_94_746990.
     */
    public static void closeInteraction(int socketIndex, int suppliedTargetSocketIndex) {
        try {
            if (socketIndex <= 0) {
                return;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long sourceRoomUserIndex = representedRoomUserIndex(socketIndex, userId);
            if (sourceRoomUserIndex <= 0L) {
                return;
            }
            int targetSocketIndex = suppliedTargetSocketIndex;
            if (targetSocketIndex <= 0) {
                targetSocketIndex = TradeState.instance().interactionPartner(socketIndex);
            }
            if (targetSocketIndex <= 0) {
                return;
            }
            String targetUserId = handlingUserIdFromSocket(targetSocketIndex);
            if (targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return;
            }
            long targetRoomUserIndex = representedRoomUserIndex(targetSocketIndex, targetUserId);
            if (targetRoomUserIndex <= 0L) {
                return;
            }
            TradeInteractionCloseAction action = TradeLookups.closeInteractionAction(
                socketIndex, targetSocketIndex, sourceRoomUserIndex, targetRoomUserIndex, TradeState.instance());
            if (!action.valid()) {
                return;
            }
            sendToSocket(socketIndex, action.payload());
            sendToSocket((int) action.targetSocketIndex(), action.payload());
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_98_747D80.
     */
    public static long sendDimmerPresets(int socketIndex) {
        long currentPresetId = 0L;
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            FurnitureDimmers.PresetPayload dimmerPayload = FurnitureDimmers.presetsForUser(
                userId, roomId, roomDao(), furnitureDao());
            currentPresetId = dimmerPayload.currentPresetId();
            if (!dimmerPayload.payload().isEmpty()) {
                sendToSocket(socketIndex, dimmerPayload.payload());
            }
            return currentPresetId;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return currentPresetId;
        }
    }

    /**
     * Original function: Proc_6_99_748460.
     */
    public static long toggleDimmerState(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            FurnitureDimmers.StatePayload dimmerPayload = FurnitureDimmers.toggleStateForUser(
                userId, roomId, roomDao(), furnitureDao());
            if (!dimmerPayload.payload().isEmpty()) {
                broadcastToCurrentRoom(socketIndex, dimmerPayload.payload());
            }
            return dimmerPayload.state();
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_100_748C80.
     */
    public static long updateDimmerPreset(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            FurnitureWire.DimmerPresetRequest request =
                FurnitureWire.dimmerPresetRequest(packetPayload);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            FurnitureDimmers.UpdatePayload dimmerPayload = FurnitureDimmers.updatePresetForUser(
                userId,
                roomId,
                request.presetId(),
                request.backgroundId(),
                request.colourText(),
                request.lightLevel(),
                roomDao(),
                furnitureDao());
            if (!dimmerPayload.payload().isEmpty()) {
                broadcastToCurrentRoom(socketIndex, dimmerPayload.payload());
            }
            return dimmerPayload.furnitureId();
        } catch (Exception ignored) {
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_101_749540.
     */
    public static long sendUserEffectList(int socketIndex, String packetPrefix, String packetPayload) {
        long listedEffects = 0L;
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            UserPayloads.EffectListPayload effectPayload =
                UserLookups.effectListPayload(userId, userDao());
            listedEffects = effectPayload.listedEffects();
            sendToSocket(socketIndex, effectPayload.payload());
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
        return listedEffects;
    }

    /**
     * Original function: Proc_6_102_749C50.
     */
    public static long activateUserEffect(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            SocialWire.EffectRequest request = SocialWire.effectRequest(packetPayload);
            long effectId = request.effectId();
            if (effectId <= 0L) {
                return 0L;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            UserEffectActivation activation = UserLookups.activateUserEffect(userId, effectId, socketIndex, userDao());
            if (!activation.valid()) {
                return 0L;
            }
            sendToSocket(socketIndex, activation.payload());
            if (!activation.broadcastPayload().isEmpty()) {
                broadcastToCurrentRoom(socketIndex, activation.broadcastPayload());
            }
            return activation.effectId();
        } catch (Exception ignored) {
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_103_74A510.
     */
    public static long expireUserEffects() {
        long expiredCount = 0L;
        try {
            for (UserEffectExpiry effect : UserLookups.expiredUserEffects(userDao())) {
                int socketIndex = (int) effect.socketIndex();
                if (effect.valid()) {
                    if (!effect.broadcastPayload().isEmpty()) {
                        broadcastToCurrentRoom(socketIndex, effect.broadcastPayload());
                    }
                    sendToSocket(socketIndex, effect.payload());
                    expiredCount++;
                }
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
        return expiredCount;
    }

    /**
     * Original function: Proc_6_104_74AB60.
     */
    public static void sendCreatableRoomCount(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            long maxOwnedRooms = AppConfigState.instance().settingsCache().longValueOrDefault("com.server.socket.game.rooms.own.max", 0);
            sendToSocket(socketIndex, RoomLookups.creatableRoomCountPayload(userId, maxOwnedRooms, roomDao()));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_105_74AD50.
     */
    public static void createRoom(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long maxOwnedRooms = AppConfigState.instance().settingsCache().longValueOrDefault("com.server.socket.game.rooms.own.max", 0);
            CreatedRoom room = RoomLookups.createRoom(
                userId, RoomWire.createRoomRequest(packetPayload),
                maxOwnedRooms, UserLookups.hcLevel(userId, userDao()), roomDao());
            if (!room.valid()) {
                return;
            }
            room.invalidateCaches();
            sendToSocket(socketIndex, room.payload());
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_107_74B7E0.
     */
    public static void toggleStaffPickedRoom(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId) || !UserLookups.hasPermission(userId, "fuse_client_staff", userDao(), AppConfigState.instance().permissionMatrix())) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
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
            broadcastToCurrentRoom(socketIndex, NavigatorRequests.roomListPayload(roomId, roomDao()));
            broadcastToCurrentRoom(socketIndex, RoomPayloads.entryUpdated(roomId));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_108_74D800.
     */
    public static void sendFavouriteRoomIds(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            long maxFavorites = AppConfigState.instance().settingsCache().longValueOrDefault("com.server.socket.game.rooms.favourites.max", 30);
            if (maxFavorites <= 0L) {
                maxFavorites = 30L;
            }
            String payload = RoomLookups.favouriteRoomIdsPayload(userId, maxFavorites, roomDao());
            if (!payload.isEmpty()) {
                sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_109_74DBD0.
     */
    public static void removeFavouriteRoom(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            RoomWire.RoomIdRequest request = RoomWire.roomIdRequest(packetPayload, packetPrefix);
            long roomId = request.roomId();
            String userId = handlingUserIdFromSocket(socketIndex);
            String payload = RoomLookups.removeFavouriteRoomPayload(userId, roomId, roomDao());
            if (!payload.isEmpty()) {
                sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_110_74DDA0.
     */
    public static void addFavouriteRoom(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            RoomWire.RoomIdRequest request = RoomWire.roomIdRequest(packetPayload, packetPrefix);
            long roomId = request.roomId();
            String userId = handlingUserIdFromSocket(socketIndex);
            String payload = RoomLookups.addFavouriteRoomPayload(userId, roomId, roomDao());
            if (!payload.isEmpty()) {
                sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_111_74DF70.
     */
    public static void sendRoomCategoryPayload(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            sendToSocket(socketIndex, roomCategoryCache().rankPayload(0L, 0L));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_115_751220.
     */
    public static void sendEventCategoryNavigatorRooms(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            sendToSocket(socketIndex, NavigatorRequests.eventCategoryQueryPayload(
                packetPayload, AppConfigState.instance().settingsCache(), recommendedRooms(), roomDao()));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_116_751550.
     */
    public static void sendPopularCategoryNavigatorRooms(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            sendToSocket(socketIndex, NavigatorRequests.popularCategoryQueryPayload(
                packetPayload, AppConfigState.instance().settingsCache(), recommendedRooms(), roomDao()));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_117_751880.
     */
    public static void sendFriendCurrentNavigatorRooms(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            sendToSocket(socketIndex, NavigatorRequests.friendCurrentQueryPayload(
                handlingUserIdFromSocket(socketIndex), AppConfigState.instance().settingsCache(), roomDao()));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_118_751A80.
     */
    public static void sendFriendOwnedNavigatorRooms(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            sendToSocket(socketIndex, NavigatorRequests.friendOwnedQueryPayload(
                handlingUserIdFromSocket(socketIndex), AppConfigState.instance().settingsCache(), roomDao()));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_119_751C80.
     */
    public static void sendFavouriteNavigatorRooms(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            sendToSocket(socketIndex, NavigatorRequests.favouriteQueryPayload(
                handlingUserIdFromSocket(socketIndex), AppConfigState.instance().settingsCache(), roomDao()));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_120_751E80.
     */
    public static void sendRecentlyVisitedNavigatorRooms(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            sendToSocket(socketIndex, NavigatorRequests.recentlyVisitedQueryPayload(
                handlingUserIdFromSocket(socketIndex), AppConfigState.instance().settingsCache(), roomDao()));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_121_752080.
     */
    public static void sendOwnedNavigatorRooms(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            sendToSocket(socketIndex, NavigatorRequests.ownedQueryPayload(
                handlingUserIdFromSocket(socketIndex), AppConfigState.instance().settingsCache(), roomDao()));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_123_754020.
     */
    public static void sendOfficialNavigator(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String payload = NavigatorRequests.officialNavigatorPayload(roomDao());
            if (!payload.isEmpty()) {
                sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_124_754D90.
     */
    public static void sendPopularNavigatorTags(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String payload = NavigatorRequests.popularTagsPayload(AppConfigState.instance().settingsCache(), roomDao());
            if (!payload.isEmpty()) {
                sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_125_755650.
     */
    public static void sendNavigatorTagResults(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            sendToSocket(socketIndex, NavigatorRequests.tagResultsQueryPayload(
                packetPayload, AppConfigState.instance().settingsCache(), roomDao()));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_126_755B40.
     */
    public static void sendTopRatedNavigatorRooms(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            sendToSocket(socketIndex, NavigatorRequests.topRatedQueryPayload(AppConfigState.instance().settingsCache(), roomDao()));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_127_755D30.
     */
    public static void sendNavigatorSearchResults(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            sendToSocket(socketIndex, NavigatorRequests.searchResultsQueryPayload(
                packetPayload, AppConfigState.instance().settingsCache(), roomDao()));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_128_756190.
     */
    public static String purchaseCatalogProduct(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            CatalogWire.ProductPurchaseRequest request =
                CatalogWire.productPurchaseRequest(packetPayload);
            long catalogProductId = request.catalogProductId();
            String signText = request.signText();
            if (catalogProductId <= 0L) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (socketIndex <= 0 || userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            CatalogRegistry.CatalogProduct catalogProduct =
                CatalogState.instance().registry().catalogProduct(catalogProductId).orElse(null);
            if (catalogProduct == null) {
                return "";
            }
            long productId = catalogProduct.productId();
            String typeSecondary = catalogProduct.typeSecondary().toLowerCase();
            long creditPrice = catalogProduct.creditPrice();
            long activityPrice = catalogProduct.activityPrice();
            long activityType = catalogProduct.activityType();
            long minClubLevel = catalogProduct.minClubLevel();
            if (productId <= 0L) {
                return "";
            }
            if (activityType < 0L || activityType > 4L) {
                activityType = 0L;
            }
            long userIdValue = NumberUtils.parseLong(userId);
            UserDao users = userDao();
            UserDao.CatalogPurchaseBalance balance = users == null
                ? null
                : users.catalogPurchaseBalance(userIdValue, activityType).orElse(null);
            if (balance == null) {
                return "";
            }
            if (minClubLevel > 0L && balance.clubLevel() < minClubLevel) {
                sendToSocket(socketIndex, CatalogPayloads.purchaseError(3));
                return "";
            }
            if (balance.credits() < creditPrice) {
                sendToSocket(socketIndex, CatalogPayloads.purchaseError(1));
                return "";
            }
            if (balance.activityPoints() < activityPrice) {
                sendToSocket(socketIndex, CatalogPayloads.purchaseError(2));
                return "";
            }
            long grantedFurnitureId = NumberUtils.parseLong(sendCatalogPurchaseItems(socketIndex, catalogProductId, signText));
            if (grantedFurnitureId <= 0L) {
                return "";
            }
            if (creditPrice > 0L || activityPrice > 0L) {
                users.spendCatalogPurchaseBalance(userIdValue, creditPrice, activityType, activityPrice);
                if (creditPrice > 0L) {
                    UserRefreshService.sendCreditsRefresh(userId);
                }
                if (activityPrice > 0L) {
                    UserRefreshService.sendActivityPointRefreshes(userId);
                }
            }
            String itemClass = "i";
            if (!"products_deals".equals(typeSecondary)
                && GameDataCaches.productCache().type(productId) == 8L) {
                itemClass = "I";
            }
            String purchasePayload = CatalogPayloads.purchase(catalogProductId, creditPrice, activityPrice,
                activityType, grantedFurnitureId, itemClass);
            sendToSocket(socketIndex, purchasePayload);
            sendInventoryToSocket(socketIndex);
            return purchasePayload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_129_7583C0.
     */
    public static String sendCatalogPurchaseItems(int socketIndex, long catalogProductId, String signText) {
        try {
            String itemSignText = StringUtils.text(signText);
            if (socketIndex <= 0 || catalogProductId <= 0L) {
                return "";
            }
            CatalogRegistry.CatalogProduct catalogProduct =
                CatalogState.instance().registry().catalogProduct(catalogProductId).orElse(null);
            if (catalogProduct == null) {
                return "";
            }
            long productId = catalogProduct.productId();
            String typeSecondary = catalogProduct.typeSecondary().toLowerCase();
            if (productId <= 0L) {
                return "";
            }
            CatalogGrantResult grantResult = grantCatalogFurniture(socketIndex, catalogProductId, signText);
            if (grantResult.isEmpty()) {
                return "";
            }
            List<Long> grantedIds = grantResult.furnitureIds();
            long[] productIds;
            int itemCount = 0;
            if ("products_deals".equals(typeSecondary)) {
                CatalogRegistry.ProductDeal deal = CatalogState.instance().registry().productDeal(productId).orElse(null);
                List<Long> dealProductIds = deal == null ? List.<Long>of() : deal.itemProductIds();
                productIds = new long[dealProductIds.size()];
                for (Long dealProductId : dealProductIds) {
                    if (dealProductId != null && dealProductId > 0L) {
                        productIds[itemCount++] = dealProductId;
                    }
                }
            } else {
                itemCount = Math.max(1, grantedIds.size());
                productIds = new long[itemCount];
                for (int index = 0; index < itemCount; index++) {
                    productIds[index] = productId;
                }
            }
            long firstFurnitureId = 0L;
            for (int index = 0; index < itemCount; index++) {
                long furnitureId = index < grantedIds.size() ? NumberUtils.parseLong(grantedIds.get(index)) : 0L;
                long itemProductId = productIds[index];
                if (furnitureId > 0L && itemProductId > 0L) {
                    if (firstFurnitureId == 0L) {
                        firstFurnitureId = furnitureId;
                    }
                    String itemData = GameDataCaches.productCache().itemData(itemProductId);
                    if (itemData.isEmpty()) {
                        itemData = GameDataCaches.productCache().defaultSign(itemProductId);
                    }
                    long productType = GameDataCaches.productCache().type(itemProductId);
                    sendToSocket(socketIndex,
                        InventoryMessagePayloads.roomAdd(furnitureId, itemProductId, itemData, 0));
                    if ("TROPHY_VAR".equalsIgnoreCase(GameDataCaches.productCache().defaultSign(itemProductId))) {
                        String trophySign = handlingUserName(handlingUserIdFromSocket(socketIndex)) + '\b'
                            + RecyclerRewards.rewardSign() + '\b' + itemSignText;
                        furnitureDao().updateSignText(furnitureId, StringUtils.singleLineText(trophySign));
                    }
                    if (productType == 8L) {
                        sendToSocket(socketIndex, CatalogPayloads.dimensionMap(furnitureId,
                            GameDataCaches.productCache().dimensionMapId(itemProductId)));
                    }
                }
            }
            return String.valueOf(firstFurnitureId);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_133_760400.
     */
    public static CatalogGrantResult grantCatalogFurniture(int socketIndex, long catalogProductId, String signText) throws Exception {
        String userId = socketIndex > 0 ? handlingUserIdFromSocket(socketIndex) : "";
        if (userId.isEmpty() || "0".equals(userId)) {
            return CatalogGrantResult.empty();
        }
        CatalogRegistry.CatalogProduct catalogProduct =
            CatalogState.instance().registry().catalogProduct(catalogProductId).orElse(null);
        if (catalogProduct == null) {
            return CatalogGrantResult.empty();
        }
        long productId = catalogProduct.productId();
        String typeSecondary = catalogProduct.typeSecondary().toLowerCase();
        long amount = catalogProduct.amount();
        if (amount <= 0L) {
            amount = 1L;
        }
        long userIdValue = NumberUtils.parseLong(userId);
        FurnitureDao furniture = furnitureDao();
        UserDao users = userDao();
        ClubDao clubs = clubDao();
        if (furniture == null || users == null || clubs == null) {
            return CatalogGrantResult.empty();
        }
        long grantedCount = 0L;
        if ("products_deals".equals(typeSecondary)) {
            CatalogRegistry.ProductDeal deal = CatalogState.instance().registry().productDeal(productId).orElse(null);
            if (deal == null) {
                return CatalogGrantResult.empty();
            }
            for (Long dealProductId : deal.itemProductIds()) {
                if (dealProductId != null && dealProductId > 0L) {
                    String defaultSign = StringUtils.singleLineText(GameDataCaches.productCache().defaultSign(dealProductId));
                    if (defaultSign.isEmpty()) {
                        defaultSign = StringUtils.singleLineText(GameDataCaches.productCache().fallbackDefaultSign(dealProductId));
                    }
                    furniture.insertCatalogFurniture(dealProductId, userIdValue, defaultSign, catalogProductId);
                    grantedCount++;
                }
            }
        } else {
            ClubDao.ContainedClubProduct containedClub = clubs.containedClubProduct(catalogProductId)
                .orElseGet(() -> {
                    try {
                        return clubs.containedClubProduct(productId).orElse(null);
                    } catch (Exception ignored) {
                        return null;
                    }
                });
            if (containedClub != null) {
                long hcMonths = containedClub.months();
                long hcLevel = containedClub.level();
                if (hcLevel <= 0L) {
                    hcLevel = 1L;
                }
                ClubPeriodService.applyClubPeriod(NumberUtils.parseLong(userId), hcLevel, hcMonths, hcMonths * 31L);
            }
            String badgeId = GameDataCaches.productCache().badgeId(productId).toUpperCase();
            if (badgeId.isEmpty()) {
                badgeId = GameDataCaches.productCache().fallbackBadgeId(productId).toUpperCase();
            }
            if (badgeId.length() > 2) {
                String existingBadge = StringUtils.text(users.badgeId(userIdValue, badgeId)).toUpperCase();
                if (!badgeId.equals(existingBadge)) {
                    users.insertBadge(userIdValue, 0L, badgeId);
                    long badgeRowId = users.badgeRowId(userIdValue, badgeId);
                    SocialLookups.equippedBadgePayload(userId, userDao());
                    sendBadgeInventory(socketIndex, "Ce", "");
                    if (badgeRowId > 0L) {
                        sendActivityPointBalanceToSocket(socketIndex);
                    }
                }
            }
            String defaultSign = signText;
            if (defaultSign.isEmpty()) {
                defaultSign = StringUtils.singleLineText(GameDataCaches.productCache().defaultSign(productId));
            }
            if (defaultSign.isEmpty()) {
                defaultSign = StringUtils.singleLineText(GameDataCaches.productCache().fallbackDefaultSign(productId));
            }
            for (long itemIndex = 1L; itemIndex <= amount; itemIndex++) {
                furniture.insertCatalogFurniture(productId, userIdValue, defaultSign, catalogProductId);
                grantedCount++;
            }
        }
        if (grantedCount <= 0L) {
            return CatalogGrantResult.empty();
        }
        List<Long> grantedIds = new ArrayList<>();
        for (Long newestId : furniture.newestFurnitureIdsByOwner(userIdValue, grantedCount)) {
            if (newestId != null && newestId > 0L) {
                grantedIds.add(newestId);
            }
        }
        long firstGrantedId = grantedIds.isEmpty() ? 0L : NumberUtils.parseLong(grantedIds.get(0));
        if (!"products_deals".equals(typeSecondary)
            && GameDataCaches.productCache().type(productId) == 9L && firstGrantedId > 0L) {
            furniture.insertDefaultDimmerPresets(firstGrantedId);
            furniture.updateDefaultDimmerSign(firstGrantedId);
        }
        return new CatalogGrantResult(grantedIds);
    }

    /**
     * Original function: Proc_6_130_75B770.
     */
    public static String claimClubGift(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            CatalogWire.ClubGiftClaimRequest request =
                CatalogWire.clubGiftClaimRequest(packetPayload);
            String requestedSprite = request.requestedSprite();
            if (requestedSprite.isEmpty()) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (socketIndex <= 0 || userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            CatalogDao catalog = catalogDao();
            long catalogProductId = catalog == null ? 0L : catalog.idBySprite(requestedSprite);
            if (catalogProductId <= 0L) {
                return "";
            }
            GiftSettings.ClubGift gift = giftSettings().clubGiftByCatalogProductId(catalogProductId);
            long productId = gift.productId();
            long requiredDays = gift.requiredDays();
            if (productId <= 0L) {
                return "";
            }
            long userIdValue = NumberUtils.parseLong(userId);
            ClubDao clubs = clubDao();
            ClubDao.ClubGiftStatus status = clubs == null ? null : clubs.clubGiftStatus(userIdValue).orElse(null);
            if (status == null) {
                return "";
            }
            if (status.presentsAvailable() <= 0L || status.activeDays() < requiredDays) {
                return "";
            }
            String itemData = GameDataCaches.productCache().itemData(productId);
            FurnitureDao furniture = furnitureDao();
            furniture.insertClubGiftFurniture(productId, catalogProductId, userIdValue, itemData);
            long insertedFurnitureId = furniture.newestFurnitureIdByOwnerAndProduct(userIdValue, productId);
            String itemClass = GameDataCaches.productCache().type(productId) == 9L ? "I" : "i";
            String responsePayload = CatalogPayloads.clubGiftClaim(productId,
                GameDataCaches.productCache().itemData(productId), itemClass, insertedFurnitureId);
            sendToSocket(socketIndex, responsePayload);
            clubs.decrementPresents(userIdValue);
            sendInventoryToSocket(socketIndex);
            return responsePayload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_131_75C700.
     */
    public static void sendClubGiftStatus(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (socketIndex <= 0 || userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            ClubDao clubs = clubDao();
            ClubDao.ClubGiftStatus status = clubs == null
                ? new ClubDao.ClubGiftStatus(0L, 0L, 0L, 0L, 0L)
                : clubs.clubGiftStatus(NumberUtils.parseLong(userId))
                    .orElse(new ClubDao.ClubGiftStatus(0L, 0L, 0L, 0L, 0L));
            sendToSocket(socketIndex, ClubPayloads.clubGiftStatus(giftSettings(), status));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_132_75D4A0.
     */
    public static String purchaseCatalogGift(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            CatalogWire.GiftPurchaseRequest request =
                CatalogWire.giftPurchaseRequest(packetPayload);
            long catalogProductId = request.catalogProductId();
            long expectedProductId = request.expectedProductId();
            String recipientName = request.recipientName();
            String giftMessage = request.giftMessage();
            long wrapProductId = request.wrapProductId();
            long ribbonId = request.ribbonId();
            long colorId = request.colorId();
            if (catalogProductId <= 0L || recipientName.isEmpty()) {
                return "";
            }
            String senderUserId = handlingUserIdFromSocket(socketIndex);
            if (socketIndex <= 0 || senderUserId.isEmpty() || "0".equals(senderUserId)) {
                return "";
            }
            CatalogRegistry.CatalogProduct catalogProduct =
                CatalogState.instance().registry().catalogProduct(catalogProductId).orElse(null);
            if (catalogProduct == null) {
                return "";
            }
            long productId = catalogProduct.productId();
            long creditPrice = catalogProduct.creditPrice();
            long activityPrice = catalogProduct.activityPrice();
            long activityType = catalogProduct.activityType();
            long allowGifts = catalogProduct.allowGifts();
            long minClubLevel = catalogProduct.minClubLevel();
            if (productId <= 0L || allowGifts == 0L || expectedProductId > 0L && expectedProductId != productId) {
                return "";
            }
            if (activityType < 0L || activityType > 4L) {
                activityType = 0L;
            }
            if (AppConfigState.instance().settingsCache().longValueOrDefault("com.client.catalog.gifts.wrap.enabled", 0) != 0L) {
                long wrapPrice = AppConfigState.instance().settingsCache().longValueOrDefault("com.client.catalog.gifts.wrap.price", 0);
                if (wrapProductId <= 0L) {
                    CatalogDao catalog = catalogDao();
                    if (catalog == null) {
                        return "";
                    }
                    wrapProductId = catalog.firstGiftWrapProductId();
                }
                if (wrapProductId > 0L && !giftSettings().containsGiftWrapProduct(wrapProductId)) {
                    return "";
                }
                creditPrice += wrapPrice;
            }
            long senderUserIdValue = NumberUtils.parseLong(senderUserId);
            UserDao users = userDao();
            UserDao.CatalogPurchaseBalance balance = users == null
                ? null
                : users.catalogPurchaseBalance(senderUserIdValue, activityType).orElse(null);
            if (balance == null) {
                return "";
            }
            if (minClubLevel > 0L && balance.clubLevel() < minClubLevel) {
                sendToSocket(socketIndex, CatalogPayloads.purchaseError(3));
                return "";
            }
            if (balance.credits() < creditPrice) {
                sendToSocket(socketIndex, CatalogPayloads.purchaseError(1));
                return "";
            }
            if (balance.activityPoints() < activityPrice) {
                sendToSocket(socketIndex, CatalogPayloads.purchaseError(2));
                return "";
            }
            String recipientUserId = String.valueOf(users.userIdByName(recipientName));
            if (recipientUserId.isEmpty() || "0".equals(recipientUserId)) {
                recipientUserId = senderUserId;
            }
            long grantedFurnitureId = grantCatalogFurniture(socketIndex, catalogProductId, giftMessage).firstFurnitureId();
            if (grantedFurnitureId <= 0L) {
                return "";
            }
            String productSign = GameDataCaches.productCache().defaultSign(productId);
            if ("TROPHY_VAR".equalsIgnoreCase(productSign)) {
                productSign = handlingUserName(senderUserId) + '\b' + RecyclerRewards.rewardSign() + '\b' + giftMessage;
            }
            long giftSecondary = colorId * 1000L + ribbonId;
            furnitureDao().updateGiftMetadata(
                grantedFurnitureId,
                StringUtils.singleLineText(giftMessage),
                StringUtils.singleLineText(productSign),
                NumberUtils.parseLong(recipientUserId),
                catalogProductId,
                giftSecondary);
            if (creditPrice > 0L || activityPrice > 0L) {
                users.spendCatalogPurchaseBalance(senderUserIdValue, creditPrice, activityType, activityPrice);
                if (creditPrice > 0L) {
                    UserRefreshService.sendCreditsRefresh(senderUserId);
                }
                if (activityPrice > 0L) {
                    UserRefreshService.sendActivityPointRefreshes(senderUserId);
                }
            }
            users.incrementGiftsGiven(senderUserIdValue);
            if (!recipientUserId.equals(senderUserId)) {
                users.incrementGiftsReceived(NumberUtils.parseLong(recipientUserId));
                advanceAchievementProgress(socketIndex, 6);
            }
            String purchasePayload = CatalogPayloads.giftPurchase(
                catalogProduct, creditPrice, activityPrice, activityType, grantedFurnitureId);
            sendToSocket(socketIndex, purchasePayload);
            long recipientSocket = handlingSocketFromUserId(recipientUserId);
            if (recipientSocket > 0L) {
                sendToSocket((int) recipientSocket,
                    InventoryMessagePayloads.roomAdd(grantedFurnitureId, productId, productSign, giftSecondary));
                advanceAchievementProgress((int) recipientSocket, 7);
            }
            return purchasePayload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_134_765B90.
     */
    public static void sendCatalogGiftAvailability(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            CatalogWire.GiftAvailabilityRequest request =
                CatalogWire.giftAvailabilityRequest(packetPayload);
            long itemId = request.itemId();
            CatalogRegistry.CatalogProduct catalogProduct =
                CatalogState.instance().registry().catalogProduct(itemId).orElse(null);
            long itemType = catalogProduct == null ? 0L : catalogProduct.activityType();
            long giftEnabled = itemType == 1L ? AppConfigState.instance().settingsCache().longValueOrDefault("com.client.catalog.gifts.enabled", 0) : 0L;
            sendToSocket(socketIndex, CatalogPayloads.giftAvailability(itemId, giftEnabled));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_135_765D80.
     */
    public static void sendCatalogGiftWrapOptions(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String defaultPayload = CatalogPayloads.giftWrapPriceFallback(
                AppConfigState.instance().settingsCache().longValueOrDefault("com.client.catalog.gifts.wrap.enabled", 0));
            long giftWrapPrice = NumberUtils.parseLong(AppConfigState.instance().settingsCache().valueOrDefault("com.client.catalog.gifts.wrap.price", defaultPayload));
            sendToSocket(socketIndex,
                CatalogPayloads.giftWrapOptions(giftWrapPrice, giftSettings()));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_136_765F10.
     */
    public static void sendCatalogPage(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            CatalogWire.PageRequest request = CatalogWire.pageRequest(packetPayload);
            long pageId = request.pageId();
            if (!catalogPages().pagePayload(pageId).isEmpty()) {
                sendToSocket(socketIndex, CatalogPayloads.page(catalogPages(), pageId));
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_137_766470.
     */
    public static void redeemVoucher(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            VoucherWire.RedeemRequest request = VoucherWire.redeemRequest(packetPayload);
            String voucherCode = request.voucherCode();
            String userId = handlingUserIdFromSocket(socketIndex);
            VoucherRedemption redemption = VoucherRedemption.redeem(
                voucherCode,
                NumberUtils.parseLong(userId),
                voucherDao(),
                userDao(),
                GameDataCaches.productCache());
            if (redemption.redeemed() && redemption.creditsRefreshRequired()) {
                UserRefreshService.sendCreditsRefresh(userId);
            }
            if (redemption.redeemed() && redemption.activityPointRefreshRequired()) {
                UserRefreshService.sendActivityPointRefreshes(userId);
            }
            sendToSocket(socketIndex, redemption.responsePayload());
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_139_768100.
     */
    public static void applyRoomDecorationFurniture(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (userId.isEmpty() || roomId <= 0L) {
                return;
            }
            long furnitureId = FurnitureWire.stickyFurnitureId(packetPayload);
            if (furnitureId <= 0L) {
                return;
            }
            FurnitureLookups.RoomDecorationApplication application = FurnitureLookups.applyRoomDecorationFurniture(
                furnitureId,
                roomId,
                NumberUtils.parseLong(userId),
                furnitureDao(),
                roomDao(),
                CatalogState.instance().registry());
            if (!application.valid()) {
                return;
            }
            broadcastToCurrentRoom(socketIndex, application.roomPayload());
            sendToSocket(socketIndex, application.inventoryRemovePayload());
            sendInventoryToSocket(socketIndex);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_140_769400.
     */
    public static void sendInventoryToSocket(int socketIndex) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty()) {
                return;
            }
            FurnitureDao furniture = furnitureDao();
            if (furniture == null) {
                return;
            }
            InventoryMessagePayloads.InventoryList payloads = InventoryMessagePayloads.listFromItems(
                furniture.inventoryFurnitureForOwner(NumberUtils.parseLong(userId)));
            sendToSocket(socketIndex, InventoryMessagePayloads.regularList(payloads));
            sendToSocket(socketIndex, InventoryMessagePayloads.iconList(payloads));
            sendToSocket(socketIndex, InventoryMessagePayloads.emptyRentalList());
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_141_76A670.
     * Original function: Proc_6_159_79FCD0.
     */
    public static String moveFloorFurnitureInRoom(int socketIndex, String floorPlacementPayload) {
        return placeOrMoveFloorFurniture(socketIndex, FurnitureWire.floorPlacementRequest(floorPlacementPayload), false);
    }

    /**
     * Original function: Proc_6_142_76B310.
     */
    public static String placeFloorFurnitureFromInventory(int socketIndex, String floorPlacementPayload) {
        return placeOrMoveFloorFurniture(socketIndex, FurnitureWire.floorPlacementRequest(floorPlacementPayload), true);
    }

    /**
     * Original function: Proc_6_143_76BB80.
     */
    public static void sendActivityPointBalanceToSocket(int socketIndex) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            String payload = UserLookups.activityPointBalancePayload(userId, userDao());
            if (!payload.isEmpty()) {
                sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_144_76BE70.
     */
    public static void returnRoomFurnitureToInventory(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (userId.isEmpty() || "0".equals(userId) || roomId <= 0L) {
                return;
            }
            long furnitureId = FurnitureWire.pickupFurnitureId(packetPayload);
            if (furnitureId <= 0L) {
                return;
            }
            boolean canPickUpAny = UserLookups.hasPermission(userId, "fuse_pick_up_any_furni", userDao(), AppConfigState.instance().permissionMatrix());
            FurnitureLookups.FurnitureInventoryReturn inventoryReturn =
                FurnitureLookups.returnRoomFurnitureToInventory(
                    furnitureId,
                    roomId,
                    NumberUtils.parseLong(userId),
                    RoomLookups.userOwnsRoom(userId, roomId, roomDao()),
                    RoomLookups.userHasRoomRight(userId, roomId, roomDao()),
                    canPickUpAny,
                    furnitureDao());
            if (!inventoryReturn.valid()) {
                return;
            }
            broadcastToCurrentRoom(socketIndex, inventoryReturn.removedPayload());
            RoomCacheFiles.invalidateRoom(roomId);
            sendInventoryToSocket(socketIndex);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_147_76E910.
     */
    public static long refreshFloorFurnitureStatesAtPosition(long roomId, long positionX, long positionY) {
        try {
            if (positionX <= 0L || positionY <= 0L) {
                return 0L;
            }
            List<FurnitureLookups.FloorPositionStateRefresh> refreshes =
                FurnitureLookups.floorStateRefreshesAtPosition(
                    roomId, positionX, positionY, furnitureDao(), GameDataCaches.productCache());
            long refreshCount = 0L;
            for (FurnitureLookups.FloorPositionStateRefresh refresh : refreshes) {
                RoomState.instance().setFurnitureRoomCache(FurnitureStateWrites.refreshState(
                    RoomState.instance().furnitureRoomCache(), refresh.roomId(), refresh.furnitureId(),
                    refresh.stateValue()));
                broadcastToRoomUsers(refresh.roomId(), refresh.payload());
                refreshCount++;
            }
            return refreshCount;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_149_775C10.
     */
    public static void toggleFloorFurnitureState(int socketIndex, String floorStatePayload) {
        try {
            if (socketIndex <= 0) {
                return;
            }
            long furnitureId = FurnitureWire.floorStateFurnitureId(floorStatePayload);
            if (furnitureId <= 0L) {
                return;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            FurnitureLookups.FloorFurnitureStateToggle toggle = FurnitureLookups.toggleFloorFurnitureState(
                furnitureId,
                roomId,
                NumberUtils.parseLong(userId),
                furnitureDao(),
                GameDataCaches.productCache(),
                CatalogState.instance().registry(),
                AppPaths.applicationPath());
            if (!toggle.valid()) {
                return;
            }
            RoomState.instance().setFurnitureRoomCache(FurnitureStateWrites.refreshState(
                RoomState.instance().furnitureRoomCache(), roomId, toggle.furnitureId(), toggle.stateValue()));
            broadcastToCurrentRoom(socketIndex, toggle.payload());
            if (toggle.hasChargePayload()) {
                sendToSocket(socketIndex, toggle.chargePayload());
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_150_777FA0.
     */
    public static Object openFloorFurniturePackageOrToggleState(int socketIndex, String packagePayload) {
        try {
            if (socketIndex <= 0) {
                return "";
            }
            FurnitureWire.FloorFurniturePackageRequest request =
                FurnitureWire.floorFurniturePackageRequest(packagePayload);
            String requestPayload = request.requestPayload();
            long furnitureId = request.furnitureId();
            if (furnitureId <= 0L) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return "";
            }
            FurnitureLookups.FloorFurniturePackageOpen packageOpen =
                FurnitureLookups.openFloorFurniturePackage(furnitureId, roomId, furnitureDao(), packageDao());
            if (packageOpen.petPreviewRequired()) {
                return sendPetPackagePreview(socketIndex, "FH", requestPayload);
            }
            if (packageOpen.hasPayload()) {
                sendToSocket(socketIndex, packageOpen.payload());
                return packageOpen.furnitureId();
            }
            toggleFloorFurnitureState(socketIndex, packagePayload);
            return furnitureId;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_154_78F040.
     */
    public static String refreshLocatedFurnitureState(long furnitureId, long productId) {
        try {
            if (furnitureId <= 0L) {
                return "";
            }
            FurnitureLookups.LocatedFurnitureStateRefresh refresh = FurnitureLookups.refreshLocatedFurnitureState(
                furnitureId, productId, furnitureDao(), GameDataCaches.productCache());
            if (!refresh.valid()) {
                return "";
            }
            RoomState.instance().setFurnitureRoomCache(FurnitureStateWrites.refreshState(
                RoomState.instance().furnitureRoomCache(), refresh.roomId(), refresh.furnitureId(), refresh.stateValue()));
            broadcastToRoomUsers(refresh.roomId(), refresh.payload());
            if (refresh.clearSoundMarkers()) {
                clearJukeboxSoundMarkers(0, refresh.roomId(), refresh.furnitureId());
            }
            return refresh.payload();
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_155_795C90.
     */
    public static void pickUpRoomFurniture(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            long furnitureId = FurnitureWire.stickyFurnitureId(packetPayload);
            if (socketIndex <= 0 || furnitureId <= 0L) {
                return;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (userId.isEmpty() || "0".equals(userId) || roomId <= 0L) {
                return;
            }
            boolean canPickUpAny = UserLookups.hasPermission(userId, "fuse_pick_up_any_furni", userDao(), AppConfigState.instance().permissionMatrix());
            long userIdValue = NumberUtils.parseLong(userId);
            FurnitureLookups.RoomFurniturePickup pickup = FurnitureLookups.pickUpRoomFurniture(
                furnitureId,
                roomId,
                userIdValue,
                RoomLookups.userOwnsRoom(userId, roomId, roomDao()),
                RoomLookups.userHasRoomRight(userId, roomId, roomDao()),
                canPickUpAny,
                furnitureDao());
            if (!pickup.valid()) {
                return;
            }
            if (pickup.moderationLogRequired()) {
                String sessionId = UserLookups.sessionId(userId, userDao());
                StaffModerationDao moderationDao = staffModerationDao();
                if (moderationDao == null) {
                    return;
                }
                moderationDao.insertFurniturePickupLog(userIdValue, roomId, furnitureId, sessionId);
            }
            RoomState.instance().setFurnitureRoomCache(FurnitureStateWrites.removeMarker(
                RoomState.instance().furnitureRoomCache(), roomId, furnitureId));
            sendToSocket(socketIndex, pickup.inventoryRemovePayload());
            broadcastToCurrentRoom(socketIndex, pickup.removedPayload());
            RoomCacheFiles.invalidateRoom(roomId);
            sendInventoryToSocket(socketIndex);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_157_7974B0.
     */
    public static void placeWallFurnitureFromInventory(
        int socketIndex,
        String wallPayload,
        FurnitureDao.InventoryPlacementFurniture placementFurniture
    ) {
        try {
            if (socketIndex <= 0) {
                return;
            }
            FurnitureWire.WallFurniturePlacementRequest request =
                FurnitureWire.wallFurniturePlacementRequest(wallPayload);
            wallPayload = request.wallPayload();
            long furnitureId = placementFurniture == null ? 0L : placementFurniture.furnitureId();
            if (furnitureId <= 0L) {
                furnitureId = request.furnitureId();
            }
            if (furnitureId <= 0L) {
                return;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L || (!RoomLookups.userHasRoomRight(userId, roomId, roomDao())
                && !UserLookups.hasPermission(userId, "fuse_pick_up_any_furni", userDao(), AppConfigState.instance().permissionMatrix()))) {
                return;
            }
            FurnitureLookups.WallFurniturePlacement placement = FurnitureLookups.placeWallFurnitureFromInventory(
                wallPayload,
                furnitureId,
                roomId,
                NumberUtils.parseLong(userId),
                placementFurniture,
                furnitureDao(),
                GameDataCaches.productCache());
            if (!placement.valid()) {
                return;
            }
            sendToSocket(socketIndex, placement.inventoryRemovePayload());
            broadcastToCurrentRoom(socketIndex, placement.roomPayload());
            RoomCacheFiles.invalidateRoom(roomId);
            sendInventoryToSocket(socketIndex);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_162_7B3310.
     */
    public static void sendClientDateSettings(int socketIndex) {
        try {
            String dateFormat = AppConfigState.instance().settingsCache().valueOrDefault("com.system.format.date", "DAQBHHIIKHJHPAHQA");
            if (dateFormat.isEmpty()) {
                dateFormat = "DAQBHHIIKHJHPAHQA";
            }
            sendToSocket(socketIndex,
                "0" + dateFormat + '\2' + "SAHPB" + "http://www.alpha-series.com/" + '\2' + "QBH");
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_163_7B3480.
     */
    public static String handleLoginTicket(int socketIndex, String packetPayload) {
        try {
            String loginTicket = SessionWire.loginTicket(packetPayload);
            if (loginTicket.isEmpty() || "NULL".equalsIgnoreCase(loginTicket)) {
                if (socketIndex > 0) {
                    disconnectSocket(socketIndex);
                }
                return "";
            }
            UserDao users = userDao();
            if (users == null) {
                return "";
            }
            UserDao.LoginUser loginUser = users.loginUser(loginTicket).orElse(null);
            if (loginUser == null) {
                if (socketIndex > 0) {
                    disconnectSocket(socketIndex);
                }
                return "";
            }
            long userIdValue = loginUser.userId();
            String userId = String.valueOf(userIdValue);
            if (userIdValue == 0L) {
                if (socketIndex > 0) {
                    disconnectSocket(socketIndex);
                }
                return "";
            }
            int oldSocketIndex = (int) loginUser.oldSocketIndex();
            if (oldSocketIndex > 0 && oldSocketIndex != socketIndex) {
                disconnectSocket(oldSocketIndex);
            }
            String userName = loginUser.userName();
            long rankIndex = loginUser.rankIndex();
            long creditsValue = loginUser.credits();
            long homeRoomId = loginUser.homeRoomId();
            long updateAgeDays = loginUser.updateAgeDays();
            long emailValidated = loginUser.emailValidated();
            long[] pointValues = loginUser.activityPointValues();
            users.assignLoginSocket(userIdValue, socketIndex);
            if (updateAgeDays > 0L) {
                users.resetDailyInteractionCounters(userIdValue);
            }
            SessionState.instance().storeSocketSession(socketIndex, userId + '\2' + socketIndex + '\2' + userName + '\2'
                + rankIndex + '\2' + loginTicket + '\2');
            sendToSocket(socketIndex, "@C");
            sendRankAndStaffState(socketIndex);
            sendToSocket(socketIndex, UserPayloads.creditsRefresh(creditsValue));
            for (int pointIndex = 0; pointIndex <= 4; pointIndex++) {
                sendToSocket(socketIndex, UserPayloads.activityPointRefresh(pointIndex, pointValues[pointIndex]));
            }
            if (homeRoomId > 0L) {
                sendToSocket(socketIndex, RoomPayloads.homeRoom(homeRoomId));
            }
            if (emailValidated > 0L) {
                sendToSocket(socketIndex, UserPayloads.emailStatus(emailValidated));
            }
            sendToSocket(socketIndex, "@a" + "com.server.socket.location" + '\2' + "invalid.location" + '\2');
            if (AppConfigState.instance().settingsCache().longValueOrDefault("com.client.motd.message.enabled", 0) != 0L) {
                String motdMessage = AppConfigState.instance().settingsCache().valueOrDefault("com.client.motd.message", "").replace("\\n", "\n");
                if (!motdMessage.isEmpty()) {
                    sendToSocket(socketIndex, WireEncoding.encodeBase64Length(motdMessage.length())
                        + " " + motdMessage + '\2');
                }
            }
            sendToSocket(socketIndex, SocialLookups.badgeDisplayPayload(userId, userDao()));
            sendToSocket(socketIndex, SocialLookups.tagDisplayPayload(userId, userDao()));
            long favouriteGroupId = loginUser.favouriteGroupId();
            if (favouriteGroupId > 0L) {
                UserGroupRow groupRow = users.userGroup(favouriteGroupId).orElse(null);
                if (groupRow != null) {
                    String groupPayload = UserPayloads.loginGroup(favouriteGroupId, groupRow);
                    sendToSocket(socketIndex, groupPayload);
                }
            }
            return userId;
        } catch (Exception ignored) {
            if (socketIndex > 0) {
                disconnectSocket(socketIndex);
            }
            return "";
        }
    }

    /**
     * Original function: Proc_6_165_7BE0B0.
     */
    public static String sendMessengerFriendOnlineNotification(int socketIndex, int targetSocketIndex) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            MessengerFriend friendSummary = MessengerLookups.friendSummary(userId, messengerDao());
            if (friendSummary == null) {
                return "";
            }
            String notifyPayload = MessengerViews.friendOnlineNotification(friendSummary, 1L);
            if (targetSocketIndex > 0) {
                if (Guardian.isSocketConnected(targetSocketIndex)) {
                    sendToSocket(targetSocketIndex, notifyPayload);
                }
            } else {
                MessengerDao messenger = messengerDao();
                if (messenger == null) {
                    return "";
                }
                for (long friendSocketIndex : messenger.acceptedFriendSocketIndexes(NumberUtils.parseLong(userId))) {
                    targetSocketIndex = (int) friendSocketIndex;
                    if (targetSocketIndex > 0 && Guardian.isSocketConnected(targetSocketIndex)) {
                        sendToSocket(targetSocketIndex, notifyPayload);
                    }
                }
            }
            return notifyPayload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_170_7C1100.
     */
    public static long deleteMessengerFriendRequests(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            MessengerWire.FriendTargetList targets = MessengerWire.friendDeleteTargetsFromPayload(packetPayload);
            if (targets.deleteAllPending()) {
                MessengerDao messenger = messengerDao();
                if (messenger == null) {
                    return 0L;
                }
                messenger.deletePendingRequests(NumberUtils.parseLong(userId));
                return 1L;
            }
            if (targets.targetIds().isEmpty()) {
                return 0L;
            }
            MessengerDao messenger = messengerDao();
            if (messenger == null) {
                return 0L;
            }
            messenger.deletePendingRequests(NumberUtils.parseLong(userId), targets.targetIds());
            return 1L;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_171_7C1520.
     */
    public static String removeMessengerFriends(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            MessengerDao messenger = messengerDao();
            if (messenger == null) {
                return "";
            }
            MessengerWire.FriendTargetList targets =
                MessengerWire.friendRemoveTargetsFromPayload(packetPayload, userId);
            RemovedFriendships removed = MessengerLookups.removeAcceptedFriends(
                NumberUtils.parseLong(userId), targets.targetIds(), messenger);
            if (!removed.valid()) {
                return "";
            }
            for (long targetUserId : removed.targetUserIds()) {
                int targetSocketIndex = handlingSocketFromUserId(String.valueOf(targetUserId));
                if (targetSocketIndex > 0) {
                    sendToSocket(targetSocketIndex, removed.notificationPayload());
                }
            }
            sendToSocket(socketIndex, removed.callerPayload());
            return removed.callerPayload();
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_172_7C25B0.
     */
    public static String searchMessengerUsers(int socketIndex, MessengerWire.SearchRequest request) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            String searchText = request.searchText();
            if (searchText.isEmpty()) {
                return "";
            }
            MessengerDao messenger = messengerDao();
            if (messenger == null) {
                return "";
            }
            String resultPayload = MessengerLookups.searchResultsPayload(NumberUtils.parseLong(userId), searchText, messenger);
            sendToSocket(socketIndex, resultPayload);
            return resultPayload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_173_7C3430.
     */
    public static String sendMessengerPrivateMessage(int socketIndex, MessengerPrivateMessage message) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            if (!message.valid()) {
                return "";
            }
            int targetSocketIndex = handlingSocketFromUserId(String.valueOf(message.targetUserId()));
            if (targetSocketIndex <= 0) {
                return "";
            }
            long currentRoomId = handlingCurrentRoomId(socketIndex, userId);
            MessengerDao messenger = messengerDao();
            if (messenger == null) {
                return "";
            }
            String payload = MessengerLookups.privateMessagePayload(
                NumberUtils.parseLong(userId),
                message.targetUserId(),
                currentRoomId,
                handlingUserName(String.valueOf(message.targetUserId())),
                message.messageText(),
                ChatLookups.filterMessage(message.messageText()),
                socketIndex,
                messenger);
            sendToSocket(targetSocketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_174_7C3BC0.
     */
    public static String requestMessengerFriend(int socketIndex, MessengerWire.FriendRequest requestPayload) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            String targetName = requestPayload.targetName();
            if (targetName.isEmpty()) {
                return "";
            }
            MessengerDao messenger = messengerDao();
            if (messenger == null) {
                return "";
            }
            long callerUserId = NumberUtils.parseLong(userId);
            MessengerFriendRequest request = MessengerLookups.requestFriend(
                callerUserId, handlingUserName(userId), targetName, messenger);
            if (!request.valid()) {
                return "";
            }
            if (request.hasTargetNotification()) {
                int targetSocketIndex = handlingSocketFromUserId(String.valueOf(request.targetUserId()));
                if (targetSocketIndex > 0) {
                    sendToSocket(targetSocketIndex, request.targetNotificationPayload());
                }
            }
            sendToSocket(socketIndex, request.callerPayload());
            return request.callerPayload();
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_175_7C4800.
     */
    public static String sendMessengerPendingRequests(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            MessengerDao messenger = messengerDao();
            if (messenger == null) {
                return "";
            }
            List<PendingFriendRequest> requests = messenger.pendingRequests(NumberUtils.parseLong(userId));
            String payload = MessengerPayloads.pendingRequests(requests);
            sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_176_7C4EE0.
     */
    public static String sendMessengerFriendList(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            MessengerDao messenger = messengerDao();
            if (messenger == null) {
                return "";
            }
            MessengerFriendList friendList = MessengerLookups.friendList(NumberUtils.parseLong(userId), messenger);
            String onlineNotificationPayload = friendList.onlineNotificationPayload();
            List<Long> onlineFriendIds = new ArrayList<>();
            for (MessengerFriend friend : friendList.friends()) {
                if (friend != null) {
                    int friendSocketIndex = (int) friend.socketIndex();
                    long friendOnline = friendSocketIndex > 0 && Guardian.isSocketConnected(friendSocketIndex) ? 1L : 0L;
                    if (friendOnline == 1L) {
                        onlineFriendIds.add(friend.userId());
                    }
                    if (friendOnline == 1L && !onlineNotificationPayload.isEmpty()) {
                        sendToSocket(friendSocketIndex, onlineNotificationPayload);
                    }
                }
            }
            String payload = friendList.listPayload(onlineFriendIds);
            sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_177_7C6580.
     */
    public static String sendPetRaceList(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            PetWire.RaceListRequest request = PetWire.raceListRequest(packetPayload);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            String productPet = request.productPet();
            if (productPet.isEmpty()) {
                return "";
            }
            BotDao bots = botDao();
            if (bots == null) {
                return "";
            }
            String payload = PetLookups.raceListPayload(userId, productPet, bots, userDao());
            sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_178_7C6E60.
     */
    public static String sendPetInventory(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            BotDao bots = botDao();
            if (bots == null) {
                return "";
            }
            String payload = PetLookups.inventoryPayload(NumberUtils.parseLong(userId), bots);
            sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_179_7C7790.
     */
    public static long placePetInRoom(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            if (NumberUtils.parseLong(AppConfigState.instance().settingsCache().valueOrDefault("com.client.rooms.bots.pets.enabled", "0")) == 0L) {
                return 0L;
            }
            PetWire.RoomPlacementRequest request = PetWire.roomPlacementRequest(packetPayload);
            long petId = request.petId();
            long positionX = request.positionX();
            long positionY = request.positionY();
            long positionR = request.rotation();
            if (socketIndex <= 0 || petId <= 0L) {
                return 0L;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return 0L;
            }
            RoomDao rooms = roomDao();
            BotDao bots = botDao();
            if (rooms == null || bots == null) {
                return 0L;
            }
            PetPlacementAction placement = PetLookups.placementAction(
                petId, NumberUtils.parseLong(userId), roomId, positionX, positionY, positionR, bots, rooms);
            if (!placement.valid()) {
                return 0L;
            }
            bots.placeBotInRoom(
                placement.botId(),
                placement.roomId(),
                placement.positionX(),
                placement.positionY(),
                placement.positionZ(),
                placement.positionR());
            if (placement.hasRoomEntryPayload()) {
                broadcastToCurrentRoom(socketIndex, placement.roomEntryPayload());
            }
            sendToSocket(socketIndex, placement.placedPayload());
            return placement.botEntityId();
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_180_7C96F0.
     */
    public static long pickUpPetFromRoom(int socketIndex, long botEntityId) {
        try {
            if (botEntityId <= 0L) {
                return 0L;
            }
            BotDao bots = botDao();
            if (bots == null) {
                return 0L;
            }
            PetPickupAction pickup = PetLookups.pickupAction(botEntityId, bots);
            if (!pickup.valid()) {
                return 0L;
            }
            bots.clearBotRoom(pickup.botId());
            bots.touchPetData(pickup.botId());
            broadcastToCurrentRoom(socketIndex, pickup.removedPayload());
            if (pickup.hasInventoryAddPayload()) {
                sendToSocket(socketIndex, pickup.inventoryAddPayload());
            }
            PetLookups.completePickup(pickup);
            return pickup.botId();
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_182_7CAAD0.
     */
    public static String validatePetName(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String requestedName = PetWire.nameValidationRequest(packetPayload).petName();
            String payload = PetLookups.nameValidationPayload(requestedName);
            sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_183_7CABF0.
     */
    public static String sendPetStatus(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            long requestedId = PetWire.petIdRequest(packetPayload, packetPrefix).petId();
            if (socketIndex <= 0 || requestedId <= 0L) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            BotDao bots = botDao();
            if (bots == null) {
                return "";
            }
            String payload = PetLookups.statusPayload(requestedId, bots);
            if (!payload.isEmpty()) {
                sendToSocket(socketIndex, payload);
            }
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_184_7CBDA0.
     */
    public static String sendPetCommandList(int socketIndex, long petLevel) {
        try {
            String payload = petSettings().commandListPayload(petLevel);
            if (socketIndex > 0) {
                sendToSocket(socketIndex, payload);
            }
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_7CC190.
     */
    public static String sendPetCommandListForTarget(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            long requestedId = PetWire.petIdRequest(packetPayload, packetPrefix).petId();
            long petLevel = 0L;
            String userId = handlingUserIdFromSocket(socketIndex);
            if (!userId.isEmpty() && !"0".equals(userId)) {
                BotDao bots = botDao();
                if (bots != null) {
                    petLevel = PetLookups.levelForOwnerTarget(requestedId, NumberUtils.parseLong(userId), bots);
                }
            }
            return sendPetCommandList(socketIndex, petLevel);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_7CA730.
     */
    public static long performPetCommand(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            if (socketIndex <= 0) {
                return 0L;
            }
            PetWire.CommandRequest request = PetWire.commandRequest(packetPayload);
            long requestedId = request.petId();
            long commandId = request.commandId();
            if (requestedId <= 0L || commandId <= 0L) {
                return 0L;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return 0L;
            }
            BotDao bots = botDao();
            if (bots == null) {
                return 0L;
            }
            PetCommandExecution execution = PetLookups.commandExecution(
                requestedId, commandId, roomId, petSettings().commands(), AppConfigState.instance().settingsCache(), bots);
            if (!execution.valid()) {
                return 0L;
            }
            if (execution.hasActionPayload()) {
                broadcastToRoomUsers(roomId, execution.actionPayload());
            }
            if (execution.hasSpeechPayload()) {
                broadcastToRoomUsers(roomId, execution.speechPayload());
            } else if (execution.shouldAwardExperience()) {
                awardPetExperience(execution.botEntityId(), execution.experienceDelta());
            }
            return commandId;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_185_7CC2D0.
     */
    public static long awardPetExperience(long botEntityId, long experienceDelta) {
        try {
            if (botEntityId <= 0L) {
                return 0L;
            }
            BotDao bots = botDao();
            if (bots == null) {
                return 0L;
            }
            PetExperienceAward award = PetLookups.experienceAward(
                botEntityId, experienceDelta, AppConfigState.instance().settingsCache(), bots);
            if (!award.valid()) {
                return 0L;
            }
            if (award.hasLevelSpeechPayload()) {
                broadcastToRoomUsers(award.roomId(), award.levelSpeechPayload());
            }
            bots.updatePetExperience(award.botId(), award.petLevel(), award.petExperience());
            if (award.hasRoomPayloads()) {
                broadcastToRoomUsers(award.roomId(), award.statusPayload());
                broadcastToRoomUsers(award.roomId(), award.experiencePayload());
            }
            return award.petLevel();
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_186_7CD040.
     */
    public static long scratchPet(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            long requestedId = PetWire.petIdRequest(packetPayload, packetPrefix).petId();
            if (socketIndex <= 0 || requestedId <= 0L) {
                return 0L;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            long userIdValue = NumberUtils.parseLong(userId);
            UserDao users = userDao();
            BotDao bots = botDao();
            if (users == null || bots == null) {
                return 0L;
            }
            PetScratchAction scratch = PetLookups.scratchAction(requestedId, userIdValue, bots, users);
            if (!scratch.valid()) {
                return 0L;
            }
            bots.updatePetScratches(scratch.botId(), scratch.scratches());
            users.spendScratch(userIdValue);
            broadcastToCurrentRoom(socketIndex, scratch.payload());
            return scratch.scratches();
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_188_7CF3C0.
     */
    public static long spawnTutorialGuideBot(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            if (socketIndex <= 0) {
                return 0L;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            long userIdValue = NumberUtils.parseLong(userId);
            UserDao users = userDao();
            RoomDao rooms = roomDao();
            BotDao bots = botDao();
            if (users == null || rooms == null || bots == null) {
                return 0L;
            }
            long tutorialGuide = users.tutorialGuide(userIdValue);
            if (tutorialGuide == 0L) {
                users.markTutorialGuide(userIdValue);
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return 0L;
            }
            PetTutorialGuideSpawn spawn = PetLookups.tutorialGuideSpawn(
                roomId, AppConfigState.instance().settingsCache(), bots, rooms);
            if (spawn.valid()) {
                sendToSocket(socketIndex, spawn.payload());
            }
            return spawn.botEntityId();
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_189_7D0630.
     */
    public static long removeTutorialGuideBots(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            long requestedEntityId = PetWire.petIdRequest(packetPayload, packetPrefix).petId();
            if (socketIndex <= 0) {
                return 0L;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return 0L;
            }
            RoomDao rooms = roomDao();
            if (rooms == null) {
                return 0L;
            }
            PetTutorialGuideRemoval removal = PetLookups.tutorialGuideRemoval(
                requestedEntityId, roomId, AppConfigState.instance().settingsCache(), rooms);
            if (!removal.hasRemovals()) {
                return 0L;
            }
            for (String removedPayload : removal.removedPayloads()) {
                broadcastToRoomUsers(roomId, removedPayload);
            }
            return removal.removedCount();
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_190_7D11D0.
     */
    public static String sendRoomUserProfile(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            SocialWire.RoomUserIndexRequest request =
                SocialWire.roomUserIndexRequest(packetPayload, packetPrefix);
            long requestedRoomUserIndex = request.roomUserIndex();
            if (socketIndex <= 0 || requestedRoomUserIndex <= 0L) {
                return "";
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return "";
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L) {
                return "";
            }
            SocialLookups.DirectPayload action =
                SocialLookups.roomUserProfileAction(roomId, requestedRoomUserIndex, roomDao());
            if (!action.hasPayload()) {
                return "";
            }
            String payload = action.payload();
            sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_191_7D18B0.
     */
    public static String sendUserTags(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            if (socketIndex <= 0) {
                return "";
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            SocialLookups.DirectPayload action = SocialLookups.tagDisplayAction(
                callerUserId, handlingCurrentRoomId(socketIndex, callerUserId), packetPayload, userDao());
            if (!action.hasPayload()) {
                return "";
            }
            String payload = action.payload();
            sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_192_7D1B80.
     */
    public static String lookAtRoomUserBadge(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            SocialWire.RoomUserIndexRequest request =
                SocialWire.roomUserIndexRequest(packetPayload, packetPrefix);
            long requestedRoomUserIndex = request.roomUserIndex();
            if (socketIndex <= 0 || requestedRoomUserIndex <= 0L) {
                return "";
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return "";
            }
            long callerRoomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (callerRoomId <= 0L) {
                return "";
            }
            long callerRoomUserIndex = representedRoomUserIndex(socketIndex, callerUserId);
            RoomUserTargetRow target = RoomLookups.activeRoomUserTarget(
                callerRoomId, requestedRoomUserIndex, roomDao()).orElse(null);
            SocialLookups.RoomUserBadgeLook look =
                SocialLookups.roomUserBadgeLookAction(callerRoomUserIndex, target, userDao());
            if (!look.hasDirectPayload()) {
                return "";
            }
            String targetBadgePayload = look.directPayload();
            sendToSocket(socketIndex, targetBadgePayload);
            RoomUserStatusPayloads statusPayloads = look.statusPayloads();
            if (statusPayloads.hasCallerPayload()) {
                broadcastToCurrentRoom(socketIndex, statusPayloads.callerPayload());
            }
            if (statusPayloads.hasTargetPayload()) {
                broadcastToCurrentRoom(socketIndex, statusPayloads.targetPayload());
            }
            return targetBadgePayload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_193_7D2BB0.
     */
    public static String sendBadgeInventory(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            if (socketIndex <= 0) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            UserDao users = userDao();
            if (users == null) {
                return "";
            }
            BadgeInventoryPayload badgePayload = SocialLookups.badgeInventoryPayload(userId, users);
            sendToSocket(socketIndex, badgePayload.inventoryPayload());
            sendToSocket(socketIndex, badgePayload.displayPayload());
            return badgePayload.inventoryPayload();
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_194_7D3180.
     */
    public static String updateEquippedBadges(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            if (socketIndex <= 0) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            UserDao users = userDao();
            if (users == null) {
                return "";
            }
            SocialLookups.BadgeUpdateResult update =
                SocialLookups.updateEquippedBadges(userId, SocialWire.badgeUpdateSelections(packetPayload), users);
            if (!update.hasDisplayPayload()) {
                return "";
            }
            sendToSocket(socketIndex, update.displayPayload());
            if (handlingCurrentRoomId(socketIndex, userId) > 0L) {
                broadcastToCurrentRoom(socketIndex, update.displayPayload());
            }
            return update.equippedPayload();
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_197_7D43C0.
     */
    public static String lookTowardRoomPosition(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            if (socketIndex <= 0) {
                return "";
            }
            RoomWire.PositionRequest request = RoomWire.positionRequest(packetPayload, packetPrefix);
            long lookX = request.positionX();
            long lookY = request.positionY();
            if (lookX < 0L || lookY < 0L) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return "";
            }
            long roomSlot = socketIndex;
            RoomUserPosition current = RoomUserPosition.from(RoomState.instance().representedRooms().movementPosition(roomSlot, socketIndex));
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
    public static String walkTowardRoomPosition(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            if (socketIndex <= 0) {
                return "";
            }
            RoomWire.PositionRequest request = RoomWire.positionRequest(packetPayload, packetPrefix);
            long targetX = request.positionX();
            long targetY = request.positionY();
            if (targetX < 0L || targetY < 0L) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L || RoomPositionService.roomPositionAvailable(roomId, targetX, targetY) == 0L) {
                return "";
            }
            long roomSlot = socketIndex;
            RoomUserPosition current = RoomUserPosition.from(RoomState.instance().representedRooms().movementPosition(roomSlot, socketIndex));
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
     * Original function: Proc_6_199_7D54E0.
     */
    public static String recordPollExit(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            PollLookups.RoomRequest request = PollLookups.roomRequest(socketIndex, userDao(), roomDao());
            PollLookups.recordExit(request, PollWire.idFromWire(packetPayload, packetPrefix), pollDao());
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_200_7D5770.
     */
    public static String submitPollAnswer(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            PollLookups.RoomRequest request = PollLookups.roomRequest(socketIndex, userDao(), roomDao());
            PollLookups.submitAnswer(request, PollWire.answerFromWire(packetPayload, packetPrefix), pollDao());
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_201_7D5AC0.
     */
    public static String sendLivePoll(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            PollLookups.RoomRequest request = PollLookups.roomRequest(socketIndex, userDao(), roomDao());
            String payload = PollLookups.livePollPayload(request, PollWire.idFromWire(packetPayload, packetPrefix), pollDao());
            if (!payload.isEmpty()) {
                sendToSocket(socketIndex, payload);
            }
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_202_7D6760.
     */
    public static String submitRecyclerItems(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            if (socketIndex <= 0) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            RecyclerLookups.SubmitResult result = RecyclerLookups.submitItems(
                userId, RecyclerWire.selectionFromWire(packetPayload),
                recyclerSettings(), furnitureDao(), catalogDao(), recyclerDao());
            if (!result.valid()) {
                return "";
            }
            sendToSocket(socketIndex, result.deliveryPayloads());
            String payload = result.rewardPayload();
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_203_7D7F80.
     */
    public static String sendRecyclerStatus(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String payload = RecyclerLookups.statusPayload();
            sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_204_7D82E0.
     */
    public static String grantAchievementReward(int socketIndex, long achievementIndex, long badgeLevel) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            AchievementRewardGrant grant = AchievementLookups.grantReward(
                userId, achievementIndex, badgeLevel, achievementSettings(), userDao());
            if (!grant.valid()) {
                return "";
            }
            String payload = grant.rewardPayload();
            sendToSocket(socketIndex, grant.deliveryPayloads());
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_205_7D9780.
     */
    public static String advanceAchievementProgress(int socketIndex, long achievementQuestId) {
        try {
            if (socketIndex <= 0 || achievementQuestId <= 0L) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            AchievementRewardGrant grant = AchievementLookups.advanceProgress(
                userId, achievementQuestId, achievementSettings(), userDao());
            if (grant.valid()) {
                sendToSocket(socketIndex, grant.deliveryPayloads());
            }
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_206_7DA450.
     */
    public static String sendAchievementList(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            String payload = AchievementLookups.listPayload(userId, achievementSettings(), userDao());
            if (payload.isEmpty()) {
                return "";
            }
            sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_207_7DB0D0.
     */
    public static long triggerRepresentedWired(int socketIndex, long fallbackRoomId, long triggerCode) {
        return representedWiredTrigger(socketIndex, fallbackRoomId, triggerCode);
    }

    /**
     * Original function: Proc_6_208_7DC030.
     */
    public static long runRepresentedWiredAction506(int socketIndex, long fallbackRoomId, long selectedFurnitureId) {
        return representedWiredAction(socketIndex, fallbackRoomId, selectedFurnitureId, 506);
    }

    /**
     * Original function: Proc_6_209_7DE480.
     */
    public static long runRepresentedWiredAction505(int socketIndex, long fallbackRoomId, long selectedFurnitureId) {
        return representedWiredAction(socketIndex, fallbackRoomId, selectedFurnitureId, 505);
    }

    /**
     * Original function: Proc_6_219_7EA390.
     */
    public static String editWiredTrigger(int socketIndex, String packetPrefix, String packetPayload) {
        return representedWiredEdit(socketIndex, packetPayload, packetPrefix, 1, 500, "wired_trigger", false);
    }

    /**
     * Original function: Proc_6_220_7EBA50.
     */
    public static String editWiredAction(int socketIndex, String packetPrefix, String packetPayload) {
        return representedWiredEdit(socketIndex, packetPayload, packetPrefix, 501, 1000, "wired_action", true);
    }

    /**
     * Original function: Proc_6_221_7ED1E0.
     */
    public static String createWiredSnapshot(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            WiredLookups.RoomRequest request = WiredLookups.roomRequest(socketIndex, userDao(), roomDao());
            return WiredLookups.createSnapshot(
                socketIndex, request, packetPayload, furnitureDao(), roomDao());
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_222_7ED710.
     */
    public static String editWiredCondition(int socketIndex, String packetPrefix, String packetPayload) {
        return representedWiredEdit(socketIndex, packetPayload, packetPrefix, 1001, 1500, "wired_condition", false);
    }

    /**
     * Original function: Proc_6_223_7EEDD0.
     */
    public static String sendJukeboxSongInfo(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            if (socketIndex <= 0) {
                return "";
            }
            String payload = JukeboxLookups.songInfoPayload(
                JukeboxRequests.songInfoFromWire(packetPayload), jukeboxDao());
            sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_224_7EF5A0.
     */
    public static String clearJukeboxSoundMarkers(int socketIndex, long roomId, long jukeboxId) {
        try {
            long effectiveRoomId = roomId > 0L ? roomId : JukeboxLookups.roomRequest(socketIndex, userDao(), roomDao()).roomId();
            RoomState.instance().setFurnitureRoomCache(JukeboxLookups.clearSoundMarkers(
                RoomState.instance().furnitureRoomCache(), effectiveRoomId, jukeboxId, jukeboxDao()));
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_225_7EFBD0.
     */
    public static String addJukeboxDisk(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            if (socketIndex <= 0) {
                return "";
            }
            JukeboxLookups.RoomRequest request = JukeboxLookups.roomRequest(socketIndex, userDao(), roomDao());
            JukeboxLookups.DiskChangeResult result =
                JukeboxLookups.addDiskAction(
                    request, JukeboxRequests.addRequestFromWire(packetPayload), jukeboxDao());
            if (!result.valid()) {
                return "";
            }
            sendToSocket(socketIndex, result.deliveryPayloads());
            return result.payload();
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_226_7F0B20.
     */
    public static String removeJukeboxDisk(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            if (socketIndex <= 0) {
                return "";
            }
            JukeboxLookups.RoomRequest request = JukeboxLookups.roomRequest(socketIndex, userDao(), roomDao());
            JukeboxLookups.DiskChangeResult result =
                JukeboxLookups.removeDiskAction(
                    request, JukeboxRequests.removeOrderFromWire(packetPayload), jukeboxDao());
            if (!result.valid()) {
                return "";
            }
            sendToSocket(socketIndex, result.deliveryPayloads());
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_227_7F2400.
     */
    public static String sendJukeboxPlaylist(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            if (socketIndex <= 0) {
                return "";
            }
            JukeboxLookups.RoomRequest request = JukeboxLookups.roomRequest(socketIndex, userDao(), roomDao());
            String payload = JukeboxLookups.playlistPayload(request, jukeboxDao());
            sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_228_7F2AF0.
     */
    public static String sendJukeboxDiskInventory(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            JukeboxLookups.RoomRequest request = JukeboxLookups.roomRequest(socketIndex, userDao(), roomDao());
            String payload = JukeboxLookups.diskInventoryPayload(request, jukeboxDao());
            sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_229_7F3070.
     */
    public static String broadcastJukeboxPlayback(int socketIndex, long roomId, long jukeboxId) {
        try {
            if (socketIndex <= 0) {
                return "";
            }
            long effectiveRoomId = roomId > 0L ? roomId : JukeboxLookups.roomRequest(socketIndex, userDao(), roomDao()).roomId();
            String payload = JukeboxLookups.playbackPayload(
                effectiveRoomId, jukeboxId, System.currentTimeMillis() / 1000L, jukeboxDao());
            if (payload.isEmpty()) {
                return "";
            }
            if (effectiveRoomId > 0L) {
                broadcastToRoomUsers(effectiveRoomId, payload);
            } else {
                broadcastToCurrentRoom(socketIndex, payload);
            }
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_230_7F3D20.
     */
    public static String updateUserMotto(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            UserLookups.UserRequest request = UserLookups.userRequest(socketIndex, userDao());
            String payload = UserLookups.updateMottoPayload(
                request, UserWire.mottoRequest(packetPayload), userDao());
            if (payload.isEmpty()) {
                return "";
            }
            sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_7F44D0.
     */
    public static long guideInviteUserIdFromWire(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            UserWire.GuideInviteRequest request = UserWire.guideInviteRequest(packetPayload);
            return request.userId();
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_231_7F4510.
     */
    public static String sendGuideInvitation(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            sendToSocket(socketIndex, "Ic" + "IQA");
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_232_7F45A0.
     */
    public static String acceptQuest(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            QuestWire.QuestIdRequest request = QuestWire.questIdRequest(packetPayload, packetPrefix);
            long requestedQuestId = request.questId();
            if (requestedQuestId <= 0L) {
                return "";
            }
            QuestAcceptResult result =
                QuestProgress.acceptQuest(userId, requestedQuestId, questSettings(), questDao());
            if (!result.accepted()) {
                return "";
            }
            if (result.complete()) {
                completeQuest(socketIndex, result.questId(), result.numericQuestId());
            } else {
                sendQuestList(socketIndex, "", "");
            }
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_233_7F5D60.
     */
    public static String autoAcceptNextQuest(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long requestedQuestId = QuestProgress.nextQuestIdForUser(userId, questSettings(), questDao());
            if (requestedQuestId > 0L) {
                acceptQuest(socketIndex, "p^", QuestPayloads.request(requestedQuestId));
            }
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_234_7F75C0.
     */
    public static String resetQuests(int socketIndex) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            QuestResetResult result = QuestProgress.resetQuests(userId, questSettings(), questDao());
            if (!result.reset()) {
                return "";
            }
            sendToSocket(socketIndex, result.deliveryPayloads());
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_235_7F77E0.
     */
    public static String refreshQuestProgress(int socketIndex) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            QuestProgressDecision decision = QuestProgress.refreshDecision(userId, questSettings(), questDao());
            if (decision.shouldComplete()) {
                completeQuest(socketIndex, decision.questId(), decision.numericQuestId());
            } else if (decision.shouldSendList()) {
                sendQuestList(socketIndex, "", "");
            }
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_236_7F8540.
     */
    public static String sendQuestList(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            String payload = QuestProgress.listPayload(userId, questSettings(), questDao());
            sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_237_7F9ED0.
     */
    public static String sendOwnProfile(int socketIndex, String packetPrefix, String packetPayload) {
        try {
            UserLookups.UserRequest request = UserLookups.userRequest(socketIndex, userDao());
            String payload = UserLookups.ownProfilePayload(request, userDao());
            if (!payload.isEmpty()) {
                sendToSocket(socketIndex, payload);
            }
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_7FA5A0.
     */
    public static String ignoreClientReadyPacket() {
        return "";
    }

    /**
     * Original function: Proc_6_238_7FA670.
     */
    public static String awardTimedActivityPoints(int socketIndex) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            UserActivityPoints.AwardBatch awardBatch = UserActivityPoints.timedActivityPointAwardBatch(
                socketIndex, userId, AppConfigState.instance().settingsCache(), userDao());
            sendToSocket(socketIndex, awardBatch.deliveryPayloads());
            return awardBatch.payload();
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_168_7C05F0.
     */
    public static String sendMessengerRoomInvite(int socketIndex, MessengerWire.RoomInviteRequest inviteRequest) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return "";
            }
            if (inviteRequest.targetCount() <= 0L) {
                return "";
            }
            long userIdValue = NumberUtils.parseLong(userId);
            String inviteText = inviteRequest.inviteText();
            String filteredText = ChatLookups.filterMessage(inviteText);
            MessengerRoomInvite invite = MessengerLookups.roomInvite(
                userIdValue,
                roomId,
                socketIndex,
                inviteRequest,
                filteredText,
                messengerDao(),
                targetUserId -> handlingSocketFromUserId(String.valueOf(targetUserId)),
                targetUserId -> handlingUserName(String.valueOf(targetUserId)));
            for (MessengerNotification notification : invite.deliveryPayloads()) {
                sendToSocket((int) notification.socketIndex(), notification.payload());
            }
            return invite.payload();
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_167_7BECA0.
     */
    public static String acceptMessengerFriendRequests(int socketIndex, MessengerWire.AcceptFriendRequests request) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long userIdValue = NumberUtils.parseLong(userId);
            MessengerDao messenger = messengerDao();
            if (messenger == null) {
                return "";
            }
            if (request.requestedCount() <= 0L) {
                return "";
            }
            String dateFormat = AppConfigState.instance().settingsCache().valueOrDefault("com.mysql.format.date", "%d-%m-%Y");
            String timeFormat = AppConfigState.instance().settingsCache().valueOrDefault("com.mysql.format.time", "%H:%i");
            String dateTimeFormat = StringUtils.sqlEscapedText(dateFormat + " " + timeFormat);
            AcceptedFriendRequests accepted = MessengerLookups.acceptPendingFriends(
                userIdValue, request.targetIds(), dateTimeFormat, messenger);
            if (!accepted.valid()) {
                return "";
            }
            for (MessengerNotification notification : accepted.deliveryPayloads()) {
                sendToSocket((int) notification.socketIndex(), notification.payload());
            }
            sendToSocket(socketIndex, accepted.callerPayload());
            return accepted.callerPayload();
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_169_7C0DC0.
     */
    public static String followMessengerFriend(int socketIndex, MessengerWire.FriendFollowRequest request) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long targetUserIdValue = request.targetUserId();
            if (targetUserIdValue <= 0L) {
                return "";
            }
            String targetUserId = String.valueOf(targetUserIdValue);
            int targetSocketIndex = handlingSocketFromUserId(targetUserId);
            if (targetSocketIndex <= 0) {
                return "";
            }
            long targetRoomId = handlingCurrentRoomId(targetSocketIndex, targetUserId);
            if (targetRoomId <= 0L) {
                return "";
            }
            long targetRoomUserIndex = representedRoomUserIndex(targetSocketIndex, targetUserId);
            String payload = MessengerLookups.followRoomPayload(
                NumberUtils.parseLong(userId),
                targetUserIdValue,
                targetRoomUserIndex,
                targetRoomId,
                messengerDao());
            if (payload.isEmpty()) {
                return "";
            }
            sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String handlingUserName(String userId) {
        try {
            if (StringUtils.text(userId).isEmpty() || "0".equals(StringUtils.text(userId))) {
                return "";
            }
            UserDao users = userDao();
            return users == null ? "" : users.name(NumberUtils.parseLong(userId));
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_241_7FC380.
     */
    public static void processPreSessionPacketBuffer(long socketIndex, String packetBuffer) {
        try {
            if (socketIndex <= 0L || !Guardian.isSocketConnected(socketIndex)) {
                return;
            }
            long packetCount = 0L;
            while (packetBuffer.length() > 2 && packetCount < 10L) {
                packetBuffer = packetBuffer.substring(1);
                long packetLength = WireEncoding.decodeBase64Length(StringUtils.left(packetBuffer, 2));
                if (packetLength <= 0L || packetBuffer.length() < packetLength + 2L) {
                    break;
                }
                String packetPayload = StringUtils.mid(packetBuffer, 3, (int) packetLength);
                String packetCode = StringUtils.left(packetPayload, 2);
                if (LifecycleState.instance().runtimeState().shouldTracePackets()) {
                    Console.logSourceLine("[" + socketIndex + "] " + packetPayload, "GAME", 16711680L);
                }
                dispatchPreReadyPacket((int) socketIndex, packetCode, packetPayload);
                packetCount++;
                packetBuffer = StringUtils.mid(packetBuffer, (int) packetLength + 3);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void dispatchPreReadyPacket(int socketIndex, String packetCode, String packetPayload) {
        try {
            switch (StringUtils.text(packetCode)) {
                case "~\u00e4": System.exit(0); break;
                case "oD": sendGuideInvitation(socketIndex, "oD", packetPayload); break;
                case "Gd": updateUserMotto(socketIndex, "Gd", packetPayload); break;
                case "C]": sendJukeboxSongInfo(socketIndex, "C]", packetPayload); break;
                case "C\u007f": addJukeboxDisk(socketIndex, "C\u007f", packetPayload); break;
                case "D@": removeJukeboxDisk(socketIndex, "D@", packetPayload); break;
                case "DC":
                    sendJukeboxPlaylist(socketIndex, "DC", packetPayload);
                    sendJukeboxDiskInventory(socketIndex, "DC", packetPayload);
                    break;
                case "AZ": returnRoomFurnitureToInventory(socketIndex, "AZ", packetPayload); break;
                case "AC": pickUpRoomFurniture(socketIndex, "AC", packetPayload); break;
                case "A[": moveFloorFurnitureInRoom(socketIndex, packetPayload); break;
                case "AI": moveFloorFurnitureInRoom(socketIndex, packetPayload); break;
                case "Ch": toggleFloorFurnitureState(socketIndex, packetPayload); break;
                case "FH": openFloorFurniturePackageOrToggleState(socketIndex, packetPayload); break;
                case "@B": loadCurrentRoomModel(socketIndex); break;
                case "rv": placeFloorFurnitureFromInventory(socketIndex, packetPayload); break;
                case "pa": sendToSocket(socketIndex, "J|H"); break;
                case "Ce": dispatchPreReadySoundSetting(socketIndex, packetPayload); break;
                case "Cy": break;
                case "pb": resetQuests(socketIndex); break;
                case "p^": acceptQuest(socketIndex, "p^", packetPayload); break;
                case "pc": autoAcceptNextQuest(socketIndex, "pc", packetPayload); break;
                case "p]": sendQuestList(socketIndex, "p]", packetPayload); break;
                case "GV": changeAvatarName(socketIndex, "GV", packetPayload); break;
                case "GW": checkAvatarName(socketIndex, "GW", packetPayload); break;
                case "F]": sendRecyclerStatus(socketIndex, "F]", packetPayload); break;
                case "F^": submitRecyclerItems(socketIndex, "F^", packetPayload); break;
                case "Cj": sendLivePoll(socketIndex, "Cj", packetPayload); break;
                case "Ck": recordPollExit(socketIndex, "Ck", packetPayload); break;
                case "Cl": submitPollAnswer(socketIndex, "Cl", packetPayload); break;
                case "EW": toggleDimmerState(socketIndex, "EW", packetPayload); break;
                case "AL": handlingSimpleFloorItemUse(socketIndex, packetPayload, "AL", -1L, false, RoomUserPosition.absent()); break;
                case "AM": handlingSimpleFloorItemUse(socketIndex, packetPayload, "AM", 0L, true, RoomUserPosition.absent()); break;
                case "FU": addTradeFurniture(socketIndex, "FU", packetPayload); break;
                case "AH": removeTradeFurniture(socketIndex, "AH", packetPayload); break;
                case "FR": confirmTrade(socketIndex, "FR", packetPayload); break;
                case "EV": updateDimmerPreset(socketIndex, "EV", packetPayload); break;
                case "EU": sendDimmerPresets(socketIndex); break;
                case "Er": sendAchievementList(socketIndex, "Er", packetPayload); break;
                case "CD": ignoreClientReadyPacket(); break;
                case "@G": sendOwnProfile(socketIndex, "@G", packetPayload); break;
                case "D{":
                case "Fe": break;
                case "@t": chatInCurrentRoom(socketIndex, "@t", packetPayload); break;
                case "@w": shoutInCurrentRoom(socketIndex, "@w", packetPayload); break;
                case "@x": whisperInCurrentRoom(socketIndex, "@x", packetPayload); break;
                case "Cd": sendUserEffectList(socketIndex, "EA", packetPayload); break;
                case "Et":
                case "Eu": activateUserEffect(socketIndex, packetCode, packetPayload); break;
                case "@Z": sendCachedRecyclerStatus(socketIndex, recyclerSettings().statusPayload(), "Gz"); break;
                case "oW": sendClubSubscriptionOffers(socketIndex, "GY", packetPayload); break;
                case "Cn": cancelLatestCallForHelp(socketIndex, "EG", packetPayload); break;
                case "A^": waveCurrentRoomUser(socketIndex, "A^", packetPayload); break;
                case "A]": danceCurrentRoomUser(socketIndex, "A]", packetPayload); break;
                case "GE": submitCallForHelp(socketIndex, "GE", packetPayload); break;
                case "F`": sendImportantFaqs(socketIndex); break;
                case "Fa": sendFaqCategories(socketIndex); break;
                case "Fb": sendFaqDescription(socketIndex, "Fb", packetPayload); break;
                case "Fc": searchFaqs(socketIndex, "Fc", packetPayload); break;
                case "Fd": sendCategoryFaqs(socketIndex, "Fd", packetPayload); break;
                case "Ae": dispatchPreReadyCatalogIndex(socketIndex); break;
                case "FC": sendCreatableRoomCount(socketIndex, "FC", packetPayload); break;
                case "@]": createRoom(socketIndex, "@]", packetPayload); break;
                case "Af": sendCatalogPage(socketIndex, "Af", packetPayload); break;
                case "Fv": sendNavigatorTagResults(socketIndex, "Fv", packetPayload); break;
                case "FG": enterRoomFromPayload(socketIndex, "FG", packetPayload); break;
                case "Bv": sendVisitRoomAdvertisement(socketIndex, "Bv", packetPayload); break;
                case "@{": sendCurrentRoomDecoration(socketIndex, "@{", packetPayload); break;
                case "Ew": sendWardrobeSlots(socketIndex, "Ew", packetPayload); break;
                case "Ex": saveWardrobeSlot(socketIndex, "Ex", packetPayload); break;
                case "@l": updateTutorialClothes(socketIndex, "@l", packetPayload); break;
                case "oC": sendCatalogGiftWrapOptions(socketIndex, "oC", packetPayload); break;
                case "oV": sendCatalogGiftAvailability(socketIndex, "oV", packetPayload); break;
                case "Ad": purchaseCatalogProduct(socketIndex, "Ad", packetPayload); break;
                case "GX": purchaseCatalogGift(socketIndex, "GX", packetPayload); break;
                case "GZ": sendClubGiftStatus(socketIndex, "GZ", packetPayload); break;
                case "G[": claimClubGift(socketIndex, "G[", packetPayload); break;
                case "Gc": toggleStaffPickedRoom(socketIndex, "Gc", packetPayload); break;
                case "GG": sendStaffRoomChatHistory(socketIndex, "GG", packetPayload); break;
                case "F@": setHomeRoom(socketIndex, "F@", packetPayload); break;
                case "FB": updateRoomIcon(socketIndex, "FB", packetPayload); break;
                case "Ab": followUserToRoom(socketIndex, "Ab", packetPayload); break;
                case "EZ": createRoomEvent(socketIndex, "EZ", packetPayload); break;
                case "E\\": editRoomEvent(socketIndex, "E\\", packetPayload); break;
                case "FP":
                case "FF": sendRoomSettings(socketIndex, "FF", packetPayload); break;
                case "FQ": updateRoomSettings(socketIndex, "FQ", packetPayload); break;
                case "@H": sendFavouriteRoomIds(socketIndex, "@H", packetPayload); break;
                case "@S": addFavouriteRoom(socketIndex, "@S", packetPayload); break;
                case "@T": removeFavouriteRoom(socketIndex, "@T", packetPayload); break;
                case "BW": sendRoomCategoryPayload(socketIndex, "BW", packetPayload); break;
                case "GI": StaffModerationPacketHandlers.sendCallForHelpChatLog(
                    socketIndex,
                    StaffWire.callForHelpChatLogRequest(packetPayload)); break;
                case "oj": editWiredTrigger(socketIndex, "oj", packetPayload); break;
                case "ok": editWiredAction(socketIndex, "ok", packetPayload); break;
                case "ol": editWiredCondition(socketIndex, "ol", packetPayload); break;
                case "on": createWiredSnapshot(socketIndex, "on", packetPayload); break;
                case "GH": StaffModerationPacketHandlers.sendRoomChatLog(
                    socketIndex,
                    StaffWire.roomChatLogRequest(packetPayload)); break;
                case "GK": StaffModerationPacketHandlers.sendRoomInfo(
                    socketIndex,
                    StaffWire.roomInfoRequest(packetPayload)); break;
                case "GF": sendStaffUserSummary(socketIndex, "GF", packetPayload); break;
                case "GJ": sendStaffRoomVisitHistory(socketIndex, "GJ", packetPayload); break;
                case "GM": sendStaffCaution(socketIndex, "GM", packetPayload); break;
                case "GN": sendStaffAlert(socketIndex, "GN", packetPayload); break;
                case "GO": staffKickUser(socketIndex, "GO", packetPayload); break;
                case "GP": staffBanUser(socketIndex, "GP", packetPayload); break;
                case "CH": moderateCurrentRoom(socketIndex, "CH", packetPayload); break;
                case "GB": moveCallForHelpToPickedTab(socketIndex, "GB", packetPayload); break;
                case "GC": moveCallForHelpToOpenTab(socketIndex, "GC", packetPayload); break;
                case "GD": closeCallForHelp(socketIndex, "GD", packetPayload); break;
                case "GL": lockCurrentRoomForModeration(socketIndex, "GL", packetPayload); break;
                case "Fw": sendEventCategoryNavigatorRooms(socketIndex, "Fw", packetPayload); break;
                case "Fn": sendPopularCategoryNavigatorRooms(socketIndex, "Fn", packetPayload); break;
                case "Fr": sendOwnedNavigatorRooms(socketIndex, "Fr", packetPayload); break;
                case "Fq": sendFriendCurrentNavigatorRooms(socketIndex, "Fq", packetPayload); break;
                case "Fp": sendFriendOwnedNavigatorRooms(socketIndex, "Fp", packetPayload); break;
                case "Fs": sendFavouriteNavigatorRooms(socketIndex, "Fs", packetPayload); break;
                case "Fo": sendTopRatedNavigatorRooms(socketIndex, "Fo", packetPayload); break;
                case "Ft": sendRecentlyVisitedNavigatorRooms(socketIndex, "Ft", packetPayload); break;
                case "E|": sendOfficialNavigator(socketIndex, "E|", packetPayload); break;
                case "Fu": sendNavigatorSearchResults(socketIndex, "Fu", packetPayload); break;
                case "E~": sendPopularNavigatorTags(socketIndex, "E~", packetPayload); break;
                case "EY": sendRoomDoorStatus(socketIndex, "EY", packetPayload); break;
                case "Gj": sendNewFriendRoom(socketIndex, "Gj", packetPayload); break;
                case "@L": sendMessengerFriendList(socketIndex, "@L", packetPayload); break;
                case "@u":
                case "Ao": sendRoomReady(socketIndex); break;
                case "@j": validatePetName(socketIndex, "@j", packetPayload); break;
                case "@f": deleteMessengerFriendRequests(socketIndex, "@f", packetPayload); break;
                case "DF": followMessengerFriend(socketIndex, MessengerWire.friendFollowRequest(packetPayload)); break;
                case "@O": break;
                case "D}":
                case "D~": dispatchPreReadyRoomUserState(socketIndex); break;
                case "@a": sendMessengerPrivateMessage(socketIndex, MessengerWire.privateMessageFromWire(packetPayload)); break;
                case "Ci": sendMessengerPendingRequests(socketIndex, "Ci", packetPayload); break;
                case "@b": sendMessengerRoomInvite(socketIndex, MessengerWire.roomInviteFromWire(packetPayload)); break;
                case "@e": acceptMessengerFriendRequests(socketIndex, MessengerWire.acceptFriendRequests(packetPayload)); break;
                case "@i": searchMessengerUsers(socketIndex, MessengerWire.searchRequest(packetPayload, "@i")); break;
                case "@g": requestMessengerFriend(socketIndex, MessengerWire.friendRequest(packetPayload, "@g")); break;
                case "@h": removeMessengerFriends(socketIndex, "@h", packetPayload); break;
                case "Fy": removeTutorialGuideBots(socketIndex, "Fy", packetPayload); break;
                case "Fx": spawnTutorialGuideBot(socketIndex, "Fx", packetPayload); break;
                case "Cg": sendRoomUserProfile(socketIndex, "Cg", packetPayload); break;
                case "DG": sendUserTags(socketIndex, "DG", packetPayload); break;
                case "B_": lookAtRoomUserBadge(socketIndex, "B_", packetPayload); break;
                case "B]": sendBadgeInventory(socketIndex, "B]", packetPayload); break;
                case "B^": updateEquippedBadges(socketIndex, "B^", packetPayload); break;
                case "pg": break;
                case "AK": lookTowardRoomPosition(socketIndex, "AK", packetPayload); break;
                case "AO": walkTowardRoomPosition(socketIndex, "AO", packetPayload); break;
                case "AG": requestInteraction(socketIndex, "AG", packetPayload); break;
                case "FT": sendInventoryToSocket(socketIndex); break;
                case "AB": applyRoomDecorationFurniture(socketIndex, "AB", packetPayload); break;
                case "BA": redeemVoucher(socketIndex, "BA", packetPayload); break;
                case "n\u007f": sendPetRaceList(socketIndex, "n\u007f", packetPayload); break;
                case "ny": sendPetStatus(socketIndex, "ny", packetPayload); break;
                case "nx": sendPetInventory(socketIndex, "nx", packetPayload); break;
                case "nz": placePetInRoom(socketIndex, "nz", packetPayload); break;
                case "p`":
                case "rt": sendPetPackagePreview(socketIndex, packetCode, packetPayload); break;
                case "n~": placePetFromPackage(socketIndex, "n~", packetPayload); break;
                case "n|": sendPetCommandListForTarget(socketIndex, "n|", packetPayload); break;
                case "n{": performPetCommand(socketIndex, "n{", packetPayload); break;
                case "n}": scratchPet(socketIndex, "n}", packetPayload); break;
                case "E[": deleteRoomEvent(socketIndex, "E[", packetPayload); break;
                case "A_": kickRoomUser(socketIndex, "A_", packetPayload); break;
                case "E@": banRoomUser(socketIndex, "E@", packetPayload); break;
                case "DE": rateCurrentRoom(socketIndex, "DE", packetPayload); break;
                case "D\u007f": revokeRoomRightByName(socketIndex, "D\u007f", packetPayload); break;
                case "EB": revokeRoomRightByTargetName(socketIndex, "EB", packetPayload); break;
                case "Bw": redeemCreditFurniture(socketIndex, "Bw", packetPayload); break;
                case "Aa": revokeRoomRights(socketIndex, "Aa", packetPayload); break;
                case "B[": revokeAllRoomRights(socketIndex, "B[", packetPayload); break;
                case "@W": deleteCurrentRoom(socketIndex, "@W", packetPayload); break;
                case "Es": giveRespect(socketIndex, "Es", packetPayload); break;
                case "FD": sendOfficialRoomModel(socketIndex, "FD", packetPayload); break;
                case "A`": grantRoomRight(socketIndex, "A`", packetPayload); break;
                case "AT": updateStickyNote(socketIndex, "AT", packetPayload); break;
                case "AS": sendStickyNote(socketIndex, "AS", packetPayload); break;
                case "AU": deleteStickyNote(socketIndex, "AU", packetPayload); break;
                case "A~":
                case "CW":
                case "Cf": break;
                case "FA": sendSingleRoomNavigatorInfo(socketIndex, "FA", packetPayload); break;
                case "FI": toggleWallFurnitureState(socketIndex, "FI", packetPayload); break;
                case "AN": openPresent(socketIndex, "AN", packetPayload); break;
                case "Aq":
                case "AE":
                case "FS":
                case "AF":
                    sendToSocket(socketIndex,
                        StringUtils.text(AppConfigState.instance().settingsCache().valueOrDefault("com.client.park.infobus.theme.title", "AQ")) + '\2');
                    break;
                case "oL": guideInviteUserIdFromWire(socketIndex, "oL", packetPayload); break;
                default:
                    if (LifecycleState.instance().runtimeState().debugLoggingEnabled()) {
                        Console.logSourceLine(packetPayload, "UNHANDLED -- index: " + socketIndex, 255L);
                    }
                    break;
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    private static void dispatchPreReadySoundSetting(int socketIndex, String packetPayload) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            UserLookups.updateSoundSetting(userId, UserWire.soundSettingRequest(packetPayload), userDao());
        } catch (Exception ignored) {
            // VB6 source suppresses dispatcher helper failures.
        }
    }

    private static void dispatchPreReadyCatalogIndex(int socketIndex) {
        try {
            String pageTree = catalogPages().defaultPageTree();
            sendToSocket(socketIndex, "A~IHHM" + '\2' + pageTree);
        } catch (Exception ignored) {
            // VB6 source suppresses dispatcher helper failures.
        }
    }

    private static void dispatchPreReadyRoomUserState(int socketIndex) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomUserIndex = representedRoomUserIndex(socketIndex, userId);
            if (roomUserIndex <= 0L) {
                return;
            }
            broadcastToCurrentRoom(socketIndex, SocialLookups.roomUserPreReadyPayload(roomUserIndex));
        } catch (Exception ignored) {
            // VB6 source suppresses dispatcher helper failures.
        }
    }

    /**
     * Original function: Proc_6_242_7FF0D0.
     */
    public static String clearSocketUser(int socketIndex) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (!userId.isEmpty() && !"0".equals(userId)) {
                UserDao users = userDao();
                if (users != null) {
                    users.clearSocket(NumberUtils.parseLong(userId));
                }
            }
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_243_7FFEB0.
     */
    public static void disconnectSocket(long socketIndex) {
        if (socketIndex <= 0) {
            return;
        }
        int socketIndexValue = (int) socketIndex;
        clearSocketUser(socketIndexValue);
        Guardian.setSocketConnected(socketIndexValue, false);
        Guardian.removeSocketMarker(socketIndexValue);
        SocketMarkerSet socketMarkers = SessionState.instance().socketMarkers();
        socketMarkers.remove(socketIndexValue);
        SessionState.instance().setSocketMarkers(socketMarkers);
        GameServerSessionState sessionState = SessionState.instance().gameServerSession();
        sessionState.removeSocket(socketIndexValue);
        SessionState.instance().setGameServerSession(sessionState);
    }

    /**
     * Original function: Proc_6_244_801E80.
     */
    public static void sendToSocket(int socketIndex, String payload) {
        if (socketIndex <= 0 || !Guardian.isSocketConnected(socketIndex)
            || SessionState.instance().representedSockets().isBusy(socketIndex)) {
            return;
        }
        MusConnectionManager.instance().sendData(socketIndex, StringUtils.text(payload) + '\1');
    }

    public static void sendToSocket(int socketIndex, List<String> payloads) {
        if (payloads == null) {
            return;
        }
        for (String payload : payloads) {
            sendToSocket(socketIndex, payload);
        }
    }

    /**
     * Original function: Proc_6_245_801FA0.
     * Original function: Proc_6_247_8027E0.
     */
    public static long broadcastToCurrentRoom(int socketIndex, String payload) {
        String userId = handlingUserIdFromSocket(socketIndex);
        long roomId = handlingCurrentRoomId(socketIndex, userId);
        return broadcastToRoomUsers(roomId, payload);
    }

    public static String handlingUserIdFromSocket(int socketIndex) {
        if (socketIndex <= 0) {
            return "";
        }
        long sessionUserId = SessionState.instance().sessionUserIdBySocket(socketIndex);
        if (sessionUserId > 0L) {
            return String.valueOf(sessionUserId);
        }
        UserDao users = userDao();
        if (users == null) {
            return "";
        }
        try {
            return String.valueOf(users.userIdBySocket(socketIndex));
        } catch (Exception ignored) {
            return "";
        }
    }

    public static int handlingSocketFromUserId(String userId) {
        String idText = String.valueOf(NumberUtils.parseLong(userId));
        if (idText.isEmpty() || "0".equals(idText)) {
            return 0;
        }
        long socketIndex = SessionState.instance().linkedUserSocketIndex(idText);
        if (socketIndex <= 0L) {
            UserDao users = userDao();
            if (users != null) {
                try {
                    socketIndex = users.socketByUserId(NumberUtils.parseLong(idText));
                } catch (Exception ignored) {
                    socketIndex = 0L;
                }
            }
        }
        return (int) socketIndex;
    }

    public static long handlingCurrentRoomId(int socketIndex, String userId) {
        long roomId = SessionState.instance().sessionCacheLong(String.valueOf(socketIndex), 1);
        if (roomId > 0L) {
            return roomId;
        }
        if (!StringUtils.text(userId).isEmpty() && !"0".equals(StringUtils.text(userId))) {
            RoomDao rooms = roomDao();
            if (rooms != null) {
                try {
                    roomId = rooms.currentRoomIdByUser(NumberUtils.parseLong(userId));
                } catch (Exception ignored) {
                    roomId = 0L;
                }
            }
        }
        if (roomId <= 0L) {
            RoomDao rooms = roomDao();
            if (rooms != null) {
                try {
                    roomId = rooms.roomIdBySlot(socketIndex);
                } catch (Exception ignored) {
                    roomId = 0L;
                }
            }
        }
        return roomId;
    }

    public static long representedRoomUserIndex(int socketIndex, String userId) {
        long roomUserIndex = 0L;
        RoomDao rooms = roomDao();
        if (rooms != null) {
            try {
                roomUserIndex = rooms.activeVisitIdByUser(NumberUtils.parseLong(userId));
            } catch (Exception ignored) {
                roomUserIndex = 0L;
            }
        }
        return roomUserIndex > 0L ? roomUserIndex : socketIndex;
    }

    private static void roomKickOrBanUser(int socketIndex, String packetPrefix, String packetPayload, boolean addRoomBan) {
        try {
            RoomWire.RoomUserTargetRequest request = RoomWire.roomUserTargetRequest(packetPayload, packetPrefix);
            String targetUserId = String.valueOf(request.targetUserId());
            if (targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return;
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long callerRoomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (callerRoomId <= 0L) {
                return;
            }
            int targetSocketIndex = handlingSocketFromUserId(targetUserId);
            if (targetSocketIndex <= 0) {
                return;
            }
            long targetRoomId = handlingCurrentRoomId(targetSocketIndex, targetUserId);
            if (targetRoomId != callerRoomId) {
                return;
            }
            if (!UserLookups.hasPermission(callerUserId, "fuse_kick", userDao(), AppConfigState.instance().permissionMatrix())
                || UserLookups.hasPermission(targetUserId, "fuse_unkickable", userDao(), AppConfigState.instance().permissionMatrix())) {
                return;
            }
            sendToSocket(targetSocketIndex, "@aXjO");
            sendRoomReady(targetSocketIndex);
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

    /**
     * Original function: Proc_6_246_8024C0.
     * Original function: Proc_6_248_802B80.
     */
    public static long broadcastToRoomUsers(long roomId, String payload) {
        if (roomId <= 0L || StringUtils.text(payload).isEmpty()) {
            return 0L;
        }
        try {
            RoomDao rooms = roomDao();
            if (rooms == null) {
                return 0L;
            }
            String sentMarkers = "";
            long sentCount = 0L;
            for (Long activeSocketIndex : rooms.activeSocketIndexesByRoomWithFallback(roomId)) {
                int socketIndex = activeSocketIndex == null ? 0 : activeSocketIndex.intValue();
                String marker = "[" + socketIndex + "]";
                if (socketIndex > 0 && !sentMarkers.contains(marker)) {
                    sendToSocket(socketIndex, payload);
                    sentMarkers += marker;
                    sentCount++;
                }
            }
            return sentCount;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_249_802F10.
     */
    public static long broadcastToStaffModerators(String payload) {
        if (StringUtils.text(payload).isEmpty()) {
            return 0L;
        }
        String sentMarkers = "";
        long sentCount = 0L;
        for (SessionRegistry.SocketSession session : SessionState.instance().socketSessions()) {
            String candidateUserId = String.valueOf(session.userId());
            int candidateSocket = session.socketIndex();
            if ("0".equals(candidateUserId)) {
                candidateUserId = handlingUserIdFromSocket(candidateSocket);
            }
            String marker = "[" + candidateSocket + "]";
            if (candidateSocket > 0 && !sentMarkers.contains(marker) && UserLookups.hasPermission(candidateUserId, "fuse_mod", userDao(), AppConfigState.instance().permissionMatrix())) {
                sendToSocket(candidateSocket, payload);
                sentMarkers += marker;
                sentCount++;
            }
        }
        try {
            UserDao users = userDao();
            if (users != null) {
                for (UserDao.UserSocket userSocket : users.activeUserSockets()) {
                    String candidateUserId = String.valueOf(userSocket.userId());
                    int candidateSocket = (int) userSocket.socketIndex();
                    String marker = "[" + candidateSocket + "]";
                    if (candidateSocket > 0 && !sentMarkers.contains(marker)
                        && UserLookups.hasPermission(candidateUserId, "fuse_mod", userDao(), AppConfigState.instance().permissionMatrix())) {
                        sendToSocket(candidateSocket, payload);
                        sentMarkers += marker;
                        sentCount++;
                    }
                }
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
        return sentCount;
    }

    private static String handlingRepresentedChatRoute(int socketIndex, String packetPayload, long chatType) {
        try {
            if (socketIndex <= 0) {
                return "";
            }
            SocialWire.RepresentedChatMessage chatMessage =
                SocialWire.representedChatMessage(packetPayload, chatType);
            String targetName = chatMessage.targetName();
            String messageText = chatMessage.messageText();
            if (messageText.isEmpty()) {
                return "";
            }
            if (chatType == 0L && messageText.startsWith(":")) {
                String commandPayload = ChatCommands.commandPayload(
                    messageText,
                    LifecycleState.instance().runtimeState().productName());
                if (commandPayload.isEmpty()) {
                    commandPayload = ChatCommands.dynamicCommandPayload(
                        messageText, SessionState.instance().socketSessions(), Handling::handlingUserName);
                }
                if (!commandPayload.isEmpty()) {
                    sendToSocket(socketIndex, commandPayload);
                    return commandPayload;
                }
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            long roomUserIndex = representedRoomUserIndex(socketIndex, userId);
            if (roomId <= 0L || roomUserIndex <= 0L) {
                return "";
            }
            long userRank = UserLookups.rank(userId, userDao());
            long hcLevel = UserLookups.hcLevel(userId, userDao());
            if (!ChatCommands.extractUrlList(messageText).isEmpty()
                && !AppConfigState.instance().permissionMatrix().allows(userRank, "", "fuse_can_chat_links", hcLevel)) {
                return "";
            }
            String filteredText = ChatLookups.filterMessage(messageText);
            if (filteredText.isEmpty()) {
                filteredText = messageText;
            }
            long gestureId = ChatLookups.gestureId(filteredText);
            RoomDao rooms = roomDao();
            if (rooms == null) {
                return "";
            }
            rooms.insertRoomChatLog(
                NumberUtils.parseLong(userId),
                roomId,
                filteredText,
                chatType,
                UserLookups.sessionId(userId, userDao()));
            String payload = UserPayloads.representedChat(roomUserIndex, filteredText, gestureId, chatType);
            if (chatType == 2L) {
                int targetSocketIndex = UserLookups.socketIndexForUserName(targetName, userDao());
                if (targetSocketIndex > 0) {
                    sendToSocket(targetSocketIndex, payload);
                    sendToSocket(socketIndex, payload);
                } else {
                    sendToSocket(socketIndex, payload);
                }
            } else {
                broadcastToCurrentRoom(socketIndex, payload);
            }
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    private static void staffDirectMessage(
        int socketIndex,
        String prefix,
        String packetPayload,
        String requiredPermission,
        String logType,
        boolean kickAfterSend,
        boolean requireOnlineTarget
    ) {
        try {
            StaffWire.DirectMessageRequest request = StaffWire.directMessageRequest(packetPayload, prefix);
            long targetUserId = request.targetUserId();
            String messageText = request.messageText();
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (targetUserId <= 0L || messageText.isEmpty()
                || callerUserId.isEmpty() || "0".equals(callerUserId)
                || !UserLookups.hasPermission(callerUserId, "fuse_mod", userDao(), AppConfigState.instance().permissionMatrix())
                || !UserLookups.hasPermission(callerUserId, requiredPermission, userDao(), AppConfigState.instance().permissionMatrix())
                || StaffPayloads.containsUnsafeAlert(messageText)) {
                return;
            }
            long callerUserIdValue = NumberUtils.parseLong(callerUserId);
            StaffModerationDao moderationDao = staffModerationDao();
            if (moderationDao == null) {
                return;
            }
            int targetSocketIndex = handlingSocketFromUserId(String.valueOf(targetUserId));
            if (requireOnlineTarget && targetSocketIndex <= 0) {
                return;
            }
            long currentRoomId = handlingCurrentRoomId(socketIndex, callerUserId);
            moderationDao.insertDirectModerationLog(
                NumberUtils.parseLong(logType),
                callerUserIdValue,
                targetUserId,
                currentRoomId,
                messageText,
                socketIndex);
            if (targetSocketIndex > 0) {
                sendToSocket(targetSocketIndex, StaffPayloads.alert(messageText));
                if (kickAfterSend) {
                    sendRoomReady(targetSocketIndex);
                }
            }
            if ("4".equals(logType)) {
                moderationDao.insertUserCaution(targetUserId, callerUserIdValue, messageText);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    private static void updateCallForHelpTab(int socketIndex, String prefix, String packetPayload, String tabId) {
        try {
            StaffWire.CallForHelpTabRequest request =
                StaffWire.callForHelpTabRequest(packetPayload, prefix);
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)
                || !UserLookups.hasPermission(callerUserId, "fuse_mod", userDao(), AppConfigState.instance().permissionMatrix())
                || !UserLookups.hasPermission(callerUserId, "fuse_receive_calls_for_help", userDao(), AppConfigState.instance().permissionMatrix())) {
                return;
            }
            List<Long> callForHelpIds = request.callForHelpIds();
            if (callForHelpIds.isEmpty()) {
                return;
            }
            StaffModerationDao moderationDao = staffModerationDao();
            if (moderationDao == null) {
                return;
            }
            moderationDao.moveCallForHelpToTab(callForHelpIds, NumberUtils.parseLong(tabId), NumberUtils.parseLong(callerUserId));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    private static void staffRoomHistory(int socketIndex, String prefix, String packetPayload, boolean includeChatRows) {
        try {
            StaffWire.HistoryRequest request =
                StaffWire.historyRequest(packetPayload, prefix, includeChatRows);
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId) || !UserLookups.hasPermission(callerUserId, "fuse_mod", userDao(), AppConfigState.instance().permissionMatrix())) {
                return;
            }
            if (includeChatRows && !UserLookups.hasPermission(callerUserId, "fuse_chatlog", userDao(), AppConfigState.instance().permissionMatrix())) {
                return;
            }
            long targetUserId = request.targetUserId();
            if (targetUserId <= 0L) {
                return;
            }
            StaffModerationDao moderationDao = staffModerationDao();
            if (moderationDao == null) {
                return;
            }
            StaffUserLookup targetUser = moderationDao.staffUserLookup(targetUserId).orElse(null);
            if (targetUser == null) {
                return;
            }
            targetUserId = targetUser.userId();
            if (targetUserId <= 0L) {
                return;
            }
            String responsePayload;
            if (includeChatRows) {
                responsePayload = StaffModerationLookups.roomChatHistoryResponse(targetUser, moderationDao);
            } else {
                responsePayload = StaffModerationLookups.roomVisitHistoryResponse(targetUser, moderationDao);
            }
            if (!responsePayload.isEmpty()) {
                sendToSocket(socketIndex, responsePayload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_96_747000.
     * Original function: Proc_6_97_747640.
     */
    public static String handlingSimpleFloorItemUse(
        int socketIndex,
        String packetPayload,
        String packetPrefix,
        long stateValue,
        boolean storeState,
        RoomUserPosition suppliedPosition
    ) {
        try {
            FurnitureWire.SimpleFloorItemUseRequest request =
                FurnitureWire.simpleFloorItemUseRequest(packetPayload, packetPrefix);
            String userId = handlingUserIdFromSocket(socketIndex);
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (userId.isEmpty() || "0".equals(userId) || roomId <= 0L) {
                return "";
            }
            long furnitureId = request.furnitureId();
            if (furnitureId <= 0L) {
                return "";
            }
            RoomDao rooms = roomDao();
            if (rooms == null) {
                return "";
            }
            long roomSlot = rooms.roomSlot(roomId);
            RoomUserPosition userPosition = suppliedPosition == null ? RoomUserPosition.absent() : suppliedPosition;
            if (!userPosition.found()) {
                userPosition = RoomUserPosition.from(
                    RoomState.instance().representedRooms().movementPosition(roomSlot, representedRoomUserIndex(socketIndex, userId)));
            }
            FurnitureLookups.SimpleFloorUse use = FurnitureLookups.simpleFloorUse(
                furnitureId,
                roomId,
                userPosition,
                stateValue,
                storeState,
                furnitureDao(),
                GameDataCaches.productCache());
            if (!use.valid()) {
                return "";
            }
            broadcastToCurrentRoom(socketIndex, use.payload());
            if (use.storeState()) {
                RoomState.instance().setFurnitureRoomCache(FurnitureStateWrites.refreshState(
                    RoomState.instance().furnitureRoomCache(), roomId, use.furnitureId(), use.stateValue()));
            } else {
                RoomState.instance().setFurnitureRoomCache(FurnitureStateWrites.trackMarker(
                    RoomState.instance().furnitureRoomCache(), roomId, use.furnitureId()));
            }
            return use.payload();
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original functions: Proc_6_141_76A670 and Proc_6_142_76B310.
     */
    public static String placeOrMoveFloorFurniture(
        int socketIndex,
        FurnitureWire.FloorPlacementRequest request,
        boolean fromInventory
    ) {
        try {
            FurnitureWire.FloorFurniturePlacement placement = request.placement();
            if (placement.furnitureId() <= 0L) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L || (!RoomLookups.userHasRoomRight(userId, roomId, roomDao())
                && !UserLookups.hasPermission(userId, "fuse_pick_up_any_furni", userDao(), AppConfigState.instance().permissionMatrix()))) {
                return "";
            }
            long userIdValue = NumberUtils.parseLong(userId);
            FurnitureLookups.FloorFurniturePlacement placementResult = FurnitureLookups.placeOrMoveFloorFurniture(
                placement, roomId, userIdValue, fromInventory, furnitureDao(), CatalogState.instance().registry());
            if (!placementResult.valid()) {
                return "";
            }
            if (placementResult.wallFurniture()) {
                if (fromInventory) {
                    FurnitureDao furniture = furnitureDao();
                    if (furniture == null) {
                        return "";
                    }
                    FurnitureDao.InventoryPlacementFurniture item = furniture
                        .inventoryPlacementFurniture(placement.furnitureId(), userIdValue)
                        .orElse(null);
                    placeWallFurnitureFromInventory(socketIndex, request.placementPayload(), item);
                }
                return "";
            }
            if (placementResult.hasInventoryRemovePayload()) {
                sendToSocket(socketIndex, placementResult.inventoryRemovePayload());
            }
            broadcastToCurrentRoom(socketIndex, placementResult.roomPayload());
            RoomCacheFiles.invalidateRoom(roomId);
            if (fromInventory) {
                sendInventoryToSocket(socketIndex);
            }
            return placementResult.roomPayload();
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    private static RecommendedRooms recommendedRooms() {
        return NavigatorState.instance().recommendedRooms();
    }

    private static RoomCategoryCache roomCategoryCache() {
        return NavigatorState.instance().roomCategoryCache();
    }

    private static VisitRoomAds visitRoomAds() {
        return AdvertisingState.instance().visitRoomAds();
    }

    private static HelpCenterCache helpCenterCache() {
        return HelpCenterState.instance().cache();
    }

    private static WiredSettings wiredSettings() {
        return WiredState.instance().settings();
    }

    private static MessengerSettings messengerSettings() {
        return MessengerState.instance().settings();
    }

    private static StaffSettings staffSettings() {
        return ModerationState.instance().staffSettings();
    }

    private static RecyclerSettings recyclerSettings() {
        return RecyclerState.instance().settings();
    }

    private static AchievementSettings achievementSettings() {
        return AchievementState.instance().settings();
    }

    private static QuestSettings questSettings() {
        return QuestState.instance().settings();
    }

    private static GiftSettings giftSettings() {
        return CatalogState.instance().giftSettings();
    }

    private static CatalogPages catalogPages() {
        return CatalogState.instance().catalogPages();
    }

    private static PetSettings petSettings() {
        return PetState.instance().settings();
    }

    /**
     * Original function: Proc_6_164_7BC820.
     */
    public static String completeQuest(int socketIndex, long questId, long numericQuestId) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            QuestDao quests = questDao();
            UserDao users = userDao();
            if (quests == null || users == null) {
                return "";
            }
            long userIdValue = NumberUtils.parseLong(userId);
            QuestCompletion completion = QuestProgress.completion(
                quests,
                QuestProgress.settingsFromSource(questSettings(), questDao()),
                userIdValue,
                questId,
                numericQuestId);
            if (!completion.valid()) {
                return "";
            }
            String completionPayload = completion.payload();
            sendToSocket(socketIndex, "Lb" + completionPayload);
            if (!completion.complete()) {
                return "";
            }
            if (completion.hasActivityPointReward()) {
                long currentPoints = users.activityPoints(userIdValue, completion.rewardType());
                users.addActivityPointsLimited(userIdValue, completion.rewardType(), completion.rewardAmount());
                sendToSocket(socketIndex,
                    UserPayloads.activityPointAward(completion.rewardType(), currentPoints + completion.rewardAmount()));
            }
            quests.completeQuest(userIdValue, completion.questId());
            sendToSocket(socketIndex, "La" + completionPayload);
            sendQuestList(socketIndex, "", "");
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    private static String representedWiredEdit(
        int socketIndex,
        String packetPayload,
        String packetCode,
        long minimumCode,
        long maximumCode,
        String cacheFolder,
        boolean includeExtraValue
    ) {
        try {
            WiredLookups.RoomRequest request = WiredLookups.roomRequest(socketIndex, userDao(), roomDao());
            return WiredLookups.editRecord(
                socketIndex,
                request,
                packetPayload,
                packetCode,
                minimumCode,
                maximumCode,
                cacheFolder,
                includeExtraValue,
                furnitureDao(),
                roomDao());
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_211_7E1E40.
     * Original function: Proc_6_212_7E36C0.
     * Original function: Proc_6_213_7E3FA0.
     * Original function: Proc_6_214_7E60C0.
     */
    private static long representedWiredTrigger(int socketIndex, long fallbackRoomId, long triggerCode) {
        try {
            long roomId = WiredLookups.roomRequest(socketIndex, userDao(), roomDao()).roomId();
            if (roomId <= 0L) {
                roomId = fallbackRoomId;
            }
            return WiredLookups.trigger(roomId, triggerCode, 0L, furnitureDao(), Handling::broadcastToRoomUsers);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_215_7E6770.
     * Original function: Proc_6_216_7E8120.
     * Original function: Proc_6_217_7E9780.
     */
    private static long representedWiredAction(int socketIndex, long fallbackRoomId, long selectedFurnitureId, long actionCode) {
        try {
            long roomId = WiredLookups.roomRequest(socketIndex, userDao(), roomDao()).roomId();
            if (roomId <= 0L) {
                roomId = fallbackRoomId;
            }
            return WiredLookups.action(roomId, actionCode, selectedFurnitureId, furnitureDao(), Handling::broadcastToRoomUsers);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    private static StaffModerationDao staffModerationDao() {
        return DaoProvider.staffModerationDao();
    }

    private static HelpDao helpDao() {
        return DaoProvider.helpDao();
    }

    private static UserDao userDao() {
        return DaoProvider.userDao();
    }

    private static ClubDao clubDao() {
        return DaoProvider.clubDao();
    }

    private static CatalogDao catalogDao() {
        return DaoProvider.catalogDao();
    }

    private static RoomDao roomDao() {
        return DaoProvider.roomDao();
    }

    private static FurnitureDao furnitureDao() {
        return DaoProvider.furnitureDao();
    }

    private static PackageDao packageDao() {
        return DaoProvider.packageDao();
    }

    private static VoucherDao voucherDao() {
        return DaoProvider.voucherDao();
    }

    private static PollDao pollDao() {
        return DaoProvider.pollDao();
    }

    private static JukeboxDao jukeboxDao() {
        return DaoProvider.jukeboxDao();
    }

    private static QuestDao questDao() {
        return DaoProvider.questDao();
    }

    private static RecyclerDao recyclerDao() {
        return DaoProvider.recyclerDao();
    }

    private static BotDao botDao() {
        return DaoProvider.botDao();
    }

    private static TradeDao tradeDao() {
        return DaoProvider.tradeDao();
    }

    private static MessengerDao messengerDao() {
        return DaoProvider.messengerDao();
    }

}
