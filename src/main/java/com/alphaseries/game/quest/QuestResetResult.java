package com.alphaseries.game.quest;

import java.util.List;

public record QuestResetResult(boolean reset, List<String> deliveryPayloads) {
    public QuestResetResult {
        deliveryPayloads = deliveryPayloads == null ? List.of() : List.copyOf(deliveryPayloads);
    }

    public static QuestResetResult empty() {
        return new QuestResetResult(false, List.of());
    }
}
