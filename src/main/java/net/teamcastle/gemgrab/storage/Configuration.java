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

    private String mysqlHost;
    private int mysqlPort;
    private String mysqlDatabase;
    private String mysqlUser;
    private String mysqlPassword;

    private int minPlayers;
    private int maxPlayers;
    private int gameDuration;
    private int gemSpawnInterval;

    private int lobbyCountdown;
    private int gameRestartDelay;

    private LocationWrapper lobbySpawn;
}
