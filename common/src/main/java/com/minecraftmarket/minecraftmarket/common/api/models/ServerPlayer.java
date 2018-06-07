package com.minecraftmarket.minecraftmarket.common.api.models;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ServerPlayer {
    private final long id;
    private final String username;
    private final String uuid;
    private final String ip;
    private final long ping;
    private final Optional<Boolean> op;
    private final Optional<String> world;
    private final Optional<Long> x;
    private final Optional<Long> y;
    private final Optional<Long> z;
    private final Optional<String> gamemode;
    private final Optional<Double> health;
    private final Optional<Double> max_health;
    private final Optional<Long> level;
    private final Optional<Double> exp;
    private final Optional<Long> food;
    private final Optional<String> server;
    private final Optional<String> ip_country;

    @JsonCreator
    public ServerPlayer(@JsonProperty("id") long id,
                        @JsonProperty("username") String username,
                        @JsonProperty("uuid") String uuid,
                        @JsonProperty("ip") String ip,
                        @JsonProperty("ping") long ping,
                        @JsonProperty("op") Optional<Boolean> op,
                        @JsonProperty("world") Optional<String> world,
                        @JsonProperty("x") Optional<Long> x,
                        @JsonProperty("y") Optional<Long> y,
                        @JsonProperty("z") Optional<Long> z,
                        @JsonProperty("gamemode") Optional<String> gamemode,
                        @JsonProperty("health") Optional<Double> health,
                        @JsonProperty("max_health") Optional<Double> max_health,
                        @JsonProperty("level") Optional<Long> level,
                        @JsonProperty("exp") Optional<Double> exp,
                        @JsonProperty("food") Optional<Long> food,
                        @JsonProperty("server") Optional<String> server,
                        @JsonProperty("ip_country") Optional<String> ip_country) {
        this.id = id;
        this.username = username;
        this.uuid = uuid;
        this.ip = ip;
        this.ping = ping;
        this.op = op;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.gamemode = gamemode;
        this.health = health;
        this.max_health = max_health;
        this.level = level;
        this.exp = exp;
        this.food = food;
        this.server = server;
        this.ip_country = ip_country;
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

    public String getIp() {
        return ip;
    }

    public long getPing() {
        return ping;
    }

    public Optional<Boolean> getOp() {
        return op;
    }

    public Optional<String> getWorld() {
        return world;
    }

    public Optional<Long> getX() {
        return x;
    }

    public Optional<Long> getY() {
        return y;
    }

    public Optional<Long> getZ() {
        return z;
    }

    public Optional<String> getGamemode() {
        return gamemode;
    }

    public Optional<Double> getHealth() {
        return health;
    }

    public Optional<Double> getMax_health() {
        return max_health;
    }

    public Optional<Long> getLevel() {
        return level;
    }

    public Optional<Double> getExp() {
        return exp;
    }

    public Optional<Long> getFood() {
        return food;
    }

    public Optional<String> getServer() {
        return server;
    }

    public Optional<String> getIp_country() {
        return ip_country;
    }

    @Override
    public String toString() {
        return "ID='" + getId() + "' " +
                "Username='" + getUsername() + "' " +
                "Uuid='" + getUuid() + "' " +
                "Ip='" + getIp() + "' " +
                "Ping='" + getPing() + "' " +
                "Op='" + getOp() + "' " +
                "World='" + getWorld() + "' " +
                "X='" + getX() + "' " +
                "Y='" + getY() + "' " +
                "Z='" + getZ() + "' " +
                "Gamemode='" + getGamemode() + "' " +
                "Health='" + getHealth() + "' " +
                "MaxHealth='" + getMax_health() + "' " +
                "Level='" + getLevel() + "' " +
                "Exp='" + getExp() + "' " +
                "Food='" + getFood() + "' " +
                "Server='" + getServer() + "' " +
                "IpCoutry='" + getIp_country() + "'";
    }
}