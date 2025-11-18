package net.teamcastle.gemgrab.manager;

import net.kyori.adventure.util.TriState;
import net.teamcastle.gemgrab.GemRush;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorldManager {
    private Logger log = GemRush.getInstance().getLogger();


    public void copyWorld(File source, File target) throws IOException {
        if (!target.exists()) target.mkdirs();
        FileUtils.copyDirectory(source, target, file -> {
            String name = file.getName();
            return !name.equals("uid.dat") &&
                    !name.equals("session.lock") &&
                    !name.equals("playerdata") &&
                    !name.equals("stats") &&
                    !name.equals("advancements");
        });
    }


    public World loadWorld(String name) {
        WorldCreator wc = new WorldCreator(name);
        wc.generator("VoidGen");
        wc.generateStructures(false);
        wc.keepSpawnLoaded(TriState.FALSE);

        return wc.createWorld();
    }

    public CompletableFuture<World> createGameWorld(String templateName, String worldName) {
        UUID id = UUID.randomUUID();

        return CompletableFuture.supplyAsync(() -> {
            File template = new File(GemRush.getInstance().getDataFolder(), "Templates/" + templateName);
            String wName = worldName + "_" + id.toString().substring(0, 8);
            File target = new File(Bukkit.getWorldContainer(), wName);

            try {
                copyWorld(template, target);
                new File(target, "uid.dat").delete();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return wName;
        }).thenApply(name -> {
            try {
                return Bukkit.getScheduler().callSyncMethod(GemRush.getInstance(), () -> loadWorld(name)).get();
            } catch (InterruptedException | ExecutionException e) {
                GemRush.getInstance().getLogger().log(Level.WARNING, "Could not load world " + name, e);
            }
            return null;
        });
    }

    public boolean unloadAndDeleteWorld(String name) {
        World w = Bukkit.getWorld(name);
        if (w != null) {
            w.getEntities().forEach(Entity::remove);
            boolean success = Bukkit.unloadWorld(w, false);

            if (!success) {
                GemRush.getInstance().getLogger().warning("Could not unload world: " + name);
                return false;
            }
        }

        File worldFolder = new File(Bukkit.getWorldContainer(), name);
        try {
            FileUtils.deleteDirectory(worldFolder);
        } catch (IOException e) {
            GemRush.getInstance().getLog().warning("Could not delete world folder: " + name);
            return false;
        }

        return true;
    }

    public boolean deleteWorld(String name) {
        File worldFolder = new File(Bukkit.getWorldContainer(), name);
        try {
            FileUtils.deleteDirectory(worldFolder);
        } catch (IOException e) {
            GemRush.getInstance().getLog().warning("Could not delete world folder: " + name);
            return false;
        }

        return true;
    }

    public void saveAsTemplate(World world) {
        world.save();
        Bukkit.unloadWorld(world, true);
        File source = new File(Bukkit.getWorldContainer(), world.getName());
        File target = new File(GemRush.getInstance().getDataFolder(), "Templates/" + world.getName());
        try {
            copyWorld(source, target);
        } catch (IOException e) {
            log.warning("Could not save world as template: " + world.getName() + e.getMessage());
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                boolean success = deleteWorld(world.getName());
                if (success) log.info("World " + world.getName() + " saved as template successfully.");
                else log.warning("Could not delete world folder after saving as template: " + world.getName());
            }
        }.runTaskLater(GemRush.getInstance(), 20);
    }
}