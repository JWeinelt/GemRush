package net.teamcastle.gemgrab.manager.game;

import fr.mrmicky.fastboard.FastBoard;
import net.teamcastle.gemgrab.GemRush;
import net.teamcastle.gemgrab.manager.GameManager;
import net.teamcastle.gemgrab.manager.teams.TeamColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class FastBoardManager {
    private final List<FastBoard> fastBoards = new ArrayList<>();

    private BukkitTask task;

    public static FastBoardManager getInstance() {
        return GemRush.getInstance().getFastBoardManager();
    }

    public void startTask() {
        task = new BukkitRunnable() {

            @Override
            public void run() {
                for (FastBoard b : fastBoards) {
                    b.updateTitle("§8» §2Gem§aRush §8•");
                    Player p = b.getPlayer();
                    Game game = GameManager.getInstance().getPlayerGame(p);
                    if (game.getState().equals(GameState.LOBBY)) {
                        b.updateLines(
                                "",
                                "§7Players: §e" + game.getPlayerCount() + "§8/§e" + game.getMap().getMaxPlayers(),
                                "§7Map: §e" + game.getMap().getName(),
                                "",
                                "§eWaiting for players...",
                                "",
                                "§8play.codeblocksmc.com"
                        );
                    } else if (game.getState().equals(GameState.STARTING)) {
                        int countDown = game.getStarterCountdown().get();
                        b.updateLines(
                                "",
                                "§7Players: §e" + game.getPlayerCount() + "§8/§e" + game.getMap().getMaxPlayers(),
                                "§7Map: §e" + game.getMap().getName(),
                                "",
                                "§eStarting in §a" + countDown + (countDown == 1 ? " second" : " seconds"),
                                "",
                                "§8play.codeblocksmc.com"
                        );
                    } else if (game.getState().equals(GameState.RUNNING)) {
                        TeamColor c = game.getPlayerTeam(p.getUniqueId());
                        b.updateLines(
                                "",
                                "§7Team: " + c.colorCode + c.displayName,
                                "",
                                "§8play.codeblocksmc.com"
                        );
                    }
                }
            }
        }.runTaskTimer(GemRush.getInstance(), 0, 2);
    }

    public void createScoreboard(Player player) {
        FastBoard b = new FastBoard(player);
        fastBoards.add(b);
    }

    public void removeScoreboard(Player player) {
        FastBoard boardToRemove = null;
        for (FastBoard b : fastBoards) {
            if (b.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                b.delete();
                boardToRemove = b;
                break;
            }
        }
        if (boardToRemove != null) {
            fastBoards.remove(boardToRemove);
        }
    }

    public void stop() {
        fastBoards.clear();
        task.cancel();
    }
}
