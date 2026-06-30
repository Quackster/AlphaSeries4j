package com.alphaseries.game.room;

public record MovementStep(long positionX, long positionY, long directionValue, long movingValue) {
    /**
     * Original functions: Functions.Proc_10_24_80E790, Functions.Proc_10_26_81E4E0.
     */
    public static MovementStep between(long currentX, long currentY, long targetX, long targetY) {
        long deltaX = Long.compare(targetX - currentX, 0L);
        long deltaY = Long.compare(targetY - currentY, 0L);
        long nextX = currentX + deltaX;
        long nextY = currentY + deltaY;
        long movingValue = nextX != currentX || nextY != currentY ? 1L : 0L;
        return new MovementStep(nextX, nextY, directionCode(deltaX, deltaY), movingValue);
    }

    /**
     * Original function: Functions.zeroMovement.
     */
    public static MovementStep zero() {
        return new MovementStep(0L, 0L, 0L, 0L);
    }

    public String toLegacyText() {
        return positionX + "\0" + positionY + "\0" + directionValue + "\0" + movingValue + "\0";
    }

    /**
     * Original function: Functions.movementDirectionCode.
     */
    public static long directionCode(long deltaX, long deltaY) {
        if (deltaX == 0L && deltaY < 0L) {
            return 0L;
        } else if (deltaX > 0L && deltaY < 0L) {
            return 1L;
        } else if (deltaX > 0L && deltaY == 0L) {
            return 2L;
        } else if (deltaX > 0L && deltaY > 0L) {
            return 3L;
        } else if (deltaX == 0L && deltaY > 0L) {
            return 4L;
        } else if (deltaX < 0L && deltaY > 0L) {
            return 5L;
        } else if (deltaX < 0L && deltaY == 0L) {
            return 6L;
        } else if (deltaX < 0L && deltaY < 0L) {
            return 7L;
        }
        return 0L;
    }
}
