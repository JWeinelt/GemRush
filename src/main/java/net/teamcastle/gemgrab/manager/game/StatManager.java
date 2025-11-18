package net.teamcastle.gemgrab.manager.game;

import lombok.Getter;
import net.teamcastle.gemgrab.GemRush;
import net.teamcastle.gemgrab.storage.database.MySQLManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StatManager {
    @Getter
    private final List<PlayerStat> playerStats = new ArrayList<>();

    public static StatManager getInstance() {
        return GemRush.getInstance().getStatManager();
    }

    public void cacheOnline() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            getPlayerStat(player.getUniqueId());
        }
    }

    @NotNull
    public PlayerStat getPlayerStat(UUID uuid) {
        for (PlayerStat stat : playerStats) {
            if (stat.getPlayerID().equals(uuid)) {
                return stat;
            }
        }
        PlayerStat newStat = MySQLManager.getInstance().getPlayerStat(uuid);
        playerStats.add(newStat);
        return newStat;
    }

    public void addKill(UUID uuid) {
        getPlayerStat(uuid).addKills(1);
    }
    public void addDeath(UUID uuid) {
        getPlayerStat(uuid).addDeaths(1);
    }
    public void addWin(UUID uuid) {
        getPlayerStat(uuid).addWins(1);
    }
    public void addLost(UUID uuid) {
        getPlayerStat(uuid).addLost(1);
    }
    public void addPlayed(UUID uuid) {
        getPlayerStat(uuid).addPlayed(1);
    }
}