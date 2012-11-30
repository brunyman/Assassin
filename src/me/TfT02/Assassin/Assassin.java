package me.TfT02.Assassin;

import java.io.IOException;
import java.util.logging.Level;

import me.TfT02.Assassin.Listeners.BlockListener;
import me.TfT02.Assassin.Listeners.ChatListener;
import me.TfT02.Assassin.Listeners.EntityListener;
import me.TfT02.Assassin.Listeners.PlayerListener;
import me.TfT02.Assassin.Listeners.TagListener;
import me.TfT02.Assassin.util.Data;
import me.TfT02.Assassin.util.Metrics;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Assassin extends JavaPlugin {
	public static Assassin instance;
	private TagListener tagListener = new TagListener(this);
	private EntityListener entityListener = new EntityListener(this);
	private PlayerListener playerListener = new PlayerListener(this);
	private ChatListener chatListener = new ChatListener(this);
	private BlockListener blockListener = new BlockListener(this);
	public boolean spoutEnabled;
	public boolean debug_mode = false;

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
//		if (pm.getPlugin("Spout") != null)
//			spoutEnabled = true;
//		else
//			spoutEnabled = false;
		if (pm.getPlugin("TagAPI") == null) {
			this.getLogger().log(Level.WARNING, "Disabled due to no TagAPI dependency found!");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		if (getConfig().getBoolean("General.debug_mode_enabled")) {
			this.getLogger().log(Level.WARNING, "Debug mode is enabled, this is only for advanced users!");
			debug_mode = true;
		}
		setupConfiguration();
		//Register events
		pm.registerEvents(tagListener, this);
		pm.registerEvents(entityListener, this);
		pm.registerEvents(playerListener, this);
		pm.registerEvents(chatListener, this);
		pm.registerEvents(blockListener, this);
		registerCommands();
//		try {
//			Data.createFiles();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		Data.loadData();
		if (getConfig().getBoolean("General.stats_tracking_enabled")) {
			try {
				Metrics metrics = new Metrics(this);
				metrics.start();
			} catch (IOException e) {
				System.out.println("Failed to submit stats.");
			}
		}
//		if (!setupEconomy()) {
//			this.getLogger().log(Level.WARNING, "Disabled due to no Vault dependency found!");
//			getServer().getPluginManager().disablePlugin(this);
//			return;
//		}
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
			System.out.println("Failed to submit stats.");
		}
		BukkitScheduler scheduler = getServer().getScheduler();
//        scheduler.scheduleSyncRepeatingTask(this, new AssassinRangeTimer(this), 0, 10 * 20);
		//Cooldown timer (Runs every two seconds)
//        scheduler.scheduleSyncRepeatingTask(this, new CooldownTimer(this), 0, 40);
	}

	private void setupConfiguration() {
		FileConfiguration config = this.getConfig();
		config.addDefault("General.debug_mode_enabled", false);
		config.addDefault("General.stats_tracking_enabled", true);
		config.addDefault("Assassin.active_length", 30);
		config.addDefault("Assassin.cooldown_length", 30);
		config.addDefault("Assassin.messages_distance", 250);

//		config.addDefault("Assassin.max_allowed", 5);
//		config.addDefault("Assassin.activation_cost", 100);
//		config.addDefault("Assassin.hide_neutral_names", false);
//		config.addDefault("Assassin.warn_others_on_activation", true);
//		config.addDefault("Assassin.warn_others_when_near", true);
//		config.addDefault("Assassin.particle_effects", true);

		config.options().copyDefaults(true);
		saveConfig();
	}

//	private boolean setupEconomy() {
//		if (getServer().getPluginManager().getPlugin("Vault") == null) {
//			return false;
//		}
//		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
//		if (rsp == null) {
//			return false;
//		}
//		econ = rsp.getProvider();
//		return econ != null;
//	}

	/**
	 * Register all the command and set Executor.
	 */
	private void registerCommands() {
		getCommand("assassin").setExecutor(new Commands(this));
		getCommand("activate").setExecutor(new Commands(this));
	}

	/**
	 * Run things on disable.
	 */
	@Override
	public void onDisable() {
		Data.saveData();
		this.getServer().getScheduler().cancelTasks(this);
	}
}
