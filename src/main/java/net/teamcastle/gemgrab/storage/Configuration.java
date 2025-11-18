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
}