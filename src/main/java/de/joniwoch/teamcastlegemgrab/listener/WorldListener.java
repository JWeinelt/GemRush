package de.joniwoch.teamcastlegemgrab.listener;

import de.joniwoch.teamcastlegemgrab.TeamcastleGemgrab;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WorldListener implements Listener {

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        switch (TeamcastleGemgrab.getGamestate()) {
            case LOBBY -> {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        switch (TeamcastleGemgrab.getGamestate()) {
            case LOBBY -> {
                event.setCancelled(true);
            }
        }
    }
}
