package com.minecraftmarket.minecraftmarket.common.api.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ServerPlugin {
    private final String name;
    private final String version;
    private final String description;
    private final String authors;
    private final Optional<String> website;

    @JsonCreator
    public ServerPlugin(@JsonProperty("name") String name,
                        @JsonProperty("version") String version,
                        @JsonProperty("description") String description,
                        @JsonProperty("authors") String authors,
                        @JsonProperty("website") Optional<String> website) {
        this.name = name;
        this.version = version;
        this.description = description;
        this.authors = authors;
        this.website = website;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthors() {
        return authors;
    }

    public Optional<String> getWebsite() {
        return website;
    }

    @Override
    public String toString() {
        return "Name='" + getName() + "' " +
                "Version='" + getVersion() + "' " +
                "Description='" + getDescription() + "' " +
                "Authors='" + getAuthors() + "' " +
                "Website='" + getWebsite() + "'";
    }
}