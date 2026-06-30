package com.alphaseries;

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
import com.alphaseries.db.Database;
import com.alphaseries.game.jukebox.JukeboxPlaybackRow;
import com.alphaseries.game.jukebox.JukeboxRow;
import com.alphaseries.game.pet.BotRoomEntryRow;
import com.alphaseries.game.pet.PetCommandActionRow;
import com.alphaseries.game.pet.PetCommandTargetRow;
import com.alphaseries.game.pet.PetExperienceStateRow;
import com.alphaseries.game.pet.PetInventoryRow;
import com.alphaseries.game.pet.PetLevelExperienceRow;
import com.alphaseries.game.pet.PetPayloads;
import com.alphaseries.game.pet.PetPlacementRow;
import com.alphaseries.game.pet.PetScratchRow;
import com.alphaseries.game.pet.PetSettings;
import com.alphaseries.game.pet.PetStatusRow;
import com.alphaseries.game.pet.RepresentedBotEntry;
import com.alphaseries.game.pet.RepresentedBotRegistry;
import com.alphaseries.game.poll.PollDefinition;
import com.alphaseries.game.poll.PollPrompt;
import com.alphaseries.game.quest.QuestSettings;
import com.alphaseries.game.room.FurnitureRoomCache;
import com.alphaseries.game.room.RoomModelFurnitureRow;
import com.alphaseries.game.room.RoomObjectEntryPayloadArgs;
import com.alphaseries.game.room.RoomOccupantRow;
import com.alphaseries.game.room.RepresentedRoomCache;
import com.alphaseries.game.room.RepresentedRoomSlots;
import com.alphaseries.game.room.RoomUserEntryRow;
import com.alphaseries.game.room.RoomUserEntryPayloadArgs;
import com.alphaseries.game.room.RoomUserProfileRow;
import com.alphaseries.game.room.RoomUserTargetRow;
import com.alphaseries.game.session.GameServerSessionState;
import com.alphaseries.game.session.SessionRegistry;
import com.alphaseries.game.session.SocketMarkerSet;
import com.alphaseries.game.social.BadgeRow;
import com.alphaseries.game.trade.RepresentedInteractionPair;
import com.alphaseries.game.trade.RepresentedTradeOffer;
import com.alphaseries.game.trade.TradePayloads;
import com.alphaseries.game.user.ExpiredUserEffectRow;
import com.alphaseries.game.user.OwnProfileRow;
import com.alphaseries.game.user.UserEffectActivationRow;
import com.alphaseries.game.user.UserGroupRow;
import com.alphaseries.game.inventory.InventoryMessagePayloads;
import com.alphaseries.game.achievement.AchievementSettings;
import com.alphaseries.game.catalog.CatalogRegistry;
import com.alphaseries.game.catalog.GiftSettings;
import com.alphaseries.game.chat.ChatSettings;
import com.alphaseries.game.help.HelpCenterCache;
import com.alphaseries.game.messenger.MessengerFriend;
import com.alphaseries.game.messenger.MessengerSearchResult;
import com.alphaseries.game.messenger.PendingFriendRequest;
import com.alphaseries.game.navigator.NewFriendRooms;
import com.alphaseries.game.navigator.NavigatorRoom;
import com.alphaseries.game.moderation.StaffCallForHelpRow;
import com.alphaseries.game.moderation.StaffPayloads;
import com.alphaseries.game.moderation.StaffModerationPacketHandlers;
import com.alphaseries.game.moderation.StaffRoomChatRow;
import com.alphaseries.game.moderation.StaffRoomChatVisitRow;
import com.alphaseries.game.moderation.StaffRoomVisitRow;
import com.alphaseries.game.moderation.StaffUserLookup;
import com.alphaseries.game.moderation.StaffUserSummaryRow;
import com.alphaseries.game.recycler.RecyclerSettings;
import com.alphaseries.game.wired.WiredPayloads;
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
import com.alphaseries.messages.outgoing.SocialPayloads;
import com.alphaseries.messages.outgoing.UserPayloads;
import com.alphaseries.messages.outgoing.VoucherPayloads;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public final class Handling {
    private static List<RepresentedInteractionPair> representedInteractionPairs = new ArrayList<>();
    private static List<RepresentedTradeOffer> representedTradeOffers = new ArrayList<>();
    private static Map<Long, Long> representedActivityPointTicks = new HashMap<>();

    private Handling() {
    }

    public static void Proc_6_0_6D7FF0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "GF");
            long targetUserId = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            if (targetUserId <= 0L) {
                targetUserId = readWireLong(requestPayload, new LongRef(1));
            }
            if (targetUserId <= 0L) {
                return;
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId) || !handlingUserHasPermission(callerUserId, "fuse_mod")) {
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
                Proc_6_244_801E80(socketIndex, payload, 0);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_1_6D8B70(Object... args) {
        staffDirectMessage(args, "GM", "fuse_alert", "4", false, false);
    }

    public static void Proc_6_2_6D9880(Object... args) {
        staffDirectMessage(args, "GO", "fuse_kick", "5", true, false);
    }

    public static void Proc_6_3_6DA490(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "GP");
            LongRef offset = new LongRef(1);
            long targetUserId = readWireLong(requestPayload, offset);
            if (targetUserId <= 0L) {
                targetUserId = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            }
            String banMessage = Functions.Proc_10_10_80A7F0(readWireString(requestPayload, offset), 0, 0);
            if (banMessage.isEmpty()) {
                banMessage = Functions.Proc_10_10_80A7F0(Functions.Proc_10_7_80A190(requestPayload, 0, 0), 0, 0);
            }
            long banHours = readWireLong(requestPayload, offset);
            if (banHours <= 0L) {
                banHours = NumberUtils.parseLong(Functions.Proc_10_6_809F10(StringUtils.mid(requestPayload, (int) offset.value), 0, 0));
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (targetUserId <= 0L || banMessage.isEmpty() || banHours <= 0L
                || callerUserId.isEmpty() || "0".equals(callerUserId)
                || !handlingUserHasPermission(callerUserId, "fuse_mod")
                || !handlingUserHasPermission(callerUserId, "fuse_alert")
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
                Proc_6_244_801E80(targetSocketIndex, "@c" + banMessage + '\2', 0);
                Proc_6_243_7FFEB0(targetSocketIndex, 0, 0);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static long Proc_6_4_6DAFB0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "CH");
            LongRef offset = new LongRef(1);
            long actionType = readWireLong(requestPayload, offset);
            if (actionType <= 0L) {
                actionType = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            }
            String messageText = readWireString(requestPayload, offset);
            if (messageText.isEmpty()) {
                messageText = Functions.Proc_10_7_80A190(requestPayload, 0, 0);
            }
            messageText = Functions.Proc_10_10_80A7F0(messageText, 0, 0);
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId) || !handlingUserHasPermission(callerUserId, "fuse_mod")) {
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
                Functions.Proc_10_18_80C9E0(roomId, 0, 0);
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

    public static void Proc_6_5_6DC340(Object... args) {
        try {
            long callForHelpId = args != null && args.length >= 1 ? NumberUtils.parseLong(args[0]) : 0L;
            int socketIndex = args != null && args.length >= 2 ? (int) NumberUtils.parseLong(args[1]) : 0;
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
            String payload = StaffPayloads.callForHelpNotification(StaffPayloads.callForHelpRow(reviewRow.toPayloadRow(), null));
            if (socketIndex > 0) {
                Proc_6_244_801E80(socketIndex, payload, 0);
            } else {
                Proc_6_249_802F10(payload, 0, 0);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_6_6DC9D0(Object... args) {
        updateCallForHelpTab(args, "GB", "2");
    }

    public static void Proc_6_7_6DD0E0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "GD");
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)
                || !handlingUserHasPermission(callerUserId, "fuse_mod")
                || !handlingUserHasPermission(callerUserId, "fuse_receive_calls_for_help")) {
                return;
            }
            LongRef offset = new LongRef(1);
            long closeState = readWireLong(requestPayload, offset);
            if (closeState < 1L || closeState > 3L) {
                return;
            }
            long callForHelpId = readWireLong(requestPayload, offset);
            if (callForHelpId <= 0L) {
                callForHelpId = readWireLong(requestPayload, offset);
            }
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
                Proc_6_244_801E80(reporterSocketIndex, StaffPayloads.callForHelpClosed(closeState), 0);
            }
            moderationDao.closeCallForHelp(callForHelpId, closeState);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_8_6DD790(Object... args) {
        updateCallForHelpTab(args, "GC", "1");
    }

    public static void Proc_6_9_6DDD70(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "GL");
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId) || !handlingUserHasPermission(callerUserId, "fuse_mod")) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            LongRef offset = new LongRef(1);
            readWireLong(requestPayload, offset);
            long lockFlag = readWireLong(requestPayload, offset);
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

    public static void Proc_6_10_6DE1D0(Object... args) {
        staffRoomHistory(args, "GG", true);
    }

    public static void Proc_6_11_6DF4A0(Object... args) {
        staffRoomHistory(args, "GJ", false);
    }

    public static void Proc_6_12_6DFE90(Object... args) {
        staffDirectMessage(args, "GN", "fuse_alert", "3", false, true);
    }

    public static long Proc_6_13_6E0A80(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return 0L;
            }
            long roomUserIndex = representedRoomUserIndex(socketIndex, userId);
            Proc_6_247_8027E0(socketIndex, SocialPayloads.roomUserWave(roomUserIndex), 0);
            return roomUserIndex;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static long Proc_6_14_6E10C0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "A]");
            long danceId = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            if (danceId <= 0L) {
                danceId = readWireLong(requestPayload, new LongRef(1));
            }
            if (danceId < 0L) {
                danceId = 0L;
            }
            if (danceId > 4L) {
                danceId = 4L;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return 0L;
            }
            long roomUserIndex = representedRoomUserIndex(socketIndex, userId);
            String payload = SocialPayloads.roomUserDance(roomUserIndex, danceId);
            Proc_6_247_8027E0(socketIndex, payload, 0);
            return danceId;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static void Proc_6_15_6E1900(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId) || !handlingUserHasPermission(userId, "fuse_use_wardrobe")) {
                return;
            }
            long maxSlots = handlingUserHasPermission(userId, "fuse_larger_wardrobe") ? 10L : 5L;
            UserDao users = userDao();
            List<UserDao.WardrobeSlotRow> wardrobeRows = users == null
                ? List.<UserDao.WardrobeSlotRow>of()
                : users.wardrobeRows(NumberUtils.parseLong(userId));
            Proc_6_244_801E80(socketIndex, UserPayloads.wardrobeSlots(wardrobeRows, maxSlots).payload(), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_16_6E2320(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "Ex");
            LongRef offset = new LongRef(1);
            long slotId = readWireLong(requestPayload, offset);
            String figureText = Functions.Proc_10_10_80A7F0(readWireString(requestPayload, offset), 0, 0);
            String genderText = StringUtils.left(readWireString(requestPayload, offset).toUpperCase(), 1);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId) || !handlingUserHasPermission(userId, "fuse_use_wardrobe")) {
                return;
            }
            long maxSlots = handlingUserHasPermission(userId, "fuse_larger_wardrobe") ? 10L : 5L;
            if (slotId < 1L || slotId > maxSlots || (!"M".equals(genderText) && !"F".equals(genderText))) {
                return;
            }
            String figureData = Proc_6_239_7FC170(Functions.applicationPath + "/figuredata.cache", 0, 0);
            if (!isValidWardrobeFigure(figureText, genderText, figureData)) {
                return;
            }
            UserDao users = userDao();
            if (users != null) {
                long numericUserId = NumberUtils.parseLong(userId);
                users.deleteWardrobeSlot(numericUserId, slotId);
                users.insertWardrobeSlot(numericUserId, slotId, figureText, genderText);
            }
            Proc_6_15_6E1900(socketIndex, "Ew", "Ew");
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_17_6E48D0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "@l");
            LongRef offset = new LongRef(1);
            String genderText = StringUtils.left(readWireString(requestPayload, offset).toUpperCase(), 1);
            String figureText = Functions.Proc_10_10_80A7F0(readWireString(requestPayload, offset), 0, 0);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId) || (!"M".equals(genderText) && !"F".equals(genderText))) {
                return;
            }
            String figureData = Proc_6_239_7FC170(Functions.applicationPath + "/figuredata.cache", 0, 0);
            if (!isValidWardrobeFigure(figureText, genderText, figureData)) {
                return;
            }
            UserDao users = userDao();
            long numericUserId = NumberUtils.parseLong(userId);
            String mottoText = "";
            if (users != null) {
                users.updateTutorialClothes(numericUserId, genderText, figureText);
                mottoText = users.motto(numericUserId);
            }
            String payload = userIdentityPayload(NumberUtils.parseLong(userId), mottoText, genderText, figureText);
            Proc_6_244_801E80(socketIndex, payload, 0);
            Proc_6_247_8027E0(socketIndex, payload, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_18_6E7480(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
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
            Proc_6_244_801E80(socketIndex, ClubPayloads.subscriptionOffers(clubDao.clubProductRows(), status), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static String Proc_6_19_6E8040(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return "";
            }
            String cachedPayload = args != null && args.length >= 2 ? StringUtils.text(args[1]) : "";
            String packetPrefix = args != null && args.length >= 3 ? StringUtils.text(args[2]) : "";
            if (packetPrefix.isEmpty()) {
                packetPrefix = "Gz";
            }
            if (cachedPayload.isEmpty()) {
                cachedPayload = Licence.recyclerSettings().statusPayload();
            }
            String payload = packetPrefix + cachedPayload;
            Proc_6_244_801E80(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    public static void Proc_6_20_6E88E0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long rankIndex = handlingUserRank(userId);
            long staffFlag = handlingUserHasPermission(userId, "fuse_client_staff") ? 1L : 0L;
            Proc_6_244_801E80(socketIndex, UserPayloads.rankAndStaffState(rankIndex, staffFlag), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static String Proc_6_24_6EA010(Object... args) {
        return handlingRepresentedChatRoute(args, 0L);
    }

    public static String Proc_6_25_6EEAC0(Object... args) {
        return handlingRepresentedChatRoute(args, 0L);
    }

    public static String legacyChatCommandPayload(String messageText) {
        String command = StringUtils.text(messageText).trim().toLowerCase(Locale.ROOT);
        if (":entwicklung".equals(command)) {
            return "BK" + "UNIQUE ID: --" + '\n'
                + "BUILD: " + legacyCommandBuildText() + '\2';
        }
        if (":about".equals(command)) {
            return "BK" + "Alpha Series" + '\n' + '\n'
                + "This is a copy of the unique Alpha Series written in Visual Basic 2006."
                + '\n' + '\n' + "UNIQUE ID:   --" + '\n'
                + "BUILD:   " + legacyCommandBuildText() + '\2';
        }
        if (":commands".equals(command)) {
            return "BK" + "You've following commands avaible:" + '\r' + '\r'
                + ":about" + '\r'
                + ":commands" + '\r'
                + ":entwicklung" + '\r'
                + ":statistics" + '\r'
                + ":drink" + '\r'
                + ":follow" + '\r'
                + ":transfer" + '\r'
                + ":tiplock" + '\r'
                + ":whosonline" + '\r' + '\r'
                + "\u2022 Please note that some commands require additional syntax, which hasn't been listed up here!"
                + '\2';
        }
        return "";
    }

    public static String legacyCommandBuildText() {
        String buildText = Licence.runtimeState().productName();
        return buildText.isEmpty() ? "ALPHASERIES_FINAL (PREMIUM)" : buildText;
    }

    public static String legacyActiveUsersPayload(String userNamesText) {
        String names = StringUtils.text(userNamesText).trim();
        return "BK" + "Active users:" + '\r' + '\r' + names + '\2' + '\2';
    }

    public static String Proc_6_26_7034C0(Object... args) {
        return handlingRepresentedChatRoute(args, 0L);
    }

    public static String Proc_6_27_706920(Object... args) {
        return handlingRepresentedChatRoute(args, 1L);
    }

    public static String Proc_6_28_709DA0(Object... args) {
        return handlingRepresentedChatRoute(args, 2L);
    }

    public static void Proc_6_53_718E00(Object... args) {
        int socketIndex = args != null && args.length >= 1 ? (int) NumberUtils.parseLong(args[0]) : 0;
        if (socketIndex <= 0) {
            return;
        }
        HandlingMUS.Proc_12_1_821AA0(socketIndex, "@R", 0);
    }

    public static final class LongRef {
        public long value;

        public LongRef(long value) {
            this.value = value;
        }
    }

    public static final class StickyNoteUpdate {
        public long furnitureId;
        public String noteColor = "";
        public String noteCaption = "";
    }

    public static final class WallPlacement {
        public long wallX;
        public long wallY;
        public long localX;
        public long localY;
    }

    public static final class RoomEventPayload {
        public long categoryId;
        public String categoryName = "";
        public String eventName = "";
        public String eventDescription = "";
        public String tagOne = "";
        public String tagTwo = "";
    }

    public static final class RoomSettingsPayload {
        public String roomName = "";
        public String roomPassword = "";
        public long doorStatus;
        public String roomDescription = "";
        public long visitorsMax;
        public long categoryId;
        public String tagOne = "";
        public String tagTwo = "";
        public long allowOthersPets;
        public long allowFeedPets;
        public long allowWalkthrough;
        public long disableWalls;
        public long thicknessFloor;
        public long thicknessWallpaper;
    }

    public static final class InventoryPayloads {
        public long regularCount;
        public long iconCount;
        public String regularPayload = "";
        public String iconPayload = "";
    }

    public static final class FurnitureMoveRequest {
        public long furnitureId;
        public long positionX;
        public long positionY;
        public long rotation;
    }

    public static final class FloorFurniturePlacement {
        public long furnitureId;
        public long positionX;
        public long positionY;
        public long rotation;
    }

    public static final class FurnitureCacheState {
        public String pendingRoomCache = "";
        public String pendingFurnitureCache = "";
        public String representedRoomCache = "";
    }

    public static final class FurnitureStateCache {
        public String pendingRoomCache = "";
        public String pendingFurnitureCache = "";
        public String representedRoomCache = "";
    }

    public static final class FriendTargetList {
        public boolean deleteAllPending;
        public String targetList = "";
        public long targetCount;
    }

    public static final class PetCommandAction {
        public boolean found;
        public long requiredLevel;
        public String action = "";
    }

    public static final class PetExperienceUpdate {
        public long petLevel;
        public long petExperience;
        public boolean leveledUp;
        public String statusPayload = "";
        public String experiencePayload = "";
    }

    public static final class PollAnswerSubmission {
        public long pollId;
        public long questionId;
        public long answerValue;
        public String answerText = "";
        public boolean valid;
    }

    public static final class RecyclerSelection {
        public long requestedCount;
        public String selectedItems = "";
        public boolean valid;
    }

    public static final class AchievementProgressDecision {
        public long achievementIndex = -1L;
        public long nextLevel;
        public long requiredProgress;
        public boolean shouldReward;
    }

    public static final class WiredApplyResult {
        public long appliedCount;
        public String statePayloads = "";
    }

    public static final class SongInfoRequest {
        public long requestedCount;
        public String requestedIds = "";
    }

    public static final class JukeboxAddRequest {
        public long diskFurnitureId;
        public long playlistOrder;
    }

    public static final class QuestProgressDecision {
        public long questId;
        public long numericQuestId;
        public long progressValue;
        public long amountRequired;
        public long waitAmount;
        public long remainingWait;
        public boolean shouldComplete;
        public boolean shouldScheduleWait;
        public boolean shouldSendList;
    }

    public static final class ActivityPointAward {
        public long pointType;
        public long awardAmount;
        public long newPoints;
        public boolean shouldAward;
        public String payload = "";
    }

    public static final class TradeOfferItemPayload {
        public long itemCount;
        public String payload = "";
    }

    public static final class MovementPosition {
        public long positionX;
        public long positionY;
        public boolean found;
    }

    public static final class StaffChatRowsPayload {
        public long chatCount;
        public String payload = "";
    }

    public static boolean isValidWardrobeFigure(String figureText, String genderText) {
        return isValidWardrobeFigure(figureText, genderText, "");
    }

    public static boolean isValidWardrobeFigure(String figureText, String genderText, String figureData) {
        String figure = StringUtils.text(figureText);
        String gender = StringUtils.text(genderText).toUpperCase();
        if (figure.isEmpty() || figure.length() > 255 || figure.indexOf('\'') >= 0 || figure.indexOf('"') >= 0) {
            return false;
        }

        String allowedTypes = ";lg;ha;wa;hr;ch;sh;cc;ea;he;ca;hd;fa;cp;";
        for (String part : figure.split("\\.", -1)) {
            if (!part.isEmpty()) {
                String[] piece = part.split("-", -1);
                if (piece.length < 2) {
                    return false;
                }
                String figureType = piece[0].toLowerCase();
                String setId = piece[1];
                if (!allowedTypes.contains(";" + figureType + ";") || NumberUtils.parseLong(setId) <= 0L) {
                    return false;
                }

                if (!StringUtils.text(figureData).isEmpty()) {
                    String lowerFigureData = figureData.toLowerCase();
                    String setTypeMarker = "<settype type=\"" + figureType + "\"";
                    int setTypeStart = lowerFigureData.indexOf(setTypeMarker.toLowerCase());
                    if (setTypeStart < 0) {
                        return false;
                    }
                    int setTypeEnd = lowerFigureData.indexOf("</settype>", setTypeStart);
                    if (setTypeEnd < 0) {
                        return false;
                    }
                    String setTypeXml = figureData.substring(setTypeStart, setTypeEnd);
                    String setMarker = "<set id=\"" + NumberUtils.parseLong(setId) + "\"";
                    if (!setTypeXml.toLowerCase().contains(setMarker.toLowerCase())) {
                        return false;
                    }
                    if (!figureSetAllowsGender(setTypeXml, setMarker, gender)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean figureSetAllowsGender(String setTypeXml, String setMarker, String genderText) {
        String setType = StringUtils.text(setTypeXml);
        String marker = StringUtils.text(setMarker);
        int setStart = setType.toLowerCase().indexOf(marker.toLowerCase());
        if (setStart < 0) {
            return false;
        }
        String lowerSetType = setType.toLowerCase();
        int setEnd = lowerSetType.indexOf("</set>", setStart);
        if (setEnd < 0) {
            setEnd = lowerSetType.indexOf("/>", setStart);
        }
        if (setEnd < 0) {
            setEnd = setType.length();
        }
        String setXml = setType.substring(setStart, setEnd);
        int genderStart = setXml.toLowerCase().indexOf("gender=\"");
        if (genderStart < 0) {
            return true;
        }
        if (genderStart + 8 >= setXml.length()) {
            return false;
        }
        String genderValue = setXml.substring(genderStart + 8, genderStart + 9).toUpperCase();
        String gender = StringUtils.text(genderText).toUpperCase();
        return "U".equals(genderValue) || genderValue.equals(gender);
    }

    public static String userIdentityPayload(long userId, String mottoText, String genderText, String figureText) {
        return UserPayloads.identityRefresh(userId, mottoText, figureText, genderText);
    }

    public static String Proc_6_21_6E8BA0(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        return extractUrlList(StringUtils.text(args[0]));
    }

    public static String extractUrlList(String messageText) {
        StringBuilder urlList = new StringBuilder();
        for (String word : StringUtils.text(messageText).split(" ", -1)) {
            String candidate = word.trim();
            String lowered = candidate.toLowerCase();
            if (!candidate.isEmpty()) {
                if (lowered.startsWith("www.") && lowered.indexOf('.', 4) > 0) {
                    urlList.append(candidate).append(';');
                } else if (lowered.startsWith("http://")) {
                    urlList.append(candidate).append(';');
                } else if (lowered.startsWith("https://")) {
                    urlList.append(candidate).append(';');
                }
            }
        }
        return urlList.toString();
    }

    public static String Proc_6_22_6E9300(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        boolean enabled = NumberUtils.parseLong(Functions.Proc_10_0_809570("com.client.chat.filter.enabled", 0)) != 0L;
        String replacement = Functions.Proc_10_0_809570("com.client.chat.filter.replacement", "");
        return Licence.chatSettings().filterText(StringUtils.text(args[0]), enabled, replacement);
    }

    public static String filterChatText(
        String messageText,
        boolean filterEnabled,
        String replacementText,
        List<ChatSettings.FilterWord> filterRows
    ) {
        return ChatSettings.fromRows(filterRows, List.of()).filterText(messageText, filterEnabled, replacementText);
    }

    public static long Proc_6_23_6E9A90(Object... args) {
        if (args == null || args.length == 0) {
            return 0L;
        }
        boolean enabled = NumberUtils.parseLong(Functions.Proc_10_0_809570("com.client.chat.gesture.enabled", 0)) != 0L;
        return Licence.chatSettings().gestureId(StringUtils.text(args[0]), enabled);
    }

    public static long findGestureId(String messageText, boolean gestureEnabled, List<ChatSettings.Gesture> gestureRows) {
        return ChatSettings.fromRows(List.of(), gestureRows).gestureId(messageText, gestureEnabled);
    }

    public static String Proc_6_29_70D800(Object... args) {
        if (args == null || args.length < 12) {
            return "";
        }
        return StaffPayloads.callForHelp(
            NumberUtils.parseLong(args[0]),
            NumberUtils.parseLong(args[1]),
            NumberUtils.parseLong(args[2]),
            NumberUtils.parseLong(args[3]),
            StringUtils.text(args[4]),
            NumberUtils.parseLong(args[5]),
            StringUtils.text(args[6]),
            StringUtils.text(args[7]),
            NumberUtils.parseLong(args[8]),
            StringUtils.text(args[9]),
            NumberUtils.parseLong(args[10]),
            StringUtils.text(args[11]));
    }

    public static void Proc_6_30_70DC90(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
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
                Proc_6_244_801E80(socketIndex, StaffPayloads.callForHelpDeleted(), 0);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_31_70DE80(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long rankIndex = handlingUserRank(userId);
            long hcLevel = handlingUserHcLevel(userId);
            if (!Functions.Proc_10_1_809790(rankIndex, "", "fuse_mod", hcLevel)) {
                return;
            }
            String payload = StaffPayloads.moderationPanel(staffModerationPayload(rankIndex, hcLevel));
            Proc_6_244_801E80(socketIndex, payload, 0);

            StaffModerationDao moderationDao = staffModerationDao();
            if (moderationDao == null) {
                return;
            }
            for (StaffCallForHelpRow row : moderationDao.openStaffCallRows()) {
                Proc_6_244_801E80(socketIndex,
                    StaffPayloads.callForHelpNotification(StaffPayloads.callForHelpRow(row, null)), 0);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_32_70EAB0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String packetPayload = handlingPacketPayload(args);
            String requestPayload = packetPayload.length() >= 3 ? packetPayload.substring(2) : packetPayload;
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
            LongRef offset = new LongRef(1);
            String descriptionText = Functions.Proc_10_10_80A7F0(readWireString(requestPayload, offset), 0, 0);
            if (descriptionText.length() < 30) {
                return;
            }
            long categoryId = readWireLong(requestPayload, offset);
            if (categoryId <= 0L) {
                categoryId = readWireLong(requestPayload, offset);
            }
            long partnerUserId = readWireLong(requestPayload, offset);
            if (partnerUserId == NumberUtils.parseLong(userId)) {
                partnerUserId = 0L;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            moderationDao.insertCallForHelp(NumberUtils.parseLong(userId), roomId, categoryId, partnerUserId, descriptionText);
            long callForHelpId = moderationDao.newestCallForHelpId();
            Proc_6_244_801E80(socketIndex, StaffPayloads.callForHelpCreated(callForHelpId), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_33_70F4F0(Object... args) {
        try {
            Proc_6_244_801E80(handlingSocketIndex(args),
                HelpPayloads.importantFaqs(Licence.helpCenterCache().importantFaqPayload()), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_34_70F590(Object... args) {
        try {
            Proc_6_244_801E80(handlingSocketIndex(args),
                HelpPayloads.categories(Licence.helpCenterCache().categoryPayload()), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_35_70F630(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String packetPayload = handlingPacketPayload(args);
            long categoryId = packetPayload.length() >= 3 ? readWireLong(packetPayload.substring(2), new LongRef(1)) : 0L;
            HelpCenterCache helpCenterCache = Licence.helpCenterCache();
            String categoryPayload = helpCenterCache.categoryFaqPayload(categoryId);
            Proc_6_244_801E80(socketIndex, HelpPayloads.categoryFaqs(categoryId, categoryPayload), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_36_70F7B0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String packetPayload = handlingPacketPayload(args);
            String searchText = packetPayload.length() >= 3
                ? Functions.Proc_10_11_80A9C0(Functions.Proc_10_7_80A190(packetPayload.substring(2), 0, 0), 0, 0)
                : "";
            if (searchText.length() < 3) {
                Proc_6_243_7FFEB0(socketIndex, 0, 0);
                return;
            }
            HelpDao helpDao = helpDao();
            if (helpDao == null) {
                return;
            }
            List<HelpDao.FaqNameRow> rows = helpDao.searchFaqs(searchText);
            Proc_6_244_801E80(socketIndex, HelpPayloads.searchResults(rows), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_37_70FC20(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String packetPayload = handlingPacketPayload(args);
            long faqId = 0L;
            if (packetPayload.length() >= 3) {
                String requestPayload = packetPayload.substring(2);
                faqId = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
                if (faqId <= 0L) {
                    faqId = readWireLong(requestPayload, new LongRef(1));
                }
            }
            Proc_6_244_801E80(socketIndex,
                HelpPayloads.description(Licence.helpCenterCache().descriptionPayload(faqId)), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static long Proc_6_38_70FD10(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "GV");
            LongRef offset = new LongRef(1);
            String candidateName = Functions.Proc_10_10_80A7F0(readWireString(requestPayload, offset), 0, 0);
            if (candidateName.isEmpty()) {
                candidateName = Functions.Proc_10_10_80A7F0(Functions.Proc_10_7_80A190(requestPayload, 0, 0), 0, 0);
            }
            return Proc_6_40_711770(socketIndex, 0, candidateName);
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static long Proc_6_39_711650(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "GW");
            LongRef offset = new LongRef(1);
            String candidateName = Functions.Proc_10_10_80A7F0(readWireString(requestPayload, offset), 0, 0);
            if (candidateName.isEmpty()) {
                candidateName = Functions.Proc_10_10_80A7F0(Functions.Proc_10_7_80A190(requestPayload, 0, 0), 0, 0);
            }
            return Proc_6_40_711770(socketIndex, -1, candidateName);
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static long Proc_6_40_711770(Object... args) {
        try {
            if (args == null || args.length < 3) {
                return 0L;
            }
            int socketIndex = handlingSocketIndex(args);
            boolean checkOnly = NumberUtils.parseLong(args[1]) < 0L;
            String candidateName = StringUtils.text(args[2]).trim();
            if (socketIndex <= 0) {
                return 0L;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            UserDao users = userDao();
            if (users == null) {
                return 0L;
            }
            long numericUserId = NumberUtils.parseLong(userId);
            String oldName = users.name(numericUserId);
            users.gender(numericUserId);
            long existingCount = users.countByName(candidateName);
            long validationCode = avatarNameValidationCode(candidateName, oldName, existingCount);
            Proc_6_244_801E80(socketIndex, UserPayloads.avatarNameValidation(validationCode, candidateName), 0);
            if (checkOnly || validationCode != 0L) {
                return validationCode;
            }
            users.updateName(numericUserId, candidateName);
            users.insertIdentityLog(oldName, candidateName, socketIndex);
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId > 0L) {
                long roomUserIndex = representedRoomUserIndex(socketIndex, userId);
                Proc_6_247_8027E0(socketIndex,
                    UserPayloads.roomUserNameChanged(NumberUtils.parseLong(userId), roomUserIndex, candidateName), 0);
                String queryTail = "users,rooms,rooms_categories WHERE rooms.id='" + roomId
                    + "' AND users.id=rooms.id_owner AND rooms_categories.id=rooms.id_category LIMIT 1";
                Proc_6_247_8027E0(socketIndex, Proc_6_112_74E0C0(queryTail, "GF", 0), 0);
                Proc_6_247_8027E0(socketIndex, RoomPayloads.entryUpdated(roomId), 0);
            }
            return 0L;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static String Proc_6_41_712730(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        RoomUserEntryPayloadArgs values = RoomUserEntryPayloadArgs.fromLegacyArgs(args);
        return SocialPayloads.roomUserEntry(values);
    }

    public static String Proc_6_42_712FB0(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        RoomObjectEntryPayloadArgs values = RoomObjectEntryPayloadArgs.fromLegacyArgs(args);
        return SocialPayloads.roomObjectEntry(values);
    }

    public static void Proc_6_43_713680(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "FF");
            long requestedRoomId = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            if (requestedRoomId <= 0L) {
                requestedRoomId = readWireLong(requestPayload, new LongRef(1));
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = requestedRoomId > 0L ? requestedRoomId : handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L) {
                return;
            }
            if (!handlingUserOwnsRoom(callerUserId, roomId) && !handlingUserHasPermission(callerUserId, "fuse_any_room_controller")) {
                return;
            }
            RoomDao rooms = roomDao();
            if (rooms == null) {
                return;
            }
            Optional<RoomDao.RoomSettingsRead> roomSettings = rooms.roomSettings(roomId);
            if (roomSettings.isEmpty()) {
                return;
            }
            List<RoomDao.RoomRight> rightsRows = rooms.rightsRows(roomId);
            String payload = RoomPayloads.settingsRead(roomSettings.get(), rightsRows);
            if (!payload.isEmpty()) {
                Proc_6_244_801E80(socketIndex, payload, 0);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_44_7145E0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String packetPayload = handlingRequestPayload(args, "FB");
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            String iconPayload = roomIconPayloadFromWire(packetPayload);
            if (iconPayload.isEmpty()) {
                return;
            }
            RoomDao rooms = roomDao();
            if (rooms != null) {
                rooms.updateIcon(roomId, iconPayload);
            }
            String queryTail = "users,rooms,rooms_categories WHERE rooms.id='" + roomId
                + "' AND users.id=rooms.id_owner AND rooms_categories.id=rooms.id_category LIMIT 1";
            Proc_6_247_8027E0(socketIndex, Proc_6_112_74E0C0(queryTail, "GF", 0), 0);
            Proc_6_244_801E80(socketIndex, RoomPayloads.iconUpdated(roomId), 0);
            Proc_6_244_801E80(socketIndex, RoomPayloads.entryUpdated(roomId), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_45_714B60(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            RoomDao rooms = roomDao();
            if (rooms != null) {
                rooms.deleteRoomEvents(roomId);
            }
            Proc_6_244_801E80(socketIndex, "Er-1" + '\2', 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_46_714D50(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            RoomDao rooms = roomDao();
            long doorStatus = rooms == null ? 0L : rooms.doorStatus(roomId);
            Proc_6_244_801E80(socketIndex, doorStatus != 0L ? "EoHK" : "EoIH", 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_47_714F60(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String packetPayload = handlingPacketPayload(args);
            if (packetPayload.length() > 2) {
                packetPayload = packetPayload.substring(2);
            }
            long roomId = readWireLong(packetPayload, new LongRef(1));
            if (roomId <= 0L) {
                return;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            UserDao users = userDao();
            if (users != null) {
                users.updateHomeRoom(NumberUtils.parseLong(userId), roomId);
            }
            Proc_6_244_801E80(socketIndex, RoomPayloads.homeRoom(roomId), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_48_7151E0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String packetPayload = handlingRequestPayload(args, "EZ");
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            RoomDao rooms = roomDao();
            long doorStatus = rooms == null ? 0L : rooms.doorStatus(roomId);
            if (doorStatus != 0L) {
                Proc_6_244_801E80(socketIndex, "EoHK", 0);
                return;
            }
            RoomEventPayload event = new RoomEventPayload();
            if (!roomEventCreatePayloadFromWire(packetPayload, event)) {
                return;
            }
            if (rooms != null) {
                rooms.insertRoomEvent(
                    roomId,
                    NumberUtils.parseLong(userId),
                    event.eventName,
                    event.eventDescription,
                    event.categoryId,
                    event.tagOne,
                    event.tagTwo,
                    event.categoryName);
            }
            Proc_6_247_8027E0(socketIndex, "Er" + Proc_6_51_716AC0(roomId), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_49_715D30(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String packetPayload = handlingRequestPayload(args, "E\\");
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            RoomEventPayload event = new RoomEventPayload();
            if (!roomEventEditPayloadFromWire(packetPayload, event)) {
                return;
            }
            RoomDao rooms = roomDao();
            if (rooms != null) {
                rooms.updateRoomEvent(
                    roomId,
                    NumberUtils.parseLong(userId),
                    event.eventName,
                    event.eventDescription,
                    event.tagOne,
                    event.tagTwo);
            }
            Proc_6_247_8027E0(socketIndex, "Er" + Proc_6_51_716AC0(roomId), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_50_7166B0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "Ab");
            String targetName = Functions.Proc_10_7_80A190(requestPayload, 0, 0).trim();
            if (targetName.isEmpty() || !requestPayload.startsWith("@")) {
                targetName = requestPayload.trim();
            }
            if (targetName.isEmpty()) {
                Proc_6_244_801E80(socketIndex, "BC", 0);
                return;
            }
            UserDao users = userDao();
            UserDao.ActiveUserLocation target = users == null ? null : users.activeLocationByName(targetName).orElse(null);
            if (target == null || target.userId() <= 0L || target.socketIndex() <= 0L || target.roomId() <= 0L) {
                Proc_6_244_801E80(socketIndex, "BC", 0);
                return;
            }
            Proc_6_57_71E8F0(socketIndex, target.roomId(), "");
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_52_7172B0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String packetPayload = handlingRequestPayload(args, "FQ");
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            if (!handlingUserOwnsRoom(userId, roomId) && !handlingUserHasPermission(userId, "fuse_any_room_controller")) {
                return;
            }
            RoomSettingsPayload settings = new RoomSettingsPayload();
            if (!roomSettingsFromWire(packetPayload, settings)) {
                return;
            }
            settings.categoryId = roomCategoryForUser(settings.categoryId, userId);
            if (settings.categoryId <= 0L) {
                return;
            }
            if (settings.disableWalls != 0L && !handlingUserHasPermission(userId, "fuse_hide_room_walls")) {
                settings.disableWalls = 0L;
            }
            RoomDao rooms = roomDao();
            if (rooms == null) {
                return;
            }
            rooms.updateSettings(
                roomId,
                settings.thicknessFloor,
                settings.thicknessWallpaper,
                settings.roomName,
                settings.roomPassword,
                settings.roomDescription,
                settings.doorStatus,
                settings.categoryId,
                settings.tagOne,
                settings.tagTwo,
                settings.allowOthersPets,
                settings.allowFeedPets,
                settings.allowWalkthrough,
                settings.visitorsMax,
                settings.disableWalls);
            String queryTail = "users,rooms,rooms_categories WHERE rooms.id='" + roomId
                + "' AND users.id=rooms.id_owner AND rooms_categories.id=rooms.id_category LIMIT 1";
            Proc_6_247_8027E0(socketIndex, Proc_6_112_74E0C0(queryTail, "GF", 0), 0);
            Proc_6_244_801E80(socketIndex, RoomPayloads.settingsUpdated(roomId), 0);
            Proc_6_244_801E80(socketIndex, RoomPayloads.entryUpdated(roomId), 0);
            Proc_6_244_801E80(socketIndex,
                RoomPayloads.wallOptions(settings.disableWalls, settings.thicknessFloor, settings.thicknessWallpaper), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static String Proc_6_51_716AC0(Object... args) {
        try {
            if (args == null || args.length == 0) {
                return "-1" + '\2';
            }
            long roomId = NumberUtils.parseLong(args[0]);
            if (roomId <= 0L) {
                return "-1" + '\2';
            }
            String timeFormat = Functions.Proc_10_0_809570("com.mysql.format.time", "%H:%i", 0);
            RoomDao rooms = roomDao();
            Optional<RoomDao.RoomEventInfo> eventInfo = rooms == null
                ? Optional.empty()
                : rooms.eventInfo(roomId, timeFormat);
            if (eventInfo.isEmpty()) {
                return "-1" + '\2';
            }
            RoomDao.RoomEventInfo event = eventInfo.get();
            return PacketBuilder.create()
                .appendString(event.eventName())
                .appendString(event.description())
                .appendString(event.formattedTime())
                .appendString(event.tagOne())
                .appendString(event.tagTwo())
                .appendInt(event.userId())
                .appendInt(event.roomId())
                .appendInt(event.categoryId())
                .build();
        } catch (Exception ignored) {
            return "-1" + '\2';
        }
    }

    public static long Proc_6_54_719050(Object... args) {
        long reservedSlot = 0L;
        try {
            int socketIndex = handlingSocketIndex(args);
            long roomId = args != null && args.length >= 2 ? NumberUtils.parseLong(args[1]) : 0L;
            long preferredSlot = args != null && args.length >= 3 ? NumberUtils.parseLong(args[2]) : 0L;
            if (socketIndex <= 0 || roomId <= 0L) {
                return 0L;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            if (handlingCurrentRoomId(socketIndex, userId) > 0L) {
                Proc_6_55_71A6E0(socketIndex, 0, 0);
            }
            reservedSlot = reserveRepresentedRoomSlot(preferredSlot);
            if (reservedSlot <= 0L) {
                Proc_6_244_801E80(socketIndex, UserPayloads.errorCode(1, 0), 0);
                return 0L;
            }
            loadRepresentedRoomBots(reservedSlot, roomId);
            UserDao users = userDao();
            RoomDao rooms = roomDao();
            if (users == null || rooms == null) {
                releaseRepresentedRoomSlot(reservedSlot);
                return 0L;
            }
            long userIdValue = NumberUtils.parseLong(userId);
            String sessionId = users.loginSession(userIdValue);
            rooms.insertVisit(userIdValue, roomId, sessionId);
            rooms.markRoomEntered(roomId, reservedSlot);
            Proc_6_56_71E730(socketIndex, 0, 0);
            Proc_6_53_718E00(socketIndex, 0, 0);
            return reservedSlot;
        } catch (Exception ignored) {
            if (reservedSlot > 0L) {
                releaseRepresentedRoomSlot(reservedSlot);
            }
            return 0L;
        }
    }

    public static long Proc_6_55_71A6E0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return 0L;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            RoomDao rooms = roomDao();
            if (rooms == null) {
                Proc_6_244_801E80(socketIndex, "J|H", 0);
                return 0L;
            }
            long userIdValue = NumberUtils.parseLong(userId);
            RoomDao.ActiveRoomVisit visit = rooms.activeVisitWithRoomSlot(userIdValue).orElse(null);
            if (visit == null) {
                Proc_6_244_801E80(socketIndex, "J|H", 0);
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
                Proc_6_247_8027E0(socketIndex, SocialPayloads.roomUserRemoved(roomUserIndex), 0);
            }
            if (visitId > 0L) {
                rooms.closeVisitById(visitId);
            } else {
                rooms.closeVisitsByUserRoom(userIdValue, roomId);
            }
            rooms.decrementVisitors(roomId);
            if (slotId > 0L) {
                releaseRepresentedRoomSlot(slotId);
                rooms.clearRoomSlot(roomId, slotId);
            }
            Proc_6_244_801E80(socketIndex, "J|H", 0);
            return roomId;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static void Proc_6_56_71E730(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long roomMode = args != null && args.length >= 2 ? NumberUtils.parseLong(args[1]) : 0L;
            Proc_6_244_801E80(socketIndex, "@S", 0);
            Proc_6_244_801E80(socketIndex, "Bf/client.php" + '\2', 0);
            Proc_6_244_801E80(socketIndex, roomMode == 0L ? "@i" : "@{", 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static long Proc_6_57_71E8F0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long roomId = args != null && args.length >= 2 ? NumberUtils.parseLong(args[1]) : 0L;
            String suppliedPassword = args != null && args.length >= 3 ? StringUtils.text(args[2]) : "";
            if (roomId <= 0L) {
                Proc_6_244_801E80(socketIndex, "C`H", 0);
                return 0L;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            RoomDao rooms = roomDao();
            if (rooms == null) {
                Proc_6_244_801E80(socketIndex, "C`H", 0);
                return 0L;
            }
            RoomDao.RoomEntryState entryState = rooms.roomEntryState(roomId).orElse(null);
            if (entryState == null) {
                Proc_6_244_801E80(socketIndex, "C`H", 0);
                return 0L;
            }
            long userIdValue = NumberUtils.parseLong(userId);
            boolean isOwner = entryState.ownerUserId() == userIdValue;
            if (!isOwner) {
                if (rooms.userBannedFromRoom(userIdValue, roomId)) {
                    Proc_6_53_718E00(socketIndex, 0, 0);
                    Proc_6_244_801E80(socketIndex, "C`PA", 0);
                    return 0L;
                }
                if (entryState.visitorsMax() > 0L && entryState.visitorsNow() >= entryState.visitorsMax()
                    && !handlingUserHasPermission(userId, "fuse_enter_full_rooms")) {
                    Proc_6_53_718E00(socketIndex, 0, 0);
                    Proc_6_244_801E80(socketIndex, "C`I", 0);
                    return 0L;
                }
                if (entryState.doorStatus() == 1L && !handlingUserHasPermission(userId, "fuse_enter_locked_rooms")) {
                    Proc_6_53_718E00(socketIndex, 0, 0);
                    Proc_6_244_801E80(socketIndex, "C`H", 0);
                    return 0L;
                }
                if (entryState.doorStatus() == 2L && !StringUtils.text(entryState.password()).equals(suppliedPassword)) {
                    Proc_6_53_718E00(socketIndex, 0, 0);
                    Proc_6_244_801E80(socketIndex, "@afhFF", 0);
                    return 0L;
                }
            }
            return Proc_6_54_719050(socketIndex, roomId, entryState.roomSlot());
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static long Proc_6_58_71FCA0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String packetPayload = handlingRequestPayload(args, "FG");
            ensureRepresentedRoomSlotPool();
            if (Licence.representedRoomSlots().isEmpty()) {
                Proc_6_244_801E80(socketIndex, UserPayloads.errorCode(1, 0), 0);
                return 0L;
            }
            String roomIdText = Functions.Proc_10_7_80A190(packetPayload, 0, 0);
            if (roomIdText.isEmpty()) {
                roomIdText = packetPayload;
            }
            long roomId = NumberUtils.parseLong(roomIdText);
            int passwordStart = 2 + roomIdText.length();
            String roomPassword = "";
            if (passwordStart < packetPayload.length()) {
                roomPassword = Functions.Proc_10_6_809F10(packetPayload.substring(passwordStart), 0, 0);
            }
            Proc_6_57_71E8F0(socketIndex, roomId, roomPassword);
            return roomId;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static void Proc_6_59_71FEE0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String advertisementPayload = "\2\2";
            if (Licence.visitRoomAds().count() > 0L) {
                String candidate = Licence.visitRoomAds().randomPayload();
                if (!candidate.isEmpty()) {
                    advertisementPayload = candidate;
                }
            }
            Proc_6_244_801E80(socketIndex, "DB" + advertisementPayload, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_60_720060(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "FA");
            LongRef offset = new LongRef(1);
            long requestMode = readWireLong(requestPayload, offset);
            long detailFlag = readWireLong(requestPayload, offset);
            if (detailFlag == 1L) {
                long roomId = readWireLong(requestPayload, offset);
                if (roomId <= 0L) {
                    return;
                }
                RoomDao rooms = roomDao();
                if (rooms == null) {
                    return;
                }
                NavigatorRoom room = rooms.navigatorRoom(roomId).orElse(null);
                Proc_6_244_801E80(socketIndex,
                    PacketBuilder.message("GF")
                        .appendInt(0)
                        .appendRaw(NavigatorPayloads.singleRoom(room))
                        .build(), 0);
            } else if (requestMode > 0L) {
                // VB6 reads a room id from packed session offsets here; those offsets are not represented yet.
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_61_720490(Object... args) {
        roomKickOrBanUser(args, false);
    }

    public static void Proc_6_62_7209F0(Object... args) {
        roomKickOrBanUser(args, true);
    }

    public static void Proc_6_63_721050(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "DE");
            LongRef offset = new LongRef(1);
            long voteValue = readWireLong(requestPayload, offset);
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
            RoomDao rooms = roomDao();
            if (rooms == null) {
                return;
            }
            long userIdValue = NumberUtils.parseLong(userId);
            if (rooms.userRatedRoom(userIdValue, roomId)) {
                return;
            }
            rooms.insertRoomRate(userIdValue, roomId);
            long roomRate = rooms.roomRate(roomId);
            if (roomRate < 0L) {
                roomRate = 0L;
            }
            roomRate++;
            rooms.updateRoomRate(roomId, roomRate);
            Proc_6_244_801E80(socketIndex, RoomPayloads.rating(roomRate), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_64_721650(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "D\u007f");
            LongRef offset = new LongRef(1);
            String targetName = readWireString(requestPayload, offset);
            if (targetName.isEmpty()) {
                return;
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !handlingUserHasRoomRight(callerUserId, roomId)) {
                return;
            }
            UserDao users = userDao();
            RoomDao rooms = roomDao();
            if (users == null || rooms == null) {
                return;
            }
            long targetUserId = users.userIdByName(targetName);
            if (targetUserId <= 0L) {
                return;
            }
            rooms.deleteRoomRight(targetUserId, roomId);
            Proc_6_244_801E80(socketIndex, RoomPayloads.roomRightRemoved(), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_65_721A10(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "A`");
            LongRef offset = new LongRef(1);
            String targetUserId = String.valueOf(readWireLong(requestPayload, offset));
            if (targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return;
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !handlingUserHasRoomRight(callerUserId, roomId)) {
                return;
            }
            int targetSocketIndex = handlingSocketFromUserId(targetUserId);
            if (targetSocketIndex <= 0) {
                return;
            }
            RoomDao rooms = roomDao();
            if (rooms == null) {
                return;
            }
            rooms.insertRoomRight(NumberUtils.parseLong(targetUserId), roomId);
            Proc_6_244_801E80(targetSocketIndex, "@j", 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_66_721D60(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "AT");
            StickyNoteUpdate note = new StickyNoteUpdate();
            if (!stickyNoteUpdateFromWire(requestPayload, note) || note.furnitureId <= 0L) {
                return;
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !handlingUserHasRoomRight(callerUserId, roomId)) {
                return;
            }
            FurnitureDao furniture = furnitureDao();
            if (furniture == null) {
                return;
            }
            FurnitureDao.RoomFurnitureWithWall sticky = furniture.roomFurnitureWithWall(note.furnitureId, roomId).orElse(null);
            if (sticky == null) {
                return;
            }
            long productId = sticky.productId();
            if (!isPostItProduct(productId)) {
                return;
            }
            furniture.updatePostIt(note.furnitureId, note.noteColor, note.noteCaption);
            String broadcastPayload = FurniturePayloads.stickyNoteUpdated(note.furnitureId, productId, note.noteColor);
            Proc_6_247_8027E0(socketIndex, broadcastPayload, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_67_722940(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "AS");
            long furnitureId = stickyFurnitureIdFromPayload(requestPayload);
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
            FurnitureDao furniture = furnitureDao();
            if (furniture == null) {
                return;
            }
            FurnitureDao.RoomFurniture sticky = furniture.roomFurniture(furnitureId, roomId).orElse(null);
            if (sticky == null) {
                return;
            }
            long productId = sticky.productId();
            if (!isPostItProduct(productId)) {
                return;
            }
            String noteColor = StringUtils.left(sticky.sign(), 6);
            if (noteColor.isEmpty()) {
                noteColor = "FFFF33";
            }
            String noteCaption = StringUtils.text(sticky.caption()).replace('\u001f', '\r');
            Proc_6_244_801E80(socketIndex, "@p" + furnitureId + '\2' + noteColor + '\r' + noteCaption + '\2', 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_68_723170(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "AU");
            long furnitureId = stickyFurnitureIdFromPayload(requestPayload);
            if (furnitureId <= 0L) {
                return;
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !handlingUserOwnsRoom(callerUserId, roomId)) {
                return;
            }
            FurnitureDao furniture = furnitureDao();
            if (furniture == null) {
                return;
            }
            FurnitureDao.RoomFurniture sticky = furniture.roomFurniture(furnitureId, roomId).orElse(null);
            if (sticky == null) {
                return;
            }
            long productId = sticky.productId();
            if (!isPostItProduct(productId)) {
                return;
            }
            furniture.deleteFurniture(furnitureId);
            Proc_6_247_8027E0(socketIndex, "AT" + furnitureId, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_69_723630(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "AN");
            long furnitureId = stickyFurnitureIdFromPayload(requestPayload);
            if (furnitureId <= 0L) {
                return;
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !handlingUserHasRoomRight(callerUserId, roomId)) {
                return;
            }
            FurnitureDao furniture = furnitureDao();
            if (furniture == null) {
                return;
            }
            FurnitureDao.GiftBoxFurniture giftBox = furniture.giftBox(furnitureId, roomId).orElse(null);
            if (giftBox == null) {
                return;
            }
            long boxProductId = giftBox.boxProductId();
            long openedProductId = giftBox.openedProductId();
            String openedSign = StringUtils.text(giftBox.openedSign());
            if (boxProductId <= 0L || openedProductId <= 0L) {
                return;
            }
            String boxAction = DataManager.Proc_8_12_806C30(boxProductId, 17, 0).toLowerCase();
            if (!boxAction.contains("present_") || "ecotron_box".equals(boxAction)) {
                return;
            }
            Proc_6_247_8027E0(socketIndex, "A^" + furnitureId + '\2' + "H" + '\2', 0);
            furniture.deleteFurniture(furnitureId);
            furniture.insertInventoryFurniture(openedProductId, NumberUtils.parseLong(callerUserId), openedSign);
            long openedProductType = NumberUtils.parseLong(DataManager.Proc_8_12_806C30(openedProductId, 0, 0));
            String responseClass = "i";
            if (openedProductType == 2L) {
                responseClass = "s";
            }
            if (openedProductType == 3L) {
                responseClass = "e";
            }
            String responsePayload = FurniturePayloads.presentOpened(openedProductId, responseClass,
                DataManager.Proc_8_12_806C30(openedProductId, 24, 0));
            Proc_6_244_801E80(socketIndex, responsePayload, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_70_724190(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "FI");
            long furnitureId = stickyFurnitureIdFromPayload(requestPayload);
            if (furnitureId <= 0L) {
                return;
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !handlingUserHasRoomRight(callerUserId, roomId)) {
                return;
            }
            FurnitureDao furniture = furnitureDao();
            if (furniture == null) {
                return;
            }
            FurnitureDao.WallStateFurniture wallState = furniture.wallState(furnitureId, roomId).orElse(null);
            if (wallState == null) {
                return;
            }
            long productId = wallState.productId();
            if (productId <= 0L) {
                return;
            }
            long currentState = NumberUtils.parseLong(wallState.sign());
            long stateCount = Licence.Proc_9_0_806F70(productId, 5, 0);
            if (stateCount <= 0L) {
                stateCount = NumberUtils.parseLong(DataManager.Proc_8_12_806C30(productId, 10, 0));
            }
            if (stateCount <= 0L) {
                stateCount = 1L;
            }
            long nextState = currentState + 1L;
            if (nextState > stateCount) {
                nextState = 0L;
            }
            if (nextState < 0L) {
                nextState = 0L;
            }
            furniture.updateSign(furnitureId, nextState);
            String payload = FurniturePayloads.wallState(furnitureId, productId, String.valueOf(nextState), "0");
            Proc_6_247_8027E0(socketIndex, payload, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_71_724CF0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !handlingUserOwnsRoom(callerUserId, roomId)) {
                return;
            }
            RoomDao rooms = roomDao();
            if (rooms == null) {
                return;
            }
            List<Long> socketIndexes = rooms.activeRightHolderSocketIndexes(roomId);
            rooms.deleteRoomRights(roomId);
            for (Long activeSocketIndex : socketIndexes) {
                int targetSocketIndex = activeSocketIndex == null ? 0 : activeSocketIndex.intValue();
                if (targetSocketIndex > 0) {
                    Proc_6_244_801E80(targetSocketIndex, "@k", 0);
                }
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_72_7250D0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "@W");
            long requestFlag = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            if (requestFlag == 0L && !requestPayload.isEmpty()) {
                requestFlag = readWireLong(requestPayload, new LongRef(1));
            }
            if (requestFlag != 0L) {
                return;
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !handlingUserOwnsRoom(callerUserId, roomId)) {
                return;
            }
            Functions.Proc_10_18_80C9E0(roomId, 0, 0);
            RoomDao rooms = roomDao();
            if (rooms != null) {
                rooms.deleteRoom(roomId);
            }
            Proc_6_53_718E00(socketIndex, 0, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_73_725540(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "AT");
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L || (!handlingUserHasRoomRight(userId, roomId)
                && !handlingUserHasPermission(userId, "fuse_pick_up_any_furni"))) {
                return;
            }
            long furnitureId = stickyFurnitureIdFromPayload(requestPayload);
            if (furnitureId <= 0L) {
                return;
            }
            FurnitureDao furniture = furnitureDao();
            if (furniture == null) {
                return;
            }
            FurnitureDao.RoomFurnitureProduct furnitureProduct = furniture.roomFurnitureProduct(furnitureId, roomId).orElse(null);
            if (furnitureProduct == null) {
                return;
            }
            long productId = furnitureProduct.productId();
            if (productId <= 0L) {
                return;
            }
            String productSprite = DataManager.Proc_8_12_806C30(productId, 17, 0);
            if (productSprite.isEmpty()) {
                productSprite = DataManager.Proc_8_12_806C30(productId, 18, 0);
            }
            if (!productSprite.startsWith("CF_") && !productSprite.startsWith("CFC_")) {
                return;
            }
            String[] productParts = productSprite.split("_", -1);
            if (productParts.length < 2) {
                return;
            }
            long creditValue = NumberUtils.parseLong(productParts[1]);
            if (creditValue <= 0L) {
                return;
            }
            UserDao users = userDao();
            if (users == null) {
                return;
            }
            long numericUserId = NumberUtils.parseLong(userId);
            users.addCredits(numericUserId, creditValue);
            long updatedCredits = users.credits(numericUserId);
            Proc_6_244_801E80(socketIndex, "@F" + updatedCredits + ".0" + '\2', 0);
            Proc_6_247_8027E0(socketIndex, "A^" + furnitureId + '\2' + "H" + '\2', 0);
            furniture.deleteFurniture(furnitureId);
            Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "ROOMS", roomId + ".cache").toString(), 0, 0);
            Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "PATHFINDER", roomId + ".cache").toString(), 0, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_74_7265B0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "Aa");
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !handlingUserHasRoomRight(callerUserId, roomId)) {
                return;
            }
            LongRef offset = new LongRef(1);
            long revokeCount = readWireLong(requestPayload, offset);
            if (revokeCount < 1L || revokeCount > 150L) {
                return;
            }
            RoomDao rooms = roomDao();
            if (rooms == null) {
                return;
            }
            for (long revokeIndex = 1L; revokeIndex <= revokeCount; revokeIndex++) {
                String targetUserId = String.valueOf(readWireLong(requestPayload, offset));
                if (!targetUserId.isEmpty() && !"0".equals(targetUserId)) {
                    rooms.deleteRoomRight(NumberUtils.parseLong(targetUserId), roomId);
                    int targetSocketIndex = handlingSocketFromUserId(targetUserId);
                    if (targetSocketIndex > 0) {
                        Proc_6_244_801E80(targetSocketIndex, "@k", 0);
                    }
                }
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_75_7269D0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "EB");
            LongRef offset = new LongRef(1);
            String targetName = readWireString(requestPayload, offset);
            if (targetName.isEmpty()) {
                return;
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !handlingUserHasRoomRight(callerUserId, roomId)) {
                return;
            }
            UserDao users = userDao();
            RoomDao rooms = roomDao();
            if (users == null || rooms == null) {
                return;
            }
            long targetUserId = users.userIdByName(targetName);
            if (targetUserId <= 0L) {
                return;
            }
            rooms.deleteRoomRight(targetUserId, roomId);
            Proc_6_244_801E80(socketIndex, RoomPayloads.roomRightRemoved(), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static String Proc_6_76_726CE0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "Es");
            LongRef offset = new LongRef(1);
            String targetUserId = String.valueOf(readWireLong(requestPayload, offset));
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
            UserDao users = userDao();
            if (users == null) {
                return "";
            }
            long giverUserIdValue = NumberUtils.parseLong(giverUserId);
            long targetUserIdValue = NumberUtils.parseLong(targetUserId);
            long respectAmount = users.respectAmount(giverUserIdValue);
            if (respectAmount <= 0L) {
                return "";
            }
            users.spendRespect(giverUserIdValue);
            users.receiveRespect(targetUserIdValue);
            Proc_6_205_7D9780(socketIndex, 3);
            Proc_6_205_7D9780(targetSocketIndex, 2);
            long respectReceived = users.respectReceived(targetUserIdValue);
            String payload = UserPayloads.respectReceived(targetUserIdValue, respectReceived);
            Proc_6_247_8027E0(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static void Proc_6_77_727590(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "FD");
            LongRef offset = new LongRef(1);
            long roomId = readWireLong(requestPayload, offset);
            if (roomId <= 0L) {
                return;
            }
            RoomDao rooms = roomDao();
            if (rooms == null) {
                return;
            }
            RoomDao.OfficialRoomModel officialRoom = rooms.officialRoomModel(roomId).orElse(null);
            if (officialRoom == null) {
                return;
            }
            Proc_6_244_801E80(socketIndex, RoomPayloads.officialRoomModel(roomId, officialRoom), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_78_7279A0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (socketIndex <= 0 || userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            RoomDao rooms = roomDao();
            if (rooms == null) {
                return;
            }
            RoomDao.RoomModelEntry roomEntry = rooms.roomModelEntry(roomId).orElse(null);
            if (roomEntry == null) {
                return;
            }
            long modelId = roomEntry.modelId();
            String modelPayload = normalizeRoomModelMap(roomEntry.modelMap());
            Proc_6_244_801E80(socketIndex, "Bf" + "/client.php" + '\2', 0);
            Proc_6_244_801E80(socketIndex, "AE" + roomId + '\2' + "H", 0);
            if (!modelPayload.isEmpty()) {
                Proc_6_244_801E80(socketIndex, "@_" + modelPayload + '\2', 0);
                Proc_6_244_801E80(socketIndex, "GV" + modelPayload + '\2', 0);
                Proc_6_244_801E80(socketIndex, "GWH" + modelPayload + '\2' + "H", 0);
            }
            Proc_6_81_730010(socketIndex, roomId, -1);
            Proc_6_82_731070(socketIndex, roomId, 0);
            Proc_6_84_733600(socketIndex, roomId);
            Proc_6_83_732640(socketIndex, modelId);
            Proc_6_85_73A8E0(socketIndex, roomId);
            Proc_6_235_7F77E0(socketIndex, 0, 0);
            Proc_6_80_72EB60(socketIndex, roomId);
            Proc_6_244_801E80(socketIndex, "CP" + '\2' + '\2', 0);
            sendRoomPollPrompt(socketIndex, userId, roomId);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_79_72A430(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (socketIndex <= 0 || userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            RoomDao rooms = roomDao();
            if (rooms == null) {
                return;
            }
            RoomDao.RoomPresentationState roomState = rooms.roomPresentationState(roomId).orElse(null);
            if (roomState == null) {
                return;
            }
            long modelId = roomState.modelId();
            String floorPattern = roomState.floorPattern();
            String wallpaperPattern = roomState.wallpaperPattern();
            String landscapePattern = roomState.landscapePattern();
            long roomRate = roomState.roomRate();
            if (roomRate < 0L) {
                roomRate = 0L;
            }
            String modelPayload = normalizeRoomModelMap(roomState.modelMap());
            String ownerUserId = String.valueOf(roomState.ownerUserId());
            long disableWalls = roomState.disableWalls();
            long thicknessFloor = roomState.thicknessFloor();
            long thicknessWallpaper = roomState.thicknessWallpaper();
            boolean hasControl = handlingUserHasRoomRight(userId, roomId)
                || handlingUserHasPermission(userId, "fuse_any_room_controller");
            boolean hasVoted = rooms.hasRatedRoom(NumberUtils.parseLong(userId), roomId);
            long ratingPayloadValue = hasVoted ? -1L : roomRate;
            Proc_6_244_801E80(socketIndex, RoomPayloads.currentRoom(roomId), 0);
            Proc_6_244_801E80(socketIndex, "@nfloor" + '\2' + floorPattern + '\2', 0);
            Proc_6_244_801E80(socketIndex, "@nwallpaper" + '\2' + wallpaperPattern + '\2', 0);
            Proc_6_244_801E80(socketIndex, "@nlandscape" + '\2' + landscapePattern + '\2', 0);
            Proc_6_244_801E80(socketIndex, RoomPayloads.rating(ratingPayloadValue), 0);
            Proc_6_244_801E80(socketIndex, "Er" + Proc_6_51_716AC0(roomId, 0, 0), 0);
            if (hasControl) {
                Proc_6_244_801E80(socketIndex, "@j", 0);
            }
            if (ownerUserId.equals(String.valueOf((long) NumberUtils.parseLong(userId)))) {
                Proc_6_244_801E80(socketIndex, "@o", 0);
            }
            if (!modelPayload.isEmpty()) {
                Proc_6_244_801E80(socketIndex, "@_" + modelPayload + '\2', 0);
                Proc_6_244_801E80(socketIndex, "GV" + modelPayload + '\2', 0);
            }
            Proc_6_244_801E80(socketIndex, RoomPayloads.wallOptions(disableWalls, thicknessFloor, thicknessWallpaper), 0);
            Proc_6_81_730010(socketIndex, roomId, -1);
            Proc_6_82_731070(socketIndex, roomId, 0);
            Proc_6_83_732640(socketIndex, modelId);
            Proc_6_84_733600(socketIndex, roomId);
            Proc_6_85_73A8E0(socketIndex, roomId);
            Proc_6_235_7F77E0(socketIndex, 0, 0);
            Proc_6_80_72EB60(socketIndex, roomId);
            Proc_6_244_801E80(socketIndex, "CP" + '\2' + '\2', 0);
            sendRoomPollPrompt(socketIndex, userId, roomId);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_80_72EB60(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = args != null && args.length >= 2 ? NumberUtils.parseLong(args[1]) : 0L;
            if (roomId <= 0L) {
                roomId = handlingCurrentRoomId(socketIndex, userId);
            }
            if (roomId <= 0L) {
                return;
            }
            RoomDao rooms = roomDao();
            if (rooms == null) {
                return;
            }
            RoomUserEntryRow entry = rooms.roomUserEntry(NumberUtils.parseLong(userId), roomId).orElse(null);
            if (entry == null) {
                return;
            }
            long roomUserIndex = representedRoomUserIndex(socketIndex, userId);
            String positionZ = "0.0";
            long directionValue = 0L;
            String entryPayload = Proc_6_41_712730(
                entry.userId(),
                entry.name(),
                entry.figure(),
                entry.motto(),
                entry.gender(),
                roomUserIndex,
                entry.positionX(),
                entry.positionY(),
                positionZ,
                directionValue,
                0);
            if (!entryPayload.isEmpty()) {
                Proc_6_247_8027E0(socketIndex, RoomPayloads.occupantEntries(1, entryPayload), 0);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_81_730010(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long roomId = args != null && args.length >= 2 ? NumberUtils.parseLong(args[1]) : 0L;
            if (roomId <= 0L && socketIndex > 0) {
                String userId = handlingUserIdFromSocket(socketIndex);
                if (!userId.isEmpty() && !"0".equals(userId)) {
                    roomId = handlingCurrentRoomId(socketIndex, userId);
                }
            }
            if (socketIndex <= 0 || roomId <= 0L) {
                return;
            }
            RoomDao rooms = roomDao();
            if (rooms == null) {
                return;
            }
            long roomSlot = rooms.roomSlot(roomId);
            StringBuilder occupantPayload = new StringBuilder();
            StringBuilder statusPayload = new StringBuilder();
            long occupantCount = 0L;
            long statusCount = 0L;
            for (RoomOccupantRow occupant : rooms.activeRoomOccupants(roomId)) {
                if (occupant != null) {
                    long roomUserIndex = occupant.roomUserIndex();
                    String genderText = StringUtils.text(occupant.gender()).toUpperCase();
                    genderText = genderText.isEmpty() ? "M" : genderText.substring(0, 1);
                    if (!"M".equals(genderText) && !"F".equals(genderText)) {
                        genderText = "M";
                    }
                    long positionX = occupant.positionX();
                    long positionY = occupant.positionY();
                    if (roomSlot > 0L) {
                        MovementPosition movementPosition = movementPosition(Licence.representedRooms().movementPosition(roomSlot, roomUserIndex));
                        if (movementPosition.found) {
                            positionX = movementPosition.positionX;
                            positionY = movementPosition.positionY;
                        }
                    }
                    String positionZ = "0.0";
                    long directionValue = 0L;
                    occupantPayload.append(Proc_6_41_712730(occupant.userId(), occupant.name(), occupant.figure(),
                        occupant.motto(), genderText, roomUserIndex, positionX, positionY, positionZ, 0, 0));
                    statusPayload.append(SocialPayloads.roomOccupantStatus(
                        roomUserIndex, positionX, positionY, positionZ, directionValue));
                    occupantCount++;
                    statusCount++;
                }
            }
            if (roomSlot > 0L) {
                String botEntities = representedBotEntitiesForRoom(roomSlot, 0);
                for (String botRow : botEntities.split("\r", -1)) {
                    long botEntityId = NumberUtils.parseLong(botRow);
                    if (botEntityId > 0L) {
                        String botName = representedBotRecordField(botEntityId, 2);
                        String botFigure = representedBotRecordField(botEntityId, 10);
                        long positionX = representedBotRecordLong(botEntityId, 6);
                        long positionY = representedBotRecordLong(botEntityId, 7);
                        String positionZ = representedBotRecordField(botEntityId, 8);
                        long directionValue = representedBotRecordLong(botEntityId, 9);
                        if (positionZ.isEmpty()) {
                            positionZ = "0.0";
                        }
                        String botEntry = Proc_6_42_712FB0(botEntityId, botName, botFigure, "M", botEntityId,
                            positionX, positionY, positionZ, 2);
                        if (!botEntry.isEmpty()) {
                            occupantPayload.append(botEntry);
                            statusPayload.append(SocialPayloads.roomOccupantStatus(
                                botEntityId, positionX, positionY, positionZ, directionValue));
                            occupantCount++;
                            statusCount++;
                        }
                    }
                }
            }
            Proc_6_244_801E80(socketIndex, RoomPayloads.occupantEntries(occupantCount, occupantPayload.toString()), -1);
            Proc_6_244_801E80(socketIndex, RoomPayloads.occupantStatuses(statusCount, statusPayload.toString()), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_82_731070(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long roomId = args != null && args.length >= 2 ? NumberUtils.parseLong(args[1]) : 0L;
            if (roomId <= 0L && socketIndex > 0) {
                String userId = handlingUserIdFromSocket(socketIndex);
                if (!userId.isEmpty() && !"0".equals(userId)) {
                    roomId = handlingCurrentRoomId(socketIndex, userId);
                }
            }
            if (socketIndex <= 0 || roomId <= 0L) {
                return;
            }
            RoomDao rooms = roomDao();
            if (rooms == null) {
                return;
            }
            for (RoomDao.ActiveRoomEffect activeEffect : rooms.activeRoomEffects(roomId)) {
                long roomUserIndex = activeEffect.roomUserIndex();
                long effectId = activeEffect.effectId();
                if (roomUserIndex > 0L && effectId > 0L) {
                    Proc_6_244_801E80(socketIndex, SocialPayloads.roomUserEffect(roomUserIndex, effectId), 0);
                }
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static String Proc_6_83_732640(Object... args) {
        try {
            int socketIndex = 0;
            long modelId = 0L;
            if (args != null && args.length >= 2) {
                socketIndex = handlingSocketIndex(args);
                modelId = NumberUtils.parseLong(args[1]);
            } else if (args != null && args.length >= 1) {
                modelId = NumberUtils.parseLong(args[0]);
            }
            if (modelId <= 0L && socketIndex > 0) {
                String userId = handlingUserIdFromSocket(socketIndex);
                if (!userId.isEmpty() && !"0".equals(userId)) {
                    long roomId = handlingCurrentRoomId(socketIndex, userId);
                    if (roomId > 0L) {
                        RoomDao rooms = roomDao();
                        if (rooms != null) {
                            modelId = rooms.modelIdByRoom(roomId);
                        }
                    }
                }
            }
            if (modelId <= 0L) {
                return "";
            }
            RoomDao rooms = roomDao();
            if (rooms == null) {
                return "";
            }
            long itemCount = 0L;
            StringBuilder itemPayload = new StringBuilder();
            for (RoomModelFurnitureRow row : rooms.modelFurnitureRows(modelId)) {
                if (row != null) {
                    long productId = row.productId();
                    long sourceId = row.sourceId();
                    if (sourceId <= 0L) {
                        sourceId = itemCount + 1L;
                    }
                    if (productId <= 0L) {
                        productId = sourceId;
                    }
                    itemPayload.append(Proc_6_161_7B2EE0(sourceId, row.positionX(), row.positionY(), row.rotation(),
                        row.positionZ(), "", row.action(), 0, productId));
                    itemCount++;
                }
            }
            String payload = FurniturePayloads.floorList(itemCount, itemPayload.toString());
            if (socketIndex > 0) {
                Proc_6_244_801E80(socketIndex, payload, 0);
            }
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    public static String Proc_6_84_733600(Object... args) {
        String payload = "Di" + Licence.wiredSettings().statePayload();
        try {
            int socketIndex = args != null && args.length >= 1 ? handlingSocketIndex(args) : 0;
            long roomId = args != null && args.length >= 2 ? NumberUtils.parseLong(args[1]) : 0L;
            if (roomId <= 0L && socketIndex > 0) {
                String userId = handlingUserIdFromSocket(socketIndex);
                if (!userId.isEmpty() && !"0".equals(userId)) {
                    roomId = handlingCurrentRoomId(socketIndex, userId);
                }
            }
            if (socketIndex > 0) {
                Proc_6_244_801E80(socketIndex, payload, 0);
            }
            if (roomId <= 0L) {
                return payload;
            }
            Path cacheRoot = Path.of(Functions.applicationPath, "cache");
            String triggerCache = handlingEnsureRoomCacheFile(cacheRoot.resolve("wired_trigger").resolve(roomId + ".cache").toString());
            String actionCache = handlingEnsureRoomCacheFile(cacheRoot.resolve("wired_action").resolve(roomId + ".cache").toString());
            String conditionCache = handlingEnsureRoomCacheFile(cacheRoot.resolve("wired_condition").resolve(roomId + ".cache").toString());
            String pathfinderCache = handlingEnsureRoomCacheFile(cacheRoot.resolve("pathfinder").resolve(roomId + ".cache").toString());
            String destinationCache = handlingEnsureRoomCacheFile(cacheRoot.resolve("rooms").resolve("destination_" + roomId + ".cache").toString());
            String roomCache = handlingEnsureRoomCacheFile(cacheRoot.resolve("rooms").resolve(roomId + ".cache").toString());
            return payload + '\t' + triggerCache + '\t' + actionCache + '\t' + conditionCache
                + '\t' + pathfinderCache + '\t' + destinationCache + '\t' + roomCache;
        } catch (Exception ignored) {
            return payload;
        }
    }

    public static String Proc_6_85_73A8E0(Object... args) {
        try {
            int socketIndex = args != null && args.length >= 1 ? handlingSocketIndex(args) : 0;
            long roomId = args != null && args.length >= 2 ? NumberUtils.parseLong(args[1]) : 0L;
            if (roomId <= 0L && socketIndex > 0) {
                String userId = handlingUserIdFromSocket(socketIndex);
                if (!userId.isEmpty() && !"0".equals(userId)) {
                    roomId = handlingCurrentRoomId(socketIndex, userId);
                }
            }
            if (roomId <= 0L) {
                return "";
            }
            FurnitureDao furniture = furnitureDao();
            if (furniture == null) {
                return "";
            }
            long itemCount = 0L;
            StringBuilder itemPayload = new StringBuilder();
            for (FurnitureDao.WallFurniture wallFurniture : furniture.wallFurnitureInRoom(roomId)) {
                long furnitureId = wallFurniture.furnitureId();
                long productId = wallFurniture.productId();
                String wallPosition = StringUtils.text(wallFurniture.wallPosition());
                String signText = StringUtils.text(wallFurniture.sign());
                long secondaryValue = wallFurniture.secondaryValue();
                if (furnitureId > 0L && productId > 0L && !wallPosition.isEmpty()) {
                    itemPayload.append(Proc_6_156_7972B0(furnitureId, productId, wallPosition, signText, secondaryValue));
                    itemCount++;
                }
            }
            String payload = FurniturePayloads.wallList(itemCount, itemPayload.toString());
            if (socketIndex > 0) {
                Proc_6_244_801E80(socketIndex, payload, 0);
            }
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    public static String Proc_6_86_73B0D0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingPacketPayload(args);
            if (requestPayload.startsWith("p`") || requestPayload.startsWith("rt")) {
                requestPayload = requestPayload.substring(2);
            }
            LongRef offset = new LongRef(1);
            long furnitureId = readWireLong(requestPayload, offset);
            if (furnitureId <= 0L) {
                furnitureId = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
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
            FurnitureDao furniture = furnitureDao();
            PackageDao packages = packageDao();
            if (furniture == null || packages == null) {
                return "";
            }
            FurnitureDao.RoomFurnitureProduct furnitureProduct = furniture.roomFurnitureProductById(furnitureId, roomId)
                .orElse(null);
            if (furnitureProduct == null) {
                return "";
            }
            long productId = furnitureProduct.productId();
            if (productId <= 0L) {
                return "";
            }
            PackageDao.PackageRow packageRow = packages.packageByProduct(productId).orElse(null);
            if (packageRow == null) {
                return "";
            }
            String packageType = StringUtils.text(packageRow.secondaryType()).toLowerCase();
            long containedPetId = packageRow.containedId();
            if (!"packages_pets".equals(packageType) || containedPetId <= 0L) {
                return "";
            }
            PackageDao.PetPackage petPackage = packages.petPackage(containedPetId).orElse(null);
            if (petPackage == null) {
                return "";
            }
            long petType = petPackage.petType();
            long petRace = petPackage.race();
            String petColor = StringUtils.text(petPackage.color());
            String payload = PetPayloads.packagePreview(furnitureId, petType, petRace, petColor);
            Proc_6_244_801E80(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    public static String Proc_6_87_73C120(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "n~");
            LongRef offset = new LongRef(1);
            long furnitureId = readWireLong(requestPayload, offset);
            if (furnitureId <= 0L) {
                furnitureId = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            }
            String petName = Functions.Proc_10_10_80A7F0(readWireString(requestPayload, offset), 0, 0);
            if (petName.isEmpty()) {
                petName = Functions.Proc_10_10_80A7F0(Functions.Proc_10_7_80A190(requestPayload, 0, 0), 0, 0);
            }
            long validationCode = Proc_6_181_7CA920(petName, 0, 0);
            if (validationCode > 0L) {
                Proc_6_244_801E80(socketIndex, PetPayloads.packageNameValidation(furnitureId, validationCode, petName), 0);
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
            FurnitureDao furniture = furnitureDao();
            PackageDao packages = packageDao();
            BotDao bots = botDao();
            if (furniture == null || packages == null || bots == null) {
                return "";
            }
            FurnitureDao.RoomFurnitureOwnerProduct furnitureProduct = furniture.roomFurnitureOwnerProduct(furnitureId, roomId)
                .orElse(null);
            if (furnitureProduct == null) {
                return "";
            }
            long productId = furnitureProduct.productId();
            long ownerId = furnitureProduct.ownerId();
            long numericUserId = NumberUtils.parseLong(userId);
            if (productId <= 0L || ownerId != numericUserId
                && !handlingUserOwnsRoom(userId, roomId)
                && !handlingUserHasRoomRight(userId, roomId)) {
                return "";
            }
            PackageDao.PackageRow packageRow = packages.packageByProduct(productId).orElse(null);
            if (packageRow == null) {
                return "";
            }
            String packageType = StringUtils.text(packageRow.secondaryType()).toLowerCase();
            long containedPetId = packageRow.containedId();
            if (!"packages_pets".equals(packageType) || containedPetId <= 0L) {
                return "";
            }
            PackageDao.PetPackage petPackage = packages.petPackage(containedPetId).orElse(null);
            if (petPackage == null) {
                return "";
            }
            String petFigure = String.valueOf(petPackage.petType()) + ' '
                + String.valueOf(petPackage.race()) + ' '
                + StringUtils.text(petPackage.color());
            bots.insertPetBot(numericUserId, petFigure.toLowerCase(), petName);
            long botId = bots.newestPetBotId(numericUserId);
            if (botId <= 0L) {
                return "";
            }
            bots.insertPetData(botId, numericUserId);
            String inventoryRow = PetPayloads.inventoryRow(new PetInventoryRow(botId, petName, petFigure, 0L));
            if (!inventoryRow.isEmpty()) {
                Proc_6_244_801E80(socketIndex, PetPayloads.inventoryAdd(inventoryRow), 0);
            }
            Proc_6_146_76D300(socketIndex, furnitureId, productId);
            Proc_6_247_8027E0(socketIndex, "A^" + furnitureId + '\2' + "H" + '\2', 0);
            furniture.deleteFurniture(furnitureId);
            Proc_6_244_801E80(socketIndex, PetPayloads.packageNameValidation(furnitureId, validationCode, petName), 0);
            return String.valueOf(botId);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static void Proc_6_88_73E4F0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            LocalDateTime now = LocalDateTime.now();
            if (Licence.newFriendRooms().shouldRefresh(now)) {
                RoomDao rooms = roomDao();
                if (rooms == null) {
                    return;
                }
                Licence.setNewFriendRooms(rooms.newFriendRoomPicks(), now.plusSeconds(90L));
            }
            NewFriendRooms.RoomPick roomPick = Licence.newFriendRooms().randomRoom();
            Proc_6_244_801E80(socketIndex, NavigatorPayloads.newFriendRoom(roomPick), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static String Proc_6_89_73EA10(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return "";
            }
            int targetSocketIndex = representedInteractionPartner(socketIndex);
            if (targetSocketIndex <= 0) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            String targetUserId = handlingUserIdFromSocket(targetSocketIndex);
            if (userId.isEmpty() || "0".equals(userId) || targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return "";
            }
            String sourceSqlIds = representedTradeOfferSqlIds(representedTradeOffers, socketIndex);
            String targetSqlIds = representedTradeOfferSqlIds(representedTradeOffers, targetSocketIndex);
            if (sourceSqlIds.isEmpty() && targetSqlIds.isEmpty()) {
                return "";
            }
            String sourceLogItems = representedTradeOfferLogItems(representedTradeOffers, socketIndex);
            String targetLogItems = representedTradeOfferLogItems(representedTradeOffers, targetSocketIndex);
            TradeDao trades = tradeDao();
            if (trades == null) {
                return "";
            }
            long numericUserId = NumberUtils.parseLong(userId);
            long numericTargetUserId = NumberUtils.parseLong(targetUserId);
            if (!sourceSqlIds.isEmpty()) {
                trades.transferInventoryFurniture(sourceSqlIds, numericUserId, numericTargetUserId);
            }
            if (!targetSqlIds.isEmpty()) {
                trades.transferInventoryFurniture(targetSqlIds, numericTargetUserId, numericUserId);
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            String sessionId = handlingUserSessionId(userId);
            trades.insertTradeLog(numericUserId, numericTargetUserId, sourceLogItems, targetLogItems, roomId, sessionId);
            Proc_6_244_801E80(socketIndex, "Ap", 0);
            Proc_6_244_801E80(targetSocketIndex, "Ap", 0);
            Proc_6_140_769400(socketIndex, "FT", "");
            Proc_6_140_769400(targetSocketIndex, "FT", "");
            removeRepresentedInteractionPair(socketIndex);
            removeRepresentedInteractionPair(targetSocketIndex);
            return "Ap";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static void Proc_6_90_742E80(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
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
            int targetSocketIndex = args != null && args.length >= 2 ? (int) NumberUtils.parseLong(args[1]) : 0;
            if (targetSocketIndex <= 0) {
                targetSocketIndex = representedInteractionPartner(socketIndex);
            }
            long interactionState = args != null && args.length >= 3
                ? NumberUtils.parseLong(args[2])
                : representedInteractionState(socketIndex);
            if (targetSocketIndex <= 0) {
                return;
            }
            String targetUserId = handlingUserIdFromSocket(targetSocketIndex);
            if (targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return;
            }
            String sourcePayload = SocialPayloads.interactionStateForSource(sourceRoomUserIndex, interactionState);
            String targetPayload = SocialPayloads.interactionStateForTarget(sourceRoomUserIndex, interactionState);
            Proc_6_244_801E80(socketIndex, sourcePayload, 0);
            Proc_6_244_801E80(targetSocketIndex, targetPayload, 0);
            if (interactionState == 1L) {
                Proc_6_244_801E80(socketIndex, "Ao", 0);
                Proc_6_244_801E80(targetSocketIndex, "Ao", 0);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static String Proc_6_91_743480(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return "";
            }
            String requestPayload = handlingRequestPayload(args, "FU");
            int targetSocketIndex = representedInteractionPartner(socketIndex);
            if (targetSocketIndex <= 0) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            String targetUserId = handlingUserIdFromSocket(targetSocketIndex);
            if (userId.isEmpty() || "0".equals(userId) || targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return "";
            }
            long furnitureId = stickyFurnitureIdFromPayload(requestPayload);
            if (furnitureId <= 0L) {
                return "";
            }
            FurnitureDao furniture = furnitureDao();
            if (furniture == null) {
                return "";
            }
            FurnitureDao.TradeFurniture tradeFurniture = furniture.tradeFurniture(furnitureId, NumberUtils.parseLong(userId))
                .orElse(null);
            if (tradeFurniture == null) {
                return "";
            }
            long productId = tradeFurniture.productId();
            String signText = StringUtils.text(tradeFurniture.sign());
            long secondaryValue = tradeFurniture.secondaryValue();
            if (productId <= 0L) {
                return "";
            }
            storeRepresentedTradeOffer(socketIndex, furnitureId, productId, signText, secondaryValue);
            String sourcePayload = representedTradeOfferPayload(representedTradeOffers, socketIndex, targetSocketIndex, userId, targetUserId);
            String targetPayload = representedTradeOfferPayload(representedTradeOffers, targetSocketIndex, socketIndex, targetUserId, userId);
            if (!sourcePayload.isEmpty()) {
                Proc_6_244_801E80(socketIndex, sourcePayload, 0);
            }
            if (!targetPayload.isEmpty()) {
                Proc_6_244_801E80(targetSocketIndex, targetPayload, 0);
            }
            return sourcePayload;
        } catch (Exception ignored) {
            return "";
        }
    }

    public static String Proc_6_92_744870(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return "";
            }
            String requestPayload = handlingRequestPayload(args, "AH");
            int targetSocketIndex = representedInteractionPartner(socketIndex);
            if (targetSocketIndex <= 0) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            String targetUserId = handlingUserIdFromSocket(targetSocketIndex);
            if (userId.isEmpty() || "0".equals(userId) || targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return "";
            }
            long furnitureId = stickyFurnitureIdFromPayload(requestPayload);
            if (furnitureId <= 0L) {
                return "";
            }
            FurnitureDao furniture = furnitureDao();
            if (furniture == null) {
                return "";
            }
            if (furniture.tradeFurnitureForRemoval(furnitureId, NumberUtils.parseLong(userId)).isEmpty()) {
                return "";
            }
            removeRepresentedTradeOffer(socketIndex, furnitureId);
            String sourcePayload = representedTradeOfferPayload(representedTradeOffers, socketIndex, targetSocketIndex, userId, targetUserId);
            String targetPayload = representedTradeOfferPayload(representedTradeOffers, targetSocketIndex, socketIndex, targetUserId, userId);
            if (!sourcePayload.isEmpty()) {
                Proc_6_244_801E80(socketIndex, sourcePayload, 0);
            }
            if (!targetPayload.isEmpty()) {
                Proc_6_244_801E80(targetSocketIndex, targetPayload, 0);
            }
            return sourcePayload;
        } catch (Exception ignored) {
            return "";
        }
    }

    public static void Proc_6_93_745D90(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return;
            }
            String requestPayload = handlingRequestPayload(args, "AG");
            long requestedRoomUserIndex = readWireLong(requestPayload, new LongRef(1));
            if (requestedRoomUserIndex <= 0L) {
                requestedRoomUserIndex = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            }
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
            RoomUserTargetRow target = activeRoomUserTarget(callerRoomId, requestedRoomUserIndex);
            if (target == null) {
                return;
            }
            long targetRoomUserIndex = target.roomUserIndex();
            String targetUserId = String.valueOf(target.userId());
            int targetSocketIndex = (int) target.socketIndex();
            if (targetRoomUserIndex <= 0L || targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return;
            }
            if (targetSocketIndex <= 0) {
                targetSocketIndex = handlingSocketFromUserId(targetUserId);
            }
            if (targetSocketIndex <= 0 || targetSocketIndex == socketIndex || representedInteractionPartner(targetSocketIndex) > 0) {
                return;
            }
            storeRepresentedInteractionPair(socketIndex, targetSocketIndex, 1L);
            String callerPayload = SocialPayloads.interactionRequest(
                NumberUtils.parseLong(callerUserId), NumberUtils.parseLong(targetUserId));
            String targetPayload = SocialPayloads.interactionRequest(
                NumberUtils.parseLong(targetUserId), NumberUtils.parseLong(callerUserId));
            Proc_6_244_801E80(socketIndex, callerPayload, 0);
            Proc_6_244_801E80(targetSocketIndex, targetPayload, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_94_746990(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
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
            int targetSocketIndex = args != null && args.length >= 2 ? (int) NumberUtils.parseLong(args[1]) : 0;
            if (targetSocketIndex <= 0) {
                targetSocketIndex = representedInteractionPartner(socketIndex);
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
            String payload = SocialPayloads.interactionClosed(sourceRoomUserIndex);
            Proc_6_244_801E80(socketIndex, payload, 0);
            Proc_6_244_801E80(targetSocketIndex, payload, 0);
            removeRepresentedInteractionPair(socketIndex);
            removeRepresentedInteractionPair(targetSocketIndex);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static String Proc_6_95_746CD0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "Cw");
            String userId = handlingUserIdFromSocket(socketIndex);
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (userId.isEmpty() || "0".equals(userId) || roomId <= 0L) {
                return "";
            }
            long furnitureId = idRequestFromWire(requestPayload, "");
            if (furnitureId <= 0L) {
                return "";
            }
            FurnitureDao.SimpleFloorFurniture item = furnitureDao().simpleFloorFurniture(furnitureId, roomId).orElse(null);
            if (item == null) {
                return "";
            }
            long productId = item.productId();
            String productAction = DataManager.Proc_8_12_806C30(productId, 17, 0);
            return "habbowheel".equals(productAction) ? String.valueOf(furnitureId) : "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_96_747000(Object... args) {
        return handlingSimpleFloorItemUse(args, "AM", 0L, true);
    }

    public static String Proc_6_97_747640(Object... args) {
        return handlingSimpleFloorItemUse(args, "AL", -1L, false);
    }

    public static long Proc_6_98_747D80(Object... args) {
        long currentPresetId = 0L;
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L || (!handlingUserOwnsRoom(userId, roomId) && !handlingUserHasRoomRight(userId, roomId))) {
                return 0L;
            }
            long dimmerFurnitureId = representedDimmerFurnitureId(roomId);
            if (dimmerFurnitureId <= 0L) {
                return 0L;
            }
            FurnitureDao furniture = furnitureDao();
            if (furniture == null) {
                return 0L;
            }
            FurniturePayloads.DimmerPresetPayload dimmerPayload =
                FurniturePayloads.dimmerPresets(furniture.dimmerPresets(dimmerFurnitureId));
            currentPresetId = dimmerPayload.currentPresetId();
            Proc_6_244_801E80(socketIndex, dimmerPayload.payload(), 0);
            return currentPresetId;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return currentPresetId;
        }
    }

    public static long Proc_6_99_748460(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L || (!handlingUserOwnsRoom(userId, roomId) && !handlingUserHasRoomRight(userId, roomId))) {
                return 0L;
            }
            long dimmerFurnitureId = representedDimmerFurnitureId(roomId);
            if (dimmerFurnitureId <= 0L) {
                return 0L;
            }
            FurnitureDao furniture = furnitureDao();
            if (furniture == null) {
                return 0L;
            }
            FurnitureDao.ActiveDimmerState dimmer = furniture.activeDimmerState(dimmerFurnitureId).orElse(null);
            if (dimmer == null) {
                return 0L;
            }
            String currentSign = StringUtils.text(dimmer.sign());
            long currentState = currentSign.isEmpty() ? 0L : NumberUtils.parseLong(currentSign.substring(0, 1));
            if (currentState <= 0L) {
                currentState = 2L;
            }
            long nextState = currentState - 1L;
            if (nextState < 1L) {
                nextState = 2L;
            }
            String signText = nextState + "," + dimmer.presetId() + "," + dimmer.backgroundId()
                + "," + dimmer.colour() + "," + dimmer.lightLevel();
            furniture.updateSignText(dimmerFurnitureId, signText);
            Proc_6_247_8027E0(socketIndex,
                FurniturePayloads.wallState(dimmerFurnitureId, dimmer.productId(), dimmer.wallPosition(), signText), 0);
            return nextState;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    public static long Proc_6_100_748C80(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "EV");
            LongRef offset = new LongRef(1);
            long presetId = readWireLong(requestPayload, offset);
            long backgroundId = readWireLong(requestPayload, offset);
            String colourText = readWireString(requestPayload, offset).toUpperCase();
            long lightLevel = readWireLong(requestPayload, offset);
            if (presetId < 1L || presetId > 3L || backgroundId < 1L || backgroundId > 2L
                || !isDimmerColour(colourText) || lightLevel < 76L || lightLevel > 225L) {
                return 0L;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L || (!handlingUserOwnsRoom(userId, roomId) && !handlingUserHasRoomRight(userId, roomId))) {
                return 0L;
            }
            long dimmerFurnitureId = representedDimmerFurnitureId(roomId);
            if (dimmerFurnitureId <= 0L) {
                return 0L;
            }
            String signText = "2," + presetId + "," + backgroundId + "," + colourText + "," + lightLevel;
            FurnitureDao furniture = furnitureDao();
            if (furniture == null) {
                return 0L;
            }
            furniture.resetDimmerPresetStates(dimmerFurnitureId);
            furniture.updateDimmerPreset(dimmerFurnitureId, presetId, lightLevel, backgroundId, colourText);
            furniture.updateSignText(dimmerFurnitureId, signText);
            FurnitureDao.WallProductPosition wallPosition = furniture.wallProductPosition(dimmerFurnitureId).orElse(null);
            if (wallPosition != null) {
                Proc_6_247_8027E0(socketIndex,
                    FurniturePayloads.wallState(
                        dimmerFurnitureId,
                        wallPosition.productId(),
                        wallPosition.wallPosition(),
                        signText), 0);
            }
            return dimmerFurnitureId;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static long Proc_6_101_749540(Object... args) {
        long listedEffects = 0L;
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            UserDao users = userDao();
            if (users == null) {
                return 0L;
            }
            UserPayloads.EffectListPayload effectPayload =
                UserPayloads.effectList(users.userEffectSummaries(NumberUtils.parseLong(userId)));
            listedEffects = effectPayload.listedEffects();
            Proc_6_244_801E80(socketIndex, effectPayload.payload(), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
        return listedEffects;
    }

    public static long Proc_6_102_749C50(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "");
            if (requestPayload.length() >= 3) {
                requestPayload = requestPayload.substring(2);
            }
            long effectId = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            if (effectId <= 0L) {
                effectId = readWireLong(requestPayload, new LongRef(1));
            }
            if (effectId <= 0L) {
                return 0L;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            UserDao users = userDao();
            if (users == null) {
                return 0L;
            }
            UserEffectActivationRow effect = users.userEffectActivation(NumberUtils.parseLong(userId), effectId).orElse(null);
            if (effect == null) {
                return 0L;
            }
            long effectRowId = effect.rowId();
            long rentSeconds = effect.rentSeconds();
            if (effectRowId <= 0L || rentSeconds <= 0L) {
                return 0L;
            }
            users.activateUserEffect(effectRowId);
            Proc_6_244_801E80(socketIndex, UserPayloads.effectActivated(effectId, rentSeconds), 0);
            String broadcastPayload = SocialPayloads.roomUserEffect(socketIndex, effectId) + "H";
            Proc_6_247_8027E0(socketIndex, broadcastPayload, 0);
            return effectId;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static long Proc_6_103_74A510(Object... args) {
        long expiredCount = 0L;
        try {
            UserDao users = userDao();
            if (users == null) {
                return 0L;
            }
            for (ExpiredUserEffectRow effect : users.expiredUserEffects()) {
                long effectId = effect.effectId();
                int socketIndex = (int) effect.socketIndex();
                if (socketIndex > 0 && effectId > 0L) {
                    Proc_6_247_8027E0(socketIndex, SocialPayloads.roomUserEffectCleared(socketIndex), 0);
                    Proc_6_244_801E80(socketIndex, UserPayloads.effectExpired(effectId), 0);
                    expiredCount++;
                }
            }
            users.deleteExpiredUserEffects();
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
        return expiredCount;
    }

    public static void Proc_6_104_74AB60(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            long maxOwnedRooms = NumberUtils.parseLong(Functions.Proc_10_0_809570("com.server.socket.game.rooms.own.max", 0, 0));
            RoomDao rooms = roomDao();
            long ownedRoomCount = rooms == null ? 0L : rooms.ownedRoomCount(NumberUtils.parseLong(userId));
            Proc_6_244_801E80(socketIndex, RoomPayloads.creatableRoomCount(maxOwnedRooms, ownedRoomCount), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_105_74AD50(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "@]");
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            RoomDao rooms = roomDao();
            if (rooms == null) {
                return;
            }
            long maxOwnedRooms = NumberUtils.parseLong(Functions.Proc_10_0_809570("com.server.socket.game.rooms.own.max", 0, 0));
            long userIdValue = NumberUtils.parseLong(userId);
            long ownedRoomCount = rooms.ownedRoomCount(userIdValue);
            if (maxOwnedRooms > 0L && ownedRoomCount >= maxOwnedRooms) {
                return;
            }
            LongRef offset = new LongRef(1);
            String roomName = left(Functions.Proc_10_10_80A7F0(readWireString(requestPayload, offset), 0, 0), 25);
            String modelName = left(Functions.Proc_10_11_80A9C0(readWireString(requestPayload, offset), 0, 0), 10);
            if (roomName.isEmpty() || modelName.isEmpty()) {
                return;
            }
            RoomDao.CreatableRoomModel model = rooms.creatableRoomModel(handlingUserHcLevel(userId), modelName).orElse(null);
            if (model == null) {
                return;
            }
            long modelId = model.modelId();
            long visitorsMax = model.visitorsMax();
            if (modelId <= 0L) {
                return;
            }
            if (visitorsMax <= 0L) {
                visitorsMax = 25L;
            }
            rooms.insertRoom(userIdValue, roomName, visitorsMax, modelId);
            long roomId = rooms.newestRoomId();
            if (roomId <= 0L) {
                return;
            }
            Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "ROOMS", roomId + ".cache").toString(), 0, 0);
            Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "PATHFINDER", roomId + ".cache").toString(), 0, 0);
            Proc_6_244_801E80(socketIndex, RoomPayloads.createdRoom(roomId, roomName), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_106_74B750(Object... args) {
        try {
            if (args != null && args.length >= 1) {
                Files.deleteIfExists(Path.of(StringUtils.text(args[0])));
            }
        } catch (Exception ignored) {
            // VB6 source suppresses file delete failures.
        }
    }

    public static void Proc_6_107_74B7E0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId) || !handlingUserHasPermission(userId, "fuse_client_staff")) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            RoomDao rooms = roomDao();
            if (rooms == null) {
                return;
            }
            long ownerUserId = rooms.roomOwnerId(roomId);
            if (ownerUserId <= 0L) {
                return;
            }
            long currentPicked = rooms.staffPickedState(roomId);
            long newPicked = currentPicked == 0L ? 1L : 0L;
            long categoryId = NumberUtils.parseLong(Functions.Proc_10_0_809570("com.client.navigator.staff_picked.category.id.default", 0, 0));
            if (categoryId <= 0L) {
                categoryId = 1L;
            }
            rooms.deleteStaffPickedOfficialRoom(categoryId, roomId);
            if (newPicked != 0L) {
                long styleId = NumberUtils.parseLong(Functions.Proc_10_0_809570("com.client.navigator.staff_picked.style.default", 0, 0));
                long iconId = NumberUtils.parseLong(Functions.Proc_10_0_809570("com.client.navigator.staff_picked.category.icon.default", 0, 0));
                rooms.insertStaffPickedOfficialRoom(categoryId, roomId, styleId, iconId);
                UserDao users = userDao();
                if (users != null) {
                    users.incrementStaffPickedCount(ownerUserId);
                }
            }
            rooms.updateStaffPickedState(roomId, newPicked);
            String queryTail = "users,rooms,rooms_categories WHERE rooms.id='" + roomId
                + "' AND users.id=rooms.id_owner AND rooms_categories.id=rooms.id_category LIMIT 1";
            Proc_6_247_8027E0(socketIndex, Proc_6_112_74E0C0(queryTail, "GF", 0), 0);
            Proc_6_247_8027E0(socketIndex, RoomPayloads.entryUpdated(roomId), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_108_74D800(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            long maxFavorites = NumberUtils.parseLong(Functions.Proc_10_0_809570("com.server.socket.game.rooms.favourites.max", 30, 0));
            if (maxFavorites <= 0L) {
                maxFavorites = 30L;
            }
            RoomDao rooms = roomDao();
            if (rooms == null) {
                return;
            }
            Proc_6_244_801E80(socketIndex,
                NavigatorPayloads.favouriteRoomIds(rooms.favouriteRoomIds(NumberUtils.parseLong(userId), maxFavorites),
                    maxFavorites).payload(),
                0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_109_74DBD0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "@T");
            long roomId = readWireLong(requestPayload, new LongRef(1));
            if (roomId <= 0L) {
                roomId = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            RoomDao rooms = roomDao();
            if (rooms != null) {
                rooms.deleteFavouriteRoom(NumberUtils.parseLong(userId), roomId);
            }
            Proc_6_244_801E80(socketIndex, RoomPayloads.favouriteRemoved(roomId), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_110_74DDA0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "@S");
            long roomId = readWireLong(requestPayload, new LongRef(1));
            if (roomId <= 0L) {
                roomId = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            RoomDao rooms = roomDao();
            if (rooms != null) {
                rooms.insertFavouriteRoom(NumberUtils.parseLong(userId), roomId);
            }
            Proc_6_244_801E80(socketIndex, RoomPayloads.favouriteAdded(roomId), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_111_74DF70(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long rankIndex = args != null && args.length >= 4 ? NumberUtils.parseLong(args[3]) : 0L;
            long hcLevel = args != null && args.length >= 5 ? NumberUtils.parseLong(args[4]) : 0L;
            if (rankIndex < 0L) {
                rankIndex = 0L;
            }
            if (rankIndex > 20L) {
                rankIndex = 20L;
            }
            if (hcLevel < 0L) {
                hcLevel = 0L;
            }
            if (hcLevel > 2L) {
                hcLevel = 2L;
            }
            String responsePayload = Licence.roomCategoryCache().payload(rankIndex, hcLevel);
            Proc_6_244_801E80(socketIndex, "C]" + responsePayload, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static String Proc_6_113_74EE70(Object... args) {
        try {
            if (args == null || args.length < 2) {
                return "";
            }
            String eventQueryTail = StringUtils.text(args[0]);
            String roomQueryTail = StringUtils.text(args[1]);
            List<RoomDao.NavigatorEventRow> eventRows = List.of();
            List<NavigatorRoom> roomRows = List.of();
            RoomDao rooms = roomDao();
            if (!eventQueryTail.isEmpty()) {
                String timeFormat = Functions.Proc_10_0_809570("com.mysql.format.time", "%H:%i", 0);
                eventRows = rooms == null ? List.of() : rooms.navigatorEventsByTail(eventQueryTail, timeFormat, true);
            }
            if (!roomQueryTail.isEmpty()) {
                roomRows = rooms == null ? List.of() : rooms.navigatorRoomsByTail(roomQueryTail, false);
            }
            return NavigatorPayloads.combinedRoomList(eventRows, roomRows);
        } catch (Exception ignored) {
            return "";
        }
    }

    public static String Proc_6_114_750550(Object... args) {
        try {
            if (args == null || args.length == 0) {
                return "";
            }
            String queryTail = StringUtils.text(args[0]);
            if (queryTail.isEmpty()) {
                return NavigatorPayloads.eventList(List.of());
            }
            String timeFormat = Functions.Proc_10_0_809570("com.mysql.format.time", "%H:%i", 0);
            RoomDao rooms = roomDao();
            return NavigatorPayloads.eventList(rooms == null ? List.of() : rooms.navigatorEventsByTail(queryTail, timeFormat, false));
        } catch (Exception ignored) {
            return "";
        }
    }

    public static void Proc_6_115_751220(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long categoryId = navigatorCategoryIdFromPacket(args, "GC");
            String categoryFilter = categoryId > 1L ? " rooms_events.id_category='" + categoryId + "' AND" : "";
            long limitValue = navigatorListLimit();
            String queryTail = "rooms_events,users,rooms,rooms_categories WHERE" + categoryFilter
                + " rooms.id=rooms_events.id_room AND rooms_categories.id=rooms.id_category AND users.id=rooms.id_owner "
                + "GROUP BY rooms_events.id ORDER BY rooms_events.id ASC LIMIT " + limitValue;
            long randomTree = Functions.Proc_10_4_809CA0(1, Licence.recommendedRooms().count(), 0);
            Proc_6_244_801E80(socketIndex, NavigatorPayloads.queryResult("GCPC", categoryId, limitValue,
                Proc_6_112_74E0C0(queryTail, 0, 0) + recommendedRoomPayload(randomTree)), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_116_751550(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long categoryId = navigatorCategoryIdFromPacket(args, "GC");
            String categoryFilter = categoryId > 1L ? " rooms.id_category='" + categoryId + "' AND" : "";
            long limitValue = navigatorListLimit();
            String queryTail = "users,rooms,rooms_categories WHERE" + categoryFilter
                + " rooms.visitors_now > 0 AND users.id=rooms.id_owner AND rooms_categories.id=rooms.id_category "
                + "GROUP BY rooms.id ORDER BY rooms.visitors_now DESC LIMIT " + limitValue;
            long randomTree = Functions.Proc_10_4_809CA0(1, Licence.recommendedRooms().count(), 0);
            Proc_6_244_801E80(socketIndex, NavigatorPayloads.queryResult("GC ", categoryId, limitValue,
                Proc_6_112_74E0C0(queryTail, 0, 0) + recommendedRoomPayload(randomTree)), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_117_751880(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            long limitValue = navigatorListLimit();
            String queryTail = "friendships,logs_visitedrooms,users,rooms,rooms_categories WHERE friendships.id_user='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0)
                + "' AND logs_visitedrooms.id_user=friendships.id_friend AND logs_visitedrooms.timestamp_left IS NULL "
                + "AND rooms.id=logs_visitedrooms.id_room AND rooms_categories.id=rooms.id_category "
                + "AND users.id=rooms.id_owner GROUP BY rooms.id ORDER BY rooms.id DESC LIMIT " + limitValue;
            Proc_6_244_801E80(socketIndex, NavigatorPayloads.queryResult("GCQA", "", limitValue,
                Proc_6_112_74E0C0(queryTail, 0, 0)), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_118_751A80(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            long limitValue = navigatorListLimit();
            String queryTail = "friendships,users,rooms,rooms_categories WHERE friendships.id_user='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0)
                + "' AND users.id=friendships.id_friend AND rooms_categories.id=rooms.id_category "
                + "AND users.id=rooms.id_owner GROUP BY rooms.id ORDER BY rooms.visitors_now DESC LIMIT " + limitValue;
            Proc_6_244_801E80(socketIndex, NavigatorPayloads.queryResult("GC", "\0", limitValue,
                Proc_6_112_74E0C0(queryTail, 0, 0)), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_119_751C80(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            long limitValue = navigatorListLimit();
            String queryTail = "rooms_favourites,users,rooms,rooms_categories WHERE rooms_favourites.id_user='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0)
                + "' AND rooms.id=rooms_favourites.id_room AND rooms_categories.id=rooms.id_category "
                + "AND users.id=rooms.id_owner GROUP BY rooms.id ORDER BY rooms.visitors_now DESC LIMIT " + limitValue;
            Proc_6_244_801E80(socketIndex, NavigatorPayloads.queryResult("GCRA", "", limitValue,
                Proc_6_112_74E0C0(queryTail, 0, 0)), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_120_751E80(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            long limitValue = navigatorListLimit();
            String queryTail = "logs_visitedrooms,users,rooms,rooms_categories WHERE logs_visitedrooms.id_user='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0)
                + "' AND rooms.id=logs_visitedrooms.id_room AND rooms_categories.id=rooms.id_category "
                + "AND users.id=rooms.id_owner GROUP BY rooms.id ORDER BY rooms.id DESC LIMIT " + limitValue;
            Proc_6_244_801E80(socketIndex, NavigatorPayloads.queryResult("GCSA", "", limitValue,
                Proc_6_112_74E0C0(queryTail, 0, 0)), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_121_752080(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            long limitValue = navigatorListLimit();
            String queryTail = "users,rooms,rooms_categories WHERE rooms.id_owner='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0)
                + "' AND rooms_categories.id=rooms.id_category AND users.id=rooms.id_owner "
                + "GROUP BY rooms.id ORDER BY rooms.visitors_now DESC LIMIT " + limitValue;
            Proc_6_244_801E80(socketIndex, NavigatorPayloads.queryResult("GCQA", "", limitValue,
                Proc_6_112_74E0C0(queryTail, 0, 0)), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_123_754020(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            RoomDao rooms = roomDao();
            if (rooms == null) {
                return;
            }
            Proc_6_244_801E80(socketIndex, "GB" + NavigatorPayloads.official(rooms.officialNavigatorItems(), true), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_124_754D90(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long limitValue = navigatorListLimit();
            RoomDao rooms = roomDao();
            if (rooms == null) {
                return;
            }
            Proc_6_244_801E80(socketIndex, "GD" + NavigatorPayloads.tagPopularity(rooms.navigatorTagPopularities(limitValue)), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_125_755650(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String tagText = Functions.Proc_10_11_80A9C0(navigatorTextFromPacket(args), 0, 0);
            long limitValue = navigatorListLimit();
            String eventQueryTail = "rooms_events,users,rooms,rooms_categories WHERE (rooms_events.name_category='" + tagText
                + "' OR rooms_events.tag_1='" + tagText + "' OR rooms_events.tag_2='" + tagText
                + "') AND rooms.id=rooms_events.id_room AND rooms_categories.id=rooms.id_category "
                + "AND users.id=rooms.id_owner GROUP BY rooms_events.id ORDER BY rooms_events.id ASC LIMIT " + limitValue;
            String roomQueryTail = "users,rooms,rooms_categories WHERE (rooms.tag_1 = '" + tagText + "' OR rooms.tag_2 = '"
                + tagText + "') AND users.id=rooms.id_owner AND rooms_categories.id=rooms.id_category "
                + "GROUP BY rooms.id ORDER BY rooms.visitors_now DESC LIMIT " + limitValue;
            Proc_6_244_801E80(socketIndex, NavigatorPayloads.queryResult("GCSA", tagText, limitValue,
                Proc_6_113_74EE70(eventQueryTail, roomQueryTail, 0)), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_126_755B40(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long limitValue = navigatorListLimit();
            String queryTail = "users,rooms,rooms_categories WHERE rooms.rate > 0 AND users.id=rooms.id_owner "
                + "AND rooms_categories.id=rooms.id_category GROUP BY rooms.id ORDER BY rooms.rate DESC LIMIT " + limitValue;
            Proc_6_244_801E80(socketIndex, NavigatorPayloads.queryResult("GC", "\b", limitValue,
                Proc_6_112_74E0C0(queryTail, 0, 0)), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_127_755D30(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String searchText = navigatorSearchTerm(navigatorTextFromPacket(args));
            String roomPredicate = searchText.length() > 2
                ? "(users.name LIKE '" + searchText + "%' OR rooms.name LIKE '" + searchText + "%')"
                : "(users.name = '" + searchText + "' OR rooms.name = '" + searchText + "')";
            long limitValue = navigatorListLimit();
            String roomQueryTail = "users,rooms,rooms_categories WHERE " + roomPredicate
                + " AND users.id=rooms.id_owner AND rooms_categories.id=rooms.id_category "
                + "GROUP BY rooms.id ORDER BY rooms.visitors_now DESC LIMIT " + limitValue;
            String eventQueryTail = "rooms_events,users,rooms,rooms_categories WHERE (users.name='" + searchText
                + "' AND rooms_events.id_user=users.id OR rooms_events.name LIKE '" + searchText
                + "%' AND users.id=rooms.id_owner) AND rooms.id=rooms_events.id_room "
                + "AND rooms_categories.id=rooms.id_category GROUP BY rooms_events.id ORDER BY rooms_events.id ASC LIMIT " + limitValue;
            Proc_6_244_801E80(socketIndex, NavigatorPayloads.queryResult("GCSA", searchText, limitValue,
                Proc_6_113_74EE70(eventQueryTail, roomQueryTail, 0)), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static String Proc_6_128_756190(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "Ad");
            LongRef offset = new LongRef(1);
            long catalogProductId = readWireLong(requestPayload, offset);
            String signText = Functions.Proc_10_10_80A7F0(readWireString(requestPayload, offset), 1, 1);
            if (catalogProductId <= 0L) {
                catalogProductId = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            }
            if (signText.isEmpty()) {
                signText = Functions.Proc_10_10_80A7F0(Functions.Proc_10_7_80A190(requestPayload, 0, 0), 1, 1);
            }
            if (catalogProductId <= 0L) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (socketIndex <= 0 || userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            CatalogRegistry.CatalogProduct catalogProduct = Licence.catalogProduct(catalogProductId);
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
                Proc_6_244_801E80(socketIndex, CatalogPayloads.purchaseError(3), 0);
                return "";
            }
            if (balance.credits() < creditPrice) {
                Proc_6_244_801E80(socketIndex, CatalogPayloads.purchaseError(1), 0);
                return "";
            }
            if (balance.activityPoints() < activityPrice) {
                Proc_6_244_801E80(socketIndex, CatalogPayloads.purchaseError(2), 0);
                return "";
            }
            long grantedFurnitureId = NumberUtils.parseLong(Proc_6_129_7583C0(socketIndex, catalogProductId, signText));
            if (grantedFurnitureId <= 0L) {
                return "";
            }
            if (creditPrice > 0L || activityPrice > 0L) {
                users.spendCatalogPurchaseBalance(userIdValue, creditPrice, activityType, activityPrice);
                if (creditPrice > 0L) {
                    Functions.Proc_10_16_80C480(userId, 0, 0);
                }
                if (activityPrice > 0L) {
                    Functions.Proc_10_17_80C6B0(userId, activityType, 0);
                }
            }
            String itemClass = "i";
            if (!"products_deals".equals(typeSecondary)
                && NumberUtils.parseLong(DataManager.Proc_8_12_806C30(productId, 0, 0)) == 8L) {
                itemClass = "I";
            }
            String purchasePayload = CatalogPayloads.purchase(catalogProductId, creditPrice, activityPrice,
                activityType, grantedFurnitureId, itemClass);
            Proc_6_244_801E80(socketIndex, purchasePayload, 0);
            Proc_6_140_769400(socketIndex, "FT", "");
            return purchasePayload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_129_7583C0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long catalogProductId = args != null && args.length >= 2 ? NumberUtils.parseLong(args[1]) : 0L;
            String signText = args != null && args.length >= 3 ? StringUtils.text(args[2]) : "";
            if (socketIndex <= 0 || catalogProductId <= 0L) {
                return "";
            }
            CatalogRegistry.CatalogProduct catalogProduct = Licence.catalogProduct(catalogProductId);
            if (catalogProduct == null) {
                return "";
            }
            long productId = catalogProduct.productId();
            String typeSecondary = catalogProduct.typeSecondary().toLowerCase();
            if (productId <= 0L) {
                return "";
            }
            String grantResult = Proc_6_133_760400(socketIndex, catalogProductId, signText);
            if (grantResult.isEmpty()) {
                return "";
            }
            String[] grantedIds = grantResult.split("[,\r]", -1);
            long[] productIds;
            int itemCount = 0;
            if ("products_deals".equals(typeSecondary)) {
                CatalogRegistry.ProductDeal deal = Licence.productDeal(productId);
                List<Long> dealProductIds = deal == null ? List.<Long>of() : deal.itemProductIds();
                productIds = new long[dealProductIds.size()];
                for (Long dealProductId : dealProductIds) {
                    if (dealProductId != null && dealProductId > 0L) {
                        productIds[itemCount++] = dealProductId;
                    }
                }
            } else {
                itemCount = Math.max(1, grantedIds.length);
                productIds = new long[itemCount];
                for (int index = 0; index < itemCount; index++) {
                    productIds[index] = productId;
                }
            }
            long firstFurnitureId = 0L;
            for (int index = 0; index < itemCount; index++) {
                long furnitureId = index < grantedIds.length ? NumberUtils.parseLong(grantedIds[index]) : 0L;
                long itemProductId = productIds[index];
                if (furnitureId > 0L && itemProductId > 0L) {
                    if (firstFurnitureId == 0L) {
                        firstFurnitureId = furnitureId;
                    }
                    String itemData = DataManager.Proc_8_12_806C30(itemProductId, 24, 0);
                    if (itemData.isEmpty()) {
                        itemData = DataManager.Proc_8_12_806C30(itemProductId, 4, 0);
                    }
                    long productType = NumberUtils.parseLong(DataManager.Proc_8_12_806C30(itemProductId, 0, 0));
                    Proc_6_244_801E80(socketIndex,
                        InventoryMessagePayloads.roomAdd(Proc_6_138_7678A0(furnitureId, itemProductId, itemData, 0)), 0);
                    if ("TROPHY_VAR".equalsIgnoreCase(DataManager.Proc_8_12_806C30(itemProductId, 4, 0))) {
                        String trophySign = handlingUserName(handlingUserIdFromSocket(socketIndex)) + '\b'
                            + recyclerRewardSign() + '\b' + signText;
                        furnitureDao().updateSignText(furnitureId, Functions.Proc_10_10_80A7F0(trophySign, 1, 1));
                    }
                    if (productType == 8L) {
                        Proc_6_244_801E80(socketIndex, CatalogPayloads.dimensionMap(furnitureId,
                            NumberUtils.parseLong(DataManager.Proc_8_12_806C30(itemProductId, 20, 0))), 0);
                    }
                }
            }
            return String.valueOf(firstFurnitureId);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_133_760400(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long catalogProductId = args != null && args.length >= 2 ? NumberUtils.parseLong(args[1]) : 0L;
            String signText = args != null && args.length >= 3 ? StringUtils.text(args[2]) : "";
            if (socketIndex <= 0 || catalogProductId <= 0L) {
                catalogProductId = args != null && args.length >= 1 ? NumberUtils.parseLong(args[0]) : 0L;
                signText = args != null && args.length >= 2 ? StringUtils.text(args[1]) : "";
                socketIndex = 0;
            }
            String userId = socketIndex > 0 ? handlingUserIdFromSocket(socketIndex) : "";
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            CatalogRegistry.CatalogProduct catalogProduct = Licence.catalogProduct(catalogProductId);
            if (catalogProduct == null) {
                return "";
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
                return "";
            }
            long grantedCount = 0L;
            if ("products_deals".equals(typeSecondary)) {
                CatalogRegistry.ProductDeal deal = Licence.productDeal(productId);
                if (deal == null) {
                    return "";
                }
                for (Long dealProductId : deal.itemProductIds()) {
                    if (dealProductId != null && dealProductId > 0L) {
                        String defaultSign = Functions.Proc_10_10_80A7F0(DataManager.Proc_8_12_806C30(dealProductId, 4, 0), 0, 0);
                        if (defaultSign.isEmpty()) {
                            defaultSign = Functions.Proc_10_10_80A7F0(DataManager.Proc_8_12_806C30(dealProductId, 5, 0), 0, 0);
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
                    Functions.Proc_10_23_80E110(userId, hcLevel, hcMonths, hcMonths * 31L);
                }
                String badgeId = DataManager.Proc_8_12_806C30(productId, 26, 0).toUpperCase();
                if (badgeId.isEmpty()) {
                    badgeId = DataManager.Proc_8_12_806C30(productId, 27, 0).toUpperCase();
                }
                if (badgeId.length() > 2) {
                    String existingBadge = StringUtils.text(users.badgeId(userIdValue, badgeId)).toUpperCase();
                    if (!badgeId.equals(existingBadge)) {
                        users.insertBadge(userIdValue, 0L, badgeId);
                        long badgeRowId = users.badgeRowId(userIdValue, badgeId);
                        Proc_6_195_7D38D0(userId, 0, 0);
                        Proc_6_193_7D2BB0(socketIndex, "Ce", "");
                        if (badgeRowId > 0L) {
                            Proc_6_143_76BB80(socketIndex, 0, 0);
                        }
                    }
                }
                String defaultSign = signText;
                if (defaultSign.isEmpty()) {
                    defaultSign = Functions.Proc_10_10_80A7F0(DataManager.Proc_8_12_806C30(productId, 4, 0), 0, 0);
                }
                if (defaultSign.isEmpty()) {
                    defaultSign = Functions.Proc_10_10_80A7F0(DataManager.Proc_8_12_806C30(productId, 5, 0), 0, 0);
                }
                for (long itemIndex = 1L; itemIndex <= amount; itemIndex++) {
                    furniture.insertCatalogFurniture(productId, userIdValue, defaultSign, catalogProductId);
                    grantedCount++;
                }
            }
            if (grantedCount <= 0L) {
                return "";
            }
            StringBuilder grantedIdsBuilder = new StringBuilder();
            for (Long newestId : furniture.newestFurnitureIdsByOwner(userIdValue, grantedCount)) {
                if (grantedIdsBuilder.length() > 0) {
                    grantedIdsBuilder.append(',');
                }
                grantedIdsBuilder.append(newestId);
            }
            String grantedIds = grantedIdsBuilder.toString();
            long firstGrantedId = NumberUtils.parseLong(grantedIds);
            if (!"products_deals".equals(typeSecondary)
                && NumberUtils.parseLong(DataManager.Proc_8_12_806C30(productId, 0, 0)) == 9L && firstGrantedId > 0L) {
                furniture.insertDefaultDimmerPresets(firstGrantedId);
                furniture.updateDefaultDimmerSign(firstGrantedId);
            }
            return grantedIds;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_130_75B770(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "G[");
            String requestedSprite = Functions.Proc_10_7_80A190(requestPayload, 0, 0);
            if (requestedSprite.isEmpty()) {
                requestedSprite = Functions.Proc_10_6_809F10(requestPayload, 0, 0);
            }
            if (requestedSprite.isEmpty()) {
                requestedSprite = requestPayload.replace("\2", "").replace("\0", "").trim();
            }
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
            GiftSettings.ClubGift gift = Licence.giftSettings().clubGiftByCatalogProductId(catalogProductId);
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
            String itemData = DataManager.Proc_8_12_806C30(productId, 24, 0);
            FurnitureDao furniture = furnitureDao();
            furniture.insertClubGiftFurniture(productId, catalogProductId, userIdValue, itemData);
            long insertedFurnitureId = furniture.newestFurnitureIdByOwnerAndProduct(userIdValue, productId);
            String itemClass = NumberUtils.parseLong(DataManager.Proc_8_12_806C30(productId, 0, 0)) == 9L ? "I" : "i";
            String responsePayload = CatalogPayloads.clubGiftClaim(productId,
                DataManager.Proc_8_12_806C30(productId, 24, 0), itemClass, insertedFurnitureId);
            Proc_6_244_801E80(socketIndex, responsePayload, 0);
            clubs.decrementPresents(userIdValue);
            Proc_6_140_769400(socketIndex, "FT", "");
            return responsePayload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static void Proc_6_131_75C700(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (socketIndex <= 0 || userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            ClubDao clubs = clubDao();
            ClubDao.ClubGiftStatus status = clubs == null
                ? new ClubDao.ClubGiftStatus(0L, 0L, 0L, 0L, 0L)
                : clubs.clubGiftStatus(NumberUtils.parseLong(userId))
                    .orElse(new ClubDao.ClubGiftStatus(0L, 0L, 0L, 0L, 0L));
            Proc_6_244_801E80(socketIndex, ClubPayloads.clubGiftStatus(Licence.giftSettings(), status), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static String Proc_6_132_75D4A0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "GX");
            LongRef offset = new LongRef(1);
            long catalogProductId = readWireLong(requestPayload, offset);
            long expectedProductId = readWireLong(requestPayload, offset);
            String recipientName = Functions.Proc_10_10_80A7F0(readWireString(requestPayload, offset), 1, 1);
            String giftMessage = Functions.Proc_10_10_80A7F0(readWireString(requestPayload, offset), 1, 1);
            if (giftMessage.length() > 142) {
                giftMessage = giftMessage.substring(0, 142);
            }
            long wrapProductId = readWireLong(requestPayload, offset);
            long ribbonId = readWireLong(requestPayload, offset);
            long colorId = readWireLong(requestPayload, offset);
            if (catalogProductId <= 0L) {
                catalogProductId = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            }
            if (recipientName.isEmpty()) {
                recipientName = Functions.Proc_10_10_80A7F0(Functions.Proc_10_7_80A190(requestPayload, 0, 0), 1, 1);
            }
            if (catalogProductId <= 0L || recipientName.isEmpty()) {
                return "";
            }
            String senderUserId = handlingUserIdFromSocket(socketIndex);
            if (socketIndex <= 0 || senderUserId.isEmpty() || "0".equals(senderUserId)) {
                return "";
            }
            CatalogRegistry.CatalogProduct catalogProduct = Licence.catalogProduct(catalogProductId);
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
            if (NumberUtils.parseLong(Functions.Proc_10_0_809570("com.client.catalog.gifts.wrap.enabled", 0, 0)) != 0L) {
                long wrapPrice = NumberUtils.parseLong(Functions.Proc_10_0_809570("com.client.catalog.gifts.wrap.price", 0, 0));
                if (wrapProductId <= 0L) {
                    CatalogDao catalog = catalogDao();
                    if (catalog == null) {
                        return "";
                    }
                    wrapProductId = catalog.firstGiftWrapProductId();
                }
                if (wrapProductId > 0L && !Licence.giftSettings().containsGiftWrapProduct(wrapProductId)) {
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
                Proc_6_244_801E80(socketIndex, CatalogPayloads.purchaseError(3), 0);
                return "";
            }
            if (balance.credits() < creditPrice) {
                Proc_6_244_801E80(socketIndex, CatalogPayloads.purchaseError(1), 0);
                return "";
            }
            if (balance.activityPoints() < activityPrice) {
                Proc_6_244_801E80(socketIndex, CatalogPayloads.purchaseError(2), 0);
                return "";
            }
            String recipientUserId = String.valueOf(users.userIdByName(recipientName));
            if (recipientUserId.isEmpty() || "0".equals(recipientUserId)) {
                recipientUserId = senderUserId;
            }
            long grantedFurnitureId = NumberUtils.parseLong(Proc_6_133_760400(socketIndex, catalogProductId, giftMessage));
            if (grantedFurnitureId <= 0L) {
                return "";
            }
            String productSign = DataManager.Proc_8_12_806C30(productId, 4, 0);
            if ("TROPHY_VAR".equalsIgnoreCase(productSign)) {
                productSign = handlingUserName(senderUserId) + '\b' + recyclerRewardSign() + '\b' + giftMessage;
            }
            long giftSecondary = colorId * 1000L + ribbonId;
            furnitureDao().updateGiftMetadata(
                grantedFurnitureId,
                Functions.Proc_10_10_80A7F0(giftMessage, 0, 0),
                Functions.Proc_10_10_80A7F0(productSign, 0, 0),
                NumberUtils.parseLong(recipientUserId),
                catalogProductId,
                giftSecondary);
            if (creditPrice > 0L || activityPrice > 0L) {
                users.spendCatalogPurchaseBalance(senderUserIdValue, creditPrice, activityType, activityPrice);
                if (creditPrice > 0L) {
                    Functions.Proc_10_16_80C480(senderUserId, 0, 0);
                }
                if (activityPrice > 0L) {
                    Functions.Proc_10_17_80C6B0(senderUserId, activityType, 0);
                }
            }
            users.incrementGiftsGiven(senderUserIdValue);
            if (!recipientUserId.equals(senderUserId)) {
                users.incrementGiftsReceived(NumberUtils.parseLong(recipientUserId));
                Proc_6_205_7D9780(socketIndex, 6);
            }
            String purchasePayload = CatalogPayloads.giftPurchase(catalogProductId,
                Licence.Proc_9_1_8072B0(catalogProductId, 0, 0), creditPrice, activityPrice, activityType,
                grantedFurnitureId);
            Proc_6_244_801E80(socketIndex, purchasePayload, 0);
            long recipientSocket = handlingSocketFromUserId(recipientUserId);
            if (recipientSocket > 0L) {
                Proc_6_244_801E80((int) recipientSocket,
                    InventoryMessagePayloads.roomAdd(Proc_6_138_7678A0(grantedFurnitureId, productId, productSign, giftSecondary)), 0);
                Proc_6_205_7D9780((int) recipientSocket, 7);
            }
            return purchasePayload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static void Proc_6_134_765B90(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "oV");
            long itemId = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            if (itemId <= 0L) {
                itemId = readWireLong(requestPayload, new LongRef(1));
            }
            long itemType = NumberUtils.parseLong(Licence.Proc_9_1_8072B0(itemId, 9, 0));
            long giftEnabled = itemType == 1L ? NumberUtils.parseLong(Functions.Proc_10_0_809570("com.client.catalog.gifts.enabled", 0, 0)) : 0L;
            Proc_6_244_801E80(socketIndex, CatalogPayloads.giftAvailability(itemId, giftEnabled), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_135_765D80(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String defaultPayload = CatalogPayloads.giftWrapPriceFallback(
                NumberUtils.parseLong(Functions.Proc_10_0_809570("com.client.catalog.gifts.wrap.enabled", 0, 0)));
            long giftWrapPrice = NumberUtils.parseLong(Functions.Proc_10_0_809570("com.client.catalog.gifts.wrap.price", defaultPayload, 0));
            Proc_6_244_801E80(socketIndex,
                CatalogPayloads.giftWrapOptions(giftWrapPrice, Licence.giftSettings().giftWrapPayload()), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_136_765F10(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingPacketPayload(args);
            if (requestPayload.length() >= 3) {
                requestPayload = requestPayload.substring(2);
            }
            long pageId = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            if (pageId <= 0L) {
                pageId = readWireLong(requestPayload, new LongRef(1));
            }
            String pagePayload = Licence.catalogPages().pagePayload(pageId);
            if (!pagePayload.isEmpty()) {
                Proc_6_244_801E80(socketIndex, CatalogPayloads.page(pageId, pagePayload), 0);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_137_766470(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "BA");
            String voucherCode = requestPayload.startsWith("@")
                ? readWireString(requestPayload, new LongRef(1))
                : requestPayload;
            voucherCode = voucherCode.replace(' ', '0');
            if (voucherCode.length() != 8) {
                Proc_6_244_801E80(socketIndex, VoucherPayloads.invalid(voucherCode), 0);
                return;
            }
            VoucherDao vouchers = voucherDao();
            VoucherDao.VoucherReward voucherReward = vouchers == null ? null : vouchers.reward(voucherCode).orElse(null);
            if (voucherReward == null) {
                Proc_6_244_801E80(socketIndex, VoucherPayloads.invalid(voucherCode), 0);
                return;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty()) {
                Proc_6_244_801E80(socketIndex, VoucherPayloads.invalid(voucherCode), 0);
                return;
            }
            String productSprite = StringUtils.text(voucherReward.productSprite());
            long creditsValue = voucherReward.credits();
            long shellsValue = voucherReward.shells();
            String rewardPayload = "";
            if (productSprite.length() > 2) {
                long productId = vouchers.catalogProductProductIdBySprite(productSprite);
                if (productId != 0L) {
                    rewardPayload = DataManager.Proc_8_12_806C30(productId, 13, 0) + '\2'
                        + DataManager.Proc_8_12_806C30(productId, 14, 0) + '\2';
                }
            }
            UserDao users = userDao();
            if (users == null) {
                Proc_6_244_801E80(socketIndex, VoucherPayloads.invalid(voucherCode), 0);
                return;
            }
            long userIdValue = NumberUtils.parseLong(userId);
            if (creditsValue != 0L) {
                users.addCredits(userIdValue, creditsValue);
                Functions.Proc_10_16_80C480(userId, 0, 0);
            }
            if (shellsValue != 0L) {
                users.addActivityPoints(userIdValue, 0L, shellsValue);
                Functions.Proc_10_17_80C6B0(userId, 0, 0);
            }
            vouchers.deleteVoucher(voucherCode);
            Proc_6_244_801E80(socketIndex, VoucherPayloads.redeemed(rewardPayload), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_139_768100(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "AB");
            String userId = handlingUserIdFromSocket(socketIndex);
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (userId.isEmpty() || roomId <= 0L) {
                return;
            }
            long furnitureId = stickyFurnitureIdFromPayload(requestPayload);
            if (furnitureId <= 0L) {
                return;
            }
            FurnitureDao furniture = furnitureDao();
            RoomDao rooms = roomDao();
            if (furniture == null || rooms == null) {
                return;
            }
            FurnitureDao.DecorationFurniture decorationFurniture = furniture
                .decorationFurniture(furnitureId, NumberUtils.parseLong(userId))
                .orElse(null);
            if (decorationFurniture == null) {
                return;
            }
            long productId = decorationFurniture.productId();
            String decoValue = StringUtils.text(decorationFurniture.sign());
            CatalogRegistry.Product product = Licence.product(productId);
            if (product == null) {
                return;
            }
            long productType = product.type();
            RoomDao.RoomDecoration decoration = RoomDao.RoomDecoration.fromProductType(productType);
            if (decoration == null) {
                return;
            }
            if (decoValue.isEmpty() || "0".equals(decoValue)) {
                decoValue = product.defaultDecoration();
            }
            if (decoValue.isEmpty()) {
                decoValue = product.sprite();
            }
            if (decoValue.isEmpty()) {
                return;
            }
            Proc_6_247_8027E0(socketIndex, "@n" + decoration.wireName() + '\2' + decoValue + '\2', 0);
            rooms.updateDecoration(roomId, decoration, decoValue);
            Proc_6_244_801E80(socketIndex, InventoryMessagePayloads.remove(furnitureId), 0);
            furniture.deleteFurniture(furnitureId);
            Proc_6_140_769400(socketIndex, "FT", "");
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_140_769400(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty()) {
                return;
            }
            FurnitureDao furniture = furnitureDao();
            if (furniture == null) {
                return;
            }
            InventoryPayloads payloads = inventoryPayloadsFromInventory(
                InventoryMessagePayloads.listFromItems(
                    furniture.inventoryFurnitureForOwner(NumberUtils.parseLong(userId))));
            Proc_6_244_801E80(socketIndex,
                InventoryMessagePayloads.regularList(payloads.regularCount, payloads.regularPayload), 0);
            Proc_6_244_801E80(socketIndex,
                InventoryMessagePayloads.iconList(payloads.iconCount, payloads.iconPayload), 0);
            Proc_6_244_801E80(socketIndex, InventoryMessagePayloads.emptyRentalList(), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static String Proc_6_141_76A670(Object... args) {
        return handlingFloorFurnitureMove(args, "A[", false);
    }

    public static String Proc_6_142_76B310(Object... args) {
        return handlingFloorFurnitureMove(args, "rv", true);
    }

    public static void Proc_6_143_76BB80(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty()) {
                return;
            }
            UserDao users = userDao();
            if (users == null) {
                return;
            }
            UserDao.ActivityPointBalance balance = users.activityPointBalance(NumberUtils.parseLong(userId)).orElse(null);
            if (balance == null) {
                return;
            }
            Proc_6_244_801E80(socketIndex, UserPayloads.activityPointBalance(
                balance.pointTypeOne(),
                balance.pointTypeTwo(),
                balance.pointTypeThree(),
                balance.pointTypeFour()), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_144_76BE70(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String packetPayload = handlingPacketPayload(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (userId.isEmpty() || "0".equals(userId) || roomId <= 0L) {
                return;
            }
            long furnitureId = pickupFurnitureIdFromPayload(packetPayload);
            if (furnitureId <= 0L) {
                return;
            }
            FurnitureDao furniture = furnitureDao();
            if (furniture == null) {
                return;
            }
            FurnitureDao.RoomFurnitureOwnerProduct furnitureProduct = furniture
                .roomFurnitureOwnerProduct(furnitureId, roomId)
                .orElse(null);
            if (furnitureProduct == null) {
                return;
            }
            long productId = furnitureProduct.productId();
            String ownerId = String.valueOf(furnitureProduct.ownerId());
            if (productId <= 0L || ownerId.isEmpty() || "0".equals(ownerId)) {
                return;
            }
            boolean canPickUpAny = handlingUserHasPermission(userId, "fuse_pick_up_any_furni");
            if (!ownerId.equals(userId) && !handlingUserOwnsRoom(userId, roomId) && !canPickUpAny) {
                return;
            }
            if (!handlingUserHasRoomRight(userId, roomId) && !ownerId.equals(userId) && !canPickUpAny) {
                return;
            }
            furniture.moveRoomFurnitureToInventory(furnitureId, NumberUtils.parseLong(userId));
            Proc_6_247_8027E0(socketIndex, "A^" + furnitureId + '\2', 0);
            Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "ROOMS", roomId + ".cache").toString(), 0, 0);
            Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "PATHFINDER", roomId + ".cache").toString(), 0, 0);
            Proc_6_140_769400(socketIndex, "FT", "");
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_145_76CA20(Object... args) {
        try {
            int socketIndex = 0;
            long roomId = 0L;
            long furnitureId = 0L;
            if (args != null && args.length >= 3) {
                socketIndex = (int) NumberUtils.parseLong(args[0]);
                roomId = NumberUtils.parseLong(args[1]);
                furnitureId = NumberUtils.parseLong(args[2]);
            } else if (args != null && args.length >= 2) {
                roomId = NumberUtils.parseLong(args[0]);
                furnitureId = NumberUtils.parseLong(args[1]);
            } else if (args != null && args.length >= 1) {
                furnitureId = NumberUtils.parseLong(args[0]);
            }
            if (roomId <= 0L && socketIndex > 0) {
                String userId = handlingUserIdFromSocket(socketIndex);
                if (!userId.isEmpty() && !"0".equals(userId)) {
                    roomId = handlingCurrentRoomId(socketIndex, userId);
                }
            }
            if (furnitureId <= 0L) {
                return;
            }
            FurnitureRoomCache.State cacheState = Licence.furnitureRoomCache();
            FurnitureCacheState state = trackFurnitureCacheMarker(
                cacheState.pendingRoomCache,
                cacheState.pendingFurnitureCache,
                cacheState.representedRoomCache,
                roomId,
                furnitureId);
            Licence.setFurnitureRoomCache(furnitureRoomCacheState(state));
            if (roomId > 0L) {
                Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "ROOMS", roomId + ".cache").toString(), 0, 0);
                Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "PATHFINDER", roomId + ".cache").toString(), 0, 0);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_146_76D300(Object... args) {
        try {
            int socketIndex = 0;
            long furnitureId = 0L;
            long productId = 0L;
            if (args != null && args.length >= 3) {
                socketIndex = (int) NumberUtils.parseLong(args[0]);
                furnitureId = NumberUtils.parseLong(args[1]);
                productId = NumberUtils.parseLong(args[2]);
            } else if (args != null && args.length >= 2) {
                furnitureId = NumberUtils.parseLong(args[0]);
                productId = NumberUtils.parseLong(args[1]);
            } else if (args != null && args.length >= 1) {
                furnitureId = NumberUtils.parseLong(args[0]);
            }
            if (furnitureId <= 0L) {
                return;
            }
            FurnitureDao furniture = furnitureDao();
            if (furniture == null) {
                return;
            }
            FurnitureDao.RoomFurnitureState furnitureState = furniture.roomFurnitureState(furnitureId).orElse(null);
            if (furnitureState == null) {
                return;
            }
            if (productId <= 0L) {
                productId = furnitureState.productId();
            }
            long roomId = furnitureState.roomId();
            FurnitureRoomCache.State cacheState = Licence.furnitureRoomCache();
            FurnitureCacheState state = removeFurnitureCacheMarker(
                cacheState.pendingRoomCache,
                cacheState.pendingFurnitureCache,
                cacheState.representedRoomCache,
                furnitureId);
            Licence.setFurnitureRoomCache(furnitureRoomCacheState(state));
            if (roomId <= 0L && socketIndex > 0) {
                String userId = handlingUserIdFromSocket(socketIndex);
                if (!userId.isEmpty() && !"0".equals(userId)) {
                    roomId = handlingCurrentRoomId(socketIndex, userId);
                }
            }
            if (roomId > 0L) {
                Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "ROOMS", roomId + ".cache").toString(), 0, 0);
                Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "PATHFINDER", roomId + ".cache").toString(), 0, 0);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static long Proc_6_147_76E910(Object... args) {
        try {
            long roomId = 0L;
            long positionX;
            long positionY;
            if (args != null && args.length >= 3) {
                roomId = NumberUtils.parseLong(args[0]);
                positionX = NumberUtils.parseLong(args[1]);
                positionY = NumberUtils.parseLong(args[2]);
            } else if (args != null && args.length >= 2) {
                positionX = NumberUtils.parseLong(args[0]);
                positionY = NumberUtils.parseLong(args[1]);
            } else {
                return 0L;
            }
            FurnitureDao furniture = furnitureDao();
            if (furniture == null) {
                return 0L;
            }
            List<FurnitureDao.FloorPositionFurniture> rows = roomId > 0L
                ? furniture.floorFurnitureAt(roomId, positionX, positionY)
                : furniture.floorFurnitureAt(positionX, positionY);
            long refreshCount = 0L;
            for (FurnitureDao.FloorPositionFurniture row : rows) {
                long furnitureId = row.furnitureId();
                long rowRoomId = roomId > 0L ? roomId : row.roomId();
                long productId = row.productId();
                if (furnitureId > 0L && rowRoomId > 0L && productId > 0L) {
                    String productAction = DataManager.Proc_8_12_806C30(productId, 7, 0).toLowerCase();
                    String productSprite = DataManager.Proc_8_12_806C30(productId, 17, 0).toLowerCase();
                    if (productSprite.isEmpty()) {
                        productSprite = DataManager.Proc_8_12_806C30(productId, 18, 0).toLowerCase();
                    }
                    if (productAction.isEmpty() || productAction.contains("switch") || productAction.contains("click")
                        || productAction.contains("score") || productSprite.contains("score") || productSprite.contains("dice")) {
                        long stateValue = NumberUtils.parseLong(row.sign());
                        Proc_6_151_78AC20(rowRoomId, furnitureId, stateValue);
                        Proc_6_246_8024C0(rowRoomId, FurniturePayloads.stateChanged(furnitureId, stateValue), 0);
                        refreshCount++;
                    }
                }
            }
            return refreshCount;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    public static void Proc_6_148_7756D0(Object... args) {
        try {
            if (args == null || args.length < 3) {
                return;
            }
            int socketIndex = handlingSocketIndex(args);
            long productId = NumberUtils.parseLong(args[1]);
            long furnitureId = NumberUtils.parseLong(args[2]);
            if (socketIndex <= 0 || productId <= 0L || furnitureId <= 0L) {
                return;
            }
            CatalogRegistry.Product product = Licence.product(productId);
            if (product == null) {
                return;
            }
            long hasCharge = product.chargeSize();
            if (hasCharge == 0L) {
                return;
            }
            long chargeSize = product.chargeSize();
            long chargePriceCredits = product.chargePriceCredits();
            long chargePricePoints = product.chargePriceActivityPoints();
            long chargePointType = product.chargePriceActivityPointsType();
            Path chargePath = Path.of(Functions.applicationPath, "cache", "items_charges", furnitureId + ".cache");
            long currentCharges = NumberUtils.parseLong(Proc_6_239_7FC170(chargePath.toString(), 0, 0));
            if (currentCharges < 1L) {
                String payload = FurniturePayloads.chargePrompt(
                    furnitureId,
                    currentCharges,
                    chargeSize,
                    chargePriceCredits,
                    chargePricePoints,
                    chargePointType);
                Proc_6_244_801E80(socketIndex, payload, 0);
            } else {
                DataManager.Proc_8_10_8068E0(chargePath.toString(), String.valueOf(currentCharges - 1L), 0);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_149_775C10(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return;
            }
            String requestPayload = handlingRequestPayload(args, "Ch");
            long furnitureId = stickyFurnitureIdFromPayload(requestPayload);
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
            FurnitureDao furniture = furnitureDao();
            if (furniture == null) {
                return;
            }
            FurnitureDao.FloorStateFurniture stateFurniture = furniture.floorStateFurniture(furnitureId, roomId).orElse(null);
            if (stateFurniture == null) {
                return;
            }
            long productId = stateFurniture.productId();
            String signText = StringUtils.text(stateFurniture.sign());
            if (productId <= 0L) {
                return;
            }
            long productType = NumberUtils.parseLong(DataManager.Proc_8_12_806C30(productId, 0, 0));
            if (productType == 9L) {
                return;
            }
            String productSprite = DataManager.Proc_8_12_806C30(productId, 17, 0).toLowerCase();
            if (productSprite.isEmpty()) {
                productSprite = DataManager.Proc_8_12_806C30(productId, 18, 0).toLowerCase();
            }
            long currentState = NumberUtils.parseLong(signText);
            long maxState = NumberUtils.parseLong(DataManager.Proc_8_12_806C30(productId, 12, 0));
            long nextState = nextFurnitureState(productSprite, currentState, maxState);
            furniture.updateRoomFurnitureState(furnitureId, roomId, NumberUtils.parseLong(userId), nextState);
            Proc_6_151_78AC20(roomId, furnitureId, nextState);
            String payload = FurniturePayloads.stateChanged(furnitureId, nextState);
            Proc_6_247_8027E0(socketIndex, payload, 0);
            if (NumberUtils.parseLong(DataManager.Proc_8_12_806C30(productId, 34, 0)) != 0L) {
                Proc_6_148_7756D0(socketIndex, productId, furnitureId);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static Object Proc_6_150_777FA0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return "";
            }
            String requestPayload = handlingRequestPayload(args, "FH");
            LongRef offset = new LongRef(1);
            long furnitureId = readWireLong(requestPayload, offset);
            if (furnitureId <= 0L) {
                furnitureId = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            }
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
            FurnitureDao furniture = furnitureDao();
            if (furniture == null) {
                return "";
            }
            FurnitureDao.FloorStateFurniture stateFurniture = furniture.floorStateFurniture(furnitureId, roomId).orElse(null);
            if (stateFurniture == null) {
                return "";
            }
            long productId = stateFurniture.productId();
            if (productId <= 0L) {
                return "";
            }
            PackageDao packages = packageDao();
            if (packages != null) {
                PackageDao.PackageRow packageRow = packages.packageByProduct(productId).orElse(null);
                String packageType = packageRow == null ? "" : StringUtils.text(packageRow.secondaryType()).toLowerCase();
                long containedId = packageRow == null ? 0L : packageRow.containedId();
                if ("packages_pets".equals(packageType) && containedId > 0L) {
                    return Proc_6_86_73B0D0(socketIndex, "FH", requestPayload);
                } else if (!packageType.isEmpty()) {
                    Proc_6_244_801E80(socketIndex, FurniturePayloads.packageOpened(productId, furnitureId, packageType), 0);
                    return furnitureId;
                }
            }
            Proc_6_149_775C10(socketIndex, "Ch", requestPayload);
            return furnitureId;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static void Proc_6_151_78AC20(Object... args) {
        try {
            long roomId = 0L;
            long furnitureId = 0L;
            long stateValue = 0L;
            if (args != null && args.length >= 3) {
                roomId = NumberUtils.parseLong(args[0]);
                furnitureId = NumberUtils.parseLong(args[1]);
                stateValue = NumberUtils.parseLong(args[2]);
            } else if (args != null && args.length >= 2) {
                roomId = NumberUtils.parseLong(args[0]);
                furnitureId = NumberUtils.parseLong(args[1]);
            } else if (args != null && args.length >= 1) {
                furnitureId = NumberUtils.parseLong(args[0]);
            }
            if (roomId <= 0L || furnitureId <= 0L) {
                return;
            }
            FurnitureRoomCache.State cacheState = Licence.furnitureRoomCache();
            FurnitureStateCache state = representedFurnitureStateCache(
                cacheState.pendingRoomCache,
                cacheState.pendingFurnitureCache,
                cacheState.representedRoomCache,
                roomId,
                furnitureId,
                stateValue);
            Licence.setFurnitureRoomCache(furnitureRoomCacheState(state));
            Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "ROOMS", roomId + ".cache").toString(), 0, 0);
            Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "PATHFINDER", roomId + ".cache").toString(), 0, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_152_78C2F0(Object... args) {
        handlingRepresentedFurnitureStateWrite(args);
    }

    public static void Proc_6_153_78D980(Object... args) {
        handlingRepresentedFurnitureStateWrite(args);
    }

    public static String Proc_6_154_78F040(Object... args) {
        try {
            if (args == null || args.length == 0) {
                return "";
            }
            long furnitureId = NumberUtils.parseLong(args[0]);
            long productId = args.length >= 2 ? NumberUtils.parseLong(args[1]) : 0L;
            if (furnitureId <= 0L) {
                return "";
            }
            FurnitureDao furniture = furnitureDao();
            if (furniture == null) {
                return "";
            }
            FurnitureDao.RoomFurnitureState furnitureState = furniture.roomFurnitureState(furnitureId).orElse(null);
            if (furnitureState == null) {
                return "";
            }
            long roomId = furnitureState.roomId();
            if (productId <= 0L) {
                productId = furnitureState.productId();
            }
            String signText = StringUtils.text(furnitureState.sign());
            if (roomId <= 0L || productId <= 0L) {
                return "";
            }
            long productType = NumberUtils.parseLong(DataManager.Proc_8_12_806C30(productId, 0, 0));
            String productSprite = DataManager.Proc_8_12_806C30(productId, 17, 0);
            if (productSprite.isEmpty()) {
                productSprite = DataManager.Proc_8_12_806C30(productId, 18, 0);
            }
            long stateValue = NumberUtils.parseLong(signText);
            String lowerSprite = productSprite.toLowerCase();
            if ((lowerSprite.startsWith("bb_score_") || lowerSprite.startsWith("es_score_")) && stateValue < 0L) {
                stateValue = 0L;
            }
            Proc_6_151_78AC20(roomId, furnitureId, stateValue);
            String payload = FurniturePayloads.stateChanged(furnitureId, stateValue);
            Proc_6_246_8024C0(roomId, payload, 0);
            if (productType == 11L || lowerSprite.contains("soundmachine") || lowerSprite.contains("jukebox")) {
                Proc_6_224_7EF5A0(0, roomId, furnitureId);
            }
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static void Proc_6_155_795C90(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "AC");
            long furnitureId = stickyFurnitureIdFromPayload(requestPayload);
            if (socketIndex <= 0 || furnitureId <= 0L) {
                return;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (userId.isEmpty() || "0".equals(userId) || roomId <= 0L) {
                return;
            }
            FurnitureDao furniture = furnitureDao();
            if (furniture == null) {
                return;
            }
            FurnitureDao.RoomFurnitureOwnerProduct furnitureProduct = furniture
                .roomFurnitureOwnerProduct(furnitureId, roomId)
                .orElse(null);
            if (furnitureProduct == null) {
                return;
            }
            long productId = furnitureProduct.productId();
            String ownerId = String.valueOf(furnitureProduct.ownerId());
            if (productId <= 0L) {
                return;
            }
            boolean canPickUpAny = handlingUserHasPermission(userId, "fuse_pick_up_any_furni");
            if (!ownerId.equals(userId) && !handlingUserOwnsRoom(userId, roomId) && !canPickUpAny) {
                return;
            }
            if (!handlingUserHasRoomRight(userId, roomId) && !ownerId.equals(userId) && !canPickUpAny) {
                return;
            }
            if (!ownerId.equals(userId)) {
                String sessionId = handlingUserSessionId(userId);
                StaffModerationDao moderationDao = staffModerationDao();
                if (moderationDao == null) {
                    return;
                }
                moderationDao.insertFurniturePickupLog(NumberUtils.parseLong(userId), roomId, furnitureId, sessionId);
            }
            Proc_6_146_76D300(socketIndex, furnitureId, productId);
            furniture.moveRoomFurnitureToInventory(furnitureId, roomId, NumberUtils.parseLong(userId));
            Proc_6_244_801E80(socketIndex, InventoryMessagePayloads.remove(furnitureId), 0);
            Proc_6_247_8027E0(socketIndex, "A^" + furnitureId + '\2', 0);
            Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "ROOMS", roomId + ".cache").toString(), 0, 0);
            Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "PATHFINDER", roomId + ".cache").toString(), 0, 0);
            Proc_6_140_769400(socketIndex, "FT", "");
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_157_7974B0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return;
            }
            String wallPayload = args != null && args.length >= 2 ? StringUtils.text(args[1]) : "";
            if (wallPayload.startsWith("rv")) {
                wallPayload = wallPayload.substring(2);
            }
            FurnitureDao.InventoryPlacementFurniture placementFurniture = null;
            if (args != null && args.length >= 3) {
                placementFurniture = FurnitureDao.InventoryPlacementFurniture.fromLegacyArg(args[2]);
            }
            long furnitureId = placementFurniture == null ? 0L : placementFurniture.furnitureId();
            long productId = placementFurniture == null ? 0L : placementFurniture.productId();
            if (furnitureId <= 0L) {
                furnitureId = NumberUtils.parseLong(Functions.Proc_10_6_809F10(wallPayload, 0, 0));
            }
            if (furnitureId <= 0L) {
                return;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L || (!handlingUserHasRoomRight(userId, roomId)
                && !handlingUserHasPermission(userId, "fuse_pick_up_any_furni"))) {
                return;
            }
            if (productId <= 0L) {
                FurnitureDao furniture = furnitureDao();
                if (furniture == null) {
                    return;
                }
                placementFurniture = furniture
                    .inventoryPlacementFurniture(furnitureId, NumberUtils.parseLong(userId))
                    .orElse(null);
                productId = placementFurniture == null ? 0L : placementFurniture.productId();
            }
            if (productId <= 0L || NumberUtils.parseLong(DataManager.Proc_8_12_806C30(productId, 0, 0)) != 9L) {
                return;
            }
            WallPlacement placement = new WallPlacement();
            if (!wallPlacementFromPayload(wallPayload, placement)) {
                return;
            }
            String wallPosition = Functions.Proc_10_11_80A9C0((":w=" + placement.wallX + "," + placement.wallY
                + " l=" + placement.localX + "," + placement.localY).toLowerCase(), 0, 0);
            FurnitureDao furniture = furnitureDao();
            if (furniture == null) {
                return;
            }
            furniture.placeWallFurniture(furnitureId, NumberUtils.parseLong(userId), roomId, wallPosition);
            Proc_6_244_801E80(socketIndex, InventoryMessagePayloads.remove(furnitureId), 0);
            String payload = Proc_6_156_7972B0(furnitureId, productId, wallPosition,
                placementFurniture == null ? "" : placementFurniture.sign(),
                placementFurniture == null ? 0L : placementFurniture.secondaryValue());
            if (!payload.isEmpty()) {
                Proc_6_247_8027E0(socketIndex, "AS" + payload, 0);
            }
            Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "ROOMS", roomId + ".cache").toString(), 0, 0);
            Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "PATHFINDER", roomId + ".cache").toString(), 0, 0);
            Proc_6_140_769400(socketIndex, "FT", "");
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static long Proc_6_158_7987C0(Object... args) {
        try {
            if (args == null || args.length < 3) {
                return 0L;
            }
            long furnitureId = NumberUtils.parseLong(args[0]);
            long positionX = NumberUtils.parseLong(args[1]);
            long positionY = NumberUtils.parseLong(args[2]);
            long footprintX = args.length >= 4 ? NumberUtils.parseLong(args[3]) : 1L;
            long footprintY = args.length >= 5 ? NumberUtils.parseLong(args[4]) : 1L;
            if (footprintX <= 0L) {
                footprintX = 1L;
            }
            if (footprintY <= 0L) {
                footprintY = 1L;
            }
            FurnitureDao furniture = furnitureDao();
            RoomDao rooms = roomDao();
            if (furniture == null || rooms == null) {
                return 0L;
            }
            long roomId = furniture.roomIdByFurniture(furnitureId);
            if (roomId <= 0L) {
                roomId = furnitureId;
                furnitureId = 0L;
            }
            if (roomId <= 0L || positionX < 0L || positionY < 0L) {
                return 0L;
            }
            RoomDao.RoomPlacementState placementState = rooms.roomPlacementState(roomId).orElse(null);
            if (placementState == null) {
                return 0L;
            }
            String modelMap = StringUtils.text(placementState.modelMap()).replace('\n', '\r');
            while (modelMap.contains("\r\r")) {
                modelMap = modelMap.replace("\r\r", "\r");
            }
            if (modelMap.endsWith("\r")) {
                modelMap = modelMap.substring(0, modelMap.length() - 1);
            }
            String[] mapRows = modelMap.split("\r", -1);
            long allowWalkthrough = placementState.allowWalkthrough();
            long roomSlot = placementState.roomSlot();
            for (long tileY = positionY; tileY <= positionY + footprintY - 1L; tileY++) {
                if (tileY < 0L || tileY >= mapRows.length) {
                    return 0L;
                }
                String mapRow = mapRows[(int) tileY];
                for (long tileX = positionX; tileX <= positionX + footprintX - 1L; tileX++) {
                    if (tileX < 0L || tileX + 1L > mapRow.length()) {
                        return 0L;
                    }
                    String mapCell = mapRow.substring((int) tileX, (int) tileX + 1).toLowerCase();
                    if (mapCell.isEmpty() || "x".equals(mapCell)) {
                        return 0L;
                    }
                    long occupiedCount = furniture.floorFurnitureCountAtExcluding(roomId, furnitureId, tileX, tileY);
                    if (occupiedCount > 0L) {
                        return 0L;
                    }
                    occupiedCount = rooms.botCountAtLimited(roomId, tileX, tileY);
                    if (occupiedCount > 0L) {
                        return 0L;
                    }
                    if (allowWalkthrough == 0L && roomSlot > 0L) {
                        for (long occupantRoomUserIndex : rooms.activeVisitIdsByRoom(roomId)) {
                            if (occupantRoomUserIndex > 0L) {
                                MovementPosition movementPosition = movementPosition(
                                    Licence.representedRooms().movementPosition(roomSlot, occupantRoomUserIndex));
                                if (movementPosition.found && movementPosition.positionX == tileX
                                    && movementPosition.positionY == tileY) {
                                    return 0L;
                                }
                            }
                        }
                    }
                }
            }
            return 1L;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    public static String Proc_6_159_79FCD0(Object... args) {
        try {
            String requestPayload = handlingRequestPayload(args, "AI");
            if (requestPayload.isEmpty()) {
                return "";
            }
            return Proc_6_141_76A670(handlingSocketIndex(args), "A[", "A[" + requestPayload);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_160_7A71A0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long firstId = args != null && args.length >= 2 ? NumberUtils.parseLong(args[1]) : 0L;
            long secondId = args != null && args.length >= 3 ? NumberUtils.parseLong(args[2]) : 0L;
            long furnitureId = 0L;
            long productId = 0L;
            FurnitureDao furniture = furnitureDao();
            if (furniture == null) {
                return "";
            }
            FurnitureDao.LocatedFurnitureState furnitureState = null;
            if (secondId > 0L) {
                furnitureState = furniture.locatedFurnitureState(secondId).orElse(null);
                if (furnitureState != null) {
                    furnitureId = secondId;
                    productId = firstId;
                }
            }
            if (furnitureState == null && firstId > 0L) {
                furnitureState = furniture.locatedFurnitureState(firstId).orElse(null);
                if (furnitureState != null) {
                    furnitureId = firstId;
                }
            }
            if (furnitureState == null && secondId > 0L) {
                furnitureState = furniture.newestLocatedFurnitureStateByProduct(secondId).orElse(null);
                if (furnitureState != null) {
                    productId = secondId;
                }
            }
            if (furnitureState == null) {
                return "";
            }
            long roomId = furnitureState.roomId();
            if (productId <= 0L) {
                productId = furnitureState.productId();
            }
            String signText = StringUtils.text(furnitureState.sign());
            if (roomId <= 0L || productId <= 0L) {
                return "";
            }
            if (furnitureId <= 0L) {
                furnitureId = furniture.newestFurnitureIdByRoomAndProduct(roomId, productId);
            }
            if (furnitureId <= 0L) {
                return "";
            }
            String productSprite = DataManager.Proc_8_12_806C30(productId, 17, 0).toLowerCase();
            if (productSprite.isEmpty()) {
                productSprite = DataManager.Proc_8_12_806C30(productId, 18, 0).toLowerCase();
            }
            if (productSprite.startsWith("bb_score_") || productSprite.startsWith("es_score_")) {
                long stateValue = NumberUtils.parseLong(signText);
                long maxState = NumberUtils.parseLong(DataManager.Proc_8_12_806C30(productId, 12, 0));
                if (maxState <= 0L) {
                    maxState = 99L;
                }
                if (stateValue < 0L) {
                    stateValue = 0L;
                }
                if (stateValue > maxState) {
                    stateValue = maxState;
                }
                if (!String.valueOf(stateValue).equals(signText)) {
                    furniture.updateSignLimited(furnitureId, stateValue);
                }
            }
            return Proc_6_154_78F040(furnitureId, productId);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static void Proc_6_162_7B3310(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String dateFormat = Functions.Proc_10_0_809570("com.system.format.date", "DAQBHHIIKHJHPAHQA", 0);
            if (dateFormat.isEmpty()) {
                dateFormat = "DAQBHHIIKHJHPAHQA";
            }
            Proc_6_244_801E80(socketIndex,
                "0" + dateFormat + '\2' + "SAHPB" + "http://www.alpha-series.com/" + '\2' + "QBH", 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static String Proc_6_163_7B3480(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String packetPayload = args != null && args.length >= 2 ? StringUtils.text(args[1]) : "";
            String loginTicket = handlingLoginTicketFromPayload(packetPayload);
            if (loginTicket.isEmpty() || "NULL".equalsIgnoreCase(loginTicket)) {
                if (socketIndex > 0) {
                    Proc_6_243_7FFEB0(socketIndex, 0, 0);
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
                    Proc_6_243_7FFEB0(socketIndex, 0, 0);
                }
                return "";
            }
            long userIdValue = loginUser.userId();
            String userId = String.valueOf(userIdValue);
            if (userIdValue == 0L) {
                if (socketIndex > 0) {
                    Proc_6_243_7FFEB0(socketIndex, 0, 0);
                }
                return "";
            }
            int oldSocketIndex = (int) loginUser.oldSocketIndex();
            if (oldSocketIndex > 0 && oldSocketIndex != socketIndex) {
                Proc_6_243_7FFEB0(oldSocketIndex, 0, 0);
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
            handlingStoreSocketSession(socketIndex, userId + '\2' + socketIndex + '\2' + userName + '\2'
                + rankIndex + '\2' + loginTicket + '\2');
            Proc_6_244_801E80(socketIndex, "@C", 0);
            Proc_6_20_6E88E0(socketIndex, 0, 0);
            Proc_6_244_801E80(socketIndex, "@F" + creditsValue + ".0" + '\2', 0);
            for (int pointIndex = 0; pointIndex <= 4; pointIndex++) {
                Proc_6_244_801E80(socketIndex, UserPayloads.activityPointRefresh(pointIndex, pointValues[pointIndex]), 0);
            }
            if (homeRoomId > 0L) {
                Proc_6_244_801E80(socketIndex, RoomPayloads.homeRoom(homeRoomId), 0);
            }
            if (emailValidated > 0L) {
                Proc_6_244_801E80(socketIndex, UserPayloads.emailStatus(emailValidated), 0);
            }
            Proc_6_244_801E80(socketIndex, "@a" + "com.server.socket.location" + '\2' + "invalid.location" + '\2', 0);
            if (NumberUtils.parseLong(Functions.Proc_10_0_809570("com.client.motd.message.enabled", 0, 0)) != 0L) {
                String motdMessage = Functions.Proc_10_0_809570("com.client.motd.message", "", 0).replace("\\n", "\n");
                if (!motdMessage.isEmpty()) {
                    Proc_6_244_801E80(socketIndex, Console.Proc_2_4_6D28B0(motdMessage.length(), 0, 0)
                        + " " + motdMessage + '\2', 0);
                }
            }
            Proc_6_244_801E80(socketIndex, SocialPayloads.badgeDisplay(userIdValue, Proc_6_195_7D38D0(userId, 0, 0)), 0);
            Proc_6_244_801E80(socketIndex, SocialPayloads.tagDisplay(userIdValue, Proc_6_196_7D3ED0(userId, 0, 0)), 0);
            long favouriteGroupId = loginUser.favouriteGroupId();
            if (favouriteGroupId > 0L) {
                UserGroupRow groupRow = users.userGroup(favouriteGroupId).orElse(null);
                if (groupRow != null) {
                    String groupPayload = loginGroupPayload(favouriteGroupId, groupRow);
                    Proc_6_244_801E80(socketIndex, groupPayload, 0);
                }
            }
            return userId;
        } catch (Exception ignored) {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex > 0) {
                Proc_6_243_7FFEB0(socketIndex, 0, 0);
            }
            return "";
        }
    }

    public static String Proc_6_165_7BE0B0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            int targetSocketIndex = args != null && args.length >= 2 ? (int) NumberUtils.parseLong(args[1]) : 0;
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            String summaryPayload = messengerFriendSummaryPayload(userId, 1L);
            if (summaryPayload.isEmpty()) {
                return "";
            }
            String notifyPayload = MessengerPayloads.friendOnlineNotification(summaryPayload);
            if (targetSocketIndex > 0) {
                if (Guardian.Proc_11_2_821390(targetSocketIndex, 0, 0) == 1L) {
                    Proc_6_244_801E80(targetSocketIndex, notifyPayload, 0);
                }
            } else {
                MessengerDao messenger = messengerDao();
                if (messenger == null) {
                    return "";
                }
                for (long friendSocketIndex : messenger.acceptedFriendSocketIndexes(NumberUtils.parseLong(userId))) {
                    targetSocketIndex = (int) friendSocketIndex;
                    if (targetSocketIndex > 0 && Guardian.Proc_11_2_821390(targetSocketIndex, 0, 0) == 1L) {
                        Proc_6_244_801E80(targetSocketIndex, notifyPayload, 0);
                    }
                }
            }
            return notifyPayload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static long Proc_6_170_7C1100(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            FriendTargetList targets = friendDeleteTargetsFromPayload(handlingPacketPayload(args));
            if (targets.deleteAllPending) {
                MessengerDao messenger = messengerDao();
                if (messenger == null) {
                    return 0L;
                }
                messenger.deletePendingRequests(NumberUtils.parseLong(userId));
                return 1L;
            }
            if (targets.targetList.isEmpty()) {
                return 0L;
            }
            MessengerDao messenger = messengerDao();
            if (messenger == null) {
                return 0L;
            }
            messenger.deletePendingRequests(NumberUtils.parseLong(userId), targets.targetList);
            return 1L;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    public static String Proc_6_171_7C1520(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "@h");
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            LongRef offset = new LongRef(1);
            long removeCount = readWireLong(requestPayload, offset);
            if (removeCount <= 0L) {
                return "";
            }
            MessengerDao messenger = messengerDao();
            if (messenger == null) {
                return "";
            }
            removeCount = Math.min(removeCount, 75L);
            StringBuilder targetList = new StringBuilder();
            StringBuilder removedIdsPayload = new StringBuilder();
            long removedCount = 0L;
            for (long removeIndex = 1L; removeIndex <= removeCount; removeIndex++) {
                long targetUserId = readWireLong(requestPayload, offset);
                String targetId = String.valueOf(targetUserId);
                if (targetUserId > 0L && !targetId.equals(userId)
                    && !("," + targetList + ",").contains("," + targetId + ",")) {
                    if (messenger.acceptedFriendshipExists(NumberUtils.parseLong(userId), targetUserId)) {
                        if (targetList.length() > 0) {
                            targetList.append(',');
                        }
                        targetList.append(targetId);
                        removedIdsPayload.append(MessengerPayloads.removedId(targetUserId));
                        removedCount++;
                        int targetSocketIndex = handlingSocketFromUserId(targetId);
                        if (targetSocketIndex > 0) {
                            Proc_6_244_801E80(targetSocketIndex,
                                MessengerPayloads.friendRemovedNotification(NumberUtils.parseLong(userId)), 0);
                        }
                    }
                }
            }
            if (targetList.length() == 0) {
                return "";
            }
            messenger.deleteAcceptedFriendships(NumberUtils.parseLong(userId), targetList.toString());
            String callerPayload = MessengerPayloads.removeFriends(removedIdsPayload.toString(), removedCount);
            Proc_6_244_801E80(socketIndex, callerPayload, 0);
            return callerPayload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_172_7C25B0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "@i");
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            String searchText = Functions.Proc_10_7_80A190(requestPayload, 0, 0);
            if (searchText.isEmpty()) {
                LongRef offset = new LongRef(1);
                searchText = readWireString(requestPayload, offset);
            }
            searchText = searchText.trim().toLowerCase();
            if (searchText.isEmpty()) {
                return "";
            }
            String dateFormat = Functions.Proc_10_0_809570("com.mysql.format.date", "%d-%m-%Y", 0);
            String timeFormat = Functions.Proc_10_0_809570("com.mysql.format.time", "%H:%i", 0);
            MessengerDao messenger = messengerDao();
            if (messenger == null) {
                return "";
            }
            long callerUserId = NumberUtils.parseLong(userId);
            List<MessengerSearchResult> results = new ArrayList<>();
            for (MessengerDao.SearchUser searchUser : messenger.searchUsers(searchText, dateFormat + " " + timeFormat)) {
                if (searchUser.userId() != callerUserId) {
                    results.add(new MessengerSearchResult(
                        searchUser.userId(),
                        searchUser.userName(),
                        searchUser.figure(),
                        searchUser.motto(),
                        searchUser.nickname(),
                        searchUser.lastOnline(),
                        searchUser.socketIndex() > 0L,
                        messenger.acceptedFriendshipExists(callerUserId, searchUser.userId())));
                }
            }
            String resultPayload = MessengerPayloads.searchResults(results);
            Proc_6_244_801E80(socketIndex, resultPayload, 0);
            return resultPayload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_173_7C3430(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "@a");
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            LongRef offset = new LongRef(1);
            String targetUserId = String.valueOf(readWireLong(requestPayload, offset));
            if (targetUserId.isEmpty() || "0".equals(targetUserId)) {
                targetUserId = String.valueOf((long) NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0)));
            }
            if (targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return "";
            }
            String messageText = Functions.Proc_10_7_80A190(requestPayload, 0, 0);
            if (messageText.length() > 122) {
                messageText = messageText.substring(0, 122);
            }
            if (messageText.isEmpty()) {
                messageText = readWireString(requestPayload, offset);
                if (messageText.length() > 122) {
                    messageText = messageText.substring(0, 122);
                }
            }
            if (messageText.isEmpty() || messageText.length() > 255) {
                return "";
            }
            int targetSocketIndex = handlingSocketFromUserId(targetUserId);
            if (targetSocketIndex <= 0) {
                return "";
            }
            long currentRoomId = handlingCurrentRoomId(socketIndex, userId);
            MessengerDao messenger = messengerDao();
            if (messenger == null) {
                return "";
            }
            messenger.insertPrivateChatLog(
                NumberUtils.parseLong(userId),
                currentRoomId,
                "(Chat To:     " + handlingUserName(targetUserId) + ") -- " + messageText,
                socketIndex);
            String filteredText = Proc_6_22_6E9300(messageText, 0, 0);
            String payload = MessengerPayloads.privateChatMessage(NumberUtils.parseLong(userId), filteredText);
            Proc_6_244_801E80(targetSocketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_174_7C3BC0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "@g");
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            String targetName = Functions.Proc_10_7_80A190(requestPayload, 0, 0);
            if (targetName.isEmpty()) {
                LongRef offset = new LongRef(1);
                targetName = readWireString(requestPayload, offset);
            }
            targetName = targetName.trim();
            if (targetName.isEmpty()) {
                return "";
            }
            MessengerDao messenger = messengerDao();
            if (messenger == null) {
                return "";
            }
            String targetUserId = String.valueOf(messenger.userIdByName(targetName));
            if (targetUserId.isEmpty() || "0".equals(targetUserId) || targetUserId.equals(userId)) {
                String callerPayload = MessengerPayloads.requestDenied();
                Proc_6_244_801E80(socketIndex, callerPayload, 0);
                return callerPayload;
            }
            long callerUserId = NumberUtils.parseLong(userId);
            long targetUserIdValue = NumberUtils.parseLong(targetUserId);
            if (messenger.friendshipExists(callerUserId, targetUserIdValue) || messenger.acceptFriends(targetUserIdValue) != 1L) {
                String callerPayload = MessengerPayloads.requestDenied();
                Proc_6_244_801E80(socketIndex, callerPayload, 0);
                return callerPayload;
            }
            messenger.insertFriendRequest(targetUserIdValue, callerUserId);
            String userName = handlingUserName(userId);
            int targetSocketIndex = handlingSocketFromUserId(targetUserId);
            if (targetSocketIndex > 0) {
                Proc_6_244_801E80(targetSocketIndex, MessengerPayloads.requestNotify(callerUserId, userName), 0);
            }
            String callerPayload = MessengerPayloads.requestAcceptedCaller(targetUserIdValue);
            Proc_6_244_801E80(socketIndex, callerPayload, 0);
            return callerPayload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_175_7C4800(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
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
            Proc_6_244_801E80(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_176_7C4EE0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long maxFriends0 = messengerMaxFriends(0L);
            long maxFriends1 = messengerMaxFriends(2L);
            long maxFriends2 = messengerMaxFriends(4L);
            long queryLimit = maxFriends2 > 0L ? maxFriends2 : 200L;
            String dateFormat = Functions.Proc_10_0_809570("com.mysql.format.date", "%d-%m-%Y", 0);
            String timeFormat = Functions.Proc_10_0_809570("com.mysql.format.time", "%H:%i", 0);
            MessengerDao messenger = messengerDao();
            if (messenger == null) {
                return "";
            }
            List<MessengerFriend> friends = messenger.acceptedFriends(
                NumberUtils.parseLong(userId),
                dateFormat + " " + timeFormat,
                queryLimit);
            String callerSummary = messengerFriendSummaryPayload(userId, 1L);
            List<Long> onlineFriendIds = new ArrayList<>();
            for (MessengerFriend friend : friends) {
                if (friend != null) {
                    int friendSocketIndex = (int) friend.socketIndex();
                    long friendOnline = friendSocketIndex > 0
                        && Guardian.Proc_11_2_821390(friendSocketIndex, 0, 0) == 1L ? 1L : 0L;
                    if (friendOnline == 1L) {
                        onlineFriendIds.add(friend.userId());
                    }
                    if (friendOnline == 1L && !callerSummary.isEmpty()) {
                        Proc_6_244_801E80(friendSocketIndex,
                            MessengerPayloads.friendOnlineNotification(callerSummary), 0);
                    }
                }
            }
            String payload = MessengerPayloads.friendList(friends, maxFriends0, maxFriends1, maxFriends2,
                onlineFriendIds, messengerFollowEnabled());
            Proc_6_244_801E80(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_177_7C6580(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "n" + '\177');
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            String productPet = Functions.Proc_10_7_80A190(requestPayload, 0, 0);
            if (productPet.isEmpty()) {
                productPet = readWireString(requestPayload, new LongRef(1));
            }
            if (productPet.isEmpty()) {
                return "";
            }
            long rankIndex = handlingUserRank(userId);
            long hcLevel = handlingUserHcLevel(userId);
            BotDao bots = botDao();
            if (bots == null) {
                return "";
            }
            String payload = PetPayloads.raceList(productPet, bots.petRaces(productPet), rankIndex, hcLevel);
            Proc_6_244_801E80(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_178_7C6E60(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            BotDao bots = botDao();
            if (bots == null) {
                return "";
            }
            String payload = PetPayloads.inventoryList(bots.inventoryPets(NumberUtils.parseLong(userId)));
            Proc_6_244_801E80(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static long Proc_6_179_7C7790(Object... args) {
        try {
            if (NumberUtils.parseLong(Functions.Proc_10_0_809570("com.client.rooms.bots.pets.enabled", "0", 0)) == 0L) {
                return 0L;
            }
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "nz");
            LongRef offset = new LongRef(1);
            long petId = readWireLong(requestPayload, offset);
            long positionX = readWireLong(requestPayload, offset);
            long positionY = readWireLong(requestPayload, offset);
            long positionR = readWireLong(requestPayload, offset);
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
            long roomSlot = rooms.roomSlot(roomId);
            if (roomSlot <= 0L) {
                return 0L;
            }
            String positionZ = String.valueOf(NumberUtils.parseLong(rooms.modelHeightmap(roomId)));
            PetPlacementRow pet = bots.availablePetForPlacement(petId, NumberUtils.parseLong(userId)).orElse(null);
            if (pet == null) {
                return 0L;
            }
            long botEntityId = allocateRepresentedBot(
                roomSlot,
                RepresentedBotEntry.from(pet, positionX, positionY, positionZ, positionR));
            if (botEntityId <= 0L) {
                return 0L;
            }
            storeRepresentedBotPosition(botEntityId, positionX, positionY, positionZ, positionR);
            bots.placeBotInRoom(petId, roomId, positionX, positionY, positionZ, positionR);
            String placementPayload = representedBotRoomEntryPayload(botEntityId);
            if (!placementPayload.isEmpty()) {
                Proc_6_247_8027E0(socketIndex, placementPayload, 0);
            }
            Proc_6_244_801E80(socketIndex, PetPayloads.placed(petId), 0);
            return botEntityId;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    public static long Proc_6_180_7C96F0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long botEntityId = args != null && args.length >= 2 ? NumberUtils.parseLong(args[1]) : 0L;
            if (botEntityId <= 0L) {
                return 0L;
            }
            long botId = representedBotRecordLong(botEntityId, 1);
            if (botId <= 0L) {
                return 0L;
            }
            BotDao bots = botDao();
            if (bots == null) {
                return 0L;
            }
            bots.clearBotRoom(botId);
            bots.touchPetData(botId);
            Proc_6_247_8027E0(socketIndex, PetPayloads.removedFromRoom(botEntityId), 0);
            String petName = representedBotRecordField(botEntityId, 2);
            String petFigure = representedBotRecordField(botEntityId, 10).toLowerCase();
            long scratches = bots.petScratches(botId);
            String pickupPayload = PetPayloads.inventoryRow(new PetInventoryRow(botId, petName, petFigure, scratches));
            if (!pickupPayload.isEmpty()) {
                Proc_6_244_801E80(socketIndex, PetPayloads.inventoryAdd(pickupPayload), 0);
            }
            removeRepresentedBotRecord(botEntityId);
            return botId;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    public static long Proc_6_181_7CA920(Object... args) {
        try {
            if (args == null || args.length == 0) {
                return 2L;
            }
            return PetPayloads.nameValidationCode(StringUtils.text(args[0]));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 2L;
        }
    }

    public static String Proc_6_182_7CAAD0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "@c");
            String requestedName = Functions.Proc_10_7_80A190(requestPayload, 0, 0);
            if (requestedName.isEmpty()) {
                requestedName = readWireString(requestPayload, new LongRef(1));
            }
            requestedName = Functions.Proc_10_11_80A9C0(Functions.Proc_10_10_80A7F0(requestedName), 0, 0);
            String payload = PetPayloads.nameValidation(requestedName);
            Proc_6_244_801E80(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_183_7CABF0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "ny");
            LongRef offset = new LongRef(1);
            long requestedId = readWireLong(requestPayload, offset);
            if (socketIndex <= 0 || requestedId <= 0L) {
                return "";
            }
            long botEntityId = requestedId;
            long botId = representedBotRecordLong(botEntityId, 1);
            if (botId <= 0L) {
                botId = requestedId;
                botEntityId = representedBotEntityFromBotId(botId);
            }
            if (botId <= 0L) {
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
            PetStatusRow petStatus = bots.petStatus(botId).orElse(null);
            if (petStatus == null) {
                return "";
            }
            if (botEntityId <= 0L) {
                botEntityId = botId;
            }
            String payload = PetPayloads.status(botEntityId, petStatus);
            if (!payload.isEmpty()) {
                Proc_6_244_801E80(socketIndex, payload, 0);
            }
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_184_7CBDA0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long petLevel = 0L;
            if (args != null && args.length >= 2) {
                petLevel = NumberUtils.parseLong(args[1]);
            } else if (args != null && args.length >= 1) {
                petLevel = NumberUtils.parseLong(args[0]);
            }
            String payload = Licence.petSettings().commandListPayload(petLevel);
            if (socketIndex > 0) {
                Proc_6_244_801E80(socketIndex, payload, 0);
            }
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_7CC190(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "n|");
            long requestedId = readWireLong(requestPayload, new LongRef(1));
            long botId = representedBotRecordLong(requestedId, 1);
            if (botId <= 0L) {
                botId = requestedId;
            }
            long petLevel = 0L;
            String userId = handlingUserIdFromSocket(socketIndex);
            if (!userId.isEmpty() && !"0".equals(userId) && botId > 0L) {
                BotDao bots = botDao();
                if (bots != null) {
                    petLevel = bots.petLevelForOwner(botId, NumberUtils.parseLong(userId));
                }
            }
            return Proc_6_184_7CBDA0(socketIndex, petLevel, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static long Proc_7CA730(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return 0L;
            }
            String requestPayload = handlingRequestPayload(args, "n{");
            LongRef offset = new LongRef(1);
            long requestedId = readWireLong(requestPayload, offset);
            long commandId = readWireLong(requestPayload, offset);
            if (requestedId <= 0L || commandId <= 0L) {
                return 0L;
            }
            long botEntityId = requestedId;
            long botId = representedBotRecordLong(botEntityId, 1);
            if (botId <= 0L) {
                botId = requestedId;
                botEntityId = representedBotEntityFromBotId(botId);
            }
            if (botId <= 0L) {
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
            PetCommandTargetRow pet = bots.petCommandTarget(botId, roomId).orElse(null);
            if (pet == null) {
                return 0L;
            }
            PetCommandAction commandAction = petCommandAction(commandId, Licence.petSettings().commandRows());
            if (!commandAction.found || commandAction.requiredLevel > pet.level()) {
                return 0L;
            }
            if (!commandAction.action.isEmpty()) {
                String payload = petCommandActionPayload(botEntityId, commandAction.action, commandId);
                Proc_6_248_802B80(roomId, payload, 0);
            }
            if (pet.energy() < 250L || pet.nutrition() < 250L) {
                String commandSpeech = Functions.Proc_10_4_809CA0(0, 2, -1) == 0L
                    ? Functions.Proc_10_0_809570("com.client.bot.pet.sad.speech", "gst thr", 0)
                    : Functions.Proc_10_0_809570("com.client.bot.pet.angry.speech", "gst grr", 0);
                if (!commandSpeech.isEmpty()) {
                    Proc_6_248_802B80(roomId, petSpeechPayload(botEntityId, commandSpeech), 0);
                }
            } else {
                Proc_6_185_7CC2D0(botEntityId, commandId * 10L, 0);
            }
            return commandId;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    public static long Proc_6_185_7CC2D0(Object... args) {
        try {
            if (args == null || args.length == 0) {
                return 0L;
            }
            long botEntityId = NumberUtils.parseLong(args[0]);
            long experienceDelta = args.length >= 2 ? NumberUtils.parseLong(args[1]) : 0L;
            if (botEntityId <= 0L) {
                return 0L;
            }
            long botId = representedBotRecordLong(botEntityId, 1);
            if (botId <= 0L) {
                botId = botEntityId;
                botEntityId = representedBotEntityFromBotId(botId);
            }
            if (botId <= 0L) {
                return 0L;
            }
            BotDao bots = botDao();
            if (bots == null) {
                return 0L;
            }
            PetExperienceStateRow petState = bots.petExperienceState(botId).orElse(null);
            if (petState == null) {
                return 0L;
            }
            if (botEntityId <= 0L) {
                botEntityId = botId;
            }
            List<PetLevelExperienceRow> levelRows = bots.petLevelExperienceRows();
            PetExperienceUpdate update = petExperienceUpdate(
                botEntityId,
                petState.name(),
                petState.figure(),
                petState.level(),
                petState.experience(),
                petState.energy(),
                petState.nutrition(),
                petState.scratches(),
                experienceDelta,
                levelRows);
            if (update.leveledUp && petState.roomId() > 0L) {
                String levelSpeech = Functions.Proc_10_0_809570("com.client.bot.pet.level_up.speech", "gst sml", 0);
                if (!levelSpeech.isEmpty()) {
                    Proc_6_248_802B80(petState.roomId(), petSpeechPayload(botEntityId, levelSpeech), 0);
                }
            }
            bots.updatePetExperience(botId, update.petLevel, update.petExperience);
            if (petState.roomId() > 0L) {
                Proc_6_248_802B80(petState.roomId(), update.statusPayload, 0);
                Proc_6_248_802B80(petState.roomId(), update.experiencePayload, 0);
            }
            return update.petLevel;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    public static long Proc_6_186_7CD040(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "n}");
            LongRef offset = new LongRef(1);
            long requestedId = readWireLong(requestPayload, offset);
            if (socketIndex <= 0 || requestedId <= 0L) {
                return 0L;
            }
            long botEntityId = requestedId;
            long botId = representedBotRecordLong(botEntityId, 1);
            if (botId <= 0L) {
                botId = requestedId;
                botEntityId = representedBotEntityFromBotId(botId);
            }
            if (botId <= 0L) {
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
            long scratchAmount = users.scratchAmount(userIdValue);
            if (scratchAmount <= 0L) {
                return 0L;
            }
            PetScratchRow pet = bots.scratchTarget(botId).orElse(null);
            if (pet == null) {
                return 0L;
            }
            long scratches = pet.scratches() + 1L;
            bots.updatePetScratches(botId, scratches);
            users.spendScratch(userIdValue);
            if (botEntityId <= 0L) {
                botEntityId = botId;
            }
            Proc_6_247_8027E0(socketIndex, petScratchPayload(botEntityId, userIdValue, scratches, pet.name(), pet.figure()), 0);
            return scratches;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    public static long Proc_6_187_7CD700(Object... args) {
        try {
            if (args == null || args.length < 2) {
                return 0L;
            }
            long roomSlot = NumberUtils.parseLong(args[0]);
            RepresentedBotEntry botEntry = args[1] instanceof RepresentedBotEntry entry
                ? entry
                : RepresentedBotEntry.fromLegacy(args[1]);
            return allocateRepresentedBot(roomSlot, botEntry);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    public static long Proc_6_188_7CF3C0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
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
            if (NumberUtils.parseLong(Functions.Proc_10_0_809570("com.client.rooms.bots.guide.enabled", "0", 0)) == 0L) {
                return 0L;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return 0L;
            }
            long roomSlot = rooms.roomSlot(roomId);
            if (roomSlot <= 0L) {
                return 0L;
            }
            long guideBotId = NumberUtils.parseLong(Functions.Proc_10_0_809570("com.client.bot.guide.id", "0", 0));
            if (guideBotId <= 0L || isRepresentedBotAllocated(roomSlot, guideBotId)) {
                return 0L;
            }
            BotRoomEntryRow guide = bots.botRoomEntry(guideBotId).orElse(null);
            if (guide == null) {
                return 0L;
            }
            long botEntityId = allocateRepresentedBot(roomSlot, RepresentedBotEntry.from(guide));
            if (botEntityId > 0L) {
                Proc_6_244_801E80(socketIndex, "@a" + "YjO", 0);
            }
            return botEntityId;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    public static long Proc_6_189_7D0630(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "Fy");
            LongRef offset = new LongRef(1);
            long requestedEntityId = readWireLong(requestPayload, offset);
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
            long roomSlot = rooms.roomSlot(roomId);
            if (roomSlot <= 0L) {
                return 0L;
            }
            String entityList = "";
            if (requestedEntityId > 0L) {
                if (representedBotRecordLong(requestedEntityId, 0) == roomSlot) {
                    entityList = String.valueOf(requestedEntityId);
                }
            } else {
                long guideBotId = NumberUtils.parseLong(Functions.Proc_10_0_809570("com.client.bot.guide.id", "0", 0));
                entityList = representedBotEntitiesForRoom(roomSlot, guideBotId);
            }
            if (entityList.isEmpty()) {
                return 0L;
            }
            long removedCount = 0L;
            for (String entityIdText : entityList.split("\r", -1)) {
                long botEntityId = NumberUtils.parseLong(entityIdText);
                if (botEntityId > 0L) {
                    Proc_6_248_802B80(roomId, PetPayloads.removedFromRoom(botEntityId), 0);
                    removeRepresentedBotRecord(botEntityId);
                    removedCount++;
                }
            }
            return removedCount;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    public static String Proc_6_190_7D11D0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "Cg");
            LongRef offset = new LongRef(1);
            long requestedRoomUserIndex = readWireLong(requestPayload, offset);
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
            RoomDao rooms = roomDao();
            java.util.Optional<RoomUserProfileRow> row = rooms.activeRoomUserProfileByVisitId(roomId, requestedRoomUserIndex);
            if (row.isEmpty()) {
                row = rooms.activeRoomUserProfileByUserId(roomId, requestedRoomUserIndex);
            }
            if (row.isEmpty()) {
                return "";
            }
            String payload = representedRoomUserProfilePayload(row.get());
            if (!payload.isEmpty()) {
                Proc_6_244_801E80(socketIndex, payload, 0);
            }
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_191_7D18B0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "DG");
            LongRef offset = new LongRef(1);
            long requestedUserId = readWireLong(requestPayload, offset);
            if (socketIndex <= 0 || requestedUserId <= 0L) {
                return "";
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return "";
            }
            UserDao users = userDao();
            if (users == null) {
                return "";
            }
            int targetSocketIndex = (int) users.socketByUserId(requestedUserId);
            if (targetSocketIndex <= 0 && handlingCurrentRoomId(socketIndex, callerUserId) <= 0L) {
                return "";
            }
            String payload = SocialPayloads.tagDisplay(requestedUserId, Proc_6_196_7D3ED0(requestedUserId, 0, 0));
            Proc_6_244_801E80(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_192_7D1B80(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "B_");
            long requestedRoomUserIndex = readWireLong(requestPayload, new LongRef(1));
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
            RoomUserTargetRow target = activeRoomUserTarget(callerRoomId, requestedRoomUserIndex);
            if (target == null) {
                return "";
            }
            long targetRoomUserIndex = target.roomUserIndex();
            String targetUserId = String.valueOf(target.userId());
            if (targetRoomUserIndex <= 0L || targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return "";
            }
            String targetBadgePayload = SocialPayloads.badgeDisplay(NumberUtils.parseLong(targetUserId),
                Proc_6_195_7D38D0(targetUserId, 0, 0));
            Proc_6_244_801E80(socketIndex, targetBadgePayload, 0);
            if (callerRoomUserIndex > 0L && callerRoomUserIndex != targetRoomUserIndex) {
                String callerStatusPayload = SocialPayloads.roomUserStatus(callerRoomUserIndex, 0L);
                String targetStatusPayload = SocialPayloads.roomUserStatus(targetRoomUserIndex, 0L);
                if (!callerStatusPayload.isEmpty()) {
                    Proc_6_247_8027E0(socketIndex, callerStatusPayload, 0);
                }
                if (!targetStatusPayload.isEmpty()) {
                    Proc_6_247_8027E0(socketIndex, targetStatusPayload, 0);
                }
            }
            return targetBadgePayload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_193_7D2BB0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
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
            List<BadgeRow> inventoryRows = users.unequippedBadges(NumberUtils.parseLong(userId));
            String equippedPayload = Proc_6_195_7D38D0(userId, 0, 0);
            String payload = SocialPayloads.badgeInventory(inventoryRows, equippedPayload);
            Proc_6_244_801E80(socketIndex, payload, 0);
            Proc_6_244_801E80(socketIndex, SocialPayloads.badgeDisplay(NumberUtils.parseLong(userId), equippedPayload), 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_194_7D3180(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return "";
            }
            String packetPayload = handlingPacketPayload(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long userIdValue = NumberUtils.parseLong(userId);
            UserDao users = userDao();
            if (users == null) {
                return "";
            }
            users.clearEquippedBadges(userIdValue);
            String[] slots = badgeUpdateSelectionsFromWire(packetPayload);
            for (int slotIndex = 0; slotIndex < slots.length; slotIndex++) {
                String badgeId = slots[slotIndex];
                if (!badgeId.isEmpty()) {
                    users.equipBadge(userIdValue, badgeId, slotIndex + 1L);
                }
            }
            String equippedPayload = Proc_6_195_7D38D0(userId, 0, 0);
            String displayPayload = SocialPayloads.badgeDisplay(NumberUtils.parseLong(userId), equippedPayload);
            Proc_6_244_801E80(socketIndex, displayPayload, 0);
            if (handlingCurrentRoomId(socketIndex, userId) > 0L) {
                Proc_6_247_8027E0(socketIndex, displayPayload, 0);
            }
            return equippedPayload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_195_7D38D0(Object... args) {
        try {
            String userId = "";
            if (args != null && args.length >= 1 && NumberUtils.parseLong(args[0]) > 0L) {
                userId = String.valueOf((long) NumberUtils.parseLong(args[0]));
            }
            if (userId.isEmpty() || "0".equals(userId)) {
                int socketIndex = handlingSocketIndex(args);
                if (socketIndex > 0) {
                    userId = handlingUserIdFromSocket(socketIndex);
                }
            }
            if (userId.isEmpty() || "0".equals(userId)) {
                return SocialPayloads.equippedBadges(List.of());
            }
            UserDao users = userDao();
            if (users == null) {
                return SocialPayloads.equippedBadges(List.of());
            }
            List<BadgeRow> rows = users.equippedBadges(NumberUtils.parseLong(userId));
            return SocialPayloads.equippedBadges(rows);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return SocialPayloads.equippedBadges(List.of());
        }
    }

    public static String Proc_6_196_7D3ED0(Object... args) {
        try {
            String userId = "";
            if (args != null && args.length >= 1 && NumberUtils.parseLong(args[0]) > 0L) {
                userId = String.valueOf((long) NumberUtils.parseLong(args[0]));
            }
            if (userId.isEmpty() || "0".equals(userId)) {
                int socketIndex = handlingSocketIndex(args);
                if (socketIndex > 0) {
                    userId = handlingUserIdFromSocket(socketIndex);
                }
            }
            if (userId.isEmpty() || "0".equals(userId)) {
                return SocialPayloads.tags(List.of());
            }
            UserDao users = userDao();
            if (users == null) {
                return SocialPayloads.tags(List.of());
            }
            List<String> rows = users.tagNames(NumberUtils.parseLong(userId));
            return SocialPayloads.tags(rows);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return SocialPayloads.tags(List.of());
        }
    }

    public static String Proc_6_197_7D43C0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return "";
            }
            String requestPayload = handlingRequestPayload(args, "AK");
            LongRef offset = new LongRef(1);
            long lookX = readWireLong(requestPayload, offset);
            long lookY = readWireLong(requestPayload, offset);
            if (lookX == 0L && lookY == 0L) {
                lookX = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
                lookY = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            }
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
            MovementPosition current = movementPosition(Licence.representedRooms().movementPosition(roomSlot, socketIndex));
            long currentX = current.found ? current.positionX : 0L;
            long currentY = current.found ? current.positionY : 0L;
            long directionValue = handlingDirectionCode(Long.compare(lookX, currentX), Long.compare(lookY, currentY));
            Licence.setRepresentedRooms(
                Licence.representedRooms().moveOccupant(roomSlot, socketIndex, currentX, currentY, directionValue, 0L));
            Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "ROOMS", roomId + ".cache").toString(), 0, 0);
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_198_7D4B70(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return "";
            }
            String requestPayload = handlingRequestPayload(args, "AO");
            LongRef offset = new LongRef(1);
            long targetX = readWireLong(requestPayload, offset);
            long targetY = readWireLong(requestPayload, offset);
            if (targetX == 0L && targetY == 0L) {
                targetX = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
                targetY = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            }
            if (targetX < 0L || targetY < 0L) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L || Functions.Proc_10_25_80F5D0(roomId, targetX, targetY) == 0L) {
                return "";
            }
            long roomSlot = socketIndex;
            MovementPosition current = movementPosition(Licence.representedRooms().movementPosition(roomSlot, socketIndex));
            long currentX = current.found ? current.positionX : 0L;
            long currentY = current.found ? current.positionY : 0L;
            String movementText = Functions.Proc_10_24_80E790(socketIndex, currentX, currentY, targetX, targetY);
            long nextX = handlingMovementField(movementText, 0);
            long nextY = handlingMovementField(movementText, 1);
            long directionValue = handlingMovementField(movementText, 2);
            long movingValue = handlingMovementField(movementText, 3);
            if (movingValue == 0L && (currentX != targetX || currentY != targetY)) {
                movingValue = 1L;
            }
            Licence.setRepresentedRooms(
                Licence.representedRooms().moveOccupant(roomSlot, socketIndex, nextX, nextY, directionValue, movingValue));
            Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "ROOMS", roomId + ".cache").toString(), 0, 0);
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_199_7D54E0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long pollId = pollIdFromWire(handlingPacketPayload(args), "Ck");
            if (pollId <= 0L) {
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
            PollDao polls = pollDao();
            if (polls == null || polls.pollHeader(pollId, roomId).isEmpty()) {
                return "";
            }
            polls.recordPollExit(NumberUtils.parseLong(userId), pollId);
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_200_7D5770(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            PollAnswerSubmission submission = pollAnswerFromWire(handlingPacketPayload(args), "Cl");
            if (!submission.valid) {
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
            PollDao polls = pollDao();
            if (polls == null || polls.pollHeader(submission.pollId, roomId).isEmpty()) {
                return "";
            }
            polls.recordPollAnswer(submission.pollId, submission.questionId, submission.answerText, NumberUtils.parseLong(userId));
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_201_7D5AC0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long pollId = pollIdFromWire(handlingPacketPayload(args), "Cj");
            if (pollId <= 0L) {
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
            PollDao polls = pollDao();
            if (polls == null) {
                return "";
            }
            PollDefinition poll = polls.pollDefinition(pollId, roomId).orElse(null);
            String payload = PollPayloads.poll(poll);
            if (!payload.isEmpty()) {
                Proc_6_244_801E80(socketIndex, payload, 0);
            }
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_202_7D6760(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return "";
            }
            long enabledValue = NumberUtils.parseLong(Functions.Proc_10_0_809570("com.client.catalog.recycler.enabled", 0, 0));
            if (enabledValue == 0L) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            RecyclerSelection selection = recyclerSelectionFromWire(handlingPacketPayload(args));
            if (!selection.valid) {
                return "";
            }
            FurnitureDao furniture = furnitureDao();
            if (furniture == null) {
                return "";
            }
            long userIdValue = NumberUtils.parseLong(userId);
            long validCount = furniture.recyclableInventoryCount(userIdValue, selection.selectedItems);
            if (validCount != selection.requestedCount) {
                return "";
            }
            long rewardProductId = representedRecyclerRewardProduct();
            if (rewardProductId <= 0L) {
                return "";
            }
            CatalogDao catalog = catalogDao();
            long rewardDestinationId = catalog == null ? 0L : catalog.destinationIdByProduct(rewardProductId);
            if (rewardDestinationId <= 0L) {
                rewardDestinationId = rewardProductId;
            }
            String rewardSign = recyclerRewardSign();
            furniture.updateRecyclerRewardBox(
                userIdValue,
                Licence.recyclerSettings().boxProductId(),
                rewardSign,
                rewardDestinationId);
            furniture.clearRecyclerItems(userIdValue, selection.selectedItems);
            furniture.insertRecyclerLog(userIdValue, selection.selectedItems, rewardProductId);
            for (String furnitureId : selection.selectedItems.split(",", -1)) {
                long selectedFurnitureId = NumberUtils.parseLong(furnitureId);
                if (selectedFurnitureId > 0L) {
                    Proc_6_244_801E80(socketIndex, InventoryMessagePayloads.remove(selectedFurnitureId), 0);
                }
            }
            String payload = RecyclerPayloads.reward(rewardProductId);
            Proc_6_244_801E80(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_203_7D7F80(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long enabledValue = NumberUtils.parseLong(Functions.Proc_10_0_809570("com.client.catalog.recycler.enabled", 0, 0));
            String payload = RecyclerPayloads.status(enabledValue, 0L);
            Proc_6_244_801E80(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_204_7D82E0(Object... args) {
        try {
            if (args == null || args.length < 2) {
                return "";
            }
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            long achievementIndex = NumberUtils.parseLong(args[1]);
            long badgeLevel = args.length >= 3 ? NumberUtils.parseLong(args[2]) : 1L;
            if (badgeLevel <= 0L) {
                badgeLevel = 1L;
            }
            AchievementSettings.Achievement achievement = achievementByIndex(achievementIndex);
            if (userId.isEmpty() || achievement == null) {
                return "";
            }
            String badgePrefix = achievement.badgePrefix();
            String badgeId = badgePrefix + badgeLevel;
            if (achievement.achievementId() == 0L || badgePrefix.isEmpty()) {
                return "";
            }
            UserDao users = userDao();
            if (users == null) {
                return "";
            }
            long userIdValue = NumberUtils.parseLong(userId);
            users.deleteBadgesByPrefix(userIdValue, badgePrefix);
            users.insertBadge(userIdValue, badgeId);
            long badgeRowId = users.badgeRowId(userIdValue, badgeId);
            String payload = achievementRewardPayload(achievementIndex, achievement, badgeLevel, badgeRowId);
            Proc_6_244_801E80(socketIndex, payload, 0);
            String awardPayload = achievementAwardPayload(achievement);
            if (!awardPayload.isEmpty()) {
                users.addAchievementReward(
                    userIdValue,
                    achievement.rewardType(),
                    achievement.rewardIncrease(),
                    achievement.scoreIncrease());
                Proc_6_244_801E80(socketIndex, awardPayload, 0);
            }
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_205_7D9780(Object... args) {
        try {
            if (args == null || args.length < 2) {
                return "";
            }
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0 && args.length >= 2) {
                socketIndex = (int) NumberUtils.parseLong(args[1]);
            }
            long achievementQuestId = NumberUtils.parseLong(args[args.length - 1]);
            if (socketIndex <= 0 || achievementQuestId <= 0L) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            AchievementSettings achievementSettings = Licence.achievementSettings();
            List<AchievementSettings.Achievement> achievements = achievementSettings.achievements();
            if (achievements.isEmpty()) {
                return "";
            }
            AchievementProgressDecision decision = achievementProgressDecision(
                achievementSettings.indexedAchievements(),
                achievementQuestId,
                achievementCurrentLevels(userId, achievements),
                representedAchievementProgress(userId, achievementQuestId));
            if (decision.shouldReward) {
                Proc_6_204_7D82E0(socketIndex, decision.achievementIndex, decision.nextLevel);
            }
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_206_7DA450(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            List<AchievementSettings.Achievement> achievements = Licence.achievementSettings().achievements();
            if (achievements.isEmpty()) {
                return "";
            }
            String payload = achievementListPayload(achievements, achievementCurrentLevels(userId, achievements));
            Proc_6_244_801E80(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static long Proc_6_207_7DB0D0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long roomId = 0L;
            if (socketIndex > 0) {
                String userId = handlingUserIdFromSocket(socketIndex);
                if (!userId.isEmpty() && !"0".equals(userId)) {
                    roomId = handlingCurrentRoomId(socketIndex, userId);
                }
            }
            if (roomId <= 0L && args != null && args.length >= 2) {
                roomId = NumberUtils.parseLong(args[1]);
            }
            long triggerCode = args != null && args.length >= 3 ? NumberUtils.parseLong(args[2]) : 0L;
            return handlingRepresentedWiredTrigger(roomId, triggerCode, socketIndex);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    public static long Proc_6_208_7DC030(Object... args) {
        return handlingRepresentedWiredActionCall(args, 506);
    }

    public static long Proc_6_209_7DE480(Object... args) {
        return handlingRepresentedWiredActionCall(args, 505);
    }

    public static String Proc_6_210_7E1DC0(Object... args) {
        return "";
    }

    public static long Proc_6_211_7E1E40(Object... args) {
        return handlingRepresentedWiredTriggerCall(args, 1004);
    }

    public static long Proc_6_212_7E36C0(Object... args) {
        return handlingRepresentedWiredTriggerCall(args, 1001);
    }

    public static long Proc_6_213_7E3FA0(Object... args) {
        return handlingRepresentedWiredTriggerCall(args, 1003);
    }

    public static long Proc_6_214_7E60C0(Object... args) {
        return handlingRepresentedWiredTriggerCall(args, 1002);
    }

    public static long Proc_6_215_7E6770(Object... args) {
        return handlingRepresentedWiredActionCall(args, 503);
    }

    public static long Proc_6_216_7E8120(Object... args) {
        return handlingRepresentedWiredActionCall(args, 502);
    }

    public static long Proc_6_217_7E9780(Object... args) {
        return handlingRepresentedWiredActionCall(args, 501);
    }

    public static String Proc_6_218_7EA200(Object... args) {
        try {
            if (args == null || args.length == 0) {
                return "";
            }
            return wiredSpecialStatePayload(NumberUtils.parseLong(args[0]));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_219_7EA390(Object... args) {
        return handlingRepresentedWiredEdit(args, "oj", 1, 500, "wired_trigger", false);
    }

    public static String Proc_6_220_7EBA50(Object... args) {
        return handlingRepresentedWiredEdit(args, "ok", 501, 1000, "wired_action", true);
    }

    public static String Proc_6_221_7ED1E0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "on");
            LongRef offset = new LongRef(1);
            long furnitureId = readWireLong(requestPayload, offset);
            if (furnitureId <= 0L) {
                furnitureId = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            }
            if (socketIndex <= 0 || furnitureId <= 0L) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L || (!handlingUserHasRoomRight(userId, roomId) && !handlingUserOwnsRoom(userId, roomId))) {
                return "";
            }
            long productId = furnitureDao().roomFurnitureProductById(furnitureId, roomId)
                .map(FurnitureDao.RoomFurnitureProduct::productId)
                .orElse(0L);
            if (productId <= 0L) {
                return "";
            }
            long wiredCode = NumberUtils.parseLong(DataManager.Proc_8_12_806C30(productId, 27, 0));
            if (wiredCode <= 0L) {
                return "";
            }
            Path snapshotPath = Path.of(Functions.applicationPath, "cache", "wired_snapshots", furnitureId + ".cache");
            Files.createDirectories(snapshotPath.getParent());
            DataManager.Proc_8_10_8068E0(snapshotPath.toString(), Licence.representedRooms().cacheText(), wiredCode);
            return snapshotPath.toString();
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_222_7ED710(Object... args) {
        return handlingRepresentedWiredEdit(args, "ol", 1001, 1500, "wired_condition", false);
    }

    public static String Proc_6_223_7EEDD0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return "";
            }
            SongInfoRequest request = songInfoRequestFromWire(handlingPacketPayload(args));
            JukeboxDao jukebox = jukeboxDao();
            String payload = jukebox == null
                ? JukeboxPayloads.songInfo(List.of())
                : JukeboxPayloads.songInfo(jukebox.songInfoRows(request.requestedIds, request.requestedCount));
            Proc_6_244_801E80(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_224_7EF5A0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long roomId = args != null && args.length >= 2 ? NumberUtils.parseLong(args[1]) : 0L;
            long jukeboxId = args != null && args.length >= 3 ? NumberUtils.parseLong(args[2]) : 0L;
            if (socketIndex > 0) {
                String userId = handlingUserIdFromSocket(socketIndex);
                if (!userId.isEmpty() && !"0".equals(userId) && roomId <= 0L) {
                    roomId = handlingCurrentRoomId(socketIndex, userId);
                }
            }
            if (jukeboxId <= 0L && roomId > 0L) {
                jukeboxId = jukeboxRowForRoom(roomId).map(JukeboxRow::id).orElse(0L);
            }
            if (jukeboxId > 0L) {
                JukeboxDao jukebox = jukeboxDao();
                long activeDestinationId = jukebox == null ? 0L : jukebox.activeDestinationId(jukeboxId);
                FurnitureRoomCache.State cacheState = Licence.furnitureRoomCache();
                cacheState.pendingFurnitureCache = removeSoundMachineMarkers(cacheState.pendingFurnitureCache, jukeboxId, activeDestinationId);
                Licence.setFurnitureRoomCache(cacheState);
            }
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_225_7EFBD0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            JukeboxAddRequest request = jukeboxAddRequestFromWire(handlingPacketPayload(args));
            if (request.diskFurnitureId <= 0L) {
                return "";
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            JukeboxDao jukebox = jukeboxDao();
            JukeboxRow jukeboxRow = jukeboxRowForRoom(roomId).orElse(null);
            if (jukebox == null || jukeboxRow == null || jukeboxRow.id() <= 0L) {
                return "";
            }
            long jukeboxId = jukeboxRow.id();
            long jukeboxProductId = jukeboxRow.productId();
            String maxOrderText = jukebox.maxPlaylistOrderText(jukeboxId);
            long playlistCount = jukebox.playlistCount(jukeboxId);
            long playlistLimit = NumberUtils.parseLong(Functions.Proc_10_0_809570(
                "com.server.socket.game.jukebox." + jukeboxProductId + ".soundsets.max", 0, 0));
            if (!jukeboxCanAddDisk(request.playlistOrder, maxOrderText, playlistCount, playlistLimit)) {
                return "";
            }
            long songDiskProductId = NumberUtils.parseLong(Functions.Proc_10_0_809570("com.server.socket.game.default.songdisk", 0, 0));
            if (songDiskProductId <= 0L) {
                return "";
            }
            long userIdValue = NumberUtils.parseLong(userId);
            long destinationId = jukebox.diskDestinationForOwner(userIdValue, request.diskFurnitureId, songDiskProductId);
            if (destinationId <= 0L) {
                return "";
            }
            jukebox.removeDiskFromOwner(userIdValue, request.diskFurnitureId, songDiskProductId);
            jukebox.addPlaylistEntry(jukeboxId, request.diskFurnitureId, request.playlistOrder, destinationId);
            String payload = InventoryMessagePayloads.remove(request.diskFurnitureId);
            Proc_6_244_801E80(socketIndex, payload, 0);
            Proc_6_227_7F2400(socketIndex);
            Proc_6_228_7F2AF0(socketIndex);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_226_7F0B20(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long playlistOrder = jukeboxRemoveOrderFromWire(handlingPacketPayload(args));
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            long jukeboxId = jukeboxRowForRoom(roomId).map(JukeboxRow::id).orElse(0L);
            if (jukeboxId <= 0L) {
                return "";
            }
            JukeboxDao jukebox = jukeboxDao();
            if (jukebox == null) {
                return "";
            }
            long cdFurnitureId = jukebox.diskFurnitureIdAtOrder(jukeboxId, playlistOrder);
            if (cdFurnitureId <= 0L) {
                return "";
            }
            long songDiskProductId = NumberUtils.parseLong(Functions.Proc_10_0_809570("com.server.socket.game.default.songdisk", 0, 0));
            jukebox.returnDiskToOwner(NumberUtils.parseLong(userId), cdFurnitureId, songDiskProductId);
            jukebox.deletePlaylistEntry(jukeboxId, cdFurnitureId);
            jukebox.decrementOrdersAfter(jukeboxId, playlistOrder);
            Proc_6_227_7F2400(socketIndex);
            Proc_6_228_7F2AF0(socketIndex);
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_227_7F2400(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
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
            JukeboxDao jukebox = jukeboxDao();
            JukeboxRow jukeboxRow = jukeboxRowForRoom(roomId).orElse(null);
            if (jukebox == null || jukeboxRow == null || jukeboxRow.id() <= 0L) {
                return "";
            }
            long jukeboxId = jukeboxRow.id();
            long jukeboxProductId = jukeboxRow.productId();
            long playlistLimit = NumberUtils.parseLong(Functions.Proc_10_0_809570("com.server.socket.game.jukebox." + jukeboxProductId + ".soundsets.max", 0, 0));
            if (playlistLimit <= 0L) {
                playlistLimit = jukebox.playlistLimitFromEntries(jukeboxId);
            }
            if (playlistLimit <= 0L) {
                playlistLimit = 100L;
            }
            String payload = JukeboxPayloads.playlist(playlistLimit, jukebox.playlistEntries(jukeboxId, playlistLimit));
            Proc_6_244_801E80(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_228_7F2AF0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long songDiskProductId = NumberUtils.parseLong(Functions.Proc_10_0_809570("com.server.socket.game.default.songdisk", 0, 0));
            if (songDiskProductId <= 0L) {
                return "";
            }
            JukeboxDao jukebox = jukeboxDao();
            if (jukebox == null) {
                return "";
            }
            String payload = JukeboxPayloads.diskInventory(jukebox.songDisks(NumberUtils.parseLong(userId), songDiskProductId));
            Proc_6_244_801E80(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_229_7F3070(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return "";
            }
            long roomId = args != null && args.length >= 2 ? NumberUtils.parseLong(args[1]) : 0L;
            long jukeboxId = args != null && args.length >= 3 ? NumberUtils.parseLong(args[2]) : 0L;
            String userId = handlingUserIdFromSocket(socketIndex);
            if (!userId.isEmpty() && !"0".equals(userId) && roomId <= 0L) {
                roomId = handlingCurrentRoomId(socketIndex, userId);
            }
            if (jukeboxId <= 0L && roomId > 0L) {
                jukeboxId = jukeboxRowForRoom(roomId).map(JukeboxRow::id).orElse(0L);
            }
            if (jukeboxId <= 0L) {
                return "";
            }
            JukeboxDao jukebox = jukeboxDao();
            JukeboxPlaybackRow row = jukebox == null ? null : jukebox.playbackRow(jukeboxId).orElse(null);
            if (row == null) {
                return "";
            }
            String payload = JukeboxPayloads.playback(System.currentTimeMillis() / 1000L,
                row.sequenceId(),
                row.destinationId(),
                row.diskFurnitureId());
            if (payload.isEmpty()) {
                return "";
            }
            if (roomId > 0L) {
                Proc_6_246_8024C0(roomId, payload, 0);
            } else {
                Proc_6_247_8027E0(socketIndex, payload, 0);
            }
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_230_7F3D20(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "Gd");
            String rawMotto = readWireString(requestPayload, new LongRef(1));
            if (rawMotto.isEmpty()) {
                rawMotto = Functions.Proc_10_7_80A190(requestPayload, 0, 0);
            }
            if (rawMotto.isEmpty()) {
                rawMotto = requestPayload;
            }
            String mottoText = left(Functions.Proc_10_10_80A7F0(rawMotto, 0, 0), 255);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long numericUserId = NumberUtils.parseLong(userId);
            UserDao users = userDao();
            if (users == null) {
                return "";
            }
            users.updateMotto(numericUserId, mottoText);
            UserDao.UserIdentity identity = users.findIdentity(numericUserId)
                .orElse(new UserDao.UserIdentity(numericUserId, 0L, "", "", "M"));
            String figureText = StringUtils.text(identity.figure());
            String genderText = left(StringUtils.text(identity.gender()).toUpperCase(), 1);
            if (!"M".equals(genderText) && !"F".equals(genderText)) {
                genderText = "M";
            }
            String payload = userIdentityPayload(numericUserId, mottoText, genderText, figureText);
            Proc_6_244_801E80(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static long Proc_7F44D0(Object... args) {
        try {
            String packetPayload = handlingPacketPayload(args);
            String requestPayload = packetPayload.startsWith("oL") ? packetPayload.substring(2) : packetPayload;
            if (packetPayload.length() >= 3) {
                Functions.Proc_10_5_809D80(packetPayload, 3, 0);
            }
            String valueText = Functions.Proc_10_6_809F10(requestPayload, 0, 0);
            if (valueText.isEmpty()) {
                valueText = readWireString(requestPayload, new LongRef(1));
            }
            return NumberUtils.parseLong(valueText);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    public static String Proc_6_231_7F4510(Object... args) {
        try {
            Proc_6_244_801E80(handlingSocketIndex(args), "Ic" + "IQA", 0);
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_232_7F45A0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long requestedQuestId = questRequestIdFromWire(handlingPacketPayload(args), "p^");
            if (requestedQuestId <= 0L) {
                requestedQuestId = NumberUtils.parseLong(Functions.Proc_10_6_809F10(handlingRequestPayload(args, "p^"), 0, 0));
            }
            if (requestedQuestId <= 0L) {
                return "";
            }
            QuestSettings.QuestDefinitionRow questDefinition = questSettingsFromSource().definitionById(requestedQuestId);
            if (questDefinition == null || questDefinition.fieldCount() < 11) {
                return "";
            }
            long questId = questDefinition.questId();
            long activityCount = questDefinition.activityAmount();
            long waitAmount = questDefinition.waitAmount();
            if (activityCount <= 0L) {
                activityCount = 1L;
            }
            QuestDao quests = questDao();
            if (quests == null) {
                return "";
            }
            long userIdValue = NumberUtils.parseLong(userId);
            quests.clearAcceptedQuest(userIdValue);
            long existingLevel = quests.existingLevel(userIdValue, questId);
            if (existingLevel != Long.MIN_VALUE) {
                quests.reactivateQuest(userIdValue, questId, requestedQuestId);
            } else {
                quests.insertQuest(userIdValue, questId, requestedQuestId);
            }
            long progressValue = quests.progress(userIdValue, questId);
            if (waitAmount > 0L && progressValue > 0L && progressValue < activityCount) {
                String timeNextText = quests.timeNext(userIdValue, questId);
                if (timeNextText.isEmpty() || "0".equals(timeNextText)) {
                    quests.scheduleNextTime(userIdValue, questId, waitAmount);
                }
            }
            if (progressValue >= activityCount) {
                Proc_6_164_7BC820(socketIndex, questId, requestedQuestId);
            } else {
                Proc_6_236_7F8540(socketIndex, "", "");
            }
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_233_7F5D60(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            QuestDao quests = questDao();
            if (quests == null) {
                return "";
            }
            long userIdValue = NumberUtils.parseLong(userId);
            QuestDao.UserQuestLevelRow activeRow = quests.activeLevelRow(userIdValue)
                .or(() -> {
                    try {
                        return quests.latestLevelRow(userIdValue);
                    } catch (Exception ignored) {
                        return java.util.Optional.empty();
                    }
                })
                .orElse(null);
            long requestedQuestId = nextQuestId(questSettingsFromSource(), activeRow);
            if (requestedQuestId > 0L) {
                Proc_6_232_7F45A0(socketIndex, QuestPayloads.request(requestedQuestId));
            }
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_234_7F75C0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            QuestDao quests = questDao();
            if (quests == null) {
                return "";
            }
            quests.resetUserQuests(NumberUtils.parseLong(userId));
            Proc_6_244_801E80(socketIndex, "Lc", 0);
            Proc_6_236_7F8540(socketIndex, "", "");
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_235_7F77E0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            QuestDao quests = questDao();
            if (quests == null) {
                return "";
            }
            long userIdValue = NumberUtils.parseLong(userId);
            QuestDao.UserQuestProgressRow activeQuest = quests.activeProgressRow(userIdValue).orElse(null);
            if (activeQuest == null) {
                return "";
            }
            long remainingWait = 0L;
            if (!StringUtils.text(activeQuest.timeNext()).isEmpty() && !"0".equals(StringUtils.text(activeQuest.timeNext()))) {
                remainingWait = quests.remainingWait(activeQuest.timeNext());
            }
            QuestProgressDecision decision = questProgressDecision(activeQuest, questSettingsFromSource(), remainingWait);
            if (decision.shouldScheduleWait) {
                quests.scheduleNextTime(userIdValue, activeQuest.questId(), decision.waitAmount);
            }
            if (decision.shouldComplete) {
                Proc_6_164_7BC820(socketIndex, activeQuest.questId(), activeQuest.numericQuestId());
            } else if (decision.shouldSendList) {
                Proc_6_236_7F8540(socketIndex, "", "");
            }
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_236_7F8540(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            QuestDao quests = questDao();
            if (quests == null) {
                return "";
            }
            String payload = QuestPayloads.list(questSettingsFromSource(),
                userQuestRowsWithRemainingWait(quests, quests.listRows(NumberUtils.parseLong(userId))));
            Proc_6_244_801E80(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_237_7F9ED0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            String payload = userDao().ownProfile(NumberUtils.parseLong(userId))
                .map(UserPayloads::ownProfile)
                .orElse("");
            if (!payload.isEmpty()) {
                Proc_6_244_801E80(socketIndex, payload, 0);
            }
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_7FA5A0(Object... args) {
        return "";
    }

    public static String Proc_6_238_7FA670(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long sessionSeconds = representedActivityPointSessionSeconds(socketIndex, userId);
            if (sessionSeconds <= 0L) {
                return "";
            }
            StringBuilder sentPayloads = new StringBuilder();
            UserDao users = userDao();
            long userIdValue = NumberUtils.parseLong(userId);
            for (long pointType = 0L; pointType <= 4L; pointType++) {
                long intervalSeconds = NumberUtils.parseLong(Functions.Proc_10_0_809570(
                    "com.server.socket.game.activitypoints_" + pointType + ".interval", 0, 0));
                if (intervalSeconds > 0L && sessionSeconds % intervalSeconds == 0L) {
                    long maxPoints = NumberUtils.parseLong(Functions.Proc_10_0_809570(
                        "com.server.socket.game.activitypoints_" + pointType + ".max", 1, 0));
                    long currentPoints = users.activityPoints(userIdValue, pointType);
                    long awardAmount = NumberUtils.parseLong(Functions.Proc_10_0_809570(
                        "com.server.socket.game.activitypoints_" + pointType + ".amount", 0, 0));
                    ActivityPointAward award = activityPointAwardDecision(
                        sessionSeconds, pointType, intervalSeconds, maxPoints, awardAmount, currentPoints);
                    if (award.shouldAward) {
                        users.addActivityPoints(userIdValue, pointType, awardAmount);
                        Proc_6_244_801E80(socketIndex, award.payload, 0);
                        sentPayloads.append(award.payload);
                    }
                }
            }
            return sentPayloads.toString();
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_168_7C05F0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "@b");
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return "";
            }
            LongRef offset = new LongRef(1);
            long targetCount = readWireLong(requestPayload, offset);
            if (targetCount <= 0L) {
                return "";
            }
            targetCount = Math.min(targetCount, 150L);
            long userIdValue = NumberUtils.parseLong(userId);
            MessengerDao messenger = messengerDao();
            StringBuilder targetList = new StringBuilder();
            for (long targetIndex = 1L; targetIndex <= targetCount; targetIndex++) {
                String targetUserId = String.valueOf(readWireLong(requestPayload, offset));
                if (!targetUserId.isEmpty() && !"0".equals(targetUserId)
                    && !("," + targetList + ",").contains("," + targetUserId + ",")) {
                    if (messenger != null
                        && messenger.acceptedFriendshipExists(userIdValue, NumberUtils.parseLong(targetUserId))
                        && handlingSocketFromUserId(targetUserId) > 0) {
                        if (targetList.length() > 0) {
                            targetList.append(',');
                        }
                        targetList.append(targetUserId);
                    }
                }
            }
            String inviteText = Functions.Proc_10_7_80A190(requestPayload, 0, 0);
            if (inviteText.length() > 122) {
                inviteText = inviteText.substring(0, 122);
            }
            if (inviteText.isEmpty()) {
                inviteText = readWireString(requestPayload, offset);
                if (inviteText.length() > 122) {
                    inviteText = inviteText.substring(0, 122);
                }
            }
            String filteredText = Proc_6_22_6E9300(inviteText, 0, 0);
            String payload = MessengerPayloads.roomInviteMessage(NumberUtils.parseLong(userId), filteredText);
            if (targetList.length() > 0) {
                for (String targetUserId : targetList.toString().split(",", -1)) {
                    int targetSocketIndex = handlingSocketFromUserId(targetUserId);
                    if (targetSocketIndex > 0) {
                        Proc_6_244_801E80(targetSocketIndex, payload, 0);
                        if (messenger != null) {
                            messenger.insertInviteChatLog(
                                userIdValue,
                                roomId,
                                "(Invite To: " + handlingUserName(targetUserId) + ") -- " + inviteText,
                                socketIndex);
                        }
                    }
                }
            }
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_167_7BECA0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "@e");
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long userIdValue = NumberUtils.parseLong(userId);
            MessengerDao messenger = messengerDao();
            if (messenger == null) {
                return "";
            }
            LongRef offset = new LongRef(1);
            long acceptCount = readWireLong(requestPayload, offset);
            if (acceptCount <= 0L) {
                return "";
            }
            acceptCount = Math.min(acceptCount, 75L);
            String dateFormat = Functions.Proc_10_0_809570("com.mysql.format.date", "%d-%m-%Y", 0);
            String timeFormat = Functions.Proc_10_0_809570("com.mysql.format.time", "%H:%i", 0);
            String dateTimeFormat = Functions.Proc_10_11_80A9C0(dateFormat + " " + timeFormat, 0, 0);
            StringBuilder targetIds = new StringBuilder();
            for (long acceptIndex = 1L; acceptIndex <= acceptCount; acceptIndex++) {
                String targetUserId = String.valueOf(readWireLong(requestPayload, offset));
                if (!targetUserId.isEmpty() && !"0".equals(targetUserId)
                    && !("," + targetIds + ",").contains("," + targetUserId + ",")) {
                    if (messenger.pendingRequestExists(userIdValue, NumberUtils.parseLong(targetUserId), dateTimeFormat)) {
                        if (targetIds.length() > 0) {
                            targetIds.append(',');
                        }
                        targetIds.append(targetUserId);
                    }
                }
            }
            if (targetIds.length() == 0) {
                return "";
            }
            long acceptedCount = 0L;
            StringBuilder payloadRows = new StringBuilder();
            for (String targetUserId : targetIds.toString().split(",", -1)) {
                long targetUserIdValue = NumberUtils.parseLong(targetUserId);
                MessengerFriend friend = messenger.messengerFriend(targetUserIdValue, dateTimeFormat).orElse(null);
                if (friend != null) {
                    int targetSocketIndex = (int) friend.socketIndex();
                    payloadRows.append('H').append(messengerFriendPayload(
                        friend.userId(),
                        friend.userName(),
                        friend.motto(),
                        friend.figure(),
                        friend.level(),
                        targetSocketIndex > 0 ? 2L : 0L,
                        targetSocketIndex > 0 ? 1L : 0L,
                        friend.lastOnline(),
                        0L));
                    messenger.insertReversePendingFriendship(targetUserIdValue, userIdValue);
                    messenger.acceptFriendshipPair(userIdValue, targetUserIdValue);
                    if (targetSocketIndex > 0) {
                        String notifyPayload = MessengerPayloads.friendOnlineNotification(
                            messengerFriendSummaryPayload(userId, 1L));
                        Proc_6_244_801E80(targetSocketIndex, notifyPayload, 0);
                    }
                    acceptedCount++;
                }
            }
            if (acceptedCount > 0L) {
                String callerPayload = MessengerPayloads.acceptedFriends(payloadRows.toString(), acceptedCount);
                Proc_6_244_801E80(socketIndex, callerPayload, 0);
                return callerPayload;
            }
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_169_7C0DC0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "DF");
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            LongRef offset = new LongRef(1);
            String targetUserId = String.valueOf(readWireLong(requestPayload, offset));
            if (targetUserId.isEmpty() || "0".equals(targetUserId)) {
                targetUserId = String.valueOf((long) NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0)));
            }
            if (targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return "";
            }
            MessengerDao messenger = messengerDao();
            if (messenger == null || !messenger.acceptedFriendshipExists(
                NumberUtils.parseLong(userId),
                NumberUtils.parseLong(targetUserId))) {
                return "";
            }
            int targetSocketIndex = handlingSocketFromUserId(targetUserId);
            if (targetSocketIndex <= 0) {
                return "";
            }
            long targetRoomId = handlingCurrentRoomId(targetSocketIndex, targetUserId);
            if (targetRoomId <= 0L) {
                return "";
            }
            long targetRoomUserIndex = representedRoomUserIndex(targetSocketIndex, targetUserId);
            String payload = MessengerPayloads.followRoom(targetRoomUserIndex, targetRoomId);
            Proc_6_244_801E80(socketIndex, payload, 0);
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

    public static void Proc_6_241_7FC380(Object... args) {
        try {
            if (args == null || args.length < 2) {
                return;
            }
            long socketIndex = NumberUtils.parseLong(args[0]);
            String packetBuffer = Functions.Proc_10_9_80A680(StringUtils.text(args[1]), 0, 0);
            if (socketIndex <= 0L || Guardian.Proc_11_2_821390(socketIndex, 0, 0) != 1) {
                return;
            }
            long packetCount = 0L;
            while (packetBuffer.length() > 2 && packetCount < 10L) {
                packetBuffer = packetBuffer.substring(1);
                long packetLength = Crypto.Proc_3_4_6D3620(StringUtils.left(packetBuffer, 2));
                if (packetLength <= 0L || packetBuffer.length() < packetLength + 2L) {
                    break;
                }
                String packetPayload = StringUtils.mid(packetBuffer, 3, (int) packetLength);
                String packetCode = StringUtils.left(packetPayload, 2);
                if (Licence.runtimeState().shouldTracePackets()) {
                    Console.Proc_2_0_6D1510("[" + socketIndex + "] " + packetPayload, "GAME", "16711680");
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
                case "oD": Proc_6_231_7F4510(socketIndex, packetPayload, 0); break;
                case "Gd": Proc_6_230_7F3D20(socketIndex, packetPayload, 0); break;
                case "C]": Proc_6_223_7EEDD0(socketIndex, packetPayload, 0); break;
                case "C\u007f": Proc_6_225_7EFBD0(socketIndex, packetPayload, 0); break;
                case "D@": Proc_6_226_7F0B20(socketIndex, packetPayload, 0); break;
                case "DC":
                    Proc_6_227_7F2400(socketIndex, packetPayload, 0);
                    Proc_6_228_7F2AF0(socketIndex, packetPayload, 0);
                    break;
                case "AZ": Proc_6_144_76BE70(socketIndex, "AZ", packetPayload); break;
                case "AC": Proc_6_155_795C90(socketIndex, "AC", packetPayload); break;
                case "A[": Proc_6_141_76A670(socketIndex, "A[", packetPayload); break;
                case "AI": Proc_6_159_79FCD0(socketIndex, "AI", packetPayload); break;
                case "Ch": Proc_6_149_775C10(socketIndex, "Ch", packetPayload); break;
                case "FH": Proc_6_150_777FA0(socketIndex, "FH", packetPayload); break;
                case "@B": Proc_6_78_7279A0(socketIndex, "@B", packetPayload); break;
                case "rv": Proc_6_142_76B310(socketIndex, "rv", packetPayload); break;
                case "pa": Proc_6_244_801E80(socketIndex, "J|H", 0); break;
                case "Ce": dispatchPreReadySoundSetting(socketIndex, packetPayload); break;
                case "Cy": break;
                case "pb": Proc_6_234_7F75C0(socketIndex, "pb", packetPayload); break;
                case "p^": Proc_6_232_7F45A0(socketIndex, "p^", packetPayload); break;
                case "pc": Proc_6_233_7F5D60(socketIndex, "pc", packetPayload); break;
                case "p]": Proc_6_236_7F8540(socketIndex, "p]", packetPayload); break;
                case "GV": Proc_6_38_70FD10(socketIndex, "GV", packetPayload); break;
                case "GW": Proc_6_39_711650(socketIndex, "GW", packetPayload); break;
                case "F]": Proc_6_203_7D7F80(socketIndex, "F]", packetPayload); break;
                case "F^": Proc_6_202_7D6760(socketIndex, "F^", packetPayload); break;
                case "Cj": Proc_6_201_7D5AC0(socketIndex, "Cj", packetPayload); break;
                case "Ck": Proc_6_199_7D54E0(socketIndex, "Ck", packetPayload); break;
                case "Cl": Proc_6_200_7D5770(socketIndex, "Cl", packetPayload); break;
                case "EW": Proc_6_99_748460(socketIndex, "EW", packetPayload); break;
                case "Cw": Proc_6_95_746CD0(socketIndex, "Cw", packetPayload); break;
                case "AL": Proc_6_97_747640(socketIndex, "AL", packetPayload); break;
                case "AM": Proc_6_96_747000(socketIndex, "AM", packetPayload); break;
                case "FU": Proc_6_91_743480(socketIndex, "FU", packetPayload); break;
                case "AH": Proc_6_92_744870(socketIndex, "AH", packetPayload); break;
                case "FR": Proc_6_89_73EA10(socketIndex, "FR", packetPayload); break;
                case "EV": Proc_6_100_748C80(socketIndex, "EV", packetPayload); break;
                case "EU": Proc_6_98_747D80(socketIndex, "EU", packetPayload); break;
                case "Er": Proc_6_206_7DA450(socketIndex, "Er", packetPayload); break;
                case "CD": Proc_7FA5A0(socketIndex, "CD", packetPayload); break;
                case "@G": Proc_6_237_7F9ED0(socketIndex, "@G", packetPayload); break;
                case "D{":
                case "Fe": break;
                case "@t": Proc_6_26_7034C0(socketIndex, "@t", packetPayload); break;
                case "@w": Proc_6_27_706920(socketIndex, "@w", packetPayload); break;
                case "@x": Proc_6_28_709DA0(socketIndex, "@x", packetPayload); break;
                case "Cd": Proc_6_101_749540(socketIndex, "EA", packetPayload); break;
                case "Et":
                case "Eu": Proc_6_102_749C50(socketIndex, packetCode, packetPayload); break;
                case "@Z": Proc_6_19_6E8040(socketIndex, Licence.recyclerSettings().statusPayload(), "Gz"); break;
                case "oW": Proc_6_18_6E7480(socketIndex, "GY", packetPayload); break;
                case "Cn": Proc_6_30_70DC90(socketIndex, packetPayload, "EG"); break;
                case "A^": Proc_6_13_6E0A80(socketIndex, "A^", packetPayload); break;
                case "A]": Proc_6_14_6E10C0(socketIndex, "A]", packetPayload); break;
                case "GE": Proc_6_32_70EAB0(socketIndex, "GE", packetPayload); break;
                case "F`": Proc_6_33_70F4F0(socketIndex); break;
                case "Fa": Proc_6_34_70F590(socketIndex); break;
                case "Fb": Proc_6_37_70FC20(socketIndex, "Fb", packetPayload); break;
                case "Fc": Proc_6_36_70F7B0(socketIndex, "Fc", packetPayload); break;
                case "Fd": Proc_6_35_70F630(socketIndex, "Fd", packetPayload); break;
                case "Ae": dispatchPreReadyCatalogIndex(socketIndex); break;
                case "FC": Proc_6_104_74AB60(socketIndex, "FC", packetPayload); break;
                case "@]": Proc_6_105_74AD50(socketIndex, "@]", packetPayload); break;
                case "Af": Proc_6_136_765F10(socketIndex, "Af", packetPayload); break;
                case "Fv": Proc_6_125_755650(socketIndex, "Fv", packetPayload); break;
                case "FG": Proc_6_58_71FCA0(socketIndex, "FG", packetPayload); break;
                case "Bv": Proc_6_59_71FEE0(socketIndex, "Bv", packetPayload); break;
                case "@{": Proc_6_79_72A430(socketIndex, "@{", packetPayload); break;
                case "Ew": Proc_6_15_6E1900(socketIndex, "Ew", packetPayload); break;
                case "Ex": Proc_6_16_6E2320(socketIndex, "Ex", packetPayload); break;
                case "@l": Proc_6_17_6E48D0(socketIndex, "@l", packetPayload); break;
                case "oC": Proc_6_135_765D80(socketIndex, "oC", packetPayload); break;
                case "oV": Proc_6_134_765B90(socketIndex, "oV", packetPayload); break;
                case "Ad": Proc_6_128_756190(socketIndex, "Ad", packetPayload); break;
                case "GX": Proc_6_132_75D4A0(socketIndex, "GX", packetPayload); break;
                case "GZ": Proc_6_131_75C700(socketIndex, "GZ", packetPayload); break;
                case "G[": Proc_6_130_75B770(socketIndex, "G[", packetPayload); break;
                case "Gc": Proc_6_107_74B7E0(socketIndex, "Gc", packetPayload); break;
                case "GG": Proc_6_10_6DE1D0(socketIndex, "GG", packetPayload); break;
                case "F@": Proc_6_47_714F60(socketIndex, "F@", packetPayload); break;
                case "FB": Proc_6_44_7145E0(socketIndex, "FB", packetPayload); break;
                case "Ab": Proc_6_50_7166B0(socketIndex, "Ab", packetPayload); break;
                case "EZ": Proc_6_48_7151E0(socketIndex, "EZ", packetPayload); break;
                case "E\\": Proc_6_49_715D30(socketIndex, "E\\", packetPayload); break;
                case "FP":
                case "FF": Proc_6_43_713680(socketIndex, "FF", packetPayload); break;
                case "FQ": Proc_6_52_7172B0(socketIndex, "FQ", packetPayload); break;
                case "@H": Proc_6_108_74D800(socketIndex, "@H", packetPayload); break;
                case "@S": Proc_6_110_74DDA0(socketIndex, "@S", packetPayload); break;
                case "@T": Proc_6_109_74DBD0(socketIndex, "@T", packetPayload); break;
                case "BW": Proc_6_111_74DF70(socketIndex, "BW", packetPayload); break;
                case "GI": StaffModerationPacketHandlers.sendCallForHelpChatLog(socketIndex, "GI", packetPayload); break;
                case "oj": Proc_6_219_7EA390(socketIndex, "oj", packetPayload); break;
                case "ok": Proc_6_220_7EBA50(socketIndex, "ok", packetPayload); break;
                case "ol": Proc_6_222_7ED710(socketIndex, "ol", packetPayload); break;
                case "on": Proc_6_221_7ED1E0(socketIndex, "on", packetPayload); break;
                case "GH": StaffModerationPacketHandlers.sendRoomChatLog(socketIndex, "GH", packetPayload); break;
                case "GK": StaffModerationPacketHandlers.sendRoomInfo(socketIndex, "GK", packetPayload); break;
                case "GF": Proc_6_0_6D7FF0(socketIndex, "GF", packetPayload); break;
                case "GJ": Proc_6_11_6DF4A0(socketIndex, "GJ", packetPayload); break;
                case "GM": Proc_6_1_6D8B70(socketIndex, "GM", packetPayload); break;
                case "GN": Proc_6_12_6DFE90(socketIndex, "GN", packetPayload); break;
                case "GO": Proc_6_2_6D9880(socketIndex, "GO", packetPayload); break;
                case "GP": Proc_6_3_6DA490(socketIndex, "GP", packetPayload); break;
                case "CH": Proc_6_4_6DAFB0(socketIndex, "CH", packetPayload); break;
                case "GB": Proc_6_6_6DC9D0(socketIndex, "GB", packetPayload); break;
                case "GC": Proc_6_8_6DD790(socketIndex, "GC", packetPayload); break;
                case "GD": Proc_6_7_6DD0E0(socketIndex, "GD", packetPayload); break;
                case "GL": Proc_6_9_6DDD70(socketIndex, "GL", packetPayload); break;
                case "Fw": Proc_6_115_751220(socketIndex, "Fw", packetPayload); break;
                case "Fn": Proc_6_116_751550(socketIndex, "Fn", packetPayload); break;
                case "Fr": Proc_6_121_752080(socketIndex, "Fr", packetPayload); break;
                case "Fq": Proc_6_117_751880(socketIndex, "Fq", packetPayload); break;
                case "Fp": Proc_6_118_751A80(socketIndex, "Fp", packetPayload); break;
                case "Fs": Proc_6_119_751C80(socketIndex, "Fs", packetPayload); break;
                case "Fo": Proc_6_126_755B40(socketIndex, "Fo", packetPayload); break;
                case "Ft": Proc_6_120_751E80(socketIndex, "Ft", packetPayload); break;
                case "E|": Proc_6_123_754020(socketIndex, "E|", packetPayload); break;
                case "Fu": Proc_6_127_755D30(socketIndex, "Fu", packetPayload); break;
                case "E~": Proc_6_124_754D90(socketIndex, "E~", packetPayload); break;
                case "EY": Proc_6_46_714D50(socketIndex, "EY", packetPayload); break;
                case "Gj": Proc_6_88_73E4F0(socketIndex, "Gj", packetPayload); break;
                case "@L": Proc_6_176_7C4EE0(socketIndex, "@L", packetPayload); break;
                case "@u":
                case "Ao": Proc_6_53_718E00(socketIndex, packetCode, packetPayload); break;
                case "@j": Proc_6_182_7CAAD0(socketIndex, "@j", packetPayload); break;
                case "@f": Proc_6_170_7C1100(socketIndex, "@f", packetPayload); break;
                case "DF": Proc_6_169_7C0DC0(socketIndex, "DF", packetPayload); break;
                case "@O": break;
                case "D}":
                case "D~": dispatchPreReadyRoomUserState(socketIndex); break;
                case "@a": Proc_6_173_7C3430(socketIndex, "@a", packetPayload); break;
                case "Ci": Proc_6_175_7C4800(socketIndex, "Ci", packetPayload); break;
                case "@b": Proc_6_168_7C05F0(socketIndex, "@b", packetPayload); break;
                case "@e": Proc_6_167_7BECA0(socketIndex, "@e", packetPayload); break;
                case "@i": Proc_6_172_7C25B0(socketIndex, "@i", packetPayload); break;
                case "@g": Proc_6_174_7C3BC0(socketIndex, "@g", packetPayload); break;
                case "@h": Proc_6_171_7C1520(socketIndex, "@h", packetPayload); break;
                case "Fy": Proc_6_189_7D0630(socketIndex, "Fy", packetPayload); break;
                case "Fx": Proc_6_188_7CF3C0(socketIndex, "Fx", packetPayload); break;
                case "Cg": Proc_6_190_7D11D0(socketIndex, "Cg", packetPayload); break;
                case "DG": Proc_6_191_7D18B0(socketIndex, "DG", packetPayload); break;
                case "B_": Proc_6_192_7D1B80(socketIndex, "B_", packetPayload); break;
                case "B]": Proc_6_193_7D2BB0(socketIndex, "B]", packetPayload); break;
                case "B^": Proc_6_194_7D3180(socketIndex, "B^", packetPayload); break;
                case "pg": break;
                case "AK": Proc_6_197_7D43C0(socketIndex, "AK", packetPayload); break;
                case "AO": Proc_6_198_7D4B70(socketIndex, "AO", packetPayload); break;
                case "AG": Proc_6_93_745D90(socketIndex, "AG", packetPayload); break;
                case "FT": Proc_6_140_769400(socketIndex, "FT", packetPayload); break;
                case "AB": Proc_6_139_768100(socketIndex, "AB", packetPayload); break;
                case "BA": Proc_6_137_766470(socketIndex, "BA", packetPayload); break;
                case "n\u007f": Proc_6_177_7C6580(socketIndex, "n\u007f", packetPayload); break;
                case "ny": Proc_6_183_7CABF0(socketIndex, "ny", packetPayload); break;
                case "nx": Proc_6_178_7C6E60(socketIndex, "nx", packetPayload); break;
                case "nz": Proc_6_179_7C7790(socketIndex, "nz", packetPayload); break;
                case "p`":
                case "rt": Proc_6_86_73B0D0(socketIndex, packetCode, packetPayload); break;
                case "n~": Proc_6_87_73C120(socketIndex, "n~", packetPayload); break;
                case "n|": Proc_7CC190(socketIndex, "n|", packetPayload); break;
                case "n{": Proc_7CA730(socketIndex, "n{", packetPayload); break;
                case "n}": Proc_6_186_7CD040(socketIndex, "n}", packetPayload); break;
                case "E[": Proc_6_45_714B60(socketIndex, "E[", packetPayload); break;
                case "A_": Proc_6_61_720490(socketIndex, "A_", packetPayload); break;
                case "E@": Proc_6_62_7209F0(socketIndex, "E@", packetPayload); break;
                case "DE": Proc_6_63_721050(socketIndex, "DE", packetPayload); break;
                case "D\u007f": Proc_6_64_721650(socketIndex, "D\u007f", packetPayload); break;
                case "EB": Proc_6_75_7269D0(socketIndex, "EB", packetPayload); break;
                case "Bw": Proc_6_73_725540(socketIndex, "Bw", packetPayload); break;
                case "Aa": Proc_6_74_7265B0(socketIndex, "Aa", packetPayload); break;
                case "B[": Proc_6_71_724CF0(socketIndex, "B[", packetPayload); break;
                case "@W": Proc_6_72_7250D0(socketIndex, "@W", packetPayload); break;
                case "Es": Proc_6_76_726CE0(socketIndex, "Es", packetPayload); break;
                case "FD": Proc_6_77_727590(socketIndex, "FD", packetPayload); break;
                case "A`": Proc_6_65_721A10(socketIndex, "A`", packetPayload); break;
                case "AT": Proc_6_66_721D60(socketIndex, "AT", packetPayload); break;
                case "AS": Proc_6_67_722940(socketIndex, "AS", packetPayload); break;
                case "AU": Proc_6_68_723170(socketIndex, "AU", packetPayload); break;
                case "A~":
                case "CW":
                case "Cf": break;
                case "FA": Proc_6_60_720060(socketIndex, "FA", packetPayload); break;
                case "FI": Proc_6_70_724190(socketIndex, "FI", packetPayload); break;
                case "AN": Proc_6_69_723630(socketIndex, "AN", packetPayload); break;
                case "Aq":
                case "AE":
                case "FS":
                case "AF":
                    Proc_6_244_801E80(socketIndex,
                        StringUtils.text(Functions.Proc_10_0_809570("com.client.park.infobus.theme.title", "AQ")) + '\2',
                        0);
                    break;
                case "oL": Proc_7F44D0(socketIndex, "oL", packetPayload); break;
                default:
                    if (Licence.runtimeState().debugLoggingEnabled()) {
                        Console.Proc_2_0_6D1510(packetPayload, "UNHANDLED -- index: " + socketIndex, "255");
                    }
                    break;
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    private static void dispatchPreReadySoundSetting(int socketIndex, String packetPayload) {
        try {
            long soundSetting = soundSettingFromWire(packetPayload);
            if (soundSetting <= 0L) {
                return;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            UserDao users = userDao();
            if (users != null) {
                users.updateSoundSetting(NumberUtils.parseLong(userId), soundSetting);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses dispatcher helper failures.
        }
    }

    private static void dispatchPreReadyCatalogIndex(int socketIndex) {
        try {
            String pageTree = Licence.catalogPages().defaultPageTree();
            Proc_6_244_801E80(socketIndex, "A~IHHM" + '\2' + pageTree, 0);
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
            Proc_6_247_8027E0(socketIndex, SocialPayloads.roomUserPreReadyState(roomUserIndex), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses dispatcher helper failures.
        }
    }

    public static String Proc_6_242_7FF0D0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
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

    public static void Proc_6_243_7FFEB0(Object... args) {
        int socketIndex = handlingSocketIndex(args);
        if (socketIndex <= 0) {
            return;
        }
        Proc_6_242_7FF0D0(socketIndex, 0, 0);
        Guardian.setSocketConnected(socketIndex, false);
        Guardian.removeSocketMarker(socketIndex);
        SocketMarkerSet socketMarkers = Licence.socketMarkers();
        socketMarkers.remove(socketIndex);
        Licence.setSocketMarkers(socketMarkers);
        GameServerSessionState sessionState = Licence.gameServerSessionState();
        sessionState.removeSocket(socketIndex);
        Licence.setGameServerSessionState(sessionState);
    }

    public static void Proc_6_244_801E80(Object... args) {
        if (args == null || args.length < 2) {
            return;
        }
        int socketIndex = (int) NumberUtils.parseLong(args[0]);
        if (socketIndex <= 0 || Guardian.Proc_11_2_821390(socketIndex, 0, 0) != 1
            || Licence.representedSockets().isBusy(socketIndex)) {
            return;
        }
        HandlingMUS.Proc_12_1_821AA0(socketIndex, StringUtils.text(args[1]) + '\1', 0);
    }

    public static long Proc_6_245_801FA0(Object... args) {
        if (args == null || args.length < 2) {
            return 0L;
        }
        int socketIndex = (int) NumberUtils.parseLong(args[0]);
        String userId = handlingUserIdFromSocket(socketIndex);
        long roomId = handlingCurrentRoomId(socketIndex, userId);
        return broadcastToRoomUsers(roomId, StringUtils.text(args[1]));
    }

    public static long Proc_6_246_8024C0(Object... args) {
        if (args == null || args.length < 2) {
            return 0L;
        }
        return broadcastToRoomUsers(NumberUtils.parseLong(args[0]), StringUtils.text(args[1]));
    }

    public static long Proc_6_247_8027E0(Object... args) {
        if (args == null || args.length < 2) {
            return 0L;
        }
        int socketIndex = handlingSocketIndex(args);
        String userId = handlingUserIdFromSocket(socketIndex);
        long roomId = handlingCurrentRoomId(socketIndex, userId);
        return broadcastToRoomUsers(roomId, StringUtils.text(args[1]));
    }

    public static long Proc_6_248_802B80(Object... args) {
        if (args == null || args.length < 2) {
            return 0L;
        }
        return broadcastToRoomUsers(NumberUtils.parseLong(args[0]), StringUtils.text(args[1]));
    }

    public static long Proc_6_249_802F10(Object... args) {
        if (args == null || args.length == 0) {
            return 0L;
        }
        return broadcastToStaffModerators(StringUtils.text(args[0]));
    }

    public static int handlingSocketIndex(Object... args) {
        return args != null && args.length >= 1 ? (int) NumberUtils.parseLong(args[0]) : 0;
    }

    public static String handlingPacketPayload(Object... args) {
        if (args == null) {
            return "";
        }
        String payload = args.length >= 3 ? StringUtils.text(args[2]) : "";
        if (payload.isEmpty() && args.length >= 2) {
            payload = StringUtils.text(args[1]);
        }
        return payload;
    }

    public static String handlingRequestPayload(Object[] args, String prefix) {
        String payload = handlingPacketPayload(args);
        String expectedPrefix = StringUtils.text(prefix);
        if (!expectedPrefix.isEmpty() && payload.startsWith(expectedPrefix)) {
            return payload.substring(expectedPrefix.length());
        }
        return payload;
    }

    public static String handlingUserIdFromSocket(int socketIndex) {
        if (socketIndex <= 0) {
            return "";
        }
        String recordPayload = Licence.getSessionRecordPayload("1:", String.valueOf(socketIndex));
        if (!recordPayload.isEmpty()) {
            String[] fields = recordPayload.split("\2", -1);
            String userId = String.valueOf(NumberUtils.parseLong(StringUtils.field(fields, 0)));
            if (!userId.isEmpty() && !"0".equals(userId)) {
                return userId;
            }
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
        long socketIndex = Licence.Proc_9_8_8086A0(idText, 0, 0);
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
        long roomId = Licence.Proc_9_10_808F30(String.valueOf(socketIndex), 1, 0);
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

    public static boolean handlingUserHasPermission(String userId, String permissionName) {
        long rankIndex = handlingUserRank(userId);
        long hcLevel = handlingUserHcLevel(userId);
        return Functions.Proc_10_1_809790(rankIndex, "", permissionName, hcLevel);
    }

    private static void roomKickOrBanUser(Object[] args, boolean addRoomBan) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingPacketPayload(args);
            if (requestPayload.startsWith("A_") || requestPayload.startsWith("E@")) {
                requestPayload = requestPayload.substring(2);
            }
            LongRef offset = new LongRef(1);
            String targetUserId = String.valueOf(readWireLong(requestPayload, offset));
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
            if (!handlingUserHasPermission(callerUserId, "fuse_kick")
                || handlingUserHasPermission(targetUserId, "fuse_unkickable")) {
                return;
            }
            Proc_6_244_801E80(targetSocketIndex, "@aXjO", 0);
            Proc_6_53_718E00(targetSocketIndex, "@aXjO", 0);
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

    public static long handlingUserRank(String userId) {
        UserDao users = userDao();
        if (users == null) {
            return 0L;
        }
        try {
            return users.rankLevel(NumberUtils.parseLong(userId));
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static long handlingUserHcLevel(String userId) {
        long hcLevel = 0L;
        UserDao users = userDao();
        if (users != null) {
            try {
                hcLevel = users.hcLevel(NumberUtils.parseLong(userId));
            } catch (Exception ignored) {
                hcLevel = 0L;
            }
        }
        if (hcLevel < 0L) {
            return 0L;
        }
        return Math.min(hcLevel, 2L);
    }

    public static String handlingUserSessionId(String userId) {
        if (StringUtils.text(userId).isEmpty() || "0".equals(StringUtils.text(userId))) {
            return "";
        }
        UserDao users = userDao();
        if (users == null) {
            return "";
        }
        try {
            return users.sessionId(NumberUtils.parseLong(userId));
        } catch (Exception ignored) {
            return "";
        }
    }

    public static boolean handlingUserOwnsRoom(String userId, long roomId) {
        if (StringUtils.text(userId).isEmpty() || "0".equals(StringUtils.text(userId)) || roomId <= 0L) {
            return false;
        }
        RoomDao rooms = roomDao();
        if (rooms == null) {
            return false;
        }
        try {
            return rooms.userOwnsRoom(NumberUtils.parseLong(userId), roomId);
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean handlingUserHasRoomRight(String userId, long roomId) {
        if (StringUtils.text(userId).isEmpty() || "0".equals(StringUtils.text(userId)) || roomId <= 0L) {
            return false;
        }
        RoomDao rooms = roomDao();
        if (rooms == null) {
            return false;
        }
        try {
            return rooms.userHasRoomRight(NumberUtils.parseLong(userId), roomId);
        } catch (Exception ignored) {
            return false;
        }
    }

    public static long roomCategoryForUser(long categoryId, String userId) {
        long rankIndex = handlingUserRank(userId);
        long hcLevel = handlingUserHcLevel(userId);
        RoomDao rooms = roomDao();
        if (rooms == null) {
            return 0L;
        }
        try {
            return rooms.visibleCategoryId(categoryId, rankIndex, hcLevel);
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static int handlingSocketIndexForUserName(String userName) {
        if (StringUtils.text(userName).isEmpty()) {
            return 0;
        }
        UserDao users = userDao();
        if (users == null) {
            return 0;
        }
        try {
            return (int) users.socketByName(userName);
        } catch (Exception ignored) {
            return 0;
        }
    }

    public static String staffModerationPayload(long rankIndex, long hcLevel) {
        return Licence.staffSettings().moderationPayload(rankIndex, hcLevel);
    }

    public static String indexedPayload(Object cache, long index) {
        int idx = (int) index;
        if (idx < 0) {
            return "";
        }
        if (cache instanceof String[]) {
            String[] values = (String[]) cache;
            return idx < values.length ? StringUtils.text(values[idx]) : "";
        }
        if (cache instanceof Object[]) {
            Object[] values = (Object[]) cache;
            return idx < values.length ? StringUtils.text(values[idx]) : "";
        }
        return "";
    }

    public static void ensureRepresentedRoomSlotPool() {
        RepresentedRoomSlots representedRoomSlots = Licence.representedRoomSlots();
        representedRoomSlots.ensureInitialized();
        Licence.setRepresentedRoomSlots(representedRoomSlots);
    }

    public static long reserveRepresentedRoomSlot(long preferredSlot) {
        RepresentedRoomSlots representedRoomSlots = Licence.representedRoomSlots();
        long slotId = representedRoomSlots.reserve(preferredSlot);
        Licence.setRepresentedRoomSlots(representedRoomSlots);
        return slotId;
    }

    public static void releaseRepresentedRoomSlot(long slotId) {
        RepresentedRoomSlots representedRoomSlots = Licence.representedRoomSlots();
        representedRoomSlots.release(slotId);
        Licence.setRepresentedRoomSlots(representedRoomSlots);
    }

    public static void loadRepresentedRoomBots(long roomSlot, long roomId) {
        if (roomSlot <= 0L || roomId <= 0L
            || NumberUtils.parseLong(Functions.Proc_10_0_809570("com.client.rooms.bots.enabled", "-1", 0)) == 0L) {
            return;
        }
        BotDao bots = botDao();
        if (bots == null) {
            return;
        }
        try {
            for (BotRoomEntryRow row : bots.roomBotEntries(roomId)) {
                allocateRepresentedBot(roomSlot, RepresentedBotEntry.from(row));
            }
        } catch (Exception ignored) {
            // VB6 source suppresses bot loading failures.
        }
    }

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
                    Proc_6_244_801E80(socketIndex, payload, 0);
                    sentMarkers += marker;
                    sentCount++;
                }
            }
            return sentCount;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static long broadcastToStaffModerators(String payload) {
        if (StringUtils.text(payload).isEmpty()) {
            return 0L;
        }
        String sentMarkers = "";
        long sentCount = 0L;
        for (SessionRegistry.SocketSession session : Licence.socketSessions()) {
            String candidateUserId = String.valueOf(session.userId());
            int candidateSocket = session.socketIndex();
            if ("0".equals(candidateUserId)) {
                candidateUserId = handlingUserIdFromSocket(candidateSocket);
            }
            String marker = "[" + candidateSocket + "]";
            if (candidateSocket > 0 && !sentMarkers.contains(marker) && handlingUserHasPermission(candidateUserId, "fuse_mod")) {
                Proc_6_244_801E80(candidateSocket, payload, 0);
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
                        && handlingUserHasPermission(candidateUserId, "fuse_mod")) {
                        Proc_6_244_801E80(candidateSocket, payload, 0);
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

    private static String handlingRepresentedChatRoute(Object[] args, long chatType) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return "";
            }
            String requestPayload = handlingPacketPayload(args);
            if (requestPayload.startsWith("@t") || requestPayload.startsWith("@w") || requestPayload.startsWith("@x")) {
                requestPayload = requestPayload.substring(2);
            }
            if (requestPayload.startsWith("H") || requestPayload.startsWith("I")) {
                requestPayload = requestPayload.substring(1);
            }

            String targetName = "";
            String messageText;
            LongRef offset = new LongRef(1);
            if (chatType == 2L) {
                targetName = readWireString(requestPayload, offset).trim();
                messageText = readWireString(requestPayload, offset);
                if (messageText.isEmpty()) {
                    messageText = requestPayload;
                    int spaceAt = messageText.indexOf(' ');
                    if (spaceAt >= 0) {
                        targetName = messageText.substring(0, spaceAt).trim();
                        messageText = messageText.substring(spaceAt + 1);
                    }
                }
            } else {
                messageText = readWireString(requestPayload, offset);
                if (messageText.isEmpty()) {
                    messageText = requestPayload;
                }
            }

            messageText = left(Functions.Proc_10_10_80A7F0(messageText, 0, 0).trim(), 122);
            if (messageText.isEmpty()) {
                return "";
            }
            if (chatType == 0L && messageText.startsWith(":")) {
                String commandPayload = legacyChatCommandPayload(messageText);
                if (commandPayload.isEmpty()) {
                    commandPayload = legacyDynamicChatCommandPayload(messageText);
                }
                if (!commandPayload.isEmpty()) {
                    Proc_6_244_801E80(socketIndex, commandPayload, 0);
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
            long userRank = handlingUserRank(userId);
            long hcLevel = handlingUserHcLevel(userId);
            if (!Proc_6_21_6E8BA0(messageText, 0, 0).isEmpty()
                && !Functions.Proc_10_1_809790(userRank, "", "fuse_can_chat_links", hcLevel)) {
                return "";
            }
            String filteredText = Proc_6_22_6E9300(messageText, 0, 0);
            if (filteredText.isEmpty()) {
                filteredText = messageText;
            }
            long gestureId = Proc_6_23_6E9A90(filteredText, 0, 0);
            RoomDao rooms = roomDao();
            if (rooms == null) {
                return "";
            }
            rooms.insertRoomChatLog(
                NumberUtils.parseLong(userId),
                roomId,
                filteredText,
                chatType,
                handlingUserSessionId(userId));
            String payload = UserPayloads.representedChat(roomUserIndex, filteredText, gestureId, chatType);
            if (chatType == 2L) {
                int targetSocketIndex = handlingSocketIndexForUserName(targetName);
                if (targetSocketIndex > 0) {
                    Proc_6_244_801E80(targetSocketIndex, payload, 0);
                    Proc_6_244_801E80(socketIndex, payload, 0);
                } else {
                    Proc_6_244_801E80(socketIndex, payload, 0);
                }
            } else {
                Proc_6_245_801FA0(socketIndex, payload, 0);
            }
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    public static String legacyDynamicChatCommandPayload(String messageText) {
        String command = StringUtils.text(messageText).trim().toLowerCase(Locale.ROOT);
        if (!":whosonline".equals(command)) {
            return "";
        }
        StringBuilder users = new StringBuilder();
        String seenSockets = "";
        for (SessionRegistry.SocketSession session : Licence.socketSessions()) {
            int socketIndex = session.socketIndex();
            String socketMarker = "[" + socketIndex + "]";
            if (socketIndex <= 0 || seenSockets.contains(socketMarker)) {
                continue;
            }
            String userName = handlingUserName(String.valueOf(session.userId()));
            if (userName.isEmpty()) {
                continue;
            }
            if (users.length() > 0) {
                users.append(", ");
            }
            users.append(userName);
            seenSockets += socketMarker;
        }
        return legacyActiveUsersPayload(users.toString());
    }

    private static void staffDirectMessage(
        Object[] args,
        String prefix,
        String requiredPermission,
        String logType,
        boolean kickAfterSend,
        boolean requireOnlineTarget
    ) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, prefix);
            LongRef offset = new LongRef(1);
            long targetUserId = readWireLong(requestPayload, offset);
            if (targetUserId <= 0L) {
                targetUserId = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            }
            String messageText = readWireString(requestPayload, offset);
            if (messageText.isEmpty()) {
                messageText = Functions.Proc_10_7_80A190(requestPayload, 0, 0);
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (targetUserId <= 0L || messageText.isEmpty()
                || callerUserId.isEmpty() || "0".equals(callerUserId)
                || !handlingUserHasPermission(callerUserId, "fuse_mod")
                || !handlingUserHasPermission(callerUserId, requiredPermission)
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
                Proc_6_244_801E80(targetSocketIndex, StaffPayloads.alert(messageText), 0);
                if (kickAfterSend) {
                    Proc_6_53_718E00(targetSocketIndex, 0, 0);
                }
            }
            if ("4".equals(logType)) {
                moderationDao.insertUserCaution(targetUserId, callerUserIdValue, messageText);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    private static void updateCallForHelpTab(Object[] args, String prefix, String tabId) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, prefix);
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)
                || !handlingUserHasPermission(callerUserId, "fuse_mod")
                || !handlingUserHasPermission(callerUserId, "fuse_receive_calls_for_help")) {
                return;
            }
            List<Long> callForHelpIds = StaffPayloads.callForHelpIds(requestPayload);
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

    private static void staffRoomHistory(Object[] args, String prefix, boolean includeChatRows) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, prefix);
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId) || !handlingUserHasPermission(callerUserId, "fuse_mod")) {
                return;
            }
            if (includeChatRows && !handlingUserHasPermission(callerUserId, "fuse_chatlog")) {
                return;
            }
            long targetUserId = includeChatRows ? staffNestedUserIdFromWire(requestPayload)
                : NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
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
            long rowCount;
            if (includeChatRows) {
                List<StaffRoomChatVisitRow> visitRows = moderationDao.recentChatHistoryVisits(targetUserId);
                rowCount = visitRows.size();
                StringBuilder rowPayload = new StringBuilder();
                for (StaffRoomChatVisitRow row : visitRows) {
                    List<StaffRoomChatRow> chatRows = moderationDao.chatRowsForVisit(
                        row.roomId(),
                        targetUserId,
                        row.timestampEnter(),
                        row.timestampLeft());
                    rowPayload.append(StaffPayloads.roomChatHistory(row, chatRows));
                }
                String responsePayload = StaffPayloads.roomChatHistoryResponse(
                    targetUser,
                    rowCount,
                    rowPayload.toString());
                Proc_6_244_801E80(socketIndex, responsePayload, 0);
            } else {
                List<StaffRoomVisitRow> visitRows = moderationDao.recentRoomVisits(targetUserId);
                rowCount = visitRows.size();
                StringBuilder rowPayload = new StringBuilder();
                for (StaffRoomVisitRow row : visitRows) {
                    rowPayload.append(StaffPayloads.roomVisit(row));
                }
                String responsePayload = StaffPayloads.roomVisitHistoryResponse(
                    targetUser,
                    rowCount,
                    rowPayload.toString());
                Proc_6_244_801E80(socketIndex, responsePayload, 0);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static long avatarNameValidationCode(String candidateName, String currentName, long existingCount) {
        String candidate = StringUtils.text(candidateName).trim();
        if (candidate.length() < 3) {
            return 2L;
        }
        if (candidate.length() > 14) {
            return 1L;
        }
        String upper = candidate.toUpperCase();
        if (upper.startsWith("MOD-") || upper.startsWith("VIP-")) {
            return 2L;
        }
        for (int index = 0; index < candidate.length(); index++) {
            char ch = candidate.charAt(index);
            boolean allowed = (ch >= 'A' && ch <= 'Z')
                || (ch >= 'a' && ch <= 'z')
                || (ch >= '0' && ch <= '9')
                || ch == '-'
                || ch == '_';
            if (!allowed) {
                return 2L;
            }
        }
        if (candidate.equalsIgnoreCase(StringUtils.text(currentName))) {
            return 0L;
        }
        return existingCount > 0L ? 3L : 0L;
    }

    public static long handlingMovementField(String movementText, long fieldIndex) {
        String[] fields = StringUtils.text(movementText).split("\0", -1);
        return fieldIndex >= 0 && fieldIndex < fields.length ? NumberUtils.parseLong(fields[(int) fieldIndex]) : 0L;
    }

    public static long handlingDirectionCode(long deltaX, long deltaY) {
        return Functions.movementDirectionCode(deltaX, deltaY);
    }

    public static String representedRoomRecord(String roomCacheText, long roomSlot) {
        return RepresentedRoomCache.fromLegacy(roomCacheText).record(roomSlot);
    }

    public static String representedRoomRecordSet(String roomCacheText, long roomSlot, String roomRecord) {
        return RepresentedRoomCache.fromLegacy(roomCacheText).setRecord(roomSlot, roomRecord).cacheText();
    }

    public static MovementPosition representedMovementPosition(String roomCacheText, long roomSlot, long entityIndex) {
        MovementPosition result = new MovementPosition();
        RepresentedRoomCache.Position position = RepresentedRoomCache.fromLegacy(roomCacheText).movementPosition(roomSlot, entityIndex);
        result.positionX = position.positionX;
        result.positionY = position.positionY;
        result.found = position.found;
        return result;
    }

    private static MovementPosition movementPosition(RepresentedRoomCache.Position position) {
        MovementPosition result = new MovementPosition();
        result.positionX = position.positionX;
        result.positionY = position.positionY;
        result.found = position.found;
        return result;
    }

    public static MovementPosition representedUserPosition(Object[] args) {
        MovementPosition result = new MovementPosition();
        if (args != null && args.length >= 5) {
            result.positionX = NumberUtils.parseLong(args[3]);
            result.positionY = NumberUtils.parseLong(args[4]);
            result.found = true;
        }
        return result;
    }

    public static String representedRoomOccupantMove(
        String roomCacheText,
        long roomSlot,
        long entityIndex,
        long positionX,
        long positionY,
        long directionValue,
        long movingValue
    ) {
        return RepresentedRoomCache.fromLegacy(roomCacheText)
            .moveOccupant(roomSlot, entityIndex, positionX, positionY, directionValue, movingValue)
            .cacheText();
    }

    public static String Proc_6_239_7FC170(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        return readFile(StringUtils.text(args[0]));
    }

    public static void Proc_6_240_7FC2B0(Object... args) {
        if (args == null || args.length < 2) {
            return;
        }
        writeFile(StringUtils.text(args[0]), StringUtils.text(args[1]));
    }

    public static String readFile(String filePath) {
        if (StringUtils.text(filePath).isEmpty()) {
            return "";
        }
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            return "";
        }
        try {
            byte[] bytes = Files.readAllBytes(path);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            return "";
        }
    }

    public static void writeFile(String filePath, String fileText) {
        if (StringUtils.text(filePath).isEmpty()) {
            return;
        }
        try {
            Path path = Path.of(filePath);
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(path, (StringUtils.text(fileText) + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
        } catch (IOException ignored) {
            // VB6 source suppresses write failures.
        }
    }

    public static String removeRepresentedLineRecord(String cacheText, String markerText) {
        String cache = StringUtils.text(cacheText);
        String marker = StringUtils.text(markerText);
        if (cache.isEmpty() || marker.isEmpty()) {
            return cache;
        }
        cache = cache.replace("\r", "");
        StringBuilder rebuilt = new StringBuilder();
        for (String rowText : cache.split("\n", -1)) {
            if (!rowText.isEmpty() && !rowText.contains(marker)) {
                if (rebuilt.length() > 0) {
                    rebuilt.append('\n');
                }
                rebuilt.append(rowText);
            }
        }
        return rebuilt.toString();
    }

    public static String handlingEnsureRoomCacheFile(String cachePath) {
        if (StringUtils.text(cachePath).isEmpty()) {
            return "";
        }
        Path path = Path.of(cachePath);
        if (!Files.exists(path)) {
            DataManager.Proc_8_10_8068E0(cachePath, "");
        }
        return readFile(cachePath);
    }

    public static String removeRepresentedCacheRecord(String cacheText, String markerText) {
        return RepresentedRoomCache.removeRecord(cacheText, markerText);
    }

    public static String readWireString(String packetPayload, LongRef offset) {
        if (offset == null) {
            return "";
        }
        String payload = StringUtils.text(packetPayload);
        if (offset.value < 1L) {
            offset.value = 1L;
        }
        if (offset.value + 1L > payload.length()) {
            return "";
        }
        int start = (int) offset.value - 1;
        long fieldLength = Crypto.Proc_3_4_6D3620(payload.substring(start));
        if (fieldLength <= 0L) {
            return "";
        }
        int valueStart = start + 2;
        int valueEnd = Math.min(payload.length(), valueStart + (int) fieldLength);
        if (valueEnd - valueStart < fieldLength) {
            return "";
        }
        offset.value = offset.value + 2L + fieldLength;
        return payload.substring(valueStart, valueEnd);
    }

    public static long readWireLong(String packetPayload, LongRef offset) {
        if (offset == null) {
            return 0L;
        }
        String payload = StringUtils.text(packetPayload);
        if (offset.value < 1L) {
            offset.value = 1L;
        }
        if (offset.value > payload.length()) {
            return 0L;
        }
        String remainingPayload = payload.substring((int) offset.value - 1);
        long encodedLengthSize = wireLongFieldLength(remainingPayload);
        if (encodedLengthSize <= 0L) {
            return 0L;
        }
        long value = Crypto.Proc_3_3_6D3240(remainingPayload);
        offset.value += encodedLengthSize;
        return value;
    }

    private static long wireLongFieldLength(String encodedValue) {
        String value = StringUtils.text(encodedValue);
        if (value.isEmpty()) {
            return 0L;
        }
        long firstByte = value.charAt(0) - 64L;
        long tailLength = (firstByte & 0x38L) / 8L;
        if (tailLength <= 0L) {
            return 1L;
        }
        return Math.min(value.length(), tailLength + 1L);
    }

    public static boolean stickyNoteUpdateFromWire(String packetPayload, StickyNoteUpdate update) {
        if (update == null) {
            return false;
        }
        String payload = StringUtils.text(packetPayload);
        String idText = Functions.Proc_10_6_809F10(payload);
        long furnitureId = NumberUtils.parseLong(idText);
        String notePayload = "";
        if (furnitureId <= 0L) {
            LongRef offset = new LongRef(1);
            furnitureId = readWireLong(payload, offset);
            notePayload = StringUtils.mid(payload, (int) offset.value);
        } else {
            long idLengthSize = Crypto.Proc_3_2_6D30A0(payload);
            if (idLengthSize > 0L) {
                notePayload = StringUtils.mid(payload, (int) idLengthSize + idText.length() + 1);
            }
        }
        if (notePayload.isEmpty()) {
            notePayload = Functions.Proc_10_7_80A190(payload);
        }
        if (notePayload.isEmpty()) {
            return false;
        }
        if (notePayload.length() > 510) {
            notePayload = notePayload.substring(0, 510);
        }

        int separatorAt = firstPositiveIndex(notePayload, '\r', '\n', '\2');
        String noteColor;
        String noteCaption;
        if (separatorAt >= 0) {
            noteColor = notePayload.substring(0, separatorAt).toUpperCase();
            noteCaption = notePayload.substring(separatorAt + 1);
        } else {
            noteColor = notePayload.substring(0, Math.min(6, notePayload.length())).toUpperCase();
            noteCaption = notePayload.length() > 6 ? notePayload.substring(6) : "";
        }
        if (noteColor.length() > 6) {
            noteColor = noteColor.substring(0, 6);
        }
        if (!isStickyNoteColor(noteColor)) {
            return false;
        }
        if (noteCaption.length() > 510) {
            noteCaption = noteCaption.substring(0, 510);
        }
        noteCaption = noteCaption.replace('\u00a0', '\u001f').replace('\r', '\u001f').replace('\n', '\u001f');
        update.furnitureId = furnitureId;
        update.noteColor = noteColor;
        update.noteCaption = noteCaption;
        return true;
    }

    public static boolean isStickyNoteColor(String noteColor) {
        String color = StringUtils.text(noteColor).toUpperCase();
        return "9CFF9C".equals(color) || "FFFF33".equals(color) || "FF9CFF".equals(color) || "9CCEFF".equals(color);
    }

    public static long stickyFurnitureIdFromPayload(String requestPayload) {
        long furnitureId = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
        if (furnitureId <= 0L) {
            furnitureId = readWireLong(requestPayload, new LongRef(1));
        }
        return furnitureId;
    }

    public static String handlingSimpleFloorItemUse(Object[] args, String packetPrefix, long stateValue, boolean storeState) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, packetPrefix);
            String userId = handlingUserIdFromSocket(socketIndex);
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (userId.isEmpty() || "0".equals(userId) || roomId <= 0L) {
                return "";
            }
            long furnitureId = idRequestFromWire(requestPayload, "");
            if (furnitureId <= 0L) {
                return "";
            }
            FurnitureDao.SimpleFloorFurniture item = furnitureDao().simpleFloorFurniture(furnitureId, roomId).orElse(null);
            if (item == null) {
                return "";
            }
            long furnitureX = item.positionX();
            long furnitureY = item.positionY();
            long productId = item.productId();
            if (productId <= 0L || NumberUtils.parseLong(DataManager.Proc_8_12_806C30(productId, 0, 0)) != 0L) {
                return "";
            }
            RoomDao rooms = roomDao();
            if (rooms == null) {
                return "";
            }
            long roomSlot = rooms.roomSlot(roomId);
            MovementPosition userPosition = representedUserPosition(args);
            if (!userPosition.found) {
                userPosition = movementPosition(
                    Licence.representedRooms().movementPosition(roomSlot, representedRoomUserIndex(socketIndex, userId)));
            }
            if (userPosition.found
                && (Math.abs(userPosition.positionX - furnitureX) > 2L || Math.abs(userPosition.positionY - furnitureY) > 2L)) {
                return "";
            }
            String payload = FurniturePayloads.simpleFloorUse(furnitureId, stateValue);
            Proc_6_247_8027E0(socketIndex, payload, 0);
            if (storeState) {
                Proc_6_151_78AC20(roomId, furnitureId, stateValue);
            } else {
                Proc_6_145_76CA20(socketIndex, roomId, furnitureId);
            }
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String handlingFloorFurnitureMove(Object[] args, String packetPrefix, boolean fromInventory) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, packetPrefix);
            FloorFurniturePlacement placement = floorFurniturePlacementFromPayload(requestPayload);
            if (placement.furnitureId <= 0L) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L || (!handlingUserHasRoomRight(userId, roomId)
                && !handlingUserHasPermission(userId, "fuse_pick_up_any_furni"))) {
                return "";
            }
            long userIdValue = NumberUtils.parseLong(userId);
            FurnitureDao furniture = furnitureDao();
            FurnitureDao.InventoryPlacementFurniture item = fromInventory
                ? furniture.inventoryPlacementFurniture(placement.furnitureId, userIdValue).orElse(null)
                : furniture.roomPlacementFurniture(placement.furnitureId, roomId).orElse(null);
            if (item == null) {
                return "";
            }
            long productId = item.productId();
            String itemData = StringUtils.text(item.sign());
            long secondaryValue = item.secondaryValue();
            if (productId <= 0L) {
                return "";
            }
            CatalogRegistry.Product product = Licence.product(productId);
            if (product == null) {
                return "";
            }
            long productType = product.type();
            if (productType == 9L) {
                if (fromInventory) {
                    Proc_6_157_7974B0(socketIndex, requestPayload, item);
                }
                return "";
            }
            String positionZ = String.valueOf(product.squareZ());
            if (fromInventory) {
                furniture.placeFloorFurniture(
                    placement.furnitureId,
                    userIdValue,
                    roomId,
                    placement.positionX,
                    placement.positionY,
                    positionZ,
                    placement.rotation);
                Proc_6_244_801E80(socketIndex, InventoryMessagePayloads.remove(placement.furnitureId), 0);
            } else {
                furniture.moveFloorFurniture(
                    placement.furnitureId,
                    roomId,
                    userIdValue,
                    placement.positionX,
                    placement.positionY,
                    positionZ,
                    placement.rotation);
            }
            String placementPayload = Proc_6_161_7B2EE0(
                placement.furnitureId, placement.positionX, placement.positionY, placement.rotation,
                NumberUtils.parseLong(positionZ), "", itemData, secondaryValue, productId);
            String payload = (fromInventory ? "A]" : "A_") + placementPayload;
            if (!placementPayload.isEmpty()) {
                Proc_6_247_8027E0(socketIndex, payload, 0);
            }
            Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "ROOMS", roomId + ".cache").toString(), 0, 0);
            Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "PATHFINDER", roomId + ".cache").toString(), 0, 0);
            if (fromInventory) {
                Proc_6_140_769400(socketIndex, "FT", "");
            }
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static FloorFurniturePlacement floorFurniturePlacementFromPayload(String packetPayload) {
        FloorFurniturePlacement placement = new FloorFurniturePlacement();
        String normalizedPayload = StringUtils.text(packetPayload)
            .replace('\1', ' ')
            .replace('\2', ' ')
            .replace('\t', ' ')
            .replace('\r', ' ')
            .replace('\n', ' ')
            .trim();
        while (normalizedPayload.contains("  ")) {
            normalizedPayload = normalizedPayload.replace("  ", " ");
        }
        if (!normalizedPayload.isEmpty()) {
            String[] tokens = normalizedPayload.split(" ", -1);
            if (tokens.length >= 1) {
                placement.furnitureId = NumberUtils.parseLong(tokens[0]);
            }
            if (tokens.length >= 2) {
                placement.positionX = NumberUtils.parseLong(tokens[1]);
            }
            if (tokens.length >= 3) {
                placement.positionY = NumberUtils.parseLong(tokens[2]);
            }
            if (tokens.length >= 4) {
                placement.rotation = NumberUtils.parseLong(tokens[3]);
            }
        }
        if (placement.furnitureId <= 0L) {
            placement.furnitureId = readWireLong(StringUtils.text(packetPayload), new LongRef(1));
        }
        return placement;
    }

    public static FurnitureDao.InventoryPlacementFurniture wallPlacementFurnitureArg(Object itemArg) {
        return FurnitureDao.InventoryPlacementFurniture.fromLegacyArg(itemArg);
    }

    public static boolean isPostItProduct(long productId) {
        return DataManager.Proc_8_12_806C30(productId, 18, 0).toLowerCase().startsWith("post.it");
    }

    public static long representedDimmerFurnitureId(long roomId) {
        if (roomId <= 0L) {
            return 0L;
        }
        FurnitureDao furniture = furnitureDao();
        if (furniture == null) {
            return 0L;
        }
        try {
            return furniture.dimmerFurnitureId(roomId);
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static boolean isDimmerColour(String colourText) {
        String color = StringUtils.text(colourText).toUpperCase();
        return "#0053F7".equals(color)
            || "#74F5F5".equals(color)
            || "#E759DE".equals(color)
            || "#EA4532".equals(color)
            || "#F2F851".equals(color)
            || "#82F349".equals(color)
            || "#000000".equals(color);
    }

    public static boolean wallPlacementFromPayload(String packetPayload, WallPlacement placement) {
        if (placement == null) {
            return false;
        }
        String normalizedPayload = StringUtils.text(packetPayload)
            .replace('\1', ' ')
            .replace('\2', ' ')
            .replace('\t', ' ')
            .replace('\r', ' ')
            .replace('\n', ' ');
        while (normalizedPayload.contains("  ")) {
            normalizedPayload = normalizedPayload.replace("  ", " ");
        }
        normalizedPayload = normalizedPayload.trim();
        String lower = normalizedPayload.toLowerCase();
        int wallAt = lower.indexOf(":w=");
        int localAt = lower.indexOf("l=");
        if (wallAt < 0 || localAt <= wallAt) {
            return false;
        }
        String wallText = normalizedPayload.substring(wallAt + 3, localAt).trim().replace(" ", "");
        String localText = normalizedPayload.substring(localAt + 2).trim().replace(" ", "");
        String[] wallParts = wallText.split(",", -1);
        String[] localParts = localText.split(",", -1);
        if (wallParts.length < 2 || localParts.length < 2) {
            return false;
        }
        placement.wallX = NumberUtils.parseLong(wallParts[0]);
        placement.wallY = NumberUtils.parseLong(wallParts[1]);
        placement.localX = NumberUtils.parseLong(localParts[0]);
        placement.localY = NumberUtils.parseLong(localParts[1]);
        return true;
    }

    public static String roomIconPayloadFromWire(String packetPayload) {
        LongRef offset = new LongRef(1);
        long previousOffset = offset.value;
        long backgroundId = readWireLong(packetPayload, offset);
        if (offset.value <= previousOffset || backgroundId < 0L || backgroundId > 24L) {
            return "";
        }

        previousOffset = offset.value;
        long foregroundId = readWireLong(packetPayload, offset);
        if (offset.value <= previousOffset || foregroundId < 0L || foregroundId > 11L) {
            return "";
        }

        previousOffset = offset.value;
        long itemCount = readWireLong(packetPayload, offset);
        if (offset.value <= previousOffset || itemCount < 0L || itemCount > 12L) {
            return "";
        }

        List<RoomPayloads.RoomIconItem> items = new ArrayList<>();
        for (long itemIndex = 1L; itemIndex <= itemCount; itemIndex++) {
            previousOffset = offset.value;
            long itemType = readWireLong(packetPayload, offset);
            if (offset.value <= previousOffset || itemType < 0L) {
                return "";
            }

            previousOffset = offset.value;
            long itemPosition = readWireLong(packetPayload, offset);
            if (offset.value <= previousOffset || itemPosition < 0L) {
                return "";
            }

            items.add(new RoomPayloads.RoomIconItem(itemType, itemPosition));
        }
        return RoomPayloads.icon(backgroundId, foregroundId, items);
    }

    public static boolean roomEventCreatePayloadFromWire(String packetPayload, RoomEventPayload result) {
        if (result == null) {
            return false;
        }
        LongRef offset = new LongRef(1);
        long categoryId = readWireLong(packetPayload, offset);
        if (categoryId < 1L) {
            return false;
        }
        String categoryName = DataManager.Proc_8_11_8069B0(categoryId, 0);
        if (categoryName.isEmpty()) {
            return false;
        }
        result.categoryId = categoryId;
        result.categoryName = categoryName;
        return readRoomEventCommon(packetPayload, offset, result, true);
    }

    public static boolean roomEventEditPayloadFromWire(String packetPayload, RoomEventPayload result) {
        if (result == null) {
            return false;
        }
        LongRef offset = new LongRef(1);
        return readRoomEventCommon(packetPayload, offset, result, true);
    }

    public static boolean roomSettingsFromWire(String packetPayload, RoomSettingsPayload result) {
        if (result == null) {
            return false;
        }
        LongRef offset = new LongRef(1);
        String roomName = Functions.Proc_10_10_80A7F0(readWireString(packetPayload, offset));
        if (roomName.length() < 3) {
            return false;
        }
        result.roomName = left(roomName, 60);
        result.roomPassword = left(Functions.Proc_10_10_80A7F0(readWireString(packetPayload, offset)), 60);
        result.doorStatus = readWireLong(packetPayload, offset);
        if (result.doorStatus < 0L || result.doorStatus > 2L) {
            return false;
        }
        result.roomDescription = left(Functions.Proc_10_10_80A7F0(readWireString(packetPayload, offset)), 255);
        result.visitorsMax = readWireLong(packetPayload, offset);
        if (result.visitorsMax < 1L) {
            result.visitorsMax = 1L;
        }
        if (result.visitorsMax > 250L) {
            result.visitorsMax = 250L;
        }
        result.categoryId = readWireLong(packetPayload, offset);
        if (result.categoryId <= 0L) {
            return false;
        }
        long tagCount = readWireLong(packetPayload, offset);
        if (tagCount < 0L || tagCount > 2L) {
            return false;
        }
        for (long tagIndex = 1L; tagIndex <= tagCount; tagIndex++) {
            String tagText = left(Functions.Proc_10_10_80A7F0(readWireString(packetPayload, offset)), 60).toLowerCase();
            if (tagIndex == 1L) {
                result.tagOne = tagText;
            } else if (tagIndex == 2L) {
                result.tagTwo = tagText;
            }
        }

        result.allowOthersPets = optionalWireLong(packetPayload, offset, 0L);
        result.allowFeedPets = optionalWireLong(packetPayload, offset, 0L);
        result.allowWalkthrough = optionalWireLong(packetPayload, offset, 0L);
        result.disableWalls = optionalWireLong(packetPayload, offset, 0L);
        result.thicknessFloor = optionalWireLong(packetPayload, offset, 0L);
        result.thicknessWallpaper = optionalWireLong(packetPayload, offset, 0L);

        result.allowOthersPets = roomSettingsFlag(result.allowOthersPets);
        result.allowFeedPets = roomSettingsFlag(result.allowFeedPets);
        result.allowWalkthrough = roomSettingsFlag(result.allowWalkthrough);
        result.disableWalls = roomSettingsFlag(result.disableWalls);
        result.thicknessFloor = roomSettingsThickness(result.thicknessFloor);
        result.thicknessWallpaper = roomSettingsThickness(result.thicknessWallpaper);
        return true;
    }

    public static long roomSettingsFlag(long flagValue) {
        return flagValue != 0L ? 1L : 0L;
    }

    public static long roomSettingsThickness(long thicknessValue) {
        if (thicknessValue < -2L) {
            return -2L;
        }
        if (thicknessValue > 1L) {
            return 1L;
        }
        return thicknessValue;
    }

    public static String nullableSqlText(String valueText) {
        return StringUtils.text(valueText).isEmpty() ? "null" : "'" + Functions.Proc_10_11_80A9C0(valueText) + "'";
    }

    public static long navigatorListLimit() {
        long limit = NumberUtils.parseLong(Functions.Proc_10_0_809570("com.client.navigator.list.limit", 50));
        return limit <= 0L ? 50L : limit;
    }

    public static String navigatorSearchTerm(String rawText) {
        return Functions.Proc_10_11_80A9C0(rawText).replace("%", "");
    }

    public static long navigatorCategoryIdFromPacket(Object[] args, String packetPrefix) {
        String requestPayload = handlingRequestPayload(args, packetPrefix);
        long categoryId = NumberUtils.parseLong(Functions.Proc_10_7_80A190(requestPayload, 0, 0));
        if (categoryId <= 0L) {
            categoryId = readWireLong(requestPayload, new LongRef(1));
        }
        return categoryId;
    }

    public static String navigatorTextFromPacket(Object[] args) {
        String requestPayload = handlingPacketPayload(args);
        if (requestPayload.length() >= 3) {
            requestPayload = requestPayload.substring(2);
        }
        if (requestPayload.startsWith("@")) {
            String value = readWireString(requestPayload, new LongRef(1));
            if (!value.isEmpty()) {
                return value;
            }
        }
        return requestPayload;
    }

    public static String recommendedRoomPayload(long treeIndex) {
        try {
            return Licence.recommendedRooms().payload(treeIndex);
        } catch (Exception ignored) {
            return "";
        }
    }

    public static String officialNavigatorQuery() {
        String separator = " UNION ALL ";
        StringBuilder queryText = new StringBuilder();
        queryText.append("SELECT rooms_official.id_type,rooms_official.id_style,rooms_official.icon,");
        queryText.append("rooms_official.caption,rooms_official.caption_2,rooms_official.caption_3,");
        queryText.append("NULL,rooms.id,rooms.name,users.name,rooms.status_door,rooms.visitors_now,");
        queryText.append("rooms.visitors_max,rooms.description,rooms_categories.has_trading,NULL,");
        queryText.append("rooms.rate,rooms.id_category,rooms.icon,rooms.tag_1,rooms.tag_2,");
        queryText.append("rooms.allow_otherspets,NULL,NULL,NULL,rooms_official.id_parent,");
        queryText.append("rooms_official.id,rooms_official.requires_level_in FROM users,rooms,");
        queryText.append("rooms_categories,rooms_official WHERE rooms_official.id_type='2' ");
        queryText.append("AND rooms_official.id_room IS NOT NULL AND rooms.id=rooms_official.id_room ");
        queryText.append("AND users.id=rooms.id_owner AND rooms_categories.id=rooms.id_category ");
        queryText.append("GROUP BY rooms_official.id");

        queryText.append(separator).append("SELECT rooms_official.id_type,rooms_official.id_style,");
        queryText.append("rooms_official.icon,rooms_official.caption,rooms_official.caption_2,");
        queryText.append("rooms_official.caption_3,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,");
        queryText.append("NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,rooms_official.id_parent,");
        queryText.append("rooms_official.id,rooms_official.requires_level_in FROM rooms_official ");
        queryText.append("WHERE rooms_official.id_type='1' GROUP BY rooms_official.id");

        queryText.append(separator).append("SELECT rooms_official.id_type,rooms_official.id_style,");
        queryText.append("rooms_official.icon,rooms_official.caption,rooms_official.caption_2,");
        queryText.append("rooms_official.caption_3,NULL,rooms.id,rooms.name,NULL,rooms.status_door,");
        queryText.append("rooms.visitors_now,rooms.visitors_max,rooms.description,");
        queryText.append("rooms_categories.has_trading,NULL,rooms.rate,rooms.id_category,rooms.icon,");
        queryText.append("rooms.tag_1,rooms.tag_2,rooms.allow_otherspets,models.name,");
        queryText.append("models.required_files,models.visitors_max,rooms_official.id_parent,");
        queryText.append("rooms_official.id,rooms_official.requires_level_in FROM models,rooms,");
        queryText.append("rooms_categories,rooms_official WHERE rooms_official.id_type='3' ");
        queryText.append("AND rooms_official.id_room IS NOT NULL AND rooms.id=rooms_official.id_room ");
        queryText.append("AND models.id=rooms.id_model AND rooms_categories.id=rooms.id_category ");
        queryText.append("GROUP BY rooms_official.id");

        queryText.append(separator).append("SELECT rooms_official.id_type,rooms_official.id_style,");
        queryText.append("rooms_official.icon,rooms_official.caption,rooms_official.caption_2,");
        queryText.append("rooms_official.caption_3,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,");
        queryText.append("NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,rooms_official.id_parent,");
        queryText.append("rooms_official.id,rooms_official.requires_level_in FROM rooms_official ");
        queryText.append("WHERE rooms_official.id_type='4' GROUP BY rooms_official.id ");
        queryText.append("ORDER BY 27 ASC LIMIT 255");
        return queryText.toString();
    }

    public static String Proc_6_112_74E0C0(Object... args) {
        try {
            if (args == null || args.length == 0) {
                return NavigatorPayloads.roomList(List.of());
            }
            String queryTail = StringUtils.text(args[0]);
            if (queryTail.isEmpty()) {
                return NavigatorPayloads.roomList(List.of());
            }
            RoomDao rooms = roomDao();
            return NavigatorPayloads.roomList(rooms == null ? List.of() : rooms.navigatorRoomsByTail(queryTail, true));
        } catch (Exception ignored) {
            return NavigatorPayloads.roomList(List.of());
        }
    }

    public static String Proc_6_138_7678A0(Object... args) {
        if (args == null || args.length < 2) {
            return "";
        }
        return InventoryMessagePayloads.item(NumberUtils.parseLong(args[0]), NumberUtils.parseLong(args[1]),
            args.length >= 3 ? StringUtils.text(args[2]) : "",
            args.length >= 4 ? NumberUtils.parseLong(args[3]) : 0L);
    }

    public static List<RepresentedTradeOffer> representedTradeOfferStore(
        List<RepresentedTradeOffer> tradeOffers,
        long socketIndex,
        long furnitureId,
        long productId,
        String signText,
        long secondaryValue
    ) {
        if (socketIndex <= 0L || furnitureId <= 0L || productId <= 0L) {
            return tradeOffers == null ? List.of() : new ArrayList<>(tradeOffers);
        }
        RepresentedTradeOffer newOffer = RepresentedTradeOffer.stored(
            socketIndex,
            furnitureId,
            productId,
            signText,
            secondaryValue);
        List<RepresentedTradeOffer> rebuilt = new ArrayList<>();
        boolean replacedExisting = false;
        for (RepresentedTradeOffer offer : tradeOffers == null ? List.<RepresentedTradeOffer>of() : tradeOffers) {
            if (offer.socketIndex() == socketIndex && offer.furnitureId() == furnitureId) {
                rebuilt.add(newOffer);
                replacedExisting = true;
            } else {
                rebuilt.add(offer);
            }
        }
        if (!replacedExisting) {
            rebuilt.add(newOffer);
        }
        return rebuilt;
    }

    public static List<RepresentedTradeOffer> representedTradeOfferRemove(
        List<RepresentedTradeOffer> tradeOffers,
        long socketIndex,
        long furnitureId
    ) {
        if (socketIndex <= 0L || tradeOffers == null || tradeOffers.isEmpty()) {
            return tradeOffers == null ? List.of() : new ArrayList<>(tradeOffers);
        }
        List<RepresentedTradeOffer> rebuilt = new ArrayList<>();
        for (RepresentedTradeOffer offer : tradeOffers) {
            if (offer.socketIndex() != socketIndex || (furnitureId > 0L && offer.furnitureId() != furnitureId)) {
                rebuilt.add(offer);
            }
        }
        return rebuilt;
    }

    public static String representedTradeOfferSqlIds(List<RepresentedTradeOffer> tradeOffers, long socketIndex) {
        if (socketIndex <= 0L || tradeOffers == null || tradeOffers.isEmpty()) {
            return "";
        }
        StringBuilder sqlIds = new StringBuilder();
        for (RepresentedTradeOffer offer : tradeOffers) {
            if (offer.socketIndex() == socketIndex) {
                if (offer.furnitureId() > 0L) {
                    if (sqlIds.length() > 0) {
                        sqlIds.append(',');
                    }
                    sqlIds.append('\'').append(offer.furnitureId()).append('\'');
                }
            }
        }
        return sqlIds.toString();
    }

    public static String representedTradeOfferLogItems(List<RepresentedTradeOffer> tradeOffers, long socketIndex) {
        if (socketIndex <= 0L || tradeOffers == null || tradeOffers.isEmpty()) {
            return "";
        }
        StringBuilder logItems = new StringBuilder();
        for (RepresentedTradeOffer offer : tradeOffers) {
            if (offer.socketIndex() == socketIndex) {
                if (offer.furnitureId() > 0L) {
                    if (logItems.length() > 0) {
                        logItems.append('\1');
                    }
                    logItems.append(offer.furnitureId()).append(':').append(offer.productId());
                }
            }
        }
        return logItems.toString();
    }

    public static TradeOfferItemPayload representedTradeOfferItemPayload(List<RepresentedTradeOffer> tradeOffers, long socketIndex) {
        TradeOfferItemPayload result = new TradeOfferItemPayload();
        if (socketIndex <= 0L || tradeOffers == null || tradeOffers.isEmpty()) {
            return result;
        }
        StringBuilder payload = new StringBuilder();
        for (RepresentedTradeOffer offer : tradeOffers) {
            if (offer.socketIndex() == socketIndex) {
                payload.append(InventoryMessagePayloads.item(
                    offer.furnitureId(),
                    offer.productId(),
                    offer.signText(),
                    offer.secondaryValue()));
                result.itemCount++;
            }
        }
        result.payload = payload.toString();
        return result;
    }

    public static String representedTradeOfferPayload(
        List<RepresentedTradeOffer> tradeOffers,
        long sourceSocketIndex,
        long targetSocketIndex,
        String sourceUserId,
        String targetUserId
    ) {
        if (sourceSocketIndex <= 0L || targetSocketIndex <= 0L) {
            return "";
        }
        TradeOfferItemPayload sourceItems = representedTradeOfferItemPayload(tradeOffers, sourceSocketIndex);
        TradeOfferItemPayload targetItems = representedTradeOfferItemPayload(tradeOffers, targetSocketIndex);
        return TradePayloads.confirmation(
            NumberUtils.parseLong(sourceUserId),
            NumberUtils.parseLong(targetUserId),
            sourceItems.itemCount,
            sourceItems.payload,
            targetItems.itemCount,
            targetItems.payload);
    }

    public static InventoryPayloads inventoryPayloadsFromInventory(InventoryMessagePayloads.InventoryList inventory) {
        InventoryPayloads result = new InventoryPayloads();
        if (inventory == null) {
            return result;
        }
        result.regularCount = inventory.regularCount;
        result.regularPayload = inventory.regularPayload;
        result.iconCount = inventory.iconCount;
        result.iconPayload = inventory.iconPayload;
        return result;
    }

    public static FurnitureMoveRequest furnitureMoveRequestFromPayload(String packetPayload) {
        FurnitureMoveRequest request = new FurnitureMoveRequest();
        String requestPayload = StringUtils.text(packetPayload);
        if (requestPayload.startsWith("A[")) {
            requestPayload = requestPayload.substring(2);
        }

        String normalizedPayload = requestPayload
            .replace('\1', ' ')
            .replace('\2', ' ')
            .replace('\t', ' ')
            .replace('\r', ' ')
            .replace('\n', ' ');
        while (normalizedPayload.contains("  ")) {
            normalizedPayload = normalizedPayload.replace("  ", " ");
        }
        normalizedPayload = normalizedPayload.trim();

        if (!normalizedPayload.isEmpty()) {
            String[] tokens = normalizedPayload.split(" ", -1);
            request.furnitureId = tokens.length >= 1 ? NumberUtils.parseLong(tokens[0]) : 0L;
            request.positionX = tokens.length >= 2 ? NumberUtils.parseLong(tokens[1]) : 0L;
            request.positionY = tokens.length >= 3 ? NumberUtils.parseLong(tokens[2]) : 0L;
            request.rotation = tokens.length >= 4 ? NumberUtils.parseLong(tokens[3]) : 0L;
        }

        if (request.furnitureId <= 0L) {
            LongRef offset = new LongRef(1);
            request.furnitureId = readWireLong(requestPayload, offset);
        }
        return request;
    }

    public static long pickupFurnitureIdFromPayload(String packetPayload) {
        String requestPayload = StringUtils.text(packetPayload);
        if (requestPayload.startsWith("AZ")) {
            requestPayload = requestPayload.substring(2);
        }
        long furnitureId = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
        if (furnitureId <= 0L) {
            LongRef offset = new LongRef(1);
            furnitureId = readWireLong(requestPayload, offset);
        }
        return furnitureId;
    }

    public static FurnitureCacheState trackFurnitureCacheMarker(
        String pendingRoomCache,
        String pendingFurnitureCache,
        String representedRoomCache,
        long roomId,
        long furnitureId
    ) {
        return furnitureCacheState(FurnitureRoomCache.trackMarker(
            pendingRoomCache, pendingFurnitureCache, representedRoomCache, roomId, furnitureId));
    }

    public static FurnitureCacheState removeFurnitureCacheMarker(
        String pendingRoomCache,
        String pendingFurnitureCache,
        String representedRoomCache,
        long furnitureId
    ) {
        return furnitureCacheState(FurnitureRoomCache.removeMarker(
            pendingRoomCache, pendingFurnitureCache, representedRoomCache, furnitureId));
    }

    public static long nextFurnitureState(String productSprite, long currentState, long maxState) {
        String sprite = StringUtils.text(productSprite).toLowerCase();
        if (sprite.contains("dice")) {
            return Functions.Proc_10_4_809CA0(1, 6);
        }
        if (sprite.startsWith("bb_score_") || sprite.startsWith("es_score_") || sprite.contains("score")) {
            long resolvedMaxState = maxState <= 0L ? 99L : maxState;
            long nextState = currentState + 1L;
            return nextState > resolvedMaxState ? 0L : nextState;
        }
        long resolvedMaxState = maxState <= 0L ? 1L : maxState;
        long nextState = currentState + 1L;
        return nextState > resolvedMaxState ? 0L : nextState;
    }

    public static FurnitureStateCache representedFurnitureStateCache(
        String pendingRoomCache,
        String pendingFurnitureCache,
        String representedRoomCache,
        long roomId,
        long furnitureId,
        long stateValue
    ) {
        return furnitureStateCache(FurnitureRoomCache.stateCache(
            pendingRoomCache, pendingFurnitureCache, representedRoomCache, roomId, furnitureId, stateValue));
    }

    public static FurnitureStateCache representedFurnitureStateWrite(
        String pendingRoomCache,
        String pendingFurnitureCache,
        String representedRoomCache,
        long roomId,
        long furnitureId,
        String stateText
    ) {
        return furnitureStateCache(FurnitureRoomCache.stateWrite(
            pendingRoomCache, pendingFurnitureCache, representedRoomCache, roomId, furnitureId, stateText));
    }

    public static void handlingRepresentedFurnitureStateWrite(Object... args) {
        try {
            long roomId = 0L;
            long furnitureId = 0L;
            String stateText = "";
            if (args != null && args.length >= 3) {
                roomId = NumberUtils.parseLong(args[0]);
                furnitureId = NumberUtils.parseLong(args[1]);
                stateText = StringUtils.text(args[2]);
            } else if (args != null && args.length >= 2) {
                furnitureId = NumberUtils.parseLong(args[0]);
                stateText = StringUtils.text(args[1]);
            }
            if (furnitureId <= 0L) {
                return;
            }
            if (roomId <= 0L) {
                roomId = furnitureDao().roomIdByFurniture(furnitureId);
            }
            FurnitureRoomCache.State cacheState = Licence.furnitureRoomCache();
            FurnitureStateCache state = representedFurnitureStateWrite(
                cacheState.pendingRoomCache,
                cacheState.pendingFurnitureCache,
                cacheState.representedRoomCache,
                roomId,
                furnitureId,
                stateText);
            Licence.setFurnitureRoomCache(furnitureRoomCacheState(state));
            if (roomId > 0L) {
                Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "ROOMS", roomId + ".cache").toString(), 0, 0);
                Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "PATHFINDER", roomId + ".cache").toString(), 0, 0);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    private static FurnitureCacheState furnitureCacheState(FurnitureRoomCache.State source) {
        FurnitureCacheState state = new FurnitureCacheState();
        state.pendingRoomCache = source.pendingRoomCache;
        state.pendingFurnitureCache = source.pendingFurnitureCache;
        state.representedRoomCache = source.representedRoomCache;
        return state;
    }

    private static FurnitureStateCache furnitureStateCache(FurnitureRoomCache.State source) {
        FurnitureStateCache state = new FurnitureStateCache();
        state.pendingRoomCache = source.pendingRoomCache;
        state.pendingFurnitureCache = source.pendingFurnitureCache;
        state.representedRoomCache = source.representedRoomCache;
        return state;
    }

    private static FurnitureRoomCache.State furnitureRoomCacheState(FurnitureCacheState source) {
        return FurnitureRoomCache.State.from(source.pendingRoomCache, source.pendingFurnitureCache, source.representedRoomCache);
    }

    private static FurnitureRoomCache.State furnitureRoomCacheState(FurnitureStateCache source) {
        return FurnitureRoomCache.State.from(source.pendingRoomCache, source.pendingFurnitureCache, source.representedRoomCache);
    }

    public static String Proc_6_156_7972B0(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        long baseValue = NumberUtils.parseLong(args[0]);
        long firstValue = args.length >= 2 ? NumberUtils.parseLong(args[1]) : 0L;
        String secondValue = args.length >= 3 ? StringUtils.text(args[2]) : "";
        String thirdValue = args.length >= 4 ? StringUtils.text(args[3]) : "";
        long fourthValue = args.length >= 5 ? NumberUtils.parseLong(args[4]) : 0L;
        return FurniturePayloads.wallInventoryPlacement(baseValue, firstValue, secondValue, thirdValue, fourthValue);
    }

    public static String Proc_6_161_7B2EE0(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        return FurniturePayloads.floorPlacement(
            NumberUtils.parseLong(args[0]),
            args.length >= 2 ? NumberUtils.parseLong(args[1]) : 0L,
            args.length >= 3 ? NumberUtils.parseLong(args[2]) : 0L,
            args.length >= 4 ? NumberUtils.parseLong(args[3]) : 0L,
            args.length >= 5 ? NumberUtils.parseLong(args[4]) : 0L,
            args.length >= 6 ? StringUtils.text(args[5]) : "",
            args.length >= 7 ? StringUtils.text(args[6]) : "",
            args.length >= 8 ? NumberUtils.parseLong(args[7]) : 0L,
            args.length >= 9 ? NumberUtils.parseLong(args[8]) : 0L);
    }

    public static String systemHandshakePayload(String configuredDateFormat) {
        String dateFormat = StringUtils.text(configuredDateFormat);
        if (dateFormat.isEmpty()) {
            dateFormat = "DAQBHHIIKHJHPAHQA";
        }
        return "0" + dateFormat + '\2' + "SAHPB" + "http://www.alpha-series.com/" + '\2' + "QBH";
    }

    public static String handlingLoginTicketFromPayload(String packetPayload) {
        String requestPayload = StringUtils.text(packetPayload);
        if (requestPayload.startsWith("F_")) {
            requestPayload = requestPayload.substring(2);
        }
        requestPayload = Functions.Proc_10_10_80A7F0(requestPayload, 0, 0).trim();
        if (requestPayload.startsWith("F_")) {
            requestPayload = requestPayload.substring(2);
        }
        return requestPayload;
    }

    public static void handlingStoreSocketSession(int socketIndex, String sessionRecord) {
        Licence.storeSocketSession(socketIndex, sessionRecord);
    }

    public static String normalizeRoomModelMap(String modelMap) {
        String modelPayload = StringUtils.text(modelMap).replace('\n', '\r');
        while (modelPayload.contains("\r\r")) {
            modelPayload = modelPayload.replace("\r\r", "\r");
        }
        return modelPayload;
    }

    public static void sendRoomPollPrompt(int socketIndex, String userId, long roomId) {
        if (socketIndex <= 0 || StringUtils.text(userId).isEmpty() || roomId <= 0L) {
            return;
        }
        PollDao polls = pollDao();
        if (polls == null) {
            return;
        }
        long userIdValue = NumberUtils.parseLong(userId);
        if (userIdValue <= 0L) {
            return;
        }
        try {
            PollPrompt pollPrompt = polls.activePrompt(roomId).orElse(null);
            if (pollPrompt == null || pollPrompt.id() <= 0L) {
                return;
            }
            if (polls.hasExited(userIdValue, pollPrompt.id())) {
                return;
            }
            if (polls.hasAnswered(userIdValue, pollPrompt.id())) {
                return;
            }
            Proc_6_244_801E80(socketIndex, PollPayloads.prompt(pollPrompt), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return;
        }
    }

    public static long representedActivityPointSessionSeconds(long socketIndex, String userId) {
        if (socketIndex <= 0L || StringUtils.text(userId).isEmpty()) {
            return 0L;
        }
        Long cachedTickValue = representedActivityPointTicks.get(socketIndex);
        long tickValue = cachedTickValue == null ? 0L : cachedTickValue;
        if (cachedTickValue == null) {
            try {
                tickValue = userDao().onlineTime(NumberUtils.parseLong(userId));
            } catch (Exception ignored) {
                tickValue = 0L;
            }
        }
        tickValue += 60L;
        representedActivityPointTicks.put(socketIndex, tickValue);
        return tickValue;
    }

    public static ActivityPointAward activityPointAwardDecision(
        long sessionSeconds,
        long pointType,
        long intervalSeconds,
        long maxPoints,
        long awardAmount,
        long currentPoints
    ) {
        ActivityPointAward result = new ActivityPointAward();
        result.pointType = pointType;
        result.awardAmount = awardAmount;
        if (sessionSeconds <= 0L || intervalSeconds <= 0L || sessionSeconds % intervalSeconds != 0L) {
            return result;
        }
        long effectiveMaxPoints = maxPoints <= 0L ? 1L : maxPoints;
        if (currentPoints >= effectiveMaxPoints || awardAmount == 0L) {
            return result;
        }
        result.newPoints = currentPoints + awardAmount;
        result.payload = UserPayloads.activityPointAward(pointType, result.newPoints);
        result.shouldAward = true;
        return result;
    }

    public static boolean isSocketMarkedBusy(String representedSocketCache, long socketIndex) {
        return com.alphaseries.game.session.RepresentedSocketCache.fromLegacy(representedSocketCache).isBusy(socketIndex);
    }

    public static long soundSettingFromWire(String packetPayload) {
        String requestPayload = StringUtils.text(packetPayload);
        if (requestPayload.startsWith("Ce")) {
            requestPayload = requestPayload.substring(2);
        }
        long soundSetting = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
        if (soundSetting <= 0L) {
            soundSetting = NumberUtils.parseLong(requestPayload);
        }
        return soundSetting > 0L && soundSetting < 101L ? soundSetting : 0L;
    }

    public static String loginGroupPayload(long groupId, UserGroupRow groupRow) {
        if (groupId <= 0L || groupRow == null) {
            return "";
        }
        return PacketBuilder.create()
            .appendRaw("Dt")
            .appendInt(groupId)
            .appendString(groupRow.name())
            .appendString(groupRow.description())
            .appendString(groupRow.badgeId())
            .appendInt(groupRow.roomId())
            .appendRaw('H')
            .build();
    }

    public static void storeRepresentedInteractionPair(long sourceSocketIndex, long targetSocketIndex, long interactionState) {
        if (sourceSocketIndex <= 0L || targetSocketIndex <= 0L) {
            return;
        }
        removeRepresentedInteractionPair(sourceSocketIndex);
        removeRepresentedInteractionPair(targetSocketIndex);
        representedInteractionPairs.add(RepresentedInteractionPair.stored(sourceSocketIndex, targetSocketIndex, interactionState));
        representedInteractionPairs.add(RepresentedInteractionPair.stored(targetSocketIndex, sourceSocketIndex, interactionState));
    }

    public static void removeRepresentedInteractionPair(long socketIndex) {
        if (socketIndex <= 0L || representedInteractionPairs.isEmpty()) {
            return;
        }
        List<RepresentedInteractionPair> rebuilt = new ArrayList<>();
        for (RepresentedInteractionPair pair : representedInteractionPairs) {
            if (pair.socketIndex() != socketIndex) {
                rebuilt.add(pair);
            }
        }
        representedInteractionPairs = rebuilt;
        removeRepresentedTradeOffer(socketIndex, 0L);
    }

    public static int representedInteractionPartner(long socketIndex) {
        if (socketIndex <= 0L || representedInteractionPairs.isEmpty()) {
            return 0;
        }
        for (RepresentedInteractionPair pair : representedInteractionPairs) {
            if (pair.socketIndex() == socketIndex) {
                return (int) pair.partnerSocketIndex();
            }
        }
        return 0;
    }

    public static long representedInteractionState(long socketIndex) {
        if (socketIndex <= 0L || representedInteractionPairs.isEmpty()) {
            return 0L;
        }
        for (RepresentedInteractionPair pair : representedInteractionPairs) {
            if (pair.socketIndex() == socketIndex) {
                return pair.interactionState();
            }
        }
        return 0L;
    }

    public static void storeRepresentedTradeOffer(long socketIndex, long furnitureId, long productId, String signText, long secondaryValue) {
        representedTradeOffers = representedTradeOfferStore(representedTradeOffers, socketIndex, furnitureId, productId, signText, secondaryValue);
    }

    public static void removeRepresentedTradeOffer(long socketIndex, long furnitureId) {
        representedTradeOffers = representedTradeOfferRemove(representedTradeOffers, socketIndex, furnitureId);
    }

    public static long questRequestIdFromWire(String packetPayload, String prefix) {
        return idRequestFromWire(packetPayload, prefix);
    }

    public static long nextQuestId(QuestSettings questSettings, QuestDao.UserQuestLevelRow activeQuest) {
        long currentQuestId = 0L;
        long currentLevel = 0L;
        if (activeQuest != null) {
            currentQuestId = activeQuest.questId();
            currentLevel = activeQuest.level();
        }

        long currentCampaignId = 0L;
        long fallbackQuestId = 0L;
        long fallbackCampaignId = 0L;
        long fallbackLevel = Integer.MAX_VALUE;
        boolean foundCurrent = false;
        QuestSettings settings = questSettings == null ? QuestSettings.fromLegacy("") : questSettings;
        for (QuestSettings.QuestDefinitionRow definition : settings.definitions()) {
            if (definition.fieldCount() >= 9) {
                if (fallbackQuestId <= 0L || definition.level() < fallbackLevel) {
                    fallbackQuestId = definition.questId();
                    fallbackCampaignId = definition.campaignId();
                    fallbackLevel = definition.level();
                }
                if (definition.questId() == currentQuestId) {
                    currentCampaignId = definition.campaignId();
                    currentLevel = definition.level();
                    foundCurrent = true;
                }
            }
        }
        if (!foundCurrent) {
            currentCampaignId = fallbackCampaignId;
            currentLevel = fallbackLevel - 1L;
        }

        long requestedQuestId = 0L;
        long bestLevel = Integer.MAX_VALUE;
        for (QuestSettings.QuestDefinitionRow definition : settings.definitions()) {
            if (definition.fieldCount() >= 9) {
                if (definition.campaignId() == currentCampaignId && definition.level() > currentLevel
                    && definition.level() < bestLevel) {
                    requestedQuestId = definition.questId();
                    bestLevel = definition.level();
                }
            }
        }
        return requestedQuestId > 0L ? requestedQuestId : fallbackQuestId;
    }

    public static QuestProgressDecision questProgressDecision(
        QuestDao.UserQuestProgressRow activeQuest,
        QuestSettings questSettings,
        long remainingWait
    ) {
        QuestProgressDecision decision = new QuestProgressDecision();
        if (activeQuest == null) {
            return decision;
        }
        decision.questId = activeQuest.questId();
        decision.numericQuestId = activeQuest.numericQuestId();
        decision.progressValue = activeQuest.progress();
        String timeNextText = StringUtils.text(activeQuest.timeNext());
        if (decision.questId <= 0L) {
            return decision;
        }

        boolean matchedQuest = false;
        QuestSettings settings = questSettings == null ? QuestSettings.fromLegacy("") : questSettings;
        QuestSettings.QuestDefinitionRow questDefinition = settings.definitionById(decision.questId);
        if (questDefinition != null && questDefinition.fieldCount() >= 11) {
            decision.amountRequired = questDefinition.activityAmount();
            decision.waitAmount = questDefinition.waitAmount();
            matchedQuest = true;
        }
        if (!matchedQuest) {
            return decision;
        }

        if (!timeNextText.isEmpty() && !"0".equals(timeNextText)) {
            decision.remainingWait = Math.max(0L, remainingWait);
            if (decision.remainingWait > 0L) {
                decision.shouldSendList = true;
                return decision;
            }
        } else if (decision.waitAmount > 0L && decision.progressValue > 0L && decision.progressValue < decision.amountRequired) {
            decision.shouldScheduleWait = true;
            decision.shouldSendList = true;
            return decision;
        }

        if (decision.amountRequired <= 0L) {
            decision.amountRequired = 1L;
        }
        if (decision.progressValue >= decision.amountRequired) {
            decision.shouldComplete = true;
        } else {
            decision.shouldSendList = true;
        }
        return decision;
    }

    public static String Proc_6_166_7BE940(Object... args) {
        long userId = args != null && args.length >= 1 ? NumberUtils.parseLong(args[0]) : 0L;
        String userName = args != null && args.length >= 2 ? StringUtils.text(args[1]) : "";
        String motto = args != null && args.length >= 3 ? StringUtils.text(args[2]) : "";
        String figure = args != null && args.length >= 4 ? StringUtils.text(args[3]) : "";
        long rankValue = args != null && args.length >= 5 ? NumberUtils.parseLong(args[4]) : 0L;
        long followCount = args != null && args.length >= 6 ? NumberUtils.parseLong(args[5]) : 0L;
        long isOnline = args != null && args.length >= 7 ? NumberUtils.parseLong(args[6]) : 0L;
        String lastOnlineText = args != null && args.length >= 8 ? StringUtils.text(args[7]) : "";
        long relationshipState = args != null && args.length >= 9 ? NumberUtils.parseLong(args[8]) : 0L;
        return messengerFriendPayload(userId, userName, motto, figure, rankValue, followCount, isOnline, lastOnlineText, relationshipState);
    }

    public static String Proc_6_164_7BC820(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long questId = args != null && args.length >= 2 ? NumberUtils.parseLong(args[1]) : 0L;
            long numericQuestId = args != null && args.length >= 3 ? NumberUtils.parseLong(args[2]) : 0L;
            QuestDao quests = questDao();
            UserDao users = userDao();
            if (quests == null || users == null) {
                return "";
            }
            long userIdValue = NumberUtils.parseLong(userId);
            QuestDao.UserQuestCompletionRow activeRow = quests.completionRow(userIdValue, questId).orElse(null);
            if (activeRow == null) {
                return "";
            }
            questId = activeRow.questId();
            if (numericQuestId <= 0L) {
                numericQuestId = activeRow.numericQuestId();
            }
            long progressValue = activeRow.progress();
            long userQuestLevel = activeRow.level();
            if (questId <= 0L) {
                return "";
            }
            QuestSettings questSettings = questSettingsFromSource();
            QuestSettings.QuestDefinitionRow questDefinition = questSettings.definitionById(questId);
            if (questDefinition == null || questDefinition.fieldCount() < 11) {
                return "";
            }
            String questName = questDefinition.name();
            long rewardAmount = questDefinition.reward();
            long rewardType = questDefinition.rewardType();
            long campaignId = questDefinition.campaignId();
            long activityCount = questDefinition.activityAmount();
            if (activityCount <= 0L) {
                activityCount = 1L;
            }
            long campaignLevelCount = questSettings.campaignLevelCount(campaignId);
            String completionPayload = QuestPayloads.completion(campaignId, questName, campaignLevelCount, questId,
                userQuestLevel, progressValue, activityCount);
            Proc_6_244_801E80(socketIndex, "Lb" + completionPayload, 0);
            if (progressValue < activityCount) {
                return "";
            }
            if (rewardAmount != 0L && rewardType >= 0L && rewardType <= 20L) {
                long currentPoints = users.activityPoints(userIdValue, rewardType);
                users.addActivityPointsLimited(userIdValue, rewardType, rewardAmount);
                Proc_6_244_801E80(socketIndex, UserPayloads.activityPointAward(rewardType, currentPoints + rewardAmount), 0);
            }
            quests.completeQuest(userIdValue, questId);
            Proc_6_244_801E80(socketIndex, "La" + completionPayload, 0);
            Proc_6_236_7F8540(socketIndex, "", "");
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String messengerFriendPayload(
        long userId,
        String userName,
        String motto,
        String figure,
        long rankValue,
        long followCount,
        long isOnline,
        String lastOnlineText,
        long relationshipState
    ) {
        return MessengerPayloads.friend(
            userId,
            userName,
            motto,
            figure,
            rankValue,
            followCount,
            isOnline,
            lastOnlineText,
            relationshipState,
            messengerFollowEnabled());
    }

    public static String messengerFriendSummaryPayload(MessengerFriend friend, long relationshipState) {
        return MessengerPayloads.friendSummary(friend, relationshipState, messengerFollowEnabled());
    }

    public static String messengerFriendSummaryPayload(String userId, long relationshipState) {
        try {
            if (StringUtils.text(userId).isEmpty() || "0".equals(StringUtils.text(userId))) {
                return "";
            }
            String dateFormat = Functions.Proc_10_0_809570("com.mysql.format.date", "%d-%m-%Y", 0);
            String timeFormat = Functions.Proc_10_0_809570("com.mysql.format.time", "%H:%i", 0);
            MessengerDao messenger = messengerDao();
            if (messenger == null) {
                return "";
            }
            MessengerFriend friend = messenger
                .messengerFriend(NumberUtils.parseLong(userId), dateFormat + " " + timeFormat)
                .orElse(null);
            return messengerFriendSummaryPayload(friend, relationshipState);
        } catch (Exception ignored) {
            return "";
        }
    }

    public static boolean messengerFollowEnabled() {
        return NumberUtils.parseLong(Functions.Proc_10_0_809570("com.client.messenger.follow.enabled", 0)) != 0L;
    }

    public static long messengerMaxFriends(long configIndex) {
        return Licence.messengerSettings().maxFriends(configIndex);
    }

    public static String requestTextFromWirePayload(String packetPayload, String prefix, int maxLength) {
        String requestPayload = StringUtils.text(packetPayload);
        if (!StringUtils.text(prefix).isEmpty() && requestPayload.startsWith(prefix)) {
            requestPayload = requestPayload.substring(prefix.length());
        }
        String value = Functions.Proc_10_7_80A190(requestPayload, 0, 0);
        if (value.isEmpty()) {
            LongRef offset = new LongRef(1);
            value = readWireString(requestPayload, offset);
        }
        if (maxLength >= 0 && value.length() > maxLength) {
            return value.substring(0, maxLength);
        }
        return value;
    }

    public static FriendTargetList friendDeleteTargetsFromPayload(String packetPayload) {
        String requestPayload = StringUtils.text(packetPayload);
        if (requestPayload.startsWith("@f")) {
            requestPayload = requestPayload.substring(2);
        }
        FriendTargetList result = new FriendTargetList();
        LongRef offset = new LongRef(1);
        long firstValue = readWireLong(requestPayload, offset);
        if (firstValue == 1L) {
            result.deleteAllPending = true;
            return result;
        }

        long maxTargets = firstValue <= 0L ? 75L : Math.min(firstValue, 75L);
        StringBuilder targetList = new StringBuilder();
        while (offset.value <= requestPayload.length() && result.targetCount < maxTargets) {
            long previousOffset = offset.value;
            long targetUserId = readWireLong(requestPayload, offset);
            if (targetUserId <= 0L || offset.value == previousOffset) {
                break;
            }
            String token = String.valueOf(targetUserId);
            if (!("," + targetList + ",").contains("," + token + ",")) {
                if (targetList.length() > 0) {
                    targetList.append(',');
                }
                targetList.append(token);
                result.targetCount++;
            }
        }
        if (targetList.length() == 0 && firstValue > 1L) {
            targetList.append(firstValue);
            result.targetCount = 1L;
        }
        result.targetList = targetList.toString();
        return result;
    }

    public static String messengerFriendListPayload(
        List<MessengerFriend> friends,
        long maxFriends0,
        long maxFriends1,
        long maxFriends2
    ) {
        return MessengerPayloads.friendList(friends, maxFriends0, maxFriends1, maxFriends2, messengerFollowEnabled());
    }

    public static FriendTargetList friendRemoveTargetsFromPayload(String packetPayload, String callerUserId) {
        String requestPayload = StringUtils.text(packetPayload);
        if (requestPayload.startsWith("@h")) {
            requestPayload = requestPayload.substring(2);
        }
        FriendTargetList result = new FriendTargetList();
        LongRef offset = new LongRef(1);
        long removeCount = readWireLong(requestPayload, offset);
        if (removeCount <= 0L) {
            return result;
        }
        if (removeCount > 75L) {
            removeCount = 75L;
        }
        StringBuilder targetList = new StringBuilder();
        for (long removeIndex = 1L; removeIndex <= removeCount; removeIndex++) {
            long targetUserId = readWireLong(requestPayload, offset);
            String token = String.valueOf(targetUserId);
            if (targetUserId > 0L && !token.equals(StringUtils.text(callerUserId)) && !("," + targetList + ",").contains("," + token + ",")) {
                if (targetList.length() > 0) {
                    targetList.append(',');
                }
                targetList.append(token);
                result.targetCount++;
            }
        }
        result.targetList = targetList.toString();
        return result;
    }

    public static PetCommandAction petCommandAction(long commandId, Object commandRows) {
        PetCommandAction result = new PetCommandAction();
        if (commandId <= 0L) {
            return result;
        }
        for (PetSettings.PetCommandRow row : PetSettings.commandRows(commandRows)) {
            if (row.commandId() == commandId) {
                result.requiredLevel = row.requiredLevel();
                result.action = row.action();
                result.found = true;
                return result;
            }
        }
        BotDao bots = botDao();
        if (bots != null) {
            try {
                PetCommandActionRow row = bots.petCommandAction(commandId).orElse(null);
                if (row != null) {
                    result.requiredLevel = row.requiredLevel();
                    result.action = StringUtils.text(row.action());
                    result.found = true;
                }
            } catch (Exception ignored) {
                // VB6 source suppresses handler failures.
            }
        }
        return result;
    }

    public static PetExperienceUpdate petExperienceUpdate(
        long botEntityId,
        String petName,
        String petFigure,
        long petLevel,
        long petExperience,
        long petEnergy,
        long petNutrition,
        long petScratches,
        long experienceDelta,
        Object levelRows
    ) {
        PetExperienceUpdate result = new PetExperienceUpdate();
        long nextExperience = petExperience + experienceDelta;
        if (nextExperience < 0L) {
            nextExperience = 0L;
        }
        long nextLevel = petLevel;
        long maxExperience = petLevelMaxExperience(petLevel, levelRows);
        if (maxExperience > 0L && nextExperience >= maxExperience && petLevelMaxExperience(petLevel + 1L, levelRows) > 0L) {
            nextLevel = petLevel + 1L;
            nextExperience = 0L;
            result.leveledUp = true;
        }
        result.petLevel = nextLevel;
        result.petExperience = nextExperience;
        result.statusPayload = petExperienceStatusPayload(botEntityId, petName, petFigure, nextLevel, nextExperience, petEnergy, petNutrition, petScratches);
        result.experiencePayload = PetPayloads.experience(botEntityId, experienceDelta, nextExperience);
        return result;
    }

    public static PetExperienceUpdate petExperienceUpdate(
        long botEntityId,
        String petName,
        String petFigure,
        long petLevel,
        long petExperience,
        long petEnergy,
        long petNutrition,
        long petScratches,
        long experienceDelta,
        List<PetLevelExperienceRow> levelRows
    ) {
        PetExperienceUpdate result = new PetExperienceUpdate();
        long nextExperience = petExperience + experienceDelta;
        if (nextExperience < 0L) {
            nextExperience = 0L;
        }
        long nextLevel = petLevel;
        long maxExperience = petLevelMaxExperience(petLevel, levelRows);
        if (maxExperience > 0L && nextExperience >= maxExperience && petLevelMaxExperience(petLevel + 1L, levelRows) > 0L) {
            nextLevel = petLevel + 1L;
            nextExperience = 0L;
            result.leveledUp = true;
        }
        result.petLevel = nextLevel;
        result.petExperience = nextExperience;
        result.statusPayload = petExperienceStatusPayload(botEntityId, petName, petFigure, nextLevel, nextExperience, petEnergy, petNutrition, petScratches);
        result.experiencePayload = PetPayloads.experience(botEntityId, experienceDelta, nextExperience);
        return result;
    }

    public static long petLevelMaxExperience(long petLevel, Object levelRows) {
        for (PetSettings.PetLevelRow row : PetSettings.levelRows(levelRows)) {
            if (row.level() == petLevel) {
                return row.maxExperience();
            }
        }
        BotDao bots = botDao();
        if (bots != null) {
            try {
                return bots.petLevelMaxExperience(petLevel);
            } catch (Exception ignored) {
                // VB6 source suppresses handler failures.
            }
        }
        return 0L;
    }

    public static long petLevelMaxExperience(long petLevel, List<PetLevelExperienceRow> levelRows) {
        if (levelRows != null) {
            for (PetLevelExperienceRow row : levelRows) {
                if (row != null && row.level() == petLevel) {
                    return row.maxExperience();
                }
            }
        }
        return 0L;
    }

    public static String petExperienceStatusPayload(
        long botEntityId,
        String petName,
        String petFigure,
        long petLevel,
        long petExperience,
        long petEnergy,
        long petNutrition,
        long petScratches
    ) {
        return PetPayloads.experienceStatus(botEntityId, petName, petFigure, petLevel, petExperience, petEnergy, petNutrition, petScratches);
    }

    public static String petScratchPayload(long botEntityId, long userId, long scratches, String petName, String petFigure) {
        return PetPayloads.scratch(botEntityId, userId, scratches, petName, petFigure);
    }

    public static String petCommandActionPayload(long botEntityId, String commandAction, long commandId) {
        return PetPayloads.commandAction(botEntityId, commandAction, commandId);
    }

    public static String petSpeechPayload(long botEntityId, String speechText) {
        return PetPayloads.speech(botEntityId, speechText);
    }

    public static long reserveRepresentedBotSlot() {
        RepresentedBotRegistry representedBots = Licence.representedBots();
        long botEntityId = representedBots.reserveSlot();
        Licence.setRepresentedBots(representedBots);
        return botEntityId;
    }

    public static long allocateRepresentedBot(long roomSlot, RepresentedBotEntry botEntry) {
        if (roomSlot <= 0L) {
            return 0L;
        }
        long botEntityId = reserveRepresentedBotSlot();
        if (botEntityId <= 0L) {
            return 0L;
        }
        storeRepresentedBotRecord(botEntityId, representedBotRecord(roomSlot, botEntry));
        return botEntityId;
    }

    public static String representedBotRecord(long roomSlot, RepresentedBotEntry botEntry) {
        if (botEntry == null) {
            return "";
        }
        return botEntry.recordText(roomSlot);
    }

    public static void storeRepresentedBotRecord(long botEntityId, String recordText) {
        RepresentedBotRegistry representedBots = Licence.representedBots();
        representedBots.storeRecord(botEntityId, recordText);
        Licence.setRepresentedBots(representedBots);
    }

    public static void removeRepresentedBotRecord(long botEntityId) {
        RepresentedBotRegistry representedBots = Licence.representedBots();
        representedBots.removeRecord(botEntityId);
        Licence.setRepresentedBots(representedBots);
    }

    public static String representedBotRecordText(long botEntityId) {
        return Licence.representedBots().recordText(botEntityId);
    }

    public static String representedBotRecordField(long botEntityId, long fieldIndex) {
        return Licence.representedBots().recordField(botEntityId, fieldIndex);
    }

    public static long representedBotRecordLong(long botEntityId, long fieldIndex) {
        return Licence.representedBots().recordLong(botEntityId, fieldIndex);
    }

    public static long representedBotEntityFromBotId(long botId) {
        return Licence.representedBots().entityFromBotId(botId);
    }

    public static String representedBotEntitiesForRoom(long roomSlot, long onlyBotId) {
        return Licence.representedBots().entitiesForRoom(roomSlot, onlyBotId);
    }

    public static boolean isRepresentedBotAllocated(long roomSlot, long botId) {
        return !representedBotEntitiesForRoom(roomSlot, botId).isEmpty();
    }

    public static void storeRepresentedBotPosition(long botEntityId, long positionX, long positionY, String positionZ, long positionR) {
        RepresentedBotRegistry representedBots = Licence.representedBots();
        representedBots.storePosition(botEntityId, positionX, positionY, positionZ, positionR);
        Licence.setRepresentedBots(representedBots);
    }

    public static String representedBotRoomEntryPayload(long botEntityId) {
        long botId = representedBotRecordLong(botEntityId, 1);
        if (botEntityId <= 0L || botId <= 0L) {
            return "";
        }
        String botName = representedBotRecordField(botEntityId, 2);
        long positionX = representedBotRecordLong(botEntityId, 6);
        long positionY = representedBotRecordLong(botEntityId, 7);
        String positionZ = representedBotRecordField(botEntityId, 8);
        long positionR = representedBotRecordLong(botEntityId, 9);
        String botFigure = representedBotRecordField(botEntityId, 10);
        return PetPayloads.representedBotRoomEntry(botEntityId, botName, positionX, positionY, positionZ, positionR, botFigure);
    }

    public static String representedRoomUserProfilePayload(
        long roomUserIndex,
        String userName,
        String mottoText,
        long achievementScore,
        String figureText
    ) {
        return SocialPayloads.roomUserProfile(roomUserIndex, userName, mottoText, achievementScore, figureText);
    }

    public static String representedRoomUserProfilePayload(RoomUserProfileRow row) {
        if (row == null) {
            return "";
        }
        return representedRoomUserProfilePayload(
            row.roomUserIndex(),
            row.userName(),
            row.motto(),
            row.achievementScore(),
            row.figure());
    }

    public static RoomUserTargetRow activeRoomUserTarget(long roomId, long requestedRoomUserIndex) throws Exception {
        RoomDao rooms = roomDao();
        java.util.Optional<RoomUserTargetRow> target = rooms.activeRoomUserTargetByVisitId(roomId, requestedRoomUserIndex);
        if (target.isEmpty()) {
            target = rooms.activeRoomUserTargetByUserId(roomId, requestedRoomUserIndex);
        }
        return target.orElse(null);
    }

    public static long pollIdFromWire(String packetPayload, String prefix) {
        return idRequestFromWire(packetPayload, prefix);
    }

    public static PollAnswerSubmission pollAnswerFromWire(String packetPayload, String prefix) {
        PollAnswerSubmission submission = new PollAnswerSubmission();
        String requestPayload = StringUtils.text(packetPayload);
        if (!StringUtils.text(prefix).isEmpty() && requestPayload.startsWith(prefix)) {
            requestPayload = requestPayload.substring(prefix.length());
        }
        LongRef offset = new LongRef(1);
        submission.pollId = readWireLong(requestPayload, offset);
        submission.questionId = readWireLong(requestPayload, offset);
        submission.answerValue = readWireLong(requestPayload, offset);
        submission.answerText = Functions.Proc_10_10_80A7F0(readWireString(requestPayload, offset), 0, 0);
        if (submission.pollId <= 0L) {
            submission.pollId = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
        }
        if (submission.answerText.isEmpty() && submission.answerValue > 0L) {
            submission.answerText = String.valueOf(submission.answerValue);
        }
        submission.valid = submission.pollId > 0L && submission.questionId > 0L;
        return submission;
    }

    public static String achievementRowByIndex(long achievementIndex) {
        return Licence.achievementSettings().rowByIndex(achievementIndex);
    }

    public static AchievementSettings.Achievement achievementByIndex(long achievementIndex) {
        return Licence.achievementSettings().achievementByIndex(achievementIndex);
    }

    public static Map<String, Long> achievementCurrentLevels(String userId, Iterable<AchievementSettings.Achievement> achievements) {
        Map<String, Long> result = new HashMap<>();
        UserDao users = userDao();
        long userIdValue = NumberUtils.parseLong(userId);
        Iterable<AchievementSettings.Achievement> rows = achievements == null
            ? List.<AchievementSettings.Achievement>of() : achievements;
        for (AchievementSettings.Achievement achievement : rows) {
            String badgePrefix = achievement.badgePrefix();
            if (!badgePrefix.isEmpty() && !result.containsKey(badgePrefix)) {
                long currentLevel = 0L;
                if (users != null && userIdValue > 0L) {
                    try {
                        currentLevel = users.badgeLevelByPrefix(userIdValue, badgePrefix);
                    } catch (Exception ignored) {
                        currentLevel = 0L;
                    }
                }
                result.put(badgePrefix, Math.max(0L, currentLevel));
            }
        }
        return result;
    }

    public static long representedAchievementProgress(String userId, long achievementQuestId) {
        UserDao users = userDao();
        if (users == null) {
            return 0L;
        }
        long userIdValue = NumberUtils.parseLong(userId);
        try {
            long progress;
            if (achievementQuestId == 1L) {
                progress = users.distinctVisitedRoomCount(userIdValue);
            } else if (achievementQuestId == 2L) {
                progress = users.respectReceived(userIdValue);
            } else if (achievementQuestId == 3L) {
                progress = users.respectGiven(userIdValue);
            } else if (achievementQuestId == 4L) {
                progress = users.onlineTime(userIdValue) / 60L;
            } else if (achievementQuestId == 6L) {
                progress = users.giftsGiven(userIdValue);
            } else if (achievementQuestId == 7L) {
                progress = users.giftsReceived(userIdValue);
            } else if (achievementQuestId == 8L) {
                progress = users.hcPeriods(userIdValue);
            } else if (achievementQuestId == 9L) {
                progress = users.hc2Periods(userIdValue);
            } else if (achievementQuestId == 11L) {
                progress = users.staffPickedAmount(userIdValue);
            } else {
                progress = users.achievementProgressSummary(userIdValue)
                    .map(UserDao.AchievementProgressSummary::respectReceived)
                    .orElse(0L);
            }
            return Math.max(0L, progress);
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static String achievementRewardPayload(
        long achievementIndex,
        AchievementSettings.Achievement achievement,
        long badgeLevel,
        long badgeRowId
    ) {
        return AchievementPayloads.reward(achievementIndex, achievement, badgeLevel, badgeRowId);
    }

    public static String achievementAwardPayload(AchievementSettings.Achievement achievement) {
        return AchievementPayloads.award(achievement);
    }

    public static AchievementProgressDecision achievementProgressDecision(
        Iterable<AchievementSettings.IndexedAchievement> indexedAchievements,
        long achievementQuestId,
        Map<String, Long> currentLevelsByBadgePrefix,
        long currentProgress
    ) {
        AchievementProgressDecision decision = new AchievementProgressDecision();
        Iterable<AchievementSettings.IndexedAchievement> rows = indexedAchievements == null
            ? List.<AchievementSettings.IndexedAchievement>of() : indexedAchievements;
        for (AchievementSettings.IndexedAchievement indexedAchievement : rows) {
            AchievementSettings.Achievement achievement = indexedAchievement.achievement();
            if (achievement.achievementId() == achievementQuestId) {
                long levelTotal = achievement.levelTotal();
                if (levelTotal <= 0L) {
                    levelTotal = 1L;
                }
                long currentLevel = currentLevelsByBadgePrefix != null
                    && currentLevelsByBadgePrefix.containsKey(achievement.badgePrefix())
                    ? currentLevelsByBadgePrefix.get(achievement.badgePrefix()) : 0L;
                if (!achievement.badgePrefix().isEmpty() && achievement.progressRequired() > 0L
                    && currentLevel >= 0L && currentLevel < levelTotal) {
                    decision.achievementIndex = indexedAchievement.achievementIndex();
                    decision.nextLevel = currentLevel + 1L;
                    decision.requiredProgress = achievement.progressRequired() * decision.nextLevel;
                    decision.shouldReward = currentProgress >= decision.requiredProgress;
                }
                return decision;
            }
        }
        return decision;
    }

    public static String achievementListPayload(
        Iterable<AchievementSettings.Achievement> achievements,
        Map<String, Long> currentLevelsByBadgePrefix
    ) {
        return AchievementPayloads.list(achievements, currentLevelsByBadgePrefix);
    }

    public static String wiredSpecialStatePayload(long itemState) {
        return WiredPayloads.specialState(itemState);
    }

    public static String handlingRepresentedWiredEdit(
        Object[] args,
        String packetCode,
        long minimumCode,
        long maximumCode,
        String cacheFolder,
        boolean includeExtraValue
    ) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String packetPayload = handlingPacketPayload(args);
            long furnitureId = firstWireLong(packetPayload, packetCode);
            if (socketIndex <= 0 || furnitureId <= 0L) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L || (!handlingUserHasRoomRight(userId, roomId) && !handlingUserOwnsRoom(userId, roomId))) {
                return "";
            }
            long productId = furnitureDao().roomFurnitureProductById(furnitureId, roomId)
                .map(FurnitureDao.RoomFurnitureProduct::productId)
                .orElse(0L);
            long wiredCode = NumberUtils.parseLong(DataManager.Proc_8_12_806C30(productId, 27, 0));
            if (wiredCode < minimumCode || wiredCode > maximumCode) {
                return "";
            }
            String recordText = wiredEditRecordFromWire(packetPayload, packetCode, wiredCode, includeExtraValue);
            if (recordText.isEmpty()) {
                return "";
            }
            String selectedIds = wiredRecordField(recordText, 2);
            if (!selectedIds.isEmpty() && !handlingRepresentedWiredSelectedItemsExist(roomId, selectedIds)) {
                return "";
            }
            String cachePath = wiredCachePath(cacheFolder, roomId);
            String cacheText = Proc_6_239_7FC170(cachePath, 0, 0);
            Proc_6_240_7FC2B0(cachePath, wiredCacheWithRecord(cacheText, recordText));
            return recordText;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static long handlingRepresentedWiredTriggerCall(Object[] args, long triggerCode) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long roomId = 0L;
            if (socketIndex > 0) {
                String userId = handlingUserIdFromSocket(socketIndex);
                if (!userId.isEmpty() && !"0".equals(userId)) {
                    roomId = handlingCurrentRoomId(socketIndex, userId);
                }
            }
            if (roomId <= 0L && args != null && args.length >= 2) {
                roomId = NumberUtils.parseLong(args[1]);
            }
            return handlingRepresentedWiredTrigger(roomId, triggerCode, socketIndex);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    public static long handlingRepresentedWiredActionCall(Object[] args, long actionCode) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long roomId = 0L;
            if (socketIndex > 0) {
                String userId = handlingUserIdFromSocket(socketIndex);
                if (!userId.isEmpty() && !"0".equals(userId)) {
                    roomId = handlingCurrentRoomId(socketIndex, userId);
                }
            }
            if (roomId <= 0L && args != null && args.length >= 2) {
                roomId = NumberUtils.parseLong(args[1]);
            }
            long selectedFurnitureId = args != null && args.length >= 3 ? NumberUtils.parseLong(args[2]) : 0L;
            return handlingRepresentedWiredAction(roomId, actionCode, selectedFurnitureId, socketIndex);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    public static long handlingRepresentedWiredTrigger(long roomId, long triggerCode, int socketIndex) {
        if (roomId <= 0L) {
            return 0L;
        }
        long executedCount = 0L;
        for (String row : readWiredCache("wired_trigger", roomId).replace("\r", "").split("\n", -1)) {
            String recordText = row.trim();
            if (!recordText.isEmpty()) {
                long recordCode = NumberUtils.parseLong(wiredRecordField(recordText, 0));
                if ((triggerCode <= 0L || recordCode == triggerCode) && handlingRepresentedWiredConditionsPass(roomId)) {
                    executedCount += handlingRepresentedWiredAction(roomId, 0L, 0L, socketIndex);
                }
            }
        }
        return executedCount;
    }

    public static boolean handlingRepresentedWiredConditionsPass(long roomId) {
        if (roomId <= 0L) {
            return false;
        }
        for (String row : readWiredCache("wired_condition", roomId).replace("\r", "").split("\n", -1)) {
            String recordText = row.trim();
            if (!recordText.isEmpty()) {
                String selectedIds = wiredRecordField(recordText, 2);
                if (!selectedIds.isEmpty() && !handlingRepresentedWiredSelectedItemsExist(roomId, selectedIds)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static long handlingRepresentedWiredAction(long roomId, long actionCode, long selectedFurnitureId, int socketIndex) {
        if (roomId <= 0L) {
            return 0L;
        }
        long actionCount = 0L;
        for (String row : readWiredCache("wired_action", roomId).replace("\r", "").split("\n", -1)) {
            String recordText = row.trim();
            if (!recordText.isEmpty()) {
                long recordCode = NumberUtils.parseLong(wiredRecordField(recordText, 0));
                if (actionCode <= 0L || recordCode == actionCode) {
                    actionCount += handlingRepresentedWiredApplySelected(
                        roomId, wiredRecordField(recordText, 2), wiredRecordField(recordText, 3), selectedFurnitureId);
                }
            }
        }
        return actionCount;
    }

    public static long handlingRepresentedWiredApplySelected(long roomId, String selectedIds, String parameterText, long selectedFurnitureId) {
        if (roomId <= 0L) {
            return 0L;
        }
        String effectiveSelectedIds = selectedFurnitureId > 0L ? String.valueOf(selectedFurnitureId) : StringUtils.text(selectedIds);
        if (effectiveSelectedIds.isEmpty()) {
            return 0L;
        }
        FurnitureDao furniture = furnitureDao();
        if (furniture == null) {
            return 0L;
        }
        long stateValue = NumberUtils.parseLong((StringUtils.text(parameterText) + ";").split(";", -1)[0]);
        long appliedCount = 0L;
        for (String idPart : effectiveSelectedIds.replace(',', ';').split(";", -1)) {
            long furnitureId = NumberUtils.parseLong(idPart);
            if (furnitureId > 0L && handlingFurnitureExistsInRoom(roomId, furnitureId)) {
                try {
                    furniture.updateSignLimited(furnitureId, stateValue);
                    Proc_6_151_78AC20(roomId, furnitureId, stateValue);
                    Proc_6_246_8024C0(roomId, FurniturePayloads.stateChanged(furnitureId, stateValue), 0);
                    appliedCount++;
                } catch (Exception ignored) {
                    // VB6 source suppresses helper failures.
                }
            }
        }
        return appliedCount;
    }

    public static boolean handlingRepresentedWiredSelectedItemsExist(long roomId, String selectedIds) {
        if (roomId <= 0L) {
            return false;
        }
        for (String idPart : StringUtils.text(selectedIds).replace(',', ';').split(";", -1)) {
            long furnitureId = NumberUtils.parseLong(idPart);
            if (furnitureId > 0L && !handlingFurnitureExistsInRoom(roomId, furnitureId)) {
                return false;
            }
        }
        return true;
    }

    public static long firstWireLong(String packetPayload, String packetCode) {
        String requestPayload = StringUtils.text(packetPayload);
        if (!StringUtils.text(packetCode).isEmpty() && requestPayload.startsWith(packetCode)) {
            requestPayload = requestPayload.substring(StringUtils.text(packetCode).length());
        }
        LongRef offset = new LongRef(1);
        long value = readWireLong(requestPayload, offset);
        return value > 0L ? value : NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
    }

    public static String wiredCachePath(String cacheFolder, long roomId) {
        return Path.of(Functions.applicationPath, "cache", StringUtils.text(cacheFolder), roomId + ".cache").toString();
    }

    public static String readWiredCache(String cacheFolder, long roomId) {
        return roomId <= 0L ? "" : Proc_6_239_7FC170(wiredCachePath(cacheFolder, roomId), 0, 0);
    }

    public static boolean handlingFurnitureExistsInRoom(long roomId, long furnitureId) {
        if (roomId <= 0L || furnitureId <= 0L) {
            return false;
        }
        try {
            FurnitureDao furniture = furnitureDao();
            return furniture != null && furniture.existsInRoom(furnitureId, roomId);
        } catch (Exception ignored) {
            // VB6 source suppresses helper failures.
            return false;
        }
    }

    public static String wiredEditRecordFromWire(String packetPayload, String packetCode, long wiredCode, boolean includeExtraValue) {
        String requestPayload = StringUtils.text(packetPayload);
        if (!StringUtils.text(packetCode).isEmpty() && requestPayload.startsWith(packetCode)) {
            requestPayload = requestPayload.substring(packetCode.length());
        }
        LongRef offset = new LongRef(1);
        long furnitureId = readWireLong(requestPayload, offset);
        if (furnitureId <= 0L) {
            furnitureId = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
        }
        if (furnitureId <= 0L || wiredCode <= 0L) {
            return "";
        }
        long parameterCount = readWireLong(requestPayload, offset);
        if (parameterCount < 0L || parameterCount > 100L) {
            return "";
        }
        String parameterValues = "";
        for (long parameterIndex = 0L; parameterIndex < parameterCount; parameterIndex++) {
            long parameterValue = readWireLong(requestPayload, offset);
            if (!parameterValues.isEmpty()) {
                parameterValues += ";";
            }
            parameterValues += parameterValue;
        }
        String textValue = left(Functions.Proc_10_10_80A7F0(readWireString(requestPayload, offset), 0, 0), 125);
        if (textValue.isEmpty()) {
            textValue = left(Functions.Proc_10_10_80A7F0(Functions.Proc_10_7_80A190(requestPayload, 0, 0), 0, 0), 125);
        }
        long selectedCount = readWireLong(requestPayload, offset);
        if (selectedCount < 0L || selectedCount > 100L) {
            return "";
        }
        String selectedIdsText = "";
        for (long selectedIndex = 0L; selectedIndex < selectedCount; selectedIndex++) {
            long selectedFurnitureId = readWireLong(requestPayload, offset);
            if (selectedFurnitureId <= 0L) {
                return "";
            }
            if (!selectedIdsText.isEmpty()) {
                selectedIdsText += ";";
            }
            selectedIdsText += selectedFurnitureId;
        }
        long extraValue = includeExtraValue ? readWireLong(requestPayload, offset) : 0L;
        return wiredRecordText(wiredCode, furnitureId, selectedIdsText, parameterValues, textValue,
            includeExtraValue ? String.valueOf(extraValue) : "");
    }

    public static String wiredRecordText(
        long wiredCode,
        long furnitureId,
        String selectedIdsText,
        String parameterValues,
        String textValue,
        String extraValue
    ) {
        return WiredPayloads.recordText(wiredCode, furnitureId, selectedIdsText, parameterValues, textValue, extraValue);
    }

    public static String wiredRecordMarker(String recordText) {
        return WiredPayloads.recordMarker(recordText);
    }

    public static String wiredCacheWithRecord(String cacheText, String recordText) {
        return WiredPayloads.cacheWithRecord(cacheText, recordText);
    }

    public static String wiredRecordField(String recordText, long fieldIndex) {
        return WiredPayloads.recordField(recordText, fieldIndex);
    }

    public static boolean wiredSelectedItemsExist(String selectedIds, String existingIds) {
        return WiredPayloads.selectedItemsExist(selectedIds, existingIds);
    }

    public static WiredApplyResult wiredApplySelected(
        String selectedIds,
        String parameterText,
        long selectedFurnitureId,
        String existingIds
    ) {
        WiredPayloads.ApplyResult applied = WiredPayloads.applySelected(
            selectedIds,
            parameterText,
            selectedFurnitureId,
            existingIds,
            FurniturePayloads::stateChanged);
        WiredApplyResult result = new WiredApplyResult();
        result.appliedCount = applied.appliedCount;
        result.statePayloads = applied.statePayloads;
        return result;
    }

    public static SongInfoRequest songInfoRequestFromWire(String packetPayload) {
        SongInfoRequest request = new SongInfoRequest();
        String requestPayload = StringUtils.text(packetPayload);
        if (requestPayload.startsWith("C]")) {
            requestPayload = requestPayload.substring(2);
        }
        LongRef offset = new LongRef(1);
        long requestedCount = readWireLong(requestPayload, offset);
        if (requestedCount <= 0L) {
            requestedCount = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
        }
        if (requestedCount > 60L) {
            requestedCount = 60L;
        }
        request.requestedCount = requestedCount;
        String requestedIds = "";
        for (long requestIndex = 0L; requestIndex < requestedCount; requestIndex++) {
            long cdId = readWireLong(requestPayload, offset);
            if (cdId > 0L) {
                if (!requestedIds.isEmpty()) {
                    requestedIds += ",";
                }
                requestedIds += cdId;
            }
        }
        request.requestedIds = requestedIds;
        return request;
    }

    public static String removeSoundMachineMarkers(String representedRoomCache, long jukeboxId, long activeDestinationId) {
        String cache = StringUtils.text(representedRoomCache);
        if (activeDestinationId > 0L) {
            cache = cache.replaceFirst(Pattern.quote("\1" + activeDestinationId + '\2'), "");
        }
        if (jukeboxId > 0L) {
            cache = cache.replaceFirst(Pattern.quote("\1" + jukeboxId + '\2'), "");
        }
        return cache;
    }

    public static JukeboxAddRequest jukeboxAddRequestFromWire(String packetPayload) {
        JukeboxAddRequest request = new JukeboxAddRequest();
        String requestPayload = StringUtils.text(packetPayload);
        if (requestPayload.startsWith("C" + '\177')) {
            requestPayload = requestPayload.substring(2);
        }
        LongRef offset = new LongRef(1);
        request.diskFurnitureId = readWireLong(requestPayload, offset);
        request.playlistOrder = readWireLong(requestPayload, offset);
        if (request.playlistOrder < 0L) {
            request.playlistOrder = 0L;
        }
        return request;
    }

    public static java.util.Optional<JukeboxRow> jukeboxRowForRoom(long roomId) {
        if (roomId <= 0L) {
            return java.util.Optional.empty();
        }
        JukeboxDao jukebox = jukeboxDao();
        if (jukebox == null) {
            return java.util.Optional.empty();
        }
        try {
            return jukebox.jukeboxInRoom(roomId);
        } catch (Exception ignored) {
            return java.util.Optional.empty();
        }
    }

    public static boolean jukeboxCanAddDisk(long playlistOrder, String maxOrderText, long playlistCount, long playlistLimit) {
        long effectiveLimit = playlistLimit <= 0L ? 100L : playlistLimit;
        if (playlistCount >= effectiveLimit) {
            return false;
        }
        String maxText = StringUtils.text(maxOrderText);
        long maxOrder = NumberUtils.parseLong(maxText);
        if (!maxText.isEmpty()) {
            return playlistOrder == maxOrder || playlistOrder == maxOrder + 1L;
        }
        return playlistOrder == 0L;
    }

    public static long jukeboxRemoveOrderFromWire(String packetPayload) {
        String requestPayload = StringUtils.text(packetPayload);
        if (requestPayload.startsWith("D@")) {
            requestPayload = requestPayload.substring(2);
        }
        LongRef offset = new LongRef(1);
        long playlistOrder = readWireLong(requestPayload, offset);
        return Math.max(0L, playlistOrder);
    }

    public static String[] badgeUpdateSelectionsFromWire(String packetPayload) {
        String requestPayload = StringUtils.text(packetPayload);
        if (requestPayload.startsWith("B^")) {
            requestPayload = requestPayload.substring(2);
        }
        String[] slots = new String[5];
        LongRef offset = new LongRef(1);
        for (int slotIndex = 0; slotIndex < slots.length; slotIndex++) {
            long hasBadge = readWireLong(requestPayload, offset);
            if (hasBadge == 1L) {
                slots[slotIndex] = Functions.Proc_10_11_80A9C0(readWireString(requestPayload, offset), 0, 0);
            } else {
                slots[slotIndex] = "";
            }
        }
        return slots;
    }

    public static long idRequestFromWire(String packetPayload, String prefix) {
        String requestPayload = StringUtils.text(packetPayload);
        if (!StringUtils.text(prefix).isEmpty() && requestPayload.startsWith(prefix)) {
            requestPayload = requestPayload.substring(prefix.length());
        }
        LongRef offset = new LongRef(1);
        long value = readWireLong(requestPayload, offset);
        if (value <= 0L) {
            value = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
        }
        return value;
    }

    public static RecyclerSelection recyclerSelectionFromWire(String packetPayload) {
        RecyclerSelection selection = new RecyclerSelection();
        String requestPayload = StringUtils.text(packetPayload);
        if (requestPayload.startsWith("F^")) {
            requestPayload = requestPayload.substring(2);
        }
        LongRef offset = new LongRef(1);
        selection.requestedCount = readWireLong(requestPayload, offset);
        if (selection.requestedCount != 5L) {
            return selection;
        }
        String selectedItems = "";
        for (long itemIndex = 0L; itemIndex < selection.requestedCount; itemIndex++) {
            long furnitureId = readWireLong(requestPayload, offset);
            if (furnitureId <= 0L) {
                return selection;
            }
            String token = String.valueOf(furnitureId);
            if (("," + selectedItems + ",").contains("," + token + ",")) {
                return selection;
            }
            if (!selectedItems.isEmpty()) {
                selectedItems += ",";
            }
            selectedItems += token;
        }
        selection.selectedItems = selectedItems;
        selection.valid = true;
        return selection;
    }

    public static String recyclerSelectionWhereClause(String selectedItems, String escapedUserId) {
        String whereClause = "";
        for (String item : StringUtils.text(selectedItems).split(",", -1)) {
            long furnitureId = NumberUtils.parseLong(item);
            if (furnitureId <= 0L) {
                continue;
            }
            if (!whereClause.isEmpty()) {
                whereClause += " OR ";
            }
            whereClause += "furnitures.id_owner='" + escapedUserId + "' AND furnitures.id_room IS NULL"
                + " AND furnitures.id='" + furnitureId + "' AND products.id=furnitures.id_product"
                + " AND products.is_recycleable='1'";
        }
        return whereClause;
    }

    public static long representedRecyclerRewardProduct() {
        try {
            RecyclerSettings recyclerSettings = Licence.recyclerSettings();
            if (recyclerSettings.hasRewardGroups()) {
                for (RecyclerSettings.RewardGroup rewardGroup : recyclerSettings.rewardGroups()) {
                    long chance = rewardGroup.chance();
                    if (chance > 0L && Functions.Proc_10_4_809CA0(1, chance, 0) == 1L) {
                        long productId = representedRandomProductFromList(rewardGroup.productIds());
                        if (productId > 0L) {
                            return productId;
                        }
                    }
                }
                for (RecyclerSettings.RewardGroup rewardGroup : recyclerSettings.rewardGroups()) {
                    long productId = representedRandomProductFromList(rewardGroup.productIds());
                    if (productId > 0L) {
                        return productId;
                    }
                }
            }
            RecyclerDao recycler = recyclerDao();
            List<Long> rewardProductIds = recycler == null ? List.of() : recycler.fallbackRewardProductIds();
            if (!rewardProductIds.isEmpty()) {
                int rowIndex = (int) NumberUtils.parseLong(Functions.Proc_10_4_809CA0(0, rewardProductIds.size() - 1L, 0));
                rowIndex = Math.max(0, Math.min(rowIndex, rewardProductIds.size() - 1));
                Long productId = rewardProductIds.get(rowIndex);
                return productId == null ? 0L : productId;
            }
            return 0L;
        } catch (Exception ignored) {
            // VB6 source suppresses helper failures.
            return 0L;
        }
    }

    public static long representedRandomProductFromList(String productList) {
        return representedRandomProductFromList(RecyclerSettings.productIds(productList));
    }

    public static long representedRandomProductFromList(List<Long> productIds) {
        try {
            if (productIds == null || productIds.isEmpty()) {
                return 0L;
            }
            int selectedIndex = (int) NumberUtils.parseLong(Functions.Proc_10_4_809CA0(0, productIds.size() - 1L, 0));
            selectedIndex = Math.max(0, Math.min(selectedIndex, productIds.size() - 1));
            return productIds.get(selectedIndex);
        } catch (Exception ignored) {
            // VB6 source suppresses helper failures.
            return 0L;
        }
    }

    public static String recyclerRewardSign() {
        return LocalDateTime.now().toString().replace('T', ' ');
    }

    public static long staffNestedUserIdFromWire(String packetPayload) {
        String requestPayload = StringUtils.text(packetPayload);
        long directValue = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
        if (directValue > 0L) {
            return directValue;
        }
        LongRef offset = new LongRef(1);
        String nestedPayload = readWireString(requestPayload, offset);
        long nestedValue = NumberUtils.parseLong(Functions.Proc_10_6_809F10(nestedPayload, 0, 0));
        if (nestedValue <= 0L) {
            nestedValue = NumberUtils.parseLong(nestedPayload);
        }
        if (nestedValue > 0L) {
            return nestedValue;
        }
        offset.value = 1L;
        return readWireLong(requestPayload, offset);
    }

    public static StaffChatRowsPayload staffRoomChatRowsPayload(List<StaffRoomChatRow> chatRows) {
        StaffPayloads.ChatRows chatRowsPayload = StaffPayloads.roomChatRows(chatRows);
        StaffChatRowsPayload result = new StaffChatRowsPayload();
        result.chatCount = chatRowsPayload.chatCount;
        result.payload = chatRowsPayload.payload;
        return result;
    }

    private static StaffModerationDao staffModerationDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new StaffModerationDao(database);
    }

    private static HelpDao helpDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new HelpDao(database);
    }

    private static UserDao userDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new UserDao(database);
    }

    private static ClubDao clubDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new ClubDao(database);
    }

    private static CatalogDao catalogDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new CatalogDao(database);
    }

    private static RoomDao roomDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new RoomDao(database);
    }

    private static FurnitureDao furnitureDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new FurnitureDao(database);
    }

    private static PackageDao packageDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new PackageDao(database);
    }

    private static VoucherDao voucherDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new VoucherDao(database);
    }

    private static PollDao pollDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new PollDao(database);
    }

    private static JukeboxDao jukeboxDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new JukeboxDao(database);
    }

    private static QuestDao questDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new QuestDao(database);
    }

    private static RecyclerDao recyclerDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new RecyclerDao(database);
    }

    private static BotDao botDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new BotDao(database);
    }

    private static TradeDao tradeDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new TradeDao(database);
    }

    private static MessengerDao messengerDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new MessengerDao(database);
    }

    private static boolean readRoomEventCommon(String packetPayload, LongRef offset, RoomEventPayload result, boolean requireText) {
        result.eventName = Functions.Proc_10_10_80A7F0(readWireString(packetPayload, offset));
        if (requireText && result.eventName.length() < 3) {
            return false;
        }
        result.eventDescription = Functions.Proc_10_10_80A7F0(readWireString(packetPayload, offset));
        if (requireText && result.eventDescription.length() < 3) {
            return false;
        }
        long tagCount = readWireLong(packetPayload, offset);
        if (tagCount < 0L || tagCount > 2L) {
            return false;
        }
        for (long tagIndex = 1L; tagIndex <= tagCount; tagIndex++) {
            String tagText = left(Functions.Proc_10_10_80A7F0(readWireString(packetPayload, offset)), 30).toLowerCase();
            if (tagIndex == 1L) {
                result.tagOne = tagText;
            } else if (tagIndex == 2L) {
                result.tagTwo = tagText;
            }
        }
        return true;
    }

    private static long optionalWireLong(String packetPayload, LongRef offset, long defaultValue) {
        long previousOffset = offset.value;
        long value = readWireLong(packetPayload, offset);
        return offset.value <= previousOffset ? defaultValue : value;
    }

    private static boolean containsDelimitedId(String idText, long wantedId) {
        String wanted = String.valueOf(wantedId);
        for (String idPart : StringUtils.text(idText).replace(',', ';').split(";", -1)) {
            if (wanted.equals(String.valueOf(NumberUtils.parseLong(idPart)))) {
                return true;
            }
        }
        return false;
    }

    private static void appendRow(StringBuilder rows, String rowText) {
        if (rows.length() > 0) {
            rows.append('\r');
        }
        rows.append(rowText);
    }

    private static String questRowsFromSource() {
        return questSettingsFromSource().rows();
    }

    private static QuestSettings questSettingsFromSource() {
        QuestSettings settings = Licence.questSettings();
        if (settings.hasRows()) {
            return settings;
        }
        QuestDao quests = questDao();
        if (quests == null) {
            return QuestSettings.fromLegacy("");
        }
        try {
            List<QuestSettings.QuestDefinitionRow> rows = new ArrayList<>();
            for (QuestDao.QuestDefinition quest : quests.questDefinitions()) {
                if (quest != null) {
                    rows.add(new QuestSettings.QuestDefinitionRow(
                        quest.questId(),
                        quest.level(),
                        quest.name(),
                        quest.legacyNullSlot(),
                        quest.reward(),
                        quest.rewardType(),
                        quest.requiredAction(),
                        quest.additionalId(),
                        quest.campaignId(),
                        quest.activityAmount(),
                        quest.waitAmount(),
                        11));
                }
            }
            return QuestSettings.fromDefinitions(rows);
        } catch (Exception ignored) {
            return QuestSettings.fromLegacy("");
        }
    }

    private static List<QuestSettings.UserQuestListRow> userQuestRowsWithRemainingWait(
        QuestDao quests,
        List<QuestDao.UserQuestListRow> userQuestRows
    )
        throws Exception {

        List<QuestSettings.UserQuestListRow> rows = new ArrayList<>();
        for (QuestDao.UserQuestListRow row : userQuestRows == null ? List.<QuestDao.UserQuestListRow>of() : userQuestRows) {
            String timeNextText = StringUtils.text(row.timeNext());
            long remainingWait = 0L;
            if (!timeNextText.isEmpty() && !"0".equals(timeNextText)) {
                remainingWait = quests.remainingWait(timeNextText);
            }
            rows.add(new QuestSettings.UserQuestListRow(
                row.questId(),
                row.level(),
                row.timestampDone(),
                row.timestampAccepted(),
                row.timeNext(),
                row.progress(),
                remainingWait,
                7));
        }
        return rows;
    }

    private static String left(String value, int maxLength) {
        String text = StringUtils.text(value);
        return text.length() <= maxLength ? text : text.substring(0, maxLength);
    }

    private static int firstPositiveIndex(String text, char... chars) {
        int result = -1;
        for (char ch : chars) {
            int at = text.indexOf(ch);
            if (at >= 0 && (result < 0 || at < result)) {
                result = at;
            }
        }
        return result;
    }

}
