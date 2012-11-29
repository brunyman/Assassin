package me.TfT02.Assassin.runnables;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.TfT02.Assassin.Assassin;
import me.TfT02.Assassin.AssassinMode;
import me.TfT02.Assassin.util.PlayerData;

public class CooldownTimer implements Runnable {
	Assassin plugin;

	public CooldownTimer(final Assassin instance) {
		plugin = instance;
	}

	private final PlayerData data = new PlayerData(plugin);
	private final AssassinMode assassin = new AssassinMode(plugin);

	@Override
	public void run() {
		updateCooldownList();
	}

	private void updateCooldownList() {
		for (Player players : Bukkit.getServer().getOnlinePlayers()) {
			System.out.println("Trying to update cooldowns.");
			if (data.isReady(players)) {
				if (!data.isNeutral(players)) {
					assassin.deactivateAssassin(players);
					System.out.println("Set back to Neutral because cooldown is done.");
				}
			} else {

			}
		}
	}
}