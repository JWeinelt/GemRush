package de.joniwoch.teamcastlegemgrab.manager.locations.map;

import de.joniwoch.teamcastlegemgrab.manager.teams.TeamColor;
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
