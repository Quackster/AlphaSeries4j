package com.alphaseries.messages.incoming;

import com.alphaseries.Handling;

public final class ReadyPacketRegistry {
    private ReadyPacketRegistry() {
    }

    public static MessageRegistry create() {
        return new MessageRegistry()
            .register(new IncomingMessage() {
                @Override
                public String[] headers() {
                    return new String[]{"CN"};
                }

                @Override
                public void handle(IncomingContext context, String header, String payload) {
                    Handling.sendClientDateSettings(context.socketIndex());
                }
            })
            .register(new IncomingMessage() {
                @Override
                public String[] headers() {
                    return new String[]{"F_"};
                }

                @Override
                public void handle(IncomingContext context, String header, String payload) {
                    Handling.handleLoginTicket(context.socketIndex(), payload);
                }
            })
            .register(new IncomingMessage() {
                @Override
                public String[] headers() {
                    return new String[]{"CD"};
                }

                @Override
                public void handle(IncomingContext context, String header, String payload) {
                    Handling.ignoreClientReadyPacket();
                }
            });
    }
}
