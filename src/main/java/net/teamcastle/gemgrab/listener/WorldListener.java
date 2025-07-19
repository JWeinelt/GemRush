package net.teamcastle.gemgrab.listener;

import net.teamcastle.gemgrab.TeamcastleGemgrab;
import net.teamcastle.gemgrab.manager.game.Gamestate;
import net.teamcastle.gemgrab.manager.game.gameplay.PlayerDeathHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.scoreboard.Team;

public class WorldListener implements Listener {

    private final PlayerDeathHandler deathHandler;

    public WorldListener(PlayerDeathHandler deathHandler) {
        this.deathHandler = deathHandler;
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (TeamcastleGemgrab.getGamestate() == Gamestate.INGAME) {
            if (event.getPlayer().getLocation().getY() <= 88) {
                deathHandler.setPlayerDead(event.getPlayer());
            }
        }
    }
}
