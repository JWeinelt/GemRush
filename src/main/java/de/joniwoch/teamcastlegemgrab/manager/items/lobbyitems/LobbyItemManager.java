package de.joniwoch.teamcastlegemgrab.manager.items.lobbyitems;

import de.joniwoch.teamcastlegemgrab.utils.ItemAPI;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class LobbyItemManager {

    public void setLobbyItems(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getInventory().setItem(1, new ItemAPI("§8» §e§lTeams §8«", Material.BLACK_BED, 1).build());
        player.getInventory().setItem(4, new ItemAPI("§8» §d§lStats §8«", Material.PAPER, 1).build());
        player.getInventory().setItem(7, new ItemAPI("§8» §c§lZurück zur Lobby §8«", Material.RED_DYE, 1).build());
    }
}
