package com.alphaseries.messages.incoming;

public interface IncomingMessage {
    String[] headers();

    void handle(IncomingContext context, String header, String payload);
}
