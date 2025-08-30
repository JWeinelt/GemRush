package net.teamcastle.gemgrab.manager.game.gameplay;

import net.teamcastle.gemgrab.TeamcastleGemgrab;
import net.teamcastle.gemgrab.manager.database.MySQLManager;
import net.teamcastle.gemgrab.manager.game.Gamestate;
import net.teamcastle.gemgrab.manager.locations.LobbyLocationManager;
import net.teamcastle.gemgrab.manager.teams.TeamManager;
import net.teamcastle.gemgrab.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class WinManager {

    private final LobbyLocationManager lobbyLocationManager;
    private final TeamManager teamManager;
    private boolean winExecuted = false;

    public static int stopServer = 21;

    public WinManager(LobbyLocationManager lobbyLocationManager, TeamManager teamManager) {
        this.lobbyLocationManager = lobbyLocationManager;
        this.teamManager = teamManager;
    }

    public void executeWinSequenze(BossbarHandler.TeamColor teamColor, BossBar bossBar) {
        if (winExecuted) return; // ✅ Schutz vor Mehrfachausführung
        winExecuted = true;

        TeamcastleGemgrab.setGamestate(Gamestate.ENDED);

        List<Player> winningPlayers = teamManager.getPlayersByTeamColor(teamColor);
        MySQLManager mysql = TeamcastleGemgrab.getInstance().getMySQLManager();

        for (Player player : winningPlayers) {
            try {
                mysql.addWin(player.getUniqueId());
            } catch (SQLException e) {
                Bukkit.getLogger().warning("[Gemgrab] Fehler beim Speichern des Wins für " + player.getName());
                e.printStackTrace();
            }
        }

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 3, 3);
            player.sendTitle("§7Team " + teamColor.getName(), "§7hat §agewonnen§7!");
            lobbyLocationManager.teleportLobbySpawn(player);
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setHealth(20);
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setFoodLevel(20);
        });

        stopServer();
    }

    public void stopServer() {
        Bukkit.getScheduler().runTaskTimer(TeamcastleGemgrab.getInstance(), () -> {
            if (stopServer > 0) {
                stopServer--;
                switch (stopServer) {
                    case 20, 15, 10, 5, 4, 3, 2, 1 -> {
                        Bukkit.broadcastMessage(Messages.mainPrefix + "Der Server stoppt in §c" + stopServer + "s§7.");
                        Bukkit.getOnlinePlayers().forEach(player ->
                                player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 3, 3));
                    }
                }
            } else {
                Bukkit.getServer().shutdown();
            }
        }, 0L, 20L);
    }

    // 🔁 Aufrufbar z. B. beim Spielneustart
    public void reset() {
        winExecuted = false;
        stopServer = 21;
    }
}
