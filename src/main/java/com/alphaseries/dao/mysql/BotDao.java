package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;

import java.sql.SQLException;

public final class BotDao {
    private final Database database;

    public BotDao(Database database) {
        this.database = database;
    }

    public int insertPetBot(long userId, String figure, String name) throws SQLException {
        return database.execute(
            "INSERT INTO bots(id_user,figure,name,id_handle) VALUES(?,?,?,?)",
            userId,
            figure,
            name,
            3L);
    }

    public long newestPetBotId(long userId) throws SQLException {
        return database.queryOne(
            "SELECT id FROM bots WHERE id_user=? AND id_handle=? ORDER BY id DESC LIMIT 1",
            resultSet -> resultSet.getLong(1),
            userId,
            3L)
            .orElse(0L);
    }

    public int insertPetData(long botId, long ownerId) throws SQLException {
        return database.execute(
            "INSERT INTO bots_petdata(id_bot,timestamp_buy,id_owner,energy,nutrition,scratches) "
                + "VALUES(?,UNIX_TIMESTAMP(),?,?,?,?)",
            botId,
            ownerId,
            100L,
            100L,
            0L);
    }
}
