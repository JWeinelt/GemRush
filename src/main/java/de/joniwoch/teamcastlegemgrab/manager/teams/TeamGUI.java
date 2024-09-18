package de.joniwoch.teamcastlegemgrab.manager.teams;

import de.joniwoch.teamcastlegemgrab.manager.game.GameSettings;
import de.joniwoch.teamcastlegemgrab.utils.ItemAPI;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RequiredArgsConstructor
public class TeamGUI {

    private final TeamManager teamManager;

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "§8» §e§lTeams §8«");

        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, new ItemAPI(" ", Material.WHITE_STAINED_GLASS_PANE, 1).build());
            }
        }
        inv.setItem(10, null);
        inv.setItem(16, null);

        teamManager.teams.forEach(gemgrabTeam -> {

            String teamName = "§7Team " + gemgrabTeam.getName() + " " + "§7(§e" + gemgrabTeam.getPlayers().size() + "§8/§e" + GameSettings.getTeamSize() + "§7)";;
            Material teamMaterial = gemgrabTeam.getMaterial();
            List<String> teamLore = new CopyOnWriteArrayList<>();
            teamLore.add(" ");

            for (int i = 0; i < GameSettings.getTeamSize(); i++) {
                if (gemgrabTeam.getPlayers().isEmpty()) {
                    teamLore.add("§7-");
                } else if (i < gemgrabTeam.getPlayers().size()) {
                    teamLore.add("§7- §d" + Bukkit.getPlayer(gemgrabTeam.getPlayers().get(i)).getName());
                } else {
                    teamLore.add("§7-");
                }
            }

            inv.addItem(new ItemAPI(teamName, teamMaterial, 1, teamLore).build());
        });

        player.openInventory(inv);

    }

}
