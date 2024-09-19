package de.joniwoch.teamcastlegemgrab.manager.player;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;


@UtilityClass
public class GemgrabPlayerManager {

    @Getter
    private final List<GemgrabPlayer> gemgrabPlayers = new CopyOnWriteArrayList<>();

    public Player getPlayerByGemgrabPlayer(GemgrabPlayer gemgrabPlayer) {
        return Bukkit.getPlayer(gemgrabPlayer.getUuid());
    }

    public void hidePlayer(GemgrabPlayer gemgrabPlayer) {
        if (gemgrabPlayer != null && isValid(gemgrabPlayer)) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (!gemgrabPlayer.isVisibility()) {
                    player.hidePlayer(getPlayerByGemgrabPlayer(gemgrabPlayer));
                } else {
                    player.showPlayer(getPlayerByGemgrabPlayer(gemgrabPlayer));
                }
            });
        }
    }

    public GemgrabPlayer getGemgrabPlayerByUUID(UUID uuid) {
        return gemgrabPlayers.stream()
                .filter(gemgrabPlayer -> gemgrabPlayer.getUuid().equals(uuid))
                .findFirst()
                .orElse(null);
    }

    public boolean isValid(GemgrabPlayer gemgrabPlayer) {
        Player player = getPlayerByGemgrabPlayer(gemgrabPlayer);
        return player != null && player.isValid();
    }

    public void addGemgrabPlayer(Player player) {
        if (!isGemgrabPlayer(player)) {
            gemgrabPlayers.add(new GemgrabPlayer(player.getName(), player.getUniqueId(), null, true, false));
        }
    }

    public boolean isGemgrabPlayer(Player player) {
        return gemgrabPlayers.stream()
                .anyMatch(gemgrabPlayer -> gemgrabPlayer.getUuid().equals(player.getUniqueId()));
    }
}
