package com.minecraftmarket.minecraftmarket.common.stats;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;
import com.minecraftmarket.minecraftmarket.common.stats.models.StatsResponse;

import java.util.Calendar;

abstract class MMStats {
    private final ObjectMapper OBJECTMAPPER = new ObjectMapper();
    private final MCMarketApi marketApi;

    MMStats(MCMarketApi marketApi) {
        this.marketApi = marketApi;
    }

    long getTime() {
        return Calendar.getInstance().getTimeInMillis();
    }

    boolean sendData(StatsResponse response) {
        try {
            return marketApi.sendEvents(OBJECTMAPPER.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return false;
    }
}