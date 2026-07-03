package com.alphaseries.game.user;

import com.alphaseries.config.AppConfigState;
import com.alphaseries.config.AppPaths;
import com.alphaseries.dao.mysql.DaoProvider;
import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.game.navigator.NavigatorRequests;
import com.alphaseries.game.social.SocialWire;
import com.alphaseries.messages.outgoing.RoomPayloads;
import com.alphaseries.messages.outgoing.UserPayloads;
import com.alphaseries.server.runtime.SessionLookups;
import com.alphaseries.server.runtime.SocketDelivery;
import com.alphaseries.util.FileUtils;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public final class UserPacketHandlers {
    private UserPacketHandlers() {
    }

    /**
     * Original function: Proc_6_15_6E1900.
     */
    public static void sendWardrobeSlots(int socketIndex) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            String payload = UserLookups.wardrobeSlotsPayload(
                NumberUtils.parseLong(userId), userDao(), AppConfigState.instance().permissionMatrix());
            if (!payload.isEmpty()) {
                SocketDelivery.sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_16_6E2320.
     */
    public static void saveWardrobeSlot(int socketIndex, UserWire.WardrobeSlotRequest request) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            String figureData = FileUtils.readTextFile(AppPaths.applicationPath() + "/figuredata.cache");
            String payload = UserLookups.saveWardrobeSlotPayload(
                NumberUtils.parseLong(userId), request, figureData, userDao(), AppConfigState.instance().permissionMatrix());
            if (!payload.isEmpty()) {
                SocketDelivery.sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_17_6E48D0.
     */
    public static void updateTutorialClothes(int socketIndex, UserWire.TutorialClothesRequest request) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            String figureData = FileUtils.readTextFile(AppPaths.applicationPath() + "/figuredata.cache");
            String payload = UserLookups.updateTutorialClothesPayload(
                NumberUtils.parseLong(userId), request, figureData, userDao());
            if (!payload.isEmpty()) {
                SocketDelivery.sendToSocket(socketIndex, payload);
                SocketDelivery.broadcastToCurrentRoom(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_20_6E88E0.
     */
    public static void sendRankAndStaffState(int socketIndex) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            String payload = UserLookups.rankAndStaffStatePayload(
                NumberUtils.parseLong(userId), userDao(), AppConfigState.instance().permissionMatrix());
            if (!payload.isEmpty()) {
                SocketDelivery.sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_38_70FD10.
     */
    public static long changeAvatarName(int socketIndex, UserWire.AvatarNameRequest request) {
        try {
            return validateOrChangeAvatarName(socketIndex, false, request.candidateName());
        } catch (Exception ignored) {
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_39_711650.
     */
    public static long checkAvatarName(int socketIndex, UserWire.AvatarNameRequest request) {
        try {
            return validateOrChangeAvatarName(socketIndex, true, request.candidateName());
        } catch (Exception ignored) {
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_230_7F3D20.
     */
    public static String updateMotto(int socketIndex, UserWire.MottoRequest mottoRequest) {
        try {
            UserLookups.UserRequest request = UserLookups.userRequest(socketIndex, userDao());
            String payload = UserLookups.updateMottoPayload(request, mottoRequest, userDao());
            if (payload.isEmpty()) {
                return "";
            }
            SocketDelivery.sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_231_7F4510.
     */
    public static String sendGuideInvitation(int socketIndex) {
        try {
            String payload = UserPayloads.guideInvitation();
            SocketDelivery.sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_237_7F9ED0.
     */
    public static String sendOwnProfile(int socketIndex) {
        try {
            UserLookups.UserRequest request = UserLookups.userRequest(socketIndex, userDao());
            String payload = UserLookups.ownProfilePayload(request, userDao());
            if (!payload.isEmpty()) {
                SocketDelivery.sendToSocket(socketIndex, payload);
            }
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_238_7FA670.
     */
    public static String awardTimedActivityPoints(int socketIndex) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            UserActivityPoints.AwardBatch awardBatch = UserActivityPoints.timedActivityPointAwardBatch(
                socketIndex, NumberUtils.parseLong(userId), AppConfigState.instance().settingsCache(), userDao());
            SocketDelivery.sendToSocket(socketIndex, awardBatch.deliveryPayloads());
            return awardBatch.payload();
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_143_76BB80.
     */
    public static void sendActivityPointBalance(int socketIndex) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            String payload = UserLookups.activityPointBalancePayload(NumberUtils.parseLong(userId), userDao());
            if (!payload.isEmpty()) {
                SocketDelivery.sendToSocket(socketIndex, payload);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void updateSoundSetting(int socketIndex, UserWire.SoundSettingRequest request) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            UserLookups.updateSoundSetting(NumberUtils.parseLong(userId), request, userDao());
        } catch (Exception ignored) {
            // VB6 source suppresses dispatcher helper failures.
        }
    }

    /**
     * Original function: Proc_6_101_749540.
     */
    public static long sendEffectList(int socketIndex) {
        long listedEffects = 0L;
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            UserPayloads.EffectListPayload effectPayload =
                UserLookups.effectListPayload(NumberUtils.parseLong(userId), userDao());
            listedEffects = effectPayload.listedEffects();
            SocketDelivery.sendToSocket(socketIndex, effectPayload.payload());
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
        return listedEffects;
    }

    /**
     * Original function: Proc_6_102_749C50.
     */
    public static long activateEffect(int socketIndex, SocialWire.EffectRequest request) {
        try {
            long effectId = request.effectId();
            if (effectId <= 0L) {
                return 0L;
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            UserEffectActivation activation =
                UserLookups.activateUserEffect(NumberUtils.parseLong(userId), effectId, socketIndex, userDao());
            if (!activation.valid()) {
                return 0L;
            }
            SocketDelivery.sendToSocket(socketIndex, activation.payload());
            if (!activation.broadcastPayload().isEmpty()) {
                SocketDelivery.broadcastToCurrentRoom(socketIndex, activation.broadcastPayload());
            }
            return activation.effectId();
        } catch (Exception ignored) {
            return 0L;
        }
    }

    private static long validateOrChangeAvatarName(int socketIndex, boolean checkOnly, String candidateName) {
        try {
            candidateName = StringUtils.text(candidateName).trim();
            if (socketIndex <= 0) {
                return 0L;
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            long numericUserId = NumberUtils.parseLong(userId);
            AvatarNameUpdate update = UserLookups.validateOrChangeAvatarName(
                numericUserId, socketIndex, checkOnly, candidateName, userDao());
            if (update.validationPayload().isEmpty()) {
                return 0L;
            }
            SocketDelivery.sendToSocket(socketIndex, update.validationPayload());
            long roomId = update.changed() ? SessionLookups.currentRoomId(socketIndex, userId) : 0L;
            if (roomId > 0L) {
                long roomUserIndex = SessionLookups.representedRoomUserIndex(socketIndex, userId);
                SocketDelivery.broadcastToCurrentRoom(socketIndex,
                    UserPayloads.roomUserNameChanged(numericUserId, roomUserIndex, update.candidateName()));
                SocketDelivery.broadcastToCurrentRoom(socketIndex, NavigatorRequests.roomListPayload(roomId, roomDao()));
                SocketDelivery.broadcastToCurrentRoom(socketIndex, RoomPayloads.entryUpdated(roomId));
            }
            return update.validationCode();
        } catch (Exception ignored) {
            return 0L;
        }
    }

    private static UserDao userDao() {
        return DaoProvider.userDao();
    }

    private static RoomDao roomDao() {
        return DaoProvider.roomDao();
    }
}
