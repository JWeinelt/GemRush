package net.teamcastle.gemgrab.manager.game.start;


import eu.cloudnetservice.driver.event.events.service.CloudServiceEvent;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;
import eu.cloudnetservice.modules.bridge.BridgeServiceHelper;
import eu.cloudnetservice.wrapper.holder.ServiceInfoHolder;
import net.teamcastle.gemgrab.TeamcastleGemgrab;
import net.teamcastle.gemgrab.manager.game.Gamestate;
import net.teamcastle.gemgrab.manager.game.gameplay.BossbarHandler;
import net.teamcastle.gemgrab.manager.game.gems.GemSpawnerManager;
import net.teamcastle.gemgrab.utils.Messages;
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
                        player.sendTitle("§a§lGemGrab", "§aViel Glück");
                        player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 3, 10);
                    });
                    Bukkit.getScheduler().cancelTask(taskIdHolder[0]);
                    GemSpawnerManager.spawnGems(TeamcastleGemgrab.getGameMap().getSpawner());
                    BossbarHandler.setDefaultBossbar();
                    TeamcastleGemgrab.setGamestate(Gamestate.INGAME);
                }
            }
        }, 0L, 20L).getTaskId();
    }
}
