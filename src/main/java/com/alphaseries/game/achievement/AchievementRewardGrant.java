package com.alphaseries.game.achievement;

import java.util.List;

public record AchievementRewardGrant(
    String rewardPayload,
    String awardPayload,
    long rewardType,
    long rewardIncrease,
    long scoreIncrease,
    DeliveryPayloads deliveryPayloads
) {
    public AchievementRewardGrant {
        rewardPayload = rewardPayload == null ? "" : rewardPayload;
        awardPayload = awardPayload == null ? "" : awardPayload;
        deliveryPayloads = deliveryPayloads == null
            ? new DeliveryPayloads(rewardPayload, awardPayload)
            : deliveryPayloads;
    }

    public AchievementRewardGrant(
        String rewardPayload,
        String awardPayload,
        long rewardType,
        long rewardIncrease,
        long scoreIncrease
    ) {
        this(rewardPayload, awardPayload, rewardType, rewardIncrease, scoreIncrease,
            new DeliveryPayloads(rewardPayload, awardPayload));
    }

    public boolean valid() {
        return !rewardPayload.isEmpty();
    }

    public boolean hasAward() {
        return !awardPayload.isEmpty();
    }

    public record DeliveryPayloads(String rewardPayload, String awardPayload) implements Iterable<String> {
        public DeliveryPayloads {
            rewardPayload = rewardPayload == null ? "" : rewardPayload;
            awardPayload = awardPayload == null ? "" : awardPayload;
        }

        @Override
        public java.util.Iterator<String> iterator() {
            return payloadList().iterator();
        }

        private List<String> payloadList() {
            if (rewardPayload.isEmpty() && awardPayload.isEmpty()) {
                return List.of();
            }
            if (rewardPayload.isEmpty()) {
                return List.of(awardPayload);
            }
            if (awardPayload.isEmpty()) {
                return List.of(rewardPayload);
            }
            return List.of(rewardPayload, awardPayload);
        }
    }
}
