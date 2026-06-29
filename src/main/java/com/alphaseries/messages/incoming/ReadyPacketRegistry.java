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
                    Handling.Proc_6_162_7B3310(context.socketIndex(), payload, 0);
                }
            })
            .register(new IncomingMessage() {
                @Override
                public String[] headers() {
                    return new String[]{"F_"};
                }

                @Override
                public void handle(IncomingContext context, String header, String payload) {
                    Handling.Proc_6_163_7B3480(context.socketIndex(), payload, 0);
                }
            })
            .register(new IncomingMessage() {
                @Override
                public String[] headers() {
                    return new String[]{"CD"};
                }

                @Override
                public void handle(IncomingContext context, String header, String payload) {
                    Handling.Proc_7FA5A0(context.socketIndex(), "CD", payload);
                }
            });
    }
}
