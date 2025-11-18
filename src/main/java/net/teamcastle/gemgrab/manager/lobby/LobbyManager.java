package net.teamcastle.gemgrab.manager.lobby;

import net.teamcastle.gemgrab.GemRush;
import net.teamcastle.gemgrab.manager.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

public class LobbyManager {
    private final WorldManager worldManager;
    private final LinkedList<String> freeLobbies = new LinkedList<>();
    private final LinkedList<String> usedLobbies = new LinkedList<>();

    private static final String TEMPLATE_NAME = "Lobby";

    public LobbyManager(WorldManager worldManager) {
        this.worldManager = worldManager;
        prepareSpareLobby();
    }

    public static LobbyManager getInstance() {
        return GemRush.instance.getLobbyManager();
    }


    public CompletableFuture<World> requestLobby() {
        if (freeLobbies.isEmpty()) {
            return worldManager.createGameWorld(TEMPLATE_NAME, "Lobby")
                    .thenApply(world -> {
                        usedLobbies.add(world.getName());
                        prepareSpareLobby();
                        return world;
                    });
        }

        String lobbyName = freeLobbies.removeFirst();
        usedLobbies.add(lobbyName);

        return CompletableFuture.completedFuture(Bukkit.getWorld(lobbyName));
    }

    public void releaseLobby(String worldName) {
        usedLobbies.remove(worldName);
        freeLobbies.add(worldName);
    }


    private void prepareSpareLobby() {
        if (!freeLobbies.isEmpty()) return;

        worldManager.createGameWorld(TEMPLATE_NAME, "Lobby_spare")
                .thenAccept(world -> freeLobbies.add(world.getName()));
    }


    public void cleanupAll() {
        for (String worldName : freeLobbies) {
            worldManager.unloadAndDeleteWorld(worldName);
        }

        for (String worldName : usedLobbies) {
            worldManager.unloadAndDeleteWorld(worldName);
        }

        freeLobbies.clear();
        usedLobbies.clear();
    }
}