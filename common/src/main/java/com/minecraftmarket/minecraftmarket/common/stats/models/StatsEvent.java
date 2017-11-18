package com.minecraftmarket.minecraftmarket.common.stats.models;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Map;

@JsonPropertyOrder({"type", "time", "data"})
public class StatsEvent {
    public final String type;
    public final long time;
    public final Map<String, Object> data;

    public StatsEvent(String type, long time, Map<String, Object> data) {
        this.type = type;
        this.time = time;
        this.data = data;
    }
}