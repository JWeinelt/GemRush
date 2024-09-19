package de.joniwoch.teamcastlegemgrab;

import de.joniwoch.teamcastlegemgrab.commands.CreateMapCommand;
import de.joniwoch.teamcastlegemgrab.commands.SetGameSpawnCommand;
import de.joniwoch.teamcastlegemgrab.commands.SetLobbySpawnCommand;
import de.joniwoch.teamcastlegemgrab.commands.StartCommand;
import de.joniwoch.teamcastlegemgrab.listener.JoinListener;
import de.joniwoch.teamcastlegemgrab.listener.PlayerListener;
import de.joniwoch.teamcastlegemgrab.listener.QuitListener;
import de.joniwoch.teamcastlegemgrab.listener.WorldListener;
import de.joniwoch.teamcastlegemgrab.manager.game.GameSettings;
import de.joniwoch.teamcastlegemgrab.manager.game.gameplay.BossbarHandler;
import de.joniwoch.teamcastlegemgrab.manager.game.gameplay.PlayerDeathHandler;
import de.joniwoch.teamcastlegemgrab.manager.game.gems.GemManager;
import de.joniwoch.teamcastlegemgrab.manager.game.start.GameStartHandler;
import de.joniwoch.teamcastlegemgrab.manager.game.Gamestate;
import de.joniwoch.teamcastlegemgrab.manager.items.gameitems.GameItemManager;
import de.joniwoch.teamcastlegemgrab.manager.items.lobbyitems.LobbyItemManager;
import de.joniwoch.teamcastlegemgrab.manager.locations.LobbyLocationManager;
import de.joniwoch.teamcastlegemgrab.manager.locations.map.GameMap;
import de.joniwoch.teamcastlegemgrab.manager.locations.map.GameMapManager;
import de.joniwoch.teamcastlegemgrab.manager.player.GemgrabPlayer;
import de.joniwoch.teamcastlegemgrab.manager.teams.TeamManager;
import de.joniwoch.teamcastlegemgrab.scoreboard.Scoreboard;
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

    @Getter
    @Setter
    private static GameMap gameMap;

    private LobbyItemManager lobbyItemManager;
    private TeamManager teamManager;
    private LobbyLocationManager lobbyLocationManager;
    private Scoreboard scoreboard;
    private GameMapManager gameMapManager;
    private GameItemManager gameItemManager;
    private GameStartHandler gameStartHandler;
    private PlayerDeathHandler playerDeathHandler;
    private GemManager gemManager;


    @Override
    public void onEnable() {
        instance = this;
        gamestate = Gamestate.LOBBY;
        Config.load(this);

        this.lobbyItemManager = new LobbyItemManager();
        this.teamManager = new TeamManager();
        this.lobbyLocationManager = new LobbyLocationManager();
        this.gameMapManager = new GameMapManager(teamManager);
        this.gameMapManager.cacheGameMap();
        this.gameItemManager = new GameItemManager(teamManager);
        this.gameStartHandler = new GameStartHandler(gameMapManager, gameItemManager, teamManager);
        this.scoreboard = new Scoreboard(teamManager, gameMapManager);
        this.playerDeathHandler = new PlayerDeathHandler(gameMapManager, gameItemManager);
        this.gemManager = new GemManager(teamManager);


        registerListener();
        registerCommands();
        preparePlugin();
        setWorldSettings();
    }

    public void preparePlugin() {
        this.teamManager.registerTeams();
        this.lobbyLocationManager.cacheLobbyLocation();
        GameSettings.setTeamSize(3);
        GameSettings.setGemCooldown(2);
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
//                case STARTING -> {
//                    Bukkit.getOnlinePlayers().forEach(player -> {
//                        this.scoreboard.updateGame(player);
//                    });
//                }
                case INGAME -> {
                    Bukkit.getScheduler().runTaskTimer(TeamcastleGemgrab.getInstance(), () -> {
                        BossbarHandler.defaultBossBar.setTitle("§8| §a§l" + this.gemManager.calculateTeamGemsBlue() + " §7- §1Blau §7----------- §4Rot §7- §a§l" + this.gemManager.calculateTeamGemsRed() + " §8|");
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
        manager.registerEvents(new WorldListener(), this);
        manager.registerEvents(new QuitListener(teamManager), this);
    }

    public void registerCommands() {
        getCommand("setlobbyspawn").setExecutor(new SetLobbySpawnCommand(lobbyLocationManager));
        getCommand("createmap").setExecutor(new CreateMapCommand(gameMapManager));
        getCommand("setgamespawn").setExecutor(new SetGameSpawnCommand(gameMapManager));
        getCommand("start").setExecutor(new StartCommand(gameStartHandler));
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
