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
public class CreateMapCommand implements CommandExecutor {

    private final GameMapManager gameMapManager;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Du musst ein Spieler sein.");
            return false;
        }

        if (!player.hasPermission("gemgrab.createmap")) {
            player.sendMessage(Messages.noPermsPrefix);
            return false;
        }

        if (!(args.length == 1)) {
            player.sendMessage(" ");
            player.sendMessage(Messages.ussagePrefix + "creategamemap <Name> §7.");
            player.sendMessage(Messages.mainPrefix + "Schaue dabei den §aBlock §7an, der");
            player.sendMessage(Messages.mainPrefix + "zum §aSpawner §7werden soll.");
            player.sendMessage(" ");
            return false;
        }

        String mapName = args[0];

        gameMapManager.createMap(player, mapName);

        return false;
    }
}
