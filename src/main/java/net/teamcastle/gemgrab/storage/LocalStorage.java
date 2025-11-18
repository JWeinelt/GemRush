package net.teamcastle.gemgrab.storage;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import net.teamcastle.gemgrab.GemRush;
import net.teamcastle.gemgrab.manager.map.GameMap;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class LocalStorage {
    private final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final File configFile;
    private final Logger logger;

    @Getter
    private List<GameMap> gameMaps = new ArrayList<>();

    @Getter
    private Configuration config;

    public LocalStorage() {
        configFile = new File(GemRush.getInstance().getDataFolder(), "config.json");
        config = new Configuration();
        logger = GemRush.getInstance().getLogger();
    }

    public GameMap getMap(String name) {
        for (GameMap map : gameMaps) {
            if (map.getName().equalsIgnoreCase(name)) {
                return map;
            }
        }
        return null;
    }

    public static LocalStorage getInstance() {
        return GemRush.getInstance().getLocalStorage();
    }

    public void loadConfig() {
        if (!configFile.exists()) {
            saveConfig();
        }
        try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
            String line;
            StringBuilder json = new StringBuilder();
            while ((line = br.readLine()) != null) {
                json.append(line);
            }
            config = GSON.fromJson(json.toString(), Configuration.class);
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }

    public void saveConfig() {
        try (FileWriter w = new FileWriter(configFile)) {
            w.write(GSON.toJson(config));
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }

    public void saveConfigNonReplace() {
        if (!configFile.exists()) {
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
            String line;
            StringBuilder json = new StringBuilder();
            while ((line = br.readLine()) != null) {
                json.append(line);
            }
            Configuration c = GSON.fromJson(json.toString(), Configuration.class);
            if (!c.equals(config)) {
                try (FileWriter w = new FileWriter(new File(GemRush.getInstance().getDataFolder(), "config.json.bak"))) {
                    w.write(GSON.toJson(config));
                }

                logger.warning("Detected changes of config.json on disk, saving current config to config.json.bak");
            }
        } catch (IOException e) {
            logger.severe("Failed to read/write config for non-replace save!");
            logger.severe(e.getMessage());
        }
    }

    public void saveMaps() {
        try (FileWriter w = new FileWriter(new File(GemRush.getInstance().getDataFolder(), "maps.json"))) {
            w.write(GSON.toJson(gameMaps));
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }
    public void loadMaps() {
        File mapsFile = new File(GemRush.getInstance().getDataFolder(), "maps.json");
        if (!mapsFile.exists()) {
            saveMaps();
        }
        try (BufferedReader br = new BufferedReader(new FileReader(mapsFile))) {
            String line;
            StringBuilder json = new StringBuilder();
            while ((line = br.readLine()) != null) {
                json.append(line);
            }
            Type type = new TypeToken<List<GameMap>>() {}.getType();
            gameMaps = GSON.fromJson(json.toString(), type);
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }
}