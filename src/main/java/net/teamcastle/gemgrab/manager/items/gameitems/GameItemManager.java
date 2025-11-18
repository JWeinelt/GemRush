package net.teamcastle.gemgrab.manager.items.gameitems;

import de.codeblocksmc.codelib.wrapping.ItemBuilder;
import net.teamcastle.gemgrab.GemRush;
import net.teamcastle.gemgrab.manager.GameManager;
import net.teamcastle.gemgrab.manager.game.Game;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class GameItemManager {
    public static GameItemManager getInstance() {
        return GemRush.getInstance().getGameItemManager();
    }

    public ItemStack createBlueChestplate() {
        return new ItemBuilder(Material.LEATHER_CHESTPLATE).leatherColor(Color.BLUE).displayname("§6Chestplate")
                .unbreakable(true).flags(ItemFlag.HIDE_UNBREAKABLE).build();
    }

    public ItemStack createRedChestplate() {
        return new ItemBuilder(Material.LEATHER_CHESTPLATE).leatherColor(Color.RED).displayname("§6Chestplate")
                .unbreakable(true).flags(ItemFlag.HIDE_UNBREAKABLE).build();
    }

    public void setGameItems(Player player, Game game) {
        player.getInventory().clear();

        player.getInventory().setItem(0, new ItemBuilder(Material.STONE_SWORD).displayname("§6Sword").unbreakable(true).build());
        player.getInventory().setItem(1, new ItemBuilder(Material.BOW).displayname("§6Bow").unbreakable(true).build());
        player.getInventory().setItem(2, new ItemBuilder(Material.GOLDEN_APPLE).displayname("§6Golden Apple").unbreakable(true).build());
        player.getInventory().setBoots(new ItemBuilder(Material.GOLDEN_BOOTS).displayname("§6Boots").unbreakable(true).build());
        player.getInventory().setLeggings(new ItemBuilder(Material.IRON_LEGGINGS).displayname("§6Leggings").unbreakable(true).build());
        player.getInventory().setHelmet(new ItemBuilder(Material.IRON_HELMET).displayname("§6Helmet").unbreakable(true).build());
        player.getInventory().addItem(new ItemBuilder(Material.ARROW).displayname("§6Arrow").amount(3).build());

        switch (game.getPlayerTeam(player.getUniqueId())) {
            case BLUE -> player.getInventory().setChestplate(createBlueChestplate());
            case RED -> player.getInventory().setChestplate(createRedChestplate());
        }

    }
}
