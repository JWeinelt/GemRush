package de.joniwoch.teamcastlegemgrab.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class LobbyScoreboard {
    public static void setScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();
        int onlinePlayers = Bukkit.getOnlinePlayers().size();

        Objective objective = scoreboard.registerNewObjective("test", "dummy", "  §4Gem§cGrab  ");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Team onlineplayers = scoreboard.registerNewTeam("onlineplayers");
        onlineplayers.addEntry("§3");
        onlineplayers.setPrefix("§2" + onlinePlayers + "§7/§416");

        Team playerRole = scoreboard.registerNewTeam("role");
        playerRole.addEntry("§4");
        playerRole.setPrefix("§cSOON");

        Score s1 = objective.getScore(" ");
        Score s2 = objective.getScore("§7Willkommen§8:");
        Score s3 = objective.getScore("§a" + player.getName());
        Score s4 = objective.getScore("     ");
        Score s5 = objective.getScore("§7Team§8:");
        Score s6 = objective.getScore("§e-");
        Score s7 = objective.getScore("  ");
        Score s8 = objective.getScore("§7Online§8:");
        Score s9 = objective.getScore("§3");
        Score s10 = objective.getScore("   ");
        Score s11 = objective.getScore("§7Server§8:");
        Score s12 = objective.getScore("§c§lTeamCastle.net");

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

    public static void update(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective("test");
        int onlinePlayers = Bukkit.getOnlinePlayers().size();

        Team onlineplayers = scoreboard.getTeam("onlineplayers");
        onlineplayers.addEntry("§3");
        onlineplayers.setPrefix("§2" + onlinePlayers + "§7/§412");

        Team playerRole = scoreboard.getTeam("role");
        playerRole.addEntry("§4");
        playerRole.setPrefix("§cSOON");

        Score s1 = objective.getScore(" ");
        Score s2 = objective.getScore("§7Willkommen§8:");
        Score s3 = objective.getScore("§a" + player.getName());
        Score s4 = objective.getScore("     ");
        Score s5 = objective.getScore("§7Team§8:");
        Score s6 = objective.getScore("§e-");
        Score s7 = objective.getScore("  ");
        Score s8 = objective.getScore("§7Online§8:");
        Score s9 = objective.getScore("§3");
        Score s10 = objective.getScore("   ");
        Score s11 = objective.getScore("§7Server§8:");
        Score s12 = objective.getScore("§c§lTeamCastle.net");

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