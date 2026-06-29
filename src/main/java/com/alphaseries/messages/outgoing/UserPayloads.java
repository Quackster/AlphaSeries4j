package com.alphaseries.messages.outgoing;

import com.alphaseries.protocol.PacketBuilder;

public final class UserPayloads {
    private UserPayloads() {
    }

    public static String creditsRefresh(long creditsValue) {
        return PacketBuilder.message("@F")
            .appendRaw(creditsValue)
            .appendString(".0")
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

    public static String identityRefresh(long userId, String mottoText, String figureText, String genderText) {
        return PacketBuilder.message("DJ")
            .appendInt(userId)
            .appendString(mottoText)
            .appendString(genderText)
            .appendString(figureText)
            .build();
    }
}
