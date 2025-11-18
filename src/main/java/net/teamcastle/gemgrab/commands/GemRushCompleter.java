package net.teamcastle.gemgrab.commands;

import de.codeblocksmc.codelib.chat.AdvancedTabCompleter;
import net.teamcastle.gemgrab.manager.map.GameMap;
import net.teamcastle.gemgrab.storage.LocalStorage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GemRushCompleter extends AdvancedTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label,
                                                @NotNull String @NotNull [] args) {
        List<String> completions = new ArrayList<>();

        if (label.equalsIgnoreCase("gemrush")) {
            if (args.length <= 1) {
                complete(completions, args[0], "join", "start", "setup", "stats", "help", "reload");
            } else if (args.length == 2 && args[0].equalsIgnoreCase("setup")) {
                complete(completions, args[1], "create", "delete", "list", "set", "add");
            } else if (args.length == 3 && args[0].equalsIgnoreCase("setup")) {
                if (args[1].equalsIgnoreCase("add")) {
                    complete(completions, args[2], "spawn");
                } else if (args[1].equalsIgnoreCase("set")) {
                    complete(completions, args[2], "lobby", "maxplayers", "gemspawn");
                } else if (args[1].equalsIgnoreCase("delete")) {
                    List<String> maps = new ArrayList<>();
                    for (GameMap m : LocalStorage.getInstance().getGameMaps()) maps.add(m.getName());
                    complete(completions, args[2], maps);
                } else if (args[1].equalsIgnoreCase("create")) {
                    complete(completions, args[2], "<MapName>");
                } else completions = Collections.emptyList();
            } else if (args.length == 4) {
                if (args[0].equalsIgnoreCase("setup") && args[1].equalsIgnoreCase("create")) {
                    complete(completions, args[3], "<MaxPlayers>");
                } else completions = Collections.emptyList();
            }
        } else completions = Collections.emptyList();

        return completions;
    }
}
