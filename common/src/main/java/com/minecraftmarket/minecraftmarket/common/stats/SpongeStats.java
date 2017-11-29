package com.minecraftmarket.minecraftmarket.common.stats;

import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;
import com.minecraftmarket.minecraftmarket.common.api.models.PlayerSession;
import com.minecraftmarket.minecraftmarket.common.api.models.ServerInfo;
import com.minecraftmarket.minecraftmarket.common.api.models.ServerPlayer;
import com.minecraftmarket.minecraftmarket.common.api.models.ServerPlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.manipulator.mutable.entity.ExperienceHolderData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.PluginContainer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
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
        playerJoins.put(e.getTargetEntity().getUniqueId(), getTime());
    }

    @Listener
    public void onClientDisconnect(ClientConnectionEvent.Disconnect e) {
        if (playerJoins.containsKey(e.getTargetEntity().getUniqueId())) {
            playerSession.add(getPlayerSession(e.getTargetEntity()));
            playerJoins.remove(e.getTargetEntity().getUniqueId());
        }
    }

    @Override
    ServerInfo getServerInfo() {
        Map<String, Object> system = getSystemStats();

        String ip = "";
        int port = 25565;
        Optional<InetSocketAddress> address = Sponge.getServer().getBoundAddress();
        if (address.isPresent()) {
            ip = address.get().getAddress().getHostAddress();
            port = address.get().getPort();
        }
        return new ServerInfo(
                0,
                getTime(),
                "Sponge",
                Sponge.getGame().getPlatform().getMinecraftVersion().getName(),
                Sponge.getServer().getOnlineMode(),
                ip,
                port,
                Sponge.getServer().getTicksPerSecond(),
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
        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            Optional<ExperienceHolderData> experienceData = player.get(ExperienceHolderData.class);
            Optional<Long> level;
            Optional<Float> exp;
            if (experienceData.isPresent()) {
                level = Optional.of((long) experienceData.get().level().get());
                exp = Optional.of((float) experienceData.get().totalExperience().get());
            } else {
                level = Optional.empty();
                exp = Optional.empty();
            }
            players.add(new ServerPlayer(
                    player.getName(),
                    player.getUniqueId().toString(),
                    player.getConnection().getAddress().getAddress().getHostAddress(),
                    player.getConnection().getLatency(),
                    Optional.empty(),
                    Optional.of(player.getWorld().getName()),
                    Optional.of((long) player.getLocation().getBlockX()),
                    Optional.of((long) player.getLocation().getBlockY()),
                    Optional.of((long) player.getLocation().getBlockZ()),
                    Optional.of(player.gameMode().get().getName()),
                    Optional.of(player.health().get()),
                    Optional.of(player.maxHealth().get()),
                    level,
                    exp,
                    Optional.of((long) player.foodLevel().get()),
                    Optional.empty()
            ));
        }
        return players;
    }

    private List<ServerPlugin> getPlugins() {
        List<ServerPlugin> plugins = new ArrayList<>();
        for (PluginContainer plugin : Sponge.getPluginManager().getPlugins()) {
            plugins.add(new ServerPlugin(
                    plugin.getName(),
                    plugin.getVersion().orElse("Unknown"),
                    plugin.getDescription().orElse("Unknown"),
                    String.join(", ", plugin.getAuthors()),
                    Optional.ofNullable(plugin.getUrl().orElse(null))
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
                player.getConnection().getAddress().getAddress().getHostAddress()
        );
    }
}