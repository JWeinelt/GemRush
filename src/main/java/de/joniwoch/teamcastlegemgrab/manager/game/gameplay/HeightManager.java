package de.joniwoch.teamcastlegemgrab.manager.game.gameplay;

import de.joniwoch.teamcastlegemgrab.manager.player.GemgrabPlayer;
import de.joniwoch.teamcastlegemgrab.manager.player.GemgrabPlayerManager;
import de.joniwoch.teamcastlegemgrab.utils.Config;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;

@RequiredArgsConstructor
public class HeightManager {

    private final PlayerDeathHandler deathHandler;

    public void checkHeight() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            int y = (int) player.getLocation().getY();
            int minHeight = Config.config.getInt("Map.Height");
            if (y < minHeight) {
                if (GemgrabPlayerManager.getGemgrabPlayerByUUID(player.getUniqueId()) != null) {
                    GemgrabPlayer gemgrabPlayer = GemgrabPlayerManager.getGemgrabPlayerByUUID(player.getUniqueId());
                    if (!gemgrabPlayer.isDead()) {
                        deathHandler.setPlayerDead(player);
                    }
                }
            }
        });
    }
}
