package de.joniwoch.teamcastlegemgrab.manager.game.gameplay;

import de.joniwoch.teamcastlegemgrab.TeamcastleGemgrab;
import de.joniwoch.teamcastlegemgrab.manager.game.Gamestate;
import de.joniwoch.teamcastlegemgrab.manager.game.gems.GemSpawnerManager;
import de.joniwoch.teamcastlegemgrab.manager.items.gameitems.GameItemManager;
import de.joniwoch.teamcastlegemgrab.manager.locations.map.GameMapManager;
import de.joniwoch.teamcastlegemgrab.utils.Messages;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class PlayerDeathHandler {

    private final GameMapManager gameMapManager;
    private final GameItemManager gameItemManager;

    public List<Player> deadPlayers = new CopyOnWriteArrayList<>();

    public void setPlayerDead(Player dead, Player killer) {
        Bukkit.broadcastMessage(Messages.mainPrefix + "Der Spieler §a" + dead.getName() + " §7wurde von §c" + killer.getName() + "§7 getötet!");
        dead.setAllowFlight(true);
        dead.getInventory().clear();
        dead.getInventory().setArmorContents(null);
        startRespawnCountdown(dead);
        dead.setHealth(20);
        dead.setFoodLevel(20);
        deadPlayers.add(dead);
        hideDead();
    }

    public void setPlayerDead(Player dead) {
        Bukkit.broadcastMessage(Messages.mainPrefix + "Der Spieler §c" + dead.getName() + " §7ist §cgestorben§7!");
        dead.setAllowFlight(true);
        dead.getInventory().clear();
        dead.getInventory().setArmorContents(null);
        startRespawnCountdown(dead);
        dead.setHealth(20);
        dead.setFoodLevel(20);
        deadPlayers.add(dead);
        hideDead();
    }

    public void hideDead() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            deadPlayers.forEach(player1 -> {
                player.hidePlayer(player1);
            });
        });
    }

    public void showPlayer(Player player) {
        Bukkit.getOnlinePlayers().forEach(player1 -> {
            player1.showPlayer(player);
        });
    }

    public void startRespawnCountdown(Player player) {
        final int[] taskIdHolder = new int[1];
        AtomicInteger countdown = new AtomicInteger(5);
        taskIdHolder[0] = Bukkit.getScheduler().runTaskTimer(TeamcastleGemgrab.getInstance(), () -> {
            int secondsLeft = countdown.getAndDecrement();

            if (secondsLeft > 0) {
                player.sendTitle("§cRespawn", "§7in §a" + secondsLeft + "s");
            }

            switch (secondsLeft) {
                case 0 -> {
                    gameMapManager.teleportGameMap(player);
                    gameItemManager.setGameItems(player);
                    player.setAllowFlight(false);
                    player.setHealth(20);
                    player.setFoodLevel(20);
                    deadPlayers.remove(player);
                    showPlayer(player);
                }
            }
        }, 0L, 20L).getTaskId();
    }

}
