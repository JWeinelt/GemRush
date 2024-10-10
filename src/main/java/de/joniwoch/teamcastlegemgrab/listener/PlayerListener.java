package de.joniwoch.teamcastlegemgrab.listener;

import de.joniwoch.teamcastlegemgrab.TeamcastleGemgrab;
import de.joniwoch.teamcastlegemgrab.manager.game.Gamestate;
import de.joniwoch.teamcastlegemgrab.manager.game.gameplay.PlayerDeathHandler;
import de.joniwoch.teamcastlegemgrab.manager.player.GemgrabPlayer;
import de.joniwoch.teamcastlegemgrab.manager.player.GemgrabPlayerManager;
import de.joniwoch.teamcastlegemgrab.manager.teams.GemgrabTeam;
import de.joniwoch.teamcastlegemgrab.manager.teams.TeamGUI;
import de.joniwoch.teamcastlegemgrab.manager.teams.TeamManager;
import de.joniwoch.teamcastlegemgrab.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final TeamManager teamManager;
    private final PlayerDeathHandler playerDeathHandler;

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (TeamcastleGemgrab.getGamestate().equals(Gamestate.LOBBY)) {
            Player player = (Player) event.getEntity();
            if (player.getFoodLevel() != 20) {
                player.setFoodLevel(20);
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Gamestate gamestate = TeamcastleGemgrab.getGamestate();
        if (event.getEntity() instanceof Player player) {
            switch (gamestate) {
                case LOBBY:
                case STARTING:
                case ENDED:
                    event.setCancelled(true);
                    break;
                case INGAME:
                    GemgrabPlayer gemgrabPlayer = GemgrabPlayerManager.getGemgrabPlayerByUUID(player.getUniqueId());
                    if (!gemgrabPlayer.isDead()) {
                        if (player.getHealth() - event.getFinalDamage() <= 0) {
                            playerDeathHandler.setPlayerDead(player);
                            event.setCancelled(true);
                        }
                    }
                    break;
            }
        }
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        Gamestate gamestate = TeamcastleGemgrab.getGamestate();
        switch (gamestate) {
            case INGAME, ENDED:
                Player killer;
                if (event.getEntity() instanceof Player player && (event.getDamager() instanceof Player || event.getDamager() instanceof Arrow)) {
                    if (event.getDamager() instanceof Player) {
                        killer = (Player) event.getDamager();
                        GemgrabTeam gemgrabTeamPlayer = teamManager.getPlayerTeam(player.getUniqueId());
                        GemgrabTeam gemgrabTeamKiller = teamManager.getPlayerTeam(killer.getUniqueId());
                        if (gemgrabTeamPlayer == gemgrabTeamKiller) {
                            event.setCancelled(true);
                            return;
                        }
                        GemgrabPlayer gemgrabPlayer = GemgrabPlayerManager.getGemgrabPlayerByUUID(player.getUniqueId());
                        GemgrabPlayer gemgrabKiller = GemgrabPlayerManager.getGemgrabPlayerByUUID(killer.getUniqueId());
                        if (gemgrabKiller.isDead()) {
                            event.setCancelled(true);
                            return;
                        }
                        if (!gemgrabPlayer.isDead()) {
                            gemgrabPlayer.setLastDamager(gemgrabKiller);
                            if (player.getHealth() - event.getFinalDamage() <= 0) {
                                playerDeathHandler.setPlayerDead(player, killer);
                                event.setCancelled(true);
                            }
                        }
                    }
                    if (event.getDamager() instanceof Arrow) {
                        killer = (Player) ((Arrow) event.getDamager()).getShooter();
                        GemgrabTeam gemgrabTeamPlayer = teamManager.getPlayerTeam(player.getUniqueId());
                        assert killer != null;
                        GemgrabTeam gemgrabTeamKiller = teamManager.getPlayerTeam(killer.getUniqueId());
                        if (gemgrabTeamPlayer == gemgrabTeamKiller) {
                            event.setCancelled(true);
                            return;
                        }
                        GemgrabPlayer gemgrabPlayer = GemgrabPlayerManager.getGemgrabPlayerByUUID(player.getUniqueId());
                        assert killer != null;
                        GemgrabPlayer gemgrabKiller = GemgrabPlayerManager.getGemgrabPlayerByUUID(killer.getUniqueId());
                        if (!gemgrabPlayer.isDead()) {
                            gemgrabPlayer.setLastDamager(gemgrabKiller);
                            if (player.getHealth() - event.getFinalDamage() <= 0) {
                                playerDeathHandler.setPlayerDead(player, killer);
                                event.setCancelled(true);
                            }
                        }
                    }
                } else {
                    event.setCancelled(true);
                }
                break;
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (TeamcastleGemgrab.getGamestate().equals(Gamestate.STARTING)) {
            Location currentLocation = event.getFrom();
            Location targetLocation = event.getTo();
            if (currentLocation.getBlockX() != targetLocation.getBlockX() ||
                    currentLocation.getBlockZ() != targetLocation.getBlockZ()) {
                event.setTo(currentLocation);
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        switch (TeamcastleGemgrab.getGamestate()) {
            case LOBBY, ENDED -> {
                event.setCancelled(true);
            }
        }

        if (event.getCurrentItem() == null) return;
        if (!event.getCurrentItem().hasItemMeta()) return;
        if (!event.getCurrentItem().getItemMeta().hasDisplayName()) return;
        if (event.getCurrentItem().getItemMeta().getDisplayName() == null) return;
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) {
            return;
        }

        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
        String inventoryTitle = event.getView().getTitle();

        switch (inventoryTitle) {
            case "§8» §e§lTeams §8«":
                if (!event.isLeftClick()) return;
                if (event.getCurrentItem() == null) return;
                if (event.getCurrentItem().getType().equals(Material.WHITE_STAINED_GLASS_PANE)) return;

                if (itemName.equals("§cTeam verlassen")) {
                    if (teamManager.isInTeam(player.getUniqueId())) {
                        teamManager.leaveTeam(player.getUniqueId());
                        player.closeInventory();
                    }
                    return;
                }

                if (!itemName.split(" ")[0].equals("§7Team")) return;

                String team = itemName.split(" ")[1].replace(" ", "");
                GemgrabTeam gemgrabTeam = teamManager.getTeamByName(team);
                if (!teamManager.isFull(gemgrabTeam)) {
                    if (teamManager.isInTeam(player.getUniqueId())) {
                        GemgrabTeam playerTeam = teamManager.getPlayerTeam(player.getUniqueId());
                        if (gemgrabTeam != playerTeam) {
                            teamManager.joinTeam(player.getUniqueId(), gemgrabTeam.getTeamColor());
                            player.closeInventory();
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 3, 3);
                            player.sendTitle("§8» §7Team " + gemgrabTeam.getName() + " §8«", "§7erfolgreich §abetreten");
                        }
                    } else {
                        teamManager.joinTeam(player.getUniqueId(), gemgrabTeam.getTeamColor());
                        player.closeInventory();
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 3, 3);
                        player.sendTitle("§8» §7Team " + gemgrabTeam.getName() + " §8«", "§7erfolgreich §abetreten");
                    }
                }
                break;
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        switch (TeamcastleGemgrab.getGamestate()) {
            case LOBBY, ENDED -> {
                event.setCancelled(true);
            }
        }
        if (event.getItem() == null) return;
        if (!event.getItem().hasItemMeta()) return;
        if (!event.getItem().getItemMeta().hasDisplayName()) return;
        if (event.getItem().getItemMeta().getDisplayName() == null) return;

        String itemName = event.getItem().getItemMeta().getDisplayName();

        switch (itemName) {
            case "§8» §e§lTeams §8«":
                if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    new TeamGUI(teamManager).open(player);
                }
                break;
            case "§8» §d§lStats §8«":
                if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    player.sendMessage(Messages.mainPrefix + "Die §dStats §7sind aktuell noch §cin arbeit§7.");
                }
                break;
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPickUpItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        switch (TeamcastleGemgrab.getGamestate()) {
            case LOBBY, ENDED-> {
                event.setCancelled(true);
            }
            case STARTING, INGAME -> {
                GemgrabPlayer gemgrabPlayer = GemgrabPlayerManager.getGemgrabPlayerByUUID(player.getUniqueId());
                if (gemgrabPlayer.isDead()) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
