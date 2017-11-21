package com.minecraftmarket.minecraftmarket.common.stats;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.plugin.Plugin;
import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;
import com.minecraftmarket.minecraftmarket.common.api.models.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                events.add(new Event(0, "player_join", getPlayerData(e.getPlayer())));
            }

            @EventHandler
            public void onPlayerJoin(PlayerQuitEvent e) {
                events.add(new Event(0, "player_leave", getPlayerData(e.getPlayer())));
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
    Map<String, Object> getServerData() {
        Map<String, Object> data = super.getServerData();
        data.put("type", "Bukkit");
        data.put("version", plugin.getServer().getVersion());

        List<Map<String, Object>> plugins = new ArrayList<>();
        for (Plugin plugin : plugin.getServer().getPluginManager().getPlugins().values()) {
            plugins.add(getPluginData(plugin));
        }
        data.put("plugins", plugins);

        List<Map<String, Object>> onlinePlayers = new ArrayList<>();
        for (Player player : plugin.getServer().getOnlinePlayers().values()) {
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

    private Map<String, Object> getPlayerData(Player player) {
        Map<String, Object> data = new HashMap<>();
        data.put("time", getTime());
        data.put("username", player.getName());
        data.put("uuid", player.getUniqueId());
        data.put("ip", player.getAddress());
        data.put("ping", player.getPing());
        data.put("is_op", player.isOp());
        data.put("world", player.getLevel().getName());
        data.put("gamemode", player.getGamemode());
        data.put("health", player.getHealth());
        data.put("max_health", player.getMaxHealth());
        data.put("level", player.getExperienceLevel());
        data.put("exp", player.getExperience());
        data.put("food", player.getFoodData().getLevel());
        return data;
    }
}