package net.teamcastle.gemgrab.manager.game;

import lombok.Getter;

import java.util.UUID;

@Getter
public class PlayerStat {
    private final UUID playerID;

    private int kills;
    private int deaths;
    private int wins;
    private int lost;
    private int played;

    public PlayerStat(UUID playerID) {
        this.playerID = playerID;
    }

    public PlayerStat(UUID playerID, int kills, int deaths, int wins, int lost, int played) {
        this.playerID = playerID;
        this.kills = kills;
        this.deaths = deaths;
        this.wins = wins;
        this.lost = lost;
        this.played = played;
    }

    public void addKills(int kills) {
        this.kills += kills;
    }
    public void addDeaths(int deaths) {
        this.deaths += deaths;
    }
    public void addWins(int wins) {
        this.wins += wins;
    }
    public void addLost(int lost) {
        this.lost += lost;
    }
    public void addPlayed(int played) {
        this.played += played;
    }
}
