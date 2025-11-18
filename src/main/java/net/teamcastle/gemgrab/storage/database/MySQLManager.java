package net.teamcastle.gemgrab.storage.database;

import de.codeblocksmc.codelib.databsae.MySQLTemplate;
import net.teamcastle.gemgrab.GemRush;
import net.teamcastle.gemgrab.manager.game.PlayerStat;
import net.teamcastle.gemgrab.manager.game.StatManager;
import net.teamcastle.gemgrab.storage.Configuration;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.UUID;

public class MySQLManager extends MySQLTemplate {
    public MySQLManager(Configuration c) {
        super(GemRush.instance.getLogger(), c.getMysqlHost(),
                c.getMysqlPort(), c.getMysqlDatabase(), c.getMysqlUser(), c.getMysqlPassword());
    }

    public static MySQLManager getInstance() {
        return GemRush.getInstance().getMySQLManager();
    }

    @Override
    public void afterSuccessfulConnection() {
        log.info("Connected to database successfully.");
        createTable();
    }

    private void createTable() {
        String sql = """
        CREATE TABLE IF NOT EXISTS gem_player_stats (
                PlayerID VARCHAR(36) NOT NULL PRIMARY KEY,
                Kills INT NOT NULL DEFAULT 0,
                Deaths INT NOT NULL DEFAULT 0,
                Wins INT NOT NULL DEFAULT 0,
                Losses INT NOT NULL DEFAULT 0,
                GamesPlayed INT NOT NULL DEFAULT 0
        );
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            log.severe(e.getMessage());
        }
    }

    @NotNull
    public PlayerStat getPlayerStat(UUID uuid) {
        String sql = "SELECT Kills, Deaths, Wins, Losses, GamesPlayed FROM gem_player_stats WHERE PlayerID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new PlayerStat(uuid,
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getInt(4),
                        rs.getInt(5)
                );
            }
        } catch (SQLException e) {
            log.severe(e.getMessage());
        }
        return new PlayerStat(uuid, 0, 0, 0, 0, 0);
    }

    public void uploadStats() {
        try {
            conn.setAutoCommit(false);
            PreparedStatement pS = conn.prepareStatement("INSERT INTO gem_player_stats " +
                    "(PlayerID, Kills, Deaths, Wins, Losses, GamesPlayed) VALUES (?, ?, ?, ?, ?, ?)" +
                    " ON DUPLICATE KEY UPDATE Kills = ?, Deaths = ?, Wins = ?, Losses = ?, GamesPlayed = ?");
            for (PlayerStat stat : StatManager.getInstance().getPlayerStats()) {
                pS.setString(1, stat.getPlayerID().toString());
                pS.setInt(2, stat.getKills());
                pS.setInt(3,  stat.getDeaths());
                pS.setInt(4, stat.getWins());
                pS.setInt(5, stat.getLost());
                pS.setInt(6, stat.getPlayed());
                pS.setInt(7, stat.getKills());
                pS.setInt(8,  stat.getDeaths());
                pS.setInt(9, stat.getWins());
                pS.setInt(10, stat.getLost());
                pS.setInt(11, stat.getPlayed());
                pS.addBatch();
            }

            pS.executeBatch();
        } catch (SQLException e) {
            log.severe(e.getMessage());
        }
    }
}
