package de.joniwoch.teamcastlegemgrab.manager.locations.map;

import de.joniwoch.teamcastlegemgrab.utils.Config;
import de.joniwoch.teamcastlegemgrab.utils.Messages;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.LinkedHashMap;

public class GameMapManager {

    @Getter
    @Setter
    public GameMap gameMap;

    public void createMap(Player player, String mapname) {
        Location location = player.getTargetBlock(null, 0).getLocation();
        try {
            Config.set("Map.Spawner.X", location.getX());
            Config.set("Map.Spawner.Y", location.getY());
            Config.set("Map.Spawner.Z", location.getZ());
            Config.set("Map.Name", mapname);
            Config.save();
            player.sendMessage(Messages.mainPrefix + "Du hast §aerfolgreich §7die Map §6" + mapname + "§7 erstellt.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void cacheGameMap() {
        Location spawner = new Location(Bukkit.getWorld("world"),
                Config.config.getInt("Map.Spawner.X"),
                Config.config.getInt("Map.Spawner.X"),
                Config.config.getInt("Map.Spawner.Z"));
        setGameMap(new GameMap(
                spawner,
                Config.getString("Map.Name"),
                new LinkedHashMap<>()
        ));
    }
}
