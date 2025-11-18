package net.teamcastle.gemgrab.manager;

import net.teamcastle.gemgrab.manager.player.PlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PreGameListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        PlayerManager.addGemGrabPlayer(e.getPlayer());
    }
}