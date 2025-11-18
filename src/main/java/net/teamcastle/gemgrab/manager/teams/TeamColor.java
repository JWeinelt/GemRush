package net.teamcastle.gemgrab.manager.teams;

import net.teamcastle.gemgrab.manager.game.Game;
import org.bukkit.Material;

public enum TeamColor {
    BLUE("§9", "Blue", Material.BLUE_CONCRETE),
    RED("§c", "Red", Material.RED_CONCRETE),
    UNKNOWN("§7", "Unknown", Material.GRAY_CONCRETE);

    public final String colorCode;
    public final String displayName;
    public final Material material;

    TeamColor(String colorCode, String displayName, Material material) {
        this.colorCode = colorCode;
        this.displayName = displayName;
        this.material = material;
    }

    public static TeamColor of(String input) {
        return valueOf(input);
    }

    public static TeamColor getTeamWithLeastPlayers(Game game) {
        int bluePlayers = game.getPlayers().get(BLUE).size();
        int redPlayers = game.getPlayers().get(RED).size();
        return bluePlayers <= redPlayers ? BLUE : RED;
    }
}
