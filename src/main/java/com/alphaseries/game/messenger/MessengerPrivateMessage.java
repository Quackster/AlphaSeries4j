package com.alphaseries.game.messenger;

public record MessengerPrivateMessage(long targetUserId, String messageText) {
    public MessengerPrivateMessage {
        messageText = messageText == null ? "" : messageText;
    }

    public boolean valid() {
        return targetUserId > 0L && !messageText.isEmpty() && messageText.length() <= 255;
    }
}
