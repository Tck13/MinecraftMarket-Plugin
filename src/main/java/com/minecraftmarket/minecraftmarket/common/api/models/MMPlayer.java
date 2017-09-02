package com.minecraftmarket.minecraftmarket.common.api.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MMPlayer {
    private final long id;
    private final String name;
    private final String uuid;
    private final boolean verified;
    private final String skinUrl;
    private final String skinSource;
    private final String capeUrl;
    private final String capeSource;

    @JsonCreator
    public MMPlayer(@JsonProperty("id") long id,
                    @JsonProperty("username") String name,
                    @JsonProperty("uuid") String uuid,
                    @JsonProperty("verified") boolean verified,
                    @JsonProperty("skin_url") String skinUrl,
                    @JsonProperty("skin_source") String skinSource,
                    @JsonProperty("cape_url") String capeUrl,
                    @JsonProperty("cape_source") String capeSource) {
        this.id = id;
        this.name = name;
        this.uuid = uuid;
        this.verified = verified;
        this.skinUrl = skinUrl;
        this.skinSource = skinSource;
        this.capeUrl = capeUrl;
        this.capeSource = capeSource;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public boolean isVerified() {
        return verified;
    }

    public String getSkinUrl() {
        return skinUrl;
    }

    public String getSkinSource() {
        return skinSource;
    }

    public String getCapeUrl() {
        return capeUrl;
    }

    public String getCapeSource() {
        return capeSource;
    }

    @Override
    public String toString() {
        return "ID='" + getId() + "' " +
                "Name='" + getName() + "' " +
                "Uuid='" + getUuid() + "' " +
                "Verified='" + isVerified() + "' " +
                "SkinUrl='" + getSkinUrl() + "' " +
                "SkinSource='" + getSkinSource() + "' " +
                "CapeUrl='" + getCapeUrl() + "' " +
                "CapeSource='" + getCapeSource() + "'";
    }
}