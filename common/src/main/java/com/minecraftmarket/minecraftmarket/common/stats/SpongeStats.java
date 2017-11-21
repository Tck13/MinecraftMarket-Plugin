package com.minecraftmarket.minecraftmarket.common.stats;

import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;
import com.minecraftmarket.minecraftmarket.common.api.models.Event;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.manipulator.mutable.entity.ExperienceHolderData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class SpongeStats extends MCMarketStats {

    public SpongeStats(MCMarketApi marketApi, PluginContainer plugin) {
        super(marketApi);

        Sponge.getEventManager().registerListeners(plugin, this);

        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                if (!Sponge.getPluginManager().isLoaded(plugin.getId())) {
                    timer.cancel();
                    return;
                }

                Sponge.getScheduler().createTaskBuilder().execute(() -> runEventsSender()).submit(plugin);
            }
        }, 1000 * 10, 1000 * 60);
    }

    @Listener
    public void onClientJoin(ClientConnectionEvent.Join e) {
        events.add(new Event(0, "player_join", getPlayerData(e.getTargetEntity())));
    }

    @Listener
    public void onClientDisconnect(ClientConnectionEvent.Disconnect e) {
        events.add(new Event(0, "player_leave", getPlayerData(e.getTargetEntity())));
    }

    @Override
    Map<String, Object> getServerData() {
        Map<String, Object> data = super.getServerData();
        data.put("type", "Sponge");
        data.put("version", Sponge.getGame().getPlatform().getMinecraftVersion().getName());
        data.put("online_mode", Sponge.getServer().getOnlineMode());

        List<Map<String, Object>> plugins = new ArrayList<>();
        for (PluginContainer plugin : Sponge.getPluginManager().getPlugins()) {
            plugins.add(getPluginData(plugin));
        }
        data.put("plugins", plugins);

        List<Map<String, Object>> onlinePlayers = new ArrayList<>();
        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            onlinePlayers.add(getPlayerData(player));
        }
        data.put("online_players", onlinePlayers);
        return data;
    }

    private Map<String, Object> getPluginData(PluginContainer plugin) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", plugin.getName());
        data.put("version", plugin.getVersion().orElse("Unknown"));
        data.put("description", plugin.getDescription().orElse("Unknown"));
        data.put("website", plugin.getUrl().orElse("Unknown"));
        data.put("authors", plugin.getAuthors());
        return data;
    }

    private Map<String, Object> getPlayerData(Player player) {
        Map<String, Object> data = new HashMap<>();
        data.put("time", getTime());
        data.put("username", player.getName());
        data.put("uuid", player.getUniqueId());
        data.put("ip", player.getConnection().getAddress().getAddress().getHostAddress());
        data.put("ping", player.getConnection().getLatency());
        data.put("is_op", player.hasPermission("sponge.command"));
        data.put("world", player.getWorld().getName());
        data.put("gamemode", player.gameMode().get().getName());
        data.put("health", player.health().get());
        data.put("max_health", player.maxHealth().get());
        Optional<ExperienceHolderData> experienceData = player.get(ExperienceHolderData.class);
        if (experienceData.isPresent()) {
            data.put("level", experienceData.get().level().get());
            data.put("exp", experienceData.get().totalExperience().get());
        } else {
            data.put("level", "Unknown");
            data.put("exp", "Unknown");
        }
        data.put("food", player.foodLevel().get());
        return data;
    }
}