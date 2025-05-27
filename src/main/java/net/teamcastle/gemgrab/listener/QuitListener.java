package net.teamcastle.gemgrab.listener;

import net.teamcastle.gemgrab.manager.teams.TeamManager;
import net.teamcastle.gemgrab.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class QuitListener implements Listener {

    private final TeamManager teamManager;

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage(Messages.mainPrefix + "§c" + player.getName() + "§7 hat den Server §cverlassen§7!");
        if (teamManager.isInTeam(player.getUniqueId())) {
            teamManager.leaveTeam(player.getUniqueId());
        }
    }
}
