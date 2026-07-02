package com.alphaseries.messages.incoming;

import java.util.List;

public interface IncomingMessage {
    List<String> headers();

    void handle(IncomingContext context, String header, String payload);
}
