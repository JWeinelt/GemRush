package net.teamcastle.gemgrab.commands;

import net.teamcastle.gemgrab.manager.game.PlayerStat;
import net.teamcastle.gemgrab.manager.game.StatManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StatsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             String @NotNull [] args) {

        if (!(sender instanceof Player player)) {
            return true;
        }

        PlayerStat s = StatManager.getInstance().getPlayerStat(player.getUniqueId());

        player.sendMessage("§e======= §aGemRush Stats §e=======");
        player.sendMessage("§aKills: §e%s".formatted(s.getKills()));
        player.sendMessage("§aDeaths: §e%s".formatted(s.getDeaths()));
        player.sendMessage("§aK/D Ratio: §e%.2f".formatted((s.getDeaths() == 0) ? s.getKills() : (double) s.getKills() / s.getDeaths()));
        player.sendMessage("§aWins: §e%s §7(%s§7)".formatted(s.getWins(), (s.getWins() * 1.0 / s.getPlayed())));
        player.sendMessage("§aLost: §e%s".formatted(s.getLost()));
        player.sendMessage("§aGames Played: §e%s".formatted(s.getPlayed()));
        player.sendMessage("§e============================");
        return true;
    }
}
