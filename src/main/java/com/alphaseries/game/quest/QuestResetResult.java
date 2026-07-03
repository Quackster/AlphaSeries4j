package com.alphaseries.game.quest;

import java.util.List;

public record QuestResetResult(boolean reset, DeliveryPayloads deliveryPayloads) {
    public QuestResetResult {
        deliveryPayloads = deliveryPayloads == null ? DeliveryPayloads.empty() : deliveryPayloads;
    }

    public static QuestResetResult empty() {
        return new QuestResetResult(false, DeliveryPayloads.empty());
    }

    public record DeliveryPayloads(String resetPayload, String listPayload) implements Iterable<String> {
        public DeliveryPayloads {
            resetPayload = resetPayload == null ? "" : resetPayload;
            listPayload = listPayload == null ? "" : listPayload;
        }

        public static DeliveryPayloads empty() {
            return new DeliveryPayloads("", "");
        }

        @Override
        public java.util.Iterator<String> iterator() {
            return payloadList().iterator();
        }

        private List<String> payloadList() {
            if (resetPayload.isEmpty() && listPayload.isEmpty()) {
                return List.of();
            }
            if (resetPayload.isEmpty()) {
                return List.of(listPayload);
            }
            if (listPayload.isEmpty()) {
                return List.of(resetPayload);
            }
            return List.of(resetPayload, listPayload);
        }
    }
}
