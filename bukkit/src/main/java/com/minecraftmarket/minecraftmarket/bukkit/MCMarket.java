package com.minecraftmarket.minecraftmarket.bukkit;

import java.io.File;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import com.minecraftmarket.minecraftmarket.bukkit.commands.MMCmd;
import com.minecraftmarket.minecraftmarket.bukkit.commands.MMGui;
import com.minecraftmarket.minecraftmarket.bukkit.configs.GUILayoutConfig;
import com.minecraftmarket.minecraftmarket.bukkit.configs.MainConfig;
import com.minecraftmarket.minecraftmarket.bukkit.configs.SignsConfig;
import com.minecraftmarket.minecraftmarket.bukkit.configs.SignsLayoutConfig;
import com.minecraftmarket.minecraftmarket.bukkit.inventory.InventoryManager;
import com.minecraftmarket.minecraftmarket.bukkit.listeners.ShopCmdListener;
import com.minecraftmarket.minecraftmarket.bukkit.listeners.SignsListener;
import com.minecraftmarket.minecraftmarket.bukkit.tasks.GUIupdateTask;
import com.minecraftmarket.minecraftmarket.bukkit.tasks.PurchasesTask;
import com.minecraftmarket.minecraftmarket.bukkit.tasks.SignsTask;
import com.minecraftmarket.minecraftmarket.bukkit.utils.inventories.InventoryGUI;
import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;
import com.minecraftmarket.minecraftmarket.common.i18n.I18n;
import com.minecraftmarket.minecraftmarket.common.stats.BukkitStats;
import com.minecraftmarket.minecraftmarket.common.updater.UpdateChecker;
import com.minecraftmarket.minecraftmarket.common.utils.FileUtils;

public final class MCMarket extends JavaPlugin {
    private I18n i18n;
    private MainConfig mainConfig;
    private SignsConfig signsConfig;
    private GUILayoutConfig guiLayoutConfig;
    private SignsLayoutConfig signsLayoutConfig;
    private static MCMarketApi marketApi;
    private static boolean authenticated;
    private InventoryManager inventoryManager;
    private SignsTask signsTask;
    private PurchasesTask purchasesTask;
    private GUIupdateTask GUIupdateTask;
    
    @Override
    public void onEnable() {
        i18n = new I18n(getLanguageFolder(), getLogger());
        i18n.onEnable();

        reloadConfigs(null);

        getCommand("MinecraftMarket").setExecutor(new MMCmd(this));
        getCommand("MMGui").setExecutor(new MMGui(this));

        getServer().getScheduler().runTaskAsynchronously(this, () -> new UpdateChecker(getDescription().getVersion(), 44031, pluginURL -> {
            getLogger().warning(I18n.tl("new_version"));
            getLogger().warning(pluginURL);
        }));
    }

    @Override
    public void onDisable() { 	
        HandlerList.unregisterAll(this);
        getServer().getScheduler().cancelTasks(this);
        i18n.onDisable();
    }

    public void reloadConfigs(Response<Boolean> response) {
        mainConfig = new MainConfig(this);
        signsConfig = new SignsConfig(this);
        guiLayoutConfig = new GUILayoutConfig(this);
        signsLayoutConfig = new SignsLayoutConfig(this);

        i18n.updateLocale(mainConfig.getLang());

        HandlerList.unregisterAll(this);
        getServer().getScheduler().cancelTasks(this);

        setKey(mainConfig.getApiKey(), false, result -> {
            if (mainConfig.isUseGUI()) {
                getServer().getPluginManager().registerEvents(new ShopCmdListener(this), this);
            }
            if (inventoryManager == null) {
                inventoryManager = new InventoryManager(this);
            }
            if (GUIupdateTask == null) {
            	GUIupdateTask = new GUIupdateTask(this);
            }
            getServer().getScheduler().runTaskTimer(this, GUIupdateTask, 20 * 10, mainConfig.getCheckInterval() > 0 ? 20 * 60 * mainConfig.getCheckInterval() : 20 * 60);
            getServer().getPluginManager().registerEvents(InventoryGUI.getListener(), this);
            
            if (mainConfig.isUseSigns()) {
                if (signsTask == null) {
                    signsTask = new SignsTask(this);
                }
                getServer().getScheduler().runTaskTimer(this, signsTask, 20 * 10, mainConfig.getCheckInterval() > 0 ? 20 * 60 * mainConfig.getCheckInterval() : 20 * 60);
                getServer().getPluginManager().registerEvents(new SignsListener(this), this);
            }

            if (purchasesTask == null) {
                purchasesTask = new PurchasesTask(this);
            }
            getServer().getScheduler().runTaskTimerAsynchronously(this, purchasesTask, 20 * 10, mainConfig.getCheckInterval() > 0 ? 20 * 60 * mainConfig.getCheckInterval() : 20 * 60);

            if (result && mainConfig.isStatistics()) {
                new BukkitStats(marketApi, this);
            }

            if (response != null) {
                response.done(result);
            }
        });
    }

    public void setKey(String apiKey, boolean save, Response<Boolean> response) {
        if (save) {
            mainConfig.setApiKey(apiKey);
        }
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            marketApi = new MCMarketApi(apiKey, getUserAgent(), mainConfig.isDebug());
            authenticated = marketApi.authAPI();
            if (!authenticated) {
                getLogger().warning(I18n.tl("invalid_key", "/MM apiKey <key>"));
            }
            if (inventoryManager != null) {
                inventoryManager.load();
            }
            if (response != null) {
                response.done(authenticated);
            }
        });
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }

    public SignsConfig getSignsConfig() {
        return signsConfig;
    }

    public GUILayoutConfig getGUILayoutConfig() {
        return guiLayoutConfig;
    }

    public SignsLayoutConfig getSignsLayoutConfig() {
        return signsLayoutConfig;
    }

    public static MCMarketApi getApi() {
        return marketApi;
    }

    public static boolean isAuthenticated() {
        return authenticated;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public SignsTask getSignsTask() {
        return signsTask;
    }

    public PurchasesTask getPurchasesTask() {
        return purchasesTask;
    }

    public interface Response<T> {
        void done(T t);
    }

    private File getLanguageFolder() {
        File langFolder = new File(getDataFolder(), "langs");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }
        for (String file : FileUtils.getJarResources(getClass())) {
            if (file.startsWith("langs/") && file.endsWith(".properties")) {
                File langFile = new File(getDataFolder(), file);
                if (!langFile.exists()) {
                    saveResource(file, false);
                }
            }
        }
        return langFolder;
    }

    private String getUserAgent() {
        return getDescription().getName() + "-v" + getDescription().getVersion() + "-BUKKIT";
    }
}