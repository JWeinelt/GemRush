package net.teamcastle.gemgrab.manager;

import de.codeblocksmc.codelib.locations.LocUtil;
import de.codeblocksmc.codelib.locations.LocationSection;
import de.codeblocksmc.codelib.locations.LocationWrapper;
import de.codeblocksmc.codelib.wrapping.ItemBuilder;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.teamcastle.gemgrab.manager.map.GameMap;
import net.teamcastle.gemgrab.manager.teams.TeamColor;
import net.teamcastle.gemgrab.storage.LocalStorage;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SetupManager implements Listener {
    private final ItemStack setupWand = new ItemBuilder(Material.IRON_AXE).displayname("§eLocation Wand").build();
    private final ItemStack arenaSetter = new ItemBuilder(Material.IRON_SWORD).displayname("§aSet arena corners").build();
    private final ItemStack playerCount = new ItemBuilder(Material.PLAYER_HEAD).displayname("§eSet Player Count §7(§4§l-§7/§2§l+§7)").build();
    private final ItemStack spawnerBlock = new ItemBuilder(Material.EMERALD_BLOCK).displayname("§aSet Gem Spawner").build();
    private final ItemStack playerSpawnRed = new ItemBuilder(Material.RED_BANNER).displayname("§cAdd player spawn").build();
    private final ItemStack playerSpawnBlue = new ItemBuilder(Material.BLUE_BANNER).displayname("§9Add player spawn").build();
    private final ItemStack saveSetup = new ItemBuilder(Material.LIME_DYE).displayname("§aSave Map").build();

    private GameMap map;

    private LocationWrapper loc1, loc2;

    private Player player;

    public void startSetup(Player player, String mapName) {
        map = new GameMap(null, mapName);

        player.getInventory().setItem(0, setupWand);
        player.getInventory().setItem(7, playerCount);
        player.getInventory().setItem(1, spawnerBlock);
        player.getInventory().setItem(2, playerSpawnRed);
        player.getInventory().setItem(3, playerSpawnBlue);
        player.getInventory().setItem(4, arenaSetter);
        player.getInventory().setItem(8, saveSetup);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = e.getItem();
        if (item == null) return;
        if (item.equals(setupWand)) {
            e.setCancelled(true);
            switch (e.getAction()) {
                case LEFT_CLICK_BLOCK -> {
                    loc1 = LocUtil.fromBukkit(e.getClickedBlock().getLocation());
                    Audience.audience(player).sendActionBar(Component.text("§aSet location 1"));
                    player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
                }
                case RIGHT_CLICK_BLOCK -> {
                    loc2 = LocUtil.fromBukkit(e.getClickedBlock().getLocation());
                    Audience.audience(player).sendActionBar(Component.text("§aSet location 2"));
                    player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
                }
                case LEFT_CLICK_AIR -> {
                    loc1 = LocUtil.fromBukkit(e.getPlayer().getLocation());
                    Audience.audience(player).sendActionBar(Component.text("§aSet location 1"));
                    player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
                }
                case RIGHT_CLICK_AIR -> {
                    loc2 = LocUtil.fromBukkit(e.getPlayer().getLocation());
                    Audience.audience(player).sendActionBar(Component.text("§aSet location 2"));
                    player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
                }
            }
        } else if (item.equals(playerCount)) {
            e.setCancelled(true);
            if (e.getAction().isLeftClick()) {
                if (map.getMaxPlayers() <= 1) return;
                map.setMaxPlayers(map.getMaxPlayers() - 1);
                Audience.audience(player).sendActionBar(Component.text("§aSet max players to §e" + map.getMaxPlayers()));
                player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
                item.setAmount(map.getMaxPlayers());
                player.getInventory().setItem(7, item);
            } else if (e.getAction().isRightClick()) {
                map.setMaxPlayers(map.getMaxPlayers() + 1);
                Audience.audience(player).sendActionBar(Component.text("§aSet max players to §e" + map.getMaxPlayers()));
                player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
                item.setAmount(map.getMaxPlayers());
                player.getInventory().setItem(7, item);
            }
        } else if (item.equals(arenaSetter)) {
            e.setCancelled(true);
            if (loc1 == null || loc2 == null) {
                Audience.audience(player).sendActionBar(Component.text("§cYou need to set both locations first!"));
                player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1, 2);
                return;
            }
            map.setArena(new LocationSection(loc1, loc2));
            Audience.audience(player).sendActionBar(Component.text("§aArena set!"));
            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
        } else if (item.equals(spawnerBlock)) {
            e.setCancelled(true);
            map.setSpawner(LocUtil.fromBukkit(player.getLocation()));
            Audience.audience(player).sendActionBar(Component.text("§aGem Spawner set!"));
            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
        } else if (item.equals(playerSpawnRed)) {
            map.addPlayerSpawn(player.getLocation(), TeamColor.RED);
            Audience.audience(player).sendActionBar(Component.text("§aPlayer spawn added!"));
            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
        } else if (item.equals(playerSpawnBlue)) {
            map.addPlayerSpawn(player.getLocation(), TeamColor.BLUE);
            Audience.audience(player).sendActionBar(Component.text("§aPlayer spawn added!"));
            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
        } else if (item.equals(saveSetup)) {
            player.sendMessage("§2Saving map...");
            if (map.getSpawner() == null || map.getArena() == null || map.getMaxPlayers() == 0 ||
                    map.getSpawnPoints().get(TeamColor.RED) == null || map.getSpawnPoints().get(TeamColor.BLUE) == null) {
                Audience.audience(player).sendActionBar(Component.text("§cYou need to set all required fields!"));
                player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1, 2);
                return;
            }
            LocalStorage.getInstance().getGameMaps().add(map);
            LocalStorage.getInstance().saveMaps();
            HandlerList.unregisterAll(this);

            player.sendMessage("§2Map data saved successfully!");
            player.sendMessage("§2Saving world to template...");


            World w = player.getWorld();
            w.save();

        }
    }
}
