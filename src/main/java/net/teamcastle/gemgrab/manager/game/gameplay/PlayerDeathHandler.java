package net.teamcastle.gemgrab.manager.game.gameplay;

import net.teamcastle.gemgrab.TeamcastleGemgrab;
import net.teamcastle.gemgrab.manager.items.ItemAPI;
import net.teamcastle.gemgrab.manager.items.gameitems.GameItemManager;
import net.teamcastle.gemgrab.manager.locations.map.GameMapManager;
import net.teamcastle.gemgrab.manager.player.GemgrabPlayer;
import net.teamcastle.gemgrab.manager.player.GemgrabPlayerManager;
import net.teamcastle.gemgrab.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class PlayerDeathHandler {

    private final GameMapManager gameMapManager;
    private final GameItemManager gameItemManager;

    public void setPlayerDead(Player dead, Player killer) {
        GemgrabPlayer gemgrabPlayerDead = GemgrabPlayerManager.getGemgrabPlayerByUUID(dead.getUniqueId());
        Bukkit.broadcastMessage(Messages.mainPrefix + "Der Spieler §a" + dead.getName() + " §7wurde von §c" + killer.getName() + "§7 getötet!");
        dead.setAllowFlight(true);
        dead.setFlying(true);
        dropPlayerGems(dead);
        dead.getInventory().clear();
        dead.getInventory().setArmorContents(null);
        startRespawnCountdown(dead);
        dead.setFoodLevel(20);
        try {
            TeamcastleGemgrab.getInstance().getMySQLManager().addDeath(dead.getUniqueId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            TeamcastleGemgrab.getInstance().getMySQLManager().addKill(gemgrabPlayerDead.getLastDamager().getUuid());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        gemgrabPlayerDead.setLastDamager(null);
        GemgrabPlayer gemgrabPlayer = GemgrabPlayerManager.getGemgrabPlayerByUUID(dead.getUniqueId());
        gemgrabPlayer.setDead(true);
        gemgrabPlayer.setVisibility(false);
        GemgrabPlayerManager.hidePlayer(gemgrabPlayer);
        Bukkit.getScheduler().runTaskLater(TeamcastleGemgrab.getInstance(), () -> {
            dead.setHealth(20);
            dead.teleport(gameMapManager.getGameMap().getSpawner().clone().add(0.5, 7.0, 0.5));
            dead.setFlying(true);
        }, 1L);
    }

    public void setPlayerDead(Player dead) {
        GemgrabPlayer gemgrabPlayerDead = GemgrabPlayerManager.getGemgrabPlayerByUUID(dead.getUniqueId());
        if (gemgrabPlayerDead.getLastDamager() == null) {
            Bukkit.broadcastMessage(Messages.mainPrefix + "Der Spieler §c" + dead.getName() + " §7ist §cgestorben§7!");
        } else {
            GemgrabPlayer gemgrabPlayerKiller = gemgrabPlayerDead.getLastDamager();
            Bukkit.broadcastMessage(Messages.mainPrefix + "Der Spieler §a" + dead.getName() + " §7wurde von §c" + gemgrabPlayerKiller.getName() + "§7 getötet!");
        }
        gemgrabPlayerDead.setLastDamager(null);
        dead.setAllowFlight(true);
        dropPlayerGems(dead);
        dead.getInventory().clear();
        dead.setFoodLevel(20);
        try {
            TeamcastleGemgrab.getInstance().getMySQLManager().addDeath(dead.getUniqueId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        dead.getInventory().setArmorContents(null);
        startRespawnCountdown(dead);
        dead.setFoodLevel(20);
        GemgrabPlayer gemgrabPlayer = GemgrabPlayerManager.getGemgrabPlayerByUUID(dead.getUniqueId());
        gemgrabPlayer.setDead(true);
        gemgrabPlayer.setVisibility(false);
        GemgrabPlayerManager.hidePlayer(gemgrabPlayer);
        Bukkit.getScheduler().runTaskLater(TeamcastleGemgrab.getInstance(), () -> {
            dead.setHealth(20);
            dead.teleport(gameMapManager.getGameMap().getSpawner().clone().add(0.5, 7.0, 0.5));
            dead.setFlying(true);
        }, 1L);
    }

    public void dropPlayerGems(Player player) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.hasDisplayName()) {
                    if (meta.getDisplayName().equals("§2§lGEM")) {
                        count += item.getAmount();
                    }
                }
            }
        }
        if (count != 0) {
            Bukkit.getWorld("world").dropItemNaturally(player.getLocation(), new ItemAPI("§2§lGEM", Material.EMERALD, count).build());
        }
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
                    GemgrabPlayer gemgrabPlayer = GemgrabPlayerManager.getGemgrabPlayerByUUID(player.getUniqueId());
                    gemgrabPlayer.setDead(false);
                    gemgrabPlayer.setVisibility(true);
                    GemgrabPlayerManager.hidePlayer(gemgrabPlayer);
                }
            }
        }, 0L, 20L).getTaskId();
    }

}
