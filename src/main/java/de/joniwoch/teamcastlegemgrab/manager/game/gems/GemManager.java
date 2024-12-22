package de.joniwoch.teamcastlegemgrab.manager.game.gems;

import de.joniwoch.teamcastlegemgrab.TeamcastleGemgrab;
import de.joniwoch.teamcastlegemgrab.manager.game.Gamestate;
import de.joniwoch.teamcastlegemgrab.manager.game.gameplay.BossbarHandler;
import de.joniwoch.teamcastlegemgrab.manager.teams.TeamColor;
import de.joniwoch.teamcastlegemgrab.manager.teams.TeamManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class GemManager {

    private final TeamManager teamManager;

    public int calculateTeamGemsBlue() {
        return calculateTeamGemsByColor(TeamColor.BLUE);
    }

    public int calculateTeamGemsRed() {
        return calculateTeamGemsByColor(TeamColor.RED);
    }

    private int calculateTeamGemsByColor(TeamColor teamColor) {
        AtomicInteger gemCount = new AtomicInteger();

        var team = teamManager.getTeamByColor(teamColor);
        if (team == null) {
            Bukkit.getLogger().warning("Das Team " + teamColor + " existiert nicht!");
            return 0;
        }

        team.getPlayers().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
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
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.hasDisplayName() && "§2§lGEM".equals(meta.getDisplayName());
    }

    public void checkForCountdown() {
        int win = 10;
        if (TeamcastleGemgrab.getGamestate().equals(Gamestate.INGAME)) {
            if (calculateTeamGemsBlue() >= win) {
                if (!BossbarHandler.isCountdownRunning(BossbarHandler.TeamColor.RED)) {
                    if (!BossbarHandler.isCountdownRunning(BossbarHandler.TeamColor.BLUE)) {
                        BossbarHandler.startBossBarCountdown(BossbarHandler.TeamColor.BLUE, 15);
                    }
                }
            } else {
                if (BossbarHandler.isCountdownRunning(BossbarHandler.TeamColor.BLUE)) {
                    BossbarHandler.stopBossBarCountdown(BossbarHandler.TeamColor.BLUE);
                }
            }

            if (calculateTeamGemsRed() >= win) {
                if (!BossbarHandler.isCountdownRunning(BossbarHandler.TeamColor.BLUE)) {
                    if (!BossbarHandler.isCountdownRunning(BossbarHandler.TeamColor.RED)) {
                        BossbarHandler.startBossBarCountdown(BossbarHandler.TeamColor.RED, 15);
                    }
                }
            } else {
                if (BossbarHandler.isCountdownRunning(BossbarHandler.TeamColor.RED)) {
                    BossbarHandler.stopBossBarCountdown(BossbarHandler.TeamColor.RED);
                }
            }
        }
    }
}
