package com.minecraftmarket.minecraftmarket.common.stats;

import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;
import com.minecraftmarket.minecraftmarket.common.api.models.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class MCMarketStats {
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

    final List<Event> events = new ArrayList<>();
    private int cycles = 1;
    void runEventsSender() {
        events.add(new Event(0, "server_info", getServerData()));
        if (cycles >= 5) {
            new Thread(() -> {
                if (marketApi.sendEvents(events)) {
                    events.clear();
                }
            }).start();
            cycles = 1;
        } else {
            cycles++;
        }
    }
}