package com.me.tft_02.assassin;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.me.tft_02.assassin.config.Config;
import com.me.tft_02.assassin.listeners.ChatListener;
import com.me.tft_02.assassin.listeners.EntityListener;
import com.me.tft_02.assassin.listeners.PlayerListener;
import com.me.tft_02.assassin.listeners.TagListener;
import com.me.tft_02.assassin.runnables.ActiveTimer;
import com.me.tft_02.assassin.runnables.AssassinRangeTimer;
import com.me.tft_02.assassin.runnables.SaveTimerTask;
import com.me.tft_02.assassin.util.Data;
import com.me.tft_02.assassin.util.Metrics;
import com.me.tft_02.assassin.util.UpdateChecker;

public class Assassin extends JavaPlugin {
    public static Assassin instance;

    private TagListener tagListener = new TagListener(this);
    private EntityListener entityListener = new EntityListener(this);
    private PlayerListener playerListener = new PlayerListener(this);
    private ChatListener chatListener = new ChatListener(this);

    private AssassinMode assassin = new AssassinMode(this);

    public boolean vaultEnabled;
    public boolean debug_mode = false;

    // Update Check
    public boolean updateAvailable;

    public static Economy econ = null;

    public static Assassin getInstance() {
        return instance;
    }

    /**
     * Run things on enable.
     */
    @Override
    public void onEnable() {
        instance = this;
        PluginManager pm = getServer().getPluginManager();
        if (pm.getPlugin("TagAPI") == null) {
            this.getLogger().log(Level.WARNING, "No TagAPI dependency found!");
            this.getLogger().log(Level.WARNING, "Download TagAPI from http://dev.bukkit.org/server-mods/tag/");
            pm.disablePlugin(this);
            return;
        }
        else if (!pm.isPluginEnabled("TagAPI")) {
            this.getLogger().log(Level.WARNING, "TagAPI is probably outdated, check the console log.");
            pm.disablePlugin(this);
            return;
        }
        if (getConfig().getBoolean("General.debug_mode_enabled")) {
            this.getLogger().log(Level.WARNING, "Debug mode is enabled, this is only for advanced users!");
            debug_mode = true;
        }
        vaultEnabled = setupEconomy();

        setupConfiguration();
        checkConfiguration();

        addCustomRecipes();

        pm.registerEvents(tagListener, this);
        pm.registerEvents(entityListener, this);
        pm.registerEvents(playerListener, this);
        pm.registerEvents(chatListener, this);
        //		pm.registerEvents(blockListener, this);

        getCommand("assassin").setExecutor(new Commands(this));

        Data.loadData();

        if (getConfig().getBoolean("General.stats_tracking_enabled")) {
            try {
                Metrics metrics = new Metrics(this);
                metrics.start();
            }
            catch (IOException e) {
                System.out.println("Failed to submit stats.");
            }
        }
        scheduleTasks();

        checkForUpdates();
    }

    private void checkForUpdates() {
        if (getConfig().getBoolean("General.update_check_enabled")) {
            try {
                updateAvailable = UpdateChecker.updateAvailable();
            }
            catch (Exception e) {
                updateAvailable = false;
            }

            if (updateAvailable) {
                this.getLogger().log(Level.INFO, "***********************************************************************************");
                this.getLogger().log(Level.INFO, "*                              Assassin is outdated!                              *");
                this.getLogger().log(Level.INFO, "* New version available on BukkitDev! http://dev.bukkit.org/server-mods/Assassin/ *");
                this.getLogger().log(Level.INFO, "***********************************************************************************");
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
        if (getConfig().getDouble("Assassin.activation_cost") > 0 && !vaultEnabled) {
            this.getLogger().log(Level.WARNING, "Vault dependency needed if you want to use currency!");
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
        this.getServer().getScheduler().cancelTasks(this);
    }

    private void scheduleTasks() {

        BukkitScheduler scheduler = getServer().getScheduler();
        // Range check timer (Runs every 10 seconds)
        if (Config.getWarnWhenNear()) {
            scheduler.scheduleSyncRepeatingTask(this, new AssassinRangeTimer(this), 0, 10 * 20);
        }
        // Active check timer (Runs every two seconds)
        scheduler.scheduleSyncRepeatingTask(this, new ActiveTimer(this), 0, 2 * 20);

        // Periodic save timer (Saves every 15 minutes by default)
        long saveIntervalTicks = Config.getSaveInterval() * 60 * 20;
        new SaveTimerTask().runTaskTimer(this, saveIntervalTicks, saveIntervalTicks);
    }
}
