package net.teamcastle.gemgrab.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public abstract class ScoreboardBuilder {

    protected final Scoreboard scoreboard;

    protected final Objective objective;

    protected final Player player;

    public ScoreboardBuilder(Player player, String displayname) {
        this.player = player;

        if (player.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }

        this.scoreboard = player.getScoreboard();


        if (this.scoreboard.getObjective("display") != null) {
            this.scoreboard.getObjective("display").unregister();
        }

        this.objective = this.scoreboard.registerNewObjective("display", "dummy", displayname);
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        createScoreboard();
    }

    public abstract void createScoreboard();

    public abstract void update();


    public void setDisplayname(String displayname) {
        this.objective.setDisplayName(displayname);
    }

    public void setscore(String content, int score) {
        this.objective.getScore(content).setScore(score);
    }

    public void removescore(String content) {
        this.scoreboard.resetScores(content);
    }

}