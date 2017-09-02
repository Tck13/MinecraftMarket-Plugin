package com.minecraftmarket.minecraftmarket.common.api.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Market {
    private final long id;
    private final String name;
    private final Currency currency;
    private final String url;

    @JsonCreator
    public Market(@JsonProperty("id") long id,
                  @JsonProperty("name") String name,
                  @JsonProperty("currency") Currency currency,
                  @JsonProperty("url") String url) {
        this.id = id;
        this.name = name;
        this.currency = currency;
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Currency getCurrency() {
        return currency;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "ID='" + getId() + "' " +
                "Name='" + getName() + "' " +
                "Currency='" + getCurrency() + "' " +
                "Url='" + getUrl() + "'";
    }
}
