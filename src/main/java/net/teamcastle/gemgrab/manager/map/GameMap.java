package net.teamcastle.gemgrab.manager.map;

import de.codeblocksmc.codelib.locations.LocUtil;
import de.codeblocksmc.codelib.locations.LocationSection;
import de.codeblocksmc.codelib.locations.LocationWrapper;
import net.teamcastle.gemgrab.manager.teams.TeamColor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;

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

    public GameMap remapAndClone(World world) {
        LocationWrapper sp = new LocationWrapper(world.getName(), spawner.getX(), spawner.getY(), spawner.getZ(), spawner.getYaw(), spawner.getPitch());
        GameMap n = new GameMap(sp, name);
        LocationWrapper l1 = new LocationWrapper(world.getName(), arena.getL1().getX(), arena.getL1().getY(), arena.getL1().getZ(), arena.getL1().getYaw(), arena.getL1().getPitch());
        LocationWrapper l2 = new LocationWrapper(world.getName(), arena.getL2().getX(), arena.getL2().getY(), arena.getL2().getZ(), arena.getL2().getYaw(), arena.getL2().getPitch());
        n.setArena(new LocationSection(l1, l2));

        for (TeamColor color : spawnPoints.keySet()) {
            for (LocationWrapper loc : spawnPoints.get(color)) {
                LocationWrapper nl = new LocationWrapper(world.getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
                n.addPlayerSpawn(LocUtil.fromWrapper(nl), color);
            }
        }
        return n;
    }
}