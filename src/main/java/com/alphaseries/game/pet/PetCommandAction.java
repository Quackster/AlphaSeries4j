package com.alphaseries.game.pet;

import com.alphaseries.util.StringUtils;

public record PetCommandAction(boolean found, long requiredLevel, String action) {
    public PetCommandAction {
        action = StringUtils.text(action);
    }

    public static PetCommandAction empty() {
        return new PetCommandAction(false, 0L, "");
    }
}
