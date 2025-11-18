package net.teamcastle.gemgrab.manager;

import net.teamcastle.gemgrab.GemRush;
import net.teamcastle.gemgrab.manager.game.Game;
import net.teamcastle.gemgrab.storage.LocalStorage;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class GamePoolManager {

    private final WorldManager worldManager;
    private final List<String> mapTemplates = new ArrayList<>();
    private final List<Game> preparedGames = new ArrayList<>();
    private final List<Game> runningGames = new ArrayList<>();

    private static final double PREPARE_THRESHOLD = 0.70;

    public GamePoolManager(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    public static GamePoolManager getInstance() {
        return GemRush.instance.getGamePool();
    }

    public void loadAvailableMaps() {
        File templateFolder = new File(GemRush.getInstance().getDataFolder(), "Templates");

        if (!templateFolder.exists()) {
            templateFolder.mkdirs();
        }

        for (File f : Objects.requireNonNull(templateFolder.listFiles())) {
            if (f.isDirectory() && !f.getName().equalsIgnoreCase("Lobby")) {
                mapTemplates.add(f.getName());
            }
        }

        GemRush.getInstance().getLogger().info("Loaded Map Templates: " + mapTemplates);
    }


    private String randomMap() {
        Random r = new Random();
        return mapTemplates.get(r.nextInt(mapTemplates.size()));
    }


    public CompletableFuture<Game> prepareNewGame() {
        String map = randomMap();
        String instanceName = "Game_" + map;

        return worldManager.createGameWorld(map, instanceName)
                .thenApply(world -> {
                    Game game = new Game(world, LocalStorage.getInstance().getMap(map));
                    preparedGames.add(game);

                    GemRush.getInstance().getLogger().info("Prepared new game: " + game);

                    return game;
                });
    }


    public Game getPreparedGame() {
        if (preparedGames.isEmpty()) return null;
        return preparedGames.getFirst();
    }


    public void onPlayerJoin(Game game) {
        double fill = game.getFillPercentage();

        if (fill >= PREPARE_THRESHOLD && preparedGames.size() <= 1) {
            prepareNewGame();
        }
    }

    public void startGame(Game game) {
        preparedGames.remove(game);
        runningGames.add(game);
    }

    public void endGame(Game game) {
        runningGames.remove(game);
        preparedGames.remove(game);

        World w = game.getWorld();
        worldManager.unloadAndDeleteWorld(w.getName());
    }

    public void cleanupAll() {
        for (Game g : preparedGames) {
            worldManager.unloadAndDeleteWorld(g.getWorld().getName());
        }
        for (Game g : runningGames) {
            worldManager.unloadAndDeleteWorld(g.getWorld().getName());
        }

        preparedGames.clear();
        runningGames.clear();
    }
}
