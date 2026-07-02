package com.alphaseries.game.user;

import com.alphaseries.util.StringUtils;

public record AvatarNameUpdate(long validationCode, String candidateName, String validationPayload, boolean changed) {
    public AvatarNameUpdate {
        candidateName = StringUtils.text(candidateName);
        validationPayload = StringUtils.text(validationPayload);
    }

    public static AvatarNameUpdate empty() {
        return new AvatarNameUpdate(0L, "", "", false);
    }
}
