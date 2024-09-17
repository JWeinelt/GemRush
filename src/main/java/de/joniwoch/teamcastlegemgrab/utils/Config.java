package de.joniwoch.teamcastlegemgrab.utils;

import de.joniwoch.teamcastlegemgrab.TeamcastleGemgrab;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.Objects;
import java.util.concurrent.FutureTask;

public class Config {
    public static YamlConfiguration config;
    private static File configFile;

    public static void load(TeamcastleGemgrab teamcastleGemgrab) {
        configFile = new File(teamcastleGemgrab.getDataFolder(), "config.yml");
        if (!configFile.getParentFile().exists()) {
            configFile.getParentFile().mkdirs();
        }
        if (!configFile.exists()) {
            InputStream inputStream = TeamcastleGemgrab.getInstance().getResource("config.yml");
            try {
                byte[] buffer = new byte[inputStream.available()];
                inputStream.read(buffer);
                OutputStream outStream = new FileOutputStream(configFile);
                outStream.write(buffer);
                inputStream.close();
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            config = new YamlConfiguration();
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        new FutureTask<>(() -> {
            try {
                config.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 1).run();
    }

    public static void set(String path, Object value) throws IOException {
        config.set(path, value);
        save();
    }

    public static boolean contains(String path) {
        return config.contains(path);
    }

    @Deprecated
    public static Object get(String path) {
        if (!contains(path)) {
            return null;
        }
        return config.get(path);
    }

    public static String getString(String path, Player player) {
        if (!contains(path)) {
            return null;
        }
        return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString(path))
                .replaceAll("%Player%", player.getName()));
    }

    public static String getString(String path) {
        if (!contains(path)) {
            return null;
        }
        return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString(path)));
    }
}