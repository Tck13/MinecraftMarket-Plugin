package com.minecraftmarket.minecraftmarket.common.api.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ServerInfo {
    private final long id;
    private final long time;
    private final String type;
    private final String version;
    private final boolean online_mode;
    private final String ip;
    private final long port;
    private final double tps;
    private final String java_version;
    private final String os_name;
    private final String os_arch;
    private final String os_version;
    private final long max_memory;
    private final long total_memory;
    private final long free_memory;
    private final long cores;
    private final double cpu_usage;
    private final List<ServerPlayer> online_players;
    private final List<ServerPlugin> plugins;

    @JsonCreator
    public ServerInfo(@JsonProperty("id") long id,
                      @JsonProperty("time") long time,
                      @JsonProperty("type") String type,
                      @JsonProperty("version") String version,
                      @JsonProperty("online_mode") boolean online_mode,
                      @JsonProperty("ip") String ip,
                      @JsonProperty("port") long port,
                      @JsonProperty("tps") double tps,
                      @JsonProperty("java_version") String java_version,
                      @JsonProperty("os_name") String os_name,
                      @JsonProperty("os_arch") String os_arch,
                      @JsonProperty("os_version") String os_version,
                      @JsonProperty("max_memory") long max_memory,
                      @JsonProperty("total_memory") long total_memory,
                      @JsonProperty("free_memory") long free_memory,
                      @JsonProperty("cores") long cores,
                      @JsonProperty("cpu_usage") double cpu_usage,
                      @JsonProperty("online_players") List<ServerPlayer> online_players,
                      @JsonProperty("plugins") List<ServerPlugin> plugins) {
        this.id = id;
        this.time = time;
        this.type = type;
        this.version = version;
        this.online_mode = online_mode;
        this.ip = ip;
        this.port = port;
        this.tps = tps;
        this.java_version = java_version;
        this.os_name = os_name;
        this.os_arch = os_arch;
        this.os_version = os_version;
        this.max_memory = max_memory;
        this.total_memory = total_memory;
        this.free_memory = free_memory;
        this.cores = cores;
        this.cpu_usage = cpu_usage;
        this.online_players = online_players;
        this.plugins = plugins;
    }

    public long getId() {
        return id;
    }

    public long getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public String getVersion() {
        return version;
    }

    public boolean isOnline_mode() {
        return online_mode;
    }

    public String getIp() {
        return ip;
    }

    public long getPort() {
        return port;
    }

    public double getTps() {
        return tps;
    }

    public String getJava_version() {
        return java_version;
    }

    public String getOs_name() {
        return os_name;
    }

    public String getOs_arch() {
        return os_arch;
    }

    public String getOs_version() {
        return os_version;
    }

    public long getMax_memory() {
        return max_memory;
    }

    public long getTotal_memory() {
        return total_memory;
    }

    public long getFree_memory() {
        return free_memory;
    }

    public long getCores() {
        return cores;
    }

    public double getCpu_usage() {
        return cpu_usage;
    }

    public List<ServerPlayer> getOnline_players() {
        return online_players;
    }

    public List<ServerPlugin> getPlugins() {
        return plugins;
    }

    @Override
    public String toString() {
        return "ID='" + getId() + "' " +
                "Time='" + getTime() + "' " +
                "Type='" + getType() + "' " +
                "Version='" + getVersion() + "' " +
                "OnlineMode='" + isOnline_mode() + "' " +
                "Ip='" + getIp() + "' " +
                "Port='" + getPort() + "' " +
                "Tps='" + getTps() + "' " +
                "OsName='" + getOs_name() + "' " +
                "OsArch='" + getOs_arch() + "' " +
                "OsVersion='" + getOs_version() + "' " +
                "MaxMemory='" + getMax_memory() + "' " +
                "TotalMemory='" + getTotal_memory() + "' " +
                "FreeMemory='" + getFree_memory() + "' " +
                "Cores='" + getCores() + "' " +
                "CpuUsage='" + getCpu_usage() + "' " +
                "OnlinePlayers='" + Arrays.toString(getOnline_players().toArray()) + "' " +
                "Plugins='" + Arrays.toString(getPlugins().toArray()) + "'";
    }
}