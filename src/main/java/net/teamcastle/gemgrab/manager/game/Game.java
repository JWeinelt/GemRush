package net.teamcastle.gemgrab.manager.game;

import com.codeblocksmc.TranslationAPI;
import com.destroystokyo.paper.ClientOption;
import de.codeblocksmc.codelib.locations.LocUtil;
import de.codeblocksmc.codelib.locations.LocationWrapper;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.teamcastle.gemgrab.GemRush;
import net.teamcastle.gemgrab.manager.GameManager;
import net.teamcastle.gemgrab.manager.GamePoolManager;
import net.teamcastle.gemgrab.manager.game.gems.GemManager;
import net.teamcastle.gemgrab.manager.game.gems.GemSpawnerManager;
import net.teamcastle.gemgrab.manager.lobby.LobbyManager;
import net.teamcastle.gemgrab.manager.map.GameMap;
import net.teamcastle.gemgrab.manager.player.GPlayer;
import net.teamcastle.gemgrab.manager.player.PlayerManager;
import net.teamcastle.gemgrab.manager.teams.TeamColor;
import net.teamcastle.gemgrab.storage.Configuration;
import org.bukkit.*;
import org.bukkit.block.data.type.TNT;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.logging.Logger;

import static net.teamcastle.gemgrab.GemRush.mainPrefix;

@Getter
public class Game implements Listener {
    private final Logger log = GemRush.getInstance().getLog();
    private final TranslationAPI TAPI = TranslationAPI.getInstance();

    private final HashMap<TeamColor, List<GPlayer>> players = new HashMap<>();

    private final GameMap map;

    private BossBar bossBar;


    private AtomicInteger starterCountdown = new AtomicInteger(60);

    private World world;
    private World lobby;

    private long timeLeft;
    @Setter
    private GameState state = GameState.LOBBY;

    private final PlayerDeathHandler deathHandler;
    private final GemSpawnerManager gemSpawnerManager;
    private final PlayerListener playerListener;
    private final GemManager gemManager;

    private final GamePoolManager gamePool;

    private BukkitTask gameTask;

    public Game(World world, GameMap map) {
        gamePool = GamePoolManager.getInstance();
        LobbyManager.getInstance().requestLobby().thenAccept(w -> {
            lobby = w;
            log.info("Lobby world for game created: " + lobby.getName());
        });

        this.world = world;
        this.map = map.remapAndClone(world);
        deathHandler = new PlayerDeathHandler(this);
        gemSpawnerManager = new GemSpawnerManager(this);
        gemManager = new GemManager(this);
        playerListener = new PlayerListener(this);
        players.put(TeamColor.RED, new ArrayList<>());
        players.put(TeamColor.BLUE, new ArrayList<>());

        timeLeft = Configuration.getInstance().getGameDuration();
    }

    public boolean canJoin() {
        return getPlayerCount() < map.getMaxPlayers() && state == GameState.LOBBY;
    }

    public List<GPlayer> getTeam(TeamColor c) {
        return players.get(c);
    }

