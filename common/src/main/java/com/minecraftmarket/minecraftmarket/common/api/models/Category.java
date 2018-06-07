package com.minecraftmarket.minecraftmarket.common.api.models;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Category {
    private final long id;
    private final String name;
    private final String description;
    private final String icon;
    private final List<Category> subCategories;
    private final List<Item> items;
    private final long order;

    @JsonCreator
    public Category(@JsonProperty("id") long id,
                    @JsonProperty("name") String name,
                    @JsonProperty("gui_description") String description,
                    @JsonProperty("gui_icon") String icon,
                    @JsonProperty("subcategories") List<Category> subCategories,
                    @JsonProperty("items") List<Item> items,
                    @JsonProperty("order") long order) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.subCategories = subCategories;
        this.items = items;
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

    public List<Category> getSubCategories() {
        return subCategories;
    }

    public List<Item> getItems() {
        return items;
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
                "SubCategories='" + Arrays.toString(getSubCategories().toArray()) + "' " +
                "Items='" + Arrays.toString(getItems().toArray()) + "' " +
                "Order='" + getOrder() + "'";
    }
}