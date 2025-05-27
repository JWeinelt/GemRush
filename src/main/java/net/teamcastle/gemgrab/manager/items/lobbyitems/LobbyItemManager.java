package net.teamcastle.gemgrab.manager.items.lobbyitems;

import net.teamcastle.gemgrab.manager.items.ItemAPI;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class LobbyItemManager {

    public void setLobbyItems(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getInventory().setItem(1, new ItemAPI("§7» §6Teams §7«", Material.ORANGE_BED, 1).build());
        player.getInventory().setItem(4, new ItemAPI("§7» §6Stats §7«", Material.PAPER, 1).build());
        player.getInventory().setItem(7, new ItemAPI("§7» §cZurück zur Lobby §7«", Material.RED_DYE, 1).build());
    }
}
