package de.joniwoch.teamcastlegemgrab.manager.teams;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class TeamManager {

    public List<GemgrabTeam> teams = new CopyOnWriteArrayList<>();
    public void registerTeams() {
        teams.add(new GemgrabTeam(1, TeamColor.BLUE, new CopyOnWriteArrayList<>()));
        teams.add(new GemgrabTeam(2, TeamColor.RED, new CopyOnWriteArrayList<>()));
    }

    public GemgrabTeam getTeamByColor(TeamColor teamColor) {
        for (GemgrabTeam gemgrabTeam : teams) {
            if (gemgrabTeam.getTeamColor().equals(teamColor)) {
                return gemgrabTeam;
            }
        }
        return null;
    }

    public boolean isInTeam(UUID player) {
        for (GemgrabTeam gemgrabTeam : teams) {
            if (gemgrabTeam.getPlayers().contains(player)) {
                return true;
            }
        }
        return false;
    }

    public void joinTeam(UUID player, TeamColor team) {
        if (isInTeam(player)) {
            leaveTeam(player);
        }
        for (GemgrabTeam gemgrabTeam : teams) {
            if (gemgrabTeam.getTeamColor().equals(team)) {
                gemgrabTeam.getPlayers().add(player);
            }
        }
    }

    public void leaveTeam(UUID player) {
        if (isInTeam(player)) {
            for (GemgrabTeam gemgrabTeam : teams) {
                if (gemgrabTeam.getPlayers().contains(player)) {
                    gemgrabTeam.getPlayers().remove(player);
                }
            }
        }
    }
}
