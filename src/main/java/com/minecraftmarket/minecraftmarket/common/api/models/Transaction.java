package com.minecraftmarket.minecraftmarket.common.api.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.List;

public class Transaction {
    private final long id;
    private final String status;
    private final String gateway;
    private final String transactionID;
    private final String price;
    private final Currency currency;
    private final String date;
    private final MMPlayer player;
    private final List<Purchase> purchases;

    @JsonCreator
    public Transaction(@JsonProperty("id") long id,
                       @JsonProperty("status") String status,
                       @JsonProperty("gateway") String gateway,
                       @JsonProperty("transaction_id") String transactionID,
                       @JsonProperty("price") String price,
                       @JsonProperty("currency") Currency currency,
                       @JsonProperty("date") String date,
                       @JsonProperty("player") MMPlayer player,
                       @JsonProperty("purchases") List<Purchase> purchases) {
        this.id = id;
        this.status = status;
        this.gateway = gateway;
        this.transactionID = transactionID;
        this.price = price;
        this.currency = currency;
        this.date = date;
        this.player = player;
        this.purchases = purchases;
    }

    public long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getGateway() {
        return gateway;
    }

    public String getTransactionID() {
        return transactionID;
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

    public List<Purchase> getPurchases() {
        return purchases;
    }

    @Override
    public String toString() {
        return "ID='" + getId() + "' " +
                "Status='" + getStatus() + "' " +
                "Gateway='" + getGateway() + "' " +
                "TransactionID='" + getTransactionID() + "' " +
                "Price='" + getPrice() + "' " +
                "Currency='" + getCurrency() + "' " +
                "Date='" + getDate() + "' " +
                "Player='" + getPlayer() + "' " +
                "Purchases='" + Arrays.toString(getPurchases().toArray()) + "'";
    }
}