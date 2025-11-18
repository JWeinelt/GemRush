package net.teamcastle.gemgrab;

import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import net.teamcastle.gemgrab.commands.GemRushCommand;
import net.teamcastle.gemgrab.commands.GemRushCompleter;
import net.teamcastle.gemgrab.commands.StatsCommand;
import net.teamcastle.gemgrab.listener.JoinListener;
import net.teamcastle.gemgrab.manager.GameManager;
import net.teamcastle.gemgrab.storage.database.MySQLManager;
import net.teamcastle.gemgrab.manager.game.GameState;
import net.teamcastle.gemgrab.manager.game.StatManager;
import net.teamcastle.gemgrab.manager.items.gameitems.GameItemManager;
import net.teamcastle.gemgrab.manager.map.GameMap;
import net.teamcastle.gemgrab.manager.map.GameMapManager;
import net.teamcastle.gemgrab.storage.LocalStorage;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

@Getter
public final class GemRush extends JavaPlugin {

    @Getter
    public static GemRush instance;
    public static String mainPrefix = "§8» §2Gem§aRush §8•";
    private Logger log;

    @Getter
    @Setter
    private static GameState gamestate;

    @Getter
    @Setter
    private static GameMap gameMap;

    @Getter
    private MySQLManager mySQLManager;

    private GameMapManager gameMapManager;
    private GameItemManager gameItemManager;

    private LocalStorage localStorage;
    private StatManager statManager;
    private GameManager gameManager;

    @Override
    public void onLoad() {
        instance = this;
        log = this.getLogger();
    }

    @Override
    public void onEnable() {
        log.info("Enabling GemRush...");
        log.info("Loading configuration...");
        localStorage = new LocalStorage();
        localStorage.loadConfig();
        log.info("Initializing stats...");
        statManager = new StatManager();
        gamestate = GameState.LOBBY;

        log.info("Connecting to MySQL database...");
        mySQLManager = new MySQLManager(localStorage.getConfig());
        mySQLManager.connect();

        this.gameMapManager = new GameMapManager();
        this.gameItemManager = new GameItemManager();
        registerCommands();
        setWorldSettings();
    }

    public void registerCommands() {
        getCommand("stats").setExecutor(new StatsCommand());
        PluginCommand cmd = getCommand("gemrush");
        if (cmd == null) return;
        cmd.setExecutor(new GemRushCommand());
        cmd.setTabCompleter(new GemRushCompleter());
    }

    public void setWorldSettings() {
        for (World world : Bukkit.getWorlds()) {
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.getEntities().forEach(Entity::remove);
        }
    }

    @Override
    public void onDisable() {
        log.info("Disabling GemRush...");
        log.info("Saving stats before disabling...");
        if (mySQLManager != null) mySQLManager.uploadStats();
        else {
            log.warning("MySQLManager is null, stats were not saved in database!");
            log.warning("Attempting to save stats to disk for later use...");
            try (FileWriter w = new FileWriter(new File(getDataFolder(), "unsaved_stats.json"))) {
                w.write(new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(statManager.getPlayerStats()));
            } catch (IOException e) {
                log.severe("Failed to save unsaved stats to file!");
                log.severe(e.getMessage());
            }
        }
        if (mySQLManager != null) {
            log.info("Disconnecting from MySQL database...");
            mySQLManager.disconnect();
        }
        log.info("Disabled GemRush.");
        log.info("Goodbye!");
    }
}
