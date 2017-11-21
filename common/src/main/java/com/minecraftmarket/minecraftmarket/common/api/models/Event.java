package com.minecraftmarket.minecraftmarket.common.api.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {
    private final long id;
    private final String type;
    private final Map<String, Object> data;

    @JsonCreator
    public Event(@JsonProperty("id") long id,
                 @JsonProperty("type") String type,
                 @JsonProperty("data") Map<String, Object> data) {
        this.id = id;
        this.type = type;
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Map<String, Object> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ID='" + getId() + "' " +
                "Type='" + getType() + "' " +
                "Data='" + getData() + "'";
    }
}