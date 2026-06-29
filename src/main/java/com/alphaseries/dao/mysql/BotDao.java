package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;
import com.alphaseries.game.pet.PetCommandActionRow;
import com.alphaseries.game.pet.PetInventoryRow;
import com.alphaseries.game.pet.PetRaceRow;
import com.alphaseries.game.pet.PetStatusRow;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

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

    public List<PetRaceRow> petRaces(String productPet) throws SQLException {
        return database.query(
            "SELECT id_pet,breed,min_rank,min_hcrank,name FROM settings_petraces WHERE product_pet=? ORDER BY breed ASC",
            resultSet -> new PetRaceRow(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getLong(3),
                resultSet.getLong(4),
                resultSet.getString(5)),
            productPet);
    }

    public List<PetInventoryRow> inventoryPets(long userId) throws SQLException {
        return database.query(
            "SELECT bots.id,bots.name,bots.figure,bots_petdata.scratches FROM bots,bots_petdata WHERE bots.id_user=? "
                + "AND bots.id_handle=? AND bots.id_room IS NULL AND bots_petdata.id_bot=bots.id",
            resultSet -> new PetInventoryRow(
                resultSet.getLong(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getLong(4)),
            userId,
            3L);
    }

    public int clearBotRoom(long botId) throws SQLException {
        return database.execute("UPDATE bots SET id_room=null WHERE id=?", botId);
    }

    public int placeBotInRoom(long botId, long roomId, long positionX, long positionY, String positionZ, long rotation)
        throws SQLException {

        return database.execute(
            "UPDATE bots SET id_room=?,position_x=?,position_y=?,position_z=?,position_r=? WHERE id=?",
            roomId,
            positionX,
            positionY,
            positionZ,
            rotation,
            botId);
    }

    public int touchPetData(long botId) throws SQLException {
        return database.execute(
            "UPDATE bots_petdata SET id_level=id_level,energy=energy,experience=experience,nutrition=nutrition,scratches=scratches "
                + "WHERE id_bot=?",
            botId);
    }

    public long petScratches(long botId) throws SQLException {
        return database.queryOne(
            "SELECT scratches FROM bots_petdata WHERE id_bot=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            botId)
            .orElse(0L);
    }

    public Optional<PetStatusRow> petStatus(long botId) throws SQLException {
        return database.queryOne(
            "SELECT bots.id,bots.name,bots.figure,bots_petdata.id_level,bots_petdata.experience,"
                + "bots_petdata.energy,bots_petdata.nutrition,bots_petdata.scratches,"
                + "ROUND((UNIX_TIMESTAMP()-bots_petdata.timestamp_buy)/60/60/24,0),bots_petdata.id_owner,users.name "
                + "FROM bots,bots_petdata,users WHERE bots.id=? AND bots.id_handle=? "
                + "AND bots_petdata.id_bot=bots.id AND users.id=bots_petdata.id_owner LIMIT 1",
            resultSet -> new PetStatusRow(
                resultSet.getLong(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getLong(4),
                resultSet.getLong(5),
                resultSet.getLong(6),
                resultSet.getLong(7),
                resultSet.getLong(8),
                resultSet.getLong(9),
                resultSet.getLong(10),
                resultSet.getString(11)),
            botId,
            3L);
    }

    public Optional<PetCommandActionRow> petCommandAction(long commandId) throws SQLException {
        return database.queryOne(
            "SELECT petlevel_required,command_action FROM bots_petcommands WHERE id_command=? LIMIT 1",
            resultSet -> new PetCommandActionRow(
                resultSet.getLong(1),
                resultSet.getString(2)),
            commandId);
    }
}
