package com.minecraftmarket.minecraftmarket.common.stats;

import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;
import com.minecraftmarket.minecraftmarket.common.api.models.PlayerSession;
import com.minecraftmarket.minecraftmarket.common.api.models.ServerInfo;
import com.minecraftmarket.minecraftmarket.common.api.models.ServerPlayer;
import com.minecraftmarket.minecraftmarket.common.api.models.ServerPlugin;
import com.minecraftmarket.minecraftmarket.common.utils.Ping;
import com.minecraftmarket.minecraftmarket.common.utils.TPS;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class BukkitStats extends MCMarketStats {
    private final JavaPlugin plugin;

    public BukkitStats(MCMarketApi marketApi, JavaPlugin plugin) {
        super(marketApi);
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerJoin(PlayerJoinEvent e) {
                playerJoins.put(e.getPlayer().getUniqueId(), getTime());
            }

            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent e) {
                if (playerJoins.containsKey(e.getPlayer().getUniqueId())) {
                    playerSession.add(getPlayerSession(e.getPlayer()));
                    playerJoins.remove(e.getPlayer().getUniqueId());
                }
            }
        }, plugin);

        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                if (!plugin.isEnabled()) {
                    timer.cancel();
                    return;
                }

                plugin.getServer().getScheduler().runTask(plugin, () -> runEventsSender());
            }
        }, 1000 * 10, 1000 * 60);
    }

    @Override
    ServerInfo getServerInfo() {
        Map<String, Object> system = getSystemStats();

        String version = plugin.getServer().getVersion();
        version = version.substring(version.indexOf("MC: ") + 4, version.length() - 1);

        return new ServerInfo(
                0,
                getTime(),
                "Bukkit",
                version,
                plugin.getServer().getOnlineMode(),
                plugin.getServer().getIp(),
                plugin.getServer().getPort(),
                TPS.getTPS(0),
                (String) system.get("javaVersion"),
                (String) system.get("osName"),
                (String) system.get("osArch"),
                (String) system.get("osVersion"),
                (long) system.get("maxMemory"),
                (long) system.get("totalMemory"),
                (long) system.get("freeMemory"),
                (long) system.get("cores"),
                (double) system.get("cpuUsage"),
                getOnlinePlayers(),
                getPlugins()
        );
    }

    @SuppressWarnings("deprecation")
    private List<ServerPlayer> getOnlinePlayers() {
        List<ServerPlayer> players = new ArrayList<>();
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            players.add(new ServerPlayer(
                    player.getName(),
                    player.getUniqueId().toString(),
                    player.getAddress().getAddress().getHostAddress(),
                    Ping.getPing(player),
                    Optional.of(player.isOp()),
                    Optional.of(player.getWorld().getName()),
                    Optional.of((long) player.getLocation().getBlockX()),
                    Optional.of((long) player.getLocation().getBlockY()),
                    Optional.of((long) player.getLocation().getBlockZ()),
                    Optional.of(player.getGameMode().name()),
                    Optional.of(round(player.getHealth())),
                    Optional.of(round(player.getMaxHealth())),
                    Optional.of((long) player.getLevel()),
                    Optional.of(round(player.getExp())),
                    Optional.of((long) player.getFoodLevel()),
                    Optional.empty()
            ));
        }
        return players;
    }

    private List<ServerPlugin> getPlugins() {
        List<ServerPlugin> plugins = new ArrayList<>();
        for (Plugin plugin : plugin.getServer().getPluginManager().getPlugins()) {
            plugins.add(new ServerPlugin(
                    plugin.getName(),
                    plugin.getDescription().getVersion(),
                    plugin.getDescription().getDescription(),
                    String.join(", ", plugin.getDescription().getAuthors()),
                    Optional.ofNullable(plugin.getDescription().getWebsite())
            ));
        }
        return plugins;
    }

    private PlayerSession getPlayerSession(Player player) {
        return new PlayerSession(
                0,
                player.getName(),
                player.getUniqueId().toString(),
                playerJoins.get(player.getUniqueId()),
                getTime(),
                player.getAddress().getAddress().getHostAddress()
        );
    }
}