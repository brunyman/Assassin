package me.TfT02.Assassin.runnables;

import me.TfT02.Assassin.Assassin;
import me.TfT02.Assassin.AssassinMode;
import me.TfT02.Assassin.util.PlayerData;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Player;

public class ActiveTimer implements Runnable {
	Assassin plugin;

	public ActiveTimer(Assassin instance) {
		plugin = instance;
	}

	private PlayerData data = new PlayerData(plugin);
	private AssassinMode assassin = new AssassinMode(plugin);

	@Override
	public void run() {
		updateActiveTime();
		updateAssassinStatus();
	}

	private void updateActiveTime() {
		for (Player players : Bukkit.getServer().getOnlinePlayers()) {
			if (data.isAssassin(players)) {
				data.addLogoutTime(players);
				data.saveActiveTime(players);
				data.addLoginTime(players);
			}
		}
	}

	private void updateAssassinStatus() {
		for (Player players : Bukkit.getServer().getOnlinePlayers()) {
			long activetime = data.getActiveTime(players);
			long maxactivetime = Assassin.getInstance().getConfig().getLong("Assassin.active_length");
			long warntime = Assassin.getInstance().getConfig().getLong("Assassin.warn_time_almost_up");
			if (activetime >= maxactivetime) {
				if (data.isAssassin(players)) {
					assassin.deactivateAssassin(players);
					data.resetActiveTime(players);
					if (plugin.debug_mode) System.out.println(players + " status set to Neutral. Active time reached max.");
				}
			} else if (Assassin.getInstance().getConfig().getBoolean("Assassin.particle_effects")) {
				if (activetime + warntime >= maxactivetime) {
					if (data.isAssassin(players)) {
						players.getWorld().playEffect(players.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
						if (plugin.debug_mode) System.out.println(players + " has received a warning because his Assassin mode is running out.");
					}
				}
			}
		}
	}
}