package com.minecraftmarket.minecraftmarket.common.api.models;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ServerPlugin {
    private final long id;
    private final String name;
    private final String version;
    private final Optional<String> description;
    private final Optional<String> authors;
    private final Optional<String> website;

    @JsonCreator
    public ServerPlugin(@JsonProperty("id") long id,
                        @JsonProperty("name") String name,
                        @JsonProperty("version") String version,
                        @JsonProperty("description") Optional<String> description,
                        @JsonProperty("authors") Optional<String> authors,
                        @JsonProperty("website") Optional<String> website) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.description = description;
        this.authors = authors;
        this.website = website;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public Optional<String> getDescription() {
        return description;
    }

    public Optional<String> getAuthors() {
        return authors;
    }

    public Optional<String> getWebsite() {
        return website;
    }

    @Override
    public String toString() {
        return "ID='" + getId() + "' " +
                "Name='" + getName() + "' " +
                "Version='" + getVersion() + "' " +
                "Description='" + getDescription() + "' " +
                "Authors='" + getAuthors() + "' " +
                "Website='" + getWebsite() + "'";
    }
}