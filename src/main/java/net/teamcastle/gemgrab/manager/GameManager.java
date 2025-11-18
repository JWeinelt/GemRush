package net.teamcastle.gemgrab.manager;

import net.teamcastle.gemgrab.GemRush;
import net.teamcastle.gemgrab.manager.game.Game;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private final List<Game> games = new ArrayList<>();
    private final GamePoolManager poolManager;

    public GameManager(GamePoolManager poolManager) {
        this.poolManager = poolManager;
    }

    public static GameManager getInstance() {
        return GemRush.getInstance().getGameManager();
    }

    public void joinGame(Player player) {
        Game game = poolManager.getPreparedGame();
        game.joinGame(player);

        poolManager.onPlayerJoin(game);
    }

    public Game getPlayerGame(Player player) {
        for (Game g : games) {
            if (g.hasPlayer(player.getUniqueId())) return g;
        }
        if (poolManager.getPreparedGame().hasPlayer(player.getUniqueId())) return poolManager.getPreparedGame();
        return null;
    }
}
