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
        // Zähler für blaue Team-Gems in die Methode verlagert
        AtomicInteger countBlue = new AtomicInteger();

        // Überprüfen, ob das Team existiert
        var blueTeam = teamManager.getTeamByColor(TeamColor.BLUE);
        if (blueTeam == null) {
            Bukkit.getLogger().warning("Das blaue Team existiert nicht!");
            return 0;
        }

        // Nur fortfahren, wenn das Team nicht null ist
        blueTeam.getPlayers().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                for (ItemStack item : player.getInventory().getContents()) {
                    if (item != null) {
                        ItemMeta meta = item.getItemMeta();
                        if (meta != null && meta.hasDisplayName() && meta.getDisplayName().equals("§2§lGEM")) {
                            countBlue.addAndGet(item.getAmount());
                        }
                    }
                }
            }
        });
        return countBlue.get();  // Korrekt die Anzahl der Gems im blauen Team zurückgeben
    }

    public int calculateTeamGemsRed() {
        // Zähler für rote Team-Gems in die Methode verlagert
        AtomicInteger countRed = new AtomicInteger();

        // Überprüfen, ob das Team existiert
        var redTeam = teamManager.getTeamByColor(TeamColor.RED);
        if (redTeam == null) {
            Bukkit.getLogger().warning("Das rote Team existiert nicht!");
            return 0;
        }

        // Nur fortfahren, wenn das Team nicht null ist
        redTeam.getPlayers().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                for (ItemStack item : player.getInventory().getContents()) {
                    if (item != null) {
                        ItemMeta meta = item.getItemMeta();
                        if (meta != null && meta.hasDisplayName() && meta.getDisplayName().equals("§2§lGEM")) {
                            countRed.addAndGet(item.getAmount());
                        }
                    }
                }
            }
        });
        return countRed.get();
    }

    public void checkForCountdown() {
        int win = 10;
        if (TeamcastleGemgrab.getGamestate().equals(Gamestate.INGAME)) {
            if (calculateTeamGemsBlue() >= win) {
                if (!BossbarHandler.checIsCountdownRunningRed()) {
                    if (!BossbarHandler.checkCountdownRunningBlue()) {
                        BossbarHandler.startBossBarCountdownBlue(15);
                    }
                }
            } else {
                if (BossbarHandler.checkCountdownRunningBlue()) {
                    BossbarHandler.stopBossBarCountdownBlue();
                }
            }
            if (calculateTeamGemsRed() >= win) {
                if (!BossbarHandler.checkCountdownRunningBlue()) {
                    if (!BossbarHandler.checIsCountdownRunningRed()) {
                        BossbarHandler.startBossBarCountdownRed(15);
                    }
                }
            } else {
                if (BossbarHandler.checIsCountdownRunningRed()) {
                    BossbarHandler.stopBossBarCountdownRed();
                }
            }
        }
    }
}
