package de.joniwoch.teamcastlegemgrab;

import de.joniwoch.teamcastlegemgrab.listener.JoinListener;
import de.joniwoch.teamcastlegemgrab.listener.PlayerListener;
import de.joniwoch.teamcastlegemgrab.listener.WorldListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class TeamcastleGemgrab extends JavaPlugin {

    @Getter
    public static TeamcastleGemgrab instance;


    @Override
    public void onEnable() {
        instance = this;
        registerListener();
    }

    public void registerListener() {
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new JoinListener(), this);
        manager.registerEvents(new PlayerListener(), this);
        manager.registerEvents(new WorldListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
