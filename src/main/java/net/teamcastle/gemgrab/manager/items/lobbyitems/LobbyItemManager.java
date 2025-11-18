package net.teamcastle.gemgrab.manager.items.lobbyitems;

import de.codeblocksmc.codelib.wrapping.ItemBuilder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.Utility;
import org.bukkit.entity.Player;

@UtilityClass
public class LobbyItemManager {

    public static void setLobbyItems(Player player) {
        player.getInventory().clear();
        player.getInventory().setItem(1, new ItemBuilder(Material.ORANGE_BED).displayname("§7» §6Teams §7«").build());
        player.getInventory().setItem(4, new ItemBuilder(Material.PAPER).displayname("§7» §6Stats §7«").build());
        player.getInventory().setItem(7, new ItemBuilder(Material.RED_DYE).displayname("§7» §cBack to Lobby §7«").build());
    }
}