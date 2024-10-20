package de.joniwoch.teamcastlegemgrab.manager.game.start;

import de.joniwoch.teamcastlegemgrab.TeamcastleGemgrab;
import de.joniwoch.teamcastlegemgrab.manager.game.Gamestate;
import de.joniwoch.teamcastlegemgrab.manager.game.gems.GemManager;
import de.joniwoch.teamcastlegemgrab.manager.game.gems.GemSpawnerManager;
import de.joniwoch.teamcastlegemgrab.manager.items.gameitems.GameItemManager;
import de.joniwoch.teamcastlegemgrab.manager.locations.map.GameMap;
import de.joniwoch.teamcastlegemgrab.manager.locations.map.GameMapManager;
import de.joniwoch.teamcastlegemgrab.manager.teams.GemgrabTeam;
import de.joniwoch.teamcastlegemgrab.manager.teams.TeamColor;
import de.joniwoch.teamcastlegemgrab.manager.teams.TeamManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Item;

import java.util.Map;

@RequiredArgsConstructor
public class GameStartHandler {

    private final GameMapManager gameMapManager;
    private final GameItemManager gameItemManager;
    private final TeamManager teamManager;

    public void startGame() {
        TeamcastleGemgrab.setGamestate(Gamestate.STARTING);
        teamManager.autofillTeams();
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setLevel(0);
            player.setExp(0);
            gameMapManager.teleportGameMap(player);
            gameItemManager.setGameItems(player);
        });
        Bukkit.getScheduler().runTaskLater(TeamcastleGemgrab.getInstance(), () -> {
            Bukkit.getWorld("world").getEntities().forEach(entity -> {
                if (entity instanceof ArmorStand) entity.remove();
                if (entity instanceof Arrow) entity.remove();
                if (entity instanceof Item) entity.remove();
            });
            GemSpawnerManager.createGemSpawner(gameMapManager.gameMap.getSpawner());
        }, 5);
        GameStartCountdown.startStarterCountdown();
    }
}
