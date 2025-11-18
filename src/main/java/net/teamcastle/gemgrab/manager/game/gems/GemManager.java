package net.teamcastle.gemgrab.manager.game.gems;

import lombok.RequiredArgsConstructor;
import net.teamcastle.gemgrab.manager.game.Game;
import net.teamcastle.gemgrab.manager.teams.TeamColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class GemManager {
    private final Game game;

    public int calculateTeamGemsBlue() {
        return calculateTeamGemsByColor(TeamColor.BLUE);
    }

    public int calculateTeamGemsRed() {
        return calculateTeamGemsByColor(TeamColor.RED);
    }

    private int calculateTeamGemsByColor(TeamColor teamColor) {
        AtomicInteger gemCount = new AtomicInteger();

        var team = game.getTeam(teamColor);
        if (team == null) {
            Bukkit.getLogger().warning("Das Team " + teamColor + " existiert nicht!");
            return 0;
        }

        team.forEach(gPlayer -> {
            Player player = Bukkit.getPlayer(gPlayer.getUuid());
            if (player != null) {
                for (ItemStack item : player.getInventory().getContents()) {
                    if (item != null && isGem(item)) {
                        gemCount.addAndGet(item.getAmount());
                    }
                }
            }
        });
        return gemCount.get();
    }

    private boolean isGem(ItemStack item) {
        return item.getType().equals(Material.EMERALD);
    }
}
