package net.teamcastle.gemgrab.manager.map;

import de.codeblocksmc.codelib.locations.LocUtil;
import de.codeblocksmc.codelib.locations.LocationSection;
import de.codeblocksmc.codelib.locations.LocationWrapper;
import net.teamcastle.gemgrab.manager.teams.TeamColor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class GameMap {
    private LocationWrapper spawner;
    private String name;
    @Deprecated
    private Map<TeamColor, Map<Integer, LocationWrapper>> playerSpawns;

    private int maxPlayers;
    private LocationSection arena;

    private final HashMap<TeamColor, List<LocationWrapper>> spawnPoints = new HashMap<>();

    public GameMap(LocationWrapper spawner, String name) {
        this.spawner = spawner;
        this.name = name;
    }

    public void addPlayerSpawn(Location loc, TeamColor color) {
        spawnPoints.computeIfAbsent(color, k -> new ArrayList<>()).add(LocUtil.fromBukkit(loc));
    }
}