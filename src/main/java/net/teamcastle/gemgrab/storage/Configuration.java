package net.teamcastle.gemgrab.storage;

import de.codeblocksmc.codelib.locations.LocationWrapper;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Configuration {
    public static Configuration getInstance() {
        return LocalStorage.getInstance().getConfig();
    }

    private String mysqlHost = "10.0.0.1";
    private int mysqlPort = 3306;
    private String mysqlDatabase = "plugins";
    private String mysqlUser = "gemrush";
    private String mysqlPassword = "password";

    private int gameDuration = 600;
    private int gemSpawnInterval = 10;
    private double minPlayersToStart = 0.8;

    private LocationWrapper lobbySpawn;

    public String connectionID() {
        return "%s@%s:%s/%s-with:%s";
    }

    @Override
    public int hashCode() {
        return mysqlHost.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Configuration that = (Configuration) obj;
        return mysqlHost.equals(that.mysqlHost) && mysqlPort == that.mysqlPort
                && mysqlDatabase.equals(that.mysqlDatabase) && mysqlUser.equals(that.mysqlUser)
                && mysqlPassword.equals(that.mysqlPassword) && gameDuration == that.gameDuration
                && gemSpawnInterval == that.gemSpawnInterval && that.minPlayersToStart == minPlayersToStart
                && ((lobbySpawn == null && that.lobbySpawn == null) || (lobbySpawn != null && lobbySpawn.equals(that.lobbySpawn)));
    }
}