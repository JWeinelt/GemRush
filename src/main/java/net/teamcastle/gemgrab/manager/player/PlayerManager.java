package net.teamcastle.gemgrab.manager.player;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.teamcastle.gemgrab.GemRush;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;


@UtilityClass
public class PlayerManager {

    @Getter
    private final List<GPlayer> gPlayers = new CopyOnWriteArrayList<>();

    public Player getPlayerByGemgrabPlayer(GPlayer gPlayer) {
        return Bukkit.getPlayer(gPlayer.getUuid());
    }

    public void hidePlayer(GPlayer gPlayer) {
        if (gPlayer != null && isValid(gPlayer)) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (!gPlayer.isVisible()) {
                    player.hidePlayer(GemRush.getInstance(), getPlayerByGemgrabPlayer(gPlayer));
                } else {
                    player.showPlayer(GemRush.getInstance(), getPlayerByGemgrabPlayer(gPlayer));
                }
            });
        }
    }

    public GPlayer getGemgrabPlayerByUUID(UUID uuid) {
        return gPlayers.stream()
                .filter(gemgrabPlayer -> gemgrabPlayer.getUuid().equals(uuid))
                .findFirst()
                .orElse(null);
    }

    public boolean isValid(GPlayer gPlayer) {
        Player player = getPlayerByGemgrabPlayer(gPlayer);
        return player != null && player.isValid();
    }

    public void addGemgrabPlayer(Player player) {
        if (!isGemgrabPlayer(player)) {
            gPlayers.add(new GPlayer(player.getName(), player.getUniqueId(), null, true, false));
        }
    }

    public boolean isGemgrabPlayer(Player player) {
        return gPlayers.stream()
                .anyMatch(gemgrabPlayer -> gemgrabPlayer.getUuid().equals(player.getUniqueId()));
    }
}
