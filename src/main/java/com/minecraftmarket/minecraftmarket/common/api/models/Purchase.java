package com.minecraftmarket.minecraftmarket.common.api.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Purchase {
    private final long id;
    private final String name;
    private final String price;
    private final Currency currency;
    private final String date;
    private final MMPlayer player;

    @JsonCreator
    public Purchase(@JsonProperty("id") long id,
                    @JsonProperty("name") String name,
                    @JsonProperty("price") String price,
                    @JsonProperty("currency") Currency currency,
                    @JsonProperty("date") String date,
                    @JsonProperty("player") MMPlayer player) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.currency = currency;
        this.date = date;
        this.player = player;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public Currency getCurrency() {
        return currency;
    }

    public String getDate() {
        return date;
    }

    public MMPlayer getPlayer() {
        return player;
    }

    @Override
    public String toString() {
        return "ID='" + getId() + "' " +
                "Name='" + getName() + "' " +
                "Price='" + getPrice() + "' " +
                "Currency='" + getCurrency() + "' " +
                "Date='" + getDate() + "' " +
                "Player='" + getPlayer() + "'";
    }
}