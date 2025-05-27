package net.teamcastle.gemgrab.manager.locations.map;

import net.teamcastle.gemgrab.manager.teams.TeamColor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class GameMap {

    private Location spawner;
    private String name;
    private Map<TeamColor, Map<Integer, Location>> playerSpawns;

}
