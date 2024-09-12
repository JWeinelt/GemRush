package de.joniwoch.teamcastlegemgrab;

import de.joniwoch.teamcastlegemgrab.listener.JoinListener;
import de.joniwoch.teamcastlegemgrab.listener.PlayerListener;
import de.joniwoch.teamcastlegemgrab.listener.WorldListener;
import de.joniwoch.teamcastlegemgrab.manager.game.Gamestate;
import de.joniwoch.teamcastlegemgrab.manager.items.lobbyitems.LobbyItemManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class TeamcastleGemgrab extends JavaPlugin {

    @Getter
    public static TeamcastleGemgrab instance;

    @Getter
    @Setter
    private static Gamestate gamestate;

    private LobbyItemManager lobbyItemManager;


    @Override
    public void onEnable() {
        instance = this;
        gamestate = Gamestate.LOBBY;

        this.lobbyItemManager = new LobbyItemManager();

        registerListener();
        setWorldSettings();
    }

    public void registerListener() {
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new JoinListener(lobbyItemManager), this);
        manager.registerEvents(new PlayerListener(), this);
        manager.registerEvents(new WorldListener(), this);
    }

    public void setWorldSettings() {
        World world = Bukkit.getWorld("world");
        world.setMonsterSpawnLimit(0);
        world.setAnimalSpawnLimit(0);
        world.getEntities().forEach(Entity::remove);
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setTime(6000);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
