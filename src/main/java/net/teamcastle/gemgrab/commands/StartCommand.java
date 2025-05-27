package net.teamcastle.gemgrab.commands;

import net.teamcastle.gemgrab.TeamcastleGemgrab;
import net.teamcastle.gemgrab.manager.game.Gamestate;
import net.teamcastle.gemgrab.manager.game.start.GameStartHandler;
import net.teamcastle.gemgrab.utils.Messages;
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

        if (!TeamcastleGemgrab.getGamestate().equals(Gamestate.LOBBY)) {
            player.sendMessage(Messages.errorPrefix + "Das Spiel §cwurde bereits §7gestartet.");
            return false;
        }

        gameStartHandler.startGame();

        return false;
    }
}
