package net.teamcastle.gemgrab.manager;

import net.teamcastle.gemgrab.GemRush;
import net.teamcastle.gemgrab.manager.game.Game;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private final List<Game> games = new ArrayList<>();

    public static GameManager getInstance() {
        return GemRush.getInstance().getGameManager();
    }

    public void joinGame(Player player) {

    }

    public void registerGame(Game game) {

    }

    public Game createOrFindGame() {
        return null;
    }

    public Game getPlayerGame(Player player) {
        return null;
    }
}
