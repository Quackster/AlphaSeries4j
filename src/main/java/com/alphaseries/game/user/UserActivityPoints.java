package com.alphaseries.game.user;

import com.alphaseries.config.AppSettingsCache;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.messages.outgoing.UserPayloads;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class UserActivityPoints {
    private static final Map<Long, Long> REPRESENTED_ACTIVITY_POINT_TICKS = new HashMap<>();

    private UserActivityPoints() {
    }

    public static long sessionSeconds(long socketIndex, String userId, UserDao users) {
        if (socketIndex <= 0L || StringUtils.text(userId).isEmpty()) {
            return 0L;
        }
        Long cachedTickValue = REPRESENTED_ACTIVITY_POINT_TICKS.get(socketIndex);
        long tickValue = cachedTickValue == null ? 0L : cachedTickValue;
        if (cachedTickValue == null && users != null) {
            try {
                tickValue = users.onlineTime(NumberUtils.parseLong(userId));
            } catch (Exception ignored) {
                tickValue = 0L;
            }
        }
        tickValue += 60L;
        REPRESENTED_ACTIVITY_POINT_TICKS.put(socketIndex, tickValue);
        return tickValue;
    }

    public static Award awardDecision(
        long sessionSeconds,
        long pointType,
        long intervalSeconds,
        long maxPoints,
        long awardAmount,
        long currentPoints
    ) {
        if (sessionSeconds <= 0L || intervalSeconds <= 0L || sessionSeconds % intervalSeconds != 0L) {
            return Award.empty(pointType, awardAmount);
        }
        long effectiveMaxPoints = maxPoints <= 0L ? 1L : maxPoints;
        if (currentPoints >= effectiveMaxPoints || awardAmount == 0L) {
            return Award.empty(pointType, awardAmount);
        }
        long newPoints = currentPoints + awardAmount;
        return new Award(
            pointType,
            awardAmount,
            newPoints,
            true,
            UserPayloads.activityPointAward(pointType, newPoints));
    }

    public static List<Award> awardSessionPoints(
        long userId,
        long sessionSeconds,
        AppSettingsCache settings,
        UserDao users
    )
        throws Exception {

        if (userId <= 0L || sessionSeconds <= 0L || users == null) {
            return List.of();
        }
        AppSettingsCache settingsCache = settings == null ? AppSettingsCache.empty() : settings;
        List<Award> awards = new ArrayList<>();
        for (long pointType = 0L; pointType <= 4L; pointType++) {
            long intervalSeconds = settingsCache.longValueOrDefault(
                "com.server.socket.game.activitypoints_" + pointType + ".interval", 0);
            if (intervalSeconds <= 0L || sessionSeconds % intervalSeconds != 0L) {
                continue;
            }
            long maxPoints = settingsCache.longValueOrDefault(
                "com.server.socket.game.activitypoints_" + pointType + ".max", 1);
            long currentPoints = users.activityPoints(userId, pointType);
            long awardAmount = settingsCache.longValueOrDefault(
                "com.server.socket.game.activitypoints_" + pointType + ".amount", 0);
            Award award = awardDecision(sessionSeconds, pointType, intervalSeconds, maxPoints, awardAmount, currentPoints);
            if (award.shouldAward()) {
                users.addActivityPoints(userId, pointType, awardAmount);
                awards.add(award);
            }
        }
        return List.copyOf(awards);
    }

    public static AwardBatch awardSessionPointBatch(
        long userId,
        long sessionSeconds,
        AppSettingsCache settings,
        UserDao users
    )
        throws Exception {

        List<Award> awards = awardSessionPoints(userId, sessionSeconds, settings, users);
        return AwardBatch.fromAwards(awards);
    }

    /**
     * Original function: Proc_6_238_7FA670.
     */
    public static AwardBatch timedActivityPointAwardBatch(
        long socketIndex,
        String userId,
        AppSettingsCache settings,
        UserDao users
    )
        throws Exception {

        if (StringUtils.text(userId).isEmpty() || "0".equals(StringUtils.text(userId))) {
            return AwardBatch.empty();
        }
        long sessionSeconds = sessionSeconds(socketIndex, userId, users);
        if (sessionSeconds <= 0L) {
            return AwardBatch.empty();
        }
        return awardSessionPointBatch(NumberUtils.parseLong(userId), sessionSeconds, settings, users);
    }

    public static String payloadForAwards(List<Award> awards) {
        PacketBuilder payload = PacketBuilder.create();
        for (Award award : awards == null ? List.<Award>of() : awards) {
            if (award != null) {
                payload.appendRaw(award.payload());
            }
        }
        return payload.build();
    }

    public record Award(
        long pointType,
        long awardAmount,
        long newPoints,
        boolean shouldAward,
        String payload
    ) {
        public Award {
            payload = StringUtils.text(payload);
        }

        public static Award empty(long pointType, long awardAmount) {
            return new Award(pointType, awardAmount, 0L, false, "");
        }
    }

    public record AwardBatch(List<Award> awards, String payload, List<String> deliveryPayloads) {
        public static AwardBatch empty() {
            return new AwardBatch(List.of(), "", List.of());
        }

        public static AwardBatch fromAwards(List<Award> awards) {
            List<Award> awardList = awards == null ? List.of() : List.copyOf(awards);
            List<String> deliveryPayloads = awardList.stream()
                .map(Award::payload)
                .filter(payload -> !payload.isEmpty())
                .toList();
            return new AwardBatch(awardList, payloadForAwards(awardList), deliveryPayloads);
        }

        public AwardBatch {
            awards = awards == null ? List.of() : List.copyOf(awards);
            payload = StringUtils.text(payload);
            deliveryPayloads = deliveryPayloads == null ? List.of() : List.copyOf(deliveryPayloads);
        }
    }
}