    public double getFillPercentage() {
        int maxPlayers = map.getMaxPlayers();
        int currentPlayers = getPlayerCount();
        return (currentPlayers * 1.0) / maxPlayers;
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

    public void sendMessageToPlayersTranslated(String key) {
        sendMessageToPlayersTranslated(key, false);
    }

    public void sendMessageToPlayersTranslated(String key, boolean prefix) {
        for (GPlayer player : getAllPlayers()) {
            player.asPlayer().ifPresent(p -> {
                String lang = p.getClientOption(ClientOption.LOCALE).split("_")[0];
                p.sendMessage(((prefix) ? mainPrefix : "") + TAPI.translate(lang, key));
            });
        }
    }

    public void sendMessageToPlayersTranslated(String key, boolean prefix, Map<String, String> placeholders) {
        for (GPlayer player : getAllPlayers()) {
            player.asPlayer().ifPresent(p -> {
                String lang = p.getClientOption(ClientOption.LOCALE).split("_")[0];
                String txt = TAPI.translate(lang, key);
                for (String phKey : placeholders.keySet()) {
                    txt = txt.replace("%" + phKey + "%", placeholders.get(phKey));
                }
                p.sendMessage(((prefix) ? mainPrefix : "") + txt);
            });
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

    private Location lobbySpawn() {
        LocationWrapper w = Configuration.getInstance().getLobbySpawn();
        w.setWorld(lobby.getName());
        return LocUtil.fromWrapper(w);
    }

    public void joinGame(Player player) {
        if (getPlayerCount() == 0) createBossBar();
        TeamColor teamColor = TeamColor.getTeamWithLeastPlayers(this);
        players.get(teamColor).add(PlayerManager.getGemgrabPlayerByUUID(player.getUniqueId()));

        bossBar.addPlayer(player);

        executeForPlayers(p -> {
            p.sendMessage(mainPrefix + "§e" + player.getName() + " §7has joined the game!");
        });

        player.teleport(lobbySpawn());
        FastBoardManager.getInstance().createScoreboard(player);

        if (getFillPercentage() >= Configuration.getInstance().getMinPlayersToStart() && state == GameState.LOBBY) {
            startStarterCountdown();
            state = GameState.STARTING;
        }
        if (getFillPercentage() == 1.0 && state == GameState.STARTING) {
            starterCountdown.set(5);
            sendMessageToPlayersTranslated("gem.game.full", true, Map.of(
                    "seconds", "" + starterCountdown.get(),
                    "desc", (starterCountdown.get() == 1) ? "second" : "seconds"
            ));
        }
    }

    public void leaveGame(Player player) {
        TeamColor teamColor = getPlayerTeam(player.getUniqueId());
        players.get(teamColor).removeIf(gPlayer -> gPlayer.getUuid().equals(player.getUniqueId()));

        bossBar.removePlayer(player);

        executeForPlayers(p -> {
            p.sendMessage(mainPrefix + "§e" + player.getName() + " §7has left the game!");
        });
        if (getFillPercentage() < Configuration.getInstance().getMinPlayersToStart() && state == GameState.STARTING) {
            state = GameState.LOBBY;
            sendMessageToPlayersTranslated("gem.game.not-enough", true);
        }
    }

    private String translate(String key, Player player) {
        String lang = player.getClientOption(ClientOption.LOCALE).split("_")[0];
        return TAPI.translate(lang, key);
    }



    public void endGame(TeamColor teamColor, BossBar bossBar) {
        GemRush.setGamestate(GameState.ENDED);

        executeForPlayers(teamColor, player -> {
            StatManager.getInstance().addWin(player.getUniqueId());
            player.showTitle(Title.title(Component.text(translate("gem.game.victory", player)), Component.empty()));
        });
        executeForPlayers(TeamColor.opposite(teamColor), player -> {
            StatManager.getInstance().addLost(player.getUniqueId());
            player.showTitle(Title.title(Component.text(translate("gem.game.defeat", player)), Component.empty()));
        });

        executeForPlayers(player -> {
            player.getInventory().clear();
            StatManager.getInstance().addPlayed(player.getUniqueId());
            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 3, 3);
            player.getInventory().clear();
            player.setHealth(20);
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setFoodLevel(20);
        });

        new BukkitRunnable() {

            @Override
            public void run() {
                executeForPlayers(p -> p.teleport(lobbySpawn()));

                FastBoardManager fb = FastBoardManager.getInstance();
                executeForPlayers(fb::removeScoreboard);

                HandlerList.unregisterAll(Game.this);
                gamePool.endGame(Game.this);
            }
        }.runTaskLater(GemRush.getInstance(), 20L * 5);

        new BukkitRunnable() {

            @Override
            public void run() {
                executeForPlayers(p -> GameManager.getInstance().joinGame(p));

                LobbyManager.getInstance().releaseLobby(lobby.getName());
            }
        }.runTaskLater(GemRush.getInstance(), 20L * 5);
    }

    public void startGame() {
        gemSpawnerManager.createGemSpawner(LocUtil.fromWrapper(map.getSpawner()));
        gamePool.startGame(this);
        state = GameState.RUNNING;

        for (TeamColor c : players.keySet()) {
            for (GPlayer gPlayer : players.get(c)) {
                gPlayer.asPlayer().ifPresent(player -> {
                    LocationWrapper spawnLoc = map.getSpawnPoints().get(c).get(new Random().nextInt(map.getSpawnPoints().get(c).size()));
                    player.teleport(LocUtil.fromWrapper(spawnLoc));
                    player.playSound(player, Sound.ITEM_FIRECHARGE_USE, 1, 1);
                    player.showTitle(Title.title(Component.text(translate("gem.game.started", player)),
                            Component.text(translate("gem.game.started.luck", player))));
                });
            }
        }

        gameTask = new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        }.runTaskTimer(GemRush.getInstance(), 0, 20);
    }

    private void startStarterCountdown() {
        starterCountdown.set(60);
        new BukkitRunnable() {
            @Override
            public void run() {
                int secondsLeft = starterCountdown.getAndDecrement();

                if (Arrays.asList(10, 1, 2, 4, 5, 3).contains(secondsLeft)) {
                    sendMessageToPlayers(mainPrefix + "Starting in §c" + secondsLeft + "s");
                    Bukkit.getOnlinePlayers()
                            .forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 10, 3));
                }

                if (secondsLeft == 0) {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        player.showTitle(Title.title(Component.text(translate("gem.game.started", player)),
                                Component.text(translate("gem.game.started.luck", player))));
                        player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 3, 10);
                    });
                    startGame();
                    cancel();
                }
            }
        }.runTaskTimer(GemRush.getInstance(), 0, 20);
    }

    private void updateBossBar() {
        bossBar.setTitle("§8| §a§l%s §7- §9Blue §7----------- §4Red §7- §a§l%s §8|"
                .formatted(gemManager.calculateTeamGemsBlue(), gemManager.calculateTeamGemsRed()));
        double progress = (timeLeft * 1.0) / Configuration.getInstance().getGameDuration();
        if (progress > 1) progress = 1;
        if (progress < 0) progress = 0;
        bossBar.setProgress(progress);
    }

    private void tick() {
        timeLeft--;
        updateBossBar();

        if (timeLeft == -1) {
            int redGems = gemManager.calculateTeamGemsRed();
            int blueGems = gemManager.calculateTeamGemsBlue();
            if (redGems > blueGems) {
                endGame(TeamColor.RED, bossBar);
            } else if (blueGems > redGems) {
                endGame(TeamColor.BLUE, bossBar);
            } else {
                suddenDeath();
            }
        }
    }

    private void suddenDeath() {
        sendMessageToPlayersTranslated("gem.game.draw", true);
        timeLeft = 60;

        int yUpperBound = (int) Math.max(map.getArena().getL1().getY(), map.getArena().getL2().getY());
        LocationWrapper randomPos = map.getArena().getRandomLocation();
        randomPos.setY(yUpperBound);

        new BukkitRunnable() {
            @Override
            public void run() {
                int height = world.getHighestBlockYAt(LocUtil.fromWrapper(randomPos));
                TNTPrimed tnt = (TNTPrimed) world.spawnEntity(LocUtil.fromWrapper(randomPos), EntityType.TNT);
                int diff = yUpperBound - height;
                double secondsToFloor = diff / 4.3;
                tnt.setFuseTicks((int) (secondsToFloor * 20));
            }
        }.runTaskTimer(GemRush.getInstance(), 30, 3);
    }

    @EventHandler
    public void onExplode(BlockExplodeEvent e) {
        if (e.getExplodedBlockState().getType().equals(Material.EMERALD_BLOCK)) e.setCancelled(true);
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
