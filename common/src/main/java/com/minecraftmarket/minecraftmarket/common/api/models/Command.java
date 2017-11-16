package com.minecraftmarket.minecraftmarket.common.api.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Command {
    private final long id;
    private final MMPlayer player;
    private final String type;
    private final String typeName;
    private final String command;
    private final long delay;
    private final long requiredSlots;
    private final boolean requiredOnline;
    private final boolean repeat;
    private final long repeatPeriod;
    private final long repeatCycles;
    private final boolean executed;

    @JsonCreator
    public Command(@JsonProperty("id") long id,
                   @JsonProperty("player") MMPlayer player,
                   @JsonProperty("type") String type,
                   @JsonProperty("type_name") String typeName,
                   @JsonProperty("command") String command,
                   @JsonProperty("delay") long delay,
                   @JsonProperty("required_slots") long requiredSlots,
                   @JsonProperty("required_online") boolean requiredOnline,
                   @JsonProperty("repeat") boolean repeat,
                   @JsonProperty("repeat_period") long repeatPeriod,
                   @JsonProperty("repeat_cycles") long repeatCycles,
                   @JsonProperty("executed") boolean executed) {
        this.id = id;
        this.player = player;
        this.type = type;
        this.typeName = typeName;
        this.command = command;
        this.delay = delay;
        this.requiredSlots = requiredSlots;
        this.requiredOnline = requiredOnline;
        this.repeat = repeat;
        this.repeatPeriod = repeatPeriod;
        this.repeatCycles = repeatCycles;
        this.executed = executed;
    }

    public long getId() {
        return id;
    }

    public MMPlayer getPlayer() {
        return player;
    }

    public String getType() {
        return type;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getCommand() {
        return command;
    }

    public long getDelay() {
        return delay;
    }

    public long getRequiredSlots() {
        return requiredSlots;
    }

    public boolean isRequiredOnline() {
        return requiredOnline;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public long getRepeatPeriod() {
        return repeatPeriod;
    }

    public long getRepeatCycles() {
        return repeatCycles;
    }

    public boolean isExecuted() {
        return executed;
    }

    @Override
    public String toString() {
        return "ID='" + getId() + "' " +
                "Player='" + getPlayer() + "' " +
                "Type='" + getType() + "' " +
                "TypeName='" + getTypeName() + "' " +
                "Command='" + getCommand() + "' " +
                "Delay='" + getDelay() + "' " +
                "RequiredSlots='" + getRequiredSlots() + "' " +
                "RequiredOnline='" + isRequiredOnline() + "' " +
                "Repeat='" + isRepeat() + "' " +
                "RepeatPeriod='" + getRepeatPeriod() + "' " +
                "RepeatCycles='" + getRepeatCycles() + "' " +
                "Executed='" + isExecuted() + "'";
    }
}