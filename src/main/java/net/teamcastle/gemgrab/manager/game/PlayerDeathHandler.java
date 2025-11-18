package net.teamcastle.gemgrab.manager.game;

import de.codeblocksmc.codelib.locations.LocUtil;
import de.codeblocksmc.codelib.wrapping.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.teamcastle.gemgrab.GemRush;
import net.teamcastle.gemgrab.manager.items.gameitems.GameItemManager;
import net.teamcastle.gemgrab.manager.player.GPlayer;
import net.teamcastle.gemgrab.manager.player.PlayerManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import static net.teamcastle.gemgrab.GemRush.mainPrefix;

@RequiredArgsConstructor
public class PlayerDeathHandler {
    private final Game game;

    public void setPlayerDead(Player dead) {
        GPlayer died = PlayerManager.getGemGrabPlayerByUUID(dead.getUniqueId());
        if (died.getLastDamager() == null) {
            game.sendMessageToPlayers(mainPrefix + "§c" + dead.getName() + " §7died!");
            applyDeathEffects(dead);
        } else {
            GPlayer killer = died.getLastDamager();
            StatManager.getInstance().addKill(killer.getUuid());
            game.sendMessageToPlayers(mainPrefix + "§c%s§7 has been killed by §c%s§7!".formatted(dead.getName(), killer.getName()));
            applyDeathEffects(dead);
        }

    }

    public void applyDeathEffects(Player dead) {
        dead.playSound(dead, Sound.ENTITY_PLAYER_DEATH, 1, 1);
        GPlayer player = PlayerManager.getGemGrabPlayerByUUID(dead.getUniqueId());
        player.setLastDamager(null);
        dead.setAllowFlight(true);
        dropPlayerGems(dead);
        dead.getInventory().clear();
        dead.setFoodLevel(20);
        StatManager.getInstance().addDeath(dead.getUniqueId());
        startRespawnCountdown(dead);
        dead.setFoodLevel(20);

        player.setDead(true);
        player.setVisible(false);
        PlayerManager.hidePlayer(player);
        Bukkit.getScheduler().runTaskLater(GemRush.getInstance(), () -> {
            dead.setHealth(20);
            dead.teleport(LocUtil.fromWrapper(game.getMap().getSpawner()).add(0.5, 7.0, 0.5));
            dead.setFlying(true);
        }, 1L);
    }

    public void dropPlayerGems(Player player) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.hasDisplayName()) {
                    if (meta.getDisplayName().equals("§2§lGEM")) {
                        count += item.getAmount();
                    }
                }
            }
        }
        if (count != 0) {
            game.getWorld().dropItemNaturally(player.getLocation(),
                    new ItemBuilder(Material.EMERALD).displayname("§2§lGEM").build());
        }
    }

    public void startRespawnCountdown(Player player) {
        new BukkitRunnable() {
            private int countDown = 5;

            @Override
            public void run() {
                player.showTitle(Title.title(Component.text("§8You died!")
                        , Component.text("§7Respawning in §a" + countDown + "s")));
                countDown--;
                if (countDown < 0) {
                    game.spawnPlayer(player);
                    GameItemManager.getInstance().setGameItems(player, game);
                    player.setAllowFlight(false);
                    player.setHealth(20);
                    player.setFoodLevel(20);
                    GPlayer gPlayer = PlayerManager.getGemGrabPlayerByUUID(player.getUniqueId());
                    gPlayer.setDead(false);
                    gPlayer.setVisible(true);
                    PlayerManager.hidePlayer(gPlayer);
                    cancel();
                }
            }
        }.runTaskTimer(GemRush.getInstance(), 0, 20);
    }

}
