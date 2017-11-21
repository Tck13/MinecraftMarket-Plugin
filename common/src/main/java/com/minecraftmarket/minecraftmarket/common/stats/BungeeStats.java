package com.minecraftmarket.minecraftmarket.common.stats;

import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;
import com.minecraftmarket.minecraftmarket.common.stats.models.StatsEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    Map<String, Object> getServerData() {
        Map<String, Object> data = super.getServerData();
        String version = plugin.getProxy().getVersion();
        if (version.contains(":")) {
            String[] split = version.split(":");
            if (split.length == 5) {
                version = split[2].split("-")[0];
            }
        }

        data.put("type", "Bungee");
        data.put("version", version);
        data.put("online_mode", plugin.getProxy().getConfig().isOnlineMode());

        List<Map<String, Object>> plugins = new ArrayList<>();
        for (Plugin plugin : plugin.getProxy().getPluginManager().getPlugins()) {
            plugins.add(getPluginData(plugin));
        }
        data.put("plugins", plugins);

        List<Map<String, Object>> onlinePlayers = new ArrayList<>();
        for (ProxiedPlayer player : plugin.getProxy().getPlayers()) {
            onlinePlayers.add(getPlayerData(player));
        }
        data.put("online_players", onlinePlayers);
        return data;
    }

    private Map<String, Object> getPluginData(Plugin plugin) {
        Map<String, Object> data = new HashMap<>();
        String version = plugin.getDescription().getVersion();
        if (version.contains(":")) {
            String[] split = version.split(":");
            if (split.length == 5) {
                version = split[2].split("-")[0];
            }
        }

        data.put("name", plugin.getDescription().getName());
        data.put("version", version);
        data.put("description", plugin.getDescription().getDescription());
        data.put("author", plugin.getDescription().getAuthor());
        return data;
    }

    private Map<String, Object> getPlayerData(ProxiedPlayer player) {
        Map<String, Object> data = new HashMap<>();
        data.put("time", getTime());
        data.put("username", player.getName());
        data.put("uuid", player.getUniqueId());
        data.put("ip", player.getAddress().getHostName());
        data.put("ping", player.getPing());
        data.put("server", player.getServer().getInfo().getName());
        return data;
    }

    public class BungeeEvents implements Listener {
        @EventHandler
        public void onPlayerJoin(ServerConnectEvent e) {
            if (e.getPlayer().getServer() == null) {
                events.add(new StatsEvent("player_join", getPlayerData(e.getPlayer())));
            }
        }

        @EventHandler
        public void onPlayerDisconnect(PlayerDisconnectEvent e) {
            events.add(new StatsEvent("player_leave", getPlayerData(e.getPlayer())));
        }
    }
}