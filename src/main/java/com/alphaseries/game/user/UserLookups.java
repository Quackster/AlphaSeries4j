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

    public record UserRequest(long userId) {
        public boolean valid() {
            return userId > 0L;
        }
    }

    public static UserRequest userRequest(int socketIndex, UserDao users) {
        if (socketIndex <= 0) {
            return new UserRequest(0L);
        }
        long userId = SessionState.instance().sessionUserIdBySocket(socketIndex);
        if (userId <= 0L && users != null) {
            try {
                userId = users.userIdBySocket(socketIndex);
            } catch (Exception ignored) {
                userId = 0L;
            }
        }
        return new UserRequest(userId);
    }

    public static boolean hasPermission(long userId, String permissionName, UserDao users, PermissionMatrix permissions) {
        long rankIndex = rank(userId, users);
        long hcLevel = hcLevel(userId, users);
        PermissionMatrix permissionMatrix = permissions == null ? PermissionMatrix.empty() : permissions;
        return permissionMatrix.allows(rankIndex, "", permissionName, hcLevel);
    }

    public static long rank(long userId, UserDao users) {
        if (users == null) {
            return 0L;
        }
        try {
            return users.rankLevel(userId);
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static long hcLevel(long userId, UserDao users) {
        long hcLevel = 0L;
        if (users != null) {
            try {
                hcLevel = users.hcLevel(userId);
            } catch (Exception ignored) {
                hcLevel = 0L;
            }
        }
        if (hcLevel < 0L) {
            return 0L;
        }
        return Math.min(hcLevel, 2L);
    }

    public static String sessionId(long userId, UserDao users) {
        if (userId <= 0L || users == null) {
            return "";
        }
        try {
            return users.sessionId(userId);
        } catch (Exception ignored) {
            return "";
        }
    }

    public static String nameByIdText(String userId, UserDao users) {
        try {
            if (StringUtils.text(userId).isEmpty() || "0".equals(StringUtils.text(userId)) || users == null) {
                return "";
            }
            return users.name(NumberUtils.parseLong(userId));
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

    public static String ownProfilePayload(UserRequest request, UserDao users) {
        if (request == null || !request.valid()) {
            return "";
        }
        try {
            long userId = request.userId();
            if (userId <= 0L || users == null) {
                return "";
            }
            return users.ownProfile(userId)
                .map(UserPayloads::ownProfile)
                .orElse("");
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_20_6E88E0.
     */
    public static String rankAndStaffStatePayload(long userId, UserDao users, PermissionMatrix permissions) {
        try {
            if (userId <= 0L) {
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
    public static String wardrobeSlotsPayload(long userId, UserDao users, PermissionMatrix permissions) {
        try {
            if (userId <= 0L) {
                return "";
            }
            if (!hasPermission(userId, "fuse_use_wardrobe", users, permissions)) {
                return "";
            }
            long maxSlots = hasPermission(userId, "fuse_larger_wardrobe", users, permissions) ? 10L : 5L;
            if (users == null) {
                return UserPayloads.wardrobeSlots(List.of(), maxSlots).payload();
            }
            return UserPayloads.wardrobeSlots(users.wardrobeRows(userId), maxSlots).payload();
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_16_6E2320.
     */
    public static String saveWardrobeSlotPayload(
        long userId,
        UserWire.WardrobeSlotRequest request,
        String figureData,
        UserDao users,
        PermissionMatrix permissions
    ) {
        try {
            if (userId <= 0L
                || !hasPermission(userId, "fuse_use_wardrobe", users, permissions)) {
                return "";
            }
            long maxSlots = hasPermission(userId, "fuse_larger_wardrobe", users, permissions) ? 10L : 5L;
            if (request.slotId() < 1L || request.slotId() > maxSlots
                || (!"M".equals(request.genderText()) && !"F".equals(request.genderText()))
                || !UserValidation.isValidWardrobeFigure(request.figureText(), request.genderText(), figureData)) {
                return "";
            }
            if (users != null) {
                users.deleteWardrobeSlot(userId, request.slotId());
                users.insertWardrobeSlot(userId, request.slotId(), request.figureText(), request.genderText());
            }
            return wardrobeSlotsPayload(userId, users, permissions);
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_17_6E48D0.
     */
    public static String updateTutorialClothesPayload(
        long userId,
        UserWire.TutorialClothesRequest request,
        String figureData,
        UserDao users
    ) {
        try {
            if (userId <= 0L
                || (!"M".equals(request.genderText()) && !"F".equals(request.genderText()))
                || !UserValidation.isValidWardrobeFigure(request.figureText(), request.genderText(), figureData)) {
                return "";
            }
            String mottoText = "";
            if (users != null) {
                users.updateTutorialClothes(userId, request.genderText(), request.figureText());
                mottoText = users.motto(userId);
            }
            return UserPayloads.identityRefresh(userId, mottoText, request.figureText(), request.genderText());
        } catch (Exception ignored) {
            return "";
        }
    }

    public static long updateSoundSetting(long userId, UserWire.SoundSettingRequest request, UserDao users) {
        try {
            long soundSetting = request == null ? 0L : request.soundSetting();
            if (userId <= 0L || soundSetting <= 0L || users == null) {
                return 0L;
            }
            users.updateSoundSetting(userId, soundSetting);
            return soundSetting;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_143_76BB80.
     */
    public static String activityPointBalancePayload(long userId, UserDao users) {
        try {
            if (userId <= 0L || users == null) {
                return "";
            }
            UserDao.ActivityPointBalance balance = users.activityPointBalance(userId).orElse(null);
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
        long userId,
        long socketIndex,
        boolean checkOnly,
        String candidateName,
        UserDao users
    ) {
        try {
            String normalizedCandidate = StringUtils.text(candidateName).trim();
            if (userId <= 0L || socketIndex <= 0L || users == null) {
                return AvatarNameUpdate.empty();
            }
            String oldName = users.name(userId);
            users.gender(userId);
            long existingCount = users.countByName(normalizedCandidate);
            long validationCode = UserValidation.avatarNameValidationCode(normalizedCandidate, oldName, existingCount);
            String validationPayload = UserPayloads.avatarNameValidation(validationCode, normalizedCandidate);
            if (checkOnly || validationCode != 0L) {
                return new AvatarNameUpdate(validationCode, normalizedCandidate, validationPayload, false);
            }
            users.updateName(userId, normalizedCandidate);
            users.insertIdentityLog(oldName, normalizedCandidate, socketIndex);
            return new AvatarNameUpdate(0L, normalizedCandidate, validationPayload, true);
        } catch (Exception ignored) {
            return AvatarNameUpdate.empty();
        }
    }

    /**
     * Original function: Proc_6_101_749540.
     */
    public static UserPayloads.EffectListPayload effectListPayload(long userId, UserDao users) {
        try {
            if (userId <= 0L || users == null) {
                return UserPayloads.effectList(List.of());
            }
            return UserPayloads.effectList(users.userEffectSummaries(userId));
        } catch (Exception ignored) {
            return UserPayloads.effectList(List.of());
        }
    }

    /**
     * Original function: Proc_6_102_749C50.
     */
    public static UserEffectActivation activateUserEffect(long userId, long effectId, UserDao users) {
        return activateUserEffect(userId, effectId, 0L, users);
    }

    /**
     * Original function: Proc_6_102_749C50.
     */
    public static UserEffectActivation activateUserEffect(long userId, long effectId, long roomUserIndex, UserDao users) {
        try {
            if (userId <= 0L || effectId <= 0L || users == null) {
                return UserEffectActivation.empty();
            }
            UserEffectActivationRow effect = users.userEffectActivation(userId, effectId).orElse(null);
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
    public static String updateMottoPayload(UserRequest userRequest, UserWire.MottoRequest request, UserDao users) {
        if (userRequest == null || !userRequest.valid()) {
            return "";
        }
        try {
            long userId = userRequest.userId();
            if (userId <= 0L || request == null || users == null) {
                return "";
            }
            String mottoText = request.mottoText();
            users.updateMotto(userId, mottoText);
            UserDao.UserIdentity identity = users.findIdentity(userId)
                .orElse(new UserDao.UserIdentity(userId, 0L, "", "", "M"));
            String figureText = StringUtils.text(identity.figure());
            String genderText = StringUtils.left(StringUtils.text(identity.gender()).toUpperCase(), 1);
            if (!"M".equals(genderText) && !"F".equals(genderText)) {
                genderText = "M";
            }
            return UserPayloads.identityRefresh(userId, mottoText, figureText, genderText);
        } catch (Exception ignored) {
            return "";
        }
    }
}
