package com.minecraftmarket.minecraftmarket.common.stats.models;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Map;

@JsonPropertyOrder({"type", "data"})
public class StatsEvent {
    public final String type;
    public final Map<String, Object> data;

    public StatsEvent(String type, Map<String, Object> data) {
        this.type = type;
        this.data = data;
    }
}