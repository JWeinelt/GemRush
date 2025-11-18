package net.teamcastle.gemgrab.manager.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class GPlayer {

    private String name;
    private UUID uuid;
    private GPlayer lastDamager;
    private boolean visible;
    private boolean dead;

    public Optional<Player> asPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(uuid));
    }
}