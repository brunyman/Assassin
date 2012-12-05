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
		double distance = Assassin.getInstance().getConfig().getDouble("Assassin.messages_distance");
		for (Player players : Bukkit.getServer().getOnlinePlayers()) {
			for (String assassins : data.getOnlineAssassins()) {
				if (players.getName().equals(assassins)) {
					Player assassin = Bukkit.getServer().getPlayer(assassins);
					System.out.println("Checking if Assassin near.");

					System.out.println("players " + players);
					System.out.println("assassins " + assassins);
					System.out.println("==========");
					if (distance > 0) {
						if (players.getWorld().equals(assassin.getWorld()) || players.getLocation().distance(assassin.getLocation()) > distance) {
							System.out.println("data.isAssassin(players) " + data.isAssassin(players));
							System.out.println("data.firstTimeNear(players) " + data.firstTimeNear(players));
							if(!data.isAssassin(players) && data.firstTimeNear(players)){
								players.sendMessage(ChatColor.DARK_RED + "ASSASSIN SIGHTED!");
								data.addNearSent(players);
							}
							else {
								//Message already been sent, dont send it again.
							}
						} if (players.getWorld().equals(assassin.getWorld()) || players.getLocation().distance(assassin.getLocation()) < distance) {
							if(!data.isAssassin(players) && !data.firstTimeNear(players)){
								data.removeNearSent(players);
							}
							else {
								
							}
						}
					}
				}
			}
		}
	}/*
	SPAM PREVENTION IDEA
	timer to check if in range => if yes set a hashmap boolean to true
	if not set a hashmap boolean to false
	if (!boolean hashmap) send message*/
}