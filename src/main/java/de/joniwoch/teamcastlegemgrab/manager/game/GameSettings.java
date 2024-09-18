package de.joniwoch.teamcastlegemgrab.manager.game;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GameSettings {

    @Getter
    @Setter
    public int teamSize;
    @Getter
    @Setter
    public int gemCooldown;
    @Getter
    @Setter
    public int startCountdown;
    @Getter
    @Setter
    public int respawnTimer;
}
