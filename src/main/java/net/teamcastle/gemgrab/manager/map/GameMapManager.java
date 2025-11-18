package net.teamcastle.gemgrab.manager.map;

import de.codeblocksmc.codelib.locations.LocUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.teamcastle.gemgrab.GemRush;
import net.teamcastle.gemgrab.manager.teams.TeamColor;
import net.teamcastle.gemgrab.storage.LocalStorage;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Optional;

@RequiredArgsConstructor
public class GameMapManager {

    @Getter
    @Setter
    @Deprecated(forRemoval = true)
    public GameMap gameMap;

    public Optional<GameMap> getGameMap(String mapName) {
        for (GameMap map : LocalStorage.getInstance().getGameMaps()) {
            if (map.getName().equalsIgnoreCase(mapName)) {
                return Optional.of(map);
            }
        }
        return Optional.empty();
    }

    public void createMap(Player player, String mapName) {
        Location location = player.getTargetBlock(null, 0).getLocation();

        GameMap map = new GameMap(LocUtil.fromBukkit(location), mapName);
        LocalStorage.getInstance().getGameMaps().add(map);
        player.sendMessage(GemRush.mainPrefix + "Du hast§a erfolgreich§7 die Map §6" + mapName + "§7 erstellt.");
    }

    public void addGameSpawn(Player player, TeamColor teamColor, String mapName) {
        getGameMap(mapName).ifPresent(map -> {
            map.addPlayerSpawn(player.getLocation(), teamColor);
            player.sendMessage(GemRush.mainPrefix + "A new spawn point has been added for team "
                    + teamColor.colorCode + teamColor.displayName + "§7 on map §6" + mapName + "§7.");
        });
    }
}