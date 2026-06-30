package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;
import com.alphaseries.db.RowMapper;
import com.alphaseries.game.pet.BotRoomEntryRow;
import com.alphaseries.game.pet.PetCommandActionRow;
import com.alphaseries.game.pet.PetCommandCacheRow;
import com.alphaseries.game.pet.PetCommandTargetRow;
import com.alphaseries.game.pet.PetExperienceStateRow;
import com.alphaseries.game.pet.PetInventoryRow;
import com.alphaseries.game.pet.PetLevelCacheRow;
import com.alphaseries.game.pet.PetLevelExperienceRow;
import com.alphaseries.game.pet.PetPlacementRow;
import com.alphaseries.game.pet.PetRaceCacheRow;
import com.alphaseries.game.pet.PetRaceRow;
import com.alphaseries.game.pet.PetScratchRow;
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

    public List<PetRaceCacheRow> petRaceCacheRows() throws SQLException {
        return database.query(
            "SELECT product_pet,id_pet,breed,min_rank,min_hcrank,name FROM settings_petraces",
            resultSet -> new PetRaceCacheRow(
                resultSet.getString(1),
                resultSet.getLong(2),
                resultSet.getLong(3),
                resultSet.getLong(4),
                resultSet.getLong(5),
                resultSet.getString(6)));
    }

    public long maxPetLevelId() throws SQLException {
        return database.queryOne(
            "SELECT MAX(id_level) FROM bots_petlevels",
            resultSet -> resultSet.getLong(1))
            .orElse(0L);
    }

    public List<PetLevelCacheRow> petLevelCacheRows() throws SQLException {
        return database.query(
            "SELECT id_level,max_energy,max_exp,max_nutrition FROM bots_petlevels ORDER BY id_level ASC",
            resultSet -> new PetLevelCacheRow(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getLong(3),
                resultSet.getLong(4)));
    }

    public long petCommandCount() throws SQLException {
        return database.queryOne(
            "SELECT COUNT(id_command) FROM bots_petcommands",
            resultSet -> resultSet.getLong(1))
            .orElse(0L);
    }

    public List<PetCommandCacheRow> petCommandCacheRows() throws SQLException {
        return database.query(
            "SELECT id_command,petlevel_required,command,command_action FROM bots_petcommands",
            resultSet -> new PetCommandCacheRow(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getString(3),
                resultSet.getString(4)));
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

    public Optional<PetPlacementRow> availablePetForPlacement(long botId, long userId) throws SQLException {
        return database.queryOne(
            "SELECT bots.id,bots.name,bots.motto,bots.speech,bots.responses,bots.figure,"
                + "bots.id_handle,bots.id_handleaction,bots.cache_action,bots.speech_submit,bots.allow_walk,bots.max_fields_away "
                + "FROM bots,bots_petdata WHERE bots_petdata.id_bot=? AND bots.id=bots_petdata.id_bot "
                + "AND bots.id_user=? AND bots.id_room IS NULL LIMIT 1",
            resultSet -> new PetPlacementRow(
                resultSet.getLong(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getString(4),
                resultSet.getString(5),
                resultSet.getString(6),
                resultSet.getLong(7),
                resultSet.getLong(8),
                resultSet.getString(9),
                resultSet.getString(10),
                resultSet.getLong(11),
                resultSet.getLong(12)),
            botId,
            userId);
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

    public long petLevelForOwner(long botId, long userId) throws SQLException {
        return database.queryOne(
            "SELECT bots_petdata.id_level FROM bots,bots_petdata WHERE bots.id=? AND bots.id_user=? "
                + "AND bots_petdata.id_bot=bots.id LIMIT 1",
            resultSet -> resultSet.getLong(1),
            botId,
            userId)
            .orElse(0L);
    }

    public Optional<PetCommandTargetRow> petCommandTarget(long botId, long roomId) throws SQLException {
        return database.queryOne(
            "SELECT bots.id,bots.id_room,bots_petdata.id_level,bots_petdata.energy,bots_petdata.nutrition "
                + "FROM bots,bots_petdata WHERE bots.id=? AND bots.id_handle=? AND bots.id_room=? "
                + "AND bots_petdata.id_bot=bots.id LIMIT 1",
            resultSet -> new PetCommandTargetRow(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getLong(3),
                resultSet.getLong(4),
                resultSet.getLong(5)),
            botId,
            3L,
            roomId);
    }

    public Optional<PetExperienceStateRow> petExperienceState(long botId) throws SQLException {
        return database.queryOne(
            "SELECT bots.name,bots.figure,bots_petdata.id_level,bots_petdata.experience,"
                + "bots_petdata.energy,bots_petdata.nutrition,bots_petdata.scratches,bots.id_room "
                + "FROM bots,bots_petdata WHERE bots.id=? AND bots_petdata.id_bot=bots.id LIMIT 1",
            resultSet -> new PetExperienceStateRow(
                resultSet.getString(1),
                resultSet.getString(2),
                resultSet.getLong(3),
                resultSet.getLong(4),
                resultSet.getLong(5),
                resultSet.getLong(6),
                resultSet.getLong(7),
                resultSet.getLong(8)),
            botId);
    }

    public List<PetLevelExperienceRow> petLevelExperienceRows() throws SQLException {
        return database.query(
            "SELECT id_level,max_exp FROM bots_petlevels ORDER BY id_level ASC",
            resultSet -> new PetLevelExperienceRow(
                resultSet.getLong(1),
                resultSet.getLong(2)));
    }

    public long petLevelMaxExperience(long petLevel) throws SQLException {
        return database.queryOne(
            "SELECT max_exp FROM bots_petlevels WHERE id_level=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            petLevel)
            .orElse(0L);
    }

    public int updatePetExperience(long botId, long level, long experience) throws SQLException {
        return database.execute(
            "UPDATE bots_petdata SET id_level=?,experience=? WHERE id_bot=?",
            level,
            experience,
            botId);
    }

    public Optional<PetScratchRow> scratchTarget(long botId) throws SQLException {
        return database.queryOne(
            "SELECT bots.id,bots.name,bots.figure,bots_petdata.scratches FROM bots,bots_petdata "
                + "WHERE bots.id=? AND bots.id_handle=? AND bots.id_room IS NOT NULL AND bots_petdata.id_bot=bots.id LIMIT 1",
            resultSet -> new PetScratchRow(
                resultSet.getLong(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getLong(4)),
            botId,
            3L);
    }

    public int updatePetScratches(long botId, long scratches) throws SQLException {
        return database.execute(
            "UPDATE bots_petdata SET scratches=? WHERE id_bot=?",
            scratches,
            botId);
    }

    public Optional<BotRoomEntryRow> botRoomEntry(long botId) throws SQLException {
        return database.queryOne(
            "SELECT id,name,motto,speech,responses,position_x,position_y,position_z,position_r,figure,"
                + "NULL,id_handle,id_handleaction,cache_action,speech_submit,allow_walk,max_fields_away "
                + "FROM bots WHERE id=? LIMIT 1",
            botRoomEntryMapper(),
            botId);
    }

    public List<BotRoomEntryRow> roomBotEntries(long roomId) throws SQLException {
        return database.query(
            "SELECT id,name,motto,speech,responses,position_x,position_y,position_z,position_r,figure,"
                + "NULL,id_handle,id_handleaction,cache_action,speech_submit,allow_walk,max_fields_away "
                + "FROM bots WHERE id_room=? LIMIT 255",
            botRoomEntryMapper(),
            roomId);
    }

    private static RowMapper<BotRoomEntryRow> botRoomEntryMapper() {
        return resultSet -> new BotRoomEntryRow(
            resultSet.getLong(1),
            resultSet.getString(2),
            resultSet.getString(3),
            resultSet.getString(4),
            resultSet.getString(5),
            resultSet.getLong(6),
            resultSet.getLong(7),
            resultSet.getString(8),
            resultSet.getLong(9),
            resultSet.getString(10),
            resultSet.getLong(12),
            resultSet.getLong(13),
            resultSet.getString(14),
            resultSet.getString(15),
            resultSet.getLong(16),
            resultSet.getLong(17));
    }
}
