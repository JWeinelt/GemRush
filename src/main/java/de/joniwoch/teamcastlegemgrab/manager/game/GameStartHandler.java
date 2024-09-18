package de.joniwoch.teamcastlegemgrab.manager.game;

import de.joniwoch.teamcastlegemgrab.manager.items.gameitems.GameItemManager;
import de.joniwoch.teamcastlegemgrab.manager.locations.map.GameMap;
import de.joniwoch.teamcastlegemgrab.manager.locations.map.GameMapManager;
import de.joniwoch.teamcastlegemgrab.manager.teams.GemgrabTeam;
import de.joniwoch.teamcastlegemgrab.manager.teams.TeamColor;
import de.joniwoch.teamcastlegemgrab.manager.teams.TeamManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Map;

@RequiredArgsConstructor
public class GameStartHandler {

    private final TeamManager teamManager;
    private final GameMapManager gameMapManager;
    private final GameItemManager gameItemManager;

    public void startGame() {
        Bukkit.getOnlinePlayers().forEach(player -> {
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

            GameMap gameMap = gameMapManager.getGameMap();
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
            gameItemManager.setGameItems(player);
        });
    }
}
