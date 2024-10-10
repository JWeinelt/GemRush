package de.joniwoch.teamcastlegemgrab.manager.game.start;


import de.joniwoch.teamcastlegemgrab.TeamcastleGemgrab;
import de.joniwoch.teamcastlegemgrab.manager.game.Gamestate;
import de.joniwoch.teamcastlegemgrab.manager.game.gameplay.BossbarHandler;
import de.joniwoch.teamcastlegemgrab.manager.game.gems.GemManager;
import de.joniwoch.teamcastlegemgrab.manager.game.gems.GemSpawnerManager;
import de.joniwoch.teamcastlegemgrab.manager.locations.map.GameMapManager;
import de.joniwoch.teamcastlegemgrab.manager.teams.TeamManager;
import de.joniwoch.teamcastlegemgrab.utils.Messages;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

@UtilityClass
public class GameStartCountdown {

    public static void startStarterCountdown() {
        final int[] taskIdHolder = new int[1];
        AtomicInteger countdown = new AtomicInteger(10);

        taskIdHolder[0] = Bukkit.getScheduler().runTaskTimer(TeamcastleGemgrab.getInstance(), () -> {
            int secondsLeft = countdown.getAndDecrement();

            if (Arrays.asList(10, 1, 2, 4, 5, 3).contains(secondsLeft)) {
                Bukkit.broadcastMessage(Messages.mainPrefix + "Das §aSpiel §7startet in §c" + secondsLeft + "s");
                Bukkit.getOnlinePlayers()
                        .forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 10, 3));
            }

            switch (secondsLeft) {
                case 0 -> {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        player.sendTitle("§4§lGem§c§lGrab", "§aViel Glück");
                        player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 3, 10);
                    });
                    Bukkit.getScheduler().cancelTask(taskIdHolder[0]);
                    TeamcastleGemgrab.setGamestate(Gamestate.INGAME);
                    GemSpawnerManager.spawnGems(TeamcastleGemgrab.getGameMap().getSpawner());

                    BossbarHandler.setDefaultBossbar();
                }
            }
        }, 0L, 20L).getTaskId();
    }
}
