package net.teamcastle.gemgrab.manager.locations;

import net.teamcastle.gemgrab.utils.Config;
import net.teamcastle.gemgrab.utils.Messages;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.IOException;


public class LobbyLocationManager {

    @Getter
    @Setter
    private Location lobbySpawn = null;

    public void cacheLobbyLocation() {
        double x = Config.config.getDouble("Locations.Lobbyspawn.X");
        double y = Config.config.getDouble("Locations.Lobbyspawn.Y");
        double z = Config.config.getDouble("Locations.Lobbyspawn.Z");
        double yaw = Config.config.getDouble("Locations.Lobbyspawn.Yaw");
        double pitch = Config.config.getDouble("Locations.Lobbyspawn.Pitch");
        setLobbySpawn(new Location(Bukkit.getWorld("world"), x, y, z, (float) yaw, (float) pitch));
    }

    public void teleportLobbySpawn(Player player) {
        player.teleport(getLobbySpawn());
    }

    public void setLobbyLocation(Player player) {
        try {
            Config.set("Locations.Lobbyspawn.X", player.getLocation().getX());
            Config.set("Locations.Lobbyspawn.Y", player.getLocation().getY());
            Config.set("Locations.Lobbyspawn.Z", player.getLocation().getZ());
            Config.set("Locations.Lobbyspawn.Yaw", player.getLocation().getYaw());
            Config.set("Locations.Lobbyspawn.Pitch", player.getLocation().getPitch());
            Config.save();
            cacheLobbyLocation();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        player.sendMessage(Messages.mainPrefix + "Du hast §aerfolgreich §7den §6Lobbyspawn §7gesetzt!");
    }


}
