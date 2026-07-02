package com.alphaseries.game.user;

import com.alphaseries.protocol.WireEncoding;
import com.alphaseries.protocol.WireReader;
import com.alphaseries.protocol.WireRequests;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public final class UserWire {
    private UserWire() {
    }

    public record WardrobeSlotRequest(long slotId, String figureText, String genderText) {
        public WardrobeSlotRequest {
            figureText = StringUtils.singleLineText(figureText);
            genderText = StringUtils.left(StringUtils.text(genderText).toUpperCase(), 1);
        }
    }

    public record TutorialClothesRequest(String genderText, String figureText) {
        public TutorialClothesRequest {
            genderText = StringUtils.left(StringUtils.text(genderText).toUpperCase(), 1);
            figureText = StringUtils.singleLineText(figureText);
        }
    }

    public record AvatarNameRequest(String candidateName) {
        public AvatarNameRequest {
            candidateName = StringUtils.singleLineText(candidateName);
        }
    }

    public record MottoRequest(String mottoText) {
        public MottoRequest {
            mottoText = StringUtils.left(StringUtils.singleLineText(mottoText), 255);
        }
    }

    public record GuideInviteRequest(long userId) {
    }

    public record SoundSettingRequest(long soundSetting) {
    }

    /**
     * Original function: Proc_6_16_6E2320.
     */
    public static WardrobeSlotRequest wardrobeSlotRequest(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "Ex");
        WireReader.Offset offset = new WireReader.Offset(1);
        long slotId = WireReader.readLong(requestPayload, offset);
        String figureText = WireReader.readString(requestPayload, offset);
        String genderText = WireReader.readString(requestPayload, offset);
        return new WardrobeSlotRequest(slotId, figureText, genderText);
    }

    /**
     * Original function: Proc_6_17_6E48D0.
     */
    public static TutorialClothesRequest tutorialClothesRequest(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "@l");
        WireReader.Offset offset = new WireReader.Offset(1);
        String genderText = WireReader.readString(requestPayload, offset);
        String figureText = WireReader.readString(requestPayload, offset);
        return new TutorialClothesRequest(genderText, figureText);
    }

    /**
     * Original function: Proc_6_38_70FD10.
     * Original function: Proc_6_39_711650.
     */
    public static AvatarNameRequest avatarNameRequest(String packetPayload, String prefix) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, prefix);
        WireReader.Offset offset = new WireReader.Offset(1);
        String candidateName = WireReader.readString(requestPayload, offset);
        if (candidateName.isEmpty()) {
            candidateName = WireEncoding.readBase64LengthString(requestPayload);
        }
        return new AvatarNameRequest(candidateName);
    }

    /**
     * Original function: Proc_6_230_7F3D20.
     */
    public static MottoRequest mottoRequest(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "Gd");
        String rawMotto = WireReader.readString(requestPayload, new WireReader.Offset(1));
        if (rawMotto.isEmpty()) {
            rawMotto = WireEncoding.readBase64LengthString(requestPayload);
        }
        if (rawMotto.isEmpty()) {
            rawMotto = requestPayload;
        }
        return new MottoRequest(rawMotto);
    }

    /**
     * Original function: dispatch pre-ready Ce packet branch.
     */
    public static SoundSettingRequest soundSettingRequest(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "Ce");
        long soundSetting = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        if (soundSetting <= 0L) {
            soundSetting = NumberUtils.parseLong(requestPayload);
        }
        if (soundSetting <= 0L || soundSetting >= 101L) {
            soundSetting = 0L;
        }
        return new SoundSettingRequest(soundSetting);
    }

    /**
     * Original function: Proc_7F44D0.
     */
    public static GuideInviteRequest guideInviteRequest(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "oL");
        String valueText = WireEncoding.readVl64LengthString(requestPayload);
        if (valueText.isEmpty()) {
            valueText = WireReader.readString(requestPayload, new WireReader.Offset(1));
        }
        return new GuideInviteRequest(NumberUtils.parseLong(valueText));
    }
}
