package de.joniwoch.teamcastlegemgrab.manager.game.gameplay;

import de.joniwoch.teamcastlegemgrab.TeamcastleGemgrab;
import de.joniwoch.teamcastlegemgrab.manager.game.Gamestate;
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

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

@UtilityClass
public class BossbarHandler {

    public BossBar defaultBossBar;
    public BossBar blueWinBossBar;
    public BossBar redWinBossBar;
    private BukkitTask countdownTaskBlue;
    private BukkitTask countdownTaskRed;
    private boolean isCountdownRunningBlue = false;
    private boolean isCountdownRunningRed = false;

    public void setDefaultBossbar() {
        defaultBossBar = Bukkit.createBossBar("§8| §a§l0 §7- §1Blau §7----------- §4Rot §7- §a§l0 §8|", BarColor.WHITE, BarStyle.SOLID);

        for (Player player : Bukkit.getOnlinePlayers()) {
            defaultBossBar.addPlayer(player);
        }
    }

    public void startBossBarCountdownBlue(int seconds) {
        stopBossBarCountdownBlue();
        blueWinBossBar = Bukkit.createBossBar("§7Team §1§lBlau §7hat genügend §2§lGems!", BarColor.BLUE, BarStyle.SOLID);
        blueWinBossBar.setProgress(1.0);
        blueWinBossBar.setColor(BarColor.BLUE);
        blueWinBossBar.addFlag(BarFlag.DARKEN_SKY);
        defaultBossBar.removeAll();
        for (Player player : Bukkit.getOnlinePlayers()) {
            blueWinBossBar.addPlayer(player);
        }
        final int totalTicks = seconds * 20;
        isCountdownRunningBlue = true;
        countdownTaskBlue = new BukkitRunnable() {
            int ticksElapsed = 0;
            @Override
            public void run() {
                switch (ticksElapsed) {
                    case 20, 40, 60, 80, 100, 120, 140, 160, 180, 200, 210, 220, 230, 240, 250, 260, 270, 280, 290 -> {
                        Bukkit.getOnlinePlayers().forEach(player -> {
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR,3, 3);
                        });
                    }
                }
                double progress = 1.0 - ((double) ticksElapsed / totalTicks);
                blueWinBossBar.setProgress(progress);
                ticksElapsed++;
                if (ticksElapsed >= totalTicks) {
                    blueWinBossBar.setProgress(0.0);
                    this.cancel();
                    isCountdownRunningBlue = false;
                    TeamcastleGemgrab.setGamestate(Gamestate.ENDED);
                    blueWinBossBar.setTitle("§7Team §1§lBlau §7hat §agewonnen!");
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 3, 3);
                        player.sendTitle("§7Team §1§lBlau", "§7hat §agewonnen§7!");
                    });
                }
            }
        }.runTaskTimer(TeamcastleGemgrab.getInstance(), 0L, 1L);
    }

    public void stopBossBarCountdownBlue() {
        if (countdownTaskBlue != null && !countdownTaskBlue.isCancelled()) {
            countdownTaskBlue.cancel();
            countdownTaskBlue = null;
            isCountdownRunningBlue = false;
            blueWinBossBar.removeAll();
            for (Player player : Bukkit.getOnlinePlayers()) {
                defaultBossBar.addPlayer(player);
            }
        }
    }

    public void startBossBarCountdownRed(int seconds) {
        stopBossBarCountdownRed();
        redWinBossBar = Bukkit.createBossBar("§7Team §4§lRot §7hat genügend §2§lGems!", BarColor.BLUE, BarStyle.SOLID);
        redWinBossBar.setProgress(1.0);
        redWinBossBar.setColor(BarColor.RED);
        redWinBossBar.addFlag(BarFlag.DARKEN_SKY);
        defaultBossBar.removeAll();
        for (Player player : Bukkit.getOnlinePlayers()) {
            redWinBossBar.addPlayer(player);
        }
        final int totalTicks = seconds * 20;
        isCountdownRunningRed = true;
        countdownTaskRed = new BukkitRunnable() {
            int ticksElapsed = 0;
            @Override
            public void run() {
                switch (ticksElapsed) {
                    case 20, 40, 60, 80, 100, 120, 140, 160, 180, 200, 210, 220, 230, 240, 250, 260, 270, 280, 290 -> {
                        Bukkit.getOnlinePlayers().forEach(player -> {
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR,3, 3);
                        });
                    }
                }
                double progress = 1.0 - ((double) ticksElapsed / totalTicks);
                redWinBossBar.setProgress(progress);
                ticksElapsed++;
                if (ticksElapsed >= totalTicks) {
                    redWinBossBar.setProgress(0.0);
                    this.cancel();
                    isCountdownRunningRed = false;
                    TeamcastleGemgrab.setGamestate(Gamestate.ENDED);
                    redWinBossBar.setTitle("§7Team §4§lRot §7hat §agewonnen!");
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 3, 3);
                        player.sendTitle("§7Team §4§lRot", "§7hat §agewonnen§7!");
                    });
                }
            }
        }.runTaskTimer(TeamcastleGemgrab.getInstance(), 0L, 1L);
    }

    public void stopBossBarCountdownRed() {
        if (countdownTaskRed != null && !countdownTaskRed.isCancelled()) {
            countdownTaskRed.cancel();
            countdownTaskRed = null;
            isCountdownRunningRed = false;
            redWinBossBar.removeAll();
            for (Player player : Bukkit.getOnlinePlayers()) {
                defaultBossBar.addPlayer(player);
            }
        }
    }

    public boolean checkCountdownRunningBlue() {
        return isCountdownRunningBlue;
    }

    public boolean checIsCountdownRunningRed() {
        return isCountdownRunningRed;
    }
}
