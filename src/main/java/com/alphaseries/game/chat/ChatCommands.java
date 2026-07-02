package com.alphaseries.game.chat;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;

import com.alphaseries.game.session.SessionRegistry;
import com.alphaseries.util.StringUtils;

public final class ChatCommands {
    private ChatCommands() {
    }

    /**
     * Original function: Proc_6_24_6EA010.
     * Original function: Proc_6_25_6EEAC0.
     */
    public static String commandPayload(String messageText, String buildText) {
        String command = StringUtils.text(messageText).trim().toLowerCase(Locale.ROOT);
        if (":entwicklung".equals(command)) {
            return "BK" + "UNIQUE ID: --" + '\n'
                + "BUILD: " + buildTextOrDefault(buildText) + '\2';
        }
        if (":about".equals(command)) {
            return "BK" + "Alpha Series" + '\n' + '\n'
                + "This is a copy of the unique Alpha Series written in Visual Basic 2006."
                + '\n' + '\n' + "UNIQUE ID:   --" + '\n'
                + "BUILD:   " + buildTextOrDefault(buildText) + '\2';
        }
        if (":commands".equals(command)) {
            return "BK" + "You've following commands avaible:" + '\r' + '\r'
                + ":about" + '\r'
                + ":commands" + '\r'
                + ":entwicklung" + '\r'
                + ":statistics" + '\r'
                + ":drink" + '\r'
                + ":follow" + '\r'
                + ":transfer" + '\r'
                + ":tiplock" + '\r'
                + ":whosonline" + '\r' + '\r'
                + "\u2022 Please note that some commands require additional syntax, which hasn't been listed up here!"
                + '\2';
        }
        return "";
    }

    /**
     * Original function: Proc_6_25_6EEAC0.
     */
    public static String activeUsersPayload(String userNamesText) {
        String names = StringUtils.text(userNamesText).trim();
        return "BK" + "Active users:" + '\r' + '\r' + names + '\2' + '\2';
    }

    public static String dynamicCommandPayload(
        String messageText,
        Iterable<SessionRegistry.SocketSession> sessions,
        Function<String, String> userNameLookup
    ) {
        String command = StringUtils.text(messageText).trim().toLowerCase(Locale.ROOT);
        if (!":whosonline".equals(command)) {
            return "";
        }
        Function<String, String> lookup = userNameLookup == null ? id -> "" : userNameLookup;
        List<String> users = new ArrayList<>();
        Set<Integer> seenSockets = new LinkedHashSet<>();
        for (SessionRegistry.SocketSession session : sessions == null
            ? java.util.List.<SessionRegistry.SocketSession>of() : sessions) {
            int socketIndex = session.socketIndex();
            if (socketIndex <= 0 || !seenSockets.add(socketIndex)) {
                continue;
            }
            String userName = StringUtils.text(lookup.apply(String.valueOf(session.userId())));
            if (userName.isEmpty()) {
                continue;
            }
            users.add(userName);
        }
        return activeUsersPayload(String.join(", ", users));
    }

    /**
     * Original function: Proc_6_21_6E8BA0.
     */
    public static String extractUrlList(String messageText) {
        List<String> urls = new ArrayList<>();
        for (String candidate : StringUtils.spaceSeparatedWords(messageText)) {
            String lowered = candidate.toLowerCase(Locale.ROOT);
            if (lowered.startsWith("www.") && lowered.indexOf('.', 4) > 0) {
                urls.add(candidate);
            } else if (lowered.startsWith("http://")) {
                urls.add(candidate);
            } else if (lowered.startsWith("https://")) {
                urls.add(candidate);
            }
        }
        return urls.isEmpty() ? "" : StringUtils.delimitedText(urls, ';') + ';';
    }

    public static String buildTextOrDefault(String buildText) {
        String text = StringUtils.text(buildText);
        return text.isEmpty() ? "ALPHASERIES_FINAL (PREMIUM)" : text;
    }
}
