package net.teamcastle.gemgrab.manager.game.gems;

import net.teamcastle.gemgrab.TeamcastleGemgrab;
import net.teamcastle.gemgrab.manager.game.GameSettings;
import net.teamcastle.gemgrab.manager.game.Gamestate;
import net.teamcastle.gemgrab.manager.items.ItemAPI;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

@UtilityClass
public class GemSpawnerManager {

    public ArmorStand textStand2 = null;
    private int gemCountdown = GameSettings.getGemCooldown();


    public void spawnGems(Location location) {
        Location spawnLocation = location.clone().add(0.5, 1, 0.5);
        Bukkit.getScheduler().runTaskTimer(TeamcastleGemgrab.getInstance(), ()-> {
            if (!TeamcastleGemgrab.getGamestate().equals(Gamestate.ENDED)) {
                if (gemCountdown > 1) {
                    gemCountdown--;
                } else {
                    gemCountdown = GameSettings.getGemCooldown();
                    Bukkit.getWorld("world").dropItem(spawnLocation, new ItemAPI("§2§lGEM", Material.EMERALD, 1).build());
                }
                textStand2.setCustomName("§7Erscheint in §c" + gemCountdown + " §7Sekunden");
            }
        }, 0, 20);
    }

    public void createGemSpawner(Location location) {
        Location spawnLocation = location.clone().add(0.5, 1.5, 0.5);
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(spawnLocation, EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);
        ItemStack diamondBlock = new ItemStack(Material.EMERALD_BLOCK);
        armorStand.getEquipment().setHelmet(diamondBlock);

        ArmorStand textStand = (ArmorStand) location.getWorld().spawnEntity(spawnLocation.clone().add(0, 2.5, 0), EntityType.ARMOR_STAND);
        textStand.setCustomName("§a§lGem-Spawner");
        textStand.setCustomNameVisible(true);
        textStand.setVisible(false);
        textStand.setInvulnerable(true);
        textStand.setGravity(false);
        textStand.setMarker(true);

        textStand2 = (ArmorStand) location.getWorld().spawnEntity(spawnLocation.clone().add(0, 2.2, 0), EntityType.ARMOR_STAND);
        textStand2.setCustomName("§7Erscheint in §c " + GameSettings.getGemCooldown() + " §7Sekunden");
        textStand2.setCustomNameVisible(true);
        textStand2.setVisible(false);
        textStand2.setInvulnerable(true);
        textStand2.setGravity(false);
        textStand2.setMarker(true);

        new BukkitRunnable() {
            double angle = 0;
            double ticks = 0;

            @Override
            public void run() {
                angle += 5;
                double yOffset = Math.sin(ticks / 10.0) * 0.15;
                ticks += 1;

                armorStand.setHeadPose(new EulerAngle(0, Math.toRadians(angle), 0));
                Location newLocation = spawnLocation.clone().add(0, yOffset, 0);
                armorStand.teleport(newLocation);

                if (angle >= 360) {
                    angle = 0;
                }
            }
        }.runTaskTimer(TeamcastleGemgrab.getInstance(), 0L, 1L);
    }
}
