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

    public GPlayer getGemGrabPlayerByUUID(UUID uuid) {
        return gPlayers.stream()
                .filter(gemGrabPlayer -> gemGrabPlayer.getUuid().equals(uuid))
                .findFirst()
                .orElse(null);
    }

    public boolean isValid(GPlayer gPlayer) {
        Player player = getPlayerByGemgrabPlayer(gPlayer);
        return player != null && player.isValid();
    }

    public void addGemGrabPlayer(Player player) {
        if (!isGemGrabPlayer(player)) {
            gPlayers.add(new GPlayer(player.getName(), player.getUniqueId(), null, true, false));
        }
    }

    public boolean isGemGrabPlayer(Player player) {
        return gPlayers.stream()
                .anyMatch(gemGrabPlayer -> gemGrabPlayer.getUuid().equals(player.getUniqueId()));
    }
}
