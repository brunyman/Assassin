package me.TfT02.Assassin;

import java.io.IOException;
import java.util.logging.Level;

import me.TfT02.Assassin.Listeners.BlockListener;
import me.TfT02.Assassin.Listeners.ChatListener;
import me.TfT02.Assassin.Listeners.EntityListener;
import me.TfT02.Assassin.Listeners.PlayerListener;
import me.TfT02.Assassin.Listeners.TagListener;
import me.TfT02.Assassin.runnables.AssassinRangeTimer;
import me.TfT02.Assassin.util.Data;
import me.TfT02.Assassin.util.Metrics;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Assassin extends JavaPlugin {
	public static Assassin instance;
	private final TagListener tagListener = new TagListener(this);
	private final EntityListener entityListener = new EntityListener(this);
	private final PlayerListener playerListener = new PlayerListener(this);
	private final ChatListener chatListener = new ChatListener(this);
	private final BlockListener blockListener = new BlockListener(this);
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
		final PluginManager pm = getServer().getPluginManager();
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
				final Metrics metrics = new Metrics(this);
				metrics.start();
			} catch (final IOException e) {
				System.out.println("Failed to submit stats.");
			}
		}
//		if (!setupEconomy()) {
//			this.getLogger().log(Level.WARNING, "Disabled due to no Vault dependency found!");
//			getServer().getPluginManager().disablePlugin(this);
//			return;
//		}
		try {
			final Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (final IOException e) {
			System.out.println("Failed to submit stats.");
		}
		BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new AssassinRangeTimer(this), 0, 1 * 1200);
	}

	private void setupConfiguration() {
		final FileConfiguration config = this.getConfig();
		config.addDefault("General.debug_mode_enabled", false);
		config.addDefault("General.stats_tracking_enabled", true);

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
//		this.getServer().getScheduler().cancelTasks(this);
	}
}
