package com.minecraftmarket.minecraftmarket.common.api.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerSession {
    private final long id;
    private final String username;
    private final String uuid;
    private final long joined;
    private final long quit;
    private final String ip;

    @JsonCreator
    public PlayerSession(@JsonProperty("id") long id,
                         @JsonProperty("username") String username,
                         @JsonProperty("uuid") String uuid,
                         @JsonProperty("joined") long joined,
                         @JsonProperty("quit") long quit,
                         @JsonProperty("ip") String ip) {
        this.id = id;
        this.username = username;
        this.uuid = uuid;
        this.joined = joined;
        this.quit = quit;
        this.ip = ip;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getUuid() {
        return uuid;
    }

    public long getJoined() {
        return joined;
    }

    public long getQuit() {
        return quit;
    }

    public String getIp() {
        return ip;
    }

    @Override
    public String toString() {
        return "ID='" + getId() + "' " +
                "Username='" + getUsername() + "' " +
                "Uuid='" + getUuid() + "' " +
                "Joined='" + getJoined() + "' " +
                "Quit='" + getQuit() + "' " +
                "Ip='" + getIp() + "'";
    }
}