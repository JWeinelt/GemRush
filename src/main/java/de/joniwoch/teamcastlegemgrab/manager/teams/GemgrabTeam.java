package de.joniwoch.teamcastlegemgrab.manager.teams;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class GemgrabTeam {

    private int id;
    private TeamColor teamColor;
    private List<UUID> players;
    private String colorcode;
    private Material material;
    private String name;

}
