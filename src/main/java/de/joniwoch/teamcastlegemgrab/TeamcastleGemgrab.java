package de.joniwoch.teamcastlegemgrab;

import de.joniwoch.teamcastlegemgrab.commands.SetLobbySpawnCommand;
import de.joniwoch.teamcastlegemgrab.listener.JoinListener;
import de.joniwoch.teamcastlegemgrab.listener.PlayerListener;
import de.joniwoch.teamcastlegemgrab.listener.WorldListener;
import de.joniwoch.teamcastlegemgrab.manager.game.GameSettings;
import de.joniwoch.teamcastlegemgrab.manager.game.Gamestate;
import de.joniwoch.teamcastlegemgrab.manager.items.lobbyitems.LobbyItemManager;
import de.joniwoch.teamcastlegemgrab.manager.locations.LobbyLocationManager;
import de.joniwoch.teamcastlegemgrab.manager.teams.TeamManager;
import de.joniwoch.teamcastlegemgrab.utils.Config;
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
    private TeamManager teamManager;
    private LobbyLocationManager lobbyLocationManager;


    @Override
    public void onEnable() {
        instance = this;
        gamestate = Gamestate.LOBBY;
        Config.load(this);

        this.lobbyItemManager = new LobbyItemManager();
        this.teamManager = new TeamManager();
        this.lobbyLocationManager = new LobbyLocationManager();

        registerListener();
        registerCommands();
        preparePlugin();
        setWorldSettings();
    }

    public void preparePlugin() {
        this.teamManager.registerTeams();
        this.lobbyLocationManager.cacheLobbyLocation();
        GameSettings.setTeamSize(3);
    }

    public void registerListener() {
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new JoinListener(lobbyItemManager, lobbyLocationManager), this);
        manager.registerEvents(new PlayerListener(teamManager), this);
        manager.registerEvents(new WorldListener(), this);
    }

    public void registerCommands() {
        getCommand("setlobbyspawn").setExecutor(new SetLobbySpawnCommand(lobbyLocationManager));
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
