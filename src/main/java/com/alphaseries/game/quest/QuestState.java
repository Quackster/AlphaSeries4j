package com.alphaseries.game.quest;

public final class QuestState {
    private static final QuestState INSTANCE = new QuestState();

    private QuestSettings settings = QuestSettings.empty();

    private QuestState() {
    }

    public static QuestState instance() {
        return INSTANCE;
    }

    public synchronized QuestSettings settings() {
        return settings;
    }

    public synchronized void setSettings(QuestSettings settings) {
        this.settings = settings == null ? QuestSettings.empty() : settings;
    }

    public synchronized void setSettingsFromLegacy(Object rows) {
        settings = QuestSettings.fromLegacy(rows);
    }
}
