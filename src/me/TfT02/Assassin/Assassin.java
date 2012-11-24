package me.TfT02.Assassin;

import java.io.IOException;
import java.util.logging.Level;

import me.TfT02.Assassin.Listeners.BlockListener;
import me.TfT02.Assassin.Listeners.EntityListener;
import me.TfT02.Assassin.Listeners.PlayerListener;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Assassin extends JavaPlugin {
	private final EntityListener entityListener = new EntityListener(this);
	private final PlayerListener playerListener = new PlayerListener(this);
	private final BlockListener blockListener = new BlockListener(this);
	public boolean spoutEnabled;
	public boolean debug_mode = false;

	/**
	 * Run things on enable.
	 */
	@Override
	public void onEnable() {
		final PluginManager pm = getServer().getPluginManager();
		if (pm.getPlugin("Spout") != null) spoutEnabled = true;
		else spoutEnabled = false;

		if (getConfig().getBoolean("General.debug_mode_enabled")) {
			this.getLogger().log(Level.WARNING, "Debug mode is enabled, this is only for advanced users!");
			debug_mode = true;
		}
		setupConfiguration();
		//Register events
		pm.registerEvents(entityListener, this);
		pm.registerEvents(playerListener, this);
		pm.registerEvents(blockListener, this);
		registerCommands();
		if (getConfig().getBoolean("General.stats_tracking_enabled")) {
			try {
				final Metrics metrics = new Metrics(this);
				metrics.start();
			} catch (final IOException e) {
				System.out.println("Failed to submit stats.");
			}
		}
	}

	private void setupConfiguration() {
		final FileConfiguration config = this.getConfig();
		config.addDefault("General.debug_mode_enabled", false);
		config.addDefault("General.stats_tracking_enabled", true);

		config.options().copyDefaults(true);
		saveConfig();
	}

	/**
	 * Register all the command and set Executor.
	 */
	private void registerCommands() {
		getCommand("assassin").setExecutor(new Commands(this));
	}

	/**
	 * Run things on disable.
	 */
	@Override
	public void onDisable() {
//		this.getServer().getScheduler().cancelTasks(this);
	}
}
