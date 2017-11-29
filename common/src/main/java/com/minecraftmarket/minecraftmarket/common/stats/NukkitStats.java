package com.minecraftmarket.minecraftmarket.common.stats;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.plugin.Plugin;
import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;
import com.minecraftmarket.minecraftmarket.common.api.models.PlayerSession;
import com.minecraftmarket.minecraftmarket.common.api.models.ServerInfo;
import com.minecraftmarket.minecraftmarket.common.api.models.ServerPlayer;
import com.minecraftmarket.minecraftmarket.common.api.models.ServerPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class NukkitStats extends MCMarketStats {
    private final Plugin plugin;

    public NukkitStats(MCMarketApi marketApi, Plugin plugin) {
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

                plugin.getServer().getScheduler().scheduleTask(plugin, () -> runEventsSender());
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
                "Nukkit",
                version,
                true,
                plugin.getServer().getIp(),
                plugin.getServer().getPort(),
                Math.min(plugin.getServer().getTicksPerSecond(), 20.0),
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

    private List<ServerPlayer> getOnlinePlayers() {
        List<ServerPlayer> players = new ArrayList<>();
        for (Player player : plugin.getServer().getOnlinePlayers().values()) {
            players.add(new ServerPlayer(
                    player.getName(),
                    player.getUniqueId().toString(),
                    player.getAddress(),
                    player.getPing(),
                    Optional.of(player.isOp()),
                    Optional.of(player.getLevel().getName()),
                    Optional.of((long) player.getLocation().getFloorX()),
                    Optional.of((long) player.getLocation().getFloorY()),
                    Optional.of((long) player.getLocation().getFloorZ()),
                    Optional.of(Server.getGamemodeString(player.getGamemode(), true)),
                    Optional.of((double) player.getHealth()),
                    Optional.of((double) player.getMaxHealth()),
                    Optional.of((long) player.getExperienceLevel()),
                    Optional.of((float) player.getExperience()),
                    Optional.of((long) player.getFoodData().getLevel()),
                    Optional.empty()
            ));
        }
        return players;
    }

    private List<ServerPlugin> getPlugins() {
        List<ServerPlugin> plugins = new ArrayList<>();
        for (Plugin plugin : plugin.getServer().getPluginManager().getPlugins().values()) {
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
                player.getAddress()
        );
    }
}