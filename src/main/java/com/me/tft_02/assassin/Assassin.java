package com.me.tft_02.assassin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.me.tft_02.assassin.commands.AssassinCommand;
import com.me.tft_02.assassin.config.Config;
import com.me.tft_02.assassin.database.DatabaseManager;
import com.me.tft_02.assassin.database.DatabaseManagerFactory;
import com.me.tft_02.assassin.hooks.TagListener;
import com.me.tft_02.assassin.items.Mask;
import com.me.tft_02.assassin.listeners.ChatListener;
import com.me.tft_02.assassin.listeners.EntityListener;
import com.me.tft_02.assassin.listeners.InventoryListener;
import com.me.tft_02.assassin.listeners.PlayerListener;
import com.me.tft_02.assassin.locale.LocaleLoader;
import com.me.tft_02.assassin.runnables.database.SaveTimerTask;
import com.me.tft_02.assassin.runnables.player.ActivityTimerTask;
import com.me.tft_02.assassin.runnables.player.RangeCheckTask;
import com.me.tft_02.assassin.util.LogFilter;
import com.me.tft_02.assassin.util.Misc;
import com.me.tft_02.assassin.util.player.UserManager;

import net.gravitydevelopment.updater.assassin.Updater;
import net.milkbowl.vault.economy.Economy;
import org.mcstats.Metrics;

public class Assassin extends JavaPlugin {
    private static DatabaseManager databaseManager;

    /* File Paths */
    private static String mainDirectory;
    private static String flatFileDirectory;
    private static String usersFile;

    public static Assassin p;

    // Jar Stuff
    public static File assassin;

    public boolean vaultEnabled;

    // Update Check
    private boolean updateAvailable;

    private boolean tagApiEnabled;

    public static Economy econ = null;

    /* Metadata Values */
    public final static String playerDataKey = "Assassin: Player Data";

    public static FixedMetadataValue metadataValue;

    /**
     * Run things on enable.
     */
    @Override
    public void onEnable() {
        p = this;
        getLogger().setFilter(new LogFilter(this));
        metadataValue = new FixedMetadataValue(this, true);

        setupTagAPI();

        if (!tagApiEnabled) {
            return;
        }

        vaultEnabled = setupEconomy();

        setupFilePaths();
        Config.getInstance();
        checkConfiguration();

        databaseManager = DatabaseManagerFactory.getDatabaseManager();

        addCustomRecipes();

        registerEvents();
        registerCommands();

        for (Player player : getServer().getOnlinePlayers()) {
            UserManager.addUser(player); // In case of reload add all users back into UserManager
        }

        setupMetrics();
        scheduleTasks();

        checkForUpdates();
    }

    private void setupMetrics() {
        if (Config.getInstance().getStatsTrackingEnabled()) {
            try {
                Metrics metrics = new Metrics(this);
                metrics.start();
            }
            catch (IOException ignored) {
            }
        }
    }

    private void registerCommands() {
        getCommand("assassin").setExecutor(new AssassinCommand());
    }

    private void setupTagAPI() {
        PluginManager pm = getServer().getPluginManager();
        if (pm.getPlugin("TagAPI") == null) {
            getLogger().log(Level.WARNING, "No TagAPI dependency found!");
            getLogger().log(Level.WARNING, "Download TagAPI from http://dev.bukkit.org/server-mods/tag/");
            pm.disablePlugin(this);
            tagApiEnabled = false;
            return;
        }
        else if (!pm.isPluginEnabled("TagAPI")) {
            getLogger().log(Level.WARNING, "TagAPI is probably outdated, check the console log.");
            pm.disablePlugin(this);
            tagApiEnabled = false;
            return;
        }

        tagApiEnabled = true;
    }

    private void registerEvents() {
        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new ChatListener(), this);
        pluginManager.registerEvents(new EntityListener(), this);
        pluginManager.registerEvents(new InventoryListener(), this);
        pluginManager.registerEvents(new PlayerListener(), this);
        pluginManager.registerEvents(new TagListener(), this);
    }

    public void debug(String message) {
        getLogger().info("[Debug] " + message);
    }

    private void checkForUpdates() {
        if (!Config.getInstance().getUpdateCheckEnabled()) {
            return;
        }

        Updater updater = new Updater(this, 47673, assassin, Updater.UpdateType.NO_DOWNLOAD, false);

        if (updater.getResult() != Updater.UpdateResult.UPDATE_AVAILABLE) {
            this.updateAvailable = false;
            return;
        }

        if (updater.getLatestType().equals("beta") && !Config.getInstance().getPreferBeta()) {
            this.updateAvailable = false;
            return;
        }

        this.updateAvailable = true;
        getLogger().info(LocaleLoader.getString("UpdateChecker.Outdated"));
        getLogger().info(LocaleLoader.getString("UpdateChecker.New_Available"));
    }

    private void addCustomRecipes() {
        getServer().addRecipe(Mask.getRecipe());
    }

    private void checkConfiguration() {
        if (Config.getInstance().getActivationCost() > 0 && !vaultEnabled) {
            getLogger().log(Level.WARNING, "Vault dependency needed if you want to use currency!");
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    /**
     * Run things on disable.
     */
    @Override
    public void onDisable() {
        try {
            UserManager.saveAll();
        }
        catch (NullPointerException ignored) {
        }

        getServer().getScheduler().cancelTasks(this);
        HandlerList.unregisterAll(this);

        debug("Was disabled.");
    }

    public static DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    @Deprecated
    public static void setDatabaseManager(DatabaseManager databaseManager) {
        Assassin.databaseManager = databaseManager;
    }

    public static String getMainDirectory() {
        return mainDirectory;
    }

    public static String getFlatFileDirectory() {
        return flatFileDirectory;
    }

    public static String getUsersFilePath() {
        return usersFile;
    }

    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    /**
     * Setup the various storage file paths
     */
    private void setupFilePaths() {
        assassin = getFile();
        mainDirectory = getDataFolder().getPath() + File.separator;
        flatFileDirectory = mainDirectory + "flatfile" + File.separator;
        usersFile = flatFileDirectory + "assassin.users";
        fixFilePaths();
    }

    private void fixFilePaths() {
        File oldFlatfilePath = new File(mainDirectory + "FlatFileStuff" + File.separator);

        if (oldFlatfilePath.exists()) {
            oldFlatfilePath.renameTo(new File(flatFileDirectory));
        }
    }

    private void scheduleTasks() {
        // Periodic save timer (Saves every 15 minutes by default)
        long saveIntervalTicks = Config.getInstance().getSaveInterval() * 1200;
        new SaveTimerTask().runTaskTimer(this, saveIntervalTicks, saveIntervalTicks);

        // Range check timer (Runs every 10 seconds)
        if (Config.getInstance().getWarnWhenNear()) {
            new RangeCheckTask().runTaskTimer(this, 10 * Misc.TICK_CONVERSION_FACTOR, 10 * Misc.TICK_CONVERSION_FACTOR);
        }

        // Activity timer task (Runs every two seconds)
        new ActivityTimerTask().runTaskTimer(this, 2 * Misc.TICK_CONVERSION_FACTOR, 2 * Misc.TICK_CONVERSION_FACTOR);
    }
}
