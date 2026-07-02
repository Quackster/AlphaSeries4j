package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;
import com.alphaseries.game.jukebox.JukeboxPlaybackRow;
import com.alphaseries.game.jukebox.JukeboxPlaylistEntry;
import com.alphaseries.game.jukebox.JukeboxRow;
import com.alphaseries.game.jukebox.SongDiskRow;
import com.alphaseries.game.jukebox.SongInfoRow;
import com.alphaseries.util.NumberUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class JukeboxDao {
    private final Database database;

    public JukeboxDao(Database database) {
        this.database = database;
    }

    public Optional<JukeboxRow> jukeboxInRoom(long roomId) throws SQLException {
        Optional<JukeboxRow> playlistJukebox = database.queryOne(
            "SELECT furnitures.id,furnitures.id_product FROM furnitures,soundmachine_jb_playlist WHERE furnitures.id_room=? "
                + "AND soundmachine_jb_playlist.id_jukebox=furnitures.id GROUP BY furnitures.id ORDER BY furnitures.id DESC LIMIT 1",
            resultSet -> new JukeboxRow(resultSet.getLong(1), resultSet.getLong(2)),
            roomId);
        if (playlistJukebox.isPresent()) {
            return playlistJukebox;
        }
        return database.queryOne(
            "SELECT furnitures.id,furnitures.id_product FROM furnitures,products WHERE furnitures.id_room=? "
                + "AND furnitures.id_product=products.id AND (products.action LIKE '%soundmachine%' OR products.action LIKE '%jukebox%' "
                + "OR products.name LIKE '%jukebox%' OR products.sprite LIKE '%jukebox%') ORDER BY furnitures.id DESC LIMIT 1",
            resultSet -> new JukeboxRow(resultSet.getLong(1), resultSet.getLong(2)),
            roomId);
    }

    public List<SongInfoRow> songInfoRows(List<Long> requestedIds, long requestedCount) throws SQLException {
        long effectiveCount = requestedCount <= 0L ? 0L : Math.min(requestedCount, 60L);
        if (effectiveCount <= 0L || requestedIds == null || requestedIds.isEmpty()) {
            return List.of();
        }
        int limit = (int) Math.min(effectiveCount, requestedIds.size());
        List<Long> cdIds = new ArrayList<>(requestedIds.subList(0, limit));
        String placeholders = SqlFragments.placeholders(cdIds.size());
        return database.query(
            "SELECT title,sequence,author,sound,id FROM soundmachine_cds WHERE id IN ("
                + placeholders + ") LIMIT " + effectiveCount,
            resultSet -> new SongInfoRow(
                resultSet.getString(1),
                resultSet.getLong(2),
                resultSet.getString(3),
                resultSet.getString(4),
                resultSet.getLong(5)),
            SqlFragments.idParameters(cdIds));
    }

    public long activeDestinationId(long jukeboxId) throws SQLException {
        return database.queryOne(
            "SELECT id_destination FROM soundmachine_jb_playlist WHERE id_jukebox=? AND id_order=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            jukeboxId,
            0L)
            .orElse(0L);
    }

    public String maxPlaylistOrderText(long jukeboxId) throws SQLException {
        return database.queryOne(
            "SELECT MAX(id_order) FROM soundmachine_jb_playlist WHERE id_jukebox=?",
            resultSet -> resultSet.getString(1),
            jukeboxId)
            .orElse("");
    }

    public long playlistCount(long jukeboxId) throws SQLException {
        return database.queryOne(
            "SELECT COUNT(*) FROM soundmachine_jb_playlist WHERE id_jukebox=?",
            resultSet -> resultSet.getLong(1),
            jukeboxId)
            .orElse(0L);
    }

    public long diskDestinationForOwner(long userId, long diskFurnitureId, long songDiskProductId) throws SQLException {
        return database.queryOne(
            "SELECT id_destination FROM furnitures WHERE id_owner=? AND id=? AND id_product=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            userId,
            diskFurnitureId,
            songDiskProductId)
            .orElse(0L);
    }

    public int removeDiskFromOwner(long userId, long diskFurnitureId, long songDiskProductId) throws SQLException {
        return database.execute(
            "UPDATE furnitures SET id_owner=NULL WHERE id_owner=? AND id=? AND id_product=? LIMIT 1",
            userId,
            diskFurnitureId,
            songDiskProductId);
    }

    public int addPlaylistEntry(long jukeboxId, long diskFurnitureId, long playlistOrder, long destinationId) throws SQLException {
        return database.execute(
            "INSERT INTO soundmachine_jb_playlist(id_jukebox,id_cd,id_order,id_destination) VALUES(?,?,?,?)",
            jukeboxId,
            diskFurnitureId,
            playlistOrder,
            destinationId);
    }

    public long diskFurnitureIdAtOrder(long jukeboxId, long playlistOrder) throws SQLException {
        return database.queryOne(
            "SELECT id_cd FROM soundmachine_jb_playlist WHERE id_jukebox=? AND id_order=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            jukeboxId,
            playlistOrder)
            .orElse(0L);
    }

    public int returnDiskToOwner(long userId, long diskFurnitureId, long songDiskProductId) throws SQLException {
        if (songDiskProductId > 0L) {
            return database.execute(
                "UPDATE furnitures SET id_owner=? WHERE id=? AND id_product=? LIMIT 1",
                userId,
                diskFurnitureId,
                songDiskProductId);
        }
        return database.execute("UPDATE furnitures SET id_owner=? WHERE id=? LIMIT 1", userId, diskFurnitureId);
    }

    public int deletePlaylistEntry(long jukeboxId, long diskFurnitureId) throws SQLException {
        return database.execute(
            "DELETE FROM soundmachine_jb_playlist WHERE id_jukebox=? AND id_cd=? LIMIT 1",
            jukeboxId,
            diskFurnitureId);
    }

    public int decrementOrdersAfter(long jukeboxId, long playlistOrder) throws SQLException {
        return database.execute(
            "UPDATE soundmachine_jb_playlist SET id_order=id_order-1 WHERE id_jukebox=? AND id_order>?",
            jukeboxId,
            playlistOrder);
    }

    public long playlistLimitFromEntries(long jukeboxId) throws SQLException {
        return database.queryOne(
            "SELECT MAX(id_order)+1 FROM soundmachine_jb_playlist WHERE id_jukebox=?",
            resultSet -> resultSet.getLong(1),
            jukeboxId)
            .orElse(0L);
    }

    public List<JukeboxPlaylistEntry> playlistEntries(long jukeboxId, long playlistLimit) throws SQLException {
        long effectiveLimit = playlistLimit <= 0L ? 100L : playlistLimit;
        return database.query(
            "SELECT id_cd,id_destination FROM soundmachine_jb_playlist WHERE id_jukebox=? ORDER BY id_order ASC LIMIT "
                + effectiveLimit,
            resultSet -> new JukeboxPlaylistEntry(resultSet.getLong(1), resultSet.getLong(2)),
            jukeboxId);
    }

    public List<SongDiskRow> songDisks(long userId, long songDiskProductId) throws SQLException {
        return database.query(
            "SELECT id,id_destination FROM furnitures WHERE id_owner=? AND id_product=? LIMIT 250",
            resultSet -> new SongDiskRow(resultSet.getLong(1), resultSet.getLong(2)),
            userId,
            songDiskProductId);
    }

    public Optional<JukeboxPlaybackRow> playbackRow(long jukeboxId) throws SQLException {
        return database.queryOne(
            "SELECT soundmachine_jb_playlist.id_destination,soundmachine_jb_playlist.id_cd,soundmachine_cds.sequence "
                + "FROM soundmachine_jb_playlist,soundmachine_cds WHERE soundmachine_jb_playlist.id_jukebox=? "
                + "AND soundmachine_jb_playlist.id_order=? AND soundmachine_cds.id=soundmachine_jb_playlist.id_destination "
                + "GROUP BY soundmachine_cds.id LIMIT 1",
            resultSet -> new JukeboxPlaybackRow(resultSet.getLong(1), resultSet.getLong(2), resultSet.getLong(3)),
            jukeboxId,
            0L);
    }
}
