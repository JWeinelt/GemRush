package de.joniwoch.teamcastlegemgrab.manager.items;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemAPI {

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    public ItemAPI(String name, Material material, int amount) {
        this.itemStack = new ItemStack(material, amount);
        this.itemMeta = this.itemStack.getItemMeta();
        this.itemMeta.setDisplayName(name);
    }

    public ItemAPI(String name, Material material, int amount, boolean unbreakable) {
        this.itemStack = new ItemStack(material, amount);
        this.itemMeta = this.itemStack.getItemMeta();
        this.itemMeta.setUnbreakable(true);
        this.itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        this.itemMeta.setDisplayName(name);
    }

    public ItemAPI(String name, Material material, int amount, byte b) {
        this.itemStack = new ItemStack(material, amount, b);
        this.itemMeta = this.itemStack.getItemMeta();
        this.itemMeta.setDisplayName(name);
    }

    public ItemAPI(String name, Material material, int amount, List<String> lore) {
        this.itemStack = new ItemStack(material, amount);
        this.itemMeta = this.itemStack.getItemMeta();
        this.itemMeta.setDisplayName(name);
        this.itemMeta.setLore(lore);
    }

    public ItemAPI(String name, Material material, int amount, List<String> lore, Enchantment enchantment) {
        this.itemStack = new ItemStack(material, amount);
        this.itemMeta = this.itemStack.getItemMeta();
        this.itemMeta.setDisplayName(name);
        this.itemMeta.setLore(lore);
        this.itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    }

    public ItemAPI(String name, Material material, int amount, byte b, List<String> lore) {
        this.itemStack = new ItemStack(material, amount, b);
        this.itemMeta = this.itemStack.getItemMeta();
        this.itemMeta.setDisplayName(name);
        this.itemMeta.setLore(lore);
    }

    public ItemAPI(String name, Material material, int amount, Enchantment enchantment, List<String> lore) {
        this.itemStack = new ItemStack(material, amount);
        this.itemMeta = this.itemStack.getItemMeta();
        this.itemMeta.setDisplayName(name);
        this.itemMeta.addEnchant(enchantment, 3, true);
        this.itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        this.itemMeta.setLore(lore);
    }

    public ItemStack build() {
        this.itemStack.setItemMeta(this.itemMeta);
        return this.itemStack;
    }
}
