package com.me.tft_02.assassin;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import com.me.tft_02.assassin.config.Config;
import com.me.tft_02.assassin.listeners.ChatListener;
import com.me.tft_02.assassin.listeners.EntityListener;
import com.me.tft_02.assassin.listeners.PlayerListener;
import com.me.tft_02.assassin.listeners.TagListener;
import com.me.tft_02.assassin.runnables.database.SaveTimerTask;
import com.me.tft_02.assassin.runnables.player.ActivityTimerTask;
import com.me.tft_02.assassin.runnables.player.RangeCheckTask;
import com.me.tft_02.assassin.util.Data;
import com.me.tft_02.assassin.util.UpdateChecker;
import com.me.tft_02.assassin.util.player.UserManager;

public class Assassin extends JavaPlugin {
    public static Assassin p;

    private AssassinMode assassin = new AssassinMode(this);

    public boolean vaultEnabled;
    public boolean debug_mode = false;

    // Update Check
    public boolean updateAvailable;

    public static Economy econ = null;

    /**
     * Run things on enable.
     */
    @Override
    public void onEnable() {
        p = this;

        if (getConfig().getBoolean("General.debug_mode_enabled")) {
            getLogger().log(Level.WARNING, "Debug mode is enabled, this is only for advanced users!");
            debug_mode = true;
        }

        setupTagAPI();
        vaultEnabled = setupEconomy();

        setupConfiguration();
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
        if (Config.getStatsTrackingEnabled()) {
            try {
                Metrics metrics = new Metrics(this);
                metrics.start();
            }
            catch (IOException e) {
            }
        }
    }

    private void registerCommands() {
        getCommand("assassin").setExecutor(new Commands(this));
    }

    private void setupTagAPI() {
        PluginManager pm = getServer().getPluginManager();
        if (pm.getPlugin("TagAPI") == null) {
            getLogger().log(Level.WARNING, "No TagAPI dependency found!");
            getLogger().log(Level.WARNING, "Download TagAPI from http://dev.bukkit.org/server-mods/tag/");
            pm.disablePlugin(this);
            return;
        }
        else if (!pm.isPluginEnabled("TagAPI")) {
            getLogger().log(Level.WARNING, "TagAPI is probably outdated, check the console log.");
            pm.disablePlugin(this);
            return;
        }
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new TagListener(this), this);
        pm.registerEvents(new EntityListener(this), this);
        pm.registerEvents(new PlayerListener(this), this);
        pm.registerEvents(new ChatListener(this), this);
        //		pm.registerEvents(blockListener, this);
    }

    private void checkForUpdates() {
        if (Config.getUpdateCheckEnabled()) {
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
        ShapedRecipe AssassinMask = new ShapedRecipe(assassin.getMask(1));
        //        AssassinMask.shape(new String[] { "XXX", "X X" });
        AssassinMask.shape("XXX", "X X");
        AssassinMask.setIngredient('X', blackWool);
        getServer().addRecipe(AssassinMask);
    }

    private void setupConfiguration() {
        FileConfiguration config = this.getConfig();
        config.addDefault("General.Stats_Tracking_Enabled", true);
        config.addDefault("General.Update_Check_Enabled", true);
        config.addDefault("General.Update_Prefer_Beta", false);
        config.addDefault("General.Debug_Mode_Enabled", false);

        config.addDefault("Assassin.Prevent_Neutral_PVP", true);
        config.addDefault("Assassin.Assassin_Mode_Duration", 3600);
        String[] defaultBlockedcmds = { "/spawn", "/home", "/tp", "/tphere", "/tpa", "/tpahere", "/tpall", "/tpaall" };
        config.addDefault("Assassin.blocked_commands", Arrays.asList(defaultBlockedcmds));
        //      config.addDefault("Assassin.Max_Allowed", 5);
        //      config.addDefault("Assassin.Hide_Neutral_Names", false);

        config.addDefault("Assassin.Warn_Time_Almost_Up", 10);
        config.addDefault("Assassin.Teleport_On_Deactivate", true);
        config.addDefault("Assassin.Return_Mask", false);

        config.addDefault("Assassin.Cooldown_Length", 600);
        config.addDefault("Assassin.Activation_Cost", 0);

        config.addDefault("Assassin.Warn_Others_On_Activation", true);
        config.addDefault("Assassin.Warn_Others_When_Near", true);
        config.addDefault("Assassin.Message_Distance", 250);

        config.addDefault("Assassin.Particle_Effects_Enabled", true);
        config.addDefault("Assassin.Potion_Effects_Enabled", true);

        config.addDefault("Assassin.Bounty_Increase_Amount", 10);
        config.addDefault("Assassin.Bounty_Currency", "$");

        config.options().copyDefaults(true);
        saveConfig();
    }

    private void checkConfiguration() {
        if (Config.getActivationCost() > 0 && !vaultEnabled) {
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
        if (Config.getWarnWhenNear()) {
            new RangeCheckTask().runTaskTimer(this, 10 * 20, 10 * 20);
        }

        // Activity timer task (Runs every two seconds)
        new ActivityTimerTask().runTaskTimer(this, 2 * 20, 2 * 20);

        // Periodic save timer (Saves every 15 minutes by default)
        long saveIntervalTicks = Config.getSaveInterval() * 60 * 20;
        new SaveTimerTask().runTaskTimer(this, saveIntervalTicks, saveIntervalTicks);
    }
}
