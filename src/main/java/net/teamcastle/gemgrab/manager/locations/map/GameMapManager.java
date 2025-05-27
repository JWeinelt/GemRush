package net.teamcastle.gemgrab.manager.locations.map;

import net.teamcastle.gemgrab.TeamcastleGemgrab;
import net.teamcastle.gemgrab.manager.teams.GemgrabTeam;
import net.teamcastle.gemgrab.manager.teams.TeamColor;
import net.teamcastle.gemgrab.manager.teams.TeamManager;
import net.teamcastle.gemgrab.utils.Config;
import net.teamcastle.gemgrab.utils.Messages;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
public class GameMapManager {

    @Getter
    @Setter
    public GameMap gameMap;

    private final TeamManager teamManager;

    public void teleportGameMap(Player player) {
        GemgrabTeam team = teamManager.getPlayerTeam(player.getUniqueId());
        if (team == null) {
            Bukkit.getLogger().warning("Team für Spieler " + player.getName() + " nicht gefunden.");
            return;
        }

        TeamColor teamColor = team.getTeamColor();
        if (teamColor == null) {
            Bukkit.getLogger().warning("Teamfarbe für Spieler " + player.getName() + " nicht gefunden.");
            return;
        }

        GameMap gameMap = getGameMap();
        if (gameMap == null) {
            Bukkit.getLogger().warning("GameMap ist null.");
            return;
        }

        Map<TeamColor, Map<Integer, Location>> playerSpawns = gameMap.getPlayerSpawns();
        if (playerSpawns == null) {
            Bukkit.getLogger().warning("PlayerSpawns Map ist null.");
            return;
        }

        if (!playerSpawns.containsKey(teamColor)) {
            Bukkit.getLogger().warning("TeamColor " + teamColor + " hat keine zugeordneten Spawns.");
            return;
        }

        int playerIndex = team.getPlayers().indexOf(player.getUniqueId()) + 1;
        if (playerIndex == -1) {
            Bukkit.getLogger().warning("Spieler " + player.getName() + " nicht im Team gefunden.");
            return;
        }

        Map<Integer, Location> spawnsForTeam = playerSpawns.get(teamColor);
        if (!spawnsForTeam.containsKey(playerIndex)) {
            Bukkit.getLogger().warning("Kein Spawn für Spielerindex " + playerIndex + " im Team " + teamColor + ".");
            return;
        }

        Location spawnLocation = spawnsForTeam.get(playerIndex);
        if (spawnLocation == null) {
            Bukkit.getLogger().warning("Spawn-Location für Spieler " + player.getName() + " konnte nicht gefunden werden.");
            return;
        }
        player.teleport(spawnLocation);
    }

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

    public void setGameSpawn(Player player, String teamcolor, String id) {
        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();
        double yaw = player.getLocation().getYaw();
        double pitch = player.getLocation().getPitch();
        try {
            Config.set("Map.Spawns." + teamcolor + "." + id + ".X", x);
            Config.set("Map.Spawns." + teamcolor + "." + id + ".Y", y);
            Config.set("Map.Spawns." + teamcolor + "." + id + ".Z", z);
            Config.set("Map.Spawns." + teamcolor + "." + id + ".Yaw", yaw);
            Config.set("Map.Spawns." + teamcolor + "." + id + ".Pitch", pitch);
            Config.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        player.sendMessage(Messages.mainPrefix + "Du hast §aerfolgreich §7den Game Spawn für §e§l" + teamcolor + "§7, §e§l" + id + "§7 gesetzt.");
    }

    public void cacheGameMap() {
        Location spawner = new Location(Bukkit.getWorld("world"),
                Config.config.getInt("Map.Spawner.X"),
                Config.config.getInt("Map.Spawner.Y"),
                Config.config.getInt("Map.Spawner.Z"));

        Map<TeamColor, Map<Integer, Location>> playerSpawns = new LinkedHashMap<>();

        for (String teamName : Config.config.getConfigurationSection("Map.Spawns").getKeys(false)) {
            TeamColor teamColor = TeamColor.valueOf(teamName);
            Map<Integer, Location> spawns = new HashMap<>();
            for (String spawnKey : Config.config.getConfigurationSection("Map.Spawns." + teamName).getKeys(false)) {
                int spawnId = Integer.parseInt(spawnKey);
                double x = Config.config.getDouble("Map.Spawns." + teamName + "." + spawnKey + ".X");
                double y = Config.config.getDouble("Map.Spawns." + teamName + "." + spawnKey + ".Y");
                double z = Config.config.getDouble("Map.Spawns." + teamName + "." + spawnKey + ".Z");
                float yaw = (float) Config.config.getDouble("Map.Spawns." + teamName + "." + spawnKey + ".Yaw");
                float pitch = (float) Config.config.getDouble("Map.Spawns." + teamName + "." + spawnKey + ".Pitch");
                Location location = new Location(Bukkit.getWorld("world"), x, y, z, yaw, pitch);
                spawns.put(spawnId, location);
            }
            playerSpawns.put(teamColor, spawns);

            setGameMap(new GameMap(
                    spawner,
                    Config.getString("Map.Name"),
                    playerSpawns
            ));
            TeamcastleGemgrab.setGameMap(new GameMap(
                    spawner,
                    Config.getString("Map.Name"),
                    playerSpawns));
        }
    }
}