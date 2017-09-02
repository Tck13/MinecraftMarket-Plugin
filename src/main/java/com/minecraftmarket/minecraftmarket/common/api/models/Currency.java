package com.minecraftmarket.minecraftmarket.common.api.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Currency {
    private final long id;
    private final String code;

    @JsonCreator
    public Currency(@JsonProperty("id") long id,
                    @JsonProperty("code") String code) {
        this.id = id;
        this.code = code;
    }

    public long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "ID='" + getId() + "' " +
                "Code='" + getCode() + "'";
    }
}