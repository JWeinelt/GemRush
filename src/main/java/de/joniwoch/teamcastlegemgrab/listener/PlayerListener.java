package de.joniwoch.teamcastlegemgrab.listener;

import de.joniwoch.teamcastlegemgrab.TeamcastleGemgrab;
import de.joniwoch.teamcastlegemgrab.manager.game.Gamestate;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

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

}
