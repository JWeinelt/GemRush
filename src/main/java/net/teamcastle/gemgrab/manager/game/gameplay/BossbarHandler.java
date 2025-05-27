package net.teamcastle.gemgrab.manager.game.gameplay;

import net.teamcastle.gemgrab.TeamcastleGemgrab;
import net.teamcastle.gemgrab.manager.game.gems.GemManager;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.EnumMap;
import java.util.Map;

@UtilityClass
public class BossbarHandler {

    private BossBar defaultBossBar;
    private Map<TeamColor, BossBar> teamBossBars = new EnumMap<>(TeamColor.class);
    private Map<TeamColor, BukkitTask> countdownTasks = new EnumMap<>(TeamColor.class);
    private Map<TeamColor, Boolean> isCountdownRunning = new EnumMap<>(TeamColor.class);

    public enum TeamColor {
        BLUE("§1Blau", BarColor.BLUE, Sound.BLOCK_NOTE_BLOCK_GUITAR),
        RED("§4Rot", BarColor.RED, Sound.BLOCK_NOTE_BLOCK_GUITAR);

        private final String name;
        private final BarColor barColor;
        private final Sound countdownSound;

        TeamColor(String name, BarColor barColor, Sound countdownSound) {
            this.name = name;
            this.barColor = barColor;
            this.countdownSound = countdownSound;
        }

        public String getName() {
            return name;
        }

        public BarColor getBarColor() {
            return barColor;
        }

        public Sound getCountdownSound() {
            return countdownSound;
        }
    }

    GemManager gemManager = TeamcastleGemgrab.getInstance().getGemManager();

    public void setDefaultBossbar() {
        defaultBossBar = Bukkit.createBossBar(
                "§8| §a§l0 §7- §1Blau §7----------- §4Rot §7- §a§l0 §8|",
                BarColor.WHITE,
                BarStyle.SOLID
        );

        addAllPlayersToBossBar(defaultBossBar);

        new BukkitRunnable() {
            @Override
            public void run() {
                String title = "§8| §a§l" + gemManager.calculateTeamGemsBlue()
                        + " §7- §1Blau §7----------- §4Rot §7- §a§l"
                        + gemManager.calculateTeamGemsRed() + " §8|";

                defaultBossBar.setTitle(title);
            }
        }.runTaskTimer(TeamcastleGemgrab.getInstance(), 0L, 2L);
    }

    private void addAllPlayersToBossBar(BossBar bossBar) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(player);
        }
    }

    public void startBossBarCountdown(TeamColor teamColor, int seconds) {
        stopBossBarCountdown(teamColor);

        BossBar bossBar = Bukkit.createBossBar(
                "§7Team " + teamColor.getName() + " §7hat genügend §2§lGems!",
                teamColor.getBarColor(), BarStyle.SOLID, BarFlag.DARKEN_SKY);
        bossBar.setProgress(1.0);

        teamBossBars.put(teamColor, bossBar);
        defaultBossBar.removeAll();
        addAllPlayersToBossBar(bossBar);

        isCountdownRunning.put(teamColor, true);
        final int totalTicks = seconds * 20;

        countdownTasks.put(teamColor, new BukkitRunnable() {
            int ticksElapsed = 0;

            @Override
            public void run() {
                if (ticksElapsed % 20 == 0) {
                    Bukkit.getOnlinePlayers().forEach(player ->
                            player.playSound(player.getLocation(), teamColor.getCountdownSound(), 3, 3)
                    );
                }

                double progress = 1.0 - ((double) ticksElapsed / totalTicks);
                bossBar.setProgress(progress);
                ticksElapsed++;

                if (ticksElapsed >= totalTicks) {
                    endBossBarCountdown(teamColor, bossBar);
                }
            }
        }.runTaskTimer(TeamcastleGemgrab.getInstance(), 0L, 1L));
    }

    private void endBossBarCountdown(TeamColor teamColor, BossBar bossBar) {
        bossBar.setProgress(0.0);
        countdownTasks.get(teamColor).cancel();
        isCountdownRunning.put(teamColor, false);
        bossBar.setTitle("§7Team " + teamColor.getName() + " §7hat §agewonnen!");
        TeamcastleGemgrab.getInstance().getWinManager().executeWinSequenze(teamColor, bossBar);
    }

    public void stopBossBarCountdown(TeamColor teamColor) {
        BukkitTask task = countdownTasks.get(teamColor);
        if (task != null && !task.isCancelled()) {
            task.cancel();
            isCountdownRunning.put(teamColor, false);
            teamBossBars.get(teamColor).removeAll();
            addAllPlayersToBossBar(defaultBossBar);
        }
    }

    public boolean isCountdownRunning(TeamColor teamColor) {
        return isCountdownRunning.getOrDefault(teamColor, false);
    }
}
