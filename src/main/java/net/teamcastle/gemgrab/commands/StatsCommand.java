package net.teamcastle.gemgrab.commands;

import net.teamcastle.gemgrab.TeamcastleGemgrab;
import net.teamcastle.gemgrab.manager.database.MySQLManager;
import net.teamcastle.gemgrab.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.UUID;

public class StatsCommand implements CommandExecutor {

    private final TeamcastleGemgrab plugin;
    private final MySQLManager sql;

    public StatsCommand(TeamcastleGemgrab plugin) {
        this.plugin = plugin;
        this.sql = plugin.getMySQLManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0 && !(sender instanceof Player)) {
            sender.sendMessage(Messages.mainPrefix + ChatColor.RED + "Du musst einen Spielernamen angeben.");
            return true;
        }

        OfflinePlayer target = (args.length == 0)
                ? (Player) sender
                : Bukkit.getOfflinePlayer(args[0]);

        if (target.getName() == null) {
            sender.sendMessage(Messages.mainPrefix + "§cDieser Spieler wurde nicht gefunden.");
            return true;
        }

        UUID uuid = target.getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                int kills = sql.getStat(uuid, "kills");
                int deaths = sql.getStat(uuid, "deaths");
                int wins = sql.getStat(uuid, "wins");

                sender.sendMessage(Messages.mainPrefix + "§7Stats von §6" + target.getName() + "§7:");
                sender.sendMessage(Messages.mainPrefix + "§a§lKills: §7" + kills);
                sender.sendMessage(Messages.mainPrefix + "§c§lTode: §7" + deaths);
                sender.sendMessage(Messages.mainPrefix + "§e§lSiege: §7" + wins);

            } catch (SQLException e) {
                sender.sendMessage(Messages.mainPrefix + ChatColor.RED + "Fehler beim Abrufen der Statistiken.");
                e.printStackTrace();
            }
        });

        return true;
    }
}
