package de.joniwoch.teamcastlegemgrab.commands;

import de.joniwoch.teamcastlegemgrab.manager.game.GameStartHandler;
import de.joniwoch.teamcastlegemgrab.manager.locations.map.GameMap;
import de.joniwoch.teamcastlegemgrab.manager.locations.map.GameMapManager;
import de.joniwoch.teamcastlegemgrab.manager.teams.TeamColor;
import de.joniwoch.teamcastlegemgrab.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class StartCommand implements CommandExecutor {

    private final GameStartHandler gameStartHandler;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Du musst ein Spieler sein.");
            return false;
        }

        if (!player.hasPermission("gemgrab.start")) {
            player.sendMessage(Messages.noPermsPrefix);
            return false;
        }

        if (!(args.length == 0)) {
            player.sendMessage(" ");
            player.sendMessage(Messages.ussagePrefix + "start");
            player.sendMessage(" ");
            return false;
        }

        gameStartHandler.startGame();

        return false;
    }
}
