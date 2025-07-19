package net.teamcastle.gemgrab.scoreboard;

import net.teamcastle.gemgrab.manager.game.GameSettings;
import net.teamcastle.gemgrab.manager.locations.map.GameMapManager;
import net.teamcastle.gemgrab.manager.teams.TeamManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

@RequiredArgsConstructor
public class Scoreboard {

    private final TeamManager teamManager;
    private final GameMapManager gameMapManager;

    public void setScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        org.bukkit.scoreboard.Scoreboard scoreboard = manager.getNewScoreboard();
        int onlinePlayers = Bukkit.getOnlinePlayers().size();

        Objective objective = scoreboard.registerNewObjective("test", "dummy", "  §aGemGrab  ");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Team gameMap = scoreboard.registerNewTeam("gamemap");
        gameMap.addEntry("§2");
        gameMap.setPrefix("§6" + gameMapManager.getGameMap().getName());

        Team onlineplayers = scoreboard.registerNewTeam("onlineplayers");
        onlineplayers.addEntry("§3");
        onlineplayers.setPrefix("§2" + onlinePlayers + "§7/§4" + GameSettings.getTeamSize() * 2);

        Team playerRole = scoreboard.registerNewTeam("team");
        playerRole.addEntry("§4");
        playerRole.setPrefix(teamManager.getPlayerTeamDisplay(player.getUniqueId()));

        Score s1 = objective.getScore(" ");
        Score s2 = objective.getScore("§7Map§8:");
        Score s3 = objective.getScore("§2");
        Score s4 = objective.getScore("     ");
        Score s5 = objective.getScore("§7Team§8:");
        Score s6 = objective.getScore("§4");
        Score s7 = objective.getScore("  ");
        Score s8 = objective.getScore("§7Online§8:");
        Score s9 = objective.getScore("§3");
        Score s10 = objective.getScore("   ");
        Score s11 = objective.getScore("§7Server§8:");
        Score s12 = objective.getScore("§6TeamCastle.net");

        s1.setScore(0);
        s2.setScore(-1);
        s3.setScore(-2);
        s4.setScore(-3);
        s5.setScore(-4);
        s6.setScore(-5);
        s7.setScore(-6);
        s8.setScore(-7);
        s9.setScore(-8);
        s10.setScore(-9);
        s11.setScore(-10);
        s12.setScore(-11);
        player.setScoreboard(scoreboard);
    }


    public void update(Player player) {
        org.bukkit.scoreboard.Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective("test");
        int onlinePlayers = Bukkit.getOnlinePlayers().size();

        Team gameMap = scoreboard.getTeam("gamemap");
        gameMap.addEntry("§2");
        gameMap.setPrefix("§6" + gameMapManager.getGameMap().getName());

        Team onlineplayers = scoreboard.getTeam("onlineplayers");
        onlineplayers.addEntry("§3");
        onlineplayers.setPrefix("§2" + onlinePlayers + "§7/§4" + GameSettings.getTeamSize() * 2);

        Team playerRole = scoreboard.getTeam("team");
        playerRole.addEntry("§4");
        playerRole.setPrefix(teamManager.getPlayerTeamDisplay(player.getUniqueId()));

        Score s1 = objective.getScore(" ");
        Score s2 = objective.getScore("§7Map§8:");
        Score s3 = objective.getScore("§2");
        Score s4 = objective.getScore("     ");
        Score s5 = objective.getScore("§7Team§8:");
        Score s6 = objective.getScore("§4");
        Score s7 = objective.getScore("  ");
        Score s8 = objective.getScore("§7Online§8:");
        Score s9 = objective.getScore("§3");
        Score s10 = objective.getScore("   ");
        Score s11 = objective.getScore("§7Server§8:");
        Score s12 = objective.getScore("§6TeamCastle.net");

        s1.setScore(0);
        s2.setScore(-1);
        s3.setScore(-2);
        s4.setScore(-3);
        s5.setScore(-4);
        s6.setScore(-5);
        s7.setScore(-6);
        s8.setScore(-7);
        s9.setScore(-8);
        s10.setScore(-9);
        s11.setScore(-10);
        s12.setScore(-11);

        player.setScoreboard(scoreboard);
    }

    public void updateGame(Player player) {
        org.bukkit.scoreboard.Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective("test");
        int onlinePlayers = Bukkit.getOnlinePlayers().size();

        Team gameMap = scoreboard.getTeam("gamemap");
        gameMap.addEntry("§2");
        gameMap.setPrefix("§6" + gameMapManager.getGameMap().getName());

        Team onlineplayers = scoreboard.getTeam("onlineplayers");
        onlineplayers.addEntry("§3");
        onlineplayers.setPrefix("§2" + onlinePlayers + "§7/§4" + GameSettings.getTeamSize() * 2);

        Team playerRole = scoreboard.getTeam("team");
        playerRole.addEntry("§4");
        playerRole.setPrefix(teamManager.getPlayerTeamDisplay(player.getUniqueId()));

        Score s1 = objective.getScore(" ");
        Score s2 = objective.getScore("§7Team§8:");
        Score s3 = objective.getScore("§4");
        Score s4 = objective.getScore("     ");
        Score s5 = objective.getScore("§7Kills§8:");
        Score s6 = objective.getScore("§c0");
        Score s7 = objective.getScore("  ");
        Score s8 = objective.getScore("§7Team-Gems§8:");
        Score s9 = objective.getScore("§20");
        Score s10 = objective.getScore("   ");
        Score s11 = objective.getScore("§7Server§8:");
        Score s12 = objective.getScore("§6TeamCastle.net");

        s1.setScore(0);
        s2.setScore(-1);
        s3.setScore(-2);
        s4.setScore(-3);
        s5.setScore(-4);
        s6.setScore(-5);
        s7.setScore(-6);
        s8.setScore(-7);
        s9.setScore(-8);
        s10.setScore(-9);
        s11.setScore(-10);
        s12.setScore(-11);

        player.setScoreboard(scoreboard);
    }
}