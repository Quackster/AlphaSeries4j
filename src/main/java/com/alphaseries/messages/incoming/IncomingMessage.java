package com.alphaseries.messages.incoming;

public interface IncomingMessage {
    MessageHeaders headers();

    void handle(IncomingContext context, String header, String payload);
}
