package de.joniwoch.teamcastlegemgrab.listener;

import de.joniwoch.teamcastlegemgrab.TeamcastleGemgrab;
import de.joniwoch.teamcastlegemgrab.manager.items.lobbyitems.LobbyItemManager;
import de.joniwoch.teamcastlegemgrab.utils.Messages;
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
        lobbyItemManager.setLobbyItems(player);
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setLevel(Calendar.getInstance().get(Calendar.YEAR));
        player.setExp((float) Calendar.getInstance().get(Calendar.DAY_OF_YEAR) / 365);
        player.sendTitle("§4§lGem§c§lGrab", "§aViel Spaß!");
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 3);
    }
}
