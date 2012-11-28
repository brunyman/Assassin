package me.TfT02.Assassin.runnables;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.TfT02.Assassin.Assassin;
import me.TfT02.Assassin.util.PlayerData;


public class AssassinRangeTimer  implements Runnable{
	Assassin plugin;

	public AssassinRangeTimer(final Assassin instance) {
		plugin = instance;
	}

	private final PlayerData data = new PlayerData(plugin);
	@Override
	public void run() {		
		for (Player players : plugin.getServer().getOnlinePlayers()) {
			if (data.isAssassin(players)){
				checkIfAssassinNear(players);
			}
		}
	}

	public void checkIfAssassinNear(Player assassin){
		double distance = 250;
		if (distance > 0)
			for (Player players : plugin.getServer().getOnlinePlayers()) {
				if (players.getWorld() != assassin.getWorld() || players.getLocation().distance(assassin.getLocation()) > distance) {
					players.sendMessage(ChatColor.DARK_RED + "ASSASSIN SIGHTED!");
				}
			}
	}
}