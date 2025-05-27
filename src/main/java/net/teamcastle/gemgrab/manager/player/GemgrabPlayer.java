package net.teamcastle.gemgrab.manager.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class GemgrabPlayer {

    private String name;
    private UUID uuid;
    private GemgrabPlayer lastDamager;
    private boolean visibility;
    private boolean dead;
}
