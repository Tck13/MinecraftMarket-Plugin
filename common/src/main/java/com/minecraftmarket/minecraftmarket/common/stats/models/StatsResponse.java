package com.minecraftmarket.minecraftmarket.common.stats.models;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({"server", "events"})
public class StatsResponse {
    public final List<StatsEvent> events;

    public StatsResponse(List<StatsEvent> events) {
        this.events = events;
    }
}