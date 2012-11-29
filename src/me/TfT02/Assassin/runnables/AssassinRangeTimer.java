package me.TfT02.Assassin.runnables;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.TfT02.Assassin.Assassin;
import me.TfT02.Assassin.util.PlayerData;

public class AssassinRangeTimer implements Runnable {
	Assassin plugin;

	public AssassinRangeTimer(final Assassin instance) {
		plugin = instance;
	}

	private final PlayerData data = new PlayerData(plugin);

	@Override
	public void run() {
		checkIfAssassinNear();
	}

	public void checkIfAssassinNear() {
		double distance = 250;
		for (Player players : Bukkit.getServer().getOnlinePlayers()) {
			for (String assassins : data.getOnlineAssassins()) {
				if (players.getName().equals(assassins)) {
					Player assassin = Bukkit.getServer().getPlayer(assassins);
					System.out.println("Checking if Assassin near.");
					if (distance > 0)
						if (players.getWorld() != assassin.getWorld() || players.getLocation().distance(assassin.getLocation()) > distance) {
							players.sendMessage(ChatColor.DARK_RED + "ASSASSIN SIGHTED!");

						}
				}
			}
		}
	}
}