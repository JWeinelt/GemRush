package de.joniwoch.teamcastlegemgrab.commands;

import de.joniwoch.teamcastlegemgrab.manager.locations.LobbyLocationManager;
import de.joniwoch.teamcastlegemgrab.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class SetLobbySpawnCommand implements CommandExecutor {

    private final LobbyLocationManager lobbyLocationManager;


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Du musst ein Spieler sein.");
            return false;
        }

        if (!player.hasPermission("gemgrab.setspawn")) {
            player.sendMessage(Messages.noPermsPrefix);
            return false;
        }

        if (!(args.length == 0)) {
            player.sendMessage(Messages.ussagePrefix + "setlobbyspawn§7.");
            return false;
        }

        lobbyLocationManager.setLobbyLocation(player);

        return false;
    }
}
