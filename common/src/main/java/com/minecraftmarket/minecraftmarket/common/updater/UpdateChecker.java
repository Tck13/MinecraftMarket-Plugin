package com.minecraftmarket.minecraftmarket.common.updater;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minecraftmarket.minecraftmarket.common.utils.Utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

public class UpdateChecker {
    private final String VERSION_URL = "https://api.spiget.org/v2/resources/%s/versions?sort=-name";
    private final String SPIGOT_URL = "https://www.spigotmc.org/resources/%s";
    private final ObjectMapper MAPPER = new ObjectMapper();

    public UpdateChecker(String currentVersion, int pluginID, UpdateCallback callback) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(String.format(VERSION_URL, pluginID)).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("User-Agent", "MinecraftMarket");
            conn.setUseCaches(false);
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);

            BufferedReader reader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            Iterator<JsonNode> results = MAPPER.readTree(reader).elements();
            if (results.hasNext()) {
                String lastVersion = results.next().get("name").asText().replace(".", "");
                currentVersion = currentVersion.replace(".", "");
                if (Utils.isInt(currentVersion) && Utils.isInt(lastVersion)) {
                    if (Utils.getInt(currentVersion) < Utils.getInt(lastVersion)) {
                        callback.newVersion(String.format(SPIGOT_URL, pluginID));
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    public interface UpdateCallback {
        void newVersion(String pluginURL);
    }
}