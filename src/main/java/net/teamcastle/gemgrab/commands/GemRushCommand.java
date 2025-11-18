package net.teamcastle.gemgrab.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SpellCheckingInspection")
public class GemRushCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label,
                             @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command.");
            return false;
        }

        if (label.equalsIgnoreCase("gemrush")) {
            if (args.length == 0) {
                player.sendMessage("§e=== §6GemRush Commands §e===");
                player.sendMessage("§a/gemrush join §7- Join a GemRush game.");
                player.sendMessage("§a/gemrush start §7- Start the current GemRush game.");
                player.sendMessage("§a/gemrush setup §7- Create GemRush maps.");
                player.sendMessage("§a/gemrush stats §7- View your GemRush statistics.");
                player.sendMessage("§a/gemrush help §7- Show this help message.");
                return true;
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("start")) {

                } else if (args[0].equalsIgnoreCase("join")) {

                } else if (args[0].equalsIgnoreCase("setup")) {
                    player.sendMessage("§e=== §6GemRush Setup Command §e===");
                    player.sendMessage("§aSetup create <MapName> <MaxCount> §7- Create a new map.");
                    player.sendMessage("§aSetup delete <Map> §7- Delete a map.");
                    player.sendMessage("§aSetup list §7- List all GemRush Maps.");
                    player.sendMessage("§aSetup set <Property> <Value> §7- Sets a property with the given value.");
                    player.sendMessage("§aSetup add <Type> §7- Adds <Type> to the game.");
                } else if (args[0].equalsIgnoreCase("help")) {
                    player.sendMessage("§e=== §6GemRush Help §e===");
                    player.sendMessage("Use §a/gemrush§7 to see a list of commands.");
                } else if (args[0].equalsIgnoreCase("reload")) {
                    player.sendMessage("§2Reloading §econfiguration data...");

                } else {
                    player.sendMessage("§cUnknown subcommand. Use §a/gemrush help §7for assistance.");
                }
            }
        }
        return false;
    }
}
