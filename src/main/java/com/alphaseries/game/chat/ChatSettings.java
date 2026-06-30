package com.alphaseries.game.chat;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ChatSettings {
    private final List<FilterWord> filterWords;
    private final List<Gesture> gestures;

    private ChatSettings(List<FilterWord> filterWords, List<Gesture> gestures) {
        this.filterWords = filterWords == null ? List.of() : List.copyOf(filterWords);
        this.gestures = gestures == null ? List.of() : List.copyOf(gestures);
    }

    public static ChatSettings fromRows(List<FilterWord> filterWords, List<Gesture> gestures) {
        return new ChatSettings(filterWords, gestures);
    }

    public static ChatSettings fromLegacy(Object filterRows, Object gestureRows) {
        return fromRows(filterWordsFromLegacy(filterRows), gesturesFromLegacy(gestureRows));
    }

    public static ChatSettings empty() {
        return new ChatSettings(List.of(), List.of());
    }

    public List<FilterWord> filterWords() {
        return List.copyOf(filterWords);
    }

    public String filterRows() {
        List<String> rows = new ArrayList<>();
        for (FilterWord row : filterWords) {
            if (row != null) {
                rows.add(StringUtils.text(row.word()));
            }
        }
        return String.join("\r", rows);
    }

    public List<Gesture> gestures() {
        return List.copyOf(gestures);
    }

    public String gestureRows() {
        List<String> rows = new ArrayList<>();
        for (Gesture row : gestures) {
            if (row != null) {
                rows.add(StringUtils.text(row.token()) + "\t" + row.gestureId());
            }
        }
        return String.join("\r", rows);
    }

    public String filterText(String messageText, boolean filterEnabled, String replacementText) {
        String filteredText = StringUtils.text(messageText);
        if (filterEnabled && !filterWords.isEmpty()) {
            for (FilterWord row : filterWords) {
                String blockedWord = row == null ? "" : StringUtils.text(row.word()).trim();
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
        if (!gestureEnabled || gestures.isEmpty()) {
            return 0L;
        }
        for (String word : StringUtils.text(messageText).split(" ", -1)) {
            String token = word.trim();
            if (!token.isEmpty()) {
                for (Gesture row : gestures) {
                    if (row != null && token.equalsIgnoreCase(StringUtils.text(row.token()))) {
                        return NumberUtils.parseLong(row.gestureId());
                    }
                }
            }
        }
        return 0L;
    }

    public static List<FilterWord> filterWordsFromLegacy(Object filterRows) {
        if (filterRows instanceof List<?> rows) {
            List<FilterWord> result = new ArrayList<>();
            for (Object row : rows) {
                if (row instanceof FilterWord filterWord) {
                    result.add(filterWord);
                }
            }
            return result;
        }
        String text = StringUtils.text(filterRows);
        if (text.isEmpty()) {
            return List.of();
        }
        List<FilterWord> result = new ArrayList<>();
        for (String row : text.split("\r", -1)) {
            if (!row.isEmpty()) {
                result.add(new FilterWord(row));
            }
        }
        return result;
    }

    public static List<Gesture> gesturesFromLegacy(Object gestureRows) {
        if (gestureRows instanceof List<?> rows) {
            List<Gesture> result = new ArrayList<>();
            for (Object row : rows) {
                if (row instanceof Gesture gesture) {
                    result.add(gesture);
                }
            }
            return result;
        }
        String text = StringUtils.text(gestureRows);
        if (text.isEmpty()) {
            return List.of();
        }
        List<Gesture> result = new ArrayList<>();
        for (String row : text.split("\r", -1)) {
            String[] fields = row.split("\t", -1);
            if (fields.length >= 2) {
                result.add(new Gesture(StringUtils.field(fields, 0),
                    NumberUtils.parseLong(StringUtils.field(fields, 1))));
            }
        }
        return result;
    }

    public record FilterWord(String word) {
    }

    public record Gesture(String token, long gestureId) {
    }
}
