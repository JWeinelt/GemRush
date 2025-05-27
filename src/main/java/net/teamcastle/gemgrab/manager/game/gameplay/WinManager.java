package net.teamcastle.gemgrab.manager.game.gameplay;

import net.teamcastle.gemgrab.TeamcastleGemgrab;
import net.teamcastle.gemgrab.manager.game.Gamestate;
import net.teamcastle.gemgrab.manager.locations.LobbyLocationManager;
import net.teamcastle.gemgrab.utils.Messages;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BossBar;

@RequiredArgsConstructor
public class WinManager {

    private final LobbyLocationManager lobbyLocationManager;

    public static int stopServer = 21;

    public void executeWinSequenze(BossbarHandler.TeamColor teamColor, BossBar bossBar) {
        TeamcastleGemgrab.setGamestate(Gamestate.ENDED);
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
        Bukkit.getScheduler().runTaskTimer(TeamcastleGemgrab.getInstance(), ()-> {
            if (stopServer > 0) {
                stopServer--;
                switch (stopServer) {
                    case 20, 15, 10, 5, 4, 3, 2, 1:
                        Bukkit.broadcastMessage(Messages.mainPrefix + "Der Server stoppt in §c" + stopServer + "s§7.");
                        Bukkit.getOnlinePlayers().forEach(player -> {
                            player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 3, 3);
                        });
                        break;
                }
            } else {
                Bukkit.getServer().shutdown();
            }
        }, 0 , 20);
    }
}
