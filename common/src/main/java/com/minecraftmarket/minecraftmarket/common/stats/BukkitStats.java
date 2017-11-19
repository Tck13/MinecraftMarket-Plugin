package com.minecraftmarket.minecraftmarket.common.stats;

import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;
import com.minecraftmarket.minecraftmarket.common.stats.models.StatsEvent;
import com.minecraftmarket.minecraftmarket.common.stats.models.StatsResponse;
import com.minecraftmarket.minecraftmarket.common.utils.Ping;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BukkitStats extends MMStats {
    private final JavaPlugin plugin;
    private List<StatsEvent> events = new ArrayList<>();

    public BukkitStats(MCMarketApi marketApi, JavaPlugin plugin) {
        super(marketApi);
        this.plugin = plugin;

        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new DataTask(), 20 * 10, 20 * 60);

        plugin.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerJoin(PlayerJoinEvent e) {
                events.add(new StatsEvent("player_join", getTime(), getPlayerData(e.getPlayer())));
            }

            @EventHandler
            public void onPlayerJoin(PlayerQuitEvent e) {
                events.add(new StatsEvent("player_leave", getTime(), getPlayerData(e.getPlayer())));
            }
        }, plugin);
    }

    @SuppressWarnings("deprecation")
    private Map<String, Object> getPlayerData(Player player) {
        Map<String, Object> data = new HashMap<>();
        data.put("username", player.getName());
        data.put("uuid", player.getUniqueId());
        data.put("ip", player.getAddress().getHostName());
        data.put("ping", Ping.getPing(player));
        data.put("is_op", player.isOp());
        data.put("world", player.getWorld().getName());
        data.put("gamemode", player.getGameMode().name());
        data.put("health", player.getHealth());
        data.put("max_health", player.getMaxHealth());
        data.put("level", player.getLevel());
        data.put("exp", player.getExp());
        data.put("food", player.getFoodLevel());
        return data;
    }

    private Map<String, Object> getServerData() {
        Map<String, Object> data = new HashMap<>();
        String version = plugin.getServer().getVersion();
        version = version.substring(version.indexOf("MC: ") + 4, version.length() - 1);

        data.put("type", "Bukkit");
        data.put("version", version);
        data.put("online_mode", plugin.getServer().getOnlineMode());
        data.put("java_version", System.getProperty("java.version"));
        data.put("os_name", System.getProperty("os.name"));
        data.put("os_arch", System.getProperty("os.arch"));
        data.put("os_version", System.getProperty("os.version"));
        data.put("max_memory", Runtime.getRuntime().maxMemory());
        data.put("total_memory", Runtime.getRuntime().totalMemory());
        data.put("free_memory", Runtime.getRuntime().freeMemory());
        data.put("processors", Runtime.getRuntime().availableProcessors());

        List<Map<String, Object>> plugins = new ArrayList<>();
        for (Plugin plugin : plugin.getServer().getPluginManager().getPlugins()) {
            plugins.add(getPluginData(plugin));
        }
        data.put("plugins", plugins);

        List<Map<String, Object>> onlinePlayers = new ArrayList<>();
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            onlinePlayers.add(getPlayerData(player));
        }
        data.put("online_players", onlinePlayers);
        return data;
    }

    private Map<String, Object> getPluginData(Plugin plugin) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", plugin.getName());
        data.put("version", plugin.getDescription().getVersion());
        data.put("description", plugin.getDescription().getDescription());
        data.put("website", plugin.getDescription().getWebsite());
        data.put("authors", plugin.getDescription().getAuthors());
        return data;
    }

    private class DataTask implements Runnable {
        private int cycles = 0;

        @Override
        public void run() {
            events.add(new StatsEvent("server_info", getTime(), getServerData()));
            if (cycles >= 4) {
                if (sendData(new StatsResponse(events))) {
                    events.clear();
                }
                cycles = 0;
            } else {
                cycles++;
            }
        }
    }
}