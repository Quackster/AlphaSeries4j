package com.alphaseries.server.mus;

import com.alphaseries.util.StringUtils;

public final class MusPayloads {
    private MusPayloads() {
    }

    /**
     * Original function: HandlingMUS.Proc_12_0_8218C0.
     */
    public static String shutdown(int socketIndex) {
        return "SHUTDOWN" + '\6' + socketIndex + '\7';
    }

    /**
     * Original function: HandlingMUS.Proc_12_1_821AA0.
     */
    public static String data(int socketIndex, String messageText) {
        return "DATA" + '\6' + socketIndex + '\6' + StringUtils.text(messageText) + '\7';
    }
}
