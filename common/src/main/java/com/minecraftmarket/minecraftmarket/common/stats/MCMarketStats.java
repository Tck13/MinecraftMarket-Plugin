package com.minecraftmarket.minecraftmarket.common.stats;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;
import com.minecraftmarket.minecraftmarket.common.stats.models.StatsEvent;

import java.util.*;

abstract class MCMarketStats {
    private final ObjectMapper OBJECTMAPPER = new ObjectMapper();
    private final MCMarketApi marketApi;

    MCMarketStats(MCMarketApi marketApi) {
        this.marketApi = marketApi;
    }

    Map<String, Object> getServerData() {
        Map<String, Object> data = new HashMap<>();
        data.put("time", getTime());
        data.put("java_version", System.getProperty("java.version"));
        data.put("os_name", System.getProperty("os.name"));
        data.put("os_arch", System.getProperty("os.arch"));
        data.put("os_version", System.getProperty("os.version"));
        data.put("max_memory", Runtime.getRuntime().maxMemory());
        data.put("total_memory", Runtime.getRuntime().totalMemory());
        data.put("free_memory", Runtime.getRuntime().freeMemory());
        data.put("processors", Runtime.getRuntime().availableProcessors());
        return data;
    }

    long getTime() {
        return Calendar.getInstance().getTimeInMillis() / 1000;
    }

    final List<StatsEvent> events = new ArrayList<>();
    class DataTask implements Runnable {
        private int cycles = 0;

        @Override
        public void run() {
            events.add(new StatsEvent("server_info", getServerData()));
            if (cycles >= 0) {
                if (sendData()) {
                    events.clear();
                }
                cycles = 0;
            } else {
                cycles++;
            }
        }
    }

    private boolean sendData() {
        try {
            return marketApi.sendEvents(OBJECTMAPPER.writeValueAsString(events));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return false;
    }
}