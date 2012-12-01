package me.TfT02.Assassin.runnables;

import me.TfT02.Assassin.Assassin;
import me.TfT02.Assassin.AssassinMode;
import me.TfT02.Assassin.util.PlayerData;

import org.bukkit.Bukkit;
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
			long maxactivetime = 60;
			if (activetime >= maxactivetime) {
				if (data.isAssassin(players)) {
				assassin.deactivateAssassin(players);
				data.resetActiveTime(players);
				System.out.println("Set back to Neutral because activetime is max.");
				}
			}
		}
	}
}