package com.alphaseries.game.quest;

import com.alphaseries.util.StringUtils;

public final class QuestSettings {
    private final String rows;

    private QuestSettings(String rows) {
        this.rows = StringUtils.text(rows);
    }

    public static QuestSettings fromLegacy(String rows) {
        return new QuestSettings(rows);
    }

    public String rows() {
        return rows;
    }

    public boolean hasRows() {
        return !rows.isEmpty();
    }
}
