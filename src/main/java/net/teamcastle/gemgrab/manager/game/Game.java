package net.teamcastle.gemgrab.manager.game;

import de.codeblocksmc.codelib.locations.LocUtil;
import lombok.Getter;
import lombok.Setter;
import net.teamcastle.gemgrab.GemRush;
import net.teamcastle.gemgrab.listener.PlayerListener;
import net.teamcastle.gemgrab.manager.game.gems.GemManager;
import net.teamcastle.gemgrab.manager.game.gems.GemSpawnerManager;
import net.teamcastle.gemgrab.manager.map.GameMap;
import net.teamcastle.gemgrab.manager.player.GPlayer;
import net.teamcastle.gemgrab.manager.player.PlayerManager;
import net.teamcastle.gemgrab.manager.teams.TeamColor;
import net.teamcastle.gemgrab.storage.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static net.teamcastle.gemgrab.GemRush.mainPrefix;

@Getter
public class Game implements Listener {
    private final HashMap<TeamColor, List<GPlayer>> players = new HashMap<>();

    private final GameMap map;

    private BossBar bossBar;

    private World world;

    private long timeLeft;
    @Setter
    private GameState state = GameState.LOBBY;

    private final PlayerDeathHandler deathHandler;
    private final GemSpawnerManager gemSpawnerManager;
    private final PlayerListener playerListener;
    private final GemManager gemManager;

    public Game(GameMap map) {
        this.map = map;
        deathHandler = new PlayerDeathHandler(this);
        gemSpawnerManager = new GemSpawnerManager(this);
        gemManager = new GemManager(this);
        playerListener = new PlayerListener(this);
        players.put(TeamColor.RED, new ArrayList<>());
        players.put(TeamColor.BLUE, new ArrayList<>());

        timeLeft = Configuration.getInstance().getGameDuration() * 20L;
    }

    public List<GPlayer> getTeam(TeamColor c) {
        return players.get(c);
    }

    public int getPlayerCount() {
        int count = 0;
        for (List<GPlayer> teamPlayers : players.values()) {
            count += teamPlayers.size();
        }
        return count;
    }

    public void sendMessageToPlayers(String message) {
        for (GPlayer player : getAllPlayers()) {
            player.asPlayer().ifPresent(p->p.sendMessage(message));
        }
    }

    public void sendMessageToPlayers(TeamColor c, String message) {
        for (GPlayer player : players.get(c)) {
            player.asPlayer().ifPresent(p->p.sendMessage(message));
        }
    }

    public List<GPlayer> getAllPlayers() {
        List<GPlayer> allPlayers = new ArrayList<>();
        for (List<GPlayer> teamPlayers : players.values()) {
            allPlayers.addAll(teamPlayers);
        }
        return allPlayers;
    }

    public void executeForPlayers(Consumer<Player> action) {
        for (GPlayer player : getAllPlayers()) {
            player.asPlayer().ifPresent(action);
        }
    }

    public void executeForPlayers(TeamColor c, Consumer<Player> action) {
        for (GPlayer player : players.get(c)) {
            player.asPlayer().ifPresent(action);
        }
    }

    @NotNull
    public TeamColor getPlayerTeam(UUID uuid) {
        for (TeamColor color : players.keySet()) {
            for (GPlayer gPlayer : players.get(color)) {
                if (gPlayer.getUuid().equals(uuid)) {
                    return color;
                }
            }
        }
        return TeamColor.UNKNOWN;
    }

    private void createBossBar() {
        bossBar = Bukkit.createBossBar(
                "§8| §a§l0 §7- §1Blau §7----------- §4Rot §7- §a§l0 §8|",
                BarColor.WHITE,
                BarStyle.SOLID
        );
    }

    public void joinGame(Player player) {
        if (getPlayerCount() == 0) createBossBar();
        TeamColor teamColor = TeamColor.getTeamWithLeastPlayers(this);
        players.get(teamColor).add(PlayerManager.getGemgrabPlayerByUUID(player.getUniqueId()));

        bossBar.addPlayer(player);

        executeForPlayers(p -> {
            p.sendMessage(mainPrefix + "§e" + player.getName() + " §7has joined the game!");
        });
    }



    public void endGame(TeamColor teamColor, BossBar bossBar) {
        GemRush.setGamestate(GameState.ENDED);

        executeForPlayers(teamColor, player -> {
            StatManager.getInstance().addWin(player.getUniqueId());
        });

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 3, 3);
            player.sendTitle("§7Team " + teamColor.colorCode + teamColor.displayName, "§7wins the game!");
            //TODO: Teleport to lobby spawn
            player.getInventory().clear();
            player.setHealth(20);
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setFoodLevel(20);
        });
    }

    public void startGame() {
        gemSpawnerManager.createGemSpawner(LocUtil.fromWrapper(map.getSpawner()));
    }

    private void startStarterCountdown() {
        final int[] taskIdHolder = new int[1];
        AtomicInteger countdown = new AtomicInteger(10);

        taskIdHolder[0] = Bukkit.getScheduler().runTaskTimer(GemRush.getInstance(), () -> {
            int secondsLeft = countdown.getAndDecrement();

            if (Arrays.asList(10, 1, 2, 4, 5, 3).contains(secondsLeft)) {
                sendMessageToPlayers(mainPrefix + "Starting in §c" + secondsLeft + "s");
                Bukkit.getOnlinePlayers()
                        .forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 10, 3));
            }

            if (secondsLeft == 0) {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.sendTitle("§a§lGame started", "§eGood luck");
                    player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 3, 10);
                });
                Bukkit.getScheduler().cancelTask(taskIdHolder[0]);
                gemSpawnerManager.spawnGems(LocUtil.fromWrapper(map.getSpawner()));
                GemRush.setGamestate(GameState.RUNNING);
            }
        }, 0L, 20L).getTaskId();
    }

    private void updateBossBar() {
        bossBar.setTitle("§8| §a§l%s §7- §9Blue §7----------- §4Red §7- §a§l%s §8|"
                .formatted(gemManager.calculateTeamGemsBlue(), gemManager.calculateTeamGemsRed()));
        double progress = timeLeft * 1.0 / (Configuration.getInstance().getGameDuration() * 20);
        if (progress > 1) progress = 1;
        if (progress < 0) progress = 0;
        bossBar.setProgress(progress);
    }

    private void tick() {
        timeLeft--;
        updateBossBar();
    }


    // Event handlers
    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (GemRush.getGamestate() == GameState.RUNNING) {
            if (event.getPlayer().getLocation().getY() <= 88) {
                deathHandler.setPlayerDead(event.getPlayer());
            }
        }
    }
}
