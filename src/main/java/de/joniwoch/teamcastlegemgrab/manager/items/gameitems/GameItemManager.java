package de.joniwoch.teamcastlegemgrab.manager.items.gameitems;

import de.joniwoch.teamcastlegemgrab.manager.items.ItemAPI;
import de.joniwoch.teamcastlegemgrab.manager.teams.GemgrabTeam;
import de.joniwoch.teamcastlegemgrab.manager.teams.TeamManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

@RequiredArgsConstructor
public class GameItemManager {

    private final TeamManager teamManager;

    public ItemStack createBlueChestplate() {
        ItemStack blueChestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta = (LeatherArmorMeta) blueChestplate.getItemMeta();
        if (meta != null) {
            meta.setColor(Color.BLUE);
            meta.setDisplayName("§6Brustplatte");
            blueChestplate.setItemMeta(meta);
        }
        return blueChestplate;
    }

    public ItemStack createRedChestplate() {
        ItemStack redChestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta = (LeatherArmorMeta) redChestplate.getItemMeta();
        if (meta != null) {
            meta.setColor(Color.RED);
            redChestplate.setItemMeta(meta);
        }
        return redChestplate;
    }

    public void setGameItems(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
        if (potionMeta != null) {
            potionMeta.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL, false, true));
            potion.setItemMeta(potionMeta);
        }

        ItemStack blueChestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta = (LeatherArmorMeta) blueChestplate.getItemMeta();
        if (meta != null) {
            meta.setColor(Color.BLUE);
            meta.setDisplayName("§6Brustplatte");
            blueChestplate.setItemMeta(meta);
        }

        player.getInventory().setItem(0, new ItemAPI("§6Schwert", Material.STONE_SWORD, 1, true).build());
        player.getInventory().setItem(1, new ItemAPI("§6Bogen", Material.BOW, 1, true).build());
        player.getInventory().setItem(2, potion);
        player.getInventory().setBoots(new ItemAPI("§6Stiefel", Material.GOLDEN_BOOTS, 1, true).build());
        player.getInventory().setLeggings(new ItemAPI("§6Hose", Material.IRON_LEGGINGS, 1, true).build());
        player.getInventory().setHelmet(new ItemAPI("§6Helm", Material.IRON_HELMET, 1, true).build());
        player.getInventory().addItem(new ItemAPI("§6Pfeil", Material.ARROW, 3).build());

        GemgrabTeam team = teamManager.getPlayerTeam(player.getUniqueId());
        switch (team.getTeamColor()) {
            case BLUE -> {
                player.getInventory().setChestplate(createBlueChestplate());
            }
            case RED -> {
                player.getInventory().setChestplate(createRedChestplate());
            }
        }

    }
}
