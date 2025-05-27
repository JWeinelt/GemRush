package net.teamcastle.gemgrab.listener;

import net.teamcastle.gemgrab.TeamcastleGemgrab;
import net.teamcastle.gemgrab.manager.items.lobbyitems.LobbyItemManager;
import net.teamcastle.gemgrab.manager.locations.LobbyLocationManager;
import net.teamcastle.gemgrab.manager.player.GemgrabPlayerManager;
import net.teamcastle.gemgrab.scoreboard.Scoreboard;
import net.teamcastle.gemgrab.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Calendar;

@RequiredArgsConstructor
public class JoinListener implements Listener {

    private final LobbyItemManager lobbyItemManager;
    private final LobbyLocationManager lobbyLocationManager;
    private final Scoreboard scoreboard;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage(Messages.mainPrefix + "§a" + player.getName() + "§7 hat den Server §abetreten§7!");
        switch (TeamcastleGemgrab.getGamestate()) {
            case LOBBY -> {
                doLobbyJoin(player);
            }
        }
    }

    public void doLobbyJoin(Player player) {
        GemgrabPlayerManager.addGemgrabPlayer(player);
        lobbyItemManager.setLobbyItems(player);
        lobbyLocationManager.teleportLobbySpawn(player);
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setLevel(Calendar.getInstance().get(Calendar.YEAR));
        player.setExp((float) Calendar.getInstance().get(Calendar.DAY_OF_YEAR) / 365);
        player.sendTitle("§6GemGrab", "§aViel Erfolg.");
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 3);
        scoreboard.setScoreboard(player);
    }
}
