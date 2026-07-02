package com.alphaseries.game.advertising;

import com.alphaseries.config.AppConfigState;
import com.alphaseries.dao.mysql.AdvertisingDao;
import com.alphaseries.db.Database;
import com.alphaseries.db.MySQL;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class AdvertisingBootCache {
    private AdvertisingBootCache() {
    }

    public record VisitRoomCache(long count, Map<Long, String> payloadByVisitRoomId) {
        public VisitRoomCache {
            payloadByVisitRoomId = payloadByVisitRoomId == null
                ? Map.of() : Map.copyOf(payloadByVisitRoomId);
        }
    }

    public static void loadVisitRoomAdsCache() {
        AdvertisingDao advertising = advertisingDao();
        List<AdvertisingDao.VisitRoomAdRow> visitRoomRows = List.of();
        if (advertising != null) {
            try {
                visitRoomRows = advertising.visitRoomAds();
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        VisitRoomCache cache = buildAdvertisementVisitRoomCache(
            visitRoomRows,
            AppConfigState.instance().settingsCache().valueOrDefault("com.server.socket.game.advertisement.visitrooms.path", ""));
        AdvertisingState.instance().setVisitRoomAds(VisitRoomAds.fromPayloads(cache.payloadByVisitRoomId(), cache.count()));
    }

    public static VisitRoomCache buildAdvertisementVisitRoomCache(List<AdvertisingDao.VisitRoomAdRow> visitRoomRows,
            String assetPath) {
        long count = 0L;
        Map<Long, String> payloadByVisitRoomId = new LinkedHashMap<Long, String>();
        if (visitRoomRows != null) {
            for (AdvertisingDao.VisitRoomAdRow row : visitRoomRows) {
                if (row != null) {
                    long visitRoomId = row.visitRoomId();
                    payloadByVisitRoomId.put(visitRoomId,
                        PacketBuilder.create()
                            .appendRaw(StringUtils.text(assetPath))
                            .appendString(visitRoomId)
                            .appendString(row.address())
                            .build());
                    count++;
                }
            }
        }
        return new VisitRoomCache(count, payloadByVisitRoomId);
    }

    private static AdvertisingDao advertisingDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new AdvertisingDao(database);
    }
}
