package com.minecraftmarket.minecraftmarket.common.stats;

import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;
import com.minecraftmarket.minecraftmarket.common.api.models.PlayerSession;
import com.minecraftmarket.minecraftmarket.common.api.models.ServerInfo;
import com.minecraftmarket.minecraftmarket.common.api.models.ServerPlayer;
import com.minecraftmarket.minecraftmarket.common.api.models.ServerPlugin;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class BungeeStats extends MCMarketStats {
    private final Plugin plugin;

    public BungeeStats(MCMarketApi marketApi, Plugin plugin) {
        super(marketApi);
        this.plugin = plugin;

        plugin.getProxy().getPluginManager().registerListener(plugin, new BungeeEvents());

        plugin.getProxy().getScheduler().schedule(plugin, this::runEventsSender, 10, 60, TimeUnit.SECONDS);
    }

    @Override
    ServerInfo getServerInfo() {
        Map<String, Object> system = getSystemStats();

        String version = plugin.getProxy().getVersion();
        if (version.contains(":")) {
            String[] split = version.split(":");
            if (split.length == 5) {
                version = split[2].split("-")[0];
            }
        }

        String ip = "Unknown";
        int port = 25565;
        for (ListenerInfo listener : plugin.getProxy().getConfig().getListeners()) {
            ip = listener.getHost().getAddress().getHostAddress();
            port = listener.getHost().getPort();
        }

        return new ServerInfo(
                0,
                getTime(),
                "Bungee",
                version,
                plugin.getProxy().getConfig().isOnlineMode(),
                ip,
                port,
                20.0,
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
        for (ProxiedPlayer player : plugin.getProxy().getPlayers()) {
            players.add(new ServerPlayer(
                    player.getName(),
                    player.getUniqueId().toString(),
                    player.getAddress().getAddress().getHostAddress(),
                    player.getPing(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.of(player.getServer() != null ? player.getServer().getInfo().getName() : "Unknown")
            ));
        }
        return players;
    }

    private List<ServerPlugin> getPlugins() {
        List<ServerPlugin> plugins = new ArrayList<>();
        for (Plugin plugin : plugin.getProxy().getPluginManager().getPlugins()) {
            String version = plugin.getDescription().getVersion();
            if (version.contains(":")) {
                String[] split = version.split(":");
                if (split.length == 5) {
                    version = split[2].split("-")[0];
                }
            }

            plugins.add(new ServerPlugin(
                    plugin.getDescription().getName(),
                    version,
                    plugin.getDescription().getDescription(),
                    plugin.getDescription().getAuthor(),
                    Optional.empty()
            ));
        }
        return plugins;
    }

    private PlayerSession getPlayerSession(ProxiedPlayer player) {
        return new PlayerSession(
                0,
                player.getName(),
                player.getUniqueId().toString(),
                playerJoins.get(player.getUniqueId()),
                getTime(),
                player.getAddress().getAddress().getHostAddress()
        );
    }

    // Use a public class because Bungee doesn't like private :(
    public class BungeeEvents implements Listener {
        @EventHandler
        public void onPlayerJoin(ServerConnectEvent e) {
            if (e.getPlayer().getServer() == null) {
                playerJoins.put(e.getPlayer().getUniqueId(), getTime());
            }
        }

        @EventHandler
        public void onPlayerDisconnect(PlayerDisconnectEvent e) {
            if (playerJoins.containsKey(e.getPlayer().getUniqueId())) {
                playerSession.add(getPlayerSession(e.getPlayer()));
                playerJoins.remove(e.getPlayer().getUniqueId());
            }
        }
    }
}