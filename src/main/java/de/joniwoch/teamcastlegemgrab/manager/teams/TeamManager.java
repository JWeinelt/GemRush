package de.joniwoch.teamcastlegemgrab.manager.teams;

import de.joniwoch.teamcastlegemgrab.manager.game.GameSettings;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class TeamManager {

    public List<GemgrabTeam> teams = new CopyOnWriteArrayList<>();
    public void registerTeams() {
        teams.add(new GemgrabTeam(1, TeamColor.BLUE, new CopyOnWriteArrayList<>(), "§1", Material.BLUE_WOOL, "§1Blau"));
        teams.add(new GemgrabTeam(2, TeamColor.RED, new CopyOnWriteArrayList<>(), "§4", Material.RED_WOOL, "§4Rot"));
    }

    public GemgrabTeam getTeamByColor(TeamColor teamColor) {
        for (GemgrabTeam gemgrabTeam : teams) {
            if (gemgrabTeam.getTeamColor().equals(teamColor)) {
                return gemgrabTeam;
            }
        }
        return null;
    }

    public String getPlayerTeamDisplay(UUID player) {
        if (isInTeam(player)) {
            for (GemgrabTeam gemgrabTeam : teams) {
                if (gemgrabTeam.getPlayers().contains(player)) {
                    return gemgrabTeam.getName();
                }
            }
        } else {
            return "§e-";
        }
        return "";
    }

    public GemgrabTeam getPlayerTeam(UUID player) {
        if (isInTeam(player)) {
            for (GemgrabTeam gemgrabTeam : teams) {
                if (gemgrabTeam.getPlayers().contains(player)) {
                    return gemgrabTeam;
                }
            }
        }
        return null;
    }

    public boolean isFull(GemgrabTeam team) {
        return team.getPlayers().size() >= GameSettings.getTeamSize();
    }

    public GemgrabTeam getTeamByName(String name) {
        for (GemgrabTeam gemgrabTeam : teams) {
            if (gemgrabTeam.getName().equals(name)) {
                return gemgrabTeam;
            }
        }
        return null;
    }

    public String getColorcodeByColor(TeamColor teamColor) {
        switch (teamColor) {
            case BLUE -> {
                return "§1";
            }
            case RED -> {
                return "§4";
            }
        }
        return "";
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
        getTeamByColor(team).getPlayers().add(player);
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
