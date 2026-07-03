package com.alphaseries.game.advertising;

import com.alphaseries.config.AppConfigState;
import com.alphaseries.dao.mysql.AdvertisingDao;
import com.alphaseries.db.Database;
import com.alphaseries.db.MySQL;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class AdvertisingBootCache {
    private AdvertisingBootCache() {
    }

    public record VisitRoomCache(VisitRoomAds visitRoomAds) {
        public VisitRoomCache {
            visitRoomAds = visitRoomAds == null ? VisitRoomAds.empty() : visitRoomAds;
        }

        public long count() {
            return visitRoomAds.count();
        }

        public String payload(long visitRoomId) {
            return visitRoomAds.payload(visitRoomId);
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
        AdvertisingState.instance().setVisitRoomAds(cache.visitRoomAds());
    }

    public static VisitRoomCache buildAdvertisementVisitRoomCache(List<AdvertisingDao.VisitRoomAdRow> visitRoomRows,
            String assetPath) {
        long count = 0L;
        List<VisitRoomAds.Payload> payloads = new ArrayList<>();
        if (visitRoomRows != null) {
            for (AdvertisingDao.VisitRoomAdRow row : visitRoomRows) {
                if (row != null) {
                    long visitRoomId = row.visitRoomId();
                    payloads.add(new VisitRoomAds.Payload(visitRoomId,
                        PacketBuilder.create()
                            .appendRaw(StringUtils.text(assetPath))
                            .appendString(visitRoomId)
                            .appendString(row.address())
                            .build()));
                    count++;
                }
            }
        }
        return new VisitRoomCache(VisitRoomAds.fromPayloads(payloads, count));
    }

    private static AdvertisingDao advertisingDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new AdvertisingDao(database);
    }
}
