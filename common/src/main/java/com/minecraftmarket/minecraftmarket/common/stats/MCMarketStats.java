package com.minecraftmarket.minecraftmarket.common.stats;

import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;
import com.minecraftmarket.minecraftmarket.common.api.models.PlayerSession;
import com.minecraftmarket.minecraftmarket.common.api.models.ServerInfo;
import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

abstract class MCMarketStats {
    private final MCMarketApi marketApi;

    MCMarketStats(MCMarketApi marketApi) {
        this.marketApi = marketApi;
    }

    abstract ServerInfo getServerInfo();

    long getTime() {
        return Calendar.getInstance().getTimeInMillis() / 1000;
    }

    Map<String, Object> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        double cpuUsage = osBean.getProcessCpuLoad() < 0 ? 0 : osBean.getProcessCpuLoad() * 100.0;

        stats.put("javaVersion", System.getProperty("java.version"));
        stats.put("osName", System.getProperty("os.name"));
        stats.put("osArch", System.getProperty("os.arch"));
        stats.put("osVersion", System.getProperty("os.version"));
        stats.put("maxMemory", Runtime.getRuntime().maxMemory());
        stats.put("totalMemory", Runtime.getRuntime().totalMemory());
        stats.put("freeMemory", Runtime.getRuntime().freeMemory());
        stats.put("cores", Long.parseLong(String.valueOf(Runtime.getRuntime().availableProcessors()))); // Is this really necessary?
        stats.put("cpuUsage", Math.min(Math.round(cpuUsage * 100.0) / 100.0, 100.0));

        return stats;
    }

    private final List<ServerInfo> serverInfo = new ArrayList<>();
    final Map<UUID, Long> playerJoins = new HashMap<>();
    final List<PlayerSession> playerSession = new ArrayList<>();

    private int cycles = 1;
    private int sessionFails = 1;
    private int serverInfoFails = 1;
    void runEventsSender() {
        serverInfo.add(getServerInfo());
        if (cycles >= 5) {
            new Thread(() -> {
                if (playerSession.size() > 0) {
                    if (marketApi.sendPlayerSessions(playerSession) || sessionFails >= 3) {
                        playerSession.clear();
                        sessionFails = 1;
                    } else {
                        sessionFails++;
                    }
                }

                if (marketApi.sendServerInformation(serverInfo) || serverInfoFails >= 3) {
                    serverInfo.clear();
                    serverInfoFails = 1;
                } else {
                    serverInfoFails++;
                }
            }).start();
            cycles = 1;
        } else {
            cycles++;
        }
    }
}