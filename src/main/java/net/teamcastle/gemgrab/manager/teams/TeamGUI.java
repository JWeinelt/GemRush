package net.teamcastle.gemgrab.manager.teams;

import net.teamcastle.gemgrab.manager.game.GameSettings;
import net.teamcastle.gemgrab.manager.items.ItemAPI;
import lombok.RequiredArgsConstructor;
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
        Inventory inv = Bukkit.createInventory(null, 27, "§7» §6Teams §7«");

        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, new ItemAPI(" ", Material.ORANGE_STAINED_GLASS_PANE, 1).build());
            }
        }
        inv.setItem(10, null);
        inv.setItem(16, null);

        teamManager.teams.forEach(gemgrabTeam -> {

            String teamName = "§7Team " + gemgrabTeam.getName() + " " + "§7(" + gemgrabTeam.getPlayers().size() + "§8/§7" + GameSettings.getTeamSize() + "§7)";;
            Material teamMaterial = gemgrabTeam.getMaterial();
            List<String> teamLore = new CopyOnWriteArrayList<>();
            teamLore.add(" ");

            for (int i = 0; i < GameSettings.getTeamSize(); i++) {
                if (gemgrabTeam.getPlayers().isEmpty()) {
                    teamLore.add("§7»");
                } else if (i < gemgrabTeam.getPlayers().size()) {
                    teamLore.add("§7» §a" + Bukkit.getPlayer(gemgrabTeam.getPlayers().get(i)).getName());
                } else {
                    teamLore.add("§7»");
                }
            }

            teamLore.add(" ");
            teamLore.add("§7Klicke um Team " + gemgrabTeam.getName() + " §7beizutreten!");

            inv.addItem(new ItemAPI(teamName, teamMaterial, 1, teamLore).build());
            inv.setItem(22, new ItemAPI("§cTeam verlassen", Material.BARRIER, 1).build());
        });

        player.openInventory(inv);

    }

}
