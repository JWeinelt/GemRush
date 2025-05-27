package net.teamcastle.gemgrab.commands;

import net.teamcastle.gemgrab.manager.locations.LobbyLocationManager;
import net.teamcastle.gemgrab.utils.Messages;
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
            player.sendMessage(" ");
            player.sendMessage(Messages.ussagePrefix + "setlobbyspawn§7.");
            player.sendMessage(" ");
            return false;
        }

        lobbyLocationManager.setLobbyLocation(player);

        return false;
    }
}
