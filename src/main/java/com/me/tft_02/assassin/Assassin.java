package com.me.tft_02.assassin;

import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.me.tft_02.assassin.config.Config;
import com.me.tft_02.assassin.listeners.ChatListener;
import com.me.tft_02.assassin.listeners.EntityListener;
import com.me.tft_02.assassin.listeners.PlayerListener;
import com.me.tft_02.assassin.listeners.TagListener;
import com.me.tft_02.assassin.runnables.database.SaveTimerTask;
import com.me.tft_02.assassin.runnables.player.ActivityTimerTask;
import com.me.tft_02.assassin.runnables.player.RangeCheckTask;
import com.me.tft_02.assassin.util.Data;
import com.me.tft_02.assassin.util.LogFilter;
import com.me.tft_02.assassin.util.UpdateChecker;
import com.me.tft_02.assassin.util.player.UserManager;
import net.milkbowl.vault.economy.Economy;
import org.mcstats.Metrics;

public class Assassin extends JavaPlugin {
    public static Assassin p;

    private AssassinMode assassin = new AssassinMode();

    public boolean vaultEnabled;

    // Update Check
    public boolean updateAvailable;

    public static Economy econ = null;

    /**
     * Run things on enable.
     */
    @Override
    public void onEnable() {
        p = this;
        getLogger().setFilter(new LogFilter(this));

        setupTagAPI();
        vaultEnabled = setupEconomy();

        checkConfiguration();

        addCustomRecipes();

        registerEvents();
        registerCommands();

        Data.loadData();

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
        getCommand("assassin").setExecutor(new Commands());
    }

    private void setupTagAPI() {
        PluginManager pm = getServer().getPluginManager();
        if (pm.getPlugin("TagAPI") == null) {
            getLogger().log(Level.WARNING, "No TagAPI dependency found!");
            getLogger().log(Level.WARNING, "Download TagAPI from http://dev.bukkit.org/server-mods/tag/");
            pm.disablePlugin(this);
        }
        else if (!pm.isPluginEnabled("TagAPI")) {
            getLogger().log(Level.WARNING, "TagAPI is probably outdated, check the console log.");
            pm.disablePlugin(this);
        }
    }

    private void registerEvents() {
        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new ChatListener(), this);
        pluginManager.registerEvents(new EntityListener(), this);
        pluginManager.registerEvents(new PlayerListener(), this);
        pluginManager.registerEvents(new TagListener(), this);
    }

    public void debug(String message) {
        getLogger().info("[Debug] " + message);
    }

    private void checkForUpdates() {
        if (Config.getInstance().getUpdateCheckEnabled()) {
            try {
                updateAvailable = UpdateChecker.updateAvailable();
            }
            catch (Exception e) {
                updateAvailable = false;
            }

            if (updateAvailable) {
                getLogger().log(Level.INFO, "***********************************************************************************");
                getLogger().log(Level.INFO, "*                              Assassin is outdated!                              *");
                getLogger().log(Level.INFO, "* New version available on BukkitDev! http://dev.bukkit.org/server-mods/Assassin/ *");
                getLogger().log(Level.INFO, "***********************************************************************************");
            }
        }
    }

    private void addCustomRecipes() {
        MaterialData blackWool = new MaterialData(Material.WOOL, (byte) 15);
        ShapedRecipe AssassinMask = new ShapedRecipe(assassin.getMask(1, false));
        //        AssassinMask.shape(new String[] { "XXX", "X X" });
        AssassinMask.shape("XXX", "X X");
        AssassinMask.setIngredient('X', blackWool);
        getServer().addRecipe(AssassinMask);
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
        Data.saveData();
        getServer().getScheduler().cancelTasks(this);
    }

    private void scheduleTasks() {
        // Range check timer (Runs every 10 seconds)
        if (Config.getInstance().getWarnWhenNear()) {
            new RangeCheckTask().runTaskTimer(this, 10 * 20, 10 * 20);
        }

        // Activity timer task (Runs every two seconds)
        new ActivityTimerTask().runTaskTimer(this, 2 * 20, 2 * 20);

        // Periodic save timer (Saves every 15 minutes by default)
        long saveIntervalTicks = Config.getInstance().getSaveInterval() * 60 * 20;
        new SaveTimerTask().runTaskTimer(this, saveIntervalTicks, saveIntervalTicks);
    }
}
