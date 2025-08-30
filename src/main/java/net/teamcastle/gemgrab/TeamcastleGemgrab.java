package net.teamcastle.gemgrab;

import net.teamcastle.gemgrab.commands.*;
import net.teamcastle.gemgrab.listener.JoinListener;
import net.teamcastle.gemgrab.listener.PlayerListener;
import net.teamcastle.gemgrab.listener.QuitListener;
import net.teamcastle.gemgrab.listener.WorldListener;
import net.teamcastle.gemgrab.manager.database.MySQLManager;
import net.teamcastle.gemgrab.manager.game.GameSettings;
import net.teamcastle.gemgrab.manager.game.gameplay.HeightManager;
import net.teamcastle.gemgrab.manager.game.gameplay.PlayerDeathHandler;
import net.teamcastle.gemgrab.manager.game.gameplay.WinManager;
import net.teamcastle.gemgrab.manager.game.gems.GemManager;
import net.teamcastle.gemgrab.manager.game.start.GameStartHandler;
import net.teamcastle.gemgrab.manager.game.Gamestate;
import net.teamcastle.gemgrab.manager.items.gameitems.GameItemManager;
import net.teamcastle.gemgrab.manager.items.lobbyitems.LobbyItemManager;
import net.teamcastle.gemgrab.manager.locations.LobbyLocationManager;
import net.teamcastle.gemgrab.manager.locations.map.GameMap;
import net.teamcastle.gemgrab.manager.locations.map.GameMapManager;
import net.teamcastle.gemgrab.manager.teams.TeamManager;
import net.teamcastle.gemgrab.scoreboard.Scoreboard;
import net.teamcastle.gemgrab.utils.Config;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
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

    @Getter
    @Setter
    private static GameMap gameMap;

    @Getter
    private MySQLManager mySQLManager;

    private LobbyItemManager lobbyItemManager;
    private TeamManager teamManager;
    private LobbyLocationManager lobbyLocationManager;
    private Scoreboard scoreboard;
    private GameMapManager gameMapManager;
    private GameItemManager gameItemManager;
    private GameStartHandler gameStartHandler;
    private PlayerDeathHandler playerDeathHandler;
    private GemManager gemManager;
    private WinManager winManager;
    private WorldListener worldListener;


    @Override
    public void onEnable() {
        getLogger().info("GemGrab Plugin successful enabled.");
        instance = this;
        gamestate = Gamestate.LOBBY;
        Config.load(this);

        mySQLManager = new MySQLManager("localhost", 3306, "gemgrab", "root", "pi19NbPF3ynrncJq");
        try {
            mySQLManager.connect();
        } catch (Exception e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }

        this.lobbyItemManager = new LobbyItemManager();
        this.teamManager = new TeamManager();
        this.lobbyLocationManager = new LobbyLocationManager();
        this.winManager = new WinManager(lobbyLocationManager, teamManager);
        this.gameMapManager = new GameMapManager(teamManager);
        this.gameMapManager.cacheGameMap();
        this.gameItemManager = new GameItemManager(teamManager);
        this.gameStartHandler = new GameStartHandler(gameMapManager, gameItemManager, teamManager);
        this.scoreboard = new Scoreboard(teamManager, gameMapManager);
        this.playerDeathHandler = new PlayerDeathHandler(gameMapManager, gameItemManager);
        this.gemManager = new GemManager(teamManager);
        this.worldListener = new WorldListener(playerDeathHandler);

        registerListener();
        registerCommands();
        preparePlugin();
        setWorldSettings();
    }

    public void preparePlugin() {
        this.teamManager.registerTeams();
        this.lobbyLocationManager.cacheLobbyLocation();
        GameSettings.setTeamSize(3);
        GameSettings.setGemCooldown(10);
        GameSettings.setStartCountdown(10);
        GameSettings.setRespawnTimer(5);
        updateScoreboard();
    }

    public void updateScoreboard() {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            switch (gamestate) {
                case LOBBY -> {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        this.scoreboard.update(player);
                    });
                }
                case INGAME -> {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        this.scoreboard.update(player);
                    });
                    Bukkit.getScheduler().runTaskTimer(TeamcastleGemgrab.getInstance(), () -> {
                        this.gemManager.checkForCountdown();
                    }, 0, 2);
                }
            }
        }, 0, 20);
    }

    public void registerListener() {
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new JoinListener(lobbyItemManager, lobbyLocationManager, scoreboard), this);
        manager.registerEvents(new PlayerListener(teamManager, playerDeathHandler), this);
        manager.registerEvents(new WorldListener(playerDeathHandler), this);
        manager.registerEvents(new QuitListener(teamManager), this);
    }

    public void registerCommands() {
        getCommand("setlobbyspawn").setExecutor(new SetLobbySpawnCommand(lobbyLocationManager));
        getCommand("createmap").setExecutor(new CreateMapCommand(gameMapManager));
        getCommand("setgamespawn").setExecutor(new SetGameSpawnCommand(gameMapManager));
        getCommand("start").setExecutor(new StartCommand(gameStartHandler));
        getCommand("stats").setExecutor(new StatsCommand(this));
    }

    public void setWorldSettings() {
        World world = Bukkit.getWorld("world");
        world.setMonsterSpawnLimit(0);
        world.setAnimalSpawnLimit(0);
        world.getEntities().forEach(Entity::remove);
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setTime(6000);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
    }

    @Override
    public void onDisable() {
        try {
            mySQLManager.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        getLogger().info("GemGrab Plugin successful disabled.");
    }
}
