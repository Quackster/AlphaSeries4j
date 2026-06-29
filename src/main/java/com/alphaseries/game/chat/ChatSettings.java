package com.alphaseries.game.chat;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ChatSettings {
    private final String filterRows;
    private final String gestureRows;

    private ChatSettings(String filterRows, String gestureRows) {
        this.filterRows = StringUtils.text(filterRows);
        this.gestureRows = StringUtils.text(gestureRows);
    }

    public static ChatSettings fromLegacy(String filterRows, String gestureRows) {
        return new ChatSettings(filterRows, gestureRows);
    }

    public String filterRows() {
        return filterRows;
    }

    public String gestureRows() {
        return gestureRows;
    }

    public String filterText(String messageText, boolean filterEnabled, String replacementText) {
        String filteredText = StringUtils.text(messageText);
        if (filterEnabled && !filterRows.isEmpty()) {
            for (String row : filterRows.split("\r", -1)) {
                String[] fields = row.split("\t", -1);
                String blockedWord = StringUtils.field(fields, 0).trim();
                if (!blockedWord.isEmpty()) {
                    if (blockedWord.length() > 3) {
                        Pattern pattern = Pattern.compile(Pattern.quote(blockedWord), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
                        filteredText = pattern.matcher(filteredText).replaceAll(Matcher.quoteReplacement(StringUtils.text(replacementText)));
                    } else if (filteredText.equalsIgnoreCase(blockedWord)) {
                        filteredText = StringUtils.text(replacementText);
                    }
                }
            }
        }
        return filteredText;
    }

    public long gestureId(String messageText, boolean gestureEnabled) {
        if (!gestureEnabled || gestureRows.isEmpty()) {
            return 0L;
        }
        String[] rows = gestureRows.split("\r", -1);
        for (String word : StringUtils.text(messageText).split(" ", -1)) {
            String token = word.trim();
            if (!token.isEmpty()) {
                for (String row : rows) {
                    if (!row.isEmpty()) {
                        String[] fields = row.split("\t", -1);
                        if (fields.length >= 2 && token.equalsIgnoreCase(fields[0])) {
                            return NumberUtils.parseLong(fields[1]);
                        }
                    }
                }
            }
        }
        return 0L;
    }
}
