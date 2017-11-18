package com.minecraftmarket.minecraftmarket.common.stats.models;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({"server", "events"})
public class StatsResponse {
    public final String server;
    public final List<StatsEvent> events;

    public StatsResponse(String server, List<StatsEvent> events) {
        this.server = server;
        this.events = events;
    }
}