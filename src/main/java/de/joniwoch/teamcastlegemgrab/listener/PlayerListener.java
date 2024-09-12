package de.joniwoch.teamcastlegemgrab.listener;

import de.joniwoch.teamcastlegemgrab.TeamcastleGemgrab;
import de.joniwoch.teamcastlegemgrab.manager.game.Gamestate;
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

public class PlayerListener implements Listener {

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
