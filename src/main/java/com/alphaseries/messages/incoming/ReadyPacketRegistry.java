package com.alphaseries.messages.incoming;

import com.alphaseries.config.AppConfigState;
import com.alphaseries.messages.outgoing.SessionPayloads;
import com.alphaseries.server.packet.PreReadyPacketDispatcher;
import com.alphaseries.server.runtime.SocketDelivery;
import com.alphaseries.server.runtime.SocketLifecycle;

import java.util.List;

public final class ReadyPacketRegistry {
    private ReadyPacketRegistry() {
    }

    public static MessageRegistry create() {
        return new MessageRegistry()
            .register(new IncomingMessage() {
                @Override
                public List<String> headers() {
                    return List.of("CN");
                }

                @Override
                public void handle(IncomingContext context, String header, String payload) {
                    String dateFormat = AppConfigState.instance().settingsCache()
                        .valueOrDefault("com.system.format.date", "DAQBHHIIKHJHPAHQA");
                    SocketDelivery.sendToSocket(context.socketIndex(), SessionPayloads.systemHandshake(dateFormat));
                }
            })
            .register(new IncomingMessage() {
                @Override
                public List<String> headers() {
                    return List.of("F_");
                }

                @Override
                public void handle(IncomingContext context, String header, String payload) {
                    PreReadyPacketDispatcher.dispatchPreReadyPacket(
                        context.socketIndex(),
                        header,
                        payload,
                        SocketLifecycle::disconnectSocket,
                        null);
                }
            })
            .register(new IncomingMessage() {
                @Override
                public List<String> headers() {
                    return List.of("CD");
                }

                @Override
                public void handle(IncomingContext context, String header, String payload) {
                }
            });
    }
}
