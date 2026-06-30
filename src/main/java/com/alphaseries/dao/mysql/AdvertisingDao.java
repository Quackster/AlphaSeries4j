package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;

import java.sql.SQLException;
import java.util.List;

public final class AdvertisingDao {
    private final Database database;

    public AdvertisingDao(Database database) {
        this.database = database;
    }

    public List<VisitRoomAdRow> visitRoomAds() throws SQLException {
        return database.query(
            "SELECT id,address FROM advertisement_visitrooms",
            resultSet -> new VisitRoomAdRow(resultSet.getLong(1), resultSet.getString(2)));
    }

    public record VisitRoomAdRow(long visitRoomId, String address) {
    }
}
