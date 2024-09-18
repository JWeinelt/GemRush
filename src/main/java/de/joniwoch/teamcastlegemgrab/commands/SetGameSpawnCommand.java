package de.joniwoch.teamcastlegemgrab.commands;

import de.joniwoch.teamcastlegemgrab.manager.locations.map.GameMapManager;
import de.joniwoch.teamcastlegemgrab.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class SetGameSpawnCommand implements CommandExecutor {

    private final GameMapManager gameMapManager;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Du musst ein Spieler sein.");
            return false;
        }

        if (!player.hasPermission("gemgrab.setgamespawn")) {
            player.sendMessage(Messages.noPermsPrefix);
            return false;
        }

        if (!(args.length == 2)) {
            player.sendMessage(" ");
            player.sendMessage(Messages.ussagePrefix + "setgamespawn <Farbe> <ID> §7.");
            player.sendMessage(" ");
            return false;
        }

        String farbe = args[0].toUpperCase();
        String id = args[1];

        gameMapManager.setGameSpawn(player, farbe, id);

        return false;
    }
}
