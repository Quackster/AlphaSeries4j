package com.alphaseries.messages.incoming;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class MessageRegistry {
    private final Map<String, IncomingMessage> messages = new LinkedHashMap<>();

    public MessageRegistry register(IncomingMessage message) {
        if (message == null) {
            return this;
        }
        for (String header : message.headers()) {
            messages.put(header, message);
        }
        return this;
    }

    public boolean dispatch(IncomingContext context, String header, String payload) {
        Optional<IncomingMessage> message = find(header);
        if (message.isEmpty()) {
            return false;
        }
        message.get().handle(context, header, payload);
        return true;
    }

    public Optional<IncomingMessage> find(String header) {
        return Optional.ofNullable(messages.get(header));
    }

    public Set<String> headers() {
        return Set.copyOf(messages.keySet());
    }
}
