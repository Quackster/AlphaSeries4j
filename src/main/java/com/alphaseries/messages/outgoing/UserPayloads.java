package com.alphaseries.messages.outgoing;

import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.game.user.OwnProfileRow;
import com.alphaseries.game.user.UserEffectSummaryRow;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.StringUtils;

import java.util.List;

public final class UserPayloads {
    private UserPayloads() {
    }

    public static String creditsRefresh(long creditsValue) {
        return PacketBuilder.message("@F")
            .appendRaw(creditsValue)
            .appendString(".0")
            .build();
    }

    public static String errorCode(long firstValue, long secondValue) {
        return PacketBuilder.message("Dk")
            .appendInt(firstValue)
            .appendInt(secondValue)
            .build();
    }

    public static String activityPointRefresh(long pointType, long pointsValue) {
        return PacketBuilder.message("Fv")
            .appendInt(pointsValue)
            .appendRaw('H')
            .appendInt(pointType)
            .build();
    }

    public static String activityPointRefreshes(long... pointValues) {
        PacketBuilder payload = PacketBuilder.create();
        for (long pointType = 0L; pointType <= 4L; pointType++) {
            long pointsValue = pointValues != null && pointType < pointValues.length ? pointValues[(int) pointType] : 0L;
            payload.appendRaw(activityPointRefresh(pointType, pointsValue));
        }
        return payload.build();
    }

    public static String activityPointBalance(long pointTypeOne, long pointTypeTwo, long pointTypeThree, long pointTypeFour) {
        long[] pointValues = {pointTypeOne, pointTypeTwo, pointTypeThree, pointTypeFour};
        PacketBuilder itemPayload = PacketBuilder.create();
        for (long pointType = 1L; pointType <= 4L; pointType++) {
            itemPayload
                .appendInt(pointType)
                .appendInt(pointValues[(int) pointType - 1]);
        }
        return PacketBuilder.message("M@")
            .appendInt(pointValues.length)
            .appendRaw(itemPayload)
            .build();
    }

    public static String rankAndStaffState(long rankIndex, long staffFlag) {
        return PacketBuilder.create()
            .appendRaw('0')
            .appendRaw(PacketBuilder.message("@B")
                .appendInt(rankIndex)
                .appendInt(rankIndex)
                .appendInt(staffFlag))
            .build();
    }

    public static String roomAlert(String alertType, String alertText) {
        return PacketBuilder.message("Ba")
            .appendString(alertType)
            .appendString(alertText)
            .build();
    }

    public static String emailValidated(long emailState) {
        long stateValue = emailState == 0L ? 1L : emailState;
        return PacketBuilder.message("L}")
            .appendString(stateValue)
            .appendInt(1L)
            .appendInt(1L)
            .appendRaw("HH")
            .build();
    }

    public static String emailStatus(long emailState) {
        return PacketBuilder.message("DX")
            .appendInt(emailState)
            .build();
    }

    public static String identityRefresh(long userId, String mottoText, String figureText, String genderText) {
        return PacketBuilder.message("DJ")
            .appendInt(userId)
            .appendString(mottoText)
            .appendString(genderText)
            .appendString(figureText)
            .build();
    }

    public static String avatarNameValidation(long validationCode, String candidateName) {
        return PacketBuilder.message("H{")
            .appendInt(validationCode)
            .appendString(candidateName)
            .build();
    }

    public static String roomUserNameChanged(long userId, long roomUserIndex, String candidateName) {
        return PacketBuilder.message("H|")
            .appendInt(userId)
            .appendInt(roomUserIndex)
            .appendString(candidateName)
            .build();
    }

    public static String wardrobeSlot(long slotId, String figureText, String genderText) {
        return PacketBuilder.create()
            .appendInt(slotId)
            .appendString(figureText)
            .appendString(genderText)
            .build();
    }

    public static WardrobePayload wardrobeSlots(List<UserDao.WardrobeSlotRow> rows, long maxSlots) {
        long slotCount = 0L;
        PacketBuilder slotPayload = PacketBuilder.create();
        for (UserDao.WardrobeSlotRow row : rows == null ? List.<UserDao.WardrobeSlotRow>of() : rows) {
            if (row != null && row.slotId() >= 1L && row.slotId() <= maxSlots) {
                slotPayload.appendRaw(wardrobeSlot(row.slotId(), row.figure(), normalizedGender(row.gender())));
                slotCount++;
            }
        }
        return new WardrobePayload(slotCount, PacketBuilder.message("DK")
            .appendInt(slotCount)
            .appendRaw(slotPayload)
            .build());
    }

    public static String representedChat(long roomUserIndex, String filteredText, long gestureId, long chatType) {
        String prefix = chatType == 1L ? "@Y" : "@X";
        return PacketBuilder.message(prefix)
            .appendInt(roomUserIndex)
            .appendString(filteredText)
            .appendInt(gestureId)
            .build();
    }

    public static String ownProfile(OwnProfileRow row) {
        if (row == null || row.userId() <= 0L) {
            return "";
        }
        return PacketBuilder.message("@E")
            .appendString(row.userId())
            .appendString(row.name())
            .appendString(row.motto())
            .appendString(normalizedGender(row.gender()))
            .appendString("")
            .appendString("")
            .appendString("H")
            .appendRaw("HIH")
            .appendInt(row.respectAmount())
            .appendInt(row.scratchAmount())
            .build();
    }

    public static EffectListPayload effectList(List<UserEffectSummaryRow> effects) {
        long listedEffects = 0L;
        PacketBuilder effectRows = PacketBuilder.create();
        for (UserEffectSummaryRow effect : effects == null ? List.<UserEffectSummaryRow>of() : effects) {
            if (effect != null && effect.effectId() > 0L) {
                long remainingSeconds = effect.expireTimestamp() - effect.currentTimestamp();
                effectRows
                    .appendInt(effect.effectId())
                    .appendInt(effect.rentSeconds())
                    .appendInt(effect.effectCount());
                if (effect.expireTimestamp() > 0L && remainingSeconds > 0L) {
                    effectRows.appendInt(remainingSeconds);
                } else {
                    effectRows.appendRaw('M');
                }
                listedEffects++;
            }
        }
        return new EffectListPayload(listedEffects, PacketBuilder.message("GL")
            .appendInt(listedEffects)
            .appendRaw(effectRows)
            .build());
    }

    public record EffectListPayload(long listedEffects, String payload) {
    }

    public record WardrobePayload(long slotCount, String payload) {
    }

    private static String normalizedGender(String genderText) {
        String normalized = StringUtils.text(genderText).toUpperCase();
        normalized = normalized.isEmpty() ? "M" : normalized.substring(0, 1);
        return "F".equals(normalized) ? "F" : "M";
    }
}
