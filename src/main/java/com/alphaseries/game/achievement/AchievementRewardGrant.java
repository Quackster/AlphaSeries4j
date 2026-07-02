package com.alphaseries.game.achievement;

import java.util.ArrayList;
import java.util.List;

public record AchievementRewardGrant(
    String rewardPayload,
    String awardPayload,
    long rewardType,
    long rewardIncrease,
    long scoreIncrease,
    List<String> deliveryPayloads
) {
    public AchievementRewardGrant {
        rewardPayload = rewardPayload == null ? "" : rewardPayload;
        awardPayload = awardPayload == null ? "" : awardPayload;
        deliveryPayloads = deliveryPayloads == null ? List.of() : List.copyOf(deliveryPayloads);
    }

    public AchievementRewardGrant(
        String rewardPayload,
        String awardPayload,
        long rewardType,
        long rewardIncrease,
        long scoreIncrease
    ) {
        this(rewardPayload, awardPayload, rewardType, rewardIncrease, scoreIncrease,
            deliveryPayloads(rewardPayload, awardPayload));
    }

    public boolean valid() {
        return !rewardPayload.isEmpty();
    }

    public boolean hasAward() {
        return !awardPayload.isEmpty();
    }

    public static List<String> deliveryPayloads(String rewardPayload, String awardPayload) {
        List<String> payloads = new ArrayList<>();
        if (rewardPayload != null && !rewardPayload.isEmpty()) {
            payloads.add(rewardPayload);
        }
        if (awardPayload != null && !awardPayload.isEmpty()) {
            payloads.add(awardPayload);
        }
        return List.copyOf(payloads);
    }
}
