package com.minecraftmarket.minecraftmarket.common.api.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Item {
    private final long id;
    private final String name;
    private final String description;
    private final String icon;
    private final String url;
    private final String price;
    private final String sale_price;
    private final long order;

    @JsonCreator
    public Item(@JsonProperty("id") long id,
                @JsonProperty("name") String name,
                @JsonProperty("gui_description") String description,
                @JsonProperty("gui_icon") String icon,
                @JsonProperty("gui_url") String url,
                @JsonProperty("price") String price,
                @JsonProperty("sale_price") String sale_price,
                @JsonProperty("order") long order) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.url = url;
        this.price = price;
        this.sale_price = sale_price;
        this.order = order;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public String getUrl() {
        return url;
    }

    public String getPrice() {
        return price;
    }
    
    public String getSalePrice() {
        return sale_price;
    }

    public boolean hasSale(){
    	return !price.equals(sale_price);
    }
    
    public long getOrder() {
        return order;
    }

    @Override
    public String toString() {
        return "ID='" + getId() + "' " +
                "Name='" + getName() + "' " +
                "Description='" + getDescription() + "' " +
                "Icon='" + getIcon() + "' " +
                "Url='" + getUrl() + "' " +
                "Price='" + getPrice() + "' " +
                "Order='" + getOrder() + "'";
    }
}