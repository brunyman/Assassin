package me.TfT02.Assassin.runnables;

import me.TfT02.Assassin.Assassin;
import me.TfT02.Assassin.util.PlayerData;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AssassinRangeTimer implements Runnable {
	Assassin plugin;

	public AssassinRangeTimer(Assassin instance) {
		plugin = instance;
	}

	private PlayerData data = new PlayerData(plugin);

	@Override
	public void run() {
		checkIfAssassinNear();
	}

	public void checkIfAssassinNear() {
		double distance = plugin.getConfig().getDouble("Assassin.messages_distance");
		for (Player players : Bukkit.getServer().getOnlinePlayers()) {
			for (String assassins : data.getOnlineAssassins()) {
				if (players.getName().equals(assassins)) {
					Player assassin = Bukkit.getServer().getPlayer(assassins);
					System.out.println("Checking if Assassin near.");

					System.out.println("players " + players);
					System.out.println("assassins " + assassins);
					System.out.println("assassin " + assassin);
					if (distance > 0) if (players.getWorld().equals(assassin.getWorld()) || players.getLocation().distance(assassin.getLocation()) > distance) {
						players.sendMessage(ChatColor.DARK_RED + "ASSASSIN SIGHTED!");
					}
				}
			}
		}
	}
}