package com.alphaseries.game.chat;

import com.alphaseries.config.AppConfigState;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public final class ChatLookups {
    private ChatLookups() {
    }

    /**
     * Original function: Proc_6_22_6E9300.
     */
    public static String filterMessage(String messageText) {
        boolean enabled = AppConfigState.instance().settingsCache()
            .longValueOrDefault("com.client.chat.filter.enabled", 0) != 0L;
        String replacement =
            AppConfigState.instance().settingsCache().valueOrDefault("com.client.chat.filter.replacement", "");
        return ChatState.instance().settings().filterText(StringUtils.text(messageText), enabled, replacement);
    }

    /**
     * Original function: Proc_6_23_6E9A90.
     */
    public static long gestureId(String messageText) {
        boolean enabled = AppConfigState.instance().settingsCache()
            .longValueOrDefault("com.client.chat.gesture.enabled", 0) != 0L;
        return ChatState.instance().settings().gestureId(StringUtils.text(messageText), enabled);
    }
}
