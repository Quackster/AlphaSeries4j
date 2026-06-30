package com.alphaseries.game.room;

import com.alphaseries.messages.outgoing.RoomPayloads;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public final class RoomRollers {
    private RoomRollers() {
    }

    /**
     * Original function: Main.mainRollerDeltaX.
     */
    public static long deltaX(long rotationValue) {
        if (rotationValue == 2L) {
            return 1L;
        }
        if (rotationValue == 6L) {
            return -1L;
        }
        return 0L;
    }

    /**
     * Original function: Main.mainRollerDeltaY.
     */
    public static long deltaY(long rotationValue) {
        if (rotationValue == 0L) {
            return -1L;
        }
        if (rotationValue == 4L) {
            return 1L;
        }
        return 0L;
    }

    /**
     * Original function: Main.mainRollerTargetHeight(String, String).
     */
    public static String targetHeight(String heightText, String fallbackHeight) {
        return !StringUtils.text(heightText).isEmpty()
            ? String.valueOf(NumberUtils.parseLong(heightText))
            : String.valueOf(NumberUtils.parseLong(fallbackHeight));
    }

    /**
     * Original function: Main.mainRollerMovePayload.
     */
    public static String movePayload(long furnitureId, long positionX, long positionY, String positionZ) {
        return RoomPayloads.rollerMove(furnitureId, positionX, positionY, positionZ);
    }
}
