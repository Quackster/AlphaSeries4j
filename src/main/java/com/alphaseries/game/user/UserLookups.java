package com.alphaseries.game.user;

import com.alphaseries.config.PermissionMatrix;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.game.session.SessionState;
import com.alphaseries.game.social.SocialLookups;
import com.alphaseries.messages.outgoing.UserPayloads;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class UserLookups {
    private UserLookups() {
    }

    public record UserRequest(String userId) {
        public boolean valid() {
            return NumberUtils.parseLong(userId) > 0L;
        }
    }

    public static UserRequest userRequest(int socketIndex, UserDao users) {
        if (socketIndex <= 0) {
            return new UserRequest("");
        }
        long userId = SessionState.instance().sessionUserIdBySocket(socketIndex);
        if (userId <= 0L && users != null) {
            try {
                userId = users.userIdBySocket(socketIndex);
            } catch (Exception ignored) {
                userId = 0L;
            }
        }
        return new UserRequest(userId > 0L ? String.valueOf(userId) : "");
    }

    public static boolean hasPermission(String userId, String permissionName, UserDao users, PermissionMatrix permissions) {
        long rankIndex = rank(userId, users);
        long hcLevel = hcLevel(userId, users);
        PermissionMatrix permissionMatrix = permissions == null ? PermissionMatrix.empty() : permissions;
        return permissionMatrix.allows(rankIndex, "", permissionName, hcLevel);
    }

    public static long rank(String userId, UserDao users) {
        if (users == null) {
            return 0L;
        }
        try {
            return users.rankLevel(NumberUtils.parseLong(userId));
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static long hcLevel(String userId, UserDao users) {
        long hcLevel = 0L;
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

    public static String sessionId(String userId, UserDao users) {
        if (StringUtils.text(userId).isEmpty() || "0".equals(StringUtils.text(userId)) || users == null) {
            return "";
        }
        try {
            return users.sessionId(NumberUtils.parseLong(userId));
        } catch (Exception ignored) {
            return "";
        }
    }

    public static int socketIndexForUserName(String userName, UserDao users) {
        if (StringUtils.text(userName).isEmpty() || users == null) {
            return 0;
        }
        try {
            return (int) users.socketByName(userName);
        } catch (Exception ignored) {
            return 0;
        }
    }

    public static String ownProfilePayload(String userId, UserDao users) {
        try {
            long numericUserId = NumberUtils.parseLong(userId);
            if (numericUserId <= 0L || users == null) {
                return "";
            }
            return users.ownProfile(numericUserId)
                .map(UserPayloads::ownProfile)
                .orElse("");
        } catch (Exception ignored) {
            return "";
        }
    }

    public static String ownProfilePayload(UserRequest request, UserDao users) {
        if (request == null || !request.valid()) {
            return "";
        }
        return ownProfilePayload(request.userId(), users);
    }

    /**
     * Original function: Proc_6_20_6E88E0.
     */
    public static String rankAndStaffStatePayload(String userId, UserDao users, PermissionMatrix permissions) {
        try {
            if (StringUtils.text(userId).isEmpty() || "0".equals(StringUtils.text(userId))) {
                return "";
            }
            long rankIndex = rank(userId, users);
            long staffFlag = hasPermission(userId, "fuse_client_staff", users, permissions) ? 1L : 0L;
            return UserPayloads.rankAndStaffState(rankIndex, staffFlag);
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_15_6E1900.
     */
    public static String wardrobeSlotsPayload(String userId, UserDao users, PermissionMatrix permissions) {
        try {
            if (StringUtils.text(userId).isEmpty() || "0".equals(StringUtils.text(userId))
                || !hasPermission(userId, "fuse_use_wardrobe", users, permissions)) {
                return "";
            }
            long maxSlots = hasPermission(userId, "fuse_larger_wardrobe", users, permissions) ? 10L : 5L;
            if (users == null) {
                return UserPayloads.wardrobeSlots(List.of(), maxSlots).payload();
            }
            return UserPayloads.wardrobeSlots(users.wardrobeRows(NumberUtils.parseLong(userId)), maxSlots).payload();
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_16_6E2320.
     */
    public static String saveWardrobeSlotPayload(
        String userId,
        String packetPayload,
        String figureData,
        UserDao users,
        PermissionMatrix permissions
    ) {
        try {
            UserWire.WardrobeSlotRequest request = UserWire.wardrobeSlotRequest(packetPayload);
            String normalizedUserId = StringUtils.text(userId);
            if (normalizedUserId.isEmpty() || "0".equals(normalizedUserId)
                || !hasPermission(normalizedUserId, "fuse_use_wardrobe", users, permissions)) {
                return "";
            }
            long maxSlots = hasPermission(normalizedUserId, "fuse_larger_wardrobe", users, permissions) ? 10L : 5L;
            if (request.slotId() < 1L || request.slotId() > maxSlots
                || (!"M".equals(request.genderText()) && !"F".equals(request.genderText()))
                || !UserValidation.isValidWardrobeFigure(request.figureText(), request.genderText(), figureData)) {
                return "";
            }
            if (users != null) {
                long numericUserId = NumberUtils.parseLong(normalizedUserId);
                users.deleteWardrobeSlot(numericUserId, request.slotId());
                users.insertWardrobeSlot(numericUserId, request.slotId(), request.figureText(), request.genderText());
            }
            return wardrobeSlotsPayload(normalizedUserId, users, permissions);
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_17_6E48D0.
     */
    public static String updateTutorialClothesPayload(
        String userId,
        String packetPayload,
        String figureData,
        UserDao users
    ) {
        try {
            UserWire.TutorialClothesRequest request = UserWire.tutorialClothesRequest(packetPayload);
            String normalizedUserId = StringUtils.text(userId);
            if (normalizedUserId.isEmpty() || "0".equals(normalizedUserId)
                || (!"M".equals(request.genderText()) && !"F".equals(request.genderText()))
                || !UserValidation.isValidWardrobeFigure(request.figureText(), request.genderText(), figureData)) {
                return "";
            }
            long numericUserId = NumberUtils.parseLong(normalizedUserId);
            String mottoText = "";
            if (users != null) {
                users.updateTutorialClothes(numericUserId, request.genderText(), request.figureText());
                mottoText = users.motto(numericUserId);
            }
            return UserPayloads.identityRefresh(numericUserId, mottoText, request.figureText(), request.genderText());
        } catch (Exception ignored) {
            return "";
        }
    }

    public static long updateSoundSetting(String userId, UserWire.SoundSettingRequest request, UserDao users) {
        try {
            long soundSetting = request == null ? 0L : request.soundSetting();
            long numericUserId = NumberUtils.parseLong(userId);
            if (numericUserId <= 0L || soundSetting <= 0L || users == null) {
                return 0L;
            }
            users.updateSoundSetting(numericUserId, soundSetting);
            return soundSetting;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_143_76BB80.
     */
    public static String activityPointBalancePayload(String userId, UserDao users) {
        try {
            long numericUserId = NumberUtils.parseLong(userId);
            if (numericUserId <= 0L || users == null) {
                return "";
            }
            UserDao.ActivityPointBalance balance = users.activityPointBalance(numericUserId).orElse(null);
            if (balance == null) {
                return "";
            }
            return UserPayloads.activityPointBalance(
                balance.pointTypeOne(),
                balance.pointTypeTwo(),
                balance.pointTypeThree(),
                balance.pointTypeFour());
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_40_711770.
     */
    public static AvatarNameUpdate validateOrChangeAvatarName(
        String userId,
        long socketIndex,
        boolean checkOnly,
        String candidateName,
        UserDao users
    ) {
        try {
            long numericUserId = NumberUtils.parseLong(userId);
            String normalizedCandidate = StringUtils.text(candidateName).trim();
            if (numericUserId <= 0L || socketIndex <= 0L || users == null) {
                return AvatarNameUpdate.empty();
            }
            String oldName = users.name(numericUserId);
            users.gender(numericUserId);
            long existingCount = users.countByName(normalizedCandidate);
            long validationCode = UserValidation.avatarNameValidationCode(normalizedCandidate, oldName, existingCount);
            String validationPayload = UserPayloads.avatarNameValidation(validationCode, normalizedCandidate);
            if (checkOnly || validationCode != 0L) {
                return new AvatarNameUpdate(validationCode, normalizedCandidate, validationPayload, false);
            }
            users.updateName(numericUserId, normalizedCandidate);
            users.insertIdentityLog(oldName, normalizedCandidate, socketIndex);
            return new AvatarNameUpdate(0L, normalizedCandidate, validationPayload, true);
        } catch (Exception ignored) {
            return AvatarNameUpdate.empty();
        }
    }

    /**
     * Original function: Proc_6_101_749540.
     */
    public static UserPayloads.EffectListPayload effectListPayload(String userId, UserDao users) {
        try {
            long numericUserId = NumberUtils.parseLong(userId);
            if (numericUserId <= 0L || users == null) {
                return UserPayloads.effectList(List.of());
            }
            return UserPayloads.effectList(users.userEffectSummaries(numericUserId));
        } catch (Exception ignored) {
            return UserPayloads.effectList(List.of());
        }
    }

    /**
     * Original function: Proc_6_102_749C50.
     */
    public static UserEffectActivation activateUserEffect(String userId, long effectId, UserDao users) {
        return activateUserEffect(userId, effectId, 0L, users);
    }

    /**
     * Original function: Proc_6_102_749C50.
     */
    public static UserEffectActivation activateUserEffect(String userId, long effectId, long roomUserIndex, UserDao users) {
        try {
            long numericUserId = NumberUtils.parseLong(userId);
            if (numericUserId <= 0L || effectId <= 0L || users == null) {
                return UserEffectActivation.empty();
            }
            UserEffectActivationRow effect = users.userEffectActivation(numericUserId, effectId).orElse(null);
            if (effect == null || effect.rowId() <= 0L || effect.rentSeconds() <= 0L) {
                return UserEffectActivation.empty();
            }
            users.activateUserEffect(effect.rowId());
            return new UserEffectActivation(effectId, effect.rentSeconds(),
                UserPayloads.effectActivated(effectId, effect.rentSeconds()),
                roomUserIndex > 0L ? SocialLookups.roomUserEffectPayload(roomUserIndex, effectId) + "H" : "");
        } catch (Exception ignored) {
            return UserEffectActivation.empty();
        }
    }

    /**
     * Original function: Proc_6_103_74A510.
     */
    public static List<UserEffectExpiry> expiredUserEffects(UserDao users) {
        try {
            if (users == null) {
                return List.of();
            }
            List<UserEffectExpiry> expiries = new ArrayList<>();
            for (ExpiredUserEffectRow effect : users.expiredUserEffects()) {
                if (effect.socketIndex() > 0L && effect.effectId() > 0L) {
                    expiries.add(new UserEffectExpiry(
                        effect.socketIndex(),
                        effect.effectId(),
                        UserPayloads.effectExpired(effect.effectId()),
                        SocialLookups.roomUserEffectClearedPayload(effect.socketIndex())));
                }
            }
            users.deleteExpiredUserEffects();
            return List.copyOf(expiries);
        } catch (Exception ignored) {
            return List.of();
        }
    }

    /**
     * Original function: Proc_6_230_7F3D20.
     */
    public static String updateMottoPayload(String userId, UserWire.MottoRequest request, UserDao users) {
        try {
            long numericUserId = NumberUtils.parseLong(userId);
            if (numericUserId <= 0L || request == null || users == null) {
                return "";
            }
            String mottoText = request.mottoText();
            users.updateMotto(numericUserId, mottoText);
            UserDao.UserIdentity identity = users.findIdentity(numericUserId)
                .orElse(new UserDao.UserIdentity(numericUserId, 0L, "", "", "M"));
            String figureText = StringUtils.text(identity.figure());
            String genderText = StringUtils.left(StringUtils.text(identity.gender()).toUpperCase(), 1);
            if (!"M".equals(genderText) && !"F".equals(genderText)) {
                genderText = "M";
            }
            return UserPayloads.identityRefresh(numericUserId, mottoText, figureText, genderText);
        } catch (Exception ignored) {
            return "";
        }
    }

    public static String updateMottoPayload(UserRequest userRequest, UserWire.MottoRequest request, UserDao users) {
        if (userRequest == null || !userRequest.valid()) {
            return "";
        }
        return updateMottoPayload(userRequest.userId(), request, users);
    }
}
