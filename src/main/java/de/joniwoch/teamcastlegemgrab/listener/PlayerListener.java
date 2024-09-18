package de.joniwoch.teamcastlegemgrab.listener;

import de.joniwoch.teamcastlegemgrab.TeamcastleGemgrab;
import de.joniwoch.teamcastlegemgrab.manager.game.Gamestate;
import de.joniwoch.teamcastlegemgrab.manager.teams.TeamGUI;
import de.joniwoch.teamcastlegemgrab.manager.teams.TeamManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final TeamManager teamManager;

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
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        switch (TeamcastleGemgrab.getGamestate()) {
            case LOBBY -> {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        switch (TeamcastleGemgrab.getGamestate()) {
            case LOBBY -> {
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
                new TeamGUI(teamManager).open(player);
                break;
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        switch (TeamcastleGemgrab.getGamestate()) {
            case LOBBY -> {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        switch (TeamcastleGemgrab.getGamestate()) {
            case LOBBY -> {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPickUpItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        switch (TeamcastleGemgrab.getGamestate()) {
            case LOBBY -> {
                event.setCancelled(true);
            }
        }
    }

}
