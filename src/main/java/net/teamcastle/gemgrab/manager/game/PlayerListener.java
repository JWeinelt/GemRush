package net.teamcastle.gemgrab.manager.game;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.teamcastle.gemgrab.GemRush;
import net.teamcastle.gemgrab.manager.player.GPlayer;
import net.teamcastle.gemgrab.manager.player.PlayerManager;
import net.teamcastle.gemgrab.manager.teams.TeamColor;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.meta.ItemMeta;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final Game game;

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (GemRush.getGamestate().equals(GameState.LOBBY)) {
            Player player = (Player) event.getEntity();
            if (player.getFoodLevel() != 20) {
                player.setFoodLevel(20);
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        e.deathMessage(Component.empty());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        GameState gamestate = GemRush.getGamestate();
        if (event.getEntity() instanceof Player player) {
            switch (gamestate) {
                case LOBBY, STARTING, ENDED -> event.setCancelled(true);
                case RUNNING -> {
                    GPlayer gPlayer = PlayerManager.getGemgrabPlayerByUUID(player.getUniqueId());
                    if (!gPlayer.isDead()) {
                        if (player.getHealth() - event.getFinalDamage() <= 0) {
                            game.getDeathHandler().setPlayerDead(player);
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        GameState gamestate = GemRush.getGamestate();
        switch (gamestate) {
            case RUNNING, ENDED -> {
                Player killer;
                if (event.getEntity() instanceof Player player && (event.getDamager() instanceof Player || event.getDamager() instanceof Arrow)) {
                    if (event.getDamager() instanceof Player) {
                        killer = (Player) event.getDamager();
                        TeamColor gemgrabTeamPlayer = game.getPlayerTeam(player.getUniqueId());
                        TeamColor gemgrabTeamKiller = game.getPlayerTeam(killer.getUniqueId());
                        if (gemgrabTeamPlayer.equals(gemgrabTeamKiller)) {
                            event.setCancelled(true);
                            return;
                        }
                        GPlayer gPlayer = PlayerManager.getGemgrabPlayerByUUID(player.getUniqueId());
                        GPlayer gemgrabKiller = PlayerManager.getGemgrabPlayerByUUID(killer.getUniqueId());
                        if (gemgrabKiller.isDead()) {
                            event.setCancelled(true);
                            return;
                        }
                        if (!gPlayer.isDead()) {
                            gPlayer.setLastDamager(gemgrabKiller);
                            if (player.getHealth() - event.getFinalDamage() <= 0) {
                                game.getDeathHandler().setPlayerDead(player);
                                event.setCancelled(true);
                            }
                        }
                    }
                    if (event.getDamager() instanceof Arrow) {
                        killer = (Player) ((Arrow) event.getDamager()).getShooter();
                        assert killer != null;
                        TeamColor gemgrabTeamPlayer = game.getPlayerTeam(player.getUniqueId());
                        TeamColor gemgrabTeamKiller = game.getPlayerTeam(killer.getUniqueId());
                        killer = (Player) event.getDamager();
                        if (gemgrabTeamPlayer.equals(gemgrabTeamKiller)) {
                            event.setCancelled(true);
                            return;
                        }
                        GPlayer gPlayer = PlayerManager.getGemgrabPlayerByUUID(player.getUniqueId());
                        GPlayer gemgrabKiller = PlayerManager.getGemgrabPlayerByUUID(killer.getUniqueId());
                        if (!gPlayer.isDead()) {
                            gPlayer.setLastDamager(gemgrabKiller);
                            if (player.getHealth() - event.getFinalDamage() <= 0) {
                                game.getDeathHandler().setPlayerDead(player);
                                event.setCancelled(true);
                            }
                        }
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (GemRush.getGamestate().equals(GameState.STARTING)) {
            Location currentLocation = e.getFrom();
            Location targetLocation = e.getTo();
            if (currentLocation.getBlockX() != targetLocation.getBlockX() ||
                    currentLocation.getBlockZ() != targetLocation.getBlockZ()) {
                e.setTo(currentLocation);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        switch (GemRush.getGamestate()) {
            case LOBBY, ENDED -> e.setCancelled(true);
        }
        if (e.getItem() == null) return;
        ItemMeta meta = e.getItem().getItemMeta();
        if (meta == null) return;
        Component display = meta.displayName();
        if (display == null) return;

        String itemName = PlainTextComponentSerializer.plainText().serialize(display);

        switch (itemName) {
            case "§7» §6Stats §7«" -> {
                if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    player.performCommand("stats");
                }
            }
            case "§7» §cZurück zur Lobby §7«" -> {
                if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
                    player.sendMessage(GemRush.mainPrefix + "§cDas Item ist derzeit in Arbeit!");
                }
            }
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPickUpItem(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        switch (GemRush.getGamestate()) {
            case LOBBY, ENDED-> e.setCancelled(true);
            case STARTING, RUNNING -> {
                GPlayer gPlayer = PlayerManager.getGemgrabPlayerByUUID(player.getUniqueId());
                if (gPlayer.isDead()) {
                    e.setCancelled(true);
                }
            }
        }
    }
}